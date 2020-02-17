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
package instances.ChambersOfDelusion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.IntStream;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.Earthquake;

import instances.AbstractInstance;

/**
 * Chambers of Delusion.
 * @author GKR
 */
public class ChamberOfDelusion extends AbstractInstance
{
	// NPCs
	private static final Map<Integer, Integer> ENTRANCE_GATEKEEPER = new HashMap<>();
	private static final int[] ROOM_GATEKEEPERS = IntStream.range(32664, 32702).toArray();
	private static final int[] AENKINEL =
	{
		25690, // East
		25691, // West
		25692, // South
		25693, // North
		25694, // Square
		25695, // Tower
	};
	private static final int[] BOX =
	{
		18838, // East, West, South, North
		18820, // Square
		18823, // Tower
	};
	// Items
	private static final int ENRIA = 4042;
	private static final int ASOFE = 4043;
	private static final int THONS = 4044;
	private static final int LEONARD = 9628;
	private static final int DELUSION_MARK = 15311;
	// Skills
	private static final SkillHolder SUCCESS_SKILL = new SkillHolder(5758, 1);
	private static final SkillHolder FAIL_SKILL = new SkillHolder(5376, 4);
	// Timers
	private static final int ROOM_CHANGE_INTERVAL = 480; // 8 min
	private static final int ROOM_CHANGE_RANDOM_TIME = 120; // 2 min
	
	static
	{
		ENTRANCE_GATEKEEPER.put(32658, 127); // East
		ENTRANCE_GATEKEEPER.put(32659, 128); // West
		ENTRANCE_GATEKEEPER.put(32660, 129); // South
		ENTRANCE_GATEKEEPER.put(32661, 130); // North
		ENTRANCE_GATEKEEPER.put(32662, 131); // Square
		ENTRANCE_GATEKEEPER.put(32663, 132); // Tower
	}
	
	public ChamberOfDelusion()
	{
		super(ENTRANCE_GATEKEEPER.values().stream().mapToInt(Integer::valueOf).toArray());
		addStartNpc(ENTRANCE_GATEKEEPER.keySet());
		addStartNpc(ROOM_GATEKEEPERS);
		addTalkId(ENTRANCE_GATEKEEPER.keySet());
		addTalkId(ROOM_GATEKEEPERS);
		addKillId(AENKINEL);
		addAttackId(BOX);
		addSpellFinishedId(BOX);
		addEventReceivedId(BOX);
		addInstanceCreatedId(ENTRANCE_GATEKEEPER.values());
		addInstanceDestroyId(ENTRANCE_GATEKEEPER.values());
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		// Choose start room
		changeRoom(instance);
		
		// Start banish task
		final ScheduledFuture<?> banishTask = ThreadPool.scheduleAtFixedRate(() ->
		{
			if (instance.getRemainingTime() < 60000)
			{
				final ScheduledFuture<?> task = instance.getParameters().getObject("banishTask", ScheduledFuture.class);
				task.cancel(false);
			}
			else
			{
				for (PlayerInstance plr : instance.getAllowed())
				{
					if ((plr != null) && plr.isOnline() && !plr.isInParty())
					{
						instance.finishInstance(0);
						break;
					}
				}
			}
		}, 60000, 60000);
		instance.setParameter("banishTask", banishTask);
	}
	
	@Override
	public void onInstanceDestroy(Instance instance)
	{
		// Stop room change task
		stopRoomChangeTask(instance);
		
		// Stop banish task
		final ScheduledFuture<?> banish = instance.getParameters().getObject("banishTask", ScheduledFuture.class);
		if (banish != null)
		{
			banish.cancel(true);
			instance.setParameter("banishTask", null);
		}
	}
	
	@Override
	protected void onEnter(PlayerInstance player, Instance instance, boolean firstEnter)
	{
		// Teleport player to instance
		super.onEnter(player, instance, firstEnter);
		
		// Take items
		if (firstEnter)
		{
			if (hasQuestItems(player, DELUSION_MARK))
			{
				takeItems(player, DELUSION_MARK, -1);
			}
			
			if (player.getParty().isLeader(player))
			{
				giveItems(player, DELUSION_MARK, 1);
			}
		}
	}
	
	@Override
	protected void teleportPlayerIn(PlayerInstance player, Instance instance)
	{
		final int room = instance.getParameters().getInt("currentRoom");
		final Location loc = instance.getEnterLocations().get(room);
		player.teleToLocation(loc, instance);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		final Instance world = npc.getInstanceWorld();
		if ((player != null) && (world != null) && CommonUtil.contains(ROOM_GATEKEEPERS, npc.getId()))
		{
			switch (event)
			{
				case "next_room":
				{
					if (player.isInParty())
					{
						htmltext = "no_party.html";
					}
					else if (player.getParty().isLeader(player))
					{
						htmltext = "no_leader.html";
					}
					else if (!hasQuestItems(player, DELUSION_MARK))
					{
						htmltext = "no_item.html";
					}
					else
					{
						takeItems(player, DELUSION_MARK, 1);
						stopRoomChangeTask(world);
						changeRoom(world);
					}
					break;
				}
				case "go_out":
				{
					if (player.isInParty())
					{
						htmltext = "no_party.html";
					}
					else if (player.getParty().isLeader(player))
					{
						htmltext = "no_leader.html";
					}
					else
					{
						world.finishInstance(0);
					}
					break;
				}
				case "look_party":
				{
					if (player.isInParty() && world.isAllowed(player))
					{
						teleportPlayerIn(player, world);
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final int npcId = npc.getId();
		if (ENTRANCE_GATEKEEPER.containsKey(npcId))
		{
			enterInstance(player, npc, ENTRANCE_GATEKEEPER.get(npcId));
		}
		return null;
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		if (!npc.isDead() && CommonUtil.contains(BOX, npc.getId()) && ((skill.getId() == FAIL_SKILL.getSkillId()) || (skill.getId() == SUCCESS_SKILL.getSkillId())))
		{
			npc.doDie(player);
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onEventReceived(String eventName, Npc sender, Npc receiver, WorldObject reference)
	{
		switch (eventName)
		{
			case "SCE_LUCKY":
			{
				receiver.setBusy(true);
				receiver.doCast(SUCCESS_SKILL.getSkill());
				break;
			}
			case "SCE_DREAM_FIRE_IN_THE_HOLE":
			{
				receiver.setBusy(true);
				receiver.doCast(FAIL_SKILL.getSkill());
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isPet, Skill skill)
	{
		if (!npc.isBusy() && (npc.getCurrentHp() < (npc.getMaxHp() / 10)))
		{
			npc.setBusy(true);
			if (getRandom(100) < 25) // 25% chance to reward
			{
				if (getRandom(100) < 33)
				{
					npc.dropItem(attacker, ENRIA, (int) (3 * Config.RATE_QUEST_DROP));
				}
				if (getRandom(100) < 50)
				{
					npc.dropItem(attacker, THONS, (int) (4 * Config.RATE_QUEST_DROP));
				}
				if (getRandom(100) < 50)
				{
					npc.dropItem(attacker, ASOFE, (int) (4 * Config.RATE_QUEST_DROP));
				}
				if (getRandom(100) < 16)
				{
					npc.dropItem(attacker, LEONARD, (int) (2 * Config.RATE_QUEST_DROP));
				}
				npc.broadcastEvent("SCE_LUCKY", 2000, null);
				npc.doCast(SUCCESS_SKILL.getSkill());
			}
			else
			{
				npc.broadcastEvent("SCE_DREAM_FIRE_IN_THE_HOLE", 2000, null);
			}
		}
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isPet)
	{
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			if (isBigChamber(world))
			{
				world.setReenterTime();
				if (world.getRemainingTime() > (Config.INSTANCE_FINISH_TIME * 60000))
				{
					world.finishInstance();
				}
			}
			else
			{
				stopRoomChangeTask(world);
				scheduleRoomChange(world, true);
			}
			world.spawnGroup("boxes");
		}
		return super.onKill(npc, player, isPet);
	}
	
	private boolean isBigChamber(Instance world)
	{
		return world.getTemplateParameters().getBoolean("isBigRoom");
	}
	
	private boolean isBossRoom(Instance world, int room)
	{
		return room == (world.getEnterLocations().size() - 1);
	}
	
	protected void changeRoom(Instance world)
	{
		final StatsSet params = world.getParameters();
		final List<Location> locations = world.getEnterLocations();
		
		int newRoom = params.getInt("currentRoom", 0);
		if (isBigChamber(world))
		{
			// Do nothing, if there are raid room of Sqare or Tower Chamber
			if (isBossRoom(world, newRoom))
			{
				return;
			}
			
			// Teleport to raid room 10 min or lesser before instance end time for Tower and Square Chambers
			else if (world.getRemainingTime() < 600000)
			{
				newRoom = locations.size() - 1;
			}
		}
		// 10% chance for teleport to raid room if not here already for Northern, Southern, Western and Eastern Chambers
		else if (!isBossRoom(world, newRoom) && (getRandom(100) < 10))
		{
			newRoom = locations.size() - 1;
		}
		else
		{
			// otherwise teleport to another room, except current
			while (newRoom == params.getInt("currentRoom", 0))
			{
				newRoom = getRandom(locations.size() - 1);
			}
		}
		world.setParameter("currentRoom", newRoom);
		
		// Teleport players into new room
		world.getPlayers().forEach(p -> teleportPlayerIn(p, world));
		
		// Do not schedule room change for Square and Tower Chambers, if raid room is reached
		if (isBigChamber(world) && isBossRoom(world, newRoom))
		{
			// Add 20 min to instance time if raid room is reached
			world.setDuration((int) (TimeUnit.MILLISECONDS.toMinutes(world.getRemainingTime() + 1200000)));
			
			// Broadcast instance duration change (NPC message)
			final int raidGatekeeper = world.getTemplateParameters().getInt("raidGatekeeper");
			final Npc gatekeeper = world.getNpc(raidGatekeeper);
			if (gatekeeper != null)
			{
				gatekeeper.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.TWENTY_MINUTES_ARE_ADDED_TO_THE_REMAINING_TIME_IN_THE_INSTANT_ZONE);
			}
		}
		else
		{
			scheduleRoomChange(world, false);
		}
	}
	
	protected void scheduleRoomChange(Instance world, boolean bossRoom)
	{
		// Schedule next room change only if remaining time is enough
		final long nextInterval = (bossRoom) ? 60000 : (ROOM_CHANGE_INTERVAL + getRandom(ROOM_CHANGE_RANDOM_TIME)) * 1000;
		if (world.getRemainingTime() > nextInterval)
		{
			final ScheduledFuture<?> roomChangeTask = ThreadPool.schedule(() ->
			{
				try
				{
					// Send earthquake packet
					for (PlayerInstance player : world.getPlayers())
					{
						player.sendPacket(new Earthquake(player, 20, 10));
					}
					// Wait for a while
					Thread.sleep(5000);
					// Change room
					changeRoom(world);
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "Error occured in room change task: ", e);
				}
			}, nextInterval - 5000);
			world.setParameter("roomChangeTask", roomChangeTask);
		}
	}
	
	protected void stopRoomChangeTask(Instance world)
	{
		final ScheduledFuture<?> task = world.getParameters().getObject("roomChangeTask", ScheduledFuture.class);
		if (task != null)
		{
			task.cancel(true);
			world.setParameter("roomChangeTask", null);
		}
	}
	
	public static void main(String[] args)
	{
		new ChamberOfDelusion();
	}
}
