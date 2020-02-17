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

import java.util.logging.Logger;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.data.xml.impl.EnsoulData;
import org.l2jbr.gameserver.enums.PrivateStoreType;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.ensoul.EnsoulOption;
import org.l2jbr.gameserver.model.ensoul.EnsoulStone;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.AbnormalType;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.ensoul.ExEnsoulResult;
import org.l2jbr.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * @author UnAfraid
 */
public class RequestItemEnsoul implements IClientIncomingPacket
{
	private static final Logger LOGGER = Logger.getLogger(IClientIncomingPacket.class.getName());
	private int _itemObjectId;
	private EnsoulItemOption[] _options;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_itemObjectId = packet.readD();
		final int options = packet.readC();
		if ((options > 0) && (options <= 3))
		{
			_options = new EnsoulItemOption[options];
			for (int i = 0; i < options; i++)
			{
				final int type = packet.readC(); // 1 = normal ; 2 = mystic
				final int position = packet.readC();
				final int soulCrystalObjectId = packet.readD();
				final int soulCrystalOption = packet.readD();
				if ((position > 0) && (position < 3) && ((type == 1) || (type == 2)))
				{
					_options[i] = new EnsoulItemOption(type, position, soulCrystalObjectId, soulCrystalOption);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
			return;
		}
		if (player.hasAbnormalType(AbnormalType.FREEZING))
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_FROZEN);
		}
		if (player.isDead())
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_DEAD);
			return;
		}
		if ((player.getActiveTradeList() != null) || player.hasItemRequest())
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_TRADING);
			return;
		}
		if (player.hasAbnormalType(AbnormalType.PARALYZE))
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_PETRIFIED);
			return;
		}
		if (player.isFishing())
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_FISHING);
			return;
		}
		if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_SEATED);
			return;
		}
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player))
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_THE_SOUL_CRYSTAL_SYSTEM_WHILE_IN_BATTLE);
			return;
		}
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_itemObjectId);
		if (item == null)
		{
			LOGGER.warning("Player: " + player + " attempting to ensoul item without having it!");
			return;
		}
		if (!item.isEquipable())
		{
			LOGGER.warning("Player: " + player + " attempting to ensoul non equippable item: " + item + "!");
			return;
		}
		if (!item.isWeapon())
		{
			LOGGER.warning("Player: " + player + " attempting to ensoul item that's not a weapon: " + item + "!");
			return;
		}
		if (item.isCommonItem())
		{
			LOGGER.warning("Player: " + player + " attempting to ensoul common item: " + item + "!");
			return;
		}
		if (item.isShadowItem())
		{
			LOGGER.warning("Player: " + player + " attempting to ensoul shadow item: " + item + "!");
			return;
		}
		if (item.isHeroItem())
		{
			LOGGER.warning("Player: " + player + " attempting to ensoul hero item: " + item + "!");
			return;
		}
		if ((_options == null) || (_options.length == 0))
		{
			LOGGER.warning("Player: " + player + " attempting to ensoul item without any special ability declared!");
			return;
		}
		
		int success = 0;
		final InventoryUpdate iu = new InventoryUpdate();
		for (EnsoulItemOption itemOption : _options)
		{
			final int position = itemOption.getPosition() - 1;
			final ItemInstance soulCrystal = player.getInventory().getItemByObjectId(itemOption.getSoulCrystalObjectId());
			if (soulCrystal == null)
			{
				player.sendPacket(SystemMessageId.INVALID_SOUL_CRYSTAL);
				continue;
			}
			
			final EnsoulStone stone = EnsoulData.getInstance().getStone(soulCrystal.getId());
			if (stone == null)
			{
				continue;
			}
			
			if (!stone.getOptions().contains(itemOption.getSoulCrystalOption()))
			{
				LOGGER.warning("Player: " + player + " attempting to ensoul item option that stone doesn't contains!");
				continue;
			}
			
			final EnsoulOption option = EnsoulData.getInstance().getOption(itemOption.getSoulCrystalOption());
			if (option == null)
			{
				LOGGER.warning("Player: " + player + " attempting to ensoul item option that doesn't exists!");
				continue;
			}
			
			ItemHolder fee;
			if (itemOption.getType() == 1)
			{
				// Normal Soul Crystal
				fee = EnsoulData.getInstance().getEnsoulFee(item.getItem().getCrystalType(), position);
				if ((itemOption.getPosition() == 1) || (itemOption.getPosition() == 2))
				{
					if (item.getSpecialAbility(position) != null)
					{
						fee = EnsoulData.getInstance().getResoulFee(item.getItem().getCrystalType(), position);
					}
				}
			}
			else if (itemOption.getType() == 2)
			{
				// Mystic Soul Crystal
				fee = EnsoulData.getInstance().getEnsoulFee(item.getItem().getCrystalType(), position);
				if (itemOption.getPosition() == 1)
				{
					if (item.getAdditionalSpecialAbility(position) != null)
					{
						fee = EnsoulData.getInstance().getResoulFee(item.getItem().getCrystalType(), position);
					}
				}
			}
			else
			{
				LOGGER.warning("Player: " + player + " attempting to ensoul item option with unhandled type: " + itemOption.getType() + "!");
				continue;
			}
			
			if (fee == null)
			{
				LOGGER.warning("Player: " + player + " attempting to ensoul item option that doesn't exists! (unknown fee)");
				continue;
			}
			
			final ItemInstance gemStones = player.getInventory().getItemByItemId(fee.getId());
			if ((gemStones == null) || (gemStones.getCount() < fee.getCount()))
			{
				continue;
			}
			
			if (player.destroyItem("EnsoulOption", soulCrystal, 1, player, true) && player.destroyItem("EnsoulOption", gemStones, fee.getCount(), player, true))
			{
				item.addSpecialAbility(option, position, stone.getSlotType(), true);
				success = 1;
			}
			
			iu.addModifiedItem(soulCrystal);
			iu.addModifiedItem(gemStones);
			iu.addModifiedItem(item);
		}
		player.sendInventoryUpdate(iu);
		if (item.isEquipped())
		{
			item.applySpecialAbilities();
		}
		player.sendPacket(new ExEnsoulResult(success, item));
		
		item.updateDatabase(true);
	}
	
	static class EnsoulItemOption
	{
		private final int _type;
		private final int _position;
		private final int _soulCrystalObjectId;
		private final int _soulCrystalOption;
		
		EnsoulItemOption(int type, int position, int soulCrystalObjectId, int soulCrystalOption)
		{
			_type = type;
			_position = position;
			_soulCrystalObjectId = soulCrystalObjectId;
			_soulCrystalOption = soulCrystalOption;
		}
		
		public int getType()
		{
			return _type;
		}
		
		public int getPosition()
		{
			return _position;
		}
		
		public int getSoulCrystalObjectId()
		{
			return _soulCrystalObjectId;
		}
		
		public int getSoulCrystalOption()
		{
			return _soulCrystalOption;
		}
	}
}
