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

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.effects.EffectFlag;
import org.l2jbr.gameserver.model.effects.EffectType;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.SystemMessageId;

/**
 * Relax effect implementation.
 */
public class Relax extends AbstractEffect
{
	private final double _power;
	
	public Relax(StatsSet params)
	{
		_power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.RELAXING.getMask();
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RELAXING;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (effected.isPlayer())
		{
			effected.getActingPlayer().sitDown(false);
		}
		else
		{
			effected.getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
		}
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (effected.isDead())
		{
			return false;
		}
		
		if (effected.isPlayer())
		{
			if (!effected.getActingPlayer().isSitting())
			{
				return false;
			}
		}
		
		if ((effected.getCurrentHp() + 1) > effected.getMaxRecoverableHp())
		{
			if (skill.isToggle())
			{
				effected.sendPacket(SystemMessageId.THAT_SKILL_HAS_BEEN_DE_ACTIVATED_AS_HP_WAS_FULLY_RECOVERED);
				return false;
			}
		}
		
		final double manaDam = _power * getTicksMultiplier();
		if (manaDam > effected.getCurrentMp())
		{
			if (skill.isToggle())
			{
				effected.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
				return false;
			}
		}
		
		effected.reduceCurrentMp(manaDam);
		
		return skill.isToggle();
	}
}
