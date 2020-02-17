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
package org.l2jbr.gameserver.model.actor.tasks.creature;

import org.l2jbr.gameserver.ai.CtrlEvent;
import org.l2jbr.gameserver.model.actor.Creature;

/**
 * Task dedicated to notify character's AI
 * @author xban1x
 */
public class NotifyAITask implements Runnable
{
	private final Creature _creature;
	private final CtrlEvent _event;
	
	public NotifyAITask(Creature creature, CtrlEvent event)
	{
		_creature = creature;
		_event = event;
	}
	
	@Override
	public void run()
	{
		if (_creature != null)
		{
			_creature.getAI().notifyEvent(_event, null);
		}
	}
}
