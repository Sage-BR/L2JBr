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
package quests.Q00762_AnOminousRequest;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * An Ominous Request (762)
 * @author St3eT
 */
public class Q00762_AnOminousRequest extends Quest
{
	// NPCs
	private static final int MYSTERIOUS_WIZARD = 31522;
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
	// Items
	private static final int BONE = 36670; // Monster Bone
	private static final int BLOOD = 36671; // Monster Blood
	private static final int STEEL_DOOR_BOX = 37391; // Steel Door Guild Reward Box (Low-grade)
	// Rewards
	//@formatter:off
	// Format: min item count, exp reward, sp reward, item count reward
	private static final int[][] REWARD =
	{
		{900, 141_403_500, 33_930, 10},
		{800, 127_263_150, 30_537, 9},
		{700, 113_122_800, 27_144, 8},
		{600, 98_982_450, 23_751, 7},
		{500, 84_842_100, 20_358, 6},
		{400, 70_701_750, 16_965, 5},
		{300, 56_546_400, 13_572, 4},
		{200, 42_421_050, 10_179, 3},
		{100, 28_280_700, 6789, 2},
		{0, 14_140_350, 3393, 1},
	};
	//@formatter:on
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 70;
	
	public Q00762_AnOminousRequest()
	{
		super(762);
		addStartNpc(MYSTERIOUS_WIZARD);
		addTalkId(MYSTERIOUS_WIZARD);
		addKillId(MONSTERS);
		registerQuestItems(BONE, BLOOD);
		addCondNotRace(Race.ERTHEIA, "31522-10.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "31522-11.htm");
		addCondInCategory(CategoryType.MAGE_GROUP, "31522-11.htm");
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
			case "31522-02.htm":
			case "31522-03.htm":
			case "31522-07.html":
			case "31522-08.html":
			{
				htmltext = event;
				break;
			}
			case "31522-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31522-09.html":
			{
				if (qs.isCond(2))
				{
					final long itemCount = getQuestItemsCount(player, BLOOD);
					
					for (int[] data : REWARD)
					{
						if (itemCount >= data[0])
						{
							if (player.getLevel() >= MIN_LEVEL)
							{
								addExpAndSp(player, data[1], data[2]);
							}
							giveItems(player, STEEL_DOOR_BOX, data[3]);
							qs.exitQuest(QuestType.DAILY, true);
							htmltext = event;
							break;
						}
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "31522-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = qs.isCond(1) ? "31522-05.html" : "31522-06.html";
				break;
			}
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = getAlreadyCompletedMsg(player, QuestType.DAILY);
				}
				else
				{
					qs.setState(State.CREATED);
					htmltext = "31522-01.htm";
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
		
		if ((qs != null) && (qs.isCond(1) || qs.isCond(2)) && (getRandom(100) < 15))
		{
			if (getQuestItemsCount(killer, BONE) < 50)
			{
				giveItems(killer, BONE, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				
				if (getQuestItemsCount(killer, BONE) >= 50)
				{
					qs.setCond(2, true);
					showOnScreenMsg(killer, NpcStringId.YOU_CAN_GATHER_MORE_MONSTER_BLOOD, ExShowScreenMessage.TOP_CENTER, 6000);
				}
			}
			else
			{
				giveItems(killer, BLOOD, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}