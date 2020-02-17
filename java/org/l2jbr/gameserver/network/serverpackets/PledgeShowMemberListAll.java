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

import java.util.Collection;

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.clan.ClanMember;
import org.l2jbr.gameserver.network.OutgoingPackets;

public class PledgeShowMemberListAll implements IClientOutgoingPacket
{
	private final Clan _clan;
	private final String _name;
	private final String _leaderName;
	private final Collection<ClanMember> _members;
	
	private PledgeShowMemberListAll(Clan clan, boolean isSubPledge)
	{
		_clan = clan;
		_leaderName = clan.getLeaderName();
		_name = clan.getName();
		_members = _clan.getMembers();
	}
	
	public static void sendAllTo(PlayerInstance player)
	{
		final Clan clan = player.getClan();
		player.sendPacket(new PledgeShowMemberListAll(clan, true));
		for (ClanMember member : clan.getMembers())
		{
			if (member.getPledgeType() != Clan.PLEDGE_CLASS_COMMON)
			{
				player.sendPacket(new PledgeShowMemberListUpdate(member));
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PLEDGE_SHOW_MEMBER_LIST_ALL.writeId(packet);
		
		packet.writeD(0x00); // _isSubPledge
		packet.writeD(_clan.getId());
		packet.writeD(Config.SERVER_ID);
		packet.writeD(0x00);
		packet.writeS(_name);
		packet.writeS(_leaderName);
		
		packet.writeD(_clan.getCrestId()); // crest id .. is used again
		packet.writeD(_clan.getLevel());
		packet.writeD(_clan.getCastleId());
		packet.writeD(0x00);
		packet.writeD(_clan.getHideoutId());
		packet.writeD(_clan.getFortId());
		packet.writeD(_clan.getRank());
		packet.writeD(_clan.getReputationScore());
		packet.writeD(0x00); // 0
		packet.writeD(0x00); // 0
		packet.writeD(_clan.getAllyId());
		packet.writeS(_clan.getAllyName());
		packet.writeD(_clan.getAllyCrestId());
		packet.writeD(_clan.isAtWar() ? 1 : 0); // new c3
		packet.writeD(0x00); // Territory castle ID
		
		packet.writeD(_members.size());
		for (ClanMember m : _members)
		{
			packet.writeS(m.getName());
			packet.writeD(m.getLevel());
			packet.writeD(m.getClassId());
			packet.writeD(0); // sex
			packet.writeD(0); // race
			packet.writeD(m.isOnline() ? m.getObjectId() : 0); // objectId = online 0 = offline
			packet.writeD(0);
			packet.writeC(0);
		}
		return true;
	}
}
