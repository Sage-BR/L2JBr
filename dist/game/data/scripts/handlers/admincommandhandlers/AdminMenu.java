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
package handlers.admincommandhandlers;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.gameserver.handler.AdminCommandHandler;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.network.Disconnection;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.util.BuilderUtil;

/**
 * This class handles following admin commands: - handles every admin menu command
 * @version $Revision: 1.3.2.6.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminMenu implements IAdminCommandHandler
{
	private static final Logger LOGGER = Logger.getLogger(AdminMenu.class.getName());
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_char_manage",
		"admin_teleport_character_to_menu",
		"admin_recall_char_menu",
		"admin_recall_party_menu",
		"admin_recall_clan_menu",
		"admin_goto_char_menu",
		"admin_kick_menu",
		"admin_kill_menu",
		"admin_ban_menu",
		"admin_unban_menu"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		if (command.equals("admin_char_manage"))
		{
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_teleport_character_to_menu"))
		{
			final String[] data = command.split(" ");
			if (data.length == 5)
			{
				final String playerName = data[1];
				final PlayerInstance player = World.getInstance().getPlayer(playerName);
				if (player != null)
				{
					teleportCharacter(player, new Location(Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4])), activeChar, "Admin is teleporting you.");
				}
			}
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_recall_char_menu"))
		{
			try
			{
				final String targetName = command.substring(23);
				final PlayerInstance player = World.getInstance().getPlayer(targetName);
				teleportCharacter(player, activeChar.getLocation(), activeChar, "Admin is teleporting you.");
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_recall_party_menu"))
		{
			try
			{
				final String targetName = command.substring(24);
				final PlayerInstance player = World.getInstance().getPlayer(targetName);
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
					return true;
				}
				if (!player.isInParty())
				{
					BuilderUtil.sendSysMessage(activeChar, "Player is not in party.");
					teleportCharacter(player, activeChar.getLocation(), activeChar, "Admin is teleporting you.");
					return true;
				}
				for (PlayerInstance pm : player.getParty().getMembers())
				{
					teleportCharacter(pm, activeChar.getLocation(), activeChar, "Your party is being teleported by an Admin.");
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "", e);
			}
		}
		else if (command.startsWith("admin_recall_clan_menu"))
		{
			try
			{
				final String targetName = command.substring(23);
				final PlayerInstance player = World.getInstance().getPlayer(targetName);
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
					return true;
				}
				final Clan clan = player.getClan();
				if (clan == null)
				{
					BuilderUtil.sendSysMessage(activeChar, "Player is not in a clan.");
					teleportCharacter(player, activeChar.getLocation(), activeChar, "Admin is teleporting you.");
					return true;
				}
				
				for (PlayerInstance member : clan.getOnlineMembers(0))
				{
					teleportCharacter(member, activeChar.getLocation(), activeChar, "Your clan is being teleported by an Admin.");
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "", e);
			}
		}
		else if (command.startsWith("admin_goto_char_menu"))
		{
			try
			{
				final PlayerInstance player = World.getInstance().getPlayer(command.substring(21));
				teleportToCharacter(activeChar, player);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.equals("admin_kill_menu"))
		{
			handleKill(activeChar);
		}
		else if (command.startsWith("admin_kick_menu"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				final String player = st.nextToken();
				final PlayerInstance plyr = World.getInstance().getPlayer(player);
				String text;
				if (plyr != null)
				{
					Disconnection.of(plyr).defaultSequence(false);
					text = "You kicked " + plyr.getName() + " from the game.";
				}
				else
				{
					text = "Player " + player + " was not found in the game.";
				}
				activeChar.sendMessage(text);
			}
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_ban_menu"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				final String subCommand = "admin_ban_char";
				AdminCommandHandler.getInstance().useAdminCommand(activeChar, subCommand + command.substring(14), true);
			}
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_unban_menu"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				final String subCommand = "admin_unban_char";
				AdminCommandHandler.getInstance().useAdminCommand(activeChar, subCommand + command.substring(16), true);
			}
			showMainPage(activeChar);
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleKill(PlayerInstance activeChar)
	{
		handleKill(activeChar, null);
	}
	
	private void handleKill(PlayerInstance activeChar, String player)
	{
		final WorldObject obj = activeChar.getTarget();
		Creature target = (Creature) obj;
		String filename = "main_menu.htm";
		if (player != null)
		{
			final PlayerInstance plyr = World.getInstance().getPlayer(player);
			if (plyr != null)
			{
				target = plyr;
				BuilderUtil.sendSysMessage(activeChar, "You killed " + plyr.getName());
			}
		}
		if (target != null)
		{
			if (target.isPlayer())
			{
				target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar, null);
				filename = "charmanage.htm";
			}
			else if (Config.CHAMPION_ENABLE && target.isChampion())
			{
				target.reduceCurrentHp((target.getMaxHp() * Config.CHAMPION_HP) + 1, activeChar, null);
			}
			else
			{
				target.reduceCurrentHp(target.getMaxHp() + 1, activeChar, null);
			}
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
		}
		AdminHtml.showAdminHtml(activeChar, filename);
	}
	
	private void teleportCharacter(PlayerInstance player, Location loc, PlayerInstance activeChar, String message)
	{
		if (player != null)
		{
			player.sendMessage(message);
			player.teleToLocation(loc, true);
		}
		showMainPage(activeChar);
	}
	
	private void teleportToCharacter(PlayerInstance activeChar, WorldObject target)
	{
		if (!target.isPlayer())
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final PlayerInstance player = target.getActingPlayer();
		if (player.getObjectId() == activeChar.getObjectId())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
		}
		else
		{
			activeChar.teleToLocation(player.getLocation(), true, player.getInstanceWorld());
			BuilderUtil.sendSysMessage(activeChar, "You're teleporting yourself to character " + player.getName());
		}
		showMainPage(activeChar);
	}
	
	/**
	 * @param activeChar
	 */
	private void showMainPage(PlayerInstance activeChar)
	{
		AdminHtml.showAdminHtml(activeChar, "charmanage.htm");
	}
}
