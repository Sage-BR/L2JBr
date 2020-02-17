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
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.holders.RecipeHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.RecipeItemMakeInfo;
import org.l2jbr.gameserver.util.Util;

/**
 * @author Nik
 */
public class RequestRecipeItemMakeSelf implements IClientIncomingPacket
{
	private int _id;
	private ItemHolder[] _offeredItems;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_id = packet.readD();
		
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
		
		if (!client.getFloodProtectors().getManufacture().tryPerformAction("RecipeMakeSelf"))
		{
			return;
		}
		
		if (player.isCastingNow())
		{
			player.sendPacket(SystemMessageId.YOUR_RECIPE_BOOK_MAY_NOT_BE_ACCESSED_WHILE_USING_A_SKILL);
			return;
		}
		
		if (player.getPrivateStoreType() == PrivateStoreType.MANUFACTURE)
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING);
			return;
		}
		
		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.ITEM_CREATION_IS_NOT_POSSIBLE_WHILE_ENGAGED_IN_A_TRADE);
			return;
		}
		
		// TODO: Check if its a retail-like check.
		if (player.isAlikeDead())
		{
			return;
		}
		
		// On retail if player is requesting trade, it is instantly canceled.
		player.cancelActiveTrade();
		
		final RecipeHolder recipe = RecipeData.getInstance().getRecipe(_id);
		if (recipe == null)
		{
			player.sendPacket(SystemMessageId.THE_RECIPE_IS_INCORRECT);
			return;
		}
		
		if (!player.hasRecipeList(recipe.getId()))
		{
			Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.", Config.DEFAULT_PUNISH);
			return;
		}
		
		// Check if stats or ingredients are met.
		if (!recipe.checkNecessaryStats(player, player, true) || !recipe.checkNecessaryIngredients(player, true))
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
		
		if (player.isCrafting())
		{
			player.sendPacket(SystemMessageId.CURRENTLY_CRAFTING_AN_ITEM_PLEASE_WAIT);
			return;
		}
		
		player.setIsCrafting(true);
		
		// Take offerings to increase chance
		double offeringBonus = 0;
		if ((_offeredItems != null) && (recipe.getMaxOffering() > 0) && (recipe.getMaxOfferingBonus() > 0))
		{
			long offeredAdenaWorth = 0;
			for (ItemHolder offer : _offeredItems)
			{
				final ItemInstance item = player.getInventory().getItemByObjectId(offer.getId());
				if (player.destroyItem("CraftOffering", item, offer.getCount(), null, true))
				{
					offeredAdenaWorth += (item.getItem().getReferencePrice() * offer.getCount());
				}
			}
			
			offeringBonus = Math.min((offeredAdenaWorth / recipe.getMaxOffering()) * recipe.getMaxOfferingBonus(), recipe.getMaxOfferingBonus());
		}
		
		final boolean success = player.tryLuck() || ((recipe.getSuccessRate() + offeringBonus) > Rnd.get(100));
		final boolean craftingCritical = success && (player.getStat().getValue(Stats.CRAFTING_CRITICAL) > Rnd.get(100));
		
		if (success) // Successful craft.
		{
			if (craftingCritical)
			{
				player.sendPacket(SystemMessageId.CRAFTING_CRITICAL);
			}
		}
		else // Failed craft.
		{
			player.sendPacket(SystemMessageId.YOU_FAILED_AT_MIXING_THE_ITEM);
		}
		
		// Perform the crafting: take the items and give reward if success.
		recipe.doCraft(player, player, success, craftingCritical, true);
		
		// Send craft window. Must be sent after crafting so it properly counts the items.
		player.sendPacket(new RecipeItemMakeInfo(recipe.getId(), player, success, recipe.getMaxOffering()));
		
		player.setIsCrafting(false);
	}
}
