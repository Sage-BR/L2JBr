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
package handlers.telnethandlers.server;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.network.telnet.ITelnetCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Performance implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "performance";
	}
	
	@Override
	public String getUsage()
	{
		return "Performance";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		// ThreadPoolManager.purge();
		final StringBuilder sb = new StringBuilder();
		for (String line : ThreadPool.getStats())
		{
			sb.append(line + Config.EOL);
		}
		return sb.toString();
	}
}
