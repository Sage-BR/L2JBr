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
package handlers.telnethandlers.player;

import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.telnet.ITelnetCommand;
import org.l2jbr.gameserver.util.GMAudit;
import org.l2jbr.gameserver.util.Util;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Enchant implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "enchant";
	}
	
	@Override
	public String getUsage()
	{
		return "Enchant <player name> <item id> [item amount] [item enchant]";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length < 3) || args[0].isEmpty() || !Util.isDigit(args[1]) || !Util.isDigit(args[2]))
		{
			return null;
		}
		final PlayerInstance player = World.getInstance().getPlayer(args[0]);
		if (player != null)
		{
			int itemType = Integer.parseInt(args[1]);
			int enchant = Integer.parseInt(args[2]);
			enchant = Math.min(enchant, 127);
			enchant = Math.max(enchant, 0);
			
			switch (itemType)
			{
				case 1:
				{
					itemType = Inventory.PAPERDOLL_HEAD;
					break;
				}
				case 2:
				{
					itemType = Inventory.PAPERDOLL_CHEST;
					break;
				}
				case 3:
				{
					itemType = Inventory.PAPERDOLL_GLOVES;
					break;
				}
				case 4:
				{
					itemType = Inventory.PAPERDOLL_FEET;
					break;
				}
				case 5:
				{
					itemType = Inventory.PAPERDOLL_LEGS;
					break;
				}
				case 6:
				{
					itemType = Inventory.PAPERDOLL_RHAND;
					break;
				}
				case 7:
				{
					itemType = Inventory.PAPERDOLL_LHAND;
					break;
				}
				case 8:
				{
					itemType = Inventory.PAPERDOLL_LEAR;
					break;
				}
				case 9:
				{
					itemType = Inventory.PAPERDOLL_REAR;
					break;
				}
				case 10:
				{
					itemType = Inventory.PAPERDOLL_LFINGER;
					break;
				}
				case 11:
				{
					itemType = Inventory.PAPERDOLL_RFINGER;
					break;
				}
				case 12:
				{
					itemType = Inventory.PAPERDOLL_NECK;
					break;
				}
				case 13:
				{
					itemType = Inventory.PAPERDOLL_UNDER;
					break;
				}
				case 14:
				{
					itemType = Inventory.PAPERDOLL_CLOAK;
					break;
				}
				case 15:
				{
					itemType = Inventory.PAPERDOLL_BELT;
					break;
				}
				default:
				{
					itemType = 0;
					break;
				}
			}
			final boolean success = setEnchant(player, enchant, itemType);
			return success ? "Item has been successfully enchanted." : "Failed to enchant player's item!";
		}
		return "Couldn't find player with such name.";
	}
	
	private boolean setEnchant(PlayerInstance player, int ench, int armorType)
	{
		// now we need to find the equipped weapon of the targeted character...
		int curEnchant = 0; // display purposes only
		ItemInstance itemInstance = null;
		
		// only attempt to enchant if there is a weapon equipped
		ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
		if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == armorType))
		{
			itemInstance = parmorInstance;
		}
		else
		{
			// for bows/crossbows and double handed weapons
			parmorInstance = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == Inventory.PAPERDOLL_RHAND))
			{
				itemInstance = parmorInstance;
			}
		}
		
		if (itemInstance != null)
		{
			curEnchant = itemInstance.getEnchantLevel();
			
			// set enchant value
			player.getInventory().unEquipItemInSlot(armorType);
			itemInstance.setEnchantLevel(ench);
			player.getInventory().equipItem(itemInstance);
			
			// send packets
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(itemInstance);
			player.sendPacket(iu);
			player.broadcastUserInfo();
			
			// informations
			player.sendMessage("Changed enchantment of " + player.getName() + "'s " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
			player.sendMessage("Admin has changed the enchantment of your " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
			
			// log
			GMAudit.auditGMAction("TelnetAdmin", "enchant", player.getName(), itemInstance.getItem().getName() + "(" + itemInstance.getObjectId() + ") from " + curEnchant + " to " + ench);
			return true;
		}
		return false;
	}
}
