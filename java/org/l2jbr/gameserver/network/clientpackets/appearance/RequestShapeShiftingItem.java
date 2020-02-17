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
package org.l2jbr.gameserver.network.clientpackets.appearance;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.data.xml.impl.AppearanceItemData;
import org.l2jbr.gameserver.enums.InventorySlot;
import org.l2jbr.gameserver.enums.ItemLocation;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.request.ShapeShiftingItemRequest;
import org.l2jbr.gameserver.model.holders.AppearanceHolder;
import org.l2jbr.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.appearance.AppearanceStone;
import org.l2jbr.gameserver.model.items.appearance.AppearanceType;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.variables.ItemVariables;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.ExAdenaInvenCount;
import org.l2jbr.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.appearance.ExShapeShiftingResult;

/**
 * @author UnAfraid
 */
public class RequestShapeShiftingItem implements IClientIncomingPacket
{
	private int _targetItemObjId;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_targetItemObjId = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final ShapeShiftingItemRequest request = player.getRequest(ShapeShiftingItemRequest.class);
		
		if (player.isInStoreMode() || player.isCrafting() || player.isProcessingRequest() || player.isProcessingTransaction() || (request == null))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			return;
		}
		
		final PlayerInventory inventory = player.getInventory();
		final ItemInstance targetItem = inventory.getItemByObjectId(_targetItemObjId);
		ItemInstance stone = request.getAppearanceStone();
		
		if ((targetItem == null) || (stone == null))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone.getOwnerId() != player.getObjectId()) || (targetItem.getOwnerId() != player.getObjectId()))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!targetItem.getItem().isAppearanceable())
		{
			client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_MODIFIED_OR_RESTORED);
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((targetItem.getItemLocation() != ItemLocation.INVENTORY) && (targetItem.getItemLocation() != ItemLocation.PAPERDOLL))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		final AppearanceStone appearanceStone = AppearanceItemData.getInstance().getStone(stone.getId());
		if (appearanceStone == null)
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!appearanceStone.checkConditions(player, targetItem))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		final ItemInstance extractItem = request.getAppearanceExtractItem();
		
		int extracItemId = 0;
		if ((appearanceStone.getType() != AppearanceType.RESTORE) && (appearanceStone.getType() != AppearanceType.FIXED))
		{
			if (extractItem == null)
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extractItem.getOwnerId() != player.getObjectId())
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (!extractItem.getItem().isAppearanceable())
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extractItem.getItemLocation() != ItemLocation.INVENTORY) && (extractItem.getItemLocation() != ItemLocation.PAPERDOLL))
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extractItem.getItem().getCrystalType().isGreater(targetItem.getItem().getCrystalType()))
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extractItem.getVisualId() > 0)
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extractItem.getItemType() != targetItem.getItemType()) || (extractItem.getId() == targetItem.getId()) || (extractItem.getObjectId() == targetItem.getObjectId()))
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extractItem.getItem().getBodyPart() != targetItem.getItem().getBodyPart()) && ((extractItem.getItem().getBodyPart() != Item.SLOT_FULL_ARMOR) || (targetItem.getItem().getBodyPart() != Item.SLOT_CHEST)))
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			extracItemId = extractItem.getId();
		}
		
		long cost = appearanceStone.getCost();
		if (cost > player.getAdena())
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_MODIFY_AS_YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (stone.getCount() < 1L)
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		if (appearanceStone.getType() == AppearanceType.NORMAL)
		{
			if (inventory.destroyItem(getClass().getSimpleName(), extractItem, 1, player, this) == null)
			{
				client.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
		}
		
		inventory.destroyItem(getClass().getSimpleName(), stone, 1, player, this);
		player.reduceAdena(getClass().getSimpleName(), cost, extractItem, false);
		
		switch (appearanceStone.getType())
		{
			case RESTORE:
			{
				targetItem.setVisualId(0);
				targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, 0);
				break;
			}
			case NORMAL:
			{
				targetItem.setVisualId(extractItem.getId());
				break;
			}
			case BLESSED:
			{
				targetItem.setVisualId(extractItem.getId());
				break;
			}
			case FIXED:
			{
				if (appearanceStone.getVisualIds().isEmpty())
				{
					extracItemId = appearanceStone.getVisualId();
					targetItem.setVisualId(appearanceStone.getVisualId());
					targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, appearanceStone.getId());
				}
				else
				{
					final AppearanceHolder holder = appearanceStone.findVisualChange(targetItem);
					if (holder != null)
					{
						extracItemId = holder.getVisualId();
						targetItem.setVisualId(holder.getVisualId());
						targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, appearanceStone.getId());
					}
				}
				break;
			}
		}
		
		if ((appearanceStone.getType() != AppearanceType.RESTORE) && (appearanceStone.getLifeTime() > 0))
		{
			targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_LIFE_TIME, System.currentTimeMillis() + appearanceStone.getLifeTime());
			targetItem.scheduleVisualLifeTime();
		}
		
		targetItem.getVariables().storeMe();
		
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(targetItem);
		if (extractItem != null)
		{
			iu.addModifiedItem(extractItem);
		}
		if (inventory.getItemByObjectId(stone.getObjectId()) == null)
		{
			iu.addRemovedItem(stone);
		}
		else
		{
			iu.addModifiedItem(stone);
		}
		player.sendInventoryUpdate(iu);
		
		player.removeRequest(ShapeShiftingItemRequest.class);
		client.sendPacket(new ExShapeShiftingResult(ExShapeShiftingResult.RESULT_SUCCESS, targetItem.getId(), extracItemId));
		if (targetItem.isEquipped())
		{
			player.broadcastUserInfo();
			final ExUserInfoEquipSlot slots = new ExUserInfoEquipSlot(player, false);
			for (InventorySlot slot : InventorySlot.values())
			{
				if (slot.getSlot() == targetItem.getLocationSlot())
				{
					slots.addComponentType(slot);
				}
			}
			client.sendPacket(slots);
		}
		client.sendPacket(new ExAdenaInvenCount(player));
	}
}
