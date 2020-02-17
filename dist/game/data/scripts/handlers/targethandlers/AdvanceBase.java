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
package handlers.targethandlers;

import org.l2jbr.gameserver.handler.ITargetTypeHandler;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.targets.TargetType;
import org.l2jbr.gameserver.network.SystemMessageId;

/**
 * Target Outpost npc (36590).
 * @author Nik
 */
public class AdvanceBase implements ITargetTypeHandler
{
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.ADVANCE_BASE;
	}
	
	@Override
	public WorldObject getTarget(Creature creature, WorldObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
	{
		final WorldObject target = creature.getTarget();
		if ((target != null) && target.isNpc() && (target.getId() == 36590))
		{
			if (!((Npc) target).isDead())
			{
				return target;
			}
		}
		
		if (sendMessage)
		{
			creature.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
		}
		
		return null;
	}
}
