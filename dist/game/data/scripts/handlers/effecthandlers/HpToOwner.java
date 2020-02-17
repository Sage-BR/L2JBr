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
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.effects.EffectType;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Formulas;

/**
 * HpToOwner effect implementation.
 * @author Sdw
 */
public class HpToOwner extends AbstractEffect
{
	private final double _power;
	private final int _stealAmount;
	
	public HpToOwner(StatsSet params)
	{
		_power = params.getDouble("power");
		_stealAmount = params.getInt("stealAmount");
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (!skill.isToggle() && skill.isMagic())
		{
			// TODO: M.Crit can occur even if this skill is resisted. Only then m.crit damage is applied and not debuff
			final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
			if (mcrit)
			{
				final double damage = _power * 10; // Tests show that 10 times HP DOT is taken during magic critical.
				effected.reduceCurrentHp(damage, effector, skill, true, false, true, false);
			}
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DMG_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (effected.isDead())
		{
			return false;
		}
		
		final double damage = _power * getTicksMultiplier();
		
		effector.doAttack(damage, effected, skill, true, false, false, false);
		if (_stealAmount > 0)
		{
			final double amount = (damage * _stealAmount) / 100;
			effector.setCurrentHp(effector.getCurrentHp() + amount);
			effector.setCurrentMp(effector.getCurrentMp() + amount);
		}
		return skill.isToggle();
	}
}