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
package quests.Q10751_WindsOfFateEncounters;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.HtmlActionScope;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.World;
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
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
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

/**
 * Winds of Fate: Encounters (10751)
 * @URL https://l2wiki.com/Winds_of_Fate:_Encounters
 * @author malyelfik
 */
public class Q10751_WindsOfFateEncounters extends Quest
{
	// NPC
	private static final int NAVARI = 33931;
	private static final int AYANTHE = 33942;
	private static final int KATALIN = 33943;
	private static final int RAYMOND = 30289;
	private static final int MYSTERIOUS_WIZARD = 33980;
	private static final int TELESHA = 33981;
	// Monsters
	private static final int[] MONSTERS =
	{
		27528, // Skeleton Warrior
		27529, // Skeleton Archer
	};
	// Items
	private static final int WIND_SPIRIT_REALMS_RELIC = 39535;
	private static final int MAJOR_HEALING_POTION = 1061;
	private static final int SOULSHOT = 1464;
	private static final int SPIRITSHOT = 3949;
	private static final int PAULINA_EQUIPMENT_SET = 46850;
	// Location
	private static final Location TELEPORT_LOC = new Location(-80565, 251763, -3080);
	// Misc
	private static final int MIN_LEVEL = 38;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10751_WindsOfFateEncounters()
	{
		super(10751);
		addStartNpc(NAVARI);
		addFirstTalkId(TELESHA);
		addTalkId(NAVARI, AYANTHE, KATALIN, RAYMOND, TELESHA, MYSTERIOUS_WIZARD);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "");
		addCondInCategory(CategoryType.FIRST_CLASS_GROUP, "");
		addCondMinLevel(MIN_LEVEL, "33931-00.htm");
		registerQuestItems(WIND_SPIRIT_REALMS_RELIC);
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
			case "30289-02.html":
			case "30289-06.html":
			case "33942-05.html":
			case "33942-06.html":
			case "33942-07.html":
			case "33942-08.html":
			case "33942-09.html":
			case "33942-10.html":
			case "33943-05.html":
			case "33943-06.html":
			case "33943-07.html":
			case "33943-08.html":
			case "33943-09.html":
			case "33943-10.html":
			{
				break;
			}
			case "33931-02.htm":
			{
				qs.startQuest();
				if (player.isMageClass())
				{
					qs.setCond(0);
					qs.setCond(3, true);
				}
				else
				{
					qs.setCond(2, true);
					htmltext = "33931-03.htm";
				}
				break;
			}
			case "33943-02.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(4, true);
				}
				break;
			}
			case "33942-02.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(5, true);
				}
				break;
			}
			case "30289-03.html":
			{
				if (qs.isCond(4) || qs.isCond(5))
				{
					giveItems(player, WIND_SPIRIT_REALMS_RELIC, 1);
					qs.setCond(6, true);
				}
				break;
			}
			case "SPAWN_WIZZARD":
			{
				if (qs.isCond(6) && (npc != null) && (npc.getId() == TELESHA))
				{
					final Npc wizzard = addSpawn(MYSTERIOUS_WIZARD, npc, true, 30000);
					wizzard.setSummoner(player);
					wizzard.setTitle(player.getAppearance().getVisibleName());
					wizzard.broadcastInfo();
					showOnScreenMsg(player, NpcStringId.TALK_TO_THE_MYSTERIOUS_WIZARD_2, ExShowScreenMessage.TOP_CENTER, 10000);
					npc.deleteMe();
				}
				htmltext = null;
				break;
			}
			case "33980-06.html":
			{
				if (qs.isCond(6))
				{
					giveItems(player, WIND_SPIRIT_REALMS_RELIC, 1);
					qs.setCond(7, true);
					showOnScreenMsg(player, NpcStringId.RETURN_TO_RAYMOND_OF_THE_TOWN_OF_GLUDIO, ExShowScreenMessage.TOP_CENTER, 8000);
				}
				break;
			}
			case "30289-07.html":
			{
				if (qs.isCond(7))
				{
					if (!player.isMageClass())
					{
						qs.setCond(8, true);
					}
					else
					{
						qs.setCond(9, true);
						htmltext = "30289-08.html";
					}
				}
				break;
			}
			case "33942-11.html":
			{
				final ClassId newClass = ClassId.CLOUD_BREAKER;
				if (qs.isCond(9) && newClass.childOf(player.getClassId()))
				{
					if (!player.isSubClassActive())
					{
						player.setBaseClass(newClass);
					}
					player.setClassId(newClass.getId());
					player.broadcastUserInfo();
					player.sendSkillList();
					player.sendPacket(new SocialAction(player.getObjectId(), 23));
					giveAdena(player, 461880, true);
					giveItems(player, MAJOR_HEALING_POTION, 50);
					giveItems(player, SOULSHOT, 3000);
					giveItems(player, SPIRITSHOT, 3000);
					giveItems(player, PAULINA_EQUIPMENT_SET, 1);
					addExpAndSp(player, 2700000, 648);
					qs.exitQuest(false, true);
				}
				break;
			}
			case "33943-11.html":
			{
				final ClassId newClass = ClassId.MARAUDER;
				if (qs.isCond(8) && newClass.childOf(player.getClassId()))
				{
					if (!player.isSubClassActive())
					{
						player.setBaseClass(newClass);
					}
					player.setClassId(newClass.getId());
					player.broadcastUserInfo();
					player.sendSkillList();
					player.sendPacket(new SocialAction(player.getObjectId(), 23));
					giveAdena(player, 461880, true);
					giveItems(player, MAJOR_HEALING_POTION, 50);
					giveItems(player, SOULSHOT, 3000);
					giveItems(player, SPIRITSHOT, 3000);
					giveItems(player, PAULINA_EQUIPMENT_SET, 1);
					addExpAndSp(player, 2700000, 648);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		if (npc.getId() == TELESHA)
		{
			htmltext = "33981-01.html";
		}
		if (npc.getId() == MYSTERIOUS_WIZARD)
		{
			htmltext = "33980-01.html";
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case NAVARI:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = "33931-01.htm";
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 2:
							{
								htmltext = "33931-04.html";
								break;
							}
							case 3:
							{
								htmltext = "33931-05.html";
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
				break;
			}
			case KATALIN:
			{
				if (!player.isMageClass())
				{
					if (qs.isStarted())
					{
						switch (qs.getCond())
						{
							case 2:
							{
								htmltext = "33943-01.html";
								break;
							}
							case 4:
							{
								htmltext = "33943-03.html";
								break;
							}
							case 8:
							{
								htmltext = "33943-04.html";
								break;
							}
						}
					}
					else if (qs.isCompleted())
					{
						htmltext = getAlreadyCompletedMsg(player);
					}
				}
				break;
			}
			case AYANTHE:
			{
				if (player.isMageClass())
				{
					if (qs.isStarted())
					{
						switch (qs.getCond())
						{
							case 3:
							{
								htmltext = "33942-01.html";
								break;
							}
							case 5:
							{
								htmltext = "33942-03.html";
								break;
							}
							case 9:
							{
								htmltext = "33942-04.html";
								break;
							}
						}
					}
					else if (qs.isCompleted())
					{
						htmltext = getAlreadyCompletedMsg(player);
					}
				}
				break;
			}
			case RAYMOND:
			{
				if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 4:
						case 5:
						{
							htmltext = "30289-01.html";
							break;
						}
						case 6:
						{
							htmltext = "30289-04.html";
							break;
						}
						case 7:
						{
							htmltext = "30289-05.html";
							break;
						}
						case 8:
						{
							htmltext = "30289-09.html";
							break;
						}
						case 9:
						{
							htmltext = "30289-10.html";
							break;
						}
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(6))
		{
			int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount <= 5)
			{
				qs.set(KILL_COUNT_VAR, ++killCount);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			
			if ((killCount >= 5) && !World.getInstance().getVisibleObjectsInRange(npc, Npc.class, 1000).stream().anyMatch(n -> ((n.getId() == TELESHA) && (n.getSummoner() == killer))))
			{
				final Npc telsha = addSpawn(TELESHA, npc, false, 30000);
				telsha.setSummoner(killer);
				telsha.setTitle(killer.getAppearance().getVisibleName());
				telsha.broadcastInfo();
				showOnScreenMsg(killer, NpcStringId.CHECK_ON_TELESHA, ExShowScreenMessage.TOP_CENTER, 10000);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(6))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>(1);
				holder.add(new NpcLogListHolder(NpcStringId.KILL_SKELETONS, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
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
				player.sendPacket(new PlaySound(3, "Npcdialog1.serenia_quest_12", 0, 0, 0, 0, 0));
				player.sendPacket(new TutorialShowHtml(getHtm(player, "popup.html")));
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
			if (command.equals("Q10751_teleport"))
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
			else if (command.equals("Q10751_close"))
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
		
		if ((qs == null) && (player.getRace() == Race.ERTHEIA) && (oldLevel < newLevel) && (newLevel >= MIN_LEVEL) && (player.isInCategory(CategoryType.FIRST_CLASS_GROUP)))
		{
			showOnScreenMsg(player, NpcStringId.QUEEN_NAVARI_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 10000);
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
		
		if ((qs == null) && (player.getRace() == Race.ERTHEIA) && (player.getLevel() >= MIN_LEVEL) && (player.isInCategory(CategoryType.FIRST_CLASS_GROUP)))
		{
			showOnScreenMsg(player, NpcStringId.QUEEN_NAVARI_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 10000);
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
		}
	}
}