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

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.actor.instance.ShuttleInstance;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author UnAfraid
 */
public class ExShuttleMove implements IClientOutgoingPacket
{
	private final ShuttleInstance _shuttle;
	private final int _x;
	private final int _y;
	private final int _z;
	
	public ExShuttleMove(ShuttleInstance shuttle, int x, int y, int z)
	{
		_shuttle = shuttle;
		_x = x;
		_y = y;
		_z = z;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SUTTLE_MOVE.writeId(packet);
		
		packet.writeD(_shuttle.getObjectId());
		packet.writeD((int) _shuttle.getStat().getMoveSpeed());
		packet.writeD((int) _shuttle.getStat().getRotationSpeed());
		packet.writeD(_x);
		packet.writeD(_y);
		packet.writeD(_z);
		return true;
	}
}
