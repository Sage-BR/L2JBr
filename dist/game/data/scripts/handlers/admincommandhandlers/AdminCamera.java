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

import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.AbstractScript;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.util.BuilderUtil;

/**
 * Camera commands.
 * @author Zoey76
 */
public class AdminCamera implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_cam",
		"admin_camex",
		"admin_cam3"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		if ((activeChar.getTarget() == null) || !activeChar.getTarget().isCreature())
		{
			activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
			return false;
		}
		
		final Creature target = (Creature) activeChar.getTarget();
		final String[] com = command.split(" ");
		switch (com[0])
		{
			case "admin_cam":
			{
				if (com.length != 12)
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //cam force angle1 angle2 time range duration relYaw relPitch isWide relAngle");
					return false;
				}
				AbstractScript.specialCamera(activeChar, target, Integer.parseInt(com[1]), Integer.parseInt(com[2]), Integer.parseInt(com[3]), Integer.parseInt(com[4]), Integer.parseInt(com[5]), Integer.parseInt(com[6]), Integer.parseInt(com[7]), Integer.parseInt(com[8]), Integer.parseInt(com[9]), Integer.parseInt(com[10]));
				break;
			}
			case "admin_camex":
			{
				if (com.length != 10)
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //camex force angle1 angle2 time duration relYaw relPitch isWide relAngle");
					return false;
				}
				AbstractScript.specialCameraEx(activeChar, target, Integer.parseInt(com[1]), Integer.parseInt(com[2]), Integer.parseInt(com[3]), Integer.parseInt(com[4]), Integer.parseInt(com[5]), Integer.parseInt(com[6]), Integer.parseInt(com[7]), Integer.parseInt(com[8]), Integer.parseInt(com[9]));
				break;
			}
			case "admin_cam3":
			{
				if (com.length != 12)
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //cam3 force angle1 angle2 time range duration relYaw relPitch isWide relAngle unk");
					return false;
				}
				AbstractScript.specialCamera3(activeChar, target, Integer.parseInt(com[1]), Integer.parseInt(com[2]), Integer.parseInt(com[3]), Integer.parseInt(com[4]), Integer.parseInt(com[5]), Integer.parseInt(com[6]), Integer.parseInt(com[7]), Integer.parseInt(com[8]), Integer.parseInt(com[9]), Integer.parseInt(com[10]), Integer.parseInt(com[11]));
				break;
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
