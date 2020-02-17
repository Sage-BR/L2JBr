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
package quests.custom.Q10516_UnveiledFafurionTemple;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Unveiled Fafurion Temple (10516)
 * @URL https://l2wiki.com/Unveiled_Fafurion_Temple
 * @author Mobius
 */
public class Q10516_UnveiledFafurionTemple extends Quest
{
	// NPCs
	private static final int START_NPC = 33907;
	private static final int TALK_NPC_1 = 34491;
	private static final int TALK_NPC_2 = 34489;
	private static final int FINISH_NPC = 34490;
	// Misc
	private static final int TALK_NPC_1_COND = 1;
	private static final int TALK_NPC_2_COND = 2;
	private static final int FINISH_NPC_COND = 3;
	private static final int MIN_LEVEL = 110;
	
	public Q10516_UnveiledFafurionTemple()
	{
		super(10516);
		addStartNpc(START_NPC);
		addTalkId(START_NPC, TALK_NPC_1, TALK_NPC_2, FINISH_NPC);
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
			case "talk_1_2.html":
			{
				if ((npc.getId() == TALK_NPC_1) && qs.isCond(TALK_NPC_1_COND))
				{
					qs.setCond(TALK_NPC_2_COND, true);
				}
				break;
			}
			case "talk_2_2.html":
			{
				if ((npc.getId() == TALK_NPC_2) && qs.isCond(TALK_NPC_2_COND))
				{
					qs.setCond(FINISH_NPC_COND, true);
				}
				break;
			}
			case "reward.html":
			{
				if ((npc.getId() == FINISH_NPC) && qs.isCond(FINISH_NPC_COND))
				{
					// Reward.
					addExpAndSp(player, 5556186900L, 5556186);
					giveAdena(player, 139671, false);
					qs.exitQuest(false, true);
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == START_NPC)
				{
					htmltext = "start.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case START_NPC:
					{
						htmltext = "accept.htm";
						break;
					}
					case TALK_NPC_1:
					{
						if (qs.isCond(TALK_NPC_1_COND))
						{
							htmltext = "talk_1_1.html";
						}
						else if (qs.getCond() > TALK_NPC_1_COND)
						{
							htmltext = "talk_1_2.html";
						}
						break;
					}
					case TALK_NPC_2:
					{
						if (qs.isCond(TALK_NPC_2_COND))
						{
							htmltext = "talk_2_1.html";
						}
						else if (qs.getCond() > TALK_NPC_2_COND)
						{
							htmltext = "talk_2_2.html";
						}
						break;
					}
					case FINISH_NPC:
					{
						if (qs.isCond(FINISH_NPC_COND))
						{
							htmltext = "finish.html";
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
