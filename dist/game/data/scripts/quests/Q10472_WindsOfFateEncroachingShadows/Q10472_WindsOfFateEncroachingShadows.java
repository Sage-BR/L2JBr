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
package quests.Q10472_WindsOfFateEncroachingShadows;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLevelChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPressTutorialMark;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.TutorialShowHtml;
import org.l2jbr.gameserver.network.serverpackets.TutorialShowQuestionMark;

/**
 * Winds of Fate: Encroaching Shadows (10472)<br>
 * This quest is also included in these AIs:<br>
 * <ul>
 * <li><b>Hardin</b> (first talk event)</li>
 * <li><b>AwakeningMaster</b> (first talk event)</li>
 * </ul>
 * @author malyelfik
 */
public class Q10472_WindsOfFateEncroachingShadows extends Quest
{
	// NPCs
	private static final int NAVARI = 33931;
	private static final int ZEPHYRA = 33978;
	private static final int MOMET = 33998;
	private static final int BLACK_MARKETEER_MAMMON = 31092;
	private static final int BLACKSMITH_OF_MAMMON = 31126;
	private static final int HARDIN = 33870;
	private static final int KARLA = 33933;
	private static final int RAINA = 33491;
	// Mobs
	private static final int[] MOBS =
	{
		23174, // Arbitor of Darkness
		23175, // Altar of Evil Spirit Offering Box
		23176, // Mutated Cerberos
		23177, // Dartanion
		23178, // Insane Phion
		23179, // Dimensional Rifter
		23180, // Hellgate Fighting Dog
	};
	// Items
	private static final int DARK_FRAGMENT = 40060;
	private static final int COUNTERFEIT_ATELIA = 40059;
	// Rewards
	private static final ItemHolder RECIPE_TWILIGHT_NECKLACE = new ItemHolder(36791, 1);
	private static final ItemHolder CRYSTAL_R = new ItemHolder(17371, 5);
	private static final ItemHolder RED_SOUL_CRYSTAL_15 = new ItemHolder(10480, 1);
	private static final ItemHolder BLUE_SOUL_CRYSTAL_15 = new ItemHolder(10481, 1);
	private static final ItemHolder GREEN_SOUL_CRYSTAL_15 = new ItemHolder(10482, 1);
	private static final ItemHolder FIRE_STONE = new ItemHolder(9546, 15);
	private static final ItemHolder WATER_STONE = new ItemHolder(9547, 15);
	private static final ItemHolder EARTH_STONE = new ItemHolder(9548, 15);
	private static final ItemHolder WIND_STONE = new ItemHolder(9549, 15);
	private static final ItemHolder DARK_STONE = new ItemHolder(9550, 15);
	private static final ItemHolder HOLY_STONE = new ItemHolder(9551, 15);
	// Skill
	private static final SkillHolder ABSORB_WIND = new SkillHolder(16389, 1);
	private static final SkillHolder ATELIA_ENERGY = new SkillHolder(16398, 1);
	private static final SkillHolder FERINS_CURE = new SkillHolder(16399, 1);
	// Misc
	private static final double DROP_CHANCE = 0.6d; // Guessed
	private static final int DARK_FRAGMENT_COUNT = 50;
	private static final int MIN_LEVEL = 85;
	// Teleport
	private static final Location TELEPORT_LOC = new Location(-80565, 251763, -3080);
	
	public Q10472_WindsOfFateEncroachingShadows()
	{
		super(10472);
		addStartNpc(NAVARI);
		addTalkId(NAVARI, ZEPHYRA, MOMET, BLACK_MARKETEER_MAMMON, BLACKSMITH_OF_MAMMON, HARDIN, KARLA, RAINA);
		addKillId(MOBS);
		addCondRace(Race.ERTHEIA, "33931-00.htm");
		addCondInCategory(CategoryType.ERTHEIA_FOURTH_CLASS_GROUP, "33931-00.htm");
		registerQuestItems(DARK_FRAGMENT, COUNTERFEIT_ATELIA);
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
			case "33931-02.htm":
			case "33931-03.htm":
			case "33931-04.htm":
			case "33978-02.html":
			case "33998-02.html":
			case "33998-03.html":
			case "31092-07.html":
			case "31126-02.html":
			case "31126-03.html":
			case "31126-04.html":
			case "31126-05.html":
			case "31126-06.html":
			case "31126-07.html":
			case "33870-02.html":
			case "33870-03.html":
			case "33870-04.html":
			case "33870-08.html":
			case "33978-06.html":
			case "33933-02.html":
			case "33491-02.html":
			case "33491-03.html":
			case "33491-04.html":
			{
				break;
			}
			case "33931-05.htm": // Navari
			{
				qs.startQuest();
				break;
			}
			case "33978-03.html": // Zephyra
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
				}
				break;
			}
			case "33978-07.html":
			{
				if (qs.isCond(17))
				{
					npc.doCast(FERINS_CURE.getSkill());
					qs.setCond(18, true);
				}
				break;
			}
			case "33998-04.html": // Momet
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
				}
				break;
			}
			case "31092-02.html": // Black Marketeer Mammon
			{
				htmltext = getHtm(player, event).replace("%playerName%", player.getName());
				break;
			}
			case "31092-03.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4, true);
				}
				break;
			}
			case "31092-06.html":
			{
				npc.setTarget(player);
				npc.doCast(ABSORB_WIND.getSkill());
				qs.setMemoState(1);
				break;
			}
			case "31092-08.html":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6, true);
					qs.setMemoState(0);
					takeItems(player, DARK_FRAGMENT, DARK_FRAGMENT_COUNT);
				}
				break;
			}
			case "31126-08.html": // Blacksmith Mammon
			{
				if (qs.isCond(6))
				{
					qs.setCond(7, true);
					giveItems(player, COUNTERFEIT_ATELIA, 1);
				}
				break;
			}
			case "33870-05.html": // Hardin
			{
				if (qs.isCond(7))
				{
					qs.setCond(8, true);
				}
				break;
			}
			case "33870-09.html":
			{
				if (qs.isCond(16))
				{
					takeItems(player, COUNTERFEIT_ATELIA, 1);
					npc.setTarget(player);
					npc.doCast(ATELIA_ENERGY.getSkill()); // TODO: Implement this skill
					qs.setCond(17, true);
				}
				break;
			}
			case "33933-03.html": // Karla
			{
				if (qs.isCond(18))
				{
					qs.setCond(19, true);
				}
				break;
			}
			case "33491-red": // Raina
			case "33491-blue":
			case "33491-green":
			{
				if (qs.isCond(19))
				{
					qs.set("SoulCrystal", event.split("-")[1]);
					htmltext = "33491-05.html";
				}
				break;
			}
			case "33491-fire":
			case "33491-water":
			case "33491-earth":
			case "33491-wind":
			case "33491-dark":
			case "33491-holy":
			{
				if (qs.isCond(19) && qs.isSet("SoulCrystal"))
				{
					// Give attribute stones
					switch (event.split("-")[1])
					{
						case "fire":
						{
							giveItems(player, FIRE_STONE);
							break;
						}
						case "water":
						{
							giveItems(player, WATER_STONE);
							break;
						}
						case "earth":
						{
							giveItems(player, EARTH_STONE);
							break;
						}
						case "wind":
						{
							giveItems(player, WIND_STONE);
							break;
						}
						case "dark":
						{
							giveItems(player, DARK_STONE);
							break;
						}
						case "holy":
						{
							giveItems(player, HOLY_STONE);
							break;
						}
					}
					// Give soul crystal
					switch (qs.get("SoulCrystal"))
					{
						case "red":
						{
							giveItems(player, RED_SOUL_CRYSTAL_15);
							break;
						}
						case "blue":
						{
							giveItems(player, BLUE_SOUL_CRYSTAL_15);
							break;
						}
						case "green":
						{
							giveItems(player, GREEN_SOUL_CRYSTAL_15);
							break;
						}
					}
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 175739575, 42177);
						giveItems(player, CRYSTAL_R);
						giveItems(player, RECIPE_TWILIGHT_NECKLACE);
						qs.exitQuest(QuestType.ONE_TIME, true);
						htmltext = "33491-06.html";
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
				break;
			}
			case "teleport":
			{
				player.teleToLocation(TELEPORT_LOC);
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
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == NAVARI)
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
					if (qs.isCond(1))
					{
						htmltext = "33931-06.html";
					}
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if ((npc.getId() == ZEPHYRA) && qs.isStarted())
		{
			switch (qs.getCond())
			{
				case 1:
				{
					htmltext = "33978-01.html";
					break;
				}
				case 2:
				{
					htmltext = "33978-04.html";
					break;
				}
				case 17:
				{
					htmltext = "33978-05.html";
					break;
				}
				case 18:
				{
					htmltext = "33978-08.html";
					break;
				}
			}
		}
		else if ((npc.getId() == MOMET) && qs.isStarted())
		{
			if (qs.isCond(2))
			{
				htmltext = "33998-01.html";
			}
			else if (qs.isCond(3))
			{
				htmltext = "33998-05.html";
			}
		}
		else if ((npc.getId() == BLACK_MARKETEER_MAMMON) && qs.isStarted())
		{
			switch (qs.getCond())
			{
				case 3:
				{
					htmltext = "31092-01.html";
					break;
				}
				case 4:
				{
					htmltext = "31092-04.html";
					break;
				}
				case 5:
				{
					htmltext = (qs.isMemoState(1)) ? "31092-06.html" : "31092-05.html";
					break;
				}
				case 6:
				{
					htmltext = "31092-08.html";
					break;
				}
			}
		}
		else if ((npc.getId() == BLACKSMITH_OF_MAMMON) && qs.isStarted())
		{
			if (qs.isCond(6))
			{
				htmltext = "31126-01.html";
			}
			else if (qs.isCond(7))
			{
				htmltext = "31126-09.html";
			}
		}
		else if ((npc.getId() == HARDIN) && qs.isStarted())
		{
			switch (qs.getCond())
			{
				case 7:
				{
					htmltext = "33870-01.html";
					break;
				}
				case 8:
				{
					htmltext = "33870-06.html";
					break;
				}
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				{
					htmltext = "33870-11.html";
					break;
				}
				case 16:
				{
					htmltext = "33870-07.html";
					break;
				}
				case 17:
				{
					htmltext = "33870-10.html";
					break;
				}
			}
		}
		else if ((npc.getId() == KARLA) && qs.isStarted())
		{
			if (qs.isCond(18))
			{
				htmltext = getHtm(player, "33933-01.html");
				htmltext = htmltext.replace("%playerName%", player.getName());
			}
			else if (qs.isCond(19))
			{
				htmltext = "33933-04.html";
			}
		}
		else if ((npc.getId() == RAINA))
		{
			if (qs.isStarted() && qs.isCond(19))
			{
				htmltext = getHtm(player, "33491-01.html");
				htmltext = htmltext.replace("%playerName%", player.getName());
			}
			else if (qs.isCompleted())
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(4) && giveItemRandomly(killer, npc, DARK_FRAGMENT, 1, DARK_FRAGMENT_COUNT, DROP_CHANCE, true))
		{
			qs.setCond(5);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final PlayerInstance player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		if ((qs == null) && (event.getOldLevel() < event.getNewLevel()) && canStartQuest(player) && (player.getLevel() >= MIN_LEVEL))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
			showOnScreenMsg(player, NpcStringId.QUEEN_NAVARI_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 5000);
			playSound(player, QuestSound.ITEMSOUND_QUEST_TUTORIAL);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		if ((qs == null) && canStartQuest(player) && (player.getLevel() >= MIN_LEVEL))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
			showOnScreenMsg(player, NpcStringId.QUEEN_NAVARI_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ, ExShowScreenMessage.TOP_CENTER, 5000);
			playSound(player, QuestSound.ITEMSOUND_QUEST_TUTORIAL);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		final PlayerInstance player = event.getPlayer();
		if ((event.getMarkId() == getId()) && canStartQuest(player) && (player.getLevel() >= MIN_LEVEL))
		{
			final String html = getHtm(player, "popup.html");
			player.sendPacket(new TutorialShowHtml(html));
		}
	}
}