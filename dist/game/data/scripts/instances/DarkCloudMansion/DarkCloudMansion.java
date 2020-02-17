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
package instances.DarkCloudMansion;

import java.util.List;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.network.NpcStringId;

import instances.AbstractInstance;

/**
 * Dark Cloud Mansion instance.
 * @author malyelfik
 */
public class DarkCloudMansion extends AbstractInstance
{
	// NPCs
	private static final int YIYEN = 32282;
	private static final int FAITH = 32288; // Symbol of Faith
	private static final int ADVERSITY = 32289; // Symbol of Adversity
	private static final int ADVENTURE = 32290; // Symbol of Adventure
	private static final int TRUTH = 32291; // Symbol of Truth
	private static final int MONOLITH = 32324; // Black Stone Monolith
	private static final int COLUMN = 22402; // Shadow Column
	// Mobs
	private static final int PARME_HEALER = 22400;
	private static final int[] BELETH_SUBORDINATE =
	{
		22272,
		22273,
		22274
	};
	private static final int[] MONOLITH_PRIVATES =
	{
		18369,
		18370
	};
	private static final int[] BELETH_SAMPLE =
	{
		18371,
		18372,
		18373,
		18374,
		18375,
		18376,
		18377
	};
	// Skill
	private static final SkillHolder INCARNATION = new SkillHolder(5441, 1);
	// Items
	private static final int CC = 9690; // Contaminated Crystal
	// Doors
	private static final int START_DOOR = 24230001; // Starting Room
	private static final int ROOM_A_DOOR = 24230002; // First Room
	private static final int ROOM_B_DOOR = 24230005; // Second Room
	private static final int ROOM_C_DOOR = 24230003; // Third Room
	private static final int ROOM_D_DOOR = 24230004; // Forth Room
	private static final int ROOM_E_DOOR = 24230006; // Fifth Room
	// Messages
	private static final NpcStringId[] SPAWN_CHAT =
	{
		NpcStringId.I_M_THE_REAL_ONE,
		NpcStringId.PICK_ME,
		NpcStringId.TRUST_ME,
		NpcStringId.NOT_THAT_DUDE_I_M_THE_REAL_ONE,
		NpcStringId.DON_T_BE_FOOLED_DON_T_BE_FOOLED_I_M_THE_REAL_ONE
	};
	private static final NpcStringId[] DECAY_CHAT =
	{
		NpcStringId.I_M_THE_REAL_ONE_PHEW,
		NpcStringId.CAN_T_YOU_EVEN_FIND_OUT,
		NpcStringId.FIND_ME
	};
	private static final NpcStringId[] SUCCESS_CHAT =
	{
		NpcStringId.HUH_HOW_DID_YOU_KNOW_IT_WAS_ME,
		NpcStringId.EXCELLENT_CHOICE_TEEHEE,
		NpcStringId.YOU_VE_DONE_WELL,
		NpcStringId.OH_VERY_SENSIBLE
	};
	private static final NpcStringId[] FAIL_CHAT =
	{
		NpcStringId.YOU_VE_BEEN_FOOLED,
		NpcStringId.SORRY_BUT_I_M_THE_FAKE_ONE
	};
	
	// Misc
	private static final int TEMPLATE_ID = 9;
	
	public DarkCloudMansion()
	{
		super(TEMPLATE_ID);
		addStartNpc(YIYEN);
		addFirstTalkId(MONOLITH, TRUTH, FAITH, ADVERSITY, ADVENTURE);
		addTalkId(YIYEN, TRUTH);
		addAttackId(COLUMN, PARME_HEALER);
		addAttackId(BELETH_SAMPLE);
		addKillId(COLUMN, PARME_HEALER);
		addKillId(BELETH_SAMPLE);
		addKillId(BELETH_SUBORDINATE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			switch (event)
			{
				case "DELETE_ONE":
				{
					npc.deleteMe();
					break;
				}
				case "DELETE":
				{
					world.getNpcs(BELETH_SAMPLE).stream().filter(n -> n != npc).forEach(Npc::deleteMe);
					world.setParameter("blocked", false);
					spawnRoomE(world);
					break;
				}
				case "CHAT":
				{
					world.getNpcs(BELETH_SAMPLE).stream().filter(n -> n.isScriptValue(1)).forEach(n -> n.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(DECAY_CHAT)));
					break;
				}
			}
		}
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		if (npc.getId() == YIYEN)
		{
			enterInstance(player, npc, TEMPLATE_ID);
		}
		else
		{
			final Instance world = npc.getInstanceWorld();
			if (world != null)
			{
				teleportPlayerOut(player, world);
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			switch (npc.getId())
			{
				case FAITH:
				{
					world.openCloseDoor(ROOM_A_DOOR, true);
					npc.showChatWindow(player);
					break;
				}
				case MONOLITH:
				{
					if (world.isStatus(4) && !npc.isScriptValue(-1))
					{
						int current = world.getParameters().getInt("current", 0);
						if (npc.isScriptValue(current))
						{
							npc.setScriptValue(-1);
							npc.doCast(INCARNATION.getSkill());
							world.setParameter("current", ++current);
							
							if (current == 6)
							{
								world.setStatus(5);
								world.spawnGroup("roomBClear");
								world.spawnGroup("hall");
								world.getNpcs(MONOLITH).forEach(Npc::deleteMe);
							}
						}
						else
						{
							addSpawn(getRandomEntry(MONOLITH_PRIVATES), npc, false, 0, false, world.getId());
						}
						world.openCloseDoor(ROOM_B_DOOR, false);
					}
					break;
				}
				case ADVERSITY:
				{
					world.openCloseDoor(ROOM_B_DOOR, true);
					npc.showChatWindow(player);
					break;
				}
				case ADVENTURE:
				{
					if (world.isStatus(7))
					{
						world.spawnGroup("roomC").forEach(n -> n.setScriptValue(8));
						world.setStatus(8);
						world.openCloseDoor(ROOM_D_DOOR, true);
					}
					npc.showChatWindow(player);
					break;
				}
				case TRUTH:
				{
					if (!hasQuestItems(player, CC))
					{
						giveItems(player, CC, 1);
					}
					npc.showChatWindow(player);
					break;
				}
			}
		}
		return null;
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			switch (npc.getId())
			{
				case PARME_HEALER:
				{
					if (world.isStatus(2) && (attacker.getY() < 179986))
					{
						world.openCloseDoor(ROOM_A_DOOR, false);
					}
					break;
				}
				case COLUMN:
				{
					if (world.isStatus(7))
					{
						world.openCloseDoor(ROOM_D_DOOR, false);
						if (npc.isHpBlocked() && (getRandom(100) < 12))
						{
							addSpawn(getRandomEntry(BELETH_SUBORDINATE), npc, false, 0, false, world.getId());
						}
					}
					break;
				}
				default:
				{
					if (world.isStatus(9))
					{
						handleRoomE(world, npc);
					}
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			switch (world.getStatus())
			{
				case 0: // Start room
				{
					if (world.getAliveNpcs().isEmpty())
					{
						world.setStatus(1);
						world.spawnGroup("hall");
						world.openCloseDoor(START_DOOR, true);
					}
					break;
				}
				case 1: // Hall
				{
					if (world.getAliveNpcs().isEmpty())
					{
						world.setStatus(2);
						world.spawnGroup("roomA");
						world.openCloseDoor(ROOM_A_DOOR, true);
					}
					break;
				}
				case 2: // Room A - cleared
				{
					if (world.getAliveNpcs().isEmpty())
					{
						world.setStatus(3);
						world.spawnGroup("roomAClear");
						world.spawnGroup("hall");
					}
					break;
				}
				case 3: // Hall
				{
					if (world.getAliveNpcs(BELETH_SUBORDINATE).isEmpty())
					{
						world.setStatus(4);
						world.openCloseDoor(ROOM_B_DOOR, true);
						
						int current = 0;
						final List<Npc> desks = world.spawnGroup("roomB");
						while (!desks.isEmpty())
						{
							final Npc desk = desks.remove(getRandom(desks.size()));
							desk.setScriptValue(current++);
						}
					}
					break;
				}
				case 5: // Hall
				{
					if (world.getAliveNpcs(BELETH_SUBORDINATE).isEmpty())
					{
						world.setStatus(6);
						world.spawnGroup("roomC");
						world.openCloseDoor(ROOM_C_DOOR, true);
					}
					break;
				}
				case 6: // Room C
				{
					if (world.getAliveNpcs(BELETH_SUBORDINATE).isEmpty())
					{
						world.setStatus(7);
						world.spawnGroup("roomD");
						world.openCloseDoor(ROOM_D_DOOR, true);
						for (int i = 1; i <= 7; i++)
						{
							final List<Npc> row = world.spawnGroup("roomD" + i);
							for (int x = 0; x < 2; x++)
							{
								final Npc col = row.remove(getRandom(row.size()));
								col.setIsInvul(true);
							}
						}
					}
					break;
				}
				case 8: // Room C
				{
					final long roomClear = world.getAliveNpcs(BELETH_SUBORDINATE).stream().filter(n -> n.isScriptValue(8)).count();
					if (roomClear == 0)
					{
						world.setStatus(9);
						world.openCloseDoor(ROOM_E_DOOR, true);
						spawnRoomE(world);
					}
					break;
				}
				case 9:
				{
					handleRoomE(world, npc);
					break;
				}
			}
		}
		return null;
	}
	
	private void spawnRoomE(Instance world)
	{
		final List<Npc> npcs = world.spawnGroup("roomE");
		for (Npc n : npcs)
		{
			n.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(SPAWN_CHAT));
		}
		for (int i = 0; i < 3; i++)
		{
			final Npc n = npcs.remove(getRandom(npcs.size()));
			n.setScriptValue(1);
		}
	}
	
	private void handleRoomE(Instance world, Npc npc)
	{
		if (CommonUtil.contains(BELETH_SAMPLE, npc.getId()))
		{
			final StatsSet params = world.getParameters();
			if (!params.getBoolean("blocked", false))
			{
				if (npc.isScriptValue(1))
				{
					final int found = params.getInt("found", 0) + 1;
					world.setParameter("found", found);
					
					npc.setScriptValue(-1);
					npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(SUCCESS_CHAT));
					
					if (found != 3)
					{
						startQuestTimer("DELETE_ONE", 1500, npc, null);
					}
					else
					{
						world.setStatus(10);
						world.getNpcs().forEach(Npc::deleteMe);
						world.spawnGroup("roomEClear");
					}
				}
				else if (!npc.isScriptValue(-1))
				{
					world.setParameter("blocked", true);
					world.setParameter("found", 0);
					
					npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(FAIL_CHAT));
					npc.setScriptValue(-1);
					startQuestTimer("CHAT", 4000, npc, null);
					startQuestTimer("DELETE", 4500, npc, null);
				}
				world.openCloseDoor(ROOM_E_DOOR, false);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new DarkCloudMansion();
	}
}