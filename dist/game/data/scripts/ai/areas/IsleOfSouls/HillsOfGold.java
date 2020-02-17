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
package ai.areas.IsleOfSouls;

import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Hills of Gold AI.
 * @author Mobius
 */
public class HillsOfGold extends AbstractNpcAI
{
	// NPCs
	private static final int GOLEM_OF_REPAIRS = 19309;
	private static final int EXCAVATOR_GOLEM = 19312;
	private static final int DRILL_GOLEM = 19310;
	private static final int SPICULA_1 = 23246;
	private static final int SPICULA_2 = 23247;
	private static final int YIN_FRAGMENT = 19308;
	private static final int SPICULA_ELITE_GUARD = 23303;
	private static final int[] GOLEMS =
	{
		23255,
		23257,
		23259,
		23261,
		23263,
		23264,
		23266,
		23267,
	};
	
	public HillsOfGold()
	{
		addAttackId(YIN_FRAGMENT);
		addSpawnId(SPICULA_1, SPICULA_2);
		addSpawnId(GOLEMS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if ((npc != null) && !npc.isDead())
		{
			World.getInstance().forEachVisibleObjectInRange(npc, MonsterInstance.class, npc.getAggroRange(), nearby ->
			{
				if (npc.isInCombat())
				{
					return;
				}
				if ((nearby.getId() == GOLEM_OF_REPAIRS) || (nearby.getId() == EXCAVATOR_GOLEM) || (nearby.getId() == DRILL_GOLEM))
				{
					addAttackDesire(npc, nearby);
					return;
				}
			});
			startQuestTimer("SPICULA_AGGRO", 10000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final Npc mob1 = addSpawn(SPICULA_ELITE_GUARD, npc.getX(), npc.getY(), npc.getZ(), attacker.getHeading() + 32500, true, npc.getSpawn().getRespawnDelay());
		addAttackDesire(mob1, attacker);
		final Npc mob2 = addSpawn(SPICULA_ELITE_GUARD, npc.getX(), npc.getY(), npc.getZ(), attacker.getHeading() + 32500, true, npc.getSpawn().getRespawnDelay());
		addAttackDesire(mob2, attacker);
		final Npc mob3 = addSpawn(SPICULA_ELITE_GUARD, npc.getX(), npc.getY(), npc.getZ(), attacker.getHeading() + 32500, true, npc.getSpawn().getRespawnDelay());
		addAttackDesire(mob3, attacker);
		final Npc mob4 = addSpawn(SPICULA_ELITE_GUARD, npc.getX(), npc.getY(), npc.getZ(), attacker.getHeading() + 32500, true, npc.getSpawn().getRespawnDelay());
		addAttackDesire(mob4, attacker);
		npc.deleteMe();
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if ((npc.getId() == SPICULA_1) || (npc.getId() == SPICULA_2))
		{
			startQuestTimer("SPICULA_AGGRO", 5000, npc, null);
		}
		else
		{
			npc.setDisplayEffect(1);
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new HillsOfGold();
	}
}