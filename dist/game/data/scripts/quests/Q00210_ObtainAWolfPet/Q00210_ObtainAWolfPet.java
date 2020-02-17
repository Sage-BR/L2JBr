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
package quests.Q00210_ObtainAWolfPet;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Obtain a Wolf Pet (210)
 * @author Stayway
 */
public class Q00210_ObtainAWolfPet extends Quest
{
	// NPCs
	private static final int LUNDY = 30827;
	private static final int BELLA = 30256;
	private static final int BYNN = 30335;
	private static final int SYDNIA = 30321;
	// Item
	private static final int WOLF_COLLAR = 2375;
	// Misc
	private static final int MIN_LEVEL = 15;
	
	public Q00210_ObtainAWolfPet()
	{
		super(210);
		addStartNpc(LUNDY);
		addTalkId(LUNDY, BELLA, BYNN, SYDNIA);
		addCondMinLevel(MIN_LEVEL, "no_level.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30827-02.htm":
			case "30827-04.htm":
			case "30256-02.html":
			{
				htmltext = event;
				break;
			}
			case "30256-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "30827-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30335-02.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3);
					htmltext = event;
				}
				break;
			}
			case "30321-02.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4);
					htmltext = event;
				}
				break;
			}
			case "30827-05.html":
			{
				if (qs.isCond(4))
				{
					rewardItems(player, WOLF_COLLAR, 1);
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
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == LUNDY)
				{
					htmltext = "30827-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case LUNDY:
					{
						if (qs.isCond(1))
						{
							htmltext = "30827-07.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "30827-07.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "30827-04.html";
						}
						break;
					}
					case BELLA:
					{
						if (qs.isCond(1))
						{
							htmltext = "30256-01.html";
						}
						break;
					}
					case BYNN:
					{
						if (qs.isCond(2))
						{
							htmltext = "30335-01.html";
						}
						break;
					}
					case SYDNIA:
					{
						if (qs.isCond(3))
						{
							htmltext = "30321-01.html";
						}
						break;
					}
				}
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