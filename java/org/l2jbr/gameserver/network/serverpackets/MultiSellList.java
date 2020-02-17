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

import static org.l2jbr.gameserver.data.xml.impl.MultisellData.PAGE_SIZE;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.model.ItemInfo;
import org.l2jbr.gameserver.model.holders.ItemChanceHolder;
import org.l2jbr.gameserver.model.holders.MultisellEntryHolder;
import org.l2jbr.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.network.OutgoingPackets;

public class MultiSellList extends AbstractItemPacket
{
	private int _size;
	private int _index;
	private final PreparedMultisellListHolder _list;
	private final boolean _finished;
	
	public MultiSellList(PreparedMultisellListHolder list, int index)
	{
		_list = list;
		_index = index;
		_size = list.getEntries().size() - index;
		if (_size > PAGE_SIZE)
		{
			_finished = false;
			_size = PAGE_SIZE;
		}
		else
		{
			_finished = true;
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.MULTI_SELL_LIST.writeId(packet);
		
		packet.writeC(0x00); // Helios
		packet.writeD(_list.getId()); // list id
		packet.writeC(0x00); // GOD Unknown
		packet.writeD(1 + (_index / PAGE_SIZE)); // page started from 1
		packet.writeD(_finished ? 0x01 : 0x00); // finished
		packet.writeD(PAGE_SIZE); // size of pages
		packet.writeD(_size); // list length
		packet.writeC(0x00); // Grand Crusade
		packet.writeC(_list.isChanceMultisell() ? 0x01 : 0x00); // new multisell window
		packet.writeD(0x20); // Helios - Always 32
		
		while (_size-- > 0)
		{
			final ItemInfo itemEnchantment = _list.getItemEnchantment(_index);
			final MultisellEntryHolder entry = _list.getEntries().get(_index++);
			
			packet.writeD(_index); // Entry ID. Start from 1.
			packet.writeC(entry.isStackable() ? 1 : 0);
			
			// Those values will be passed down to MultiSellChoose packet.
			packet.writeH(itemEnchantment != null ? itemEnchantment.getEnchantLevel() : 0); // enchant level
			writeItemAugment(packet, itemEnchantment);
			writeItemElemental(packet, itemEnchantment);
			writeItemEnsoulOptions(packet, itemEnchantment);
			
			packet.writeH(entry.getProducts().size());
			packet.writeH(entry.getIngredients().size());
			
			for (ItemChanceHolder product : entry.getProducts())
			{
				final Item template = ItemTable.getInstance().getTemplate(product.getId());
				final ItemInfo displayItemEnchantment = (_list.isMaintainEnchantment() && (itemEnchantment != null) && (template != null) && template.getClass().equals(itemEnchantment.getItem().getClass())) ? itemEnchantment : null;
				
				packet.writeD(product.getId());
				if (template != null)
				{
					packet.writeQ(template.getBodyPart());
					packet.writeH(template.getType2());
				}
				else
				{
					packet.writeQ(0);
					packet.writeH(65535);
				}
				packet.writeQ(_list.getProductCount(product));
				packet.writeH(product.getEnchantmentLevel() > 0 ? product.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0); // enchant level
				packet.writeD((int) Math.ceil(product.getChance())); // chance
				writeItemAugment(packet, displayItemEnchantment);
				writeItemElemental(packet, displayItemEnchantment);
				writeItemEnsoulOptions(packet, displayItemEnchantment);
			}
			
			for (ItemChanceHolder ingredient : entry.getIngredients())
			{
				final Item template = ItemTable.getInstance().getTemplate(ingredient.getId());
				final ItemInfo displayItemEnchantment = ((itemEnchantment != null) && (itemEnchantment.getItem().getId() == ingredient.getId())) ? itemEnchantment : null;
				
				packet.writeD(ingredient.getId());
				packet.writeH(template != null ? template.getType2() : 65535);
				packet.writeQ(_list.getIngredientCount(ingredient));
				packet.writeH(ingredient.getEnchantmentLevel() > 0 ? ingredient.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0); // enchant level
				writeItemAugment(packet, displayItemEnchantment);
				writeItemElemental(packet, displayItemEnchantment);
				writeItemEnsoulOptions(packet, displayItemEnchantment);
			}
		}
		return true;
	}
}