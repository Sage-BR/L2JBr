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
package org.l2jbr.gameserver;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.events.EventDispatcher;
import org.l2jbr.gameserver.model.events.impl.OnDayNightChange;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * Game Time controller class.
 * @author Forsaiken
 */
public class GameTimeController extends Thread
{
	private static final Logger LOGGER = Logger.getLogger(GameTimeController.class.getName());
	
	public static final int TICKS_PER_SECOND = 10; // not able to change this without checking through code
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	public static final int IG_DAYS_PER_DAY = 6;
	public static final int MILLIS_PER_IG_DAY = (3600000 * 24) / IG_DAYS_PER_DAY;
	public static final int SECONDS_PER_IG_DAY = MILLIS_PER_IG_DAY / 1000;
	public static final int TICKS_PER_IG_DAY = SECONDS_PER_IG_DAY * TICKS_PER_SECOND;
	private final static int SHADOW_SENSE_ID = 294;
	
	private static GameTimeController _instance;
	
	private final Set<Creature> _movingObjects = ConcurrentHashMap.newKeySet();
	private final Set<Creature> _shadowSenseCharacters = ConcurrentHashMap.newKeySet();
	private final long _referenceTime;
	
	private GameTimeController()
	{
		super("GameTimeController");
		super.setDaemon(true);
		super.setPriority(MAX_PRIORITY);
		
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		_referenceTime = c.getTimeInMillis();
		
		super.start();
	}
	
	public static void init()
	{
		_instance = new GameTimeController();
	}
	
	public int getGameTime()
	{
		return (getGameTicks() % TICKS_PER_IG_DAY) / MILLIS_IN_TICK;
	}
	
	public int getGameHour()
	{
		return getGameTime() / 60;
	}
	
	public int getGameMinute()
	{
		return getGameTime() % 60;
	}
	
	public boolean isNight()
	{
		return getGameHour() < 6;
	}
	
	/**
	 * The true GameTime tick. Directly taken from current time. This represents the tick of the time.
	 * @return
	 */
	public int getGameTicks()
	{
		return (int) ((System.currentTimeMillis() - _referenceTime) / MILLIS_IN_TICK);
	}
	
	/**
	 * Add a Creature to movingObjects of GameTimeController.
	 * @param creature The Creature to add to movingObjects of GameTimeController
	 */
	public void registerMovingObject(Creature creature)
	{
		if (creature == null)
		{
			return;
		}
		
		if (!_movingObjects.contains(creature))
		{
			_movingObjects.add(creature);
		}
	}
	
	/**
	 * Move all Creatures contained in movingObjects of GameTimeController.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * All Creature in movement are identified in <B>movingObjects</B> of GameTimeController.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <ul>
	 * <li>Update the position of each Creature</li>
	 * <li>If movement is finished, the Creature is removed from movingObjects</li>
	 * <li>Create a task to update the _knownObject and _knowPlayers of each Creature that finished its movement and of their already known WorldObject then notify AI with EVT_ARRIVED</li>
	 * </ul>
	 */
	private void moveObjects()
	{
		_movingObjects.removeIf(Creature::updatePosition);
	}
	
	public void stopTimer()
	{
		super.interrupt();
		LOGGER.info(getClass().getSimpleName() + ": Stopped.");
	}
	
	@Override
	public void run()
	{
		LOGGER.info(getClass().getSimpleName() + ": Started.");
		
		long nextTickTime;
		long sleepTime;
		boolean isNight = isNight();
		
		EventDispatcher.getInstance().notifyEventAsync(new OnDayNightChange(isNight));
		
		while (true)
		{
			nextTickTime = ((System.currentTimeMillis() / MILLIS_IN_TICK) * MILLIS_IN_TICK) + 100;
			
			try
			{
				moveObjects();
			}
			catch (Throwable e)
			{
				LOGGER.log(Level.WARNING, getClass().getSimpleName(), e);
			}
			
			sleepTime = nextTickTime - System.currentTimeMillis();
			if (sleepTime > 0)
			{
				try
				{
					Thread.sleep(sleepTime);
				}
				catch (InterruptedException e)
				{
				}
			}
			
			if (isNight() != isNight)
			{
				isNight = !isNight;
				EventDispatcher.getInstance().notifyEventAsync(new OnDayNightChange(isNight));
				notifyShadowSense();
			}
		}
	}
	
	public synchronized void addShadowSenseCharacter(Creature creature)
	{
		if (!_shadowSenseCharacters.contains(creature))
		{
			_shadowSenseCharacters.add(creature);
			if (isNight())
			{
				final SystemMessage msg = new SystemMessage(SystemMessageId.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT);
				msg.addSkillName(SHADOW_SENSE_ID);
				creature.sendPacket(msg);
			}
		}
	}
	
	public void removeShadowSenseCharacter(Creature creature)
	{
		_shadowSenseCharacters.remove(creature);
	}
	
	private void notifyShadowSense()
	{
		final SystemMessage msg = new SystemMessage(isNight() ? SystemMessageId.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT : SystemMessageId.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR);
		msg.addSkillName(SHADOW_SENSE_ID);
		for (Creature creature : _shadowSenseCharacters)
		{
			creature.getStat().recalculateStats(true);
			creature.sendPacket(msg);
		}
	}
	
	public static GameTimeController getInstance()
	{
		return _instance;
	}
}