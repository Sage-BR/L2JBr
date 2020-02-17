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
package quests.Q00763_ADauntingTask;

import java.util.Arrays;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * A Daunting Task (763)
 * @author St3eT
 */
public class Q00763_ADauntingTask extends Quest
{
	// NPCs
	private static final int ANDREI = 31292;
	private static final int JANITT = 33851;
	private static final int[] MONSTERS_MALICE =
	{
		21294, // Canyon Antelope
		21296, // Canyon Bandersnatch
		23311, // Valley Buffalo
		23312, // Valley Grendel
	};
	private static final int[] MONSTERS_EYE =
	{
		21295, // Canyon Antelope Slave
		21297, // Canyon Bandersnatch Slave
		21299, // Valley Buffalo Slave
		21304, // Valley Grendel Slave
	};
	// Items
	private static final ItemHolder EYE = new ItemHolder(36672, 200);
	private static final ItemHolder MALICE = new ItemHolder(36673, 200);
	// Rewards
	private static final long EXP = 474767890;
	private static final int SP = 5026;
	// Misc
	private static final int MIN_LEVEL = 70;
	private static final int MAX_LEVEL = 75;
	private static final QuestType QUEST_TYPE = QuestType.REPEATABLE; // REPEATABLE, ONE_TIME, DAILY
	
	public Q00763_ADauntingTask()
	{
		super(763);
		addStartNpc(ANDREI);
		addTalkId(ANDREI, JANITT);
		addKillId(MONSTERS_EYE);
		addKillId(MONSTERS_MALICE);
		registerQuestItems(EYE.getId(), MALICE.getId());
		addCondMinLevel(MIN_LEVEL, "noLevel.html");
		addCondMaxLevel(MAX_LEVEL, "noLevel.html");
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
			case "31292-02.htm":
			case "31292-03.htm":
			case "33851-02.html":
			{
				htmltext = event;
				break;
			}
			case "31292-04.htm":
			{
				// Starts
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33851-03.html":
			{
				// Killing Cond
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "31292-06.html":
			{
				if (qs.isCond(3))
				{
					// Rewards
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, EXP, SP);
					}
					takeAllItems(player, EYE, MALICE);
					qs.exitQuest(QUEST_TYPE, true);
					htmltext = event;
					break;
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
				htmltext = "31292-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == ANDREI)
				{
					if (qs.isCond(3))
					{
						htmltext = "31292-05.html";
					}
					else
					{
						htmltext = "31292-04.html";
					}
				}
				else if (npc.getId() == JANITT)
				{
					if (qs.isCond(1))
					{
						htmltext = "33851-01.html";
					}
					else
					{
						htmltext = "33851-03.html";
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = getAlreadyCompletedMsg(player, QuestType.DAILY);
				}
				else
				{
					qs.setState(State.CREATED);
					htmltext = "31292-01.htm";
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
			if (contains(MONSTERS_EYE, npc.getId()) && (getQuestItemsCount(killer, EYE.getId()) < 200))
			{
				giveItems(killer, EYE.getId(), 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else if (contains(MONSTERS_MALICE, npc.getId()) && (getQuestItemsCount(killer, MALICE.getId()) < 200))
			{
				giveItems(killer, MALICE.getId(), 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			
			if ((getQuestItemsCount(killer, EYE.getId()) >= 200) && (getQuestItemsCount(killer, MALICE.getId()) >= 200))
			{
				// Finish
				qs.setCond(3, true);
				showOnScreenMsg(killer, NpcStringId.YOU_CAN_GATHER_MORE_POWERFUL_DARK_MALICE, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		return super.onKill(npc, killer, isSummon);
		
	}
	
	public static boolean contains(int[] arr, Integer item)
	{
		return Arrays.stream(arr).anyMatch(item::equals);
	}
}