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
package quests.Q00787_TheRoleOfAWatcher;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * The Role of a Watcher (00787)
 * @URL https://l2wiki.com/The_Role_of_a_Watcher
 * @author Gigi / Stayway (rework helios)
 */
public class Q00787_TheRoleOfAWatcher extends Quest
{
	// NPCs
	private static final int NAMO = 33973;
	// Monsters
	private static final int[] MONSTERS =
	{
		23423, // Mesmer Dragon
		23424, // Gargoyle Dragon
		23425, // Black Dragon
		23427, // Sand Dragon
		23428, // Captain Dragonblood
		23429, // Minion Dragonblood
		23436, // Cave Servant Archer
		23437, // Cave Servant Warrior
		23438, // Metallic Cave Servant
		23439, // Iron Cave Servant
		23440, // Headless Knight
		23430, // Prey Drake
		23431, // Beast Drake
		23432, // Dust Drake
		23433, // Vampiric Drake
		23441, // Bloody Grave Warrior
		23442, // Dark Grave Warrior
		23443, // Dark Grave Wizard
		23444, // Dark Grave Knight
	};
	
	// Items
	private static final int DRAGON_BONE_DUST = 39736; // min 50
	private static final int DRAGON_BONE_FRAGMENT = 39737; // max 900
	private static final int EMISSARY_REWARD_BOX = 39728; // Emissary's Reward Box (High-grade)
	// Misc
	private static final int MIN_LEVEL = 75;
	private static final int MAX_LEVEL = 85;
	
	public Q00787_TheRoleOfAWatcher()
	{
		super(787);
		addStartNpc(NAMO);
		addTalkId(NAMO);
		addKillId(MONSTERS);
		registerQuestItems(DRAGON_BONE_DUST, DRAGON_BONE_FRAGMENT);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_level.html");
		addCondRace(Race.ERTHEIA, "noErtheia.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "33973-02.htm":
			case "33973-03.htm":
			case "33973-07.html":
			case "33973-08.html":
			{
				htmltext = event;
				break;
			}
			case "33973-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33973-09.html":
			{
				if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) < 100))
				{
					addExpAndSp(player, 14140350, 3393);
					giveItems(player, EMISSARY_REWARD_BOX, 1);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && ((getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) >= 100) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) <= 199)))
				{
					addExpAndSp(player, 28280700, 6786);
					giveItems(player, EMISSARY_REWARD_BOX, 2);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && ((getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) >= 200) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) <= 299)))
				{
					addExpAndSp(player, 42421050, 10179);
					giveItems(player, EMISSARY_REWARD_BOX, 3);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && ((getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) >= 300) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) <= 399)))
				{
					addExpAndSp(player, 56561400, 13572);
					giveItems(player, EMISSARY_REWARD_BOX, 4);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && ((getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) >= 400) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) <= 499)))
				{
					addExpAndSp(player, 70701750, 16965);
					giveItems(player, EMISSARY_REWARD_BOX, 5);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && ((getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) >= 500) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) <= 599)))
				{
					addExpAndSp(player, 84842100, 20358);
					giveItems(player, EMISSARY_REWARD_BOX, 6);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && ((getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) >= 600) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) <= 699)))
				{
					addExpAndSp(player, 98982450, 23751);
					giveItems(player, EMISSARY_REWARD_BOX, 7);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && ((getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) >= 700) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) <= 799)))
				{
					addExpAndSp(player, 113122800, 27144);
					giveItems(player, EMISSARY_REWARD_BOX, 8);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && ((getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) >= 800) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) <= 899)))
				{
					addExpAndSp(player, 127263150, 30537);
					giveItems(player, EMISSARY_REWARD_BOX, 9);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				if ((getQuestItemsCount(player, DRAGON_BONE_DUST) >= 50) && (getQuestItemsCount(player, DRAGON_BONE_FRAGMENT) >= 900))
				{
					addExpAndSp(player, 141403500, 33930);
					giveItems(player, EMISSARY_REWARD_BOX, 10);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == NAMO)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "33973-10.html";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "33973-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33973-05.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33973-06.html";
					}
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
		
		if ((qs != null) && (qs.isCond(1)))
		{
			if (giveItemRandomly(killer, npc, DRAGON_BONE_DUST, 1, 50, 0.15, true))
			{
				qs.setCond(2, true);
			}
		}
		if ((qs != null) && (qs.isCond(2)))
		{
			if (giveItemRandomly(killer, npc, DRAGON_BONE_FRAGMENT, 1, 900, 0.25, true))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}