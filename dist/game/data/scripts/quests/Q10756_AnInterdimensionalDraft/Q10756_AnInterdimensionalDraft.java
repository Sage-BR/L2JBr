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
package quests.Q10756_AnInterdimensionalDraft;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * An Interdimensional Draft (10756)
 * @author malyelfik
 */
public class Q10756_AnInterdimensionalDraft extends Quest
{
	// NPC
	private static final int PIO = 33963;
	// Monsters
	private static final int[] MONSTERS =
	{
		20078, // Whispering Wind
		21023, // Sobbing Wind
		21024, // Babbling Wind
		21025, // Giggling Wind
		21026, // Singing Wind
		23414, // Windima
		23415, // Windima Feri
		23416, // Windima Resh
	};
	// Items
	private static final int UNWORLDLY_WIND = 39493;
	// Misc
	private static final int MIN_LEVEL = 20;
	private static final double DROP_RATE = 0.7d;
	
	public Q10756_AnInterdimensionalDraft()
	{
		super(10756);
		addStartNpc(PIO);
		addTalkId(PIO);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "33963-00.htm");
		addCondMinLevel(MIN_LEVEL, "33963-00.htm");
		registerQuestItems(UNWORLDLY_WIND);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33963-01.htm":
			case "33963-02.htm":
			case "33963-03.htm":
			case "33963-04.htm":
			{
				break;
			}
			case "33963-05.htm":
			{
				qs.startQuest();
				break;
			}
			case "33963-08.html":
			{
				if (qs.isCond(2))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 267479, 41);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
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
				htmltext = "33963-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33963-06.html" : "33963-07.html";
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
		if ((qs != null) && qs.isCond(1) && giveItemRandomly(killer, UNWORLDLY_WIND, 1, 30, DROP_RATE, true))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
