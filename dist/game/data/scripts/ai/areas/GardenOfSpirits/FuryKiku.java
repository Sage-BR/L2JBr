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
package ai.areas.GardenOfSpirits;

import org.l2jbr.gameserver.ai.CtrlEvent;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Fury Kiku AI
 * @author Gigi
 * @date 2018-07-23 - [15:47:15]
 */
public class FuryKiku extends AbstractNpcAI
{
	// Monsters
	private static final int FURYKIKU = 23545;
	private static final int[] MONSTERS =
	{
		23544, // Fury Sylph Barrena
		23553, // Fury Sylph Barrena (night)
	};
	
	public FuryKiku()
	{
		addKillId(MONSTERS);
		addSpawnId(FURYKIKU);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "SPAWN":
			{
				Party party = player.getParty();
				if (party != null)
				{
					party.getMembers().forEach(p -> addSpawn(FURYKIKU, p, true, 180000, true, 0));
				}
				else
				{
					addSpawn(FURYKIKU, player, true, 180000, true, 0);
				}
				break;
			}
			case "ATTACK":
			{
				npc.setRunning();
				World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 300, p ->
				{
					if ((p != null) && p.isPlayable() && !p.isDead())
					{
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1000);
					}
				});
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		if (getRandom(10) < 5)
		{
			startQuestTimer("SPAWN", 2000, npc, killer);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		startQuestTimer("ATTACK", 1000, npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new FuryKiku();
	}
}