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
package org.l2jbr.gameserver.model.clanhallauction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.data.sql.impl.ClanTable;
import org.l2jbr.gameserver.data.xml.impl.ClanHallData;
import org.l2jbr.gameserver.instancemanager.ClanHallAuctionManager;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.entity.ClanHall;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;

/**
 * @author Sdw
 */
public class ClanHallAuction
{
	private static final Logger LOGGER = Logger.getLogger(ClanHallAuction.class.getName());
	
	private final int _clanHallId;
	private static final String LOAD_CLANHALL_BIDDERS = "SELECT * FROM clanhall_auctions_bidders WHERE clanHallId=?";
	private static final String DELETE_CLANHALL_BIDDERS = "DELETE FROM clanhall_auctions_bidders WHERE clanHallId=?";
	private static final String INSERT_CLANHALL_BIDDER = "REPLACE INTO clanhall_auctions_bidders (clanHallId, clanId, bid, bidTime) VALUES (?,?,?,?)";
	private static final String DELETE_CLANHALL_BIDDER = "DELETE FROM clanhall_auctions_bidders WHERE clanId=?";
	
	public ClanHallAuction(int clanHallId)
	{
		_clanHallId = clanHallId;
		loadBidder();
	}
	
	private final void loadBidder()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_CLANHALL_BIDDERS))
		{
			ps.setInt(1, _clanHallId);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final Clan clan = ClanTable.getInstance().getClan(rs.getInt("clanId"));
					addBid(clan, rs.getLong("bid"), rs.getLong("bidTime"));
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed loading clan hall auctions bidder for clan hall " + _clanHallId + ".", e);
		}
	}
	
	private volatile Map<Integer, Bidder> _bidders;
	
	public Map<Integer, Bidder> getBids()
	{
		return _bidders == null ? Collections.emptyMap() : _bidders;
	}
	
	public void addBid(Clan clan, long bid)
	{
		addBid(clan, bid, System.currentTimeMillis());
	}
	
	public void addBid(Clan clan, long bid, long bidTime)
	{
		if (_bidders == null)
		{
			synchronized (this)
			{
				if (_bidders == null)
				{
					_bidders = new ConcurrentHashMap<>();
				}
			}
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_CLANHALL_BIDDER))
		{
			ps.setInt(1, _clanHallId);
			ps.setInt(2, clan.getId());
			ps.setLong(3, bid);
			ps.setLong(4, bidTime);
			ps.execute();
			_bidders.put(clan.getId(), new Bidder(clan, bid, bidTime));
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Failed insert clan hall auctions bidder " + clan.getName() + " for clan hall " + _clanHallId + ".", e);
		}
	}
	
	public void removeBid(Clan clan)
	{
		getBids().remove(clan.getId());
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_CLANHALL_BIDDER))
		{
			ps.setInt(1, clan.getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Failed clearing bidder " + clan.getName() + " for clan hall " + _clanHallId + ": ", e);
		}
	}
	
	public long getHighestBid()
	{
		final ClanHall clanHall = ClanHallData.getInstance().getClanHallById(_clanHallId);
		return getBids().values().stream().mapToLong(Bidder::getBid).max().orElse(clanHall.getMinBid());
	}
	
	public long getClanBid(Clan clan)
	{
		return getBids().get(clan.getId()).getBid();
	}
	
	public Optional<Bidder> getHighestBidder()
	{
		return getBids().values().stream().sorted(Comparator.comparingLong(Bidder::getBid).reversed()).findFirst();
	}
	
	public int getBidCount()
	{
		return getBids().values().size();
	}
	
	public void returnAdenas(Bidder bidder)
	{
		bidder.getClan().getWarehouse().addItem("Clan Hall Auction Outbid", Inventory.ADENA_ID, bidder.getBid(), null, null);
	}
	
	public void finalizeAuctions()
	{
		final Optional<Bidder> potentialHighestBidder = getHighestBidder();
		
		if (potentialHighestBidder.isPresent())
		{
			final Bidder highestBidder = potentialHighestBidder.get();
			final ClanHall clanHall = ClanHallData.getInstance().getClanHallById(_clanHallId);
			clanHall.setOwner(highestBidder.getClan());
			getBids().clear();
			
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement(DELETE_CLANHALL_BIDDERS))
			{
				ps.setInt(1, _clanHallId);
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Failed clearing bidder for clan hall " + _clanHallId + ": ", e);
			}
		}
	}
	
	public int getClanHallId()
	{
		return _clanHallId;
	}
	
	public long getRemaingTime()
	{
		return ClanHallAuctionManager.getInstance().getScheduler("endAuction").getRemainingTime(TimeUnit.MILLISECONDS);
	}
}
