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
package quests.Q10307_TheCorruptedLeaderHisTruth;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;

import quests.Q10306_TheCorruptedLeader.Q10306_TheCorruptedLeader;

/**
 * The Corrupted Leader: His Truth (10307)
 * @URL https://l2wiki.com/The_Corrupted_Leader:_His_Truth
 * @VIDEO https://www.youtube.com/watch?v=MI5Hyu7TtLw
 * @author Gigi
 */
public class Q10307_TheCorruptedLeaderHisTruth extends Quest
{
	// NPCs
	private static final int NEOTI_MIMILEAD = 32895;
	private static final int NAOMI_KASHERON = 32896;
	private static final int[] BOSS =
	{
		25745,
		25747
	};
	private static final int ENCHANT_ARMOR_R = 17527;
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q10307_TheCorruptedLeaderHisTruth()
	{
		super(10307);
		addStartNpc(NAOMI_KASHERON);
		addTalkId(NAOMI_KASHERON, NEOTI_MIMILEAD);
		addKillId(BOSS);
		addCondMinLevel(MIN_LEVEL, "32896-03.html");
		addCondCompletedQuest(Q10306_TheCorruptedLeader.class.getSimpleName(), "32896-03.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		switch (event)
		{
			case "32896-07.html":
			case "32895-02.html":
			case "32895-03.html":
			{
				htmltext = event;
				break;
			}
			case "32896-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32896-08.html":
			{
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "32895-04.html":
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					addExpAndSp(player, 11779522, 2827);
					giveItems(player, ENCHANT_ARMOR_R, 5);
					qs.exitQuest(QuestType.ONE_TIME, true);
					htmltext = event;
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
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
		
		switch (npc.getId())
		{
			case NAOMI_KASHERON:
			{
				if (qs.isCreated())
				{
					htmltext = "32896-01.htm";
					break;
				}
				else if (qs.isCond(1))
				{
					htmltext = "32896-05.htm";
					break;
				}
				else if (qs.isCond(2))
				{
					htmltext = "32895-06.html";
					break;
				}
				else if (qs.isCompleted())
				{
					htmltext = "32896-02.html";
				}
				break;
			}
			case NEOTI_MIMILEAD:
			{
				if (qs.isCond(3))
				{
					htmltext = "32895-01.html";
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1) && CommonUtil.contains(BOSS, npc.getId()))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, player, isSummon);
	}
}