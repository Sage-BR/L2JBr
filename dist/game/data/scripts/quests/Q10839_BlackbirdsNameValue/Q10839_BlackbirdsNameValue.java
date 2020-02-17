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
package quests.Q10839_BlackbirdsNameValue;

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
 * Blackbird's Name Value (10839)
 * @URL https://l2wiki.com/Blackbird%27s_Name_Value
 * @author Gigi
 */
public class Q10839_BlackbirdsNameValue extends Quest
{
	// NPC
	private static final int LAFFIAN = 34065;
	private static final int[] MONSTERS =
	{
		23512, // Atelia High Priest
		23509, // Fortress Dark Wizard
		23507, // Atelia Passionate Soldier
		23508 // Atelia Elite Captain
	};
	// Items
	private static final int BLACKBIRD_REPORT_LAFFIAN = 46136;
	private static final int BLACKBIRD_SEAL = 46132;
	// Misc
	private static final int MIN_LEVEL = 101;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10839_BlackbirdsNameValue()
	{
		super(10839);
		addStartNpc(LAFFIAN);
		addTalkId(LAFFIAN);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "34065-00.htm");
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
			case "34065-02.htm":
			case "34065-04.htm":
			case "34065-05.htm":
			{
				htmltext = event;
				break;
			}
			case "34065-06.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34065-09.html":
			{
				giveItems(player, BLACKBIRD_REPORT_LAFFIAN, 1);
				addExpAndSp(player, 12103836150L, 2904900);
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
					htmltext = "34065-03.htm";
					break;
				}
				htmltext = "34065-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "34065-07.html";
				}
				else
				{
					htmltext = "34065-08.html";
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
			if (count >= 200)
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
				holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_EMBRYO_2, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}