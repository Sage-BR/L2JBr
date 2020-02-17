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
package quests.Q00420_LittleWing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

/**
 * Little Wing (420)
 * @author Pandragon / Stayway (Rework Helios)
 */
public class Q00420_LittleWing extends Quest
{
	// NPCs
	private static final int MARIA = 30608;
	private static final int CRONOS = 30610;
	private static final int BYRON = 30711;
	private static final int MIMYU = 30747;
	private static final int EXARION = 30748;
	private static final int ZWOV = 30749;
	private static final int KALIBRAN = 30750;
	private static final int SUZET = 30751;
	private static final int SHAMHAI = 30752;
	private static final int COOPER = 30829;
	// Items
	private static final int ACCESSORY_GEM = 36556;
	private static final int ARMOR_FRAGMENT = 36551;
	private static final int COKES = 36561;
	private static final int GEMSTONE_D = 2130;
	private static final int GEMSTONE_C = 2131;
	private static final int FAIRY_DUST = 3499;
	private static final int FAIRY_STONE = 3816;
	private static final int DELUXE_FAIRY_STONE = 3817;
	private static final int FAIRY_STONE_LIST = 3818;
	private static final int DELUXE_STONE_LIST = 3819;
	private static final int TOAD_SKIN = 3820;
	private static final int MONKSHOOD_JUICE = 3821;
	private static final int EXARION_SCALE = 3822;
	private static final int EXARION_EGG = 3823;
	private static final int ZWOV_SCALE = 3824;
	private static final int ZWOV_EGG = 3825;
	private static final int KALIBRAN_SCALE = 3826;
	private static final int KALIBRAN_EGG = 3827;
	private static final int SUZET_SCALE = 3828;
	private static final int SUZET_EGG = 3829;
	private static final int SHAMHAI_SCALE = 3830;
	private static final int SHAMHAI_EGG = 3831;
	// Monsters
	private static final int LESSER_BASILISK = 20070;
	private static final int BASILISK = 20072;
	private static final int TOAD_LORD = 20231;
	private static final int MARSH_SPIDER = 20233;
	private static final int BREKA_PREFECT = 20270;
	private static final int ROAD_SCAVENGER = 20551;
	private static final int LETO_WARRIOR = 20580;
	private static final int[] DELUXE_STONE_BREAKERS =
	{
		23566, // Nymph Rose
		23567, // Nymph Rose
		23568, // Nymph Lily
		23569, // Nymph Lily
		23570, // Nymph Tulip
		23571, // Nymph Tulip
		23572, // Nymph Cosmos
		23573, // Nymph Cosmos
		23578, // Nymph Guardian
		23581, // Apherus
		23582, // Nymph Rose
	};
	// Rewards
	private static final int DRAGONFLUTE_OF_WIND = 3500;
	private static final int DRAGONFLUTE_OF_STAR = 3501;
	private static final int DRAGONFLUTE_OF_TWILIGHT = 3502;
	private static final int HATCHLING_ARMOR = 3912;
	private static final int HATCHLING_FOOD = 4038;
	private static final List<Integer> EGGS = Arrays.asList(EXARION_EGG, SUZET_EGG, KALIBRAN_EGG, SHAMHAI_EGG, ZWOV_EGG);
	// Drake Drops
	private static final Map<Integer, Integer> EGG_DROPS = new HashMap<>();
	static
	{
		EGG_DROPS.put(LESSER_BASILISK, SHAMHAI_EGG);
		EGG_DROPS.put(BASILISK, SHAMHAI_EGG);
		EGG_DROPS.put(MARSH_SPIDER, ZWOV_EGG);
		EGG_DROPS.put(BREKA_PREFECT, SUZET_EGG);
		EGG_DROPS.put(ROAD_SCAVENGER, KALIBRAN_EGG);
		EGG_DROPS.put(LETO_WARRIOR, EXARION_EGG);
	}
	// Misc
	private static final int MIN_LVL = 35;
	
	public Q00420_LittleWing()
	{
		super(420);
		addStartNpc(COOPER);
		addTalkId(MARIA, CRONOS, BYRON, MIMYU, EXARION, ZWOV, KALIBRAN, SUZET, SHAMHAI, COOPER);
		addAttackId(DELUXE_STONE_BREAKERS);
		addKillId(TOAD_LORD, BASILISK, LESSER_BASILISK, MARSH_SPIDER, BREKA_PREFECT, ROAD_SCAVENGER, LETO_WARRIOR);
		registerQuestItems(FAIRY_DUST, FAIRY_STONE, DELUXE_FAIRY_STONE, FAIRY_STONE_LIST, DELUXE_STONE_LIST, TOAD_SKIN, MONKSHOOD_JUICE, EXARION_SCALE, EXARION_EGG, ZWOV_SCALE, ZWOV_EGG, KALIBRAN_SCALE, KALIBRAN_EGG, SUZET_SCALE, SUZET_EGG, SHAMHAI_SCALE, SHAMHAI_EGG);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30610-02.html":
			case "30610-03.html":
			case "30610-04.html":
			case "30711-02.html":
			case "30747-05.html":
			case "30747-06.html":
			case "30751-02.html":
			{
				htmltext = event;
				break;
			}
			case "30829-02.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30610-05.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					qs.set("old_stone", 0);
					qs.set("fairy_stone", 1);
					giveItems(player, FAIRY_STONE_LIST, 1);
					htmltext = event;
				}
				break;
			}
			case "30610-06.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					qs.set("old_stone", 0);
					qs.set("fairy_stone", 2);
					giveItems(player, DELUXE_STONE_LIST, 1);
					htmltext = event;
				}
				break;
			}
			case "30610-12.html":
			{
				if (qs.isCond(5))
				{
					qs.setCond(2, true);
					qs.set("old_stone", qs.getInt("fairy_stone"));
					qs.set("fairy_stone", 1);
					giveItems(player, FAIRY_STONE_LIST, 1);
					htmltext = event;
				}
				break;
			}
			case "30610-13.html":
			{
				if (qs.isCond(5))
				{
					qs.setCond(2, true);
					qs.set("old_stone", qs.getInt("fairy_stone"));
					qs.set("fairy_stone", 2);
					giveItems(player, DELUXE_STONE_LIST, 1);
					htmltext = event;
				}
				break;
			}
			case "30608-03.html":
			{
				if (qs.isCond(2))
				{
					if ((qs.getInt("fairy_stone") == 1) && (getQuestItemsCount(player, ACCESSORY_GEM) >= 4) && (getQuestItemsCount(player, ARMOR_FRAGMENT) >= 2) && (getQuestItemsCount(player, GEMSTONE_D) >= 1) && (getQuestItemsCount(player, COKES) >= 1) && (getQuestItemsCount(player, TOAD_SKIN) >= 10))
					{
						takeItems(player, FAIRY_STONE_LIST, -1);
						takeItems(player, ACCESSORY_GEM, 4);
						takeItems(player, ARMOR_FRAGMENT, 2);
						takeItems(player, GEMSTONE_D, 1);
						takeItems(player, COKES, 1);
						takeItems(player, TOAD_SKIN, -1);
						giveItems(player, FAIRY_STONE, 1);
					}
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30608-05.html":
			{
				if (qs.isCond(2))
				{
					if ((qs.getInt("fairy_stone") == 2) && (getQuestItemsCount(player, ACCESSORY_GEM) >= 5) && (getQuestItemsCount(player, ARMOR_FRAGMENT) >= 2) && (getQuestItemsCount(player, GEMSTONE_C) >= 1) && (getQuestItemsCount(player, COKES) >= 1) && (getQuestItemsCount(player, TOAD_SKIN) >= 20))
					{
						takeItems(player, DELUXE_STONE_LIST, -1);
						takeItems(player, ACCESSORY_GEM, 5);
						takeItems(player, ARMOR_FRAGMENT, 2);
						takeItems(player, GEMSTONE_C, 1);
						takeItems(player, COKES, 1);
						takeItems(player, TOAD_SKIN, -1);
						giveItems(player, DELUXE_FAIRY_STONE, 1);
					}
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30711-03.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4, true);
					if (qs.getInt("fairy_stone") == 2)
					{
						htmltext = "30711-04.html";
					}
					else
					{
						htmltext = event;
					}
				}
				break;
			}
			case "30747-02.html":
			case "30747-04.html":
			{
				if (qs.isCond(4) && ((getQuestItemsCount(player, FAIRY_STONE) + getQuestItemsCount(player, DELUXE_FAIRY_STONE)) > 0))
				{
					takeItems(player, -1, FAIRY_STONE, DELUXE_FAIRY_STONE);
					if (qs.getInt("fairy_stone") == 2)
					{
						giveItems(player, FAIRY_DUST, 1);
					}
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "30747-07.html":
			case "30747-08.html":
			{
				if (qs.isCond(5) && (getQuestItemsCount(player, MONKSHOOD_JUICE) == 0))
				{
					giveItems(player, MONKSHOOD_JUICE, 1);
					htmltext = event;
				}
				break;
			}
			case "30747-12.html":
			{
				if (qs.isCond(7))
				{
					if ((qs.getInt("fairy_stone") == 1) || (getQuestItemsCount(player, FAIRY_DUST) == 0))
					{
						giveReward(player);
						qs.exitQuest(true, true);
						htmltext = "30747-16.html";
					}
					else
					{
						qs.setCond(8, false);
						htmltext = event;
					}
				}
				else if (qs.isCond(8))
				{
					htmltext = event;
				}
				break;
			}
			case "30747-13.html":
			{
				if (qs.isCond(8))
				{
					giveReward(player);
					qs.exitQuest(true, true);
					htmltext = event;
				}
				break;
			}
			case "30747-15.html":
			{
				if (qs.isCond(8) && (getQuestItemsCount(player, FAIRY_DUST) > 1))
				{
					if (getRandom(100) < 5)
					{
						giveItems(player, HATCHLING_ARMOR, 1);
						htmltext = "30747-14.html";
					}
					else
					{
						giveItems(player, HATCHLING_FOOD, 20);
						htmltext = event;
					}
					giveReward(player);
					takeItems(player, FAIRY_DUST, -1);
					qs.exitQuest(true, true);
				}
				break;
			}
			case "30748-02.html":
			{
				if (qs.isCond(5))
				{
					takeItems(player, MONKSHOOD_JUICE, -1);
					giveItems(player, EXARION_SCALE, 1);
					qs.setCond(6, true);
					qs.set("drake_hunt", LETO_WARRIOR);
					htmltext = event;
				}
				break;
			}
			case "30749-02.html":
			{
				if (qs.isCond(5))
				{
					takeItems(player, MONKSHOOD_JUICE, -1);
					giveItems(player, ZWOV_SCALE, 1);
					qs.setCond(6, true);
					qs.set("drake_hunt", MARSH_SPIDER);
					htmltext = event;
				}
				break;
			}
			case "30750-02.html":
			{
				if (qs.isCond(5))
				{
					takeItems(player, MONKSHOOD_JUICE, -1);
					giveItems(player, KALIBRAN_SCALE, 1);
					qs.setCond(6, true);
					qs.set("drake_hunt", ROAD_SCAVENGER);
					htmltext = event;
				}
				break;
			}
			case "30750-05.html":
			{
				if (qs.isCond(6) && (getQuestItemsCount(player, KALIBRAN_EGG) >= 20))
				{
					takeItems(player, -1, KALIBRAN_SCALE, KALIBRAN_EGG);
					giveItems(player, KALIBRAN_EGG, 1);
					qs.setCond(7, true);
					htmltext = event;
				}
				break;
			}
			case "30751-03.html":
			{
				if (qs.isCond(5))
				{
					takeItems(player, MONKSHOOD_JUICE, -1);
					giveItems(player, SUZET_SCALE, 1);
					qs.setCond(6, true);
					qs.set("drake_hunt", BREKA_PREFECT);
					htmltext = event;
				}
				break;
			}
			case "30752-02.html":
			{
				if (qs.isCond(5))
				{
					takeItems(player, MONKSHOOD_JUICE, -1);
					giveItems(player, SHAMHAI_SCALE, 1);
					qs.setCond(6, true);
					qs.set("drake_hunt", LESSER_BASILISK);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && (getQuestItemsCount(attacker, DELUXE_FAIRY_STONE) > 0) && (getRandom(100) < 30))
		{
			takeItems(attacker, DELUXE_FAIRY_STONE, -1);
			playSound(attacker, QuestSound.ITEMSOUND_QUEST_MIDDLE);
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_STONE_THE_ELVEN_STONE_BROKE);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == COOPER)
				{
					htmltext = (talker.getLevel() >= MIN_LVL) ? "30829-01.htm" : "30829-03.html";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case COOPER:
					{
						htmltext = "30829-04.html";
						break;
					}
					case CRONOS:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "30610-01.html";
								break;
							}
							case 2:
							{
								htmltext = "30610-07.html";
								break;
							}
							case 3:
							{
								if (qs.getInt("old_stone") > 0)
								{
									htmltext = "30610-14.html";
								}
								else
								{
									htmltext = "30610-08.html";
								}
								break;
							}
							case 4:
							{
								htmltext = "30610-09.html";
								break;
							}
							case 5:
							{
								if ((getQuestItemsCount(talker, FAIRY_STONE) == 0) && (getQuestItemsCount(talker, DELUXE_FAIRY_STONE) == 0))
								{
									htmltext = "30610-10.html";
								}
								else
								{
									htmltext = "30610-11.html";
								}
								break;
							}
						}
						break;
					}
					case MARIA:
					{
						switch (qs.getCond())
						{
							case 2:
							{
								if ((qs.getInt("fairy_stone") == 1) && (getQuestItemsCount(talker, ACCESSORY_GEM) >= 4) && (getQuestItemsCount(talker, ARMOR_FRAGMENT) >= 2) && (getQuestItemsCount(talker, GEMSTONE_D) >= 1) && (getQuestItemsCount(talker, COKES) >= 1) && (getQuestItemsCount(talker, TOAD_SKIN) >= 10))
								{
									htmltext = "30608-02.html";
								}
								else if ((qs.getInt("fairy_stone") == 2) && (getQuestItemsCount(talker, ACCESSORY_GEM) >= 5) && (getQuestItemsCount(talker, ARMOR_FRAGMENT) >= 2) && (getQuestItemsCount(talker, GEMSTONE_C) >= 1) && (getQuestItemsCount(talker, COKES) >= 1) && (getQuestItemsCount(talker, TOAD_SKIN) >= 20))
								{
									htmltext = "30608-04.html";
								}
								else
								{
									htmltext = "30608-01.html";
								}
								break;
							}
							case 3:
							{
								htmltext = "30608-06.html";
								break;
							}
						}
						break;
					}
					case BYRON:
					{
						switch (qs.getCond())
						{
							case 2:
							{
								htmltext = "30711-10.html";
								break;
							}
							case 3:
							{
								if (qs.getInt("old_stone") == 0)
								{
									htmltext = "30711-01.html";
								}
								else if (qs.getInt("old_stone") == 1)
								{
									qs.setCond(5, true);
									htmltext = "30711-05.html";
								}
								else
								{
									qs.setCond(4, true);
									htmltext = "30711-06.html";
								}
								break;
							}
							case 4:
							{
								if ((getQuestItemsCount(talker, FAIRY_STONE) == 0) && (getQuestItemsCount(talker, DELUXE_FAIRY_STONE) == 0))
								{
									htmltext = "30711-09.html";
								}
								else if (getQuestItemsCount(talker, FAIRY_STONE) == 0)
								{
									htmltext = "30711-08.html";
								}
								else
								{
									htmltext = "30711-07.html";
								}
								break;
							}
						}
						break;
					}
					case MIMYU:
					{
						switch (qs.getCond())
						{
							case 4:
							{
								if (getQuestItemsCount(talker, FAIRY_STONE) > 0)
								{
									htmltext = "30747-01.html";
								}
								else if (getQuestItemsCount(talker, DELUXE_FAIRY_STONE) > 0)
								{
									htmltext = "30747-03.html";
								}
								break;
							}
							case 5:
							{
								if (getQuestItemsCount(talker, MONKSHOOD_JUICE) > 0)
								{
									htmltext = "30747-09.html";
								}
								else if (qs.getInt("fairy_stone") == 1)
								{
									htmltext = "30747-05.html";
								}
								else
								{
									htmltext = "30747-06.html";
								}
								break;
							}
							case 6:
							{
								if ((getQuestItemsCount(talker, EXARION_EGG) >= 20) || (getQuestItemsCount(talker, ZWOV_EGG) >= 20) || (getQuestItemsCount(talker, KALIBRAN_EGG) >= 20) || (getQuestItemsCount(talker, SUZET_EGG) >= 20) || (getQuestItemsCount(talker, SHAMHAI_EGG) >= 20))
								{
									htmltext = "30747-10.html";
								}
								else
								{
									htmltext = "30747-09.html";
								}
								break;
							}
							case 7:
							{
								htmltext = "30747-11.html";
								break;
							}
							case 8:
							{
								htmltext = "30747-12.html";
								break;
							}
						}
						break;
					}
					case EXARION:
					{
						switch (qs.getCond())
						{
							case 5:
							{
								if (getQuestItemsCount(talker, MONKSHOOD_JUICE) > 0)
								{
									htmltext = "30748-01.html";
								}
								break;
							}
							case 6:
							{
								if (getQuestItemsCount(talker, EXARION_EGG) >= 20)
								{
									takeItems(talker, -1, EXARION_SCALE, EXARION_EGG);
									giveItems(talker, EXARION_EGG, 1);
									qs.setCond(7, true);
									htmltext = "30748-04.html";
								}
								else
								{
									htmltext = "30748-03.html";
								}
								break;
							}
							case 7:
							{
								htmltext = "30748-05.html";
								break;
							}
						}
						break;
					}
					case ZWOV:
					{
						switch (qs.getCond())
						{
							case 5:
							{
								if (getQuestItemsCount(talker, MONKSHOOD_JUICE) > 0)
								{
									htmltext = "30749-01.html";
								}
								break;
							}
							case 6:
							{
								if (getQuestItemsCount(talker, ZWOV_EGG) >= 20)
								{
									takeItems(talker, -1, ZWOV_SCALE, ZWOV_EGG);
									giveItems(talker, ZWOV_EGG, 1);
									qs.setCond(7, true);
									htmltext = "30749-04.html";
								}
								else
								{
									htmltext = "30749-03.html";
								}
								break;
							}
							case 7:
							{
								htmltext = "30749-05.html";
								break;
							}
						}
						break;
					}
					case KALIBRAN:
					{
						switch (qs.getCond())
						{
							case 5:
							{
								if (getQuestItemsCount(talker, MONKSHOOD_JUICE) > 0)
								{
									htmltext = "30750-01.html";
								}
								break;
							}
							case 6:
							{
								if (getQuestItemsCount(talker, KALIBRAN_EGG) >= 20)
								{
									htmltext = "30750-04.html";
								}
								else
								{
									htmltext = "30750-03.html";
								}
								break;
							}
							case 7:
							{
								htmltext = "30750-06.html";
								break;
							}
						}
						break;
					}
					case SUZET:
					{
						switch (qs.getCond())
						{
							case 5:
							{
								if (getQuestItemsCount(talker, MONKSHOOD_JUICE) > 0)
								{
									htmltext = "30751-01.html";
								}
								break;
							}
							case 6:
							{
								if (getQuestItemsCount(talker, SUZET_EGG) >= 20)
								{
									takeItems(talker, -1, SUZET_SCALE, SUZET_EGG);
									giveItems(talker, SUZET_EGG, 1);
									qs.setCond(7, true);
									htmltext = "30751-05.html";
								}
								else
								{
									htmltext = "30751-04.html";
								}
								break;
							}
							case 7:
							{
								htmltext = "30751-06.html";
								break;
							}
						}
						break;
					}
					case SHAMHAI:
					{
						switch (qs.getCond())
						{
							case 5:
							{
								if (getQuestItemsCount(talker, MONKSHOOD_JUICE) > 0)
								{
									htmltext = "30752-01.html";
								}
								break;
							}
							case 6:
							{
								if (getQuestItemsCount(talker, SHAMHAI_EGG) >= 20)
								{
									takeItems(talker, -1, SHAMHAI_SCALE, SHAMHAI_EGG);
									giveItems(talker, SHAMHAI_EGG, 1);
									qs.setCond(7, true);
									htmltext = "30752-04.html";
								}
								else
								{
									htmltext = "30752-03.html";
								}
								break;
							}
							case 7:
							{
								htmltext = "30752-05.html";
								break;
							}
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(talker);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if (qs != null)
		{
			if (qs.isCond(2) && (npc.getId() == TOAD_LORD))
			{
				if (qs.getInt("fairy_stone") == 1)
				{
					giveItemRandomly(qs.getPlayer(), npc, TOAD_SKIN, 1, 10, 0.3, true);
				}
				else
				{
					giveItemRandomly(qs.getPlayer(), npc, TOAD_SKIN, 1, 20, 0.3, true);
				}
			}
			else if (qs.isCond(6) && (npc.getId() == qs.getInt("drake_hunt")))
			{
				giveItemRandomly(qs.getPlayer(), npc, EGG_DROPS.get(npc.getId()), 1, 20, 0.5, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	/**
	 * Gives the reward to the player.
	 * @param player the player
	 */
	private void giveReward(PlayerInstance player)
	{
		final int random = getRandom(100);
		for (int i : EGGS)
		{
			if (hasQuestItems(player, i))
			{
				final int mul = EGGS.indexOf(i) * 5;
				if (hasQuestItems(player, FAIRY_DUST))
				{
					if (random < (45 + mul))
					{
						giveItems(player, DRAGONFLUTE_OF_WIND, 1);
					}
					else if (random < (75 + mul))
					{
						giveItems(player, DRAGONFLUTE_OF_STAR, 1);
					}
					else
					{
						giveItems(player, DRAGONFLUTE_OF_TWILIGHT, 1);
					}
				}
				else if (random < (50 + mul))
				{
					giveItems(player, DRAGONFLUTE_OF_WIND, 1);
				}
				else if (random < (85 + mul))
				{
					giveItems(player, DRAGONFLUTE_OF_STAR, 1);
				}
				else
				{
					giveItems(player, DRAGONFLUTE_OF_TWILIGHT, 1);
				}
				takeItems(player, i, -1);
				break;
			}
		}
	}
}
