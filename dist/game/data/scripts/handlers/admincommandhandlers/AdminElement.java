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
package handlers.admincommandhandlers;

import org.l2jbr.gameserver.enums.AttributeType;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.items.enchant.attribute.AttributeHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.util.BuilderUtil;

/**
 * This class handles following admin commands: - delete = deletes target
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/04/11 10:05:56 $
 */
public class AdminElement implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_setlh",
		"admin_setlc",
		"admin_setll",
		"admin_setlg",
		"admin_setlb",
		"admin_setlw",
		"admin_setls"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		int armorType = -1;
		
		if (command.startsWith("admin_setlh"))
		{
			armorType = Inventory.PAPERDOLL_HEAD;
		}
		else if (command.startsWith("admin_setlc"))
		{
			armorType = Inventory.PAPERDOLL_CHEST;
		}
		else if (command.startsWith("admin_setlg"))
		{
			armorType = Inventory.PAPERDOLL_GLOVES;
		}
		else if (command.startsWith("admin_setlb"))
		{
			armorType = Inventory.PAPERDOLL_FEET;
		}
		else if (command.startsWith("admin_setll"))
		{
			armorType = Inventory.PAPERDOLL_LEGS;
		}
		else if (command.startsWith("admin_setlw"))
		{
			armorType = Inventory.PAPERDOLL_RHAND;
		}
		else if (command.startsWith("admin_setls"))
		{
			armorType = Inventory.PAPERDOLL_LHAND;
		}
		
		if (armorType != -1)
		{
			try
			{
				final String[] args = command.split(" ");
				
				final AttributeType type = AttributeType.findByName(args[1]);
				final int value = Integer.parseInt(args[2]);
				if ((type == null) || (value < 0) || (value > 450))
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //setlh/setlc/setlg/setlb/setll/setlw/setls <element> <value>[0-450]");
					return false;
				}
				
				setElement(activeChar, type, value, armorType);
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //setlh/setlc/setlg/setlb/setll/setlw/setls <element>[0-5] <value>[0-450]");
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void setElement(PlayerInstance activeChar, AttributeType type, int value, int armorType)
	{
		// get the target
		WorldObject target = activeChar.getTarget();
		if (target == null)
		{
			target = activeChar;
		}
		PlayerInstance player = null;
		if (target.isPlayer())
		{
			player = (PlayerInstance) target;
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		ItemInstance itemInstance = null;
		
		// only attempt to enchant if there is a weapon equipped
		final ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
		if ((parmorInstance != null) && (parmorInstance.getLocationSlot() == armorType))
		{
			itemInstance = parmorInstance;
		}
		
		if (itemInstance != null)
		{
			String old;
			String current;
			final AttributeHolder element = itemInstance.getAttribute(type);
			if (element == null)
			{
				old = "None";
			}
			else
			{
				old = element.toString();
			}
			
			// set enchant value
			player.getInventory().unEquipItemInSlot(armorType);
			if (type == AttributeType.NONE)
			{
				itemInstance.clearAllAttributes();
			}
			else if (value < 1)
			{
				itemInstance.clearAttribute(type);
			}
			else
			{
				itemInstance.setAttribute(new AttributeHolder(type, value), true);
			}
			player.getInventory().equipItem(itemInstance);
			
			if (itemInstance.getAttributes() == null)
			{
				current = "None";
			}
			else
			{
				current = itemInstance.getAttribute(type).toString();
			}
			
			// send packets
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(itemInstance);
			player.sendInventoryUpdate(iu);
			
			// informations
			BuilderUtil.sendSysMessage(activeChar, "Changed elemental power of " + player.getName() + "'s " + itemInstance.getItem().getName() + " from " + old + " to " + current + ".");
			if (player != activeChar)
			{
				player.sendMessage(activeChar.getName() + " has changed the elemental power of your " + itemInstance.getItem().getName() + " from " + old + " to " + current + ".");
			}
		}
	}
}
