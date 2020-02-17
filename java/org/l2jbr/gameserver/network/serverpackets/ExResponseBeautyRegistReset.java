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
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author Sdw
 */
public class ExResponseBeautyRegistReset implements IClientOutgoingPacket
{
	private final PlayerInstance _player;
	private final int _type;
	private final int _result;
	
	public static final int FAILURE = 0;
	public static final int SUCCESS = 1;
	
	public static final int CHANGE = 0;
	public static final int RESTORE = 1;
	
	public ExResponseBeautyRegistReset(PlayerInstance player, int type, int result)
	{
		_player = player;
		_type = type;
		_result = result;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_RESPONSE_BEAUTY_REGIST_RESET.writeId(packet);
		
		packet.writeQ(_player.getAdena());
		packet.writeQ(_player.getBeautyTickets());
		packet.writeD(_type);
		packet.writeD(_result);
		packet.writeD(_player.getVisualHair());
		packet.writeD(_player.getVisualFace());
		packet.writeD(_player.getVisualHairColor());
		return true;
	}
}
