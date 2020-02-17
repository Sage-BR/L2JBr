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
package org.l2jbr.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.enums.MailType;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.entity.Message;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.itemcontainer.Mail;
import org.l2jbr.gameserver.util.Util;

/**
 * @author Mobius
 */
public class CustomMailManager
{
	private static final Logger LOGGER = Logger.getLogger(CustomMailManager.class.getName());
	
	// SQL Statements
	private static final String READ_SQL = "SELECT * FROM custom_mail";
	private static final String DELETE_SQL = "DELETE FROM custom_mail WHERE date=? AND receiver=?";
	
	protected CustomMailManager()
	{
		ThreadPool.scheduleAtFixedRate(() ->
		{
			try (Connection con = DatabaseFactory.getConnection();
				Statement ps = con.createStatement();
				ResultSet rs = ps.executeQuery(READ_SQL))
			{
				while (rs.next())
				{
					final int playerId = rs.getInt("receiver");
					final PlayerInstance player = World.getInstance().getPlayer(playerId);
					if ((player != null) && player.isOnline())
					{
						// Create message.
						final String items = rs.getString("items");
						final Message msg = new Message(playerId, rs.getString("subject"), rs.getString("message"), items.length() > 0 ? MailType.PRIME_SHOP_GIFT : MailType.REGULAR);
						final List<ItemHolder> itemHolders = new ArrayList<>();
						for (String str : items.split(";"))
						{
							if (str.contains(" "))
							{
								final String itemId = str.split(" ")[0];
								final String itemCount = str.split(" ")[1];
								if (Util.isDigit(itemId) && Util.isDigit(itemCount))
								{
									itemHolders.add(new ItemHolder(Integer.parseInt(itemId), Long.parseLong(itemCount)));
								}
							}
							else if (Util.isDigit(str))
							{
								itemHolders.add(new ItemHolder(Integer.parseInt(str), 1));
							}
						}
						if (!itemHolders.isEmpty())
						{
							final Mail attachments = msg.createAttachments();
							for (ItemHolder itemHolder : itemHolders)
							{
								attachments.addItem("Custom-Mail", itemHolder.getId(), itemHolder.getCount(), null, null);
							}
						}
						
						// Delete entry from database.
						try (PreparedStatement stmt = con.prepareStatement(DELETE_SQL))
						{
							stmt.setString(1, rs.getString("date"));
							stmt.setInt(2, playerId);
							stmt.execute();
						}
						catch (SQLException e)
						{
							LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error deleting entry from database: ", e);
						}
						
						// Send message.
						MailManager.getInstance().sendMessage(msg);
						LOGGER.info(getClass().getSimpleName() + ": Message sent to " + player.getName() + ".");
					}
				}
			}
			catch (SQLException e)
			{
				LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error reading from database: ", e);
			}
		}, Config.CUSTOM_MAIL_MANAGER_DELAY, Config.CUSTOM_MAIL_MANAGER_DELAY);
		
		LOGGER.info(getClass().getSimpleName() + ": Enabled.");
	}
	
	public static CustomMailManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CustomMailManager INSTANCE = new CustomMailManager();
	}
}
