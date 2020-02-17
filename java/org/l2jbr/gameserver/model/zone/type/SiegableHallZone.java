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

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

/**
 * @author BiggBoss
 */
public class SiegableHallZone extends ClanHallZone
{
	private List<Location> _challengerLocations;
	
	public SiegableHallZone(int id)
	{
		super(id);
	}
	
	@Override
	public void parseLoc(int x, int y, int z, String type)
	{
		if ((type != null) && type.equals("challenger"))
		{
			if (_challengerLocations == null)
			{
				_challengerLocations = new ArrayList<>();
			}
			_challengerLocations.add(new Location(x, y, z));
		}
		else
		{
			super.parseLoc(x, y, z, type);
		}
	}
	
	public List<Location> getChallengerSpawns()
	{
		return _challengerLocations;
	}
	
	public void banishNonSiegeParticipants()
	{
		for (PlayerInstance player : getPlayersInside())
		{
			if ((player != null) && player.isInHideoutSiege())
			{
				player.teleToLocation(getBanishSpawnLoc(), true);
			}
		}
	}
}
