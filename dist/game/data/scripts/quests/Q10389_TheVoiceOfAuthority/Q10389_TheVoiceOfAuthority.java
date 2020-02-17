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
package quests.Q10389_TheVoiceOfAuthority;

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

import quests.Q10388_ConspiracyBehindDoor.Q10388_ConspiracyBehindDoor;

/**
 * @author Gigi
 */
public class Q10389_TheVoiceOfAuthority extends Quest
{
	// NPCs
	private static final int RADZEN = 33803;
	// Monsters
	private static final int[] MONSTERS =
	{
		22139, // Old Aristocrat's Soldier
		22140, // Zombie Worker
		22141, // Forgotten Victim
		22142, // Triol's Layperson
		22144, // Resurrected Temple Knight
		22145, // Ritual Sacrifice
		22147, // Ritual Offering
		22148, // Triol's Believer
		22154, // Ritual Sacrifice
		22155, // Triol's High Priest
	};
	// Misc
	private static final int MIN_LEVEL = 97;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10389_TheVoiceOfAuthority()
	{
		super(10389);
		addCondCompletedQuest(Q10388_ConspiracyBehindDoor.class.getSimpleName(), "no_quest.html");
		addStartNpc(RADZEN);
		addTalkId(RADZEN);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "accepted.html":
			{
				qs.startQuest();
				break;
			}
			case "endquest.html":
			{
				giveAdena(player, 1302720, true);
				giveItems(player, 8067, 1);
				addExpAndSp(player, 592767000, 142264);
				qs.exitQuest(false, true);
				htmltext = "endquest.html";
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
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
			case State.CREATED:
			{
				htmltext = player.getLevel() >= MIN_LEVEL ? "start.htm" : "no_level.html";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "notcollected.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "collected.html";
				}
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
			if (killCount >= 30)
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
				holder.add(new NpcLogListHolder(NpcStringId.ELIMINATE_THE_PAGANS_IN_THE_ANTEROOM, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}