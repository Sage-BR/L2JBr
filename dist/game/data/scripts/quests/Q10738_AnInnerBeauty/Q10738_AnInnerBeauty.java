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
package quests.Q10738_AnInnerBeauty;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;

import quests.Q10737_GrakonsWarehouse.Q10737_GrakonsWarehouse;

/**
 * An Inner Beauty (10738)
 * @author Sdw
 */
public class Q10738_AnInnerBeauty extends Quest
{
	// NPCs
	private static final int GRAKON = 33947;
	private static final int EVNA = 33935;
	// Items
	private static final int GRAKON_S_NOTE = 39521;
	// Misc
	private static final int MIN_LEVEL = 5;
	private static final int MAX_LEVEL = 20;
	
	public Q10738_AnInnerBeauty()
	{
		super(10738);
		addStartNpc(GRAKON);
		addTalkId(GRAKON, EVNA);
		
		addCondRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33947-00.htm");
		addCondCompletedQuest(Q10737_GrakonsWarehouse.class.getSimpleName(), "33947-00.htm");
		registerQuestItems(GRAKON_S_NOTE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33947-02.htm":
			case "33947-03.htm":
			case "33935-02.html":
			{
				break;
			}
			case "33947-04.htm":
			{
				qs.startQuest();
				qs.setCond(2); // arrow hack
				qs.setCond(1);
				giveItems(player, GRAKON_S_NOTE, 1);
				break;
			}
			case "33935-03.html":
			{
				if (qs.isStarted())
				{
					addExpAndSp(player, 2625, 1);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		
		if (npc.getId() == GRAKON)
		{
			if (qs.isCreated())
			{
				htmltext = "33947-01.htm";
			}
			else if (qs.isStarted())
			{
				htmltext = "33947-05.html";
			}
		}
		else if (npc.getId() == EVNA)
		{
			if (qs.isStarted())
			{
				htmltext = "33935-01.html";
			}
		}
		return htmltext;
	}
}
