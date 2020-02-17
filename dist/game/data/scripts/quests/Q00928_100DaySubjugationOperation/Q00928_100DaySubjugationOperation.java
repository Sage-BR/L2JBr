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
package quests.Q00928_100DaySubjugationOperation;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * 100-day Subjugation Operation (928)
 * @URL https://l2wiki.com/100-day_Subjugation_Operation
 * @VIDEO https://www.youtube.com/watch?v=83Z85GRNpzA
 * @author Gigi
 * @date 2017-12-05 - [11:45:30]
 */
public class Q00928_100DaySubjugationOperation extends Quest
{
	// Npc
	private static final int LIAS = 34265;
	// Monsters
	private static final int LILLIM_ROYAL_KNIGHT = 23801;
	private static final int LILLIM_SLAYER = 23802;
	private static final int GIGANTIC_HEALER = 23803;
	private static final int LILLIM_GRAT_MAGUS = 23804;
	private static final int NEPHILM_ROYAL_GUARD = 23805;
	
	private static final int WANDERING_OF_DIMENSION = 23806;
	private static final int WANDERING_SPIRIT = 23807;
	private static final int LOST_SOUL_DIMENSION = 23808;
	private static final int LOST_DIMENSION_EVIL = 23809;
	private static final int ROAMING_VENGEANCE = 23810;
	// Items
	private static final int ATTACKERS_SOUL = 47512;
	private static final int LIAS_SUPPLY_ITEMS = 47504;
	private static final int DIMENSIONAL_TRACES = 47511;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q00928_100DaySubjugationOperation()
	{
		super(928);
		addStartNpc(LIAS);
		addTalkId(LIAS);
		addKillId(LILLIM_ROYAL_KNIGHT, LILLIM_SLAYER, GIGANTIC_HEALER, LILLIM_GRAT_MAGUS, NEPHILM_ROYAL_GUARD);
		addKillId(WANDERING_OF_DIMENSION, WANDERING_SPIRIT, LOST_SOUL_DIMENSION, LOST_DIMENSION_EVIL, ROAMING_VENGEANCE);
		registerQuestItems(ATTACKERS_SOUL);
		addCondMinLevel(MIN_LEVEL, "34265-00.html");
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
			case "34265-07.html":
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					if (getQuestItemsCount(player, DIMENSIONAL_TRACES) < 99)
					{
						giveItems(player, DIMENSIONAL_TRACES, 1);
						giveItems(player, LIAS_SUPPLY_ITEMS, 1);
						addExpAndSp(player, 11_028_245_723L, 26_467_790);
						qs.exitQuest(QuestType.DAILY, true);
						htmltext = event;
						break;
					}
					addExpAndSp(player, 11_028_245_723L, 26_467_790);
					giveItems(player, DIMENSIONAL_TRACES, 1);
					giveItems(player, LIAS_SUPPLY_ITEMS, 1);
					qs.exitQuest(QuestType.ONE_TIME, true);
					htmltext = event;
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
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
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
				qs.setState(State.CREATED);
			}
			case State.CREATED:
			{
				htmltext = "34265-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "34265-05.html" : "34265-06.html";
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
				case LILLIM_ROYAL_KNIGHT:
				{
					final Npc mob1 = addSpawn(WANDERING_OF_DIMENSION, npc, false, 180000, false);
					addAttackPlayerDesire(mob1, killer);
					break;
				}
				case LILLIM_SLAYER:
				{
					final Npc mob2 = addSpawn(ROAMING_VENGEANCE, npc, false, 180000, false);
					addAttackPlayerDesire(mob2, killer);
					break;
				}
				case GIGANTIC_HEALER:
				{
					final Npc mob3 = addSpawn(LOST_SOUL_DIMENSION, npc, false, 180000, false);
					addAttackPlayerDesire(mob3, killer);
					break;
				}
				case LILLIM_GRAT_MAGUS:
				{
					final Npc mob4 = addSpawn(LOST_DIMENSION_EVIL, npc, false, 180000, false);
					addAttackPlayerDesire(mob4, killer);
					break;
				}
				case NEPHILM_ROYAL_GUARD:
				{
					final Npc mob5 = addSpawn(WANDERING_SPIRIT, npc, false, 180000, false);
					addAttackPlayerDesire(mob5, killer);
					break;
				}
				case WANDERING_OF_DIMENSION:
				case WANDERING_SPIRIT:
				case LOST_SOUL_DIMENSION:
				case LOST_DIMENSION_EVIL:
				case ROAMING_VENGEANCE:
				{
					if (giveItemRandomly(killer, npc, ATTACKERS_SOUL, 1, 100, 1.0, true))
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