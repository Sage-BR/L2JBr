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

import java.util.List;

import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.zone.ZoneType;

/**
 * @author UnAfraid
 */
public class ConditionPlayerInsideZoneId extends Condition
{
	private final List<Integer> _zones;
	
	public ConditionPlayerInsideZoneId(List<Integer> zones)
	{
		_zones = zones;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effector.getActingPlayer() == null)
		{
			return false;
		}
		
		for (ZoneType zone : ZoneManager.getInstance().getZones(effector))
		{
			if (_zones.contains(zone.getId()))
			{
				return true;
			}
		}
		return false;
	}
}
