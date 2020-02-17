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
public class AttackAttributeAdd extends AbstractEffect
{
	private final double _amount;
	
	public AttackAttributeAdd(StatsSet params)
	{
		_amount = params.getDouble("amount", 0);
	}
	
	@Override
	public void pump(Creature effected, Skill skill)
	{
		Stats stat = Stats.FIRE_POWER;
		AttributeType maxAttribute = AttributeType.FIRE;
		int maxValue = 0;
		
		for (AttributeType attribute : AttributeType.values())
		{
			final int attributeValue = effected.getStat().getAttackElementValue(attribute);
			if ((attributeValue > 0) && (attributeValue > maxValue))
			{
				maxAttribute = attribute;
				maxValue = attributeValue;
			}
		}
		
		switch (maxAttribute)
		{
			case WATER:
			{
				stat = Stats.WATER_POWER;
				break;
			}
			case WIND:
			{
				stat = Stats.WIND_POWER;
				break;
			}
			case EARTH:
			{
				stat = Stats.EARTH_POWER;
				break;
			}
			case HOLY:
			{
				stat = Stats.HOLY_POWER;
				break;
			}
			case DARK:
			{
				stat = Stats.DARK_POWER;
				break;
			}
		}
		
		effected.getStat().mergeAdd(stat, _amount);
	}
}
