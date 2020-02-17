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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.handler.EffectHandler;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.options.Options;
import org.l2jbr.gameserver.model.options.OptionsSkillHolder;
import org.l2jbr.gameserver.model.options.OptionsSkillType;

/**
 * @author UnAfraid
 */
public class OptionData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(OptionData.class.getName());
	
	private final Map<Integer, Options> _optionData = new ConcurrentHashMap<>();
	
	protected OptionData()
	{
		load();
	}
	
	@Override
	public synchronized void load()
	{
		_optionData.clear();
		parseDatapackDirectory("data/stats/augmentation/options", false);
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _optionData.size() + " options.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "option", optionNode ->
		{
			final int id = parseInteger(optionNode.getAttributes(), "id");
			final Options option = new Options(id);
			
			forEach(optionNode, IXmlReader::isNode, innerNode ->
			{
				switch (innerNode.getNodeName())
				{
					case "effects":
					{
						forEach(innerNode, "effect", effectNode ->
						{
							final String name = parseString(effectNode.getAttributes(), "name");
							final StatsSet params = new StatsSet();
							forEach(effectNode, IXmlReader::isNode, paramNode ->
							{
								params.set(paramNode.getNodeName(), SkillData.getInstance().parseValue(paramNode, true, false, Collections.emptyMap()));
							});
							option.addEffect(EffectHandler.getInstance().getHandlerFactory(name).apply(params));
						});
						break;
					}
					case "active_skill":
					{
						option.addActiveSkill(new SkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level")));
						break;
					}
					case "passive_skill":
					{
						option.addPassiveSkill(new SkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level")));
						break;
					}
					case "attack_skill":
					{
						option.addActivationSkill(new OptionsSkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level"), parseDouble(innerNode.getAttributes(), "chance"), OptionsSkillType.ATTACK));
						break;
					}
					case "magic_skill":
					{
						option.addActivationSkill(new OptionsSkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level"), parseDouble(innerNode.getAttributes(), "chance"), OptionsSkillType.MAGIC));
						break;
					}
					case "critical_skill":
					{
						option.addActivationSkill(new OptionsSkillHolder(parseInteger(innerNode.getAttributes(), "id"), parseInteger(innerNode.getAttributes(), "level"), parseDouble(innerNode.getAttributes(), "chance"), OptionsSkillType.CRITICAL));
						break;
					}
				}
			});
			_optionData.put(option.getId(), option);
		}));
	}
	
	public Options getOptions(int id)
	{
		return _optionData.get(id);
	}
	
	/**
	 * Gets the single instance of OptionsData.
	 * @return single instance of OptionsData
	 */
	public static OptionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final OptionData INSTANCE = new OptionData();
	}
}
