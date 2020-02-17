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
package org.l2jbr.gameserver.network.serverpackets.shuttle;

import java.util.List;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.instance.ShuttleInstance;
import org.l2jbr.gameserver.model.shuttle.ShuttleStop;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author UnAfraid
 */
public class ExShuttleInfo implements IClientOutgoingPacket
{
	private final ShuttleInstance _shuttle;
	private final List<ShuttleStop> _stops;
	
	public ExShuttleInfo(ShuttleInstance shuttle)
	{
		_shuttle = shuttle;
		_stops = shuttle.getStops();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHUTTLE_INFO.writeId(packet);
		
		packet.writeD(_shuttle.getObjectId());
		packet.writeD(_shuttle.getX());
		packet.writeD(_shuttle.getY());
		packet.writeD(_shuttle.getZ());
		packet.writeD(_shuttle.getHeading());
		packet.writeD(_shuttle.getId());
		packet.writeD(_stops.size());
		for (ShuttleStop stop : _stops)
		{
			packet.writeD(stop.getId());
			for (Location loc : stop.getDimensions())
			{
				packet.writeD(loc.getX());
				packet.writeD(loc.getY());
				packet.writeD(loc.getZ());
			}
			packet.writeD(stop.isDoorOpen() ? 0x01 : 0x00);
			packet.writeD(stop.hasDoorChanged() ? 0x01 : 0x00);
		}
		return true;
	}
}
