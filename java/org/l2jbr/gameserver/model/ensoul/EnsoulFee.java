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
package org.l2jbr.gameserver.model.ensoul;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.items.type.CrystalType;

/**
 * @author UnAfraid
 */
public class EnsoulFee
{
	private final CrystalType _type;
	
	private final ItemHolder[] _ensoulFee = new ItemHolder[3];
	private final ItemHolder[] _resoulFees = new ItemHolder[3];
	private final List<ItemHolder> _removalFee = new ArrayList<>();
	
	public EnsoulFee(CrystalType type)
	{
		_type = type;
	}
	
	public CrystalType getCrystalType()
	{
		return _type;
	}
	
	public void setEnsoul(int index, ItemHolder item)
	{
		_ensoulFee[index] = item;
	}
	
	public void setResoul(int index, ItemHolder item)
	{
		_resoulFees[index] = item;
	}
	
	public void addRemovalFee(ItemHolder itemHolder)
	{
		_removalFee.add(itemHolder);
	}
	
	public ItemHolder getEnsoul(int index)
	{
		return _ensoulFee[index];
	}
	
	public ItemHolder getResoul(int index)
	{
		return _resoulFees[index];
	}
	
	public List<ItemHolder> getRemovalFee()
	{
		return _removalFee;
	}
}
