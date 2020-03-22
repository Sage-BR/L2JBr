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
package org.l2jbr.gameserver.model.quest;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.data.sql.impl.AnnouncementsTable;
import org.l2jbr.gameserver.data.xml.impl.NpcData;
import org.l2jbr.gameserver.datatables.EventDroplist;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.instancemanager.EventShrineManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.announce.EventAnnouncement;
import org.l2jbr.gameserver.model.holders.EventDropHolder;
import org.l2jbr.gameserver.script.DateRange;
import org.l2jbr.gameserver.util.Broadcast;

/**
 * Parent class for long time events.<br>
 * Maintains config reading, spawn of NPCs, adding of event's drop.
 * @author GKR
 */
public class LongTimeEvent extends Quest
{
	protected Logger LOGGER = Logger.getLogger(getClass().getName());
	protected String _eventName;
	boolean _enableShrines = false;
	
	// Messages
	protected String _onEnterMsg = "";
	protected String _endMsg = "";
	int _enterAnnounceId = -1;
	
	protected DateRange _eventPeriod = null;
	protected DateRange _dropPeriod;
	
	// NPCs to spawm and their spawn points
	protected final List<NpcSpawn> _spawnList = new ArrayList<>();
	
	// Drop data for event
	protected final List<EventDropHolder> _dropList = new ArrayList<>();
	
	// Items to destroy when event ends.
	protected final List<Integer> _destoyItemsOnEnd = new ArrayList<>();
	
	protected class NpcSpawn
	{
		protected final Location loc;
		protected final int npcId;
		
		protected NpcSpawn(int pNpcId, Location spawnLoc)
		{
			loc = spawnLoc;
			npcId = pNpcId;
		}
	}
	
	public LongTimeEvent()
	{
		super(-1);
		loadConfig();
		
		if (_eventPeriod != null)
		{
			if (_eventPeriod.isWithinRange(new Date()))
			{
				startEvent();
				LOGGER.info("Event " + _eventName + " active till " + _eventPeriod.getEndDate());
			}
			else if (_eventPeriod.getStartDate().after(new Date()))
			{
				final long delay = _eventPeriod.getStartDate().getTime() - System.currentTimeMillis();
				ThreadPool.schedule(new ScheduleStart(), delay);
				LOGGER.info("Event " + _eventName + " will be started at " + _eventPeriod.getStartDate());
			}
			else
			{
				// Destroy items that must exist only on event period.
				destoyItemsOnEnd();
				LOGGER.info("Event " + _eventName + " has passed... Ignored ");
			}
		}
	}
	
	/**
	 * Load event configuration file
	 */
	private void loadConfig()
	{
		new IXmlReader()
		{
			@Override
			public void load()
			{
				parseDatapackFile("data/scripts/events/" + getScriptName() + "/config.xml");
			}
			
			@Override
			public void parseDocument(Document doc, File f)
			{
				if (!doc.getDocumentElement().getNodeName().equalsIgnoreCase("event"))
				{
					throw new NullPointerException("WARNING!!! " + getScriptName() + " event: bad config file!");
				}
				_eventName = doc.getDocumentElement().getAttributes().getNamedItem("name").getNodeValue();
				final String period = doc.getDocumentElement().getAttributes().getNamedItem("active").getNodeValue();
				_eventPeriod = DateRange.parse(period, new SimpleDateFormat("dd MM yyyy", Locale.US));
				
				if ((doc.getDocumentElement().getAttributes().getNamedItem("enableShrines") != null) && doc.getDocumentElement().getAttributes().getNamedItem("enableShrines").getNodeValue().equalsIgnoreCase("true"))
				{
					_enableShrines = true;
				}
				
				if (doc.getDocumentElement().getAttributes().getNamedItem("dropPeriod") != null)
				{
					final String dropPeriod = doc.getDocumentElement().getAttributes().getNamedItem("dropPeriod").getNodeValue();
					_dropPeriod = DateRange.parse(dropPeriod, new SimpleDateFormat("dd MM yyyy", Locale.US));
					// Check if drop period is within range of event period
					if (!_eventPeriod.isWithinRange(_dropPeriod.getStartDate()) || !_eventPeriod.isWithinRange(_dropPeriod.getEndDate()))
					{
						_dropPeriod = _eventPeriod;
					}
				}
				else
				{
					_dropPeriod = _eventPeriod; // Drop period, if not specified, assumes all event period.
				}
				
				if (_eventPeriod == null)
				{
					throw new NullPointerException("WARNING!!! " + getScriptName() + " event: illegal event period");
				}
				
				final Date today = new Date();
				
				if (_eventPeriod.getStartDate().after(today) || _eventPeriod.isWithinRange(today))
				{
					for (Node n = doc.getDocumentElement().getFirstChild(); n != null; n = n.getNextSibling())
					{
						// Loading droplist
						if (n.getNodeName().equalsIgnoreCase("droplist"))
						{
							for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
							{
								if (d.getNodeName().equalsIgnoreCase("add"))
								{
									try
									{
										final int itemId = Integer.parseInt(d.getAttributes().getNamedItem("item").getNodeValue());
										final int minCount = Integer.parseInt(d.getAttributes().getNamedItem("min").getNodeValue());
										final int maxCount = Integer.parseInt(d.getAttributes().getNamedItem("max").getNodeValue());
										final String chance = d.getAttributes().getNamedItem("chance").getNodeValue();
										final double finalChance = !chance.isEmpty() && chance.endsWith("%") ? Double.parseDouble(chance.substring(0, chance.length() - 1)) : 0;
										final Node minLevelNode = d.getAttributes().getNamedItem("minLevel");
										final int minLevel = minLevelNode == null ? 1 : Integer.parseInt(minLevelNode.getNodeValue());
										final Node maxLevelNode = d.getAttributes().getNamedItem("maxLevel");
										final int maxLevel = maxLevelNode == null ? Integer.MAX_VALUE : Integer.parseInt(maxLevelNode.getNodeValue());
										final Node monsterIdsNode = d.getAttributes().getNamedItem("monsterIds");
										final List<Integer> monsterIds = new ArrayList<>();
										if (monsterIdsNode != null)
										{
											for (String id : monsterIdsNode.getNodeValue().split(","))
											{
												monsterIds.add(Integer.parseInt(id));
											}
										}
										
										if (ItemTable.getInstance().getTemplate(itemId) == null)
										{
											LOGGER.warning(getScriptName() + " event: " + itemId + " is wrong item id, item was not added in droplist");
											continue;
										}
										
										if (minCount > maxCount)
										{
											LOGGER.warning(getScriptName() + " event: item " + itemId + " - min greater than max, item was not added in droplist");
											continue;
										}
										
										if ((finalChance < 0) || (finalChance > 100))
										{
											LOGGER.warning(getScriptName() + " event: item " + itemId + " - incorrect drop chance, item was not added in droplist");
											continue;
										}
										
										_dropList.add(new EventDropHolder(itemId, minCount, maxCount, finalChance, minLevel, maxLevel, monsterIds));
									}
									catch (NumberFormatException nfe)
									{
										LOGGER.warning("Wrong number format in config.xml droplist block for " + getScriptName() + " event");
									}
								}
							}
						}
						else if (n.getNodeName().equalsIgnoreCase("spawnlist"))
						{
							// Loading spawnlist
							for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
							{
								if (d.getNodeName().equalsIgnoreCase("add"))
								{
									try
									{
										final int npcId = Integer.parseInt(d.getAttributes().getNamedItem("npc").getNodeValue());
										final int xPos = Integer.parseInt(d.getAttributes().getNamedItem("x").getNodeValue());
										final int yPos = Integer.parseInt(d.getAttributes().getNamedItem("y").getNodeValue());
										final int zPos = Integer.parseInt(d.getAttributes().getNamedItem("z").getNodeValue());
										final int heading = d.getAttributes().getNamedItem("heading").getNodeValue() != null ? Integer.parseInt(d.getAttributes().getNamedItem("heading").getNodeValue()) : 0;
										
										if (NpcData.getInstance().getTemplate(npcId) == null)
										{
											LOGGER.warning(getScriptName() + " event: " + npcId + " is wrong NPC id, NPC was not added in spawnlist");
											continue;
										}
										
										_spawnList.add(new NpcSpawn(npcId, new Location(xPos, yPos, zPos, heading)));
									}
									catch (NumberFormatException nfe)
									{
										LOGGER.warning("Wrong number format in config.xml spawnlist block for " + getScriptName() + " event");
									}
								}
							}
						}
						else if (n.getNodeName().equalsIgnoreCase("messages"))
						{
							// Loading Messages
							for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
							{
								if (d.getNodeName().equalsIgnoreCase("add"))
								{
									final String msgType = d.getAttributes().getNamedItem("type").getNodeValue();
									final String msgText = d.getAttributes().getNamedItem("text").getNodeValue();
									if ((msgType != null) && (msgText != null))
									{
										if (msgType.equalsIgnoreCase("onEnd"))
										{
											_endMsg = msgText;
										}
										else if (msgType.equalsIgnoreCase("onEnter"))
										{
											_onEnterMsg = msgText;
										}
									}
								}
							}
						}
					}
				}
				
				// Load destroy item list at all times.
				for (Node n = doc.getDocumentElement().getFirstChild(); n != null; n = n.getNextSibling())
				{
					if (n.getNodeName().equalsIgnoreCase("destoyItemsOnEnd"))
					{
						for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if (d.getNodeName().equalsIgnoreCase("item"))
							{
								try
								{
									final int itemId = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
									if (ItemTable.getInstance().getTemplate(itemId) == null)
									{
										LOGGER.warning(getScriptName() + " event: Item " + itemId + " does not exist.");
										continue;
									}
									_destoyItemsOnEnd.add(itemId);
								}
								catch (NumberFormatException nfe)
								{
									LOGGER.warning("Wrong number format in config.xml destoyItemsOnEnd block for " + getScriptName() + " event");
								}
							}
						}
					}
				}
			}
		}.load();
		
	}
	
	/**
	 * Maintenance event start - adds global drop, spawns event NPCs, shows start announcement.
	 */
	protected void startEvent()
	{
		// Add drop.
		if (_dropList != null)
		{
			for (EventDropHolder drop : _dropList)
			{
				EventDroplist.getInstance().addGlobalDrop(_dropPeriod, drop);
			}
		}
		
		// Add spawns.
		final Long millisToEventEnd = _eventPeriod.getEndDate().getTime() - System.currentTimeMillis();
		if (_spawnList != null)
		{
			for (NpcSpawn spawn : _spawnList)
			{
				addSpawn(spawn.npcId, spawn.loc.getX(), spawn.loc.getY(), spawn.loc.getZ(), spawn.loc.getHeading(), false, millisToEventEnd, false);
			}
		}
		
		// Enable town shrines.
		if (_enableShrines)
		{
			EventShrineManager.getInstance().setEnabled(true);
		}
		
		// Event enter announcement.
		if (!_onEnterMsg.isEmpty())
		{
			// Send message on begin.
			Broadcast.toAllOnlinePlayers(_onEnterMsg);
			
			// Add announce for entering players.
			final EventAnnouncement announce = new EventAnnouncement(_eventPeriod, _onEnterMsg);
			AnnouncementsTable.getInstance().addAnnouncement(announce);
			_enterAnnounceId = announce.getId();
		}
		
		// Schedule event end.
		ThreadPool.schedule(new ScheduleEnd(), millisToEventEnd);
	}
	
	/**
	 * @return event period
	 */
	public DateRange getEventPeriod()
	{
		return _eventPeriod;
	}
	
	/**
	 * @return {@code true} if now is event period
	 */
	public boolean isEventPeriod()
	{
		return _eventPeriod.isWithinRange(new Date());
	}
	
	/**
	 * @return {@code true} if now is drop period
	 */
	public boolean isDropPeriod()
	{
		return _dropPeriod.isWithinRange(new Date());
	}
	
	protected class ScheduleStart implements Runnable
	{
		@Override
		public void run()
		{
			startEvent();
		}
	}
	
	protected class ScheduleEnd implements Runnable
	{
		@Override
		public void run()
		{
			// Disable town shrines.
			if (_enableShrines)
			{
				EventShrineManager.getInstance().setEnabled(false);
			}
			
			// Destroy items that must exist only on event period.
			destoyItemsOnEnd();
			
			// Send message on end.
			if (!_endMsg.isEmpty())
			{
				Broadcast.toAllOnlinePlayers(_endMsg);
			}
			
			// Remove announce for entering players.
			if (_enterAnnounceId != -1)
			{
				AnnouncementsTable.getInstance().deleteAnnouncement(_enterAnnounceId);
			}
		}
	}
	
	void destoyItemsOnEnd()
	{
		if (!_destoyItemsOnEnd.isEmpty())
		{
			for (int itemId : _destoyItemsOnEnd)
			{
				// Remove item from online players.
				for (PlayerInstance player : World.getInstance().getPlayers())
				{
					if (player != null)
					{
						player.destroyItemByItemId(_eventName, itemId, -1, player, true);
					}
				}
				// Update database.
				try (Connection con = DatabaseFactory.getConnection();
					PreparedStatement statement = con.prepareStatement("DELETE FROM items WHERE item_id=?"))
				{
					statement.setInt(1, itemId);
					statement.execute();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
