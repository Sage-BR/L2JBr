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
package quests.Q10840_TimeToRecover;

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

/**
 * Time to Recover (10840)
 * @URL https://l2wiki.com/Time_to_Recover
 * @author Gigi
 */
public class Q10840_TimeToRecover extends Quest
{
	// NPC
	private static final int SHERRY = 34066;
	private static final int[] MONSTERS =
	{
		23512, // Atelia High Priest
		23509, // Fortress Dark Wizard
		23507, // Atelia Passionate Soldier
		23508, // Atelia Elite Captain
		23510, // Atelia Flame Master
		23511 // Fortress Archon
	};
	// Items
	private static final int BLACKBIRD_REPORT_SHERRY = 46137;
	private static final int BLACKBIRD_SEAL = 46132;
	// Misc
	private static final int MIN_LEVEL = 101;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10840_TimeToRecover()
	{
		super(10840);
		addStartNpc(SHERRY);
		addTalkId(SHERRY);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "34066-00.htm");
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
			case "34066-02.htm":
			case "34066-04.htm":
			{
				htmltext = event;
				break;
			}
			case "34066-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34066-08.html":
			{
				giveItems(player, BLACKBIRD_REPORT_SHERRY, 1);
				addExpAndSp(player, 14524603380L, 34858800);
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
				if (!hasQuestItems(player, BLACKBIRD_SEAL))
				{
					htmltext = "34066-03.htm";
					break;
				}
				htmltext = "34066-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "34066-06.html";
				}
				else
				{
					htmltext = "34066-07.html";
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
		if ((qs != null) && qs.isCond(1))
		{
			int count = qs.getInt(KILL_COUNT_VAR);
			qs.set(KILL_COUNT_VAR, ++count);
			if (count >= 250)
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
				holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_EMBRYO_3, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}