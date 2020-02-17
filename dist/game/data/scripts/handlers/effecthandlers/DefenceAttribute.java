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

import org.l2jbr.gameserver.enums.AttributeType;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public class DefenceAttribute extends AbstractEffect
{
	private final AttributeType _attribute;
	private final double _amount;
	
	public DefenceAttribute(StatsSet params)
	{
		_amount = params.getDouble("amount", 0);
		_attribute = params.getEnum("attribute", AttributeType.class, AttributeType.FIRE);
	}
	
	@Override
	public void pump(Creature effected, Skill skill)
	{
		Stats stat = Stats.FIRE_RES;
		
		switch (_attribute)
		{
			case WATER:
			{
				stat = Stats.WATER_RES;
				break;
			}
			case WIND:
			{
				stat = Stats.WIND_RES;
				break;
			}
			case EARTH:
			{
				stat = Stats.EARTH_RES;
				break;
			}
			case HOLY:
			{
				stat = Stats.HOLY_RES;
				break;
			}
			case DARK:
			{
				stat = Stats.DARK_RES;
				break;
			}
		}
		effected.getStat().mergeAdd(stat, _amount);
	}
}
