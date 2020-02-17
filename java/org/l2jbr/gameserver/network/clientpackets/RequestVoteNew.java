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
import org.l2jbr.gameserver.data.xml.impl.FakePlayerData;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.network.serverpackets.UserInfo;

public class RequestVoteNew implements IClientIncomingPacket
{
	private int _targetId;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_targetId = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final WorldObject object = player.getTarget();
		if (!(object instanceof PlayerInstance))
		{
			if (object == null)
			{
				client.sendPacket(SystemMessageId.SELECT_TARGET);
			}
			else if (object.isFakePlayer() && FakePlayerData.getInstance().isTalkable(object.getName()))
			{
				if (player.getRecomLeft() <= 0)
				{
					client.sendPacket(SystemMessageId.YOU_ARE_OUT_OF_RECOMMENDATIONS_TRY_AGAIN_LATER);
					return;
				}
				
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT);
				sm.addString(FakePlayerData.getInstance().getProperName(object.getName()));
				sm.addInt(player.getRecomLeft());
				client.sendPacket(sm);
				
				player.setRecomLeft(player.getRecomLeft() - 1);
				client.sendPacket(new UserInfo(player));
				client.sendPacket(new ExVoteSystemInfo(player));
			}
			else
			{
				client.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
			}
			return;
		}
		
		final PlayerInstance target = (PlayerInstance) object;
		
		if (target.getObjectId() != _targetId)
		{
			return;
		}
		
		if (target == player)
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_RECOMMEND_YOURSELF);
			return;
		}
		
		if (player.getRecomLeft() <= 0)
		{
			client.sendPacket(SystemMessageId.YOU_ARE_OUT_OF_RECOMMENDATIONS_TRY_AGAIN_LATER);
			return;
		}
		
		if (target.getRecomHave() >= 255)
		{
			client.sendPacket(SystemMessageId.YOUR_SELECTED_TARGET_CAN_NO_LONGER_RECEIVE_A_RECOMMENDATION);
			return;
		}
		
		player.giveRecom(target);
		
		SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT);
		sm.addPcName(target);
		sm.addInt(player.getRecomLeft());
		client.sendPacket(sm);
		
		sm = new SystemMessage(SystemMessageId.YOU_HAVE_BEEN_RECOMMENDED_BY_C1);
		sm.addPcName(player);
		target.sendPacket(sm);
		
		client.sendPacket(new UserInfo(player));
		target.broadcastUserInfo();
		
		client.sendPacket(new ExVoteSystemInfo(player));
		target.sendPacket(new ExVoteSystemInfo(target));
	}
}
