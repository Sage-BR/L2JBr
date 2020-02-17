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
package ai.areas.TalkingIsland.Walkers;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Rinne AI.
 * @author Gladicek
 */
public class Rinne extends AbstractNpcAI
{
	// NPC
	private static final int RINNE = 33234;
	private static final int ALLADA = 33220;
	// Items
	private static final int WEAPON = 15302;
	
	private Rinne()
	{
		addSpawnId(RINNE);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "TID_LEFTWALK":
			{
				npc.setRHandId(WEAPON);
				getTimers().addTimer("TID_STRAIGHTWALK", (5000 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "TID_STRAIGHTWALK":
			{
				npc.setRHandId(0);
				getTimers().addTimer("TID_LEFTWALK", (5000 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "NPC_SHOUT":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ALL_RACES_CAME_TOGETHER_TO_REBUILD_TALKING_ISLAND_VILLAGE);
				getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
				break;
			}
			case "NPC_FOLLOW":
			{
				addSpawn(ALLADA, npc.getX() + 10, npc.getY() + 10, npc.getZ(), 0, false, 0);
				break;
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		getTimers().addTimer("TID_LEFTWALK", (5000 + getRandom(5)) * 1000, npc, null);
		getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		getTimers().addTimer("NPC_FOLLOW", 100, npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Rinne();
	}
}