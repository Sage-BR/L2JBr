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
package org.l2jbr.gameserver.network.serverpackets.ability;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.data.xml.impl.SkillTreesData;
import org.l2jbr.gameserver.model.SkillLearn;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Sdw
 */
public class ExAcquireAPSkillList implements IClientOutgoingPacket
{
	private final int _abilityPoints;
	private final int _usedAbilityPoints;
	// private final long _price; Removed on Grand Crusade
	private final boolean _enable;
	private final List<Skill> _skills = new ArrayList<>();
	
	public ExAcquireAPSkillList(PlayerInstance player)
	{
		_abilityPoints = player.getAbilityPoints();
		_usedAbilityPoints = player.getAbilityPointsUsed();
		// _price = AbilityPointsData.getInstance().getPrice(_abilityPoints); Removed on Grand Crusade
		for (SkillLearn sk : SkillTreesData.getInstance().getAbilitySkillTree().values())
		{
			final Skill knownSkill = player.getKnownSkill(sk.getSkillId());
			if (knownSkill != null)
			{
				if (knownSkill.getLevel() == sk.getSkillLevel())
				{
					_skills.add(knownSkill);
				}
			}
		}
		_enable = (!player.isSubClassActive() || player.isDualClassActive()) && (player.getLevel() >= 85);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_ACQUIRE_AP_SKILL_LIST.writeId(packet);
		
		packet.writeD(_enable ? 1 : 0);
		packet.writeQ(Config.ABILITY_POINTS_RESET_SP); // Changed to from Adena to SP on Grand Crusade
		// packet.writeQ(_price); Removed on Grand Crusade
		// packet.writeD(Config.ABILITY_MAX_POINTS); Removed on Grand Crusade
		packet.writeD(_abilityPoints);
		packet.writeD(_usedAbilityPoints);
		packet.writeD(_skills.size());
		for (Skill skill : _skills)
		{
			packet.writeD(skill.getId());
			packet.writeD(skill.getLevel());
		}
		return true;
	}
}