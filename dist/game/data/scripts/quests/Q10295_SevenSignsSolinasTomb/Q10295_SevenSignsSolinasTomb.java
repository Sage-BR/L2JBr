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
package quests.Q10295_SevenSignsSolinasTomb;

import java.util.List;

import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.model.skills.AbnormalVisualEffect;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

import quests.Q10294_SevenSignsToTheMonasteryOfSilence.Q10294_SevenSignsToTheMonasteryOfSilence;

/**
 * Seven Signs, Solina's Tomb (10295)
 * @URL https://l2wiki.com/Seven_Signs,_Solina%27s_Tomb
 * @author Mobius
 */
public class Q10295_SevenSignsSolinasTomb extends Quest
{
	// NPCs
	private static final int ERIS_EVIL_THOUGHTS = 32792;
	private static final int ELCADIA_INSTANCE = 32787;
	private static final int TELEPORT_DEVICE = 32837;
	private static final int TELEPORT_DEVICE_2 = 32842;
	private static final int STAFF_ALTAR = 32857;
	private static final int SWORD_ALTAR = 32858;
	private static final int SHIELD_ALTAR = 32860;
	private static final int SCROLL_ALTAR = 32859;
	private static final int STAFF_DEVICE = 32841;
	private static final int SWORD_DEVICE = 32840;
	private static final int SHIELD_DEVICE = 32839;
	private static final int SCROLL_DEVICE = 32838;
	private static final int GUARDIAN_STAFF = 18952;
	private static final int GUARDIAN_SWORD = 18953;
	private static final int GUARDIAN_SHIELD = 18954;
	private static final int GUARDIAN_SCROLL = 18955;
	private static final int TOMB = 32843;
	private static final int SUPPLICANT_OF_REST = 27403;
	private static final int TRAINEE_OF_REST = 27404;
	private static final int SOLINAS_EVIL_THOUGHTS = 32793;
	// Items
	private static final int SCROLL_RELIC = 17228;
	private static final int SHIELD_RELIC = 17229;
	private static final int SWORD_RELIC = 17230;
	private static final int STAFF_RELIC = 17231;
	// Teleports
	private static final Location START_LOC = new Location(45545, -249423, -6760);
	private static final Location BACK_LOC = new Location(120727, -86868, -3392);
	private static final Location ROOM_LOC = new Location(56078, -252944, -6768);
	private static final Location ROOM_2_LOC = new Location(55955, -250394, -6760);
	// Client Effects
	private static final int TELEPORT_DEVICE_EVENT = 21100100;
	private static final int SCROLL_ALTAR_EVENT = 21100200;
	private static final int SHIELD_ALTAR_EVENT = 21100202;
	private static final int SWORD_ALTAR_EVENT = 21100204;
	private static final int STAFF_ALTAR_EVENT = 21100206;
	// Misc
	private static final int TELEPORTER_WALL = 21100018;
	private static final int[] WALLS =
	{
		21100001,
		21100006,
		21100010,
		21100014
	};
	private static final int[] TRAP_DOORS =
	{
		21100101,
		21100102,
		21100103,
		21100104
	};
	private static final int MIN_LEVEL = 81;
	
	public Q10295_SevenSignsSolinasTomb()
	{
		super(10295);
		addStartNpc(ERIS_EVIL_THOUGHTS);
		addTalkId(ERIS_EVIL_THOUGHTS, TOMB, TELEPORT_DEVICE, TELEPORT_DEVICE_2, SCROLL_ALTAR, STAFF_ALTAR, SWORD_ALTAR, SHIELD_ALTAR, SCROLL_DEVICE, STAFF_DEVICE, SWORD_DEVICE, SHIELD_DEVICE, SOLINAS_EVIL_THOUGHTS);
		addFirstTalkId(TOMB, TELEPORT_DEVICE, TELEPORT_DEVICE_2, SCROLL_ALTAR, STAFF_ALTAR, SWORD_ALTAR, SHIELD_ALTAR, SCROLL_DEVICE, STAFF_DEVICE, SWORD_DEVICE, SHIELD_DEVICE);
		addKillId(GUARDIAN_STAFF, GUARDIAN_SWORD, GUARDIAN_SHIELD, GUARDIAN_SCROLL, SUPPLICANT_OF_REST, TRAINEE_OF_REST);
		registerQuestItems(SCROLL_RELIC, SHIELD_RELIC, SWORD_RELIC, STAFF_RELIC);
		addCondMinLevel(MIN_LEVEL, "");
		addCondCompletedQuest(Q10294_SevenSignsToTheMonasteryOfSilence.class.getSimpleName(), "");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "32792-02.htm":
			case "32792-04.html":
			case "32792-05.html":
			case "32792-06.html":
			case "32837-03.html":
			case "32793-02.html":
			case "32793-03.html":
			case "32793-05.html":
			case "32793-06.html":
			case "32793-07.html":
			{
				htmltext = event;
				break;
			}
			case "32792-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "intro_movie":
			{
				player.teleToLocation(START_LOC);
				final Npc elcadia = player.getInstanceWorld().getNpc(ELCADIA_INSTANCE);
				elcadia.teleToLocation(player, true);
				if (qs.isCond(1))
				{
					player.sendPacket(new OnEventTrigger(TELEPORT_DEVICE_EVENT, true));
					player.sendPacket(new OnEventTrigger(SCROLL_ALTAR_EVENT, true));
					player.sendPacket(new OnEventTrigger(SHIELD_ALTAR_EVENT, true));
					player.sendPacket(new OnEventTrigger(SWORD_ALTAR_EVENT, true));
					player.sendPacket(new OnEventTrigger(STAFF_ALTAR_EVENT, true));
					playMovie(player, Movie.SSQ2_SOLINA_TOMB_OPENING);
				}
				return null;
			}
			case "32857-02.html":
			{
				if (!hasQuestItems(player, STAFF_RELIC))
				{
					giveItems(player, STAFF_RELIC, 1, true);
					tryToOpenDoors(player);
					htmltext = event;
				}
				break;
			}
			case "32858-02.html":
			{
				if (!hasQuestItems(player, SWORD_RELIC))
				{
					giveItems(player, SWORD_RELIC, 1, true);
					tryToOpenDoors(player);
					htmltext = event;
				}
				break;
			}
			case "32859-02.html":
			{
				if (!hasQuestItems(player, SCROLL_RELIC))
				{
					giveItems(player, SCROLL_RELIC, 1, true);
					tryToOpenDoors(player);
					htmltext = event;
				}
				break;
			}
			case "32860-02.html":
			{
				if (!hasQuestItems(player, SHIELD_RELIC))
				{
					giveItems(player, SHIELD_RELIC, 1, true);
					tryToOpenDoors(player);
					htmltext = event;
				}
				break;
			}
			case "teleport_back":
			{
				player.teleToLocation(BACK_LOC);
				final Npc elcadia = player.getInstanceWorld().getNpc(ELCADIA_INSTANCE);
				elcadia.teleToLocation(player, true);
				break;
			}
			case "enable_staff_guardian":
			{
				if (hasQuestItems(player, STAFF_RELIC))
				{
					takeItems(player, STAFF_RELIC, -1);
					final Npc guardian = player.getInstanceWorld().getNpc(GUARDIAN_STAFF);
					guardian.setIsInvul(false);
					guardian.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
				}
				return null;
			}
			case "enable_sword_guardian":
			{
				if (hasQuestItems(player, SWORD_RELIC))
				{
					takeItems(player, SWORD_RELIC, -1);
					final Npc guardian = player.getInstanceWorld().getNpc(GUARDIAN_SWORD);
					guardian.setIsInvul(false);
					guardian.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
				}
				return null;
			}
			case "enable_shield_guardian":
			{
				if (hasQuestItems(player, SHIELD_RELIC))
				{
					takeItems(player, SHIELD_RELIC, -1);
					final Npc guardian = player.getInstanceWorld().getNpc(GUARDIAN_SHIELD);
					guardian.setIsInvul(false);
					guardian.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
				}
				return null;
			}
			case "enable_scroll_guardian":
			{
				if (hasQuestItems(player, SCROLL_RELIC))
				{
					takeItems(player, SCROLL_RELIC, -1);
					final Npc guardian = player.getInstanceWorld().getNpc(GUARDIAN_SCROLL);
					guardian.setIsInvul(false);
					guardian.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
				}
				return null;
			}
			case "teleport_to_room":
			{
				if (qs.getInt("progress") == 2)
				{
					player.getInstanceWorld().despawnGroup("solina_trap");
					player.getInstanceWorld().setParameter("monstersCount", 0);
					for (int door : TRAP_DOORS)
					{
						closeDoor(door, player.getInstanceId());
					}
					player.teleToLocation(ROOM_LOC);
					final Npc elcadia = player.getInstanceWorld().getNpc(ELCADIA_INSTANCE);
					elcadia.teleToLocation(player, true);
				}
				return null;
			}
			case "activate_trap":
			{
				final List<Npc> monsters = player.getInstanceWorld().spawnGroup("solina_trap");
				player.getInstanceWorld().setParameter("monstersCount", monsters.size());
				for (int door : TRAP_DOORS)
				{
					openDoor(door, player.getInstanceId());
				}
				htmltext = "32843-02.html";
				break;
			}
			case "teleport_to_room_2":
			{
				if (qs.getInt("progress") == 3)
				{
					player.teleToLocation(ROOM_2_LOC);
					final Npc elcadia = player.getInstanceWorld().getNpc(ELCADIA_INSTANCE);
					elcadia.teleToLocation(player, true);
				}
				return null;
			}
			case "32793-04.html":
			{
				if (qs.getInt("progress") == 3)
				{
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "32793-08.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3);
				}
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	private void tryToOpenDoors(PlayerInstance player)
	{
		if (hasQuestItems(player, STAFF_RELIC, SWORD_RELIC, SCROLL_RELIC, SHIELD_RELIC))
		{
			for (int wall : WALLS)
			{
				openDoor(wall, player.getInstanceId());
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = getNoQuestMsg(player);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (npc.getId())
		{
			case TELEPORT_DEVICE:
			{
				
				if (qs.getInt("progress") == 2)
				{
					htmltext = "32837-02.html";
				}
				else
				{
					htmltext = "32837-01.html";
				}
				break;
			}
			case STAFF_ALTAR:
			{
				if (!hasQuestItems(player, STAFF_RELIC))
				{
					htmltext = "32857-01.html";
				}
				else
				{
					htmltext = "32857-03.html";
				}
				break;
			}
			case SWORD_ALTAR:
			{
				if (!hasQuestItems(player, SWORD_RELIC))
				{
					htmltext = "32858-01.html";
				}
				else
				{
					htmltext = "32858-03.html";
				}
				break;
			}
			case SHIELD_ALTAR:
			{
				if (!hasQuestItems(player, SHIELD_RELIC))
				{
					htmltext = "32860-01.html";
				}
				else
				{
					htmltext = "32860-03.html";
				}
				break;
			}
			case SCROLL_ALTAR:
			{
				if (!hasQuestItems(player, SCROLL_RELIC))
				{
					htmltext = "32859-01.html";
				}
				else
				{
					htmltext = "32859-03.html";
				}
				break;
			}
			case STAFF_DEVICE:
			{
				if (hasQuestItems(player, STAFF_RELIC))
				{
					htmltext = "32841-01.html";
				}
				else
				{
					htmltext = "32841-02.html";
				}
				break;
			}
			case SWORD_DEVICE:
			{
				if (hasQuestItems(player, SWORD_RELIC))
				{
					htmltext = "32840-01.html";
				}
				else
				{
					htmltext = "32840-02.html";
				}
				break;
			}
			case SHIELD_DEVICE:
			{
				if (hasQuestItems(player, SHIELD_RELIC))
				{
					htmltext = "32839-01.html";
				}
				else
				{
					htmltext = "32839-02.html";
				}
				break;
			}
			case SCROLL_DEVICE:
			{
				if (hasQuestItems(player, SCROLL_RELIC))
				{
					htmltext = "32838-01.html";
				}
				else
				{
					htmltext = "32838-02.html";
				}
				break;
			}
			case TOMB:
			{
				if (player.getInstanceWorld().getParameters().getInt("monstersCount") > 0)
				{
					htmltext = "32843-02.html";
				}
				else if (qs.getInt("progress") != 3)
				{
					htmltext = "32843-01.html";
				}
				else
				{
					htmltext = "32843-03.html";
				}
				break;
			}
			case TELEPORT_DEVICE_2:
			{
				if (qs.getInt("progress") == 3)
				{
					htmltext = "32842-01.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == ERIS_EVIL_THOUGHTS)
				{
					htmltext = "32792-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ERIS_EVIL_THOUGHTS:
					{
						if (qs.isCond(3))
						{
							qs.unset("guardianKills");
							qs.unset("progress");
							addExpAndSp(player, 44000000, 10560);
							qs.exitQuest(false, true);
							htmltext = "32792-10.html";
						}
						else if (qs.getInt("progress") == 3)
						{
							htmltext = "32792-09.html";
						}
						else if (qs.isCond(1))
						{
							htmltext = "32792-07.html";
						}
						else if (qs.getInt("progress") == 2)
						{
							htmltext = "32792-08.html";
						}
						break;
					}
					case SOLINAS_EVIL_THOUGHTS:
					{
						if (qs.isCond(3))
						{
							htmltext = "32793-08.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "32793-04.html";
						}
						else if (qs.getInt("progress") == 3)
						{
							htmltext = "32793-01.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		switch (npc.getId())
		{
			case GUARDIAN_STAFF:
			case GUARDIAN_SWORD:
			case GUARDIAN_SHIELD:
			case GUARDIAN_SCROLL:
			{
				int kills = qs.getInt("guardianKills");
				kills++;
				if (kills >= 4)
				{
					qs.set("progress", 2);
					playMovie(player, Movie.SSQ2_SOLINA_TOMB_CLOSING);
				}
				else
				{
					qs.set("guardianKills", kills);
				}
				break;
			}
			case SUPPLICANT_OF_REST:
			case TRAINEE_OF_REST:
			{
				int alive = player.getInstanceWorld().getParameters().getInt("monstersCount");
				alive--;
				if (alive <= 1)
				{
					qs.set("progress", 3);
					openDoor(TELEPORTER_WALL, player.getInstanceId());
				}
				else
				{
					player.getInstanceWorld().getParameters().set("monstersCount", alive);
				}
				break;
			}
		}
		
		return super.onKill(npc, player, isSummon);
	}
}
