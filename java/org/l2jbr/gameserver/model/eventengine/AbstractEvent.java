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
package org.l2jbr.gameserver.model.eventengine;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.AbstractScript;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author UnAfraid
 * @param <T>
 */
public abstract class AbstractEvent<T extends AbstractEventMember<?>>extends AbstractScript
{
	private final Map<Integer, T> _members = new ConcurrentHashMap<>();
	private IEventState _state;
	
	public Map<Integer, T> getMembers()
	{
		return _members;
	}
	
	public T getMember(int objectId)
	{
		return _members.get(objectId);
	}
	
	public void addMember(T member)
	{
		_members.put(member.getObjectId(), member);
	}
	
	public void broadcastPacket(IClientOutgoingPacket... packets)
	{
		_members.values().forEach(member -> member.sendPacket(packets));
	}
	
	public IEventState getState()
	{
		return _state;
	}
	
	public void setState(IEventState state)
	{
		_state = state;
	}
	
	@Override
	public String getScriptName()
	{
		return getClass().getSimpleName();
	}
	
	@Override
	public Path getScriptPath()
	{
		return null;
	}
	
	/**
	 * @param player
	 * @return {@code true} if player is on event, {@code false} otherwise.
	 */
	public boolean isOnEvent(PlayerInstance player)
	{
		return _members.containsKey(player.getObjectId());
	}
	
	/**
	 * @param player
	 * @return {@code true} if player is blocked from leaving the game, {@code false} otherwise.
	 */
	public boolean isBlockingExit(PlayerInstance player)
	{
		return false;
	}
	
	/**
	 * @param player
	 * @return {@code true} if player is blocked from receiving death penalty upon death, {@code false} otherwise.
	 */
	public boolean isBlockingDeathPenalty(PlayerInstance player)
	{
		return false;
	}
	
	/**
	 * @param player
	 * @return {@code true} if player can revive after death, {@code false} otherwise.
	 */
	public boolean canRevive(PlayerInstance player)
	{
		return true;
	}
}
