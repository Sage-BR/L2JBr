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
package quests.Q00485_HotSpringWater;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Hot Spring Water (485)
 * @author St3eT
 */
public class Q00485_HotSpringWater extends Quest
{
	// NPCs
	private static final int ADVENTURER = 32327;
	private static final int WALDERAL = 30844;
	private static final int[] MONSTERS =
	{
		21314, // Hot Springs Bandersnatchling
		21315, // Hot Springs Buffalo
		21316, // Hot Springs Flava
		21317, // Hot Springs Atroxspawn
		21318, // Hot Springs Antelope
		21319, // Hot Springs Nepenthes
		21320, // Hot Springs Yeti
		21321, // Hot Springs Atrox
		21322, // Hot Springs Bandersnatch
		21323, // Hot Springs Grendel
	};
	// Items
	private static final int WATER = 19497; // Hot Springs Water Sample
	// Misc
	private static final int MIN_LEVEL = 70;
	private static final int MAX_LEVEL = 74;
	
	public Q00485_HotSpringWater()
	{
		super(485);
		addStartNpc(ADVENTURER);
		addTalkId(ADVENTURER, WALDERAL);
		addKillId(MONSTERS);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "");
		registerQuestItems(WATER);
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
			case "32327-02.htm":
			case "32327-03.htm":
			{
				htmltext = event;
				break;
			}
			case "32327-04.htm":
			{
				qs.startQuest();
				htmltext = event;
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
				if (npc.getId() == ADVENTURER)
				{
					htmltext = "32327-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = npc.getId() == ADVENTURER ? "32327-05.html" : "30844-01.html";
				}
				else if (qs.isCond(2))
				{
					if (npc.getId() == ADVENTURER)
					{
						htmltext = "32327-06.html";
					}
					else if (npc.getId() == WALDERAL)
					{
						qs.exitQuest(QuestType.DAILY, true);
						giveAdena(player, 371_745, true);
						if (player.getLevel() >= MIN_LEVEL)
						{
							addExpAndSp(player, 9_483_000, 2_275);
						}
						htmltext = "30844-02.html";
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if ((npc.getId() == ADVENTURER) && qs.isNowAvailable())
				{
					qs.setState(State.CREATED);
					htmltext = "32327-01.html";
				}
				else if ((npc.getId() == WALDERAL) && qs.isCompleted() && !qs.isNowAvailable())
				{
					htmltext = "30844-03.html";
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
		
		if ((qs != null) && qs.isCond(1))
		{
			if (giveItemRandomly(killer, WATER, 1, 40, 0.4, true))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}