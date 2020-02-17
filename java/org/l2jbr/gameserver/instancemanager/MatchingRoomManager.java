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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.l2jbr.gameserver.enums.MatchingRoomType;
import org.l2jbr.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.matching.MatchingRoom;

/**
 * @author Sdw
 */
public class MatchingRoomManager
{
	private volatile Set<PlayerInstance> _waitingList;
	
	private static final Map<MatchingRoomType, Map<Integer, MatchingRoom>> _rooms = new ConcurrentHashMap<>(2);
	
	private final AtomicInteger _id = new AtomicInteger(0);
	
	public void addToWaitingList(PlayerInstance player)
	{
		if (_waitingList == null)
		{
			synchronized (this)
			{
				if (_waitingList == null)
				{
					_waitingList = ConcurrentHashMap.newKeySet(1);
				}
			}
		}
		_waitingList.add(player);
	}
	
	public void removeFromWaitingList(PlayerInstance player)
	{
		getPlayerInWaitingList().remove(player);
	}
	
	public Set<PlayerInstance> getPlayerInWaitingList()
	{
		return _waitingList == null ? Collections.emptySet() : _waitingList;
	}
	
	public List<PlayerInstance> getPlayerInWaitingList(int minLevel, int maxLevel, List<ClassId> classIds, String query)
	{
		if (_waitingList == null)
		{
			return Collections.emptyList();
		}
		return _waitingList.stream() //
			.filter(p -> (p != null) //
				&& (p.getLevel() >= minLevel) //
				&& (p.getLevel() <= maxLevel)) //
			.filter(p -> (classIds == null) //
				|| classIds.contains(p.getClassId())) //
			.filter(p -> (query == null) //
				|| query.isEmpty() //
				|| p.getName().toLowerCase().contains(query)) //
			.collect(Collectors.toList());
	}
	
	public int addMatchingRoom(MatchingRoom room)
	{
		final int roomId = _id.incrementAndGet();
		_rooms.computeIfAbsent(room.getRoomType(), k -> new ConcurrentHashMap<>()).put(roomId, room);
		return roomId;
	}
	
	public void removeMatchingRoom(MatchingRoom room)
	{
		_rooms.getOrDefault(room.getRoomType(), Collections.emptyMap()).remove(room.getId());
	}
	
	public Map<Integer, MatchingRoom> getPartyMathchingRooms()
	{
		return _rooms.get(MatchingRoomType.PARTY);
	}
	
	public List<MatchingRoom> getPartyMathchingRooms(int location, PartyMatchingRoomLevelType type, int requestorLevel)
	{
		//@formatter:off
		return _rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).values().stream()
				.filter(room -> (location < 0) || (room.getLocation() == location))
				.filter(room -> (type == PartyMatchingRoomLevelType.ALL) || ((room.getMinLvl() >= requestorLevel) && (room.getMaxLvl() <= requestorLevel)))
				.collect(Collectors.toList());
		//@formatter:on
	}
	
	public Map<Integer, MatchingRoom> getCCMathchingRooms()
	{
		return _rooms.get(MatchingRoomType.COMMAND_CHANNEL);
	}
	
	public List<MatchingRoom> getCCMathchingRooms(int location, int level)
	{
		//@formatter:off
		return _rooms.getOrDefault(MatchingRoomType.COMMAND_CHANNEL, Collections.emptyMap()).values().stream()
				.filter(r -> r.getLocation() == location)
				.filter(r -> (r.getMinLvl() <= level) && (r.getMaxLvl() >= level))
				.collect(Collectors.toList());
		//@formatter:on
	}
	
	public MatchingRoom getCCMatchingRoom(int roomId)
	{
		return _rooms.getOrDefault(MatchingRoomType.COMMAND_CHANNEL, Collections.emptyMap()).get(roomId);
	}
	
	public MatchingRoom getPartyMathchingRoom(int location, int level)
	{
		//@formatter:off
		return _rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).values().stream()
				.filter(r -> r.getLocation() == location)
				.filter(r -> (r.getMinLvl() <= level) && (r.getMaxLvl() >= level))
				.findFirst()
				.orElse(null);
		//@formatter:on
	}
	
	public MatchingRoom getPartyMathchingRoom(int roomId)
	{
		return _rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).get(roomId);
	}
	
	public static MatchingRoomManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MatchingRoomManager INSTANCE = new MatchingRoomManager();
	}
}
