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
package quests.Q00551_OlympiadStarter;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.olympiad.CompetitionType;
import org.l2jbr.gameserver.model.olympiad.Participant;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Olympiad Starter (551)
 * @author Gnacik
 */
public class Q00551_OlympiadStarter extends Quest
{
	private static final int MANAGER = 31688;
	
	private static final int CERT_3 = 17238;
	private static final int CERT_5 = 17239;
	private static final int CERT_10 = 17240;
	
	private static final int OLY_CHEST = 17169;
	private static final int MEDAL_OF_GLORY = 21874;
	
	public Q00551_OlympiadStarter()
	{
		super(551);
		addStartNpc(MANAGER);
		addTalkId(MANAGER);
		registerQuestItems(CERT_3, CERT_5, CERT_10);
		addOlympiadMatchFinishId();
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
		
		if (event.equalsIgnoreCase("31688-03.html"))
		{
			qs.startQuest();
		}
		else if (event.equalsIgnoreCase("31688-04.html"))
		{
			final long count = getQuestItemsCount(player, CERT_3) + getQuestItemsCount(player, CERT_5);
			if (count > 0)
			{
				giveItems(player, OLY_CHEST, count); // max 2
				if (count == 2)
				{
					giveItems(player, MEDAL_OF_GLORY, 3);
				}
				qs.exitQuest(QuestType.DAILY, true);
			}
			else
			{
				htmltext = getNoQuestMsg(player);
			}
		}
		return htmltext;
	}
	
	@Override
	public void onOlympiadLose(PlayerInstance loser, CompetitionType type)
	{
		if (loser == null)
		{
			return;
		}
		final QuestState qs = getQuestState(loser, false);
		if ((qs == null) || !qs.isStarted())
		{
			return;
		}
		final int matches = qs.getInt("matches") + 1;
		switch (matches)
		{
			case 3:
			{
				if (!hasQuestItems(loser, CERT_3))
				{
					giveItems(loser, CERT_3, 1);
				}
				break;
			}
			case 5:
			{
				if (!hasQuestItems(loser, CERT_5))
				{
					giveItems(loser, CERT_5, 1);
				}
				break;
			}
			case 10:
			{
				if (!hasQuestItems(loser, CERT_10))
				{
					giveItems(loser, CERT_10, 1);
				}
				break;
			}
		}
		qs.set("matches", String.valueOf(matches));
	}
	
	@Override
	public void onOlympiadMatchFinish(Participant winner, Participant looser, CompetitionType type)
	{
		if (winner != null)
		{
			final PlayerInstance player = winner.getPlayer();
			if (player == null)
			{
				return;
			}
			final QuestState qs = getQuestState(player, false);
			if ((qs != null) && qs.isStarted())
			{
				final int matches = qs.getInt("matches") + 1;
				switch (matches)
				{
					case 3:
					{
						if (!hasQuestItems(player, CERT_3))
						{
							giveItems(player, CERT_3, 1);
						}
						break;
					}
					case 5:
					{
						if (!hasQuestItems(player, CERT_5))
						{
							giveItems(player, CERT_5, 1);
						}
						break;
					}
					case 10:
					{
						if (!hasQuestItems(player, CERT_10))
						{
							giveItems(player, CERT_10, 1);
						}
						break;
					}
				}
				qs.set("matches", String.valueOf(matches));
			}
		}
		
		if (looser == null)
		{
			return;
		}
		final PlayerInstance player = looser.getPlayer();
		if (player == null)
		{
			return;
		}
		final QuestState qs = getQuestState(player, false);
		if ((qs == null) || !qs.isStarted())
		{
			return;
		}
		final int matches = qs.getInt("matches") + 1;
		switch (matches)
		{
			case 3:
			{
				if (!hasQuestItems(player, CERT_3))
				{
					giveItems(player, CERT_3, 1);
				}
				break;
			}
			case 5:
			{
				if (!hasQuestItems(player, CERT_5))
				{
					giveItems(player, CERT_5, 1);
				}
				break;
			}
			case 10:
			{
				if (!hasQuestItems(player, CERT_10))
				{
					giveItems(player, CERT_10, 1);
				}
				break;
			}
		}
		qs.set("matches", String.valueOf(matches));
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if ((player.getLevel() < 75) || (player.getNobleLevel() == 0))
		{
			htmltext = "31688-00.htm";
		}
		else if (qs.isCreated())
		{
			htmltext = "31688-01.htm";
		}
		else if (qs.isCompleted())
		{
			if (qs.isNowAvailable())
			{
				qs.setState(State.CREATED);
				htmltext = (player.getLevel() < 75) || (player.getNobleLevel() == 0) ? "31688-00.htm" : "31688-01.htm";
			}
			else
			{
				htmltext = "31688-05.html";
			}
		}
		else if (qs.isStarted())
		{
			final long count = getQuestItemsCount(player, CERT_3) + getQuestItemsCount(player, CERT_5) + getQuestItemsCount(player, CERT_10);
			if (count == 3)
			{
				htmltext = "31688-04.html";
				giveItems(player, OLY_CHEST, 4);
				giveItems(player, MEDAL_OF_GLORY, 5);
				qs.exitQuest(QuestType.DAILY, true);
			}
			else
			{
				htmltext = "31688-s" + count + ".html";
			}
		}
		return htmltext;
	}
}
