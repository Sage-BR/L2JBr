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

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.instance.StaticObjectInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.CreatureSay;
import org.l2jbr.gameserver.util.BuilderUtil;

/**
 * This class handles following admin commands: - targetsay <message> = makes talk a Creature
 * @author nonom
 */
public class AdminTargetSay implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_targetsay"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		if (command.startsWith("admin_targetsay"))
		{
			try
			{
				final WorldObject obj = activeChar.getTarget();
				if ((obj instanceof StaticObjectInstance) || !obj.isCreature())
				{
					activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
					return false;
				}
				
				final String message = command.substring(16);
				final Creature target = (Creature) obj;
				target.broadcastPacket(new CreatureSay(target.getObjectId(), target.isPlayer() ? ChatType.GENERAL : ChatType.NPC_GENERAL, target.getName(), message));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //targetsay <text>");
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
