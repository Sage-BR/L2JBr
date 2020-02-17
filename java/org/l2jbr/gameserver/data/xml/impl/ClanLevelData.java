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
package org.l2jbr.gameserver.data.xml.impl;

/**
 * @author Mobius
 */
public class ClanLevelData
{
	// TODO: Move to XML.
	private final static int[] CLAN_LEVEL_REQUIREMENTS =
	{
		35000,
		80000,
		140000,
		315000,
		560000,
		965000,
		2690000,
		4050000,
		5930000,
		7560000,
		11830000,
		19110000,
		27300000,
		36400000,
		46410000,
		0
	};
	private final static int[] COMMON_CLAN_MEMBER_LIMIT =
	{
		10,
		15,
		20,
		30,
		40,
		42,
		68,
		85,
		94,
		102,
		111,
		120,
		128,
		137,
		145,
		171
	};
	private final static int[] ELITE_CLAN_MEMBER_LIMIT =
	{
		0,
		0,
		0,
		0,
		0,
		8,
		12,
		15,
		16,
		18,
		19,
		20,
		22,
		23,
		25,
		29
	};
	
	public static int getLevelRequirement(int clanLevel)
	{
		return CLAN_LEVEL_REQUIREMENTS[clanLevel];
	}
	
	public static int getCommonMemberLimit(int clanLevel)
	{
		return COMMON_CLAN_MEMBER_LIMIT[clanLevel];
	}
	
	public static int getEliteMemberLimit(int clanLevel)
	{
		return ELITE_CLAN_MEMBER_LIMIT[clanLevel];
	}
}
