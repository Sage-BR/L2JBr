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
package quests.Q10796_TheEyeThatDefiedTheGods;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;

/**
 * The Eye that Defied the Gods (10796)
 * @URL https://l2wiki.com/The_Eye_that_Defied_the_Gods
 * @author Gigi
 */
public class Q10796_TheEyeThatDefiedTheGods extends Quest
{
	// NPCs
	private static final int HERMIT = 31616;
	private static final int EYE_OF_ARGOS = 31683;
	// Monsters
	private static final int[] MONSTERS =
	{
		21294, // Canyon Antelope
		21296, // Canyon Bandersnatch
		23311, // Valley Buffalo
		23312, // Valley Grendel
		21295, // Canyon Antelope Slave
		21297, // Canyon Bandersnatch Slave
		21299, // Valley Buffalo Slave
		21304 // Valley Grendel Slave
	};
	// Misc
	private static final int MIN_LEVEL = 70;
	private static final int MAX_LEVEL = 75;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10796_TheEyeThatDefiedTheGods()
	{
		super(10796);
		addStartNpc(HERMIT);
		addTalkId(HERMIT, EYE_OF_ARGOS);
		addKillId(MONSTERS);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_level.html");
		addCondRace(Race.ERTHEIA, "noErtheia.html");
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
			case "31616-02.htm":
			case "31616-03.htm":
			{
				break;
			}
			case "31616-04.htm":
			{
				qs.startQuest();
				break;
			}
			case "31683-02.html":
			{
				if (qs.isCond(2) && (qs.getInt(KILL_COUNT_VAR) >= 200))
				{
					
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 178732196, 261);
						giveStoryQuestReward(npc, player);
						qs.exitQuest(false, true);
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
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
		switch (npc.getId())
		{
			case HERMIT:
			{
				if (qs.isCreated())
				{
					htmltext = "31616-01.htm";
				}
				else if (qs.isCond(1))
				{
					htmltext = "31616-05.html";
				}
				if (qs.isCompleted())
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
			case EYE_OF_ARGOS:
			{
				if (qs.isCond(2))
				{
					htmltext = "31683-01.html";
				}
				else if (qs.isCompleted())
				{
					htmltext = getAlreadyCompletedMsg(player);
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
			if (killCount >= 200)
			{
				qs.setCond(0);
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
				holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_BEASTS_OF_THE_VALLEY_3, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}