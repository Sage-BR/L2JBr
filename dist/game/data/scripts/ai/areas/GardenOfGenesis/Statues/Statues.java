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
package ai.areas.GardenOfGenesis.Statues;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * Statues AI.
 * @author St3eT
 */
public class Statues extends AbstractNpcAI
{
	// NPCs
	private static final int STATUE_1 = 33138; // Genesis Angel Statue
	private static final int STATUE_2 = 33139; // Fountain of Genesis
	private static final int STATUE_3 = 33140; // Genesis Goddess Statue
	private static final int STATUE_KEEPER_1 = 23038; // Angel Statue Keeper
	private static final int STATUE_KEEPER_2 = 23039; // Fountain Keeper
	private static final int STATUE_KEEPER_3 = 23040; // Goddess Statue Keeper
	private static final int BUFF_NPC = 19073; // Genesis Transparent
	// Skills
	private static final SkillHolder REWARD_BUFF = new SkillHolder(14200, 1); // Blessing of Garden
	// Locations
	private static final Location LOC_1 = new Location(210049, 119367, -1352);
	private static final Location LOC_2 = new Location(217785, 110621, -1344);
	private static final Location LOC_3 = new Location(217792, 118844, -1760);
	
	private Statues()
	{
		addStartNpc(STATUE_1, STATUE_2, STATUE_3);
		addFirstTalkId(STATUE_1, STATUE_2, STATUE_3);
		addTalkId(STATUE_1, STATUE_2, STATUE_3);
		addKillId(STATUE_KEEPER_1, STATUE_KEEPER_2, STATUE_KEEPER_3);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		if (event.equals("fight"))
		{
			if (npc.isScriptValue(0))
			{
				Location loc = null;
				int npcId = -1;
				switch (npc.getId())
				{
					case STATUE_1:
					{
						loc = LOC_1;
						npcId = STATUE_KEEPER_1;
						break;
					}
					case STATUE_2:
					{
						loc = LOC_2;
						npcId = STATUE_KEEPER_2;
						break;
					}
					case STATUE_3:
					{
						loc = LOC_3;
						npcId = STATUE_KEEPER_3;
						break;
					}
				}
				
				if ((loc != null) && (npcId != -1))
				{
					npc.setScriptValue(1);
					startQuestTimer("FUNCTION_REUSE", 900000, npc, null);
					final Npc keeper = addSpawn(npcId, loc, false, 900000);
					addAttackPlayerDesire(keeper, player);
				}
			}
			else
			{
				htmltext = npc.getId() + "-no.html";
			}
		}
		else if (event.equals("FUNCTION_REUSE"))
		{
			npc.setScriptValue(0);
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + ".html";
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Npc buffNpc = addSpawn(BUFF_NPC, npc, false, 5000);
		buffNpc.setIsInvul(true);
		addSkillCastDesire(buffNpc, buffNpc, REWARD_BUFF, 23);
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new Statues();
	}
}