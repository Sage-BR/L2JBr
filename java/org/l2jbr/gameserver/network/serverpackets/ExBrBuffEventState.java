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

/**
 * Eva's Inferno event packet. info params: <br>
 * type (1 - %, 2 - npcId), <br>
 * value (depending on type: for type 1 - % value; for type 2 - 20573-20575), <br>
 * state (0-1), endtime (only when type 2)
 */
public class ExBrBuffEventState implements IClientOutgoingPacket
{
	private final int _type; // 1 - %, 2 - npcId
	private final int _value; // depending on type: for type 1 - % value; for type 2 - 20573-20575
	private final int _state; // 0-1
	private final int _endtime; // only when type 2 as unix time in seconds from 1970
	
	public ExBrBuffEventState(int type, int value, int state, int endtime)
	{
		_type = type;
		_value = value;
		_state = state;
		_endtime = endtime;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_BR_BUFF_EVENT_STATE.writeId(packet);
		
		packet.writeD(_type);
		packet.writeD(_value);
		packet.writeD(_state);
		packet.writeD(_endtime);
		return true;
	}
}
