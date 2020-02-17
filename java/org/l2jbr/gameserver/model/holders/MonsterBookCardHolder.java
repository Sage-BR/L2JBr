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

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.gameserver.enums.Faction;

/**
 * @author Mobius
 */
public class MonsterBookCardHolder
{
	private final int _id;
	private final int _monsterId;
	private final Faction _faction;
	private final List<MonsterBookRewardHolder> _rewards = new ArrayList<>(4);
	
	public MonsterBookCardHolder(int id, int monsterId, Faction faction)
	{
		_id = id;
		_monsterId = monsterId;
		_faction = faction;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getMonsterId()
	{
		return _monsterId;
	}
	
	public Faction getFaction()
	{
		return _faction;
	}
	
	public MonsterBookRewardHolder getReward(int level)
	{
		return _rewards.get(level);
	}
	
	public void addReward(MonsterBookRewardHolder reward)
	{
		_rewards.add(reward);
	}
}
