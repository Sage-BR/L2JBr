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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

/**
 * Contains objectId and factionId for all players.
 * @author Mobius
 */
public class FactionManager
{
	private static Logger LOGGER = Logger.getLogger(FactionManager.class.getName());
	private final Map<Integer, Integer> _playerFactions = new ConcurrentHashMap<>();
	
	protected FactionManager()
	{
		loadAll();
	}
	
	private void loadAll()
	{
		_playerFactions.clear();
		try (Connection con = DatabaseFactory.getConnection();
			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery("SELECT charId, faction FROM characters"))
		{
			while (rs.next())
			{
				_playerFactions.put(rs.getInt(1), rs.getInt(2));
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not load character faction information: " + e.getMessage(), e);
		}
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _playerFactions.size() + " character faction values.");
	}
	
	public int getFactionByCharId(int id)
	{
		if (id <= 0)
		{
			return 0;
		}
		
		Integer factionId = _playerFactions.get(id);
		if (factionId != null)
		{
			return factionId;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT faction FROM characters WHERE charId=?"))
		{
			ps.setInt(1, id);
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					factionId = rset.getInt(1);
					_playerFactions.put(id, factionId);
					return factionId;
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing char id: " + e.getMessage(), e);
		}
		
		return 0; // not found
	}
	
	public boolean isSameFaction(PlayerInstance player1, PlayerInstance player2)
	{
		// TODO: Maybe add support for multiple factions?
		return (player1.isGood() && player2.isGood()) || (player1.isEvil() && player2.isEvil());
	}
	
	public static FactionManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FactionManager INSTANCE = new FactionManager();
	}
}
