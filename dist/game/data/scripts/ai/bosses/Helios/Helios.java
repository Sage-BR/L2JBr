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
package ai.bosses.Helios;

import org.l2jbr.Config;
import org.l2jbr.gameserver.instancemanager.GrandBossManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.zone.type.NoSummonFriendZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class Helios extends AbstractNpcAI
{
	// NPCs
	private static final int HELIOS1 = 29303;
	private static final int HELIOS2 = 29304;
	private static final int HELIOS3 = 29305;
	// Location
	private static final Location HELIOS_SPAWN_LOC = new Location(92771, 161909, 3494, 38329);
	// Zone
	private static final int ZONE_ID = 210109;
	// Status
	private static final int ALIVE = 0;
	private static final int WAITING = 1;
	private static final int FIGHTING = 2;
	private static final int DEAD = 3;
	// Misc
	private static final int HELIOS_RAID_DURATION = 5; // hours
	private static Npc bossInstance;
	private final NoSummonFriendZone bossZone;
	
	private Helios()
	{
		addKillId(HELIOS1, HELIOS2, HELIOS3);
		// Zone
		bossZone = ZoneManager.getInstance().getZoneById(ZONE_ID, NoSummonFriendZone.class);
		// Unlock
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(HELIOS3);
		final int status = GrandBossManager.getInstance().getBossStatus(HELIOS3);
		if (status == DEAD)
		{
			final long time = info.getLong("respawn_time") - System.currentTimeMillis();
			if (time > 0)
			{
				startQuestTimer("unlock_helios", time, null, null);
			}
			else
			{
				GrandBossManager.getInstance().setBossStatus(HELIOS3, ALIVE);
			}
		}
		else if (status != ALIVE)
		{
			GrandBossManager.getInstance().setBossStatus(HELIOS3, ALIVE);
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "unlock_helios":
			{
				GrandBossManager.getInstance().setBossStatus(HELIOS3, ALIVE);
				break;
			}
			case "beginning":
			{
				if (GrandBossManager.getInstance().getBossStatus(HELIOS3) == WAITING)
				{
					GrandBossManager.getInstance().setBossStatus(HELIOS3, FIGHTING);
					bossInstance = addSpawn(HELIOS1, HELIOS_SPAWN_LOC.getX(), HELIOS_SPAWN_LOC.getY(), HELIOS_SPAWN_LOC.getZ(), HELIOS_SPAWN_LOC.getHeading(), false, 0, false);
					startQuestTimer("resetRaid", HELIOS_RAID_DURATION * 60 * 60 * 1000, bossInstance, null);
				}
				break;
			}
			case "resetRaid":
			{
				final int status = GrandBossManager.getInstance().getBossStatus(HELIOS3);
				if ((status > ALIVE) && (status < DEAD))
				{
					GrandBossManager.getInstance().setBossStatus(HELIOS3, ALIVE);
					npc.deleteMe();
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case HELIOS1:
			{
				bossInstance.deleteMe();
				bossInstance = addSpawn(HELIOS2, HELIOS_SPAWN_LOC.getX(), HELIOS_SPAWN_LOC.getY(), HELIOS_SPAWN_LOC.getZ(), HELIOS_SPAWN_LOC.getHeading(), false, 0, false);
				bossZone.broadcastPacket(new ExShowScreenMessage(NpcStringId.HELIOS_APPEARANCE_CHANGES_AND_HE_BEGINS_TO_GROW_STRONGER, ExShowScreenMessage.TOP_CENTER, 10000, true));
				break;
			}
			case HELIOS2:
			{
				bossInstance.deleteMe();
				bossInstance = addSpawn(HELIOS3, HELIOS_SPAWN_LOC.getX(), HELIOS_SPAWN_LOC.getY(), HELIOS_SPAWN_LOC.getZ(), HELIOS_SPAWN_LOC.getHeading(), false, 0, false);
				break;
			}
			case HELIOS3:
			{
				bossZone.broadcastPacket(new ExShowScreenMessage(NpcStringId.HELIOS_DEFEATED_TAKES_FLIGHT_DEEP_IN_TO_THE_SUPERION_FORT_HIS_THRONE_IS_RENDERED_INACTIVE, ExShowScreenMessage.TOP_CENTER, 10000, true));
				GrandBossManager.getInstance().setBossStatus(HELIOS3, DEAD);
				final long respawnTime = (Config.HELIOS_SPAWN_INTERVAL + getRandom(-Config.HELIOS_SPAWN_RANDOM, Config.HELIOS_SPAWN_RANDOM)) * 3600000;
				final StatsSet info = GrandBossManager.getInstance().getStatsSet(HELIOS3);
				info.set("respawn_time", System.currentTimeMillis() + respawnTime);
				GrandBossManager.getInstance().setStatsSet(HELIOS3, info);
				startQuestTimer("unlock_helios", respawnTime, null, null);
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new Helios();
	}
}
