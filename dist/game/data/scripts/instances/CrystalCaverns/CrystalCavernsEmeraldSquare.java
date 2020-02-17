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

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;

/**
 * Crystal Caverns - Emerald Square instance zone.
 * @author St3eT
 */
public class CrystalCavernsEmeraldSquare extends AbstractInstance
{
	// NPCs
	private static final int CAVERNS_ENTRACE = 33522;
	private static final int VERIDAN_NORMAL = 25796;
	private static final int VERIDAN_WISE = 26107;
	private static final int VERIDAN_WEALTHY = 26106;
	private static final int VERIDAN_ARMED = 26105;
	private static final int WATER_CANNON = 19008;
	private static final int WATER_CANNON_SKILL = 19009;
	private static final int STRONGHOLD_PROTECTOR = 23012;
	private static final int SQUARE_INTRUDER = 23010;
	private static final int SQUARE_ATTACKER = 23011;
	// Skills
	private static final SkillHolder DESTROY_SKILL = new SkillHolder(12003, 1);
	private static final SkillHolder WATER_CANNON_SKILL_ATTACK = new SkillHolder(14179, 1);
	// Locations
	private static final Location[] BOSS_SPAWNS =
	{
		new Location(152745, 145957, -12584, 16446),
		new Location(152816, 145968, -12633, 16446),
	};
	// Misc
	private static final int TEMPLATE_ID = 163;
	private static final int RAID_DOOR_1 = 24220005;
	private static final int RAID_DOOR_2 = 24220006;
	
	public CrystalCavernsEmeraldSquare()
	{
		super(TEMPLATE_ID);
		addStartNpc(CAVERNS_ENTRACE);
		addTalkId(CAVERNS_ENTRACE);
		addSpawnId(WATER_CANNON);
		addKillId(WATER_CANNON, VERIDAN_NORMAL, VERIDAN_WISE, VERIDAN_WEALTHY, VERIDAN_ARMED);
		addAttackId(WATER_CANNON, VERIDAN_NORMAL, VERIDAN_WISE, VERIDAN_WEALTHY, VERIDAN_ARMED);
		addSpellFinishedId(WATER_CANNON_SKILL);
		addInstanceEnterId(TEMPLATE_ID);
		addInstanceLeaveId(TEMPLATE_ID);
		setCreatureSeeId(this::onCreatureSee, WATER_CANNON);
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
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcParams = npc.getParameters();
			final StatsSet npcVars = npc.getVariables();
			final int baseId = npcParams.getInt("base_id", -1);
			
			switch (event)
			{
				case "HP_REGEN_TIMER":
				{
					int value = ((baseId == 5) || (baseId == 6)) ? 5 : baseId;
					npc.getStat().addFixedValue(Stats.REGENERATE_HP_RATE, Double.valueOf(value * 1000));
					break;
				}
				case "SUPPORT_SPAWN_TIMER":
				{
					int supportVal = npcVars.getInt("SUPPORT_VALUE", 0);
					
					if (supportVal > 3)
					{
						return;
					}
					
					if ((supportVal == 0) || (supportVal == 1) || (supportVal == 2))
					{
						final String spawnName = npcParams.getString("SupportMaker" + (supportVal + 1), null);
						if (spawnName != null)
						{
							instance.spawnGroup(spawnName);
						}
						npcVars.increaseInt("SUPPORT_VALUE", 1);
					}
					
					if (!npcVars.getBoolean("PREVIOUS_BASE_DESTROYED", false))
					{
						getTimers().addTimer("SUPPORT_SPAWN_TIMER", 60000, npc, null);
					}
					break;
				}
				case "CANNON_LOOP_ATTACK":
				{
					if (npc.getCurrentHpPercent() > 30)
					{
						if (npcVars.getBoolean("IS_DESTROY_ACTIVATED", false) || (getRandom(10) < 2))
						{
							final Npc cannonSkill = addSpawn(WATER_CANNON_SKILL, npc, true, 0, false, instance.getId());
							addSkillCastDesire(cannonSkill, cannonSkill, WATER_CANNON_SKILL_ATTACK, 23);
						}
					}
					break;
				}
				case "SUICIDE_TIMER":
				{
					npc.doDie(null);
					break;
				}
			}
		}
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcVars = npc.getVariables();
			
			switch (npc.getId())
			{
				case VERIDAN_WISE:
				case VERIDAN_WEALTHY:
				case VERIDAN_ARMED:
				{
					if (!npcVars.getBoolean("CLOSED_DOORS", false))
					{
						npcVars.set("CLOSED_DOORS", true);
						instance.openCloseDoor(RAID_DOOR_2, false);
					}
				}
				case WATER_CANNON:
				{
					if ((skill != null) && (skill.getId() == DESTROY_SKILL.getSkillId()) && !npcVars.getBoolean("IS_DESTROY_ACTIVATED", false))
					{
						npcVars.set("IS_DESTROY_ACTIVATED", true);
						npc.setDisplayEffect(2);
						getTimers().addTimer("SUICIDE_TIMER", 60000, npc, null);
					}
					
					if (npc.getCurrentHpPercent() < 30)
					{
						if (!npcVars.getBoolean("IS_DESTROY_ACTIVATED", false))
						{
							npc.setDisplayEffect(3);
							
						}
					}
					else if (!npcVars.getBoolean("IS_DESTROY_ACTIVATED", false))
					{
						npc.setDisplayEffect(1);
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
			final int baseId = npcParams.getInt("base_id", -1);
			
			switch (npc.getId())
			{
				case VERIDAN_NORMAL:
				case VERIDAN_WISE:
				case VERIDAN_WEALTHY:
				case VERIDAN_ARMED:
				{
					if (instance.getAliveNpcs(VERIDAN_NORMAL, VERIDAN_WISE, VERIDAN_WEALTHY, VERIDAN_ARMED).isEmpty())
					{
						instance.finishInstance();
					}
					else
					{
						instance.setReenterTime();
					}
					break;
				}
				case WATER_CANNON:
				{
					npc.setDisplayEffect(4);
					showOnScreenMsg(instance, NpcStringId.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_S1, ExShowScreenMessage.MIDDLE_CENTER, 4000, String.valueOf(npc.getParameters().getInt("base_id", -1)));
					
					World.getInstance().forEachVisibleObjectInRange(npc, MonsterInstance.class, 400, monster ->
					{
						if ((monster.getId() == STRONGHOLD_PROTECTOR) || (monster.getId() == SQUARE_INTRUDER) || (monster.getId() == SQUARE_ATTACKER))
						{
							monster.doDie(null);
						}
					});
					
					instance.getAliveNpcs(WATER_CANNON).forEach(cannon ->
					{
						final int cannonBaseId = cannon.getParameters().getInt("base_id", -1);
						
						switch (baseId)
						{
							case 1:
							{
								if (cannonBaseId == 2)
								{
									cannon.getVariables().set("PREVIOUS_BASE_DESTROYED", true);
									cannon.setTargetable(true);
								}
								break;
							}
							case 2:
							{
								if (cannonBaseId == 3)
								{
									cannon.getVariables().set("PREVIOUS_BASE_DESTROYED", true);
									cannon.setTargetable(true);
								}
								break;
							}
							case 3:
							{
								if (cannonBaseId == 4)
								{
									cannon.getVariables().set("PREVIOUS_BASE_DESTROYED", true);
									cannon.setTargetable(true);
								}
								break;
							}
							case 4:
							{
								if ((cannonBaseId == 5) || (cannonBaseId == 6))
								{
									cannon.getVariables().set("PREVIOUS_BASE_DESTROYED", true);
									cannon.setTargetable(true);
								}
								break;
							}
							case 5:
							case 6:
							{
								if (cannonBaseId == 7)
								{
									cannon.getVariables().set("PREVIOUS_BASE_DESTROYED", true);
									cannon.setTargetable(true);
								}
								break;
							}
							case 7:
							{
								if ((cannonBaseId == 8) || (cannonBaseId == 9))
								{
									cannon.getVariables().set("PREVIOUS_BASE_DESTROYED", true);
									cannon.setTargetable(true);
								}
								break;
							}
						}
					});
					
					if ((baseId == 8) || (baseId == 9))
					{
						instance.getParameters().increaseInt("MAIN_TARGETS_KILLED", 0, 1);
						
						if (instance.getParameters().getInt("MAIN_TARGETS_KILLED", 0) == 2)
						{
							showOnScreenMsg(instance, NpcStringId.SUCCESSFUL_DESTRUCTION_OF_STRONGHOLD_ENTRY_ACCESSED, ExShowScreenMessage.MIDDLE_CENTER, 4000);
							instance.openCloseDoor(RAID_DOOR_1, true);
							instance.openCloseDoor(RAID_DOOR_2, true);
							
							final int random = getRandom(100);
							int bossId = -1;
							
							if (random < 55)
							{
								bossId = VERIDAN_NORMAL;
							}
							else if (random < 80)
							{
								bossId = VERIDAN_WISE;
							}
							else if (random < 95)
							{
								bossId = VERIDAN_WEALTHY;
							}
							else
							{
								bossId = VERIDAN_ARMED;
							}
							
							for (Location loc : BOSS_SPAWNS)
							{
								addSpawn(bossId, loc, false, 0, false, instance.getId());
							}
						}
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance) && (npc.getId() == WATER_CANNON_SKILL) && (skill.getId() == WATER_CANNON_SKILL_ATTACK.getSkillId()))
		{
			npc.deleteMe();
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case WATER_CANNON:
				{
					final StatsSet npcParams = npc.getParameters();
					final int baseId = npcParams.getInt("base_id", -1);
					
					if (baseId != 1)
					{
						npc.setTargetable(false);
					}
					
					getTimers().addTimer("HP_REGEN_TIMER", 10000, npc, null);
					
					if (baseId > 0)
					{
						getTimers().addTimer("SUPPORT_SPAWN_TIMER", (baseId * 60) * 1000, npc, null);
					}
					npc.initSeenCreatures();
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	public void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance world = npc.getInstanceWorld();
		
		if ((world != null) && creature.isPlayer() && npc.isScriptValue(0))
		{
			npc.setScriptValue(1);
			npc.setDisplayEffect(1);
			getTimers().addRepeatingTimer("CANNON_LOOP_ATTACK", 1000, npc, null);
		}
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
	
	public static void main(String[] args)
	{
		new CrystalCavernsEmeraldSquare();
	}
}