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

import java.util.HashMap;
import java.util.Map;

import org.l2jbr.Config;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.items.type.CrystalType;

public class Elementals
{
	private static final Map<Integer, ElementalItems> TABLE = new HashMap<>();
	
	static
	{
		for (ElementalItems item : ElementalItems.values())
		{
			TABLE.put(item._itemId, item);
		}
	}
	
	protected static final byte NONE = -1;
	protected static final byte FIRE = 0;
	protected static final byte WATER = 1;
	protected static final byte WIND = 2;
	protected static final byte EARTH = 3;
	protected static final byte HOLY = 4;
	protected static final byte DARK = 5;
	
	public static final int FIRST_WEAPON_BONUS = 20;
	public static final int NEXT_WEAPON_BONUS = 5;
	public static final int ARMOR_BONUS = 6;
	
	public static final int[] WEAPON_VALUES =
	{
		0, // Level 1
		25, // Level 2
		75, // Level 3
		150, // Level 4
		175, // Level 5
		225, // Level 6
		300, // Level 7
		325, // Level 8
		375, // Level 9
		450, // Level 10
		475, // Level 11
		525, // Level 12
		600, // Level 13
		Integer.MAX_VALUE
		// TODO: Higher stones
	};
	
	public static final int[] ARMOR_VALUES =
	{
		0, // Level 1
		12, // Level 2
		30, // Level 3
		60, // Level 4
		72, // Level 5
		90, // Level 6
		120, // Level 7
		132, // Level 8
		150, // Level 9
		180, // Level 10
		192, // Level 11
		210, // Level 12
		240, // Level 13
		Integer.MAX_VALUE
		// TODO: Higher stones
	};
	
	/* @formatter:off */
	private static final int[][] CHANCE_TABLE =
	{
		{Config.S_WEAPON_STONE,		Config.S_ARMOR_STONE,		Config.S_WEAPON_CRYSTAL,	Config.S_ARMOR_CRYSTAL,		Config.S_WEAPON_STONE_SUPER,	Config.S_ARMOR_STONE_SUPER,		Config.S_WEAPON_CRYSTAL_SUPER,		Config.S_ARMOR_CRYSTAL_SUPER,		Config.S_WEAPON_JEWEL,		Config.S_ARMOR_JEWEL},
		{Config.S80_WEAPON_STONE,	Config.S80_ARMOR_STONE,		Config.S80_WEAPON_CRYSTAL,	Config.S80_ARMOR_CRYSTAL,	Config.S80_WEAPON_STONE_SUPER,	Config.S80_ARMOR_STONE_SUPER,	Config.S80_WEAPON_CRYSTAL_SUPER,	Config.S80_ARMOR_CRYSTAL_SUPER,		Config.S80_WEAPON_JEWEL,	Config.S80_ARMOR_JEWEL},
		{Config.S84_WEAPON_STONE,	Config.S84_ARMOR_STONE,		Config.S84_WEAPON_CRYSTAL,	Config.S84_ARMOR_CRYSTAL,	Config.S84_WEAPON_STONE_SUPER,	Config.S84_ARMOR_STONE_SUPER,	Config.S84_WEAPON_CRYSTAL_SUPER,	Config.S84_ARMOR_CRYSTAL_SUPER,		Config.S84_WEAPON_JEWEL,	Config.S84_ARMOR_JEWEL},
		{Config.R_WEAPON_STONE,		Config.R_ARMOR_STONE,		Config.R_WEAPON_CRYSTAL,	Config.R_ARMOR_CRYSTAL,		Config.R_WEAPON_STONE_SUPER,	Config.R_ARMOR_STONE_SUPER,		Config.R_WEAPON_CRYSTAL_SUPER,		Config.R_ARMOR_CRYSTAL_SUPER,		Config.R_WEAPON_JEWEL,		Config.R_ARMOR_JEWEL},
		{Config.R95_WEAPON_STONE,	Config.R95_ARMOR_STONE,		Config.R95_WEAPON_CRYSTAL,	Config.R95_ARMOR_CRYSTAL,	Config.R95_WEAPON_STONE_SUPER,	Config.R95_ARMOR_STONE_SUPER,	Config.R95_WEAPON_CRYSTAL_SUPER,	Config.R95_ARMOR_CRYSTAL_SUPER,		Config.R95_WEAPON_JEWEL,	Config.R95_ARMOR_JEWEL},
		{Config.R99_WEAPON_STONE,	Config.R99_ARMOR_STONE,		Config.R99_WEAPON_CRYSTAL,	Config.R99_ARMOR_CRYSTAL,	Config.R99_WEAPON_STONE_SUPER,	Config.R99_ARMOR_STONE_SUPER,	Config.R99_WEAPON_CRYSTAL_SUPER,	Config.R99_ARMOR_CRYSTAL_SUPER,		Config.R99_WEAPON_JEWEL,	Config.R99_ARMOR_JEWEL},
		{Config.R110_WEAPON_STONE,	Config.R110_ARMOR_STONE,	Config.R110_WEAPON_CRYSTAL,	Config.R110_ARMOR_CRYSTAL,	Config.R110_WEAPON_STONE_SUPER,	Config.R110_ARMOR_STONE_SUPER,	Config.R110_WEAPON_CRYSTAL_SUPER,	Config.R110_ARMOR_CRYSTAL_SUPER,	Config.R110_WEAPON_JEWEL,	Config.R110_ARMOR_JEWEL},
	};	
	/* @formatter:on */
	
	public enum ElementalItemType
	{
		Stone(3),
		StoneSuper(3),
		Crystal(6),
		CrystalSuper(6),
		Jewel(9),
		Energy(12);
		
		public int _maxLevel;
		
		ElementalItemType(int maxLvl)
		{
			_maxLevel = maxLvl;
		}
	}
	
	public enum ElementalItems
	{
		attributePracticeFireStone(FIRE, 48169, ElementalItemType.Stone, 0),
		
		fireStone(FIRE, 9546, ElementalItemType.Stone, 0),
		waterStone(WATER, 9547, ElementalItemType.Stone, 0),
		windStone(WIND, 9549, ElementalItemType.Stone, 0),
		earthStone(EARTH, 9548, ElementalItemType.Stone, 0),
		divineStone(HOLY, 9551, ElementalItemType.Stone, 0),
		darkStone(DARK, 9550, ElementalItemType.Stone, 0),
		
		fireRoughtore(FIRE, 10521, ElementalItemType.Stone, 0),
		waterRoughtore(WATER, 10522, ElementalItemType.Stone, 0),
		windRoughtore(WIND, 10524, ElementalItemType.Stone, 0),
		earthRoughtore(EARTH, 10523, ElementalItemType.Stone, 0),
		divineRoughtore(HOLY, 10526, ElementalItemType.Stone, 0),
		darkRoughtore(DARK, 10525, ElementalItemType.Stone, 0),
		
		fireCrystal(FIRE, 9552, ElementalItemType.Crystal, 0),
		waterCrystal(WATER, 9553, ElementalItemType.Crystal, 0),
		windCrystal(WIND, 9555, ElementalItemType.Crystal, 0),
		earthCrystal(EARTH, 9554, ElementalItemType.Crystal, 0),
		divineCrystal(HOLY, 9557, ElementalItemType.Crystal, 0),
		darkCrystal(DARK, 9556, ElementalItemType.Crystal, 0),
		
		// jewels are only for R110 grade
		fireJewel(FIRE, 9558, ElementalItemType.Jewel, 0),
		waterJewel(WATER, 9559, ElementalItemType.Jewel, 0),
		windJewel(WIND, 9561, ElementalItemType.Jewel, 0),
		earthJewel(EARTH, 9560, ElementalItemType.Jewel, 0),
		divineJewel(HOLY, 9563, ElementalItemType.Jewel, 0),
		darkJewel(DARK, 9562, ElementalItemType.Jewel, 0),
		
		fireEnergy(FIRE, 9564, ElementalItemType.Energy, 0),
		waterEnergy(WATER, 9565, ElementalItemType.Energy, 0),
		windEnergy(WIND, 9567, ElementalItemType.Energy, 0),
		earthEnergy(EARTH, 9566, ElementalItemType.Energy, 0),
		divineEnergy(HOLY, 9569, ElementalItemType.Energy, 0),
		darkEnergy(DARK, 9568, ElementalItemType.Energy, 0),
		
		// GoD+ Stones
		GoD_22635(FIRE, 22635, ElementalItemType.Stone, 0),
		GoD_22636(WATER, 22636, ElementalItemType.Stone, 0),
		GoD_22637(EARTH, 22637, ElementalItemType.Stone, 0),
		GoD_22638(WIND, 22638, ElementalItemType.Stone, 0),
		GoD_22639(DARK, 22639, ElementalItemType.Stone, 0),
		GoD_22640(HOLY, 22640, ElementalItemType.Stone, 0),
		GoD_22919(FIRE, 22919, ElementalItemType.Stone, 0),
		GoD_22920(WATER, 22920, ElementalItemType.Stone, 0),
		GoD_22921(EARTH, 22921, ElementalItemType.Stone, 0),
		GoD_22922(WIND, 22922, ElementalItemType.Stone, 0),
		GoD_22923(DARK, 22923, ElementalItemType.Stone, 0),
		GoD_22924(HOLY, 22924, ElementalItemType.Stone, 0),
		GoD_33481(FIRE, 33481, ElementalItemType.StoneSuper, 0),
		GoD_33482(WATER, 33482, ElementalItemType.StoneSuper, 0),
		GoD_33483(EARTH, 33483, ElementalItemType.StoneSuper, 0),
		GoD_33484(WIND, 33484, ElementalItemType.StoneSuper, 0),
		GoD_33485(DARK, 33485, ElementalItemType.StoneSuper, 0),
		GoD_33486(HOLY, 33486, ElementalItemType.StoneSuper, 0),
		GoD_33863(FIRE, 33863, ElementalItemType.StoneSuper, 60),
		GoD_33864(WATER, 33864, ElementalItemType.StoneSuper, 60),
		GoD_33865(EARTH, 33865, ElementalItemType.StoneSuper, 60),
		GoD_33866(WIND, 33866, ElementalItemType.StoneSuper, 60),
		GoD_33867(DARK, 33867, ElementalItemType.StoneSuper, 60),
		GoD_33868(HOLY, 33868, ElementalItemType.StoneSuper, 60),
		GoD_33869(FIRE, 33869, ElementalItemType.StoneSuper, 150),
		GoD_33870(WATER, 33870, ElementalItemType.StoneSuper, 150),
		GoD_33871(EARTH, 33871, ElementalItemType.StoneSuper, 150),
		GoD_33872(WIND, 33872, ElementalItemType.StoneSuper, 150),
		GoD_33873(DARK, 33873, ElementalItemType.StoneSuper, 150),
		GoD_33874(HOLY, 33874, ElementalItemType.StoneSuper, 150),
		GoD_34661(FIRE, 34661, ElementalItemType.StoneSuper, 60),
		GoD_34662(WATER, 34662, ElementalItemType.StoneSuper, 60),
		GoD_34663(EARTH, 34663, ElementalItemType.StoneSuper, 60),
		GoD_34664(WIND, 34664, ElementalItemType.StoneSuper, 60),
		GoD_34665(DARK, 34665, ElementalItemType.StoneSuper, 60),
		GoD_34666(HOLY, 34666, ElementalItemType.StoneSuper, 60),
		GoD_34667(FIRE, 34667, ElementalItemType.StoneSuper, 150),
		GoD_34668(WATER, 34668, ElementalItemType.StoneSuper, 150),
		GoD_34669(EARTH, 34669, ElementalItemType.StoneSuper, 150),
		GoD_34670(WIND, 34670, ElementalItemType.StoneSuper, 150),
		GoD_34671(DARK, 34671, ElementalItemType.StoneSuper, 150),
		GoD_34672(HOLY, 34672, ElementalItemType.StoneSuper, 150),
		GoD_34790(FIRE, 34790, ElementalItemType.Stone, 0),
		GoD_34791(WATER, 34791, ElementalItemType.Stone, 0),
		GoD_34792(EARTH, 34792, ElementalItemType.Stone, 0),
		GoD_34793(WIND, 34793, ElementalItemType.Stone, 0),
		GoD_34794(DARK, 34794, ElementalItemType.Stone, 0),
		GoD_34795(HOLY, 34795, ElementalItemType.Stone, 0),
		GoD_35729(FIRE, 35729, ElementalItemType.StoneSuper, 60),
		GoD_35730(WATER, 35730, ElementalItemType.StoneSuper, 60),
		GoD_35731(EARTH, 35731, ElementalItemType.StoneSuper, 60),
		GoD_35732(WIND, 35732, ElementalItemType.StoneSuper, 60),
		GoD_35733(DARK, 35733, ElementalItemType.StoneSuper, 60),
		GoD_35734(HOLY, 35734, ElementalItemType.StoneSuper, 60),
		GoD_35735(FIRE, 35735, ElementalItemType.StoneSuper, 150),
		GoD_35736(WATER, 35736, ElementalItemType.StoneSuper, 150),
		GoD_35737(EARTH, 35737, ElementalItemType.StoneSuper, 150),
		GoD_35738(WIND, 35738, ElementalItemType.StoneSuper, 150),
		GoD_35739(DARK, 35739, ElementalItemType.StoneSuper, 150),
		GoD_35740(HOLY, 35740, ElementalItemType.StoneSuper, 150),
		GoD_36960(FIRE, 36960, ElementalItemType.StoneSuper, 60),
		GoD_36961(WATER, 36961, ElementalItemType.StoneSuper, 60),
		GoD_36962(EARTH, 36962, ElementalItemType.StoneSuper, 60),
		GoD_36963(WIND, 36963, ElementalItemType.StoneSuper, 60),
		GoD_36964(DARK, 36964, ElementalItemType.StoneSuper, 60),
		GoD_36965(HOLY, 36965, ElementalItemType.StoneSuper, 60),
		GoD_36966(FIRE, 36966, ElementalItemType.StoneSuper, 150),
		GoD_36967(WATER, 36967, ElementalItemType.StoneSuper, 150),
		GoD_36968(EARTH, 36968, ElementalItemType.StoneSuper, 150),
		GoD_36969(WIND, 36969, ElementalItemType.StoneSuper, 150),
		GoD_36970(DARK, 36970, ElementalItemType.StoneSuper, 150),
		GoD_36971(HOLY, 36971, ElementalItemType.StoneSuper, 150),
		GoD_37499(FIRE, 37499, ElementalItemType.Stone, 0),
		GoD_37500(WATER, 37500, ElementalItemType.Stone, 0),
		GoD_37501(EARTH, 37501, ElementalItemType.Stone, 0),
		GoD_37502(WIND, 37502, ElementalItemType.Stone, 0),
		GoD_37503(DARK, 37503, ElementalItemType.Stone, 0),
		GoD_37504(HOLY, 37504, ElementalItemType.Stone, 0),
		
		// GoD+ Crystals
		GoD_22641(FIRE, 22641, ElementalItemType.Crystal, 0),
		GoD_22642(WATER, 22642, ElementalItemType.Crystal, 0),
		GoD_22643(EARTH, 22643, ElementalItemType.Crystal, 0),
		GoD_22644(WIND, 22644, ElementalItemType.Crystal, 0),
		GoD_22645(DARK, 22645, ElementalItemType.Crystal, 0),
		GoD_22646(HOLY, 22646, ElementalItemType.Crystal, 0),
		GoD_22925(FIRE, 22925, ElementalItemType.Crystal, 0),
		GoD_22926(WATER, 22926, ElementalItemType.Crystal, 0),
		GoD_22927(EARTH, 22927, ElementalItemType.Crystal, 0),
		GoD_22928(WIND, 22928, ElementalItemType.Crystal, 0),
		GoD_22929(DARK, 22929, ElementalItemType.Crystal, 0),
		GoD_22930(HOLY, 22930, ElementalItemType.Crystal, 0),
		GoD_33487(FIRE, 33487, ElementalItemType.CrystalSuper, 0),
		GoD_33488(WATER, 33488, ElementalItemType.CrystalSuper, 0),
		GoD_33489(EARTH, 33489, ElementalItemType.CrystalSuper, 0),
		GoD_33490(WIND, 33490, ElementalItemType.CrystalSuper, 0),
		GoD_33491(DARK, 33491, ElementalItemType.CrystalSuper, 0),
		GoD_33492(HOLY, 33492, ElementalItemType.CrystalSuper, 0),
		GoD_34796(FIRE, 34796, ElementalItemType.Crystal, 0),
		GoD_34797(WATER, 34797, ElementalItemType.Crystal, 0),
		GoD_34798(EARTH, 34798, ElementalItemType.Crystal, 0),
		GoD_34799(WIND, 34799, ElementalItemType.Crystal, 0),
		GoD_34800(DARK, 34800, ElementalItemType.Crystal, 0),
		GoD_34801(HOLY, 34801, ElementalItemType.Crystal, 0),
		GoD_36972(FIRE, 36972, ElementalItemType.CrystalSuper, 0),
		GoD_36973(WATER, 36973, ElementalItemType.CrystalSuper, 0),
		GoD_36974(EARTH, 36974, ElementalItemType.CrystalSuper, 0),
		GoD_36975(WIND, 36975, ElementalItemType.CrystalSuper, 0),
		GoD_36976(DARK, 36976, ElementalItemType.CrystalSuper, 0),
		GoD_36977(HOLY, 36977, ElementalItemType.CrystalSuper, 0);
		
		public byte _element;
		public int _itemId;
		public ElementalItemType _type;
		public int _fixedPower;
		
		ElementalItems(byte element, int itemId, ElementalItemType type, int fixedPower)
		{
			_element = element;
			_itemId = itemId;
			_type = type;
			_fixedPower = fixedPower;
		}
	}
	
	public static byte getItemElement(int itemId)
	{
		final ElementalItems item = TABLE.get(itemId);
		if (item != null)
		{
			return item._element;
		}
		return NONE;
	}
	
	public static ElementalItems getItemElemental(int itemId)
	{
		return TABLE.get(itemId);
	}
	
	public static int getMaxElementLevel(int itemId)
	{
		final ElementalItems item = TABLE.get(itemId);
		if (item != null)
		{
			return item._type._maxLevel;
		}
		return -1;
	}
	
	public static boolean isElementableWithStone(ItemInstance targetItem, int stoneId)
	{
		if (!targetItem.isElementable())
		{
			return false;
		}
		
		if (TABLE.get(stoneId)._type == ElementalItemType.Jewel)
		{
			if (targetItem.getItem().getCrystalType() != CrystalType.R110)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isSuccess(ItemInstance item, int stoneId)
	{
		int row = -1;
		int column = -1;
		switch (item.getItem().getCrystalType())
		{
			case S:
			{
				row = 0;
				break;
			}
			case S80:
			{
				row = 1;
				break;
			}
			case S84:
			{
				row = 2;
				break;
			}
			case R:
			{
				row = 3;
				break;
			}
			case R95:
			{
				row = 4;
				break;
			}
			case R99:
			{
				row = 5;
				break;
			}
			case R110:
			{
				row = 6;
				break;
			}
		}
		
		switch (TABLE.get(stoneId)._type)
		{
			case Stone:
			{
				column = item.isWeapon() ? 0 : 1;
				break;
			}
			case Crystal:
			{
				column = item.isWeapon() ? 2 : 3;
				break;
			}
			case StoneSuper:
			{
				column = item.isWeapon() ? 4 : 5;
				break;
			}
			case CrystalSuper:
			{
				column = item.isWeapon() ? 6 : 7;
				break;
			}
			case Jewel:
			{
				column = item.isWeapon() ? 8 : 9;
				break;
			}
		}
		if ((row != -1) && (column != -1))
		{
			return Rnd.get(100) < CHANCE_TABLE[row][column];
		}
		return true;
	}
}
