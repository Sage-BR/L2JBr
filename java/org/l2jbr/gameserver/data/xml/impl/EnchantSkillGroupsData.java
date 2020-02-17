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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.enums.SkillEnchantType;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.holders.EnchantSkillHolder;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * This class holds the Enchant Groups information.
 * @author Micr0
 */
public class EnchantSkillGroupsData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(EnchantSkillGroupsData.class.getName());
	
	private final Map<Integer, EnchantSkillHolder> _enchantSkillHolders = new ConcurrentHashMap<>();
	private final Map<SkillHolder, Set<Integer>> _enchantSkillTrees = new ConcurrentHashMap<>();
	
	public static int MAX_ENCHANT_LEVEL;
	
	/**
	 * Instantiates a new enchant groups table.
	 */
	protected EnchantSkillGroupsData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_enchantSkillHolders.clear();
		parseDatapackFile("data/EnchantSkillGroups.xml");
		MAX_ENCHANT_LEVEL = _enchantSkillHolders.size();
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _enchantSkillHolders.size() + " enchant routes, max enchant set to " + MAX_ENCHANT_LEVEL + ".");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "enchant", enchantNode ->
		{
			final EnchantSkillHolder enchantSkillHolder = new EnchantSkillHolder(new StatsSet(parseAttributes(enchantNode)));
			
			forEach(enchantNode, "sps", spsNode -> forEach(spsNode, "sp", spNode ->
			{
				enchantSkillHolder.addSp(parseEnum(spNode.getAttributes(), SkillEnchantType.class, "type"), parseInteger(spNode.getAttributes(), "amount"));
			}));
			
			forEach(enchantNode, "chances", chancesNode -> forEach(chancesNode, "chance", chanceNode ->
			{
				enchantSkillHolder.addChance(parseEnum(chanceNode.getAttributes(), SkillEnchantType.class, "type"), parseInteger(chanceNode.getAttributes(), "value"));
			}));
			
			forEach(enchantNode, "items", itemsNode -> forEach(itemsNode, "item", itemNode ->
			{
				enchantSkillHolder.addRequiredItem(parseEnum(itemNode.getAttributes(), SkillEnchantType.class, "type"), new ItemHolder(new StatsSet(parseAttributes(itemNode))));
			}));
			
			_enchantSkillHolders.put(parseInteger(enchantNode.getAttributes(), "level"), enchantSkillHolder);
		}));
	}
	
	public void addRouteForSkill(int skillId, int level, int route)
	{
		addRouteForSkill(new SkillHolder(skillId, level), route);
	}
	
	public void addRouteForSkill(SkillHolder holder, int route)
	{
		_enchantSkillTrees.computeIfAbsent(holder, k -> new HashSet<>()).add(route);
	}
	
	public Set<Integer> getRouteForSkill(int skillId, int level)
	{
		return getRouteForSkill(skillId, level, 0);
	}
	
	public Set<Integer> getRouteForSkill(int skillId, int level, int subLevel)
	{
		return getRouteForSkill(new SkillHolder(skillId, level, subLevel));
	}
	
	public Set<Integer> getRouteForSkill(SkillHolder holder)
	{
		return _enchantSkillTrees.getOrDefault(holder, Collections.emptySet());
	}
	
	public boolean isEnchantable(Skill skill)
	{
		return isEnchantable(new SkillHolder(skill.getId(), skill.getLevel()));
	}
	
	public boolean isEnchantable(SkillHolder holder)
	{
		return _enchantSkillTrees.containsKey(holder);
	}
	
	public EnchantSkillHolder getEnchantSkillHolder(int level)
	{
		return _enchantSkillHolders.getOrDefault(level, null);
	}
	
	public static EnchantSkillGroupsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final EnchantSkillGroupsData INSTANCE = new EnchantSkillGroupsData();
	}
}