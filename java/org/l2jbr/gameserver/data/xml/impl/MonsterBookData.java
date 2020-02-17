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
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.model.holders.MonsterBookCardHolder;
import org.l2jbr.gameserver.model.holders.MonsterBookRewardHolder;

/**
 * @author Mobius
 */
public class MonsterBookData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(MonsterBookData.class.getName());
	private final List<MonsterBookCardHolder> _monsterBook = new ArrayList<>();
	
	protected MonsterBookData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_monsterBook.clear();
		parseDatapackFile("data/MonsterBook.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _monsterBook.size() + " monster data.");
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
					if ("card".equalsIgnoreCase(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						
						final int itemId = parseInteger(attrs, "id");
						final int monster = parseInteger(attrs, "monster");
						final String faction = parseString(attrs, "faction");
						
						final MonsterBookCardHolder card = new MonsterBookCardHolder(itemId, monster, Faction.valueOf(faction));
						
						if (NpcData.getInstance().getTemplate(monster) == null)
						{
							LOGGER.severe(getClass().getSimpleName() + ": Could not find NPC template with id " + monster + ".");
						}
						
						for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling())
						{
							if ("rewards".equalsIgnoreCase(b.getNodeName()))
							{
								final NamedNodeMap rewardAttrs = b.getAttributes();
								
								final int kills = parseInteger(rewardAttrs, "kills");
								final Long exp = parseLong(rewardAttrs, "exp");
								final int sp = parseInteger(rewardAttrs, "sp");
								final int points = parseInteger(rewardAttrs, "points");
								
								card.addReward(new MonsterBookRewardHolder(kills, exp, sp, points));
							}
						}
						
						_monsterBook.add(card);
					}
				}
			}
		}
	}
	
	public List<MonsterBookCardHolder> getMonsterBookCards()
	{
		return _monsterBook;
	}
	
	public MonsterBookCardHolder getMonsterBookCardByMonsterId(int monsterId)
	{
		for (MonsterBookCardHolder card : _monsterBook)
		{
			if (card.getMonsterId() == monsterId)
			{
				return card;
			}
		}
		return null;
	}
	
	public MonsterBookCardHolder getMonsterBookCardById(int cardId)
	{
		for (MonsterBookCardHolder card : _monsterBook)
		{
			if (card.getId() == cardId)
			{
				return card;
			}
		}
		return null;
	}
	
	public static MonsterBookData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MonsterBookData INSTANCE = new MonsterBookData();
	}
}
