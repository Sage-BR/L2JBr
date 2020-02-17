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
package ai.areas.KeucereusAllianceBase.SeedTeleportDevice;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Seed Teleport Device AI.
 * @author St3eT
 */
public class SeedTeleportDevice extends AbstractNpcAI
{
	// NPCs
	private static final int SEED_TELEPORT_DEVICE = 15929;
	// Locations
	private static final Location SOA = new Location(-175572, 154572, 2712);
	private static final Location SOD = new Location(-247024, 251794, 4336);
	private static final Location SOI = new Location(-213699, 210686, 4408);
	private static final Location SOH = new Location(-147354, 152581, -14048);
	// Misc
	private static final int SOH_MIN_LV = 95;
	
	private SeedTeleportDevice()
	{
		addStartNpc(SEED_TELEPORT_DEVICE);
		addFirstTalkId(SEED_TELEPORT_DEVICE);
		addTalkId(SEED_TELEPORT_DEVICE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		
		switch (event)
		{
			case "seedOfAnnihilation":
			{
				player.teleToLocation(SOA);
				break;
			}
			case "seedOfDestruction":
			{
				player.teleToLocation(SOD);
				break;
			}
			case "seedOfInfinity":
			{
				player.teleToLocation(SOI);
				break;
			}
			case "seedOfHellfire":
			{
				if (player.getLevel() < SOH_MIN_LV)
				{
					return "SeedOfHellfire-noLv.html";
				}
				player.teleToLocation(SOH);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new SeedTeleportDevice();
	}
}