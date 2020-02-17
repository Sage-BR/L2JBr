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
package org.l2jbr.gameserver.network.clientpackets.sayune;

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.data.xml.impl.SayuneData;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.SayuneEntry;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.request.SayuneRequest;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.model.zone.type.SayuneZone;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;

/**
 * @author UnAfraid
 */
public class RequestFlyMoveStart implements IClientIncomingPacket
{
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if ((player == null) || !player.isInsideZone(ZoneId.SAYUNE) || player.hasRequest(SayuneRequest.class) || (!player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) && !Config.FREE_JUMPS_FOR_ALL))
		{
			return;
		}
		
		if (player.hasSummon())
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_USE_SAYUNE_WHILE_PET_OR_SUMMONED_PET_IS_OUT);
			return;
		}
		
		if (player.getReputation() < 0)
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_SAYUNE_WHILE_IN_A_CHAOTIC_STATE);
			return;
		}
		
		if (player.hasRequests())
		{
			player.sendPacket(SystemMessageId.SAYUNE_CANNOT_BE_USED_WHILE_TAKING_OTHER_ACTIONS);
			return;
		}
		
		final SayuneZone zone = ZoneManager.getInstance().getZone(player, SayuneZone.class);
		if (zone.getMapId() == -1)
		{
			player.sendMessage("That zone is not supported yet!");
			LOGGER.warning(getClass().getSimpleName() + ": " + player + " Requested sayune on zone with no map id set");
			return;
		}
		
		final SayuneEntry map = SayuneData.getInstance().getMap(zone.getMapId());
		if (map == null)
		{
			player.sendMessage("This zone is not handled yet!!");
			LOGGER.warning(getClass().getSimpleName() + ": " + player + " Requested sayune on unhandled map zone " + zone.getName());
			return;
		}
		
		final SayuneRequest request = new SayuneRequest(player, map.getId());
		if (player.addRequest(request))
		{
			request.move(player, 0);
		}
	}
}
