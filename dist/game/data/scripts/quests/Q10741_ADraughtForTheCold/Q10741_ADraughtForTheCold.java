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
package quests.Q10741_ADraughtForTheCold;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * A Draught For The Cold (10741)
 * @author Sdw
 */
public class Q10741_ADraughtForTheCold extends Quest
{
	// NPCs
	private static final int SIVANTHE = 33951;
	private static final int LEIRA = 33952;
	// Items
	private static final int EMPTY_HONEY_JAR = 39527;
	private static final int SWEET_HONEY = 39528;
	private static final int NUTRITIOUS_MEAT = 39529;
	// Mobs
	private static final int HONEY_BEE = 23452;
	private static final int KIKU = 23453;
	private static final int ROBUST_HONEY_BEE = 23484;
	// Misc
	private static final int MIN_LEVEL = 10;
	private static final int MAX_LEVEL = 20;
	
	public Q10741_ADraughtForTheCold()
	{
		super(10741);
		addStartNpc(SIVANTHE);
		addTalkId(SIVANTHE, LEIRA);
		addKillId(HONEY_BEE, KIKU, ROBUST_HONEY_BEE);
		
		addCondRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33951-00.htm");
		registerQuestItems(EMPTY_HONEY_JAR, SWEET_HONEY, NUTRITIOUS_MEAT);
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
				giveItems(player, EMPTY_HONEY_JAR, 10);
				break;
			}
			case "33952-02.html":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 22973, 2);
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
						if (qs.isCond(1))
						{
							htmltext = "33951-04.html";
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
			case LEIRA:
			{
				if (qs.isCond(2))
				{
					htmltext = "33952-01.html";
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
		if ((qs != null) && qs.isCond(1))
		{
			switch (npc.getId())
			{
				case HONEY_BEE:
				case ROBUST_HONEY_BEE:
				{
					if (hasQuestItems(killer, EMPTY_HONEY_JAR))
					{
						takeItems(killer, EMPTY_HONEY_JAR, 1);
						giveItems(killer, SWEET_HONEY, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case KIKU:
				{
					giveItemRandomly(killer, npc, NUTRITIOUS_MEAT, 1, 10, 1.0, true);
					break;
				}
			}
			
			if ((getQuestItemsCount(killer, SWEET_HONEY) >= 10) && (getQuestItemsCount(killer, NUTRITIOUS_MEAT) >= 10))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
