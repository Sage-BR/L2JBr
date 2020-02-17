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
package quests.Q00474_WaitingForTheSummer;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Waiting for the Summer (474)
 * @author malyelfik
 */
public class Q00474_WaitingForTheSummer extends Quest
{
	// NPCs
	private static final int ADVENTURER = 32327;
	private static final int VISHOTSKY = 31981;
	// Monsters
	private static final int LOST_BUFFALO = 22093;
	private static final int FROST_BUFFALO = 22094;
	private static final int URSUS_CUB = 22095;
	private static final int URSUS = 22096;
	private static final int LOST_YETI = 22097;
	private static final int FROST_YETI = 22098;
	// Items
	private static final int BUFFALO_MEAT = 19490;
	private static final int URSUS_MEAT = 19491;
	private static final int YETI_MEAT = 19492;
	// Misc
	private static final int MIN_LEVEL = 60;
	private static final int MAX_LEVEL = 64;
	private static final double DROP_CHANCE = 0.16d;
	
	public Q00474_WaitingForTheSummer()
	{
		super(474);
		addStartNpc(ADVENTURER);
		addTalkId(ADVENTURER, VISHOTSKY);
		addKillId(LOST_BUFFALO, FROST_BUFFALO, URSUS_CUB, URSUS, LOST_YETI, FROST_YETI);
		
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "");
		registerQuestItems(BUFFALO_MEAT, URSUS_MEAT, YETI_MEAT);
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
			case "32327-02.htm":
			case "32327-03.htm":
			{
				break;
			}
			case "32327-04.htm":
			{
				qs.startQuest();
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
		
		if (npc.getId() == ADVENTURER)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "32327-01.htm";
					break;
				}
				case State.STARTED:
				{
					htmltext = (qs.isCond(1)) ? "32327-05.html" : "32327-06.html";
					break;
				}
			}
		}
		else if (qs.isStarted() && qs.isCond(2))
		{
			giveAdena(player, 194000, true);
			if (player.getLevel() >= MIN_LEVEL)
			{
				addExpAndSp(player, 1879400, 451);
			}
			qs.exitQuest(QuestType.DAILY, true);
			htmltext = "31981-01.html";
		}
		else if (qs.isCompleted() && !qs.isNowAvailable())
		{
			htmltext = "31981-02.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			// Get item id by mob id
			final int itemId;
			switch (npc.getId())
			{
				case LOST_BUFFALO:
				case FROST_BUFFALO:
				{
					itemId = BUFFALO_MEAT;
					break;
				}
				case URSUS:
				case URSUS_CUB:
				{
					itemId = URSUS_MEAT;
					break;
				}
				case LOST_YETI:
				case FROST_YETI:
				{
					itemId = YETI_MEAT;
					break;
				}
				default:
				{
					itemId = -1;
				}
			}
			
			// Give item
			if (itemId != -1)
			{
				giveItemRandomly(killer, npc, itemId, 1, 30, DROP_CHANCE, true);
				if ((getQuestItemsCount(killer, BUFFALO_MEAT) >= 30) && (getQuestItemsCount(killer, URSUS_MEAT) >= 30) && (getQuestItemsCount(killer, YETI_MEAT) >= 30))
				{
					qs.setCond(2);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
