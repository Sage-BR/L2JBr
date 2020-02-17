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
package quests.Q00738_DimensionalExplorationOfTheUnworldlyVisitors;

import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10571_StrategicReconciliation.Q10571_StrategicReconciliation;

/**
 * Dimensional Exploration of the Unworldly Visitors (738)
 * @URL https://l2wiki.com/Dimensional_Exploration_of_the_Unworldly_Visitors
 * @VIDEO https://www.youtube.com/watch?v=NBvHfIzHh_o
 * @author Gigi
 * @date 2019-08-27 - [20:08:40]
 */
public class Q00738_DimensionalExplorationOfTheUnworldlyVisitors extends Quest
{
	// NPCs
	private static final int TARTI = 34360;
	// Monsters
	private static final int[] MONSTERS =
	{
		// Dimensional Crack
		23755, // Wandering Dead of the Dimension
		23757, // Lost Soul of the Dimension
		23759, // Roaming Vengeance of the Dimension
		23760, // Dimensional Vagabond
		23761, // Dimension Dissolver
		// Dimensional Rift quest monsters
		23806, // Wandering Dead of the Dimension
		23807, // Wandering Dimensional Spirit
		23808, // Lost Soul of the Dimension
		23809, // Lost Dimensional Evil Thoughts
		23810 // Roaming Vengeance of the Dimension
	};
	// Items
	private static final int DIMENSIONAL_ENERGY_FRAGMENT = 48163;
	private static final int DIMENSIONAL_EXPLORATION_REPORT = 48164;
	// Misc
	private static final int MIN_LEVEL = 95;
	private static final int MAX_LEVEL = 106;
	
	public Q00738_DimensionalExplorationOfTheUnworldlyVisitors()
	{
		super(738);
		addStartNpc(TARTI);
		addTalkId(TARTI);
		addKillId(MONSTERS);
		registerQuestItems(DIMENSIONAL_ENERGY_FRAGMENT);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "34360-00.htm");
		addCondCompletedQuest(Q10571_StrategicReconciliation.class.getSimpleName(), "34360-00.htm");
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
			case "34360-02.htm":
			case "34360-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34360-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34360-07.html":
			{
				if (qs.isCond(2))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						giveItems(player, DIMENSIONAL_EXPLORATION_REPORT, 1);
						addExpAndSp(player, 5_379_299_640L, 5_379_210);
						addFactionPoints(player, Faction.UNWORLDLY_VISITORS, 100);
						qs.exitQuest(QuestType.DAILY, true);
						htmltext = event;
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
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = getAlreadyCompletedMsg(player, QuestType.DAILY);
					break;
				}
				qs.setState(State.CREATED);
			}
			case State.CREATED:
			{
				if (getQuestItemsCount(player, DIMENSIONAL_EXPLORATION_REPORT) == 10)
				{
					htmltext = "34360-08.html";
					break;
				}
				htmltext = "34360-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "34360-05.html" : "34360-06.html";
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
			if (giveItemRandomly(killer, npc, DIMENSIONAL_ENERGY_FRAGMENT, 1, 50, 0.5, true))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
