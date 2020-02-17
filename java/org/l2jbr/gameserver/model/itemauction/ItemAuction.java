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
package org.l2jbr.gameserver.model.itemauction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.instancemanager.ItemAuctionManager;
import org.l2jbr.gameserver.model.ItemInfo;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Forsaiken
 */
public class ItemAuction
{
	private static final Logger LOGGER = Logger.getLogger(ItemAuctionManager.class.getName());
	private static final long ENDING_TIME_EXTEND_5 = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
	private static final long ENDING_TIME_EXTEND_3 = TimeUnit.MILLISECONDS.convert(3, TimeUnit.MINUTES);
	
	private final int _auctionId;
	private final int _instanceId;
	private final long _startingTime;
	private volatile long _endingTime;
	private final AuctionItem _auctionItem;
	private final ArrayList<ItemAuctionBid> _auctionBids;
	private final Object _auctionStateLock;
	
	private volatile ItemAuctionState _auctionState;
	private volatile ItemAuctionExtendState _scheduledAuctionEndingExtendState;
	private volatile ItemAuctionExtendState _auctionEndingExtendState;
	
	private final ItemInfo _itemInfo;
	
	private ItemAuctionBid _highestBid;
	private int _lastBidPlayerObjId;
	
	// SQL
	private static final String DELETE_ITEM_AUCTION_BID = "DELETE FROM item_auction_bid WHERE auctionId = ? AND playerObjId = ?";
	private static final String INSERT_ITEM_AUCTION_BID = "INSERT INTO item_auction_bid (auctionId, playerObjId, playerBid) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE playerBid = ?";
	
	public ItemAuction(int auctionId, int instanceId, long startingTime, long endingTime, AuctionItem auctionItem)
	{
		this(auctionId, instanceId, startingTime, endingTime, auctionItem, new ArrayList<>(), ItemAuctionState.CREATED);
	}
	
	public ItemAuction(int auctionId, int instanceId, long startingTime, long endingTime, AuctionItem auctionItem, ArrayList<ItemAuctionBid> auctionBids, ItemAuctionState auctionState)
	{
		_auctionId = auctionId;
		_instanceId = instanceId;
		_startingTime = startingTime;
		_endingTime = endingTime;
		_auctionItem = auctionItem;
		_auctionBids = auctionBids;
		_auctionState = auctionState;
		_auctionStateLock = new Object();
		_scheduledAuctionEndingExtendState = ItemAuctionExtendState.INITIAL;
		_auctionEndingExtendState = ItemAuctionExtendState.INITIAL;
		
		final ItemInstance item = _auctionItem.createNewItemInstance();
		_itemInfo = new ItemInfo(item);
		World.getInstance().removeObject(item);
		
		for (ItemAuctionBid bid : _auctionBids)
		{
			if ((_highestBid == null) || (_highestBid.getLastBid() < bid.getLastBid()))
			{
				_highestBid = bid;
			}
		}
	}
	
	public ItemAuctionState getAuctionState()
	{
		final ItemAuctionState auctionState;
		
		synchronized (_auctionStateLock)
		{
			auctionState = _auctionState;
		}
		
		return auctionState;
	}
	
	public boolean setAuctionState(ItemAuctionState expected, ItemAuctionState wanted)
	{
		synchronized (_auctionStateLock)
		{
			if (_auctionState != expected)
			{
				return false;
			}
			
			_auctionState = wanted;
			storeMe();
			return true;
		}
	}
	
	public int getAuctionId()
	{
		return _auctionId;
	}
	
	public int getInstanceId()
	{
		return _instanceId;
	}
	
	public ItemInfo getItemInfo()
	{
		return _itemInfo;
	}
	
	public ItemInstance createNewItemInstance()
	{
		return _auctionItem.createNewItemInstance();
	}
	
	public long getAuctionInitBid()
	{
		return _auctionItem.getAuctionInitBid();
	}
	
	public ItemAuctionBid getHighestBid()
	{
		return _highestBid;
	}
	
	public ItemAuctionExtendState getAuctionEndingExtendState()
	{
		return _auctionEndingExtendState;
	}
	
	public ItemAuctionExtendState getScheduledAuctionEndingExtendState()
	{
		return _scheduledAuctionEndingExtendState;
	}
	
	public void setScheduledAuctionEndingExtendState(ItemAuctionExtendState state)
	{
		_scheduledAuctionEndingExtendState = state;
	}
	
	public long getStartingTime()
	{
		return _startingTime;
	}
	
	public long getEndingTime()
	{
		return _endingTime;
	}
	
	public long getStartingTimeRemaining()
	{
		return Math.max(_endingTime - System.currentTimeMillis(), 0);
	}
	
	public long getFinishingTimeRemaining()
	{
		return Math.max(_endingTime - System.currentTimeMillis(), 0);
	}
	
	public void storeMe()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO item_auction (auctionId,instanceId,auctionItemId,startingTime,endingTime,auctionStateId) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE auctionStateId=?"))
		{
			statement.setInt(1, _auctionId);
			statement.setInt(2, _instanceId);
			statement.setInt(3, _auctionItem.getAuctionItemId());
			statement.setLong(4, _startingTime);
			statement.setLong(5, _endingTime);
			statement.setByte(6, _auctionState.getStateId());
			statement.setByte(7, _auctionState.getStateId());
			statement.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "", e);
		}
	}
	
	public int getAndSetLastBidPlayerObjectId(int playerObjId)
	{
		final int lastBid = _lastBidPlayerObjId;
		_lastBidPlayerObjId = playerObjId;
		return lastBid;
	}
	
	private void updatePlayerBid(ItemAuctionBid bid, boolean delete)
	{
		// TODO nBd maybe move such stuff to you db updater :D
		updatePlayerBidInternal(bid, delete);
	}
	
	final void updatePlayerBidInternal(ItemAuctionBid bid, boolean delete)
	{
		final String query = delete ? DELETE_ITEM_AUCTION_BID : INSERT_ITEM_AUCTION_BID;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(query))
		{
			ps.setInt(1, _auctionId);
			ps.setInt(2, bid.getPlayerObjId());
			if (!delete)
			{
				ps.setLong(3, bid.getLastBid());
				ps.setLong(4, bid.getLastBid());
			}
			ps.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "", e);
		}
	}
	
	public void registerBid(PlayerInstance player, long newBid)
	{
		if (player == null)
		{
			throw new NullPointerException();
		}
		
		if (newBid < _auctionItem.getAuctionInitBid())
		{
			player.sendPacket(SystemMessageId.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
			return;
		}
		
		if (newBid > 100000000000L)
		{
			player.sendPacket(SystemMessageId.THE_HIGHEST_BID_IS_OVER_999_9_BILLION_THEREFORE_YOU_CANNOT_PLACE_A_BID);
			return;
		}
		
		if (getAuctionState() != ItemAuctionState.STARTED)
		{
			return;
		}
		
		final int playerObjId = player.getObjectId();
		
		synchronized (_auctionBids)
		{
			if ((_highestBid != null) && (newBid < _highestBid.getLastBid()))
			{
				player.sendPacket(SystemMessageId.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID);
				return;
			}
			
			ItemAuctionBid bid = getBidFor(playerObjId);
			if (bid == null)
			{
				if (!reduceItemCount(player, newBid))
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
					return;
				}
				
				bid = new ItemAuctionBid(playerObjId, newBid);
				_auctionBids.add(bid);
			}
			else
			{
				if (!bid.isCanceled())
				{
					if (newBid < bid.getLastBid()) // just another check
					{
						player.sendPacket(SystemMessageId.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID);
						return;
					}
					
					if (!reduceItemCount(player, newBid - bid.getLastBid()))
					{
						player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
						return;
					}
				}
				else if (!reduceItemCount(player, newBid))
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
					return;
				}
				
				bid.setLastBid(newBid);
			}
			
			onPlayerBid(player, bid);
			updatePlayerBid(bid, false);
			
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_SUBMITTED_A_BID_FOR_THE_AUCTION_OF_S1);
			sm.addLong(newBid);
			player.sendPacket(sm);
		}
	}
	
	private void onPlayerBid(PlayerInstance player, ItemAuctionBid bid)
	{
		if (_highestBid == null)
		{
			_highestBid = bid;
		}
		else if (_highestBid.getLastBid() < bid.getLastBid())
		{
			final PlayerInstance old = _highestBid.getPlayer();
			if (old != null)
			{
				old.sendPacket(SystemMessageId.YOU_WERE_OUTBID_THE_NEW_HIGHEST_BID_IS_S1_ADENA);
			}
			
			_highestBid = bid;
		}
		
		if ((_endingTime - System.currentTimeMillis()) <= (1000 * 60 * 10)) // 10 minutes
		{
			switch (_auctionEndingExtendState)
			{
				case INITIAL:
				{
					_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_5_MIN;
					_endingTime += ENDING_TIME_EXTEND_5;
					broadcastToAllBidders(new SystemMessage(SystemMessageId.BIDDER_EXISTS_THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES));
					break;
				}
				case EXTEND_BY_5_MIN:
				{
					if (getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId())
					{
						_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_3_MIN;
						_endingTime += ENDING_TIME_EXTEND_3;
						broadcastToAllBidders(new SystemMessage(SystemMessageId.BIDDER_EXISTS_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES));
					}
					break;
				}
				case EXTEND_BY_3_MIN:
				{
					if (Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID > 0)
					{
						if (getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId())
						{
							_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A;
							_endingTime += Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
						}
					}
					break;
				}
				case EXTEND_BY_CONFIG_PHASE_A:
				{
					if (getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId())
					{
						if (_scheduledAuctionEndingExtendState == ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B)
						{
							_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B;
							_endingTime += Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
						}
					}
					break;
				}
				case EXTEND_BY_CONFIG_PHASE_B:
				{
					if (getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId())
					{
						if (_scheduledAuctionEndingExtendState == ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A)
						{
							_endingTime += Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
							_auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A;
						}
					}
				}
			}
		}
	}
	
	public void broadcastToAllBidders(IClientOutgoingPacket packet)
	{
		ThreadPool.execute(() -> broadcastToAllBiddersInternal(packet));
	}
	
	public void broadcastToAllBiddersInternal(IClientOutgoingPacket packet)
	{
		for (int i = _auctionBids.size(); i-- > 0;)
		{
			final ItemAuctionBid bid = _auctionBids.get(i);
			if (bid != null)
			{
				final PlayerInstance player = bid.getPlayer();
				if (player != null)
				{
					player.sendPacket(packet);
				}
			}
		}
	}
	
	public boolean cancelBid(PlayerInstance player)
	{
		if (player == null)
		{
			throw new NullPointerException();
		}
		
		switch (getAuctionState())
		{
			case CREATED:
			{
				return false;
			}
			case FINISHED:
			{
				if (_startingTime < (System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(Config.ALT_ITEM_AUCTION_EXPIRED_AFTER, TimeUnit.DAYS)))
				{
					return false;
				}
				break;
			}
		}
		
		final int playerObjId = player.getObjectId();
		
		synchronized (_auctionBids)
		{
			if (_highestBid == null)
			{
				return false;
			}
			
			final int bidIndex = getBidIndexFor(playerObjId);
			if (bidIndex == -1)
			{
				return false;
			}
			
			final ItemAuctionBid bid = _auctionBids.get(bidIndex);
			if (bid.getPlayerObjId() == _highestBid.getPlayerObjId())
			{
				// can't return winning bid
				if (getAuctionState() == ItemAuctionState.FINISHED)
				{
					return false;
				}
				
				player.sendPacket(SystemMessageId.YOU_CURRENTLY_HAVE_THE_HIGHEST_BID_BUT_THE_RESERVE_HAS_NOT_BEEN_MET);
				return true;
			}
			
			if (bid.isCanceled())
			{
				return false;
			}
			
			increaseItemCount(player, bid.getLastBid());
			bid.cancelBid();
			
			// delete bid from database if auction already finished
			updatePlayerBid(bid, getAuctionState() == ItemAuctionState.FINISHED);
			
			player.sendPacket(SystemMessageId.YOU_HAVE_CANCELED_YOUR_BID);
		}
		return true;
	}
	
	public void clearCanceledBids()
	{
		if (getAuctionState() != ItemAuctionState.FINISHED)
		{
			throw new IllegalStateException("Attempt to clear canceled bids for non-finished auction");
		}
		
		synchronized (_auctionBids)
		{
			for (ItemAuctionBid bid : _auctionBids)
			{
				if ((bid == null) || !bid.isCanceled())
				{
					continue;
				}
				updatePlayerBid(bid, true);
			}
		}
	}
	
	private boolean reduceItemCount(PlayerInstance player, long count)
	{
		if (!player.reduceAdena("ItemAuction", count, player, true))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
			return false;
		}
		return true;
	}
	
	private void increaseItemCount(PlayerInstance player, long count)
	{
		player.addAdena("ItemAuction", count, player, true);
	}
	
	/**
	 * Returns the last bid for the given player or -1 if he did not made one yet.
	 * @param player The player that made the bid
	 * @return The last bid the player made or -1
	 */
	public long getLastBid(PlayerInstance player)
	{
		final ItemAuctionBid bid = getBidFor(player.getObjectId());
		return bid != null ? bid.getLastBid() : -1L;
	}
	
	public ItemAuctionBid getBidFor(int playerObjId)
	{
		final int index = getBidIndexFor(playerObjId);
		return index != -1 ? _auctionBids.get(index) : null;
	}
	
	private int getBidIndexFor(int playerObjId)
	{
		for (int i = _auctionBids.size(); i-- > 0;)
		{
			final ItemAuctionBid bid = _auctionBids.get(i);
			if ((bid != null) && (bid.getPlayerObjId() == playerObjId))
			{
				return i;
			}
		}
		return -1;
	}
}
