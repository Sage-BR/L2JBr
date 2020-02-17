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
package ai.areas.EnchantedValley;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * AI from Flower Bud in Enchanted Valley, after kill random spawn<br>
 * [Nymph Rose (Elegant), Nymph Lily (Elegant), Nymph Tulip (Elegant), Nymph Cosmos (Elegant)]
 * @author Gigi
 */
public class FlowerBud extends AbstractNpcAI
{
	// NPCs
	private static final int FLOWER_BUD = 19600;
	private static final List<Integer> FLOWER_SPAWNS = new ArrayList<>();
	static
	{
		FLOWER_SPAWNS.add(23582);
		FLOWER_SPAWNS.add(23583);
		FLOWER_SPAWNS.add(23584);
		FLOWER_SPAWNS.add(23585);
	}
	
	private FlowerBud()
	{
		addKillId(FLOWER_BUD);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("spawn") && npc.isDead())
		{
			final Npc elegant = addSpawn(FLOWER_SPAWNS.get(Rnd.get(FLOWER_SPAWNS.size())), npc, false, 120000, false);
			addAttackPlayerDesire(elegant, player);
		}
		return event;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		
		startQuestTimer("spawn", 3000, npc, killer);
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new FlowerBud();
	}
}