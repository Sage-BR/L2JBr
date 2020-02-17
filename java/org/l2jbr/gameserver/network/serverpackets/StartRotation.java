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
import org.l2jbr.gameserver.network.OutgoingPackets;

public class StartRotation implements IClientOutgoingPacket
{
	private final int _objectId;
	private final int _degree;
	private final int _side;
	private final int _speed;
	
	public StartRotation(int objectId, int degree, int side, int speed)
	{
		_objectId = objectId;
		_degree = degree;
		_side = side;
		_speed = speed;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.START_ROTATING.writeId(packet);
		
		packet.writeD(_objectId);
		packet.writeD(_degree);
		packet.writeD(_side);
		packet.writeD(_speed);
		return true;
	}
}
