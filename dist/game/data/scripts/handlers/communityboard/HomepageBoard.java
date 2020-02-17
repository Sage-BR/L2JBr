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
package handlers.communityboard;

import org.l2jbr.gameserver.cache.HtmCache;
import org.l2jbr.gameserver.handler.CommunityBoardHandler;
import org.l2jbr.gameserver.handler.IParseBoardHandler;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

/**
 * Homepage board.
 * @author Zoey76
 */
public class HomepageBoard implements IParseBoardHandler
{
	private static final String[] COMMANDS =
	{
		"_bbslink"
	};
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, PlayerInstance player)
	{
		CommunityBoardHandler.separateAndSend(HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/homepage.html"), player);
		return true;
	}
}
