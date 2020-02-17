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
import org.l2jbr.gameserver.data.sql.impl.CrestTable;
import org.l2jbr.gameserver.model.Crest;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.serverpackets.ExPledgeEmblem;

/**
 * @author -Wooden-, Sdw
 */
public class RequestExPledgeCrestLarge implements IClientIncomingPacket
{
	private int _crestId;
	private int _clanId;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_crestId = packet.readD();
		_clanId = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final Crest crest = CrestTable.getInstance().getCrest(_crestId);
		final byte[] data = crest != null ? crest.getData() : null;
		if (data != null)
		{
			for (int i = 0; i <= 4; i++)
			{
				if (i < 4)
				{
					final byte[] fullChunk = new byte[14336];
					System.arraycopy(data, (14336 * i), fullChunk, 0, 14336);
					client.sendPacket(new ExPledgeEmblem(_crestId, fullChunk, _clanId, i));
				}
				else
				{
					final byte[] lastChunk = new byte[8320];
					System.arraycopy(data, (14336 * i), lastChunk, 0, 8320);
					client.sendPacket(new ExPledgeEmblem(_crestId, lastChunk, _clanId, i));
				}
			}
		}
	}
}
