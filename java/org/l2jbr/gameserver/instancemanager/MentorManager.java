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
package org.l2jbr.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.model.Mentee;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.skills.BuffInfo;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.variables.PlayerVariables;

/**
 * @author UnAfraid
 */
public class MentorManager
{
	private static final Logger LOGGER = Logger.getLogger(MentorManager.class.getName());
	
	private final Map<Integer, Map<Integer, Mentee>> _menteeData = new ConcurrentHashMap<>();
	private final Map<Integer, Mentee> _mentors = new ConcurrentHashMap<>();
	
	protected MentorManager()
	{
		load();
	}
	
	private void load()
	{
		try (Connection con = DatabaseFactory.getConnection();
			Statement statement = con.createStatement();
			ResultSet rset = statement.executeQuery("SELECT * FROM character_mentees"))
		{
			while (rset.next())
			{
				addMentor(rset.getInt("mentorId"), rset.getInt("charId"));
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	/**
	 * Removes mentee for current PlayerInstance
	 * @param mentorId
	 * @param menteeId
	 */
	public void deleteMentee(int mentorId, int menteeId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_mentees WHERE mentorId = ? AND charId = ?"))
		{
			statement.setInt(1, mentorId);
			statement.setInt(2, menteeId);
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	/**
	 * @param mentorId
	 * @param menteeId
	 */
	public void deleteMentor(int mentorId, int menteeId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_mentees WHERE mentorId = ? AND charId = ?"))
		{
			statement.setInt(1, mentorId);
			statement.setInt(2, menteeId);
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
		finally
		{
			removeMentor(mentorId, menteeId);
		}
	}
	
	public boolean isMentor(int objectId)
	{
		return _menteeData.containsKey(objectId);
	}
	
	public boolean isMentee(int objectId)
	{
		return _menteeData.values().stream().anyMatch(map -> map.containsKey(objectId));
	}
	
	public Map<Integer, Map<Integer, Mentee>> getMentorData()
	{
		return _menteeData;
	}
	
	public void cancelAllMentoringBuffs(PlayerInstance player)
	{
		if (player == null)
		{
			return;
		}
		
		//@formatter:off
		player.getEffectList().getEffects()
			.stream()
			.map(BuffInfo::getSkill)
			.filter(Skill::isMentoring)
			.forEach(player::stopSkillEffects);
		//@formatter:on
	}
	
	public void setPenalty(int mentorId, long penalty)
	{
		final PlayerInstance player = World.getInstance().getPlayer(mentorId);
		final PlayerVariables vars = player != null ? player.getVariables() : new PlayerVariables(mentorId);
		vars.set("Mentor-Penalty-" + mentorId, String.valueOf(System.currentTimeMillis() + penalty));
	}
	
	public long getMentorPenalty(int mentorId)
	{
		final PlayerInstance player = World.getInstance().getPlayer(mentorId);
		final PlayerVariables vars = player != null ? player.getVariables() : new PlayerVariables(mentorId);
		return vars.getLong("Mentor-Penalty-" + mentorId, 0);
	}
	
	/**
	 * @param mentorId
	 * @param menteeId
	 */
	public void addMentor(int mentorId, int menteeId)
	{
		final Map<Integer, Mentee> mentees = _menteeData.computeIfAbsent(mentorId, map -> new ConcurrentHashMap<>());
		if (mentees.containsKey(menteeId))
		{
			mentees.get(menteeId).load(); // Just reloading data if is already there
		}
		else
		{
			mentees.put(menteeId, new Mentee(menteeId));
		}
	}
	
	/**
	 * @param mentorId
	 * @param menteeId
	 */
	public void removeMentor(int mentorId, int menteeId)
	{
		if (_menteeData.containsKey(mentorId))
		{
			_menteeData.get(mentorId).remove(menteeId);
			if (_menteeData.get(mentorId).isEmpty())
			{
				_menteeData.remove(mentorId);
				_mentors.remove(mentorId);
			}
		}
	}
	
	/**
	 * @param menteeId
	 * @return
	 */
	public Mentee getMentor(int menteeId)
	{
		for (Entry<Integer, Map<Integer, Mentee>> map : _menteeData.entrySet())
		{
			if (map.getValue().containsKey(menteeId))
			{
				if (!_mentors.containsKey(map.getKey()))
				{
					_mentors.put(map.getKey(), new Mentee(map.getKey()));
				}
				return _mentors.get(map.getKey());
			}
		}
		return null;
	}
	
	public Collection<Mentee> getMentees(int mentorId)
	{
		if (_menteeData.containsKey(mentorId))
		{
			return _menteeData.get(mentorId).values();
		}
		return Collections.emptyList();
	}
	
	/**
	 * @param mentorId
	 * @param menteeId
	 * @return
	 */
	public Mentee getMentee(int mentorId, int menteeId)
	{
		if (_menteeData.containsKey(mentorId))
		{
			return _menteeData.get(mentorId).get(menteeId);
		}
		return null;
	}
	
	public boolean isAllMenteesOffline(int menteorId, int menteeId)
	{
		boolean isAllMenteesOffline = true;
		for (Mentee men : getMentees(menteorId))
		{
			if (men.isOnline() && (men.getObjectId() != menteeId))
			{
				if (isAllMenteesOffline)
				{
					isAllMenteesOffline = false;
					break;
				}
			}
		}
		return isAllMenteesOffline;
	}
	
	public boolean hasOnlineMentees(int menteorId)
	{
		return getMentees(menteorId).stream().filter(Objects::nonNull).filter(Mentee::isOnline).count() > 0;
	}
	
	public static MentorManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MentorManager INSTANCE = new MentorManager();
	}
}
