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
package org.l2jbr.gameserver.network.serverpackets.primeshop;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Gnacik, UnAfraid
 */
public class ExBRGamePoint implements IClientOutgoingPacket
{
	private final int _charId;
	private final int _charPoints;
	
	public ExBRGamePoint(PlayerInstance player)
	{
		_charId = player.getObjectId();
		_charPoints = player.getPrimePoints();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_BR_GAME_POINT.writeId(packet);
		
		packet.writeD(_charId);
		packet.writeQ(_charPoints);
		packet.writeD(0x00);
		return true;
	}
}
