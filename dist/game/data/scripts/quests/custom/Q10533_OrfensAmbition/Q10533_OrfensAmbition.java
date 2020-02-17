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
package quests.custom.Q10533_OrfensAmbition;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Orfen's Ambition (10533)
 * @URL https://l2wiki.com/Orfen%27s_Ambition
 * @author Mobius
 */
public class Q10533_OrfensAmbition extends Quest
{
	// NPCs
	private static final int START_NPC = 33846;
	private static final int TALK_NPC_1 = 34449;
	private static final int[] BOSS_IDS =
	{
		29325
	};
	// Misc
	private static final QuestType QUEST_TYPE = QuestType.ONE_TIME; // REPEATABLE, ONE_TIME, DAILY
	private static final int TALK_NPC_1_COND = 1;
	private static final int KILLING_COND = 2;
	private static final int FINISH_COND = 3;
	private static final int MIN_LEVEL = 106;
	
	public Q10533_OrfensAmbition()
	{
		super(10533);
		addStartNpc(START_NPC);
		addTalkId(START_NPC, TALK_NPC_1);
		addKillId(BOSS_IDS);
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
			case "accept.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					qs.setCond(TALK_NPC_1_COND);
				}
				break;
			}
			case "accept2.htm":
			{
				if (qs.isCond(TALK_NPC_1_COND))
				{
					qs.startQuest();
					qs.setCond(KILLING_COND);
				}
				break;
			}
			case "reward.html":
			{
				if (qs.isCond(FINISH_COND))
				{
					// Reward.
					addExpAndSp(player, 99527685300L, 99527580);
					rewardItems(player, 46151, 1);
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
					if (qs.isCond(TALK_NPC_1_COND))
					{
						htmltext = "accept.htm";
					}
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player, QUEST_TYPE);
					break;
				}
			}
		}
		else if (npc.getId() == TALK_NPC_1)
		{
			switch (qs.getState())
			{
				case State.STARTED:
				{
					if (qs.isCond(TALK_NPC_1_COND))
					{
						htmltext = "start2.htm";
					}
					else if (qs.isCond(KILLING_COND))
					{
						htmltext = "accept2.htm";
					}
					else if (qs.isCond(FINISH_COND))
					{
						htmltext = "finish.html";
					}
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player, QUEST_TYPE);
					break;
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(KILLING_COND) && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
		{
			qs.setCond(FINISH_COND);
		}
	}
}
