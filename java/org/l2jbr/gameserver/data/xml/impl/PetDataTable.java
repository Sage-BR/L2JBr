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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.enums.MountType;
import org.l2jbr.gameserver.model.PetData;
import org.l2jbr.gameserver.model.PetLevelData;
import org.l2jbr.gameserver.model.StatsSet;

/**
 * This class parse and hold all pet parameters.<br>
 * TODO: load and use all pet parameters.
 * @author Zoey76 (rework)
 */
public class PetDataTable implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(PetDataTable.class.getName());
	
	private final Map<Integer, PetData> _pets = new ConcurrentHashMap<>();
	
	/**
	 * Instantiates a new pet data table.
	 */
	protected PetDataTable()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_pets.clear();
		parseDatapackDirectory("data/stats/pets", false);
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _pets.size() + " pets.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		NamedNodeMap attrs;
		final Node n = doc.getFirstChild();
		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
		{
			if (d.getNodeName().equals("pet"))
			{
				final int npcId = parseInteger(d.getAttributes(), "id");
				final int itemId = parseInteger(d.getAttributes(), "itemId");
				// index ignored for now
				final PetData data = new PetData(npcId, itemId);
				for (Node p = d.getFirstChild(); p != null; p = p.getNextSibling())
				{
					if (p.getNodeName().equals("set"))
					{
						attrs = p.getAttributes();
						final String type = attrs.getNamedItem("name").getNodeValue();
						if ("food".equals(type))
						{
							for (String foodId : attrs.getNamedItem("val").getNodeValue().split(";"))
							{
								data.addFood(Integer.valueOf(foodId));
							}
						}
						else if ("load".equals(type))
						{
							data.setLoad(parseInteger(attrs, "val"));
						}
						else if ("hungry_limit".equals(type))
						{
							data.setHungryLimit(parseInteger(attrs, "val"));
						}
						else if ("sync_level".equals(type))
						{
							data.setSyncLevel(parseInteger(attrs, "val") == 1);
						}
						// evolve ignored
					}
					else if (p.getNodeName().equals("skills"))
					{
						for (Node s = p.getFirstChild(); s != null; s = s.getNextSibling())
						{
							if (s.getNodeName().equals("skill"))
							{
								attrs = s.getAttributes();
								data.addNewSkill(parseInteger(attrs, "skillId"), parseInteger(attrs, "skillLvl"), parseInteger(attrs, "minLvl"));
							}
						}
					}
					else if (p.getNodeName().equals("stats"))
					{
						for (Node s = p.getFirstChild(); s != null; s = s.getNextSibling())
						{
							if (s.getNodeName().equals("stat"))
							{
								final int level = Integer.parseInt(s.getAttributes().getNamedItem("level").getNodeValue());
								final StatsSet set = new StatsSet();
								for (Node bean = s.getFirstChild(); bean != null; bean = bean.getNextSibling())
								{
									if (bean.getNodeName().equals("set"))
									{
										attrs = bean.getAttributes();
										if (attrs.getNamedItem("name").getNodeValue().equals("speed_on_ride"))
										{
											set.set("walkSpeedOnRide", attrs.getNamedItem("walk").getNodeValue());
											set.set("runSpeedOnRide", attrs.getNamedItem("run").getNodeValue());
											set.set("slowSwimSpeedOnRide", attrs.getNamedItem("slowSwim").getNodeValue());
											set.set("fastSwimSpeedOnRide", attrs.getNamedItem("fastSwim").getNodeValue());
											if (attrs.getNamedItem("slowFly") != null)
											{
												set.set("slowFlySpeedOnRide", attrs.getNamedItem("slowFly").getNodeValue());
											}
											if (attrs.getNamedItem("fastFly") != null)
											{
												set.set("fastFlySpeedOnRide", attrs.getNamedItem("fastFly").getNodeValue());
											}
										}
										else
										{
											set.set(attrs.getNamedItem("name").getNodeValue(), attrs.getNamedItem("val").getNodeValue());
										}
									}
								}
								data.addNewStat(level, new PetLevelData(set));
							}
						}
					}
				}
				_pets.put(npcId, data);
			}
		}
	}
	
	/**
	 * @param itemId
	 * @return
	 */
	public PetData getPetDataByItemId(int itemId)
	{
		for (PetData data : _pets.values())
		{
			if (data.getItemId() == itemId)
			{
				return data;
			}
		}
		return null;
	}
	
	/**
	 * Gets the pet level data.
	 * @param petId the pet Id.
	 * @param petLevel the pet level.
	 * @return the pet's parameters for the given Id and level.
	 */
	public PetLevelData getPetLevelData(int petId, int petLevel)
	{
		final PetData pd = getPetData(petId);
		if (pd != null)
		{
			if (petLevel > pd.getMaxLevel())
			{
				return pd.getPetLevelData(pd.getMaxLevel());
			}
			return pd.getPetLevelData(petLevel);
		}
		return null;
	}
	
	/**
	 * Gets the pet data.
	 * @param petId the pet Id.
	 * @return the pet data
	 */
	public PetData getPetData(int petId)
	{
		if (!_pets.containsKey(petId))
		{
			LOGGER.info(getClass().getSimpleName() + ": Missing pet data for npcid: " + petId);
		}
		return _pets.get(petId);
	}
	
	/**
	 * Gets the pet min level.
	 * @param petId the pet Id.
	 * @return the pet min level
	 */
	public int getPetMinLevel(int petId)
	{
		return _pets.get(petId).getMinLevel();
	}
	
	/**
	 * Gets the pet items by npc.
	 * @param npcId the NPC ID to get its summoning item
	 * @return summoning item for the given NPC ID
	 */
	public int getPetItemsByNpc(int npcId)
	{
		return _pets.get(npcId).getItemId();
	}
	
	/**
	 * Checks if is mountable.
	 * @param npcId the NPC Id to verify.
	 * @return {@code true} if the given Id is from a mountable pet, {@code false} otherwise.
	 */
	public static boolean isMountable(int npcId)
	{
		return MountType.findByNpcId(npcId) != MountType.NONE;
	}
	
	/**
	 * Gets the single instance of PetDataTable.
	 * @return this class unique instance.
	 */
	public static PetDataTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PetDataTable INSTANCE = new PetDataTable();
	}
}