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
package quests.Q10311_PeacefulDaysAreOver;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;

import quests.Q10312_AbandonedGodsCreature.Q10312_AbandonedGodsCreature;

/**
 * Peaceful Days are Over (10311)
 * @URL https://l2wiki.com/Peaceful_Days_are_Over
 * @author Gigi
 */
public class Q10311_PeacefulDaysAreOver extends Quest
{
	// npc
	private static final int SELINA = 33032;
	private static final int SLASKI = 32893;
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q10311_PeacefulDaysAreOver()
	{
		super(10311);
		addStartNpc(SELINA);
		addTalkId(SELINA, SLASKI);
		addCondMinLevel(MIN_LEVEL, "33032-00.htm");
		addCondCompletedQuest(Q10312_AbandonedGodsCreature.class.getSimpleName(), "33032-00.htm");
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
			case "33032-02.htm":
			case "33032-03.htm":
			case "32893-02.html":
			case "32893-03.html":
			case "32893-04.html":
			{
				htmltext = event;
				break;
			}
			case "33032-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32893-05.html":
			{
				giveAdena(player, 489220, false);
				addExpAndSp(player, 7168395, 1720);
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
			case SELINA:
			{
				if (qs.isCreated())
				{
					htmltext = "33032-01.htm";
				}
				else if (qs.isCond(1))
				{
					htmltext = "33032-05.html";
				}
				else if (qs.isCompleted())
				{
					htmltext = "Complete.html";
				}
				break;
			}
			case SLASKI:
			{
				if (qs.isCond(1))
				{
					htmltext = "32893-01.html";
				}
				else if (qs.isCompleted())
				{
					htmltext = "32893-00.html";
				}
				break;
			}
		}
		return htmltext;
	}
}