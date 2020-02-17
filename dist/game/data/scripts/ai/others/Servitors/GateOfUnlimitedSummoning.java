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
package ai.others.Servitors;

import java.util.HashMap;
import java.util.Map;

import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.skills.Skill;

import ai.AbstractNpcAI;

/**
 * Death Gate AI.
 * @author Sdw
 */
public class GateOfUnlimitedSummoning extends AbstractNpcAI
{
	// NPCs
	private static final Map<Integer, Integer> DEATH_GATE = new HashMap<>();
	
	static
	{
		DEATH_GATE.put(14927, 1); // Death Gate
		DEATH_GATE.put(15200, 2); // Death Gate
		DEATH_GATE.put(15201, 3); // Death Gate
		DEATH_GATE.put(15202, 4); // Death Gate
	}
	
	// Skills
	private static final int GATE_ROOT = 11289;
	private static final int GATE_VORTEX = 11291;
	
	private GateOfUnlimitedSummoning()
	{
		addSpawnId(DEATH_GATE.keySet());
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Creature summoner = npc.getSummoner();
		if ((summoner != null) && summoner.isPlayer())
		{
			getTimers().addTimer("SKILL_CAST_SLOW", 1000, npc, null);
			getTimers().addTimer("SKILL_CAST_DAMAGE", 2000, npc, null);
			getTimers().addTimer("END_OF_LIFE", 30000, npc, null);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "SKILL_CAST_SLOW":
			{
				final int skillLevel = DEATH_GATE.get(npc.getId());
				if (skillLevel > 0)
				{
					final Skill skill = SkillData.getInstance().getSkill(GATE_ROOT, skillLevel);
					if (skill != null)
					{
						npc.doCast(skill);
					}
				}
				getTimers().addTimer("SKILL_CAST_SLOW", 3000, npc, null);
				break;
			}
			case "SKILL_CAST_DAMAGE":
			{
				final Skill skill = SkillData.getInstance().getSkill(GATE_VORTEX, 1);
				if (skill != null)
				{
					npc.doCast(skill);
				}
				
				getTimers().addTimer("SKILL_CAST_DAMAGE", 2000, npc, null);
				break;
			}
			case "END_OF_LIFE":
			{
				getTimers().cancelTimer("SKILL_CAST_SLOW", npc, null);
				getTimers().cancelTimer("SKILL_CAST_DAMAGE", npc, null);
				npc.deleteMe();
				break;
			}
		}
	}
	
	public static void main(String[] args)
	{
		new GateOfUnlimitedSummoning();
	}
}
