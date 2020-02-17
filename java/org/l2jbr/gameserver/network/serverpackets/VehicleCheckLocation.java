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
package org.l2jbr.gameserver.network.serverpackets;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author Maktakien
 */
public class VehicleCheckLocation implements IClientOutgoingPacket
{
	private final Creature _boat;
	
	public VehicleCheckLocation(Creature boat)
	{
		_boat = boat;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.VEHICLE_CHECK_LOCATION.writeId(packet);
		
		packet.writeD(_boat.getObjectId());
		packet.writeD(_boat.getX());
		packet.writeD(_boat.getY());
		packet.writeD(_boat.getZ());
		packet.writeD(_boat.getHeading());
		return true;
	}
}
