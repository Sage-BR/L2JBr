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
package org.l2jbr.gameserver.model.actor.appearance;

import org.l2jbr.gameserver.enums.Sex;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

public class PlayerAppearance
{
	public static final int DEFAULT_TITLE_COLOR = 0xECF9A2;
	
	private PlayerInstance _owner;
	
	private byte _face;
	
	private byte _hairColor;
	
	private byte _hairStyle;
	
	private boolean _sex; // Female true(1)
	
	/** The current visible name of this player, not necessarily the real one */
	private String _visibleName;
	
	/** The current visible title of this player, not necessarily the real one */
	private String _visibleTitle;
	
	/** The default name color is 0xFFFFFF. */
	private int _nameColor = 0xFFFFFF;
	
	/** The default title color is 0xECF9A2. */
	private int _titleColor = DEFAULT_TITLE_COLOR;
	
	private int _visibleClanId = -1;
	private int _visibleClanCrestId = -1;
	private int _visibleClanLargeCrestId = -1;
	private int _visibleAllyId = -1;
	private int _visibleAllyCrestId = -1;
	
	public PlayerAppearance(byte face, byte hColor, byte hStyle, boolean sex)
	{
		_face = face;
		_hairColor = hColor;
		_hairStyle = hStyle;
		_sex = sex;
	}
	
	/**
	 * @param visibleName The visibleName to set.
	 */
	public void setVisibleName(String visibleName)
	{
		_visibleName = visibleName;
	}
	
	/**
	 * @return Returns the visibleName.
	 */
	public String getVisibleName()
	{
		if (_visibleName == null)
		{
			return _owner.getName();
		}
		return _visibleName;
	}
	
	/**
	 * @param visibleTitle The visibleTitle to set.
	 */
	public void setVisibleTitle(String visibleTitle)
	{
		_visibleTitle = visibleTitle;
	}
	
	/**
	 * @return Returns the visibleTitle.
	 */
	public String getVisibleTitle()
	{
		if (_visibleTitle == null)
		{
			return _owner.getTitle();
		}
		return _visibleTitle;
	}
	
	public byte getFace()
	{
		return _face;
	}
	
	/**
	 * @param value
	 */
	public void setFace(int value)
	{
		_face = (byte) value;
	}
	
	public byte getHairColor()
	{
		return _hairColor;
	}
	
	/**
	 * @param value
	 */
	public void setHairColor(int value)
	{
		_hairColor = (byte) value;
	}
	
	public byte getHairStyle()
	{
		return _hairStyle;
	}
	
	/**
	 * @param value
	 */
	public void setHairStyle(int value)
	{
		_hairStyle = (byte) value;
	}
	
	/**
	 * @return true if char is female
	 */
	public boolean isFemale()
	{
		return _sex;
	}
	
	public void setFemale()
	{
		_sex = true;
	}
	
	public void setMale()
	{
		_sex = false;
	}
	
	/**
	 * @return Sex of the char
	 */
	public Sex getSexType()
	{
		return _sex ? Sex.FEMALE : Sex.MALE;
	}
	
	public int getNameColor()
	{
		return _nameColor;
	}
	
	public void setNameColor(int nameColor)
	{
		if (nameColor < 0)
		{
			return;
		}
		
		_nameColor = nameColor;
	}
	
	public void setNameColor(int red, int green, int blue)
	{
		_nameColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
	}
	
	public int getTitleColor()
	{
		return _titleColor;
	}
	
	public void setTitleColor(int titleColor)
	{
		if (titleColor < 0)
		{
			return;
		}
		
		_titleColor = titleColor;
	}
	
	public void setTitleColor(int red, int green, int blue)
	{
		_titleColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
	}
	
	/**
	 * @param owner The owner to set.
	 */
	public void setOwner(PlayerInstance owner)
	{
		_owner = owner;
	}
	
	/**
	 * @return Returns the owner.
	 */
	public PlayerInstance getOwner()
	{
		return _owner;
	}
	
	public int getVisibleClanId()
	{
		return _visibleClanId != -1 ? _visibleClanId : _owner.isCursedWeaponEquipped() ? 0 : _owner.getClanId();
	}
	
	public int getVisibleClanCrestId()
	{
		return _visibleClanCrestId != -1 ? _visibleClanCrestId : _owner.isCursedWeaponEquipped() ? 0 : _owner.getClanCrestId();
	}
	
	public int getVisibleClanLargeCrestId()
	{
		return _visibleClanLargeCrestId != -1 ? _visibleClanLargeCrestId : _owner.isCursedWeaponEquipped() ? 0 : _owner.getClanCrestLargeId();
	}
	
	public int getVisibleAllyId()
	{
		return _visibleAllyId != -1 ? _visibleAllyId : _owner.isCursedWeaponEquipped() ? 0 : _owner.getAllyId();
	}
	
	public int getVisibleAllyCrestId()
	{
		return _visibleAllyCrestId != -1 ? _visibleAllyCrestId : (_owner == null) || _owner.isCursedWeaponEquipped() ? 0 : _owner.getAllyCrestId();
	}
	
	public void setVisibleClanData(int clanId, int clanCrestId, int clanLargeCrestId, int allyId, int allyCrestId)
	{
		_visibleClanId = clanId;
		_visibleClanCrestId = clanCrestId;
		_visibleClanLargeCrestId = clanLargeCrestId;
		_visibleAllyId = allyId;
		_visibleAllyCrestId = allyCrestId;
	}
}
