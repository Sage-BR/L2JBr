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

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Formulas;
import org.l2jbr.gameserver.util.Util;

/**
 * Transfer Hate effect implementation.
 * @author Adry_85
 */
public class TransferHate extends AbstractEffect
{
	private final int _chance;
	
	public TransferHate(StatsSet params)
	{
		_chance = params.getInt("chance", 100);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return Formulas.calcProbability(_chance, effector, effected, skill);
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return Util.checkIfInRange(skill.getEffectRange(), effector, effected, true);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		World.getInstance().forEachVisibleObjectInRange(effector, Attackable.class, skill.getAffectRange(), hater ->
		{
			if (hater.isDead())
			{
				return;
			}
			final int hate = hater.getHating(effector);
			if (hate <= 0)
			{
				return;
			}
			
			hater.reduceHate(effector, -hate);
			hater.addDamageHate(effected, 0, hate);
		});
	}
}
