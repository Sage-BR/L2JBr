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
package org.l2jbr.gameserver.model.clanhallauction;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.l2jbr.gameserver.model.clan.Clan;

/**
 * @author Sdw
 */
public class Bidder
{
	private final Clan _clan;
	private final long _bid;
	private final long _time;
	
	public Bidder(Clan clan, long bid, long time)
	{
		_clan = clan;
		_bid = bid;
		_time = time == 0 ? Instant.now().toEpochMilli() : time;
	}
	
	public Clan getClan()
	{
		return _clan;
	}
	
	public String getClanName()
	{
		return _clan.getName();
	}
	
	public String getLeaderName()
	{
		return _clan.getLeaderName();
	}
	
	public long getBid()
	{
		return _bid;
	}
	
	public long getTime()
	{
		return _time;
	}
	
	public String getFormattedTime()
	{
		return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(Instant.ofEpochMilli(_time).atZone(ZoneId.systemDefault()));
	}
}
