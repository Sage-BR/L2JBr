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
package quests.Q10752_WindsOfFateAPromise;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.HtmlActionScope;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerBypass;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLevelChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPressTutorialMark;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;
import org.l2jbr.gameserver.network.serverpackets.SocialAction;
import org.l2jbr.gameserver.network.serverpackets.TutorialCloseHtml;
import org.l2jbr.gameserver.network.serverpackets.TutorialShowHtml;
import org.l2jbr.gameserver.network.serverpackets.TutorialShowQuestionMark;
import org.l2jbr.gameserver.taskmanager.AttackStanceTaskManager;

import quests.Q10751_WindsOfFateEncounters.Q10751_WindsOfFateEncounters;

/**
 * Winds of Fate: A Promise (10752)
 * @author Gladicek
 */
public class Q10752_WindsOfFateAPromise extends Quest
{
	// NPCs
	private static final int KATALIN = 33943;
	private static final int AYANTHE = 33942;
	private static final int KARLA = 33933;
	private static final int SIEGMUND = 31321;
	private static final int LOMBERT = 31317;
	private static final int MYSTERIOUS_WIZARD = 31522;
	private static final int TOMBSTONE = 31523;
	private static final int GHOST_OF_VON_HELLMAN = 31524;
	private static final int BROKEN_BOOKSHELF = 31526;
	// Items
	private static final int NAVARI_MARK = 39536;
	private static final int PROPHECY_MACHINE_FRAGMENT = 39537;
	private static final int KAIN_PROPHECY_MACHINE_FRAGMENT = 39538;
	private static final int SOULSHOT = 1467;
	private static final int SPIRITSHOT = 3952;
	private static final int BLESSED_SCROLL_OF_RESURRECTION = 33518;
	private static final int PAULINAS_EQUIPMENT_SET = 46852;
	// Location
	private static final Location TELEPORT_LOC = new Location(-81948, 249635, -3371);
	private static final Location GHOST_SPAWN_LOC = new Location(51483, -54407, -3160);
	// Misc
	private static final int MIN_LEVEL = 76;
	
	public Q10752_WindsOfFateAPromise()
	{
		super(10752);
		addStartNpc(KATALIN, AYANTHE);
		addTalkId(KATALIN, AYANTHE, KARLA, SIEGMUND, LOMBERT, MYSTERIOUS_WIZARD, GHOST_OF_VON_HELLMAN, TOMBSTONE, BROKEN_BOOKSHELF);
		
		addCondRace(Race.ERTHEIA, "");
		addCondInCategory(CategoryType.THIRD_CLASS_GROUP, "");
		registerQuestItems(NAVARI_MARK, PROPHECY_MACHINE_FRAGMENT, KAIN_PROPHECY_MACHINE_FRAGMENT);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		
		switch (event)
		{
			case "33943-02.htm":
			case "33943-03.htm":
			case "33943-04.htm":
			case "33942-02.htm":
			case "33942-03.htm":
			case "33942-04.htm":
			case "31522-02.html":
			case "31522-03.html":
			case "31522-04.html":
			case "31321-02.html":
			case "31317-02.html":
			case "31317-03.html":
			case "31317-04.html":
			case "31523-02.html":
			case "31524-02.html":
			{
				htmltext = event;
				break;
			}
			case "33942-05.html":
			case "33943-05.html":
			{
				qs.startQuest();
				break;
			}
			case "33933-04.html":
			{
				if (qs.isCond(1))
				{
					giveItems(player, NAVARI_MARK, 1);
					giveItems(player, PROPHECY_MACHINE_FRAGMENT, 1);
					qs.setCond(2, true);
				}
				break;
			}
			case "31321-03.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
				}
				break;
			}
			case "31317-05.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4, true);
				}
				break;
			}
			case "31522-05.html":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5, true);
				}
				break;
			}
			case "31523-03.html":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6, true);
				}
				showOnScreenMsg(player, NpcStringId.TALK_TO_THE_GHOST_OF_VON_HELLMANN, ExShowScreenMessage.TOP_CENTER, 5000);
				addSpawn(GHOST_OF_VON_HELLMAN, GHOST_SPAWN_LOC, true, 20000);
				break;
			}
			case "31524-03.html":
			{
				if (qs.isCond(6))
				{
					qs.setCond(7, true);
					showOnScreenMsg(player, NpcStringId.TIME_TO_MOVE_ONTO_THE_NEXT_PLACE, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				break;
			}
			case "31526-03.html":
			{
				if (qs.isCond(7))
				{
					qs.setCond(8, true);
				}
				break;
			}
			case "checkClass":
			{
				if (qs.isCond(9))
				{
					takeItems(player, NAVARI_MARK, -1);
					takeItems(player, PROPHECY_MACHINE_FRAGMENT, -1);
					takeItems(player, KAIN_PROPHECY_MACHINE_FRAGMENT, -1);
					
					if (player.isMageClass())
					{
						qs.setCond(10, true);
						htmltext = "33933-10.html";
					}
					else
					{
						qs.setCond(11, true);
						htmltext = "33933-09.html";
					}
				}
				break;
			}
			case "33942-12.html":
			{
				final ClassId newClass = ClassId.STRATOMANCER;
				if (qs.isCond(10) && newClass.childOf(player.getClassId()))
				{
					if (!player.isSubClassActive())
					{
						player.setBaseClass(newClass);
					}
					player.setClassId(newClass.getId());
					player.broadcastUserInfo();
					player.sendSkillList();
					player.sendPacket(new SocialAction(player.getObjectId(), 24));
					giveAdena(player, 1599120, false);
					giveItems(player, SOULSHOT, 8000);
					giveItems(player, SPIRITSHOT, 8000);
					giveItems(player, BLESSED_SCROLL_OF_RESURRECTION, 3);
					giveItems(player, PAULINAS_EQUIPMENT_SET, 1);
					addExpAndSp(player, 42000000, 0);
					qs.exitQuest(false, true);
				}
				break;
			}
			case "33943-10.html":
			{
				final ClassId newClass = ClassId.RIPPER;
				if (qs.isCond(11) && newClass.childOf(player.getClassId()))
				{
					if (!player.isSubClassActive())
					{
						player.setBaseClass(newClass);
					}
					player.setClassId(newClass.getId());
					player.broadcastUserInfo();
					player.sendSkillList();
					player.sendPacket(new SocialAction(player.getObjectId(), 24));
					giveAdena(player, 1599120, false);
					giveItems(player, SOULSHOT, 8000);
					giveItems(player, SPIRITSHOT, 8000);
					giveItems(player, BLESSED_SCROLL_OF_RESURRECTION, 3);
					giveItems(player, PAULINAS_EQUIPMENT_SET, 1);
					addExpAndSp(player, 42000000, 0);
					qs.exitQuest(false, true);
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
		final QuestState qs1 = player.getQuestState(Q10751_WindsOfFateEncounters.class.getSimpleName());
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == KATALIN)
				{
					if ((player.getLevel() >= MIN_LEVEL) && qs1.isCompleted())
					{
						htmltext = !player.isMageClass() ? "33943-01.htm" : "33943-12.html";
						break;
					}
					htmltext = "33943-12.html";
					break;
				}
				else if (npc.getId() == AYANTHE)
				{
					if ((player.getLevel() >= MIN_LEVEL) && qs1.isCompleted())
					{
						htmltext = player.isMageClass() ? "33942-01.htm" : "33942-09.html";
						break;
					}
					htmltext = "33942-09.html";
					break;
				}
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case KATALIN:
					{
						if (qs.isCond(1))
						{
							htmltext = "33943-06.html";
							break;
						}
						else if (qs.isCond(11))
						{
							htmltext = "33943-07.html";
							break;
						}
						break;
					}
					case AYANTHE:
					{
						if (qs.isCond(1))
						{
							htmltext = "33942-06.html";
							break;
						}
						else if (qs.isCond(10))
						{
							htmltext = "33942-07.html";
							break;
						}
						break;
					}
					case KARLA:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "33933-01.html";
								break;
							}
							case 2:
							{
								htmltext = "33933-05.html";
								break;
							}
							case 9:
							{
								htmltext = "33933-06.html";
								break;
							}
							case 10:
							{
								htmltext = "33933-12.html";
								break;
							}
							case 11:
							{
								htmltext = "33933-11.html";
								break;
							}
						}
						break;
					}
					case SIEGMUND:
					{
						if (qs.isCond(2))
						{
							htmltext = "31321-01.html";
							break;
						}
						else if (qs.isCond(3))
						{
							htmltext = "31321-04.html";
							break;
						}
						break;
					}
					case LOMBERT:
					{
						if (qs.isCond(3))
						{
							htmltext = "31317-01.html";
							break;
						}
						else if (qs.isCond(4))
						{
							htmltext = "31317-06.html";
							break;
						}
						break;
					}
					case MYSTERIOUS_WIZARD:
					{
						if (qs.isCond(4))
						{
							htmltext = "31522-01.html";
							break;
						}
						else if (qs.isCond(5))
						{
							htmltext = "31522-06.html";
							break;
						}
						break;
					}
					case TOMBSTONE:
					{
						if (qs.isCond(5))
						{
							htmltext = "31523-01.html";
							break;
						}
						else if (qs.isCond(6))
						{
							htmltext = "31523-01.html";
							break;
						}
						else if (qs.isCond(7))
						{
							htmltext = "31523-03.html";
							break;
						}
						break;
					}
					case GHOST_OF_VON_HELLMAN:
					{
						if (qs.isCond(6))
						{
							htmltext = "31524-01.html";
							break;
						}
						else if (qs.isCond(7))
						{
							htmltext = "31524-03.html";
							break;
						}
						break;
					}
					case BROKEN_BOOKSHELF:
					{
						if (qs.isCond(7))
						{
							htmltext = "31526-01.html";
							break;
						}
						else if (qs.isCond(8))
						{
							htmltext = "31526-03.html";
							break;
						}
						else if (qs.isCond(9))
						{
							htmltext = "31526-04.html";
							break;
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
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		if (event.getMarkId() == getId())
		{
			final PlayerInstance player = event.getPlayer();
			final QuestState qs = getQuestState(player, false);
			if (qs == null)
			{
				if (player.isMageClass())
				{
					player.sendPacket(new PlaySound(3, "Npcdialog1.aris_quest_1", 0, 0, 0, 0, 0));
					player.sendPacket(new TutorialShowHtml(getHtm(player, "popup1.html")));
				}
				else
				{
					player.sendPacket(new PlaySound(3, "Npcdialog1.katrina_quest_1", 0, 0, 0, 0, 0));
					player.sendPacket(new TutorialShowHtml(getHtm(player, "popup2.html")));
				}
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerBypass(OnPlayerBypass event)
	{
		final String command = event.getCommand();
		final PlayerInstance player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		
		if (qs == null)
		{
			if (command.equals("Q10752_teleport"))
			{
				player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				
				if (CastleManager.getInstance().getCastles().stream().anyMatch(c -> c.getSiege().isInProgress()))
				{
					showOnScreenMsg(player, NpcStringId.YOU_MAY_NOT_TELEPORT_IN_MIDDLE_OF_A_SIEGE, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (player.isInParty())
				{
					showOnScreenMsg(player, NpcStringId.YOU_CANNOT_TELEPORT_IN_PARTY_STATUS, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (player.isInInstance())
				{
					showOnScreenMsg(player, NpcStringId.YOU_MAY_NOT_TELEPORT_WHILE_USING_INSTANCE_ZONE, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player))
				{
					showOnScreenMsg(player, NpcStringId.YOU_CANNOT_TELEPORT_IN_COMBAT, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (player.isTransformed())
				{
					showOnScreenMsg(player, NpcStringId.YOU_CANNOT_TELEPORT_WHILE_IN_A_TRANSFORMED_STATE, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (player.isDead())
				{
					showOnScreenMsg(player, NpcStringId.YOU_CANNOT_TELEPORT_WHILE_YOU_ARE_DEAD, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else
				{
					player.teleToLocation(TELEPORT_LOC);
				}
				player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
			}
			else if (command.equals("Q10752_close"))
			{
				player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
				player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		if (Config.DISABLE_TUTORIAL)
		{
			return;
		}
		
		final PlayerInstance player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		final int oldLevel = event.getOldLevel();
		final int newLevel = event.getNewLevel();
		
		if ((qs == null) && (player.getRace() == Race.ERTHEIA) && (oldLevel < newLevel) && (newLevel >= MIN_LEVEL) && (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)))
		{
			if (player.isMageClass())
			{
				showOnScreenMsg(player, NpcStringId.MAGISTER_AYANTHE_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 10000);
			}
			else
			{
				showOnScreenMsg(player, NpcStringId.MASTER_KATALIN_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 10000);
			}
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		if (Config.DISABLE_TUTORIAL)
		{
			return;
		}
		
		final PlayerInstance player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		
		if ((qs == null) && (player.getRace() == Race.ERTHEIA) && (player.getLevel() >= MIN_LEVEL) && (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)))
		{
			if (player.isMageClass())
			{
				showOnScreenMsg(player, NpcStringId.MAGISTER_AYANTHE_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 10000);
			}
			else
			{
				showOnScreenMsg(player, NpcStringId.MASTER_KATALIN_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 10000);
			}
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
		}
	}
}