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
package handlers.usercommandhandlers;

import org.l2jbr.gameserver.handler.IUserCommandHandler;
import org.l2jbr.gameserver.model.CommandChannel;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.ExMultiPartyCommandChannelInfo;

/**
 * Channel Info user command.
 * @author chris_00
 */
public class ChannelInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		97
	};
	
	@Override
	public boolean useUserCommand(int id, PlayerInstance player)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		if ((player.getParty() == null) || (player.getParty().getCommandChannel() == null))
		{
			return false;
		}
		
		final CommandChannel channel = player.getParty().getCommandChannel();
		player.sendPacket(new ExMultiPartyCommandChannelInfo(channel));
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
