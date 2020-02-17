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
package instances.PrisonOfDarkness;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;

/**
 * Prison of Darkness instance zone.
 * @author St3eT
 */
public class PrisonOfDarkness extends AbstractInstance
{
	// NPCs
	private static final int SPEZION = 25779;
	private static final int SPEZION_HEADSTONE = 32945;
	private static final int WARP_POINT = 32947;
	private static final int EXIT_PORTAL = 32944;
	private static final int TIME_BOMB_1 = 32951;
	private static final int TIME_BOMB_2 = 32952;
	private static final int TIME_BOMB_3 = 32953;
	private static final int ESCORT_WARRIOR = 22983;
	private static final int SPEZIONS_PAWN = 22985;
	private static final int STARLIGHT_LATTICE = 32955;
	private static final int JOSEPHINA = 32956;
	private static final int[] MONSTERS =
	{
		19018,
		19019,
		19020
	};
	// Items
	private static final int GIANT_CANNONBALL = 17611;
	// Skill
	private static final SkillHolder TELEPORT = new SkillHolder(14139, 1);
	// Locations
	private static final Location ORBIS_LOCATION = new Location(213242, 53235, -8213);
	private static final Location TIME_BOMB_1_LOC = new Location(213242, 53235, -9213);
	private static final Location SPEZION_LAIR = new Location(184972, 144176, -11755);
	private static final Location SPEZION_LOC = new Location(184901, 143307, -11761);
	private static final Location[] WARP_POINT_RANDOM_LOCS =
	{
		new Location(212276, 115403, -816),
		new Location(213494, 116823, -831),
		new Location(219057, 112289, -1233),
		new Location(218010, 109434, -1223),
		new Location(217812, 119060, -1690),
		new Location(219287, 118906, -1671),
		new Location(210842, 120480, -1239),
		new Location(207996, 117838, -1239),
	};
	private static final Location[] PLAYERS_RANDOM_LOCS =
	{
		new Location(212423, 115695, -825),
		new Location(212445, 114285, -829),
		new Location(213656, 113256, -825),
		new Location(217769, 110626, -1268),
		new Location(218588, 110497, -1237),
		new Location(218591, 111554, -1235),
		new Location(218186, 120196, -1666),
		new Location(217028, 118864, -1670),
		new Location(209554, 118818, -1274),
		new Location(210374, 118688, -1243),
		new Location(209795, 117626, -1229),
	};
	private static final Location[] PLAYERS_TELEPORT_RANDOM_LOCS =
	{
		new Location(213003, 114101, -814),
		new Location(214256, 113238, -833),
		new Location(217713, 112080, -1233),
		new Location(218794, 109451, -1221),
		new Location(218232, 118350, -1675),
		new Location(218236, 117922, -1662),
		new Location(209498, 120271, -1239),
		new Location(208832, 117624, -1237),
	};
	// Misc
	private static final int TEMPLATE_ID = 159;
	private static final int DOOR_1 = 26190001;
	private static final int DOOR_2 = 26190006;
	private static final int DOOR_3 = 26190005;
	private static final int DOOR_4 = 26190004;
	
	public PrisonOfDarkness()
	{
		super(TEMPLATE_ID);
		addStartNpc(SPEZION_HEADSTONE);
		addTalkId(SPEZION_HEADSTONE, WARP_POINT, TIME_BOMB_1, STARLIGHT_LATTICE, JOSEPHINA);
		addSpawnId(WARP_POINT, EXIT_PORTAL, ESCORT_WARRIOR);
		addSpellFinishedId(MONSTERS);
		setCreatureSeeId(this::onCreatureSee, EXIT_PORTAL);
		addKillId(SPEZIONS_PAWN);
		addFirstTalkId(JOSEPHINA);
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
				case "CHANGE_POSITION":
				{
					if (npcVars.getBoolean("CAN_TELEPORT", true))
					{
						npc.teleToLocation(getRandomEntry(WARP_POINT_RANDOM_LOCS));
						showOnScreenMsg(instance, NpcStringId.THE_LOCATION_OF_THE_ESCAPE_DEVICE_IS_MOVED, ExShowScreenMessage.TOP_CENTER, 4000);
						
						switch (npcVars.getInt("TIME_MULTIPLER", 5))
						{
							case 5:
								npcVars.set("TIME_MULTIPLER", 7);
								break;
							case 7:
								npcVars.set("TIME_MULTIPLER", 10);
								break;
						}
						getTimers().addTimer("CHANGE_POSITION", (60000 * npcVars.getInt("TIME_MULTIPLER", 5)), npc, null);
						break;
					}
				}
				case "START_BOSS":
				{
					final Attackable spezion = (Attackable) addSpawn(SPEZION, SPEZION_LOC, true, 0, false, instance.getId());
					spezion.setCanReturnToSpawnPoint(false);
					showOnScreenMsg(instance, NpcStringId.SPEZION_S_STATUS_WILL_ONLY_CHANGE_WHEN_EXPOSED_TO_LIGHT, ExShowScreenMessage.TOP_CENTER, 4000);
					break;
				}
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance) && (npc.getId() == JOSEPHINA))
		{
			return npc.isScriptValue(0) ? "32956-01.html" : "32956-02.html";
		}
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterInstance"))
		{
			enterInstance(player, npc, TEMPLATE_ID);
		}
		else
		{
			final Instance instance = npc.getInstanceWorld();
			
			if (isInInstance(instance))
			{
				final StatsSet npcVars = npc.getVariables();
				
				switch (event)
				{
					case "leaveRecord":
					{
						if (npcVars.getBoolean("CAN_REGISTRED_PLAYER_" + player.getObjectId(), true))
						{
							npcVars.set("CAN_REGISTRED_PLAYER_" + player.getObjectId(), false);
							npcVars.increaseInt("PLAYERS_REGISTRED", 0, 1);
							
							if (npcVars.getInt("PLAYERS_REGISTRED", 0) >= instance.getParameters().getInt("PLAYERS_COUNT", 0))
							{
								npcVars.set("CAN_TELEPORT", false);
								
							}
							return "32947-01.html";
						}
						break;
					}
					case "escapeGog":
					{
						return npcVars.getInt("PLAYERS_REGISTRED", 0) >= instance.getParameters().getInt("PLAYERS_COUNT", 0) ? "32947-02.html" : "32947-03.html";
					}
					case "acceptTeleport":
					{
						if (player.isInParty())
						{
							for (PlayerInstance member : player.getParty().getMembers())
							{
								if (member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
								{
									member.teleToLocation(ORBIS_LOCATION);
								}
							}
							showOnScreenMsg(instance, NpcStringId.WHEN_THE_TIME_BOMB_IS_ACTIVATED_A_DOOR_OPENS_SOMEWHERE, ExShowScreenMessage.TOP_CENTER, 4000);
							addSpawn(TIME_BOMB_1, TIME_BOMB_1_LOC, false, 0, false, instance.getId());
							final Npc portal = instance.getAliveNpcs(EXIT_PORTAL).stream().findAny().orElse(null);
							if (portal != null)
							{
								portal.getVariables().set("PORTAL_STATE", 1);
							}
						}
						else if (instance.getParameters().getInt("PLAYERS_COUNT", 0) == 1)
						{
							player.teleToLocation(ORBIS_LOCATION);
							showOnScreenMsg(instance, NpcStringId.WHEN_THE_TIME_BOMB_IS_ACTIVATED_A_DOOR_OPENS_SOMEWHERE, ExShowScreenMessage.TOP_CENTER, 4000);
							addSpawn(TIME_BOMB_1, TIME_BOMB_1_LOC, false, 0, false, instance.getId());
							final Npc portal = instance.getAliveNpcs(EXIT_PORTAL).stream().findAny().orElse(null);
							if (portal != null)
							{
								portal.getVariables().set("PORTAL_STATE", 1);
							}
						}
						break;
					}
					case "activateBomb":
					{
						if (npc.isScriptValue(0))
						{
							switch (npc.getId())
							{
								case TIME_BOMB_1:
								{
									instance.openCloseDoor(DOOR_1, true);
									instance.spawnGroup("timebomb_1_guards");
									showOnScreenMsg(instance, NpcStringId.THE_DOOR_IS_OPEN_SOMEBODY_NEEDS_TO_STAY_TO_WATCH_THE_TIME_BOMB, ExShowScreenMessage.TOP_CENTER, 4000);
									break;
								}
								case TIME_BOMB_2:
								{
									instance.openCloseDoor(DOOR_1, false);
									instance.openCloseDoor(DOOR_2, true);
									instance.spawnGroup("timebomb_2_guards");
									showOnScreenMsg(instance, NpcStringId.THE_DOOR_IS_OPEN_SOMEBODY_NEEDS_TO_STAY_TO_WATCH_THE_TIME_BOMB, ExShowScreenMessage.TOP_CENTER, 4000);
									break;
								}
								case TIME_BOMB_3:
								{
									instance.openCloseDoor(DOOR_2, false);
									instance.openCloseDoor(DOOR_3, true);
									instance.spawnGroup("timebomb_3_guards");
									showOnScreenMsg(instance, NpcStringId.THE_DOOR_IS_OPEN_SOMEBODY_NEEDS_TO_STAY_TO_WATCH_THE_TIME_BOMB, ExShowScreenMessage.TOP_CENTER, 4000);
									break;
								}
							}
						}
						break;
					}
					case "spezionTeleport":
					{
						if (player.isInParty())
						{
							for (PlayerInstance member : player.getParty().getMembers())
							{
								if (member.getInstanceWorld() == instance)
								{
									member.teleToLocation(SPEZION_LAIR);
								}
							}
						}
						else
						{
							player.teleToLocation(SPEZION_LAIR);
						}
						
						final Npc portal = instance.getAliveNpcs(EXIT_PORTAL).stream().findAny().orElse(null);
						if (portal != null)
						{
							portal.getVariables().set("PORTAL_STATE", 2);
						}
						break;
					}
					case "spawnSpezion":
					{
						if (npcVars.getBoolean("CAN_SPAWN_SPEZION", true))
						{
							npcVars.set("CAN_SPAWN_SPEZION", false);
							playMovie(instance, Movie.SC_SPACIA_C);
							getTimers().addTimer("START_BOSS", 36000, npc, null);
						}
						break;
					}
					case "leaveInstance":
					{
						player.teleToLocation(instance.getExitLocation(player));
						break;
					}
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcVars = npc.getVariables();
			
			switch (npc.getId())
			{
				case WARP_POINT:
				{
					npc.teleToLocation(getRandomEntry(WARP_POINT_RANDOM_LOCS));
					getTimers().addTimer("CHANGE_POSITION", (60000 * npcVars.getInt("TIME_MULTIPLER", 5)), npc, null);
					break;
				}
				case EXIT_PORTAL:
				{
					npc.initSeenCreatures();
					break;
				}
				case ESCORT_WARRIOR:
				{
					// TODO: attack logic
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
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance) && (npc.getId() == EXIT_PORTAL))
		{
			final StatsSet npcVars = npc.getVariables();
			
			switch (npcVars.getInt("PORTAL_STATE", 0))
			{
				case 0:
					takeItems(creature.getActingPlayer(), GIANT_CANNONBALL, -1);
					creature.teleToLocation(getRandomEntry(PLAYERS_RANDOM_LOCS));
					showOnScreenMsg(creature.getActingPlayer(), NpcStringId.YOU_NEED_TO_FIND_ESCAPE_DEVICE_RE_ENTRY_IS_NOT_ALLOWED_ONCE_YOU_VE_LEFT_THE_INSTANT_ZONE, ExShowScreenMessage.TOP_CENTER, 4000);
					instance.getParameters().increaseInt("PLAYERS_COUNT", 0, 1);
					break;
				case 1:
					creature.teleToLocation(ORBIS_LOCATION);
					break;
				case 2:
					creature.teleToLocation(SPEZION_LAIR);
					break;
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance) && (npc.getId() == SPEZIONS_PAWN))
		{
			instance.openCloseDoor(DOOR_3, false);
			instance.openCloseDoor(DOOR_4, true);
			showOnScreenMsg(instance, NpcStringId.THE_DOOR_IS_OPEN, ExShowScreenMessage.TOP_CENTER, 4000);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			if ((skill == TELEPORT.getSkill()) && (player != null) && (npc.calculateDistance3D(player) < 1000) && (npc.getCurrentHpPercent() > 10))
			{
				player.teleToLocation(getRandomEntry(PLAYERS_TELEPORT_RANDOM_LOCS));
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	public static void main(String[] args)
	{
		new PrisonOfDarkness();
	}
}