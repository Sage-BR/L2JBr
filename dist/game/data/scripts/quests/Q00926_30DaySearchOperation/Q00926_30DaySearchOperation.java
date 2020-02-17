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
package quests.Q00926_30DaySearchOperation;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Exploring the Dimension - 30-day Search Operation (926)
 * @URL https://l2wiki.com/Exploring_the_Dimension_-_30-day_Search_Operation
 * @Custom-Made based on quest 928
 * @author Mobius
 */
public class Q00926_30DaySearchOperation extends Quest
{
	// NPC
	private static final int BELOA = 34227;
	// Monsters
	private static final int WANDERING_OF_DIMENSION = 23755;
	private static final int LOST_SOUL_DIMENSION = 23757;
	private static final int ROAMING_VENGEANCE = 23759;
	// Items
	private static final int SPIRIT_FRAGMENTS = 46785;
	private static final int BELOAS_SUPPLY_ITEMS = 47043;
	private static final int REMNANT_OF_THE_RIFT = 46787;
	// Misc
	private static final QuestType QUEST_TYPE = QuestType.DAILY; // REPEATABLE, ONE_TIME, DAILY
	private static final int MIN_LEVEL = 95;
	private static final int MAX_LEVEL = 102;
	
	public Q00926_30DaySearchOperation()
	{
		super(926);
		addStartNpc(BELOA);
		addTalkId(BELOA);
		addKillId(WANDERING_OF_DIMENSION, LOST_SOUL_DIMENSION, ROAMING_VENGEANCE);
		registerQuestItems(SPIRIT_FRAGMENTS);
		addCondMinLevel(MIN_LEVEL, "34227-00.html");
		addCondMaxLevel(MAX_LEVEL, getNoQuestMsg(null));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "34227-02.htm":
			case "34227-03.htm":
			{
				return event;
			}
			case "34227-04.htm":
			{
				qs.startQuest();
				break;
			}
			case "34227-07.html":
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					if (getQuestItemsCount(player, REMNANT_OF_THE_RIFT) < 29)
					{
						giveItems(player, REMNANT_OF_THE_RIFT, 1);
						giveItems(player, BELOAS_SUPPLY_ITEMS, 1);
						addExpAndSp(player, 1507592779L, 3618222);
						qs.exitQuest(QUEST_TYPE, true);
						break;
					}
					addExpAndSp(player, 1507592779L, 3618222);
					giveItems(player, REMNANT_OF_THE_RIFT, 1);
					giveItems(player, BELOAS_SUPPLY_ITEMS, 1);
					qs.exitQuest(QUEST_TYPE, true);
				}
				break;
			}
			default:
			{
				return null;
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (qs.getState())
		{
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = getAlreadyCompletedMsg(player, QUEST_TYPE);
					break;
				}
				qs.setState(State.CREATED);
			}
			case State.CREATED:
			{
				htmltext = "34227-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "34227-05.html" : "34227-06.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && (qs.isCond(1)))
		{
			switch (npc.getId())
			{
				case WANDERING_OF_DIMENSION:
				case LOST_SOUL_DIMENSION:
				case ROAMING_VENGEANCE:
				{
					if (giveItemRandomly(killer, npc, SPIRIT_FRAGMENTS, 1, 100, 1.0, true))
					{
						qs.setCond(2, true);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}