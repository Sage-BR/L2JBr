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

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.alchemy.AlchemyCraftData;
import org.l2jbr.gameserver.model.holders.ItemHolder;

/**
 * @author Sdw
 */
public class AlchemyData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(AlchemyData.class.getName());
	
	private final Map<Long, AlchemyCraftData> _alchemy = new HashMap<>();
	
	protected AlchemyData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_alchemy.clear();
		parseDatapackFile("data/AlchemyData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _alchemy.size() + " alchemy craft skills.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		StatsSet set;
		Node att;
		NamedNodeMap attrs;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("skill".equalsIgnoreCase(d.getNodeName()))
					{
						attrs = d.getAttributes();
						set = new StatsSet();
						for (int i = 0; i < attrs.getLength(); i++)
						{
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						final AlchemyCraftData alchemyCraft = new AlchemyCraftData(set);
						
						for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
						{
							if ("ingredients".equalsIgnoreCase(c.getNodeName()))
							{
								for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
								{
									if ("item".equalsIgnoreCase(b.getNodeName()))
									{
										final int ingId = Integer.parseInt(b.getAttributes().getNamedItem("id").getNodeValue());
										final int ingCount = Integer.parseInt(b.getAttributes().getNamedItem("count").getNodeValue());
										alchemyCraft.addIngredient(new ItemHolder(ingId, ingCount));
									}
								}
							}
							else if ("production".equalsIgnoreCase(c.getNodeName()))
							{
								for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
								{
									if ("item".equalsIgnoreCase(b.getNodeName()))
									{
										final String type = b.getAttributes().getNamedItem("type").getNodeValue();
										final int prodId = Integer.parseInt(b.getAttributes().getNamedItem("id").getNodeValue());
										final int prodCount = Integer.parseInt(b.getAttributes().getNamedItem("count").getNodeValue());
										
										if (type.equalsIgnoreCase("ON_SUCCESS"))
										{
											alchemyCraft.setProductionSuccess(new ItemHolder(prodId, prodCount));
										}
										else if (type.equalsIgnoreCase("ON_FAILURE"))
										{
											alchemyCraft.setProductionFailure(new ItemHolder(prodId, prodCount));
										}
									}
								}
							}
						}
						_alchemy.put(SkillData.getSkillHashCode(set.getInt("id"), set.getInt("level")), alchemyCraft);
					}
				}
			}
		}
	}
	
	public AlchemyCraftData getCraftData(int skillId, int skillLevel)
	{
		return _alchemy.get(SkillData.getSkillHashCode(skillId, skillLevel));
	}
	
	/**
	 * Gets the single instance of AlchemyData.
	 * @return single instance of AlchemyData
	 */
	public static AlchemyData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AlchemyData INSTANCE = new AlchemyData();
	}
}
