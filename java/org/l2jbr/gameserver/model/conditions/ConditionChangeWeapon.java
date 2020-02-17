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

import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.Weapon;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * The Class ConditionChangeWeapon.
 * @author nBd
 */
public class ConditionChangeWeapon extends Condition
{
	private final boolean _required;
	
	/**
	 * Instantiates a new condition change weapon.
	 * @param required the required
	 */
	public ConditionChangeWeapon(boolean required)
	{
		_required = required;
	}
	
	/**
	 * Test impl.
	 * @return true, if successful
	 */
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effector.getActingPlayer() == null)
		{
			return false;
		}
		
		if (_required)
		{
			final Weapon weaponItem = effector.getActiveWeaponItem();
			if (weaponItem == null)
			{
				return false;
			}
			
			if (weaponItem.getChangeWeaponId() == 0)
			{
				return false;
			}
			
			if (effector.getActingPlayer().hasItemRequest())
			{
				return false;
			}
		}
		return true;
	}
}
