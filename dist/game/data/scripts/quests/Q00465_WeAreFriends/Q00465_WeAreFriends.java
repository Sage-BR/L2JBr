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
package quests.Q00465_WeAreFriends;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * We Are Friends (465)
 * @URL https://l2wiki.com/We_Are_Friends
 * @author Gigi
 */
public class Q00465_WeAreFriends extends Quest
{
	// NPCs
	private static final int FAIRY_CITIZEN = 32921;
	private static final int FAIRY_CITIZEN_SPAWN = 32923;
	// Item
	private static final int MARK_OF_FRIENDSHIP = 17377;
	private static final int FAIRY_LEAF_FLUTE = 17378;
	private static final int CERTIFICATE_OF_PROMISE = 30384;
	// Misc
	private static final int MIN_LEVEL = 88;
	
	public Q00465_WeAreFriends()
	{
		super(465);
		addStartNpc(FAIRY_CITIZEN);
		addTalkId(FAIRY_CITIZEN, FAIRY_CITIZEN_SPAWN);
		registerQuestItems(MARK_OF_FRIENDSHIP);
		addCondMinLevel(MIN_LEVEL, "no_level.htm");
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
			case "32921-02.htm":
			case "32921-07.html":
			{
				htmltext = event;
				break;
			}
			case "32921-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32923-02.html":
			{
				giveItems(player, MARK_OF_FRIENDSHIP, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				if ((getQuestItemsCount(player, MARK_OF_FRIENDSHIP) >= 2))
				{
					qs.setCond(2, true);
				}
				htmltext = event;
				npc.deleteMe();
				break;
			}
			case "32921-08.html":
			{
				giveItems(player, FAIRY_LEAF_FLUTE, 1);
				giveItems(player, CERTIFICATE_OF_PROMISE, getRandom(1, 4));
				qs.exitQuest(QuestType.DAILY, true);
				htmltext = event;
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
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable() && (npc.getId() == FAIRY_CITIZEN))
				{
					htmltext = "32921-04.html";
					break;
				}
				qs.setState(State.CREATED);
			}
			case State.CREATED:
			{
				if (npc.getId() == FAIRY_CITIZEN)
				{
					htmltext = "32921-01.htm";
				}
			}
				break;
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case FAIRY_CITIZEN:
					{
						if (qs.isCond(1))
						{
							htmltext = "32921-05.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "32921-06.html";
						}
						break;
					}
					case FAIRY_CITIZEN_SPAWN:
					{
						if (qs.isCond(1) && (npc.getTitle() == player.getName()))
						{
							htmltext = "32923-01.html";
							break;
						}
						return null;
					}
				}
			}
		}
		return htmltext;
	}
}