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
package org.l2jbr.gameserver.network.clientpackets;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.instancemanager.ClanEntryManager;
import org.l2jbr.gameserver.instancemanager.FortManager;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExPledgeCount;
import org.l2jbr.gameserver.network.serverpackets.JoinPledge;
import org.l2jbr.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import org.l2jbr.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import org.l2jbr.gameserver.network.serverpackets.PledgeShowMemberListAll;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Sdw
 */
public class RequestPledgeWaitingUserAccept implements IClientIncomingPacket
{
	private boolean _acceptRequest;
	private int _playerId;
	private int _clanId;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_acceptRequest = packet.readD() == 1;
		_playerId = packet.readD();
		_clanId = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if ((player == null) || (player.getClan() == null))
		{
			return;
		}
		
		if (_acceptRequest)
		{
			final PlayerInstance target = World.getInstance().getPlayer(_playerId);
			final Clan clan = player.getClan();
			if ((target != null) && (target.getClan() == null) && (clan != null))
			{
				target.sendPacket(new JoinPledge(clan.getId()));
				
				// player.setPowerGrade(9); // academy
				target.setPowerGrade(5); // New member starts at 5, not confirmed.
				
				clan.addClanMember(target);
				target.setClanPrivileges(target.getClan().getRankPrivs(target.getPowerGrade()));
				target.sendPacket(SystemMessageId.ENTERED_THE_CLAN);
				
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_JOINED_THE_CLAN);
				sm.addString(target.getName());
				clan.broadcastToOnlineMembers(sm);
				
				if (clan.getCastleId() > 0)
				{
					CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(target);
				}
				if (clan.getFortId() > 0)
				{
					FortManager.getInstance().getFortByOwner(clan).giveResidentialSkills(target);
				}
				target.sendSkillList();
				
				clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(target), target);
				clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
				clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
				
				// This activates the clan tab on the new member.
				PledgeShowMemberListAll.sendAllTo(target);
				target.setClanJoinExpiryTime(0);
				target.broadcastUserInfo();
				
				ClanEntryManager.getInstance().removePlayerApplication(_clanId, _playerId);
			}
		}
		else
		{
			ClanEntryManager.getInstance().removePlayerApplication(_clanId, _playerId);
		}
	}
}
