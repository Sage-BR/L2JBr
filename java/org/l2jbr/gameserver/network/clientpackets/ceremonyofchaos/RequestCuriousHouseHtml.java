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
package org.l2jbr.gameserver.network.clientpackets.ceremonyofchaos;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.enums.CeremonyOfChaosState;
import org.l2jbr.gameserver.instancemanager.CeremonyOfChaosManager;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Sdw
 */
public class RequestCuriousHouseHtml implements IClientIncomingPacket
{
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		// Nothing to read
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (CeremonyOfChaosManager.getInstance().getState() != CeremonyOfChaosState.REGISTRATION)
		{
			return;
		}
		else if (CeremonyOfChaosManager.getInstance().isRegistered(player))
		{
			player.sendPacket(SystemMessageId.YOU_ARE_ON_THE_WAITING_LIST_FOR_THE_CEREMONY_OF_CHAOS);
			return;
		}
		
		if (CeremonyOfChaosManager.getInstance().canRegister(player, true))
		{
			final NpcHtmlMessage message = new NpcHtmlMessage(0);
			message.setFile(player, "data/html/CeremonyOfChaos/invite.htm");
			player.sendPacket(message);
		}
	}
}
