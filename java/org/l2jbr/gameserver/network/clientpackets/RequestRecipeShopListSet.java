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

import static org.l2jbr.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.data.xml.impl.RecipeData;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.enums.PrivateStoreType;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.RecipeHolder;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;
import org.l2jbr.gameserver.network.serverpackets.RecipeShopMsg;
import org.l2jbr.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2jbr.gameserver.util.Broadcast;
import org.l2jbr.gameserver.util.Util;

/**
 * RequestRecipeShopListSet client packet class.
 */
public class RequestRecipeShopListSet implements IClientIncomingPacket
{
	private static final int BATCH_LENGTH = 12;
	
	private Map<Integer, Long> _manufactureRecipes = null;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		int count = packet.readD();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != packet.getReadableBytes()))
		{
			return false;
		}
		
		_manufactureRecipes = new HashMap<>(count);
		for (int i = 0; i < count; i++)
		{
			int id = packet.readD();
			long cost = packet.readQ();
			if (cost < 0)
			{
				_manufactureRecipes = null;
				return false;
			}
			_manufactureRecipes.put(id, cost);
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
		
		if (_manufactureRecipes == null)
		{
			player.sendPacket(SystemMessageId.ITEMS_ARE_NOT_AVAILABLE_FOR_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.setPrivateStoreType(PrivateStoreType.NONE);
			player.broadcastUserInfo();
			return;
		}
		
		if (player.isCastingNow())
		{
			player.sendPacket(SystemMessageId.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
			return;
		}
		
		if (player.isCrafting())
		{
			player.sendPacket(SystemMessageId.CURRENTLY_CRAFTING_AN_ITEM_PLEASE_WAIT);
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel())
		{
			client.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isInsideZone(ZoneId.NO_STORE))
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		for (Entry<Integer, Long> item : _manufactureRecipes.entrySet())
		{
			final int recipeId = item.getKey();
			final long recipeCost = item.getValue();
			final RecipeHolder recipe = RecipeData.getInstance().getRecipe(recipeId);
			if (recipe == null)
			{
				player.sendPacket(SystemMessageId.THE_RECIPE_IS_INCORRECT);
				return;
			}
			if (ItemTable.getInstance().getTemplate(recipe.getItemId()).isQuestItem())
			{
				player.sendPacket(SystemMessageId.QUEST_RECIPES_CAN_NOT_BE_REGISTERED);
				return;
			}
			if (!player.hasRecipeList(recipe.getId()))
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Player " + player.getName() + " of account " + player.getAccountName() + " tried to set recipe which he dont have.", Config.DEFAULT_PUNISH);
				return;
			}
			
			if (recipeCost > MAX_ADENA)
			{
				Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set price of " + recipeCost + " adena in Private Manufacture.", Config.DEFAULT_PUNISH);
				return;
			}
		}
		
		player.setManufactureItems(_manufactureRecipes);
		
		player.setStoreName(!player.hasManufactureShop() ? "" : player.getStoreName());
		player.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
		player.sitDown();
		player.broadcastUserInfo();
		Broadcast.toSelfAndKnownPlayers(player, new RecipeShopMsg(player));
	}
}
