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
package quests.Q00017_LightAndDarkness;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q00015_SweetWhispers.Q00015_SweetWhispers;

/**
 * Light And Darkness (17)
 * @author Gladicek
 */
public class Q00017_LightAndDarkness extends Quest
{
	// NPCs
	private static final int HIERARCH = 31517;
	private static final int SAINT_ALTAR_1 = 31508;
	private static final int SAINT_ALTAR_2 = 31509;
	private static final int SAINT_ALTAR_3 = 31510;
	private static final int SAINT_ALTAR_4 = 31511;
	// Item
	private static final int BLOOD_OF_SAINT = 7168;
	// Misc
	private static final int MIN_LEVEL = 61;
	
	public Q00017_LightAndDarkness()
	{
		super(17);
		addStartNpc(HIERARCH);
		addTalkId(HIERARCH, SAINT_ALTAR_1, SAINT_ALTAR_2, SAINT_ALTAR_3, SAINT_ALTAR_4);
		registerQuestItems(BLOOD_OF_SAINT);
		addCondMinLevel(MIN_LEVEL, "31517-07.html");
		addCondCompletedQuest(Q00015_SweetWhispers.class.getSimpleName(), "");
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
			case "31517-03.htm":
			{
				qs.startQuest();
				giveItems(player, BLOOD_OF_SAINT, 4);
			}
				break;
			case "31508-02.htm":
			{
				if (qs.isCond(1))
				{
					if (hasQuestItems(player, BLOOD_OF_SAINT))
					{
						qs.setMemoState(1);
						qs.setCond(2);
						takeItems(player, BLOOD_OF_SAINT, 1);
						htmltext = event;
					}
					else
					{
						htmltext = "31508-03.html";
					}
				}
				break;
			}
			case "31509-02.htm":
			{
				if (qs.isCond(2))
				{
					if (hasQuestItems(player, BLOOD_OF_SAINT))
					{
						qs.setMemoState(2);
						qs.setCond(3);
						takeItems(player, BLOOD_OF_SAINT, 1);
						htmltext = event;
					}
					else
					{
						htmltext = "31512-03.html";
					}
				}
				break;
			}
			case "31510-02.htm":
			{
				if (qs.isCond(3))
				{
					if (hasQuestItems(player, BLOOD_OF_SAINT))
					{
						qs.setMemoState(3);
						qs.setCond(4);
						takeItems(player, BLOOD_OF_SAINT, 1);
						htmltext = event;
					}
					else
					{
						htmltext = "31512-03.html";
					}
				}
				break;
			}
			case "31511-02.htm":
			{
				if (qs.isCond(4))
				{
					if (hasQuestItems(player, BLOOD_OF_SAINT))
					{
						qs.setMemoState(4);
						qs.setCond(5);
						takeItems(player, BLOOD_OF_SAINT, 1);
						htmltext = event;
					}
					else
					{
						htmltext = "31512-03.html";
					}
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
				if (npc.getId() == HIERARCH)
				{
					htmltext = "31517-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case HIERARCH:
					{
						if ((qs.getCond() >= 1) && (qs.getCond() <= 4))
						{
							if (hasQuestItems(player, BLOOD_OF_SAINT))
							{
								htmltext = "31517-04.html";
							}
							else
							{
								qs.exitQuest(true, true);
								htmltext = "31517-06.html";
							}
							break;
						}
						else if (qs.isCond(5))
						{
							addExpAndSp(player, 1_469_840, 352);
							qs.exitQuest(false, true);
							htmltext = "31517-05.html";
						}
					}
						break;
					case SAINT_ALTAR_1:
					{
						if (qs.isCond(1))
						{
							htmltext = "31508-01.html";
						}
						else if (qs.isMemoState(1))
						{
							htmltext = "31508-04.html";
						}
						break;
					}
					case SAINT_ALTAR_2:
					{
						if (qs.isCond(2))
						{
							htmltext = "31509-01.html";
						}
						else if (qs.isMemoState(2))
						{
							htmltext = "31509-04.html";
						}
						break;
					}
					case SAINT_ALTAR_3:
					{
						if (qs.isCond(3))
						{
							htmltext = "31510-01.html";
						}
						else if (qs.isMemoState(3))
						{
							htmltext = "31510-04.html";
						}
						break;
					}
					case SAINT_ALTAR_4:
					{
						if (qs.isCond(4))
						{
							htmltext = "31511-01.html";
						}
						else if (qs.isMemoState(4))
						{
							htmltext = "31511-04.html";
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
