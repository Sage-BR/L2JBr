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
package ai.areas.AteliaFortress.TeleportDevice;

import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Kingdom's Royal Guard Teleport Device
 * @author Gigi
 * @date 2018-04-30 - [23:32:48]
 */
public class TeleportDevice extends AbstractNpcAI
{
	// NPC
	private static final int TELEPORT_DEVICE = 34242;
	// Teleport's
	private static final Location LOCATION1 = new Location(-46335, 59575, -2960);
	private static final Location LOCATION2 = new Location(-42307, 51232, -2032);
	private static final Location LOCATION3 = new Location(-44060, 40139, -1432);
	private static final Location LOCATION4 = new Location(-57242, 43811, -1552);
	
	private TeleportDevice()
	{
		addFirstTalkId(TELEPORT_DEVICE);
		addTalkId(TELEPORT_DEVICE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (player.getFactionLevel(Faction.KINGDOM_ROYAL_GUARDS) < 3)
		{
			return "34242-01.html";
		}
		switch (event)
		{
			case "teleport1":
			{
				player.teleToLocation(LOCATION1);
				break;
			}
			case "teleport2":
			{
				player.teleToLocation(LOCATION2);
				break;
			}
			case "teleport3":
			{
				player.teleToLocation(LOCATION3);
				break;
			}
			case "teleport4":
			{
				player.teleToLocation(LOCATION4);
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "34242.html";
	}
	
	public static void main(String[] args)
	{
		new TeleportDevice();
	}
}
