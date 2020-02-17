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
package quests.Q10460_ReturnOfTheAlligatorHunter;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Return of the Alligator Hunter (10460)
 * @author St3eT
 */
public class Q10460_ReturnOfTheAlligatorHunter extends Quest
{
	// NPCs
	private static final int ENRON = 33860;
	private static final int[] MONSTERS =
	{
		20135, // Alligator
		20804, // Crokian Lad
		20805, // Dailaon Lad
		20806, // Crokian Lad Warrior
		20807, // Farhite Lad
		20808, // Nos Lad
	};
	// Items
	private static final int LEATHER = 36710; // Alligator Leather
	private static final int BLUE_LEATHER = 36711; // Blue Alligator Leather
	private static final int BEJEWELED_LEATHER = 36712; // Bejeweled Alligator Leather
	// Misc
	private static final int MIN_LEVEL = 40;
	private static final int MAX_LEVEL = 46;
	
	public Q10460_ReturnOfTheAlligatorHunter()
	{
		super(10460);
		addStartNpc(ENRON);
		addTalkId(ENRON);
		addKillId(MONSTERS);
		registerQuestItems(LEATHER, BLUE_LEATHER, BEJEWELED_LEATHER);
		addCondNotRace(Race.ERTHEIA, "33860-09.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33860-08.htm");
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
			case "33860-02.htm":
			case "33860-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33860-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33860-07.html":
			{
				if (qs.isCond(2))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 4150144, 670);
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
				htmltext = "33860-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33860-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33860-06.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
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
			int itemId = 0;
			int itemCount = 0;
			
			final int chance = getRandom(100);
			if (chance < 10)
			{
				itemId = BEJEWELED_LEATHER;
				itemCount = 10;
			}
			else if (chance < 40)
			{
				itemId = BLUE_LEATHER;
				itemCount = 20;
			}
			else
			{
				itemId = LEATHER;
				itemCount = 30;
			}
			
			giveItemRandomly(killer, npc, itemId, 1, 2, itemCount, 1, true);
			
			if (getQuestItemsCount(killer, BEJEWELED_LEATHER, BLUE_LEATHER, LEATHER) == 60)
			{
				qs.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}