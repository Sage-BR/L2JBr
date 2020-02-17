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
package org.l2jbr.gameserver.model.actor.tasks.player;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

/**
 * @author UnAfraid
 */
public class TeleportTask implements Runnable
{
	private final PlayerInstance _player;
	private final Location _loc;
	
	public TeleportTask(PlayerInstance player, Location loc)
	{
		_player = player;
		_loc = loc;
	}
	
	@Override
	public void run()
	{
		if ((_player != null) && _player.isOnline())
		{
			_player.teleToLocation(_loc, true);
		}
	}
}
