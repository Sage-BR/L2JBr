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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.data.sql.impl.ClanTable;
import org.l2jbr.gameserver.data.xml.impl.DailyMissionData;
import org.l2jbr.gameserver.model.DailyMissionDataHolder;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.stat.PlayerStat;
import org.l2jbr.gameserver.model.base.SubClass;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.clan.ClanMember;
import org.l2jbr.gameserver.model.eventengine.AbstractEvent;
import org.l2jbr.gameserver.model.eventengine.AbstractEventManager;
import org.l2jbr.gameserver.model.eventengine.ScheduleTarget;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.olympiad.Olympiad;
import org.l2jbr.gameserver.model.variables.PlayerVariables;
import org.l2jbr.gameserver.network.serverpackets.ExVoteSystemInfo;
import org.l2jbr.gameserver.network.serverpackets.ExWorldChatCnt;

/**
 * @author UnAfraid
 */
public class DailyTaskManager extends AbstractEventManager<AbstractEvent<?>>
{
	private static final Logger LOGGER = Logger.getLogger(DailyTaskManager.class.getName());
	
	protected DailyTaskManager()
	{
	}
	
	@Override
	public void onInitialized()
	{
	}
	
	@ScheduleTarget
	private void onReset()
	{
		resetExtendDrop();
		resetDailyMissionRewards();
		resetDailySkills();
		resetRecommends();
		resetWorldChatPoints();
		resetTrainingCamp();
	}
	
	@ScheduleTarget
	private void onSave()
	{
		GlobalVariablesManager.getInstance().storeMe();
		
		if (Olympiad.getInstance().inCompPeriod())
		{
			Olympiad.getInstance().saveOlympiadStatus();
			LOGGER.info("Olympiad System: Data updated.");
		}
	}
	
	@ScheduleTarget
	private void onClanLeaderApply()
	{
		for (Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getNewLeaderId() != 0)
			{
				final ClanMember member = clan.getClanMember(clan.getNewLeaderId());
				if (member == null)
				{
					continue;
				}
				
				clan.setNewLeader(member);
			}
		}
		LOGGER.info("Clan leaders has been updated.");
	}
	
	@ScheduleTarget
	private void onVitalityReset()
	{
		if (!Config.ENABLE_VITALITY)
		{
			return;
		}
		
		for (PlayerInstance player : World.getInstance().getPlayers())
		{
			player.setVitalityPoints(PlayerStat.MAX_VITALITY_POINTS, false);
			
			for (SubClass subclass : player.getSubClasses().values())
			{
				subclass.setVitalityPoints(PlayerStat.MAX_VITALITY_POINTS);
			}
		}
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement st = con.prepareStatement("UPDATE character_subclasses SET vitality_points = ?"))
			{
				st.setInt(1, PlayerStat.MAX_VITALITY_POINTS);
				st.execute();
			}
			
			try (PreparedStatement st = con.prepareStatement("UPDATE characters SET vitality_points = ?"))
			{
				st.setInt(1, PlayerStat.MAX_VITALITY_POINTS);
				st.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error while updating vitality", e);
		}
		LOGGER.info("Vitality resetted");
	}
	
	private void resetExtendDrop()
	{
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ?"))
		{
			ps.setString(1, PlayerVariables.EXTEND_DROP);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset extend drop : ", e);
		}
		
		// Update data for online players.
		World.getInstance().getPlayers().stream().forEach(player ->
		{
			player.getVariables().remove(PlayerVariables.EXTEND_DROP);
			player.getVariables().storeMe();
		});
		
		LOGGER.info("Daily world chat points has been resetted.");
	}
	
	private void resetDailySkills()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			final List<SkillHolder> dailySkills = getVariables().getList("reset_skills", SkillHolder.class, Collections.emptyList());
			for (SkillHolder skill : dailySkills)
			{
				try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills_save WHERE skill_id=?;"))
				{
					ps.setInt(1, skill.getSkillId());
					ps.execute();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset daily skill reuse: ", e);
		}
		LOGGER.info("Daily skill reuse cleaned.");
	}
	
	private void resetWorldChatPoints()
	{
		if (!Config.ENABLE_WORLD_CHAT)
		{
			return;
		}
		
		// Update data for offline players.
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE character_variables SET val = ? WHERE var = ?"))
		{
			ps.setInt(1, 0);
			ps.setString(2, PlayerVariables.WORLD_CHAT_VARIABLE_NAME);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset daily world chat points: ", e);
		}
		
		// Update data for online players.
		World.getInstance().getPlayers().stream().forEach(player ->
		{
			player.setWorldChatUsed(0);
			player.sendPacket(new ExWorldChatCnt(player));
			player.getVariables().storeMe();
		});
		
		LOGGER.info("Daily world chat points has been resetted.");
	}
	
	private void resetRecommends()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left = ?, rec_have = 0 WHERE rec_have <= 20"))
			{
				ps.setInt(1, 0); // Rec left = 0
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left = ?, rec_have = GREATEST(rec_have - 20,0) WHERE rec_have > 20"))
			{
				ps.setInt(1, 0); // Rec left = 0
				ps.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not reset Recommendations System: ", e);
		}
		
		World.getInstance().getPlayers().stream().forEach(player ->
		{
			player.setRecomLeft(0);
			player.setRecomHave(player.getRecomHave() - 20);
			player.sendPacket(new ExVoteSystemInfo(player));
			player.broadcastUserInfo();
		});
	}
	
	private void resetTrainingCamp()
	{
		if (Config.TRAINING_CAMP_ENABLE)
		{
			// Update data for offline players.
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM account_gsdata WHERE var = ?"))
			{
				ps.setString(1, "TRAINING_CAMP_DURATION");
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Could not reset Training Camp: ", e);
			}
			
			// Update data for online players.
			World.getInstance().getPlayers().stream().forEach(player ->
			{
				player.resetTraingCampDuration();
				player.getAccountVariables().storeMe();
			});
			
			LOGGER.info("Training Camp daily time has been resetted.");
		}
	}
	
	private void resetDailyMissionRewards()
	{
		DailyMissionData.getInstance().getDailyMissionData().forEach(DailyMissionDataHolder::reset);
	}
	
	public static DailyTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DailyTaskManager INSTANCE = new DailyTaskManager();
	}
}
