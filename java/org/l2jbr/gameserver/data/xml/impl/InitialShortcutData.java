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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.enums.MacroType;
import org.l2jbr.gameserver.enums.ShortcutType;
import org.l2jbr.gameserver.model.Macro;
import org.l2jbr.gameserver.model.MacroCmd;
import org.l2jbr.gameserver.model.Shortcut;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.serverpackets.ShortCutRegister;

/**
 * This class holds the Initial Shortcuts information.<br>
 * What shortcuts get each newly created character.
 * @author Zoey76
 */
public class InitialShortcutData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(InitialShortcutData.class.getName());
	
	private final Map<ClassId, List<Shortcut>> _initialShortcutData = new HashMap<>();
	private final List<Shortcut> _initialGlobalShortcutList = new ArrayList<>();
	private final Map<Integer, Macro> _macroPresets = new HashMap<>();
	
	/**
	 * Instantiates a new initial shortcuts data.
	 */
	protected InitialShortcutData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_initialShortcutData.clear();
		_initialGlobalShortcutList.clear();
		
		parseDatapackFile("data/stats/initialShortcuts.xml");
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _initialGlobalShortcutList.size() + " initial global shortcuts data.");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _initialShortcutData.size() + " initial shortcuts data.");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _macroPresets.size() + " macro presets.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equals(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					switch (d.getNodeName())
					{
						case "shortcuts":
						{
							parseShortcuts(d);
							break;
						}
						case "macros":
						{
							parseMacros(d);
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Parses a shortcut.
	 * @param d the node
	 */
	private void parseShortcuts(Node d)
	{
		NamedNodeMap attrs = d.getAttributes();
		final Node classIdNode = attrs.getNamedItem("classId");
		final List<Shortcut> list = new ArrayList<>();
		for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
		{
			if ("page".equals(c.getNodeName()))
			{
				attrs = c.getAttributes();
				final int pageId = parseInteger(attrs, "pageId");
				for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
				{
					if ("slot".equals(b.getNodeName()))
					{
						list.add(createShortcut(pageId, b));
					}
				}
			}
		}
		
		if (classIdNode != null)
		{
			_initialShortcutData.put(ClassId.getClassId(Integer.parseInt(classIdNode.getNodeValue())), list);
		}
		else
		{
			_initialGlobalShortcutList.addAll(list);
		}
	}
	
	/**
	 * Parses a macro.
	 * @param d the node
	 */
	private void parseMacros(Node d)
	{
		for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
		{
			if ("macro".equals(c.getNodeName()))
			{
				NamedNodeMap attrs = c.getAttributes();
				if (!parseBoolean(attrs, "enabled", true))
				{
					continue;
				}
				
				final int macroId = parseInteger(attrs, "macroId");
				final int icon = parseInteger(attrs, "icon");
				final String name = parseString(attrs, "name");
				final String description = parseString(attrs, "description");
				final String acronym = parseString(attrs, "acronym");
				final List<MacroCmd> commands = new ArrayList<>(1);
				int entry = 0;
				for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
				{
					if ("command".equals(b.getNodeName()))
					{
						attrs = b.getAttributes();
						final MacroType type = parseEnum(attrs, MacroType.class, "type");
						int d1 = 0;
						int d2 = 0;
						final String cmd = b.getTextContent();
						switch (type)
						{
							case SKILL:
							{
								d1 = parseInteger(attrs, "skillId"); // Skill ID
								d2 = parseInteger(attrs, "skillLvl", 0); // Skill level
								break;
							}
							case ACTION:
							{
								// Not handled by client.
								d1 = parseInteger(attrs, "actionId");
								break;
							}
							case TEXT:
							{
								// Doesn't have numeric parameters.
								break;
							}
							case SHORTCUT:
							{
								d1 = parseInteger(attrs, "page"); // Page
								d2 = parseInteger(attrs, "slot", 0); // Slot
								break;
							}
							case ITEM:
							{
								// Not handled by client.
								d1 = parseInteger(attrs, "itemId");
								break;
							}
							case DELAY:
							{
								d1 = parseInteger(attrs, "delay"); // Delay in seconds
								break;
							}
						}
						commands.add(new MacroCmd(entry++, type, d1, d2, cmd));
					}
				}
				_macroPresets.put(macroId, new Macro(macroId, icon, name, description, acronym, commands));
			}
		}
	}
	
	/**
	 * Parses a node an create a shortcut from it.
	 * @param pageId the page ID
	 * @param b the node to parse
	 * @return the new shortcut
	 */
	private Shortcut createShortcut(int pageId, Node b)
	{
		final NamedNodeMap attrs = b.getAttributes();
		final int slotId = parseInteger(attrs, "slotId");
		final ShortcutType shortcutType = parseEnum(attrs, ShortcutType.class, "shortcutType");
		final int shortcutId = parseInteger(attrs, "shortcutId");
		final int shortcutLevel = parseInteger(attrs, "shortcutLevel", 0);
		final int characterType = parseInteger(attrs, "characterType", 0);
		return new Shortcut(slotId, pageId, shortcutType, shortcutId, shortcutLevel, 0, characterType);
	}
	
	/**
	 * Gets the shortcut list.
	 * @param cId the class ID for the shortcut list
	 * @return the shortcut list for the give class ID
	 */
	public List<Shortcut> getShortcutList(ClassId cId)
	{
		return _initialShortcutData.get(cId);
	}
	
	/**
	 * Gets the shortcut list.
	 * @param cId the class ID for the shortcut list
	 * @return the shortcut list for the give class ID
	 */
	public List<Shortcut> getShortcutList(int cId)
	{
		return _initialShortcutData.get(ClassId.getClassId(cId));
	}
	
	/**
	 * Gets the global shortcut list.
	 * @return the global shortcut list
	 */
	public List<Shortcut> getGlobalMacroList()
	{
		return _initialGlobalShortcutList;
	}
	
	/**
	 * Register all the available shortcuts for the given player.
	 * @param player the player
	 */
	public void registerAllShortcuts(PlayerInstance player)
	{
		if (player == null)
		{
			return;
		}
		
		// Register global shortcuts.
		for (Shortcut shortcut : _initialGlobalShortcutList)
		{
			int shortcutId = shortcut.getId();
			switch (shortcut.getType())
			{
				case ITEM:
				{
					final ItemInstance item = player.getInventory().getItemByItemId(shortcutId);
					if (item == null)
					{
						continue;
					}
					shortcutId = item.getObjectId();
					break;
				}
				case SKILL:
				{
					if (!player.getSkills().containsKey(shortcutId))
					{
						continue;
					}
					break;
				}
				case MACRO:
				{
					final Macro macro = _macroPresets.get(shortcutId);
					if (macro == null)
					{
						continue;
					}
					player.registerMacro(macro);
					break;
				}
			}
			
			// Register shortcut
			final Shortcut newShortcut = new Shortcut(shortcut.getSlot(), shortcut.getPage(), shortcut.getType(), shortcutId, shortcut.getLevel(), shortcut.getSubLevel(), shortcut.getCharacterType());
			player.sendPacket(new ShortCutRegister(newShortcut));
			player.registerShortCut(newShortcut);
		}
		
		// Register class specific shortcuts.
		if (_initialShortcutData.containsKey(player.getClassId()))
		{
			for (Shortcut shortcut : _initialShortcutData.get(player.getClassId()))
			{
				int shortcutId = shortcut.getId();
				switch (shortcut.getType())
				{
					case ITEM:
					{
						final ItemInstance item = player.getInventory().getItemByItemId(shortcutId);
						if (item == null)
						{
							continue;
						}
						shortcutId = item.getObjectId();
						break;
					}
					case SKILL:
					{
						if (!player.getSkills().containsKey(shortcut.getId()))
						{
							continue;
						}
						break;
					}
					case MACRO:
					{
						final Macro macro = _macroPresets.get(shortcutId);
						if (macro == null)
						{
							continue;
						}
						player.registerMacro(macro);
						break;
					}
				}
				// Register shortcut
				final Shortcut newShortcut = new Shortcut(shortcut.getSlot(), shortcut.getPage(), shortcut.getType(), shortcutId, shortcut.getLevel(), shortcut.getSubLevel(), shortcut.getCharacterType());
				player.sendPacket(new ShortCutRegister(newShortcut));
				player.registerShortCut(newShortcut);
			}
		}
	}
	
	/**
	 * Gets the single instance of InitialEquipmentData.
	 * @return single instance of InitialEquipmentData
	 */
	public static InitialShortcutData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final InitialShortcutData INSTANCE = new InitialShortcutData();
	}
}