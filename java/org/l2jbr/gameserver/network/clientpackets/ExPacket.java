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
package org.l2jbr.gameserver.network.clientpackets;

import org.l2jbr.commons.network.IIncomingPacket;
import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.network.ExIncomingPackets;
import org.l2jbr.gameserver.network.GameClient;

/**
 * @author Nos
 */
public class ExPacket implements IClientIncomingPacket
{
	// private static final Logger LOGGER = Logger.getLogger(ExPacket.class.getName());
	
	private ExIncomingPackets _exIncomingPacket;
	private IIncomingPacket<GameClient> _exPacket;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		final int exPacketId = packet.readH() & 0xFFFF;
		if ((exPacketId < 0) || (exPacketId >= ExIncomingPackets.PACKET_ARRAY.length))
		{
			return false;
		}
		
		_exIncomingPacket = ExIncomingPackets.PACKET_ARRAY[exPacketId];
		if (_exIncomingPacket == null)
		{
			// LOGGER.finer(getClass().getSimpleName() + ": Unknown packet: " + Integer.toHexString(exPacketId));
			return false;
		}
		
		_exPacket = _exIncomingPacket.newIncomingPacket();
		return (_exPacket != null) && _exPacket.read(client, packet);
	}
	
	@Override
	public void run(GameClient client) throws Exception
	{
		if (!_exIncomingPacket.getConnectionStates().contains(client.getConnectionState()))
		{
			// LOGGER.finer(_exIncomingPacket + ": Connection at invalid state: " + client.getConnectionState() + " Required State: " + _exIncomingPacket.getConnectionStates());
			return;
		}
		_exPacket.run(client);
	}
}
