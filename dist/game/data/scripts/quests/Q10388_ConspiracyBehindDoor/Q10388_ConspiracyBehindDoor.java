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
package quests.Q10388_ConspiracyBehindDoor;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * @author hlwrave
 */
public class Q10388_ConspiracyBehindDoor extends Quest
{
	// NPCs
	private static final int ELIA = 31329;
	private static final int KARGOS = 33821;
	private static final int HICHEN = 33820;
	private static final int RAZDEN = 33803;
	// Item
	private static final int VISITORS_BADGE = 8064;
	// Misc
	private static final int MIN_LEVEL = 97;
	
	public Q10388_ConspiracyBehindDoor()
	{
		super(10388);
		addStartNpc(ELIA);
		addTalkId(ELIA, KARGOS, HICHEN, RAZDEN);
		addCondMinLevel(MIN_LEVEL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "go.html":
			{
				qs.startQuest();
				break;
			}
			case "toCond2.html":
			{
				qs.setCond(2, true);
				break;
			}
			case "toCond3.html":
			{
				qs.setCond(0);
				qs.setCond(3, true);
				giveItems(player, VISITORS_BADGE, 1);
				break;
			}
			case "final.html":
			{
				addExpAndSp(player, 29638350, 2963835);
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
		
		final int npcId = npc.getId();
		switch (qs.getState())
		{
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
			case State.CREATED:
			{
				if (npcId == ELIA)
				{
					htmltext = player.getLevel() >= MIN_LEVEL ? "start.htm" : "nolvl.html";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npcId)
				{
					case KARGOS:
					{
						if (qs.isCond(1))
						{
							htmltext = "cond1.html";
						}
						break;
					}
					case HICHEN:
					{
						if (qs.isCond(2))
						{
							htmltext = "cond2.html";
						}
						break;
					}
					case RAZDEN:
					{
						if (qs.isCond(3))
						{
							htmltext = "cond3.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}