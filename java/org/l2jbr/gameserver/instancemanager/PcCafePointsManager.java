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

import org.l2jbr.Config;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExPCCafePointInfo;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

public class PcCafePointsManager
{
	public void givePcCafePoint(PlayerInstance player, double exp)
	{
		if (!Config.PC_CAFE_ENABLED || player.isInsideZone(ZoneId.PEACE) || player.isInsideZone(ZoneId.PVP) || player.isInsideZone(ZoneId.SIEGE) || (player.isOnlineInt() == 0) || player.isJailed())
		{
			return;
		}
		
		// PC-points only premium accounts
		if (Config.PC_CAFE_ONLY_PREMIUM && !player.hasPremiumStatus())
		{
			return;
		}
		
		if (player.getPcCafePoints() >= Config.PC_CAFE_MAX_POINTS)
		{
			final SystemMessage message = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_THE_MAXIMUM_NUMBER_OF_PA_POINTS);
			player.sendPacket(message);
			return;
		}
		
		int points = (int) (exp * 0.0001 * Config.PC_CAFE_POINT_RATE);
		
		if (Config.PC_CAFE_RANDOM_POINT)
		{
			points = Rnd.get(points / 2, points);
		}
		
		if ((points == 0) && (exp > 0) && Config.PC_CAFE_REWARD_LOW_EXP_KILLS && (Rnd.get(100) < Config.PC_CAFE_LOW_EXP_KILLS_CHANCE))
		{
			points = 1; // minimum points
		}
		
		if (points <= 0)
		{
			return;
		}
		
		SystemMessage message = null;
		if (Config.PC_CAFE_ENABLE_DOUBLE_POINTS && (Rnd.get(100) < Config.PC_CAFE_DOUBLE_POINTS_CHANCE))
		{
			points *= 2;
			message = new SystemMessage(SystemMessageId.DOUBLE_POINTS_YOU_EARNED_S1_PA_POINT_S);
		}
		else
		{
			message = new SystemMessage(SystemMessageId.YOU_EARNED_S1_PA_POINT_S);
		}
		if ((player.getPcCafePoints() + points) > Config.PC_CAFE_MAX_POINTS)
		{
			points = Config.PC_CAFE_MAX_POINTS - player.getPcCafePoints();
		}
		message.addLong(points);
		player.sendPacket(message);
		player.setPcCafePoints(player.getPcCafePoints() + points);
		player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), points, 1));
	}
	
	/**
	 * Gets the single instance of {@code PcCafePointsManager}.
	 * @return single instance of {@code PcCafePointsManager}
	 */
	public static PcCafePointsManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PcCafePointsManager INSTANCE = new PcCafePointsManager();
	}
}