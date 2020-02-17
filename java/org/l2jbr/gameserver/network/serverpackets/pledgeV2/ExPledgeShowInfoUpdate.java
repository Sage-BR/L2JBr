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
import org.l2jbr.gameserver.data.xml.impl.ClanLevelData;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExPledgeShowInfoUpdate extends AbstractItemPacket
{
	final PlayerInstance _player;
	
	public ExPledgeShowInfoUpdate(PlayerInstance player)
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
		
		OutgoingPackets.EX_PLEDGE_SHOW_INFO_UPDATE.writeId(packet);
		packet.writeD(clan.getId()); // Clan ID
		packet.writeD(ClanLevelData.getLevelRequirement(clan.getLevel())); // Next level cost
		packet.writeD(ClanLevelData.getCommonMemberLimit(clan.getLevel())); // Max pledge members
		packet.writeD(ClanLevelData.getEliteMemberLimit(clan.getLevel())); // Max elite members
		return true;
	}
}
