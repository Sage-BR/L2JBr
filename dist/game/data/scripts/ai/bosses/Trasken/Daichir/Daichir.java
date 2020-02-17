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
package ai.bosses.Trasken.Daichir;

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
 * Daichir Teleporter AI
 * @author Mobius
 */
public class Daichir extends AbstractNpcAI
{
	// NPCs
	private static final int DAICHIR = 30537;
	private static final int TRASKEN = 29197;
	// Locations
	private static final Location ENTER_LOCATION = new Location(75445, -182112, -9880);
	// Status
	private static final int FIGHTING = 1;
	private static final int DEAD = 3;
	
	public Daichir()
	{
		addFirstTalkId(DAICHIR);
		addTalkId(DAICHIR);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterEarthWyrnCave"))
		{
			final int status = GrandBossManager.getInstance().getBossStatus(TRASKEN);
			if (player.isGM())
			{
				player.teleToLocation(ENTER_LOCATION, true);
				GrandBossManager.getInstance().setBossStatus(TRASKEN, FIGHTING);
			}
			else
			{
				if (status == FIGHTING)
				{
					return "30537-1.html";
				}
				if (status == DEAD)
				{
					return "30537-2.html";
				}
				if (!player.isInParty())
				{
					return "30537-3.html";
				}
				final Party party = player.getParty();
				final boolean isInCC = party.isInCommandChannel();
				final List<PlayerInstance> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
				final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
				if (!isPartyLeader)
				{
					return "30537-3.html";
				}
				if ((members.size() < Config.TRASKEN_MIN_PLAYERS) || (members.size() > Config.TRASKEN_MAX_PLAYERS))
				{
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "30537-4.html"));
					packet.replace("%min%", Integer.toString(Config.TRASKEN_MIN_PLAYERS));
					packet.replace("%max%", Integer.toString(Config.TRASKEN_MAX_PLAYERS));
					player.sendPacket(packet);
					return null;
				}
				for (PlayerInstance member : members)
				{
					if (member.getLevel() < Config.TRASKEN_MIN_PLAYER_LVL)
					{
						final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
						packet.setHtml(getHtm(player, "30537-5.html"));
						packet.replace("%minlvl%", Integer.toString(Config.TRASKEN_MIN_PLAYER_LVL));
						player.sendPacket(packet);
						return null;
					}
				}
				for (PlayerInstance member : members)
				{
					if (member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
					{
						member.teleToLocation(ENTER_LOCATION, true);
						GrandBossManager.getInstance().setBossStatus(TRASKEN, FIGHTING);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + ".html";
	}
	
	public static void main(String[] args)
	{
		new Daichir();
	}
}
