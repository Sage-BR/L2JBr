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
package instances.SSQMonasteryOfSilence;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.skills.AbnormalVisualEffect;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.network.NpcStringId;

import instances.AbstractInstance;
import quests.Q10295_SevenSignsSolinasTomb.Q10295_SevenSignsSolinasTomb;

/**
 * Monastery of Silence instance zone.
 * @author Adry_85
 */
public class SSQMonasteryOfSilence extends AbstractInstance
{
	// NPCs
	private static final int ELCADIA_INSTANCE = 32787;
	private static final int ERIS_EVIL_THOUGHTS = 32792;
	private static final int RELIC_GUARDIAN = 32803;
	private static final int RELIC_WATCHER1 = 32804;
	private static final int RELIC_WATCHER2 = 32805;
	private static final int RELIC_WATCHER3 = 32806;
	private static final int RELIC_WATCHER4 = 32807;
	private static final int ODD_GLOBE = 32815;
	private static final int TELEPORT_CONTROL_DEVICE1 = 32817;
	private static final int TELEPORT_CONTROL_DEVICE2 = 32818;
	private static final int TELEPORT_CONTROL_DEVICE3 = 32819;
	private static final int TELEPORT_CONTROL_DEVICE4 = 32820;
	private static final int GUARDIAN_STAFF = 18952;
	private static final int GUARDIAN_SWORD = 18953;
	private static final int GUARDIAN_SHIELD = 18954;
	private static final int GUARDIAN_SCROLL = 18955;
	// Skills
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(6725, 1), // Bless the Blood of Elcadia
		new SkillHolder(6728, 1), // Recharge of Elcadia
		new SkillHolder(6730, 1), // Greater Battle Heal of Elcadia
	};
	// Locations
	private static final Location CENTRAL_ROOM_LOC = new Location(85794, -249788, -8320);
	private static final Location SOUTH_WATCHERS_ROOM_LOC = new Location(85798, -246566, -8320);
	private static final Location WEST_WATCHERS_ROOM_LOC = new Location(82531, -249405, -8320);
	private static final Location EAST_WATCHERS_ROOM_LOC = new Location(88665, -249784, -8320);
	private static final Location NORTH_WATCHERS_ROOM_LOC = new Location(85792, -252336, -8320);
	private static final Location BACK_LOC = new Location(120710, -86971, -3392);
	// NpcString
	private static final NpcStringId[] ELCADIA_DIALOGS =
	{
		NpcStringId.IT_SEEMS_THAT_YOU_CANNOT_REMEMBER_TO_THE_ROOM_OF_THE_WATCHER_WHO_FOUND_THE_BOOK,
		NpcStringId.WE_MUST_SEARCH_HIGH_AND_LOW_IN_EVERY_ROOM_FOR_THE_READING_DESK_THAT_CONTAINS_THE_BOOK_WE_SEEK,
		NpcStringId.REMEMBER_THE_CONTENT_OF_THE_BOOKS_THAT_YOU_FOUND_YOU_CAN_T_TAKE_THEM_OUT_WITH_YOU
	};
	private static final NpcStringId[] ELCADIA_DIALOGS2 =
	{
		NpcStringId.TO_REMOVE_THE_BARRIER_YOU_MUST_FIND_THE_RELICS_THAT_FIT_THE_BARRIER_AND_ACTIVATE_THE_DEVICE,
	};
	// Misc
	private static final int TEMPLATE_ID = 151;
	
	public SSQMonasteryOfSilence()
	{
		super(TEMPLATE_ID);
		addFirstTalkId(TELEPORT_CONTROL_DEVICE1, TELEPORT_CONTROL_DEVICE2, TELEPORT_CONTROL_DEVICE3, TELEPORT_CONTROL_DEVICE4, ERIS_EVIL_THOUGHTS);
		addStartNpc(ODD_GLOBE, TELEPORT_CONTROL_DEVICE1, TELEPORT_CONTROL_DEVICE2, TELEPORT_CONTROL_DEVICE3, TELEPORT_CONTROL_DEVICE4, ERIS_EVIL_THOUGHTS);
		addTalkId(ODD_GLOBE, ERIS_EVIL_THOUGHTS, RELIC_GUARDIAN, RELIC_WATCHER1, RELIC_WATCHER2, RELIC_WATCHER3, RELIC_WATCHER4, TELEPORT_CONTROL_DEVICE1, TELEPORT_CONTROL_DEVICE2, TELEPORT_CONTROL_DEVICE3, TELEPORT_CONTROL_DEVICE4, ERIS_EVIL_THOUGHTS);
	}
	
	@Override
	protected void onEnter(PlayerInstance player, Instance instance, boolean firstEnter)
	{
		super.onEnter(player, instance, firstEnter);
		
		if (firstEnter)
		{
			final Npc elcadia = addSpawn(ELCADIA_INSTANCE, player, false, 0, false, player.getInstanceId());
			startQuestTimer("FOLLOW", 3000, elcadia, player);
			
			final Npc guardianStaff = player.getInstanceWorld().getNpc(GUARDIAN_STAFF);
			guardianStaff.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
			guardianStaff.setIsInvul(true);
			final Npc guardianSword = player.getInstanceWorld().getNpc(GUARDIAN_SWORD);
			guardianSword.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
			guardianSword.setIsInvul(true);
			final Npc guardianShield = player.getInstanceWorld().getNpc(GUARDIAN_SHIELD);
			guardianShield.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
			guardianShield.setIsInvul(true);
			final Npc guardianScroll = player.getInstanceWorld().getNpc(GUARDIAN_SCROLL);
			guardianScroll.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
			guardianScroll.setIsInvul(true);
		}
		else
		{
			final Npc elcadia = player.getInstanceWorld().getNpc(ELCADIA_INSTANCE);
			if (elcadia != null)
			{
				elcadia.teleToLocation(player);
				cancelQuestTimers("FOLLOW");
				startQuestTimer("FOLLOW", 3000, elcadia, player);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			final Npc elcadia = world.getNpc(ELCADIA_INSTANCE);
			switch (event)
			{
				case "TELE2":
				{
					player.teleToLocation(CENTRAL_ROOM_LOC);
					elcadia.teleToLocation(CENTRAL_ROOM_LOC);
					startQuestTimer("START_MOVIE", 2000, npc, player);
					break;
				}
				case "EXIT":
				{
					cancelQuestTimer("FOLLOW", npc, player);
					world.finishInstance(0);
					break;
				}
				case "START_MOVIE":
				{
					playMovie(player, Movie.SSQ2_HOLY_BURIAL_GROUND_OPENING);
					break;
				}
				case "BACK":
				{
					player.teleToLocation(BACK_LOC);
					elcadia.teleToLocation(BACK_LOC);
					break;
				}
				case "EAST":
				{
					player.teleToLocation(EAST_WATCHERS_ROOM_LOC);
					elcadia.teleToLocation(EAST_WATCHERS_ROOM_LOC);
					break;
				}
				case "WEST":
				{
					player.teleToLocation(WEST_WATCHERS_ROOM_LOC);
					elcadia.teleToLocation(WEST_WATCHERS_ROOM_LOC);
					break;
				}
				case "NORTH":
				{
					player.teleToLocation(NORTH_WATCHERS_ROOM_LOC);
					elcadia.teleToLocation(NORTH_WATCHERS_ROOM_LOC);
					break;
				}
				case "SOUTH":
				{
					player.teleToLocation(SOUTH_WATCHERS_ROOM_LOC);
					elcadia.teleToLocation(SOUTH_WATCHERS_ROOM_LOC);
					break;
				}
				case "CENTER":
				{
					player.teleToLocation(CENTRAL_ROOM_LOC);
					elcadia.teleToLocation(CENTRAL_ROOM_LOC);
					break;
				}
				case "FOLLOW":
				{
					if (npc == null)
					{
						return null;
					}
					npc.setRunning();
					npc.getAI().startFollow(player);
					if (player.isInCombat())
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_GUARDIAN_OF_THE_SEAL_DOESN_T_SEEM_TO_GET_INJURED_AT_ALL_UNTIL_THE_BARRIER_IS_DESTROYED);
						SkillCaster.triggerCast(npc, player, getRandomEntry(BUFFS).getSkill());
					}
					else
					{
						final QuestState qs = player.getQuestState(Q10295_SevenSignsSolinasTomb.class.getSimpleName());
						if ((qs != null) && (qs.getCond() < 2) && !qs.isCompleted())
						{
							npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ELCADIA_DIALOGS2));
						}
						else if (qs == null)
						{
							npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ELCADIA_DIALOGS));
						}
					}
					startQuestTimer("FOLLOW", 10000, npc, player);
					break;
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance talker)
	{
		if (npc.getId() == ODD_GLOBE)
		{
			enterInstance(talker, npc, TEMPLATE_ID);
		}
		return super.onTalk(npc, talker);
	}
	
	public static void main(String[] args)
	{
		new SSQMonasteryOfSilence();
	}
}