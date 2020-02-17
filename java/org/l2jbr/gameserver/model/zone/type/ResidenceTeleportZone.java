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

import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.model.zone.ZoneRespawn;

/**
 * based on Kerberos work for custom CastleTeleportZone
 * @author Nyaran
 */
public class ResidenceTeleportZone extends ZoneRespawn
{
	private int _residenceId;
	
	public ResidenceTeleportZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("residenceId"))
		{
			_residenceId = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true); // FIXME: Custom ?
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false); // FIXME: Custom ?
	}
	
	@Override
	public void oustAllPlayers()
	{
		for (PlayerInstance player : getPlayersInside())
		{
			if ((player != null) && player.isOnline())
			{
				player.teleToLocation(getSpawnLoc(), 200);
			}
		}
	}
	
	public int getResidenceId()
	{
		return _residenceId;
	}
}
