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
package org.l2jbr.gameserver.model.shuttle;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.data.xml.impl.DoorData;
import org.l2jbr.gameserver.model.actor.instance.DoorInstance;
import org.l2jbr.gameserver.model.actor.instance.ShuttleInstance;

/**
 * @author UnAfraid
 */
public class ShuttleEngine implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(ShuttleEngine.class.getName());
	
	private static final int DELAY = 15 * 1000;
	
	private final ShuttleInstance _shuttle;
	private int _cycle = 0;
	private final DoorInstance _door1;
	private final DoorInstance _door2;
	
	public ShuttleEngine(ShuttleDataHolder data, ShuttleInstance shuttle)
	{
		_shuttle = shuttle;
		_door1 = DoorData.getInstance().getDoor(data.getDoors().get(0));
		_door2 = DoorData.getInstance().getDoor(data.getDoors().get(1));
	}
	
	// TODO: Rework me..
	@Override
	public void run()
	{
		try
		{
			if (!_shuttle.isSpawned())
			{
				return;
			}
			switch (_cycle)
			{
				case 0:
				{
					_door1.openMe();
					_door2.closeMe();
					_shuttle.openDoor(0);
					_shuttle.closeDoor(1);
					_shuttle.broadcastShuttleInfo();
					ThreadPool.schedule(this, DELAY);
					break;
				}
				case 1:
				{
					_door1.closeMe();
					_door2.closeMe();
					_shuttle.closeDoor(0);
					_shuttle.closeDoor(1);
					_shuttle.broadcastShuttleInfo();
					ThreadPool.schedule(this, 1000);
					break;
				}
				case 2:
				{
					_shuttle.executePath(_shuttle.getShuttleData().getRoutes().get(0));
					break;
				}
				case 3:
				{
					_door1.closeMe();
					_door2.openMe();
					_shuttle.openDoor(1);
					_shuttle.closeDoor(0);
					_shuttle.broadcastShuttleInfo();
					ThreadPool.schedule(this, DELAY);
					break;
				}
				case 4:
				{
					_door1.closeMe();
					_door2.closeMe();
					_shuttle.closeDoor(0);
					_shuttle.closeDoor(1);
					_shuttle.broadcastShuttleInfo();
					ThreadPool.schedule(this, 1000);
					break;
				}
				case 5:
				{
					_shuttle.executePath(_shuttle.getShuttleData().getRoutes().get(1));
					break;
				}
			}
			
			_cycle++;
			if (_cycle > 5)
			{
				_cycle = 0;
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.INFO, e.getMessage(), e);
		}
	}
}
