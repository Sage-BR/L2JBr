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

import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.data.xml.impl.EnsoulData;
import org.l2jbr.gameserver.data.xml.impl.MultisellData;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.enums.AttributeType;
import org.l2jbr.gameserver.enums.SpecialItemType;
import org.l2jbr.gameserver.model.ItemInfo;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.ensoul.EnsoulOption;
import org.l2jbr.gameserver.model.holders.ItemChanceHolder;
import org.l2jbr.gameserver.model.holders.MultisellEntryHolder;
import org.l2jbr.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.enchant.attribute.AttributeHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExPCCafePointInfo;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.network.serverpackets.UserInfo;

/**
 * The Class MultiSellChoose.
 */
public class MultiSellChoose implements IClientIncomingPacket
{
	private int _listId;
	private int _entryId;
	private long _amount;
	private int _enchantLevel;
	private int _augmentOption1;
	private int _augmentOption2;
	private short _attackAttribute;
	private short _attributePower;
	private short _fireDefence;
	private short _waterDefence;
	private short _windDefence;
	private short _earthDefence;
	private short _holyDefence;
	private short _darkDefence;
	private EnsoulOption[] _soulCrystalOptions;
	private EnsoulOption[] _soulCrystalSpecialOptions;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_listId = packet.readD();
		_entryId = packet.readD();
		_amount = packet.readQ();
		_enchantLevel = packet.readH();
		_augmentOption1 = packet.readD();
		_augmentOption2 = packet.readD();
		_attackAttribute = (short) packet.readH();
		_attributePower = (short) packet.readH();
		_fireDefence = (short) packet.readH();
		_waterDefence = (short) packet.readH();
		_windDefence = (short) packet.readH();
		_earthDefence = (short) packet.readH();
		_holyDefence = (short) packet.readH();
		_darkDefence = (short) packet.readH();
		_soulCrystalOptions = new EnsoulOption[packet.readC()]; // Ensoul size
		for (int i = 0; i < _soulCrystalOptions.length; i++)
		{
			final int ensoulId = packet.readD(); // Ensoul option id
			_soulCrystalOptions[i] = EnsoulData.getInstance().getOption(ensoulId);
		}
		_soulCrystalSpecialOptions = new EnsoulOption[packet.readC()]; // Special ensoul size
		for (int i = 0; i < _soulCrystalSpecialOptions.length; i++)
		{
			final int ensoulId = packet.readD(); // Special ensoul option id.
			_soulCrystalSpecialOptions[i] = EnsoulData.getInstance().getOption(ensoulId);
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
		
		if (!client.getFloodProtectors().getMultiSell().tryPerformAction("multisell choose"))
		{
			player.setMultiSell(null);
			return;
		}
		
		if ((_amount < 1) || (_amount > 10000)) // 999 999 is client max.
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		
		PreparedMultisellListHolder list = player.getMultiSell();
		if ((list == null) || (list.getId() != _listId))
		{
			player.setMultiSell(null);
			return;
		}
		
		final Npc npc = player.getLastFolkNPC();
		if (!list.isNpcAllowed(-1))
		{
			if ((npc == null) //
				|| !list.isNpcAllowed(npc.getId()) //
				|| !list.checkNpcObjectId(npc.getObjectId()) //
				|| (player.getInstanceId() != npc.getInstanceId()) //
				|| !player.isInsideRadius3D(npc, Npc.INTERACTION_DISTANCE))
			{
				if (player.isGM())
				{
					player.sendMessage("Multisell " + _listId + " is restricted. Under current conditions cannot be used. Only GMs are allowed to use it.");
				}
				else
				{
					player.setMultiSell(null);
					return;
				}
			}
		}
		
		if (((_soulCrystalOptions != null) && CommonUtil.contains(_soulCrystalOptions, null)) || ((_soulCrystalSpecialOptions != null) && CommonUtil.contains(_soulCrystalSpecialOptions, null)))
		{
			LOGGER.severe("Character: " + player.getName() + " requested multisell entry with invalid soul crystal options. Multisell: " + _listId + " entry: " + _entryId);
			player.setMultiSell(null);
			return;
		}
		
		final MultisellEntryHolder entry = list.getEntries().get(_entryId - 1); // Entry Id begins from 1. We currently use entry IDs as index pointer.
		if (entry == null)
		{
			LOGGER.severe("Character: " + player.getName() + " requested inexistant prepared multisell entry. Multisell: " + _listId + " entry: " + _entryId);
			player.setMultiSell(null);
			return;
		}
		
		if (!entry.isStackable() && (_amount > 1))
		{
			LOGGER.severe("Character: " + player.getName() + " is trying to set amount > 1 on non-stackable multisell. Id: " + _listId + " entry: " + _entryId);
			player.setMultiSell(null);
			return;
		}
		
		final ItemInfo itemEnchantment = list.getItemEnchantment(_entryId - 1); // Entry Id begins from 1. We currently use entry IDs as index pointer.
		
		// Validate the requested item with its full stats.
		//@formatter:off
		if ((itemEnchantment != null) && ((_amount > 1)
			|| (itemEnchantment.getEnchantLevel() != _enchantLevel)
			|| (itemEnchantment.getAttackElementType() != _attackAttribute) 
			|| (itemEnchantment.getAttackElementPower() != _attributePower)
			|| (itemEnchantment.getAttributeDefence(AttributeType.FIRE) != _fireDefence)
			|| (itemEnchantment.getAttributeDefence(AttributeType.WATER) != _waterDefence)
			|| (itemEnchantment.getAttributeDefence(AttributeType.WIND) != _windDefence)
			|| (itemEnchantment.getAttributeDefence(AttributeType.EARTH) != _earthDefence)
			|| (itemEnchantment.getAttributeDefence(AttributeType.HOLY) != _holyDefence)
			|| (itemEnchantment.getAttributeDefence(AttributeType.DARK) != _darkDefence)
			|| ((itemEnchantment.getAugmentation() == null) && ((_augmentOption1 != 0) || (_augmentOption2 != 0)))
			|| ((itemEnchantment.getAugmentation() != null) && ((itemEnchantment.getAugmentation().getOption1Id() != _augmentOption1) || (itemEnchantment.getAugmentation().getOption2Id() != _augmentOption2)))
			|| ((_soulCrystalOptions != null) && itemEnchantment.getSoulCrystalOptions().stream().anyMatch(e -> !CommonUtil.contains(_soulCrystalOptions, e)))
			|| ((_soulCrystalOptions == null) && !itemEnchantment.getSoulCrystalOptions().isEmpty())
			|| ((_soulCrystalSpecialOptions != null) && itemEnchantment.getSoulCrystalSpecialOptions().stream().anyMatch(e -> !CommonUtil.contains(_soulCrystalSpecialOptions, e)))
			|| ((_soulCrystalSpecialOptions == null) && !itemEnchantment.getSoulCrystalSpecialOptions().isEmpty())
			))
		//@formatter:on
		{
			LOGGER.severe("Character: " + player.getName() + " is trying to upgrade equippable item, but the stats doesn't match. Id: " + _listId + " entry: " + _entryId);
			player.setMultiSell(null);
			return;
		}
		
		final Clan clan = player.getClan();
		final PlayerInventory inventory = player.getInventory();
		
		try
		{
			int slots = 0;
			int weight = 0;
			for (ItemChanceHolder product : entry.getProducts())
			{
				if (product.getId() < 0)
				{
					// Check if clan exists for clan reputation products.
					if ((clan == null) && (SpecialItemType.CLAN_REPUTATION.getClientId() == product.getId()))
					{
						player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
						return;
					}
					
					continue;
				}
				
				final Item template = ItemTable.getInstance().getTemplate(product.getId());
				if (template == null)
				{
					player.setMultiSell(null);
					return;
				}
				
				final long totalCount = Math.multiplyExact(list.getProductCount(product), _amount);
				if ((totalCount < 1) || (totalCount > Integer.MAX_VALUE))
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
					return;
				}
				
				if (!template.isStackable() || (player.getInventory().getItemByItemId(product.getId()) == null))
				{
					slots++;
				}
				
				weight += totalCount * template.getWeight();
				
				if (!inventory.validateWeight(weight))
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
					return;
				}
				
				if ((slots > 0) && !inventory.validateCapacity(slots))
				{
					player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
					return;
				}
				
				// If this is a chance multisell, reset slots and weight because only one item should be seleted. We just need to check if conditions for every item is met.
				if (list.isChanceMultisell())
				{
					slots = 0;
					weight = 0;
				}
			}
			
			// Check for enchanted item if its present in the inventory.
			if ((itemEnchantment != null) && (inventory.getItemByObjectId(itemEnchantment.getObjectId()) == null))
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.YOU_NEED_A_N_S1);
				sm.addItemName(itemEnchantment.getItem().getId());
				player.sendPacket(sm);
				return;
			}
			
			// Check for enchanted level and ingredient count requirements.
			for (ItemChanceHolder ingredient : entry.getIngredients())
			{
				if (ingredient.getEnchantmentLevel() > 0)
				{
					int found = 0;
					for (ItemInstance item : inventory.getAllItemsByItemId(ingredient.getId(), ingredient.getEnchantmentLevel()))
					{
						if (item.getEnchantLevel() >= ingredient.getEnchantmentLevel())
						{
							found++;
						}
					}
					
					if (found < ingredient.getCount())
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_NEED_A_N_S1);
						sm.addString("+" + ingredient.getEnchantmentLevel() + " " + ItemTable.getInstance().getTemplate(ingredient.getId()).getName());
						player.sendPacket(sm);
						return;
					}
				}
				else if (!checkIngredients(player, list, inventory, clan, ingredient.getId(), Math.multiplyExact(ingredient.getCount(), _amount)))
				{
					return;
				}
			}
			
			final InventoryUpdate iu = new InventoryUpdate();
			boolean itemEnchantmentProcessed = (itemEnchantment == null);
			
			// Take all ingredients
			for (ItemChanceHolder ingredient : entry.getIngredients())
			{
				if (ingredient.isMaintainIngredient())
				{
					continue;
				}
				
				final long totalCount = Math.multiplyExact(list.getIngredientCount(ingredient), _amount);
				final SpecialItemType specialItem = SpecialItemType.getByClientId(ingredient.getId());
				if (specialItem != null)
				{
					// Take special item.
					switch (specialItem)
					{
						case CLAN_REPUTATION:
						{
							if (clan != null)
							{
								clan.takeReputationScore((int) totalCount, true);
								SystemMessage smsg = new SystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION);
								smsg.addLong(totalCount);
								player.sendPacket(smsg);
							}
							break;
						}
						case FAME:
						{
							player.setFame(player.getFame() - (int) totalCount);
							player.sendPacket(new UserInfo(player));
							// player.sendPacket(new ExBrExtraUserInfo(player));
							break;
						}
						case RAIDBOSS_POINTS:
						{
							player.setRaidbossPoints(player.getRaidbossPoints() - (int) totalCount);
							player.sendPacket(new UserInfo(player));
							player.sendPacket(new SystemMessage(SystemMessageId.YOU_CONSUMED_S1_RAID_POINTS).addLong(totalCount));
							break;
						}
						case PC_CAFE_POINTS:
						{
							player.setPcCafePoints((int) (player.getPcCafePoints() - totalCount));
							player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), (int) -totalCount, 1));
							break;
						}
						default:
						{
							LOGGER.severe("Character: " + player.getName() + " has suffered possible item loss by using multisell " + _listId + " which has non-implemented special ingredient with id: " + ingredient.getId() + ".");
							return;
						}
					}
				}
				else if (ingredient.getEnchantmentLevel() > 0)
				{
					// Take the enchanted item.
					final ItemInstance destroyedItem = inventory.destroyItem("Multisell", inventory.getAllItemsByItemId(ingredient.getId(), ingredient.getEnchantmentLevel()).iterator().next(), totalCount, player, npc);
					if (destroyedItem != null)
					{
						itemEnchantmentProcessed = true;
						iu.addItem(destroyedItem);
					}
					else
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.YOU_NEED_A_N_S1);
						sm.addItemName(ingredient.getId());
						player.sendPacket(sm);
						return;
					}
				}
				else if (!itemEnchantmentProcessed && (itemEnchantment != null) && (itemEnchantment.getItem().getId() == ingredient.getId()))
				{
					// Take the enchanted item.
					final ItemInstance destroyedItem = inventory.destroyItem("Multisell", itemEnchantment.getObjectId(), totalCount, player, npc);
					if (destroyedItem != null)
					{
						itemEnchantmentProcessed = true;
						iu.addItem(destroyedItem);
					}
					else
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.YOU_NEED_A_N_S1);
						sm.addItemName(ingredient.getId());
						player.sendPacket(sm);
						return;
					}
				}
				else
				{
					// Take a regular item.
					final ItemInstance destroyedItem = inventory.destroyItemByItemId("Multisell", ingredient.getId(), totalCount, player, npc);
					if (destroyedItem != null)
					{
						iu.addItem(destroyedItem);
					}
					else
					{
						SystemMessage sm = new SystemMessage(SystemMessageId.YOU_NEED_S2_S1_S);
						sm.addItemName(ingredient.getId());
						sm.addLong(totalCount);
						player.sendPacket(sm);
						return;
					}
				}
			}
			
			// Generate the appropriate items
			List<ItemChanceHolder> products = entry.getProducts();
			if (list.isChanceMultisell())
			{
				final ItemChanceHolder randomProduct = ItemChanceHolder.getRandomHolder(entry.getProducts());
				products = randomProduct != null ? Collections.singletonList(randomProduct) : Collections.emptyList();
			}
			
			for (ItemChanceHolder product : products)
			{
				final long totalCount = Math.multiplyExact(list.getProductCount(product), _amount);
				final SpecialItemType specialItem = SpecialItemType.getByClientId(product.getId());
				if (specialItem != null)
				{
					// Give special item.
					switch (specialItem)
					{
						case CLAN_REPUTATION:
						{
							if (clan != null)
							{
								clan.addReputationScore((int) totalCount, true);
							}
							break;
						}
						case FAME:
						{
							player.setFame((int) (player.getFame() + totalCount));
							player.sendPacket(new UserInfo(player));
							// player.sendPacket(new ExBrExtraUserInfo(player));
							break;
						}
						case RAIDBOSS_POINTS:
						{
							player.increaseRaidbossPoints((int) totalCount);
							player.sendPacket(new UserInfo(player));
							break;
						}
						default:
						{
							LOGGER.severe("Character: " + player.getName() + " has suffered possible item loss by using multisell " + _listId + " which has non-implemented special product with id: " + product.getId() + ".");
							return;
						}
					}
				}
				else
				{
					// Give item.
					final ItemInstance addedItem = inventory.addItem("Multisell", product.getId(), totalCount, player, npc, false);
					
					// Check if the newly given item should be enchanted.
					if (itemEnchantmentProcessed && list.isMaintainEnchantment() && (itemEnchantment != null) && addedItem.isEquipable() && addedItem.getItem().getClass().equals(itemEnchantment.getItem().getClass()))
					{
						addedItem.setEnchantLevel(itemEnchantment.getEnchantLevel());
						addedItem.setAugmentation(itemEnchantment.getAugmentation(), false);
						if (addedItem.isWeapon())
						{
							if (itemEnchantment.getAttackElementPower() > 0)
							{
								addedItem.setAttribute(new AttributeHolder(AttributeType.findByClientId(itemEnchantment.getAttackElementType()), itemEnchantment.getAttackElementPower()), false);
							}
						}
						else
						{
							if (itemEnchantment.getAttributeDefence(AttributeType.FIRE) > 0)
							{
								addedItem.setAttribute(new AttributeHolder(AttributeType.FIRE, itemEnchantment.getAttributeDefence(AttributeType.FIRE)), false);
							}
							if (itemEnchantment.getAttributeDefence(AttributeType.WATER) > 0)
							{
								addedItem.setAttribute(new AttributeHolder(AttributeType.WATER, itemEnchantment.getAttributeDefence(AttributeType.WATER)), false);
							}
							if (itemEnchantment.getAttributeDefence(AttributeType.WIND) > 0)
							{
								addedItem.setAttribute(new AttributeHolder(AttributeType.WIND, itemEnchantment.getAttributeDefence(AttributeType.WIND)), false);
							}
							if (itemEnchantment.getAttributeDefence(AttributeType.EARTH) > 0)
							{
								addedItem.setAttribute(new AttributeHolder(AttributeType.EARTH, itemEnchantment.getAttributeDefence(AttributeType.EARTH)), false);
							}
							if (itemEnchantment.getAttributeDefence(AttributeType.HOLY) > 0)
							{
								addedItem.setAttribute(new AttributeHolder(AttributeType.HOLY, itemEnchantment.getAttributeDefence(AttributeType.HOLY)), false);
							}
							if (itemEnchantment.getAttributeDefence(AttributeType.DARK) > 0)
							{
								addedItem.setAttribute(new AttributeHolder(AttributeType.DARK, itemEnchantment.getAttributeDefence(AttributeType.DARK)), false);
							}
						}
						if (_soulCrystalOptions != null)
						{
							int pos = -1;
							for (EnsoulOption ensoul : _soulCrystalOptions)
							{
								pos++;
								addedItem.addSpecialAbility(ensoul, pos, 1, false);
							}
						}
						if (_soulCrystalSpecialOptions != null)
						{
							for (EnsoulOption ensoul : _soulCrystalSpecialOptions)
							{
								addedItem.addSpecialAbility(ensoul, 0, 2, false);
							}
						}
						addedItem.updateDatabase(true);
						// Mark that we have already upgraded the item.
						itemEnchantmentProcessed = false;
					}
					if (product.getEnchantmentLevel() > 0)
					{
						addedItem.setEnchantLevel(product.getEnchantmentLevel());
						addedItem.updateDatabase(true);
					}
					
					if (addedItem.getCount() > 1)
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
						sm.addItemName(addedItem.getId());
						sm.addLong(totalCount);
						player.sendPacket(sm);
					}
					else if (addedItem.getEnchantLevel() > 0)
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_S2);
						sm.addLong(addedItem.getEnchantLevel());
						sm.addItemName(addedItem.getId());
						player.sendPacket(sm);
					}
					else
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
						sm.addItemName(addedItem);
						player.sendPacket(sm);
					}
					
					// Inventory update.
					iu.addItem(addedItem);
				}
			}
			
			// Update inventory and weight.
			player.sendInventoryUpdate(iu);
			
			// finally, give the tax to the castle...
			if ((npc != null) && list.isApplyTaxes())
			{
				final OptionalLong taxPaid = entry.getIngredients().stream().filter(i -> i.getId() == Inventory.ADENA_ID).mapToLong(i -> Math.round(i.getCount() * list.getIngredientMultiplier() * list.getTaxRate()) * _amount).reduce(Math::multiplyExact);
				if (taxPaid.isPresent())
				{
					npc.handleTaxPayment(taxPaid.getAsLong());
				}
			}
		}
		catch (ArithmeticException ae)
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		
		// Re-send multisell after successful exchange of inventory-only shown items.
		if (list.isInventoryOnly())
		{
			MultisellData.getInstance().separateAndSend(list.getId(), player, npc, list.isInventoryOnly(), list.getProductMultiplier(), list.getIngredientMultiplier());
		}
	}
	
	/**
	 * @param player
	 * @param list
	 * @param inventory
	 * @param clan
	 * @param ingredientId
	 * @param totalCount
	 * @return {@code false} if ingredient amount is not enough, {@code true} otherwise.
	 */
	private boolean checkIngredients(PlayerInstance player, PreparedMultisellListHolder list, PlayerInventory inventory, Clan clan, int ingredientId, long totalCount)
	{
		final SpecialItemType specialItem = SpecialItemType.getByClientId(ingredientId);
		if (specialItem != null)
		{
			// Check special item.
			switch (specialItem)
			{
				case CLAN_REPUTATION:
				{
					if (clan == null)
					{
						player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
						return false;
					}
					else if (!player.isClanLeader())
					{
						player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_IS_ENABLED);
						return false;
					}
					else if (clan.getReputationScore() < totalCount)
					{
						player.sendPacket(SystemMessageId.THE_CLAN_REPUTATION_IS_TOO_LOW);
						return false;
					}
					return true;
				}
				case FAME:
				{
					if (player.getFame() < totalCount)
					{
						player.sendPacket(SystemMessageId.YOU_DON_T_HAVE_ENOUGH_CLAN_REPUTATION_POINTS_TO_DO_THAT);
						return false;
					}
					return true;
				}
				case RAIDBOSS_POINTS:
				{
					if (player.getRaidbossPoints() < totalCount)
					{
						player.sendPacket(SystemMessageId.NOT_ENOUGH_RAID_POINTS);
						return false;
					}
					return true;
				}
				case PC_CAFE_POINTS:
				{
					if (player.getPcCafePoints() < totalCount)
					{
						player.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_SHORT_OF_PA_POINTS));
						return false;
					}
					return true;
				}
				default:
				{
					LOGGER.severe("Multisell: " + _listId + " is using a non-implemented special ingredient with id: " + ingredientId + ".");
					return false;
				}
			}
		}
		// Check if the necessary items are there. If list maintains enchantment, allow all enchanted items, otherwise only unenchanted. TODO: Check how retail does it.
		else if (inventory.getInventoryItemCount(ingredientId, list.isMaintainEnchantment() ? -1 : 0, false) < totalCount)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.YOU_NEED_S2_S1_S);
			sm.addItemName(ingredientId);
			sm.addLong(totalCount);
			player.sendPacket(sm);
			return false;
		}
		
		return true;
	}
}