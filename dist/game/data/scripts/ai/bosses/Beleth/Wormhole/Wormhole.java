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
package ai.bosses.Beleth.Wormhole;

import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.gameserver.instancemanager.GrandBossManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.AbstractNpcAI;

/**
 * Wormhole AI (33901).
 * @author gigi
 */
public class Wormhole extends AbstractNpcAI
{
	// NPCs
	private static final int WORMHOLE = 33901;
	private static final int BELETH = 29118;
	// Location
	private static final Location BELETH_LOCATION = new Location(16327, 209228, -9357);
	// TODO: New location
	// private static final Location BELETH_LOCATION = new Location(-17551, 245949, -832);
	
	public Wormhole()
	{
		addFirstTalkId(WORMHOLE);
		addTalkId(WORMHOLE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("teleport"))
		{
			final int status = GrandBossManager.getInstance().getBossStatus(BELETH);
			if (status == 1)
			{
				return "33901-4.html";
			}
			if (status == 2)
			{
				return "33901-5.html";
			}
			
			if (!player.isInParty())
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "33901-2.html"));
				packet.replace("%min%", Integer.toString(Config.BELETH_MIN_PLAYERS));
				player.sendPacket(packet);
				return null;
			}
			
			final Party party = player.getParty();
			final boolean isInCC = party.isInCommandChannel();
			final List<PlayerInstance> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
			final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
			if (!isPartyLeader)
			{
				return "33901-3.html";
			}
			else if ((members.size() < Config.BELETH_MIN_PLAYERS) || (members.size() > Config.BELETH_MAX_PLAYERS))
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "33901-2.html"));
				packet.replace("%min%", Integer.toString(Config.BELETH_MIN_PLAYERS));
				player.sendPacket(packet);
			}
			else
			{
				for (PlayerInstance member : members)
				{
					if (member.isInsideRadius3D(npc, 1000))
					{
						member.teleToLocation(BELETH_LOCATION, true);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
		packet.setHtml(getHtm(player, "33901-1.html"));
		packet.replace("%min%", Integer.toString(Config.BELETH_MIN_PLAYERS));
		packet.replace("%max%", Integer.toString(Config.BELETH_MAX_PLAYERS));
		player.sendPacket(packet);
		return null;
	}
	
	public static void main(String[] args)
	{
		new Wormhole();
	}
}