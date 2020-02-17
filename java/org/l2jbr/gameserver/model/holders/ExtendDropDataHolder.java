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
package org.l2jbr.gameserver.model.holders;

import java.util.List;
import java.util.Map;

import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.conditions.ICondition;
import org.l2jbr.gameserver.network.SystemMessageId;

/**
 * @author Sdw
 */
public class ExtendDropDataHolder
{
	private final int _id;
	private final List<ExtendDropItemHolder> _items;
	private final List<ICondition> _conditions;
	private final Map<Long, SystemMessageId> _systemMessages;
	
	public ExtendDropDataHolder(StatsSet set)
	{
		_id = set.getInt("id");
		_items = set.getList("items", ExtendDropItemHolder.class);
		_conditions = set.getList("conditions", ICondition.class);
		_systemMessages = set.getMap("systemMessages", Long.class, SystemMessageId.class);
	}
	
	public void reward(PlayerInstance player, Npc npc)
	{
		if (_conditions.isEmpty() || _conditions.stream().allMatch(cond -> cond.test(player, npc)))
		{
			_items.forEach(i ->
			{
				final long currentAmount = player.getVariables().getExtendDropCount(_id);
				if ((Rnd.nextDouble() < i.getChance()) && (currentAmount < i.getMaxCount()))
				{
					boolean sendMessage = true;
					final long newAmount = currentAmount + i.getCount();
					if (_systemMessages != null)
					{
						final SystemMessageId systemMessageId = _systemMessages.get(newAmount);
						if (systemMessageId != null)
						{
							sendMessage = false;
							player.sendPacket(systemMessageId);
						}
					}
					player.addItem("ExtendDrop", i.getId(), i.getCount(), player, sendMessage);
					player.getVariables().updateExtendDrop(_id, newAmount);
				}
			});
		}
	}
}