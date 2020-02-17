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
package org.l2jbr.gameserver.model.holders;

import org.l2jbr.gameserver.model.TradeItem;
import org.l2jbr.gameserver.model.items.Item;

/**
 * @author Mobius
 */
public class ClanShopProductHolder
{
	private final int _clanLevel;
	private final TradeItem _item;
	private final int _count;
	private final long _adena;
	private final int _fame;
	
	public ClanShopProductHolder(int clanLevel, Item item, int count, long adena, int fame)
	{
		_clanLevel = clanLevel;
		_item = new TradeItem(item, 0, 0);
		_count = count;
		_adena = adena;
		_fame = fame;
	}
	
	public int getClanLevel()
	{
		return _clanLevel;
	}
	
	public TradeItem getTradeItem()
	{
		return _item;
	}
	
	public int getCount()
	{
		return _count;
	}
	
	public long getAdena()
	{
		return _adena;
	}
	
	public int getFame()
	{
		return _fame;
	}
}
