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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.data.xml.impl.SkillTreesData;
import org.l2jbr.gameserver.model.SkillLearn;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;
import org.l2jbr.gameserver.network.serverpackets.ability.ExAcquireAPSkillList;

/**
 * @author UnAfraid
 */
public class RequestAcquireAbilityList implements IClientIncomingPacket
{
	private static final int TREE_SIZE = 3;
	private final Map<Integer, SkillHolder> _skills = new LinkedHashMap<>();
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		packet.readD(); // Total size
		for (int i = 0; i < TREE_SIZE; i++)
		{
			final int size = packet.readD();
			for (int j = 0; j < size; j++)
			{
				final SkillHolder holder = new SkillHolder(packet.readD(), packet.readD());
				if (holder.getSkillLevel() < 1)
				{
					LOGGER.warning("Player " + client + " is trying to learn skill " + holder + " by sending packet with level 0!");
					return false;
				}
				if (_skills.putIfAbsent(holder.getSkillId(), holder) != null)
				{
					LOGGER.warning("Player " + client + " is trying to send two times one skill " + holder + " to learn!");
					return false;
				}
			}
		}
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
		
		if ((player.getAbilityPoints() == 0) || (player.getAbilityPoints() == player.getAbilityPointsUsed()))
		{
			LOGGER.warning("Player " + player + " is trying to learn ability without ability points!");
			return;
		}
		
		if (player.getLevel() < 85)
		{
			player.sendPacket(SystemMessageId.REACH_LEVEL_85_TO_USE_THE_ABILITY);
			return;
		}
		else if (player.isInOlympiadMode() || player.isOnEvent(CeremonyOfChaosEvent.class))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_OR_RESET_ABILITY_POINTS_WHILE_PARTICIPATING_IN_THE_OLYMPIAD_OR_CEREMONY_OF_CHAOS);
			return;
		}
		else if (player.isOnEvent()) // custom event message
		{
			player.sendMessage("You cannot use or reset Ability Points while participating in an event.");
			return;
		}
		
		final int[] pointsSpent = new int[TREE_SIZE];
		Arrays.fill(pointsSpent, 0);
		
		final List<SkillLearn> skillsToLearn = new ArrayList<>(_skills.size());
		for (SkillHolder holder : _skills.values())
		{
			final SkillLearn learn = SkillTreesData.getInstance().getAbilitySkill(holder.getSkillId(), holder.getSkillLevel());
			if (learn == null)
			{
				LOGGER.warning("SkillLearn " + holder.getSkillId() + " (" + holder.getSkillLevel() + ") not found!");
				client.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
			
			final Skill skill = holder.getSkill();
			if (skill == null)
			{
				LOGGER.warning("Skill " + holder.getSkillId() + " (" + holder.getSkillLevel() + ") not found!");
				client.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
			
			if (player.getSkillLevel(skill.getId()) > 0)
			{
				pointsSpent[learn.getTreeId() - 1] += skill.getLevel();
			}
			
			skillsToLearn.add(learn);
		}
		
		// Sort the skills by their tree id -> row -> column
		skillsToLearn.sort(Comparator.comparingInt(SkillLearn::getTreeId).thenComparing(SkillLearn::getRow).thenComparing(SkillLearn::getColumn));
		
		for (SkillLearn learn : skillsToLearn)
		{
			final Skill skill = SkillData.getInstance().getSkill(learn.getSkillId(), learn.getSkillLevel());
			final int points;
			final int knownLevel = player.getSkillLevel(skill.getId());
			if (knownLevel == 0) // player didn't knew it at all!
			{
				points = learn.getSkillLevel();
			}
			else
			{
				points = learn.getSkillLevel() - knownLevel;
			}
			
			// Case 1: Learning skill without having X points spent on the specific tree
			if (learn.getPointsRequired() > pointsSpent[learn.getTreeId() - 1])
			{
				LOGGER.warning("Player " + player + " is trying to learn " + skill + " without enough ability points spent!");
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			// Case 2: Learning skill without having its parent
			for (SkillHolder required : learn.getPreReqSkills())
			{
				if (player.getSkillLevel(required.getSkillId()) < required.getSkillLevel())
				{
					LOGGER.warning("Player " + player + " is trying to learn " + skill + " without having prerequsite skill: " + required.getSkill() + "!");
					client.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
			
			// Case 3 Learning a skill without having enough points
			if ((player.getAbilityPoints() - player.getAbilityPointsUsed()) < points)
			{
				LOGGER.warning("Player " + player + " is trying to learn ability without ability points!");
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			pointsSpent[learn.getTreeId() - 1] += points;
			
			player.addSkill(skill, true);
			player.setAbilityPointsUsed(player.getAbilityPointsUsed() + points);
		}
		player.sendPacket(new ExAcquireAPSkillList(player));
		player.broadcastUserInfo();
	}
}