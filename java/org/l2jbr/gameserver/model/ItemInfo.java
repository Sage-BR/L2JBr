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
package org.l2jbr.gameserver.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.l2jbr.gameserver.enums.AttributeType;
import org.l2jbr.gameserver.model.buylist.Product;
import org.l2jbr.gameserver.model.ensoul.EnsoulOption;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.WarehouseItem;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;

/**
 * Get all information from ItemInstance to generate ItemInfo.
 */
public class ItemInfo
{
	/** Identifier of the ItemInstance */
	private int _objectId;
	
	/** The Item template of the ItemInstance */
	private Item _item;
	
	/** The level of enchant on the ItemInstance */
	private int _enchantLevel;
	
	/** The augmentation of the item */
	private VariationInstance _augmentation;
	
	/** The quantity of ItemInstance */
	private long _count;
	
	/** The price of the ItemInstance */
	private int _price;
	
	/** The custom ItemInstance types (used loto, race tickets) */
	private int _type1;
	private int _type2;
	
	/** If True the ItemInstance is equipped */
	private int _equipped;
	
	/** The action to do clientside (1=ADD, 2=MODIFY, 3=REMOVE) */
	private int _change;
	
	/** The mana of this item */
	private int _mana;
	private int _time;
	
	private boolean _available = true;
	
	private int _location;
	
	private byte _elemAtkType = -2;
	private int _elemAtkPower = 0;
	private final int[] _attributeDefence =
	{
		0,
		0,
		0,
		0,
		0,
		0
	};
	
	private int[] _option;
	private Collection<EnsoulOption> _soulCrystalOptions;
	private Collection<EnsoulOption> _soulCrystalSpecialOptions;
	private int _visualId;
	private long _visualExpiration;
	
	/**
	 * Get all information from ItemInstance to generate ItemInfo.
	 * @param item
	 */
	public ItemInfo(ItemInstance item)
	{
		Objects.requireNonNull(item);
		
		// Get the Identifier of the ItemInstance
		_objectId = item.getObjectId();
		
		// Get the Item of the ItemInstance
		_item = item.getItem();
		
		// Get the enchant level of the ItemInstance
		_enchantLevel = item.getEnchantLevel();
		
		// Get the augmentation bonus
		_augmentation = item.getAugmentation();
		
		// Get the quantity of the ItemInstance
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		
		// Verify if the ItemInstance is equipped
		_equipped = item.isEquipped() ? 1 : 0;
		
		// Get the action to do clientside
		switch (item.getLastChange())
		{
			case ItemInstance.ADDED:
			{
				_change = 1;
				break;
			}
			case ItemInstance.MODIFIED:
			{
				_change = 2;
				break;
			}
			case ItemInstance.REMOVED:
			{
				_change = 3;
				break;
			}
		}
		
		// Get shadow item mana
		_mana = item.getMana();
		_time = item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) : -9999;
		_available = item.isAvailable();
		_location = item.getLocationSlot();
		
		_elemAtkType = item.getAttackAttributeType().getClientId();
		_elemAtkPower = item.getAttackAttributePower();
		for (AttributeType type : AttributeType.ATTRIBUTE_TYPES)
		{
			_attributeDefence[type.getClientId()] = item.getDefenceAttribute(type);
		}
		_option = item.getEnchantOptions();
		_soulCrystalOptions = item.getSpecialAbilities();
		_soulCrystalSpecialOptions = item.getAdditionalSpecialAbilities();
		_visualId = item.getVisualId();
		_visualExpiration = item.getVisualLifeTime() > 0 ? (item.getVisualLifeTime() - System.currentTimeMillis()) / 1000 : 0;
	}
	
	public ItemInfo(ItemInstance item, int change)
	{
		this(item);
		_change = change;
		_visualExpiration = item.getVisualLifeTime() > 0 ? (item.getVisualLifeTime() - System.currentTimeMillis()) / 1000 : 0;
	}
	
	public ItemInfo(TradeItem item)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the ItemInstance
		_objectId = item.getObjectId();
		
		// Get the Item of the ItemInstance
		_item = item.getItem();
		
		// Get the enchant level of the ItemInstance
		_enchantLevel = item.getEnchant();
		
		// Get the augmentation bonus
		if ((item.getAugmentationOption1() >= 0) && (item.getAugmentationOption2() >= 0))
		{
			_augmentation = new VariationInstance(0, item.getAugmentationOption1(), item.getAugmentationOption2());
		}
		
		// Get the quantity of the ItemInstance
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		
		// Verify if the ItemInstance is equipped
		_equipped = 0;
		
		// Get the action to do clientside
		_change = 0;
		
		// Get shadow item mana
		_mana = -1;
		_time = -9999;
		
		_location = item.getLocationSlot();
		
		_elemAtkType = item.getAttackElementType();
		_elemAtkPower = item.getAttackElementPower();
		for (byte i = 0; i < 6; i++)
		{
			_attributeDefence[i] = item.getElementDefAttr(i);
		}
		
		_option = item.getEnchantOptions();
		_soulCrystalOptions = item.getSoulCrystalOptions();
		_soulCrystalSpecialOptions = item.getSoulCrystalSpecialOptions();
		_visualId = item.getVisualId();
	}
	
	public ItemInfo(Product item)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the ItemInstance
		_objectId = 0;
		
		// Get the Item of the ItemInstance
		_item = item.getItem();
		
		// Get the enchant level of the ItemInstance
		_enchantLevel = 0;
		
		// Get the augmentation bonus
		_augmentation = null;
		
		// Get the quantity of the ItemInstance
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getItem().getType1();
		_type2 = item.getItem().getType2();
		
		// Verify if the ItemInstance is equipped
		_equipped = 0;
		
		// Get the action to do clientside
		_change = 0;
		
		// Get shadow item mana
		_mana = -1;
		_time = -9999;
		
		_location = 0;
		
		_soulCrystalOptions = Collections.emptyList();
		_soulCrystalSpecialOptions = Collections.emptyList();
	}
	
	public ItemInfo(WarehouseItem item)
	{
		if (item == null)
		{
			return;
		}
		
		// Get the Identifier of the ItemInstance
		_objectId = item.getObjectId();
		
		// Get the Item of the ItemInstance
		_item = item.getItem();
		
		// Get the enchant level of the ItemInstance
		_enchantLevel = item.getEnchantLevel();
		
		// Get the augmentation bonus
		_augmentation = item.getAugmentation();
		
		// Get the quantity of the ItemInstance
		_count = item.getCount();
		
		// Get custom item types (used loto, race tickets)
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		
		// Verify if the ItemInstance is equipped
		_equipped = 0;
		
		// Get shadow item mana
		_mana = item.getMana();
		_time = item.getTime();
		_location = item.getLocationSlot();
		
		_elemAtkType = item.getAttackElementType();
		_elemAtkPower = item.getAttackElementPower();
		for (byte i = 0; i < 6; i++)
		{
			_attributeDefence[i] = item.getElementDefAttr(i);
		}
		_option = item.getEnchantOptions();
		_soulCrystalOptions = item.getSoulCrystalOptions();
		_soulCrystalSpecialOptions = item.getSoulCrystalSpecialOptions();
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public Item getItem()
	{
		return _item;
	}
	
	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
	
	public VariationInstance getAugmentation()
	{
		return _augmentation;
	}
	
	public long getCount()
	{
		return _count;
	}
	
	public int getPrice()
	{
		return _price;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public int getEquipped()
	{
		return _equipped;
	}
	
	public int getChange()
	{
		return _change;
	}
	
	public int getMana()
	{
		return _mana;
	}
	
	public int getTime()
	{
		return _time > 0 ? _time : _visualExpiration > 0 ? (int) _visualExpiration : -9999;
	}
	
	public boolean isAvailable()
	{
		return _available;
	}
	
	public int getLocation()
	{
		return _location;
	}
	
	public int getAttackElementType()
	{
		return _elemAtkType;
	}
	
	public int getAttackElementPower()
	{
		return _elemAtkPower;
	}
	
	public int getAttributeDefence(AttributeType attribute)
	{
		return _attributeDefence[attribute.getClientId()];
	}
	
	public int[] getEnchantOptions()
	{
		return _option;
	}
	
	public int getVisualId()
	{
		return _visualId;
	}
	
	public Collection<EnsoulOption> getSoulCrystalOptions()
	{
		return _soulCrystalOptions != null ? _soulCrystalOptions : Collections.emptyList();
	}
	
	public Collection<EnsoulOption> getSoulCrystalSpecialOptions()
	{
		return _soulCrystalSpecialOptions != null ? _soulCrystalSpecialOptions : Collections.emptyList();
	}
	
	public long getVisualExpiration()
	{
		return _visualExpiration;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(_item) + "[objId: " + _objectId + ", count: " + _count + "]";
	}
}
