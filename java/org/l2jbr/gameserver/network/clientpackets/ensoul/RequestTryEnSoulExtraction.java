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
package org.l2jbr.gameserver.network.clientpackets.ensoul;

import java.util.Collection;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.data.xml.impl.EnsoulData;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.ensoul.EnsoulOption;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.ensoul.ExEnSoulExtractionResult;

/**
 * @author Mobius
 */
public class RequestTryEnSoulExtraction implements IClientIncomingPacket
{
	private int _itemObjectId;
	private int _type;
	private int _position;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_itemObjectId = packet.readD();
		_type = packet.readC();
		_position = packet.readC() - 1;
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		PlayerInstance player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_itemObjectId);
		if (item == null)
		{
			return;
		}
		
		EnsoulOption option = null;
		if (_type == 1)
		{
			option = item.getSpecialAbility(_position);
			// If position is invalid, check the other one.
			if ((option == null) && (_position == 0))
			{
				option = item.getSpecialAbility(1);
				if (option != null)
				{
					_position = 1;
				}
			}
		}
		if (_type == 2)
		{
			option = item.getAdditionalSpecialAbility(_position);
		}
		if (option == null)
		{
			return;
		}
		
		final Collection<ItemHolder> removalFee = EnsoulData.getInstance().getRemovalFee(item.getItem().getCrystalType());
		if (removalFee.isEmpty())
		{
			return;
		}
		
		// Check if player has required items.
		for (ItemHolder itemHolder : removalFee)
		{
			if (player.getInventory().getInventoryItemCount(itemHolder.getId(), -1) < itemHolder.getCount())
			{
				player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
				player.sendPacket(new ExEnSoulExtractionResult(false, item));
				return;
			}
		}
		
		// Take required items.
		for (ItemHolder itemHolder : removalFee)
		{
			player.destroyItemByItemId("Rune Extract", itemHolder.getId(), itemHolder.getCount(), player, true);
		}
		
		// Remove equipped rune.
		item.removeSpecialAbility(_position, _type);
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(item);
		
		// Add rune in player inventory.
		final int runeId = EnsoulData.getInstance().getStone(_type, option.getId());
		if (runeId > 0)
		{
			iu.addItem(player.addItem("Rune Extract", runeId, 1, player, true));
		}
		
		player.sendInventoryUpdate(iu);
		player.sendPacket(new ExEnSoulExtractionResult(true, item));
	}
}