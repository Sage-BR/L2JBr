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
package quests.Q00563_BasicMissionBloodySwampland;

import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Q00563_BasicMissionBloodySwampland
 * @URL https://l2wiki.com/Basic_Mission:_Bloody_Swampland
 * @author NightBR
 */
public class Q00563_BasicMissionBloodySwampland extends Quest
{
	// NPCs
	private static final int PENNY = 34413;
	private static final int VOLLODOS = 30137;
	// Reward's
	private static final long EXP = 429526470;
	private static final int SP = 429510;
	private static final int FP = 220; // Faction points
	private static final int SCROLL_OF_ESCAPE_BLOODY_SWAMPLAND = 39494;
	// Misc
	private static final int MIN_LEVEL = 86;
	private static final int MAX_LEVEL = 90;
	
	public Q00563_BasicMissionBloodySwampland()
	{
		super(563);
		addStartNpc(PENNY);
		addTalkId(PENNY, VOLLODOS);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "nolevel.html");
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
			case "34413-02.htm":
			case "34413-03.htm":
			case "34413-05.html":
			{
				htmltext = event;
				break;
			}
			case "34413-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34413-06.html":
			{
				StringBuilder str = new StringBuilder("00");
				checkQuestCompleted(player, str); // Initialize the array with all quests completed
				
				if (str.indexOf("11") != -1) // verify if all quests completed
				{
					giveItems(player, SCROLL_OF_ESCAPE_BLOODY_SWAMPLAND, 1);
					addExpAndSp(player, EXP, SP);
					addFactionPoints(player, Faction.ADVENTURE_GUILD, FP); // add FP points to ADVENTURE_GUILD Faction
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
				}
				else
				{
					htmltext = "34413-07.html";
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
				if (npc.getId() == PENNY)
				{
					htmltext = "34413-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case PENNY:
					{
						htmltext = qs.isCond(1) ? "34413-07.html" : "34413-05.html";
						break;
					}
					case VOLLODOS:
					{
						if (qs.isCond(1))
						{
							qs.setCond(2, true);
							htmltext = "30137-01.html";
						}
						else if (qs.isCond(2))
						{
							StringBuilder str = new StringBuilder("00");
							checkQuestCompleted(player, str); // Initialize the array with all quests completed
							
							if (str.indexOf("11") != -1) // verify if all quests completed
							{
								qs.setCond(3, true);
								htmltext = "30137-02.html";
							}
							else
							{
								htmltext = "30137-01.html";
							}
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable())
				{
					qs.setState(State.CREATED);
					htmltext = "34413-01.htm";
				}
				else
				{
					htmltext = getAlreadyCompletedMsg(player, QuestType.DAILY);
				}
				break;
			}
		}
		return htmltext;
	}
	
	private StringBuilder checkQuestCompleted(PlayerInstance player, StringBuilder string)
	{
		int index = 0;
		char ch = '1';
		final QuestState st1 = player.getQuestState("Q00583_MeaningOfSurvival");
		if ((st1 != null) && st1.isCompleted())
		{
			index = 0;
			string.setCharAt(index, ch);
		}
		final QuestState st2 = player.getQuestState("Q00584_NeverSayGoodBye");
		if ((st2 != null) && st2.isCompleted())
		{
			index = 1;
			string.setCharAt(index, ch);
		}
		return string;
	}
}
