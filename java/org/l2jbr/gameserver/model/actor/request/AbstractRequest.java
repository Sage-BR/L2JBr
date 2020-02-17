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
package org.l2jbr.gameserver.model.actor.request;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

/**
 * @author UnAfraid
 */
public abstract class AbstractRequest
{
	private final PlayerInstance _player;
	private volatile long _timestamp = 0;
	private volatile boolean _isProcessing;
	private ScheduledFuture<?> _timeOutTask;
	
	public AbstractRequest(PlayerInstance player)
	{
		Objects.requireNonNull(player);
		_player = player;
	}
	
	public PlayerInstance getActiveChar()
	{
		return _player;
	}
	
	public long getTimestamp()
	{
		return _timestamp;
	}
	
	public void setTimestamp(long timestamp)
	{
		_timestamp = timestamp;
	}
	
	public void scheduleTimeout(long delay)
	{
		_timeOutTask = ThreadPool.schedule(this::onTimeout, delay);
	}
	
	public boolean isTimeout()
	{
		return (_timeOutTask != null) && !_timeOutTask.isDone();
	}
	
	public boolean isProcessing()
	{
		return _isProcessing;
	}
	
	public boolean setProcessing(boolean isProcessing)
	{
		return _isProcessing = isProcessing;
	}
	
	public boolean canWorkWith(AbstractRequest request)
	{
		return true;
	}
	
	public boolean isItemRequest()
	{
		return false;
	}
	
	public abstract boolean isUsing(int objectId);
	
	public void onTimeout()
	{
		
	}
}
