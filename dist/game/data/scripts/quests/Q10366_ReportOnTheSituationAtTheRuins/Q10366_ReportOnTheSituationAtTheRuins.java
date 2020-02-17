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
package quests.Q10366_ReportOnTheSituationAtTheRuins;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10365_ForTheSearchdogKing.Q10365_ForTheSearchdogKing;

/**
 * Report on the situation at the ruins (10366)
 * @URL https://l2wiki.com/Report_on_the_situation_at_the_ruins
 * @author Gigi
 */
public class Q10366_ReportOnTheSituationAtTheRuins extends Quest
{
	// NPCs
	private static final int SEBION = 32978;
	private static final int[] MONSTERS =
	{
		22993, // Critter
		22994, // Avian
		22995, // Rider
		23122 // Eyesaroch
	};
	// Misc
	private static final int MIN_LEVEL = 17;
	private static final int MAX_LEVEL = 25;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10366_ReportOnTheSituationAtTheRuins()
	{
		super(10366);
		addStartNpc(SEBION);
		addTalkId(SEBION);
		addKillId(MONSTERS);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "32978-08.html");
		addCondCompletedQuest(Q10365_ForTheSearchdogKing.class.getSimpleName(), "32978-08.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "32978-02.htm":
			case "32978-02a.htm":
			{
				htmltext = event;
				break;
			}
			case "32978-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32978-06.html":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 114000, 15);
					qs.exitQuest(false, true);
				}
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
		String htmltext = null;
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "32978-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "32978-04.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "32978-05.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "32978-07.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			int killCount = qs.getInt(KILL_COUNT_VAR);
			qs.set(KILL_COUNT_VAR, ++killCount);
			if (killCount >= 40)
			{
				qs.setCond(2, true);
			}
			else
			{
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>();
				holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_HUSKS, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}