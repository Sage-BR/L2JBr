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
package org.l2jbr.gameserver.network.serverpackets.alchemy;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Sdw
 */
public class ExAlchemyConversion implements IClientOutgoingPacket
{
	private final int _successCount;
	private final int _failureCount;
	
	public ExAlchemyConversion(int successCount, int failureCount)
	{
		_successCount = successCount;
		_failureCount = failureCount;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_ALCHEMY_CONVERSION.writeId(packet);
		
		packet.writeC((_successCount == 0) && (_failureCount == 0) ? 0x01 : 0x00);
		packet.writeD(_successCount);
		packet.writeD(_failureCount);
		return true;
	}
}
