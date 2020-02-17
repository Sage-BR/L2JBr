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
package handlers.effecthandlers;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Formulas;

/**
 * Randomize Hate effect implementation.
 */
public class RandomizeHate extends AbstractEffect
{
	private final int _chance;
	
	public RandomizeHate(StatsSet params)
	{
		_chance = params.getInt("chance", 100);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return Formulas.calcProbability(_chance, effector, effected, skill);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if ((effected == effector) || !effected.isAttackable())
		{
			return;
		}
		
		final Attackable effectedMob = (Attackable) effected;
		final List<Creature> targetList = new ArrayList<>();
		World.getInstance().forEachVisibleObject(effected, Creature.class, cha ->
		{
			if ((cha != effectedMob) && (cha != effector))
			{
				// Aggro cannot be transfered to a mob of the same faction.
				if (cha.isAttackable() && ((Attackable) cha).isInMyClan(effectedMob))
				{
					return;
				}
				
				targetList.add(cha);
			}
		});
		// if there is no target, exit function
		if (targetList.isEmpty())
		{
			return;
		}
		
		// Choosing randomly a new target
		final Creature target = targetList.get(Rnd.get(targetList.size()));
		final int hate = effectedMob.getHating(effector);
		effectedMob.stopHating(effector);
		effectedMob.addDamageHate(target, 0, hate);
	}
}