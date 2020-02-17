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
import org.l2jbr.gameserver.model.actor.Summon;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author Yme
 */
public class PetStatusShow implements IClientOutgoingPacket
{
	private final int _summonType;
	private final int _summonObjectId;
	
	public PetStatusShow(Summon summon)
	{
		_summonType = summon.getSummonType();
		_summonObjectId = summon.getObjectId();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PET_STATUS_SHOW.writeId(packet);
		
		packet.writeD(_summonType);
		packet.writeD(_summonObjectId);
		return true;
	}
}
