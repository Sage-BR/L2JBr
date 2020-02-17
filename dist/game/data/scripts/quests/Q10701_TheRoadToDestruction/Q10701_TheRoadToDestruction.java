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
package quests.Q10701_TheRoadToDestruction;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * The Road to Destruction (10701)
 * @author Gladicek
 */
public class Q10701_TheRoadToDestruction extends Quest
{
	// NPCs
	private static final int KEUCEREUS = 32548;
	private static final int ALLENOS = 32526;
	// Item
	private static final int KEUCEREUS_INTRODUCTION_SOD = 38577;
	// Misc
	private static final int MIN_LEVEL = 93;
	
	public Q10701_TheRoadToDestruction()
	{
		super(10701);
		addStartNpc(KEUCEREUS);
		addTalkId(KEUCEREUS, ALLENOS);
		addCondMinLevel(MIN_LEVEL, "32548-06.htm");
		registerQuestItems(KEUCEREUS_INTRODUCTION_SOD);
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
			case "32548-02.htm":
			case "32548-03.htm":
			{
				htmltext = event;
				break;
			}
			case "32548-04.html":
			{
				qs.startQuest();
				giveItems(player, KEUCEREUS_INTRODUCTION_SOD, 1);
				break;
			}
			case "32526-02.html":
			{
				if (qs.isCond(1))
				{
					giveAdena(player, 17612, true);
					addExpAndSp(player, 8_173_305, 1961);
					qs.exitQuest(false, true);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == KEUCEREUS)
				{
					htmltext = "32548-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				htmltext = npc.getId() == KEUCEREUS ? "32548-05.html" : "32526-01.html";
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == KEUCEREUS)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
}