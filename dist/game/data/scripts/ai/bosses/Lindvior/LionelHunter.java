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
package ai.bosses.Lindvior;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.instancemanager.WalkingManager;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Lionel Hunter AI
 * @author Gigi
 * @date 2017-07-23 - [22:54:59]
 */
public class LionelHunter extends AbstractNpcAI
{
	// Npc
	private static final int LIONEL_HUNTER = 33886;
	// Misc
	private static final String ROUTE_NAME = "Rune_Lionel";
	
	public LionelHunter()
	{
		addSpawnId(LIONEL_HUNTER);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		if (event.equals("NPC_SHOUT") && (npc != null))
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WE_JUST_LOCATED_LINDVIOR_THOSE_WHO_ARE_WILLING_TO_FIGHT_CAN_DO_SO_AT_ANY_TIME_NOW);
			getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		getTimers().addTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		WalkingManager.getInstance().startMoving(npc, ROUTE_NAME);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new LionelHunter();
	}
}