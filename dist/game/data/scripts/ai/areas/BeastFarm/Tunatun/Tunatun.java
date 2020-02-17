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
package ai.areas.BeastFarm.Tunatun;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Beast Herder Tunatun AI.
 * @author Adry_85
 */
public class Tunatun extends AbstractNpcAI
{
	// NPC
	private static final int TUNATUN = 31537;
	// Item
	private static final int BEAST_HANDLERS_WHIP = 15473;
	// Misc
	private static final int MIN_LEVEL = 82;
	
	private Tunatun()
	{
		addStartNpc(TUNATUN);
		addFirstTalkId(TUNATUN);
		addTalkId(TUNATUN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		
		switch (event)
		{
			case "31537-04.html":
			case "31537-05.html":
			case "31537-06.html":
			{
				htmltext = event;
				break;
			}
			case "whip":
			{
				{
					if (!hasQuestItems(player, BEAST_HANDLERS_WHIP))
					{
						if (player.getLevel() >= MIN_LEVEL)
						{
							giveItems(player, BEAST_HANDLERS_WHIP, 1);
							htmltext = "31537-03.html";
						}
						else
						{
							htmltext = "31537-02.html";
						}
					}
					else
					{
						htmltext = "31537-01.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Tunatun();
	}
}