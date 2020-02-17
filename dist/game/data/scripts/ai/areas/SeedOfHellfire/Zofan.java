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
package ai.areas.SeedOfHellfire;

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;

import ai.AbstractNpcAI;

/**
 * Zofan AI.
 * @author St3eT
 */
public class Zofan extends AbstractNpcAI
{
	// NPCs
	private static final int[] ZOFAN =
	{
		23215, // Zofan
		23216, // Zofan
		23227, // Beggar Zofan
		23229, // Zofan
		23237, // Engineer Zofan
	};
	// Misc
	private static final String[] MINION_PARAMS =
	{
		"i_adult1_sil",
		"i_adult2_sil",
		"i_adult3_sil",
		"i_child1_sil",
		"i_child2_sil",
		"i_child3_sil",
		"i_child4_sil",
		"i_child5_sil",
		"i_child6_sil",
	};
	
	private Zofan()
	{
		addSpawnId(ZOFAN);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if ((npc.getInstanceWorld() == null) && (npc.getSpawn() != null))
		{
			final StatsSet params = npc.getParameters();
			if (params.getInt("i_childrengarden_guard", 0) == 0)
			{
				for (String param : MINION_PARAMS)
				{
					if (params.getInt(param, -1) != -1)
					{
						addMinion((MonsterInstance) npc, params.getInt(param));
					}
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Zofan();
	}
}
