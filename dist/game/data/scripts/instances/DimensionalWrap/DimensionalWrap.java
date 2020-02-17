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
package instances.DimensionalWrap;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.Earthquake;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

import instances.AbstractInstance;

/**
 * Dimensional Wrap instance
 * @URL https://l2wiki.com/Dimensional_Warp
 * @Video https://www.youtube.com/watch?v=hOnzk0ELwIg
 * @author Gigi
 * @date 2018-09-04 - [14:33:31]
 */
public class DimensionalWrap extends AbstractInstance
{
	// NPCs
	private static final int RESED = 33974;
	private static final int EINSTER = 33975;
	// Monsters
	private static final int DEMINSIONAL_INVISIBLE_FRAGMENT = 19564;
	private static final int SALAMANDRA_GENERATOR = 19563;
	private static final int SALAMANDRA_GENERATOR_DUMMY = 19480;
	private static final int DIMENSIONAL_SALAMANDRA = 23466;
	private static final int UNWORDLY_SALAMANDER = 23473;
	private static final int DIMENSIONAL_IMP = 19553;
	private static final int UNWORDLY_IMP = 19554;
	private static final int ABYSSAL_IMP = 19555;
	private static final int ABYSSAL_MAKKUM = 26090;
	private static final int[] MONSTERS =
	{
		23462, // Dimensional Orc Butcher
		23463, // Dimensional Orc Hunter
		23464, // Dimensional Shaman
		23465, // Dimensional Bugbear
		23467, // Dimensional Binder
		23468, // Dimensional Demon
		23469, // Dimensional Archon
		23470, // Unworldly Demon
		23471, // Unworldly Etin
		23472, // Unworldly Shaman
		23474, // Unworldly Golem
		23475, // Unworldly Archon
		23476, // Unworldly Harpy
		23477, // Abyssal Shaman
		23478, // Abyssal Berserker
		23480, // Abyssal Harpy
		23481, // Abyssal Binder
		23482, // Abyssal Archon
		23483 // Abyssal Golem
	};
	private static final int[] TRAPS =
	{
		19556, // Debuff trap, power 1
		19557, // Debuff trap, power 2
		19558, // Debuff trap, power 3
		19559, // Damage trap, power 1
		19560, // Damage trap, power 2
		19561, // Damage trap, power 3
		19562 // Heal Trap
	};
	// Location
	private static final Location TELEPORTS = new Location(-76136, -216216, 4040);
	private static final Location FIRST_TELEPORT = new Location(-219544, 248776, 3360);
	private static final Location SECOND_TELEPORT = new Location(-206756, 242009, 6584);
	private static final Location THRID_TELEPORT = new Location(-219813, 248484, 9928);
	private static final Location FOURTH_TELEPORT = new Location(-87191, -210129, 6984);
	// Misc
	private static final int TEMPLATE_ID = 250;
	private static final int DIMENSIONAL_DARK_FORCES = 16415;
	private static final int WARP_CRYSTAL = 39597;
	protected double _chance = 0;
	protected int _count = 0;
	public int _skilllevel = 1;
	public int _worldState = 0;
	protected ScheduledFuture<?> _debufTask;
	
	public DimensionalWrap()
	{
		super(TEMPLATE_ID);
		addStartNpc(RESED);
		addTalkId(EINSTER);
		addKillId(MONSTERS);
		addKillId(ABYSSAL_MAKKUM);
		addSpawnId(SALAMANDRA_GENERATOR, DEMINSIONAL_INVISIBLE_FRAGMENT, SALAMANDRA_GENERATOR_DUMMY);
		addFirstTalkId(EINSTER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final Instance world = npc.getInstanceWorld();
		if (event.equals("enterInstance"))
		{
			if (!player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
			{
				htmltext = "no_awakened.html";
			}
			else if (!player.isInParty())
			{
				enterInstance(player, npc, TEMPLATE_ID);
			}
			else if (player.isInParty())
			{
				if (!player.getParty().isLeader(player))
				{
					player.sendPacket(new SystemMessage(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER));
				}
				else
				{
					final Party party = player.getParty();
					final List<PlayerInstance> members = party.getMembers();
					for (PlayerInstance member : members)
					{
						if (member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
						{
							enterInstance(member, npc, TEMPLATE_ID);
						}
					}
				}
			}
		}
		if (isInInstance(world))
		{
			switch (event)
			{
				case "33975-01.html":
				{
					htmltext = event;
					break;
				}
				case "12_warp_crystals":
				{
					_chance = 0.3;
					if (!player.isInParty())
					{
						_count = 12;
						CheckCrystallCount(world, player, npc);
						break;
					}
					else if (player.isInParty() && !player.getParty().isLeader(player))
					{
						player.sendPacket(new SystemMessage(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER));
						break;
					}
					switch (player.getParty().getMemberCount())
					{
						case 2:
						{
							_count = 6;
							CheckCrystallCount(world, player, npc);
							break;
						}
						case 3:
						{
							_count = 4;
							CheckCrystallCount(world, player, npc);
							break;
						}
						case 4:
						{
							_count = 3;
							CheckCrystallCount(world, player, npc);
							break;
						}
					}
					break;
				}
				case "240_warp_crystals":
				{
					_chance = 0.6;
					if (!player.isInParty())
					{
						_count = 240;
						CheckCrystallCount(world, player, npc);
						break;
					}
					else if (player.isInParty() && !player.getParty().isLeader(player))
					{
						player.sendPacket(new SystemMessage(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER));
						break;
					}
					switch (player.getParty().getMemberCount())
					{
						case 2:
						{
							_count = 120;
							CheckCrystallCount(world, player, npc);
							break;
						}
						case 3:
						{
							_count = 80;
							CheckCrystallCount(world, player, npc);
							break;
						}
						case 4:
						{
							_count = 60;
							CheckCrystallCount(world, player, npc);
							break;
						}
					}
					break;
				}
				case "1200_warp_crystals":
				{
					_chance = 0.9;
					if (!player.isInParty())
					{
						_count = 1200;
						CheckCrystallCount(world, player, npc);
						break;
					}
					else if (player.isInParty() && !player.getParty().isLeader(player))
					{
						player.sendPacket(new SystemMessage(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER));
						break;
					}
					switch (player.getParty().getMemberCount())
					{
						case 2:
						{
							_count = 600;
							CheckCrystallCount(world, player, npc);
							break;
						}
						case 3:
						{
							_count = 400;
							CheckCrystallCount(world, player, npc);
							break;
						}
						case 4:
						{
							_count = 300;
							CheckCrystallCount(world, player, npc);
							break;
						}
					}
					break;
				}
				case "send_6_f":
				{
					if (_worldState == 0)
					{
						htmltext = "33975-02.html";
						break;
					}
					if (player.getVariables().getInt("DIMENSIONAL_WRAP_LEVEL", 0) == 0)
					{
						htmltext = "33975-05.html";
						break;
					}
					
					for (Npc n : world.getAliveNpcs())
					{
						if (n.getId() != EINSTER)
						{
							n.deleteMe();
						}
					}
					if (world.getStatus() < 5)
					{
						world.setStatus(5);
						cancelQuestTimers("SECOND_SPAWN");
						cancelQuestTimers("THRID_SPAWN");
						startQuestTimer("START_STAGE", 5000, npc, null);
					}
					for (PlayerInstance pl : world.getPlayers())
					{
						pl.teleToLocation(FIRST_TELEPORT, world.getTemplateId());
					}
					break;
				}
				case "send_11_f":
				{
					if (_worldState == 0)
					{
						htmltext = "33975-02.html";
						break;
					}
					if ((player.getVariables().getInt("DIMENSIONAL_WRAP_LEVEL", 0) == 0) || (player.getVariables().getInt("DIMENSIONAL_WRAP_LEVEL") < 2))
					{
						htmltext = "33975-05.html";
						break;
					}
					for (Npc n : world.getAliveNpcs())
					{
						if (n.getId() != EINSTER)
						{
							n.deleteMe();
						}
					}
					if (world.getStatus() < 10)
					{
						world.setStatus(10);
						cancelQuestTimers("SECOND_SPAWN");
						cancelQuestTimers("THRID_SPAWN");
						startQuestTimer("START_STAGE", 5000, npc, null);
					}
					for (PlayerInstance pl : world.getPlayers())
					{
						pl.teleToLocation(SECOND_TELEPORT, world.getTemplateId());
					}
					break;
				}
				case "send_16_f":
				{
					if (_worldState == 0)
					{
						htmltext = "33975-02.html";
						break;
					}
					if ((player.getVariables().getInt("DIMENSIONAL_WRAP_LEVEL", 0) == 0) || (player.getVariables().getInt("DIMENSIONAL_WRAP_LEVEL") < 3))
					{
						htmltext = "33975-05.html";
						break;
					}
					for (Npc n : world.getAliveNpcs())
					{
						if (n.getId() != EINSTER)
						{
							n.deleteMe();
						}
					}
					if (world.getStatus() < 15)
					{
						world.setStatus(15);
						cancelQuestTimers("SECOND_SPAWN");
						cancelQuestTimers("THRID_SPAWN");
						startQuestTimer("START_STAGE", 5000, npc, null);
					}
					for (PlayerInstance pl : world.getPlayers())
					{
						pl.teleToLocation(THRID_TELEPORT, world.getTemplateId());
					}
					break;
				}
				case "send_21_f":
				{
					if (_worldState == 0)
					{
						htmltext = "33975-02.html";
						break;
					}
					if ((player.getVariables().getInt("DIMENSIONAL_WRAP_LEVEL", 0) == 0) || (player.getVariables().getInt("DIMENSIONAL_WRAP_LEVEL") < 4))
					{
						htmltext = "33975-05.html";
						break;
					}
					for (Npc n : world.getAliveNpcs())
					{
						if (n.getId() != EINSTER)
						{
							n.deleteMe();
						}
					}
					if (world.getStatus() < 20)
					{
						world.setStatus(20);
						cancelQuestTimers("SECOND_SPAWN");
						cancelQuestTimers("THRID_SPAWN");
						startQuestTimer("START_STAGE", 5000, npc, null);
					}
					for (PlayerInstance pl : world.getPlayers())
					{
						pl.teleToLocation(TELEPORTS, world.getTemplateId());
					}
					break;
				}
				case "send_26_f":
				{
					if (_worldState == 0)
					{
						htmltext = "33975-02.html";
						break;
					}
					if ((player.getVariables().getInt("DIMENSIONAL_WRAP_LEVEL", 0) == 0) || (player.getVariables().getInt("DIMENSIONAL_WRAP_LEVEL") < 5))
					{
						htmltext = "33975-05.html";
						break;
					}
					for (Npc n : world.getAliveNpcs())
					{
						if (n.getId() != EINSTER)
						{
							n.deleteMe();
						}
					}
					if (world.getStatus() < 25)
					{
						world.setStatus(25);
						cancelQuestTimers("SECOND_SPAWN");
						cancelQuestTimers("THRID_SPAWN");
						startQuestTimer("START_STAGE", 5000, npc, null);
					}
					for (PlayerInstance pl : world.getPlayers())
					{
						pl.teleToLocation(FOURTH_TELEPORT, world.getTemplateId());
					}
					break;
				}
				case "jump_location":
				{
					player.teleToLocation(TELEPORTS, world.getTemplateId());
					break;
				}
				case "SALAMANDRA_SPAWN":
				case "SALAMANDRA_SPAWN_DUMMY":
				{
					if (getRandom(100) < (_worldState / 2))
					{
						final Npc salamandra = addSpawn(_worldState < 17 ? DIMENSIONAL_SALAMANDRA : UNWORDLY_SALAMANDER, npc, false, 0, false, world.getId());
						salamandra.setRunning();
						World.getInstance().forEachVisibleObjectInRange(salamandra, PlayerInstance.class, 2500, p ->
						{
							if ((p != null) && !p.isDead())
							{
								addAttackPlayerDesire(salamandra, p);
							}
						});
					}
					break;
				}
				case "START_STAGE":
				{
					world.setStatus(world.getStatus() + 1);
					_worldState = world.getStatus();
					world.broadcastPacket(new ExShowScreenMessage(NpcStringId.DIMENSIONAL_WARP_LV_S1, ExShowScreenMessage.TOP_CENTER, 10000, true, String.valueOf(world.getStatus())));
					startQuestTimer("FIRST_SPAWN", 1500, npc, null);
					break;
				}
				case "FIRST_SPAWN":
				{
					world.spawnGroup(_worldState + "_first_spawn");
					world.spawnGroup(_worldState + "_trap_spawn");
					startQuestTimer("SECOND_SPAWN", 40000, npc, null);
					_debufTask = ThreadPool.scheduleAtFixedRate(() ->
					{
						if ((_worldState > 0) && (_worldState <= 11))
						{
							_skilllevel = 1;
						}
						else if ((_worldState > 11) && (_worldState <= 20))
						{
							_skilllevel = 2;
						}
						else if ((_worldState > 20) && (_worldState <= 30))
						{
							_skilllevel = 3;
						}
						else if (_worldState > 30)
						{
							_skilllevel = 4;
						}
						final Skill skill = SkillData.getInstance().getSkill(DIMENSIONAL_DARK_FORCES, _skilllevel);
						for (PlayerInstance p : world.getPlayers())
						{
							if ((p != null) && !p.isDead())
							{
								skill.applyEffects(p, p);
							}
						}
					}, 5000, 10000);
					break;
				}
				case "SECOND_SPAWN":
				{
					world.spawnGroup(_worldState + "_second_spawn");
					startQuestTimer("THRID_SPAWN", 40000, npc, null);
					break;
				}
				case "THRID_SPAWN":
				{
					world.spawnGroup(_worldState + "_thred_spawn");
					break;
				}
				case "CHANGE_LOCATION":
				{
					world.getAliveNpcs(TRAPS).forEach(t -> t.deleteMe());
					world.spawnGroup(_worldState + "_trap_spawn");
					startQuestTimer("CHANGE_LOCATION", 60000 - (_worldState * 1430), npc, null);
					break;
				}
				case "SWITCH_STAGE":
				{
					if (world.getAliveNpcs(MONSTERS).isEmpty())
					{
						world.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_SURROUNDING_ENERGY_HAS_DISSIPATED, ExShowScreenMessage.TOP_CENTER, 5000, true));
						world.broadcastPacket(new Earthquake(npc, 50, 5));
						world.openCloseDoor(world.getTemplateParameters().getInt(_worldState + "_st_door"), true);
						Clean();
						if (_worldState < 35)
						{
							startQuestTimer("NEXT_STAGE", 5000, npc, null);
						}
					}
					break;
				}
				case "NEXT_STAGE":
				{
					world.broadcastPacket(new ExShowScreenMessage(NpcStringId.S1_SECONDS_HAVE_BEEN_ADDED_TO_THE_INSTANCED_ZONE_DURATION, ExShowScreenMessage.TOP_CENTER, 5000, true, String.valueOf(180)));
					world.setDuration((int) ((world.getRemainingTime() / 60000) + 3));
					startQuestTimer("START_STAGE", 8000, npc, null);
					for (Npc n : world.getAliveNpcs())
					{
						if (n.getId() != EINSTER)
						{
							n.deleteMe();
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (CommonUtil.contains(MONSTERS, npc.getId()) && (getRandom(100) < _chance))
			{
				if (_worldState < 9)
				{
					addSpawn(DIMENSIONAL_IMP, npc, true, 0, false, world.getId());
					world.broadcastPacket(new ExShowScreenMessage(NpcStringId.DIMENSIONAL_IMP, ExShowScreenMessage.TOP_CENTER, 5000, true));
				}
				else if ((_worldState >= 9) && (_worldState < 20))
				{
					addSpawn(UNWORDLY_IMP, npc, true, 0, false, world.getId());
					world.broadcastPacket(new ExShowScreenMessage(NpcStringId.UNWORLDLY_IMP, ExShowScreenMessage.TOP_CENTER, 5000, true));
				}
				else if (_worldState >= 20)
				{
					addSpawn(ABYSSAL_IMP, npc, true, 0, false, world.getId());
					world.broadcastPacket(new ExShowScreenMessage(NpcStringId.ABYSSAL_IMP, ExShowScreenMessage.TOP_CENTER, 5000, true));
				}
			}
			else if (npc.getId() == ABYSSAL_MAKKUM)
			{
				world.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_INSTANCED_ZONE_WILL_CLOSE_SOON, ExShowScreenMessage.TOP_CENTER, 10000, true));
				world.finishInstance(3);
				Clean();
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		if (_worldState == 20)
		{
			return "33975-04.html";
		}
		return "33975.html";
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case SALAMANDRA_GENERATOR:
				{
					startQuestTimer("SALAMANDRA_SPAWN", 25000, npc, null, true);
					startQuestTimer("CHANGE_LOCATION", 60000 - (_worldState * 1300), npc, null);
					break;
				}
				case SALAMANDRA_GENERATOR_DUMMY:
				{
					startQuestTimer("SALAMANDRA_SPAWN_DUMMY", 20000, npc, null, true);
					break;
				}
				case DEMINSIONAL_INVISIBLE_FRAGMENT:
				{
					startQuestTimer("SWITCH_STAGE", 5000, npc, null, true);
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	public void CheckCrystallCount(Instance world, PlayerInstance player, Npc npc)
	{
		boolean canStart = true;
		for (PlayerInstance p : world.getPlayers())
		{
			if (p.getInventory().getInventoryItemCount(WARP_CRYSTAL, -1) < _count)
			{
				for (PlayerInstance ps : world.getPlayers())
				{
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(ps, "33975-03.html"));
					packet.replace("%count%", Integer.toString(_count));
					ps.sendPacket(packet);
					ps.sendPacket(new SystemMessage(SystemMessageId.C1_S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addString(ps.getName()));
					canStart = false;
				}
			}
		}
		if (!canStart || (_worldState != 0))
		{
			return;
		}
		
		startQuestTimer("START_STAGE", 1000, npc, null);
		for (PlayerInstance p : world.getPlayers())
		{
			takeItems(p, WARP_CRYSTAL, _count);
		}
	}
	
	protected void Clean()
	{
		cancelQuestTimers("SWITCH_STAGE");
		cancelQuestTimers("SALAMANDRA_SPAWN");
		cancelQuestTimers("SALAMANDRA_SPAWN_DUMMY");
		cancelQuestTimers("CHANGE_LOCATION");
		if (_debufTask != null)
		{
			_debufTask.cancel(false);
			_debufTask = null;
		}
	}
	
	public static void main(String[] args)
	{
		new DimensionalWrap();
	}
}
