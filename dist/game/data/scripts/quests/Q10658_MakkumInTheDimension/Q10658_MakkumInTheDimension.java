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
package quests.Q10658_MakkumInTheDimension;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q00928_100DaySubjugationOperation.Q00928_100DaySubjugationOperation;

/**
 * Makkum in the Dimension (10658)
 * @URL https://l2wiki.com/Makkum_in_the_Dimension
 * @VIDEO https://www.youtube.com/watch?v=1z5zLnMmKtw
 * @author Gigi
 */
public class Q10658_MakkumInTheDimension extends Quest
{
	// Npc
	private static final int LIAS = 34265;
	// Items
	private static final int DIMENSIONAL_TRACES = 47511;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q10658_MakkumInTheDimension()
	{
		super(10658);
		addStartNpc(LIAS);
		addTalkId(LIAS);
		addCondMinLevel(MIN_LEVEL, "34265-00.htm");
		addCondCompletedQuest(Q00928_100DaySubjugationOperation.class.getSimpleName(), "34265-00.htm");
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
			case "34265-02.htm":
			case "34265-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34265-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			default:
			{
				if (qs.isCond(2) && event.startsWith("giveReward_"))
				{
					final int itemId = Integer.parseInt(event.replace("giveReward_", ""));
					if (player.getLevel() >= MIN_LEVEL)
					{
						giveItems(player, itemId, 1);
						addExpAndSp(player, 4_303_647_428L, 10_328_753);
						takeItems(player, DIMENSIONAL_TRACES, -1);
						qs.exitQuest(QuestType.ONE_TIME, true);
						htmltext = "34265-07.html";
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
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
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (getQuestItemsCount(player, DIMENSIONAL_TRACES) >= 100)
				{
					htmltext = "34265-01.htm";
					break;
				}
				htmltext = "34265-00.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "34265-05.html" : "34265-06.html";
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
}
