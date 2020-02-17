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
package org.l2jbr.gameserver.model.teleporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.network.NpcStringId;

/**
 * @author UnAfraid
 */
public class TeleportLocation extends Location
{
	private final int _id;
	private final String _name;
	private final NpcStringId _npcStringId;
	private final int _questZoneId;
	private final int _feeId;
	private final long _feeCount;
	private final List<Integer> _castleId;
	
	public TeleportLocation(int id, StatsSet set)
	{
		super(set);
		_id = id;
		_name = set.getString("name", null);
		_npcStringId = NpcStringId.getNpcStringIdOrDefault(set.getInt("npcStringId", -1), null);
		_questZoneId = set.getInt("questZoneId", 0);
		_feeId = set.getInt("feeId", Inventory.ADENA_ID);
		_feeCount = set.getLong("feeCount", 0);
		
		final String castleIds = set.getString("castleId", "");
		if (castleIds.isEmpty())
		{
			_castleId = Collections.emptyList();
		}
		else if (!castleIds.contains(";"))
		{
			_castleId = Collections.singletonList(Integer.parseInt(castleIds));
		}
		else
		{
			_castleId = new ArrayList<>();
			for (String castleId : castleIds.split(";"))
			{
				_castleId.add(Integer.parseInt(castleId));
			}
		}
	}
	
	public int getId()
	{
		return _id;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public NpcStringId getNpcStringId()
	{
		return _npcStringId;
	}
	
	public int getQuestZoneId()
	{
		return _questZoneId;
	}
	
	public int getFeeId()
	{
		return _feeId;
	}
	
	public long getFeeCount()
	{
		return _feeCount;
	}
	
	public List<Integer> getCastleId()
	{
		return _castleId;
	}
}