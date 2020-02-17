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
package org.l2jbr.gameserver.model;

/**
 ** @author Gnacik
 */
public class PremiumItem
{
	private final int _itemId;
	private long _count;
	private final String _sender;
	
	public PremiumItem(int itemid, long count, String sender)
	{
		_itemId = itemid;
		_count = count;
		_sender = sender;
	}
	
	public void updateCount(long newcount)
	{
		_count = newcount;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public long getCount()
	{
		return _count;
	}
	
	public String getSender()
	{
		return _sender;
	}
}
