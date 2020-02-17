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
package ai.areas.WallOfArgos.ElmoredenServantsGhost;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Elmoreden Servant's Ghost AI.
 * @author St3eT
 */
public class ElmoredenServantsGhost extends AbstractNpcAI
{
	// NPC
	private static final int GHOST = 31920; // Elmoreden Servant's Ghost
	// Items
	private static final int USED_GRAVE_PASS = 7261;
	private static final int ANTIQUE_BROOCH = 7262;
	
	private ElmoredenServantsGhost()
	{
		addStartNpc(GHOST);
		addTalkId(GHOST);
		addFirstTalkId(GHOST);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		if (event.equals("teleport1") || event.equals("teleport2"))
		{
			if (!hasAtLeastOneQuestItem(player, USED_GRAVE_PASS, ANTIQUE_BROOCH))
			{
				htmltext = "31920-no.html";
			}
			else
			{
				takeItems(player, USED_GRAVE_PASS, 1);
				
				final Location loc;
				final StatsSet npcParameters = npc.getParameters();
				if (event.equals("teleport1"))
				{
					loc = new Location(npcParameters.getInt("TelPos_X1", 0), npcParameters.getInt("TelPos_Y1", 0), npcParameters.getInt("TelPos_Z1", 0));
				}
				else
				{
					loc = new Location(npcParameters.getInt("TelPos_X2", 0), npcParameters.getInt("TelPos_Y2", 0), npcParameters.getInt("TelPos_Z2", 0));
				}
				
				player.teleToLocation(loc);
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new ElmoredenServantsGhost();
	}
}
