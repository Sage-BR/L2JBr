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
package ai.bosses.Ramona;

import java.util.ArrayList;

import org.l2jbr.Config;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jbr.gameserver.instancemanager.MapRegionManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.TeleportWhereType;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.DoorInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestTimer;
import org.l2jbr.gameserver.model.zone.type.EffectZone;
import org.l2jbr.gameserver.model.zone.type.NoSummonFriendZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

import ai.AbstractNpcAI;

/**
 * Ramona RB
 * @author Gigi
 * @date 2017-04-09 - [10:22:38]
 */
public class Ramona extends AbstractNpcAI
{
	// Status
	private static enum Status
	{
		ALIVE,
		IN_FIGHT,
		DEAD
	}
	
	// NPC
	private static final int MP_CONTROL = 19642;
	private static final int RAMONA = 19648;
	private static final int RAMONA_1 = 26141;
	private static final int RAMONA_2 = 26142;
	private static final int RAMONA_3 = 26143;
	private static final int[] MINION_LIST =
	{
		26144, // Dancer of the Queen
		26145, // Commander of the Queen
		26146, // Shooter of the Queen
		26147 // Wizard of the Queen
	};
	// Trigers
	private static final int FIRST_GENERATOR = 22230702;
	private static final int SECOND_GENERATOR = 22230704;
	private static final int THRID_GENERATOR = 22230706;
	private static final int FOURTH_GENERATOR = 22230708;
	// Locations
	private static final Location DEFAULT_LOC = new Location(86338, 172099, -10602, 16383);
	private static final Location RAMONA_SPAWN_LOC = new Location(86327, 169759, -10465, 16383);
	// Other
	private static final int ROOM_CONTROL_DOOR = 22230711;
	private static final NoSummonFriendZone ZONE = ZoneManager.getInstance().getZoneById(210108, NoSummonFriendZone.class);
	private static final EffectZone ZONE_ATTACK = ZoneManager.getInstance().getZoneById(200109, EffectZone.class);
	private static final EffectZone ZONE_DEFENCE = ZoneManager.getInstance().getZoneById(200110, EffectZone.class);
	private static final EffectZone ZONE_HP = ZoneManager.getInstance().getZoneById(200111, EffectZone.class);
	private static final EffectZone ZONE_ERADICATION = ZoneManager.getInstance().getZoneById(200112, EffectZone.class);
	// Vars
	private static final String RAMONA_RESPAWN_VAR = "RamonaRespawn";
	private static Status _boss = Status.ALIVE;
	private static ArrayList<Npc> _minions = new ArrayList<>();
	private static long _lastAction;
	private static Npc _ramona1;
	private static Npc _ramona2;
	private static Npc _ramona3;
	
	private Ramona()
	{
		addStartNpc(MP_CONTROL);
		addKillId(MP_CONTROL, RAMONA_3);
		addSeeCreatureId(MP_CONTROL);
		addAttackId(MP_CONTROL, RAMONA_1, RAMONA_2, RAMONA_3);
		addSpawnId(RAMONA_1, RAMONA_2, RAMONA_3);
		
		final long temp = GlobalVariablesManager.getInstance().getLong(RAMONA_RESPAWN_VAR, 0) - System.currentTimeMillis();
		if (temp > 0)
		{
			_boss = Status.DEAD;
			startQuestTimer("RAMONA_UNLOCK", temp, null, null);
		}
		else
		{
			addSpawn(MP_CONTROL, RAMONA_SPAWN_LOC, false, 0, false);
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "RAMONA_UNLOCK":
			{
				_boss = Status.ALIVE;
				addSpawn(MP_CONTROL, RAMONA_SPAWN_LOC, false, 0, false);
				break;
			}
			case "SPAWN_MS":
			{
				if (ZONE.getCharactersInside().size() >= Config.RAMONA_MIN_PLAYER)
				{
					npc.setIsInvul(false);
					cancelQuestTimers("SPAWN_MS");
					startQuestTimer("CHECK_ACTIVITY_TASK", 5000, null, null);
					_lastAction = System.currentTimeMillis();
				}
				break;
			}
			case "SPAWN_RAMONA_1":
			{
				World.getInstance().forEachVisibleObjectInRange(npc, Npc.class, 3000, ramona ->
				{
					if (ramona.getId() == RAMONA)
					{
						ramona.deleteMe();
					}
				});
				_ramona1 = addSpawn(RAMONA_1, RAMONA_SPAWN_LOC, false, 1200000, true);
				startQuestTimer("GENERATOR_1", getRandom(300000, 600000), null, null);
				startQuestTimer("GENERATOR_2", getRandom(900000, 1200000), null, null);
				startQuestTimer("GENERATOR_3", getRandom(1500000, 1800000), null, null);
				startQuestTimer("GENERATOR_4", getRandom(2100000, 2400000), null, null);
				_lastAction = System.currentTimeMillis();
				break;
			}
			case "GENERATOR_1":
			{
				ZONE.broadcastPacket(new OnEventTrigger(FIRST_GENERATOR, true));
				ZONE_ATTACK.setEnabled(true);
				break;
			}
			case "GENERATOR_2":
			{
				ZONE.broadcastPacket(new OnEventTrigger(SECOND_GENERATOR, true));
				ZONE_DEFENCE.setEnabled(true);
				break;
			}
			case "GENERATOR_3":
			{
				ZONE.broadcastPacket(new OnEventTrigger(THRID_GENERATOR, true));
				ZONE_HP.setEnabled(true);
				break;
			}
			case "GENERATOR_4":
			{
				ZONE.broadcastPacket(new OnEventTrigger(FOURTH_GENERATOR, true));
				ZONE_ERADICATION.setEnabled(true);
				break;
			}
			case "CHECK_ACTIVITY_TASK":
			{
				if ((_lastAction + 900000) < System.currentTimeMillis())
				{
					for (Creature charInside : ZONE.getCharactersInside())
					{
						if (charInside != null)
						{
							if (charInside.isNpc())
							{
								charInside.deleteMe();
							}
							else if (charInside.isPlayer())
							{
								charInside.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(charInside, TeleportWhereType.TOWN));
							}
						}
					}
					startQuestTimer("END_RAMONA", 2000, null, null);
				}
				else
				{
					startQuestTimer("CHECK_ACTIVITY_TASK", 30000, null, null);
				}
				break;
			}
			case "END_RAMONA":
			{
				ZONE.oustAllPlayers();
				if (_ramona1 != null)
				{
					_ramona1.deleteMe();
				}
				if (_ramona2 != null)
				{
					_ramona2.deleteMe();
				}
				if (_ramona3 != null)
				{
					_ramona3.deleteMe();
				}
				if (!_minions.isEmpty())
				{
					for (Npc minion : _minions)
					{
						if (minion == null)
						{
							continue;
						}
						minion.deleteMe();
					}
				}
				if ((_boss == Status.ALIVE) || (_boss == Status.IN_FIGHT))
				{
					addSpawn(MP_CONTROL, RAMONA_SPAWN_LOC, false, 0, false);
				}
				QuestTimer activityTimer = getQuestTimer("CHECK_ACTIVITY_TASK", null, null);
				if (activityTimer != null)
				{
					activityTimer.cancel();
				}
				for (int i = FIRST_GENERATOR; i <= FOURTH_GENERATOR; i++)
				{
					ZONE.broadcastPacket(new OnEventTrigger(i, false));
				}
				ZONE_ATTACK.setEnabled(false);
				ZONE_DEFENCE.setEnabled(false);
				ZONE_HP.setEnabled(false);
				ZONE_ERADICATION.setEnabled(false);
				cancelQuestTimers("GENERATOR_1");
				cancelQuestTimers("GENERATOR_2");
				cancelQuestTimers("GENERATOR_3");
				cancelQuestTimers("GENERATOR_4");
				addSpawn(RAMONA, DEFAULT_LOC, false, 0, false);
				_minions.clear();
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		switch (npc.getId())
		{
			case MP_CONTROL:
			{
				if (ZONE.getCharactersInside().size() < Config.RAMONA_MIN_PLAYER)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHAT_S_UP_WITH_YOUR_EYES_YOU_NEED_MORE_ENERGY);
				}
				break;
			}
			case RAMONA_1:
			{
				if (npc.getCurrentHpPercent() < 75)
				{
					playMovie(ZONE.getPlayersInside(), Movie.SC_RAMONA_TRANS_A);
					_ramona2 = addSpawn(RAMONA_2, RAMONA_SPAWN_LOC, false, 1200000, false);
					_ramona2.setCurrentHp(_ramona1.getCurrentHp());
					_ramona1.deleteMe();
					for (int i = 0; i < 7; i++)
					{
						final Npc minion = addSpawn(MINION_LIST[Rnd.get(MINION_LIST.length)], npc.getX() + getRandom(-200, 200), npc.getY() + getRandom(-200, 200), npc.getZ(), npc.getHeading(), false, 600000);
						minion.isRunning();
						((Attackable) minion).setIsRaidMinion(true);
						addAttackPlayerDesire(minion, attacker);
						_minions.add(minion);
					}
				}
				break;
			}
			case RAMONA_2:
			{
				if (npc.getCurrentHpPercent() < 50)
				{
					playMovie(ZONE.getPlayersInside(), Movie.SC_RAMONA_TRANS_B);
					_ramona3 = addSpawn(RAMONA_3, RAMONA_SPAWN_LOC, false, 1200000, false);
					_ramona3.setCurrentHp(_ramona2.getCurrentHp());
					_ramona2.deleteMe();
					for (int i = 0; i < 7; i++)
					{
						final Npc minion = addSpawn(MINION_LIST[Rnd.get(MINION_LIST.length)], npc.getX() + getRandom(-200, 200), npc.getY() + getRandom(-200, 200), npc.getZ(), npc.getHeading(), false, 600000);
						minion.isRunning();
						((Attackable) minion).setIsRaidMinion(true);
						addAttackPlayerDesire(minion, attacker);
						_minions.add(minion);
					}
				}
				break;
			}
			case RAMONA_3:
			{
				if ((npc.getCurrentHpPercent() < 25) && npc.isScriptValue(2))
				{
					_lastAction = System.currentTimeMillis();
					npc.setScriptValue(1);
				}
				break;
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case MP_CONTROL:
			{
				World.getInstance().forEachVisibleObjectInRange(npc, DoorInstance.class, 8000, door ->
				{
					if (door.getId() == ROOM_CONTROL_DOOR)
					{
						door.closeMe();
					}
				});
				startQuestTimer("SPAWN_RAMONA_1", 10000, npc, null);
				break;
			}
			case RAMONA_3:
			{
				_boss = Status.DEAD;
				long respawnTime = (Config.RAMONA_SPAWN_INTERVAL + getRandom(-Config.RAMONA_SPAWN_RANDOM, Config.RAMONA_SPAWN_RANDOM)) * 3600000;
				GlobalVariablesManager.getInstance().set(RAMONA_RESPAWN_VAR, System.currentTimeMillis() + respawnTime);
				startQuestTimer("RAMONA_UNLOCK", respawnTime, null, null);
				startQuestTimer("END_RAMONA", 90000, null, null);
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		npc.setIsInvul(true);
		if (creature.isPlayer())
		{
			startQuestTimer("SPAWN_MS", 10000, npc, null, true);
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case RAMONA_1:
			{
				_boss = Status.IN_FIGHT;
				_lastAction = System.currentTimeMillis();
				break;
			}
			case RAMONA_2:
			case RAMONA_3:
			{
				_lastAction = System.currentTimeMillis();
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Ramona();
	}
}