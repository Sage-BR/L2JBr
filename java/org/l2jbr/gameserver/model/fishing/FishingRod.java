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
package org.l2jbr.gameserver.model.fishing;

/**
 * @author Zarcos
 */
public class FishingRod
{
	private final int _itemId;
	private final int _reduceFishingTime;
	private final float _xpMultiplier;
	private final float _spMultiplier;
	
	public FishingRod(int itemId, int reduceFishingTime, float xpMultiplier, float spMultiplier)
	{
		_itemId = itemId;
		_reduceFishingTime = reduceFishingTime;
		_xpMultiplier = xpMultiplier;
		_spMultiplier = spMultiplier;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getReduceFishingTime()
	{
		return _reduceFishingTime;
	}
	
	public float getXpMultiplier()
	{
		return _xpMultiplier;
	}
	
	public float getSpMultiplier()
	{
		return _spMultiplier;
	}
}
