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
package quests.Q10369_NoblesseSoulTesting;

import org.l2jbr.Config;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.EvasHiddenSpace.EvasHiddenSpace;

/**
 * Noblesse, Soul Testing (10369)
 * @author Gladicek, St3eT
 */
public class Q10369_NoblesseSoulTesting extends Quest
{
	// NPCs
	private static final int CERENAS = 31281;
	private static final int ONE_WHO_EATS_PROPHECIES = 27482;
	private static final int EVAS_AVATAR = 33686;
	private static final int LANYA = 33696;
	private static final int HELPING_TREE_SUMMON_DEVICE = 19293;
	private static final int HELPING_TREE = 27486;
	private static final int INVISIBLE_NPC_NOBLE = 27484;
	private static final int INVISIBLE_NPC_NOBLE_2 = 27487;
	private static final int FLAME_FLOWER = 33735;
	// Items
	private static final int SUMMONING_STONE = 34912;
	private static final int NOVELLA_PROPHECY = 34886;
	private static final int EMPTY_HOT_SPRINGS_WATER_BOTTLE = 34887;
	private static final int HOT_SPRINGS_WATER_BOTTLE = 34888;
	private static final int DURABLE_LEATHER = 34889;
	private static final int TROWEL = 34890;
	private static final int FIRE_ENERGY = 34891;
	private static final int HARD_FOSSIL_CONTAINING_WATER_ENERGY = 34892;
	private static final int SACK_CONTAINING_INGREDIENTS = 34913;
	private static final int HELPING_SEED = 34961;
	private static final int ASHES_OF_REMNANTS = 34962;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	private static final int STEEL_COIN = 37045;
	private static final int NOBLESSE_TIARA = 7694;
	private static final int SOE_HOT_SPRINGS = 34978;
	private static final int SOE_ADEN_CASTLE = 34981;
	private static final int SOE_RUNE_CASTLE = 34982;
	private static final int SOE_ISLE_OF_PRAYER = 34980;
	private static final int SOE_FORGE_OF_GODS = 34979;
	private static final int SOE_SECRET_ROOM = 39517;
	// Skills
	private static final SkillHolder EMPTY_WATER_SKILL = new SkillHolder(9443, 1);
	private static final SkillHolder TROWEL_SKILL = new SkillHolder(9442, 1);
	private static final SkillHolder HELPING_SEED_SKILL = new SkillHolder(9444, 1);
	// TODO: Casting this skill on HELPING_TREE_SUMMON_DEVICE?? Or its AOE??
	// private static final SkillHolder HELPING_TREE_CP_RECOVERY_RANGE = new SkillHolder(7103, 1);
	private static final SkillHolder NOBLESSE_PRESENTATION = new SkillHolder(18176, 1);
	// Locations
	private static final Location HELPING_TREE_LOC_1 = new Location(148223, 14869, -1368, 23);
	private static final Location HELPING_TREE_LOC_2 = new Location(22036, -49785, -1296, 23);
	// Monsters
	private static final int[] HOT_SPRINGS =
	{
		21320, // Hot Springs Yeti
		21322, // Hot Springs Bandersnatch
		21323, // Hot Springs Grendel
		21314, // Hot Springs Bandersnatch
	};
	private static final int[] ISLE_OF_PRAYER =
	{
		22261, // Seychelles
		22262, // Naiad
		22263, // Sonneratia
		22264, // Castalia
		22265, // Chrysocolla
		22266, // Pythia
	};
	// Misc
	private static final int MIN_LEVEL = 75;
	
	public Q10369_NoblesseSoulTesting()
	{
		super(10369);
		addStartNpc(CERENAS);
		addTalkId(CERENAS, EVAS_AVATAR, LANYA);
		addKillId(ONE_WHO_EATS_PROPHECIES);
		addKillId(HOT_SPRINGS);
		addKillId(ISLE_OF_PRAYER);
		addSeeCreatureId(INVISIBLE_NPC_NOBLE);
		addSpawnId(INVISIBLE_NPC_NOBLE_2, FLAME_FLOWER, LANYA, HELPING_TREE);
		addSkillSeeId(INVISIBLE_NPC_NOBLE, FLAME_FLOWER, HELPING_TREE_SUMMON_DEVICE);
		addFirstTalkId(FLAME_FLOWER, HELPING_TREE_SUMMON_DEVICE, HELPING_TREE);
		addCondMinLevel(MIN_LEVEL, "31281-13.htm");
		addCondIsSubClassActive("");
		registerQuestItems(SUMMONING_STONE, NOVELLA_PROPHECY, EMPTY_HOT_SPRINGS_WATER_BOTTLE, HOT_SPRINGS_WATER_BOTTLE, DURABLE_LEATHER, TROWEL, FIRE_ENERGY, HARD_FOSSIL_CONTAINING_WATER_ENERGY, HELPING_SEED, ASHES_OF_REMNANTS, SOE_HOT_SPRINGS, SOE_ADEN_CASTLE, SOE_RUNE_CASTLE, SOE_ISLE_OF_PRAYER, SOE_FORGE_OF_GODS, SOE_SECRET_ROOM);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (player == null)
		{
			return super.onAdvEvent(event, npc, player);
		}
		
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		
		switch (event)
		{
			case "31281-02.htm":
			{
				htmltext = event;
				break;
			}
			case "31281-04.html":
			case "33686-02.html":
			{
				if (canProgress(player))
				{
					htmltext = event;
				}
				break;
			}
			case "31281-05.html":
			{
				if (canProgress(player) && qs.isCond(1))
				{
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "31281-09.html":
			{
				if (canProgress(player) && qs.isCond(4) && hasQuestItems(player, NOVELLA_PROPHECY))
				{
					takeItems(player, NOVELLA_PROPHECY, -1);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "33686-03.html":
			{
				if (canProgress(player) && qs.isCond(5))
				{
					giveItems(player, EMPTY_HOT_SPRINGS_WATER_BOTTLE, 1);
					giveItems(player, SOE_HOT_SPRINGS, 1);
					giveItems(player, SUMMONING_STONE, 1);
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			case "33686-06.html":
			{
				if (canProgress(player) && qs.isCond(15))
				{
					takeItems(player, SACK_CONTAINING_INGREDIENTS, -1);
					takeItems(player, SUMMONING_STONE, -1);
					giveItems(player, HELPING_SEED, 1);
					giveItems(player, getRandomBoolean() ? SOE_RUNE_CASTLE : SOE_ADEN_CASTLE, 1);
					qs.setCond(16, true);
					htmltext = event;
				}
				break;
			}
			case "33696-03.html":
			{
				if (canProgress(player) && qs.isCond(7))
				{
					qs.setCond(8, true);
					takeItems(player, HOT_SPRINGS_WATER_BOTTLE, -1);
					takeItems(player, EMPTY_HOT_SPRINGS_WATER_BOTTLE, -1);
					htmltext = event;
				}
				break;
			}
			case "33696-06.html":
			{
				if (canProgress(player) && qs.isCond(9))
				{
					qs.setCond(10, true);
					takeItems(player, DURABLE_LEATHER, -1);
					giveItems(player, TROWEL, 1);
					giveItems(player, SOE_FORGE_OF_GODS, 1);
					htmltext = event;
				}
				break;
			}
			case "33696-09.html":
			{
				if (canProgress(player) && qs.isCond(11))
				{
					qs.setCond(12, true);
					takeItems(player, FIRE_ENERGY, -1);
					takeItems(player, TROWEL, 1);
					giveItems(player, SOE_ISLE_OF_PRAYER, 1);
					htmltext = event;
				}
				break;
			}
			case "startQuest":
			{
				if (canProgress(player))
				{
					qs.startQuest();
					playMovie(player, Movie.SC_NOBLE_OPENING);
				}
				break;
			}
			case "movieQuest":
			{
				if (canProgress(player) && qs.isCond(3))
				{
					qs.setCond(4, true);
					playMovie(player, Movie.SC_NOBLE_ENDING);
				}
				break;
			}
			case "bottle":
			{
				if (qs.isCond(5) && canProgress(player) && !hasQuestItems(player, EMPTY_HOT_SPRINGS_WATER_BOTTLE) && !hasQuestItems(player, HOT_SPRINGS_WATER_BOTTLE))
				{
					giveItems(player, EMPTY_HOT_SPRINGS_WATER_BOTTLE, 1);
				}
				else
				{
					htmltext = "33696-01.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == CERENAS)
				{
					htmltext = "31281-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (canProgress(player))
				{
					switch (npc.getId())
					{
						case CERENAS:
						{
							switch (qs.getCond())
							{
								case 1:
								{
									htmltext = "31281-03.html";
									break;
								}
								case 2:
								{
									htmltext = "31281-06.html";
									break;
								}
								case 3:
								{
									htmltext = "31281-07.html";
									break;
								}
								case 4:
								{
									htmltext = "31281-08.html";
									break;
								}
								case 5:
								{
									htmltext = "31281-10.html";
									break;
								}
								case 14:
								{
									final Quest instance = QuestManager.getInstance().getQuest(EvasHiddenSpace.class.getSimpleName());
									if (instance != null)
									{
										instance.onAdvEvent("enterInstance", npc, player);
									}
									htmltext = null;
									break;
								}
								case 17:
								{
									htmltext = "31281-11.html";
									break;
								}
							}
							break;
						}
						case EVAS_AVATAR:
						{
							switch (qs.getCond())
							{
								case 5:
								{
									htmltext = "33686-01.html";
									break;
								}
								case 6:
								{
									htmltext = "33686-04.html";
									break;
								}
								case 15:
								{
									htmltext = "33686-05.html";
									break;
								}
								case 16:
								{
									htmltext = "33686-06.html";
									break;
								}
								case 18:
								{
									player.doCast(NOBLESSE_PRESENTATION.getSkill());
									showOnScreenMsg(player, NpcStringId.CONGRATULATIONS_YOU_ARE_NOW_A_NOBLESSE, ExShowScreenMessage.TOP_CENTER, 5000);
									player.setNobleLevel(1);
									player.broadcastUserInfo();
									giveItems(player, DIMENSIONAL_DIAMOND, 10);
									giveItems(player, NOBLESSE_TIARA, 1);
									takeItems(player, ASHES_OF_REMNANTS, -1);
									giveItems(player, STEEL_COIN, 87);
									addExpAndSp(player, 12_625_440, 0);
									qs.exitQuest(false, true);
									htmltext = "33686-07.html";
									break;
								}
							}
							break;
						}
						case LANYA:
						{
							switch (qs.getCond())
							{
								case 7:
								{
									htmltext = "33696-02.html";
									break;
								}
								case 8:
								{
									htmltext = "33696-04.html";
									break;
								}
								case 9:
								{
									htmltext = "33696-05.html";
									break;
								}
								case 10:
								{
									htmltext = "33696-07.html";
									break;
								}
								case 11:
								{
									htmltext = "33696-08.html";
									break;
								}
								case 12:
								{
									htmltext = "33696-10.html";
									break;
								}
								case 13:
								{
									qs.setCond(14, true);
									takeItems(player, HARD_FOSSIL_CONTAINING_WATER_ENERGY, -1);
									giveItems(player, SACK_CONTAINING_INGREDIENTS, 1);
									giveItems(player, SOE_SECRET_ROOM, 1);
									htmltext = "33696-11.html";
									break;
								}
							}
							break;
						}
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == CERENAS)
				{
					htmltext = "31281-12.html";
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final PlayerInstance partyMember = getRandomPartyMember(killer);
		final QuestState qs = getQuestState(partyMember, false);
		
		if (canProgress(partyMember) && (qs != null) && qs.isStarted() && (partyMember.calculateDistance2D(npc) <= Config.ALT_PARTY_RANGE))
		{
			switch (qs.getCond())
			{
				case 2:
				{
					if (npc.getId() == ONE_WHO_EATS_PROPHECIES)
					{
						giveItems(partyMember, NOVELLA_PROPHECY, 1);
						qs.setCond(3, true);
					}
					break;
				}
				case 8:
				{
					if (CommonUtil.contains(HOT_SPRINGS, npc.getId()))
					{
						giveItems(partyMember, DURABLE_LEATHER, 1);
						
						if (getQuestItemsCount(killer, DURABLE_LEATHER) >= 10)
						{
							{
								qs.setCond(9, true);
								showOnScreenMsg(partyMember, NpcStringId.CLICK_ON_THE_SUMMONING_STONE_TO_TALK_TO_LANYA_THE_WATER_SPIRIT, ExShowScreenMessage.TOP_CENTER, 5000);
							}
						}
						else
						{
							playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case 12:
				{
					if (CommonUtil.contains(ISLE_OF_PRAYER, npc.getId()))
					{
						giveItems(partyMember, HARD_FOSSIL_CONTAINING_WATER_ENERGY, 1);
						
						if (getQuestItemsCount(killer, HARD_FOSSIL_CONTAINING_WATER_ENERGY) >= 10)
						{
							qs.setCond(13, true);
							showOnScreenMsg(partyMember, NpcStringId.CLICK_ON_THE_SUMMONING_STONE_TO_TALK_TO_LANYA_THE_WATER_SPIRIT, ExShowScreenMessage.TOP_CENTER, 5000);
						}
						else
						{
							playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			final QuestState qs = getQuestState(player, false);
			
			if ((qs != null) && qs.isStarted())
			{
				if (qs.isCond(6))
				{
					if (!hasQuestItems(player, HOT_SPRINGS_WATER_BOTTLE))
					{
						showOnScreenMsg(player, NpcStringId.OPEN_THE_ITEM_SCREEN_AND_DOUBLE_CLICK_THE_EMPTY_WATER_BOTTLE, ExShowScreenMessage.TOP_CENTER, 5000);
						getTimers().addTimer("LOL", null, 5000, npc, player, event -> showOnScreenMsg(event.getPlayer(), NpcStringId.IF_YOU_DOUBLE_CLICK_THE_EMPTY_BOTTLE_IT_WILL_BECOME_FULL_OF_WATER, ExShowScreenMessage.TOP_CENTER, 5000));
					}
				}
				else if (qs.isCond(16))
				{
					if (hasQuestItems(player, HELPING_SEED))
					{
						showOnScreenMsg(player, NpcStringId.IF_YOU_DOUBLE_CLICK_THE_HELPING_SEED_ITEM_A_TREE_OF_HELPING_WILL_BE_PLANTED, ExShowScreenMessage.TOP_CENTER, 5000);
					}
				}
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case INVISIBLE_NPC_NOBLE_2:
			{
				final Creature summoner = npc.getSummoner();
				if (summoner != null)
				{
					final QuestState qs = getQuestState(summoner.getActingPlayer(), false);
					if ((qs != null) && qs.isStarted() && qs.isCond(6) && hasQuestItems(summoner.getActingPlayer(), HOT_SPRINGS_WATER_BOTTLE))
					{
						qs.setCond(7, true);
					}
				}
				break;
			}
			case LANYA:
			{
				final Creature summoner = npc.getSummoner();
				if (summoner != null)
				{
					final QuestState qs = getQuestState(summoner.getActingPlayer(), false);
					if ((qs != null) && qs.isStarted())
					{
						npc.setTitle(summoner.getActingPlayer().getName());
						npc.broadcastInfo();
					}
				}
				break;
			}
			case FLAME_FLOWER:
			case HELPING_TREE:
			case HELPING_TREE_SUMMON_DEVICE:
			{
				npc.disableCoreAI(true);
				npc.setUndying(true);
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance player, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		if (player != null)
		{
			switch (npc.getId())
			{
				case INVISIBLE_NPC_NOBLE:
				{
					if (skill == EMPTY_WATER_SKILL.getSkill())
					{
						final QuestState qs = getQuestState(player, false);
						
						if ((qs != null) && qs.isStarted() && qs.isCond(6))
						{
							qs.setCond(7, true);
							showOnScreenMsg(player, NpcStringId.CLICK_ON_THE_SUMMONING_STONE_TO_TALK_TO_LANYA_THE_WATER_SPIRIT, ExShowScreenMessage.TOP_CENTER, 5000);
						}
					}
					break;
				}
				case FLAME_FLOWER:
				{
					if ((skill == TROWEL_SKILL.getSkill()) && (CommonUtil.contains(targets, npc)))
					{
						final QuestState qs = getQuestState(player, false);
						
						if ((qs != null) && qs.isStarted() && qs.isCond(10))
						{
							giveItems(player, FIRE_ENERGY, 1);
							
							if (getQuestItemsCount(player, FIRE_ENERGY) >= 5)
							{
								qs.setCond(11, true);
								showOnScreenMsg(player, NpcStringId.CLICK_ON_THE_SUMMONING_STONE_TO_TALK_TO_LANYA_THE_WATER_SPIRIT, ExShowScreenMessage.TOP_CENTER, 5000);
							}
							else
							{
								playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
							npc.doDie(player);
						}
					}
					break;
				}
				case HELPING_TREE_SUMMON_DEVICE:
				{
					if ((skill == HELPING_SEED_SKILL.getSkill()) && (CommonUtil.contains(targets, npc)))
					{
						final QuestState qs = getQuestState(player, false);
						
						if ((qs != null) && qs.isStarted() && qs.isCond(16))
						{
							qs.setCond(17, true);
							giveItems(player, ASHES_OF_REMNANTS, 1);
							
							if (npc.isInsideRadius2D(HELPING_TREE_LOC_1, 2000))
							{
								addSpawn(HELPING_TREE, HELPING_TREE_LOC_1, false, 300000);
							}
							else if (npc.isInsideRadius2D(HELPING_TREE_LOC_2, 2000))
							{
								addSpawn(HELPING_TREE, HELPING_TREE_LOC_2, false, 300000);
							}
						}
					}
					break;
				}
			}
		}
		return super.onSkillSee(npc, player, skill, targets, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		
		if ((qs != null) && qs.isStarted() && qs.isCond(10))
		{
			showOnScreenMsg(player, NpcStringId.CLICK_THE_FLAME_FLOWER_THEN_DOUBLE_CLICK_THE_TROWEL, ExShowScreenMessage.TOP_CENTER, 5000);
		}
		return super.onFirstTalk(npc, player);
	}
	
	private boolean canProgress(PlayerInstance player)
	{
		return ((player.getLevel() > MIN_LEVEL) && player.isSubClassActive());
	}
}