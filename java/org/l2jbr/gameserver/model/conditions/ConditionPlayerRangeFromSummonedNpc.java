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
package org.l2jbr.gameserver.model.conditions;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * Condition which checks if you are within the given range of a summoned by you npc.
 * @author Nik
 */
public class ConditionPlayerRangeFromSummonedNpc extends Condition
{
	/** NPC Ids. */
	private final int[] _npcIds;
	/** Radius to check. */
	private final int _radius;
	/** Expected value. */
	private final boolean _val;
	
	public ConditionPlayerRangeFromSummonedNpc(int[] npcIds, int radius, boolean val)
	{
		_npcIds = npcIds;
		_radius = radius;
		_val = val;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, Item item)
	{
		boolean existNpc = false;
		if ((_npcIds != null) && (_npcIds.length > 0) && (_radius > 0))
		{
			for (Npc target : World.getInstance().getVisibleObjectsInRange(effector, Npc.class, _radius))
			{
				if (CommonUtil.contains(_npcIds, target.getId()) && (effector == target.getSummoner()))
				{
					existNpc = true;
					break;
				}
			}
		}
		return existNpc == _val;
	}
}
