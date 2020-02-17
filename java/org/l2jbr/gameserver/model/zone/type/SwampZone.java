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
package org.l2jbr.gameserver.model.zone.type;

import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.entity.Castle;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

/**
 * another type of zone where your speed is changed
 * @author kerberos, Pandragon
 */
public class SwampZone extends ZoneType
{
	private double _move_bonus;
	private int _castleId;
	private Castle _castle;
	private int _eventId;
	
	public SwampZone(int id)
	{
		super(id);
		
		// Setup default speed reduce (in %)
		_move_bonus = 0.5;
		
		// no castle by default
		_castleId = 0;
		_castle = null;
		
		// no event by default
		_eventId = 0;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("move_bonus"))
		{
			_move_bonus = Double.parseDouble(value);
		}
		else if (name.equals("castleId"))
		{
			_castleId = Integer.parseInt(value);
		}
		else if (name.equals("eventId"))
		{
			_eventId = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	private Castle getCastle()
	{
		if ((_castleId > 0) && (_castle == null))
		{
			_castle = CastleManager.getInstance().getCastleById(_castleId);
		}
		
		return _castle;
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (getCastle() != null)
		{
			// castle zones active only during siege
			if (!getCastle().getSiege().isInProgress() || !isEnabled())
			{
				return;
			}
			
			// defenders not affected
			final PlayerInstance player = creature.getActingPlayer();
			if ((player != null) && player.isInSiege() && (player.getSiegeState() == 2))
			{
				return;
			}
		}
		
		creature.setInsideZone(ZoneId.SWAMP, true);
		if (creature.isPlayer())
		{
			if (_eventId > 0)
			{
				creature.sendPacket(new OnEventTrigger(_eventId, true));
			}
			creature.getActingPlayer().broadcastUserInfo();
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		// don't broadcast info if not needed
		if (creature.isInsideZone(ZoneId.SWAMP))
		{
			creature.setInsideZone(ZoneId.SWAMP, false);
			if (creature.isPlayer())
			{
				if (_eventId > 0)
				{
					creature.sendPacket(new OnEventTrigger(_eventId, false));
				}
				if (!creature.isTeleporting())
				{
					creature.getActingPlayer().broadcastUserInfo();
				}
			}
		}
	}
	
	public double getMoveBonus()
	{
		return _move_bonus;
	}
}