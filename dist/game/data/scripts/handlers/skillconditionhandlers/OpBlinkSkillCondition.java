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

import org.l2jbr.gameserver.enums.Position;
import org.l2jbr.gameserver.geoengine.GeoEngine;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.skills.ISkillCondition;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.util.Util;

/**
 * @author Sdw
 */
public class OpBlinkSkillCondition implements ISkillCondition
{
	private final int _angle;
	private final int _range;
	
	public OpBlinkSkillCondition(StatsSet params)
	{
		switch (params.getEnum("direction", Position.class))
		{
			case BACK:
			{
				_angle = 0;
				break;
			}
			case FRONT:
			{
				_angle = 180;
				break;
			}
			default:
			{
				_angle = -1;
				break;
			}
		}
		
		_range = params.getInt("range");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		final double angle = Util.convertHeadingToDegree(caster.getHeading());
		final double radian = Math.toRadians(angle);
		final double course = Math.toRadians(_angle);
		final int x1 = (int) (Math.cos(Math.PI + radian + course) * _range);
		final int y1 = (int) (Math.sin(Math.PI + radian + course) * _range);
		
		final int x = caster.getX() + x1;
		final int y = caster.getY() + y1;
		final int z = caster.getZ();
		
		return GeoEngine.getInstance().canMoveToTarget(caster.getX(), caster.getY(), caster.getZ(), x, y, z, caster.getInstanceWorld());
	}
}
