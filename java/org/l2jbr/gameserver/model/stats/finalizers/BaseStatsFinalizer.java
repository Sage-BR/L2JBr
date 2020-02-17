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

import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;

import org.l2jbr.gameserver.data.xml.impl.ArmorSetsData;
import org.l2jbr.gameserver.model.ArmorSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.stats.BaseStats;
import org.l2jbr.gameserver.model.stats.IStatsFunction;
import org.l2jbr.gameserver.model.stats.Stats;

/**
 * @author UnAfraid
 */
public class BaseStatsFinalizer implements IStatsFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, Stats stat)
	{
		throwIfPresent(base);
		
		// Apply template value
		double baseValue = creature.getTemplate().getBaseValue(stat, 0);
		
		// Should not apply armor set and henna bonus to summons.
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			final Set<ArmorSet> appliedSets = new HashSet<>(2);
			
			// Armor sets calculation
			for (ItemInstance item : player.getInventory().getPaperdollItems())
			{
				for (ArmorSet set : ArmorSetsData.getInstance().getSets(item.getId()))
				{
					if ((set.getPiecesCount(player, ItemInstance::getId) >= set.getMinimumPieces()) && appliedSets.add(set))
					{
						baseValue += set.getStatsBonus(BaseStats.valueOf(stat));
					}
				}
			}
			
			// Henna calculation
			baseValue += player.getHennaValue(BaseStats.valueOf(stat));
		}
		return validateValue(creature, Stats.defaultValue(creature, stat, baseValue), 1, BaseStats.MAX_STAT_VALUE - 1);
	}
	
}
