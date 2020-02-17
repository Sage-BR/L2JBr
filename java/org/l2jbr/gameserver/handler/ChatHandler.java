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
package org.l2jbr.gameserver.handler;

import java.util.EnumMap;
import java.util.Map;

import org.l2jbr.gameserver.enums.ChatType;

/**
 * This class handles all chat handlers
 * @author durgus, UnAfraid
 */
public class ChatHandler implements IHandler<IChatHandler, ChatType>
{
	private final Map<ChatType, IChatHandler> _datatable = new EnumMap<>(ChatType.class);
	
	/**
	 * Singleton constructor
	 */
	protected ChatHandler()
	{
	}
	
	/**
	 * Register a new chat handler
	 * @param handler
	 */
	@Override
	public void registerHandler(IChatHandler handler)
	{
		for (ChatType type : handler.getChatTypeList())
		{
			_datatable.put(type, handler);
		}
	}
	
	@Override
	public synchronized void removeHandler(IChatHandler handler)
	{
		for (ChatType type : handler.getChatTypeList())
		{
			_datatable.remove(type);
		}
	}
	
	/**
	 * Get the chat handler for the given chat type
	 * @param chatType
	 * @return
	 */
	@Override
	public IChatHandler getHandler(ChatType chatType)
	{
		return _datatable.get(chatType);
	}
	
	/**
	 * Returns the size
	 * @return
	 */
	@Override
	public int size()
	{
		return _datatable.size();
	}
	
	public static ChatHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ChatHandler INSTANCE = new ChatHandler();
	}
}