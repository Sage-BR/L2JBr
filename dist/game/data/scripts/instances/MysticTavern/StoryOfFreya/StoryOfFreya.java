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
package instances.MysticTavern.StoryOfFreya;

import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.FriendlyNpcInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.zone.type.ScriptZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

import instances.AbstractInstance;
import quests.Q00835_PitiableMelisa.Q00835_PitiableMelisa;

/**
 * Mystic Tavern Freya Instance
 * @author Gigi
 * @date 2019-02-05 - [19:54:29]
 */
public class StoryOfFreya extends AbstractInstance
{
	// NPCs
	private static final int SAYAN = 34172;
	private static final int KANNA = 34173;
	private static final int SYRRA = 34174;
	private static final int FREYA = 23689;
	private static final int ICE_KNIGHT = 23703;
	private static final int KNIGHT = 34175;
	private static final int FROST_GOLEM = 23686;
	private static final int FROST_KNIGHT = 23688;
	private static final int GLACIER_GOLEM = 23718;
	private static final int GLACIER_FROSTBRINGER = 23687;
	// Other
	private static final int TEMPLATE_ID = 263;
	private static final int ICE_CRYSTAL_SHARD = 46594;
	private static final Location CASTLE_TELEPORT = new Location(212410, -46728, -11225);
	private static final Location LABIRYNTH_TELEPORT = new Location(213145, -43145, -872);
	private static final ScriptZone FIRST_SPAWN_ZONE = ZoneManager.getInstance().getZoneById(80013, ScriptZone.class);
	private static final ScriptZone SECOND_SPAWN_ZONE = ZoneManager.getInstance().getZoneById(80014, ScriptZone.class);
	private static final ScriptZone THRID_SPAWN_ZONE = ZoneManager.getInstance().getZoneById(80015, ScriptZone.class);
	private static final ScriptZone FOURTH_SPAWN_ZONE = ZoneManager.getInstance().getZoneById(80016, ScriptZone.class);
	private static final ScriptZone FIFTH_SPAWN_ZONE = ZoneManager.getInstance().getZoneById(80017, ScriptZone.class);
	private static final ScriptZone SIXTH_SPAWN_ZONE = ZoneManager.getInstance().getZoneById(80018, ScriptZone.class);
	private static final int FREYA_SNOW = 26160228;
	
	public StoryOfFreya()
	{
		super(TEMPLATE_ID);
		addTalkId(SAYAN, KANNA, SYRRA);
		addFirstTalkId(SAYAN, KANNA, SYRRA);
		addSpawnId(FREYA, ICE_KNIGHT);
		addAttackId(FROST_GOLEM, GLACIER_FROSTBRINGER, FREYA);
		addKillId(ICE_KNIGHT, FROST_GOLEM, GLACIER_FROSTBRINGER, FREYA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final Instance world = npc.getInstanceWorld();
		switch (event)
		{
			case "34172-02.html":
			case "34172-03.html":
			{
				htmltext = event;
				break;
			}
			case "34172-01.html":
			{
				if (!player.isInParty() || !player.getParty().isLeader(player))
				{
					htmltext = "34172-04.html";
					break;
				}
				htmltext = event;
				break;
			}
			case "34172-06.html":
			{
				if (!player.isInParty() || !player.getParty().isLeader(player))
				{
					htmltext = "34172-04.html";
					break;
				}
				final Party party = player.getParty();
				final List<PlayerInstance> members = party.getMembers();
				for (PlayerInstance member : members)
				{
					if (member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
					{
						QuestProgress(npc, member);
					}
				}
				htmltext = event;
				break;
			}
			case "start_story":
			{
				player.standUp();
				enterInstance(player, null, TEMPLATE_ID);
				final Party party = player.getParty();
				if (party != null)
				{
					final Instance instance = player.getInstanceWorld();
					for (PlayerInstance member : party.getMembers())
					{
						if (member != player)
						{
							member.standUp();
							member.teleToLocation(player, instance);
							instance.addPlayer(member);
							instance.addAllowed(member);
						}
					}
					instance.setReenterTime();
				}
				break;
			}
			case "startInstance":
			{
				if (player.isInParty())
				{
					final Party party = player.getParty();
					final List<PlayerInstance> members = party.getMembers();
					for (PlayerInstance member : members)
					{
						if (member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
						{
							member.teleToLocation(LABIRYNTH_TELEPORT, world.getTemplateId());
							world.setStatus(1);
							QuestProgress(npc, member);
						}
					}
					if (getRandom(10) < 5)
					{
						world.spawnGroup("knight");
						world.broadcastPacket(new ExShowScreenMessage(NpcStringId.ICE_KNIGHTS_GOT_IN_DEFEAT_THE_ICE_KNIGHTS, ExShowScreenMessage.TOP_CENTER, 20000, true));
					}
					else
					{
						world.spawnGroup("knightSolo");
					}
				}
				break;
			}
			case "openDoor":
			{
				if (world.isStatus(0))
				{
					if (!player.isInParty() || !player.getParty().isLeader(player))
					{
						htmltext = "34173-03.html";
						break;
					}
					if (player.isInParty())
					{
						final Party party = player.getParty();
						final List<PlayerInstance> members = party.getMembers();
						for (PlayerInstance member : members)
						{
							if ((member == null) || (member.calculateDistance3D(npc) > 300))
							{
								htmltext = "34173-02.html";
								break;
							}
						}
					}
					world.openCloseDoor(world.getTemplateParameters().getInt("1_st_door"), true);
					startQuestTimer("closeDoor", 60000, npc, null);
					break;
				}
				
				else if (world.getStatus() > 0)
				{
					if (player.getInventory().getInventoryItemCount(ICE_CRYSTAL_SHARD, -1) == 10)
					{
						world.openCloseDoor(world.getTemplateParameters().getInt("1_st_door"), true);
						startQuestTimer("closeDoor", 30000, npc, null);
						break;
					}
					htmltext = "34173-03.html";
					break;
				}
			}
			case "closeDoor":
			{
				world.openCloseDoor(world.getTemplateParameters().getInt("1_st_door"), false);
				break;
			}
			case "summerLabirynth":
			{
				player.teleToLocation(LABIRYNTH_TELEPORT, world.getTemplateId());
				break;
			}
			case "backCastle":
			{
				if (!player.isInParty() || !player.getParty().isLeader(player))
				{
					htmltext = "34174-01.html";
					break;
				}
				if (player.isInParty())
				{
					final Party party = player.getParty();
					final List<PlayerInstance> members = party.getMembers();
					for (PlayerInstance member : members)
					{
						if (member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
						{
							player.teleToLocation(CASTLE_TELEPORT, world.getTemplateId());
						}
					}
				}
				break;
			}
			case "startFreya":
			{
				world.despawnGroup("general");
				playMovie(world.getPlayers(), Movie.EPIC_FREYA_SCENE);
				startQuestTimer("freyaSpawn", 20000, npc, null);
				break;
			}
			case "freyaSpawn":
			{
				world.spawnGroup("freya");
				world.broadcastPacket(new OnEventTrigger(FREYA_SNOW, true));
				break;
			}
			case "startAttack":
			{
				for (Npc nearby : World.getInstance().getVisibleObjectsInRange(npc, FriendlyNpcInstance.class, 300))
				{
					if (nearby.getId() == KNIGHT)
					{
						nearby.setIsInvul(true);
						npc.reduceCurrentHp(1, nearby, null);
						nearby.reduceCurrentHp(1, npc, null);
						addAttackDesire(nearby, npc);
					}
				}
				break;
			}
			case "finishInstance":
			{
				world.finishInstance(0);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case FROST_GOLEM:
				{
					if ((FIRST_SPAWN_ZONE.isInsideZone(npc) || SIXTH_SPAWN_ZONE.isInsideZone(npc)) && (npc.isScriptValue(0)))
					{
						npc.setScriptValue(1);
						for (int i = 0; i < 6; i++)
						{
							final Npc knight = addSpawn(FROST_KNIGHT, npc.getX() + getRandom(-100, 100), npc.getY() + getRandom(-100, 100), npc.getZ(), getRandom(64000), false, 300000, false, world.getId());
							addAttackPlayerDesire(knight, attacker);
						}
					}
					if (THRID_SPAWN_ZONE.isInsideZone(npc) && (npc.isScriptValue(0)))
					{
						npc.setScriptValue(1);
						for (int a = 0; a < 8; a++)
						{
							final Npc golem = addSpawn(GLACIER_GOLEM, npc.getX() + getRandom(-100, 100), npc.getY() + getRandom(-100, 100), npc.getZ(), getRandom(64000), false, 300000, false, world.getId());
							addAttackPlayerDesire(golem, attacker);
						}
					}
					break;
				}
				case GLACIER_FROSTBRINGER:
				{
					if (npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						for (int b = 0; b < 4; b++)
						{
							final Npc knight = addSpawn(FROST_KNIGHT, npc.getX() + getRandom(-100, 100), npc.getY() + getRandom(-100, 100), npc.getZ(), getRandom(64000), false, 300000, false, world.getId());
							addAttackPlayerDesire(knight, attacker);
						}
					}
					break;
				}
				case FREYA:
				{
					if (npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						for (int c = 0; c < 6; c++)
						{
							final Npc knight = addSpawn(FROST_KNIGHT, npc.getX() + getRandom(-100, 100), npc.getY() + getRandom(-100, 100), npc.getZ(), getRandom(64000), false, 300000, false, world.getId());
							addAttackPlayerDesire(knight, attacker);
						}
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case FROST_GOLEM:
				{
					if (SECOND_SPAWN_ZONE.isInsideZone(npc))
					{
						world.spawnGroup("buffalo");
					}
					if (FOURTH_SPAWN_ZONE.isInsideZone(npc) && (world.getStatus() == 1))
					{
						world.setStatus(2);
						world.broadcastPacket(new ExShowScreenMessage(NpcStringId.AN_INTENSE_COLD_IS_COMING_LOOK_AROUND, ExShowScreenMessage.TOP_CENTER, 7000, true));
						world.spawnGroup("panthera");
					}
					if (FIFTH_SPAWN_ZONE.isInsideZone(npc))
					{
						world.spawnGroup("ursus");
					}
					break;
				}
				case GLACIER_FROSTBRINGER:
				{
					addSpawn(SYRRA, npc, false, 120000, false, world.getId());
					break;
				}
				case FREYA:
				{
					startQuestTimer("finishInstance", 5000, npc, null);
					if (player.isInParty())
					{
						final Party party = player.getParty();
						final List<PlayerInstance> members = party.getMembers();
						for (PlayerInstance member : members)
						{
							if (member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
							{
								QuestProgress(npc, member);
							}
						}
					}
					break;
					
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case FREYA:
				{
					npc.setCurrentHp(npc.getMaxHp() / 2);
					break;
				}
				case ICE_KNIGHT:
				{
					startQuestTimer("startAttack", 2000, npc, null);
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		String htmltext = null;
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case SAYAN:
				{
					if (world.isStatus(0))
					{
						htmltext = "34172.html";
						break;
					}
					htmltext = "34172-05.html";
					break;
				}
				case KANNA:
				{
					if (world.isStatus(0))
					{
						htmltext = "34173.html";
						break;
					}
					htmltext = "34173-01.html";
					break;
				}
				case SYRRA:
				{
					htmltext = "34174.html";
					break;
				}
			}
		}
		return htmltext;
	}
	
	protected void QuestProgress(Npc npc, PlayerInstance player)
	{
		final QuestState qs = player.getQuestState(Q00835_PitiableMelisa.class.getSimpleName());
		if ((qs != null) && qs.isStarted())
		{
			final Quest qs835 = QuestManager.getInstance().getQuest(Q00835_PitiableMelisa.class.getSimpleName());
			if (qs835 != null)
			{
				qs835.notifyEvent("NOTIFY_Q835", npc, player);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new StoryOfFreya();
	}
}