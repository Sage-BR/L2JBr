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
package quests.Q10740_NeverForget;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.areas.FaeronVillage.RemembranceTower.RemembranceTower;

/**
 * Never Forget (10740)
 * @author Sdw
 */
public class Q10740_NeverForget extends Quest
{
	// NPCs
	private static final int SIVANTHE = 33951;
	private static final int REMEMBERANCE_TOWER = 33989;
	// Items
	private static final int UNNAMED_RELICS = 39526;
	private static final ItemHolder RING_OF_KNOWLEDGE = new ItemHolder(875, 2);
	private static final ItemHolder HEALING_POTION = new ItemHolder(1060, 100);
	// Mobs
	private static final int[] MOBS =
	{
		23449, // Keen Floato
		23450, // Ratel
		23451, // Robust Ratel
	};
	// Misc
	private static final int MIN_LEVEL = 8;
	private static final int MAX_LEVEL = 20;
	
	public Q10740_NeverForget()
	{
		super(10740);
		addStartNpc(SIVANTHE);
		addTalkId(SIVANTHE, REMEMBERANCE_TOWER);
		addKillId(MOBS);
		
		addCondRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33951-07.htm");
		registerQuestItems(UNNAMED_RELICS);
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
			case "33951-02.htm":
			{
				break;
			}
			case "33951-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "33989-02.html":
			{
				if (qs.isCond(2) && (getQuestItemsCount(player, UNNAMED_RELICS) >= 20))
				{
					final Quest q = QuestManager.getInstance().getQuest(RemembranceTower.class.getSimpleName());
					if (q != null)
					{
						q.notifyEvent("action", npc, null);
					}
					takeItems(player, UNNAMED_RELICS, -1);
					qs.setCond(3, true);
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
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case SIVANTHE:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = "33951-01.htm";
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "33951-04.html";
								break;
							}
							case 2:
							{
								htmltext = "33951-05.html";
								break;
							}
							case 3:
							{
								showOnScreenMsg(player, NpcStringId.CHECK_YOUR_EQUIPMENT_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 10000);
								giveItems(player, RING_OF_KNOWLEDGE);
								giveItems(player, HEALING_POTION);
								addExpAndSp(player, 24001, 0);
								qs.exitQuest(false, true);
								htmltext = "33951-06.html";
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
			case REMEMBERANCE_TOWER:
			{
				if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 2:
						{
							htmltext = "33989-01.html";
							break;
						}
						case 3:
						{
							htmltext = "33989-03.html";
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
		if ((qs != null) && qs.isCond(1) && giveItemRandomly(killer, npc, UNNAMED_RELICS, 1, 20, 1.0, true))
		{
			qs.setCond(2);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
