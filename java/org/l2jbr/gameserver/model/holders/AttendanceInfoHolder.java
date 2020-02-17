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
public class AttendanceInfoHolder
{
	private final int _rewardIndex;
	private final boolean _rewardAvailable;
	
	public AttendanceInfoHolder(int rewardIndex, boolean rewardAvailable)
	{
		_rewardIndex = rewardIndex;
		_rewardAvailable = rewardAvailable;
	}
	
	public int getRewardIndex()
	{
		return _rewardIndex;
	}
	
	public boolean isRewardAvailable()
	{
		return _rewardAvailable;
	}
}
