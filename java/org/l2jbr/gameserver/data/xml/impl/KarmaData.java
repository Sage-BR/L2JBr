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

/**
 * @author UnAfraid
 */
public class KarmaData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(KarmaData.class.getName());
	
	private final Map<Integer, Double> _karmaTable = new HashMap<>();
	
	public KarmaData()
	{
		load();
	}
	
	@Override
	public synchronized void load()
	{
		_karmaTable.clear();
		parseDatapackFile("data/stats/chars/pcKarmaIncrease.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _karmaTable.size() + " karma modifiers.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("pcKarmaIncrease".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("increase".equalsIgnoreCase(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						final int level = parseInteger(attrs, "lvl");
						if (level >= Config.PLAYER_MAXIMUM_LEVEL)
						{
							break;
						}
						_karmaTable.put(level, parseDouble(attrs, "val"));
					}
				}
			}
		}
	}
	
	/**
	 * @param level
	 * @return {@code double} modifier used to calculate karma lost upon death.
	 */
	public double getMultiplier(int level)
	{
		return _karmaTable.get(level);
	}
	
	/**
	 * Gets the single instance of KarmaData.
	 * @return single instance of KarmaData
	 */
	public static KarmaData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final KarmaData INSTANCE = new KarmaData();
	}
}
