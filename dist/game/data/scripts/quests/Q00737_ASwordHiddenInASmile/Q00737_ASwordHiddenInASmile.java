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
package quests.Q00737_ASwordHiddenInASmile;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * A Sword Hidden in a Smile (737)
 * @URL https://l2wiki.com/A_Sword_Hidden_in_a_Smile
 * @author Dmitri
 */
public class Q00737_ASwordHiddenInASmile extends Quest
{
	// NPCs
	private static final int HISTY = 34243;
	// Monsters
	private static final int[] MONSTERS =
	{
		23816, // Batus Ohm
		23817, // Kshana Oma
	};
	// Misc
	private static final int MIN_LEVEL = 102;
	// Items
	private static final int FIGHTER_STONE_SHARD = 48166;
	private static final int PROOF_OF_LIBERTY = 48165;
	private static final int BASIC_SUPPLY_BOX = 48254;
	private static final int INTERMEDIATE_SUPPLY_BOX = 48255;
	private static final int ADVANCED_SUPPLY_BOX = 48256;
	
	public Q00737_ASwordHiddenInASmile()
	{
		super(737);
		addStartNpc(HISTY);
		addTalkId(HISTY);
		addKillId(MONSTERS);
		registerQuestItems(PROOF_OF_LIBERTY);
		addCondMinLevel(MIN_LEVEL, "34243-00.htm");
		addFactionLevel(Faction.GIANT_TRACKERS, 4, "34243-00.htm");
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
			case "34243-02.htm":
			case "34243-03.htm":
			case "34243-04.htm":
			case "34243-04a.htm":
			case "34243-04b.htm":
			case "34243-06.html":
			case "34243-06a.html":
			case "34243-06b.html":
			{
				htmltext = event;
				break;
			}
			case "select_mission":
			{
				qs.startQuest();
				if ((player.getFactionLevel(Faction.GIANT_TRACKERS) >= 8) && (player.getFactionLevel(Faction.GIANT_TRACKERS) < 9))
				{
					htmltext = "34243-04a.htm";
					break;
				}
				else if (player.getFactionLevel(Faction.GIANT_TRACKERS) >= 9)
				{
					htmltext = "34243-04b.htm";
					break;
				}
				htmltext = "34243-04.htm";
				break;
			}
			case "return":
			{
				if ((player.getFactionLevel(Faction.GIANT_TRACKERS) >= 8) && (player.getFactionLevel(Faction.GIANT_TRACKERS) < 9))
				{
					htmltext = "34243-04a.htm";
					break;
				}
				else if (player.getFactionLevel(Faction.GIANT_TRACKERS) >= 9)
				{
					htmltext = "34243-04b.htm";
					break;
				}
				htmltext = "34243-04.htm";
				break;
			}
			case "34243-07.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "34243-07a.html":
			{
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "34243-07b.html":
			{
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "34243-10.html":
			{
				final int chance = getRandom(100);
				switch (qs.getCond())
				{
					case 5:
					{
						if ((getQuestItemsCount(player, PROOF_OF_LIBERTY) == 10) && (player.getLevel() >= MIN_LEVEL))
						{
							if (chance < 2)
							{
								giveItems(player, ADVANCED_SUPPLY_BOX, 1);
							}
							else if (chance < 20)
							{
								giveItems(player, INTERMEDIATE_SUPPLY_BOX, 1);
							}
							else if (chance < 100)
							{
								giveItems(player, BASIC_SUPPLY_BOX, 1);
							}
							addExpAndSp(player, 22_221_427_950L, 22_221_360);
							giveItems(player, FIGHTER_STONE_SHARD, 1);
							addFactionPoints(player, Faction.GIANT_TRACKERS, 100);
							qs.exitQuest(QuestType.DAILY, true);
							htmltext = event;
						}
						else
						{
							htmltext = getNoQuestLevelRewardMsg(player);
						}
						break;
					}
					case 6:
					{
						if ((getQuestItemsCount(player, PROOF_OF_LIBERTY) == 20) && (player.getLevel() >= MIN_LEVEL))
						{
							if (chance < 2)
							{
								giveItems(player, ADVANCED_SUPPLY_BOX, 1);
							}
							else if (chance < 20)
							{
								giveItems(player, BASIC_SUPPLY_BOX, 1);
							}
							else if (chance < 100)
							{
								giveItems(player, INTERMEDIATE_SUPPLY_BOX, 1);
							}
							addExpAndSp(player, 44_442_855_900L, 44_442_720);
							giveItems(player, FIGHTER_STONE_SHARD, 3);
							addFactionPoints(player, Faction.GIANT_TRACKERS, 200);
							qs.exitQuest(QuestType.DAILY, true);
							htmltext = event;
						}
						else
						{
							htmltext = getNoQuestLevelRewardMsg(player);
						}
						break;
					}
					case 7:
					{
						if ((getQuestItemsCount(player, PROOF_OF_LIBERTY) == 30) && (player.getLevel() >= MIN_LEVEL))
						{
							if (chance < 2)
							{
								giveItems(player, BASIC_SUPPLY_BOX, 1);
							}
							else if (chance < 20)
							{
								giveItems(player, INTERMEDIATE_SUPPLY_BOX, 1);
							}
							else if (chance < 100)
							{
								giveItems(player, ADVANCED_SUPPLY_BOX, 1);
							}
							addExpAndSp(player, 66_664_283_850L, 66_664_080);
							giveItems(player, FIGHTER_STONE_SHARD, 5);
							addFactionPoints(player, Faction.GIANT_TRACKERS, 300);
							qs.exitQuest(QuestType.DAILY, true);
							htmltext = event;
						}
						else
						{
							htmltext = getNoQuestLevelRewardMsg(player);
						}
						break;
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
				htmltext = "34243-01.htm";
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						if ((player.getFactionLevel(Faction.GIANT_TRACKERS) >= 8) && (player.getFactionLevel(Faction.GIANT_TRACKERS) < 9))
						{
							htmltext = "34243-04a.htm";
							break;
						}
						else if (player.getFactionLevel(Faction.GIANT_TRACKERS) >= 9)
						{
							htmltext = "34243-04b.htm";
							break;
						}
						htmltext = "34243-04.htm";
						break;
					}
					case 2:
					{
						htmltext = "34243-08.html";
						break;
					}
					case 3:
					{
						htmltext = "34243-08a.html";
						break;
					}
					case 4:
					{
						htmltext = "34243-08b.html";
						break;
					}
					case 5:
					case 6:
					case 7:
					{
						htmltext = "34243-09.html";
						break;
					}
				}
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
					htmltext = "34243-01.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && (qs.getCond() > 1) && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
		{
			switch (qs.getCond())
			{
				case 2:
				{
					if (giveItemRandomly(player, npc, PROOF_OF_LIBERTY, 1, 10, 1, true))
					{
						qs.setCond(5, true);
					}
					break;
				}
				case 3:
				{
					if (giveItemRandomly(player, npc, PROOF_OF_LIBERTY, 1, 20, 1, true))
					{
						qs.setCond(6, true);
					}
					break;
				}
				case 4:
				{
					if (giveItemRandomly(player, npc, PROOF_OF_LIBERTY, 1, 30, 1, true))
					{
						qs.setCond(7, true);
					}
					break;
				}
			}
		}
	}
}