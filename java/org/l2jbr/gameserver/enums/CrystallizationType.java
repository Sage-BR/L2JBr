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
package org.l2jbr.gameserver.enums;

import org.l2jbr.gameserver.model.items.Armor;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.Weapon;

/**
 * @author Nik
 */
public enum CrystallizationType
{
	NONE,
	WEAPON,
	ARMOR,
	ACCESORY;
	
	public static CrystallizationType getByItem(Item item)
	{
		if (item instanceof Weapon)
		{
			return WEAPON;
		}
		if (item instanceof Armor)
		{
			return ARMOR;
		}
		if ((item.getBodyPart() == Item.SLOT_R_EAR) //
			|| (item.getBodyPart() == Item.SLOT_L_EAR) //
			|| (item.getBodyPart() == Item.SLOT_R_FINGER) //
			|| (item.getBodyPart() == Item.SLOT_L_FINGER) //
			|| (item.getBodyPart() == Item.SLOT_NECK) //
			|| (item.getBodyPart() == Item.SLOT_HAIR) //
			|| (item.getBodyPart() == Item.SLOT_HAIR2) //
			|| (item.getBodyPart() == Item.SLOT_HAIRALL) //
			|| (item.getBodyPart() == Item.SLOT_ARTIFACT_BOOK) //
			|| (item.getBodyPart() == Item.SLOT_ARTIFACT))
		{
			return ACCESORY;
		}
		
		return NONE;
	}
}
