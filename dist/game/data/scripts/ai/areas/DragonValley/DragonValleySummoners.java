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
package ai.areas.DragonValley;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Dragon Valley summoner NPC AI
 * @author Gigi, Mobius
 */
public class DragonValleySummoners extends AbstractNpcAI
{
	// NPCs
	private static final int BLOODY_GRAVE_WARRIOR = 23441;
	private static final int DARK_GRAVE_WARRIOR = 23442;
	private static final int CAVE_SERVANT_ARCHER = 23436;
	private static final int CAVE_SERVANT_WARRIOR = 23437;
	// Config
	private static final int CHANCE = 15;
	
	private DragonValleySummoners()
	{
		addKillId(BLOODY_GRAVE_WARRIOR, DARK_GRAVE_WARRIOR, CAVE_SERVANT_ARCHER, CAVE_SERVANT_WARRIOR);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		if (getRandom(100) < CHANCE)
		{
			switch (npc.getId())
			{
				case BLOODY_GRAVE_WARRIOR:
				{
					final Npc summon1 = addSpawn(BLOODY_GRAVE_WARRIOR, npc.getX() + 40, npc.getY() + 40, npc.getZ(), npc.getHeading(), false, 120000, true);
					addAttackPlayerDesire(summon1, killer);
					break;
				}
				case DARK_GRAVE_WARRIOR:
				{
					final Npc summon2 = addSpawn(DARK_GRAVE_WARRIOR, npc.getX() + 40, npc.getY() + 40, npc.getZ(), npc.getHeading(), false, 120000, true);
					addAttackPlayerDesire(summon2, killer);
					break;
				}
				case CAVE_SERVANT_ARCHER:
				{
					final Npc summon3 = addSpawn(CAVE_SERVANT_ARCHER, npc.getX() + 40, npc.getY() + 40, npc.getZ(), npc.getHeading(), false, 120000, true);
					addAttackPlayerDesire(summon3, killer);
					break;
				}
				case CAVE_SERVANT_WARRIOR:
				{
					final Npc summon4 = addSpawn(CAVE_SERVANT_WARRIOR, npc.getX() + 40, npc.getY() + 40, npc.getZ(), npc.getHeading(), false, 120000, true);
					addAttackPlayerDesire(summon4, killer);
					break;
				}
			}
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_DEAD_ARE_CALLING_AND_I_ANSWER);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new DragonValleySummoners();
	}
}