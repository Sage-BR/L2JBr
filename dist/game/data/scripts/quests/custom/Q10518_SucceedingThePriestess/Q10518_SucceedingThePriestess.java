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
package quests.custom.Q10518_SucceedingThePriestess;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

/**
 * Succeeding the Priestess (10518)
 * @URL https://l2wiki.com/Succeeding_the_Priestess
 * @author Mobius
 */
public class Q10518_SucceedingThePriestess extends Quest
{
	// NPCs
	private static final int START_NPC = 33907;
	private static final int[] MONSTERS_1 =
	{
		24304,
		24305,
		24306,
		24307,
		24308,
		24309,
		24310,
		24311,
		24312,
		24313,
		24314,
		24315,
		24316,
	};
	private static final int[] MONSTERS_2 =
	{
		24318,
		24319,
		24320,
		24321,
		24322,
		24323,
		24324,
		24325,
		24326,
		24327,
		24328,
		24329,
	};
	// Item
	private static final int MONSTER_DROP_1 = 80325;
	private static final int MONSTER_DROP_2 = 80326;
	// Misc
	private static final int REQUIRED_DROP_COUNT_1 = 10;
	private static final int REQUIRED_DROP_COUNT_2 = 30;
	private static final int REQUIRED_KILL_COUNT = 50;
	private static final String KILL_COUNT_VAR = "KillCount";
	private static final int KILLING_NPCSTRING_ID_1 = NpcStringId.DEFEAT_KROFINS.getId();
	private static final int KILLING_NPCSTRING_ID_2 = NpcStringId.LV_110_SUCCEEDING_THE_PRIESTESS_2.getId();
	private static final QuestType QUEST_TYPE = QuestType.ONE_TIME; // REPEATABLE, ONE_TIME, DAILY
	private static final boolean PARTY_QUEST = false;
	private static final int KILLING_COND_1 = 1;
	private static final int FINISH_COND_1 = 2;
	private static final int KILLING_COND_2 = 3;
	private static final int FINISH_COND_2 = 4;
	private static final int MIN_LEVEL = 110;
	
	public Q10518_SucceedingThePriestess()
	{
		super(10518);
		addStartNpc(START_NPC);
		addTalkId(START_NPC);
		addKillId(MONSTERS_1);
		addKillId(MONSTERS_2);
		registerQuestItems(MONSTER_DROP_1, MONSTER_DROP_2);
		addCondMinLevel(MIN_LEVEL, getNoQuestMsg(null));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "accept_1.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					qs.setCond(KILLING_COND_1);
				}
				break;
			}
			case "accept_2.html":
			{
				if (qs.isCond(FINISH_COND_1) && (getQuestItemsCount(player, MONSTER_DROP_1) >= REQUIRED_DROP_COUNT_1) && (qs.getInt(KILL_COUNT_VAR) >= REQUIRED_KILL_COUNT))
				{
					takeItems(player, MONSTER_DROP_1, -1);
					qs.setCond(KILLING_COND_2, true);
				}
				break;
			}
			case "reward.html":
			{
				if (qs.isCond(FINISH_COND_2) && (getQuestItemsCount(player, MONSTER_DROP_2) >= REQUIRED_DROP_COUNT_2))
				{
					takeItems(player, MONSTER_DROP_2, -1);
					// Reward.
					addExpAndSp(player, 500056821000L, 500056740);
					rewardItems(player, 45932, 1);
					qs.exitQuest(QUEST_TYPE, true);
				}
				break;
			}
			default:
			{
				return null;
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == START_NPC)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "start.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(KILLING_COND_1))
					{
						htmltext = "accept.htm";
					}
					else if (qs.isCond(FINISH_COND_1))
					{
						htmltext = "finish_1.html";
					}
					else if (qs.isCond(KILLING_COND_2))
					{
						htmltext = "accept_2.html";
					}
					else if (qs.isCond(FINISH_COND_2))
					{
						htmltext = "finish_2.html";
					}
					break;
				}
				case State.COMPLETED:
				{
					if (qs.isNowAvailable())
					{
						qs.setState(State.CREATED);
						htmltext = "start.htm";
					}
					else
					{
						htmltext = getAlreadyCompletedMsg(player, QUEST_TYPE);
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		QuestState qs = PARTY_QUEST ? getRandomPartyMemberState(killer, -1, 3, npc) : getQuestState(killer, false);
		if (qs != null)
		{
			if (qs.isCond(KILLING_COND_1) && CommonUtil.contains(MONSTERS_1, npc.getId()))
			{
				final PlayerInstance player = qs.getPlayer();
				giveItemRandomly(player, npc, MONSTER_DROP_1, 1, REQUIRED_DROP_COUNT_1, 0.5, true);
				
				final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
				if (killCount <= REQUIRED_KILL_COUNT)
				{
					qs.set(KILL_COUNT_VAR, killCount);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				
				if ((killCount >= REQUIRED_KILL_COUNT) && (getQuestItemsCount(player, MONSTER_DROP_1) >= REQUIRED_DROP_COUNT_1))
				{
					qs.setCond(FINISH_COND_1, true);
				}
				
				sendNpcLogList(player);
			}
			else if (qs.isCond(KILLING_COND_2) && CommonUtil.contains(MONSTERS_2, npc.getId()))
			{
				final PlayerInstance player = qs.getPlayer();
				if (giveItemRandomly(player, npc, MONSTER_DROP_2, 1, REQUIRED_DROP_COUNT_2, 1, true))
				{
					qs.setCond(FINISH_COND_2, true);
				}
			}
		}
		
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs != null)
		{
			if (qs.isCond(KILLING_COND_1))
			{
				final Set<NpcLogListHolder> holder = new HashSet<>();
				holder.add(new NpcLogListHolder(KILLING_NPCSTRING_ID_1, true, qs.getInt(KILL_COUNT_VAR)));
				holder.add(new NpcLogListHolder(KILLING_NPCSTRING_ID_2, true, (int) getQuestItemsCount(player, MONSTER_DROP_1)));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}
