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
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage.SMParam;

/**
 * ConfirmDlg server packet implementation.
 * @author kombat
 */
public class ConfirmDlg implements IClientOutgoingPacket
{
	private int _time;
	private int _requesterId;
	private final SystemMessage _systemMessage;
	
	public ConfirmDlg(SystemMessageId smId)
	{
		_systemMessage = new SystemMessage(smId);
	}
	
	public ConfirmDlg(int id)
	{
		_systemMessage = new SystemMessage(id);
	}
	
	public ConfirmDlg(String text)
	{
		_systemMessage = new SystemMessage(SystemMessageId.S1_3);
		_systemMessage.addString(text);
	}
	
	public ConfirmDlg addTime(int time)
	{
		_time = time;
		return this;
	}
	
	public ConfirmDlg addRequesterId(int id)
	{
		_requesterId = id;
		return this;
	}
	
	public SystemMessage getSystemMessage()
	{
		return _systemMessage;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.CONFIRM_DLG.writeId(packet);
		
		final SMParam[] params = _systemMessage.getParams();
		packet.writeD(_systemMessage.getId());
		packet.writeD(params.length);
		for (SMParam param : params)
		{
			packet.writeD(param.getType());
			switch (param.getType())
			{
				case SystemMessage.TYPE_ELEMENT_NAME:
				case SystemMessage.TYPE_BYTE:
				case SystemMessage.TYPE_FACTION_NAME:
				{
					packet.writeC(param.getIntValue());
					break;
				}
				case SystemMessage.TYPE_CASTLE_NAME:
				case SystemMessage.TYPE_SYSTEM_STRING:
				case SystemMessage.TYPE_INSTANCE_NAME:
				case SystemMessage.TYPE_CLASS_ID:
				{
					packet.writeH(param.getIntValue());
					break;
				}
				case SystemMessage.TYPE_ITEM_NAME:
				case SystemMessage.TYPE_INT_NUMBER:
				case SystemMessage.TYPE_NPC_NAME:
				case SystemMessage.TYPE_DOOR_NAME:
				{
					packet.writeD(param.getIntValue());
					break;
				}
				case SystemMessage.TYPE_LONG_NUMBER:
				{
					packet.writeQ(param.getLongValue());
					break;
				}
				case SystemMessage.TYPE_TEXT:
				case SystemMessage.TYPE_PLAYER_NAME:
				{
					packet.writeS(param.getStringValue());
					break;
				}
				case SystemMessage.TYPE_SKILL_NAME:
				{
					final int[] array = param.getIntArrayValue();
					packet.writeD(array[0]); // skill id
					packet.writeH(array[1]); // skill level
					packet.writeH(array[2]); // skill sub level
					break;
				}
				case SystemMessage.TYPE_POPUP_ID:
				case SystemMessage.TYPE_ZONE_NAME:
				{
					final int[] array = param.getIntArrayValue();
					packet.writeD(array[0]); // x
					packet.writeD(array[1]); // y
					packet.writeD(array[2]); // z
					break;
				}
			}
		}
		
		packet.writeD(_time);
		packet.writeD(_requesterId);
		return true;
	}
}
