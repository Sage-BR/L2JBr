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
package org.l2jbr.gameserver.model.quest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.l2jbr.gameserver.model.KeyValuePair;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

/**
 * @author UnAfraid
 */
public class QuestCondition
{
	private final Predicate<PlayerInstance> _condition;
	private Map<Integer, String> _perNpcDialog;
	private final String _html;
	
	public QuestCondition(Predicate<PlayerInstance> cond, String html)
	{
		_condition = cond;
		_html = html;
	}
	
	@SafeVarargs
	public QuestCondition(Predicate<PlayerInstance> cond, KeyValuePair<Integer, String>... pairs)
	{
		_condition = cond;
		_html = null;
		_perNpcDialog = new HashMap<>();
		Stream.of(pairs).forEach(pair -> _perNpcDialog.put(pair.getKey(), pair.getValue()));
	}
	
	public boolean test(PlayerInstance player)
	{
		return _condition.test(player);
	}
	
	public String getHtml(Npc npc)
	{
		return _perNpcDialog != null ? _perNpcDialog.get(npc.getId()) : _html;
	}
}
