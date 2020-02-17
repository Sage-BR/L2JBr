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

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Stats;

/**
 * @author Nik
 */
public class MaxCp extends AbstractStatEffect
{
	private final boolean _heal;
	
	public MaxCp(StatsSet params)
	{
		super(params, Stats.MAX_CP);
		
		_heal = params.getBoolean("heal", false);
	}
	
	@Override
	public void continuousInstant(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (_heal)
		{
			ThreadPool.schedule(() ->
			{
				switch (_mode)
				{
					case DIFF:
					{
						effected.setCurrentCp(effected.getCurrentCp() + _amount);
						break;
					}
					case PER:
					{
						effected.setCurrentCp(effected.getCurrentCp() + (effected.getMaxCp() * (_amount / 100)));
						break;
					}
				}
			}, 100);
		}
	}
}
