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
package quests.Q10771_VolatilePower;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10770_InSearchOfTheGrail.Q10770_InSearchOfTheGrail;

/**
 * Volatile Power (10771)
 * @author malyelfik
 */
public class Q10771_VolatilePower extends Quest
{
	// NPCs
	private static final int JANSSEN = 30484;
	private static final int HIDDEN_CRUSHER = 33990;
	// Monsters
	private static final int FRAGMENT_EATER = 27533;
	// Items
	private static final int SHINING_MYSTERIOUS_FRAGMENT = 39713;
	private static final int NORMAL_FRAGMENT_DUST = 39714;
	// Misc
	private static final int MIN_LEVEL = 44;
	
	public Q10771_VolatilePower()
	{
		super(10771);
		addStartNpc(JANSSEN);
		addFirstTalkId(HIDDEN_CRUSHER);
		addTalkId(JANSSEN, HIDDEN_CRUSHER);
		
		addCondRace(Race.ERTHEIA, "30484-00.htm");
		addCondMinLevel(MIN_LEVEL, "30484-00.htm");
		addCondCompletedQuest(Q10770_InSearchOfTheGrail.class.getSimpleName(), "30484-00.htm");
		registerQuestItems(SHINING_MYSTERIOUS_FRAGMENT, NORMAL_FRAGMENT_DUST);
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
			case "30484-02.htm":
			case "30484-03.htm":
			case "30484-04.htm":
			{
				break;
			}
			case "30484-05.htm":
			{
				qs.startQuest();
				giveItems(player, SHINING_MYSTERIOUS_FRAGMENT, 20);
				break;
			}
			case "30484-08.html":
			{
				if (qs.isCond(3))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 4150144, 650);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "33990.html";
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == JANSSEN)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "30484-01.htm";
					break;
				}
				case State.STARTED:
				{
					htmltext = (qs.isCond(1)) ? "30484-06.html" : "30484-07.html";
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				final int itemCount = (int) getQuestItemsCount(player, SHINING_MYSTERIOUS_FRAGMENT);
				int reduceCount = getRandom(1, 3);
				if (reduceCount > itemCount)
				{
					reduceCount = itemCount;
				}
				
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_CRUSHER_IS_ACTIVATED);
				npc.setScriptValue(reduceCount);
				
				for (int i = 0; i < 3; i++)
				{
					final Npc mob = addSpawn(FRAGMENT_EATER, player, true, 70000);
					mob.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.KILL_THEM_DON_T_LET_THEM_GET_AWAY_WITH_THE_FRAGMENT);
					addAttackPlayerDesire(mob, player);
				}
				takeItems(player, SHINING_MYSTERIOUS_FRAGMENT, reduceCount);
				giveItems(player, NORMAL_FRAGMENT_DUST, reduceCount);
				getTimers().addTimer("DESTROY_COUNT", 2000, npc, player);
				
				if (getQuestItemsCount(player, NORMAL_FRAGMENT_DUST) >= 20)
				{
					qs.setCond(3, true); // Looks like cond 2 is skipped.
				}
				htmltext = null;
			}
			else
			{
				htmltext = "33990-02.html";
			}
		}
		else
		{
			htmltext = "33990-01.html";
		}
		return htmltext;
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "DESTROY_COUNT":
			{
				if ((npc != null) && (npc.getId() == HIDDEN_CRUSHER))
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.S1_OBJECT_S_DESTROYED, String.valueOf(npc.getScriptValue()));
					getTimers().addTimer("DESPAWN_MSG", 2000, npc, player);
				}
				break;
			}
			case "DESPAWN_MSG":
			{
				if ((npc != null) && (npc.getId() == HIDDEN_CRUSHER))
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_DEVICE_RAN_OUT_OF_MAGIC);
					showOnScreenMsg(player, NpcStringId.THE_DEVICE_RAN_OUT_OF_MAGIC_TRY_LOOKING_FOR_ANOTHER, ExShowScreenMessage.TOP_CENTER, 5000);
					if (!getTimers().hasTimer("DESPAWN", npc, null))
					{
						getTimers().addTimer("DESPAWN", 1000, npc, null);
					}
				}
				break;
			}
			case "DESPAWN":
			{
				if ((npc != null) && (npc.getId() == HIDDEN_CRUSHER))
				{
					npc.deleteMe();
				}
				break;
			}
			default:
			{
				super.onTimerEvent(event, params, npc, player);
			}
		}
	}
}
