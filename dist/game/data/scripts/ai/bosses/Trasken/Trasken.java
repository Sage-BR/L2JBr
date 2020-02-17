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
package ai.bosses.Trasken;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.data.xml.impl.DoorData;
import org.l2jbr.gameserver.datatables.SpawnTable;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.instancemanager.GrandBossManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.Playable;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.BuffInfo;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.NoSummonFriendZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

import ai.AbstractNpcAI;

/**
 * Trasken RB
 * @author Gigi
 * @date 2017-07-27 - [10:11:22]
 */
public class Trasken extends AbstractNpcAI
{
	// NPCs
	private static final int TRASKEN = 29197;
	private static final int TAIL_TRASKEN = 29200;
	private static final int TIE = 29205;
	private static final int BIG_TIE = 29199;
	private static final int VICTIM_EARTWORMS_1 = 29201;
	private static final int VICTIM_EARTWORMS_2 = 29202;
	private static final int VICTIM_EARTWORMS_3 = 29203;
	private static final int LAVRA_1 = 29204;
	private static final int LAVRA_2 = 29207;
	private static final int LAVRA_3 = 29208;
	private static final int DIGISTIVE = 29206;
	private static final int TRADJAN = 19160;
	private static final int HEART_ERTHWYRM = 19081;
	private static final int TELEPORT_ORB = 33513;
	// Zone
	private static final int ZONE_ID = 12108;
	private static final int ZONE_ID_HEART = 12109;
	private final int[] eventTriggers = new int[]
	{
		22120840,
		22120842,
		22120844,
		22120846
	};
	private static final int DOOR = 22120001;
	private static final Location EXIT_LOCATION = new Location(87679, -141982, -1341);
	static final Location CENTER_LOCATION = new Location(81208, -182095, -9895);
	static final Location HEART_LOCATION = new Location(81208, -182095, -9895);
	// Skill
	private static final SkillHolder SKILL_TAIL = new SkillHolder(14342, 1);
	private static final SkillHolder SKILL_TAIL_2 = new SkillHolder(14343, 1);
	private static final SkillHolder SKILL_TRASKEN_FIRST = new SkillHolder(14336, 1);
	private static final SkillHolder SKILL_TRASKEN_BUFF = new SkillHolder(14341, 1);
	private static final SkillHolder SKILL_TRASKEN_SLEEP = new SkillHolder(14504, 1); // 24 sec
	private static final SkillHolder SKILL_TRASKEN_UP = new SkillHolder(14505, 1);
	private static final SkillHolder SKILL_TIE_ROOT = new SkillHolder(14332, 1);
	private static final SkillHolder SKILL_TIE_CHAIN = new SkillHolder(14333, 1);
	private static final SkillHolder SKILL_1 = new SkillHolder(14334, 1); // Poison Puddle
	private static final SkillHolder SKILL_2 = new SkillHolder(14337, 1); // Earth Wyrm Body Strike
	private static final SkillHolder SKILL_3 = new SkillHolder(14338, 1); // Earth Wyrm Body Strike
	private static final SkillHolder SKILL_4 = new SkillHolder(14339, 1); // Earth Wyrm Body Strike
	private static final SkillHolder SKILL_5 = new SkillHolder(14340, 1); // Earth Wyrm Poison Cannon
	// Status
	private static final int ALIVE = 0;
	private static final int DEAD = 3;
	// Others
	protected double hp_tail;
	protected double hp_trasken;
	private static Npc tie_trasken;
	private static Npc trasken;
	private static NoSummonFriendZone _zoneLair;
	private static NoSummonFriendZone _zoneLair2;
	private int playersToEnter;
	protected int _statusZone = 0;
	protected ScheduledFuture<?> _collapseTask;
	protected AtomicInteger _killsTie = new AtomicInteger(0);
	protected AtomicInteger _killsTradjan = new AtomicInteger(0);
	// Spawns
	private static final Location Spawn_Heart = new Location(88292, -173758, -15965);
	private static final Location[] Spawn_Tie = new Location[]
	{
		new Location(79075, -180963, -9897, 4527),
		new Location(81047, -182282, -9897, 33582),
		new Location(79942, -179851, -9897, 40574),
		new Location(81931, -180069, -9897, 2849),
		new Location(80864, -184281, -9897, 46457),
		new Location(81233, -179842, -9897, 28503),
		new Location(78735, -183107, -9897, 34160),
		new Location(80139, -180923, -9897, 34232),
		new Location(81758, -181902, -9897, 29365),
		new Location(81851, -180719, -9897, 11837),
		new Location(79181, -182178, -9897, 65110),
		new Location(83296, -182275, -9897, 4710),
		new Location(83966, -181084, -9897, 18435),
		new Location(83181, -181023, -9897, 63036),
		new Location(82263, -182977, -9897, 36550),
		new Location(80950, -182856, -9897, 27788),
		new Location(79966, -182812, -9897, 26489),
		new Location(82237, -184076, -9897, 45551),
		new Location(80868, -181154, -9897, 13967),
		new Location(80209, -184234, -9897, 27683),
		new Location(83150, -183279, -9897, 62418),
		new Location(79795, -182271, -9897, 5870)
	};
	private static final Location[] Spawn_Big_Tie = new Location[]
	{
		new Location(83235, -182070, -9897, 31663),
		new Location(83913, -183256, -9897, 4038),
		new Location(82853, -180868, -9897, 32158),
		new Location(78730, -182917, -9897, 35257),
		new Location(82175, -180300, -9897, 39388),
		new Location(79981, -181057, -9897, 14008),
		new Location(79019, -181882, -9897, 6394),
		new Location(79846, -182025, -9897, 28780),
		new Location(81224, -184186, -9897, 4064),
		new Location(80725, -181051, -9897, 34486),
		new Location(79838, -184216, -9897, 45196),
		new Location(82073, -181956, -9897, 27212),
		new Location(81920, -180981, -9897, 47056),
		new Location(80820, -183285, -9897, 31129),
		new Location(81788, -183047, -9897, 18980),
		new Location(78860, -179717, -9897, 53788),
		new Location(81105, -180268, -9897, 23643),
		new Location(83222, -184038, -9897, 13689),
		new Location(82093, -184188, -9897, 61993)
	};
	private static final Location[] Spawn_Larva_1 = new Location[]
	{
		new Location(81869, -181917, -9897, 59394),
		new Location(82831, -182837, -9897, 19463),
		new Location(79771, -184114, -9897, 15764),
		new Location(79887, -180114, -9897, 17733),
		new Location(80987, -181006, -9897, 12119),
		new Location(79065, -182890, -9897, 63705),
		new Location(78883, -183839, -9897, 5843),
		new Location(80014, -182944, -9897, 6195)
	};
	private static final Location[] Spawn_Larva_2 = new Location[]
	{
		new Location(81869, -181917, -9897, 20701),
		new Location(82831, -182837, -9897, 6257),
		new Location(79771, -184114, -9897, 28729),
		new Location(79887, -180114, -9897, 26087),
		new Location(80987, -181006, -9897, 59020),
		new Location(79065, -182890, -9897, 44820),
		new Location(78883, -183839, -9897, 23282),
		new Location(80014, -182944, -9897, 9306)
	};
	private static final Location[] Spawn_Larva_3 = new Location[]
	{
		new Location(79785, -181954, -9897, 65516),
		new Location(81727, -184036, -9897, 63858),
		new Location(81909, -181006, -9897, 12875),
		new Location(79264, -180704, -9897, 53464),
		new Location(80769, -183944, -9897, 31310),
		new Location(79886, -183771, -9897, 53311),
		new Location(78706, -183267, -9897, 862),
		new Location(81947, -182190, -9897, 43213),
		new Location(83103, -181089, -9897, 30877),
		new Location(81847, -179971, -9897, 40880),
		new Location(81908, -183298, -9897, 6597),
		new Location(79227, -181739, -9897, 63462),
		new Location(79918, -183288, -9897, 31839),
		new Location(80720, -181130, -9897, 9352),
		new Location(80166, -179956, -9897, 28989),
		new Location(81156, -179891, -9897, 9000),
		new Location(80874, -182796, -9897, 51715),
		new Location(80205, -180998, -9897, 1193),
		new Location(77961, -182792, -9897, 16867),
		new Location(83190, -184199, -9897, 57438),
		new Location(82764, -182099, -9897, 36113),
		new Location(78890, -179873, -9897, 50574),
		new Location(77805, -180767, -9897, 20522),
		new Location(82806, -180142, -9897, 46858),
		new Location(82152, -184742, -9897, 26490),
		new Location(82732, -183220, -9897, 60425),
		new Location(77975, -181902, -9897, 23116),
		new Location(81255, -182176, -9897, 53943),
		new Location(78796, -184218, -9897, 40593)
	};
	private static final Location[] Spawn_Victim_1 = new Location[]
	{
		new Location(87891, -173888, 0, 14559),
		new Location(87777, -172808, 0, 54130),
		new Location(88896, -174206, 0, 4641)
	};
	private static final Location[] Spawn_Victim_2 = new Location[]
	{
		new Location(88085, -174105, 0, 39106),
		new Location(88949, -174227, 0, 58094),
		new Location(89000, -172909, 0, 55350),
		new Location(87941, -173185, 0, 22119)
	};
	private static final Location[] Spawn_Victim_3 = new Location[]
	{
		new Location(88247, -174298, 0, 4884),
		new Location(88924, -173858, 0, 44289),
		new Location(88204, -172812, 0, 24052)
	};
	private static final Location[] Spawn_Tradjan = new Location[]
	{
		new Location(79785, -181954, -9897, 65516),
		new Location(81727, -184036, -9897, 63858),
		new Location(81909, -181006, -9897, 12875),
		new Location(79264, -180704, -9897, 53464),
		new Location(80769, -183944, -9897, 31310),
		new Location(79886, -183771, -9897, 53311),
		new Location(78706, -183267, -9897, 862),
		new Location(81947, -182190, -9897, 43213),
		new Location(83103, -181089, -9897, 30877),
		new Location(81847, -179971, -9897, 40880),
		new Location(81908, -183298, -9897, 6597),
		new Location(79227, -181739, -9897, 63462),
		new Location(79918, -183288, -9897, 31839),
		new Location(80720, -181130, -9897, 9352),
		new Location(80166, -179956, -9897, 28989),
		new Location(81156, -179891, -9897, 9000),
		new Location(80874, -182796, -9897, 51715),
		new Location(80205, -180998, -9897, 1193),
		new Location(77961, -182792, -9897, 16867),
		new Location(83190, -184199, -9897, 57438),
		new Location(82764, -182099, -9897, 36113),
		new Location(78890, -179873, -9897, 50574),
		new Location(77805, -180767, -9897, 20522),
		new Location(82806, -180142, -9897, 46858),
		new Location(82152, -184742, -9897, 26490),
		new Location(82732, -183220, -9897, 60425),
		new Location(77975, -181902, -9897, 23116),
		new Location(81255, -182176, -9897, 53943),
		new Location(78796, -184218, -9897, 40593)
	};
	private static final Location[] Spawn_Digestive = new Location[]
	{
		new Location(88114, -173387, -15980),
		new Location(88640, -173491, -15980),
		new Location(88546, -174051, -15981),
		new Location(87913, -173950, -15981)
	};
	// @formatter:off
	private static final int[][] Rnd_Spawn_Tail = new int[][]
    {
        {80966, -183780, -9896},
        {82949, -181947, -9899},
        {81688, -181059, -9895},
        {81208, -182095, -9895}
    };
    private static final int[][] Rnd_Spawn_Trasken = new int[][]
    {
        {82564, -180742, -9896},
        {82379, -183532, -9896},
        {79602, -183321, -9896},
        {79698, -180859, -9896},
        {81208, -182095, -9896}
    };
	// @formatter:on
	
	public Trasken()
	{
		super();
		_zoneLair = ZoneManager.getInstance().getZoneById(ZONE_ID, NoSummonFriendZone.class);
		_zoneLair2 = ZoneManager.getInstance().getZoneById(ZONE_ID_HEART, NoSummonFriendZone.class);
		int[] creature = new int[]
		{
			TRASKEN,
			TIE,
			BIG_TIE,
			TAIL_TRASKEN,
			VICTIM_EARTWORMS_1,
			VICTIM_EARTWORMS_2,
			VICTIM_EARTWORMS_3,
			LAVRA_1,
			LAVRA_2,
			LAVRA_3,
			TRADJAN,
			HEART_ERTHWYRM
		};
		registerMobs(creature);
		addEnterZoneId(ZONE_ID);
		addExitZoneId(ZONE_ID);
		addEnterZoneId(ZONE_ID_HEART);
		addExitZoneId(ZONE_ID_HEART);
		init();
		if (DoorData.getInstance().getDoor(DOOR) != null)
		{
			DoorData.getInstance().getDoor(DOOR).openMe();
		}
		// Unlock
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(TRASKEN);
		final int status = GrandBossManager.getInstance().getBossStatus(TRASKEN);
		if (status == DEAD)
		{
			final long time = info.getLong("respawn_time") - System.currentTimeMillis();
			if (time > 0)
			{
				startQuestTimer("unlock_trasken", time, null, null);
			}
			else
			{
				GrandBossManager.getInstance().setBossStatus(TRASKEN, ALIVE);
			}
		}
		else if (status != ALIVE)
		{
			GrandBossManager.getInstance().setBossStatus(TRASKEN, ALIVE);
		}
	}
	
	private void init()
	{
		int size = _zoneLair.getPlayersInside().size();
		if ((size >= 14) && (size <= 28))
		{
			playersToEnter = 7;
		}
		else if ((size >= 28) && (size <= 56))
		{
			playersToEnter = 14;
		}
		else if ((size >= 56) && (size <= 102))
		{
			playersToEnter = 21;
		}
		else
		{
			playersToEnter = 1;
		}
	}
	
	protected void Clean()
	{
		_statusZone = 0;
		if (_collapseTask != null)
		{
			_collapseTask.cancel(false);
			_collapseTask = null;
		}
		_zoneLair.getCharactersInside().forEach(mob ->
		{
			if (mob.isNpc())
			{
				mob.deleteMe();
				mob.setIsDead(true);
			}
		});
		_zoneLair2.getCharactersInside().forEach(mob ->
		{
			if (mob.isNpc())
			{
				mob.deleteMe();
				mob.setIsDead(true);
			}
		});
	}
	
	private void Fail(boolean clean)
	{
		if (clean)
		{
			Clean();
		}
		_zoneLair.oustAllPlayers();
		_zoneLair2.oustAllPlayers();
		GrandBossManager.getInstance().setBossStatus(TRASKEN, ALIVE);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		switch (npc.getId())
		{
			case TAIL_TRASKEN:
			{
				hp_tail = npc.getCurrentHp();
				break;
			}
			case TRASKEN:
			{
				if (npc.isCastingNow())
				{
					return super.onAttack(npc, attacker, damage, isSummon);
				}
				World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 250, cha ->
				{
					if (cha != null)
					{
						npc.setTarget(cha);
					}
				});
				if (getRandom(100) < 30)
				{
					final Npc doom = addSpawn(18998, attacker.getX() + 25, attacker.getY() + 25, attacker.getZ(), 0, false, 30, false);
					doom.setTarget(doom);
					doom.isCastingNow();
					doom.doCast(SKILL_1.getSkill());
					ThreadPool.schedule(() -> doom.deleteMe(), 15000);
				}
				final double percent = ((npc.getCurrentHp() - damage) / npc.getMaxHp()) * 100.0;
				if ((percent <= 30) && (_statusZone == 4))
				{
					TraskenStay(npc);
					_statusZone = 5;
				}
				if ((percent <= 40) && (_statusZone == 3))
				{
					TraskenStay(npc);
					_statusZone = 4;
				}
				if (getRandom(100) < 50)
				{
					npc.doCast(SKILL_2.getSkill());
				}
				
				if (getRandom(100) < 40)
				{
					npc.doCast(SKILL_3.getSkill());
				}
				
				if (getRandom(100) < 25)
				{
					npc.doCast(SKILL_4.getSkill());
				}
				
				if (getRandom(100) < 15)
				{
					npc.doCast(SKILL_5.getSkill());
				}
				hp_trasken = npc.getCurrentHp();
				break;
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case TAIL_TRASKEN:
			{
				npc.setIsOverloaded(true);
				npc.setRandomWalking(true);
				npc.doCast(SKILL_TAIL.getSkill());
				break;
			}
			case TRASKEN:
			{
				npc.setIsOverloaded(true);
				npc.setRandomWalking(true);
				npc.setIsDead(false);
				break;
			}
			case TIE:
			case BIG_TIE:
			{
				World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 600, cha ->
				{
					npc.setTarget(cha);
				});
				npc.setIsOverloaded(true);
				npc.setRandomWalking(true);
				npc.getSpawn().setRespawnDelay(60);
				break;
			}
			case TRADJAN:
			{
				npc.getSpawn().setRespawnDelay(120);
				World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 3500, cha ->
				{
					npc.setTarget(cha);
				});
				break;
			}
			case LAVRA_1:
			case LAVRA_2:
			case LAVRA_3:
			{
				npc.getSpawn().setRespawnDelay(200);
				World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 3500, cha ->
				{
					npc.setTarget(cha);
				});
				break;
			}
			case VICTIM_EARTWORMS_1:
			case VICTIM_EARTWORMS_2:
			case VICTIM_EARTWORMS_3:
			{
				npc.getSpawn().setRespawnDelay(30);
				World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 1000, cha ->
				{
					npc.setTarget(cha);
				});
				break;
			}
			case DIGISTIVE:
			{
				npc.setIsOverloaded(true);
				npc.setRandomWalking(true);
				npc.getSpawn().setRespawnDelay(60);
				World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 1000, cha ->
				{
					npc.setTarget(cha);
				});
				break;
			}
			case HEART_ERTHWYRM:
			{
				npc.setIsOverloaded(true);
				npc.setRandomWalking(true);
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (npc.isDead() || (creature == null))
		{
			return null;
		}
		if ((npc.getId() == LAVRA_1) || (npc.getId() == LAVRA_2) || (npc.getId() == LAVRA_3) || (npc.getId() == TRADJAN))
		{
			addAttackPlayerDesire(npc, getRandomEntry(_zoneLair.getPlayersInside()));
		}
		else if ((npc.getId() == TIE) || (npc.getId() == BIG_TIE))
		{
			if (getRandom(100) < 60)
			{
				npc.setTarget(creature);
				npc.doCast(SKILL_TIE_ROOT.getSkill());
				addAttackPlayerDesire(npc, (Playable) creature);
			}
			else
			{
				npc.setTarget(creature);
				npc.doCast(SKILL_TIE_CHAIN.getSkill());
				addAttackPlayerDesire(npc, (Playable) creature);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "unlock_trasken":
			{
				GrandBossManager.getInstance().setBossStatus(TRASKEN, ALIVE);
				break;
			}
			case "exitEarthWyrnCave":
			{
				if (npc.getId() == TELEPORT_ORB)
				{
					player.teleToLocation(EXIT_LOCATION);
				}
				break;
			}
			case "finish":
			{
				trasken.doDie(player);
				trasken.setIsDead(true);
				_zoneLair2.getPlayersInside().forEach(players -> players.teleToLocation(CENTER_LOCATION));
				_zoneLair2.getPlayersInside().forEach(p ->
				{
					playMovie(p, Movie.SC_EARTHWORM_ENDING);
				});
				if (_collapseTask != null)
				{
					_collapseTask.cancel(true);
					_collapseTask = null;
				}
				_zoneLair.getCharactersInside().stream().filter(Creature::isNpc).forEach(mob -> mob.deleteMe());
				_zoneLair.getCharactersInside().stream().filter(WorldObject::isMonster).forEach(cha -> ((MonsterInstance) cha).getSpawn().stopRespawn());
				_zoneLair2.getCharactersInside().stream().filter(Creature::isNpc).forEach(mob -> mob.deleteMe());
				_zoneLair2.getCharactersInside().stream().filter(WorldObject::isMonster).forEach(cha -> ((MonsterInstance) cha).getSpawn().stopRespawn());
				ThreadPool.schedule(() -> npc.decayMe(), 10000);
				cancelQuestTimer("finish", npc, null);
				
				GrandBossManager.getInstance().setBossStatus(TRASKEN, DEAD);
				final long respawnTime = (Config.TRASKEN_SPAWN_INTERVAL + getRandom(-Config.TRASKEN_SPAWN_RANDOM, Config.TRASKEN_SPAWN_RANDOM)) * 3600000;
				final StatsSet info = GrandBossManager.getInstance().getStatsSet(TRASKEN);
				info.set("respawn_time", System.currentTimeMillis() + respawnTime);
				GrandBossManager.getInstance().setStatsSet(TRASKEN, info);
				startQuestTimer("unlock_trasken", respawnTime, null, null);
				break;
			}
			case "spawn_rnd":
			{
				switch (npc.getId())
				{
					case TAIL_TRASKEN:
					{
						if (_statusZone == 2)
						{
							tie_trasken.doCast(SKILL_TAIL_2.getSkill());
							tie_trasken.getSpawn().stopRespawn();
							tie_trasken.decayMe();
							
							final int[] spawn = Rnd_Spawn_Tail[Rnd.get(Rnd_Spawn_Tail.length)];
							if (SpawnTable.getInstance().getSpawns(TAIL_TRASKEN) == null)
							{
								ThreadPool.schedule(() ->
								{
									tie_trasken = addSpawn(TAIL_TRASKEN, spawn[0], spawn[1], spawn[2], 0, false, 0, true);
									tie_trasken.setCurrentHp(hp_tail);
								}, 5000);
							}
							startQuestTimer("spawn_rnd", 30000, tie_trasken, null);
						}
						break;
					}
					case TRASKEN:
					{
						if (_statusZone == 3)
						{
							trasken.doCast(SKILL_TRASKEN_FIRST.getSkill());
							trasken.getSpawn().stopRespawn();
							trasken.decayMe();
							
							final int[] spawn1 = Rnd_Spawn_Trasken[Rnd.get(Rnd_Spawn_Trasken.length)];
							if (SpawnTable.getInstance().getSpawns(TRASKEN) == null)
							{
								ThreadPool.schedule(() ->
								{
									trasken = addSpawn(TRASKEN, spawn1[0], spawn1[1], spawn1[2], 0, false, 0, true);
									trasken.doCast(SKILL_TRASKEN_UP.getSkill());
									trasken.setCurrentHp(hp_trasken);
								}, 10000);
							}
							startQuestTimer("spawn_rnd", 70000, trasken, null);
						}
						break;
					}
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case TIE:
			case BIG_TIE:
			{
				_killsTie.incrementAndGet();
				if ((_killsTie.get() == 27) && (_statusZone == 0))
				{
					_statusZone = 1;
					nextStage(_statusZone);
				}
				break;
			}
			case TRADJAN:
			{
				_killsTradjan.incrementAndGet();
				if ((_killsTradjan.get() == 18) && (_statusZone == 1))
				{
					_statusZone = 2;
					nextStage(_statusZone);
				}
				npc.getSpawn().startRespawn();
				break;
			}
			case TAIL_TRASKEN:
			{
				_statusZone = 3;
				nextStage(_statusZone);
				break;
			}
			case HEART_ERTHWYRM:
			{
				_zoneLair.getPlayersInside().forEach(p -> p.broadcastPacket(new ExShowScreenMessage(NpcStringId.HEART_OF_EARTH_WYRM_HAS_BEEN_DESTROYED, 5, 4000, true)));
				_zoneLair2.getPlayersInside().forEach(p -> p.broadcastPacket(new ExShowScreenMessage(NpcStringId.HEART_OF_EARTH_WYRM_HAS_BEEN_DESTROYED, 5, 4000, true)));
				cancelQuestTimer("spawn_rnd", trasken, null);
				startQuestTimer("finish", 5000, npc, killer);
				break;
			}
			case LAVRA_1:
			case LAVRA_2:
			case LAVRA_3:
			{
				npc.getSpawn().startRespawn();
				break;
			}
			case VICTIM_EARTWORMS_1:
			case VICTIM_EARTWORMS_2:
			case VICTIM_EARTWORMS_3:
			{
				npc.getSpawn().startRespawn();
				break;
			}
			case DIGISTIVE:
			{
				npc.getSpawn().startRespawn();
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		if ((zone.getId() == ZONE_ID) && creature.isPlayer())
		{
			for (int info : eventTriggers)
			{
				creature.broadcastPacket(new OnEventTrigger(info, true));
			}
			
			if (_collapseTask != null)
			{
				_collapseTask.cancel(true);
				_collapseTask = null;
			}
			_statusZone = 0;
			nextStage(_statusZone);
		}
		
		if ((zone.getId() == ZONE_ID_HEART) && creature.isPlayer())
		{
			_zoneLair2.movePlayersTo(HEART_LOCATION);
			if (_collapseTask != null)
			{
				_collapseTask.cancel(true);
				_collapseTask = null;
			}
			final int time = 540000;
			zone.getPlayersInside().forEach(temp -> temp.sendPacket(new ExSendUIEvent(temp, false, false, 540, 0, NpcStringId.REMAINING_TIME)));
			_collapseTask = ThreadPool.schedule(() -> Fail(true), time);
		}
		return super.onEnterZone(creature, zone);
	}
	
	@Override
	public String onExitZone(Creature creature, ZoneType zone)
	{
		if ((zone.getId() == ZONE_ID_HEART) && zone.getPlayersInside().isEmpty())
		{
			_collapseTask = ThreadPool.schedule(() ->
			{
				Fail(true);
				for (int info : eventTriggers)
				{
					creature.broadcastPacket(new OnEventTrigger(info, false));
				}
			}, 900000);
		}
		return super.onExitZone(creature, zone);
	}
	
	private void nextStage(int taskId)
	{
		switch (taskId)
		{
			case 0:
			{
				for (Location loc : Spawn_Tie)
				{
					addSpawn(TIE, loc, false, 0, true);
				}
				
				for (Location loc : Spawn_Big_Tie)
				{
					addSpawn(BIG_TIE, loc, false, 0, true);
				}
				
				addSpawn(HEART_ERTHWYRM, Spawn_Heart, false, 0, true);
				
				for (Location loc : Spawn_Victim_1)
				{
					addSpawn(VICTIM_EARTWORMS_1, loc, false, 0, true);
				}
				
				for (Location loc : Spawn_Victim_2)
				{
					addSpawn(VICTIM_EARTWORMS_2, loc, false, 0, true);
				}
				
				for (Location loc : Spawn_Victim_3)
				{
					addSpawn(VICTIM_EARTWORMS_3, loc, false, 0, true);
				}
				
				for (Location loc : Spawn_Digestive)
				{
					addSpawn(DIGISTIVE, loc, false, 0, true);
				}
				break;
			}
			case 1:
			{
				for (Location loc : Spawn_Tradjan)
				{
					addSpawn(TRADJAN, loc, false, 0, true);
				}
				break;
			}
			case 2:
			{
				tie_trasken = addSpawn(TAIL_TRASKEN, CENTER_LOCATION, false, 0, true);
				hp_tail = tie_trasken.getCurrentHp();
				startQuestTimer("spawn_rnd", 3000000, tie_trasken, null);
				break;
			}
			case 3:
			{
				cancelQuestTimer("spawn_rnd", tie_trasken, null);
				
				trasken = addSpawn(TRASKEN, CENTER_LOCATION, false, 0, true);
				trasken.doCast(SKILL_TRASKEN_UP.getSkill());
				hp_trasken = trasken.getCurrentHp();
				
				startQuestTimer("spawn_rnd", 9000000, trasken, null);
				for (Location loc : Spawn_Larva_1)
				{
					addSpawn(LAVRA_1, loc, false, 0, true);
				}
				
				for (Location loc : Spawn_Larva_2)
				{
					addSpawn(LAVRA_2, loc, false, 0, true);
				}
				
				for (Location loc : Spawn_Larva_3)
				{
					addSpawn(LAVRA_3, loc, false, 0, true);
				}
				break;
			}
		}
	}
	
	private void TraskenStay(Creature creature)
	{
		creature.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_EARTH_WYRM_HAS_LOST_CONSCIOUSNESS, 5, 4600, true));
		creature.doCast(SKILL_TRASKEN_BUFF.getSkill()); // 12 sec combo
		if (playersToEnter == _zoneLair2.getPlayersInside().size())
		{
			final BuffInfo traskenBuff = creature.getEffectList().getBuffInfoBySkillId(SKILL_TRASKEN_BUFF.getSkillId());
			if (traskenBuff != null)
			{
				creature.getEffectList().stopSkillEffects(true, traskenBuff.getSkill());
			}
		}
		ThreadPool.schedule(() ->
		{
			creature.broadcastPacket(new ExShowScreenMessage(NpcStringId.YOU_VE_EXCEEDED_THE_MAXIMUM_NUMBER_OF_PERSONNEL, 5, 24000, true));
			creature.doCast(SKILL_TRASKEN_SLEEP.getSkill());
		}, 4050);
	}
	
	public static void main(String[] args)
	{
		new Trasken();
	}
}
