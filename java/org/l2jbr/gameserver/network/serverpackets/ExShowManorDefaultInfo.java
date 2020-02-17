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

import java.util.List;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.instancemanager.CastleManorManager;
import org.l2jbr.gameserver.model.Seed;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author l3x
 */
public class ExShowManorDefaultInfo implements IClientOutgoingPacket
{
	private final List<Seed> _crops;
	private final boolean _hideButtons;
	
	public ExShowManorDefaultInfo(boolean hideButtons)
	{
		_crops = CastleManorManager.getInstance().getCrops();
		_hideButtons = hideButtons;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHOW_MANOR_DEFAULT_INFO.writeId(packet);
		
		packet.writeC(_hideButtons ? 0x01 : 0x00); // Hide "Seed Purchase" and "Crop Sales" buttons
		packet.writeD(_crops.size());
		for (Seed crop : _crops)
		{
			packet.writeD(crop.getCropId()); // crop Id
			packet.writeD(crop.getLevel()); // level
			packet.writeD((int) crop.getSeedReferencePrice()); // seed price
			packet.writeD((int) crop.getCropReferencePrice()); // crop price
			packet.writeC(1); // Reward 1 type
			packet.writeD(crop.getReward(1)); // Reward 1 itemId
			packet.writeC(1); // Reward 2 type
			packet.writeD(crop.getReward(2)); // Reward 2 itemId
		}
		return true;
	}
}