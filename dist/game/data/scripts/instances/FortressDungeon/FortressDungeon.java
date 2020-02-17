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
package instances.FortressDungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.entity.Fort;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.instancezone.InstanceTemplate;
import org.l2jbr.gameserver.model.quest.QuestState;

import instances.AbstractInstance;
import quests.Q00511_AwlUnderFoot.Q00511_AwlUnderFoot;

/**
 * <b>Fortress dungeon</b> instance for quest <b>Awl Under Foot (511)</b>
 * @author malyelfik
 */
public class FortressDungeon extends AbstractInstance
{
	// NPCs
	private static final Map<Integer, Integer> NPCS = new HashMap<>();
	static
	{
		NPCS.put(35666, 22); // Shanty
		NPCS.put(35698, 23); // Southern
		NPCS.put(35735, 24); // Hive
		NPCS.put(35767, 25); // Valley
		NPCS.put(35804, 26); // Ivory
		NPCS.put(35835, 27); // Narsell
		NPCS.put(35867, 28); // Bayou
		NPCS.put(35904, 29); // White Sands
		NPCS.put(35936, 30); // Borderland
		NPCS.put(35974, 31); // Swamp
		NPCS.put(36011, 32); // Archaic
		NPCS.put(36043, 33); // Floran
		NPCS.put(36081, 34); // Cloud Mountain
		NPCS.put(36118, 35); // Tanor
		NPCS.put(36149, 36); // Dragonspine
		NPCS.put(36181, 37); // Antharas
		NPCS.put(36219, 38); // Western
		NPCS.put(36257, 39); // Hunter
		NPCS.put(36294, 40); // Aaru
		NPCS.put(36326, 41); // Demon
		NPCS.put(36364, 42); // Monastic
	}
	
	// Monsters
	private static final int[] RAIDS1 =
	{
		25572,
		25575,
		25578
	};
	private static final int[] RAIDS2 =
	{
		25579,
		25582,
		25585,
		25588
	};
	private static final int[] RAIDS3 =
	{
		25589,
		25592,
		25593
	};
	// Item
	private static final int MARK = 9797;
	// Locations
	private static final Location SPAWN_LOC = new Location(53319, 245814, -6576);
	// Misc
	private static final int MARK_COUNT = 2520;
	private static final long REENTER = 24 * 3600000; // 24 hours
	private static final Map<Integer, Long> REENETER_HOLDER = new ConcurrentHashMap<>();
	
	public FortressDungeon()
	{
		super(NPCS.values().stream().mapToInt(Integer::valueOf).toArray());
		// NPCs
		addStartNpc(NPCS.keySet());
		addTalkId(NPCS.keySet());
		// Monsters
		addKillId(RAIDS1);
		addKillId(RAIDS2);
		addKillId(RAIDS3);
		// Instance
		addInstanceCreatedId(NPCS.values());
		addInstanceDestroyId(NPCS.values());
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final int npcId = npc.getId();
		if (NPCS.containsKey(npcId))
		{
			enterInstance(player, npc, NPCS.get(npcId));
		}
		return null;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			if (CommonUtil.contains(RAIDS3, npc.getId()))
			{
				// Get players with active quest
				final List<PlayerInstance> members = new ArrayList<>();
				for (PlayerInstance member : world.getPlayers())
				{
					final QuestState qs = member.getQuestState(Q00511_AwlUnderFoot.class.getSimpleName());
					if ((qs != null) && qs.isCond(1))
					{
						members.add(member);
					}
				}
				
				// Distribute marks between them
				if (!members.isEmpty())
				{
					final long itemCount = MARK_COUNT / members.size();
					for (PlayerInstance member : members)
					{
						giveItems(member, MARK, itemCount);
						playSound(member, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				world.finishInstance();
			}
			else
			{
				world.incStatus();
				spawnRaid(world);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		// Put re-enter for instance
		REENETER_HOLDER.put(instance.getTemplateId(), System.currentTimeMillis() + REENTER);
		// Schedule spawn of first raid
		spawnRaid(instance);
	}
	
	@Override
	protected boolean validateConditions(List<PlayerInstance> group, Npc npc, InstanceTemplate template)
	{
		final PlayerInstance groupLeader = group.get(0);
		final Fort fort = npc.getFort();
		if (fort == null)
		{
			showHtmlFile(groupLeader, "noProperPledge.html");
			return false;
		}
		else if (fort.getFortState() == 0)
		{
			showHtmlFile(groupLeader, "noContractYet.html");
			return false;
		}
		else if (fort.getFortState() == 2)
		{
			showHtmlFile(groupLeader, "noCastleContract.html");
			return false;
		}
		else if (REENETER_HOLDER.containsKey(template.getId()))
		{
			final long time = REENETER_HOLDER.get(template.getId());
			if (time > System.currentTimeMillis())
			{
				showHtmlFile(groupLeader, "enterRestricted.html");
				return false;
			}
			REENETER_HOLDER.remove(template.getId());
		}
		return true;
	}
	
	@Override
	public void onInstanceDestroy(Instance instance)
	{
		// Stop running spawn task
		final ScheduledFuture<?> task = instance.getParameters().getObject("spawnTask", ScheduledFuture.class);
		if ((task != null) && !task.isDone())
		{
			task.cancel(true);
		}
		instance.setParameter("spawnTask", null);
	}
	
	/**
	 * Spawn raid boss according to instance status.
	 * @param instance instance world where instance should be spawned
	 */
	private void spawnRaid(Instance instance)
	{
		final ScheduledFuture<?> spawnTask = ThreadPool.schedule(() ->
		{
			// Get template id of raid
			final int npcId;
			switch (instance.getStatus())
			{
				case 0:
				{
					npcId = getRandomEntry(RAIDS1);
					break;
				}
				case 1:
				{
					npcId = getRandomEntry(RAIDS2);
					break;
				}
				default:
				{
					npcId = getRandomEntry(RAIDS3);
				}
			}
			
			// Spawn raid
			addSpawn(npcId, SPAWN_LOC, false, 0, false, instance.getId());
			
			// Unset spawn task reference
			instance.setParameter("spawnTask", null);
		}, 2 * 60 * 1000); // 2 minutes
		
		// Save timer to instance world
		instance.setParameter("spawnTask", spawnTask);
	}
	
	public static void main(String[] args)
	{
		new FortressDungeon();
	}
}