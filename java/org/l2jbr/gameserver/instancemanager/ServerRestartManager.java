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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.Shutdown;

/**
 * @author Gigi
 */
public class ServerRestartManager
{
	static final Logger LOGGER = Logger.getLogger(ServerRestartManager.class.getName());
	
	private String nextRestartTime = "unknown";
	
	protected ServerRestartManager()
	{
		try
		{
			final Calendar currentTime = Calendar.getInstance();
			final Calendar restartTime = Calendar.getInstance();
			Calendar lastRestart = null;
			long delay = 0;
			long lastDelay = 0;
			
			for (String scheduledTime : Config.SERVER_RESTART_SCHEDULE)
			{
				final String[] splitTime = scheduledTime.trim().split(":");
				restartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTime[0]));
				restartTime.set(Calendar.MINUTE, Integer.parseInt(splitTime[1]));
				restartTime.set(Calendar.SECOND, 00);
				
				if (restartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					restartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				
				delay = restartTime.getTimeInMillis() - currentTime.getTimeInMillis();
				if (lastDelay == 0)
				{
					lastDelay = delay;
					lastRestart = restartTime;
				}
				if (delay < lastDelay)
				{
					lastDelay = delay;
					lastRestart = restartTime;
				}
			}
			
			if (lastRestart != null)
			{
				nextRestartTime = new SimpleDateFormat("HH:mm").format(lastRestart.getTime());
				ThreadPool.schedule(new ServerRestartTask(), lastDelay - (Config.SERVER_RESTART_SCHEDULE_COUNTDOWN * 1000));
				LOGGER.info("Scheduled server restart at " + lastRestart.getTime() + ".");
			}
		}
		catch (Exception e)
		{
			LOGGER.info("The scheduled server restart config is not set properly, please correct it!");
		}
	}
	
	public String getNextRestartTime()
	{
		return nextRestartTime;
	}
	
	class ServerRestartTask implements Runnable
	{
		@Override
		public void run()
		{
			Shutdown.getInstance().startShutdown(null, Config.SERVER_RESTART_SCHEDULE_COUNTDOWN, true);
		}
	}
	
	public static ServerRestartManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ServerRestartManager INSTANCE = new ServerRestartManager();
	}
}