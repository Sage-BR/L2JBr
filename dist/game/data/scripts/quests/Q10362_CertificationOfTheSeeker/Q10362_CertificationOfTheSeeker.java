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
package quests.Q10362_CertificationOfTheSeeker;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10330_ToTheRuinsOfYeSagira.Q10330_ToTheRuinsOfYeSagira;

/**
 * Certification of The Seeker (10362)
 * @URL https://l2wiki.com/Certification_of_the_Seeker
 * @author Gladicek, Gigi
 */
public class Q10362_CertificationOfTheSeeker extends Quest
{
	// NPCs
	private static final int LAKCIS = 32977;
	private static final int CHESHA = 33449;
	private static final int NAGEL = 33450;
	private static final int STALKER = 22992;
	private static final int CRAWLER = 22991;
	// Items
	private static final int GLOVES = 49;
	// Misc
	private static final int MIN_LEVEL = 9;
	private static final int MAX_LEVEL = 20;
	
	public Q10362_CertificationOfTheSeeker()
	{
		super(10362);
		addStartNpc(LAKCIS);
		addTalkId(LAKCIS, CHESHA, NAGEL);
		addKillId(STALKER, CRAWLER);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33449-05.html");
		addCondCompletedQuest(Q10330_ToTheRuinsOfYeSagira.class.getSimpleName(), "33449-05.html");
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
			case "32977-02.htm":
			case "32977-03.htm":
			case "33449-02.html":
			{
				htmltext = event;
				break;
			}
			case "32977-04.htm":
			{
				qs.startQuest();
				qs.setMemoStateEx(STALKER, 0);
				qs.setMemoStateEx(CRAWLER, 0);
				htmltext = event;
				break;
			}
			case "33449-03.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "33450-02.html":
			{
				if (qs.isCond(3))
				{
					giveItems(player, GLOVES, 1);
					addExpAndSp(player, 40000, 12);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isCond(2))
		{
			int killedStalker = qs.getMemoStateEx(STALKER);
			int killedCrawler = qs.getMemoStateEx(CRAWLER);
			
			if (npc.getId() == STALKER)
			{
				killedStalker++;
				if (killedStalker <= 10)
				{
					qs.setMemoStateEx(STALKER, killedStalker);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
			else if (npc.getId() == CRAWLER)
			{
				killedCrawler++;
				if (killedCrawler <= 10)
				{
					qs.setMemoStateEx(CRAWLER, killedCrawler);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
			
			if ((killedStalker == 10) && (killedCrawler == 10))
			{
				qs.setCond(0);
				qs.setCond(3, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
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
				if (npc.getId() == LAKCIS)
				{
					htmltext = "32977-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case LAKCIS:
					{
						if (qs.isCond(1))
						{
							htmltext = "32977-05.html";
						}
						break;
					}
					case CHESHA:
					{
						if (qs.isCond(1))
						{
							htmltext = "33449-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "33449-04.html";
						}
						break;
					}
					case NAGEL:
					{
						if (qs.isCond(3))
						{
							htmltext = "33450-01.html";
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
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(2))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(2);
			npcLogList.add(new NpcLogListHolder(STALKER, false, qs.getMemoStateEx(STALKER)));
			npcLogList.add(new NpcLogListHolder(CRAWLER, false, qs.getMemoStateEx(CRAWLER)));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}