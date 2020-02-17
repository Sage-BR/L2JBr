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
package org.l2jbr.gameserver.model.stats.finalizers;

import java.util.OptionalDouble;

import org.l2jbr.gameserver.enums.AttributeType;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.items.enchant.attribute.AttributeHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.stats.IStatsFunction;
import org.l2jbr.gameserver.model.stats.Stats;

/**
 * @author UnAfraid
 */
public class AttributeFinalizer implements IStatsFunction
{
	private final AttributeType _type;
	private final boolean _isWeapon;
	
	public AttributeFinalizer(AttributeType type, boolean isWeapon)
	{
		_type = type;
		_isWeapon = isWeapon;
	}
	
	@Override
	public double calc(Creature creature, OptionalDouble base, Stats stat)
	{
		throwIfPresent(base);
		
		double baseValue = creature.getTemplate().getBaseValue(stat, 0);
		if (creature.isPlayable())
		{
			if (_isWeapon)
			{
				final ItemInstance weapon = creature.getActiveWeaponInstance();
				if (weapon != null)
				{
					final AttributeHolder weaponInstanceHolder = weapon.getAttribute(_type);
					if (weaponInstanceHolder != null)
					{
						baseValue += weaponInstanceHolder.getValue();
					}
					
					final AttributeHolder weaponHolder = weapon.getItem().getAttribute(_type);
					if (weaponHolder != null)
					{
						baseValue += weaponHolder.getValue();
					}
				}
			}
			else
			{
				final Inventory inventory = creature.getInventory();
				if (inventory != null)
				{
					for (ItemInstance item : inventory.getPaperdollItems(ItemInstance::isArmor))
					{
						final AttributeHolder weaponInstanceHolder = item.getAttribute(_type);
						if (weaponInstanceHolder != null)
						{
							baseValue += weaponInstanceHolder.getValue();
						}
						
						final AttributeHolder weaponHolder = item.getItem().getAttribute(_type);
						if (weaponHolder != null)
						{
							baseValue += weaponHolder.getValue();
						}
					}
				}
			}
		}
		return Stats.defaultValue(creature, stat, baseValue);
	}
}
