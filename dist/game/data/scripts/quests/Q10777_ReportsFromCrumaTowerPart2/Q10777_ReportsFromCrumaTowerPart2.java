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
package quests.Q10777_ReportsFromCrumaTowerPart2;

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

import quests.Q10776_TheWrathOfTheGiants.Q10776_TheWrathOfTheGiants;

/**
 * Reports from Cruma Tower, Part 2 (10777)
 * @author malyelfik
 */
public class Q10777_ReportsFromCrumaTowerPart2 extends Quest
{
	// NPCs
	private static final int BELKADHI = 30485;
	private static final int MAGIC_OWL = 33991;
	// Location
	private static final Location OWL_LOC = new Location(17666, 108589, -9072);
	// Skill
	private static final SkillHolder TELEPORT = new SkillHolder(2588, 1);
	// Misc
	private static final int MIN_LEVEL = 46;
	
	public Q10777_ReportsFromCrumaTowerPart2()
	{
		super(10777);
		addStartNpc(BELKADHI);
		addTalkId(BELKADHI, MAGIC_OWL);
		
		addCondRace(Race.ERTHEIA, "30485-00.htm");
		addCondMinLevel(MIN_LEVEL, "30485-00.htm");
		addCondCompletedQuest(Q10776_TheWrathOfTheGiants.class.getSimpleName(), "30485-00.htm");
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
			case "30485-02.htm":
			case "30485-03.htm":
			case "30485-04.htm":
			case "30485-05.htm":
			case "33991-02.html":
			{
				break;
			}
			case "30485-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "summon":
			{
				if (qs.isCond(1) && !World.getInstance().getVisibleObjectsInRange(player, Npc.class, 700).stream().anyMatch(n -> n.getId() == MAGIC_OWL))
				{
					final Npc owl = addSpawn(MAGIC_OWL, OWL_LOC);
					getTimers().addTimer("DESPAWN_OWL", 20000, owl, null);
				}
				htmltext = null;
				break;
			}
			case "despawn":
			{
				if (qs.isCond(1))
				{
					getTimers().cancelTimer("DESPAWN_OWL", npc, null);
					qs.setCond(2, true);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.TO_QUEEN_NAVARI_OF_FAERON);
					npc.doCast(TELEPORT.getSkill());
					getTimers().addTimer("DESPAWN_OWL", 4000, npc, null);
				}
				htmltext = null;
				break;
			}
			case "30485-09.html":
			{
				if (qs.isCond(2))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 1257435, 36);
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
		
		if (npc.getId() == BELKADHI)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "30485-01.htm";
					break;
				}
				case State.STARTED:
				{
					htmltext = qs.isCond(1) ? "30485-07.html" : "30485-08.html";
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
		if ((npc != null) && (npc.getId() == MAGIC_OWL) && event.equals("DESPAWN_OWL"))
		{
			npc.deleteMe();
		}
		else
		{
			super.onTimerEvent(event, params, npc, player);
		}
	}
}
