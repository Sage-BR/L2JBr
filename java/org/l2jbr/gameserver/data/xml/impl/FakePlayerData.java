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

import org.l2jbr.Config;
import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.data.sql.impl.CharNameTable;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.templates.NpcTemplate;
import org.l2jbr.gameserver.model.holders.FakePlayerHolder;

/**
 * @author Mobius
 */
public class FakePlayerData implements IXmlReader
{
	private static Logger LOGGER = Logger.getLogger(FakePlayerData.class.getName());
	
	private final Map<Integer, FakePlayerHolder> _fakePlayerInfos = new HashMap<>();
	private final Map<String, String> _fakePlayerNames = new HashMap<>();
	private final Map<String, Integer> _fakePlayerIds = new HashMap<>();
	private final List<String> _talkableFakePlayerNames = new ArrayList<>();
	
	protected FakePlayerData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		if (Config.FAKE_PLAYERS_ENABLED)
		{
			_fakePlayerInfos.clear();
			_fakePlayerNames.clear();
			_fakePlayerIds.clear();
			_talkableFakePlayerNames.clear();
			parseDatapackFile("data/FakePlayerVisualData.xml");
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + _fakePlayerInfos.size() + " templates.");
		}
		else
		{
			LOGGER.info(getClass().getSimpleName() + ": Disabled.");
		}
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "fakePlayer", fakePlayerNode ->
		{
			final StatsSet set = new StatsSet(parseAttributes(fakePlayerNode));
			final int npcId = set.getInt("npcId");
			final NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
			final String name = template.getName();
			if (CharNameTable.getInstance().getIdByName(name) > 0)
			{
				LOGGER.info(getClass().getSimpleName() + ": Could not create fake player template " + npcId + ", player name already exists.");
			}
			else
			{
				_fakePlayerIds.put(name, npcId); // name - npcId
				_fakePlayerNames.put(name.toLowerCase(), name); // name to lowercase - name
				_fakePlayerInfos.put(npcId, new FakePlayerHolder(set));
				if (template.isFakePlayerTalkable())
				{
					_talkableFakePlayerNames.add(name.toLowerCase());
				}
			}
		}));
	}
	
	public int getNpcIdByName(String name)
	{
		return _fakePlayerIds.get(name);
	}
	
	public String getProperName(String name)
	{
		return _fakePlayerNames.get(name.toLowerCase());
	}
	
	public Boolean isTalkable(String name)
	{
		return _talkableFakePlayerNames.contains(name.toLowerCase());
	}
	
	public FakePlayerHolder getInfo(int npcId)
	{
		return _fakePlayerInfos.get(npcId);
	}
	
	public static FakePlayerData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FakePlayerData INSTANCE = new FakePlayerData();
	}
}
