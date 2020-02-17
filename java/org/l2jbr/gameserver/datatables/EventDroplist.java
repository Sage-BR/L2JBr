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
package org.l2jbr.gameserver.datatables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.gameserver.model.holders.EventDropHolder;
import org.l2jbr.gameserver.script.DateRange;

/**
 * This class manage drop of Special Events created by GM for a defined period.<br>
 * During a Special Event all Attackable can drop extra Items.<br>
 * Those extra Items are defined in the table <b>allNpcDateDrops</b>.<br>
 * Each Special Event has a start and end date to stop to drop extra Items automatically.
 */
public class EventDroplist
{
	/**
	 * The table containing all DataDrop object
	 */
	private static final Collection<DateDrop> ALL_NPC_DATE_DROPS = ConcurrentHashMap.newKeySet();
	
	private static final class DateDrop
	{
		private final DateRange _dateRange;
		private final EventDropHolder _eventDrop;
		
		public DateDrop(DateRange dateRange, EventDropHolder eventDrop)
		{
			_dateRange = dateRange;
			_eventDrop = eventDrop;
		}
		
		public EventDropHolder getEventDrop()
		{
			return _eventDrop;
		}
		
		public DateRange getDateRange()
		{
			return _dateRange;
		}
	}
	
	/**
	 * @param dateRange the event drop rate range
	 * @param drop the event drop
	 */
	public void addGlobalDrop(DateRange dateRange, EventDropHolder drop)
	{
		ALL_NPC_DATE_DROPS.add(new DateDrop(dateRange, drop));
	}
	
	/**
	 * @return all DateDrop of EventDroplist allNpcDateDrops within the date range.
	 */
	public Collection<EventDropHolder> getAllDrops()
	{
		final Collection<EventDropHolder> list = new ArrayList<>();
		final Date currentDate = new Date();
		for (DateDrop drop : ALL_NPC_DATE_DROPS)
		{
			if (drop.getDateRange().isWithinRange(currentDate))
			{
				list.add(drop.getEventDrop());
			}
		}
		return list;
	}
	
	public static EventDroplist getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final EventDroplist INSTANCE = new EventDroplist();
	}
}
