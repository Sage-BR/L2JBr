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
import org.l2jbr.gameserver.data.xml.impl.ClanMasteryData;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.holders.ClanMasteryHolder;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExPledgeMasteryInfo extends AbstractItemPacket
{
	final PlayerInstance _player;
	
	public ExPledgeMasteryInfo(PlayerInstance player)
	{
		_player = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		final Clan clan = _player.getClan();
		if (clan == null)
		{
			return false;
		}
		
		OutgoingPackets.EX_PLEDGE_MASTERY_INFO.writeId(packet);
		
		packet.writeD(clan.getUsedDevelopmentPoints()); // Consumed development points
		packet.writeD(clan.getTotalDevelopmentPoints()); // Total development points
		packet.writeD(16); // Mastery count
		for (ClanMasteryHolder mastery : ClanMasteryData.getInstance().getMasteries())
		{
			if (mastery.getId() < 17)
			{
				final int id = mastery.getId();
				packet.writeD(id); // Mastery
				packet.writeD(0x00); // ?
				
				boolean available = true;
				if (clan.getLevel() < mastery.getClanLevel())
				{
					available = false;
				}
				else
				{
					final int previous = mastery.getPreviousMastery();
					final int previousAlt = mastery.getPreviousMasteryAlt();
					if (previousAlt > 0)
					{
						available = clan.hasMastery(previous) || clan.hasMastery(previousAlt);
					}
					else if (previous > 0)
					{
						available = clan.hasMastery(previous);
					}
				}
				
				packet.writeC(clan.hasMastery(id) ? 0x02 : available ? 0x01 : 0x00); // Availability.
			}
		}
		
		return true;
	}
}
