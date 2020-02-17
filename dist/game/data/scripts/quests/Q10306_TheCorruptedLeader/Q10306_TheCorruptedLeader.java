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
package quests.Q10306_TheCorruptedLeader;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10305_UnstoppableFutileEfforts.Q10305_UnstoppableFutileEfforts;

/**
 * The Corrupted Leader (10306)
 * @author Gladicek
 */
public class Q10306_TheCorruptedLeader extends Quest
{
	// NPCs
	private static final int NOETI_KASHERON = 32896;
	private static final int KIMERIAN = 25745;
	// Items
	private static final int ENCHANT_ARMOR_R = 17527;
	private static final int[] REWARD_CRYSTALS =
	{
		9552,
		9553,
		9554,
		9555,
		9556,
		9557,
	};
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q10306_TheCorruptedLeader()
	{
		super(10306);
		addStartNpc(NOETI_KASHERON);
		addTalkId(NOETI_KASHERON);
		addKillId(KIMERIAN);
		
		addCondMinLevel(MIN_LEVEL, "32896-08.htm");
		addCondCompletedQuest(Q10305_UnstoppableFutileEfforts.class.getSimpleName(), "32896-08.htm");
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
			case "32896-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32896-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32896-06.html":
			{
				if (qs.isCond(2) && (player.getLevel() >= MIN_LEVEL))
				{
					addExpAndSp(player, 9_479_594, 2_275);
					giveItems(player, ENCHANT_ARMOR_R, 2);
					giveItems(player, getRandomEntry(REWARD_CRYSTALS), 1);
					qs.exitQuest(false, true);
					htmltext = "32895-09.html";
					
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
				}
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
				htmltext = "32896-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "32896-04.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "32896-05.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "32896-07.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		
		if ((qs != null) && (qs.isCond(1)))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, player, isSummon);
	}
}