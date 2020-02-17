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
package org.l2jbr.gameserver.network.clientpackets.attributechange;

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.enums.AttributeType;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jbr.gameserver.model.items.enchant.attribute.AttributeHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.network.serverpackets.attributechange.ExChangeAttributeFail;
import org.l2jbr.gameserver.network.serverpackets.attributechange.ExChangeAttributeOk;
import org.l2jbr.gameserver.util.Util;

/**
 * @author Mobius
 */
public class RequestChangeAttributeItem implements IClientIncomingPacket
{
	private int _consumeItemId;
	private int _itemObjId;
	private int _newElementId;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_consumeItemId = packet.readD();
		_itemObjId = packet.readD();
		_newElementId = packet.readD();
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
		
		final PlayerInventory inventory = player.getInventory();
		final ItemInstance item = inventory.getItemByObjectId(_itemObjId);
		
		// attempting to destroy item
		if (player.getInventory().destroyItemByItemId("ChangeAttribute", _consumeItemId, 1, player, item) == null)
		{
			client.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			client.sendPacket(ExChangeAttributeFail.STATIC);
			Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to change attribute without an attribute change crystal.", Config.DEFAULT_PUNISH);
			return;
		}
		
		// get values
		final int oldElementId = item.getAttackAttributeType().getClientId();
		final int elementValue = item.getAttackAttribute().getValue();
		item.clearAllAttributes();
		item.setAttribute(new AttributeHolder(AttributeType.findByClientId(_newElementId), elementValue), true);
		
		// send packets
		final SystemMessage msg = new SystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_HAS_SUCCESSFULLY_CHANGED_TO_S3_ATTRIBUTE);
		msg.addItemName(item);
		msg.addAttribute(oldElementId);
		msg.addAttribute(_newElementId);
		player.sendPacket(msg);
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(item);
		for (ItemInstance i : player.getInventory().getItemsByItemId(_consumeItemId))
		{
			iu.addItem(i);
		}
		player.sendPacket(iu);
		player.broadcastUserInfo();
		player.sendPacket(ExChangeAttributeOk.STATIC);
	}
}
