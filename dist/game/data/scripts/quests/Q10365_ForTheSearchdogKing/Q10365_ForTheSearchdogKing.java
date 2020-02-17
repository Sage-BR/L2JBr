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
package quests.Q10365_ForTheSearchdogKing;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10364_ObligationsOfTheSeeker.Q10364_ObligationsOfTheSeeker;

/**
 * For the Searchdog King (10365)
 * @author Stayway
 */
public class Q10365_ForTheSearchdogKing extends Quest
{
	// NPCs
	private static final int DEP = 33453;
	private static final int SEBION = 32978;
	// MOBs
	private static final int EYESAROCH = 23122;
	private static final int CRITTER = 22993;
	private static final int RIDER = 22995;
	// Items
	private static final int KINGS_TONIC = 47607;
	// Misc
	private static final int MIN_LEVEL = 15;
	private static final int MAX_LEVEL = 25;
	
	public Q10365_ForTheSearchdogKing()
	{
		super(10365);
		addStartNpc(DEP);
		addTalkId(DEP, SEBION);
		registerQuestItems(KINGS_TONIC);
		addKillId(EYESAROCH, CRITTER, RIDER);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33453-06.html");
		addCondCompletedQuest(Q10364_ObligationsOfTheSeeker.class.getSimpleName(), "33453-06.html");
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
			case "33453-02.htm":
			case "33453-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33453-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32978-02.html":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 172000, 15);
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
		if ((qs != null) && qs.isCond(1))
		{
			if (giveItemRandomly(killer, npc, KINGS_TONIC, 1, 20, 0.5, true))
			{
				qs.setCond(0);
				qs.setCond(2);
				showOnScreenMsg(killer, NpcStringId.USE_THE_YE_SAGIRA_TELEPORT_DEVICE_SHINING_WITH_A_RED_SHIMMER_TO_GO_TO_EXPLORATION_AREA_5, ExShowScreenMessage.TOP_CENTER, 4500);
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
				if (npc.getId() == DEP)
				{
					htmltext = "33453-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case DEP:
					{
						if (qs.isCond(1))
						{
							showOnScreenMsg(player, NpcStringId.USE_THE_YE_SAGIRA_TELEPORT_DEVICE_SHINING_WITH_A_RED_SHIMMER_TO_GO_TO_EXPLORATION_AREA_5, ExShowScreenMessage.TOP_CENTER, 10000);
							htmltext = "33453-05.html";
						}
						break;
					}
					case SEBION:
					{
						if (qs.isCond(2))
						{
							htmltext = "32978-01.html";
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