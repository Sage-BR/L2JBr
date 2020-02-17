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
package org.l2jbr.gameserver.network.serverpackets.pledgeV2;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.data.xml.impl.ClanShopData;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ClanShopProductHolder;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExPledgeItemList extends AbstractItemPacket
{
	final PlayerInstance _player;
	
	public ExPledgeItemList(PlayerInstance player)
	{
		_player = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if (_player.getClan() == null)
		{
			return false;
		}
		
		OutgoingPackets.EX_PLEDGE_ITEM_LIST.writeId(packet);
		
		packet.writeH(ClanShopData.getInstance().getProducts().size()); // Product count.
		for (ClanShopProductHolder product : ClanShopData.getInstance().getProducts())
		{
			writeItem(packet, product.getTradeItem());
			packet.writeC(_player.getClan().getLevel() < product.getClanLevel() ? 0 : 2); // 0 locked, 1 need activation, 2 available
			packet.writeQ(product.getAdena()); // Purchase price: adena
			packet.writeD(product.getFame()); // Purchase price: fame
			packet.writeC(product.getClanLevel()); // Required pledge level
			packet.writeC(0); // Required pledge mastery
			packet.writeQ(0); // Activation price: adena
			packet.writeD(0); // Activation price: reputation
			packet.writeD(0); // Time to deactivation
			packet.writeD(0); // Time to restock
			packet.writeH(0); // Current stock
			packet.writeH(0); // Total stock
		}
		
		return true;
	}
}
