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
package quests.Q10830_TheLostGardenOfSpirits;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10829_InSearchOfTheCause.Q10829_InSearchOfTheCause;

/**
 * The Lost Garden of Spirits (10830)
 * @URL https://l2wiki.com/The_Lost_Garden_of_Spirits
 * @author Gigi
 */
public class Q10830_TheLostGardenOfSpirits extends Quest
{
	// NPC
	private static final int CYPHONIA = 34055;
	// Monsters
	private static final int[] MONSTERS =
	{
		23550, // Kerberos Lager
		23551, // Kerberos Fort
		23552, // Kerberos Nero
		23553, // Fury Sylph Barrena
		23555, // Fury Sylph Temptress
		23556, // Fury Sylph Purka
		23557, // Fury Kerberos Leger
		23558 // Fury Kerberos Nero
	};
	// Items
	private static final int UNSTABLE_SPIRITS_ENERGY = 45821;
	private static final int SOE = 46158;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q10830_TheLostGardenOfSpirits()
	{
		super(10830);
		addStartNpc(CYPHONIA);
		addTalkId(CYPHONIA);
		addKillId(MONSTERS);
		registerQuestItems(UNSTABLE_SPIRITS_ENERGY);
		addCondMinLevel(MIN_LEVEL, "34055-00.htm");
		addCondCompletedQuest(Q10829_InSearchOfTheCause.class.getSimpleName(), "34055-00.htm");
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
			case "34055-02.htm":
			case "34055-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34055-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34055-07.html":
			{
				giveItems(player, SOE, 1);
				addExpAndSp(player, 1637472704L, 14237820);
				qs.exitQuest(false, true);
				htmltext = event;
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
				htmltext = "34055-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "34055-05.html";
				}
				else
				{
					htmltext = "34055-06.html";
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
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && giveItemRandomly(killer, npc, UNSTABLE_SPIRITS_ENERGY, 1, 100, 0.5, true))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}