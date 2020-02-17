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
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.skills.BuffInfo;
import org.l2jbr.gameserver.network.OutgoingPackets;

public class ExAbnormalStatusUpdateFromTarget implements IClientOutgoingPacket
{
	private final Creature _creature;
	private final List<BuffInfo> _effects;
	
	public ExAbnormalStatusUpdateFromTarget(Creature creature)
	{
		//@formatter:off
		_creature = creature;
		_effects = creature.getEffectList().getEffects()
					.stream()
					.filter(Objects::nonNull)
					.filter(BuffInfo::isInUse)
					.filter(b -> !b.getSkill().isToggle())
					.collect(Collectors.toList());
		//@formatter:on
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_ABNORMAL_STATUS_UPDATE_FROM_TARGET.writeId(packet);
		
		packet.writeD(_creature.getObjectId());
		packet.writeH(_effects.size());
		
		for (BuffInfo info : _effects)
		{
			packet.writeD(info.getSkill().getDisplayId());
			packet.writeH(info.getSkill().getDisplayLevel());
			packet.writeH(info.getSkill().getSubLevel());
			packet.writeH(info.getSkill().getAbnormalType().getClientId());
			writeOptionalD(packet, info.getSkill().isAura() ? -1 : info.getTime());
			packet.writeD(info.getEffectorObjectId());
		}
		return true;
	}
}
