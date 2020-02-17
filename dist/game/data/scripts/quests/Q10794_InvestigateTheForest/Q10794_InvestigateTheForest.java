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
package quests.Q10794_InvestigateTheForest;

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

import quests.Q10793_SaveTheSouls.Q10793_SaveTheSouls;

/**
 * Investigate The Forest (10794)
 * @URL https://l2wiki.com/Investigate_the_Forest
 * @author Gigi
 */
public class Q10794_InvestigateTheForest extends Quest
{
	// NPCs
	private static final int HATUBA = 33849;
	private static final int TOMBSTONE = 31531;
	// Monsters
	private static final int[] MONSTERS =
	{
		21562, // Guillotine's Ghost
		21563, // Bone Collector
		21564, // Skull Collector
		21565, // Bone Animator
		21566, // Skull Animator
		21567, // Bone Slayer
		21568, // Devil Bat
		21570, // Ghost of Betrayer
		21571, // Ghost of Rebel Soldier
		21572, // Bone Sweeper
		21573, // Atrox
		21574, // Bone Grinder
		21576, // Ghost of Guillotine
		21578, // Behemoth Zombie
		21579, // Ghost of Rebel Leader
		21580, // Bone Caster
		21581, // Bone Puppeteer
		21582, // Vampire Soldier
		21583, // Bone Scavenger
		21585, // Vampire Magician
		21586, // Vampire Adept
		21587, // Vampire Warrior
		21588, // Vampire Wizard
		21590, // Vampire Magister
		21593, // Vampire Warlord
		21596, // Requiem Lord
		21597, // Requiem Lord
		21599, // Requiem Priest
	};
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 70;
	private static final String KILL_COUNT_VAR = "KillCount";
	// Items
	private static final int OLD_JEWELRY_BOX = 39725;
	
	public Q10794_InvestigateTheForest()
	{
		super(10794);
		addStartNpc(HATUBA);
		addTalkId(HATUBA, TOMBSTONE);
		addKillId(MONSTERS);
		registerQuestItems(OLD_JEWELRY_BOX);
		addCondRace(Race.ERTHEIA, "33849-00.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33849-01.htm");
		addCondStart(PlayerInstance::isMageClass, "33849-01.htm");
		addCondCompletedQuest(Q10793_SaveTheSouls.class.getSimpleName(), "restriction.html");
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
			case "33849-03.htm":
			case "33849-04.htm":
			{
				htmltext = event;
				break;
			}
			case "33849-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31531-02.html":
			{
				giveItems(player, OLD_JEWELRY_BOX, 1);
				qs.setCond(2);
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "33849-08.html":
			{
				if (qs.isCond(4))
				{
					takeItems(player, OLD_JEWELRY_BOX, -1);
					giveStoryQuestReward(npc, player);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 93856309, 4072);
					}
					htmltext = event;
					qs.exitQuest(false, true);
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33849-02.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case HATUBA:
					{
						htmltext = (qs.isCond(1)) ? "33849-06.html" : "33849-07.html";
						break;
					}
					case TOMBSTONE:
					{
						final int count = qs.getInt(KILL_COUNT_VAR);
						if ((count >= 100) && (getQuestItemsCount(player, OLD_JEWELRY_BOX) < 1))
						{
							htmltext = "31531-01.html";
						}
						break;
					}
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
			final int count = qs.getInt(KILL_COUNT_VAR) + 1;
			qs.set(KILL_COUNT_VAR, count);
			if (count >= 100)
			{
				qs.setCond(1, true);
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
				holder.add(new NpcLogListHolder(NpcStringId.KILL_THE_UNDEAD_NEAR_THE_CURSED_VILLAGE, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}