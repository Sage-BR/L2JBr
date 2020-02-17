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
package quests.Q00137_TempleChampionPart1;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q00134_TempleMissionary.Q00134_TempleMissionary;
import quests.Q00135_TempleExecutor.Q00135_TempleExecutor;

/**
 * Temple Champion - 1 (137)
 * @author nonom, Gladicek
 */
public class Q00137_TempleChampionPart1 extends Quest
{
	// NPCs
	private static final int SYLVAIN = 30070;
	private static final int MOBS[] =
	{
		20055, // Monster Eye Watchman
		20147, // Hobgoblin
		20265, // Monster Eye Searcher
		20224, // Ol Mahum Ranger
		20205, // Dire Wolf
		20203, // Dion Grizzly
		20291, // Enku Orc Hero
		20292, // Enku Orc Shaman
	};
	// Items
	private static final int FRAGMENT = 10340;
	private static final int EXECUTOR = 10334;
	private static final int MISSIONARY = 10339;
	// Misc
	private static final int MIN_LEVEL = 35;
	private static final int MAX_LEVEL = 41;
	
	public Q00137_TempleChampionPart1()
	{
		super(137);
		addStartNpc(SYLVAIN);
		addTalkId(SYLVAIN);
		addKillId(MOBS);
		addCondMinLevel(MIN_LEVEL, "30070-17.html");
		addCondCompletedQuest(Q00134_TempleMissionary.class.getSimpleName(), "30070-18.html");
		addCondCompletedQuest(Q00135_TempleExecutor.class.getSimpleName(), "30070-18.html");
		registerQuestItems(FRAGMENT);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		String htmltext = event;
		switch (event)
		{
			case "30070-02.htm":
			{
				qs.startQuest();
				break;
			}
			case "30070-05.html":
			{
				qs.set("talk", "1");
				break;
			}
			case "30070-06.html":
			{
				qs.set("talk", "2");
				break;
			}
			case "30070-08.html":
			{
				if (qs.isCond(1))
				{
					qs.unset("talk");
					qs.setCond(2, true);
				}
				break;
			}
			case "30070-16.html":
			{
				if (qs.isCond(3) && (hasQuestItems(player, EXECUTOR) && hasQuestItems(player, MISSIONARY)))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						takeItems(player, EXECUTOR, -1);
						takeItems(player, MISSIONARY, -1);
						giveAdena(player, 69146, true);
						if (player.getLevel() < MAX_LEVEL)
						{
							addExpAndSp(player, 219975, 20);
						}
						qs.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(2) && (getQuestItemsCount(player, FRAGMENT) < 30))
		{
			giveItems(player, FRAGMENT, 1);
			if (getQuestItemsCount(player, FRAGMENT) >= 30)
			{
				qs.setCond(3, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "30070-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						switch (qs.getInt("talk"))
						{
							case 1:
							{
								htmltext = "30070-05.html";
								break;
							}
							case 2:
							{
								htmltext = "30070-06.html";
								break;
							}
							default:
							{
								htmltext = "30070-03.html";
								break;
							}
						}
						break;
					}
					case 2:
					{
						htmltext = "30070-08.html";
						break;
					}
					case 3:
					{
						if (qs.getInt("talk") == 1)
						{
							htmltext = "30070-10.html";
						}
						else if (getQuestItemsCount(player, FRAGMENT) >= 30)
						{
							qs.set("talk", "1");
							htmltext = "30070-09.html";
							takeItems(player, FRAGMENT, -1);
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
