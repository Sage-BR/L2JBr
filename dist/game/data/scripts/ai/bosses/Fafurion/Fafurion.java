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
package ai.bosses.Fafurion;

import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.gameserver.instancemanager.GrandBossManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class Fafurion extends AbstractNpcAI
{
	// NPCs
	private static final int HEART_OF_TSUNAMI = 34488;
	private static final int FAFURION_GRANDBOSS_ID = 19740;
	private static final int FAFURION_FINAL_FORM = 29367;
	// Item
	private static final int FONDUS_STONE = 80322;
	// Locations
	private static final Location RAID_ENTER_LOC = new Location(180059, 212896, -14727);
	private static final Location FAFURION_SPAWN_LOC = new Location(180712, 210664, -14823, 22146);
	// Status
	private static final int ALIVE = 0;
	private static final int WAITING = 1;
	private static final int FIGHTING = 2;
	private static final int DEAD = 3;
	// Misc
	private static final int RAID_DURATION = 5; // hours
	private static Npc bossInstance;
	
	private Fafurion()
	{
		addStartNpc(HEART_OF_TSUNAMI);
		addTalkId(HEART_OF_TSUNAMI);
		addFirstTalkId(HEART_OF_TSUNAMI);
		addKillId(FAFURION_FINAL_FORM);
		// Unlock
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(FAFURION_GRANDBOSS_ID);
		final int status = GrandBossManager.getInstance().getBossStatus(FAFURION_GRANDBOSS_ID);
		if (status == DEAD)
		{
			final long time = info.getLong("respawn_time") - System.currentTimeMillis();
			if (time > 0)
			{
				startQuestTimer("unlock_fafurion", time, null, null);
			}
			else
			{
				GrandBossManager.getInstance().setBossStatus(FAFURION_GRANDBOSS_ID, ALIVE);
			}
		}
		else if (status != ALIVE)
		{
			GrandBossManager.getInstance().setBossStatus(FAFURION_GRANDBOSS_ID, ALIVE);
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "unlock_fafurion":
			{
				GrandBossManager.getInstance().setBossStatus(FAFURION_GRANDBOSS_ID, ALIVE);
				break;
			}
			case "warning":
			{
				if (player.calculateDistance2D(FAFURION_SPAWN_LOC) < 5000)
				{
					player.sendPacket(new ExShowScreenMessage(NpcStringId.ALL_WHO_FEAR_FAFURION_LEAVE_THIS_PLACE_AT_ONCE, ExShowScreenMessage.TOP_CENTER, 10000, true));
					for (PlayerInstance plr : World.getInstance().getVisibleObjectsInRange(player, PlayerInstance.class, 5000))
					{
						plr.sendPacket(new ExShowScreenMessage(NpcStringId.ALL_WHO_FEAR_FAFURION_LEAVE_THIS_PLACE_AT_ONCE, ExShowScreenMessage.TOP_CENTER, 10000, true));
					}
				}
				break;
			}
			case "beginning":
			{
				if (GrandBossManager.getInstance().getBossStatus(FAFURION_GRANDBOSS_ID) == WAITING)
				{
					GrandBossManager.getInstance().setBossStatus(FAFURION_GRANDBOSS_ID, FIGHTING);
					bossInstance = addSpawn(FAFURION_FINAL_FORM, FAFURION_SPAWN_LOC.getX(), FAFURION_SPAWN_LOC.getY(), FAFURION_SPAWN_LOC.getZ(), FAFURION_SPAWN_LOC.getHeading(), false, 0, false);
					startQuestTimer("resetRaid", RAID_DURATION * 60 * 60 * 1000, bossInstance, null);
				}
				break;
			}
			case "resetRaid":
			{
				final int status = GrandBossManager.getInstance().getBossStatus(FAFURION_GRANDBOSS_ID);
				if ((status > ALIVE) && (status < DEAD))
				{
					for (PlayerInstance plr : World.getInstance().getVisibleObjectsInRange(npc, PlayerInstance.class, 5000))
					{
						plr.sendPacket(new ExShowScreenMessage(NpcStringId.EXCEEDED_THE_FAFURION_S_NEST_RAID_TIME_LIMIT, ExShowScreenMessage.TOP_CENTER, 10000, true));
					}
					GrandBossManager.getInstance().setBossStatus(FAFURION_GRANDBOSS_ID, ALIVE);
					npc.deleteMe();
				}
				break;
			}
			case "enter_area":
			{
				final int status = GrandBossManager.getInstance().getBossStatus(FAFURION_GRANDBOSS_ID);
				if (player.isGM())
				{
					player.teleToLocation(RAID_ENTER_LOC, true);
				}
				else
				{
					if (((status > ALIVE) && (status < DEAD)) || (status == DEAD))
					{
						return "34488-02.html";
					}
					if (!player.isInParty())
					{
						return "34488-01.html";
					}
					final Party party = player.getParty();
					final boolean isInCC = party.isInCommandChannel();
					final List<PlayerInstance> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
					final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
					if (!isPartyLeader)
					{
						return "34488-02.html";
					}
					if ((members.size() < Config.FAFURION_MIN_PLAYERS) || (members.size() > Config.FAFURION_MAX_PLAYERS))
					{
						return "34488-01.html";
					}
					for (PlayerInstance member : members)
					{
						if (member.getLevel() < Config.FAFURION_MIN_PLAYER_LVL)
						{
							return "34488-01.html";
						}
					}
					if (!hasQuestItems(player, FONDUS_STONE))
					{
						// TODO: Retail message.
						player.sendMessage("You need to own a fondus stone.");
						return null;
					}
					takeItems(player, FONDUS_STONE, 1);
					for (PlayerInstance member : members)
					{
						if ((member.calculateDistance2D(npc) < 1000) && (npc.getId() == HEART_OF_TSUNAMI))
						{
							member.teleToLocation(RAID_ENTER_LOC, true);
						}
					}
				}
				if (status == ALIVE)
				{
					GrandBossManager.getInstance().setBossStatus(FAFURION_GRANDBOSS_ID, WAITING);
					startQuestTimer("beginning", Config.FAFURION_WAIT_TIME * 60000, null, null);
					startQuestTimer("warning", Config.FAFURION_WAIT_TIME > 0 ? (Config.FAFURION_WAIT_TIME * 60000) - 30000 : 0, null, player);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		for (PlayerInstance player : World.getInstance().getVisibleObjectsInRange(npc, PlayerInstance.class, 5000))
		{
			player.sendPacket(new ExShowScreenMessage(NpcStringId.HONORED_WARRIORS_HAVE_DEFEATED_THE_WATER_DRAGON_FAFURION, ExShowScreenMessage.TOP_CENTER, 10000, true));
		}
		
		GrandBossManager.getInstance().setBossStatus(FAFURION_GRANDBOSS_ID, DEAD);
		final long respawnTime = (Config.FAFURION_SPAWN_INTERVAL + getRandom(-Config.FAFURION_SPAWN_RANDOM, Config.FAFURION_SPAWN_RANDOM)) * 3600000;
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(FAFURION_GRANDBOSS_ID);
		info.set("respawn_time", System.currentTimeMillis() + respawnTime);
		GrandBossManager.getInstance().setStatsSet(FAFURION_GRANDBOSS_ID, info);
		startQuestTimer("unlock_fafurion", respawnTime, null, null);
		
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "34488.html";
	}
	
	public static void main(String[] args)
	{
		new Fafurion();
	}
}
