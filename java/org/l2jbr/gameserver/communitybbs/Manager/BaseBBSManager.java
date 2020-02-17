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
package org.l2jbr.gameserver.communitybbs.Manager;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.ShowBoard;

public abstract class BaseBBSManager
{
	public abstract void parsecmd(String command, PlayerInstance player);
	
	public abstract void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, PlayerInstance player);
	
	/**
	 * @param html
	 * @param acha
	 */
	protected void send1001(String html, PlayerInstance acha)
	{
		if (html.length() < 8192)
		{
			acha.sendPacket(new ShowBoard(html, "1001"));
		}
	}
	
	/**
	 * @param acha
	 */
	protected void send1002(PlayerInstance acha)
	{
		send1002(acha, " ", " ", "0");
	}
	
	/**
	 * @param player
	 * @param string
	 * @param string2
	 * @param string3
	 */
	protected void send1002(PlayerInstance player, String string, String string2, String string3)
	{
		final List<String> _arg = new ArrayList<>(20);
		_arg.add("0");
		_arg.add("0");
		_arg.add("0");
		_arg.add("0");
		_arg.add("0");
		_arg.add("0");
		_arg.add(player.getName());
		_arg.add(Integer.toString(player.getObjectId()));
		_arg.add(player.getAccountName());
		_arg.add("9");
		_arg.add(string2); // subject?
		_arg.add(string2); // subject?
		_arg.add(string); // text
		_arg.add(string3); // date?
		_arg.add(string3); // date?
		_arg.add("0");
		_arg.add("0");
		player.sendPacket(new ShowBoard(_arg));
	}
}
