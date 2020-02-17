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
package custom.FactionSystem;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class FactionSystem extends AbstractNpcAI
{
	// NPCs
	private static final int MANAGER = 500;
	// Other
	private static final String[] TEXTS =
	{
		Config.FACTION_GOOD_TEAM_NAME + " or " + Config.FACTION_EVIL_TEAM_NAME + "?",
		"Select your faction!",
		"The choice is yours!"
	};
	
	private FactionSystem()
	{
		addSpawnId(MANAGER);
		addStartNpc(MANAGER);
		addTalkId(MANAGER);
		addFirstTalkId(MANAGER);
		
		if (Config.FACTION_SYSTEM_ENABLED)
		{
			addSpawn(MANAGER, Config.FACTION_MANAGER_LOCATION, false, 0);
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "selectGoodFaction":
			{
				if (Config.FACTION_BALANCE_ONLINE_PLAYERS && (World.getInstance().getAllGoodPlayers().size() >= (World.getInstance().getAllEvilPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
				{
					final String htmltext = null;
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "onlinelimit.html"));
					packet.replace("%name%", player.getName());
					packet.replace("%more%", Config.FACTION_GOOD_TEAM_NAME);
					packet.replace("%less%", Config.FACTION_EVIL_TEAM_NAME);
					player.sendPacket(packet);
					return htmltext;
				}
				if (Config.FACTION_AUTO_NOBLESS)
				{
					player.setNobleLevel(1);
				}
				player.setGood();
				player.getAppearance().setNameColor(Config.FACTION_GOOD_NAME_COLOR);
				player.getAppearance().setTitleColor(Config.FACTION_GOOD_NAME_COLOR);
				player.setTitle(Config.FACTION_GOOD_TEAM_NAME);
				player.sendMessage("You are now fighting for the " + Config.FACTION_GOOD_TEAM_NAME + " faction.");
				player.teleToLocation(Config.FACTION_GOOD_BASE_LOCATION);
				broadcastMessageToFaction(Config.FACTION_GOOD_TEAM_NAME, Config.FACTION_GOOD_TEAM_NAME + " faction grows stronger with the arrival of " + player.getName() + ".");
				World.addFactionPlayerToWorld(player);
				break;
			}
			case "selectEvilFaction":
			{
				if (Config.FACTION_BALANCE_ONLINE_PLAYERS && (World.getInstance().getAllEvilPlayers().size() >= (World.getInstance().getAllGoodPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
				{
					final String htmltext = null;
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "onlinelimit.html"));
					packet.replace("%name%", player.getName());
					packet.replace("%more%", Config.FACTION_EVIL_TEAM_NAME);
					packet.replace("%less%", Config.FACTION_GOOD_TEAM_NAME);
					player.sendPacket(packet);
					return htmltext;
				}
				if (Config.FACTION_AUTO_NOBLESS)
				{
					player.setNobleLevel(1);
				}
				player.setEvil();
				player.getAppearance().setNameColor(Config.FACTION_EVIL_NAME_COLOR);
				player.getAppearance().setTitleColor(Config.FACTION_EVIL_NAME_COLOR);
				player.setTitle(Config.FACTION_EVIL_TEAM_NAME);
				player.sendMessage("You are now fighting for the " + Config.FACTION_EVIL_TEAM_NAME + " faction.");
				player.teleToLocation(Config.FACTION_EVIL_BASE_LOCATION);
				broadcastMessageToFaction(Config.FACTION_EVIL_TEAM_NAME, Config.FACTION_EVIL_TEAM_NAME + " faction grows stronger with the arrival of " + player.getName() + ".");
				World.addFactionPlayerToWorld(player);
				break;
			}
			case "SPEAK":
			{
				if (npc != null)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(TEXTS), 1500);
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final String htmltext = null;
		final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
		packet.setHtml(getHtm(player, "manager.html"));
		packet.replace("%name%", player.getName());
		packet.replace("%good%", Config.FACTION_GOOD_TEAM_NAME);
		packet.replace("%evil%", Config.FACTION_EVIL_TEAM_NAME);
		player.sendPacket(packet);
		return htmltext;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (npc.getId() == MANAGER)
		{
			startQuestTimer("SPEAK", 10000, npc, null, true);
		}
		return super.onSpawn(npc);
	}
	
	private void broadcastMessageToFaction(String factionName, String message)
	{
		if (factionName.equals(Config.FACTION_GOOD_TEAM_NAME))
		{
			for (PlayerInstance player : World.getInstance().getAllGoodPlayers())
			{
				player.sendMessage(message);
			}
		}
		else
		{
			for (PlayerInstance player : World.getInstance().getAllEvilPlayers())
			{
				player.sendMessage(message);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new FactionSystem();
	}
}
