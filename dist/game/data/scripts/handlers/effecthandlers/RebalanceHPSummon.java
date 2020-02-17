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
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Summon;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.effects.EffectType;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.util.Util;

/**
 * Rebalance HP effect implementation.
 * @author Adry_85, earendil
 */
public class RebalanceHPSummon extends AbstractEffect
{
	public RebalanceHPSummon(StatsSet params)
	{
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.REBALANCE_HP;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (!effector.isPlayer())
		{
			return;
		}
		
		double fullHP = 0;
		double currentHPs = 0;
		
		for (Summon summon : effector.getServitors().values())
		{
			if (!summon.isDead() && Util.checkIfInRange(skill.getAffectRange(), effector, summon, true))
			{
				fullHP += summon.getMaxHp();
				currentHPs += summon.getCurrentHp();
			}
		}
		
		fullHP += effector.getMaxHp();
		currentHPs += effector.getCurrentHp();
		
		double percentHP = currentHPs / fullHP;
		for (Summon summon : effector.getServitors().values())
		{
			if (!summon.isDead() && Util.checkIfInRange(skill.getAffectRange(), effector, summon, true))
			{
				double newHP = summon.getMaxHp() * percentHP;
				if (newHP > summon.getCurrentHp()) // The target gets healed
				{
					// The heal will be blocked if the current hp passes the limit
					if (summon.getCurrentHp() > summon.getMaxRecoverableHp())
					{
						newHP = summon.getCurrentHp();
					}
					else if (newHP > summon.getMaxRecoverableHp())
					{
						newHP = summon.getMaxRecoverableHp();
					}
				}
				
				summon.setCurrentHp(newHP);
			}
		}
		
		double newHP = effector.getMaxHp() * percentHP;
		if (newHP > effector.getCurrentHp()) // The target gets healed
		{
			// The heal will be blocked if the current hp passes the limit
			if (effector.getCurrentHp() > effector.getMaxRecoverableHp())
			{
				newHP = effector.getCurrentHp();
			}
			else if (newHP > effector.getMaxRecoverableHp())
			{
				newHP = effector.getMaxRecoverableHp();
			}
		}
		effector.setCurrentHp(newHP);
	}
}
