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

import java.util.List;

/**
 * @author Mobius
 */
public class EquipmentUpgradeHolder
{
	private final int _id;
	private final int _requiredItemId;
	private final int _requiredItemEnchant;
	private final List<ItemHolder> _materials;
	private final long _adena;
	private final int _resultItemId;
	private final int _resultItemEnchant;
	
	public EquipmentUpgradeHolder(int id, int requiredItemId, int requiredItemEnchant, List<ItemHolder> materials, long adena, int resultItemId, int resultItemEnchant)
	{
		_id = id;
		_requiredItemId = requiredItemId;
		_requiredItemEnchant = requiredItemEnchant;
		_materials = materials;
		_adena = adena;
		_resultItemId = resultItemId;
		_resultItemEnchant = resultItemEnchant;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getRequiredItemId()
	{
		return _requiredItemId;
	}
	
	public int getRequiredItemEnchant()
	{
		return _requiredItemEnchant;
	}
	
	public List<ItemHolder> getMaterials()
	{
		return _materials;
	}
	
	public long getAdena()
	{
		return _adena;
	}
	
	public int getResultItemId()
	{
		return _resultItemId;
	}
	
	public int getResultItemEnchant()
	{
		return _resultItemEnchant;
	}
}
