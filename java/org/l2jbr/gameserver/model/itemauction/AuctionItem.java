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
package org.l2jbr.gameserver.model.itemauction;

import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.idfactory.IdFactory;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;

/**
 * @author Forsaiken
 */
public class AuctionItem
{
	private final int _auctionItemId;
	private final int _auctionLength;
	private final long _auctionInitBid;
	
	private final int _itemId;
	private final long _itemCount;
	@SuppressWarnings("unused")
	private final StatsSet _itemExtra;
	
	public AuctionItem(int auctionItemId, int auctionLength, long auctionInitBid, int itemId, long itemCount, StatsSet itemExtra)
	{
		_auctionItemId = auctionItemId;
		_auctionLength = auctionLength;
		_auctionInitBid = auctionInitBid;
		
		_itemId = itemId;
		_itemCount = itemCount;
		_itemExtra = itemExtra;
	}
	
	public boolean checkItemExists()
	{
		return ItemTable.getInstance().getTemplate(_itemId) != null;
	}
	
	public int getAuctionItemId()
	{
		return _auctionItemId;
	}
	
	public int getAuctionLength()
	{
		return _auctionLength;
	}
	
	public long getAuctionInitBid()
	{
		return _auctionInitBid;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public long getItemCount()
	{
		return _itemCount;
	}
	
	public ItemInstance createNewItemInstance()
	{
		final ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), _itemId);
		World.getInstance().addObject(item);
		item.setCount(_itemCount);
		item.setEnchantLevel(item.getItem().getDefaultEnchantLevel());
		return item;
	}
}