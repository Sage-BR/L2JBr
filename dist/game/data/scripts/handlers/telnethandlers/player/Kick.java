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
package handlers.telnethandlers.player;

import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.Disconnection;
import org.l2jbr.gameserver.network.telnet.ITelnetCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Kick implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "kick";
	}
	
	@Override
	public String getUsage()
	{
		return "Kick <player name>";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length == 0) || args[0].isEmpty())
		{
			return null;
		}
		final PlayerInstance player = World.getInstance().getPlayer(args[0]);
		if (player != null)
		{
			Disconnection.of(player).defaultSequence(false);
			return "Player has been successfully kicked.";
		}
		return "Couldn't find player with such name.";
	}
}
