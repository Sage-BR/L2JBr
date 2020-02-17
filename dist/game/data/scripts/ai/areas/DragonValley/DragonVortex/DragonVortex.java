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
package ai.areas.DragonValley.DragonVortex;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Dragon Vortex AI.
 * @author UnAfraid, improved by Adry_85
 */
public class DragonVortex extends AbstractNpcAI
{
	// NPC
	private static final int VORTEX = 32871;
	// Raids
	private static final int[] RAIDS =
	{
		25718, // Emerald Horn
		25719, // Dust Rider
		25720, // Bleeding Fly
		25721, // Blackdagger Wing
		25722, // Shadow Summoner
		25723, // Spike Slasher
		25724, // Muscle Bomber
	};
	// Item
	private static final int LARGE_DRAGON_BONE = 17248;
	// Misc
	private static final int DESPAWN_DELAY = 1800000; // 30min
	
	private DragonVortex()
	{
		addStartNpc(VORTEX);
		addFirstTalkId(VORTEX);
		addTalkId(VORTEX);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if ("Spawn".equals(event))
		{
			if (!hasQuestItems(player, LARGE_DRAGON_BONE))
			{
				return "32871-noItem.html";
			}
			else if (npc.isScriptValue(1))
			{
				return "32871-noTime.html";
			}
			
			takeItems(player, LARGE_DRAGON_BONE, 1);
			final int random = getRandom(100);
			int raid = 0;
			
			if (random < 3)
			{
				raid = RAIDS[6]; // Muscle Bomber
			}
			else if (random < 8)
			{
				raid = RAIDS[5]; // Shadow Summoner
			}
			else if (random < 15)
			{
				raid = RAIDS[4]; // Spike Slasher
			}
			else if (random < 25)
			{
				raid = RAIDS[3]; // Blackdagger Wing
			}
			else if (random < 45)
			{
				raid = RAIDS[2]; // Bleeding Fly
			}
			else if (random < 67)
			{
				raid = RAIDS[1]; // Dust Rider
			}
			else
			{
				raid = RAIDS[0]; // Emerald Horn
			}
			addSpawn(raid, npc.getX() + getRandom(-500, 500), npc.getY() + getRandom(-500, 500), npc.getZ() + 10, 0, false, DESPAWN_DELAY, true);
			startQuestTimer("RESET", 60000, npc, null);
		}
		else if ("RESET".equals(event))
		{
			npc.setScriptValue(0);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new DragonVortex();
	}
}