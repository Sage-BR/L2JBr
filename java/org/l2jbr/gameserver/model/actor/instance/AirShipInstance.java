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

import org.l2jbr.gameserver.ai.AirShipAI;
import org.l2jbr.gameserver.enums.InstanceType;
import org.l2jbr.gameserver.instancemanager.AirShipManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Vehicle;
import org.l2jbr.gameserver.model.actor.templates.CreatureTemplate;
import org.l2jbr.gameserver.network.serverpackets.ExAirShipInfo;
import org.l2jbr.gameserver.network.serverpackets.ExGetOffAirShip;
import org.l2jbr.gameserver.network.serverpackets.ExGetOnAirShip;
import org.l2jbr.gameserver.network.serverpackets.ExMoveToLocationAirShip;
import org.l2jbr.gameserver.network.serverpackets.ExStopMoveAirShip;

/**
 * Flying airships. Very similar to Maktakien boats (see BoatInstance) but these do fly :P
 * @author DrHouse, DS
 */
public class AirShipInstance extends Vehicle
{
	public AirShipInstance(CreatureTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.AirShipInstance);
		setAI(new AirShipAI(this));
	}
	
	@Override
	public boolean isAirShip()
	{
		return true;
	}
	
	public boolean isOwner(PlayerInstance player)
	{
		return false;
	}
	
	public int getOwnerId()
	{
		return 0;
	}
	
	public boolean isCaptain(PlayerInstance player)
	{
		return false;
	}
	
	public int getCaptainId()
	{
		return 0;
	}
	
	public int getHelmObjectId()
	{
		return 0;
	}
	
	public int getHelmItemId()
	{
		return 0;
	}
	
	public boolean setCaptain(PlayerInstance player)
	{
		return false;
	}
	
	public int getFuel()
	{
		return 0;
	}
	
	public void setFuel(int f)
	{
		
	}
	
	public int getMaxFuel()
	{
		return 0;
	}
	
	public void setMaxFuel(int mf)
	{
		
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
			broadcastPacket(new ExMoveToLocationAirShip(this));
		}
		
		return result;
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
		player.broadcastPacket(new ExGetOnAirShip(player, this));
		player.setXYZ(getX(), getY(), getZ());
		player.revalidateZone(true);
		player.stopMove(null);
		return true;
	}
	
	@Override
	public void oustPlayer(PlayerInstance player)
	{
		super.oustPlayer(player);
		final Location loc = getOustLoc();
		if (player.isOnline())
		{
			player.broadcastPacket(new ExGetOffAirShip(player, this, loc.getX(), loc.getY(), loc.getZ()));
			player.teleToLocation(loc.getX(), loc.getY(), loc.getZ());
		}
		else
		{
			player.setXYZInvisible(loc.getX(), loc.getY(), loc.getZ());
		}
	}
	
	@Override
	public boolean deleteMe()
	{
		if (!super.deleteMe())
		{
			return false;
		}
		
		AirShipManager.getInstance().removeAirShip(this);
		return true;
	}
	
	@Override
	public void stopMove(Location loc)
	{
		super.stopMove(loc);
		
		broadcastPacket(new ExStopMoveAirShip(this));
	}
	
	@Override
	public void updateAbnormalVisualEffects()
	{
		broadcastPacket(new ExAirShipInfo(this));
	}
	
	@Override
	public void sendInfo(PlayerInstance player)
	{
		if (isVisibleFor(player))
		{
			player.sendPacket(new ExAirShipInfo(this));
		}
	}
}