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
package quests.Q10417_DaimonTheWhiteEyed;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10416_InSearchOfTheEyeOfArgos.Q10416_InSearchOfTheEyeOfArgos;

/**
 * Daimon the White-eyed (10417)
 * @author St3eT
 */
public class Q10417_DaimonTheWhiteEyed extends Quest
{
	// NPCs
	private static final int EYE_OF_ARGOS = 31683;
	private static final int JANITT = 33851;
	private static final int DAIMON_THE_WHITEEYED = 27499;
	private static final int[] MONSTERS =
	{
		21294, // Canyon Antelope
		21296, // Canyon Bandersnatch
		23311, // Valley Buffalo
		23312, // Valley Grendel
		21295, // Canyon Antelope Slave
		21297, // Canyon Bandersnatch Slave
		21299, // Valley Buffalo Slave
		21304, // Valley Grendel Slave
	};
	// Items
	private static final int EAA = 730; // Scroll: Enchant Armor (A-grade)
	// Misc
	private static final int MIN_LEVEL = 70;
	private static final int MAX_LEVEL = 75;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10417_DaimonTheWhiteEyed()
	{
		super(10417);
		addStartNpc(EYE_OF_ARGOS);
		addTalkId(EYE_OF_ARGOS, JANITT);
		addKillId(DAIMON_THE_WHITEEYED);
		addKillId(MONSTERS);
		addCondNotRace(Race.ERTHEIA, "31683-09.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "31683-08.htm");
		addCondCompletedQuest(Q10416_InSearchOfTheEyeOfArgos.class.getSimpleName(), "31683-08.html");
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
			case "31683-02.htm":
			case "31683-03.htm":
			{
				htmltext = event;
				break;
			}
			case "31683-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31683-07.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					htmltext = event;
				}
				else if (qs.isCond(3))
				{
					qs.setCond(4, true);
					htmltext = "31683-07.html";
				}
				break;
			}
			case "31683-03.html":
			{
				if (qs.isCond(4))
				{
					qs.exitQuest(false, true);
					giveItems(player, EAA, 5);
					giveStoryQuestReward(npc, player);
					if (player.getLevel() > MIN_LEVEL)
					{
						addExpAndSp(player, 2_721_600, 653);
					}
					htmltext = event;
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
		
		if (qs.getState() == State.CREATED)
		{
			if (npc.getId() == EYE_OF_ARGOS)
			{
				htmltext = "31683-01.htm";
			}
		}
		else if (qs.getState() == State.STARTED)
		{
			switch (qs.getCond())
			{
				case 1:
				{
					htmltext = npc.getId() == EYE_OF_ARGOS ? "31683-05.html" : "33851-01.html";
					break;
				}
				case 2:
				{
					htmltext = npc.getId() == EYE_OF_ARGOS ? "31683-06.html" : "33851-01.html";
					break;
				}
				case 3:
				{
					htmltext = npc.getId() == EYE_OF_ARGOS ? "31683-06.html" : "33851-01.html";
					break;
				}
				case 4:
				{
					htmltext = npc.getId() == EYE_OF_ARGOS ? "31683-06.html" : "33851-02.html";
					break;
				}
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
			if (count >= 100)
			{
				qs.setCond(2, true);
			}
			else
			{
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		else if ((qs != null) && qs.isCond(2))
		{
			int killeddaimoneye = qs.getInt("killed_" + DAIMON_THE_WHITEEYED);
			if (npc.getId() == DAIMON_THE_WHITEEYED)
			{
				killeddaimoneye++;
				qs.set("killed_" + DAIMON_THE_WHITEEYED, killeddaimoneye);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				if (killeddaimoneye > 0)
				{
					qs.setCond(3, true);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1) && qs.isStarted())
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>();
				holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_BEASTS_OF_THE_VALLEY_2, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}