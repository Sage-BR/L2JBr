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
public class FishingCatch
{
	private final int _itemId;
	private final float _chance;
	private final float _multiplier;
	
	public FishingCatch(int itemId, float chance, float multiplier)
	{
		_itemId = itemId;
		_chance = chance;
		_multiplier = multiplier;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public float getChance()
	{
		return _chance;
	}
	
	public float getMultiplier()
	{
		return _multiplier;
	}
}
