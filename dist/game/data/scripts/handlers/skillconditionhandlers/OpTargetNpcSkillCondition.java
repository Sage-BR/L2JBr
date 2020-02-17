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
package handlers.skillconditionhandlers;

import java.util.List;

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.skills.ISkillCondition;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * @author Sdw
 */
public class OpTargetNpcSkillCondition implements ISkillCondition
{
	private final List<Integer> _npcId;
	
	public OpTargetNpcSkillCondition(StatsSet params)
	{
		_npcId = params.getList("npcIds", Integer.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		final WorldObject actualTarget = (caster == null) || !caster.isPlayer() ? target : caster.getTarget();
		return (actualTarget != null) && (actualTarget.isNpc() || actualTarget.isDoor()) && _npcId.contains(actualTarget.getId());
	}
}
