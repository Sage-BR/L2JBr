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

import java.util.StringTokenizer;

import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.handler.IItemHandler;
import org.l2jbr.gameserver.handler.ItemHandler;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.serverpackets.ExAdenaInvenCount;
import org.l2jbr.gameserver.network.serverpackets.GMViewItemList;
import org.l2jbr.gameserver.util.BuilderUtil;

/**
 * This class handles following admin commands: - itemcreate = show menu - create_item <id> [num] = creates num items with respective id, if num is not specified, assumes 1.
 * @version $Revision: 1.2.2.2.2.3 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminCreateItem implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_itemcreate",
		"admin_create_item",
		"admin_create_coin",
		"admin_give_item_target",
		"admin_give_item_to_all",
		"admin_delete_item",
		"admin_use_item"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		if (command.equals("admin_itemcreate"))
		{
			AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_create_item"))
		{
			try
			{
				final String val = command.substring(17);
				final StringTokenizer st = new StringTokenizer(val);
				if (st.countTokens() == 2)
				{
					final String id = st.nextToken();
					final int idval = Integer.parseInt(id);
					final String num = st.nextToken();
					final long numval = Long.parseLong(num);
					createItem(activeChar, activeChar, idval, numval);
				}
				else if (st.countTokens() == 1)
				{
					final String id = st.nextToken();
					final int idval = Integer.parseInt(id);
					createItem(activeChar, activeChar, idval, 1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //create_item <itemId> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				BuilderUtil.sendSysMessage(activeChar, "Specify a valid number.");
			}
			AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_create_coin"))
		{
			try
			{
				final String val = command.substring(17);
				final StringTokenizer st = new StringTokenizer(val);
				if (st.countTokens() == 2)
				{
					final String name = st.nextToken();
					final int idval = getCoinId(name);
					if (idval > 0)
					{
						final String num = st.nextToken();
						final long numval = Long.parseLong(num);
						createItem(activeChar, activeChar, idval, numval);
					}
				}
				else if (st.countTokens() == 1)
				{
					final String name = st.nextToken();
					final int idval = getCoinId(name);
					createItem(activeChar, activeChar, idval, 1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //create_coin <name> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				BuilderUtil.sendSysMessage(activeChar, "Specify a valid number.");
			}
			AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_give_item_target"))
		{
			try
			{
				final WorldObject target = activeChar.getTarget();
				if ((target == null) || !target.isPlayer())
				{
					BuilderUtil.sendSysMessage(activeChar, "Invalid target.");
					return false;
				}
				
				final String val = command.substring(22);
				final StringTokenizer st = new StringTokenizer(val);
				if (st.countTokens() == 2)
				{
					final String id = st.nextToken();
					final int idval = Integer.parseInt(id);
					final String num = st.nextToken();
					final long numval = Long.parseLong(num);
					createItem(activeChar, (PlayerInstance) target, idval, numval);
				}
				else if (st.countTokens() == 1)
				{
					final String id = st.nextToken();
					final int idval = Integer.parseInt(id);
					createItem(activeChar, (PlayerInstance) target, idval, 1);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //give_item_target <itemId> [amount]");
			}
			catch (NumberFormatException nfe)
			{
				BuilderUtil.sendSysMessage(activeChar, "Specify a valid number.");
			}
			AdminHtml.showAdminHtml(activeChar, "itemcreation.htm");
		}
		else if (command.startsWith("admin_give_item_to_all"))
		{
			final String val = command.substring(22);
			final StringTokenizer st = new StringTokenizer(val);
			int idval = 0;
			long numval = 0;
			if (st.countTokens() == 2)
			{
				final String id = st.nextToken();
				idval = Integer.parseInt(id);
				final String num = st.nextToken();
				numval = Long.parseLong(num);
			}
			else if (st.countTokens() == 1)
			{
				final String id = st.nextToken();
				idval = Integer.parseInt(id);
				numval = 1;
			}
			int counter = 0;
			final Item template = ItemTable.getInstance().getTemplate(idval);
			if (template == null)
			{
				BuilderUtil.sendSysMessage(activeChar, "This item doesn't exist.");
				return false;
			}
			if ((numval > 10) && !template.isStackable())
			{
				BuilderUtil.sendSysMessage(activeChar, "This item does not stack - Creation aborted.");
				return false;
			}
			for (PlayerInstance onlinePlayer : World.getInstance().getPlayers())
			{
				if ((activeChar != onlinePlayer) && onlinePlayer.isOnline() && ((onlinePlayer.getClient() != null) && !onlinePlayer.getClient().isDetached()))
				{
					onlinePlayer.getInventory().addItem("Admin", idval, numval, onlinePlayer, activeChar);
					onlinePlayer.sendMessage("Admin spawned " + numval + " " + template.getName() + " in your inventory.");
					counter++;
				}
			}
			activeChar.sendMessage(counter + " players rewarded with " + template.getName());
		}
		else if (command.startsWith("admin_delete_item"))
		{
			final String val = command.substring(18);
			final StringTokenizer st = new StringTokenizer(val);
			int idval = 0;
			long numval = 0;
			if (st.countTokens() == 2)
			{
				final String id = st.nextToken();
				idval = Integer.parseInt(id);
				final String num = st.nextToken();
				numval = Long.parseLong(num);
			}
			else if (st.countTokens() == 1)
			{
				final String id = st.nextToken();
				idval = Integer.parseInt(id);
				numval = 1;
			}
			final ItemInstance item = (ItemInstance) World.getInstance().findObject(idval);
			final int ownerId = item.getOwnerId();
			if (ownerId > 0)
			{
				final PlayerInstance player = World.getInstance().getPlayer(ownerId);
				if (player == null)
				{
					BuilderUtil.sendSysMessage(activeChar, "Player is not online.");
					return false;
				}
				
				if (numval == 0)
				{
					numval = item.getCount();
				}
				
				player.getInventory().destroyItem("AdminDelete", idval, numval, activeChar, null);
				activeChar.sendPacket(new GMViewItemList(1, player));
				BuilderUtil.sendSysMessage(activeChar, "Item deleted.");
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "Item doesn't have owner.");
				return false;
			}
		}
		else if (command.startsWith("admin_use_item"))
		{
			final String val = command.substring(15);
			final int idval = Integer.parseInt(val);
			final ItemInstance item = (ItemInstance) World.getInstance().findObject(idval);
			final int ownerId = item.getOwnerId();
			if (ownerId > 0)
			{
				final PlayerInstance player = World.getInstance().getPlayer(ownerId);
				if (player == null)
				{
					BuilderUtil.sendSysMessage(activeChar, "Player is not online.");
					return false;
				}
				
				// equip
				if (item.isEquipable())
				{
					player.useEquippableItem(item, false);
				}
				else
				{
					final IItemHandler ih = ItemHandler.getInstance().getHandler(item.getEtcItem());
					if (ih != null)
					{
						ih.useItem(player, item, false);
					}
				}
				activeChar.sendPacket(new GMViewItemList(1, player));
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "Item doesn't have owner.");
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
	
	private void createItem(PlayerInstance activeChar, PlayerInstance target, int id, long num)
	{
		final Item template = ItemTable.getInstance().getTemplate(id);
		if (template == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "This item doesn't exist.");
			return;
		}
		if ((num > 10) && !template.isStackable())
		{
			BuilderUtil.sendSysMessage(activeChar, "This item does not stack - Creation aborted.");
			return;
		}
		
		target.getInventory().addItem("Admin", id, num, activeChar, null);
		
		if (activeChar != target)
		{
			target.sendMessage("Admin spawned " + num + " " + template.getName() + " in your inventory.");
		}
		BuilderUtil.sendSysMessage(activeChar, "You have spawned " + num + " " + template.getName() + "(" + id + ") in " + target.getName() + " inventory.");
		target.sendPacket(new ExAdenaInvenCount(target));
	}
	
	private int getCoinId(String name)
	{
		int id;
		if (name.equalsIgnoreCase("adena"))
		{
			id = 57;
		}
		else if (name.equalsIgnoreCase("ancientadena"))
		{
			id = 5575;
		}
		else if (name.equalsIgnoreCase("festivaladena"))
		{
			id = 6673;
		}
		else if (name.equalsIgnoreCase("blueeva"))
		{
			id = 4355;
		}
		else if (name.equalsIgnoreCase("goldeinhasad"))
		{
			id = 4356;
		}
		else if (name.equalsIgnoreCase("silvershilen"))
		{
			id = 4357;
		}
		else if (name.equalsIgnoreCase("bloodypaagrio"))
		{
			id = 4358;
		}
		else if (name.equalsIgnoreCase("fantasyislecoin"))
		{
			id = 13067;
		}
		else
		{
			id = 0;
		}
		
		return id;
	}
}
