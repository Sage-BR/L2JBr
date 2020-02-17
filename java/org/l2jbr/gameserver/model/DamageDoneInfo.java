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

import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

/**
 * @author xban1x
 */
public class DamageDoneInfo
{
	private final PlayerInstance _attacker;
	private long _damage = 0;
	
	public DamageDoneInfo(PlayerInstance attacker)
	{
		_attacker = attacker;
	}
	
	public PlayerInstance getAttacker()
	{
		return _attacker;
	}
	
	public void addDamage(long damage)
	{
		_damage += damage;
	}
	
	public long getDamage()
	{
		return _damage;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return (this == obj) || ((obj instanceof DamageDoneInfo) && (((DamageDoneInfo) obj).getAttacker() == _attacker));
	}
	
	@Override
	public int hashCode()
	{
		return _attacker.getObjectId();
	}
}
