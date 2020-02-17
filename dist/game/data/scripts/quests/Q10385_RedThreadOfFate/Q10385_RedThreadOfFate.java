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
package quests.Q10385_RedThreadOfFate;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSocialAction;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.TalkingIslandPast.TalkingIslandPast;

/**
 * Red Thread of Fate (10385)
 * @author Gladicek
 */
public class Q10385_RedThreadOfFate extends Quest
{
	// NPCs
	private static final int RAINA = 33491;
	private static final int MORELYN = 30925;
	private static final int LANYA = 33783;
	private static final int LADY_OF_THE_LAKE = 31745;
	private static final int NERUPA = 30370;
	private static final int ENFEUX = 31519;
	private static final int INNOCENTIN = 31328;
	private static final int VULCAN = 31539;
	private static final int MIXING_URN = 31149;
	private static final int WESLEY = 30166;
	private static final int HEINE_WATER_SOURCE = 33784;
	private static final int ALTAR_OF_SHILEN = 33785;
	private static final int MOTHER_TREE = 33786;
	private static final int PAAGRIO_TEMPLE = 33787;
	private static final int DESERTED_DWARVEN_HOUSE = 33788;
	private static final int CAVE_OF_SOULS = 33789;
	private static final int MYSTERIOUS_DARK_KNIGHT = 33751;
	private static final int DARIN = 33748;
	private static final int ROXXY = 33749;
	private static final int BIOTIN = 30031;
	private static final int INVISIBLE_ANGHEL_WATERFALL_NPC = 19544;
	private static final int SHILEN_MESSENGER = 27492;
	// Items
	private static final int MYSTERIOUS_LETTER = 36072;
	private static final int HEINE_FROM_THE_GARDEN_OF_EVA = 36066;
	private static final int CLEAREST_WATER = 36067;
	private static final int BRIGHTEST_LIGHT = 36068;
	private static final int PUREST_SOUL = 36069;
	private static final int SCROLL_OF_ESCAPE_VALLEY_OF_SAINTS = 39514;
	private static final int SCROLL_OF_ESCAPE_FORGE_OF_THE_GODS = 39515;
	private static final int SCROLL_OF_ESCAPE_IVORY_TOWER = 39516;
	private static final int SCROLL_OF_ESCAPE_DWARVEN_VILLAGE = 40309;
	private static final int VULCUN_TRUE_GOLD = 36113;
	private static final int VULCUN_PURE_SILVER = 36114;
	private static final int VULCUN_BLOOD_FIRE = 36115;
	private static final int FIERCEST_FLAME = 36070;
	private static final int FONDEST_HEART = 36071;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	// Location
	private static final Location LANYA_TELEPORT = new Location(80732, 254670, -10360);
	private static final Location LADY_OF_THE_LAKE_TELEPORT = new Location(143218, 43916, -3024);
	private static final Location ANGHEL_WATERFALL = new Location(172458, 90314, -1984);
	private static final Location VULCUN_TELEPORT = new Location(180162, -111760, -5824);
	// Skills
	private static final SkillHolder FONDEST_HEART_SKILL = new SkillHolder(9583, 1);
	private static final SkillHolder FIERCEST_FLAME_SKILL = new SkillHolder(9582, 1);
	private static final SkillHolder PUREST_SOUL_SKILL = new SkillHolder(9581, 1);
	private static final SkillHolder BRIGHTEST_LIGHT_SKILL = new SkillHolder(9580, 1);
	private static final SkillHolder CLEAREST_WATER_SKILL = new SkillHolder(9579, 1);
	private static final SkillHolder SUB_PRESENTATION = new SkillHolder(18177, 1);
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int SOCIAL_BOW = 7;
	private static final String ROXXY_VAR = "roxxy_npc_check";
	private static final String DARIN_VAR = "darin_npc_check";
	
	public Q10385_RedThreadOfFate()
	{
		super(10385);
		addStartNpc(RAINA);
		addFirstTalkId(ALTAR_OF_SHILEN, MOTHER_TREE, PAAGRIO_TEMPLE, DESERTED_DWARVEN_HOUSE, CAVE_OF_SOULS);
		addTalkId(RAINA, MORELYN, LANYA, LADY_OF_THE_LAKE, NERUPA, ENFEUX, INNOCENTIN, VULCAN, MIXING_URN, WESLEY, HEINE_WATER_SOURCE, MYSTERIOUS_DARK_KNIGHT, DARIN, ROXXY, BIOTIN);
		addSkillSeeId(ALTAR_OF_SHILEN, MOTHER_TREE, PAAGRIO_TEMPLE, DESERTED_DWARVEN_HOUSE, CAVE_OF_SOULS);
		addSeeCreatureId(INVISIBLE_ANGHEL_WATERFALL_NPC);
		addKillId(SHILEN_MESSENGER);
		addCondMinLevel(MIN_LEVEL, "33491-08.html");
		addCondNotRace(Race.ERTHEIA, "33491-09.html");
		registerQuestItems(MYSTERIOUS_LETTER, HEINE_FROM_THE_GARDEN_OF_EVA, CLEAREST_WATER, BRIGHTEST_LIGHT, PUREST_SOUL, FIERCEST_FLAME, FONDEST_HEART, VULCUN_TRUE_GOLD, VULCUN_PURE_SILVER, VULCUN_BLOOD_FIRE, SCROLL_OF_ESCAPE_VALLEY_OF_SAINTS, SCROLL_OF_ESCAPE_FORGE_OF_THE_GODS, SCROLL_OF_ESCAPE_IVORY_TOWER, SCROLL_OF_ESCAPE_DWARVEN_VILLAGE);
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
			case "33491-02.htm":
			case "31745-03.html":
			case "31745-04.html":
			case "31745-05.html":
			case "30370-02.html":
			case "30370-03.html":
			case "31539-02.html":
			case "31539-03.html":
			case "30166-02.html":
			case "31539-07.html":
			case "31539-08.html":
			case "33748-02.html":
			case "30031-02.html":
			case "33751-01.html":
			case "33751-02.html":
			{
				htmltext = event;
				break;
			}
			case "33491-03.html":
			{
				qs.startQuest();
				giveItems(player, MYSTERIOUS_LETTER, 1);
				showOnScreenMsg(player, NpcStringId.READ_THE_MYSTERIOUS_LETTER_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 5000);
				htmltext = event;
				break;
			}
			case "30925-02.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "33783-02.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(0);
					qs.setCond(3, true);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HEY_I_DIDN_T_GET_TO_PUT_IN_ONE_WORD_SHEESH);
					htmltext = event;
				}
				break;
			}
			case "31745-02.html":
			{
				if (qs.isCond(6) && (qs.isMemoState(0)))
				{
					qs.setMemoState(1);
					takeItems(player, HEINE_FROM_THE_GARDEN_OF_EVA, -1);
					htmltext = event;
				}
				else if (qs.isCond(6) && (qs.isMemoState(1)))
				{
					htmltext = event;
				}
				break;
			}
			case "31745-06.html":
			{
				if (qs.isCond(6))
				{
					qs.setCond(7, true);
					giveItems(player, CLEAREST_WATER, 1);
					player.teleToLocation(ANGHEL_WATERFALL);
					htmltext = event;
				}
				else if (qs.isCond(7))
				{
					player.teleToLocation(ANGHEL_WATERFALL);
				}
				break;
			}
			case "30370-04.html":
			{
				if (qs.isCond(7))
				{
					qs.setCond(8, true);
					giveItems(player, BRIGHTEST_LIGHT, 1);
					giveItems(player, SCROLL_OF_ESCAPE_VALLEY_OF_SAINTS, 1);
					showOnScreenMsg(player, NpcStringId.TRY_USING_THE_TELEPORT_SCROLL_NERUPA_GAVE_YOUT_O_GO_TO_THE_VALLEY_OF_SAINTS, ExShowScreenMessage.TOP_CENTER, 5000);
					htmltext = event;
				}
				break;
			}
			case "31519-02.html":
			{
				if (qs.isCond(8))
				{
					qs.setCond(9, true);
					giveItems(player, PUREST_SOUL, 1);
					htmltext = event;
				}
				break;
			}
			case "31328-02.html":
			{
				if (qs.isCond(9))
				{
					qs.setCond(10, true);
					giveItems(player, SCROLL_OF_ESCAPE_FORGE_OF_THE_GODS, 1);
					showOnScreenMsg(player, NpcStringId.TRY_USING_THE_TELEPORT_SCROLL_INNOCENTIN_GAVE_YOU_TO_GO_TO_THE_FORGE_OF_THE_GODS, ExShowScreenMessage.TOP_CENTER, 5000);
					htmltext = event;
				}
				break;
			}
			case "31539-04.html":
			{
				if (qs.isCond(10))
				{
					qs.setCond(11, true);
					giveItems(player, VULCUN_TRUE_GOLD, 1);
					giveItems(player, VULCUN_PURE_SILVER, 1);
					giveItems(player, VULCUN_BLOOD_FIRE, 1);
					giveItems(player, SCROLL_OF_ESCAPE_IVORY_TOWER, 1);
					showOnScreenMsg(player, NpcStringId.TRY_USING_THE_TELEPORT_SCROLL_VULCAN_GAVE_YOU_TO_GO_TO_IVORY_TOWER, ExShowScreenMessage.TOP_CENTER, 5000);
					htmltext = event;
				}
				break;
			}
			case "31149-02.html":
			{
				if (qs.isCond(11))
				{
					qs.setCond(12, true);
					takeItems(player, VULCUN_TRUE_GOLD, 1);
					takeItems(player, VULCUN_PURE_SILVER, 1);
					takeItems(player, VULCUN_BLOOD_FIRE, 1);
					htmltext = event;
				}
				break;
			}
			case "30166-03.html":
			{
				if (qs.isCond(12))
				{
					qs.setCond(13, true);
					player.teleToLocation(VULCUN_TELEPORT);
					htmltext = event;
				}
				else if (qs.isCond(13))
				{
					player.teleToLocation(VULCUN_TELEPORT);
				}
				break;
			}
			case "31539-09.html":
			{
				if (qs.isCond(13))
				{
					qs.setCond(14, true);
					giveItems(player, FIERCEST_FLAME, 1);
					giveItems(player, FONDEST_HEART, 1);
					giveItems(player, SCROLL_OF_ESCAPE_DWARVEN_VILLAGE, 1);
					htmltext = event;
				}
				else if (qs.isCond(13))
				{
					player.teleToLocation(VULCUN_TELEPORT);
				}
				break;
			}
			case "33748-03.html":
			{
				if (qs.isCond(19))
				{
					if (qs.get(ROXXY_VAR) != null)
					{
						qs.setCond(20, true);
						htmltext = event;
					}
					else
					{
						showOnScreenMsg(player, NpcStringId.SPEAK_WITH_ROXXY, ExShowScreenMessage.TOP_CENTER, 5000);
						qs.set(DARIN_VAR, 1);
						htmltext = event;
					}
				}
				break;
			}
			case "33749-02.html":
			{
				if (qs.isCond(19))
				{
					if (qs.get(DARIN_VAR) != null)
					{
						qs.setCond(20, true);
						htmltext = event;
					}
					else
					{
						showOnScreenMsg(player, NpcStringId.SPEAK_WITH_DARIN, ExShowScreenMessage.TOP_CENTER, 5000);
						qs.set(ROXXY_VAR, 1);
						htmltext = "33749-03.html";
					}
				}
				break;
			}
			case "30031-03.html":
			{
				if (qs.isCond(20))
				{
					qs.setCond(21, true);
					showOnScreenMsg(player, NpcStringId.GO_OUTSIDE_THE_TEMPLE, ExShowScreenMessage.TOP_CENTER, 5000);
					htmltext = event;
				}
				break;
			}
			case "33491-06.html":
			{
				if (qs.isCond(22))
				{
					showOnScreenMsg(player, NpcStringId.CONGRATULATIONS_YOU_CAN_NOW_ACCESS_A_SUBCLASS, ExShowScreenMessage.TOP_CENTER, 5000);
					player.doCast(SUB_PRESENTATION.getSkill());
					giveItems(player, DIMENSIONAL_DIAMOND, 40);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
			case "teleportLanya":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5, true);
					player.teleToLocation(LANYA_TELEPORT);
				}
				else if (qs.isCond(5))
				{
					player.teleToLocation(LANYA_TELEPORT);
				}
				break;
			}
			case "teleportLady":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6, true);
					giveItems(player, HEINE_FROM_THE_GARDEN_OF_EVA, 1);
					player.teleToLocation(LADY_OF_THE_LAKE_TELEPORT);
				}
				else if (qs.isCond(6))
				{
					player.teleToLocation(LADY_OF_THE_LAKE_TELEPORT);
				}
				break;
			}
			case "movieQuest":
			{
				if (qs.isCond(21))
				{
					playMovie(player, Movie.SC_SUB_QUEST);
					startQuestTimer("movie_end", 35000, npc, player);
					qs.setCond(22, true);
				}
				break;
			}
			case "movie_end":
			{
				if (qs.isCond(22))
				{
					final Instance instance = npc.getInstanceWorld();
					instance.finishInstance(0);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_SOCIAL_ACTION)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerSocialAction(OnPlayerSocialAction event)
	{
		final PlayerInstance player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		final WorldObject target = player.getTarget();
		
		if ((target != null) && target.isNpc() && (target.getId() == LANYA))
		{
			final Npc npc = (Npc) player.getTarget();
			
			if (((qs != null) && (qs.isCond(3))) && (event.getSocialActionId() == SOCIAL_BOW) && (player.isInsideRadius3D(npc, 120)))
			{
				qs.setCond(4, true);
			}
		}
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == RAINA)
				{
					htmltext = "33491-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case RAINA:
					{
						if (qs.isCond(1))
						{
							htmltext = "33491-04.html";
						}
						else if (qs.isCond(22))
						{
							htmltext = "33491-05.html";
						}
						break;
					}
					case MORELYN:
					{
						if (qs.isCond(1))
						{
							htmltext = "30925-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "30925-03.html";
						}
						break;
					}
					case LANYA:
					{
						switch (qs.getCond())
						{
							case 2:
							{
								htmltext = "33783-01.html";
								break;
							}
							case 3:
							{
								htmltext = "33783-03.html";
								break;
							}
							case 4:
							{
								htmltext = "33783-04.html";
								break;
							}
							case 5:
							{
								htmltext = "33783-04.html";
								break;
							}
						}
						break;
					}
					case HEINE_WATER_SOURCE:
					{
						if (qs.isCond(5))
						{
							htmltext = "33784-01.html";
						}
						else if (qs.isCond(6))
						{
							htmltext = "33784-01.html";
						}
						break;
					}
					case LADY_OF_THE_LAKE:
					{
						if (qs.isCond(6))
						{
							htmltext = "31745-01.html";
						}
						else if (qs.isCond(7))
						{
							htmltext = "31745-05.html";
						}
						break;
					}
					case NERUPA:
					{
						if (qs.isCond(7))
						{
							htmltext = "30370-01.html";
						}
						else if (qs.isCond(8))
						{
							htmltext = "30370-05.html";
						}
						break;
					}
					case ENFEUX:
					{
						if (qs.isCond(8))
						{
							htmltext = "31519-01.html";
						}
						else if (qs.isCond(9))
						{
							htmltext = "31519-03.html";
						}
						break;
					}
					case INNOCENTIN:
					{
						if (qs.isCond(9))
						{
							htmltext = "31328-01.html";
						}
						else if (qs.isCond(10))
						{
							htmltext = "31328-03.html";
						}
						break;
					}
					case VULCAN:
					{
						switch (qs.getCond())
						{
							case 10:
							{
								htmltext = "31539-01.html";
								break;
							}
							case 11:
							{
								htmltext = "31539-05.html";
								break;
							}
							case 13:
							{
								htmltext = "31539-06.html";
								break;
							}
							case 14:
							{
								htmltext = "31539-10.html";
								break;
							}
						}
						break;
					}
					case MIXING_URN:
					{
						if (qs.isCond(11))
						{
							htmltext = "31149-01.html";
						}
						else if (qs.isCond(12))
						{
							htmltext = "31149-03.html";
						}
						break;
					}
					case WESLEY:
					{
						if (qs.isCond(12))
						{
							htmltext = "30166-01.html";
						}
						else if (qs.isCond(13))
						{
							htmltext = "30166-02.html";
						}
						break;
					}
					case DARIN:
					{
						if (qs.isCond(19))
						{
							if (qs.get(DARIN_VAR) == null)
							{
								htmltext = "33748-01.html";
							}
							else
							{
								htmltext = "33748-03.html";
							}
						}
						else if (qs.isCond(20))
						{
							htmltext = "33748-04.html";
						}
						break;
					}
					case ROXXY:
					{
						if (qs.isCond(19))
						{
							if (qs.get(ROXXY_VAR) == null)
							{
								htmltext = "33749-01.html";
							}
							else
							{
								htmltext = "33749-03.html";
							}
						}
						else if (qs.isCond(20))
						{
							htmltext = "33749-04.html";
						}
						break;
					}
					case BIOTIN:
					{
						if (qs.isCond(20))
						{
							htmltext = "30031-01.html";
						}
						else if (qs.isCond(21))
						{
							htmltext = "30031-04.html";
						}
						break;
					}
					case MYSTERIOUS_DARK_KNIGHT:
					{
						if (qs.isCond(21))
						{
							htmltext = "30031-01.html";
						}
						else if (qs.isCond(21))
						{
							htmltext = "30031-04.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == RAINA)
				{
					htmltext = "33491-07.html";
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
		
		if ((qs != null) && qs.isCond(16))
		{
			qs.setCond(17, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			final QuestState qs = getQuestState(player, false);
			
			if ((npc.getId() == INVISIBLE_ANGHEL_WATERFALL_NPC) && (qs != null) && qs.isCond(7))
			{
				showOnScreenMsg(player, NpcStringId.YOU_HAVE_REACHED_ANGHEL_WATERFALL_GO_INSIDE_THE_CAVE, ExShowScreenMessage.TOP_CENTER, 5000);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance player, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		
		if ((qs != null) && qs.isStarted() && CommonUtil.contains(targets, npc))
		{
			switch (npc.getId())
			{
				case DESERTED_DWARVEN_HOUSE:
				{
					if ((skill == FONDEST_HEART_SKILL.getSkill()) && qs.isCond(14))
					{
						qs.setCond(15, true);
					}
					break;
				}
				case PAAGRIO_TEMPLE:
				{
					if ((skill == FIERCEST_FLAME_SKILL.getSkill()) && qs.isCond(15))
					{
						qs.setCond(16, true);
					}
					break;
				}
				case ALTAR_OF_SHILEN:
				{
					if ((skill == BRIGHTEST_LIGHT_SKILL.getSkill()) && qs.isCond(16))
					{
						showOnScreenMsg(player, NpcStringId.YOU_MUST_DEFEAT_SHILEN_S_MESSENGER, ExShowScreenMessage.TOP_CENTER, 5000);
						final Npc monster = addSpawn(SHILEN_MESSENGER, 28767, 11030, -4232, 0, false, 0, false);
						monster.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.BRIGHTEST_LIGHT_HOW_DARE_YOU_DESECRATE_THE_ALTAR_OF_SHILEN);
						addAttackPlayerDesire(monster, player, 23);
					}
					break;
				}
				case CAVE_OF_SOULS:
				{
					if ((skill == PUREST_SOUL_SKILL.getSkill()) && qs.isCond(17))
					{
						qs.setCond(18, true);
					}
					break;
				}
				case MOTHER_TREE:
				{
					if (((skill == CLEAREST_WATER_SKILL.getSkill()) && qs.isCond(18)))
					{
						qs.setCond(19, true);
						final Quest instance = QuestManager.getInstance().getQuest(TalkingIslandPast.class.getSimpleName());
						if (instance != null)
						{
							instance.onAdvEvent("enterInstance", npc, player);
						}
					}
					break;
				}
			}
		}
		return super.onSkillSee(npc, player, skill, targets, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		
		if (qs != null)
		{
			switch (npc.getId())
			{
				case DESERTED_DWARVEN_HOUSE:
				{
					if (qs.isCond(14))
					{
						showOnScreenMsg(player, NpcStringId.USE_THE_FONDEST_HEART_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 5000);
						htmltext = "33788-01.html";
					}
					else
					{
						npc.showChatWindow(player);
					}
					break;
				}
				case PAAGRIO_TEMPLE:
				{
					if (qs.isCond(15))
					{
						showOnScreenMsg(player, NpcStringId.USE_THE_FIERCEST_FLAME_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 5000);
						htmltext = "33787-01.html";
					}
					else
					{
						npc.showChatWindow(player);
					}
					break;
				}
				case ALTAR_OF_SHILEN:
				{
					if (qs.isCond(16))
					{
						showOnScreenMsg(player, NpcStringId.USE_THE_BRIGHTEST_LIGHT_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 5000);
						htmltext = "33785-01.html";
					}
					else
					{
						npc.showChatWindow(player);
					}
					break;
				}
				case CAVE_OF_SOULS:
				{
					if (qs.isCond(17))
					{
						showOnScreenMsg(player, NpcStringId.USE_THE_PUREST_SOUL_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 5000);
						htmltext = "33789-01.html";
					}
					else
					{
						npc.showChatWindow(player);
					}
					break;
				}
				case MOTHER_TREE:
				{
					switch (qs.getCond())
					{
						case 18:
						{
							showOnScreenMsg(player, NpcStringId.USE_THE_CLEAREST_WATER_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 5000);
							htmltext = "33786-01.html";
							break;
						}
						case 19:
						case 20:
						case 21:
						{
							final Quest instance = QuestManager.getInstance().getQuest(TalkingIslandPast.class.getSimpleName());
							if (instance != null)
							{
								instance.onAdvEvent("enterInstance", npc, player);
							}
							break;
						}
						default:
						{
							npc.showChatWindow(player);
						}
					}
					break;
				}
			}
		}
		else
		{
			npc.showChatWindow(player);
		}
		return htmltext;
	}
}