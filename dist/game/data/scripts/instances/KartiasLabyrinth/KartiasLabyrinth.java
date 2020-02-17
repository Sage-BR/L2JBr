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
package instances.KartiasLabyrinth;

import java.util.List;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.InstanceType;
import org.l2jbr.gameserver.instancemanager.WalkingManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureDeath;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;

/**
 * Kartia Labyrinth instance zone.
 * @author flanagak
 */
public class KartiasLabyrinth extends AbstractInstance
{
	// NPCs
	private static final int KARTIA_RESEARCHER = 33647;
	private static final int BOZ_ENERGY = 18830;
	private static final int[] ADOLPH =
	{
		33608,
		33619,
		33630,
	};
	private static final int[] BARTON =
	{
		33610,
		33621,
		33632,
	};
	private static final int[] ELISE =
	{
		33616,
		33627,
		33638,
	};
	private static final int[] ELIYAH =
	{
		33614,
		33625,
		33636,
	};
	private static final int[] HAYUK =
	{
		33612,
		33623,
		33634,
	};
	private static final int[] MONSTERS =
	{
		19220,
		19221,
		19222, // Solo 85
		19223,
		19224,
		19225, // Solo 90
		19226,
		19227,
		19228, // Solo 95
		19229,
		19230,
		19231, // Group 85
		19232,
		19233,
		19234, // Group 90
		19235,
		19236,
		19237, // Group 95
	};
	private static final int[] BOSSES =
	{
		19253, // Zellaka (Solo 85)
		25882, // Zellaka (Group 85)
		19254, // Pelline (Solo 90)
		25883, // Pelline (Group 90)
		19255, // Kalios (Solo 95)
		25884, // Kalios (Group 95)
	};
	private static final int[] MINI_BOSSES =
	{
		19222, // Kartia Dimensional Watchman (solo 85)
		19225, // Kartia Dimensional Watchman (solo 90)
		19228, // Kartia Dimensional Watchman (solo 95)
		19231, // Kartia Dimensional Watchman (group 85)
		19234, // Kartia Dimensional Watchman (group 90)
		19237, // Kartia Dimensional Watchman (group 95)
	};
	private static final int[] MIRRORS =
	{
		33798, // Life Plunderer (85)
		33799, // Life Plunderer (90)
		33800, // Life Plunderer (95)
	};
	private static final int[] PRISONERS =
	{
		33641, // Kartia Prisoner (85)
		33643, // Kartia Prisoner (90)
		33645, // Kartia Prisoner (95)
	};
	// Skills
	private static final SkillHolder MIRROR_SKILL_1 = new SkillHolder(15401, 1);
	private static final SkillHolder MIRROR_SKILL_2 = new SkillHolder(14065, 1);
	private static final SkillHolder BOSS_STONE = new SkillHolder(15155, 1);
	private static final SkillHolder PRISONER_HOLD = new SkillHolder(14988, 1);
	private static final SkillHolder PRISONER_CLEANSE = new SkillHolder(14992, 1);
	// Zones
	private static final int KARTIA_85_DETECT_1 = 12020;
	private static final int KARTIA_85_DETECT_2 = 12021;
	private static final int KARTIA_90_DETECT_1 = 12025;
	private static final int KARTIA_90_DETECT_2 = 12026;
	private static final int KARTIA_95_DETECT_1 = 12030;
	private static final int KARTIA_95_DETECT_2 = 12031;
	// Misc
	private static final int TEMPLATE_ID_SOLO_85 = 205;
	private static final int TEMPLATE_ID_SOLO_90 = 206;
	private static final int TEMPLATE_ID_SOLO_95 = 207;
	private static final int TEMPLATE_ID_GROUP_85 = 208;
	private static final int TEMPLATE_ID_GROUP_90 = 209;
	private static final int TEMPLATE_ID_GROUP_95 = 210;
	private static final long WAVE_DEALY = 30000;
	private static final long WAVE_MONITOR_DELAY = 3000;
	// Solo instance XP reward values
	private static final int SOLO_BASE_EXP_85 = 480000000;
	private static final int SOLO_RAND_EXP_85 = 8055934;
	private static final int SOLO_BASE_EXP_90 = 670000000;
	private static final int SOLO_RAND_EXP_90 = 6173918;
	private static final int SOLO_BASE_EXP_95 = 970000000;
	private static final int SOLO_RAND_EXP_95 = 6305195;
	// Solo instance SP reward values
	private static final int SOLO_BASE_SP_85 = 3800000;
	private static final int SOLO_RAND_SP_85 = 73460;
	private static final int SOLO_BASE_SP_90 = 5600000;
	private static final int SOLO_RAND_SP_90 = 82134;
	private static final int SOLO_BASE_SP_95 = 8500000;
	private static final int SOLO_RAND_SP_95 = 39112;
	
	public KartiasLabyrinth()
	{
		super(TEMPLATE_ID_SOLO_85, TEMPLATE_ID_SOLO_90, TEMPLATE_ID_SOLO_95, TEMPLATE_ID_GROUP_85, TEMPLATE_ID_GROUP_90, TEMPLATE_ID_GROUP_95);
		addStartNpc(KARTIA_RESEARCHER);
		addFirstTalkId(KARTIA_RESEARCHER);
		addFirstTalkId(ADOLPH);
		addFirstTalkId(BARTON);
		addFirstTalkId(ELISE);
		addFirstTalkId(ELIYAH);
		addFirstTalkId(HAYUK);
		addTalkId(ADOLPH);
		addTalkId(KARTIA_RESEARCHER);
		addSpawnId(BOZ_ENERGY);
		addSpawnId(BOSSES);
		addAttackId(MINI_BOSSES);
		addAttackId(MIRRORS);
		addMoveFinishedId(MINI_BOSSES);
		addMoveFinishedId(PRISONERS);
		addRouteFinishedId(MONSTERS);
		setCreatureKillId(this::onCreatureKill, MONSTERS);
		setCreatureKillId(this::onBossKill, BOSSES);
		setCreatureSeeId(this::onCreatureSee, MONSTERS);
		addEnterZoneId(KARTIA_85_DETECT_1, KARTIA_85_DETECT_2);
		addEnterZoneId(KARTIA_90_DETECT_1, KARTIA_90_DETECT_2);
		addEnterZoneId(KARTIA_95_DETECT_1, KARTIA_95_DETECT_2);
		addInstanceCreatedId(TEMPLATE_ID_SOLO_85, TEMPLATE_ID_SOLO_90, TEMPLATE_ID_SOLO_95, TEMPLATE_ID_GROUP_85, TEMPLATE_ID_GROUP_90, TEMPLATE_ID_GROUP_95);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "33647-1.htm":
			case "33647-2.htm":
			case "33647-3.htm":
			case "33647-4.htm":
			{
				return event;
			}
			case "request_zellaka_solo":
			{
				enterInstance(player, npc, TEMPLATE_ID_SOLO_85);
				return null;
			}
			case "request_pelline_solo":
			{
				enterInstance(player, npc, TEMPLATE_ID_SOLO_90);
				return null;
			}
			case "request_kalios_solo":
			{
				enterInstance(player, npc, TEMPLATE_ID_SOLO_95);
				return null;
			}
			case "request_zellaka_party":
			{
				enterInstance(player, npc, TEMPLATE_ID_GROUP_85);
				return null;
			}
			case "request_pelline_party":
			{
				enterInstance(player, npc, TEMPLATE_ID_GROUP_90);
				return null;
			}
			case "request_kalios_party":
			{
				enterInstance(player, npc, TEMPLATE_ID_GROUP_95);
				return null;
			}
			default:
			{
				final Instance instance = npc.getInstanceWorld();
				if (instance != null)
				{
					switch (event)
					{
						case "adolph-01.html":
						case "adolph-03.html":
						{
							return event;
						}
						case "sendBarton":
						case "sendHayuk":
						case "sendEliyah":
						case "sendElise":
						{
							if (npc.isScriptValue(0))
							{
								npc.setScriptValue(1);
								instance.openCloseDoor(instance.getTemplateParameters().getInt("firstDoorId"), true);
								instance.spawnGroup("HELPERS").stream().filter(n -> n.getId() == instance.getTemplateParameters().getInt(event.replace("send", "helper"))).forEach(n -> n.deleteMe());
								getTimers().addTimer("TELEPORT_PLAYER", 3000, npc, player);
								return "adolph-04.html";
							}
							return "adolph-02.html";
						}
					}
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = (npc != null) ? npc.getInstanceWorld() : player.getInstanceWorld();
		if (instance != null)
		{
			switch (event)
			{
				case "TELEPORT_PLAYER":
				{
					instance.openCloseDoor(instance.getTemplateParameters().getInt("firstDoorId"), false);
					instance.setStatus(1); // Used for notify helper's AI
					player.teleToLocation(instance.getTemplateParameters().getLocation("playerLoc"));
					manageProgressInInstance(instance);
					break;
				}
				case "MOVE_TO_MIDDLE":
				{
					if (npc != null)
					{
						if (npc.getInstanceWorld().getParameters().getInt("ROOM", 1) < 2)
						{
							// Move mobs in Room 1 to the middle at top of the steps. Add timer to start moving towards prisoners.
							final Location loc = instance.getTemplateParameters().getLocation("middlePointRoom1");
							final Location moveTo = new Location(loc.getX() + getRandom(-100, 100), loc.getY() + getRandom(-100, 100), loc.getZ());
							npc.setRunning();
							addMoveToDesire(npc, moveTo, 6);
							getTimers().addTimer("START_MOVE", 12000, npc, null);
						}
						else if (npc.getInstanceWorld().getParameters().getInt("ROOM", 1) == 3)
						{
							// Move mobs in Room 3 towards center of room.
							final Location loc = instance.getTemplateParameters().getLocation("middlePointRoom3");
							final Location moveTo = new Location(loc.getX() + getRandom(-200, 200), loc.getY() + getRandom(-200, 200), loc.getZ());
							npc.setRunning();
							addMoveToDesire(npc, moveTo, 23);
						}
					}
					break;
				}
				case "START_MOVE":
				{
					if (npc != null)
					{
						final String selectedRoute = (getRandomBoolean() ? "route1_" : "route2_") + Integer.toString(getRandom(1, 3));
						WalkingManager.getInstance().startMoving(npc, instance.getTemplateParameters().getString(selectedRoute));
					}
					break;
				}
				case "START_3RD_ROOM":
				{
					instance.openCloseDoor(instance.getTemplateParameters().getInt("thirdDoorId"), false);
					instance.getAliveNpcs(MONSTERS).forEach(n -> n.doDie(null));
					getTimers().addTimer("CALL_PROGRESS", 1000, n -> manageProgressInInstance(instance));
					instance.getParameters().set("TELEPORT_3_ENABLED", true);
					break;
				}
				case "MIRROR_DESPAWN":
				{
					showOnScreenMsg(instance, NpcStringId.THE_LIFE_PLUNDERER_HAS_DISAPPEARED, ExShowScreenMessage.TOP_CENTER, 5000, true);
					manageProgressInInstance(instance);
					break;
				}
			}
		}
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		instance.spawnGroup("PRISONERS").forEach(npc ->
		{
			final SkillHolder poison = npc.getParameters().getSkillHolder("poison_skill");
			poison.getSkill().applyEffects(npc, npc);
			PRISONER_HOLD.getSkill().applyEffects(npc, npc);
			
		});
		
		if (!isSoloKartia(instance))
		{
			getTimers().addTimer("CALL_PROGRESS", 2500, n -> manageProgressInInstance(instance));
		}
	}
	
	public void onCreatureKill(OnCreatureDeath event)
	{
		final Npc npc = (Npc) event.getTarget();
		final Instance instance = npc.getInstanceWorld();
		
		if (instance != null)
		{
			final StatsSet param = instance.getParameters();
			if (param.getBoolean("BOSS_KILL_OPEN_DOOR", false) && CommonUtil.contains(MINI_BOSSES, npc.getId()))
			{
				instance.setParameter("BOSS_KILL_OPEN_DOOR", true);
				instance.openCloseDoor(instance.getTemplateParameters().getInt("thirdDoorId"), true);
				instance.setStatus(3); // Used for notify helper's AI
			}
			else if (param.getBoolean("CONTINUE_AFTER_KILL", false) && instance.getAliveNpcs(MINI_BOSSES).isEmpty())
			{
				param.set("CONTINUE_AFTER_KILL", false);
				getTimers().addTimer("CALL_PROGRESS", 5000, n -> manageProgressInInstance(instance));
			}
		}
	}
	
	private void manageWaves(Instance instance)
	{
		if ((instance != null) && instance.getAliveNpcs(MONSTERS).isEmpty())
		{
			getTimers().cancelTimers("NEXT_WAVE_DELAY");
			getTimers().cancelTimers("MONITOR_WAVE");
			getTimers().addTimer("CALL_PROGRESS", 5000, n -> manageProgressInInstance(instance));
		}
		else
		{
			getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
		}
	}
	
	public void onBossKill(OnCreatureDeath event)
	{
		final Npc npc = (Npc) event.getTarget();
		final Instance instance = npc.getInstanceWorld();
		
		if (instance != null)
		{
			if (isSoloKartia(instance))
			{
				final PlayerInstance player = instance.getFirstPlayer();
				if (player != null)
				{
					switch (instance.getTemplateId())
					{
						case TEMPLATE_ID_SOLO_85:
						{
							player.addExpAndSp(SOLO_BASE_EXP_85 + getRandom(SOLO_RAND_EXP_85), SOLO_BASE_SP_85 + getRandom(SOLO_RAND_SP_85));
							break;
						}
						case TEMPLATE_ID_SOLO_90:
						{
							player.addExpAndSp(SOLO_BASE_EXP_90 + getRandom(SOLO_RAND_EXP_90), SOLO_BASE_SP_90 + getRandom(SOLO_RAND_SP_90));
							break;
						}
						case TEMPLATE_ID_SOLO_95:
						{
							player.addExpAndSp(SOLO_BASE_EXP_95 + getRandom(SOLO_RAND_EXP_95), SOLO_BASE_SP_95 + getRandom(SOLO_RAND_SP_95));
							break;
						}
					}
				}
			}
			instance.finishInstance();
		}
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (instance != null)
		{
			if (CommonUtil.contains(MINI_BOSSES, npc.getId()))
			{
				if (npc.isScriptValue(0) && (npc.getCurrentHpPercent() < 50) && instance.getParameters().getBoolean("BOSS_CAN_ESCAPE", false))
				{
					instance.setParameter("BOSS_CAN_ESCAPE", false);
					npc.setScriptValue(1);
					npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.NOT_BAD_FOR_A_BUNCH_OF_HUMANS_I_M_LEAVING);
				}
			}
			else
			{
				npc.setUndying(true);
				if (npc.isScriptValue(0) && (npc.getCurrentHpPercent() < 10))
				{
					npc.setScriptValue(1);
					showOnScreenMsg(instance, NpcStringId.BURNING_BLOOD_S_EFFECT_IS_FELT, ExShowScreenMessage.TOP_CENTER, 5000, true);
					MIRROR_SKILL_1.getSkill().applyEffects(attacker, attacker);
					MIRROR_SKILL_2.getSkill().applyEffects(attacker, attacker);
					manageProgressInInstance(instance);
					getTimers().addTimer("SUICIDE", 5000, n -> npc.doDie(null));
					getTimers().cancelTimer("MIRROR_DESPAWN", npc, null);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		final Instance instance = creature.getInstanceWorld();
		if ((instance != null) && creature.isPlayer())
		{
			switch (zone.getId())
			{
				case KARTIA_85_DETECT_1:
				case KARTIA_90_DETECT_1:
				case KARTIA_95_DETECT_1:
				{
					if (instance.getParameters().getBoolean("SECOND_ROOM_OPENED", true))
					{
						instance.getParameters().set("SECOND_ROOM_OPENED", false);
						getTimers().addTimer("CLOSE_SECOND_DOORS", 20000, n ->
						{
							instance.openCloseDoor(instance.getTemplateParameters().getInt("secondDoorId"), false);
							instance.getParameters().set("TELEPORT_2_ENABLED", true);
						});
					}
					break;
				}
				case KARTIA_85_DETECT_2:
				case KARTIA_90_DETECT_2:
				case KARTIA_95_DETECT_2:
				{
					if (instance.getParameters().getBoolean("LAST_ROOM_OPENED", true))
					{
						instance.getParameters().set("LAST_ROOM_OPENED", false);
						getTimers().addTimer("START_3RD_ROOM", 10000, null, creature.getActingPlayer());
					}
					break;
				}
			}
		}
		return super.onEnterZone(creature, zone);
	}
	
	@Override
	public void onMoveFinished(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if ((instance != null) && CommonUtil.contains(PRISONERS, npc.getId()))
		{
			if (npc.isScriptValue(0))
			{
				npc.setScriptValue(1);
				final Location moveTo = new Location(npc.getX() + getRandom(-200, 200), npc.getY() + getRandom(-200, 200), npc.getZ());
				addMoveToDesire(npc, moveTo, 23);
			}
			else
			{
				npc.deleteMe();
			}
		}
		super.onMoveFinished(npc);
	}
	
	@Override
	public void onRouteFinished(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (instance != null)
		{
			final Location moveTo = new Location(npc.getX() + getRandom(-100, 100), npc.getY() + getRandom(-100, 100), npc.getZ());
			npc.setRandomWalking(true);
			addMoveToDesire(npc, moveTo, 6);
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (instance != null)
		{
			if (npc.getId() == BOZ_ENERGY)
			{
				npc.setDisplayEffect(2);
			}
			else if (CommonUtil.contains(BOSSES, npc.getId()))
			{
				npc.setTarget(npc);
				npc.doCast(BOSS_STONE.getSkill());
				((Attackable) npc).setCanReturnToSpawnPoint(false);
				npc.setRandomWalking(false);
				npc.setTargetable(false);
				npc.setIsInvul(true);
			}
			else if (CommonUtil.contains(MONSTERS, npc.getId()) || CommonUtil.contains(MINI_BOSSES, npc.getId()))
			{
				npc.setTargetable(false);
				npc.setIsInvul(true);
			}
		}
		return super.onSpawn(npc);
	}
	
	private void manageProgressInInstance(Instance instance)
	{
		final StatsSet param = instance.getParameters();
		final int room = param.getInt("ROOM", 1);
		final int stage = param.getInt("STAGE", 1);
		final int wave = param.getInt("WAVE", 1);
		
		if (room == 1)
		{
			switch (stage)
			{
				case 1:
				{
					switch (wave)
					{
						case 1:
						{
							showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
							moveMonsters(instance.spawnGroup("ROOM1_STAGE1_WAVE1"));
							param.set("WAVE", 2);
							getTimers().addTimer("NEXT_WAVE_DELAY", WAVE_DEALY, n -> manageProgressInInstance(instance));
							getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
							break;
						}
						case 2:
						{
							moveMonsters(instance.spawnGroup("ROOM1_STAGE1_WAVE2"));
							param.set("WAVE", 3);
							getTimers().addTimer("NEXT_WAVE_DELAY", WAVE_DEALY, n -> manageProgressInInstance(instance));
							getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
							break;
						}
						case 3:
						{
							moveMonsters(instance.spawnGroup("ROOM1_STAGE1_WAVE3"));
							param.set("WAVE", 1);
							param.set("STAGE", 2);
							getTimers().addTimer("CALL_PROGRESS", 8000, n -> manageProgressInInstance(instance));
							break;
						}
					}
					break;
				}
				case 2:
				{
					switch (wave)
					{
						case 1:
						{
							showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
							moveMonsters(instance.spawnGroup("ROOM1_STAGE2_WAVE1"));
							param.set("WAVE", 2);
							getTimers().addTimer("NEXT_WAVE_DELAY", WAVE_DEALY, n -> manageProgressInInstance(instance));
							getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
							break;
						}
						case 2:
						{
							moveMonsters(instance.spawnGroup("ROOM1_STAGE2_WAVE2"));
							param.set("WAVE", 3);
							getTimers().addTimer("NEXT_WAVE_DELAY", WAVE_DEALY, n -> manageProgressInInstance(instance));
							getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
							break;
						}
						case 3:
						{
							moveMonsters(instance.spawnGroup("ROOM1_STAGE2_WAVE3"));
							param.set("WAVE", 1);
							param.set("STAGE", 3);
							getTimers().addTimer("CALL_PROGRESS", 8000, n -> manageProgressInInstance(instance));
							break;
						}
					}
					break;
				}
				case 3:
				{
					switch (wave)
					{
						case 1:
						{
							showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
							moveMonsters(instance.spawnGroup("ROOM1_STAGE3_WAVE1"));
							param.set("WAVE", 2);
							getTimers().addTimer("NEXT_WAVE_DELAY", WAVE_DEALY, n -> manageProgressInInstance(instance));
							getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
							break;
						}
						case 2:
						{
							moveMonsters(instance.spawnGroup("ROOM1_STAGE3_WAVE2"));
							param.set("WAVE", 3);
							getTimers().addTimer("NEXT_WAVE_DELAY", WAVE_DEALY, n -> manageProgressInInstance(instance));
							getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
							break;
						}
						case 3:
						{
							moveMonsters(instance.spawnGroup("ROOM1_STAGE3_WAVE3"));
							if (isSoloKartia(instance))
							{
								param.set("WAVE", 4);
								
								getTimers().addTimer("PRISONERS_ESCAPE", 5000, n ->
								{
									instance.getAliveNpcs(PRISONERS).forEach(prisoner ->
									{
										param.set("SURVIVOR_COUNT", param.getInt("SURVIVOR_COUNT", 0) + 1);
										prisoner.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.I_AM_SAFE_THANKS_TO_YOU_I_WILL_BEGIN_SUPPORTING_AS_SOON_AS_PREPARATIONS_ARE_COMPLETE);
										prisoner.setTargetable(false);
										PRISONER_CLEANSE.getSkill().applyEffects(prisoner, prisoner);
										final Location loc = instance.getTemplateParameters().getLocation("prisonerEscapeLoc");
										addMoveToDesire(prisoner, loc, 23);
									});
								});
							}
							else
							{
								param.set("STAGE", 4);
								param.set("WAVE", 1);
							}
							getTimers().addTimer("CALL_PROGRESS", 8000, n -> manageProgressInInstance(instance));
							break;
						}
						case 4:
						{
							showOnScreenMsg(instance, NpcStringId.THE_LIFE_PLUNDERER_S_TRUE_FORM_IS_REVEALED, ExShowScreenMessage.TOP_CENTER, 5000, true);
							instance.spawnGroup("ROOM1_STAGE3_WAVE4");
							instance.getAliveNpcs(BOZ_ENERGY).forEach(npc -> npc.deleteMe());
							instance.getAliveNpcs(MIRRORS).forEach(npc -> getTimers().addTimer("MIRROR_DESPAWN", 180000, npc, null));
							param.set("ROOM", 2);
							param.set("STAGE", 1);
							param.set("WAVE", 1);
							break;
						}
					}
					break;
				}
				case 4: // Only used in group version of instance.
				{
					switch (wave)
					{
						case 1:
						{
							showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
							moveMonsters(instance.spawnGroup("ROOM1_STAGE4_WAVE1"));
							param.set("WAVE", 2);
							getTimers().addTimer("NEXT_WAVE_DELAY", WAVE_DEALY, n -> manageProgressInInstance(instance));
							getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
							break;
						}
						case 2:
						{
							moveMonsters(instance.spawnGroup("ROOM1_STAGE4_WAVE2"));
							param.set("WAVE", 3);
							getTimers().addTimer("NEXT_WAVE_DELAY", WAVE_DEALY, n -> manageProgressInInstance(instance));
							getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
							break;
						}
						case 3:
						{
							moveMonsters(instance.spawnGroup("ROOM1_STAGE4_WAVE3"));
							param.set("WAVE", 1);
							param.set("STAGE", 5);
							getTimers().addTimer("CALL_PROGRESS", 8000, n -> manageProgressInInstance(instance));
							break;
						}
					}
					break;
				}
				case 5: // Only used in group version of instance.
				{
					switch (wave)
					{
						case 1:
						{
							showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
							moveMonsters(instance.spawnGroup("ROOM1_STAGE5_WAVE1"));
							param.set("WAVE", 2);
							getTimers().addTimer("NEXT_WAVE_DELAY", WAVE_DEALY, n -> manageProgressInInstance(instance));
							getTimers().addTimer("MONITOR_WAVE", WAVE_MONITOR_DELAY, n -> manageWaves(instance));
							break;
						}
						case 2:
						{
							moveMonsters(instance.spawnGroup("ROOM1_STAGE5_WAVE2"));
							param.set("ROOM", 2);
							param.set("STAGE", 1);
							param.set("WAVE", 1);
							param.set("CONTINUE_AFTER_KILL", true);
							
							getTimers().addTimer("PRISONERS_ESCAPE", 5000, n ->
							{
								instance.getAliveNpcs(PRISONERS).forEach(prisoner ->
								{
									param.set("SURVIVOR_COUNT", param.getInt("SURVIVOR_COUNT", 0) + 1);
									prisoner.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.I_AM_SAFE_THANKS_TO_YOU_I_WILL_BEGIN_SUPPORTING_AS_SOON_AS_PREPARATIONS_ARE_COMPLETE);
									prisoner.setTargetable(false);
									PRISONER_CLEANSE.getSkill().applyEffects(prisoner, prisoner);
									final Location loc = instance.getTemplateParameters().getLocation("prisonerEscapeLoc");
									addMoveToDesire(prisoner, loc, 23);
								});
							});
							break;
						}
					}
					break;
				}
			}
		}
		else if (room == 2)
		{
			instance.setParameter("BOSS_CAN_ESCAPE", false);
			instance.setParameter("BOSS_KILL_OPEN_DOOR", true);
			instance.spawnGroup("ROOM2_STAGE1_WAVE1");
			instance.openCloseDoor(instance.getTemplateParameters().getInt("secondDoorId"), true);
			instance.setStatus(2); // Used for notify helper's AI
			instance.getAliveNpcs(BOZ_ENERGY).forEach(npc -> npc.deleteMe());
			param.set("ROOM", 3);
			param.set("STAGE", 1);
			param.set("WAVE", 1);
		}
		else if (room == 3)
		{
			switch (stage)
			{
				case 1:
				{
					showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
					moveMonsters(instance.spawnGroup("ROOM3_STAGE1_WAVE1"));
					param.set("STAGE", 2);
					getTimers().addTimer("CALL_PROGRESS", 36000, n -> manageProgressInInstance(instance));
					
					final Location survivorLoc = instance.getTemplateParameters().getLocation("middlePointRoom3");
					final int survivorCount = param.getInt("SURVIVOR_COUNT");
					for (int i = 0; i < survivorCount; i++)
					{
						final Location loc = new Location(survivorLoc.getX() + getRandom(-200, 200), survivorLoc.getY() + getRandom(-200, 200), survivorLoc.getZ(), 47595);
						addSpawn(instance.getTemplateParameters().getInt("helperSurvivor"), loc, false, 0, false, instance.getId());
					}
					break;
				}
				case 2:
				{
					showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
					moveMonsters(instance.spawnGroup("ROOM3_STAGE2_WAVE1"));
					param.set("STAGE", 3);
					getTimers().addTimer("CALL_PROGRESS", 36000, n -> manageProgressInInstance(instance));
					break;
				}
				case 3:
				{
					showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
					moveMonsters(instance.spawnGroup("ROOM3_STAGE3_WAVE1"));
					
					if (isSoloKartia(instance))
					{
						instance.getAliveNpcs(BOSSES).forEach(npc ->
						{
							npc.stopSkillEffects(BOSS_STONE.getSkill());
							npc.setTargetable(true);
							npc.setIsInvul(false);
							final Location loc = instance.getTemplateParameters().getLocation("middlePointRoom3");
							final Location moveTo = new Location(loc.getX() + getRandom(-200, 200), loc.getY() + getRandom(-200, 200), loc.getZ());
							addMoveToDesire(npc, moveTo, 23);
						});
					}
					else
					{
						param.set("STAGE", 4);
						getTimers().addTimer("CALL_PROGRESS", 36000, n -> manageProgressInInstance(instance));
					}
					break;
				}
				case 4:
				{
					showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
					moveMonsters(instance.spawnGroup("ROOM3_STAGE4_WAVE1"));
					param.set("STAGE", 5);
					getTimers().addTimer("CALL_PROGRESS", 36000, n -> manageProgressInInstance(instance));
					break;
				}
				case 5:
				{
					showOnScreenMsg(instance, NpcStringId.STAGE_S1, ExShowScreenMessage.TOP_CENTER, 5000, true, Integer.toString(stage));
					moveMonsters(instance.spawnGroup("ROOM3_STAGE4_WAVE1"));
					instance.getAliveNpcs(BOSSES).forEach(npc ->
					{
						npc.stopSkillEffects(BOSS_STONE.getSkill());
						npc.setTargetable(true);
						npc.setIsInvul(false);
						npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.THE_RITUAL_IS_COMPLETE_NOW_I_WILL_SHOW_YOU_HELL);
						final Location loc = instance.getTemplateParameters().getLocation("middlePointRoom3");
						final Location moveTo = new Location(loc.getX() + getRandom(-200, 200), loc.getY() + getRandom(-200, 200), loc.getZ());
						addMoveToDesire(npc, moveTo, 23);
					});
					break;
				}
			}
		}
	}
	
	private void moveMonsters(List<Npc> monsterList)
	{
		int delay = 500;
		for (Npc monster : monsterList)
		{
			final Instance world = monster.getInstanceWorld();
			if (monster.isAttackable() && (world != null))
			{
				if (world.getParameters().getInt("ROOM", 1) <= 2)
				{
					monster.setRandomWalking(false);
					getTimers().addTimer("MOVE_TO_MIDDLE", delay, monster, null);
					delay += 250;
				}
				else if (world.getParameters().getInt("ROOM", 1) == 3)
				{
					onTimerEvent("MOVE_TO_MIDDLE", null, monster, null);
				}
				((Attackable) monster).setCanReturnToSpawnPoint(false);
				monster.initSeenCreatures();
			}
		}
	}
	
	public void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance world = npc.getInstanceWorld();
		
		if ((world != null) && (creature.isPlayer() || creature.getInstanceType().isType(InstanceType.FriendlyNpcInstance)) && npc.isScriptValue(1))
		{
			final double distance = npc.calculateDistance2D(creature);
			if ((distance < 450) && !CommonUtil.contains(PRISONERS, creature.getId()))
			{
				npc.setTargetable(true);
				npc.setIsInvul(false);
				npc.setScriptValue(1);
				WalkingManager.getInstance().cancelMoving(npc);
				((MonsterInstance) npc).addDamageHate(creature, 0, 1000);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				addAttackDesire(npc, creature);
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		if (npc.getId() == KARTIA_RESEARCHER)
		{
			return "33647.htm";
		}
		if (CommonUtil.contains(ADOLPH, npc.getId()))
		{
			return "adolph.html";
		}
		if (CommonUtil.contains(BARTON, npc.getId()))
		{
			return "barton.html";
		}
		if (CommonUtil.contains(ELISE, npc.getId()))
		{
			return "elise.html";
		}
		if (CommonUtil.contains(ELIYAH, npc.getId()))
		{
			return "eliyah.html";
		}
		if (CommonUtil.contains(HAYUK, npc.getId()))
		{
			return "hayuk.html";
		}
		return "";
	}
	
	private boolean isSoloKartia(Instance instance)
	{
		return (instance.getTemplateId() == TEMPLATE_ID_SOLO_85) || (instance.getTemplateId() == TEMPLATE_ID_SOLO_90) || (instance.getTemplateId() == TEMPLATE_ID_SOLO_95);
	}
	
	public static void main(String[] args)
	{
		new KartiasLabyrinth();
	}
}