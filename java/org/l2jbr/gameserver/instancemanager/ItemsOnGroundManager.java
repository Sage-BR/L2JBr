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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.ItemsAutoDestroy;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;

/**
 * This class manage all items on ground.
 * @author Enforcer
 */
public class ItemsOnGroundManager implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(ItemsOnGroundManager.class.getName());
	
	private final Set<ItemInstance> _items = ConcurrentHashMap.newKeySet();
	
	protected ItemsOnGroundManager()
	{
		if (Config.SAVE_DROPPED_ITEM_INTERVAL > 0)
		{
			ThreadPool.scheduleAtFixedRate(this, Config.SAVE_DROPPED_ITEM_INTERVAL, Config.SAVE_DROPPED_ITEM_INTERVAL);
		}
		load();
	}
	
	private void load()
	{
		// If SaveDroppedItem is false, may want to delete all items previously stored to avoid add old items on reactivate
		if (!Config.SAVE_DROPPED_ITEM && Config.CLEAR_DROPPED_ITEM_TABLE)
		{
			emptyTable();
		}
		
		if (!Config.SAVE_DROPPED_ITEM)
		{
			return;
		}
		
		// if DestroyPlayerDroppedItem was previously false, items currently protected will be added to ItemsAutoDestroy
		if (Config.DESTROY_DROPPED_PLAYER_ITEM)
		{
			String str = null;
			if (!Config.DESTROY_EQUIPABLE_PLAYER_ITEM)
			{
				// Recycle misc. items only
				str = "UPDATE itemsonground SET drop_time = ? WHERE drop_time = -1 AND equipable = 0";
			}
			else if (Config.DESTROY_EQUIPABLE_PLAYER_ITEM)
			{
				// Recycle all items including equip-able
				str = "UPDATE itemsonground SET drop_time = ? WHERE drop_time = -1";
			}
			
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement(str))
			{
				ps.setLong(1, System.currentTimeMillis());
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Error while updating table ItemsOnGround " + e.getMessage(), e);
			}
		}
		
		// Add items to world
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT object_id,item_id,count,enchant_level,x,y,z,drop_time,equipable FROM itemsonground"))
		{
			int count = 0;
			try (ResultSet rs = ps.executeQuery())
			{
				ItemInstance item;
				while (rs.next())
				{
					item = new ItemInstance(rs.getInt(1), rs.getInt(2));
					World.getInstance().addObject(item);
					// this check and..
					if (item.isStackable() && (rs.getInt(3) > 1))
					{
						item.setCount(rs.getInt(3));
					}
					// this, are really necessary?
					if (rs.getInt(4) > 0)
					{
						item.setEnchantLevel(rs.getInt(4));
					}
					item.setXYZ(rs.getInt(5), rs.getInt(6), rs.getInt(7));
					item.setWorldRegion(World.getInstance().getRegion(item));
					item.getWorldRegion().addVisibleObject(item);
					final long dropTime = rs.getLong(8);
					item.setDropTime(dropTime);
					item.setProtected(dropTime == -1);
					item.setSpawned(true);
					World.getInstance().addVisibleObject(item, item.getWorldRegion());
					_items.add(item);
					count++;
					// add to ItemsAutoDestroy only items not protected
					if (!Config.LIST_PROTECTED_ITEMS.contains(item.getId()))
					{
						if (dropTime > -1)
						{
							if (((Config.AUTODESTROY_ITEM_AFTER > 0) && !item.getItem().hasExImmediateEffect()) || ((Config.HERB_AUTO_DESTROY_TIME > 0) && item.getItem().hasExImmediateEffect()))
							{
								ItemsAutoDestroy.getInstance().addItem(item);
							}
						}
					}
				}
			}
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + count + " items.");
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Error while loading ItemsOnGround " + e.getMessage(), e);
		}
		
		if (Config.EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD)
		{
			emptyTable();
		}
	}
	
	public void save(ItemInstance item)
	{
		if (Config.SAVE_DROPPED_ITEM)
		{
			_items.add(item);
		}
	}
	
	public void removeObject(ItemInstance item)
	{
		if (Config.SAVE_DROPPED_ITEM)
		{
			_items.remove(item);
		}
	}
	
	public void saveInDb()
	{
		run();
	}
	
	public void cleanUp()
	{
		_items.clear();
	}
	
	public void emptyTable()
	{
		try (Connection con = DatabaseFactory.getConnection();
			Statement s = con.createStatement())
		{
			s.executeUpdate("DELETE FROM itemsonground");
		}
		catch (Exception e1)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Error while cleaning table ItemsOnGround " + e1.getMessage(), e1);
		}
	}
	
	@Override
	public synchronized void run()
	{
		if (!Config.SAVE_DROPPED_ITEM)
		{
			return;
		}
		
		emptyTable();
		
		if (_items.isEmpty())
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO itemsonground(object_id,item_id,count,enchant_level,x,y,z,drop_time,equipable) VALUES(?,?,?,?,?,?,?,?,?)"))
		{
			for (ItemInstance item : _items)
			{
				if (item == null)
				{
					continue;
				}
				
				if (CursedWeaponsManager.getInstance().isCursed(item.getId()))
				{
					continue; // Cursed Items not saved to ground, prevent double save
				}
				
				try
				{
					statement.setInt(1, item.getObjectId());
					statement.setInt(2, item.getId());
					statement.setLong(3, item.getCount());
					statement.setInt(4, item.getEnchantLevel());
					statement.setInt(5, item.getX());
					statement.setInt(6, item.getY());
					statement.setInt(7, item.getZ());
					statement.setLong(8, (item.isProtected() ? -1 : item.getDropTime())); // item is protected or AutoDestroyed
					statement.setLong(9, (item.isEquipable() ? 1 : 0)); // set equip-able
					statement.execute();
					statement.clearParameters();
				}
				catch (Exception e)
				{
					LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Error while inserting into table ItemsOnGround: " + e.getMessage(), e);
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": SQL error while storing items on ground: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Gets the single instance of {@code ItemsOnGroundManager}.
	 * @return single instance of {@code ItemsOnGroundManager}
	 */
	public static ItemsOnGroundManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemsOnGroundManager INSTANCE = new ItemsOnGroundManager();
	}
}
