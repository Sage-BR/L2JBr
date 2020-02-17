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
package org.l2jbr.gameserver.model.actor.stat;

import org.l2jbr.gameserver.model.actor.Vehicle;

public class VehicleStat extends CreatureStat
{
	private float _moveSpeed = 0;
	private int _rotationSpeed = 0;
	
	public VehicleStat(Vehicle activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public double getMoveSpeed()
	{
		return _moveSpeed;
	}
	
	public void setMoveSpeed(float speed)
	{
		_moveSpeed = speed;
	}
	
	public double getRotationSpeed()
	{
		return _rotationSpeed;
	}
	
	public void setRotationSpeed(int speed)
	{
		_rotationSpeed = speed;
	}
}