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

import org.l2jbr.gameserver.enums.StatModifierType;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Stats;

/**
 * @author Mobius
 */
public class FatalBlowRate extends AbstractEffect
{
	private final double _amount;
	private final StatModifierType _mode;
	
	public FatalBlowRate(StatsSet params)
	{
		_amount = params.getDouble("amount", 0);
		_mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		switch (_mode)
		{
			case DIFF:
			{
				effector.getStat().mergeAdd(Stats.BLOW_RATE, _amount);
				break;
			}
			case PER:
			{
				effector.getStat().mergeMul(Stats.BLOW_RATE, (_amount / 100) + 1);
				break;
			}
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		switch (_mode)
		{
			case DIFF:
			{
				effector.getStat().mergeAdd(Stats.BLOW_RATE, _amount * -1);
				break;
			}
			case PER:
			{
				effector.getStat().mergeMul(Stats.BLOW_RATE, ((_amount / 100) + 1) * -1);
				break;
			}
		}
	}
}
