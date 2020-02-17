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
package quests.Q00483_IntendedTactic;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Intended Tactic (483)
 * @URL https://l2wiki.com/Intended_Tactic
 * @author Gigi
 */
public class Q00483_IntendedTactic extends Quest
{
	// NPC
	private static final int ENDE = 33357;
	// Monsters
	private static final int[] MOBS =
	{
		23069, // Vladimir's Warrior
		23070, // Lazearth' Warrior
		23071, // Beastian
		23072, // Birestian
		23073, // Kenneth Bastian
		23074, // Heaven's Palace Noble Warrior
		23075 // Heaven's Palace Noble Knight
	};
	private static final int[] BOSSES =
	{
		25809, // Vladimir
		25811, // Lazearth
		25815 // Ken
	};
	// Items
	private static final int LOYAL_SERVANS_BLOOD = 17736;
	private static final int TRUTTHFUL_ONES_BLOOD = 17737;
	private static final int TOKEN_OF_INSOLENCE_TOWER = 17624;
	// Misc
	private static final int MIN_LEVEL = 48;
	
	public Q00483_IntendedTactic()
	{
		super(483);
		addStartNpc(ENDE);
		addTalkId(ENDE);
		addKillId(MOBS);
		addKillId(BOSSES);
		addCondMinLevel(MIN_LEVEL, "33357-02.htm");
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
			case "33357-05.htm":
			case "33357-06.htm":
			case "33357-07.htm":
			{
				htmltext = event;
				break;
			}
			case "33357-08.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "endquest":
			{
				if (getQuestItemsCount(player, TRUTTHFUL_ONES_BLOOD) >= 10)
				{
					takeItems(player, LOYAL_SERVANS_BLOOD, -1);
					takeItems(player, TRUTTHFUL_ONES_BLOOD, -1);
					giveItems(player, TOKEN_OF_INSOLENCE_TOWER, 1);
					addExpAndSp(player, 1500000, 360);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = "33357-12.html";
					break;
				}
				takeItems(player, LOYAL_SERVANS_BLOOD, -1);
				addExpAndSp(player, 1500000, 360);
				qs.exitQuest(QuestType.DAILY, true);
				htmltext = "33357-11.html";
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
		if (npc.getId() == ENDE)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "33357-03.html";
						break;
					}
					qs.setState(State.CREATED);
					break;
				}
				case State.CREATED:
				{
					htmltext = "33357-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33357-09.html";
					}
					else if (qs.isStarted() && qs.isCond(2))
					{
						htmltext = "33357-10.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted() && !qs.isNowAvailable())
		{
			htmltext = "33357-03.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && (CommonUtil.contains(MOBS, npc.getId())))
		{
			if (giveItemRandomly(killer, npc, LOYAL_SERVANS_BLOOD, 1, 10, 0.10, true))
			{
				qs.setCond(2, true);
			}
		}
		if ((qs != null) && qs.isCond(2) && (CommonUtil.contains(BOSSES, npc.getId())))
		{
			if (giveItemRandomly(killer, npc, TRUTTHFUL_ONES_BLOOD, 1, 10, 1.0, true))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}