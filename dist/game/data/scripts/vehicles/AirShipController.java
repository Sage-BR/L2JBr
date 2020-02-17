/*
 * This file is part of the L2J Br project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package vehicles;

import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.instancemanager.AirShipManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.VehiclePathPoint;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.AirShipInstance;
import org.l2jbr.gameserver.model.actor.instance.ControllableAirShipInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.ClanPrivilege;
import org.l2jbr.gameserver.model.skills.AbnormalType;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.ScriptZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

import ai.AbstractNpcAI;

public abstract class AirShipController extends AbstractNpcAI
{
	private final class DecayTask implements Runnable
	{
		public DecayTask()
		{
		}
		
		@Override
		public void run()
		{
			if (_dockedShip != null)
			{
				_dockedShip.deleteMe();
			}
		}
	}
	
	private final class DepartTask implements Runnable
	{
		public DepartTask()
		{
		}
		
		@Override
		public void run()
		{
			if ((_dockedShip != null) && _dockedShip.isInDock() && !_dockedShip.isMoving())
			{
				if (_departPath != null)
				{
					_dockedShip.executePath(_departPath);
				}
				else
				{
					_dockedShip.deleteMe();
				}
			}
		}
	}
	
	private static final Logger LOGGER = Logger.getLogger(AirShipController.class.getName());
	protected int _dockZone = 0;
	protected int _shipSpawnX = 0;
	protected int _shipSpawnY = 0;
	
	protected int _shipSpawnZ = 0;
	
	private final int _shipHeading = 0;
	protected Location _oustLoc = null;
	protected int _locationId = 0;
	
	protected VehiclePathPoint[] _arrivalPath = null;
	protected VehiclePathPoint[] _departPath = null;
	
	protected VehiclePathPoint[][] _teleportsTable = null;
	
	protected int[] _fuelTable = null;
	
	protected Movie _movie = null;
	
	private boolean _isBusy = false;
	ControllableAirShipInstance _dockedShip = null;
	private final Runnable _decayTask = new DecayTask();
	
	private final Runnable _departTask = new DepartTask();
	
	private Future<?> _departSchedule = null;
	
	private static final int DEPART_INTERVAL = 300000; // 5 min
	private static final int LICENSE = 13559;
	
	private static final int STARSTONE = 13277;
	private static final int SUMMON_COST = 5;
	
	private static final SystemMessage SM_NEED_MORE = new SystemMessage(SystemMessageId.AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_YOU_DON_T_HAVE_ENOUGH_S1).addItemName(STARSTONE);
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if ("summon".equalsIgnoreCase(event))
		{
			if (_dockedShip != null)
			{
				if (_dockedShip.isOwner(player))
				{
					player.sendPacket(SystemMessageId.THE_CLAN_OWNED_AIRSHIP_ALREADY_EXISTS);
				}
				return null;
			}
			if (_isBusy)
			{
				player.sendPacket(SystemMessageId.ANOTHER_AIRSHIP_HAS_ALREADY_BEEN_SUMMONED_PLEASE_TRY_AGAIN_LATER);
				return null;
			}
			if (!player.hasClanPrivilege(ClanPrivilege.CL_SUMMON_AIRSHIP))
			{
				player.sendPacket(SystemMessageId.AIRSHIP_SUMMON_LICENSE_REGISTRATION_CAN_ONLY_BE_DONE_BY_THE_CLAN_LEADER);
				return null;
			}
			final int ownerId = player.getClanId();
			if (!AirShipManager.getInstance().hasAirShipLicense(ownerId))
			{
				player.sendPacket(SystemMessageId.AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_EITHER_YOU_HAVE_NOT_REGISTERED_YOUR_AIRSHIP_LICENSE_OR_THE_AIRSHIP_HAS_NOT_YET_BEEN_SUMMONED);
				return null;
			}
			if (AirShipManager.getInstance().hasAirShip(ownerId))
			{
				player.sendPacket(SystemMessageId.YOUR_CLAN_S_AIRSHIP_IS_ALREADY_BEING_USED_BY_ANOTHER_CLAN_MEMBER);
				return null;
			}
			if (!player.destroyItemByItemId("AirShipSummon", STARSTONE, SUMMON_COST, npc, true))
			{
				player.sendPacket(SM_NEED_MORE);
				return null;
			}
			
			_isBusy = true;
			final AirShipInstance ship = AirShipManager.getInstance().getNewAirShip(_shipSpawnX, _shipSpawnY, _shipSpawnZ, _shipHeading, ownerId);
			if (ship != null)
			{
				if (_arrivalPath != null)
				{
					ship.executePath(_arrivalPath);
				}
				
				npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.THE_AIRSHIP_HAS_BEEN_SUMMONED_IT_WILL_AUTOMATICALLY_DEPART_IN_5_MINUTES);
			}
			else
			{
				_isBusy = false;
			}
			
			return null;
		}
		else if ("board".equalsIgnoreCase(event))
		{
			if (player.isTransformed())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED);
				return null;
			}
			else if (player.hasBlockActions() && player.hasAbnormalType(AbnormalType.PARALYZE))
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED);
				return null;
			}
			else if (player.isDead() || player.isFakeDeath())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD);
				return null;
			}
			else if (player.isFishing())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING);
				return null;
			}
			else if (player.isInCombat())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE);
				return null;
			}
			else if (player.isInDuel())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL);
				return null;
			}
			else if (player.isSitting())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING);
				return null;
			}
			else if (player.isCastingNow())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_CASTING);
				return null;
			}
			else if (player.isCursedWeaponEquipped())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHEN_A_CURSED_WEAPON_IS_EQUIPPED);
				return null;
			}
			else if (player.isCombatFlagEquipped())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG);
				return null;
			}
			else if (player.hasSummon() || player.isMounted())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED);
				return null;
			}
			else if (player.isFlyingMounted())
			{
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
				return null;
			}
			
			if (_dockedShip != null)
			{
				_dockedShip.addPassenger(player);
			}
			
			return null;
		}
		else if ("register".equalsIgnoreCase(event))
		{
			if ((player.getClan() == null) || (player.getClan().getLevel() < 5))
			{
				player.sendPacket(SystemMessageId.IN_ORDER_TO_ACQUIRE_AN_AIRSHIP_THE_CLAN_S_LEVEL_MUST_BE_LEVEL_5_OR_ABOVE);
				return null;
			}
			if (!player.isClanLeader())
			{
				player.sendPacket(SystemMessageId.AIRSHIP_SUMMON_LICENSE_REGISTRATION_CAN_ONLY_BE_DONE_BY_THE_CLAN_LEADER);
				return null;
			}
			final int ownerId = player.getClanId();
			if (AirShipManager.getInstance().hasAirShipLicense(ownerId))
			{
				player.sendPacket(SystemMessageId.THE_AIRSHIP_SUMMON_LICENSE_HAS_ALREADY_BEEN_ACQUIRED);
				return null;
			}
			if (!player.destroyItemByItemId("AirShipLicense", LICENSE, 1, npc, true))
			{
				player.sendPacket(SM_NEED_MORE);
				return null;
			}
			
			AirShipManager.getInstance().registerLicense(ownerId);
			player.sendPacket(SystemMessageId.THE_AIRSHIP_SUMMON_LICENSE_HAS_BEEN_ENTERED_YOUR_CLAN_CAN_NOW_SUMMON_THE_AIRSHIP);
			return null;
		}
		else
		{
			return event;
		}
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		if (creature instanceof ControllableAirShipInstance)
		{
			if (_dockedShip == null)
			{
				_dockedShip = (ControllableAirShipInstance) creature;
				_dockedShip.setInDock(_dockZone);
				_dockedShip.setOustLoc(_oustLoc);
				
				// Ship is not empty - display movie to passengers and dock
				if (!_dockedShip.isEmpty())
				{
					if (_movie != null)
					{
						playMovie(_dockedShip.getPassengers(), _movie);
					}
					
					ThreadPool.schedule(_decayTask, 1000);
				}
				else
				{
					_departSchedule = ThreadPool.schedule(_departTask, DEPART_INTERVAL);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onExitZone(Creature creature, ZoneType zone)
	{
		if (creature instanceof ControllableAirShipInstance)
		{
			if (creature.equals(_dockedShip))
			{
				if (_departSchedule != null)
				{
					_departSchedule.cancel(false);
					_departSchedule = null;
				}
				
				_dockedShip.setInDock(0);
				_dockedShip = null;
				_isBusy = false;
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + ".htm";
	}
	
	protected void validityCheck()
	{
		final ScriptZone zone = ZoneManager.getInstance().getZoneById(_dockZone, ScriptZone.class);
		if (zone == null)
		{
			LOGGER.warning(getName() + ": Invalid zone " + _dockZone + ", controller disabled");
			_isBusy = true;
			return;
		}
		
		VehiclePathPoint p;
		if (_arrivalPath != null)
		{
			if (_arrivalPath.length == 0)
			{
				LOGGER.warning(getName() + ": Zero arrival path length.");
				_arrivalPath = null;
			}
			else
			{
				p = _arrivalPath[_arrivalPath.length - 1];
				if (!zone.isInsideZone(p.getLocation()))
				{
					LOGGER.warning(getName() + ": Arrival path finish point (" + p.getX() + "," + p.getY() + "," + p.getZ() + ") not in zone " + _dockZone);
					_arrivalPath = null;
				}
			}
		}
		if (_arrivalPath == null)
		{
			if (!ZoneManager.getInstance().getZoneById(_dockZone, ScriptZone.class).isInsideZone(_shipSpawnX, _shipSpawnY, _shipSpawnZ))
			{
				LOGGER.warning(getName() + ": Arrival path is null and spawn point not in zone " + _dockZone + ", controller disabled");
				_isBusy = true;
				return;
			}
		}
		
		if (_departPath != null)
		{
			if (_departPath.length == 0)
			{
				LOGGER.warning(getName() + ": Zero depart path length.");
				_departPath = null;
			}
			else
			{
				p = _departPath[_departPath.length - 1];
				if (zone.isInsideZone(p.getLocation()))
				{
					LOGGER.warning(getName() + ": Departure path finish point (" + p.getX() + "," + p.getY() + "," + p.getZ() + ") in zone " + _dockZone);
					_departPath = null;
				}
			}
		}
		
		if (_teleportsTable != null)
		{
			if (_fuelTable == null)
			{
				LOGGER.warning(getName() + ": Fuel consumption not defined.");
			}
			else if (_teleportsTable.length != _fuelTable.length)
			{
				LOGGER.warning(getName() + ": Fuel consumption not match teleport list.");
			}
			else
			{
				AirShipManager.getInstance().registerAirShipTeleportList(_dockZone, _locationId, _teleportsTable, _fuelTable);
			}
		}
	}
}
