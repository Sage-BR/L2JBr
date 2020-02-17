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
package quests.Q00566_BasicMissionFairySettlementEast;

import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Basic Mission: Fairy Settlement - East (566)
 * @URL https://l2wiki.com/Basic_Mission:_Fairy_Settlement_-_East
 * @author Dmitri
 */
public class Q00566_BasicMissionFairySettlementEast extends Quest
{
	// NPCs
	private static final int PENNY = 34413;
	private static final int NERUPA = 30370;
	private static final int ELISA = 30848;
	private static final int RADA = 33100;
	private static final int DE_VILLAGE_TELEPORT_DEVICE = 30134;
	// Rewards
	private static final long EXP = 793414440;
	private static final int SP = 793410;
	private static final int FP = 250; // Faction points
	private static final int SCROLL_OF_ESCAPE_FAIRY_COLONY = 39498;
	private static final int SCROLL_OF_ESCAPE_TOWN_OF_ADEN = 48413;
	// Misc
	private static final int MIN_LEVEL = 90;
	private static final int MAX_LEVEL = 94;
	// Location
	private static final Location TOWN_OF_ADEN = new Location(146632, 26760, -2213);
	
	public Q00566_BasicMissionFairySettlementEast()
	{
		super(566);
		addStartNpc(PENNY);
		addTalkId(PENNY, NERUPA, ELISA, RADA, DE_VILLAGE_TELEPORT_DEVICE);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "nolevel.html");
		addFactionLevel(Faction.ADVENTURE_GUILD, 4, "34413-11.html");
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
			case "34413-06.html":
			case "33100-02.html":
			case "30370-03.html":
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
			case "34413-05.html":
			{
				qs.setCond(5, true);
				htmltext = event;
				break;
			}
			case "34413-09.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "34413-07.html":
			{
				StringBuilder str = new StringBuilder("00");
				checkQuestCompleted(player, str); // Initialize the array with all quests completed
				
				if (str.indexOf("11") != -1) // verify if all quests completed
				{
					giveItems(player, SCROLL_OF_ESCAPE_FAIRY_COLONY, 1);
					addExpAndSp(player, EXP, SP);
					addFactionPoints(player, Faction.ADVENTURE_GUILD, FP); // add FP points to ADVENTURE_GUILD Faction
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
				}
				else
				{
					htmltext = "34413-08.html";
				}
				break;
			}
			case "30848-02.html": // ELISA
			{
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "33100-03.html": // RADA
			{
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "30370-04.html": // LEPATHIA
			{
				giveItems(player, SCROLL_OF_ESCAPE_TOWN_OF_ADEN, 1);
				htmltext = event;
				break;
			}
			case "usescroll":
			{
				// TODO: force player to use item SCROLL_OF_ESCAPE_TOWN_OF_ADEN
				player.teleToLocation(TOWN_OF_ADEN); // Town of Aden near Npc Penny - temp solution
				takeItems(player, SCROLL_OF_ESCAPE_TOWN_OF_ADEN, -1); // remove SOE - temp solution
				qs.setCond(8, true);
				break;
			}
			case "keepscroll":
			{
				qs.setCond(8, true);
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
						if (qs.getCond() == 1)
						{
							htmltext = "34413-04.htm";
						}
						else if (qs.getCond() == 2)
						{
							// htmltext = qs.getCond() == 2 ? "34413-10.htm" : "34413-06.html";
							htmltext = "33509-10.htm";
						}
						else if (qs.getCond() == 5)
						{
							// htmltext = qs.getCond() == 5 ? "34413-08.htm" : "34413-06.html";
							htmltext = "34413-08.html";
						}
						else
						{
							htmltext = "34413-06.html";
						}
						break;
					}
					case ELISA:
					{
						htmltext = "30848-01.html";
						break;
					}
					case RADA:
					{
						htmltext = "33100-01.html";
						break;
					}
					case DE_VILLAGE_TELEPORT_DEVICE:
					{
						qs.setCond(5, true);
						htmltext = "30134-01.html";
						break;
					}
					case NERUPA:
					{
						if (qs.getCond() == 5)
						{
							qs.setCond(6, true);
							htmltext = "30370-01.html";
						}
						else
						{
							StringBuilder str = new StringBuilder("00");
							checkQuestCompleted(player, str); // Initialize the array with all quests completed
							if (str.indexOf("11") != -1) // verify if all quests completed
							{
								qs.setCond(7, true);
								htmltext = "30370-02.html";
							}
							else
							{
								htmltext = "30370-01.html";
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
		final QuestState st1 = player.getQuestState("Q00774_DreamingOfPeace");
		if ((st1 != null) && st1.isCompleted())
		{
			index = 0;
			string.setCharAt(index, ch);
		}
		final QuestState st2 = player.getQuestState("Q00586_MutatedCreatures");
		if ((st2 != null) && st2.isCompleted())
		{
			index = 1;
			string.setCharAt(index, ch);
		}
		return string;
	}
}
