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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.instancemanager.CastleManorManager;
import org.l2jbr.gameserver.model.CropProcure;
import org.l2jbr.gameserver.model.Seed;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author l3x
 */
public class ExShowCropSetting implements IClientOutgoingPacket
{
	private final int _manorId;
	private final Set<Seed> _seeds;
	private final Map<Integer, CropProcure> _current = new HashMap<>();
	private final Map<Integer, CropProcure> _next = new HashMap<>();
	
	public ExShowCropSetting(int manorId)
	{
		final CastleManorManager manor = CastleManorManager.getInstance();
		_manorId = manorId;
		_seeds = manor.getSeedsForCastle(_manorId);
		for (Seed s : _seeds)
		{
			// Current period
			CropProcure cp = manor.getCropProcure(manorId, s.getCropId(), false);
			if (cp != null)
			{
				_current.put(s.getCropId(), cp);
			}
			// Next period
			cp = manor.getCropProcure(manorId, s.getCropId(), true);
			if (cp != null)
			{
				_next.put(s.getCropId(), cp);
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHOW_CROP_SETTING.writeId(packet);
		
		packet.writeD(_manorId); // manor id
		packet.writeD(_seeds.size()); // size
		
		for (Seed s : _seeds)
		{
			packet.writeD(s.getCropId()); // crop id
			packet.writeD(s.getLevel()); // seed level
			packet.writeC(1);
			packet.writeD(s.getReward(1)); // reward 1 id
			packet.writeC(1);
			packet.writeD(s.getReward(2)); // reward 2 id
			packet.writeD(s.getCropLimit()); // next sale limit
			packet.writeD(0); // ???
			packet.writeD(s.getCropMinPrice()); // min crop price
			packet.writeD((int) s.getCropMaxPrice()); // max crop price
			// Current period
			if (_current.containsKey(s.getCropId()))
			{
				final CropProcure cp = _current.get(s.getCropId());
				packet.writeQ(cp.getStartAmount()); // buy
				packet.writeQ(cp.getPrice()); // price
				packet.writeC(cp.getReward()); // reward
			}
			else
			{
				packet.writeQ(0);
				packet.writeQ(0);
				packet.writeC(0);
			}
			// Next period
			if (_next.containsKey(s.getCropId()))
			{
				final CropProcure cp = _next.get(s.getCropId());
				packet.writeQ(cp.getStartAmount()); // buy
				packet.writeQ(cp.getPrice()); // price
				packet.writeC(cp.getReward()); // reward
			}
			else
			{
				packet.writeQ(0);
				packet.writeQ(0);
				packet.writeC(0);
			}
		}
		_next.clear();
		_current.clear();
		return true;
	}
}