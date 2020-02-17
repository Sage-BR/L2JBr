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
package quests.Q00765_WeakeningTheKetraOrcForces;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Weakening the Ketra Orc Forces (765)
 * @URL https://l2wiki.com/Weakening_the_Ketra_Orc_Forces
 * @author Gigi
 */
public class Q00765_WeakeningTheKetraOrcForces extends Quest
{
	// NPC
	private static final int LUGONESS = 33852;
	// Monsters
	private static final int[] MONSTERS =
	{
		21324, // Ketra Orc Footman
		21327, // Ketra Orc Raider
		21328, // Ketra Orc Scout
		21329, // Ketra Orc Shaman
		21331, // Ketra Orc Warrior
		21332, // Ketra Orc Lieutenant
		21334, // Ketra Orc Medium
		21336, // Ketra Orc White Captain
		21338, // Ketra Orc Seer
		21339, // Ketra Orc General
		21340, // Ketra Orc Battalion Commander
		21342, // Ketra Orc Grand Seer
		21343, // Ketra Commander
		21345, // Ketra's Head Shaman
		21347 // Ketra Prophet
	};
	// Items
	private static final int BADGE_SOLDIER = 36676;
	private static final int BADGE_GENERAL = 36677;
	// Rewards
	private static final int STEEL_DOOR_GUILD_BOX = 37393;
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 80;
	
	public Q00765_WeakeningTheKetraOrcForces()
	{
		super(765);
		addStartNpc(LUGONESS);
		addTalkId(LUGONESS);
		addKillId(MONSTERS);
		registerQuestItems(BADGE_SOLDIER, BADGE_GENERAL);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33852-00.htm");
		addCondNotRace(Race.ERTHEIA, "33852-07.html");
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
			case "33852-02.htm":
			case "33852-03.htm":
			case "33852-09.html":
			case "33852-09a.html":
			case "33852-10.html":
			{
				htmltext = event;
				break;
			}
			case "33852-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33852-06a.html":
			{
				if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && (getQuestItemsCount(player, BADGE_GENERAL) < 100))
				{
					addExpAndSp(player, 19164600, 191646);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 1);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && ((getQuestItemsCount(player, BADGE_GENERAL) >= 100) && (getQuestItemsCount(player, BADGE_GENERAL) <= 199)))
				{
					addExpAndSp(player, 38329200, 383292);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 2);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && ((getQuestItemsCount(player, BADGE_GENERAL) >= 200) && (getQuestItemsCount(player, BADGE_GENERAL) <= 299)))
				{
					addExpAndSp(player, 57493800, 574938);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 3);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && ((getQuestItemsCount(player, BADGE_GENERAL) >= 300) && (getQuestItemsCount(player, BADGE_GENERAL) <= 399)))
				{
					addExpAndSp(player, 76658400, 766584);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 4);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && ((getQuestItemsCount(player, BADGE_GENERAL) >= 400) && (getQuestItemsCount(player, BADGE_GENERAL) <= 499)))
				{
					addExpAndSp(player, 95823000, 958230);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 5);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && ((getQuestItemsCount(player, BADGE_GENERAL) >= 500) && (getQuestItemsCount(player, BADGE_GENERAL) <= 599)))
				{
					addExpAndSp(player, 114987600, 1149876);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 6);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && ((getQuestItemsCount(player, BADGE_GENERAL) >= 600) && (getQuestItemsCount(player, BADGE_GENERAL) <= 699)))
				{
					addExpAndSp(player, 134152200, 1341522);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 7);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && ((getQuestItemsCount(player, BADGE_GENERAL) >= 700) && (getQuestItemsCount(player, BADGE_GENERAL) <= 799)))
				{
					addExpAndSp(player, 153316800, 1533168);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 8);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && ((getQuestItemsCount(player, BADGE_GENERAL) >= 800) && (getQuestItemsCount(player, BADGE_GENERAL) <= 899)))
				{
					addExpAndSp(player, 172481400, 1724814);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 9);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
				else if ((getQuestItemsCount(player, BADGE_SOLDIER) >= 50) && (getQuestItemsCount(player, BADGE_GENERAL) >= 900))
				{
					addExpAndSp(player, 191646000, 1916460);
					giveItems(player, STEEL_DOOR_GUILD_BOX, 10);
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
		if ((npc.getId() == LUGONESS) && !player.isMageClass())
		{
			return "33852-00.htm";
		}
		
		if (npc.getId() == LUGONESS)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "33852-06.html";
						break;
					}
					qs.setState(State.CREATED);
					break;
				}
				case State.CREATED:
				{
					htmltext = "33852-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33852-05.html";
					}
					else if (qs.isStarted() && qs.isCond(2))
					{
						htmltext = "33852-08.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted() && !qs.isNowAvailable())
		{
			htmltext = "33852-06.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isCond(1) && (giveItemRandomly(killer, npc, BADGE_SOLDIER, 1, 50, 0.15, true)))
		{
			qs.setCond(2, true);
		}
		if ((qs != null) && qs.isCond(2) && (giveItemRandomly(killer, npc, BADGE_GENERAL, 1, 900, 0.35, true)))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}