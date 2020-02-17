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
package quests.Q10330_ToTheRuinsOfYeSagira;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10544_SeekerSupplies.Q10544_SeekerSupplies;

/**
 * To the Ruins of Ye Sagira (10330)
 * @author Gladicek
 */
public class Q10330_ToTheRuinsOfYeSagira extends Quest
{
	// NPCs
	private static final int FRANCO = 32153; // Human
	private static final int RIVIAN = 32147; // Elf
	private static final int TOOK = 32150; // Orc
	private static final int DEVON = 32160; // Dark Elf
	private static final int MOKA = 32157; // Dwarf
	private static final int VALFAR = 32146; // Kamael
	private static final int MILA = 30006;
	private static final int LAKCIS = 32977;
	// Items
	private static final int RING_OF_KNOWLEDGE = 875;
	private static final int HEALING_POTION = 1060;
	// Misc
	private static final int MIN_LEVEL = 7;
	private static final int MAX_LEVEL = 20;
	
	public Q10330_ToTheRuinsOfYeSagira()
	{
		super(10330);
		addStartNpc(FRANCO, RIVIAN, TOOK, DEVON, MOKA, VALFAR);
		addTalkId(FRANCO, RIVIAN, TOOK, DEVON, MOKA, VALFAR, MILA, LAKCIS);
		addCondNotRace(Race.ERTHEIA, "noRace.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "noLevel.html");
		addCondCompletedQuest(Q10544_SeekerSupplies.class.getSimpleName(), "noLevel.html");
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
			case "32169-02.html":
			case "32153-02.htm":
			case "32153-03.htm":
			case "32147-02.htm":
			case "32147-03.htm":
			case "32150-02.htm":
			case "32150-03.htm":
			case "32160-02.htm":
			case "32160-03.htm":
			case "32157-02.htm":
			case "32157-03.htm":
			case "32146-02.htm":
			case "32146-03.htm":
			case "32169-05.html":
			{
				htmltext = event;
				break;
			}
			case "32153-04.htm":
			case "32147-04.htm":
			case "32150-04.htm":
			case "32160-04.htm":
			case "32157-04.htm":
			case "32146-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32169-03.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "32977-02.html":
			{
				if (qs.isStarted())
				{
					giveItems(player, RING_OF_KNOWLEDGE, 2);
					giveItems(player, HEALING_POTION, 100);
					addExpAndSp(player, 20100, 11);
					qs.exitQuest(false, true);
					htmltext = event;
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
		String htmltext = null;
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				switch (npc.getId())
				{
					case FRANCO:
					{
						htmltext = (player.getRace() == Race.HUMAN) ? "32153-01.htm" : "32153-00.html";
						break;
					}
					case RIVIAN:
					{
						htmltext = (player.getRace() == Race.ELF) ? "32147-01.htm" : "32147-00.html";
						break;
					}
					case TOOK:
					{
						htmltext = (player.getRace() == Race.ORC) ? "32150-01.htm" : "32150-00.html";
						break;
					}
					case DEVON:
					{
						htmltext = (player.getRace() == Race.DARK_ELF) ? "32160-01.htm" : "32160-00.html";
						break;
					}
					case MOKA:
					{
						htmltext = (player.getRace() == Race.DWARF) ? "32157-01.htm" : "32157-00.html";
						break;
					}
					case VALFAR:
					{
						htmltext = (player.getRace() == Race.KAMAEL) ? "32146-01.htm" : "32146-00.html";
						break;
					}
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case VALFAR:
					{
						if (qs.isCond(1))
						{
							htmltext = "32146-05.html";
						}
						break;
					}
					case MOKA:
					{
						if (qs.isCond(1))
						{
							htmltext = "32157-05.html";
						}
						break;
					}
					case DEVON:
					{
						if (qs.isCond(1))
						{
							htmltext = "32160-05.html";
						}
						break;
					}
					case TOOK:
					{
						if (qs.isCond(1))
						{
							htmltext = "32150-05.html";
						}
						break;
					}
					case FRANCO:
					{
						if (qs.isCond(1))
						{
							htmltext = "32153-05.html";
						}
						break;
					}
					case RIVIAN:
					{
						if (qs.isCond(1))
						{
							htmltext = "32147-05.html";
						}
						break;
					}
					case MILA:
					{
						if (qs.isCond(1))
						{
							htmltext = "32169-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "32169-04.html";
						}
						break;
					}
					case LAKCIS:
					{
						if (qs.isCond(2))
						{
							htmltext = "32977-01.html";
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
}