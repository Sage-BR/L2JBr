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

import java.util.Map;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.data.xml.impl.BeautyShopData;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.beautyshop.BeautyItem;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author Sdw
 */
public class ExResponseBeautyList implements IClientOutgoingPacket
{
	private final PlayerInstance _player;
	private final int _type;
	private final Map<Integer, BeautyItem> _beautyItem;
	
	public static final int SHOW_FACESHAPE = 1;
	public static final int SHOW_HAIRSTYLE = 0;
	
	public ExResponseBeautyList(PlayerInstance player, int type)
	{
		_player = player;
		_type = type;
		if (type == SHOW_HAIRSTYLE)
		{
			_beautyItem = BeautyShopData.getInstance().getBeautyData(player.getRace(), player.getAppearance().getSexType()).getHairList();
		}
		else
		{
			_beautyItem = BeautyShopData.getInstance().getBeautyData(player.getRace(), player.getAppearance().getSexType()).getFaceList();
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_RESPONSE_BEAUTY_LIST.writeId(packet);
		
		packet.writeQ(_player.getAdena());
		packet.writeQ(_player.getBeautyTickets());
		packet.writeD(_type);
		packet.writeD(_beautyItem.size());
		for (BeautyItem item : _beautyItem.values())
		{
			packet.writeD(item.getId());
			packet.writeD(1); // Limit
		}
		packet.writeD(0);
		return true;
	}
}
