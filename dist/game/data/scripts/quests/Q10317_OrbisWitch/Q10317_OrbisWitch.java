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
package quests.Q10317_OrbisWitch;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;

import quests.Q10316_UndecayingMemoryOfThePast.Q10316_UndecayingMemoryOfThePast;

/**
 * Orbis' Witch (10317)
 * @URL https://l2wiki.com/Orbis%27_Witch
 * @author Gigi
 */
public class Q10317_OrbisWitch extends Quest
{
	// NPCs
	private static final int OPERA = 32946;
	private static final int LYDIA = 32892;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10317_OrbisWitch()
	{
		super(10317);
		addStartNpc(OPERA);
		addTalkId(OPERA, LYDIA);
		
		addCondMinLevel(MIN_LEVEL, "32946-09.html");
		addCondCompletedQuest(Q10316_UndecayingMemoryOfThePast.class.getSimpleName(), "32946-09.html");
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
			case "32946-02.html":
			case "32946-03.html":
			case "32946-04.html":
			case "32946-05.htm":
			{
				htmltext = event;
				break;
			}
			case "32946-06.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32892-02.html":
			{
				giveAdena(player, 506760, false);
				addExpAndSp(player, 7412805, 1779);
				qs.exitQuest(false, true);
				htmltext = event;
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
		
		switch (npc.getId())
		{
			case OPERA:
			{
				if (qs.isCreated())
				{
					htmltext = "32946-01.htm";
				}
				else if (qs.isCond(1))
				{
					htmltext = "32946-07.html";
				}
				else if (qs.isCompleted())
				{
					htmltext = "32946-08.html";
				}
				break;
			}
			case LYDIA:
			{
				if (qs.isCond(1))
				{
					htmltext = "32892-01.html";
					break;
				}
			}
		}
		return htmltext;
	}
}