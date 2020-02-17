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
package ai.areas.OrcBarracks;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.AbstractNpcAI;

/**
 * Orc Barracks AI
 * @author malyelfik
 */
public class OrcBarracks extends AbstractNpcAI
{
	// NPC
	private static final int TUREK_ORC_FOOTMAN = 20499;
	private static final int TUREK_WAR_HOUND = 20494;
	private static final int CHERTUBA_MIRAGE = 23421;
	private static final int CHERTUBA_ILLUSION = 23422;
	private static final int[] MOBS =
	{
		20495, // Turek Orc Prefect
		20496, // Turek Orc Archer
		20497, // Turek Orc Skirmisher
		20498, // Turek Orc Supplier
		20500, // Turek Orc Sentinel
		20501, // Turek Orc Priest
		20546, // Turek Orc Elder
		23418, // Marionette Spirit
	};
	// Misc
	private static final int MINION_COUNT = 2;
	private static final int SPAWN_RATE = 80;
	
	public OrcBarracks()
	{
		addSpawnId(TUREK_ORC_FOOTMAN);
		addKillId(TUREK_ORC_FOOTMAN);
		addKillId(MOBS);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final boolean hasMinions = npc.getParameters().getBoolean("hasMinions", false);
		if (hasMinions)
		{
			for (int i = 0; i < MINION_COUNT; i++)
			{
				addMinion((MonsterInstance) npc, TUREK_WAR_HOUND);
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		if ((killer.getRace() == Race.ERTHEIA) && (SPAWN_RATE > getRandom(100)))
		{
			final int npcId = (killer.isMageClass()) ? CHERTUBA_ILLUSION : CHERTUBA_MIRAGE;
			showOnScreenMsg(killer, NpcStringId.A_POWERFUL_MONSTER_HAS_COME_TO_FACE_YOU, ExShowScreenMessage.TOP_CENTER, 5000);
			addSpawn(npcId, npc, false, 180000);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new OrcBarracks();
	}
}
