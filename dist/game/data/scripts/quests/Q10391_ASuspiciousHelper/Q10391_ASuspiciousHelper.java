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
package quests.Q10391_ASuspiciousHelper;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * A Suspicious Helper (10391)
 * @author St3eT
 */
public class Q10391_ASuspiciousHelper extends Quest
{
	// NPCs
	private static final int ELI = 33858;
	private static final int CHEL = 33861;
	private static final int IASON = 33859;
	// Items
	private static final int CARD = 36707; // Forged Identification Card
	private static final int EXP_MATERTIAL = 36708; // Experimental Material
	// Misc
	private static final int MIN_LEVEL = 40;
	private static final int MAX_LEVEL = 46;
	
	public Q10391_ASuspiciousHelper()
	{
		super(10391);
		addStartNpc(ELI);
		addTalkId(ELI, CHEL, IASON);
		registerQuestItems(CARD, EXP_MATERTIAL);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33858-06.htm");
		addCondNotRace(Race.ERTHEIA, "33858-07.htm");
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
			case "33858-02.htm":
			case "33858-03.htm":
			case "33861-02.html":
			case "33859-02.html":
			case "33859-03.html":
			{
				htmltext = event;
				break;
			}
			case "33858-04.htm":
			{
				giveItems(player, CARD, 1);
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33861-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					takeItems(player, CARD, -1);
					giveItems(player, EXP_MATERTIAL, 1);
					htmltext = event;
				}
				break;
			}
			case "33859-04.html":
			{
				if (qs.isCond(2))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 388290, 93);
				}
				break;
			}
		}
		return htmltext;
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
				if (npc.getId() == ELI)
				{
					htmltext = "33858-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ELI:
					{
						if (qs.isCond(1))
						{
							htmltext = "33858-05.html";
						}
						break;
					}
					case CHEL:
					{
						if (qs.isCond(1))
						{
							htmltext = "33861-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "33861-04.html";
						}
						break;
					}
					case IASON:
					{
						if (qs.isCond(2))
						{
							htmltext = "33859-01.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == ELI)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
}