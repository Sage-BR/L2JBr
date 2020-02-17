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
import org.l2jbr.gameserver.model.actor.instance.AirShipInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.OutgoingPackets;

public class ExAirShipStopMove implements IClientOutgoingPacket
{
	private final int _playerId;
	private final int _airShipId;
	private final int _x;
	private final int _y;
	private final int _z;
	
	public ExAirShipStopMove(PlayerInstance player, AirShipInstance ship, int x, int y, int z)
	{
		_playerId = player.getObjectId();
		_airShipId = ship.getObjectId();
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_MOVE_TO_LOCATION_AIR_SHIP.writeId(packet);
		
		packet.writeD(_airShipId);
		packet.writeD(_playerId);
		packet.writeD(_x);
		packet.writeD(_y);
		packet.writeD(_z);
		return true;
	}
}