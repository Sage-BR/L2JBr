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
package org.l2jbr.gameserver.network.serverpackets.pledgeV2;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.data.xml.impl.DailyMissionData;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Bonux (bonuxq@gmail.com)
 * @date 29.09.2019
 **/
public class ExPledgeMissionRewardCount implements IClientOutgoingPacket
{
	private final int _doneMissionsCount;
	private final int _availableMissionsCount;
	
	public ExPledgeMissionRewardCount(PlayerInstance player)
	{
		_doneMissionsCount = (int) DailyMissionData.getInstance().getDailyMissionData(player).stream().filter(d -> d.getRecentlyCompleted(player)).count();
		_availableMissionsCount = DailyMissionData.getInstance().getDailyMissionData(player).size();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PLEDGE_MISSION_REWARD_COUNT.writeId(packet);
		
		packet.writeD(Math.min(_availableMissionsCount, _doneMissionsCount)); // Received missions rewards.
		packet.writeD(_availableMissionsCount); // Available missions rewards. 18 - for noble, 20 - for honnorable noble.
		
		return true;
	}
}
