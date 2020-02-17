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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.data.xml.impl.SkillTreesData;
import org.l2jbr.gameserver.model.SkillLearn;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author Sdw
 */
public class AcquireSkillList implements IClientOutgoingPacket
{
	private PlayerInstance _player;
	private List<SkillLearn> _learnable;
	
	public AcquireSkillList(PlayerInstance player)
	{
		if (!player.isSubclassLocked()) // Changing class.
		{
			_player = player;
			_learnable = SkillTreesData.getInstance().getAvailableSkills(player, player.getClassId(), false, true, false);
			_learnable.addAll(SkillTreesData.getInstance().getNextAvailableSkills(player, player.getClassId(), false, true, false));
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if (_player == null)
		{
			return false;
		}
		
		OutgoingPackets.ACQUIRE_SKILL_LIST.writeId(packet);
		
		packet.writeH(_learnable.size());
		for (SkillLearn skill : _learnable)
		{
			packet.writeD(skill.getSkillId());
			packet.writeD(skill.getSkillLevel());
			packet.writeQ(skill.getLevelUpSp());
			packet.writeC(skill.getGetLevel());
			packet.writeH(skill.getDualClassLevel()); // Salvation: Changed from byte to short.
			packet.writeC(skill.getRequiredItems().size());
			for (ItemHolder item : skill.getRequiredItems())
			{
				packet.writeD(item.getId());
				packet.writeQ(item.getCount());
			}
			
			final List<Skill> skillRem = skill.getRemoveSkills().stream().map(_player::getKnownSkill).filter(Objects::nonNull).collect(Collectors.toList());
			
			packet.writeC(skillRem.size());
			for (Skill skillRemove : skillRem)
			{
				packet.writeD(skillRemove.getId());
				packet.writeD(skillRemove.getLevel());
			}
		}
		return true;
	}
}
