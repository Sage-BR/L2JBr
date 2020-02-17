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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * Transformation effect implementation.
 * @author nBd
 */
public class Transformation extends AbstractEffect
{
	private final List<Integer> _id;
	
	public Transformation(StatsSet params)
	{
		final String ids = params.getString("transformationId", null);
		if ((ids != null) && !ids.isEmpty())
		{
			_id = new ArrayList<>();
			for (String id : ids.split(";"))
			{
				_id.add(Integer.parseInt(id));
			}
		}
		else
		{
			_id = Collections.emptyList();
		}
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return !effected.isDoor();
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (!_id.isEmpty())
		{
			effected.transform(_id.get(Rnd.get(_id.size())), true);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.stopTransformation(false);
	}
}
