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
package org.l2jbr.gameserver.model.fishing;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.data.xml.impl.FishingData;
import org.l2jbr.gameserver.enums.ShotType;
import org.l2jbr.gameserver.geoengine.GeoEngine;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.PlayerCondOverride;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventDispatcher;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerFishing;
import org.l2jbr.gameserver.model.interfaces.ILocational;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.items.type.WeaponType;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.FishingZone;
import org.l2jbr.gameserver.model.zone.type.WaterZone;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.network.serverpackets.fishing.ExFishingEnd;
import org.l2jbr.gameserver.network.serverpackets.fishing.ExFishingEnd.FishingEndReason;
import org.l2jbr.gameserver.network.serverpackets.fishing.ExFishingEnd.FishingEndType;
import org.l2jbr.gameserver.network.serverpackets.fishing.ExFishingStart;
import org.l2jbr.gameserver.network.serverpackets.fishing.ExUserInfoFishing;
import org.l2jbr.gameserver.util.Util;

/**
 * @author bit
 */
public class Fishing
{
	protected static final Logger LOGGER = Logger.getLogger(Fishing.class.getName());
	private volatile ILocational _baitLocation = new Location(0, 0, 0);
	
	private final PlayerInstance _player;
	private ScheduledFuture<?> _reelInTask;
	private ScheduledFuture<?> _startFishingTask;
	private boolean _isFishing = false;
	
	public Fishing(PlayerInstance player)
	{
		_player = player;
	}
	
	public synchronized boolean isFishing()
	{
		return _isFishing;
	}
	
	public boolean isAtValidLocation()
	{
		// TODO: implement checking direction
		// if (calculateBaitLocation() == null)
		// {
		// return false;
		// }
		return _player.isInsideZone(ZoneId.FISHING);
	}
	
	public boolean canFish()
	{
		return !_player.isDead() && !_player.isAlikeDead() && !_player.hasBlockActions() && !_player.isSitting();
	}
	
	private FishingBait getCurrentBaitData()
	{
		final ItemInstance bait = _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		return bait != null ? FishingData.getInstance().getBaitData(bait.getId()) : null;
	}
	
	private void cancelTasks()
	{
		if (_reelInTask != null)
		{
			_reelInTask.cancel(false);
			_reelInTask = null;
		}
		
		if (_startFishingTask != null)
		{
			_startFishingTask.cancel(false);
			_startFishingTask = null;
		}
	}
	
	public synchronized void startFishing()
	{
		if (_isFishing)
		{
			return;
		}
		_isFishing = true;
		castLine();
	}
	
	private void castLine()
	{
		if (!Config.ALLOW_FISHING && !_player.canOverrideCond(PlayerCondOverride.ZONE_CONDITIONS))
		{
			_player.sendMessage("Fishing is disabled.");
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		cancelTasks();
		
		if (!canFish())
		{
			if (_isFishing)
			{
				_player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
			}
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		final FishingBait baitData = getCurrentBaitData();
		if (baitData == null)
		{
			_player.sendPacket(SystemMessageId.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (Config.PREMIUM_SYSTEM_ENABLED)
		{
			if (Config.PREMIUM_ONLY_FISHING && !_player.hasPremiumStatus())
			{
				_player.sendPacket(SystemMessageId.FISHING_IS_AVAILABLE_TO_PREMIUM_USERS_ONLY);
				_player.sendPacket(ActionFailed.STATIC_PACKET);
				stopFishing(FishingEndType.ERROR);
				return;
			}
			
			if (baitData.isPremiumOnly() && !_player.hasPremiumStatus())
			{
				_player.sendPacket(SystemMessageId.FAILED_PLEASE_TRY_AGAIN_USING_THE_CORRECT_BAIT);
				_player.sendPacket(ActionFailed.STATIC_PACKET);
				stopFishing(FishingEndType.ERROR);
				return;
			}
		}
		
		final int minPlayerLevel = baitData.getMinPlayerLevel();
		final int maxPLayerLevel = baitData.getMaxPlayerLevel();
		if ((_player.getLevel() < minPlayerLevel) && (_player.getLevel() > maxPLayerLevel))
		{
			_player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_FISHING_LEVEL_REQUIREMENTS);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		final ItemInstance rod = _player.getActiveWeaponInstance();
		if ((rod == null) || (rod.getItemType() != WeaponType.FISHINGROD))
		{
			_player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		final FishingRod rodData = FishingData.getInstance().getRodData(rod.getId());
		if (rodData == null)
		{
			_player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (_player.isTransformed() || _player.isInBoat())
		{
			_player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHEN_TRANSFORMED_OR_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT_IT_S_AGAINST_THE_RULES);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (_player.isCrafting() || _player.isInStoreMode())
		{
			_player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_WORKSHOP_OR_PRIVATE_STORE);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (_player.isInsideZone(ZoneId.WATER) || _player.isInWater())
		{
			_player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		_baitLocation = calculateBaitLocation();
		if (!_player.isInsideZone(ZoneId.FISHING) || (_baitLocation == null))
		{
			if (_isFishing)
			{
				// _player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
				_player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			else
			{
				_player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE_YOUR_CHARACTER_IS_NOT_FACING_WATER_OR_YOU_ARE_NOT_IN_A_FISHING_GROUND);
				_player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (!_player.isChargedShot(ShotType.FISH_SOULSHOTS))
		{
			_player.rechargeShots(false, false, true);
		}
		
		final long fishingTime = Math.max(Rnd.get(baitData.getTimeMin(), baitData.getTimeMax()) - rodData.getReduceFishingTime(), 1000);
		final long fishingWaitTime = Rnd.get(baitData.getWaitMin(), baitData.getWaitMax());
		
		_reelInTask = ThreadPool.schedule(() ->
		{
			_player.getFishing().reelInWithReward();
			_startFishingTask = ThreadPool.schedule(() -> _player.getFishing().castLine(), fishingWaitTime);
		}, fishingTime);
		_player.stopMove(null);
		_player.broadcastPacket(new ExFishingStart(_player, -1, _baitLocation));
		_player.sendPacket(new ExUserInfoFishing(_player, true, _baitLocation));
		_player.sendPacket(new PlaySound("SF_P_01"));
		_player.sendPacket(SystemMessageId.YOU_CAST_YOUR_LINE_AND_START_TO_FISH);
	}
	
	public void reelInWithReward()
	{
		// Fish may or may not eat the hook. If it does - it consumes fishing bait and fishing shot.
		// Then player may or may not catch the fish. Using fishing shots increases chance to win.
		final FishingBait baitData = getCurrentBaitData();
		if (baitData == null)
		{
			reelIn(FishingEndReason.LOSE, false);
			LOGGER.warning("Player " + _player + " is fishing with unhandled bait: " + _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND));
			return;
		}
		
		double chance = baitData.getChance();
		if (_player.isChargedShot(ShotType.FISH_SOULSHOTS))
		{
			chance *= 2;
		}
		
		if (Rnd.get(100) <= chance)
		{
			reelIn(FishingEndReason.WIN, true);
		}
		else
		{
			reelIn(FishingEndReason.LOSE, true);
		}
	}
	
	private void reelIn(FishingEndReason reason, boolean consumeBait)
	{
		if (!_isFishing)
		{
			return;
		}
		
		cancelTasks();
		
		try
		{
			final ItemInstance bait = _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			if (consumeBait)
			{
				if ((bait == null) || !_player.getInventory().updateItemCount(null, bait, -1, _player, null))
				{
					reason = FishingEndReason.LOSE; // no bait - no reward
					return;
				}
			}
			
			if ((reason == FishingEndReason.WIN) && (bait != null))
			{
				final FishingBait baitData = FishingData.getInstance().getBaitData(bait.getId());
				final FishingCatch fishingCatchData = baitData.getRandom();
				if (fishingCatchData != null)
				{
					final FishingData fishingData = FishingData.getInstance();
					final double lvlModifier = (Math.pow(_player.getLevel(), 2.2) * fishingCatchData.getMultiplier());
					final long xp = (long) (Rnd.get(fishingData.getExpRateMin(), fishingData.getExpRateMax()) * lvlModifier * _player.getStat().getValue(Stats.FISHING_EXP_SP_BONUS, 1));
					final long sp = (long) (Rnd.get(fishingData.getSpRateMin(), fishingData.getSpRateMax()) * lvlModifier * _player.getStat().getValue(Stats.FISHING_EXP_SP_BONUS, 1));
					_player.addExpAndSp(xp, sp, true);
					_player.getInventory().addItem("Fishing Reward", fishingCatchData.getItemId(), 1, _player, null);
					final SystemMessage msg = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
					msg.addItemName(fishingCatchData.getItemId());
					_player.sendPacket(msg);
					_player.unchargeShot(ShotType.FISH_SOULSHOTS);
					_player.rechargeShots(false, false, true);
				}
				else
				{
					LOGGER.log(Level.WARNING, "Could not find fishing rewards for bait ", bait.getId());
				}
			}
			else if (reason == FishingEndReason.LOSE)
			{
				_player.sendPacket(SystemMessageId.THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
			}
			
			if (consumeBait)
			{
				EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFishing(_player, reason), _player);
			}
		}
		finally
		{
			_player.broadcastPacket(new ExFishingEnd(_player, reason));
			_player.sendPacket(new ExUserInfoFishing(_player, false));
		}
	}
	
	public void stopFishing()
	{
		stopFishing(FishingEndType.PLAYER_STOP);
	}
	
	public synchronized void stopFishing(FishingEndType endType)
	{
		if (_isFishing)
		{
			reelIn(FishingEndReason.STOP, false);
			_isFishing = false;
			switch (endType)
			{
				case PLAYER_STOP:
				{
					_player.sendPacket(SystemMessageId.YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING);
					break;
				}
				case PLAYER_CANCEL:
				{
					_player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
					break;
				}
			}
		}
	}
	
	public ILocational getBaitLocation()
	{
		return _baitLocation;
	}
	
	private Location calculateBaitLocation()
	{
		// calculate a position in front of the player with a random distance
		final int distMin = FishingData.getInstance().getBaitDistanceMin();
		final int distMax = FishingData.getInstance().getBaitDistanceMax();
		int distance = Rnd.get(distMin, distMax);
		final double angle = Util.convertHeadingToDegree(_player.getHeading());
		final double radian = Math.toRadians(angle);
		final double sin = Math.sin(radian);
		final double cos = Math.cos(radian);
		int baitX = (int) (_player.getX() + (cos * distance));
		int baitY = (int) (_player.getY() + (sin * distance));
		
		// search for fishing zone
		FishingZone fishingZone = null;
		for (ZoneType zone : ZoneManager.getInstance().getZones(_player))
		{
			if (zone instanceof FishingZone)
			{
				fishingZone = (FishingZone) zone;
				break;
			}
		}
		// search for water zone
		WaterZone waterZone = null;
		for (ZoneType zone : ZoneManager.getInstance().getZones(baitX, baitY))
		{
			if (zone instanceof WaterZone)
			{
				waterZone = (WaterZone) zone;
				break;
			}
		}
		
		int baitZ = computeBaitZ(_player, baitX, baitY, fishingZone, waterZone);
		if (baitZ == Integer.MIN_VALUE)
		{
			_player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE_YOUR_CHARACTER_IS_NOT_FACING_WATER_OR_YOU_ARE_NOT_IN_A_FISHING_GROUND);
			return null;
		}
		
		return new Location(baitX, baitY, baitZ);
	}
	
	/**
	 * Computes the Z of the bait.
	 * @param player the player
	 * @param baitX the bait x
	 * @param baitY the bait y
	 * @param fishingZone the fishing zone
	 * @param waterZone the water zone
	 * @return the bait z or {@link Integer#MIN_VALUE} when you cannot fish here
	 */
	private static int computeBaitZ(PlayerInstance player, int baitX, int baitY, FishingZone fishingZone, WaterZone waterZone)
	{
		if ((fishingZone == null))
		{
			return Integer.MIN_VALUE;
		}
		
		if ((waterZone == null))
		{
			return Integer.MIN_VALUE;
		}
		
		// always use water zone, fishing zone high z is high in the air...
		final int baitZ = waterZone.getWaterZ();
		
		// if (!GeoEngine.getInstance().canSeeTarget(player.getX(), player.getY(), player.getZ(), baitX, baitY, baitZ))
		//
		// return Integer.MIN_VALUE;
		// }
		
		if (GeoEngine.getInstance().hasGeo(baitX, baitY))
		{
			if (GeoEngine.getInstance().getHeight(baitX, baitY, baitZ) > baitZ)
			{
				return Integer.MIN_VALUE;
			}
			
			if (GeoEngine.getInstance().getHeight(baitX, baitY, player.getZ()) > baitZ)
			{
				return Integer.MIN_VALUE;
			}
		}
		
		return baitZ;
	}
}
