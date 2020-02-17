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
package quests.Q10733_TheTestForSurvival;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.serverpackets.ExTutorialShowId;

import quests.Q10732_AForeignLand.Q10732_AForeignLand;

/**
 * The Test For Survival (10733)
 * @URL https://l2wiki.com/The_Test_for_Survival
 * @author Sdw
 */
public class Q10733_TheTestForSurvival extends Quest
{
	// NPCs
	private static final int GERETH = 33932;
	private static final int DIA = 34005;
	private static final int KATALIN = 33943;
	private static final int AYANTHE = 33942;
	// Items
	private static final int GERETH_RECOMMENDATION = 39519;
	// Misc
	private static final int MAX_LEVEL = 20;
	
	public Q10733_TheTestForSurvival()
	{
		super(10733);
		addStartNpc(GERETH);
		addTalkId(GERETH, DIA, KATALIN, AYANTHE);
		registerQuestItems(GERETH_RECOMMENDATION);
		addCondMaxLevel(MAX_LEVEL, "33932-04.htm");
		addCondCompletedQuest(Q10732_AForeignLand.class.getSimpleName(), "33932-04.htm");
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
			case "34005-02.html":
			{
				break;
			}
			case "33932-02.htm":
			{
				qs.startQuest();
				player.sendPacket(new ExTutorialShowId(11));
				giveItems(player, GERETH_RECOMMENDATION, 1);
				break;
			}
			case "34005-03.html":
			{
				if (qs.isCond(1) && hasQuestItems(player, GERETH_RECOMMENDATION))
				{
					if (player.isMageClass())
					{
						qs.setCond(3, true);
					}
					else
					{
						qs.setCond(2, true);
						htmltext = "34005-04.html";
					}
				}
				break;
			}
			case "33942-02.html":
			case "33943-02.html":
			{
				if ((qs.isCond(2) || qs.isCond(3)) && hasQuestItems(player, GERETH_RECOMMENDATION))
				{
					addExpAndSp(player, 295, 2);
					qs.exitQuest(false, true);
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
		if (qs.isCompleted())
		{
			return getAlreadyCompletedMsg(player);
		}
		
		String htmltext = getNoQuestMsg(player);
		switch (npc.getId())
		{
			case GERETH:
			{
				if (qs.isCreated())
				{
					htmltext = "33932-01.htm";
				}
				else if (qs.isStarted())
				{
					htmltext = "33932-03.html";
				}
				break;
			}
			case DIA:
			{
				if (qs.isStarted() && qs.isCond(1))
				{
					htmltext = "34005-01.html";
				}
				break;
			}
			case KATALIN:
			{
				if (qs.isStarted() && qs.isCond(2))
				{
					htmltext = "33943-01.html";
				}
				break;
			}
			case AYANTHE:
			{
				if (qs.isStarted() && qs.isCond(3))
				{
					htmltext = "33942-01.html";
				}
				break;
			}
		}
		return htmltext;
	}
}