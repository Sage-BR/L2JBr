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
package quests.Q10413_EmbryoInTheForestOfTheDead;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10412_ASuspiciousVagabondInTheForest.Q10412_ASuspiciousVagabondInTheForest;

/**
 * Embryo in the Forest of the Dead (10413)
 * @author St3eT
 */
public class Q10413_EmbryoInTheForestOfTheDead extends Quest
{
	// NPCs
	private static final int HATUBA = 33849;
	private static final int EMBRYO = 27509;
	private static final int[] MONSTERS =
	{
		21547, // Corrupted Knight
		21551, // Resurrected Royal Guard
		21553, // Trampled Man
		21557, // Bone Snatcher
		21559, // Bone Maker
		21561, // Sacrificed Man
		21563, // Bone Collector
		21565, // Bone Animator
		21567, // Bone Slayer
		21570, // Ghost of Betrayer
		21572, // Bone Sweeper
		21574, // Bone Grinder
		21578, // Behemoth Zombie
		21580, // Bone Caster
		21581, // Bone Puppeteer
		21583, // Bone Scavenger
		21587, // Vampire Warrior
		21590, // Vampire Magister
		21593, // Vampire Warlord
		21596, // Requiem Lord
		21599, // Requiem Priest
		21549, // Corrupted Royal Guard
		21555, // Slaughter Executioner
		21560, // Bone Shaper
		21562, // Guillotine's Ghost
		21564, // Skull Collector
		21566, // Skull Animator
		21568, // Devil Bat
		21571, // Ghost of Rebel Soldier
		21573, // Atrox
		21576, // Ghost of Guillotine
		21579, // Ghost of Rebel Leader
		21582, // Vampire Soldier
		21585, // Vampire Magician
		21586, // Vampire Adept
		21588, // Vampire Wizard
		21591, // Vampire Magister
		21595, // Vampire Warlord
		21599, // Requiem Priest
	};
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 70;
	
	public Q10413_EmbryoInTheForestOfTheDead()
	{
		super(10413);
		addStartNpc(HATUBA);
		addTalkId(HATUBA);
		addKillId(MONSTERS);
		addKillId(EMBRYO);
		addCondNotRace(Race.ERTHEIA, "33849-08.html");
		addCondInCategory(CategoryType.MAGE_GROUP, "33849-09.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33849-09.htm");
		addCondCompletedQuest(Q10412_ASuspiciousVagabondInTheForest.class.getSimpleName(), "33849-09.htm");
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
			case "33849-02.htm":
			case "33849-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33849-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33849-07.html":
			{
				if (qs.isCond(2))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(npc, player);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 161046201, 4072);
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
		String htmltext = null;
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33849-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = qs.isCond(1) ? "33849-05.html" : "33849-06.html";
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
			if (npc.getId() == EMBRYO)
			{
				int count = qs.getInt("KillCount");
				qs.set("KillCount", ++count);
				if (count >= 300)
				{
					qs.setCond(2, true);
				}
				else
				{
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
			else
			{
				final Npc embryo = addSpawn(EMBRYO, npc, false, 60000);
				addAttackPlayerDesire(embryo, killer);
				embryo.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_DARE_INTERFERE_WITH_EMBRYO_SURELY_YOU_WISH_FOR_DEATH);
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
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			npcLogList.add(new NpcLogListHolder(EMBRYO, false, qs.getInt("KillCount")));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}