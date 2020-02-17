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
package org.l2jbr.gameserver.model.eventengine.drop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.model.holders.ItemHolder;

/**
 * @author UnAfraid
 */
public class NormalDrop implements IEventDrop
{
	private final List<EventDropItem> _items = new ArrayList<>();
	
	public List<EventDropItem> getItems()
	{
		return _items;
	}
	
	public void addItem(EventDropItem item)
	{
		_items.add(item);
	}
	
	@Override
	public Collection<ItemHolder> calculateDrops()
	{
		final List<ItemHolder> rewards = new ArrayList<>();
		double totalChance = 0;
		final double random = (Rnd.nextDouble() * 100);
		for (EventDropItem item : _items)
		{
			totalChance += item.getChance();
			if (totalChance > random)
			{
				final long count = Rnd.get(item.getMin(), item.getMax());
				if (count > 0)
				{
					rewards.add(new ItemHolder(item.getId(), count));
				}
			}
		}
		return rewards;
	}
}
