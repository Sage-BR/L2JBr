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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.holders.ClanMasteryHolder;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * @author Mobius
 */
public class ClanMasteryData implements IXmlReader
{
	private static Logger LOGGER = Logger.getLogger(ClanMasteryData.class.getName());
	
	private final Map<Integer, ClanMasteryHolder> _clanMasteryData = new HashMap<>();
	
	protected ClanMasteryData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_clanMasteryData.clear();
		
		parseDatapackFile("data/ClanMasteryData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _clanMasteryData.size() + " clan masteries.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "clan", clanNode ->
		{
			final StatsSet set = new StatsSet(parseAttributes(clanNode));
			final int id = set.getInt("mastery");
			final int skill1Id = set.getInt("skill1Id");
			final int skill1Level = set.getInt("skill1Level");
			final Skill skill1 = SkillData.getInstance().getSkill(skill1Id, skill1Level);
			if (skill1 == null)
			{
				LOGGER.info(getClass().getSimpleName() + ": Could not create clan mastery, skill id " + skill1Id + " with level " + skill1Level + " does not exist.");
				return;
			}
			final int skill2Id = set.getInt("skill2Id", 0);
			final int skill2Level = set.getInt("skill2Level", 0);
			Skill skill2 = null;
			if (skill2Id > 0)
			{
				skill2 = SkillData.getInstance().getSkill(skill2Id, skill2Level);
				if (skill2 == null)
				{
					LOGGER.info(getClass().getSimpleName() + ": Could not create clan mastery, skill id " + skill2Id + " with level " + skill2Level + " does not exist.");
					return;
				}
			}
			final int skill3Id = set.getInt("skill3Id", 0);
			final int skill3Level = set.getInt("skill3Level", 0);
			Skill skill3 = null;
			if (skill3Id > 0)
			{
				skill3 = SkillData.getInstance().getSkill(skill3Id, skill3Level);
				if (skill3 == null)
				{
					LOGGER.info(getClass().getSimpleName() + ": Could not create clan mastery, skill id " + skill3Id + " with level " + skill3Level + " does not exist.");
					return;
				}
			}
			final int skill4Id = set.getInt("skill4Id", 0);
			final int skill4Level = set.getInt("skill4Level", 0);
			Skill skill4 = null;
			if (skill4Id > 0)
			{
				skill4 = SkillData.getInstance().getSkill(skill4Id, skill4Level);
				if (skill4 == null)
				{
					LOGGER.info(getClass().getSimpleName() + ": Could not create clan mastery, skill id " + skill4Id + " with level " + skill4Level + " does not exist.");
					return;
				}
			}
			final int clanLevel = set.getInt("clanLevel");
			final int clanReputation = set.getInt("clanReputation");
			final int previousMastery = set.getInt("previousMastery", 0);
			final int previousMasteryAlt = set.getInt("previousMasteryAlt", 0);
			
			_clanMasteryData.put(id, new ClanMasteryHolder(id, skill1, skill2, skill3, skill4, clanLevel, clanReputation, previousMastery, previousMasteryAlt));
		}));
	}
	
	public Collection<ClanMasteryHolder> getMasteries()
	{
		return _clanMasteryData.values();
	}
	
	public ClanMasteryHolder getClanMastery(int id)
	{
		return _clanMasteryData.get(id);
	}
	
	public static ClanMasteryData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanMasteryData INSTANCE = new ClanMasteryData();
	}
}
