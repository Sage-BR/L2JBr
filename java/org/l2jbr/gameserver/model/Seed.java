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
package org.l2jbr.gameserver.model;

import org.l2jbr.Config;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.model.items.Item;

public class Seed
{
	private final int _seedId;
	private final int _cropId; // crop type
	private final int _level; // seed level
	private final int _matureId; // mature crop type
	private final int _reward1;
	private final int _reward2;
	private final int _castleId; // id of manor (castle id) where seed can be farmed
	private final boolean _isAlternative;
	private final int _limitSeeds;
	private final int _limitCrops;
	private final long _seedReferencePrice;
	private final long _cropReferencePrice;
	
	public Seed(StatsSet set)
	{
		_cropId = set.getInt("id");
		_seedId = set.getInt("seedId");
		_level = set.getInt("level");
		_matureId = set.getInt("mature_Id");
		_reward1 = set.getInt("reward1");
		_reward2 = set.getInt("reward2");
		_castleId = set.getInt("castleId");
		_isAlternative = set.getBoolean("alternative");
		_limitCrops = set.getInt("limit_crops");
		_limitSeeds = set.getInt("limit_seed");
		// Set prices
		Item item = ItemTable.getInstance().getTemplate(_cropId);
		_cropReferencePrice = (item != null) ? item.getReferencePrice() : 1;
		item = ItemTable.getInstance().getTemplate(_seedId);
		_seedReferencePrice = (item != null) ? item.getReferencePrice() : 1;
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
	
	public int getSeedId()
	{
		return _seedId;
	}
	
	public int getCropId()
	{
		return _cropId;
	}
	
	public int getMatureId()
	{
		return _matureId;
	}
	
	public int getReward(int type)
	{
		return (type == 1) ? _reward1 : _reward2;
	}
	
	public int getLevel()
	{
		return _level;
	}
	
	public boolean isAlternative()
	{
		return _isAlternative;
	}
	
	public int getSeedLimit()
	{
		return _limitSeeds * Config.RATE_DROP_MANOR;
	}
	
	public int getCropLimit()
	{
		return _limitCrops * Config.RATE_DROP_MANOR;
	}
	
	public long getSeedReferencePrice()
	{
		return _seedReferencePrice;
	}
	
	public long getSeedMaxPrice()
	{
		return _seedReferencePrice * 10;
	}
	
	public long getSeedMinPrice()
	{
		return (int) (_seedReferencePrice * 0.6);
	}
	
	public long getCropReferencePrice()
	{
		return _cropReferencePrice;
	}
	
	public long getCropMaxPrice()
	{
		return _cropReferencePrice * 10;
	}
	
	public int getCropMinPrice()
	{
		return (int) (_cropReferencePrice * 0.6);
	}
	
	@Override
	public String toString()
	{
		return "SeedData [_id=" + _seedId + ", _level=" + _level + ", _crop=" + _cropId + ", _mature=" + _matureId + ", _type1=" + _reward1 + ", _type2=" + _reward2 + ", _manorId=" + _castleId + ", _isAlternative=" + _isAlternative + ", _limitSeeds=" + _limitSeeds + ", _limitCrops=" + _limitCrops + "]";
	}
}