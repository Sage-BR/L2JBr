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
package quests.Q10744_StrongerThanSteel;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;

/**
 * Stronger Than Steel (10744)
 * @author Sdw
 */
public class Q10744_StrongerThanSteel extends Quest
{
	// NPCs
	private static final int MILONE = 33953;
	private static final int DOLKIN = 33954;
	// Monsters
	private static final int TREANT = 23457;
	private static final int LEAFIE = 23458;
	// Items
	private static final int TREANT_LEAF = 39532;
	private static final int LEAFIE_LEAF = 39531;
	// Misc
	private static final int MIN_LEVEL = 15;
	private static final int MAX_LEVEL = 20;
	
	public Q10744_StrongerThanSteel()
	{
		super(10744);
		addStartNpc(MILONE);
		addTalkId(MILONE, DOLKIN);
		addKillId(TREANT, LEAFIE);
		
		addCondRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33953-00.htm");
		registerQuestItems(TREANT_LEAF, LEAFIE_LEAF);
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
			case "33953-02.htm":
			case "33954-02.html":
			{
				break;
			}
			case "33953-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "33954-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
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
		
		if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		
		switch (npc.getId())
		{
			case MILONE:
			{
				if (qs.isCreated())
				{
					htmltext = "33953-01.htm";
				}
				else if (qs.isStarted() && qs.isCond(1))
				{
					htmltext = "33953-04.html";
				}
				break;
			}
			case DOLKIN:
			{
				if (qs.isStarted())
				{
					if (qs.isCond(1))
					{
						htmltext = "33954-01.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33954-04.html";
					}
					else if (qs.isCond(3))
					{
						addExpAndSp(player, 153994, 5);
						qs.exitQuest(false, true);
						htmltext = "33954-05.html";
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
		
		if ((qs != null) && qs.isCond(2))
		{
			if (npc.getId() == TREANT)
			{
				giveItemRandomly(killer, npc, TREANT_LEAF, 1, 20, 1.0, true);
			}
			else if (npc.getId() == LEAFIE)
			{
				giveItemRandomly(killer, npc, LEAFIE_LEAF, 1, 15, 1.0, true);
			}
			
			if ((getQuestItemsCount(killer, TREANT_LEAF) >= 20) && (getQuestItemsCount(killer, LEAFIE_LEAF) >= 15))
			{
				qs.setCond(3, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
