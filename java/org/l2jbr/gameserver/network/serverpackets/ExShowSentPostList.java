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

import java.util.List;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.instancemanager.MailManager;
import org.l2jbr.gameserver.model.entity.Message;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author Migi, DS
 */
public class ExShowSentPostList implements IClientOutgoingPacket
{
	private final List<Message> _outbox;
	
	public ExShowSentPostList(int objectId)
	{
		_outbox = MailManager.getInstance().getOutbox(objectId);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHOW_SENT_POST_LIST.writeId(packet);
		
		packet.writeD((int) (System.currentTimeMillis() / 1000));
		if ((_outbox != null) && (_outbox.size() > 0))
		{
			packet.writeD(_outbox.size());
			for (Message msg : _outbox)
			{
				packet.writeD(msg.getId());
				packet.writeS(msg.getSubject());
				packet.writeS(msg.getReceiverName());
				packet.writeD(msg.isLocked() ? 0x01 : 0x00);
				packet.writeD(msg.getExpirationSeconds());
				packet.writeD(msg.isUnread() ? 0x01 : 0x00);
				packet.writeD(0x01);
				packet.writeD(msg.hasAttachments() ? 0x01 : 0x00);
				packet.writeD(0x00);
			}
		}
		else
		{
			packet.writeD(0x00);
		}
		return true;
	}
}
