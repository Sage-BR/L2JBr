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
package quests.Q10753_WindsOfFateChoices;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.HtmlActionScope;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.geoengine.GeoEngine;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.Id;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerBypass;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerItemAdd;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLevelChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPressTutorialMark;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;
import org.l2jbr.gameserver.network.serverpackets.SocialAction;
import org.l2jbr.gameserver.network.serverpackets.TutorialCloseHtml;
import org.l2jbr.gameserver.network.serverpackets.TutorialShowHtml;
import org.l2jbr.gameserver.network.serverpackets.TutorialShowQuestionMark;
import org.l2jbr.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * Winds of Fate: Choices (10753)
 * @URL https://l2wiki.com/Winds_of_Fate:_Choices
 * @VIDEO https://www.youtube.com/watch?v=ysCNSVHRcoo
 * @author Gigi
 */
public class Q10753_WindsOfFateChoices extends Quest
{
	// NPCs
	private static final int KATALIN = 33943;
	private static final int AYANTHE = 33942;
	private static final int ARKENIAS = 30174;
	private static final int ALCHEMISTS_MIXING_URN = 31149;
	private static final int HARDIN = 30832;
	private static final int ICARUS = 30835;
	private static final int ATHREA = 30758;
	private static final int ATHREAS_BOX = 33997;
	private static final int GERETH = 33932;
	private static final int QUEEN_NAVARI = 33931;
	// Monsters
	private static final int QUEST_MONSTER_NEBULITE_EYE = 27544;
	private static final int QUEST_MONSTER_NEBULITE_WATCH = 27545;
	private static final int QUEST_MONSTER_NEBULITE_GOLEM = 27546;
	// Item
	private static final int PROPHECY_MACHINE = 39540;
	private static final int CRYSTAL_EYE = 39545;
	private static final int BROKEN_STONE_OF_PURITY = 39546;
	private static final int MIRACLE_DRUG_OF_ENCHANTMENT = 39547;
	private static final int CRUDE_PHILOPERS_STONE = 39544;
	private static final int EMPTY_REGEANT_FLASK = 39548;
	private static final int RESTORATION_REGEANT = 39549;
	private static final int WHITE_ROSE = 39551;
	private static final int ATHREAS_BELONGINGS = 39550;
	private static final int CRIMSON_ROSE = 39552;
	private static final int ATELIA = 39542;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final String CRYSTAL_EYE_VAR = "EyeCount";
	private static final String BROKEN_STONE_OF_PURITY_VAR = "PurityCount";
	private static final String EMPTY_REGEANT_FLASK_VAR = "FlaskCount";
	// Reward
	private static final int SAYHAS_BOX_W = 40268;
	private static final int SAYHAS_BOX_M = 40269;
	private static final int CHAOS_POMANDER = 37374;
	// Location
	private static final Location TELEPORT_LOC = new Location(-81948, 249635, -3371);
	private static final Location[] BOX_SPAWNS =
	{
		new Location(102069, 103220, -3506, 0),
		new Location(102009, 103220, -3506, 0),
		new Location(101949, 103220, -3506, 0),
		new Location(101889, 103220, -3506, 0),
		new Location(102069, 103280, -3506, 0),
		new Location(102009, 103280, -3506, 0),
		new Location(101949, 103280, -3506, 0),
		new Location(101889, 103280, -3506, 0),
		new Location(102069, 103340, -3506, 0),
		new Location(102009, 103340, -3506, 0),
		new Location(101949, 103340, -3506, 0),
		new Location(101889, 103340, -3506, 0),
		new Location(102069, 103400, -3506, 0),
		new Location(102009, 103400, -3506, 0),
		new Location(101949, 103400, -3506, 0),
		new Location(101889, 103400, -3506, 0),
	};
	
	public Q10753_WindsOfFateChoices()
	{
		super(10753);
		addStartNpc(KATALIN, AYANTHE);
		addTalkId(KATALIN, AYANTHE, ARKENIAS, ALCHEMISTS_MIXING_URN, HARDIN, ICARUS, ATHREA, ATHREAS_BOX, GERETH, QUEEN_NAVARI);
		addFirstTalkId(ATHREAS_BOX);
		addKillId(QUEST_MONSTER_NEBULITE_EYE, QUEST_MONSTER_NEBULITE_WATCH, QUEST_MONSTER_NEBULITE_GOLEM);
		registerQuestItems(PROPHECY_MACHINE, CRYSTAL_EYE, BROKEN_STONE_OF_PURITY, MIRACLE_DRUG_OF_ENCHANTMENT, CRUDE_PHILOPERS_STONE, EMPTY_REGEANT_FLASK, RESTORATION_REGEANT, WHITE_ROSE, ATHREAS_BELONGINGS, CRIMSON_ROSE, ATELIA);
		addCondRace(Race.ERTHEIA, "");
		addCondInCategory(CategoryType.FOURTH_CLASS_GROUP, "");
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
			case "33943-02.htm":
			case "33943-03.htm":
			case "33942-02.htm":
			case "33942-03.htm":
			case "30174-02.html":
			case "31149-02.html":
			case "30174-08.html":
			case "30174-09.html":
			case "30832-02.html":
			case "30835-02.html":
			case "30835-03.html":
			case "30835-04.html":
			case "30758-03.html":
			case "30758-08.html":
			case "33943-07.html":
			case "33943-08.html":
			case "33943-09.html":
			case "33942-07.html":
			case "33942-08.html":
			case "33942-09.html":
			case "33932-02.html":
			case "33932-03.html":
			case "33932-04.html":
			case "33932-05.html":
			case "33932-06.html":
			case "33931-02.html":
			{
				htmltext = event;
				break;
			}
			case "33943-04.html":
			case "33942-04.html":
			{
				giveItems(player, PROPHECY_MACHINE, 1);
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30174-03.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "31149-03.html":
			{
				if (qs.isCond(4))
				{
					giveItems(player, RESTORATION_REGEANT, 1);
					takeItems(player, CRUDE_PHILOPERS_STONE, -1);
					takeItems(player, EMPTY_REGEANT_FLASK, -1);
					takeItems(player, CRYSTAL_EYE, -1);
					takeItems(player, BROKEN_STONE_OF_PURITY, -1);
					takeItems(player, MIRACLE_DRUG_OF_ENCHANTMENT, -1);
					qs.unset("EyeCount");
					qs.unset("FlaskCount");
					qs.unset("PurityCount");
					qs.setCond(5, true);
				}
				htmltext = event;
				break;
			}
			case "30174-10.html":
			{
				if (qs.isCond(5))
				{
					takeItems(player, RESTORATION_REGEANT, -1);
					qs.setCond(6, true);
				}
				htmltext = event;
				break;
			}
			case "30832-03.html":
			{
				if (qs.isCond(6))
				{
					qs.setCond(7, true);
				}
				htmltext = event;
				break;
			}
			case "30835-05.html":
			{
				if (qs.isCond(7))
				{
					giveItems(player, WHITE_ROSE, 1);
					qs.setCond(8, true);
				}
				htmltext = event;
				break;
			}
			case "30758-02.html":
			{
				if (npc.isScriptValue(0) && qs.isCond(8))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "busy.html";
				}
				break;
			}
			case "30758-04.html":
			{
				if (qs.isCond(8))
				{
					npc.setScriptValue(1);
					player.sendPacket(new ExSendUIEvent(player, false, false, 180, 1, NpcStringId.REMAINING_TIME));
					for (Location loc : BOX_SPAWNS)
					{
						addSpawn(ATHREAS_BOX, loc, false, 180000);
					}
					startQuestTimer("despawn", 180000, npc, player);
					qs.setCond(9);
				}
				htmltext = event;
				break;
			}
			case "despawn":
			{
				npc.setScriptValue(0);
				break;
			}
			case "open":
			{
				if ((npc.getId() == ATHREAS_BOX) && qs.isCond(9))
				{
					if (getRandom(100) < 35)
					{
						giveItems(player, ATHREAS_BELONGINGS, 1);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						htmltext = "33997-2.html";
					}
					else
					{
						htmltext = "33997-3.html";
					}
				}
				npc.setScriptValue(1);
				break;
			}
			case "30758-09.html":
			{
				if (qs.isCond(10))
				{
					takeItems(player, WHITE_ROSE, -1);
					takeItems(player, ATHREAS_BELONGINGS, -1);
					giveItems(player, CRIMSON_ROSE, 1);
					npc.setScriptValue(0);
					qs.setCond(11, true);
				}
				htmltext = event;
				break;
			}
			case "30835-08.html":
			{
				if (qs.isCond(11))
				{
					if (player.isMageClass())
					{
						qs.setCond(12, true);
					}
					else
					{
						qs.setCond(13, true);
					}
					takeItems(player, CRIMSON_ROSE, -1);
				}
				htmltext = event;
				break;
			}
			case "33943-10.html":
			{
				if (qs.isCond(13))
				{
					qs.setCond(14, true);
				}
				htmltext = event;
				break;
			}
			case "33942-10.html":
			{
				if (qs.isCond(12))
				{
					qs.setCond(14, true);
				}
				htmltext = event;
				break;
			}
			case "33932-09.html":
			{
				if (qs.isCond(17))
				{
					qs.setCond(18, true);
				}
				htmltext = event;
				break;
			}
			case "class":
			{
				htmltext = !player.isMageClass() ? "33931-03.html" : "33931-03a.html";
				break;
			}
			case "33931-04.html":
			{
				final ClassId newClass = ClassId.EVISCERATOR;
				if (qs.isCond(18) && newClass.childOf(player.getClassId()))
				{
					if (!player.isSubClassActive())
					{
						player.setBaseClass(newClass);
					}
					player.setClassId(newClass.getId());
					player.broadcastUserInfo();
					player.sendSkillList();
					player.sendPacket(new SocialAction(player.getObjectId(), 24));
					showOnScreenMsg(player, NpcStringId.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_YOU_CAN_NOW_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES, ExShowScreenMessage.TOP_CENTER, 10000);
					giveStoryQuestReward(npc, player);
					giveItems(player, SAYHAS_BOX_W, 1);
					giveItems(player, CHAOS_POMANDER, 2);
					addExpAndSp(player, 0, 500000);
					giveAdena(player, 2030400, false);
					qs.exitQuest(false, true);
				}
				htmltext = event;
				break;
			}
			case "33931-04a.html":
			{
				final ClassId newClass = ClassId.SAYHA_SEER;
				if (qs.isCond(18) && newClass.childOf(player.getClassId()))
				{
					if (!player.isSubClassActive())
					{
						player.setBaseClass(newClass);
					}
					player.setClassId(newClass.getId());
					player.broadcastUserInfo();
					player.sendSkillList();
					player.sendPacket(new SocialAction(player.getObjectId(), 24));
					showOnScreenMsg(player, NpcStringId.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_YOU_CAN_NOW_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES, ExShowScreenMessage.TOP_CENTER, 10000);
					giveStoryQuestReward(npc, player);
					giveItems(player, SAYHAS_BOX_M, 1);
					giveItems(player, CHAOS_POMANDER, 2);
					addExpAndSp(player, 0, 500000);
					giveAdena(player, 2030400, false);
					qs.exitQuest(false, true);
				}
				htmltext = event;
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
				if (npc.getId() == KATALIN)
				{
					if ((player.getLevel() >= MIN_LEVEL) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
					{
						htmltext = !player.isMageClass() ? "33943-01.html" : "33943-00.html";
						break;
					}
					htmltext = "33943-00.html";
					break;
				}
				else if (npc.getId() == AYANTHE)
				{
					if ((player.getLevel() >= MIN_LEVEL) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP))
					{
						htmltext = player.isMageClass() ? "33942-01.html" : "33942-00.html";
						break;
					}
					htmltext = "33942-00.html";
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
							htmltext = "33943-05.html";
						}
						else if (qs.isCond(13))
						{
							htmltext = "33943-06.html";
						}
						else if (qs.isCond(14))
						{
							htmltext = "33943-11.html";
						}
						break;
					}
					case AYANTHE:
					{
						if (qs.isCond(1))
						{
							htmltext = "33942-05.html";
						}
						else if (qs.isCond(12))
						{
							htmltext = "33942-06.html";
						}
						else if (qs.isCond(14))
						{
							htmltext = "33942-11.html";
						}
						break;
					}
					case ARKENIAS:
					{
						if (qs.isCond(1))
						{
							htmltext = "30174-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "30174-04.html";
						}
						else if (qs.isCond(3))
						{
							giveItems(player, CRUDE_PHILOPERS_STONE, 1);
							giveItems(player, EMPTY_REGEANT_FLASK, 1);
							qs.setCond(4, true);
							htmltext = "30174-05.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "30174-06.html";
						}
						else if (qs.isCond(5) && hasQuestItems(player, RESTORATION_REGEANT))
						{
							htmltext = "30174-07.html";
						}
						else if (qs.isCond(6))
						{
							htmltext = "30174-11.html";
						}
						break;
					}
					case ALCHEMISTS_MIXING_URN:
					{
						if (qs.isCond(4) && hasQuestItems(player, EMPTY_REGEANT_FLASK))
						{
							htmltext = "31149-01.html";
						}
						else if (qs.isCond(5))
						{
							htmltext = "31149-04.html";
						}
						break;
					}
					case HARDIN:
					{
						if (qs.isCond(6))
						{
							htmltext = "30832-01.html";
						}
						else if (qs.isCond(7))
						{
							htmltext = "30832-04.html";
						}
						break;
					}
					case ICARUS:
					{
						if (qs.isCond(7))
						{
							htmltext = "30835-01.html";
						}
						else if (qs.isCond(8))
						{
							htmltext = "30835-06.html";
						}
						else if (qs.isCond(11) && hasQuestItems(player, CRIMSON_ROSE))
						{
							htmltext = "30835-07.html";
						}
						else if (qs.isCond(12) || qs.isCond(13))
						{
							htmltext = "30835-09.html";
						}
						break;
					}
					case ATHREA:
					{
						if (qs.isCond(8) && hasQuestItems(player, WHITE_ROSE))
						{
							htmltext = "30758-01.html";
						}
						else if (qs.isCond(9) && !hasQuestItems(player, ATHREAS_BELONGINGS))
						{
							htmltext = "30758-05.html";
						}
						else if (qs.isCond(9) && ((getQuestItemsCount(player, ATHREAS_BELONGINGS) > 0) && (getQuestItemsCount(player, ATHREAS_BELONGINGS) < 4)))
						{
							htmltext = "30758-06.html";
						}
						else if (qs.isCond(10))
						{
							player.sendPacket(new ExSendUIEvent(player, true, false, 1, 0, NpcStringId.REMAINING_TIME));
							World.getInstance().forEachVisibleObjectInRange(npc, Npc.class, 1000, box ->
							{
								if ((box.getId() == ATHREAS_BOX) && GeoEngine.getInstance().canSeeTarget(npc, box))
								{
									box.deleteMe();
								}
							});
							npc.setScriptValue(0);
							htmltext = "30758-07.html";
						}
						else if (qs.isCond(11))
						{
							htmltext = "30758-10.html";
						}
						break;
					}
					case GERETH:
					{
						if (qs.isCond(14) && hasQuestItems(player, PROPHECY_MACHINE))
						{
							htmltext = "33932-01.html";
						}
						else if (qs.isCond(15) || qs.isCond(16))
						{
							htmltext = "33932-07.html";
						}
						else if (qs.isCond(17))
						{
							htmltext = "33932-08.html";
						}
						else if (qs.isCond(18))
						{
							htmltext = "33932-10.html";
						}
						break;
					}
					case QUEEN_NAVARI:
					{
						if (qs.isCond(18) && hasQuestItems(player, ATELIA))
						{
							htmltext = "33931-01.html";
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
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(2))
		{
			int EyeCount = qs.getInt(CRYSTAL_EYE_VAR);
			int PurityCount = qs.getInt(BROKEN_STONE_OF_PURITY_VAR);
			int FlaskCount = qs.getInt(EMPTY_REGEANT_FLASK_VAR);
			
			switch (npc.getId())
			{
				case QUEST_MONSTER_NEBULITE_EYE:
				{
					if ((EyeCount < 3) && (getRandom(100) > 50))
					{
						giveItems(killer, CRYSTAL_EYE, 1);
						qs.set(CRYSTAL_EYE_VAR, ++EyeCount);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case QUEST_MONSTER_NEBULITE_WATCH:
				{
					if ((PurityCount < 3) && (getRandom(100) > 50))
					{
						giveItems(killer, BROKEN_STONE_OF_PURITY, 1);
						qs.set(BROKEN_STONE_OF_PURITY_VAR, ++PurityCount);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case QUEST_MONSTER_NEBULITE_GOLEM:
				{
					if ((FlaskCount < 3) && (getRandom(100) > 50))
					{
						giveItems(killer, MIRACLE_DRUG_OF_ENCHANTMENT, 1);
						qs.set(EMPTY_REGEANT_FLASK_VAR, ++FlaskCount);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			if ((EyeCount >= 3) && (PurityCount >= 3) && (FlaskCount >= 3))
			{
				qs.setCond(0);
				qs.setCond(3, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(2))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>(3);
			final int EyeCount = qs.getInt(CRYSTAL_EYE_VAR);
			if (EyeCount > 0)
			{
				holder.add(new NpcLogListHolder(NpcStringId.NEBULITE_EYE_CRYSTAL_EYE, EyeCount));
			}
			final int PurityCount = qs.getInt(BROKEN_STONE_OF_PURITY_VAR);
			if (PurityCount > 0)
			{
				holder.add(new NpcLogListHolder(NpcStringId.NEBULITE_WATCH_BROKEN_STONE_OF_PURITY, PurityCount));
			}
			final int FlaskCount = qs.getInt(EMPTY_REGEANT_FLASK_VAR);
			if (FlaskCount > 0)
			{
				holder.add(new NpcLogListHolder(NpcStringId.NEBULITE_GOLEM_MIRACLE_DRUG_OF_ENCHANTMENT, FlaskCount));
			}
			return holder;
		}
		return super.getNpcLogList(player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(9) && npc.isScriptValue(0))
		{
			return "33997.html";
		}
		return "33997-1.html";
	}
	
	@RegisterEvent(EventType.ON_PLAYER_ITEM_ADD)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(ATHREAS_BELONGINGS)
	public void onItemAdd(OnPlayerItemAdd event)
	{
		final PlayerInstance player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && (qs.isCond(9)) && (getQuestItemsCount(player, ATHREAS_BELONGINGS) >= 4))
		{
			qs.setCond(10, true);
		}
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
					player.sendPacket(new PlaySound(3, "Npcdialog1.aris_quest_2", 0, 0, 0, 0, 0));
					player.sendPacket(new TutorialShowHtml(getHtm(player, "popup1.html")));
				}
				else
				{
					player.sendPacket(new PlaySound(3, "Npcdialog1.katrina_quest_2", 0, 0, 0, 0, 0));
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
			if (command.equals("Q10753_teleport"))
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
			else if (command.equals("Q10753_close"))
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
		
		if ((qs == null) && (player.getRace() == Race.ERTHEIA) && (oldLevel < newLevel) && (newLevel >= MIN_LEVEL) && (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)))
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
			playSound(player, QuestSound.ITEMSOUND_QUEST_TUTORIAL);
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
		
		if ((qs == null) && (player.getRace() == Race.ERTHEIA) && (player.getLevel() >= MIN_LEVEL) && (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)))
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
			playSound(player, QuestSound.ITEMSOUND_QUEST_TUTORIAL);
		}
	}
}