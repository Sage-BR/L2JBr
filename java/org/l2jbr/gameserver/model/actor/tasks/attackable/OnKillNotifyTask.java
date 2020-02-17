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
package org.l2jbr.gameserver.model.actor.tasks.attackable;

import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;

/**
 * @author xban1x
 */
public class OnKillNotifyTask implements Runnable
{
	private final Attackable _attackable;
	private final Quest _quest;
	private final PlayerInstance _killer;
	private final boolean _isSummon;
	
	public OnKillNotifyTask(Attackable attackable, Quest quest, PlayerInstance killer, boolean isSummon)
	{
		_attackable = attackable;
		_quest = quest;
		_killer = killer;
		_isSummon = isSummon;
	}
	
	@Override
	public void run()
	{
		if ((_quest != null) && (_attackable != null) && (_killer != null))
		{
			_quest.notifyKill(_attackable, _killer, _isSummon);
		}
	}
}
