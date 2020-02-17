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
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.ClanMember;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author -Wooden-
 */
public class PledgeShowMemberListUpdate implements IClientOutgoingPacket
{
	private final int _pledgeType;
	private final String _name;
	private final int _level;
	private final int _classId;
	private final int _objectId;
	
	public PledgeShowMemberListUpdate(PlayerInstance player)
	{
		this(player.getClan().getClanMember(player.getObjectId()));
	}
	
	public PledgeShowMemberListUpdate(ClanMember member)
	{
		_name = member.getName();
		_level = member.getLevel();
		_classId = member.getClassId();
		_objectId = member.isOnline() ? member.getObjectId() : 0;
		_pledgeType = member.getPledgeType();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PLEDGE_SHOW_MEMBER_LIST_UPDATE.writeId(packet);
		
		packet.writeS(_name);
		packet.writeD(_level);
		packet.writeD(_classId);
		packet.writeD(0); // _sex
		packet.writeD(0); // _race
		packet.writeD(_objectId);
		packet.writeD(_pledgeType);
		packet.writeD(0); // _hasSponsor
		packet.writeC(0);
		return true;
	}
}
