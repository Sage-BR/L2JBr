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
package quests.Q10757_QuietingTheStorm;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10756_AnInterdimensionalDraft.Q10756_AnInterdimensionalDraft;

/**
 * Quieting the Storm (10757)
 * @author malyelfik
 */
public class Q10757_QuietingTheStorm extends Quest
{
	// NPC
	private static final int PIO = 33963;
	// Monsters
	private static final int VORTEX = 23417;
	private static final int GIANT_WINDIMA = 23419;
	private static final int IMMENSE_WINDIMA = 23420;
	// Misc
	private static final int MIN_LEVEL = 24;
	private static final String VORTEX_COUNT_VAR = "VortexKillCount";
	private static final String WINDIMA_COUNT_VAR = "WindimaKillCount";
	
	public Q10757_QuietingTheStorm()
	{
		super(10757);
		addStartNpc(PIO);
		addTalkId(PIO);
		addKillId(VORTEX, GIANT_WINDIMA, IMMENSE_WINDIMA);
		
		addCondRace(Race.ERTHEIA, "33963-00.htm");
		addCondMinLevel(MIN_LEVEL, "33963-00.htm");
		addCondCompletedQuest(Q10756_AnInterdimensionalDraft.class.getSimpleName(), "33963-00.htm");
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
					addExpAndSp(player, 808754, 151);
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
		if ((qs != null) && qs.isCond(1))
		{
			int vortexCount = qs.getInt(VORTEX_COUNT_VAR);
			int windimaCount = qs.getInt(WINDIMA_COUNT_VAR);
			if (npc.getId() == VORTEX)
			{
				if (vortexCount < 5)
				{
					qs.set(VORTEX_COUNT_VAR, ++vortexCount);
				}
			}
			else if (windimaCount != 1)
			{
				qs.set(WINDIMA_COUNT_VAR, ++windimaCount);
			}
			
			if ((vortexCount >= 5) && (windimaCount >= 1))
			{
				qs.setCond(2, true);
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
			final Set<NpcLogListHolder> holder = new HashSet<>(2);
			
			// Wind vortex
			final int vortexCount = qs.getInt(VORTEX_COUNT_VAR);
			if (vortexCount > 0)
			{
				holder.add(new NpcLogListHolder(VORTEX, false, vortexCount));
			}
			
			// Windima
			final int windimaCount = qs.getInt(WINDIMA_COUNT_VAR);
			if (windimaCount == 1)
			{
				holder.add(new NpcLogListHolder(NpcStringId.IMMENSE_WINDIMA_OR_GIANT_WINDIMA, windimaCount));
			}
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
