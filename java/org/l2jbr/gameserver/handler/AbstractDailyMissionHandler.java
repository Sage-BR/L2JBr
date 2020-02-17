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
package org.l2jbr.gameserver.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.enums.DailyMissionStatus;
import org.l2jbr.gameserver.enums.SpecialItemType;
import org.l2jbr.gameserver.model.DailyMissionDataHolder;
import org.l2jbr.gameserver.model.DailyMissionPlayerEntry;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.ListenersContainer;
import org.l2jbr.gameserver.model.holders.ItemHolder;

/**
 * @author Sdw
 */
public abstract class AbstractDailyMissionHandler extends ListenersContainer
{
	protected Logger LOGGER = Logger.getLogger(getClass().getName());
	
	private final Map<Integer, DailyMissionPlayerEntry> _entries = new ConcurrentHashMap<>();
	private final DailyMissionDataHolder _holder;
	
	protected AbstractDailyMissionHandler(DailyMissionDataHolder holder)
	{
		_holder = holder;
		init();
	}
	
	public DailyMissionDataHolder getHolder()
	{
		return _holder;
	}
	
	public abstract boolean isAvailable(PlayerInstance player);
	
	public boolean isLevelUpMission()
	{
		return false;
	}
	
	public abstract void init();
	
	public int getStatus(PlayerInstance player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		return entry != null ? entry.getStatus().getClientId() : DailyMissionStatus.NOT_AVAILABLE.getClientId();
	}
	
	public int getProgress(PlayerInstance player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		return entry != null ? entry.getProgress() : 0;
	}
	
	public boolean getRecentlyCompleted(PlayerInstance player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		return (entry != null) && entry.getRecentlyCompleted();
	}
	
	public synchronized void reset()
	{
		if (!_holder.dailyReset())
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM character_daily_rewards WHERE rewardId = ? AND status = ?"))
		{
			ps.setInt(1, _holder.getId());
			ps.setInt(2, DailyMissionStatus.COMPLETED.getClientId());
			ps.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Error while clearing data for: " + getClass().getSimpleName(), e);
		}
		finally
		{
			_entries.clear();
		}
	}
	
	public boolean requestReward(PlayerInstance player)
	{
		if (isAvailable(player) || isLevelUpMission())
		{
			giveRewards(player);
			
			final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), true);
			entry.setStatus(DailyMissionStatus.COMPLETED);
			entry.setLastCompleted(System.currentTimeMillis());
			entry.setRecentlyCompleted(true);
			storePlayerEntry(entry);
			
			return true;
		}
		return false;
	}
	
	protected void giveRewards(PlayerInstance player)
	{
		for (ItemHolder reward : _holder.getRewards())
		{
			if (reward.getId() == SpecialItemType.CLAN_REPUTATION.getClientId())
			{
				player.getClan().addReputationScore((int) reward.getCount(), true);
			}
			else if (reward.getId() == SpecialItemType.FAME.getClientId())
			{
				player.setFame(player.getFame() + (int) reward.getCount());
				player.broadcastUserInfo();
			}
			else
			{
				player.addItem("Daily Reward", reward, player, true);
			}
		}
	}
	
	protected void storePlayerEntry(DailyMissionPlayerEntry entry)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("REPLACE INTO character_daily_rewards (charId, rewardId, status, progress, lastCompleted) VALUES (?, ?, ?, ?, ?)"))
		{
			ps.setInt(1, entry.getObjectId());
			ps.setInt(2, entry.getRewardId());
			ps.setInt(3, entry.getStatus().getClientId());
			ps.setInt(4, entry.getProgress());
			ps.setLong(5, entry.getLastCompleted());
			ps.execute();
			
			// Cache if not exists
			_entries.computeIfAbsent(entry.getObjectId(), id -> entry);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error while saving reward " + entry.getRewardId() + " for player: " + entry.getObjectId() + " in database: ", e);
		}
	}
	
	protected DailyMissionPlayerEntry getPlayerEntry(int objectId, boolean createIfNone)
	{
		final DailyMissionPlayerEntry existingEntry = _entries.get(objectId);
		if (existingEntry != null)
		{
			return existingEntry;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM character_daily_rewards WHERE charId = ? AND rewardId = ?"))
		{
			ps.setInt(1, objectId);
			ps.setInt(2, _holder.getId());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					final DailyMissionPlayerEntry entry = new DailyMissionPlayerEntry(rs.getInt("charId"), rs.getInt("rewardId"), rs.getInt("status"), rs.getInt("progress"), rs.getLong("lastCompleted"));
					_entries.put(objectId, entry);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error while loading reward " + _holder.getId() + " for player: " + objectId + " in database: ", e);
		}
		
		if (createIfNone)
		{
			final DailyMissionPlayerEntry entry = new DailyMissionPlayerEntry(objectId, _holder.getId());
			_entries.put(objectId, entry);
			return entry;
		}
		return null;
	}
}
