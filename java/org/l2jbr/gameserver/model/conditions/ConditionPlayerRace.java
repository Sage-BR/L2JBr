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
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * The Class ConditionPlayerRace.
 * @author mkizub, Zoey76
 */
public class ConditionPlayerRace extends Condition
{
	private final Race[] _races;
	
	/**
	 * Instantiates a new condition player race.
	 * @param races the list containing the allowed races.
	 */
	public ConditionPlayerRace(Race[] races)
	{
		_races = races;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, Item item)
	{
		if ((effector == null) || !effector.isPlayer())
		{
			return false;
		}
		return CommonUtil.contains(_races, effector.getActingPlayer().getRace());
	}
}
