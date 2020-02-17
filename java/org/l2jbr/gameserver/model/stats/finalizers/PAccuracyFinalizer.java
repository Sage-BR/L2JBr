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

import org.l2jbr.gameserver.GameTimeController;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.stats.IStatsFunction;
import org.l2jbr.gameserver.model.stats.Stats;

/**
 * @author UnAfraid
 */
public class PAccuracyFinalizer implements IStatsFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, Stats stat)
	{
		throwIfPresent(base);
		
		double baseValue = calcWeaponPlusBaseValue(creature, stat);
		
		// [Square(DEX)] * 5 + lvl + weapon hitbonus;
		final int level = creature.getLevel();
		baseValue += (Math.sqrt(creature.getDEX()) * 5) + level;
		if (level > 69)
		{
			baseValue += level - 69;
		}
		if (level > 77)
		{
			baseValue += 1;
		}
		if (level > 80)
		{
			baseValue += 2;
		}
		if (level > 87)
		{
			baseValue += 2;
		}
		if (level > 92)
		{
			baseValue += 1;
		}
		if (level > 97)
		{
			baseValue += 1;
		}
		
		if (creature.isPlayer())
		{
			// Enchanted gloves bonus
			baseValue += calcEnchantBodyPart(creature, Item.SLOT_GLOVES);
		}
		
		// Shadow sense
		if (GameTimeController.getInstance().isNight())
		{
			baseValue += creature.getStat().getAdd(Stats.HIT_AT_NIGHT, 0);
		}
		
		return Stats.defaultValue(creature, stat, baseValue);
	}
	
	@Override
	public double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed)
	{
		if (isBlessed)
		{
			return (0.3 * Math.max(enchantLevel - 3, 0)) + (0.3 * Math.max(enchantLevel - 6, 0));
		}
		
		return (0.2 * Math.max(enchantLevel - 3, 0)) + (0.2 * Math.max(enchantLevel - 6, 0));
	}
}
