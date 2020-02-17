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
package org.l2jbr.gameserver.network.clientpackets.pledgeV2;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.pledgeV2.ExPledgeSkillInfo;

/**
 * @author Mobius
 */
public class RequestExPledgeSkillActivate implements IClientIncomingPacket
{
	private int _skillId;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_skillId = packet.readD();
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
		final Clan clan = player.getClan();
		if (clan == null)
		{
			return;
		}
		if (player.getObjectId() != clan.getLeaderId())
		{
			player.sendMessage("You do not have enough privileges to take this action.");
			return;
		}
		
		// Check if it can be learned.
		int previous = 0;
		int cost = 0;
		switch (_skillId)
		{
			case 19538:
			{
				previous = 4;
				cost = 40000;
				break;
			}
			case 19539:
			{
				previous = 9;
				cost = 30000;
				break;
			}
			case 19540:
			{
				previous = 11;
				cost = 50000;
				break;
			}
			case 19541:
			{
				previous = 14;
				cost = 30000;
				break;
			}
			case 19542:
			{
				previous = 16;
				cost = 50000;
				break;
			}
		}
		if (clan.getReputationScore() < cost)
		{
			player.sendMessage("Your clan reputation is lower than the requirement.");
			return;
		}
		if (!clan.hasMastery(previous))
		{
			player.sendMessage("You need to learn the previous mastery.");
			return;
		}
		
		// Check if already enabled.
		if (clan.getMasterySkillRemainingTime(_skillId) > 0)
		{
			clan.removeMasterySkill(_skillId);
			return;
		}
		
		// Learn.
		clan.takeReputationScore(cost, true);
		clan.addMasterySkill(_skillId);
		player.sendPacket(new ExPledgeSkillInfo(_skillId, 1, 1296000, 2));
	}
}