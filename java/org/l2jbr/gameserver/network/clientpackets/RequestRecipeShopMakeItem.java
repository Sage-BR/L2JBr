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
package org.l2jbr.gameserver.network.clientpackets;

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.data.xml.impl.RecipeData;
import org.l2jbr.gameserver.enums.PrivateStoreType;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.holders.RecipeHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.RecipeShopItemInfo;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.util.Util;

/**
 * It appears you are able to successfully create from manufacture store while casting, while in manufacture store and within 250 range. So basic checks <br>
 * from your own recipe list crafting are skipped. With the exception of trading, if you request trade, it is cancelled, if you are already trading, you get message.
 * @author Nik
 */
public class RequestRecipeShopMakeItem implements IClientIncomingPacket
{
	private int _objectId;
	private int _recipeId;
	private long _manufacturePrice;
	private ItemHolder[] _offeredItems;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_objectId = packet.readD();
		_recipeId = packet.readD();
		_manufacturePrice = packet.readQ();
		
		final int offeringsCount = packet.readD();
		if (offeringsCount > 0)
		{
			_offeredItems = new ItemHolder[offeringsCount];
			for (int i = 0; i < offeringsCount; i++)
			{
				final int objectId = packet.readD();
				final long count = packet.readQ();
				_offeredItems[i] = new ItemHolder(objectId, count);
			}
		}
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
		
		if (!Config.IS_CRAFTING_ENABLED)
		{
			player.sendMessage("Item creation is currently disabled.");
			return;
		}
		
		if (!client.getFloodProtectors().getManufacture().tryPerformAction("RecipeShopMake"))
		{
			return;
		}
		
		final PlayerInstance manufacturer = World.getInstance().getPlayer(_objectId);
		if (manufacturer == null)
		{
			return;
		}
		
		if ((manufacturer.getInstanceWorld() != player.getInstanceWorld()) || (player.calculateDistance2D(manufacturer) > 250))
		{
			return;
		}
		
		if (manufacturer.getPrivateStoreType() != PrivateStoreType.MANUFACTURE)
		{
			return;
		}
		
		if (player.isProcessingTransaction() || manufacturer.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.ITEM_CREATION_IS_NOT_POSSIBLE_WHILE_ENGAGED_IN_A_TRADE);
			return;
		}
		
		// TODO: Check if its a retail-like check.
		if (player.isAlikeDead() || manufacturer.isAlikeDead())
		{
			return;
		}
		
		// On retail if player is requesting trade, it is instantly canceled.
		player.cancelActiveTrade();
		
		final RecipeHolder recipe = RecipeData.getInstance().getRecipe(_recipeId);
		if (recipe == null)
		{
			player.sendPacket(SystemMessageId.THE_RECIPE_IS_INCORRECT);
			return;
		}
		
		if (!manufacturer.hasRecipeList(recipe.getId()))
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.", Config.DEFAULT_PUNISH);
			return;
		}
		
		final Long manufactureRecipeCost = manufacturer.getManufactureItems().get(_recipeId);
		if (manufactureRecipeCost == null)
		{
			return;
		}
		
		// Check if the price is the same as requested price.
		if (_manufacturePrice != manufactureRecipeCost)
		{
			return;
		}
		
		// Check if player can pay.
		if (player.getAdena() < manufactureRecipeCost)
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}
		
		// Check if stats or ingredients are met.
		if (!recipe.checkNecessaryStats(player, manufacturer, true) || !recipe.checkNecessaryIngredients(player, true))
		{
			return;
		}
		
		// Check if all offerings are legit.
		if ((_offeredItems != null) && (recipe.getMaxOffering() > 0) && (recipe.getMaxOfferingBonus() > 0))
		{
			for (ItemHolder offer : _offeredItems)
			{
				final ItemInstance item = player.getInventory().getItemByObjectId(offer.getId());
				if ((item == null) || (item.getCount() < offer.getCount()) || !item.isDestroyable())
				{
					return;
				}
			}
		}
		
		if (manufacturer.isCrafting())
		{
			player.sendPacket(SystemMessageId.CURRENTLY_CRAFTING_AN_ITEM_PLEASE_WAIT);
			return;
		}
		
		manufacturer.setIsCrafting(true);
		
		// First you must pay for the manufacturing service of the other player.
		if (manufactureRecipeCost > 0)
		{
			// Attempt to pay the required manufacturing price by the manufacturer.
			ItemInstance paidAdena = player.transferItem("PayManufacture", player.getInventory().getAdenaInstance().getObjectId(), manufactureRecipeCost, manufacturer.getInventory(), manufacturer);
			
			if (paidAdena == null)
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		
		// Take offerings to increase chance
		double offeringBonus = 0;
		if ((_offeredItems != null) && (recipe.getMaxOffering() > 0) && (recipe.getMaxOfferingBonus() > 0))
		{
			long offeredAdenaWorth = 0;
			for (ItemHolder offer : _offeredItems)
			{
				final ItemInstance item = player.getInventory().getItemByObjectId(offer.getId());
				if (player.destroyItem("CraftOffering", item, offer.getCount(), manufacturer, true))
				{
					offeredAdenaWorth += (item.getItem().getReferencePrice() * offer.getCount());
				}
			}
			
			offeringBonus = Math.min((offeredAdenaWorth / recipe.getMaxOffering()) * recipe.getMaxOfferingBonus(), recipe.getMaxOfferingBonus());
		}
		
		final boolean success = player.tryLuck() || ((recipe.getSuccessRate() + offeringBonus) > Rnd.get(100));
		final boolean craftingCritical = success && (player.getStat().getValue(Stats.CRAFTING_CRITICAL) > Rnd.get(100));
		
		final ItemHolder craftedItem = recipe.doCraft(player, manufacturer, success, craftingCritical, true);
		if (success)
		{
			if (craftingCritical)
			{
				player.sendPacket(SystemMessageId.CRAFTING_CRITICAL);
			}
			if (craftedItem.getCount() > 1)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S3_S2_S_HAVE_BEEN_CREATED_FOR_C1_AT_THE_PRICE_OF_S4_ADENA);
				sm.addString(player.getName());
				sm.addInt((int) craftedItem.getCount());
				sm.addItemName(craftedItem.getId());
				sm.addLong(manufactureRecipeCost);
				manufacturer.sendPacket(sm);
				
				sm = new SystemMessage(SystemMessageId.C1_CREATED_S3_S2_S_AT_THE_PRICE_OF_S4_ADENA);
				sm.addString(manufacturer.getName());
				sm.addInt((int) craftedItem.getCount());
				sm.addItemName(craftedItem.getId());
				sm.addLong(manufactureRecipeCost);
				player.sendPacket(sm);
			}
			else
			{
				
				SystemMessage sm = new SystemMessage(SystemMessageId.S2_HAS_BEEN_CREATED_FOR_C1_AFTER_THE_PAYMENT_OF_S3_ADENA_WAS_RECEIVED);
				sm.addString(player.getName());
				sm.addItemName(craftedItem.getId());
				sm.addLong(manufactureRecipeCost);
				manufacturer.sendPacket(sm);
				
				sm = new SystemMessage(SystemMessageId.C1_CREATED_S2_AFTER_RECEIVING_S3_ADENA);
				sm.addString(manufacturer.getName());
				sm.addItemName(craftedItem.getId());
				sm.addLong(manufactureRecipeCost);
				player.sendPacket(sm);
			}
		}
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FAILED_TO_CREATE_S2_FOR_C1_AT_THE_PRICE_OF_S3_ADENA);
			sm.addString(player.getName());
			sm.addItemName(craftedItem.getId());
			sm.addLong(manufactureRecipeCost);
			manufacturer.sendPacket(sm);
			
			sm = new SystemMessage(SystemMessageId.C1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA);
			sm.addString(manufacturer.getName());
			sm.addItemName(craftedItem.getId());
			sm.addLong(manufactureRecipeCost);
			player.sendPacket(sm);
		}
		
		// Show manufacturing window.
		player.sendPacket(new RecipeShopItemInfo(manufacturer, recipe.getId(), success, _manufacturePrice, recipe.getMaxOffering()));
		
		manufacturer.setIsCrafting(false);
	}
}
