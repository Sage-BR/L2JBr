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
package org.l2jbr.gameserver.data.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.enums.PrivateStoreType;
import org.l2jbr.gameserver.instancemanager.PlayerCountManager;
import org.l2jbr.gameserver.model.TradeItem;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SellBuffHolder;
import org.l2jbr.gameserver.network.Disconnection;
import org.l2jbr.gameserver.network.GameClient;

public class OfflineTradersTable
{
	private static Logger LOGGER = Logger.getLogger(OfflineTradersTable.class.getName());
	
	// SQL DEFINITIONS
	private static final String SAVE_OFFLINE_STATUS = "INSERT INTO character_offline_trade (`charId`,`time`,`type`,`title`) VALUES (?,?,?,?)";
	private static final String SAVE_ITEMS = "INSERT INTO character_offline_trade_items (`charId`,`item`,`count`,`price`) VALUES (?,?,?,?)";
	private static final String CLEAR_OFFLINE_TABLE = "DELETE FROM character_offline_trade";
	private static final String CLEAR_OFFLINE_TABLE_PLAYER = "DELETE FROM character_offline_trade WHERE `charId`=?";
	private static final String CLEAR_OFFLINE_TABLE_ITEMS = "DELETE FROM character_offline_trade_items";
	private static final String CLEAR_OFFLINE_TABLE_ITEMS_PLAYER = "DELETE FROM character_offline_trade_items WHERE `charId`=?";
	private static final String LOAD_OFFLINE_STATUS = "SELECT * FROM character_offline_trade";
	private static final String LOAD_OFFLINE_ITEMS = "SELECT * FROM character_offline_trade_items WHERE `charId`=?";
	
	protected OfflineTradersTable()
	{
	}
	
	public void storeOffliners()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stm1 = con.prepareStatement(CLEAR_OFFLINE_TABLE);
			PreparedStatement stm2 = con.prepareStatement(CLEAR_OFFLINE_TABLE_ITEMS);
			PreparedStatement stm3 = con.prepareStatement(SAVE_OFFLINE_STATUS);
			PreparedStatement stm_items = con.prepareStatement(SAVE_ITEMS))
		{
			stm1.execute();
			stm2.execute();
			con.setAutoCommit(false); // avoid halfway done
			
			for (PlayerInstance pc : World.getInstance().getPlayers())
			{
				try
				{
					if ((pc.getPrivateStoreType() != PrivateStoreType.NONE) && ((pc.getClient() == null) || pc.getClient().isDetached()))
					{
						stm3.setInt(1, pc.getObjectId()); // Char Id
						stm3.setLong(2, pc.getOfflineStartTime());
						stm3.setInt(3, pc.isSellingBuffs() ? PrivateStoreType.SELL_BUFFS.getId() : pc.getPrivateStoreType().getId()); // store type
						String title = null;
						
						switch (pc.getPrivateStoreType())
						{
							case BUY:
							{
								if (!Config.OFFLINE_TRADE_ENABLE)
								{
									continue;
								}
								title = pc.getBuyList().getTitle();
								for (TradeItem i : pc.getBuyList().getItems())
								{
									stm_items.setInt(1, pc.getObjectId());
									stm_items.setInt(2, i.getItem().getId());
									stm_items.setLong(3, i.getCount());
									stm_items.setLong(4, i.getPrice());
									stm_items.executeUpdate();
									stm_items.clearParameters();
								}
								break;
							}
							case SELL:
							case PACKAGE_SELL:
							{
								if (!Config.OFFLINE_TRADE_ENABLE)
								{
									continue;
								}
								title = pc.getSellList().getTitle();
								if (pc.isSellingBuffs())
								{
									for (SellBuffHolder holder : pc.getSellingBuffs())
									{
										stm_items.setInt(1, pc.getObjectId());
										stm_items.setInt(2, holder.getSkillId());
										stm_items.setLong(3, 0);
										stm_items.setLong(4, holder.getPrice());
										stm_items.executeUpdate();
										stm_items.clearParameters();
									}
								}
								else
								{
									for (TradeItem i : pc.getSellList().getItems())
									{
										stm_items.setInt(1, pc.getObjectId());
										stm_items.setInt(2, i.getObjectId());
										stm_items.setLong(3, i.getCount());
										stm_items.setLong(4, i.getPrice());
										stm_items.executeUpdate();
										stm_items.clearParameters();
									}
								}
								break;
							}
							case MANUFACTURE:
							{
								if (!Config.OFFLINE_CRAFT_ENABLE)
								{
									continue;
								}
								title = pc.getStoreName();
								for (Entry<Integer, Long> entry : pc.getManufactureItems().entrySet())
								{
									stm_items.setInt(1, pc.getObjectId());
									stm_items.setInt(2, entry.getKey());
									stm_items.setLong(3, 0);
									stm_items.setLong(4, entry.getValue());
									stm_items.executeUpdate();
									stm_items.clearParameters();
								}
								break;
							}
						}
						stm3.setString(4, title);
						stm3.executeUpdate();
						stm3.clearParameters();
						con.commit(); // flush
					}
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while saving offline trader: " + pc.getObjectId() + " " + e, e);
				}
			}
			LOGGER.info(getClass().getSimpleName() + ": Offline traders stored.");
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while saving offline traders: " + e, e);
		}
	}
	
	public void restoreOfflineTraders()
	{
		LOGGER.info(getClass().getSimpleName() + ": Loading offline traders...");
		int nTraders = 0;
		try (Connection con = DatabaseFactory.getConnection();
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(LOAD_OFFLINE_STATUS))
		{
			while (rs.next())
			{
				final long time = rs.getLong("time");
				if (Config.OFFLINE_MAX_DAYS > 0)
				{
					final Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(time);
					cal.add(Calendar.DAY_OF_YEAR, Config.OFFLINE_MAX_DAYS);
					if (cal.getTimeInMillis() <= System.currentTimeMillis())
					{
						continue;
					}
				}
				
				final int typeId = rs.getInt("type");
				boolean isSellBuff = false;
				
				if (typeId == PrivateStoreType.SELL_BUFFS.getId())
				{
					isSellBuff = true;
				}
				
				final PrivateStoreType type = isSellBuff ? PrivateStoreType.PACKAGE_SELL : PrivateStoreType.findById(typeId);
				
				if (type == null)
				{
					LOGGER.warning(getClass().getSimpleName() + ": PrivateStoreType with id " + rs.getInt("type") + " could not be found.");
					continue;
				}
				
				if (type == PrivateStoreType.NONE)
				{
					continue;
				}
				
				PlayerInstance player = null;
				
				try
				{
					final GameClient client = new GameClient();
					client.setDetached(true);
					player = PlayerInstance.load(rs.getInt("charId"));
					client.setPlayer(player);
					player.setOnlineStatus(true, false);
					client.setAccountName(player.getAccountNamePlayer());
					player.setClient(client);
					player.setOfflineStartTime(time);
					
					if (isSellBuff)
					{
						player.setIsSellingBuffs(true);
					}
					
					player.spawnMe(player.getX(), player.getY(), player.getZ());
					try (PreparedStatement stm_items = con.prepareStatement(LOAD_OFFLINE_ITEMS))
					{
						stm_items.setInt(1, player.getObjectId());
						try (ResultSet items = stm_items.executeQuery())
						{
							switch (type)
							{
								case BUY:
								{
									while (items.next())
									{
										if (player.getBuyList().addItemByItemId(items.getInt(2), items.getLong(3), items.getLong(4)) == null)
										{
											continue;
											// throw new NullPointerException();
										}
									}
									player.getBuyList().setTitle(rs.getString("title"));
									break;
								}
								case SELL:
								case PACKAGE_SELL:
								{
									if (player.isSellingBuffs())
									{
										while (items.next())
										{
											player.getSellingBuffs().add(new SellBuffHolder(items.getInt("item"), items.getLong("price")));
										}
									}
									else
									{
										while (items.next())
										{
											if (player.getSellList().addItem(items.getInt(2), items.getLong(3), items.getLong(4)) == null)
											{
												continue;
												// throw new NullPointerException();
											}
										}
									}
									player.getSellList().setTitle(rs.getString("title"));
									player.getSellList().setPackaged(type == PrivateStoreType.PACKAGE_SELL);
									break;
								}
								case MANUFACTURE:
								{
									final Map<Integer, Long> manufactureItems = new HashMap<>();
									while (items.next())
									{
										manufactureItems.put(items.getInt(2), items.getLong(4));
									}
									player.setManufactureItems(manufactureItems);
									player.setStoreName(rs.getString("title"));
									break;
								}
							}
						}
					}
					player.sitDown();
					if (Config.OFFLINE_SET_NAME_COLOR)
					{
						player.getAppearance().setNameColor(Config.OFFLINE_NAME_COLOR);
					}
					player.setPrivateStoreType(type);
					player.setOnlineStatus(true, true);
					player.restoreEffects();
					player.broadcastUserInfo();
					PlayerCountManager.getInstance().incOfflineTradeCount();
					nTraders++;
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error loading trader: " + player, e);
					if (player != null)
					{
						Disconnection.of(player).defaultSequence(false);
					}
				}
			}
			
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + nTraders + " offline traders.");
			
			if (!Config.STORE_OFFLINE_TRADE_IN_REALTIME)
			{
				try (Statement stm1 = con.createStatement())
				{
					stm1.execute(CLEAR_OFFLINE_TABLE);
					stm1.execute(CLEAR_OFFLINE_TABLE_ITEMS);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while loading offline traders: ", e);
		}
	}
	
	public static synchronized void onTransaction(PlayerInstance trader, boolean finished, boolean firstCall)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stm1 = con.prepareStatement(CLEAR_OFFLINE_TABLE_ITEMS_PLAYER);
			PreparedStatement stm2 = con.prepareStatement(CLEAR_OFFLINE_TABLE_PLAYER);
			PreparedStatement stm3 = con.prepareStatement(SAVE_ITEMS);
			PreparedStatement stm4 = con.prepareStatement(SAVE_OFFLINE_STATUS))
		{
			String title = null;
			
			stm1.setInt(1, trader.getObjectId()); // Char Id
			stm1.execute();
			stm1.close();
			
			// Trade is done - clear info
			if (finished)
			{
				stm2.setInt(1, trader.getObjectId()); // Char Id
				stm2.execute();
				stm2.close();
			}
			else
			{
				try
				{
					if ((trader.getClient() == null) || trader.getClient().isDetached())
					{
						switch (trader.getPrivateStoreType())
						{
							case BUY:
							{
								if (firstCall)
								{
									title = trader.getBuyList().getTitle();
								}
								for (TradeItem i : trader.getBuyList().getItems())
								{
									stm3.setInt(1, trader.getObjectId());
									stm3.setInt(2, i.getItem().getId());
									stm3.setLong(3, i.getCount());
									stm3.setLong(4, i.getPrice());
									stm3.executeUpdate();
									stm3.clearParameters();
								}
								break;
							}
							case SELL:
							case PACKAGE_SELL:
							{
								if (firstCall)
								{
									title = trader.getSellList().getTitle();
								}
								if (trader.isSellingBuffs())
								{
									for (SellBuffHolder holder : trader.getSellingBuffs())
									{
										stm3.setInt(1, trader.getObjectId());
										stm3.setInt(2, holder.getSkillId());
										stm3.setLong(3, 0);
										stm3.setLong(4, holder.getPrice());
										stm3.executeUpdate();
										stm3.clearParameters();
									}
								}
								else
								{
									for (TradeItem i : trader.getSellList().getItems())
									{
										stm3.setInt(1, trader.getObjectId());
										stm3.setInt(2, i.getObjectId());
										stm3.setLong(3, i.getCount());
										stm3.setLong(4, i.getPrice());
										stm3.executeUpdate();
										stm3.clearParameters();
									}
								}
								break;
							}
							case MANUFACTURE:
							{
								if (firstCall)
								{
									title = trader.getStoreName();
								}
								for (Entry<Integer, Long> entry : trader.getManufactureItems().entrySet())
								{
									stm3.setInt(1, trader.getObjectId());
									stm3.setInt(2, entry.getKey());
									stm3.setLong(3, 0);
									stm3.setLong(4, entry.getValue());
									stm3.executeUpdate();
									stm3.clearParameters();
								}
								break;
							}
						}
						stm3.close();
						if (firstCall)
						{
							stm4.setInt(1, trader.getObjectId()); // Char Id
							stm4.setLong(2, trader.getOfflineStartTime());
							stm4.setInt(3, trader.isSellingBuffs() ? PrivateStoreType.SELL_BUFFS.getId() : trader.getPrivateStoreType().getId()); // store type
							stm4.setString(4, title);
							stm4.executeUpdate();
							stm4.clearParameters();
							stm4.close();
						}
					}
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "OfflineTradersTable[storeTradeItems()]: Error while saving offline trader: " + trader.getObjectId() + " " + e, e);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "OfflineTradersTable[storeTradeItems()]: Error while saving offline traders: " + e, e);
		}
	}
	
	public static synchronized void removeTrader(int traderObjId)
	{
		PlayerCountManager.getInstance().decOfflineTradeCount();
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stm1 = con.prepareStatement(CLEAR_OFFLINE_TABLE_ITEMS_PLAYER);
			PreparedStatement stm2 = con.prepareStatement(CLEAR_OFFLINE_TABLE_PLAYER))
		{
			stm1.setInt(1, traderObjId);
			stm1.execute();
			stm1.close();
			
			stm2.setInt(1, traderObjId);
			stm2.execute();
			stm2.close();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "OfflineTradersTable[removeTrader()]: Error while removing offline trader: " + traderObjId + " " + e, e);
		}
	}
	
	/**
	 * Gets the single instance of OfflineTradersTable.
	 * @return single instance of OfflineTradersTable
	 */
	public static OfflineTradersTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final OfflineTradersTable INSTANCE = new OfflineTradersTable();
	}
}
