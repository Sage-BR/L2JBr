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
package org.l2jbr.gameserver.network.clientpackets;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.serverpackets.PledgeCrest;

/**
 * @version $Revision: 1.4.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPledgeCrest implements IClientIncomingPacket
{
	private int _crestId;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_crestId = packet.readD();
		packet.readD(); // clanId
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		client.sendPacket(new PledgeCrest(_crestId));
	}
}
