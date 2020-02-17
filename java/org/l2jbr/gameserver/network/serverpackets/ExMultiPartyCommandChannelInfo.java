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

import java.util.Objects;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.CommandChannel;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author chris_00
 */
public class ExMultiPartyCommandChannelInfo implements IClientOutgoingPacket
{
	private final CommandChannel _channel;
	
	public ExMultiPartyCommandChannelInfo(CommandChannel channel)
	{
		Objects.requireNonNull(channel);
		_channel = channel;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_MULTI_PARTY_COMMAND_CHANNEL_INFO.writeId(packet);
		
		packet.writeS(_channel.getLeader().getName());
		packet.writeD(0x00); // Channel loot 0 or 1
		packet.writeD(_channel.getMemberCount());
		
		packet.writeD(_channel.getParties().size());
		for (Party p : _channel.getParties())
		{
			packet.writeS(p.getLeader().getName());
			packet.writeD(p.getLeaderObjectId());
			packet.writeD(p.getMemberCount());
		}
		return true;
	}
}
