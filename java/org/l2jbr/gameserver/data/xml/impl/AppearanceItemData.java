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
import org.w3c.dom.Node;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.holders.AppearanceHolder;
import org.l2jbr.gameserver.model.items.appearance.AppearanceStone;
import org.l2jbr.gameserver.model.items.appearance.AppearanceTargetType;
import org.l2jbr.gameserver.model.items.type.CrystalType;

/**
 * @author UnAfraid
 */
public class AppearanceItemData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(AppearanceItemData.class.getName());
	
	private final Map<Integer, AppearanceStone> _stones = new HashMap<>();
	
	protected AppearanceItemData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("data/AppearanceStones.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _stones.size() + " stones.");
		
		//@formatter:off
		/*
		for (Item item : ItemTable.getInstance().getAllItems())
		{
			if ((item == null) || !item.getName().contains("Appearance Stone"))
			{
				continue;
			}
			if (item.getName().contains("Pack") || _stones.containsKey(item.getId()))
			{
				continue;
			}
			
			System.out.println("Unhandled appearance stone: " + item);
		}
		*/
		//@formatter:on
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("appearance_stone".equalsIgnoreCase(d.getNodeName()))
					{
						final AppearanceStone stone = new AppearanceStone(new StatsSet(parseAttributes(d)));
						for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
						{
							switch (c.getNodeName())
							{
								case "grade":
								{
									final CrystalType type = CrystalType.valueOf(c.getTextContent());
									stone.addCrystalType(type);
									break;
								}
								case "targetType":
								{
									final AppearanceTargetType type = AppearanceTargetType.valueOf(c.getTextContent());
									stone.addTargetType(type);
									break;
								}
								case "bodyPart":
								{
									final long part = ItemTable.SLOTS.get(c.getTextContent());
									stone.addBodyPart(part);
									break;
								}
								case "race":
								{
									final Race race = Race.valueOf(c.getTextContent());
									stone.addRace(race);
									break;
								}
								case "raceNot":
								{
									final Race raceNot = Race.valueOf(c.getTextContent());
									stone.addRaceNot(raceNot);
									break;
								}
								case "visual":
								{
									stone.addVisualId(new AppearanceHolder(new StatsSet(parseAttributes(c))));
								}
							}
						}
						if (ItemTable.getInstance().getTemplate(stone.getId()) != null)
						{
							_stones.put(stone.getId(), stone);
						}
						else
						{
							LOGGER.info(getClass().getSimpleName() + ": Could not find appearance stone item " + stone.getId());
						}
					}
				}
			}
		}
	}
	
	public int getLoadedElementsCount()
	{
		return _stones.size();
	}
	
	public AppearanceStone getStone(int stone)
	{
		return _stones.get(stone);
	}
	
	/**
	 * Gets the single instance of AppearanceItemData.
	 * @return single instance of AppearanceItemData
	 */
	public static AppearanceItemData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AppearanceItemData INSTANCE = new AppearanceItemData();
	}
}
