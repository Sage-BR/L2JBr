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
package quests.Q00564_BasicMissionKartiasLabyrinthSolo;

import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.serverpackets.ExTutorialShowId;

/**
 * Q00564_KartiasLabyrinthSolo
 * @URL https://l2wiki.com/Basic_Mission:_Kartia%27s_Labyrinth_(Solo)
 * @author NightBR
 */
public class Q00564_BasicMissionKartiasLabyrinthSolo extends Quest
{
	// NPCs
	private static final int PENNY = 34413;
	private static final int KARTIA = 33647;
	// Reward's
	private static final long EXP = 1409345453;
	private static final int SP = 3968411;
	private static final int SCROLL_OF_ESCAPE_KARTIAS_LABYRINTH = 39497;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int MAX_LEVEL = 99;
	
	public Q00564_BasicMissionKartiasLabyrinthSolo()
	{
		super(564);
		addStartNpc(PENNY);
		addTalkId(PENNY, KARTIA);
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
			case "34413-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34413-06.html":
			{
				// Show Service/Help/Instance Zone page
				player.sendPacket(new ExTutorialShowId(29));
				htmltext = event;
				break;
			}
			case "34413-07.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "34413-09.html":
			{
				StringBuilder str = new StringBuilder("000");
				checkQuestCompleted(player, str); // Initialize the array with all quests completed
				
				if (str.indexOf("1") == -1) // verify if no quest completed
				{
					htmltext = "34413-07.html";
				}
				else
				{
					if (str.charAt(0) == '1')
					{
						addFactionPoints(player, Faction.ADVENTURE_GUILD, 100); // add 100 points to ADVENTURE_GUILD Faction
					}
					if (str.charAt(1) == '1')
					{
						addFactionPoints(player, Faction.ADVENTURE_GUILD, 125); // add 125 points to ADVENTURE_GUILD Faction
					}
					if (str.charAt(2) == '1')
					{
						addFactionPoints(player, Faction.ADVENTURE_GUILD, 150); // add 150 points to ADVENTURE_GUILD Faction
					}
					giveItems(player, SCROLL_OF_ESCAPE_KARTIAS_LABYRINTH, 1);
					addExpAndSp(player, EXP, SP);
					qs.exitQuest(QuestType.DAILY, true);
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
						htmltext = qs.isCond(2) ? "34413-07.html" : "34413-08.html";
						break;
					}
					case KARTIA:
					{
						if (qs.isCond(2))
						{
							StringBuilder str = new StringBuilder("000");
							checkQuestCompleted(player, str); // Initialize the array with all quests completed
							
							if (str.indexOf("1") != -1) // verify if any quest completed
							{
								qs.setCond(4, true);
								htmltext = "33647-02.html";
							}
							else
							{
								htmltext = "33647-01.html";
							}
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
	
	private StringBuilder checkQuestCompleted(PlayerInstance player, StringBuilder string)
	{
		int index = 0;
		char ch = '1';
		final QuestState st1 = player.getQuestState("Q00497_IncarnationOfGreedZellakaSolo");
		if ((st1 != null) && st1.isCompleted())
		{
			index = 0;
			string.setCharAt(index, ch);
		}
		final QuestState st2 = player.getQuestState("Q00498_IncarnationOfJealousyPellineSolo");
		if ((st2 != null) && st2.isCompleted())
		{
			index = 1;
			string.setCharAt(index, ch);
		}
		final QuestState st3 = player.getQuestState("Q00499_IncarnationOfGluttonyKaliosSolo");
		if ((st3 != null) && st3.isCompleted())
		{
			index = 2;
			string.setCharAt(index, ch);
		}
		return string;
	}
}
