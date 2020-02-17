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
package quests.Q10297_GrandOpeningComeToOurPub;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Grand Opening! Come to Our Pub! (10297)
 * @author Gladicek
 */
public class Q10297_GrandOpeningComeToOurPub extends Quest
{
	// NPCs
	private static final int SETTLEN = 34180;
	private static final int LOLLIA = 34182;
	private static final int HANNA = 34183;
	private static final int MEI = 34186;
	private static final int BRODIEN = 34184;
	private static final int LUPIA = 34185;
	private static final int LAILLY = 34181;
	// Item
	private static final int SCROLL_OF_ESCAPE_MYSTIC_TAVERN = 46564;
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10297_GrandOpeningComeToOurPub()
	{
		super(10297);
		addStartNpc(SETTLEN);
		addTalkId(SETTLEN, LOLLIA, HANNA, MEI, BRODIEN, LUPIA, LAILLY);
		addCondMinLevel(MIN_LEVEL, "34180-05.htm");
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
			case "34180-02.htm":
			case "34182-02.html":
			case "34183-02.html":
			case "34183-03.html":
			case "34184-02.html":
			case "34184-03.html":
			case "34185-02.html":
			case "34185-03.html":
			case "34186-02.html":
			case "34186-03.html":
			{
				htmltext = event;
				break;
			}
			case "34180-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34182-04.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "34183-04.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "34186-04.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "34184-04.html":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "34185-04.html":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			case "34181-02.html":
			{
				if (qs.isCond(6))
				{
					giveItems(player, SCROLL_OF_ESCAPE_MYSTIC_TAVERN, 5);
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
				if (npc.getId() == SETTLEN)
				{
					htmltext = "34180-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SETTLEN:
					{
						if (qs.isCond(1))
						{
							htmltext = "34180-04.html";
						}
						break;
					}
					case LOLLIA:
					{
						if (qs.isCond(1))
						{
							htmltext = "34182-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "34182-05.html";
						}
						break;
					}
					case HANNA:
					{
						if (qs.isCond(2))
						{
							htmltext = "34183-01.html";
						}
						else if (qs.isCond(3))
						{
							htmltext = "34183-05.html";
						}
						break;
					}
					case MEI:
					{
						if (qs.isCond(3))
						{
							htmltext = "34186-01.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "34186-05.html";
						}
						break;
					}
					case BRODIEN:
					{
						if (qs.isCond(4))
						{
							htmltext = "34184-01.html";
						}
						else if (qs.isCond(5))
						{
							htmltext = "34184-05.html";
						}
						break;
					}
					case LUPIA:
					{
						if (qs.isCond(5))
						{
							htmltext = "34185-01.html";
						}
						else if (qs.isCond(6))
						{
							htmltext = "34185-05.html";
						}
						break;
					}
					case LAILLY:
					{
						if (qs.isCond(6))
						{
							htmltext = "34181-01.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == SETTLEN)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
}