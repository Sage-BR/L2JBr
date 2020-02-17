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
import org.l2jbr.gameserver.model.PremiumItem;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExGetPremiumItemList;
import org.l2jbr.gameserver.util.Util;

/**
 * @author Gnacik
 */
public class RequestWithDrawPremiumItem implements IClientIncomingPacket
{
	private int _itemNum;
	private int _charId;
	private long _itemCount;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_itemNum = packet.readD();
		_charId = packet.readD();
		_itemCount = packet.readQ();
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
		else if (_itemCount <= 0)
		{
			return;
		}
		else if (player.getObjectId() != _charId)
		{
			Util.handleIllegalPlayerAction(player, "[RequestWithDrawPremiumItem] Incorrect owner, Player: " + player.getName(), Config.DEFAULT_PUNISH);
			return;
		}
		else if (player.getPremiumItemList().isEmpty())
		{
			Util.handleIllegalPlayerAction(player, "[RequestWithDrawPremiumItem] Player: " + player.getName() + " try to get item with empty list!", Config.DEFAULT_PUNISH);
			return;
		}
		else if ((player.getWeightPenalty() >= 3) || !player.isInventoryUnder90(false))
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_THE_DIMENSIONAL_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHT_QUANTITY_LIMIT);
			return;
		}
		else if (player.isProcessingTransaction())
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_A_DIMENSIONAL_ITEM_DURING_AN_EXCHANGE);
			return;
		}
		
		final PremiumItem _item = player.getPremiumItemList().get(_itemNum);
		if (_item == null)
		{
			return;
		}
		else if (_item.getCount() < _itemCount)
		{
			return;
		}
		
		final long itemsLeft = (_item.getCount() - _itemCount);
		
		player.addItem("PremiumItem", _item.getItemId(), _itemCount, player.getTarget(), true);
		
		if (itemsLeft > 0)
		{
			_item.updateCount(itemsLeft);
			player.updatePremiumItem(_itemNum, itemsLeft);
		}
		else
		{
			player.getPremiumItemList().remove(_itemNum);
			player.deletePremiumItem(_itemNum);
		}
		
		if (player.getPremiumItemList().isEmpty())
		{
			client.sendPacket(SystemMessageId.THERE_ARE_NO_MORE_DIMENSIONAL_ITEMS_TO_BE_FOUND);
		}
		else
		{
			client.sendPacket(new ExGetPremiumItemList(player));
		}
	}
}
