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
package ai.bosses.Lindvior;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.datatables.SpawnTable;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.instancemanager.GrandBossManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.FriendlyNpcInstance;
import org.l2jbr.gameserver.model.actor.instance.GrandBossInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.Id;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureDamageReceived;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.NoSummonFriendZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.Earthquake;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;
import org.l2jbr.gameserver.network.serverpackets.SocialAction;
import org.l2jbr.gameserver.network.serverpackets.SpecialCamera;

import ai.AbstractNpcAI;

/**
 * Lindvior Boss
 * @author Gigi
 * @date 2017-07-11 - [17:42:53]
 *       <p>
 * @VIDEO - https://www.youtube.com/watch?v=VknjOjRO9Cw
 */
public class Lindvior extends AbstractNpcAI
{
	// Monsters
	private static final int LINDVIOR_FAKE = 19423;
	private static final int LINDVIOR_GROUND = 25899;
	private static final int LINDVIOR_RAID = 29240;
	private static final int LINDVIOR_FLY = 19424;
	private static final int NPC_GENERATOR = 19477;
	private static final int GENERATOR_GUARD = 19479;
	private static final int NPC_ATTACKER_GENERATORS = 25897;
	private static final int NPC_ATTACKER_GENERATORS_1 = 25895;
	private static final int LYN_DRACO_ATTACKER_GENERATORS = 29241;
	private static final int NPC_ATTACKER_SMALL_VORTEX = 25898;
	private static final int NPC_ATTACKER_BIG_VORTEX = 19427;
	private static final int INVISIBLE = 8572;
	private static final int LIONEL_HUNTER = 33886;
	private static final int LINDVIOR_CAMERA = 19428;
	// Zone
	private static final int ZONE_ID = 12107;
	static final Location CENTER_LOCATION = new Location(46424, -26200, -1400);
	// Skills
	private static final SkillHolder SKILL_RECHARGE_POSIBLE = new SkillHolder(15605, 1);
	private static final SkillHolder RECHARGE = new SkillHolder(15606, 1);
	private static final SkillHolder SKILL_REFLECT = new SkillHolder(15592, 1);
	// Item
	private static final int LINDVIORS_SCALE = 37495;
	// Trigers
	private static final int FIRST_STAGE_EVENT_TRIGGER = 21170112;
	private static final int SECOND_STAGE_EVENT_TRIGGER = 21170100;
	private static final int ALL_GENERATORS_CONNECTED_EFFECT = 21170110;
	private static final int RED_ZONE_EFFECT = 21170120;
	// Status
	private static final int ALIVE = 0;
	private static final int FIGHTING = 2;
	private static final int DEAD = 3;
	// Tasks
	protected ScheduledFuture<?> _socialTask;
	protected ScheduledFuture<?> _mobsSpawnTask;
	protected ScheduledFuture<?> _collapseTask;
	protected ScheduledFuture<?> _announceTask;
	protected ScheduledFuture<?> _announceProtect;
	protected ScheduledFuture<?> _skillCastTask;
	protected ScheduledFuture<?> _LynDracoTask;
	protected ScheduledFuture<?> _smallVortexesTask;
	protected ScheduledFuture<?> _bigVortexesTask;
	protected NoSummonFriendZone _zoneLair;
	protected GrandBossInstance _lindvior = null;
	protected Npc _lindvior2 = null;
	protected Npc _dummyLindvior;
	protected Npc _vortex = null;
	protected Npc _lionel = null;
	protected List<Npc> _guardSpawn = new ArrayList<>();
	protected List<FriendlyNpcInstance> _generatorSpawn = new ArrayList<>();
	protected List<Npc> _monsterSpawn = new ArrayList<>();
	protected List<Npc> _LinDracoSpawn = new ArrayList<>();
	protected int _activeMask = 0;
	protected int _chargedMask = 0;
	protected int _status = 0;
	
	private static final Location[] CONTROL_GENERATOR_SPAWNS =
	{
		new Location(45288, -30360, -1432, 0),
		new Location(48486, -27175, -1432, 0),
		new Location(45272, -23976, -1432, 0),
		new Location(42088, -27160, -1432, 0)
	};
	private static final Location[] SCHEME_GENERATOR_SPAWNS =
	{
		new Location(48440, -26824, -1438, 0),
		new Location(48392, -27448, -1438, 0),
		new Location(42136, -27480, -1438, 0),
		new Location(42136, -26840, -1438, 0),
		new Location(44936, -24024, -1438, 0),
		new Location(45592, -24008, -1438, 0),
		new Location(45608, -30312, -1438, 0),
		new Location(44984, -30360, -1438, 0)
	};
	private static final Location[] ATTACKER_GENERATOR_SPAWNS =
	{
		new Location(44863, -24272, -1413, 33713),
		new Location(45675, -24272, -1413, 33713),
		new Location(45675, -30057, -1413, 64987),
		new Location(44863, -30057, -1413, 64987),
		new Location(42350, -27563, -1413, 46871),
		new Location(42350, -26809, -1413, 46871),
		new Location(48220, -26809, -1413, 16383),
		new Location(48220, -27563, -1413, 16383)
	};
	private static final Location[] LYN_DRACO_SPAWNS =
	{
		new Location(45300, -28402, -1400, 48845),
		new Location(46379, -27178, -1400, 1154),
		new Location(45292, -26043, -1400, 13027),
		new Location(44215, -27172, -1400, 33966)
	};
	private static final Location[] ATTACKER_SMALL_VORTEX_SPAWNS = new Location[]
	{
		new Location(46256, -30159, -1430, 57430),
		new Location(45155, -29987, -1430, 14860),
		new Location(46219, -27704, -1430, 1744),
		new Location(46135, -28995, -1430, 43626),
		new Location(43973, -28265, -1430, 16516),
		new Location(46782, -29065, -1430, 63368),
		new Location(47214, -29836, -1430, 46966),
		new Location(44754, -29120, -1430, 56118),
		new Location(47089, -28198, -1430, 8537),
		new Location(44992, -28152, -1430, 11592),
		new Location(44737, -24885, -1430, 3146),
		new Location(46096, -24976, -1430, 49650),
		new Location(46972, -25911, -1430, 62925),
		new Location(46977, -27136, -1430, 2150),
		new Location(42889, -24767, -1430, 10246),
		new Location(47299, -25256, -1430, 1453),
		new Location(44204, -25026, -1430, 39225),
		new Location(42875, -28035, -1430, 34755),
		new Location(41963, -26031, -1430, 18822),
		new Location(43171, -25942, -1430, 44279),
		new Location(41874, -27174, -1430, 56030),
		new Location(44983, -26082, -1430, 7042),
		new Location(46145, -26804, -1430, 24394),
		new Location(46148, -26019, -1430, 34151),
		new Location(45161, -24275, -1430, 39262),
		new Location(47288, -24141, -1430, 21644),
		new Location(43722, -26174, -1430, 11001),
		new Location(44942, -27169, -1430, 39703),
		new Location(46105, -24170, -1430, 28224),
		new Location(49084, -27206, -1430, 41996),
		new Location(48159, -27091, -1430, 62682),
		new Location(48094, -28789, -1430, 49189),
		new Location(48958, -27844, -1430, 59758),
		new Location(43828, -23981, -1430, 10994),
		new Location(48165, -25777, -1430, 53084),
		new Location(48267, -28086, -1430, 9266),
		new Location(43268, -28981, -1430, 23736),
		new Location(44155, -29821, -1430, 39281),
		new Location(43991, -29275, -1430, 27277),
		new Location(44057, -27133, -1430, 64484),
		new Location(43257, -26764, -1430, 14161),
		new Location(42300, -25194, -1430, 7811),
		new Location(42091, -27981, -1430, 30628),
		new Location(47854, -24735, -1430, 14438)
	};
	// @formatter:off
	private static final int[][] GENERATOR_TRIGERS =
	{
		{21170102, 21170103},
		{21170104, 21170105},
		{21170106, 21170107},
		{21170108, 21170109}
	};
	protected final int _chargedValues[] = new int[] {0, 0, 0, 0};
	private static final int[] LINDVIOR_SERVITOR = {25895, 25896, 25897, 29242, 29241, 29243};
	// @formatter:on
	private static final NpcStringId[] GUARD_MSG =
	{
		NpcStringId.ACTIVATE_THE_GENERATOR_HURRY,
		NpcStringId.WE_WILL_HOLD_OFF_LINDVIOR_S_MINIONS,
	};
	private static final NpcStringId[] GUARD_MSG_1 =
	{
		NpcStringId.HOLD_ONTO_THE_GENERATOR_TO_ACTIVATE_THE_CHARGE_SKILL,
		NpcStringId.THE_GENERATOR_IS_CONNECTED_TO_THE_CANNON,
	};
	
	public Lindvior()
	{
		super();
		addAttackId(LINDVIOR_GROUND, LINDVIOR_FLY, LINDVIOR_RAID);
		addEnterZoneId(ZONE_ID);
		addExitZoneId(ZONE_ID);
		addKillId(LINDVIOR_RAID, NPC_GENERATOR);
		addSkillSeeId(NPC_GENERATOR);
		addSpawnId(NPC_ATTACKER_GENERATORS, NPC_ATTACKER_GENERATORS_1, LYN_DRACO_ATTACKER_GENERATORS, GENERATOR_GUARD, NPC_GENERATOR);
		addFirstTalkId(NPC_GENERATOR);
		addSeeCreatureId(INVISIBLE);
		_zoneLair = ZoneManager.getInstance().getZoneById(ZONE_ID, NoSummonFriendZone.class);
		// Unlock
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(LINDVIOR_RAID);
		final int status = GrandBossManager.getInstance().getBossStatus(LINDVIOR_RAID);
		if (status == DEAD)
		{
			final long time = info.getLong("respawn_time") - System.currentTimeMillis();
			if (time > 0)
			{
				startQuestTimer("unlock_lindvior", time, null, null);
			}
			else
			{
				GrandBossManager.getInstance().setBossStatus(LINDVIOR_RAID, ALIVE);
			}
		}
		else if (status != ALIVE)
		{
			GrandBossManager.getInstance().setBossStatus(LINDVIOR_RAID, ALIVE);
		}
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		// Anti BUGGERS
		if (!_zoneLair.isInsideZone(attacker))
		{
			attacker.doDie(null);
			LOGGER.warning(getName() + ": Character: " + attacker.getName() + " attacked: " + npc.getName() + " out of the boss zone!");
		}
		if (!_zoneLair.isInsideZone(npc))
		{
			npc.teleToLocation(CENTER_LOCATION, true);
			LOGGER.warning(getName() + ": Character: " + attacker.getName() + " attacked: " + npc.getName() + " wich is out of the boss zone!");
		}
		
		double percent = ((npc.getCurrentHp() - damage) / npc.getMaxHp()) * 100;
		if ((percent <= 80) && (_status == 0))
		{
			_zoneLair.broadcastPacket(new OnEventTrigger(RED_ZONE_EFFECT, true));
			_zoneLair.getPlayersInside().stream().forEach(p ->
			{
				startQuestTimer("stop_red_zone", 10000, _lindvior, p);
				p.broadcastPacket(new ExShowScreenMessage(NpcStringId.A_FEARSOME_POWER_EMANATES_FROM_LINDVIOR, ExShowScreenMessage.TOP_CENTER, 2000, true));
			});
			_lindvior.broadcastPacket(new SocialAction(_lindvior.getObjectId(), 1));
			_lindvior.setIsDead(true);
			_lindvior.deleteMe();
			
			_lindvior = (GrandBossInstance) addSpawn(LINDVIOR_FLY, _lindvior.getLocation(), false, 0, false);
			_lindvior.setCurrentHp(_lindvior.getMaxHp() * 0.8);
			for (Location loc : ATTACKER_SMALL_VORTEX_SPAWNS)
			{
				addSpawn(NPC_ATTACKER_SMALL_VORTEX, loc, true);
				addSpawn(LINDVIOR_SERVITOR[0], loc, true);
			}
			_status = 1;
		}
		else if ((percent <= 75) && (_status == 1))
		{
			_bigVortexesTask = ThreadPool.schedule(() -> spawnServitor(1, 300, _lindvior.getLocation(), NPC_ATTACKER_BIG_VORTEX), 1000);
			for (Location loc : ATTACKER_SMALL_VORTEX_SPAWNS)
			{
				addSpawn(LINDVIOR_SERVITOR[1], loc, true);
			}
			_zoneLair.getPlayersInside().stream().forEach(p -> p.broadcastPacket(new ExShowScreenMessage(NpcStringId.A_GIGANTIC_WHIRLWIND_HAS_APPEARED, ExShowScreenMessage.TOP_CENTER, 2000, true)));
			_status = 2;
		}
		else if ((percent <= 60) && (_status == 2))
		{
			_lindvior.broadcastPacket(new SocialAction(_lindvior.getObjectId(), 1));
			_lindvior.setIsDead(true);
			_lindvior.deleteMe();
			
			_lindvior = (GrandBossInstance) addSpawn(LINDVIOR_GROUND, _lindvior.getLocation(), false, 0, false);
			_lindvior.setCurrentHp(_lindvior.getMaxHp() * 0.6);
			spawnServitor(10, 2000, _lindvior.getLocation(), LINDVIOR_SERVITOR);
			for (Location loc : ATTACKER_SMALL_VORTEX_SPAWNS)
			{
				addSpawn(LINDVIOR_SERVITOR[2], loc, true);
			}
			_skillCastTask = ThreadPool.scheduleAtFixedRate(() -> _lindvior.doCast(SKILL_REFLECT.getSkill()), 5000, 80000);
			_status = 3;
		}
		else if ((percent <= 40) && (_status == 3))
		{
			_lindvior.broadcastPacket(new SocialAction(_lindvior.getObjectId(), 1));
			_lindvior.setIsDead(true);
			_lindvior.deleteMe();
			
			_lindvior = (GrandBossInstance) addSpawn(LINDVIOR_FLY, _lindvior.getLocation(), false, 0, false);
			_lindvior.setCurrentHp(_lindvior.getMaxHp() * 0.4);
			
			if (SpawnTable.getInstance().getSpawns(NPC_ATTACKER_BIG_VORTEX) != null)
			{
				if ((_vortex != null) && (_vortex.getId() == NPC_ATTACKER_SMALL_VORTEX))
				{
					_vortex.getSpawn().stopRespawn();
					_vortex.deleteMe();
				}
			}
			_status = 4;
		}
		else if ((percent <= 35) && (_status == 4))
		{
			_smallVortexesTask = ThreadPool.scheduleAtFixedRate(() ->
			{
				for (Location loc : ATTACKER_SMALL_VORTEX_SPAWNS)
				{
					addSpawn(NPC_ATTACKER_SMALL_VORTEX, loc, true, 60000);
					addSpawn(LINDVIOR_SERVITOR[3], loc, true);
				}
				
			}, 20000, 60000);
			_status = 5;
		}
		else if ((percent <= 20) && (_status == 5))
		{
			if (_smallVortexesTask != null)
			{
				_smallVortexesTask.cancel(true);
				_smallVortexesTask = null;
			}
			_zoneLair.broadcastPacket(new ExShowScreenMessage(NpcStringId.LINDVIOR_HAS_LANDED, 2, 5000, true));
			_lindvior.broadcastPacket(new SocialAction(_lindvior.getObjectId(), 1));
			_lindvior.setIsDead(true);
			_lindvior.deleteMe();
			_lindvior = (GrandBossInstance) addSpawn(LINDVIOR_RAID, _lindvior.getLocation(), false, 0, false);
			_lindvior.setCurrentHp(_lindvior.getMaxHp() * 0.2);
			_bigVortexesTask = ThreadPool.schedule(() -> spawnServitor(1, 300, _lindvior.getLocation(), NPC_ATTACKER_BIG_VORTEX), 1000);
			for (Location loc : ATTACKER_SMALL_VORTEX_SPAWNS)
			{
				addSpawn(NPC_ATTACKER_SMALL_VORTEX, loc, true);
				addSpawn(LINDVIOR_SERVITOR[4], loc, true);
				addSpawn(LINDVIOR_SERVITOR[3], loc, true);
			}
			_collapseTask = ThreadPool.schedule(Lindvior.this::Clean, 600000);
			_status = 6;
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	protected void Clean()
	{
		_status = 0;
		if (_socialTask != null)
		{
			_socialTask.cancel(false);
			_socialTask = null;
		}
		if (_announceTask != null)
		{
			_announceTask.cancel(false);
			_announceTask = null;
		}
		if (_announceProtect != null)
		{
			_announceProtect.cancel(false);
			_announceProtect = null;
		}
		if (_skillCastTask != null)
		{
			_skillCastTask.cancel(false);
			_skillCastTask = null;
		}
		if (_LynDracoTask != null)
		{
			_LynDracoTask.cancel(false);
			_LynDracoTask = null;
		}
		if (_mobsSpawnTask != null)
		{
			_mobsSpawnTask.cancel(false);
			_mobsSpawnTask = null;
		}
		if (_collapseTask != null)
		{
			_collapseTask.cancel(false);
			_collapseTask = null;
		}
		if (_bigVortexesTask != null)
		{
			_bigVortexesTask.cancel(false);
			_bigVortexesTask = null;
		}
		if (_smallVortexesTask != null)
		{
			_smallVortexesTask.cancel(false);
			_smallVortexesTask = null;
		}
		if (_lionel != null)
		{
			_lionel.deleteMe();
		}
		_zoneLair.getCharactersInside().forEach(mob ->
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
		_zoneLair.broadcastPacket(new OnEventTrigger(FIRST_STAGE_EVENT_TRIGGER, false));
		cancelQuestTimers("attack_generator");
		if (GrandBossManager.getInstance().getBossStatus(LINDVIOR_RAID) != 3)
		{
			GrandBossManager.getInstance().setBossStatus(LINDVIOR_RAID, ALIVE);
		}
	}
	
	@Override
	public synchronized String onSkillSee(Npc npc, PlayerInstance caster, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		if ((skill.getId() == 15606) && (npc.getId() == NPC_GENERATOR))
		{
			synchronized (_chargedValues)
			{
				int index = npc.getScriptValue();
				if (!hasFlag(_chargedMask, 1 << index))
				{
					_chargedValues[index] += caster.isGM() ? (30 / 4) + 2 : (1 / 4) + 2;
					_chargedValues[index] = Math.min(_chargedValues[index], 120);
					
					World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 3000, player ->
					{
						player.sendPacket(new ExShowScreenMessage(NpcStringId.S1_HAS_CHARGED_THE_CANNON, ExShowScreenMessage.TOP_CENTER, 10000, true, caster.getName()));
						player.sendPacket(new ExSendUIEvent(player, ExSendUIEvent.TYPE_NORNIL, _chargedValues[index], 120, NpcStringId.CHARGING));
					});
					if (_chargedValues[index] >= 120)
					{
						_chargedMask |= 1 << index;
						_chargedValues[index] = 0;
					}
					
					if (hasFlag(_chargedMask, 0xf))
					{
						nextStage(3);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case NPC_ATTACKER_GENERATORS:
			case NPC_ATTACKER_GENERATORS_1:
			{
				World.getInstance().forEachVisibleObjectInRange(npc, FriendlyNpcInstance.class, 800, cha ->
				{
					
					if (cha.getId() == GENERATOR_GUARD)
					{
						npc.reduceCurrentHp(1, cha, null);
						cha.reduceCurrentHp(1, npc, null);
					}
					if (cha.getId() == NPC_GENERATOR)
					{
						((Attackable) npc).addDamageHate(cha, 500, 98);
					}
				});
				break;
			}
			case LYN_DRACO_ATTACKER_GENERATORS:
			{
				((Attackable) npc).setCanReturnToSpawnPoint(false);
				startQuestTimer("attack_generator", 10000, npc, null, true);
				break;
			}
			case GENERATOR_GUARD:
			{
				getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
				((FriendlyNpcInstance) npc).setIsInvul(true);
				break;
			}
			case NPC_GENERATOR:
			{
				npc.disableCoreAI(true);
				npc.setDisplayEffect(1);
				npc.setRandomWalking(false);
				npc.setIsInvul(true); // Can't get damage now
				_activeMask = 0;
				_chargedMask = 0;
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		setLindviorSpawnTask();
		npc.getSpawn().stopRespawn();
		npc.deleteMe();
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	private void nextStage(int _taskId)
	{
		switch (_taskId)
		{
			case 1: // Spawn Generators
			{
				_zoneLair.broadcastPacket(new OnEventTrigger(FIRST_STAGE_EVENT_TRIGGER, true));
				int i = 0;
				FriendlyNpcInstance guard;
				for (Location loc : CONTROL_GENERATOR_SPAWNS)
				{
					guard = (FriendlyNpcInstance) addSpawn(NPC_GENERATOR, loc, true);
					guard.setDisplayEffect(0x01);
					guard.setScriptValue(i++);
					_generatorSpawn.add(guard);
				}
				
				Npc npc;
				for (Location loc : SCHEME_GENERATOR_SPAWNS)
				{
					npc = addSpawn(GENERATOR_GUARD, loc, true);
					npc.setRandomWalking(false);
					_guardSpawn.add(npc);
				}
				
				_mobsSpawnTask = ThreadPool.scheduleAtFixedRate(() ->
				{
					for (Location loc : ATTACKER_GENERATOR_SPAWNS)
					{
						if (getRandom(10) <= 5)
						{
							_monsterSpawn.add(addSpawn(NPC_ATTACKER_GENERATORS, loc, true));
						}
						else
						{
							_monsterSpawn.add(addSpawn(NPC_ATTACKER_GENERATORS_1, loc, true));
						}
					}
				}, 30000, 80000);
				
				_dummyLindvior = addSpawn(LINDVIOR_CAMERA, 45259, -27115, -638, 41325, false, 0, false);
				_announceTask = ThreadPool.scheduleAtFixedRate(() -> _zoneLair.getPlayersInside().forEach(player -> player.sendPacket(new ExShowScreenMessage(NpcStringId.YOU_MUST_ACTIVATE_THE_4_GENERATORS, ExShowScreenMessage.TOP_CENTER, 7000, true))), 10000, 20000);
				break;
			}
			case 2: // After activation of 4 generators, we wait to be charged
			{
				if (_announceTask != null)
				{
					_announceTask.cancel(true);
					_announceTask = null;
				}
				_generatorSpawn.forEach(npc ->
				{
					npc.setDisplayEffect(1);
					npc.setIsInvul(false);
					npc.broadcastInfo();
				});
				
				_zoneLair.getPlayersInside().forEach(player ->
				{
					player.sendPacket(new OnEventTrigger(FIRST_STAGE_EVENT_TRIGGER, false));
					cancelQuestTimers("NPC_SHOUT");
					_guardSpawn.stream().forEach(guard ->
					{
						guard.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ALL_4_GENERATORS_MUST_BE_ACTIVATED);
					});
					_announceProtect = ThreadPool.scheduleAtFixedRate(() -> _zoneLair.getPlayersInside().forEach(p -> player.sendPacket(new ExShowScreenMessage(NpcStringId.PROTECT_THE_GENERATOR, ExShowScreenMessage.TOP_CENTER, 7000, true))), 10000, 18000);
					_zoneLair.broadcastPacket(new SpecialCamera(_dummyLindvior, 3300, 200, 20, 11000, 10500, 0, 8, 1, 0, 0));
					_generatorSpawn.forEach(npc -> npc.sendInfo(player));
					startQuestTimer("show_movie", 13000, null, null);
					startQuestTimer("start_charge", 35000, null, null);
					startQuestTimer("show_shield_animation", 2000, null, null);
				});
				break;
			}
			case 3: // After charging all the generators
			{
				_zoneLair.broadcastPacket(new OnEventTrigger(ALL_GENERATORS_CONNECTED_EFFECT, true));
				if (_announceTask != null)
				{
					_announceTask.cancel(true);
					_announceTask = null;
				}
				if (_announceProtect != null)
				{
					_announceProtect.cancel(false);
					_announceProtect = null;
				}
				if (_skillCastTask != null)
				{
					_skillCastTask.cancel(true);
					_skillCastTask = null;
				}
				if (_LynDracoTask != null)
				{
					_LynDracoTask.cancel(true);
					_LynDracoTask = null;
				}
				if (_mobsSpawnTask != null)
				{
					_mobsSpawnTask.cancel(true);
					_mobsSpawnTask = null;
				}
				_monsterSpawn.forEach(npc ->
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
				});
				_LinDracoSpawn.forEach(npc ->
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
				});
				_generatorSpawn.forEach(npc ->
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
				});
				_guardSpawn.forEach(npc ->
				{
					if (npc != null)
					{
						npc.deleteMe();
					}
				});
				cancelQuestTimers("attack_generator");
				_lindvior2.setIsDead(true);
				_lindvior2.deleteMe();
				GrandBossManager.getInstance().setBossStatus(LINDVIOR_RAID, FIGHTING);
				_lionel = addSpawn(LIONEL_HUNTER, 42630, -48231, -792, 855, false, 0, false);
				
				_lindvior = (GrandBossInstance) addSpawn(LINDVIOR_GROUND, CENTER_LOCATION, false, 0, true);
				_zoneLair.broadcastPacket(new SocialAction(_lindvior.getObjectId(), 1));
				_zoneLair.getPlayersInside().forEach(_lindvior::sendInfo);
				_zoneLair.broadcastPacket(new ExShowScreenMessage(NpcStringId.LINDVIOR_HAS_FALLEN_FROM_THE_SKY, ExShowScreenMessage.TOP_CENTER, 7000));
				_mobsSpawnTask = ThreadPool.scheduleAtFixedRate(() -> spawnServitor(2, 1000, _lindvior.getLocation(), LINDVIOR_SERVITOR), 60000, 180000);
				break;
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "unlock_lindvior":
			{
				GrandBossManager.getInstance().setBossStatus(LINDVIOR_RAID, ALIVE);
				break;
			}
			case "stage_1_activate_generator":
			{
				int index = npc.getScriptValue();
				if (!hasFlag(_activeMask, 1 << index))
				{
					_activeMask |= 1 << index;
					npc.setDisplayEffect(0x02);
					sendEventTrigger(true, GENERATOR_TRIGERS[index]);
					_zoneLair.getPlayersInside().stream().forEach(p -> p.broadcastPacket(new Earthquake(p.getX(), p.getY(), p.getZ(), 20, 10)));
					if (hasFlag(_activeMask, 0xf))
					{
						nextStage(2);
					}
				}
				break;
			}
			case "show_shield_animation": // zone broadcast shield event triger
			{
				_zoneLair.getPlayersInside().forEach(p ->
				{
					p.sendPacket(new OnEventTrigger(SECOND_STAGE_EVENT_TRIGGER, true));
				});
				_guardSpawn.stream().forEach(guard ->
				{
					guard.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_GENERATOR_IS_CONNECTED_TO_THE_CANNON);
				});
				break;
			}
			case "show_movie": // zone broadcast Lindvior scene movie
			{
				_zoneLair.getPlayersInside().forEach(p ->
				{
					playMovie(p, Movie.SC_LIND_OPENING);
				});
				_dummyLindvior.deleteMe();
				_lindvior2 = addSpawn(LINDVIOR_FAKE, CENTER_LOCATION, false, 0, false);
				_lindvior2.setTargetable(false);
				_announceTask = ThreadPool.scheduleAtFixedRate(() -> _zoneLair.getPlayersInside().forEach(p -> p.sendPacket(new ExShowScreenMessage(NpcStringId.CHARGE_THE_CANNON_USING_THE_GENERATOR, ExShowScreenMessage.TOP_CENTER, 7000, true))), 40000, 20000);
				break;
			}
			case "start_charge":
			{
				_skillCastTask = ThreadPool.scheduleAtFixedRate(() -> _generatorSpawn.forEach(generators ->
				{
					int index = generators.getScriptValue();
					if (!generators.isCastingNow() && (generators.getEffectList().getBuffInfoBySkillId(SKILL_RECHARGE_POSIBLE.getSkillId()) == null) && !hasFlag(_chargedMask, 1 << index))
					{
						// TODO Need core implemented combo skill packet.
						// On this moment player automatic charge generator if distance generator and player <= 900
						generators.doCast(SKILL_RECHARGE_POSIBLE.getSkill());
						World.getInstance().forEachVisibleObjectInRange(generators, PlayerInstance.class, 900, p ->
						{
							p.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
							p.setTarget(generators);
							p.doCast(RECHARGE.getSkill());
						});
						_guardSpawn.stream().forEach(guard ->
						{
							guard.setTarget(generators);
							guard.doCast(RECHARGE.getSkill());
							guard.setIsInvul(false);
							if (!guard.isDead())
							{
								guard.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(GUARD_MSG_1));
							}
						});
					}
				}), 10000, 20000);
				_LynDracoTask = ThreadPool.scheduleAtFixedRate(() ->
				{
					for (Location loc : LYN_DRACO_SPAWNS)
					{
						_LinDracoSpawn.add(addSpawn(LYN_DRACO_ATTACKER_GENERATORS, loc, true));
					}
				}, 20000, 60000);
				break;
			}
			case "stop_red_zone":
			{
				_zoneLair.broadcastPacket(new OnEventTrigger(RED_ZONE_EFFECT, false));
				break;
			}
			case "attack_generator":
			{
				if ((npc != null) && !npc.isDead())
				{
					World.getInstance().forEachVisibleObjectInRange(npc, FriendlyNpcInstance.class, 3000, generator ->
					{
						if (generator.getId() == NPC_GENERATOR)
						{
							npc.setTarget(generator);
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, generator.getLocation());
							if (npc.calculateDistance3D(generator) < 500)
							{
								npc.reduceCurrentHp(1, generator, null);
								generator.reduceCurrentHp(1, npc, null);
							}
						}
					});
				}
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		if (npc.getId() == LINDVIOR_RAID)
		{
			_zoneLair.broadcastPacket(new ExShowScreenMessage(NpcStringId.HONORABLE_WARRIORS_HAVE_DRIVEN_OFF_LINDVIOR_THE_EVIL_WIND_DRAGON, ExShowScreenMessage.TOP_CENTER, 10000, true));
			if (_mobsSpawnTask != null)
			{
				_mobsSpawnTask.cancel(true);
				_mobsSpawnTask = null;
			}
			_zoneLair.getCharactersInside().stream().filter(Creature::isNpc).forEach(mob -> mob.deleteMe());
			ThreadPool.schedule(() -> npc.decayMe(), 10000);
			_zoneLair.broadcastPacket(new OnEventTrigger(SECOND_STAGE_EVENT_TRIGGER, false));
			_zoneLair.broadcastPacket(new OnEventTrigger(FIRST_STAGE_EVENT_TRIGGER, true));
			_lionel.deleteMe();
			
			GrandBossManager.getInstance().setBossStatus(LINDVIOR_RAID, DEAD);
			final long respawnTime = (Config.LINDVIOR_SPAWN_INTERVAL + getRandom(-Config.LINDVIOR_SPAWN_RANDOM, Config.LINDVIOR_SPAWN_RANDOM)) * 3600000;
			final StatsSet info = GrandBossManager.getInstance().getStatsSet(LINDVIOR_RAID);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatsSet(LINDVIOR_RAID, info);
			startQuestTimer("unlock_lindvior", respawnTime, null, null);
		}
		else if (npc.getId() == NPC_GENERATOR)
		{
			_zoneLair.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_GENERATOR_HAS_BEEN_DESTROYED, ExShowScreenMessage.TOP_CENTER, 5000, true));
			Clean();
			_collapseTask = ThreadPool.schedule(() -> Fail(false), 20000);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		if (npc.getId() == NPC_GENERATOR)
		{
			return npc.getDisplayEffect() == 1 ? "19477.html" : "19477-01.html";
		}
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		if (zone.getId() == ZONE_ID)
		{
			if (_collapseTask != null)
			{
				_collapseTask.cancel(true);
				_collapseTask = null;
			}
		}
		return super.onEnterZone(creature, zone);
	}
	
	@Override
	public String onExitZone(Creature creature, ZoneType zone)
	{
		if (zone.getId() == ZONE_ID)
		{
			if (zone.getPlayersInside().isEmpty())
			{
				_collapseTask = ThreadPool.schedule(() -> Fail(true), 900000);
			}
		}
		return super.onExitZone(creature, zone);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		if (event.equals("NPC_SHOUT"))
		{
			if ((npc != null) && !npc.isDead())
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(GUARD_MSG));
				getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
			}
		}
	}
	
	private void sendEventTrigger(boolean status, int... triggers)
	{
		IClientOutgoingPacket[] pakets = new IClientOutgoingPacket[triggers.length];
		for (int i = 0; i < triggers.length; i++)
		{
			pakets[i] = new OnEventTrigger(triggers[i], status);
		}
		for (IClientOutgoingPacket packet : pakets)
		{
			_zoneLair.broadcastPacket(packet);
		}
	}
	
	private void spawnServitor(int count, int radius, Location loc, int... npcIds)
	{
		int x = loc.getX();
		int y = loc.getY();
		if (radius > 0)
		{
			x += Rnd.get(-radius, radius);
			y += Rnd.get(-radius, radius);
		}
		
		for (int i = 0; i < count; i++)
		{
			_monsterSpawn.add(addSpawn(getRandomEntry(npcIds), x, y, loc.getZ(), loc.getHeading(), true, 0, true));
		}
	}
	
	private static boolean hasFlag(int val, int flag)
	{
		return (val & flag) == flag;
	}
	
	public void setLindviorSpawnTask()
	{
		synchronized (this)
		{
			if (_socialTask == null)
			{
				_socialTask = ThreadPool.schedule(() -> nextStage(1), 3000);
			}
		}
	}
	
	@RegisterEvent(EventType.ON_CREATURE_DAMAGE_RECEIVED)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(LINDVIOR_FLY)
	@Id(LINDVIOR_RAID)
	@Id(LINDVIOR_GROUND)
	public void onCreatureDamageReceived(OnCreatureDamageReceived event)
	{
		_zoneLair.getPlayersInside().stream().forEach(p ->
		{
			switch (_status)
			{
				case 0:
				case 1:
				{
					giveItemRandomly(p, null, LINDVIORS_SCALE, 1, -1, 0.015, true);
					break;
				}
				case 2:
				{
					giveItemRandomly(p, null, LINDVIORS_SCALE, 2, -1, 0.015, true);
					break;
				}
				case 3:
				case 4:
				{
					giveItemRandomly(p, null, LINDVIORS_SCALE, 3, -1, 0.015, true);
					break;
				}
				case 5:
				{
					giveItemRandomly(p, null, LINDVIORS_SCALE, 4, -1, 0.015, true);
					break;
				}
			}
		});
	}
	
	public static void main(String[] args)
	{
		new Lindvior();
	}
}