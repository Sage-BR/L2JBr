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
package quests.Q10772_ReportsFromCrumaTowerPart1;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10771_VolatilePower.Q10771_VolatilePower;

/**
 * Reports from Cruma Tower, Part 1 (10772)
 * @author malyelfik
 */
public class Q10772_ReportsFromCrumaTowerPart1 extends Quest
{
	// NPCs
	private static final int JANSSEN = 30484;
	private static final int MAGIC_OWL = 33991;
	// Location
	private static final Location OWL_LOC = new Location(17698, 115064, -11736);
	// Skill
	private static final SkillHolder OWL_TELEPORT = new SkillHolder(2588, 1);
	// Misc
	private static final int MIN_LEVEL = 45;
	
	public Q10772_ReportsFromCrumaTowerPart1()
	{
		super(10772);
		addStartNpc(JANSSEN);
		addTalkId(JANSSEN, MAGIC_OWL);
		
		addCondRace(Race.ERTHEIA, "30484-00.htm");
		addCondMinLevel(MIN_LEVEL, "30484-00.htm");
		addCondCompletedQuest(Q10771_VolatilePower.class.getSimpleName(), "30484-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "30484-02.htm":
			case "30484-03.htm":
			case "30484-04.htm":
			case "30484-05.htm":
			case "33991-02.html":
			{
				break;
			}
			case "30484-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "spawn_owl":
			{
				if (qs.isCond(1) && !World.getInstance().getVisibleObjectsInRange(player, Npc.class, 700).stream().anyMatch(n -> n.getId() == MAGIC_OWL))
				{
					addSpawn(MAGIC_OWL, OWL_LOC, true, 20000);
				}
				htmltext = null;
				break;
			}
			case "despawn_owl":
			{
				if (qs.isCond(1) && (npc != null))
				{
					getTimers().addTimer("DESPAWN_OWL", 4000, npc, null);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.TO_QUEEN_NAVARI_OF_FAERON);
					npc.doCast(OWL_TELEPORT.getSkill());
					qs.setCond(2, true);
				}
				htmltext = null;
				break;
			}
			case "30484-09.html":
			{
				if (qs.isCond(2))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 838290, 30);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == JANSSEN)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "30484-01.htm";
					break;
				}
				case State.STARTED:
				{
					htmltext = (qs.isCond(1)) ? "30484-07.html" : "30484-08.html";
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if (qs.isStarted() && qs.isCond(1))
		{
			htmltext = "33991-01.html";
		}
		return htmltext;
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		if (event.equals("DESPAWN_OWL") && (npc != null) && (npc.getId() == MAGIC_OWL))
		{
			npc.deleteMe();
		}
		else
		{
			super.onTimerEvent(event, params, npc, player);
		}
	}
}
