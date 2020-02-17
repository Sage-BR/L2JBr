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
package instances.Fortuna;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;

/**
 * @author Mobius, Stayway
 * @URL https://l2wiki.com/Fortuna
 * @VIDEO https://www.youtube.com/watch?v=OeDVPu-STY4
 */
public class Fortuna extends AbstractInstance
{
	// NPCs
	private static final int ARCAN_IZAEL = 32894;
	private static final int INSTANCE_IZAEL = 33153;
	private static final int RON = 25825;
	// Location
	private static final Location RE_ENTRY_LOCATION = new Location(42104, -172712, -7980);
	// Misc
	private static final int TEMPLATE_ID = 179;
	
	public Fortuna()
	{
		super(TEMPLATE_ID);
		addStartNpc(ARCAN_IZAEL, INSTANCE_IZAEL);
		addFirstTalkId(ARCAN_IZAEL, INSTANCE_IZAEL);
		addTalkId(ARCAN_IZAEL, INSTANCE_IZAEL);
		addKillId(RON);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "32894-1.htm":
			case "32894-2.htm":
			case "33153-1.htm":
			case "33153-2.htm":
			{
				return event;
			}
			case "request_enter_fortuna":
			{
				enterInstance(player, npc, TEMPLATE_ID);
				if (player.getInstanceWorld() != null)
				{
					startQuestTimer("start_fortuna", 60000, player.getInstanceWorld().getNpc(INSTANCE_IZAEL), null);
				}
				return null;
			}
			case "request_re_enter_fortuna":
			{
				if (player.getParty() != null)
				{
					for (PlayerInstance member : player.getParty().getMembers())
					{
						final Instance world = member.getInstanceWorld();
						if ((world != null) && (world.getTemplateId() == TEMPLATE_ID) && (world.getPlayersCount() < 7))
						{
							player.teleToLocation(RE_ENTRY_LOCATION, world);
							break;
						}
					}
				}
				return null;
			}
			case "start_fortuna":
			{
				startQuestTimer("check_fortuna_status", 1000, npc, null);
				return null;
			}
			case "boss_1_pause":
			{
				final Instance world = npc.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				world.setStatus(11);
				world.spawnGroup("fortuna_11");
				showOnScreenMsg(world, NpcStringId.BLOODSUCKING_CREATURES_ABSORB_THE_LIGHT_AND_FILL_IT_INTO_DARKNESS, ExShowScreenMessage.TOP_CENTER, 10000, true);
				startQuestTimer("check_fortuna_status", 5000, npc, null);
				return null;
			}
			case "boss_2_pause":
			{
				final Instance world = npc.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				world.setStatus(16);
				world.spawnGroup("fortuna_16");
				showOnScreenMsg(world, NpcStringId.WE_NEED_A_LITTLE_MORE, ExShowScreenMessage.TOP_CENTER, 10000, true);
				startQuestTimer("check_fortuna_status", 5000, npc, null);
				return null;
			}
			case "boss_3_pause":
			{
				final Instance world = npc.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				world.setStatus(19);
				world.spawnGroup("fortuna_19");
				showOnScreenMsg(world, NpcStringId.BLOODSUCKING_CREATURES_WAKE_THE_SOLDIERS_NOW, ExShowScreenMessage.TOP_CENTER, 10000, true);
				startQuestTimer("check_fortuna_status", 5000, npc, null);
				return null;
			}
			case "boss_4_pause":
			{
				final Instance world = npc.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				world.setStatus(23);
				world.spawnGroup("fortuna_23");
				showOnScreenMsg(world, NpcStringId.DARKNESS_SWALLOW_EVERYTHING_AWAY, ExShowScreenMessage.TOP_CENTER, 10000, true);
				startQuestTimer("check_fortuna_status", 5000, npc, null);
				return null;
			}
			case "boss_5_pause":
			{
				final Instance world = npc.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				world.setStatus(26);
				world.spawnGroup("fortuna_26");
				showOnScreenMsg(world, NpcStringId.I_NOW_HAVE_TO_GO_AND_HANDLE_IT, ExShowScreenMessage.TOP_CENTER, 10000, true);
				startQuestTimer("check_fortuna_status", 5000, npc, null);
				return null;
			}
			case "boss_6_pause":
			{
				final Instance world = npc.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				world.setStatus(27);
				world.spawnGroup("fortuna_last_boss");
				showOnScreenMsg(world, NpcStringId.RON_APPEARS, ExShowScreenMessage.BOTTOM_RIGHT, 6000, true);
				return null;
			}
			case "check_fortuna_status":
			{
				final Instance world = npc.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				switch (world.getStatus())
				{
					case 0:
					{
						world.setStatus(1);
						world.spawnGroup("fortuna_1");
						showOnScreenMsg(world, NpcStringId.WHO_DARE_TO_INTERRUPT_OUR_REST, ExShowScreenMessage.TOP_CENTER, 10000, true);
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 1:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(2);
							world.spawnGroup("fortuna_2");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 2:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(3);
							world.spawnGroup("fortuna_3");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 3:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(4);
							world.spawnGroup("fortuna_4");
							showOnScreenMsg(world, NpcStringId.THOSE_WHO_CAME_HERE_LOOKING_FOR_CURSED_ONES_WELCOME, ExShowScreenMessage.TOP_CENTER, 10000, true);
						}
						startQuestTimer("check_fortuna_status", 15000, npc, null);
						break;
					}
					case 4:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(5);
							world.spawnGroup("fortuna_5");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 5:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(6);
							world.spawnGroup("fortuna_6");
							showOnScreenMsg(world, NpcStringId.LET_S_SEE_HOW_MUCH_YOU_CAN_ENDURE, ExShowScreenMessage.TOP_CENTER, 10000, true);
						}
						startQuestTimer("check_fortuna_status", 15000, npc, null);
						break;
					}
					case 6:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(7);
							world.spawnGroup("fortuna_7");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 7:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(8);
							world.spawnGroup("fortuna_8");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 8:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(9);
							world.spawnGroup("fortuna_9");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 9:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(10);
							world.spawnGroup("fortuna_10");
							showOnScreenMsg(world, NpcStringId.WEEPING_YUI_APPEARS, ExShowScreenMessage.BOTTOM_RIGHT, 6000, true);
						}
						startQuestTimer("check_fortuna_status", 35000, npc, null);
						break;
					}
					case 10:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							startQuestTimer("boss_1_pause", 30000, npc, null);
						}
						else
						{
							startQuestTimer("check_fortuna_status", 5000, npc, null);
						}
						break;
					}
					case 11:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(12);
							world.spawnGroup("fortuna_12");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 12:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(13);
							world.spawnGroup("fortuna_13");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 13:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(14);
							world.spawnGroup("fortuna_14");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 14:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(15);
							world.spawnGroup("fortuna_15");
							showOnScreenMsg(world, NpcStringId.ENRAGED_MASTER_KINEN_APPEARS, ExShowScreenMessage.BOTTOM_RIGHT, 6000, true);
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 15:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							startQuestTimer("boss_2_pause", 30000, npc, null);
						}
						else
						{
							startQuestTimer("check_fortuna_status", 5000, npc, null);
						}
						break;
					}
					case 16:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(17);
							world.spawnGroup("fortuna_17");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 17:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(18);
							world.spawnGroup("fortuna_18");
							showOnScreenMsg(world, NpcStringId.MAGICAL_WARRIOR_KONYAR_APPEARS, ExShowScreenMessage.BOTTOM_RIGHT, 6000, true);
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 18:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							startQuestTimer("boss_3_pause", 30000, npc, null);
						}
						else
						{
							startQuestTimer("check_fortuna_status", 5000, npc, null);
						}
						break;
					}
					case 19:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(20);
							world.spawnGroup("fortuna_20");
							showOnScreenMsg(world, NpcStringId.THIS_IS_ONLY_THE_START, ExShowScreenMessage.TOP_CENTER, 10000, true);
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 20:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(21);
							world.spawnGroup("fortuna_21");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 21:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(22);
							world.spawnGroup("fortuna_22");
							showOnScreenMsg(world, NpcStringId.SIR_LESYINDA_OF_THE_BLACK_SHADOW_APPEARS, ExShowScreenMessage.BOTTOM_RIGHT, 6000, true);
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 22:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							startQuestTimer("boss_4_pause", 30000, npc, null);
						}
						else
						{
							startQuestTimer("check_fortuna_status", 5000, npc, null);
						}
						break;
					}
					case 23:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(24);
							world.spawnGroup("fortuna_24");
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 24:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							world.setStatus(25);
							world.spawnGroup("fortuna_25");
							showOnScreenMsg(world, NpcStringId.MUKSHU_THE_COWARD_AND_BLIND_HORNAFI_APPEARS, ExShowScreenMessage.BOTTOM_RIGHT, 6000, true);
						}
						startQuestTimer("check_fortuna_status", 5000, npc, null);
						break;
					}
					case 25:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							startQuestTimer("boss_5_pause", 30000, npc, null);
							showOnScreenMsg(world, NpcStringId.DARKNESS_SWALLOW_EVERYTHING_AWAY, ExShowScreenMessage.TOP_CENTER, 10000, true);
						}
						else
						{
							startQuestTimer("check_fortuna_status", 5000, npc, null);
						}
						break;
					}
					case 26:
					{
						if (world.getAliveNpcs().size() == 1)
						{
							startQuestTimer("boss_6_pause", 30000, npc, null);
							showOnScreenMsg(world, NpcStringId.I_NOW_HAVE_TO_GO_AND_HANDLE_IT, ExShowScreenMessage.TOP_CENTER, 10000, true);
						}
						else
						{
							startQuestTimer("check_fortuna_status", 5000, npc, null);
						}
						break;
					}
				}
				return null;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		if (npc.getInstanceWorld() != null)
		{
			npc.getInstanceWorld().finishInstance();
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + ".htm";
	}
	
	public static void main(String[] args)
	{
		new Fortuna();
	}
}