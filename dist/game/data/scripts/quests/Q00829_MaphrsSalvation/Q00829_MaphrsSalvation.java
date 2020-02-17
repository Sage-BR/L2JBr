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
package quests.Q00829_MaphrsSalvation;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Maphr's Salvation (829)
 * @URL https://l2wiki.com/Maphr%27s_Salvation
 * @author Liamxroy
 */
public class Q00829_MaphrsSalvation extends Quest
{
	// NPC
	private static final int BLACKSMITH_KLUTO = 34098;
	private static final int BLACKSMITH_KLUTO_FINISH = 34153;
	private static final int TRANSPORT_GOODS_NPC = 34102;
	// Items
	private static final int TRASPORT_GOODS_ITEM = 46373;
	private static final int GLUDIN_HERO_REWARD = 46375;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q00829_MaphrsSalvation()
	{
		super(829);
		addStartNpc(BLACKSMITH_KLUTO);
		addFirstTalkId(TRANSPORT_GOODS_NPC);
		addTalkId(BLACKSMITH_KLUTO, BLACKSMITH_KLUTO_FINISH);
		addCondMinLevel(MIN_LEVEL, "34098-00.htm");
		registerQuestItems(TRASPORT_GOODS_ITEM);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "34098-02.htm":
			case "34098-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34098-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34153-02.html":
			{
				if (qs.isCond(2))
				{
					takeItems(player, -1, TRASPORT_GOODS_ITEM);
					rewardItems(player, GLUDIN_HERO_REWARD, 1);
					addExpAndSp(player, 2175228000L, 5220534);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
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
				if (npc.getId() == BLACKSMITH_KLUTO)
				{
					htmltext = "34098-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == BLACKSMITH_KLUTO)
				{
					htmltext = "34098-05.html";
				}
				else
				{
					htmltext = "34153-01.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable() && (npc.getId() == BLACKSMITH_KLUTO))
				{
					qs.setState(State.CREATED);
					htmltext = "34098-01.htm";
				}
				else
				{
					htmltext = "34098-06.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && (qs.isCond(1)))
		{
			List<PlayerInstance> members = new ArrayList<>();
			if (player.getParty() != null)
			{
				members = player.getParty().getMembers();
			}
			else
			{
				members.add(player);
			}
			for (PlayerInstance member : members)
			{
				final QuestState ms = getQuestState(member, false);
				if ((ms != null) && ms.isCond(1))
				{
					if (getQuestItemsCount(member, TRASPORT_GOODS_ITEM) < 1)
					{
						giveItems(member, TRASPORT_GOODS_ITEM, 1);
					}
					if (getQuestItemsCount(member, TRASPORT_GOODS_ITEM) >= 1)
					{
						ms.setCond(2, true);
					}
				}
			}
			npc.deleteMe();
			return "34102-01.html";
		}
		return null;
	}
}
