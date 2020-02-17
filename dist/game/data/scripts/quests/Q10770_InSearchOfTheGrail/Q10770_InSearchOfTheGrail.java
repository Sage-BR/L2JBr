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
package quests.Q10770_InSearchOfTheGrail;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * In Search of the Grail (10770)
 * @author malyelfik
 */
public class Q10770_InSearchOfTheGrail extends Quest
{
	// NPCs
	private static final int LORAIN = 30673;
	private static final int JANSSEN = 30484;
	// Monsters
	private static final int[] MONSTERS =
	{
		20213, // Porta
		20214, // Excuro
		20216, // Ricenseo
		20217, // Krator
		21036, // Shindebarn
	};
	// Items
	private static final int MYSTERIOUS_FRAGMENT = 39711;
	// Misc
	private static final int MIN_LEVEL = 40;
	private static final double DROP_RATE = 0.4;
	
	public Q10770_InSearchOfTheGrail()
	{
		super(10770);
		addStartNpc(LORAIN);
		addTalkId(LORAIN, JANSSEN);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "30673-00.htm");
		addCondMinLevel(MIN_LEVEL, "30673-00.htm");
		registerQuestItems(MYSTERIOUS_FRAGMENT);
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
			case "30673-02.htm":
			case "30673-03.htm":
			case "30673-04.htm":
			{
				break;
			}
			case "30673-05.htm":
			{
				qs.startQuest();
				break;
			}
			case "30484-02.html":
			{
				if (qs.isCond(2))
				{
					takeItems(player, MYSTERIOUS_FRAGMENT, -1);
					qs.setCond(3, true);
				}
				break;
			}
			case "30484-04.html":
			{
				if (qs.isCond(3))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 4175045, 562);
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
		
		if (npc.getId() == LORAIN)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "30673-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "30673-06.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "30673-07.html";
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
		else if (qs.isStarted())
		{
			if (qs.isCond(2))
			{
				htmltext = "30484-01.html";
			}
			else if (qs.isCond(3))
			{
				htmltext = "30484-03.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && giveItemRandomly(killer, MYSTERIOUS_FRAGMENT, 1, 20, DROP_RATE, true))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}