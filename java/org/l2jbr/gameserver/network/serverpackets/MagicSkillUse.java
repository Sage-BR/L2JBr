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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.interfaces.IPositionable;
import org.l2jbr.gameserver.model.skills.SkillCastingType;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * MagicSkillUse server packet implementation.
 * @author UnAfraid, NosBit
 */
public class MagicSkillUse implements IClientOutgoingPacket
{
	private final int _skillId;
	private final int _skillLevel;
	private final int _hitTime;
	private final int _reuseGroup;
	private final int _reuseDelay;
	private final int _actionId; // If skill is called from RequestActionUse, use that ID.
	private final SkillCastingType _castingType; // Defines which client bar is going to use.
	private final Creature _creature;
	private final WorldObject _target;
	private final List<Integer> _unknown = Collections.emptyList();
	private final List<Location> _groundLocations;
	
	public MagicSkillUse(Creature creature, WorldObject target, int skillId, int skillLevel, int hitTime, int reuseDelay, int reuseGroup, int actionId, SkillCastingType castingType)
	{
		_creature = creature;
		_target = target;
		_skillId = skillId;
		_skillLevel = skillLevel;
		_hitTime = hitTime;
		_reuseGroup = reuseGroup;
		_reuseDelay = reuseDelay;
		_actionId = actionId;
		_castingType = castingType;
		Location skillWorldPos = null;
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			if (player.getCurrentSkillWorldPosition() != null)
			{
				skillWorldPos = player.getCurrentSkillWorldPosition();
			}
		}
		_groundLocations = skillWorldPos != null ? Arrays.asList(skillWorldPos) : Collections.emptyList();
	}
	
	public MagicSkillUse(Creature creature, WorldObject target, int skillId, int skillLevel, int hitTime, int reuseDelay)
	{
		this(creature, target, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
	}
	
	public MagicSkillUse(Creature creature, int skillId, int skillLevel, int hitTime, int reuseDelay)
	{
		this(creature, creature, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.MAGIC_SKILL_USE.writeId(packet);
		
		packet.writeD(_castingType.getClientBarId()); // Casting bar type: 0 - default, 1 - default up, 2 - blue, 3 - green, 4 - red.
		packet.writeD(_creature.getObjectId());
		packet.writeD(_target.getObjectId());
		packet.writeD(_skillId);
		packet.writeD(_skillLevel);
		packet.writeD(_hitTime);
		packet.writeD(_reuseGroup);
		packet.writeD(_reuseDelay);
		packet.writeD(_creature.getX());
		packet.writeD(_creature.getY());
		packet.writeD(_creature.getZ());
		packet.writeH(_unknown.size()); // TODO: Implement me!
		for (int unknown : _unknown)
		{
			packet.writeH(unknown);
		}
		packet.writeH(_groundLocations.size());
		for (IPositionable target : _groundLocations)
		{
			packet.writeD(target.getX());
			packet.writeD(target.getY());
			packet.writeD(target.getZ());
		}
		packet.writeD(_target.getX());
		packet.writeD(_target.getY());
		packet.writeD(_target.getZ());
		packet.writeD(_actionId >= 0 ? 0x01 : 0x00); // 1 when ID from RequestActionUse is used
		packet.writeD(_actionId >= 0 ? _actionId : 0); // ID from RequestActionUse. Used to set cooldown on summon skills.
		return true;
	}
}
