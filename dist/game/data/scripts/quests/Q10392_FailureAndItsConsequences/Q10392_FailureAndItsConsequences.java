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
package quests.Q10392_FailureAndItsConsequences;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10391_ASuspiciousHelper.Q10391_ASuspiciousHelper;

/**
 * Failure and its Consequences (10392)
 * @author St3eT
 */
public class Q10392_FailureAndItsConsequences extends Quest
{
	// NPCs
	private static final int IASON = 33859;
	private static final int ELI = 33858;
	private static final int[] MONSTERS =
	{
		20991, // Swamp Tribe
		20992, // Swamp Alligator
		20993, // Swamp Warrior
	};
	// Items
	private static final int FRAGMENT = 36709; // Suspicious Fragment
	// Misc
	private static final int MIN_LEVEL = 40;
	private static final int MAX_LEVEL = 46;
	
	public Q10392_FailureAndItsConsequences()
	{
		super(10392);
		addStartNpc(IASON);
		addTalkId(IASON, ELI);
		addKillId(MONSTERS);
		registerQuestItems(FRAGMENT);
		addCondNotRace(Race.ERTHEIA, "33859-10.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33859-09.htm");
		addCondCompletedQuest(Q10391_ASuspiciousHelper.class.getSimpleName(), "33859-09.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33859-02.htm":
			case "33859-03.htm":
			case "33858-02.html":
			case "33858-03.html":
			{
				htmltext = event;
				break;
			}
			case "33859-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33859-07.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "33858-04.html":
			{
				if (qs.isCond(3))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 4175045, 559);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == IASON)
				{
					htmltext = "33859-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == IASON)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "33859-05.html";
							break;
						}
						case 2:
						{
							htmltext = "33859-06.html";
							break;
						}
						case 3:
						{
							htmltext = "33859-08.html";
							break;
						}
					}
				}
				else if (npc.getId() == ELI)
				{
					if (qs.isCond(3))
					{
						htmltext = "33858-01.html";
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == IASON)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isStarted() && qs.isCond(1))
		{
			if (giveItemRandomly(killer, npc, FRAGMENT, 1, 4, 30, 0.8, true))
			{
				qs.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}