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
package quests.Q00817_BlackAteliaResearch;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10841_DeepInsideAteliaFortress.Q10841_DeepInsideAteliaFortress;

/**
 * Black Atelia Research (817)
 * @URL https://l2wiki.com/Black_Atelia_Research
 * @author Gigi
 */
public class Q00817_BlackAteliaResearch extends Quest
{
	// NPC
	private static final int KAYSYA = 34051;
	private static final int[] BOSS =
	{
		23603, // Guardian Sinistra
		23604, // Guardian Destra
		26128, // Kelbim's Clone
	};
	// Items
	private static final int BLACK_ATELIA_POWDER = 46145;
	private static final int HARDENER_POUCH_R = 32779;
	// Misc
	private static final int MIN_LEVEL = 101;
	
	public Q00817_BlackAteliaResearch()
	{
		super(817);
		addStartNpc(KAYSYA);
		addTalkId(KAYSYA);
		addKillId(BOSS);
		registerQuestItems(BLACK_ATELIA_POWDER);
		addCondMinLevel(MIN_LEVEL, "34051-00.htm");
		addCondCompletedQuest(Q10841_DeepInsideAteliaFortress.class.getSimpleName(), "34051-00.htm");
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
			case "34051-02.htm":
			case "34051-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34051-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34051-07.html":
			{
				giveItems(player, HARDENER_POUCH_R, 1);
				addExpAndSp(player, 3631150845L, 8714700);
				qs.exitQuest(QuestType.REPEATABLE, true);
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
				htmltext = "34051-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "34051-05.html";
				}
				else if (qs.isCond(2) && hasQuestItems(player, BLACK_ATELIA_POWDER))
				{
					htmltext = "34051-06.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 5, npc);
		if ((qs != null) && qs.isCond(1) && giveItemRandomly(killer, BLACK_ATELIA_POWDER, 1, 1, 0.6, true))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}