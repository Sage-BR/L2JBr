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

import java.util.Set;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.skills.AbnormalVisualEffect;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author Sdw
 */
public class NpcInfoAbnormalVisualEffect implements IClientOutgoingPacket
{
	private final Npc _npc;
	
	public NpcInfoAbnormalVisualEffect(Npc npc)
	{
		_npc = npc;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.NPC_INFO_ABNORMAL_VISUAL_EFFECT.writeId(packet);
		
		packet.writeD(_npc.getObjectId());
		packet.writeD(_npc.getTransformationDisplayId());
		
		final Set<AbnormalVisualEffect> abnormalVisualEffects = _npc.getEffectList().getCurrentAbnormalVisualEffects();
		packet.writeD(abnormalVisualEffects.size());
		for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects)
		{
			packet.writeH(abnormalVisualEffect.getClientId());
		}
		return true;
	}
}
