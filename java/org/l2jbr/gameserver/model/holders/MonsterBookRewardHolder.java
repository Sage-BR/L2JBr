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

/**
 * @author Mobius
 */
public class MonsterBookRewardHolder
{
	private final int _kills;
	private final long _exp;
	private final int _sp;
	private final int _points;
	
	public MonsterBookRewardHolder(int kills, long exp, int sp, int points)
	{
		_kills = kills;
		_exp = exp;
		_sp = sp;
		_points = points;
	}
	
	public int getKills()
	{
		return _kills;
	}
	
	public long getExp()
	{
		return _exp;
	}
	
	public int getSp()
	{
		return _sp;
	}
	
	public int getPoints()
	{
		return _points;
	}
}
