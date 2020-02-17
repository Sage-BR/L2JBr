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
import org.l2jbr.gameserver.model.primeshop.PrimeShopGroup;
import org.l2jbr.gameserver.model.primeshop.PrimeShopItem;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Gnacik
 */
public class ExBRProductInfo implements IClientOutgoingPacket
{
	private final PrimeShopGroup _item;
	private final int _charPoints;
	private final long _charAdena;
	private final long _charCoins;
	
	public ExBRProductInfo(PrimeShopGroup item, PlayerInstance player)
	{
		_item = item;
		_charPoints = player.getPrimePoints();
		_charAdena = player.getAdena();
		_charCoins = player.getInventory().getInventoryItemCount(23805, -1);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_BR_PRODUCT_INFO.writeId(packet);
		
		packet.writeD(_item.getBrId());
		packet.writeD(_item.getPrice());
		packet.writeD(_item.getItems().size());
		for (PrimeShopItem item : _item.getItems())
		{
			packet.writeD(item.getId());
			packet.writeD((int) item.getCount());
			packet.writeD(item.getWeight());
			packet.writeD(item.isTradable());
		}
		packet.writeQ(_charAdena);
		packet.writeQ(_charPoints);
		packet.writeQ(_charCoins); // Hero coins
		return true;
	}
}
