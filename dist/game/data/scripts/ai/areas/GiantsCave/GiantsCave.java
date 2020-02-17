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
package ai.areas.GiantsCave;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Giants Cave Npcs AI
 * @author Gigi
 * @date 2017-03-04 - [17:33:25]
 */
public class GiantsCave extends AbstractNpcAI
{
	// NPC
	private static final int SUMADRIBA = 34217;
	private static final int KRENAHT = 34237;
	private static final int STHOR = 34219;
	private static final int GIANT_TELEPORT = 34223;
	
	private GiantsCave()
	{
		addTalkId(SUMADRIBA, KRENAHT, GIANT_TELEPORT, STHOR);
		addFirstTalkId(SUMADRIBA, KRENAHT, GIANT_TELEPORT, STHOR);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34237.html":
			case "34237-1.html":
			case "34237-2.html":
			case "34237-3.html":
			case "34237-4.html":
			case "34237-5.html":
			case "34237-6.html":
			{
				htmltext = event;
				break;
			}
			case "first_area":
			{
				htmltext = "34223-1.html";
				break;
			}
			case "second_area":
			{
				htmltext = "34223-1.html";
				break;
			}
			case "thrid_area":
			{
				htmltext = "34223-1.html";
				break;
			}
			case "stronghold":
			{
				htmltext = "34223-2.html";
				break;
			}
			case "relics_rooom":
			{
				htmltext = "34223-3.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		switch (npc.getId())
		{
			case SUMADRIBA:
			{
				player.sendPacket(new PlaySound(3, "Npcdialog1.schmadriba_faction_1", 0, 0, 0, 0, 0));
				break;
			}
			case KRENAHT:
			{
				if (getRandom(10) < 5)
				{
					player.sendPacket(new PlaySound(3, "Npcdialog1.krenat_faction_2", 0, 0, 0, 0, 0));
				}
				else
				{
					player.sendPacket(new PlaySound(3, "Npcdialog1.krenat_faction_1", 0, 0, 0, 0, 0));
				}
				break;
			}
			case STHOR:
			{
				if (getRandom(10) < 4)
				{
					player.sendPacket(new PlaySound(3, "Npcdialog1.stor_faction_2", 0, 0, 0, 0, 0));
				}
				else
				{
					player.sendPacket(new PlaySound(3, "Npcdialog1.stor_faction_1", 0, 0, 0, 0, 0));
				}
				break;
			}
		}
		return npc.getId() + ".html";
	}
	
	public static void main(String[] args)
	{
		new GiantsCave();
	}
}
