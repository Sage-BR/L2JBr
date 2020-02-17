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
package ai.areas.GardenOfGenesis;

import org.l2jbr.gameserver.geoengine.GeoEngine;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * Garden Watchman AI
 * @author Gigi
 * @date 2018-08-26 - [12:27:45]
 */
public class GardenWatchman extends AbstractNpcAI
{
	// NPCs
	private static final int GARDEN_WATCHMAN = 22952;
	private static final int GENESIS_TRAP_1 = 18985;
	private static final int GENESIS_TRAP_2 = 18986;
	// Skills
	private static final SkillHolder TRAP_SETUP = new SkillHolder(14418, 1);
	private static final SkillHolder HARMFUL_TRAP_1 = new SkillHolder(14075, 1);
	private static final SkillHolder HARMFUL_TRAP_2 = new SkillHolder(14076, 1);
	
	public GardenWatchman()
	{
		addSpawnId(GARDEN_WATCHMAN);
		addSeeCreatureId(GENESIS_TRAP_1, GENESIS_TRAP_2);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "SPAWN_TRAP":
			{
				if ((npc != null) && npc.isSpawned() && !npc.isInCombat())
				{
					npc.doCast(TRAP_SETUP.getSkill());
					final Npc trap = addSpawn((getRandom(10) < 5) ? GENESIS_TRAP_1 : GENESIS_TRAP_2, npc, true, 90000, false);
					trap.setDisplayEffect(1);
					startQuestTimer("SPAWN_TRAP", getRandom(50000, 100000), npc, null);
				}
				break;
			}
			case "DEBUFF":
			{
				World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 100, nearby ->
				{
					if ((npc != null) && npc.isScriptValue(0) && nearby.isPlayer() && GeoEngine.getInstance().canSeeTarget(npc, nearby))
					{
						npc.setScriptValue(1);
						npc.setTarget(nearby);
						npc.doCast((getRandom(10) < 5) ? HARMFUL_TRAP_1.getSkill() : HARMFUL_TRAP_2.getSkill());
						npc.deleteMe();
					}
				});
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		cancelQuestTimer("SPAWN_TRAP", npc, null);
		startQuestTimer("SPAWN_TRAP", 50000, npc, null);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			startQuestTimer("DEBUFF", 3000, npc, null, true);
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	public static void main(String[] args)
	{
		new GardenWatchman();
	}
}
