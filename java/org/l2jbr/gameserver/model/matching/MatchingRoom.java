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
package org.l2jbr.gameserver.model.matching;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.gameserver.enums.MatchingMemberType;
import org.l2jbr.gameserver.enums.MatchingRoomType;
import org.l2jbr.gameserver.enums.UserInfoType;
import org.l2jbr.gameserver.instancemanager.MapRegionManager;
import org.l2jbr.gameserver.instancemanager.MatchingRoomManager;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.interfaces.IIdentifiable;

/**
 * @author Sdw
 */
public abstract class MatchingRoom implements IIdentifiable
{
	private final int _id;
	private String _title;
	private int _loot;
	private int _minLvl;
	private int _maxLvl;
	private int _maxCount;
	private volatile Set<PlayerInstance> _members;
	private PlayerInstance _leader;
	
	public MatchingRoom(String title, int loot, int minlvl, int maxlvl, int maxmem, PlayerInstance leader)
	{
		_id = MatchingRoomManager.getInstance().addMatchingRoom(this);
		_title = title;
		_loot = loot;
		_minLvl = minlvl;
		_maxLvl = maxlvl;
		_maxCount = maxmem;
		_leader = leader;
		
		addMember(_leader);
		onRoomCreation(leader);
	}
	
	public Set<PlayerInstance> getMembers()
	{
		if (_members == null)
		{
			synchronized (this)
			{
				if (_members == null)
				{
					_members = ConcurrentHashMap.newKeySet(1);
				}
			}
		}
		
		return _members;
	}
	
	public void addMember(PlayerInstance player)
	{
		if ((player.getLevel() < _minLvl) || (player.getLevel() > _maxLvl) || ((_members != null) && (_members.size() >= _maxCount)))
		{
			notifyInvalidCondition(player);
			return;
		}
		
		getMembers().add(player);
		MatchingRoomManager.getInstance().removeFromWaitingList(player);
		notifyNewMember(player);
		player.setMatchingRoom(this);
		player.broadcastUserInfo(UserInfoType.CLAN);
	}
	
	public void deleteMember(PlayerInstance player, boolean kicked)
	{
		boolean leaderChanged = false;
		
		if (player == _leader)
		{
			if (getMembers().isEmpty())
			{
				MatchingRoomManager.getInstance().removeMatchingRoom(this);
			}
			else
			{
				final Iterator<PlayerInstance> iter = getMembers().iterator();
				if (iter.hasNext())
				{
					_leader = iter.next();
					iter.remove();
					leaderChanged = true;
				}
			}
		}
		else
		{
			getMembers().remove(player);
		}
		
		player.setMatchingRoom(null);
		player.broadcastUserInfo(UserInfoType.CLAN);
		MatchingRoomManager.getInstance().addToWaitingList(player);
		
		notifyRemovedMember(player, kicked, leaderChanged);
	}
	
	@Override
	public int getId()
	{
		return _id;
	}
	
	public int getLootType()
	{
		return _loot;
	}
	
	public int getMinLvl()
	{
		return _minLvl;
	}
	
	public int getMaxLvl()
	{
		return _maxLvl;
	}
	
	public int getLocation()
	{
		return MapRegionManager.getInstance().getBBs(_leader.getLocation());
	}
	
	public int getMembersCount()
	{
		return _members == null ? 0 : _members.size();
	}
	
	public int getMaxMembers()
	{
		return _maxCount;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public PlayerInstance getLeader()
	{
		return _leader;
	}
	
	public boolean isLeader(PlayerInstance player)
	{
		return player == _leader;
	}
	
	public void setMinLvl(int minLvl)
	{
		_minLvl = minLvl;
	}
	
	public void setMaxLvl(int maxLvl)
	{
		_maxLvl = maxLvl;
	}
	
	public void setLootType(int loot)
	{
		_loot = loot;
	}
	
	public void setMaxMembers(int maxCount)
	{
		_maxCount = maxCount;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	
	protected abstract void onRoomCreation(PlayerInstance player);
	
	protected abstract void notifyInvalidCondition(PlayerInstance player);
	
	protected abstract void notifyNewMember(PlayerInstance player);
	
	protected abstract void notifyRemovedMember(PlayerInstance player, boolean kicked, boolean leaderChanged);
	
	public abstract void disbandRoom();
	
	public abstract MatchingRoomType getRoomType();
	
	public abstract MatchingMemberType getMemberType(PlayerInstance player);
}
