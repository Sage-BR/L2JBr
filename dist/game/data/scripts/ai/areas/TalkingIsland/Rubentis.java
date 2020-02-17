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
package ai.areas.TalkingIsland;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.geoengine.GeoEngine;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.util.Util;

import ai.AbstractNpcAI;

/**
 * Rubentis AI.
 * @author St3eT
 */
public class Rubentis extends AbstractNpcAI
{
	// NPC
	private static final int RUBENTIS = 33120;
	
	private Rubentis()
	{
		addSpawnId(RUBENTIS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("NPC_MOVE"))
		{
			if (getRandomBoolean())
			{
				final Location randomLoc = Util.getRandomPosition(npc.getSpawn().getLocation(), 0, 500);
				addMoveToDesire(npc, GeoEngine.getInstance().canMoveToTargetLoc(npc.getLocation().getX(), npc.getLocation().getY(), npc.getLocation().getZ(), randomLoc.getX(), randomLoc.getY(), randomLoc.getZ(), npc.getInstanceWorld()), 23);
			}
			startQuestTimer("NPC_MOVE", 10000 + (getRandom(5) * 1000), npc, null);
		}
		else if (event.equals("NPC_SHOUT"))
		{
			final int rand = getRandom(3);
			if (rand == 0)
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HUNTING_AT_THE_BEACH_IS_A_BAD_IDEA);
			}
			else if (rand == 1)
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ONLY_THE_STRONG_SURVIVE_AT_RUINS_OF_YE_SAGIRA);
			}
			startQuestTimer("NPC_SHOUT", 10000 + (getRandom(5) * 1000), npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		startQuestTimer("NPC_MOVE", 10000 + (getRandom(5) * 1000), npc, null);
		startQuestTimer("NPC_SHOUT", 10000 + (getRandom(5) * 1000), npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Rubentis();
	}
}