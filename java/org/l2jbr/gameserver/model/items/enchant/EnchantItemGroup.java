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
package org.l2jbr.gameserver.model.items.enchant;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.gameserver.model.holders.RangeChanceHolder;

/**
 * @author UnAfraid
 */
public class EnchantItemGroup
{
	private static final Logger LOGGER = Logger.getLogger(EnchantItemGroup.class.getName());
	private final List<RangeChanceHolder> _chances = new ArrayList<>();
	private final String _name;
	private int _maximumEnchant = -1;
	
	public EnchantItemGroup(String name)
	{
		_name = name;
	}
	
	/**
	 * @return name of current enchant item group.
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * @param holder
	 */
	public void addChance(RangeChanceHolder holder)
	{
		_chances.add(holder);
	}
	
	/**
	 * @param index
	 * @return chance for success rate for current enchant item group.
	 */
	public double getChance(int index)
	{
		if (!_chances.isEmpty())
		{
			for (RangeChanceHolder holder : _chances)
			{
				if ((holder.getMin() <= index) && (holder.getMax() >= index))
				{
					return holder.getChance();
				}
			}
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't match proper chance for item group: " + _name, new IllegalStateException());
			return _chances.get(_chances.size() - 1).getChance();
		}
		LOGGER.warning(getClass().getSimpleName() + ": item group: " + _name + " doesn't have any chances!");
		return -1;
	}
	
	/**
	 * @return the maximum enchant level for current enchant item group.
	 */
	public int getMaximumEnchant()
	{
		if (_maximumEnchant == -1)
		{
			for (RangeChanceHolder holder : _chances)
			{
				if ((holder.getChance() > 0) && (holder.getMax() > _maximumEnchant))
				{
					_maximumEnchant = holder.getMax();
				}
			}
			_maximumEnchant++;
		}
		return _maximumEnchant;
	}
}
