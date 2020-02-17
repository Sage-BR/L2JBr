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
package org.l2jbr.gameserver.network.serverpackets.fishing;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author -Wooden-
 */
public class ExFishingEnd implements IClientOutgoingPacket
{
	public enum FishingEndReason
	{
		LOSE(0),
		WIN(1),
		STOP(2);
		
		private final int _reason;
		
		FishingEndReason(int reason)
		{
			_reason = reason;
		}
		
		public int getReason()
		{
			return _reason;
		}
	}
	
	public enum FishingEndType
	{
		PLAYER_STOP,
		PLAYER_CANCEL,
		ERROR;
	}
	
	private final PlayerInstance _player;
	private final FishingEndReason _reason;
	
	public ExFishingEnd(PlayerInstance player, FishingEndReason reason)
	{
		_player = player;
		_reason = reason;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_FISHING_END.writeId(packet);
		packet.writeD(_player.getObjectId());
		packet.writeC(_reason.getReason());
		return true;
	}
}
