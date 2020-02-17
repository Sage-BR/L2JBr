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
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.util.BuilderUtil;

/**
 * @author
 */
public class AdminRide implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_ride_horse",
		"admin_ride_bike",
		"admin_ride_wyvern",
		"admin_ride_strider",
		"admin_unride_wyvern",
		"admin_unride_strider",
		"admin_unride",
		"admin_ride_wolf",
		"admin_unride_wolf",
	};
	private int _petRideId;
	
	private static final int PURPLE_MANED_HORSE_TRANSFORMATION_ID = 106;
	
	private static final int JET_BIKE_TRANSFORMATION_ID = 20001;
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final PlayerInstance player = getRideTarget(activeChar);
		if (player == null)
		{
			return false;
		}
		
		if (command.startsWith("admin_ride"))
		{
			if (player.isMounted() || player.hasSummon())
			{
				BuilderUtil.sendSysMessage(activeChar, "Target already have a summon.");
				return false;
			}
			if (command.startsWith("admin_ride_wyvern"))
			{
				_petRideId = 12621;
			}
			else if (command.startsWith("admin_ride_strider"))
			{
				_petRideId = 12526;
			}
			else if (command.startsWith("admin_ride_wolf"))
			{
				_petRideId = 16041;
			}
			else if (command.startsWith("admin_ride_horse")) // handled using transformation
			{
				if (player.isTransformed())
				{
					activeChar.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				}
				else
				{
					player.transform(PURPLE_MANED_HORSE_TRANSFORMATION_ID, true);
				}
				
				return true;
			}
			else if (command.startsWith("admin_ride_bike")) // handled using transformation
			{
				if (player.isTransformed())
				{
					activeChar.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				}
				else
				{
					player.transform(JET_BIKE_TRANSFORMATION_ID, true);
				}
				
				return true;
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "Command '" + command + "' not recognized");
				return false;
			}
			
			player.mount(_petRideId, 0, false);
			
			return false;
		}
		else if (command.startsWith("admin_unride"))
		{
			if (player.getTransformationId() == PURPLE_MANED_HORSE_TRANSFORMATION_ID)
			{
				player.untransform();
			}
			
			if (player.getTransformationId() == JET_BIKE_TRANSFORMATION_ID)
			{
				player.untransform();
			}
			else
			{
				player.dismount();
			}
		}
		return true;
	}
	
	private PlayerInstance getRideTarget(PlayerInstance activeChar)
	{
		PlayerInstance player = null;
		
		if ((activeChar.getTarget() == null) || (activeChar.getTarget().getObjectId() == activeChar.getObjectId()) || !activeChar.getTarget().isPlayer())
		{
			player = activeChar;
		}
		else
		{
			player = (PlayerInstance) activeChar.getTarget();
		}
		
		return player;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
}
