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
package quests.Q00823_DisappearedRaceNewFairy;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Disappeared Race, New Fairy (823)
 * @URL https://l2wiki.com/Disappeared_Race,_New_Fairy
 * @author Dmitri
 */
public class Q00823_DisappearedRaceNewFairy extends Quest
{
	// NPCs
	private static final int MIMYU = 30747;
	// Monsters
	private static final int[] MONSTERS =
	{
		23566, // Nymph Rose
		23567, // Nymph Rose
		23568, // Nymph Lily
		23569, // Nymph Lily
		23570, // Nymph Tulip
		23571, // Nymph Tulip
		23572, // Nymph Cosmos
		23573, // Nymph Cosmos
		23578 // Nymph Guardian
	};
	// Items
	private static final int NYMPH_STAMEN = 46258;
	private static final int BASIC_SUPPLY_BOX = 47178;
	private static final int INTERMEDIATE_SUPPLY_BOX = 47179;
	private static final int ADVANCED_SUPPLY_BOX = 47180;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q00823_DisappearedRaceNewFairy()
	{
		super(823);
		addStartNpc(MIMYU);
		addTalkId(MIMYU);
		addKillId(MONSTERS);
		registerQuestItems(NYMPH_STAMEN);
		addCondMinLevel(MIN_LEVEL, "30747-00.htm");
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
			case "30747-02.htm":
			case "30747-03.htm":
			case "30747-04.htm":
			case "30747-04a.htm":
			case "30747-04b.htm":
			case "30747-06.html":
			case "30747-06a.html":
			case "30747-06b.html":
			{
				htmltext = event;
				break;
			}
			case "select_mission":
			{
				qs.startQuest();
				if ((player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS) >= 1) && (player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS) < 2))
				{
					htmltext = "30747-04a.htm";
					break;
				}
				else if (player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS) >= 2)
				{
					htmltext = "30747-04b.htm";
					break;
				}
				htmltext = "30747-04.htm";
				break;
			}
			case "return":
			{
				if ((player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS) >= 1) && (player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS) < 2))
				{
					htmltext = "30747-04a.htm";
					break;
				}
				else if (player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS) >= 2)
				{
					htmltext = "30747-04b.htm";
					break;
				}
				htmltext = "30747-04.htm";
				break;
			}
			case "30747-07.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30747-07a.html":
			{
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30747-07b.html":
			{
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "30747-10.html":
			{
				final int chance = getRandom(100);
				switch (qs.getCond())
				{
					case 5:
					{
						if ((getQuestItemsCount(player, NYMPH_STAMEN) == 200) && (player.getLevel() >= MIN_LEVEL))
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
							addExpAndSp(player, 5_536_944_000L, 13_288_590);
							addFactionPoints(player, Faction.MOTHER_TREE_GUARDIANS, 100);
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
						if ((getQuestItemsCount(player, NYMPH_STAMEN) == 400) && (player.getLevel() >= MIN_LEVEL))
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
							addExpAndSp(player, 11_073_888_000L, 26_577_180);
							addFactionPoints(player, Faction.MOTHER_TREE_GUARDIANS, 200);
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
						if ((getQuestItemsCount(player, NYMPH_STAMEN) == 600) && (player.getLevel() >= MIN_LEVEL))
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
							addExpAndSp(player, 16_610_832_000L, 39_865_770);
							addFactionPoints(player, Faction.MOTHER_TREE_GUARDIANS, 300);
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
				htmltext = "30747-01.htm";
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						if ((player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS) >= 1) && (player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS) < 2))
						{
							htmltext = "30747-04a.htm";
							break;
						}
						else if (player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS) >= 2)
						{
							htmltext = "30747-04b.htm";
							break;
						}
						htmltext = "30747-04.htm";
						break;
					}
					case 2:
					{
						htmltext = "30747-08.html";
						break;
					}
					case 3:
					{
						htmltext = "30747-08a.html";
						break;
					}
					case 4:
					{
						htmltext = "30747-08b.html";
						break;
					}
					case 5:
					case 6:
					case 7:
					{
						htmltext = "30747-09.html";
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
					htmltext = "30747-01.htm";
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
					if (giveItemRandomly(player, npc, NYMPH_STAMEN, 1, 200, 1, true))
					{
						qs.setCond(5, true);
					}
					break;
				}
				case 3:
				{
					if (giveItemRandomly(player, npc, NYMPH_STAMEN, 1, 400, 1, true))
					{
						qs.setCond(6, true);
					}
					break;
				}
				case 4:
				{
					if (giveItemRandomly(player, npc, NYMPH_STAMEN, 1, 600, 1, true))
					{
						qs.setCond(7, true);
					}
					break;
				}
			}
		}
	}
}