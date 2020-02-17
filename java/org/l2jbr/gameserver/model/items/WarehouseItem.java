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
package org.l2jbr.gameserver.model.items;

import java.util.Collection;
import java.util.Objects;

import org.l2jbr.gameserver.enums.AttributeType;
import org.l2jbr.gameserver.model.VariationInstance;
import org.l2jbr.gameserver.model.ensoul.EnsoulOption;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.items.type.CrystalType;
import org.l2jbr.gameserver.model.items.type.ItemType;

/**
 * This class contains ItemInstance<BR>
 * Use to sort ItemInstance of :
 * <ul>
 * <li>Armor</li>
 * <li>EtcItem</li>
 * <li>Weapon</li>
 * </ul>
 * @version $Revision: 1.7.2.2.2.5 $ $Date: 2005/04/06 18:25:18 $
 */
public class WarehouseItem
{
	private final Item _item;
	private final int _object;
	private final long _count;
	private final int _owner;
	private final int _locationSlot;
	private final int _enchant;
	private final CrystalType _grade;
	private final VariationInstance _augmentation;
	private final int _customType1;
	private final int _customType2;
	private final int _mana;
	
	private byte _elemAtkType = -2;
	private int _elemAtkPower = 0;
	
	private final int[] _elemDefAttr =
	{
		0,
		0,
		0,
		0,
		0,
		0
	};
	
	private final int[] _enchantOptions;
	private final Collection<EnsoulOption> _soulCrystalOptions;
	private final Collection<EnsoulOption> _soulCrystalSpecialOptions;
	
	private final int _time;
	
	public WarehouseItem(ItemInstance item)
	{
		Objects.requireNonNull(item);
		_item = item.getItem();
		_object = item.getObjectId();
		_count = item.getCount();
		_owner = item.getOwnerId();
		_locationSlot = item.getLocationSlot();
		_enchant = item.getEnchantLevel();
		_customType1 = item.getCustomType1();
		_customType2 = item.getCustomType2();
		_grade = item.getItem().getCrystalType();
		_augmentation = item.getAugmentation();
		_mana = item.getMana();
		_time = item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) : -1;
		
		_elemAtkType = item.getAttackAttributeType().getClientId();
		_elemAtkPower = item.getAttackAttributePower();
		for (AttributeType type : AttributeType.ATTRIBUTE_TYPES)
		{
			_elemDefAttr[type.getClientId()] = item.getDefenceAttribute(type);
		}
		_enchantOptions = item.getEnchantOptions();
		_soulCrystalOptions = item.getSpecialAbilities();
		_soulCrystalSpecialOptions = item.getAdditionalSpecialAbilities();
	}
	
	/**
	 * @return the item.
	 */
	public Item getItem()
	{
		return _item;
	}
	
	/**
	 * @return the unique objectId.
	 */
	public int getObjectId()
	{
		return _object;
	}
	
	/**
	 * @return the owner.
	 */
	public int getOwnerId()
	{
		return _owner;
	}
	
	/**
	 * @return the location slot.
	 */
	public int getLocationSlot()
	{
		return _locationSlot;
	}
	
	/**
	 * @return the count.
	 */
	public long getCount()
	{
		return _count;
	}
	
	/**
	 * @return the first type.
	 */
	public int getType1()
	{
		return _item.getType1();
	}
	
	/**
	 * @return the second type.
	 */
	public int getType2()
	{
		return _item.getType2();
	}
	
	/**
	 * @return the second type.
	 */
	public ItemType getItemType()
	{
		return _item.getItemType();
	}
	
	/**
	 * @return the ItemId.
	 */
	public int getItemId()
	{
		return _item.getId();
	}
	
	/**
	 * @return the part of body used with this item.
	 */
	public long getBodyPart()
	{
		return _item.getBodyPart();
	}
	
	/**
	 * @return the enchant level.
	 */
	public int getEnchantLevel()
	{
		return _enchant;
	}
	
	/**
	 * @return the item grade
	 */
	public CrystalType getItemGrade()
	{
		return _grade;
	}
	
	/**
	 * @return {@code true} if the item is a weapon, {@code false} otherwise.
	 */
	public boolean isWeapon()
	{
		return (_item instanceof Weapon);
	}
	
	/**
	 * @return {@code true} if the item is an armor, {@code false} otherwise.
	 */
	public boolean isArmor()
	{
		return (_item instanceof Armor);
	}
	
	/**
	 * @return {@code true} if the item is an etc item, {@code false} otherwise.
	 */
	public boolean isEtcItem()
	{
		return (_item instanceof EtcItem);
	}
	
	/**
	 * @return the name of the item
	 */
	public String getItemName()
	{
		return _item.getName();
	}
	
	/**
	 * @return the augmentation If.
	 */
	public VariationInstance getAugmentation()
	{
		return _augmentation;
	}
	
	/**
	 * @return the name of the item
	 */
	public String getName()
	{
		return _item.getName();
	}
	
	public int getCustomType1()
	{
		return _customType1;
	}
	
	public int getCustomType2()
	{
		return _customType2;
	}
	
	public int getMana()
	{
		return _mana;
	}
	
	public byte getAttackElementType()
	{
		return _elemAtkType;
	}
	
	public int getAttackElementPower()
	{
		return _elemAtkPower;
	}
	
	public int getElementDefAttr(byte i)
	{
		return _elemDefAttr[i];
	}
	
	public int[] getEnchantOptions()
	{
		return _enchantOptions;
	}
	
	public Collection<EnsoulOption> getSoulCrystalOptions()
	{
		return _soulCrystalOptions;
	}
	
	public Collection<EnsoulOption> getSoulCrystalSpecialOptions()
	{
		return _soulCrystalSpecialOptions;
	}
	
	public int getTime()
	{
		return _time;
	}
	
	/**
	 * @return the name of the item
	 */
	@Override
	public String toString()
	{
		return _item.toString();
	}
}
