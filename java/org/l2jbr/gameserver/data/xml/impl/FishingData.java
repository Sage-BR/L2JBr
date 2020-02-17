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
package org.l2jbr.gameserver.data.xml.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jbr.Config;
import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.model.fishing.FishingBait;
import org.l2jbr.gameserver.model.fishing.FishingCatch;
import org.l2jbr.gameserver.model.fishing.FishingRod;

/**
 * This class holds the Fishing information.
 * @author bit
 */
public class FishingData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FishingData.class.getName());
	private final Map<Integer, FishingBait> _baitData = new HashMap<>();
	private final Map<Integer, FishingRod> _rodData = new HashMap<>();
	private int _baitDistanceMin;
	private int _baitDistanceMax;
	private double _expRateMin;
	private double _expRateMax;
	private double _spRateMin;
	private double _spRateMax;
	
	/**
	 * Instantiates a new fishing data.
	 */
	protected FishingData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_baitData.clear();
		parseDatapackFile("data/Fishing.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _baitData.size() + " bait and " + _rodData.size() + " rod data.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node listItem = n.getFirstChild(); listItem != null; listItem = listItem.getNextSibling())
				{
					switch (listItem.getNodeName())
					{
						case "baitDistance":
						{
							_baitDistanceMin = parseInteger(listItem.getAttributes(), "min");
							_baitDistanceMax = parseInteger(listItem.getAttributes(), "max");
							break;
						}
						case "xpRate":
						{
							_expRateMin = parseDouble(listItem.getAttributes(), "min");
							_expRateMax = parseDouble(listItem.getAttributes(), "max");
							break;
						}
						case "spRate":
						{
							_spRateMin = parseDouble(listItem.getAttributes(), "min");
							_spRateMax = parseDouble(listItem.getAttributes(), "max");
							break;
						}
						case "baits":
						{
							for (Node bait = listItem.getFirstChild(); bait != null; bait = bait.getNextSibling())
							{
								if ("bait".equalsIgnoreCase(bait.getNodeName()))
								{
									final NamedNodeMap attrs = bait.getAttributes();
									final int itemId = parseInteger(attrs, "itemId");
									final byte level = parseByte(attrs, "level", (byte) 1);
									final byte minPlayerLevel = parseByte(attrs, "minPlayerLevel");
									final byte maxPlayerLevel = parseByte(attrs, "minPlayerLevel", Config.PLAYER_MAXIMUM_LEVEL);
									final double chance = parseDouble(attrs, "chance");
									final int timeMin = parseInteger(attrs, "timeMin");
									final int timeMax = parseInteger(attrs, "timeMax", timeMin);
									final int waitMin = parseInteger(attrs, "waitMin");
									final int waitMax = parseInteger(attrs, "waitMax", waitMin);
									final boolean isPremiumOnly = parseBoolean(attrs, "isPremiumOnly", false);
									
									if (ItemTable.getInstance().getTemplate(itemId) == null)
									{
										LOGGER.info(getClass().getSimpleName() + ": Could not find item with id " + itemId);
										continue;
									}
									
									final FishingBait baitData = new FishingBait(itemId, level, minPlayerLevel, maxPlayerLevel, chance, timeMin, timeMax, waitMin, waitMax, isPremiumOnly);
									for (Node c = bait.getFirstChild(); c != null; c = c.getNextSibling())
									{
										if ("catch".equalsIgnoreCase(c.getNodeName()))
										{
											final NamedNodeMap cAttrs = c.getAttributes();
											final int cId = parseInteger(cAttrs, "itemId");
											final float cChance = parseFloat(cAttrs, "chance");
											final float cMultiplier = parseFloat(cAttrs, "multiplier", 1f);
											
											if (ItemTable.getInstance().getTemplate(cId) == null)
											{
												LOGGER.info(getClass().getSimpleName() + ": Could not find item with id " + itemId);
												continue;
											}
											
											baitData.addReward(new FishingCatch(cId, cChance, cMultiplier));
										}
									}
									_baitData.put(baitData.getItemId(), baitData);
								}
							}
							break;
						}
						case "rods":
						{
							for (Node rod = listItem.getFirstChild(); rod != null; rod = rod.getNextSibling())
							{
								if ("rod".equalsIgnoreCase(rod.getNodeName()))
								{
									final NamedNodeMap attrs = rod.getAttributes();
									final int itemId = parseInteger(attrs, "itemId");
									final int reduceFishingTime = parseInteger(attrs, "reduceFishingTime", 0);
									final float xpMultiplier = parseFloat(attrs, "xpMultiplier", 1f);
									final float spMultiplier = parseFloat(attrs, "spMultiplier", 1f);
									
									if (ItemTable.getInstance().getTemplate(itemId) == null)
									{
										LOGGER.info(getClass().getSimpleName() + ": Could not find item with id " + itemId);
										continue;
									}
									
									_rodData.put(itemId, new FishingRod(itemId, reduceFishingTime, xpMultiplier, spMultiplier));
								}
							}
						}
					}
				}
			}
		}
	}
	
	public FishingBait getBaitData(int baitItemId)
	{
		return _baitData.get(baitItemId);
	}
	
	public FishingRod getRodData(int rodItemId)
	{
		return _rodData.get(rodItemId);
	}
	
	public int getBaitDistanceMin()
	{
		return _baitDistanceMin;
	}
	
	public int getBaitDistanceMax()
	{
		return _baitDistanceMax;
	}
	
	public double getExpRateMin()
	{
		return _expRateMin;
	}
	
	public double getExpRateMax()
	{
		return _expRateMax;
	}
	
	public double getSpRateMin()
	{
		return _spRateMin;
	}
	
	public double getSpRateMax()
	{
		return _spRateMax;
	}
	
	/**
	 * Gets the single instance of FishingData.
	 * @return single instance of FishingData
	 */
	public static FishingData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FishingData INSTANCE = new FishingData();
	}
}
