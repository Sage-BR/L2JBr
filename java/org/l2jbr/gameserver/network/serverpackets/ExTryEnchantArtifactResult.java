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
package org.l2jbr.gameserver.network.serverpackets;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author Bonux (bonuxq@gmail.com)
 * @date 09.09.2019
 **/
public class ExTryEnchantArtifactResult implements IClientOutgoingPacket
{
	public static final int SUCCESS = 0;
	public static final int FAIL = 1;
	public static final int ERROR = 2;
	
	public static final ExTryEnchantArtifactResult ERROR_PACKET = new ExTryEnchantArtifactResult(ERROR, 0);
	
	private final int _state;
	private final int _enchant;
	
	public ExTryEnchantArtifactResult(int state, int enchant)
	{
		_state = state;
		_enchant = enchant;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_TRY_ENCHANT_ARTIFACT_RESULT.writeId(packet);
		
		packet.writeD(_state);
		packet.writeD(_enchant);
		packet.writeD(0);
		packet.writeD(0);
		packet.writeD(0);
		return true;
	}
}