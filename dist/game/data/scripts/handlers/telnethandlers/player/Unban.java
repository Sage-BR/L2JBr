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

import org.l2jbr.gameserver.data.sql.impl.CharNameTable;
import org.l2jbr.gameserver.instancemanager.PunishmentManager;
import org.l2jbr.gameserver.model.punishment.PunishmentAffect;
import org.l2jbr.gameserver.model.punishment.PunishmentType;
import org.l2jbr.gameserver.network.telnet.ITelnetCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Unban implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "unban";
	}
	
	@Override
	public String getUsage()
	{
		return "Unban <player name>";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length == 0) || args[0].isEmpty())
		{
			return null;
		}
		final int objectId = CharNameTable.getInstance().getIdByName(args[0]);
		if (objectId > 0)
		{
			if (!PunishmentManager.getInstance().hasPunishment(objectId, PunishmentAffect.CHARACTER, PunishmentType.BAN))
			{
				return "Player is not banned at all.";
			}
			PunishmentManager.getInstance().stopPunishment(objectId, PunishmentAffect.CHARACTER, PunishmentType.BAN);
			return "Player has been successfully unbanned.";
		}
		return "Couldn't find player with such name.";
	}
}
