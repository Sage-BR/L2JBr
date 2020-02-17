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
import org.l2jbr.gameserver.model.interfaces.ILocational;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Sdw
 */
public class ExUserInfoFishing implements IClientOutgoingPacket
{
	private final PlayerInstance _player;
	private final boolean _isFishing;
	private final ILocational _baitLocation;
	
	public ExUserInfoFishing(PlayerInstance player, boolean isFishing, ILocational baitLocation)
	{
		_player = player;
		_isFishing = isFishing;
		_baitLocation = baitLocation;
	}
	
	public ExUserInfoFishing(PlayerInstance player, boolean isFishing)
	{
		_player = player;
		_isFishing = isFishing;
		_baitLocation = null;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_USER_INFO_FISHING.writeId(packet);
		
		packet.writeD(_player.getObjectId());
		packet.writeC(_isFishing ? 1 : 0);
		if (_baitLocation == null)
		{
			packet.writeD(0);
			packet.writeD(0);
			packet.writeD(0);
		}
		else
		{
			packet.writeD(_baitLocation.getX());
			packet.writeD(_baitLocation.getY());
			packet.writeD(_baitLocation.getZ());
		}
		return true;
	}
}
