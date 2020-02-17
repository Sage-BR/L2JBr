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
package ai.areas.GardenOfGenesis;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.SkillCaster;

import ai.AbstractNpcAI;

/**
 * Genesis Vines AI.
 * @author St3eT
 */
public class GenesisVines extends AbstractNpcAI
{
	// NPCs
	private static final int VINE = 18987; // Vine
	private static final int ROSE_VINE = 18988; // Rose Vine
	// Skills
	private static final SkillHolder VINE_SKILL = new SkillHolder(14092, 1);
	private static final SkillHolder ROSE_VINE_SKILL = new SkillHolder(14091, 1);
	
	private GenesisVines()
	{
		addSpawnId(VINE, ROSE_VINE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("CAST_SKILL") && npc.isScriptValue(1))
		{
			final SkillHolder skill = npc.getId() == VINE ? VINE_SKILL : ROSE_VINE_SKILL;
			if (SkillCaster.checkUseConditions(npc, skill.getSkill()))
			{
				addSkillCastDesire(npc, npc, skill, 23);
			}
			startQuestTimer("CAST_SKILL", 3000, npc, null);
		}
		else if (event.equals("DELETE"))
		{
			npc.setScriptValue(0);
			npc.deleteMe();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.disableCoreAI(true);
		npc.setScriptValue(1);
		cancelQuestTimer("CAST_SKILL", npc, null);
		cancelQuestTimer("DELETE", npc, null);
		startQuestTimer("CAST_SKILL", 3000, npc, null);
		startQuestTimer("DELETE", 150000, npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new GenesisVines();
	}
}