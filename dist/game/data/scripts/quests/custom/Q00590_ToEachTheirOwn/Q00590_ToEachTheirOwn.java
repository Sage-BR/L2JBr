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
package quests.custom.Q00590_ToEachTheirOwn;

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

import quests.custom.Q00589_ASecretChange.Q00589_ASecretChange;

/**
 * To Each Their Own (590)
 * @URL https://l2wiki.com/To_Each_Their_Own
 * @author Mobius
 */
public class Q00590_ToEachTheirOwn extends Quest
{
	// NPCs
	private static final int START_NPC = 34424;
	private static final int[] MONSTERS =
	{
		24204,
		24205,
		24206,
	};
	// Item
	private static final int MONSTER_DROP = 48534;
	// Misc
	private static final int REQUIRED_DROP_COUNT = 50;
	private static final int KILLING_NPCSTRING_ID = NpcStringId.LV_95_105_TO_EACH_THEIR_OWN_IN_PROGRESS.getId();
	private static final QuestType QUEST_TYPE = QuestType.DAILY; // REPEATABLE, ONE_TIME, DAILY
	private static final boolean PARTY_QUEST = false;
	private static final int KILLING_COND = 1;
	private static final int FINISH_COND = 2;
	private static final int MIN_LEVEL = 95;
	private static final int MAX_LEVEL = 105;
	
	public Q00590_ToEachTheirOwn()
	{
		super(590);
		addStartNpc(START_NPC);
		addTalkId(START_NPC);
		addKillId(MONSTERS);
		registerQuestItems(MONSTER_DROP);
		addCondMinLevel(MIN_LEVEL, getNoQuestMsg(null));
		addCondMaxLevel(MAX_LEVEL, getNoQuestMsg(null));
		addCondCompletedQuest(Q00589_ASecretChange.class.getSimpleName(), "34424-05.htm");
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
			case "accept.htm":
			{
				qs.startQuest();
				break;
			}
			case "reward.html":
			{
				if (qs.isCond(FINISH_COND) && (getQuestItemsCount(player, MONSTER_DROP) >= REQUIRED_DROP_COUNT))
				{
					takeItems(player, MONSTER_DROP, -1);
					// Reward.
					addExpAndSp(player, 1793099880, 1793070);
					giveAdena(player, 680100, false);
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
					if (qs.isCond(KILLING_COND))
					{
						htmltext = "accept.htm";
					}
					else if (qs.isCond(FINISH_COND))
					{
						htmltext = "finish.html";
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
		final QuestState qs = PARTY_QUEST ? getRandomPartyMemberState(killer, -1, 3, npc) : getQuestState(killer, false);
		if ((qs != null) && qs.isCond(KILLING_COND))
		{
			final PlayerInstance player = qs.getPlayer();
			if (giveItemRandomly(player, npc, MONSTER_DROP, 1, REQUIRED_DROP_COUNT, 1, true))
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
			holder.add(new NpcLogListHolder(KILLING_NPCSTRING_ID, true, (int) getQuestItemsCount(player, MONSTER_DROP)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
