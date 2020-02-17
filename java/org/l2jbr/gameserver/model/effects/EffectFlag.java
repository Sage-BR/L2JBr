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
package org.l2jbr.gameserver.model.effects;

/**
 * @author UnAfraid
 */
public enum EffectFlag
{
	NONE,
	RESURRECTION_SPECIAL,
	NOBLESS_BLESSING,
	SILENT_MOVE,
	PROTECTION_BLESSING,
	RELAXING,
	BLOCK_CONTROL,
	CONFUSED,
	MUTED,
	PSYCHICAL_MUTED,
	PSYCHICAL_ATTACK_MUTED,
	PASSIVE,
	DISARMED,
	ROOTED,
	BLOCK_ACTIONS,
	CONDITIONAL_BLOCK_ACTIONS,
	BETRAYED,
	HP_BLOCK,
	MP_BLOCK,
	BUFF_BLOCK,
	DEBUFF_BLOCK,
	ABNORMAL_SHIELD,
	BLOCK_RESURRECTION,
	UNTARGETABLE,
	CANNOT_ESCAPE,
	DOUBLE_CAST,
	ATTACK_BEHIND,
	TARGETING_DISABLED,
	FACEOFF,
	PHYSICAL_SHIELD_ANGLE_ALL,
	CHEAPSHOT,
	IGNORE_DEATH,
	HPCPHEAL_CRITICAL,
	PROTECT_DEATH_PENALTY,
	CHAT_BLOCK,
	FAKE_DEATH,
	DUELIST_FURY,
	FEAR;
	
	public long getMask()
	{
		return 1L << ordinal();
	}
}
