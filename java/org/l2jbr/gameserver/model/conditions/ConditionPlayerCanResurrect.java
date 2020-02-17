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
package org.l2jbr.gameserver.model.conditions;

import org.l2jbr.gameserver.instancemanager.SiegeManager;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Summon;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.entity.Siege;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * Player Can Resurrect condition implementation.
 * @author UnAfraid
 */
public class ConditionPlayerCanResurrect extends Condition
{
	private final boolean _val;
	
	public ConditionPlayerCanResurrect(boolean val)
	{
		_val = val;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, Item item)
	{
		// Need skill rework for fix that properly
		if (skill.getAffectRange() > 0)
		{
			return true;
		}
		if (effected == null)
		{
			return false;
		}
		boolean canResurrect = true;
		
		if (effected.isPlayer())
		{
			final PlayerInstance player = effected.getActingPlayer();
			if (!player.isDead())
			{
				canResurrect = false;
				if (effector.isPlayer())
				{
					final SystemMessage msg = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
					msg.addSkillName(skill);
					effector.sendPacket(msg);
				}
			}
			else if (player.isResurrectionBlocked())
			{
				canResurrect = false;
				if (effector.isPlayer())
				{
					effector.sendPacket(SystemMessageId.REJECT_RESURRECTION);
				}
			}
			else if (player.isReviveRequested())
			{
				canResurrect = false;
				if (effector.isPlayer())
				{
					effector.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
				}
			}
			else if (skill.getId() != 2393) // Blessed Scroll of Battlefield Resurrection
			{
				final Siege siege = SiegeManager.getInstance().getSiege(player);
				if ((siege != null) && siege.isInProgress())
				{
					final Clan clan = player.getClan();
					if (clan == null)
					{
						canResurrect = false;
						if (effector.isPlayer())
						{
							effector.sendPacket(SystemMessageId.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEGROUNDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
						}
					}
					else if (siege.checkIsDefender(clan) && (siege.getControlTowerCount() == 0))
					{
						canResurrect = false;
						if (effector.isPlayer())
						{
							effector.sendPacket(SystemMessageId.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
						}
					}
					else if (siege.checkIsAttacker(clan) && (siege.getAttackerClan(clan).getNumFlags() == 0))
					{
						canResurrect = false;
						if (effector.isPlayer())
						{
							effector.sendPacket(SystemMessageId.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
						}
					}
					else
					{
						canResurrect = false;
						if (effector.isPlayer())
						{
							effector.sendPacket(SystemMessageId.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEGROUNDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
						}
					}
				}
			}
		}
		else if (effected.isSummon())
		{
			final Summon summon = (Summon) effected;
			final PlayerInstance player = summon.getOwner();
			if (!summon.isDead())
			{
				canResurrect = false;
				if (effector.isPlayer())
				{
					final SystemMessage msg = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
					msg.addSkillName(skill);
					effector.sendPacket(msg);
				}
			}
			else if (summon.isResurrectionBlocked())
			{
				canResurrect = false;
				if (effector.isPlayer())
				{
					effector.sendPacket(SystemMessageId.REJECT_RESURRECTION);
				}
			}
			else if ((player != null) && player.isRevivingPet())
			{
				canResurrect = false;
				if (effector.isPlayer())
				{
					effector.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
				}
			}
		}
		return _val == canResurrect;
	}
}
