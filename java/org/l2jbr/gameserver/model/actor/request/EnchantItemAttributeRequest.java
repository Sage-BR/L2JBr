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
package org.l2jbr.gameserver.model.actor.request;

import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;

/**
 * @author UnAfraid
 */
public class EnchantItemAttributeRequest extends AbstractRequest
{
	private volatile int _enchantingItemObjectId;
	private volatile int _enchantingStoneObjectId;
	
	public EnchantItemAttributeRequest(PlayerInstance player, int enchantingStoneObjectId)
	{
		super(player);
		_enchantingStoneObjectId = enchantingStoneObjectId;
	}
	
	public ItemInstance getEnchantingItem()
	{
		return getActiveChar().getInventory().getItemByObjectId(_enchantingItemObjectId);
	}
	
	public void setEnchantingItem(int objectId)
	{
		_enchantingItemObjectId = objectId;
	}
	
	public ItemInstance getEnchantingStone()
	{
		return getActiveChar().getInventory().getItemByObjectId(_enchantingStoneObjectId);
	}
	
	public void setEnchantingStone(int objectId)
	{
		_enchantingStoneObjectId = objectId;
	}
	
	@Override
	public boolean isItemRequest()
	{
		return true;
	}
	
	@Override
	public boolean canWorkWith(AbstractRequest request)
	{
		return !request.isItemRequest();
	}
	
	@Override
	public boolean isUsing(int objectId)
	{
		return (objectId > 0) && ((objectId == _enchantingItemObjectId) || (objectId == _enchantingStoneObjectId));
	}
}
