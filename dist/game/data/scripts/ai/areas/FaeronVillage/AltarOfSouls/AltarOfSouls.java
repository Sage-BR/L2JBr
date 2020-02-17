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
package ai.areas.FaeronVillage.AltarOfSouls;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Altar of Souls AI.
 * @author Mobius
 */
public class AltarOfSouls extends AbstractNpcAI
{
	// NPCs
	private static final int ALTAR_OF_SOULS = 33920;
	private static final int LADAR = 25942;
	private static final int CASSIUS = 25943;
	private static final int TERAKAN = 25944;
	// Items
	private static final int APPARITION_STONE_88 = 38572;
	private static final int APPARITION_STONE_93 = 38573;
	private static final int APPARITION_STONE_98 = 38574;
	// Misc
	private Npc BOSS_88;
	private Npc BOSS_93;
	private Npc BOSS_98;
	
	private AltarOfSouls()
	{
		addStartNpc(ALTAR_OF_SOULS);
		addFirstTalkId(ALTAR_OF_SOULS);
		addTalkId(ALTAR_OF_SOULS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "request_boss_88":
			{
				if ((BOSS_88 != null) && !BOSS_88.isDead())
				{
					return "33920-4.html";
				}
				if (hasQuestItems(player, APPARITION_STONE_88))
				{
					takeItems(player, APPARITION_STONE_88, 1);
					BOSS_88 = addSpawn(TERAKAN, player.getX() + getRandom(-300, 300), player.getY() + getRandom(-300, 300), player.getZ() + 10, getRandom(64000), false, 0, true);
					return "33920-1.html";
				}
				return "33920-7.html";
			}
			case "request_boss_93":
			{
				if ((BOSS_93 != null) && !BOSS_93.isDead())
				{
					return "33920-5.html";
				}
				if (hasQuestItems(player, APPARITION_STONE_93))
				{
					takeItems(player, APPARITION_STONE_93, 1);
					BOSS_93 = addSpawn(CASSIUS, player.getX() + getRandom(-300, 300), player.getY() + getRandom(-300, 300), player.getZ() + 10, getRandom(64000), false, 0, true);
					return "33920-2.html";
				}
				return "33920-8.html";
			}
			case "request_boss_98":
			{
				if ((BOSS_98 != null) && !BOSS_98.isDead())
				{
					return "33920-6.html";
				}
				if (hasQuestItems(player, APPARITION_STONE_98))
				{
					takeItems(player, APPARITION_STONE_98, 1);
					BOSS_98 = addSpawn(LADAR, player.getX() + getRandom(-300, 300), player.getY() + getRandom(-300, 300), player.getZ() + 10, getRandom(64000), false, 0, true);
					return "33920-3.html";
				}
				return "33920-9.html";
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "33920.html";
	}
	
	public static void main(String[] args)
	{
		new AltarOfSouls();
	}
}