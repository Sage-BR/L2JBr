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
package quests.Q00043_HelpTheSister;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Help The Sister! (43)
 * @author malyelfik
 */
public class Q00043_HelpTheSister extends Quest
{
	// NPCs
	private static final int COOPER = 30829;
	private static final int GALLADUCCI = 30097;
	// Monsters
	private static final int HOBGOBLIN = 20147;
	private static final int DION_GRIZZLY = 20203;
	private static final int DIRE_WOLF = 20205;
	private static final int OL_MAHUM_RANGER = 20224;
	private static final int MONSTER_EYE_SEARCHER = 20265;
	private static final int MONSTER_EYE_GAZER = 20266;
	private static final int ENKU_ORC_HERO = 20291;
	private static final int ENKU_ORC_SHAMAN = 20292;
	// Items
	private static final int CRAFTED_DAGGER = 220;
	private static final int MAP_PIECE = 7550;
	private static final int MAP = 7551;
	private static final int PET_TICKET = 7584;
	// Misc
	private static final int MIN_LVL = 26;
	
	public Q00043_HelpTheSister()
	{
		super(43);
		addStartNpc(COOPER);
		addTalkId(COOPER, GALLADUCCI);
		addKillId(DION_GRIZZLY, HOBGOBLIN, DIRE_WOLF, OL_MAHUM_RANGER, MONSTER_EYE_SEARCHER, MONSTER_EYE_GAZER, ENKU_ORC_HERO, ENKU_ORC_SHAMAN);
		registerQuestItems(MAP, MAP_PIECE);
		addCondMinLevel(MIN_LVL, "30829-00a.html");
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
			case "30829-01.htm":
			{
				qs.startQuest();
				break;
			}
			case "30829-03.html":
			{
				if (hasQuestItems(player, CRAFTED_DAGGER))
				{
					takeItems(player, CRAFTED_DAGGER, 1);
					qs.setCond(2, true);
				}
				else
				{
					htmltext = getNoQuestMsg(player);
				}
				break;
			}
			case "30829-06.html":
			{
				if (getQuestItemsCount(player, MAP_PIECE) == 30)
				{
					takeItems(player, MAP_PIECE, -1);
					giveItems(player, MAP, 1);
					qs.setCond(4, true);
				}
				else
				{
					htmltext = "30829-06a.html";
				}
				break;
			}
			case "30097-02.html":
			{
				if (hasQuestItems(player, MAP))
				{
					takeItems(player, MAP, -1);
					qs.setCond(5, true);
				}
				else
				{
					htmltext = "30097-02a.html";
				}
				break;
			}
			case "30829-09.html":
			{
				giveItems(player, PET_TICKET, 1);
				qs.exitQuest(false, true);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		
		if ((qs != null) && qs.isCond(2))
		{
			giveItems(player, MAP_PIECE, 1);
			if (getQuestItemsCount(player, MAP_PIECE) == 30)
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
		
		switch (npc.getId())
		{
			case COOPER:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = "30829-00.htm";
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = (hasQuestItems(player, CRAFTED_DAGGER)) ? "30829-02.html" : "30829-02a.html";
								break;
							}
							case 2:
							{
								htmltext = "30829-04.html";
								break;
							}
							case 3:
							{
								htmltext = "30829-05.html";
								break;
							}
							case 4:
							{
								htmltext = "30829-07.html";
								break;
							}
							case 5:
							{
								htmltext = "30829-08.html";
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
				break;
			}
			case GALLADUCCI:
			{
				if (qs.isStarted())
				{
					if (qs.isCond(4))
					{
						htmltext = "30097-01.html";
					}
					else if (qs.isCond(5))
					{
						htmltext = "30097-03.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
