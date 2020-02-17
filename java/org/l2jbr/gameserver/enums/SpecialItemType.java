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

/**
 * @author Nik
 */
public enum SpecialItemType
{
	PC_CAFE_POINTS(-100),
	CLAN_REPUTATION(-200),
	FAME(-300),
	FIELD_CYCLE_POINTS(-400),
	RAIDBOSS_POINTS(-500);
	
	private int _clientId;
	
	private SpecialItemType(int clientId)
	{
		_clientId = clientId;
	}
	
	public int getClientId()
	{
		return _clientId;
	}
	
	public static SpecialItemType getByClientId(int clientId)
	{
		for (SpecialItemType type : values())
		{
			if (type.getClientId() == clientId)
			{
				return type;
			}
		}
		
		return null;
	}
}
