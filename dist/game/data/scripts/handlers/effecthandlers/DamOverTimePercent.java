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
import org.l2jbr.gameserver.network.SystemMessageId;

/**
 * Damage Over Time Percent effect implementation.
 * @author Adry_85
 */
public class DamOverTimePercent extends AbstractEffect
{
	private final boolean _canKill;
	private final double _power;
	
	public DamOverTimePercent(StatsSet params)
	{
		_canKill = params.getBoolean("canKill", false);
		_power = params.getDouble("power");
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DMG_OVER_TIME_PERCENT;
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (effected.isDead())
		{
			return false;
		}
		
		double damage = effected.getCurrentHp() * _power * getTicksMultiplier();
		if (damage >= (effected.getCurrentHp() - 1))
		{
			if (skill.isToggle())
			{
				effected.sendPacket(SystemMessageId.YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP);
				return false;
			}
			
			// For DOT skills that will not kill effected player.
			if (!_canKill)
			{
				// Fix for players dying by DOTs if HP < 1 since reduceCurrentHP method will kill them
				if (effected.getCurrentHp() <= 1)
				{
					return skill.isToggle();
				}
				
				damage = effected.getCurrentHp() - 1;
			}
		}
		
		effector.doAttack(damage, effected, skill, true, false, false, false);
		
		return skill.isToggle();
	}
}
