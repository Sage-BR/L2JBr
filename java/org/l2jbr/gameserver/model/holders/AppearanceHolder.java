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
package org.l2jbr.gameserver.model.holders;

import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.items.appearance.AppearanceHandType;
import org.l2jbr.gameserver.model.items.appearance.AppearanceMagicType;
import org.l2jbr.gameserver.model.items.appearance.AppearanceTargetType;
import org.l2jbr.gameserver.model.items.type.ArmorType;
import org.l2jbr.gameserver.model.items.type.WeaponType;

/**
 * @author Sdw
 */
public class AppearanceHolder
{
	private final int _visualId;
	private final WeaponType _weaponType;
	private final ArmorType _armorType;
	private final AppearanceHandType _handType;
	private final AppearanceMagicType _magicType;
	private final AppearanceTargetType _targetType;
	private final long _bodyPart;
	
	public AppearanceHolder(StatsSet set)
	{
		_visualId = set.getInt("id", 0);
		_weaponType = set.getEnum("weaponType", WeaponType.class, WeaponType.NONE);
		_armorType = set.getEnum("armorType", ArmorType.class, ArmorType.NONE);
		_handType = set.getEnum("handType", AppearanceHandType.class, AppearanceHandType.NONE);
		_magicType = set.getEnum("magicType", AppearanceMagicType.class, AppearanceMagicType.NONE);
		_targetType = set.getEnum("targetType", AppearanceTargetType.class, AppearanceTargetType.NONE);
		_bodyPart = ItemTable.SLOTS.get(set.getString("bodyPart", "none"));
	}
	
	public WeaponType getWeaponType()
	{
		return _weaponType;
	}
	
	public ArmorType getArmorType()
	{
		return _armorType;
	}
	
	public AppearanceHandType getHandType()
	{
		return _handType;
	}
	
	public AppearanceMagicType getMagicType()
	{
		return _magicType;
	}
	
	public AppearanceTargetType getTargetType()
	{
		return _targetType;
	}
	
	public long getBodyPart()
	{
		return _bodyPart;
	}
	
	public int getVisualId()
	{
		return _visualId;
	}
}
