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
package quests.Q00453_NotStrongEnoughAlone;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.Config;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.util.Util;

import quests.Q10282_ToTheSeedOfAnnihilation.Q10282_ToTheSeedOfAnnihilation;

/**
 * Not Strong Enough Alone (453)
 * @author malyelfik
 */
public class Q00453_NotStrongEnoughAlone extends Quest
{
	// NPCs
	private static final int KLEMIS = 32734;
	private static final int[] MONSTER1 =
	{
		22746, // Bgurent
		22747, // Brakian
		22748, // Groikan
		22749, // Treykan
		22750, // Elite Bgurent
		22751, // Elite Brakian
		22752, // Elite Groikan
		22753, // Elite Treykan
	};
	private static final int[] MONSTER2 =
	{
		22754, // Turtlelian
		22755, // Krajian
		22756, // Tardyon
		22757, // Elite Turtlelian
		22758, // Elite Krajian
		22759, // Elite Tardyon
	};
	private static final int[] MONSTER3 =
	{
		22760, // Kanibi
		22761, // Kiriona
		22762, // Kaiona
		22763, // Elite Kanibi
		22764, // Elite Kiriona
		22765, // Elite Kaiona
	};
	
	// Misc
	private static final int MIN_LV = 85;
	
	public Q00453_NotStrongEnoughAlone()
	{
		super(453);
		addStartNpc(KLEMIS);
		addTalkId(KLEMIS);
		addKillId(MONSTER1);
		addKillId(MONSTER2);
		addKillId(MONSTER3);
		addCondCompletedQuest(Q10282_ToTheSeedOfAnnihilation.class.getSimpleName(), "32734-03.html");
		addCondMinLevel(MIN_LV, "32734-03.html");
	}
	
	private void increaseKill(PlayerInstance player, Npc npc)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return;
		}
		
		int npcId = npc.getId();
		
		if (Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false))
		{
			if (CommonUtil.contains(MONSTER1, npcId) && qs.isCond(2))
			{
				if (npcId == MONSTER1[4])
				{
					npcId = MONSTER1[0];
				}
				else if (npcId == MONSTER1[5])
				{
					npcId = MONSTER1[1];
				}
				else if (npcId == MONSTER1[6])
				{
					npcId = MONSTER1[2];
				}
				else if (npcId == MONSTER1[7])
				{
					npcId = MONSTER1[3];
				}
				
				final int currValue = qs.getInt("count_" + npcId);
				if (currValue < 15)
				{
					qs.set("count_" + npcId, currValue + 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				checkProgress(qs, 15, MONSTER1[0], MONSTER1[1], MONSTER1[2], MONSTER1[3]);
			}
			else if (CommonUtil.contains(MONSTER2, npcId) && qs.isCond(3))
			{
				if (npcId == MONSTER2[3])
				{
					npcId = MONSTER2[0];
				}
				else if (npcId == MONSTER2[4])
				{
					npcId = MONSTER2[1];
				}
				else if (npcId == MONSTER2[5])
				{
					npcId = MONSTER2[2];
				}
				
				final int currValue = qs.getInt("count_" + npcId);
				if (currValue < 20)
				{
					qs.set("count_" + npcId, currValue + 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				checkProgress(qs, 20, MONSTER2[0], MONSTER2[1], MONSTER2[2]);
			}
			else if (CommonUtil.contains(MONSTER3, npcId) && qs.isCond(4))
			{
				if (npcId == MONSTER3[3])
				{
					npcId = MONSTER3[0];
				}
				else if (npcId == MONSTER3[4])
				{
					npcId = MONSTER3[1];
				}
				else if (npcId == MONSTER3[5])
				{
					npcId = MONSTER3[2];
				}
				
				final int currValue = qs.getInt("count_" + npcId);
				if (currValue < 20)
				{
					qs.set("count_" + npcId, currValue + 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				checkProgress(qs, 20, MONSTER3[0], MONSTER3[1], MONSTER3[2]);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "32734-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "32734-07.html":
			{
				qs.setCond(2, true);
				break;
			}
			case "32734-08.html":
			{
				qs.setCond(3, true);
				break;
			}
			case "32734-09.html":
			{
				qs.setCond(4, true);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		if (player.getParty() != null)
		{
			for (PlayerInstance member : player.getParty().getMembers())
			{
				increaseKill(member, npc);
			}
		}
		else
		{
			increaseKill(player, npc);
		}
		return null;
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
				htmltext = "32734-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "32734-10.html";
						break;
					}
					case 2:
					{
						htmltext = "32734-11.html";
						break;
					}
					case 3:
					{
						htmltext = "32734-12.html";
						break;
					}
					case 4:
					{
						htmltext = "32734-13.html";
						break;
					}
					case 5:
					{
						final int random = getRandom(1000);
						if (random < 34)
						{
							giveItems(player, 34861, 1); // Ingredient and Hardener Pouch (R-grade)
						}
						else if (random < 52)
						{
							giveItems(player, 34861, 2); // Ingredient and Hardener Pouch (R-grade)
						}
						else if (random < 64)
						{
							giveItems(player, 34861, 3); // Ingredient and Hardener Pouch (R-grade)
						}
						else if (random < 73)
						{
							giveItems(player, 34861, 4); // Ingredient and Hardener Pouch (R-grade)
						}
						else if (random < 77)
						{
							giveItems(player, 17526, 1); // Scroll: Enchant Weapon (R-grade)
						}
						else if (random < 124)
						{
							giveItems(player, 17527, 1); // Scroll: Enchant Armor (R-grade)
						}
						else if (random < 153)
						{
							giveItems(player, 9552, 1); // Fire Crystal
						}
						else if (random < 182)
						{
							giveItems(player, 9553, 1); // Water Crystal
						}
						else if (random < 211)
						{
							giveItems(player, 9554, 1); // Earth Crystal
						}
						else if (random < 240)
						{
							giveItems(player, 9555, 1); // Wind Crystal
						}
						else if (random < 269)
						{
							giveItems(player, 9556, 1); // Dark Crystal
						}
						else if (random < 298)
						{
							giveItems(player, 9557, 1); // Holy Crystal
						}
						else if (random < 415)
						{
							giveItems(player, 9546, 1); // Fire Stone
						}
						else if (random < 532)
						{
							giveItems(player, 9547, 1); // Water Stone
						}
						else if (random < 649)
						{
							giveItems(player, 9548, 1); // Earth Stone
						}
						else if (random < 766)
						{
							giveItems(player, 9549, 1); // Wind Stone
						}
						else if (random < 883)
						{
							giveItems(player, 9550, 1); // Dark Stone
						}
						else if (random < 1000)
						{
							giveItems(player, 9551, 1); // Holy Stone
						}
						qs.exitQuest(QuestType.DAILY, true);
						htmltext = "32734-14.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable())
				{
					qs.setState(State.CREATED);
					htmltext = "32734-01.htm";
				}
				else
				{
					htmltext = "32734-02.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	private void checkProgress(QuestState qs, int count, int... mobs)
	{
		for (int mob : mobs)
		{
			if (qs.getInt("count_" + mob) < count)
			{
				return;
			}
		}
		qs.setCond(5, true);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		final Set<NpcLogListHolder> npcLogList = new HashSet<>(3);
		if (qs != null)
		{
			switch (qs.getCond())
			{
				case 2:
				{
					npcLogList.add(new NpcLogListHolder(MONSTER1[0], false, qs.getInt("count_" + MONSTER1[0])));
					npcLogList.add(new NpcLogListHolder(MONSTER1[1], false, qs.getInt("count_" + MONSTER1[1])));
					npcLogList.add(new NpcLogListHolder(MONSTER1[2], false, qs.getInt("count_" + MONSTER1[2])));
					npcLogList.add(new NpcLogListHolder(MONSTER1[3], false, qs.getInt("count_" + MONSTER1[3])));
					break;
				}
				case 3:
				{
					npcLogList.add(new NpcLogListHolder(MONSTER2[0], false, qs.getInt("count_" + MONSTER2[0])));
					npcLogList.add(new NpcLogListHolder(MONSTER2[1], false, qs.getInt("count_" + MONSTER2[1])));
					npcLogList.add(new NpcLogListHolder(MONSTER2[2], false, qs.getInt("count_" + MONSTER2[2])));
					break;
				}
				case 4:
				{
					npcLogList.add(new NpcLogListHolder(MONSTER3[0], false, qs.getInt("count_" + MONSTER3[0])));
					npcLogList.add(new NpcLogListHolder(MONSTER3[1], false, qs.getInt("count_" + MONSTER3[1])));
					npcLogList.add(new NpcLogListHolder(MONSTER3[2], false, qs.getInt("count_" + MONSTER3[2])));
					break;
				}
			}
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}