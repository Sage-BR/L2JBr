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
package ai.areas.TownOfGoddard.Cerenas;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestState;

import ai.AbstractNpcAI;
import quests.Q10369_NoblesseSoulTesting.Q10369_NoblesseSoulTesting;

/**
 * Cerenas AI.
 * @author Gladicek
 */
public class Cerenas extends AbstractNpcAI
{
	// NPC
	private static final int CERENAS = 31281;
	// Item
	private static final int NOBLESSE_TIARA = 7694;
	
	private Cerenas()
	{
		addStartNpc(CERENAS);
		addTalkId(CERENAS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = player.getQuestState(Q10369_NoblesseSoulTesting.class.getSimpleName());
		String htmltext = null;
		
		if (event.equals("tiara"))
		{
			if ((qs != null) && qs.isCompleted())
			{
				if (!hasQuestItems(player, NOBLESSE_TIARA))
				{
					giveItems(player, NOBLESSE_TIARA, 1);
				}
				else
				{
					htmltext = "31281-02.html";
				}
			}
			else
			{
				htmltext = "31281-01.html";
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Cerenas();
	}
}