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
package org.l2jbr.gameserver.model.events.impl.creature.npc;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.impl.IBaseEvent;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
public class OnNpcSkillFinished implements IBaseEvent
{
	private final Npc _caster;
	private final PlayerInstance _target;
	private final Skill _skill;
	
	public OnNpcSkillFinished(Npc caster, PlayerInstance target, Skill skill)
	{
		_caster = caster;
		_target = target;
		_skill = skill;
	}
	
	public PlayerInstance getTarget()
	{
		return _target;
	}
	
	public Npc getCaster()
	{
		return _caster;
	}
	
	public Skill getSkill()
	{
		return _skill;
	}
	
	@Override
	public EventType getType()
	{
		return EventType.ON_NPC_SKILL_FINISHED;
	}
}
