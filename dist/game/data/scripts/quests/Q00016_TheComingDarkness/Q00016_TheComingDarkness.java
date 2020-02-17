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
package quests.Q00016_TheComingDarkness;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q00017_LightAndDarkness.Q00017_LightAndDarkness;

/**
 * The Coming Darkness (16)
 * @author Gladicek
 */
public class Q00016_TheComingDarkness extends Quest
{
	// NPCs
	private static final int HIERARCH = 31517;
	private static final int EVIL_ALTAR_1 = 31512;
	private static final int EVIL_ALTAR_2 = 31513;
	private static final int EVIL_ALTAR_3 = 31514;
	private static final int EVIL_ALTAR_4 = 31515;
	private static final int EVIL_ALTAR_5 = 31516;
	// Item
	private static final int CRYSTAL_OF_SEAL = 7167;
	// Misc
	private static final int MIN_LEVEL = 62;
	
	public Q00016_TheComingDarkness()
	{
		super(16);
		addStartNpc(HIERARCH);
		addTalkId(HIERARCH, EVIL_ALTAR_1, EVIL_ALTAR_2, EVIL_ALTAR_3, EVIL_ALTAR_4, EVIL_ALTAR_5);
		registerQuestItems(CRYSTAL_OF_SEAL);
		addCondMinLevel(MIN_LEVEL, "31517-07.html");
		addCondCompletedQuest(Q00017_LightAndDarkness.class.getSimpleName(), "");
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
				giveItems(player, CRYSTAL_OF_SEAL, 5);
				break;
			}
			case "31512-02.htm":
			{
				if (qs.isCond(1))
				{
					if (hasQuestItems(player, CRYSTAL_OF_SEAL))
					{
						qs.setMemoState(1);
						qs.setCond(2);
						takeItems(player, CRYSTAL_OF_SEAL, 1);
						htmltext = event;
					}
					else
					{
						htmltext = "31512-03.html";
					}
				}
				break;
			}
			case "31513-02.htm":
			{
				if (qs.isCond(2))
				{
					if (hasQuestItems(player, CRYSTAL_OF_SEAL))
					{
						qs.setMemoState(2);
						qs.setCond(3);
						takeItems(player, CRYSTAL_OF_SEAL, 1);
						htmltext = event;
					}
					else
					{
						htmltext = "31513-03.html";
					}
				}
				break;
			}
			case "31514-02.htm":
			{
				if (qs.isCond(3))
				{
					if (hasQuestItems(player, CRYSTAL_OF_SEAL))
					{
						qs.setMemoState(3);
						qs.setCond(4);
						takeItems(player, CRYSTAL_OF_SEAL, 1);
						htmltext = event;
					}
					else
					{
						htmltext = "31514-03.html";
					}
				}
				break;
			}
			case "31515-02.htm":
			{
				if (qs.isCond(4))
				{
					if (hasQuestItems(player, CRYSTAL_OF_SEAL))
					{
						qs.setMemoState(4);
						qs.setCond(5);
						takeItems(player, CRYSTAL_OF_SEAL, 1);
						htmltext = event;
					}
					else
					{
						htmltext = "31515-03.html";
					}
				}
				break;
			}
			case "31516-02.htm":
			{
				if (qs.isCond(5))
				{
					if (hasQuestItems(player, CRYSTAL_OF_SEAL))
					{
						qs.setMemoState(5);
						qs.setCond(6);
						takeItems(player, CRYSTAL_OF_SEAL, 1);
						htmltext = event;
					}
					else
					{
						htmltext = "31516-03.html";
					}
					break;
				}
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
						if ((qs.getCond() >= 1) && (qs.getCond() <= 5))
						{
							if (hasQuestItems(player, CRYSTAL_OF_SEAL))
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
						else if (qs.isCond(6))
						{
							addExpAndSp(player, 1_795_524, 79);
							qs.exitQuest(false, true);
							htmltext = "31517-05.html";
						}
					}
						break;
					case EVIL_ALTAR_1:
					{
						if (qs.isCond(1))
						{
							htmltext = "31512-01.html";
						}
						else if (qs.isMemoState(1))
						{
							htmltext = "31512-04.html";
						}
						break;
					}
					case EVIL_ALTAR_2:
					{
						if (qs.isCond(2))
						{
							htmltext = "31513-01.html";
						}
						else if (qs.isMemoState(2))
						{
							htmltext = "31513-04.html";
						}
						break;
					}
					case EVIL_ALTAR_3:
					{
						if (qs.isCond(3))
						{
							htmltext = "31514-01.html";
						}
						else if (qs.isMemoState(3))
						{
							htmltext = "31514-04.html";
						}
						break;
					}
					case EVIL_ALTAR_4:
					{
						if (qs.isCond(4))
						{
							htmltext = "31515-01.html";
						}
						else if (qs.isMemoState(4))
						{
							htmltext = "31515-04.html";
						}
						break;
					}
					case EVIL_ALTAR_5:
					{
						if (qs.isCond(5))
						{
							htmltext = "31516-01.html";
						}
						else if (qs.isMemoState(5))
						{
							htmltext = "31516-04.html";
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
