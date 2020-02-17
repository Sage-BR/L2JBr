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
package quests.Q10542_SearchingForNewPower;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.ExTutorialShowId;

import quests.Q10541_TrainLikeTheRealThing.Q10541_TrainLikeTheRealThing;

/**
 * Searching for New Power (10542)
 * @URL https://l2wiki.com/Searching_for_New_Power
 * @author Gigi
 */
public class Q10542_SearchingForNewPower extends Quest
{
	// NPCs
	private static final int SHANNON = 32974;
	private static final int TOYRON = 33004;
	private static final int THIEF = 23121;
	// Items
	private static final int THE_WAR_OF_GODS_AND_GIANTS = 17575;
	private static final int SOULSHOTS = 5789;
	private static final int SPIRITSHOTS = 5790;
	// Misc
	public static final int KILL_COUNT_VAR = 0;
	private static final int MAX_LEVEL = 20;
	
	public Q10542_SearchingForNewPower()
	{
		super(10542);
		addStartNpc(SHANNON);
		addTalkId(SHANNON, TOYRON);
		registerQuestItems(THE_WAR_OF_GODS_AND_GIANTS);
		addCondNotRace(Race.ERTHEIA, "noRace.html");
		addCondMaxLevel(MAX_LEVEL, "noLevel.html");
		addCondCompletedQuest(Q10541_TrainLikeTheRealThing.class.getSimpleName(), "noLevel.html");
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
			case "32974-02.htm":
			{
				htmltext = event;
				break;
			}
			case "check":
			{
				qs.startQuest();
				if (player.isInCategory(CategoryType.MAGE_GROUP))
				{
					giveItems(player, SPIRITSHOTS, 100);
					showOnScreenMsg(player, NpcStringId.AUTOMATE_SPIRITSHOT_AS_SHOWN_IN_THE_TUTORIAL, ExShowScreenMessage.TOP_CENTER, 4500);
					htmltext = "32974-04.htm";
				}
				else
				{
					giveItems(player, SOULSHOTS, 100);
					showOnScreenMsg(player, NpcStringId.AUTOMATE_SOULSHOT_AS_SHOWN_IN_THE_TUTORIAL, ExShowScreenMessage.TOP_CENTER, 4500);
					htmltext = "32974-03.htm";
				}
				break;
			}
			case "33004-02.html":
			{
				if (qs.isCond(3))
				{
					showOnScreenMsg(player, NpcStringId.AMONG_THE_4_BOOKSHELVES_FIND_THE_ONE_CONTAINING_A_VOLUME_CALLED_THE_WAR_OF_GODS_AND_GIANTS, ExShowScreenMessage.TOP_CENTER, 4500);
					htmltext = "32974-08.html";
				}
				break;
			}
			case "32974-08.html":
			{
				if (qs.isCond(5))
				{
					giveItems(player, (player.isInCategory(CategoryType.MAGE_GROUP) ? SPIRITSHOTS : SOULSHOTS), 100);
					addExpAndSp(player, 3200, 8);
					qs.exitQuest(false, true);
					htmltext = "32974-08.html";
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
				if (npc.getId() == SHANNON)
				{
					htmltext = "32974-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == SHANNON)
				{
					switch (qs.getCond())
					{
						case 1:
						case 2:
						case 3:
						{
							htmltext = "32974-05.html";
							break;
						}
						case 4:
						{
							htmltext = "32974-06.html";
							break;
						}
						case 5:
						{
							htmltext = "32974-07.html";
							break;
						}
					}
					break;
				}
				else if (npc.getId() == TOYRON)
				{
					if (qs.isCond(2))
					{
						htmltext = "33004-01.html";
						qs.setCond(3, true);
						player.sendPacket(new ExTutorialShowId(15));
						htmltext = "33004-01.html";
						
						NpcStringId npcStringId = null;
						switch (player.getClassId())
						{
							case FIGHTER:
							case ELVEN_FIGHTER:
							case DARK_FIGHTER:
							{
								npcStringId = NpcStringId.PREPARE_TO_USE_THE_SKILL_POWER_STRIKE_OR_MORTAL_BLOW;
								break;
							}
							case MAGE:
							case ELVEN_MAGE:
							case DARK_MAGE:
							{
								npcStringId = NpcStringId.PREPARE_TO_USE_THE_SKILL_WIND_STRIKE;
								break;
							}
							case ORC_FIGHTER:
							{
								npcStringId = NpcStringId.PREPARE_TO_USE_THE_SKILL_POWER_STRIKE_OR_IRON_PUNCH;
								break;
							}
							case ORC_MAGE:
							{
								npcStringId = NpcStringId.PREPARE_TO_USE_THE_SKILL_CHILL_FLAME;
								break;
							}
							case DWARVEN_FIGHTER:
							{
								npcStringId = NpcStringId.PREPARE_TO_USE_THE_SKILL_SPOIL;
								break;
							}
							case MALE_SOLDIER:
							case FEMALE_SOLDIER:
							{
								npcStringId = NpcStringId.PREPARE_TO_USE_THE_SKILL_FALLEN_ATTACK_OR_FALLEN_ARROW;
								break;
							}
							default:
							{
								break;
							}
						}
						if (npcStringId != null)
						{
							showOnScreenMsg(player, npcStringId, ExShowScreenMessage.TOP_CENTER, 4500);
						}
					}
					else if (qs.isCond(5))
					{
						htmltext = "33004-03.html";
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
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final Set<NpcLogListHolder> holder = new HashSet<>();
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(4))
		{
			holder.add(new NpcLogListHolder(THIEF, false, qs.getMemoStateEx(KILL_COUNT_VAR)));
		}
		return holder;
	}
}