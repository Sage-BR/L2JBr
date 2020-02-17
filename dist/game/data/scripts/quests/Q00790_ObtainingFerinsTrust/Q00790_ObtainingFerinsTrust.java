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
package quests.Q00790_ObtainingFerinsTrust;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Obtaining Ferin's Trust (790)
 * @URL https://l2wiki.com/Obtaining_Ferin%27s_Trust
 * @author Gigi
 */
public class Q00790_ObtainingFerinsTrust extends Quest
{
	// NPCs
	private static final int CYPHONA = 34055;
	// Monsters
	private static final int[] MONSTERS =
	{
		23541, // Kerberos Lager
		23550, // Kerberos Lager (night)
		23542, // Kerberos Fort
		23551, // Kerberos Fort (night)
		23543, // Kerberos Nero
		23552, // Kerberos Nero (night)
		23544, // Fury Sylph Barrena
		23553, // Fury Sylph Barrena (night)
		23546, // Fury Sylph Temptress
		23555, // Fury Sylph Temptress (night)
		23547, // Fury Sylph Purka
		23556, // Fury Sylph Purka (night)
		23545, // Fury Kerberos Leger
		23557, // Fury Kerberos Leger (night)
		23549, // Fury Kerberos Nero
		23558 // Fury Kerberos Nero (night)
	};
	// Misc
	private static final int MIN_LEVEL = 102;
	// Item's
	private static final int MUTATED_SPIRITS_SOUL = 45849;
	private static final int UNWORLDLY_VISITORS_BASIC_SUPPLY_BOX = 47181;
	private static final int UNWORLDLY_VISITORS_INTERMEDIATE_SUPPLY_BOX = 47182;
	private static final int UNWORLDLY_VISITORS_ADVANCED_SUPPLY_BOX = 47183;
	
	public Q00790_ObtainingFerinsTrust()
	{
		super(790);
		addStartNpc(CYPHONA);
		addTalkId(CYPHONA);
		addKillId(MONSTERS);
		registerQuestItems(MUTATED_SPIRITS_SOUL);
		addCondMinLevel(MIN_LEVEL, "34055-00.htm");
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
			case "34055-02.htm":
			case "34055-03.htm":
			case "34055-04.htm":
			case "34055-04a.htm":
			case "34055-04b.htm":
			case "34055-06.html":
			case "34055-06a.html":
			case "34055-06b.html":
			{
				htmltext = event;
				break;
			}
			case "select_mission":
			{
				qs.startQuest();
				if ((player.getFactionLevel(Faction.UNWORLDLY_VISITORS) >= 1) && (player.getFactionLevel(Faction.UNWORLDLY_VISITORS) < 2))
				{
					htmltext = "34055-04a.htm";
					break;
				}
				else if (player.getFactionLevel(Faction.UNWORLDLY_VISITORS) >= 2)
				{
					htmltext = "34055-04b.htm";
					break;
				}
				htmltext = "34055-04.htm";
				break;
			}
			case "return":
			{
				if ((player.getFactionLevel(Faction.UNWORLDLY_VISITORS) >= 1) && (player.getFactionLevel(Faction.UNWORLDLY_VISITORS) < 2))
				{
					htmltext = "34055-04a.htm";
					break;
				}
				else if (player.getFactionLevel(Faction.UNWORLDLY_VISITORS) >= 2)
				{
					htmltext = "34055-04b.htm";
					break;
				}
				htmltext = "34055-04.htm";
				break;
			}
			case "34055-07.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "34055-07a.html":
			{
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "34055-07b.html":
			{
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "34055-10.html":
			{
				final int chance = getRandom(100);
				switch (qs.getCond())
				{
					case 5:
					{
						if ((getQuestItemsCount(player, MUTATED_SPIRITS_SOUL) == 200) && (player.getLevel() >= MIN_LEVEL))
						{
							if (chance < 2)
							{
								giveItems(player, UNWORLDLY_VISITORS_ADVANCED_SUPPLY_BOX, 1);
							}
							else if (chance < 20)
							{
								giveItems(player, UNWORLDLY_VISITORS_INTERMEDIATE_SUPPLY_BOX, 1);
							}
							else if (chance < 100)
							{
								giveItems(player, UNWORLDLY_VISITORS_BASIC_SUPPLY_BOX, 1);
							}
							addExpAndSp(player, 22_221_427_950L, 22_221_360);
							addFactionPoints(player, Faction.UNWORLDLY_VISITORS, 100);
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
						if ((getQuestItemsCount(player, MUTATED_SPIRITS_SOUL) == 400) && (player.getLevel() >= MIN_LEVEL))
						{
							if (chance < 2)
							{
								giveItems(player, UNWORLDLY_VISITORS_ADVANCED_SUPPLY_BOX, 1);
							}
							else if (chance < 20)
							{
								giveItems(player, UNWORLDLY_VISITORS_BASIC_SUPPLY_BOX, 1);
							}
							else if (chance < 100)
							{
								giveItems(player, UNWORLDLY_VISITORS_INTERMEDIATE_SUPPLY_BOX, 1);
							}
							addExpAndSp(player, 44_442_855_900L, 44_442_720);
							addFactionPoints(player, Faction.UNWORLDLY_VISITORS, 200);
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
						if ((getQuestItemsCount(player, MUTATED_SPIRITS_SOUL) == 600) && (player.getLevel() >= MIN_LEVEL))
						{
							if (chance < 2)
							{
								giveItems(player, UNWORLDLY_VISITORS_BASIC_SUPPLY_BOX, 1);
							}
							else if (chance < 20)
							{
								giveItems(player, UNWORLDLY_VISITORS_INTERMEDIATE_SUPPLY_BOX, 1);
							}
							else if (chance < 100)
							{
								giveItems(player, UNWORLDLY_VISITORS_ADVANCED_SUPPLY_BOX, 1);
							}
							addExpAndSp(player, 66_664_283_850L, 66_664_080);
							addFactionPoints(player, Faction.UNWORLDLY_VISITORS, 300);
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
				htmltext = "34055-01.htm";
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						if ((player.getFactionLevel(Faction.UNWORLDLY_VISITORS) >= 1) && (player.getFactionLevel(Faction.UNWORLDLY_VISITORS) < 2))
						{
							htmltext = "34055-04a.htm";
							break;
						}
						else if (player.getFactionLevel(Faction.UNWORLDLY_VISITORS) >= 2)
						{
							htmltext = "34055-04b.htm";
							break;
						}
						htmltext = "34055-04.htm";
						break;
					}
					case 2:
					{
						htmltext = "34055-08.html";
						break;
					}
					case 3:
					{
						htmltext = "34055-08a.html";
						break;
					}
					case 4:
					{
						htmltext = "34055-08b.html";
						break;
					}
					case 5:
					case 6:
					case 7:
					{
						htmltext = "34055-09.html";
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
					htmltext = "34055-01.htm";
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
					if (giveItemRandomly(player, npc, MUTATED_SPIRITS_SOUL, 1, 200, 1, true))
					{
						qs.setCond(5, true);
					}
					break;
				}
				case 3:
				{
					if (giveItemRandomly(player, npc, MUTATED_SPIRITS_SOUL, 1, 400, 1, true))
					{
						qs.setCond(6, true);
					}
					break;
				}
				case 4:
				{
					if (giveItemRandomly(player, npc, MUTATED_SPIRITS_SOUL, 1, 600, 1, true))
					{
						qs.setCond(7, true);
					}
					break;
				}
			}
		}
	}
}