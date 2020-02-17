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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.data.xml.impl.NpcData;
import org.l2jbr.gameserver.instancemanager.tasks.GrandBossManagerStoreTask;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.instance.GrandBossInstance;
import org.l2jbr.gameserver.model.interfaces.IStorable;

/**
 * Grand Boss manager.
 * @author DaRkRaGe Revised by Emperorc
 */
public class GrandBossManager implements IStorable
{
	// SQL queries
	private static final String UPDATE_GRAND_BOSS_DATA = "UPDATE grandboss_data set loc_x = ?, loc_y = ?, loc_z = ?, heading = ?, respawn_time = ?, currentHP = ?, currentMP = ?, status = ? where boss_id = ?";
	private static final String UPDATE_GRAND_BOSS_DATA2 = "UPDATE grandboss_data set status = ? where boss_id = ?";
	
	protected static Logger LOGGER = Logger.getLogger(GrandBossManager.class.getName());
	
	protected static Map<Integer, GrandBossInstance> _bosses = new ConcurrentHashMap<>();
	
	protected static Map<Integer, StatsSet> _storedInfo = new HashMap<>();
	
	private final Map<Integer, Integer> _bossStatus = new HashMap<>();
	
	protected GrandBossManager()
	{
		init();
	}
	
	private void init()
	{
		try (Connection con = DatabaseFactory.getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("SELECT * from grandboss_data ORDER BY boss_id"))
		{
			// Read all info from DB, and store it for AI to read and decide what to do faster than accessing DB in real time
			while (rs.next())
			{
				final int bossId = rs.getInt("boss_id");
				if (NpcData.getInstance().getTemplate(bossId) != null)
				{
					final StatsSet info = new StatsSet();
					info.set("loc_x", rs.getInt("loc_x"));
					info.set("loc_y", rs.getInt("loc_y"));
					info.set("loc_z", rs.getInt("loc_z"));
					info.set("heading", rs.getInt("heading"));
					info.set("respawn_time", rs.getLong("respawn_time"));
					info.set("currentHP", rs.getDouble("currentHP"));
					info.set("currentMP", rs.getDouble("currentMP"));
					final int status = rs.getInt("status");
					_bossStatus.put(bossId, status);
					_storedInfo.put(bossId, info);
					LOGGER.info(getClass().getSimpleName() + ": " + NpcData.getInstance().getTemplate(bossId).getName() + "(" + bossId + ") status is " + status);
					if (status > 0)
					{
						LOGGER.info(getClass().getSimpleName() + ": Next spawn date of " + NpcData.getInstance().getTemplate(bossId).getName() + " is " + new Date(info.getLong("respawn_time")));
					}
				}
				else
				{
					LOGGER.warning(getClass().getSimpleName() + ": Could not find GrandBoss NPC template for " + bossId);
				}
			}
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + _storedInfo.size() + " instances.");
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not load grandboss_data table: " + e.getMessage(), e);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while initializing GrandBossManager: " + e.getMessage(), e);
		}
		ThreadPool.scheduleAtFixedRate(new GrandBossManagerStoreTask(), 5 * 60 * 1000, 5 * 60 * 1000);
	}
	
	public int getBossStatus(int bossId)
	{
		if (!_bossStatus.containsKey(bossId))
		{
			return -1;
		}
		return _bossStatus.get(bossId);
	}
	
	public void setBossStatus(int bossId, int status)
	{
		_bossStatus.put(bossId, status);
		LOGGER.info(getClass().getSimpleName() + ": Updated " + NpcData.getInstance().getTemplate(bossId).getName() + "(" + bossId + ") status to " + status + ".");
		updateDb(bossId, true);
	}
	
	/**
	 * Adds a GrandBossInstance to the list of bosses.
	 * @param boss
	 */
	public void addBoss(GrandBossInstance boss)
	{
		if (boss != null)
		{
			_bosses.put(boss.getId(), boss);
		}
	}
	
	public GrandBossInstance getBoss(int bossId)
	{
		return _bosses.get(bossId);
	}
	
	public StatsSet getStatsSet(int bossId)
	{
		return _storedInfo.get(bossId);
	}
	
	public void setStatsSet(int bossId, StatsSet info)
	{
		_storedInfo.put(bossId, info);
		updateDb(bossId, false);
	}
	
	@Override
	public boolean storeMe()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			for (Entry<Integer, StatsSet> e : _storedInfo.entrySet())
			{
				final GrandBossInstance boss = _bosses.get(e.getKey());
				final StatsSet info = e.getValue();
				if ((boss == null) || (info == null))
				{
					try (PreparedStatement update = con.prepareStatement(UPDATE_GRAND_BOSS_DATA2))
					{
						update.setInt(1, _bossStatus.get(e.getKey()));
						update.setInt(2, e.getKey());
						update.executeUpdate();
						update.clearParameters();
					}
				}
				else
				{
					try (PreparedStatement update = con.prepareStatement(UPDATE_GRAND_BOSS_DATA))
					{
						update.setInt(1, boss.getX());
						update.setInt(2, boss.getY());
						update.setInt(3, boss.getZ());
						update.setInt(4, boss.getHeading());
						update.setLong(5, info.getLong("respawn_time"));
						double hp = boss.getCurrentHp();
						double mp = boss.getCurrentMp();
						if (boss.isDead())
						{
							hp = boss.getMaxHp();
							mp = boss.getMaxMp();
						}
						update.setDouble(6, hp);
						update.setDouble(7, mp);
						update.setInt(8, _bossStatus.get(e.getKey()));
						update.setInt(9, e.getKey());
						update.executeUpdate();
						update.clearParameters();
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Couldn't store grandbosses to database: " + e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	private void updateDb(int bossId, boolean statusOnly)
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			final GrandBossInstance boss = _bosses.get(bossId);
			final StatsSet info = _storedInfo.get(bossId);
			
			if (statusOnly || (boss == null) || (info == null))
			{
				try (PreparedStatement ps = con.prepareStatement(UPDATE_GRAND_BOSS_DATA2))
				{
					ps.setInt(1, _bossStatus.get(bossId));
					ps.setInt(2, bossId);
					ps.executeUpdate();
				}
			}
			else
			{
				try (PreparedStatement ps = con.prepareStatement(UPDATE_GRAND_BOSS_DATA))
				{
					ps.setInt(1, boss.getX());
					ps.setInt(2, boss.getY());
					ps.setInt(3, boss.getZ());
					ps.setInt(4, boss.getHeading());
					ps.setLong(5, info.getLong("respawn_time"));
					double hp = boss.getCurrentHp();
					double mp = boss.getCurrentMp();
					if (boss.isDead())
					{
						hp = boss.getMaxHp();
						mp = boss.getMaxMp();
					}
					ps.setDouble(6, hp);
					ps.setDouble(7, mp);
					ps.setInt(8, _bossStatus.get(bossId));
					ps.setInt(9, bossId);
					ps.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Couldn't update grandbosses to database:" + e.getMessage(), e);
		}
	}
	
	/**
	 * Saves all Grand Boss info and then clears all info from memory, including all schedules.
	 */
	public void cleanUp()
	{
		storeMe();
		
		_bosses.clear();
		_storedInfo.clear();
		_bossStatus.clear();
	}
	
	/**
	 * Gets the single instance of {@code GrandBossManager}.
	 * @return single instance of {@code GrandBossManager}
	 */
	public static GrandBossManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GrandBossManager INSTANCE = new GrandBossManager();
	}
}
