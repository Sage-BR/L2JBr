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
package org.l2jbr.gameserver.model.skills;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.gameserver.model.actor.Creature;

/**
 * @author UnAfraid
 */
public class SkillChannelized
{
	private final Map<Integer, Map<Integer, Creature>> _channelizers = new ConcurrentHashMap<>();
	
	public void addChannelizer(int skillId, Creature channelizer)
	{
		_channelizers.computeIfAbsent(skillId, k -> new ConcurrentHashMap<>()).put(channelizer.getObjectId(), channelizer);
	}
	
	public void removeChannelizer(int skillId, Creature channelizer)
	{
		getChannelizers(skillId).remove(channelizer.getObjectId());
	}
	
	public int getChannerlizersSize(int skillId)
	{
		return getChannelizers(skillId).size();
	}
	
	public Map<Integer, Creature> getChannelizers(int skillId)
	{
		return _channelizers.getOrDefault(skillId, Collections.emptyMap());
	}
	
	public void abortChannelization()
	{
		for (Map<Integer, Creature> map : _channelizers.values())
		{
			for (Creature channelizer : map.values())
			{
				channelizer.abortCast();
			}
		}
		_channelizers.clear();
	}
	
	public boolean isChannelized()
	{
		for (Map<Integer, Creature> map : _channelizers.values())
		{
			if (!map.isEmpty())
			{
				return true;
			}
		}
		return false;
	}
}
