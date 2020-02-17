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
package org.l2jbr.gameserver.model.actor.instance;

import java.util.Iterator;
import java.util.List;

import org.l2jbr.gameserver.ai.ShuttleAI;
import org.l2jbr.gameserver.enums.InstanceType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Vehicle;
import org.l2jbr.gameserver.model.actor.templates.CreatureTemplate;
import org.l2jbr.gameserver.model.shuttle.ShuttleDataHolder;
import org.l2jbr.gameserver.model.shuttle.ShuttleStop;
import org.l2jbr.gameserver.network.serverpackets.shuttle.ExShuttleGetOff;
import org.l2jbr.gameserver.network.serverpackets.shuttle.ExShuttleGetOn;
import org.l2jbr.gameserver.network.serverpackets.shuttle.ExShuttleInfo;

/**
 * @author UnAfraid
 */
public class ShuttleInstance extends Vehicle
{
	private ShuttleDataHolder _shuttleData;
	
	public ShuttleInstance(CreatureTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.ShuttleInstance);
		setAI(new ShuttleAI(this));
	}
	
	public List<ShuttleStop> getStops()
	{
		return _shuttleData.getStops();
	}
	
	public void closeDoor(int id)
	{
		for (ShuttleStop stop : _shuttleData.getStops())
		{
			if (stop.getId() == id)
			{
				stop.closeDoor();
				break;
			}
		}
	}
	
	public void openDoor(int id)
	{
		for (ShuttleStop stop : _shuttleData.getStops())
		{
			if (stop.getId() == id)
			{
				stop.openDoor();
				break;
			}
		}
	}
	
	@Override
	public int getId()
	{
		return _shuttleData.getId();
	}
	
	@Override
	public boolean addPassenger(PlayerInstance player)
	{
		if (!super.addPassenger(player))
		{
			return false;
		}
		
		player.setVehicle(this);
		player.setInVehiclePosition(new Location(0, 0, 0));
		player.broadcastPacket(new ExShuttleGetOn(player, this));
		player.setXYZ(getX(), getY(), getZ());
		player.revalidateZone(true);
		return true;
	}
	
	public void removePassenger(PlayerInstance player, int x, int y, int z)
	{
		oustPlayer(player);
		if (player.isOnline())
		{
			player.broadcastPacket(new ExShuttleGetOff(player, this, x, y, z));
			player.setXYZ(x, y, z);
			player.revalidateZone(true);
		}
		else
		{
			player.setXYZInvisible(x, y, z);
		}
	}
	
	@Override
	public void oustPlayers()
	{
		PlayerInstance player;
		
		// Use iterator because oustPlayer will try to remove player from _passengers
		final Iterator<PlayerInstance> iter = _passengers.iterator();
		while (iter.hasNext())
		{
			player = iter.next();
			iter.remove();
			if (player != null)
			{
				oustPlayer(player);
			}
		}
	}
	
	@Override
	public void sendInfo(PlayerInstance player)
	{
		player.sendPacket(new ExShuttleInfo(this));
	}
	
	public void broadcastShuttleInfo()
	{
		broadcastPacket(new ExShuttleInfo(this));
	}
	
	public void setData(ShuttleDataHolder data)
	{
		_shuttleData = data;
	}
	
	public ShuttleDataHolder getShuttleData()
	{
		return _shuttleData;
	}
}
