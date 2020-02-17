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
package ai.areas.ImperialTomb.Zenya;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Zenya AI.
 * @author Stayway
 */
public class Zenya extends AbstractNpcAI
{
	// NPC
	private static final int ZENYA = 32140;
	// Location
	private static final Location IMPERIAL_TOMB = new Location(183400, -81208, -5323);
	// Misc
	private static final int MIN_LEVEL = 80;
	
	private Zenya()
	{
		addStartNpc(ZENYA);
		addFirstTalkId(ZENYA);
		addTalkId(ZENYA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "32140.html":
			case "32140-1.html":
			case "32140-2.html":
			case "32140-4.html":
			{
				htmltext = event;
				break;
			}
			case "teleport":
			{
				player.teleToLocation(IMPERIAL_TOMB);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return player.getLevel() < MIN_LEVEL ? "32140-3.html" : "32140.html";
	}
	
	public static void main(String[] args)
	{
		new Zenya();
	}
}