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
package quests.Q10833_PutTheQueenOfSpiritsToSleep;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10832_EnergyOfSadnessAndAnger.Q10832_EnergyOfSadnessAndAnger;

/**
 * Put the Queen of Spirits to Sleep (10833)
 * @URL https://l2wiki.com/Put_the_Queen_of_Spirits_to_Sleep
 * @author Gigi
 */
public class Q10833_PutTheQueenOfSpiritsToSleep extends Quest
{
	// NPC
	private static final int FERIN = 34054;
	private static final int ISABELLA = 26131;
	// Items
	private static final int MARK_OF_TRUST_HIGH_GRADE = 45848;
	private static final int ISABELLAS_EVIL_THOUGHTS = 45839;
	private static final int SOE = 46158;
	private static final int ELCYUM_CRYSTAL = 36514;
	private static final int GIANTS_CODEX = 46152;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q10833_PutTheQueenOfSpiritsToSleep()
	{
		super(10833);
		addStartNpc(FERIN);
		addTalkId(FERIN);
		addKillId(ISABELLA);
		registerQuestItems(ISABELLAS_EVIL_THOUGHTS);
		addCondMinLevel(MIN_LEVEL, "34054-00.htm");
		addCondCompletedQuest(Q10832_EnergyOfSadnessAndAnger.class.getSimpleName(), "34054-00.htm");
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
			case "34054-02.htm":
			case "34054-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34054-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34054-07.html":
			{
				giveItems(player, GIANTS_CODEX, 1);
				giveItems(player, ELCYUM_CRYSTAL, 1);
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
				if (!hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE))
				{
					htmltext = "noItem.htm";
					break;
				}
				htmltext = "34054-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "34054-05.html";
				}
				else if (qs.isCond(2) && hasQuestItems(player, ISABELLAS_EVIL_THOUGHTS))
				{
					htmltext = "34054-06.html";
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
		if ((qs != null) && qs.isCond(1) && (npc.getId() == ISABELLA))
		{
			giveItems(killer, ISABELLAS_EVIL_THOUGHTS, 1);
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}