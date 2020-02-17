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
package org.l2jbr.loginserver.network.serverpackets;

import org.l2jbr.commons.network.IOutgoingPacket;
import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.loginserver.network.OutgoingPackets;

/**
 * @author UnAfraid
 */
public class PIAgreementCheck implements IOutgoingPacket
{
	private final int _accountId;
	private final int _status;
	
	public PIAgreementCheck(int accountId, int status)
	{
		_accountId = accountId;
		_status = status;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PI_AGREEMENT_CHECK.writeId(packet);
		packet.writeD(_accountId);
		packet.writeC(_status);
		return true;
	}
}
