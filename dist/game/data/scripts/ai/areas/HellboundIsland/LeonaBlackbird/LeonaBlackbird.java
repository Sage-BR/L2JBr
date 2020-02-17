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
package ai.areas.HellboundIsland.LeonaBlackbird;

import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Leona Blackbird AI.
 * @author St3eT
 */
public class LeonaBlackbird extends AbstractNpcAI
{
	// NPCs
	private static final int LEONA = 31595; // Leona Blackbird
	
	public LeonaBlackbird()
	{
		addFirstTalkId(LEONA);
		addTalkId(LEONA);
		addStartNpc(LEONA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "31595.html":
			case "31595-01.html":
			case "31595-02.html":
			case "31595-03.html":
			{
				htmltext = event;
				break;
			}
			case "playMovie":
			{
				playMovie(player, Movie.SC_HELLBOUND);
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new LeonaBlackbird();
	}
}
