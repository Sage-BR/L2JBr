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
package instances.CrystalCaverns;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.TeleportZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;

/**
 * Crystal Caverns - Steam Corridor instance zone.
 * @author St3eT
 */
public class CrystalCavernsSteamCorridor extends AbstractInstance
{
	// NPCs
	private static final int CAVERNS_ENTRACE = 33522;
	private static final int KECHI_NORMAL = 25797;
	private static final int KECHI_WISE = 26113;
	private static final int KECHI_WEALTHY = 26112;
	private static final int KECHI_ARMED = 26111;
	private static final int VICIOUS_DUELER = 23014;
	private static final int VICIOUS_WARRIOR = 23016;
	private static final int VICIOUS_SWORDSMAN = 23015;
	private static final int SPIRIT_PROTECTOR = 23013;
	private static final int FIRE_REGION = 19161;
	private static final int PLAYER_DETECTOR = 19075;
	private static final int TRAP_1 = 19011;
	private static final int TRAP_2 = 19012;
	// Skills
	private static final SkillHolder FIRE_SKILL_1 = new SkillHolder(14373, 1);
	private static final SkillHolder FIRE_SKILL_2 = new SkillHolder(14373, 2);
	private static final SkillHolder FIRE_SKILL_3 = new SkillHolder(14197, 1);
	private static final SkillHolder TRAP_SKILL_1 = new SkillHolder(14180, 1);
	private static final SkillHolder TRAP_SKILL_2 = new SkillHolder(14181, 1);
	private static final SkillHolder TRAP_SKILL_3 = new SkillHolder(14372, 1);
	// Location
	private static final Location BOSS_LOC = new Location(154078, 215125, -12140);
	// Misc
	private static final int TEMPLATE_ID = 164;
	
	public CrystalCavernsSteamCorridor()
	{
		super(TEMPLATE_ID);
		addStartNpc(CAVERNS_ENTRACE);
		addTalkId(CAVERNS_ENTRACE);
		addAttackId(TRAP_1, TRAP_2);
		addKillId(VICIOUS_DUELER, VICIOUS_WARRIOR, VICIOUS_SWORDSMAN, KECHI_NORMAL, KECHI_WISE, KECHI_WEALTHY, KECHI_ARMED);
		addSpawnId(SPIRIT_PROTECTOR, VICIOUS_DUELER, VICIOUS_WARRIOR, VICIOUS_SWORDSMAN, FIRE_REGION, PLAYER_DETECTOR);
		addEventReceivedId(SPIRIT_PROTECTOR, VICIOUS_DUELER, VICIOUS_WARRIOR, VICIOUS_SWORDSMAN);
		addInstanceCreatedId(TEMPLATE_ID);
		addInstanceEnterId(TEMPLATE_ID);
		addInstanceLeaveId(TEMPLATE_ID);
		setCreatureSeeId(this::onCreatureSee, PLAYER_DETECTOR);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcVars = npc.getVariables();
			
			switch (event)
			{
				case "FIRE_REGION_TIMER_1":
				{
					addSkillCastDesire(npc, npc, FIRE_SKILL_1, 23);
					getTimers().addTimer("FIRE_REGION_TIMER_2", 10000, npc, null);
					break;
				}
				case "FIRE_REGION_TIMER_2":
				{
					addSkillCastDesire(npc, npc, FIRE_SKILL_2, 23);
					getTimers().addTimer("FIRE_REGION_TIMER_3", 10000, npc, null);
					break;
				}
				case "FIRE_REGION_TIMER_3":
				{
					addSkillCastDesire(npc, npc, FIRE_SKILL_3, 23);
					getTimers().addTimer("FIRE_REGION_TIMER_3", 1000, npc, null);
					break;
				}
				case "TRAP_REACT_TIMER":
				{
					final int timer = npcVars.increaseInt("TIMER_VAL", -1);
					if (timer > 0)
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, " " + timer);
					}
					else
					{
						if (npc.getId() == TRAP_1)
						{
							addSkillCastDesire(npc, npc, (getRandom(10) < 8 ? TRAP_SKILL_1 : TRAP_SKILL_3), 23);
						}
						else if (npc.getId() == TRAP_2)
						{
							addSkillCastDesire(npc, npc, (getRandom(10) < 8 ? TRAP_SKILL_2 : TRAP_SKILL_3), 23);
						}
					}
					getTimers().addTimer("TRAP_REACT_TIMER", 1000, npc, null);
					break;
				}
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterInstance"))
		{
			enterInstance(player, npc, TEMPLATE_ID);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		instance.setStatus(1);
		for (int i = 0; i < 6; i++)
		{
			final ZoneType zone = ZoneManager.getInstance().getZoneByName("24_24_fire_telezone_0" + i, TeleportZone.class);
			if (zone != null)
			{
				zone.setEnabled(false, instance.getId());
			}
		}
		super.onInstanceCreated(instance, player);
	}
	
	@Override
	public void onInstanceEnter(PlayerInstance player, Instance instance)
	{
		final int startTime = (int) (instance.getElapsedTime() / 1000);
		final int endTime = (int) (instance.getRemainingTime() / 1000);
		player.sendPacket(new ExSendUIEvent(player, false, true, startTime, endTime, NpcStringId.ELAPSED_TIME));
	}
	
	@Override
	public void onInstanceLeave(PlayerInstance player, Instance instance)
	{
		player.sendPacket(new ExSendUIEvent(player, true, true, 0, 0, NpcStringId.ELAPSED_TIME));
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcParams = npc.getParameters();
			
			switch (npc.getId())
			{
				case SPIRIT_PROTECTOR:
				case VICIOUS_DUELER:
				case VICIOUS_WARRIOR:
				case VICIOUS_SWORDSMAN:
				{
					npc.setTargetable(false);
					npc.disableCoreAI(true);
					npc.setInvisible(true);
					break;
				}
				case FIRE_REGION:
				{
					final int timeLimit = npcParams.getInt("Limit_Time", 0);
					if (timeLimit > 0)
					{
						getTimers().addTimer("FIRE_REGION_TIMER_1", ((timeLimit * 30) * 100), npc, null);
					}
					npc.setTargetable(false);
					npc.setIsInvul(true);
					npc.setRandomAnimation(false);
					npc.setRandomWalking(false);
					npc.disableCoreAI(true);
					break;
				}
				case PLAYER_DETECTOR:
				{
					npc.initSeenCreatures();
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onEventReceived(String eventName, Npc sender, Npc receiver, WorldObject reference)
	{
		final Instance instance = receiver.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcParams = receiver.getParameters();
			
			if (eventName.equals(String.valueOf(24220005 + npcParams.getInt("Terri_ID", 0))))
			{
				receiver.setTargetable(true);
				receiver.disableCoreAI(false);
				receiver.setInvisible(false);
			}
		}
		return super.onEventReceived(eventName, sender, receiver, reference);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case TRAP_1:
				case TRAP_2:
				{
					if (npc.isScriptValue(0))
					{
						getTimers().addTimer("TRAP_REACT_TIMER", 1000, npc, null);
						npc.setScriptValue(1);
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcParams = npc.getParameters();
			final int killTarget = instance.getParameters().getInt("KILL_TARGET", 5);
			int currentKillCount = instance.getParameters().getInt("KILL_COUNT", 0);
			
			switch (npc.getId())
			{
				case VICIOUS_DUELER:
				case VICIOUS_WARRIOR:
				case VICIOUS_SWORDSMAN:
				{
					if (npcParams.getInt("last_checker", 0) == 1)
					{
						currentKillCount = instance.getParameters().increaseInt("KILL_COUNT", 0, 1);
						
						if (currentKillCount >= killTarget)
						{
							final ZoneType zone = ZoneManager.getInstance().getZoneByName(npc.getParameters().getString("AreaTeleName"), TeleportZone.class);
							if (zone != null)
							{
								zone.setEnabled(true, instance.getId());
								showOnScreenMsg(instance, NpcStringId.THE_PORTAL_TO_THE_NEXT_ROOM_IS_NOW_OPEN, ExShowScreenMessage.MIDDLE_CENTER, 4000);
								instance.spawnGroup("innadril23_mb2422_pt" + instance.getStatus() + "m1");
								instance.getParameters().set("KILL_COUNT", 0);
								
								switch (instance.getStatus())
								{
									case 1:
									{
										instance.getParameters().set("KILL_TARGET", 12);
										instance.setStatus(2);
										break;
									}
									case 2:
									{
										instance.getParameters().set("KILL_TARGET", 3);
										instance.setStatus(3);
										break;
									}
									case 3:
									{
										instance.getParameters().set("KILL_TARGET", 18);
										instance.setStatus(4);
										break;
									}
									case 4:
									{
										instance.getParameters().set("KILL_TARGET", 5);
										instance.setStatus(5);
										break;
									}
									case 5:
									{
										instance.getParameters().set("KILL_TARGET", 20);
										instance.setStatus(6);
										break;
									}
									case 6:
									{
										final int random = getRandom(100);
										int bossId = -1;
										
										if (random < 55)
										{
											bossId = KECHI_NORMAL;
										}
										else if (random < 80)
										{
											bossId = KECHI_WISE;
										}
										else if (random < 95)
										{
											bossId = KECHI_WEALTHY;
										}
										else
										{
											bossId = KECHI_ARMED;
										}
										
										addSpawn(bossId, BOSS_LOC, false, 0, false, instance.getId());
										break;
									}
								}
							}
							else
							{
								LOGGER.warning("Cannot find teleport zone for Crystal Cavern: Steam Corridor instance!!!");
							}
						}
					}
					break;
				}
				case KECHI_NORMAL:
				case KECHI_WISE:
				case KECHI_WEALTHY:
				case KECHI_ARMED:
				{
					instance.finishInstance();
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance) && creature.isPlayer())
		{
			final StatsSet npcParams = npc.getParameters();
			
			switch (npc.getId())
			{
				case PLAYER_DETECTOR:
				{
					if (npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						npc.broadcastEvent(String.valueOf(24220005 + npcParams.getInt("Terri_ID", 0)), 2000, null);
						
						for (int i = 0; i < getRandom(5); i++)
						{
							final Npc trap = addSpawn(((npcParams.getInt("MobType", 0) == 0) ? TRAP_1 : TRAP_2), npc, true, 0, false, instance.getId());
							trap.getVariables().set("TIMER_VAL", 4);
						}
						npc.deleteMe();
					}
					break;
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new CrystalCavernsSteamCorridor();
	}
}