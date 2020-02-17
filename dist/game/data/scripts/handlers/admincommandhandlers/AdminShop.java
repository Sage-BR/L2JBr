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

import java.util.logging.Logger;

import org.l2jbr.gameserver.data.xml.impl.BuyListData;
import org.l2jbr.gameserver.data.xml.impl.MultisellData;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.buylist.ProductList;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;
import org.l2jbr.gameserver.network.serverpackets.BuyList;
import org.l2jbr.gameserver.network.serverpackets.ExBuySellList;
import org.l2jbr.gameserver.util.BuilderUtil;

/**
 * This class handles following admin commands:
 * <ul>
 * <li>gmshop = shows menu</li>
 * <li>buy id = shows shop with respective id</li>
 * </ul>
 */
public class AdminShop implements IAdminCommandHandler
{
	private static final Logger LOGGER = Logger.getLogger(AdminShop.class.getName());
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_buy",
		"admin_gmshop",
		"admin_multisell",
		"admin_exc_multisell"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		if (command.startsWith("admin_buy"))
		{
			try
			{
				handleBuyRequest(activeChar, command.substring(10));
			}
			catch (IndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Please specify buylist.");
			}
		}
		else if (command.equals("admin_gmshop"))
		{
			AdminHtml.showAdminHtml(activeChar, "gmshops.htm");
		}
		else if (command.startsWith("admin_multisell"))
		{
			try
			{
				int listId = Integer.parseInt(command.substring(16).trim());
				MultisellData.getInstance().separateAndSend(listId, activeChar, null, false);
			}
			catch (NumberFormatException | IndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Please specify multisell list ID.");
			}
		}
		else if (command.toLowerCase().startsWith("admin_exc_multisell"))
		{
			try
			{
				int listId = Integer.parseInt(command.substring(20).trim());
				MultisellData.getInstance().separateAndSend(listId, activeChar, null, true);
			}
			catch (NumberFormatException | IndexOutOfBoundsException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Please specify multisell list ID.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleBuyRequest(PlayerInstance activeChar, String command)
	{
		int val = -1;
		try
		{
			val = Integer.parseInt(command);
		}
		catch (Exception e)
		{
			LOGGER.warning("admin buylist failed:" + command);
		}
		
		final ProductList buyList = BuyListData.getInstance().getBuyList(val);
		if (buyList != null)
		{
			activeChar.sendPacket(new BuyList(buyList, activeChar, 0));
			activeChar.sendPacket(new ExBuySellList(activeChar, false));
		}
		else
		{
			LOGGER.warning("no buylist with id:" + val);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
}
