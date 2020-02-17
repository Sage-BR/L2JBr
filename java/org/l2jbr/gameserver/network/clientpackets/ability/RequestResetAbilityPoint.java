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
package org.l2jbr.gameserver.network.clientpackets.ability;

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.data.xml.impl.SkillTreesData;
import org.l2jbr.gameserver.enums.PrivateStoreType;
import org.l2jbr.gameserver.model.SkillLearn;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.ability.ExAcquireAPSkillList;

/**
 * @author UnAfraid
 */
public class RequestResetAbilityPoint implements IClientIncomingPacket
{
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
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
		
		if (player.isSubClassActive() && !player.isDualClassActive())
		{
			return;
		}
		
		if ((player.getPrivateStoreType() != PrivateStoreType.NONE) || (player.getActiveRequester() != null))
		{
			return;
		}
		else if (player.getLevel() < 85)
		{
			client.sendPacket(SystemMessageId.REACH_LEVEL_85_TO_USE_THE_ABILITY);
			return;
		}
		else if (player.isInOlympiadMode() || player.isOnEvent(CeremonyOfChaosEvent.class))
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_USE_OR_RESET_ABILITY_POINTS_WHILE_PARTICIPATING_IN_THE_OLYMPIAD_OR_CEREMONY_OF_CHAOS);
			return;
		}
		else if (player.isOnEvent()) // custom event message
		{
			player.sendMessage("You cannot use or reset Ability Points while participating in an event.");
			return;
		}
		else if (player.getAbilityPoints() == 0)
		{
			player.sendMessage("You don't have ability points to reset!");
			return;
		}
		else if (player.getAbilityPointsUsed() == 0)
		{
			player.sendMessage("You haven't used your ability points yet!");
			return;
		}
		else if (player.getSp() < Config.ABILITY_POINTS_RESET_SP)
		{
			client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SP_FOR_THIS);
			return;
		}
		player.setSp(player.getSp() - Config.ABILITY_POINTS_RESET_SP);
		
		for (SkillLearn sk : SkillTreesData.getInstance().getAbilitySkillTree().values())
		{
			final Skill skill = player.getKnownSkill(sk.getSkillId());
			if (skill != null)
			{
				player.removeSkill(skill);
			}
		}
		player.setAbilityPointsUsed(0);
		player.sendPacket(new ExAcquireAPSkillList(player));
		player.broadcastUserInfo();
	}
}
