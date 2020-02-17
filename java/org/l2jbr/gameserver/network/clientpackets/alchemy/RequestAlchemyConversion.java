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
package org.l2jbr.gameserver.network.clientpackets.alchemy;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.data.xml.impl.AlchemyData;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.enums.PrivateStoreType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.alchemy.AlchemyCraftData;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.alchemy.ExAlchemyConversion;
import org.l2jbr.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * @author Sdw
 */
public class RequestAlchemyConversion implements IClientIncomingPacket
{
	private int _craftTimes;
	private int _skillId;
	private int _skillLevel;
	// private final Set<ItemHolder> _ingredients = new HashSet<>();
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_craftTimes = packet.readD();
		packet.readH();
		_skillId = packet.readD();
		_skillLevel = packet.readD();
		// final int ingredientsSize = packet.readD();
		// for (int i = 0; i < ingredientsSize; i++)
		// {
		// _ingredients.add(new ItemHolder(packet.readD(), packet.readQ()));
		// }
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if ((player == null) || (player.getRace() != Race.ERTHEIA))
		{
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player))
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ALCHEMY_DURING_BATTLE);
			return;
		}
		else if (player.isInStoreMode() || (player.getPrivateStoreType() != PrivateStoreType.NONE))
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ALCHEMY_WHILE_TRADING_OR_USING_A_PRIVATE_STORE_OR_SHOP);
			return;
		}
		else if (player.isDead())
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ALCHEMY_WHILE_DEAD);
			return;
		}
		else if (player.isMovementDisabled())
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ALCHEMY_WHILE_IMMOBILE);
			return;
		}
		
		final AlchemyCraftData data = AlchemyData.getInstance().getCraftData(_skillId, _skillLevel);
		if (data == null)
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			LOGGER.warning("Missing AlchemyData for skillId: " + _skillId + ", skillLevel: " + _skillLevel);
			return;
		}
		
		// if (!_ingredients.equals(data.getIngredients()))
		// {
		// LOGGER.warning("Client ingredients are not same as server ingredients for alchemy conversion player: "+ +"", player);
		// return;
		// }
		
		// Chance based on grade.
		final int baseChance;
		switch (data.getGrade())
		{
			case 1: // Elementary
			{
				baseChance = 100;
				break;
			}
			case 2: // Intermediate
			{
				baseChance = 80;
				break;
			}
			case 3: // Advanced
			{
				baseChance = 60;
				break;
			}
			default: // Master
			{
				baseChance = 50;
				break;
			}
		}
		
		// Calculate success and failure count.
		final Item successItemTemplate = ItemTable.getInstance().getTemplate(data.getProductionSuccess().getId());
		final Item failureItemTemplate = ItemTable.getInstance().getTemplate(data.getProductionFailure().getId());
		int totalWeight = 0;
		int totalslots = (successItemTemplate.isStackable() ? 1 : 0) + (failureItemTemplate.isStackable() ? 1 : 0);
		int successCount = 0;
		int failureCount = 0;
		for (int i = 0; i < _craftTimes; i++)
		{
			if (Rnd.get(100) < baseChance)
			{
				successCount++;
				totalWeight += successItemTemplate.getWeight();
				totalslots += successItemTemplate.isStackable() ? 0 : 1;
			}
			else
			{
				failureCount++;
				totalWeight += failureItemTemplate.getWeight();
				totalslots += failureItemTemplate.isStackable() ? 0 : 1;
			}
		}
		
		// Check if player has enough ingredients.
		for (ItemHolder ingredient : data.getIngredients())
		{
			if (player.getInventory().getInventoryItemCount(ingredient.getId(), -1) < (ingredient.getCount() * _craftTimes))
			{
				player.sendPacket(new ExAlchemyConversion(0, 0));
				player.sendPacket(SystemMessageId.NOT_ENOUGH_INGREDIENTS);
				return;
			}
		}
		
		// Weight and capacity check.
		final PlayerInventory inventory = player.getInventory();
		if (!inventory.validateWeight(totalWeight) || ((totalslots > 0) && !inventory.validateCapacity(totalslots)))
		{
			player.sendPacket(new ExAlchemyConversion(0, 0));
			player.sendPacket(SystemMessageId.THERE_IS_NOT_ENOUGH_INVENTORY_SPACE_PLEASE_MAKE_MORE_ROOM_AND_TRY_AGAIN);
			return;
		}
		
		final InventoryUpdate ui = new InventoryUpdate();
		
		// Destroy ingredients.
		for (ItemHolder ingredient : data.getIngredients())
		{
			final ItemInstance item = player.getInventory().getItemByItemId(ingredient.getId());
			ui.addItem(item);
			player.getInventory().destroyItem("Alchemy", item, ingredient.getCount() * _craftTimes, player, null);
		}
		// Add success items.
		if (successCount > 0)
		{
			final ItemInstance item = player.getInventory().addItem("Alchemy", data.getProductionSuccess().getId(), data.getProductionSuccess().getCount() * successCount, player, null);
			ui.addItem(item);
		}
		// Add failed items.
		if (failureCount > 0)
		{
			final ItemInstance item = player.getInventory().addItem("Alchemy", data.getProductionFailure().getId(), data.getProductionFailure().getCount() * failureCount, player, null);
			ui.addItem(item);
		}
		
		player.sendPacket(new ExAlchemyConversion(successCount, failureCount));
		player.sendInventoryUpdate(ui);
	}
}
