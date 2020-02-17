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
package ai.areas.AteliaFortress.AteliaManager;

import java.util.ArrayList;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.datatables.SpawnTable;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Spawn;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.AbnormalType;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * URL https://l2wiki.com/Atelia_Fortress
 * @author hlwrave, Stayway, Mobius
 */
public class AteliaManager extends AbstractNpcAI
{
	// Npc Devianne
	private static final int DEVIANNE = 34089;
	// Location Devianne
	private static final Location DEVIANNE_LOC = new Location(-50063, 49439, -1760, 40362);
	// Door Atelia Fortess Guard
	private static final int GUARD = 23539;
	private static final NpcStringId[] ATELIA_MSG =
	{
		NpcStringId.HAVE_YOU_SEEN_KELBIM_S_POWER_WE_LL_SHOW_YOU_THE_WRATH_OF_THE_EMBRYO,
		NpcStringId.I_CAN_FEEL_THE_ENERGY_FROM_THE_ATELIA_FEEL_THE_POWER_OF_KELBIM,
		NpcStringId.LIONA_AND_THE_LOWLY_SOLDIERS_WILL_BE_BURIED_HERE,
		NpcStringId.SHOW_THEM_THE_POWER_OF_KELBIM
	};
	private static final SkillHolder[] ATELIA_POISON =
	{
		new SkillHolder(23653, 2), // Poisonous Atelia
		new SkillHolder(23653, 3), // Poisonous Atelia
		new SkillHolder(23653, 4), // Poisonous Atelia
	};
	// AI (Hummel,Geork,Burnstein) in Zone
	private static final SkillHolder SUPPLY_BLOCKADE = new SkillHolder(16526, 1);
	private static final SkillHolder POOR_EQUIPMENT = new SkillHolder(16542, 2);
	private static final SkillHolder INDISCEPLINE = new SkillHolder(16542, 3);
	// Bosses
	private static final int GEORK = 23586; // Geork
	private static final int BURNSTEIN = 23587; // Burnstein
	private static final int HUMMEL = 23588; // Hummel
	// Npcs Stronghold I
	private static final int BARTON = 34059; // Barton Aden Vanguard
	private static final int GLENKI = 34063; // Glenkinchie Blackbird Clan Member
	// Location Stronghold I
	private static final Location BARTON_LOC = new Location(-45675, 59130, -2904, 54353);
	private static final Location GLENKI_LOC = new Location(-45579, 59169, -2904, 55286);
	// Flag Stronghold I
	private static final Location FLAG_1_LOC = new Location(-45690, 58901, -2864, 36407);
	private static final Location FLAG_2_LOC = new Location(-45419, 59066, -2864, 54421);
	// Npcs Stronghold II
	private static final int HAYUK = 34060; // Hayuk Aden Vanguard
	private static final int HURAK = 34064; // Hurak Blackbird Clan Member
	// Location Stronghold II
	private static final Location HURAK_LOC = new Location(-41766, 50416, -2032, 54353); //
	private static final Location HAYUK_LOC = new Location(-41879, 50389, -2032, 55286); //
	// Flag Stronghold II
	private static final Location FLAG_3_LOC = new Location(-41962, 50182, -1988, 36407); //
	private static final Location FLAG_4_LOC = new Location(-41631, 50246, -2001, 54421); //
	// Npcs Stronghold III
	private static final int ELISE = 34061; // Elise Aden Vanguard
	private static final int LAFFIAN = 34065; // Laffian Blackbird Clan Member
	// Other Stronghold III
	private static final int JULIA = 34074; // Julia Warehouse Keeper
	private static final int MION = 34076; // Mion Grocer
	// Location Stronghold III
	private static final Location ELISE_LOC = new Location(-44715, 40358, -1416, 29326);
	private static final Location LAFFIAN_LOC = new Location(-44574, 40318, -1416, 28937);
	private static final Location JULIA_LOC = new Location(-44603, 40202, -1416, 32350);
	private static final Location MION_LOC = new Location(-44525, 40430, -1416, 22568);
	// Flag Stronghold III
	private static final Location FLAG_5_LOC = new Location(-44778, 40556, -1384, 22322);
	private static final Location FLAG_6_LOC = new Location(-44860, 40254, -1376, 23239);
	// Npcs Stronghold IV
	private static final int ELIYAH = 34062; // Eliyah Aden Vanguard
	private static final int SHERRY = 34066; // Sherry Blackbird Clan Member
	// Other Stronghold IV
	private static final int SAYLEM = 34075; // Saylem Warehouse Keeper
	private static final int NIKA = 34077; // Nika Grocer
	// Location Stronghold IV
	private static final Location ELIYAH_LOC = new Location(-58480, 44000, -1552, 25300);
	private static final Location SHERRY_LOC = new Location(-58395, 43905, -1552, 28798);
	private static final Location SAYLEM_LOC = new Location(-58327, 43957, -1552, 25179);
	private static final Location NIKA_LOC = new Location(-58450, 43843, -1552, 32767);
	// Flag Stronghold IV
	private static final Location FLAG_7_LOC = new Location(-58449, 44207, -1512, 20327);
	private static final Location FLAG_8_LOC = new Location(-58693, 43986, -1520, 17904);
	// Stages (Floors)
	private static final int[] FLOOR_MOBS =
	{
		23505,
		23506,
		23507,
		23508,
		23509,
		23510,
		23511,
		23512
	};
	private static final int[] ALERT =
	{
		23595,
		23596,
		23597,
		23598,
		23599,
		23600,
		23601,
		23602
	};
	// Skills Stages
	private static final int[] ATELIA_CURSE =
	{
		23506,
		23508,
		23511,
		23512
	};
	// PART OF BOSS AI
	private static final int[] SB_GROUP =
	{
		23505,
		23506,
		23507,
		23508,
		23509,
		23510,
		23511,
		23512
	};
	static final int[][] FORTESS_SPY =
	{
		{
			23589,
			-41659,
			44081,
			-1448,
			0
		},
		{
			23589,
			-50091,
			48822,
			-1760,
			0
		},
		{
			23589,
			-49263,
			50204,
			-2400,
			0
		},
		{
			23589,
			-48556,
			45595,
			-1768,
			0
		},
		{
			23589,
			-44548,
			58729,
			-2928,
			0
		},
		{
			23589,
			-44636,
			45261,
			-1528,
			0
		},
		{
			23589,
			-45055,
			44769,
			-1544,
			0
		},
		{
			23589,
			-45729,
			41010,
			-1512,
			0
		},
		{
			23589,
			-46178,
			49001,
			-2400,
			0
		},
		{
			23589,
			-46466,
			56947,
			-3184,
			0
		},
		{
			23589,
			-46619,
			43794,
			-1560,
			0
		},
		{
			23589,
			-46814,
			50187,
			-2376,
			0
		},
		{
			23589,
			-47309,
			55932,
			-3184,
			0
		},
		{
			23589,
			-47470,
			52576,
			-2392,
			0
		},
		{
			23589,
			-47503,
			58967,
			-3192,
			0
		},
		{
			23589,
			-47815,
			51378,
			-2400,
			0
		},
		{
			23589,
			-48077,
			55335,
			-3160,
			0
		},
		{
			23589,
			-43866,
			47379,
			-2048,
			0
		},
		{
			23589,
			-43866,
			47379,
			-2048,
			0
		}
		
	};
	// Infusers
	private static final int INFUSER_1 = 23537;
	private static final int INFUSER_2 = 23538;
	// Static Npcs
	private static final int FLAG = 19594; // Stronghold Flag
	// Items
	private static final int TPST_1 = 46146; // Atelia Fortress Stronghold I Teleport Device
	private static final int TPST_2 = 46147; // Atelia Fortress Stronghold II Teleport Device
	private static final int TPST_3 = 46148; // Atelia Fortress Stronghold III Teleport Device
	private static final int TPST_4 = 46149; // Atelia Fortress Stronghold VI Teleport Device
	// Misc
	private static int _killCount = 0;
	// Other
	private static final int DESPAWN = 1800000; // Time 30 Min
	private static final int SBCANCEL = 3600000; // Time 1 Hour
	private static final int DDESPAWN = 10800000; // Time 3 Hour
	
	static ArrayList<Npc> FortessSpawns = new ArrayList<>();
	
	private AteliaManager()
	{
		addStartNpc(BARTON, GLENKI, HAYUK, HURAK, ELISE, LAFFIAN, JULIA, MION, ELIYAH, SHERRY, SAYLEM, NIKA);
		addFirstTalkId(BARTON, GLENKI, HAYUK, HURAK, ELISE, LAFFIAN, JULIA, MION, ELIYAH, SHERRY, SAYLEM, NIKA);
		addTalkId(BARTON, GLENKI, HAYUK, HURAK, ELISE, LAFFIAN, JULIA, MION, ELIYAH, SHERRY, SAYLEM, NIKA);
		addKillId(FLOOR_MOBS);
		addKillId(ALERT);
		addKillId(GEORK, BURNSTEIN, HUMMEL, GUARD, INFUSER_1, INFUSER_2);
		addSpawnId(BARTON, GLENKI, FLAG, HAYUK, HURAK);
		addSpawnId(ELISE, LAFFIAN, JULIA, MION);
		addSpawnId(ELIYAH, SHERRY, SAYLEM, NIKA);
		addSpawnId(HUMMEL, GEORK, BURNSTEIN, DEVIANNE);
		addSpawnId(SB_GROUP);
		addAttackId(ATELIA_CURSE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34059-1.htm":
			case "34060-1.htm":
			case "34061-1.htm":
			case "34062-1.htm":
			case "34063-1.htm":
			case "34063-2.htm":
			case "34064-1.htm":
			case "34064-2.htm":
			case "34065-1.htm":
			case "34065-2.htm":
			case "34066-1.htm":
			case "34066-2.htm":
			case "34074-1.htm":
			case "34074-2.htm":
			case "34074-3.htm":
			case "34075-1.htm":
			case "34075-2.htm":
			case "34075-3.htm":
			case "34076-1.htm":
			case "34077-1.htm":
			{
				htmltext = event;
				break;
			}
			case "give_tp_st_1":
			{
				if (!hasQuestItems(player, TPST_1))
				{
					giveItems(player, TPST_1, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					htmltext = "34059-2.htm"; // fix
				}
				break;
			}
			case "give_tp_st_2":
			{
				if (!hasQuestItems(player, TPST_2))
				{
					giveItems(player, TPST_2, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					htmltext = "34060-2.htm"; // todo off html text
				}
				break;
			}
			case "give_tp_st_3":
			{
				if (!hasQuestItems(player, TPST_3))
				{
					giveItems(player, TPST_3, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					htmltext = "34061-2.htm"; // fix
				}
				break;
			}
			case "give_tp_st_4":
			{
				if (!hasQuestItems(player, TPST_4))
				{
					giveItems(player, TPST_4, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					htmltext = "34062-2.htm"; // fix
				}
				break;
			}
			// Stronghold's
			case "SH_1":
			{
				if (npc == null)
				{
					addSpawn(BARTON, BARTON_LOC, false, DESPAWN);
					addSpawn(GLENKI, GLENKI_LOC, false, DESPAWN);
					addSpawn(FLAG, FLAG_1_LOC, false, DESPAWN);
					addSpawn(FLAG, FLAG_2_LOC, false, DESPAWN);
				}
				break;
			}
			case "SH_2":
			{
				if (npc == null)
				{
					addSpawn(HAYUK, HAYUK_LOC, false, DESPAWN);
					addSpawn(HURAK, HURAK_LOC, false, DESPAWN);
					addSpawn(FLAG, FLAG_3_LOC, false, DESPAWN);
					addSpawn(FLAG, FLAG_4_LOC, false, DESPAWN);
				}
				break;
			}
			case "SH_3":
			{
				if (npc == null)
				{
					addSpawn(ELISE, ELISE_LOC, false, DESPAWN);
					addSpawn(LAFFIAN, LAFFIAN_LOC, false, DESPAWN);
					addSpawn(JULIA, JULIA_LOC, false, DESPAWN);
					addSpawn(MION, MION_LOC, false, DESPAWN);
					addSpawn(FLAG, FLAG_5_LOC, false, DESPAWN);
					addSpawn(FLAG, FLAG_6_LOC, false, DESPAWN);
				}
				break;
			}
			case "SH_4":
			{
				if (npc == null)
				{
					addSpawn(ELIYAH, ELIYAH_LOC, false, DESPAWN);
					addSpawn(SHERRY, SHERRY_LOC, false, DESPAWN);
					addSpawn(SAYLEM, SAYLEM_LOC, false, DESPAWN);
					addSpawn(NIKA, NIKA_LOC, false, DESPAWN);
					addSpawn(FLAG, FLAG_7_LOC, false, DESPAWN);
					addSpawn(FLAG, FLAG_8_LOC, false, DESPAWN);
				}
				break;
			}
			case "SB_1":
			{
				for (int sb : SB_GROUP)
				{
					for (Spawn spawn : SpawnTable.getInstance().getSpawns(sb))
					{
						for (Npc monster : spawn.getSpawnedNpcs())
						{
							if ((monster.getZ() <= -2759) && (monster.getZ() >= -3246))
							{
								monster.setTarget(monster);
								monster.doCast(SUPPLY_BLOCKADE.getSkill());
							}
						}
					}
				}
				break;
			}
			case "SB_2":
			{
				for (int sb : SB_GROUP)
				{
					for (Spawn spawn : SpawnTable.getInstance().getSpawns(sb))
					{
						for (Npc monster : spawn.getSpawnedNpcs())
						{
							if ((monster.getZ() <= -2020) && (monster.getZ() >= -2759))
							{
								monster.setTarget(monster);
								monster.doCast(POOR_EQUIPMENT.getSkill());
							}
						}
					}
				}
				break;
			}
			case "SB_3":
			{
				for (int sb : SB_GROUP)
				{
					for (Spawn spawn : SpawnTable.getInstance().getSpawns(sb))
					{
						for (Npc monster : spawn.getSpawnedNpcs())
						{
							if ((monster.getZ() <= -1477) && (monster.getZ() >= -2212)) // need correct!
							{
								monster.setTarget(monster);
								monster.doCast(INDISCEPLINE.getSkill());
							}
						}
					}
				}
				break;
			}
			case "SB_1_C":
			{
				for (int sb : SB_GROUP)
				{
					for (Spawn spawn : SpawnTable.getInstance().getSpawns(sb))
					{
						for (Npc monster : spawn.getSpawnedNpcs())
						{
							monster.getEffectList().stopEffects(AbnormalType.ALL_ATTACK_DOWN);
							monster.stopSkillEffects(true, 16526);
						}
					}
				}
				break;
			}
			case "SB_2_C":
			{
				for (int sb : SB_GROUP)
				{
					for (Spawn spawn : SpawnTable.getInstance().getSpawns(sb))
					{
						for (Npc monster : spawn.getSpawnedNpcs())
						{
							monster.getEffectList().stopEffects(AbnormalType.MAX_HP_DOWN);
							monster.stopSkillEffects(true, 16542);
						}
					}
				}
				break;
			}
			case "SB_3_C":
			{
				for (int sb : SB_GROUP)
				{
					for (Spawn spawn : SpawnTable.getInstance().getSpawns(sb))
					{
						for (Npc monster : spawn.getSpawnedNpcs())
						{
							monster.getEffectList().stopEffects(AbnormalType.MAX_HP_DOWN);
							monster.stopSkillEffects(true, 16542);
						}
					}
				}
				break;
			}
			case "SPY_CLEAR":
			{
				for (Npc spawn : FortessSpawns)
				{
					if (spawn != null)
					{
						spawn.deleteMe();
					}
				}
				FortessSpawns.clear();
				break;
			}
			case "SPY_SPAWN":
			{
				for (int[] spawn : FORTESS_SPY)
				{
					FortessSpawns.add(addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, 0));
				}
				break;
			}
			case "DOOR_CLOSE":
			{
				_killCount = 0;
				closeDoor(18190002, 0);
				closeDoor(18190004, 0);
				break;
			}
			case "ALERT":
			{
				final int rnd = getRandom(3, 4);
				for (int i = 0; i < rnd; i++)
				{
					final Npc alert = addSpawn(ALERT[i], npc.getX() + 10, npc.getY() + 10, npc.getZ() + 10, npc.getHeading(), false, 0, false);
					alert.setTitle("On Alert Stage 1");
					addAttackDesire(alert, player);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final int chance = getRandom(1000);
		if (CommonUtil.contains(ATELIA_CURSE, npc.getId()))
		{
			if (!npc.isCastingNow() && (chance <= 20))
			{
				npc.setTarget(attacker);
				npc.doCast(getRandomEntry(ATELIA_POISON).getSkill());
			}
		}
		else if (CommonUtil.contains(FLOOR_MOBS, npc.getId()) && (chance > 90))
		{
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), ATELIA_MSG[getRandom(1)]));
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		
		if ((npc.getZ() <= -2804) && (npc.getZ() >= -2999) && (npc.getId() == INFUSER_1))
		{
			startQuestTimer("SH_1", 100, null, null);
		}
		if ((npc.getZ() <= -2029) && (npc.getZ() >= -2050) && (npc.getId() == INFUSER_1))
		{
			startQuestTimer("SH_2", 100, null, null);
		}
		if ((npc.getZ() <= -1419) && (npc.getZ() >= -1520) && (npc.getId() == INFUSER_2))
		{
			startQuestTimer("SH_3", 100, null, null);
		}
		if ((npc.getZ() <= -1552) && (npc.getZ() >= -1580) && (npc.getId() == INFUSER_2))
		{
			startQuestTimer("SH_4", 100, null, null);
		}
		if (npc.getId() == GUARD)
		{
			_killCount++;
			if (_killCount == 2)
			{
				openDoor(18190002, 0);
				openDoor(18190004, 0);
				startQuestTimer("DOOR_CLOSE", SBCANCEL, npc, killer);
			}
		}
		else if (npc.getId() == HUMMEL)
		{
			startQuestTimer("SB_1", 100, npc, killer);
		}
		else if (npc.getId() == GEORK)
		{
			startQuestTimer("SB_2", 100, npc, killer);
		}
		else if (npc.getId() == BURNSTEIN)
		{
			addSpawn(DEVIANNE, DEVIANNE_LOC, false, DDESPAWN);
			startQuestTimer("SPY_CLEAR", 100, npc, null);
			startQuestTimer("SB_3", 100, npc, killer);
		}
		else if (CommonUtil.contains(FLOOR_MOBS, npc.getId()) && (getRandom(100) <= 6))
		{
			startQuestTimer("ALERT", 100, npc, killer);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + "-1.htm";
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case HUMMEL:
			{
				startQuestTimer("SB_1_C", 100, npc, null);
				break;
			}
			case GEORK:
			{
				startQuestTimer("SB_2_C", 100, npc, null);
				break;
			}
			case BURNSTEIN:
			{
				startQuestTimer("SB_3_C", 100, npc, null);
				startQuestTimer("SPY_SPAWN", 100, npc, null);
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new AteliaManager();
	}
}
