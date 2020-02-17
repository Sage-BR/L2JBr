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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.holders.EquipmentUpgradeHolder;
import org.l2jbr.gameserver.model.holders.ItemHolder;

/**
 * @author Mobius
 */
public class EquipmentUpgradeData implements IXmlReader
{
	private static Logger LOGGER = Logger.getLogger(EquipmentUpgradeData.class.getName());
	private static final Map<Integer, EquipmentUpgradeHolder> _upgrades = new HashMap<>();
	
	protected EquipmentUpgradeData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_upgrades.clear();
		parseDatapackFile("data/EquipmentUpgradeData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _upgrades.size() + " upgrade equipment data.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "upgrade", upgradeNode ->
		{
			final StatsSet set = new StatsSet(parseAttributes(upgradeNode));
			final int id = set.getInt("id");
			final String[] item = set.getString("item").split(",");
			final int requiredItemId = Integer.parseInt(item[0]);
			final int requiredItemEnchant = Integer.parseInt(item[1]);
			final String materials = set.getString("materials");
			final List<ItemHolder> materialList = new ArrayList<>();
			if (!materials.isEmpty())
			{
				for (String mat : materials.split(";"))
				{
					final String[] matValues = mat.split(",");
					final int matItemId = Integer.parseInt(matValues[0]);
					if (ItemTable.getInstance().getTemplate(matItemId) == null)
					{
						LOGGER.info(getClass().getSimpleName() + ": Material item with id " + matItemId + " does not exist.");
					}
					else
					{
						materialList.add(new ItemHolder(matItemId, Long.parseLong(matValues[1])));
					}
				}
			}
			final long adena = set.getLong("adena", 0);
			final String[] resultItem = set.getString("result").split(",");
			final int resultItemId = Integer.parseInt(resultItem[0]);
			final int resultItemEnchant = Integer.parseInt(resultItem[1]);
			if (ItemTable.getInstance().getTemplate(requiredItemId) == null)
			{
				LOGGER.info(getClass().getSimpleName() + ": Required item with id " + requiredItemId + " does not exist.");
			}
			else
			{
				_upgrades.put(id, new EquipmentUpgradeHolder(id, requiredItemId, requiredItemEnchant, materialList, adena, resultItemId, resultItemEnchant));
			}
		}));
	}
	
	public EquipmentUpgradeHolder getUpgrade(int id)
	{
		return _upgrades.get(id);
	}
	
	public static EquipmentUpgradeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final EquipmentUpgradeData INSTANCE = new EquipmentUpgradeData();
	}
}
