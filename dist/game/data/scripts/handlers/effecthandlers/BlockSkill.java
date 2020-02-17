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

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSkillUse;
import org.l2jbr.gameserver.model.events.listeners.FunctionEventListener;
import org.l2jbr.gameserver.model.events.returns.TerminateReturn;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * Block Skills by isMagic type.
 * @author Nik
 */
public class BlockSkill extends AbstractEffect
{
	private final int[] _magicTypes;
	
	public BlockSkill(StatsSet params)
	{
		_magicTypes = params.getIntArray("magicTypes", ";");
	}
	
	private TerminateReturn onSkillUseEvent(OnCreatureSkillUse event)
	{
		if (CommonUtil.contains(_magicTypes, event.getSkill().getMagicType()))
		{
			return new TerminateReturn(true, true, true);
		}
		
		return null;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if ((_magicTypes == null) || (_magicTypes.length == 0))
		{
			return;
		}
		
		effected.addListener(new FunctionEventListener(effected, EventType.ON_CREATURE_SKILL_USE, (OnCreatureSkillUse event) -> onSkillUseEvent(event), this));
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_SKILL_USE, listener -> listener.getOwner() == this);
	}
}
