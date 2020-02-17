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
package org.l2jbr.gameserver.model.events.impl.item;

import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.impl.IBaseEvent;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;

/**
 * @author UnAfraid
 */
public class OnItemCreate implements IBaseEvent
{
	private final String _process;
	private final ItemInstance _item;
	private final Creature _creature;
	private final Object _reference;
	
	public OnItemCreate(String process, ItemInstance item, Creature actor, Object reference)
	{
		_process = process;
		_item = item;
		_creature = actor;
		_reference = reference;
	}
	
	public String getProcess()
	{
		return _process;
	}
	
	public ItemInstance getItem()
	{
		return _item;
	}
	
	public Creature getActiveChar()
	{
		return _creature;
	}
	
	public Object getReference()
	{
		return _reference;
	}
	
	@Override
	public EventType getType()
	{
		return EventType.ON_ITEM_CREATE;
	}
}
