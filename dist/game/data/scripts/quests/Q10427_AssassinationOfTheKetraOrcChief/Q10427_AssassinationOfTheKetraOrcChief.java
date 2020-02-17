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
package quests.Q10427_AssassinationOfTheKetraOrcChief;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10426_AssassinationOfTheKetraOrcCommander.Q10426_AssassinationOfTheKetraOrcCommander;

/**
 * Assassination of the Ketra Orc Chief (10427)
 * @author Stayway
 */
public class Q10427_AssassinationOfTheKetraOrcChief extends Quest
{
	// NPCs
	private static final int LUGONNES = 33852;
	private static final int CHIEF_BRAKKI = 27501;
	private static final int KAMPF = 27516;
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 80;
	
	public Q10427_AssassinationOfTheKetraOrcChief()
	{
		super(10427);
		addStartNpc(LUGONNES);
		addTalkId(LUGONNES);
		addKillId(CHIEF_BRAKKI);
		addSpawnId(KAMPF);
		addCondNotRace(Race.ERTHEIA, "33852-08.html");
		addCondInCategory(CategoryType.WIZARD_GROUP, "33852-09.htm");
		addCondMinLevel(MIN_LEVEL, "33852-09.htm");
		addCondMaxLevel(MAX_LEVEL, "33852-09.htm");
		addCondCompletedQuest(Q10426_AssassinationOfTheKetraOrcCommander.class.getSimpleName(), "33853-09.htm");
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
			case "33852-02.htm":
			case "33852-04.html":
			{
				htmltext = event;
				break;
			}
			case "33852-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward_9546":
			case "reward_9547":
			case "reward_9548":
			case "reward_9549":
			case "reward_9550":
			case "reward_9551":
			{
				if (qs.isCond(2))
				{
					final int stoneId = Integer.parseInt(event.replaceAll("reward_", ""));
					qs.exitQuest(false, true);
					giveItems(player, stoneId, 15);
					giveStoryQuestReward(npc, player);
					if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL))
					{
						addExpAndSp(player, 327446943, 1839);
					}
					htmltext = "33852-07.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == LUGONNES)
		{
			if (qs.getState() == State.CREATED)
			{
				htmltext = "33852-01.htm";
			}
			else if (qs.getState() == State.STARTED)
			{
				if (qs.isCond(1))
				{
					htmltext = "33852-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33852-06.html";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isCond(1))
		{
			qs.setCond(2, true);
		}
		if ((qs != null) && qs.isCond(2))
		{
			addSpawn(KAMPF, 142130, -82520, -6487, 0, true, 60000);
		}
		return super.onKill(npc, killer, isSummon);
	}
}