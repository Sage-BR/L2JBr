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
package org.l2jbr.gameserver.model.instancezone.conditions;

import org.l2jbr.Config;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.InstanceTemplate;
import org.l2jbr.gameserver.network.SystemMessageId;

/**
 * Instance level condition
 * @author malyelfik
 */
public class ConditionLevel extends Condition
{
	private final int _min;
	private final int _max;
	
	public ConditionLevel(InstanceTemplate template, StatsSet parameters, boolean onlyLeader, boolean showMessageAndHtml)
	{
		super(template, parameters, onlyLeader, showMessageAndHtml);
		// Load params
		_min = Math.min(Config.PLAYER_MAXIMUM_LEVEL, parameters.getInt("min", 1));
		_max = Math.min(Config.PLAYER_MAXIMUM_LEVEL, parameters.getInt("max", Integer.MAX_VALUE));
		// Set message
		setSystemMessage(SystemMessageId.C1_S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY, (msg, player) -> msg.addString(player.getName()));
	}
	
	@Override
	protected boolean test(PlayerInstance player, Npc npc)
	{
		return (player.getLevel() >= _min) && (player.getLevel() <= _max);
	}
}
