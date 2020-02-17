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
package org.l2jbr.gameserver.model.matching;

import org.l2jbr.gameserver.enums.ExManagePartyRoomMemberType;
import org.l2jbr.gameserver.enums.MatchingMemberType;
import org.l2jbr.gameserver.enums.MatchingRoomType;
import org.l2jbr.gameserver.enums.UserInfoType;
import org.l2jbr.gameserver.instancemanager.MatchingRoomManager;
import org.l2jbr.gameserver.model.CommandChannel;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExDissmissMPCCRoom;
import org.l2jbr.gameserver.network.serverpackets.ExMPCCRoomInfo;
import org.l2jbr.gameserver.network.serverpackets.ExMPCCRoomMember;
import org.l2jbr.gameserver.network.serverpackets.ExManageMpccRoomMember;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Sdw
 */
public class CommandChannelMatchingRoom extends MatchingRoom
{
	public CommandChannelMatchingRoom(String title, int loot, int minlvl, int maxlvl, int maxmem, PlayerInstance leader)
	{
		super(title, loot, minlvl, maxlvl, maxmem, leader);
	}
	
	@Override
	protected void onRoomCreation(PlayerInstance player)
	{
		player.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CREATED);
	}
	
	@Override
	protected void notifyInvalidCondition(PlayerInstance player)
	{
		player.sendPacket(SystemMessageId.YOU_CANNOT_ENTER_THE_COMMAND_CHANNEL_MATCHING_ROOM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
	}
	
	@Override
	protected void notifyNewMember(PlayerInstance player)
	{
		// Update others player
		getMembers().stream().filter(p -> p != player).forEach(p ->
		{
			p.sendPacket(new ExManageMpccRoomMember(p, this, ExManagePartyRoomMemberType.ADD_MEMBER));
		});
		
		// Send SystemMessage to others player
		final SystemMessage sm = new SystemMessage(SystemMessageId.C1_ENTERED_THE_COMMAND_CHANNEL_MATCHING_ROOM);
		sm.addPcName(player);
		getMembers().stream().filter(p -> p != player).forEach(sm::sendTo);
		
		// Update new player
		player.sendPacket(new ExMPCCRoomInfo(this));
		player.sendPacket(new ExMPCCRoomMember(player, this));
	}
	
	@Override
	protected void notifyRemovedMember(PlayerInstance player, boolean kicked, boolean leaderChanged)
	{
		getMembers().forEach(p ->
		{
			p.sendPacket(new ExMPCCRoomInfo(this));
			p.sendPacket(new ExMPCCRoomMember(player, this));
		});
		
		final SystemMessage sm = new SystemMessage(kicked ? SystemMessageId.YOU_WERE_EXPELLED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM : SystemMessageId.YOU_EXITED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM);
		player.sendPacket(sm);
	}
	
	@Override
	public void disbandRoom()
	{
		getMembers().forEach(p ->
		{
			p.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CANCELLED);
			p.sendPacket(ExDissmissMPCCRoom.STATIC_PACKET);
			p.setMatchingRoom(null);
			p.broadcastUserInfo(UserInfoType.CLAN);
			MatchingRoomManager.getInstance().addToWaitingList(p);
		});
		
		getMembers().clear();
		
		MatchingRoomManager.getInstance().removeMatchingRoom(this);
	}
	
	@Override
	public MatchingRoomType getRoomType()
	{
		return MatchingRoomType.COMMAND_CHANNEL;
	}
	
	@Override
	public MatchingMemberType getMemberType(PlayerInstance player)
	{
		if (isLeader(player))
		{
			return MatchingMemberType.COMMAND_CHANNEL_LEADER;
		}
		
		final Party playerParty = player.getParty();
		
		if (playerParty == null)
		{
			return MatchingMemberType.WAITING_PLAYER_NO_PARTY;
		}
		
		final Party leaderParty = getLeader().getParty();
		if (leaderParty != null)
		{
			final CommandChannel cc = leaderParty.getCommandChannel();
			if ((leaderParty == playerParty) || ((cc != null) && cc.getParties().contains(playerParty)))
			{
				return MatchingMemberType.COMMAND_CHANNEL_PARTY_MEMBER;
			}
		}
		
		return MatchingMemberType.WAITING_PARTY;
	}
}
