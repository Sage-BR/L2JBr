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
package quests.Q10856_SuperionAppears;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Superion Appears (10856)
 * @URL https://l2wiki.com/Superion_Appears
 * @author Dmitri
 */
public class Q10856_SuperionAppears extends Quest
{
	// NPCs
	private static final int KEKROPUS = 34222;
	private static final int MELDINA = 32214;
	private static final int HISTY = 34243;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q10856_SuperionAppears()
	{
		super(10856);
		addStartNpc(KEKROPUS);
		addTalkId(KEKROPUS, MELDINA, HISTY);
		addCondMinLevel(MIN_LEVEL, "level_check.htm");
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
			case "34222-02.htm":
			case "34222-06.htm":
			case "34214-02.htm":
			case "34222-05.htm":
			{
				htmltext = event;
				break;
			}
			case "34222-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34214-03.htm":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "34222-04.htm":
			{
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "teleport":
			{
				qs.setCond(3, true);
				player.teleToLocation(79827, 152588, 2309);
				break;
			}
			case "finish":
			{
				htmltext = "34243-02.htm";
				giveAdena(player, 164122, true);
				addExpAndSp(player, 592571412, 1422162);
				qs.exitQuest(false, true);
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
				if (npc.getId() == KEKROPUS)
				{
					htmltext = "34222-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case KEKROPUS:
					{
						if (qs.getCond() == 1)
						{
							htmltext = "34222-09.htm";
						}
						else if (qs.getCond() == 2)
						{
							htmltext = "34222-07.htm";
						}
						else if (qs.getCond() == 3)
						{
							htmltext = "34222-08.htm";
						}
						break;
					}
					case MELDINA:
					{
						if (qs.getCond() == 1)
						{
							htmltext = "34214-01.htm";
						}
						else if (qs.getCond() == 2)
						{
							htmltext = "34214-04.htm";
						}
						break;
					}
					case HISTY:
					{
						if (qs.getCond() == 3)
						{
							htmltext = "34243-01.htm";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable())
				{
					htmltext = "34222-01.htm";
					break;
				}
				qs.setState(State.CREATED);
			}
		}
		return htmltext;
	}
}