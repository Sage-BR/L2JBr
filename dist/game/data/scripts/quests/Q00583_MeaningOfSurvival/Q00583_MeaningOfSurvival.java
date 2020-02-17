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
package quests.Q00583_MeaningOfSurvival;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

/**
 * Meaning Of Survival (00583)
 * @URL https://l2wiki.com/Meaning_of_Survival
 * @author NightBR
 */
public class Q00583_MeaningOfSurvival extends Quest
{
	// NPCs
	private static final int START_NPC = 30137; // Vollodos
	private static final int[] MONSTERS =
	{
		23162, // Corpse Devourer
		23163, // Corpse Absorber
		23166, // Contaminated Rotten Root
		23167, // Decayed Spore
		23171, // Corpse Collector
	};
	// Item
	private static final int MONSTER_DROP = 48379; // Putrefied Extracts
	// Misc
	private static final int REQUIRED_DROP_COUNT = 100;
	private static final int REQUIRED_KILL_COUNT = 100;
	private static final String KILL_COUNT_VAR = "KillCount";
	private static final int KILLING_NPCSTRING_ID1 = NpcStringId.LV_86_96_MEANING_OF_SURVIVAL_IN_PROGRESS.getId();
	private static final int KILLING_NPCSTRING_ID2 = NpcStringId.LV_86_96_MEANING_OF_SURVIVAL_2.getId();
	private static final QuestType QUEST_TYPE = QuestType.DAILY; // REPEATABLE, ONE_TIME, DAILY
	private static final boolean PARTY_QUEST = true;
	private static final int KILLING_COND = 1;
	private static final int FINISH_COND = 2;
	private static final int MIN_LEVEL = 86;
	private static final int MAX_LEVEL = 96;
	// Rewards
	private static final int XP = 284703720;
	private static final int SP = 284700;
	private static final int REWARD_ITEM1 = 57; // ADENA
	private static final int REWARD_ITEM1_AMOUNT = 512370;
	
	public Q00583_MeaningOfSurvival()
	{
		super(583);
		addStartNpc(START_NPC);
		addTalkId(START_NPC);
		addKillId(MONSTERS);
		registerQuestItems(MONSTER_DROP);
		addCondMinLevel(MIN_LEVEL, getNoQuestMsg(null));
		addCondMaxLevel(MAX_LEVEL, getNoQuestMsg(null));
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
			case "30137-01.htm":
			case "30137-02.htm":
			{
				return event;
			}
			case "30137-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "30137-05.html":
			{
				if (qs.isCond(FINISH_COND) && (getQuestItemsCount(player, MONSTER_DROP) >= REQUIRED_DROP_COUNT))
				{
					takeItems(player, MONSTER_DROP, -1);
					// Reward.
					addExpAndSp(player, XP, SP);
					rewardItems(player, REWARD_ITEM1, REWARD_ITEM1_AMOUNT);
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
					htmltext = "30137-00.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(KILLING_COND))
					{
						htmltext = "30137-03.htm";
					}
					else if (qs.isCond(FINISH_COND))
					{
						// Check if Faction quest has been taken
						final QuestState st = player.getQuestState("Q00563_BasicMissionBloodySwampland");
						if ((st != null) && st.isStarted())
						{
							htmltext = "30137-04.html";
						}
						else
						{
							htmltext = "30137-06.html";
						}
					}
					break;
				}
				case State.COMPLETED:
				{
					if (qs.isNowAvailable())
					{
						qs.setState(State.CREATED);
						htmltext = "30137-00.htm";
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
		if ((qs != null) && qs.isCond(KILLING_COND))
		{
			final PlayerInstance player = qs.getPlayer();
			giveItemRandomly(player, npc, MONSTER_DROP, 1, REQUIRED_DROP_COUNT, 1, true);
			
			final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
			if (killCount < REQUIRED_KILL_COUNT)
			{
				qs.set(KILL_COUNT_VAR, killCount);
			}
			
			if ((killCount >= REQUIRED_KILL_COUNT) && (getQuestItemsCount(player, MONSTER_DROP) >= REQUIRED_DROP_COUNT))
			{
				qs.setCond(FINISH_COND, true);
			}
			
			sendNpcLogList(player);
		}
		
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(KILLING_COND))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(KILLING_NPCSTRING_ID1, true, (int) getQuestItemsCount(player, MONSTER_DROP)));
			holder.add(new NpcLogListHolder(KILLING_NPCSTRING_ID2, true, qs.getInt(KILL_COUNT_VAR)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
