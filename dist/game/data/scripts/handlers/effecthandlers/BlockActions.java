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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.l2jbr.gameserver.ai.CtrlEvent;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Summon;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.effects.EffectFlag;
import org.l2jbr.gameserver.model.effects.EffectType;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * Block Actions effect implementation.
 * @author mkizub
 */
public class BlockActions extends AbstractEffect
{
	private final Set<Integer> _allowedSkills;
	
	public BlockActions(StatsSet params)
	{
		final String[] allowedSkills = params.getString("allowedSkills", "").split(";");
		_allowedSkills = Arrays.stream(allowedSkills).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toSet());
	}
	
	@Override
	public long getEffectFlags()
	{
		return _allowedSkills.isEmpty() ? EffectFlag.BLOCK_ACTIONS.getMask() : EffectFlag.CONDITIONAL_BLOCK_ACTIONS.getMask();
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BLOCK_ACTIONS;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if ((effected == null) || effected.isRaid())
		{
			return;
		}
		
		_allowedSkills.stream().forEach(effected::addBlockActionsAllowedSkill);
		effected.startParalyze();
		// Cancel running skill casters.
		effected.abortAllSkillCasters();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		_allowedSkills.stream().forEach(effected::removeBlockActionsAllowedSkill);
		if (effected.isPlayable())
		{
			if (effected.isSummon())
			{
				if ((effector != null) && !effector.isDead())
				{
					if (effector.isPlayable() && (effected.getActingPlayer().getPvpFlag() == 0))
					{
						effected.disableCoreAI(false);
					}
					else
					{
						((Summon) effected).doAutoAttack(effector);
					}
				}
				else
				{
					effected.disableCoreAI(false);
				}
			}
			else
			{
				effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		}
		else
		{
			effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
}
