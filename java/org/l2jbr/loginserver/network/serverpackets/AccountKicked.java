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
 * @author KenM
 */
public class AccountKicked implements IOutgoingPacket
{
	public enum AccountKickedReason
	{
		REASON_DATA_STEALER(0x01),
		REASON_GENERIC_VIOLATION(0x08),
		REASON_7_DAYS_SUSPENDED(0x10),
		REASON_PERMANENTLY_BANNED(0x20);
		
		private final int _code;
		
		AccountKickedReason(int code)
		{
			_code = code;
		}
		
		public int getCode()
		{
			return _code;
		}
	}
	
	private final AccountKickedReason _reason;
	
	/**
	 * @param reason
	 */
	public AccountKicked(AccountKickedReason reason)
	{
		_reason = reason;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.ACCOUNT_KICKED.writeId(packet);
		packet.writeD(_reason.getCode());
		
		return true;
	}
}
