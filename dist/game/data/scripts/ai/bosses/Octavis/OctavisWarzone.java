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
package ai.bosses.Octavis;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.instancemanager.WalkingManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.ScriptZone;
import org.l2jbr.gameserver.network.serverpackets.ExShowUsm;
import org.l2jbr.gameserver.util.Util;

import instances.AbstractInstance;

/**
 * Octavis Warzone instance zone.
 * @author St3eT
 */
public class OctavisWarzone extends AbstractInstance
{
	// NPCs
	private static final int[] OCTAVIS_STAGE_1 =
	{
		29191, // Common
		29209, // Extreme
	};
	private static final int[] OCTAVIS_STAGE_2 =
	{
		29193, // Common
		29211, // Extreme
	};
	private static final int[] OCTAVIS_STAGE_3 =
	{
		29194, // Common
		29212, // Extreme
	};
	private static final int[] BEASTS =
	{
		29192, // Common
		29210, // Extreme
	};
	private static final int[] BEASTS_MINIONS =
	{
		22929, // Common
		23087, // Extreme
	};
	private static final int[] GLADIATORS =
	{
		22928, // Common
		23086, // Extreme
	};
	private static final int LYDIA = 32892;
	private static final int DOOR_MANAGER = 18984;
	// Skills
	private static final SkillHolder STAGE_2_SKILL_1 = new SkillHolder(14026, 1);
	private static final SkillHolder STAGE_2_SKILL_2 = new SkillHolder(14027, 1);
	private static final SkillHolder STAGE_2_SKILL_3 = new SkillHolder(14575, 1);
	// Locations
	private static final Location BATTLE_LOC = new Location(208720, 120576, -10000);
	private static final Location OCTAVIS_SPAWN_LOC = new Location(207069, 120580, -9987);
	private static final Location BEASTS_RANDOM_POINT = new Location(207244, 120579, -10008);
	private static final Location[] BEASTS_MINIONS_LOC =
	{
		new Location(206681, 119327, -9987),
		new Location(207724, 119303, -9987),
		new Location(208472, 120047, -9987),
		new Location(208484, 121110, -9987),
		new Location(207730, 121859, -9987),
		new Location(206654, 121865, -9987),
	};
	// Zones
	private static final ScriptZone TELEPORT_ZONE = ZoneManager.getInstance().getZoneById(12042, ScriptZone.class);
	// Misc
	private static final int TEMPLATE_ID = 180;
	private static final int EXTREME_TEMPLATE_ID = 181;
	private static final int MAIN_DOOR_1 = 26210002;
	private static final int MAIN_DOOR_2 = 26210001;
	
	public OctavisWarzone()
	{
		super(TEMPLATE_ID, EXTREME_TEMPLATE_ID);
		addStartNpc(LYDIA);
		addTalkId(LYDIA);
		addSpawnId(DOOR_MANAGER);
		addSpawnId(GLADIATORS);
		addAttackId(OCTAVIS_STAGE_1);
		addAttackId(OCTAVIS_STAGE_2);
		addAttackId(BEASTS);
		addKillId(OCTAVIS_STAGE_1);
		addKillId(OCTAVIS_STAGE_2);
		addKillId(OCTAVIS_STAGE_3);
		addMoveFinishedId(GLADIATORS);
		addSpellFinishedId(OCTAVIS_STAGE_2);
		addEnterZoneId(TELEPORT_ZONE.getId());
		setCreatureSeeId(this::onCreatureSee, DOOR_MANAGER);
		addInstanceCreatedId(TEMPLATE_ID, EXTREME_TEMPLATE_ID);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "enterEasyInstance":
			{
				enterInstance(player, npc, TEMPLATE_ID);
				break;
			}
			case "enterExtremeInstance":
			{
				enterInstance(player, npc, EXTREME_TEMPLATE_ID);
				break;
			}
			case "reenterInstance":
			{
				final Instance activeInstance = getPlayerInstance(player);
				if (isInInstance(activeInstance))
				{
					enterInstance(player, npc, activeInstance.getTemplateId());
					return "PartyMemberReenter.html";
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			final StatsSet npcVars = npc.getVariables();
			final StatsSet npcParams = npc.getParameters();
			
			switch (event)
			{
				case "SECOND_DOOR_OPEN":
				{
					world.openCloseDoor(MAIN_DOOR_2, true);
					break;
				}
				case "CLOSE_DOORS":
				{
					world.openCloseDoor(MAIN_DOOR_2, false);
					world.openCloseDoor(MAIN_DOOR_1, false);
					world.getParameters().set("TELEPORT_ACTIVE", true);
					npc.teleToLocation(BATTLE_LOC);
					playMovie(world, Movie.SC_OCTABIS_OPENING);
					getTimers().addTimer("START_STAGE_1", 26500, npc, null);
					break;
				}
				case "START_STAGE_1":
				{
					world.spawnGroup("STAGE_1");
					world.getAliveNpcs(BEASTS).forEach(beasts ->
					{
						beasts.disableCoreAI(true);
						beasts.setUndying(true);
						((Attackable) beasts).setCanReturnToSpawnPoint(false);
						final Npc octavis = addSpawn((!isExtremeMode(world) ? OCTAVIS_STAGE_1[0] : OCTAVIS_STAGE_1[1]), OCTAVIS_SPAWN_LOC, false, 0, false, world.getId());
						octavis.disableCoreAI(true);
						octavis.setRunning();
						octavis.sendChannelingEffect(beasts, 1);
						octavis.setTargetable(false);
						((Attackable) octavis).setCanReturnToSpawnPoint(false);
						getTimers().addRepeatingTimer("FOLLOW_BEASTS", 500, octavis, null);
						getTimers().addRepeatingTimer("BEASTS_CHECK_HP", 5000, beasts, null);
						WalkingManager.getInstance().startMoving(beasts, "octabis_superpoint");
					});
					break;
				}
				case "FOLLOW_BEASTS":
				{
					world.getAliveNpcs(BEASTS).forEach(beasts ->
					{
						addMoveToDesire(npc, beasts.getLocation(), 23);
						npc.sendChannelingEffect(beasts, 1);
					});
					break;
				}
				case "BEASTS_CHECK_HP":
				{
					final int hpPer = npc.getCurrentHpPercent();
					
					if ((hpPer < 50) && npc.isScriptValue(0))
					{
						npc.getStat().addFixedValue(Stats.REGENERATE_HP_RATE, 100d); // On original script 95000d was added, making Octavis targetable for two seconds.
						npc.setScriptValue(1);
					}
					else if ((hpPer > 90) && npc.isScriptValue(1))
					{
						npc.getStat().addFixedValue(Stats.REGENERATE_HP_RATE, 0d);
						npc.setScriptValue(0);
					}
					
					final Npc octavis = world.getAliveNpcs(OCTAVIS_STAGE_1).stream().findAny().orElse(null);
					if (octavis != null)
					{
						octavis.setTargetable(hpPer < 50);
					}
					break;
				}
				case "END_STAGE_1":
				{
					playMovie(world, Movie.SC_OCTABIS_PHASECH_A);
					getTimers().addTimer("START_STAGE_2", 12000, npc, null);
					break;
				}
				case "START_STAGE_2":
				{
					world.spawnGroup("STAGE_2").forEach(octavis -> ((Attackable) octavis).setCanReturnToSpawnPoint(false));
					break;
				}
				case "END_STAGE_2":
				{
					playMovie(world, Movie.SC_OCTABIS_PHASECH_B);
					getTimers().addTimer("START_STAGE_3", 15000, npc, null);
					break;
				}
				case "START_STAGE_3":
				{
					world.spawnGroup("STAGE_3").forEach(octavis -> ((Attackable) octavis).setCanReturnToSpawnPoint(false));
					break;
				}
				case "END_STAGE_3":
				{
					playMovie(world, Movie.SC_OCTABIS_ENDING);
					getTimers().addTimer("USM_SCENE_TIMER", 40000, npc, null);
					break;
				}
				case "USM_SCENE_TIMER":
				{
					world.broadcastPacket(ExShowUsm.OCTAVIS_INSTANCE_END);
					break;
				}
				case "GLADIATOR_START_SPAWN":
				{
					final int spawnIndex = npcVars.getInt("SPAWN_INDEX", 1);
					if (spawnIndex < 7)
					{
						if (isExtremeMode(world))
						{
							world.spawnGroup("magmeld4_2621_gro" + spawnIndex + "m1");
						}
						else
						{
							world.spawnGroup("magmeld4_2621_gmo" + spawnIndex + "m1");
						}
						npcVars.set("SPAWN_INDEX", spawnIndex + 1);
						getTimers().addTimer("GLADIATOR_START_SPAWN", 3000, npc, null);
					}
					break;
				}
				case "GLADIATOR_MOVING":
				{
					final int moveX = npcParams.getInt("Move_to_X", 0);
					final int moveY = npcParams.getInt("Move_to_Y", 0);
					
					if ((moveX != 0) && (moveY != 0))
					{
						npc.setRunning();
						addMoveToDesire(npc, new Location(moveX, moveY, -10008), 23);
					}
					break;
				}
				case "BEASTS_MINIONS_SPAWN":
				{
					for (int i = 0; i < getRandom(10); i++)
					{
						final Npc beast = addSpawn((!isExtremeMode(world) ? BEASTS_MINIONS[0] : BEASTS_MINIONS[1]), getRandomEntry(BEASTS_MINIONS_LOC), false, 0, false, world.getId());
						beast.setRunning();
						((Attackable) beast).setCanReturnToSpawnPoint(false);
						addMoveToDesire(beast, Util.getRandomPosition(BEASTS_RANDOM_POINT, 500, 500), 23);
					}
					
					getTimers().addTimer("BEASTS_MINIONS_SPAWN", 30000 + (getRandom(10) * 1000), npc, null);
					break;
				}
				case "MINION_CALL":
				{
					final PlayerInstance mostHated = ((Attackable) npc).getMostHated().getActingPlayer();
					if ((mostHated != null) && (mostHated.calculateDistance3D(npc) < 5000))
					{
						World.getInstance().getVisibleObjectsInRange(npc, Attackable.class, 4000, obj -> CommonUtil.contains(BEASTS_MINIONS, obj.getId()) || CommonUtil.contains(GLADIATORS, obj.getId())).forEach(minion ->
						{
							addAttackPlayerDesire(minion, mostHated, 23);
						});
					}
					getTimers().addTimer("MINION_CALL", 5000 + (getRandom(5) * 1000), npc, null);
					break;
				}
				case "ATTACK_TIMER":
				{
					final Creature mostHated = ((Attackable) npc).getMostHated();
					if ((mostHated != null) && mostHated.isPlayable() && (npc.calculateDistance2D(mostHated) < 1000))
					{
						final int random = getRandom(5);
						if (random < 3)
						{
							addSkillCastDesire(npc, mostHated, STAGE_2_SKILL_1, 23);
						}
						else if (random < 5)
						{
							addSkillCastDesire(npc, mostHated, STAGE_2_SKILL_2, 23);
						}
					}
					getTimers().addTimer("ATTACK_TIMER", getRandom(7, 9) * 1000, npc, null);
					break;
				}
				case "MEDUSA_SKILL_TIMER":
				{
					addSkillCastDesire(npc, npc, STAGE_2_SKILL_3, 23);
					break;
				}
			}
		}
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			final int hpPer = npc.getCurrentHpPercent();
			
			if (CommonUtil.contains(OCTAVIS_STAGE_1, npc.getId()))
			{
				if (hpPer >= 90)
				{
					npc.setDisplayEffect(0);
				}
				else if (hpPer >= 80)
				{
					npc.setDisplayEffect(1);
				}
				else if (hpPer >= 70)
				{
					npc.setDisplayEffect(2);
				}
				else if (hpPer >= 60)
				{
					npc.setDisplayEffect(3);
				}
				else if (hpPer >= 50)
				{
					npc.setDisplayEffect(4);
				}
				else
				{
					npc.setDisplayEffect(5);
				}
			}
			else if (CommonUtil.contains(OCTAVIS_STAGE_2, npc.getId()))
			{
				final StatsSet npcVars = npc.getVariables();
				
				if (npcVars.getBoolean("START_TIMERS", true))
				{
					npcVars.set("START_TIMERS", false);
					getTimers().addTimer("GLADIATOR_START_SPAWN", 6000, npc, null);
					getTimers().addTimer("ATTACK_TIMER", 15000, npc, null);
					getTimers().addTimer("MINION_CALL", 30000, npc, null);
					// myself->AddTimerEx(Royal_Timer, 30 * 1000);
					// myself->AddTimerEx(Scan_Timer, 1000);
					getTimers().addTimer("BEASTS_MINIONS_SPAWN", 1000, npc, null);
					// myself->AddTimerEx(Gladiator_Fishnet_Timer, 15 * 1000);
				}
				
				final int hpState = npcVars.getInt("HP_STATE", 0);
				if ((npc.getMaxHp() - npc.getCurrentHp()) > (npc.getMaxHp() * 0.01 * hpState))
				{
					final int state = hpState % 5;
					if (state == 0)
					{
						npc.setDisplayEffect(5);
						getTimers().addTimer("MEDUSA_SKILL_TIMER", 15000, npc, null);
					}
					else
					{
						npc.setDisplayEffect(state);
					}
					npcVars.set("HP_STATE", hpState + 1);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (CommonUtil.contains(OCTAVIS_STAGE_1, npc.getId()))
			{
				getTimers().cancelTimer("FOLLOW_BEASTS", npc, null);
				world.getAliveNpcs(BEASTS).forEach(beast ->
				{
					getTimers().cancelTimer("BEASTS_CHECK_HP", beast, null);
					beast.teleToLocation(new Location(-113360, -244676, -15536)); // Don't even ask, it's pure hack. - St3eT 2k16
					beast.deleteMe();
				});
				getTimers().addTimer("END_STAGE_1", 1000, npc, null);
			}
			else if (CommonUtil.contains(OCTAVIS_STAGE_2, npc.getId()))
			{
				// Cancel timers
				getTimers().cancelTimer("BEASTS_MINIONS_SPAWN", npc, null);
				getTimers().cancelTimer("MINION_CALL", npc, null);
				getTimers().cancelTimer("ATTACK_TIMER", npc, null);
				getTimers().cancelTimer("MEDUSA_SKILL_TIMER", npc, null);
				// Despawn beasts
				world.getAliveNpcs(BEASTS_MINIONS).forEach(beast -> beast.doDie(null));
				
				// Despawn gladiators
				for (int i = 1; i < 7; i++)
				{
					world.despawnGroup(isExtremeMode(world) ? ("magmeld4_2621_gro" + i + "m1") : ("magmeld4_2621_gmo" + i + "m1"));
				}
				getTimers().addTimer("END_STAGE_2", 3000, npc, null);
			}
			else if (CommonUtil.contains(OCTAVIS_STAGE_3, npc.getId()))
			{
				world.finishInstance();
				getTimers().addTimer("END_STAGE_3", 2000, npc, null);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		if ((player != null) && isInInstance(instance))
		{
			showHtmlFile(player, (instance.getTemplateId() == TEMPLATE_ID) ? "PartyEnterCommon.html" : "PartyEnterExtreme.html");
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (CommonUtil.contains(GLADIATORS, npc.getId()))
			{
				npc.setRandomWalking(false);
				world.openCloseDoor(npc.getParameters().getInt("My_DoorName", -1), true);
				getTimers().addTimer("GLADIATOR_MOVING", 3000, npc, null);
			}
			npc.initSeenCreatures();
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public void onMoveFinished(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			world.openCloseDoor(npc.getParameters().getInt("My_DoorName", -1), false);
		}
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		if (skill.getId() == STAGE_2_SKILL_3.getSkillId())
		{
			npc.setDisplayEffect(6);
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	public void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance world = npc.getInstanceWorld();
		
		if (isInInstance(world) && creature.isPlayer() && npc.isScriptValue(0))
		{
			world.openCloseDoor(MAIN_DOOR_1, true);
			getTimers().addTimer("SECOND_DOOR_OPEN", 3000, npc, null);
			getTimers().addTimer("CLOSE_DOORS", 60000, npc, null);
			npc.setScriptValue(1);
		}
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		final Instance world = creature.getInstanceWorld();
		if (creature.isPlayer() && isInInstance(world))
		{
			if (world.getParameters().getBoolean("TELEPORT_ACTIVE", false))
			{
				creature.teleToLocation(BATTLE_LOC);
			}
		}
		return super.onEnterZone(creature, zone);
	}
	
	private boolean isExtremeMode(Instance instance)
	{
		return instance.getTemplateId() == EXTREME_TEMPLATE_ID;
	}
	
	public static void main(String[] args)
	{
		new OctavisWarzone();
	}
}