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

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExTryEnchantArtifactResult;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Bonux (bonuxq@gmail.com)
 * @date 09.09.2019
 **/
public class RequestExTryEnchantArtifact implements IClientIncomingPacket
{
	private static final int[] ENCHANT_CHANCES =
	{
		100,
		70,
		70,
		50,
		40,
		40,
		40,
		30,
		30,
		20
	};
	
	private int _targetObjectId = 0;
	private int _count = 0;
	private final Set<Integer> _ingridients = new HashSet<>();
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_targetObjectId = packet.readD();
		_count = packet.readD();
		for (int i = 0; i < _count; i++)
		{
			_ingridients.add(packet.readD());
		}
		return !_ingridients.contains(_targetObjectId);
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.hasBlockActions() || player.isInStoreMode() || player.isProcessingRequest() || player.isFishing() || player.isInTraingCamp() || (_count != _ingridients.size()))
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		final ItemInstance targetItem = player.getInventory().getItemByObjectId(_targetObjectId);
		if (targetItem == null)
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		final Item item = targetItem.getItem();
		final int artifactSlot = item.getArtifactSlot();
		if (artifactSlot <= 0)
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		final int enchantLevel = targetItem.getEnchantLevel();
		
		int needCount = 0;
		if (enchantLevel <= 6)
		{
			needCount = 3;
		}
		else if (enchantLevel <= 9)
		{
			needCount = 2;
		}
		
		if ((needCount == 0) || (needCount != _ingridients.size()))
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		int chance = ENCHANT_CHANCES[enchantLevel];
		if (chance == 0)
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		int minIngridientEnchant = -1;
		if (enchantLevel <= 2)
		{
			minIngridientEnchant = 0;
		}
		else if (enchantLevel <= 6)
		{
			minIngridientEnchant = 2;
		}
		else if (enchantLevel <= 9)
		{
			minIngridientEnchant = 3;
		}
		
		if (minIngridientEnchant == -1)
		{
			player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
			return;
		}
		
		for (int objectId : _ingridients)
		{
			final ItemInstance ingridient = player.getInventory().getItemByObjectId(objectId);
			if ((ingridient == null) || (ingridient.getEnchantLevel() < minIngridientEnchant) || (ingridient.getItem().getArtifactSlot() != artifactSlot))
			{
				player.sendPacket(ExTryEnchantArtifactResult.ERROR_PACKET);
				return;
			}
			player.destroyItem("Artifact", ingridient, 1, player, true);
		}
		
		if (Rnd.get(100) < chance)
		{
			targetItem.setEnchantLevel(enchantLevel + 1);
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(targetItem);
			player.sendPacket(iu);
			player.sendPacket(new SystemMessage(SystemMessageId.ARTIFACT_UPGRADE_SUCCEEDED_AND_YOU_OBTAINED_S1).addItemName(targetItem.getId()));
			player.sendPacket(new ExTryEnchantArtifactResult(ExTryEnchantArtifactResult.SUCCESS, targetItem.getEnchantLevel()));
		}
		else
		{
			player.sendPacket(new SystemMessage(SystemMessageId.FAILED_TO_UPGRADE_ARTIFACT_THE_ITEM_S_UPGRADE_LEVEL_WILL_REMAIN_THE_SAME));
			player.sendPacket(new ExTryEnchantArtifactResult(ExTryEnchantArtifactResult.FAIL, targetItem.getEnchantLevel()));
		}
	}
}
