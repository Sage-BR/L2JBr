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
package ai.others.NpcBuffers;

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
class NpcBufferSkillData
{
	private final SkillHolder _skill;
	private final int _scaleToLevel;
	private final int _initialDelay;
	
	NpcBufferSkillData(StatsSet set)
	{
		_skill = new SkillHolder(set.getInt("id"), set.getInt("level"));
		_scaleToLevel = set.getInt("scaleToLevel", -1);
		_initialDelay = set.getInt("initialDelay", 0) * 1000;
	}
	
	public Skill getSkill()
	{
		return _skill.getSkill();
	}
	
	public int getScaleToLevel()
	{
		return _scaleToLevel;
	}
	
	public int getInitialDelay()
	{
		return _initialDelay;
	}
}
