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

import java.util.logging.Logger;

import org.l2jbr.gameserver.ai.BoatAI;
import org.l2jbr.gameserver.enums.InstanceType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Vehicle;
import org.l2jbr.gameserver.model.actor.templates.CreatureTemplate;
import org.l2jbr.gameserver.network.serverpackets.VehicleDeparture;
import org.l2jbr.gameserver.network.serverpackets.VehicleInfo;
import org.l2jbr.gameserver.network.serverpackets.VehicleStarted;

/**
 * @author Maktakien, DS
 */
public class BoatInstance extends Vehicle
{
	protected static final Logger LOGGER_BOAT = Logger.getLogger(BoatInstance.class.getName());
	
	public BoatInstance(CreatureTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.BoatInstance);
		setAI(new BoatAI(this));
	}
	
	@Override
	public boolean isBoat()
	{
		return true;
	}
	
	@Override
	public int getId()
	{
		return 0;
	}
	
	@Override
	public boolean moveToNextRoutePoint()
	{
		final boolean result = super.moveToNextRoutePoint();
		if (result)
		{
			broadcastPacket(new VehicleDeparture(this));
		}
		
		return result;
	}
	
	@Override
	public void oustPlayer(PlayerInstance player)
	{
		super.oustPlayer(player);
		
		final Location loc = getOustLoc();
		if (player.isOnline())
		{
			player.teleToLocation(loc.getX(), loc.getY(), loc.getZ());
		}
		else
		{
			player.setXYZInvisible(loc.getX(), loc.getY(), loc.getZ()); // disconnects handling
		}
	}
	
	@Override
	public void stopMove(Location loc)
	{
		super.stopMove(loc);
		
		broadcastPacket(new VehicleStarted(this, 0));
		broadcastPacket(new VehicleInfo(this));
	}
	
	@Override
	public void sendInfo(PlayerInstance player)
	{
		player.sendPacket(new VehicleInfo(this));
	}
}
