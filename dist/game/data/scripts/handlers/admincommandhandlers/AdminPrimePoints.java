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
package handlers.admincommandhandlers;

import java.util.Collection;
import java.util.StringTokenizer;

import org.l2jbr.gameserver.cache.HtmCache;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jbr.gameserver.util.BuilderUtil;
import org.l2jbr.gameserver.util.Util;

/**
 * Admin Prime Points manage admin commands.
 * @author St3eT
 */
public class AdminPrimePoints implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_primepoints",
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		if (actualCommand.equals("admin_primepoints"))
		{
			if (st.hasMoreTokens())
			{
				final String action = st.nextToken();
				
				final PlayerInstance target = getTarget(activeChar);
				if ((target == null) || !st.hasMoreTokens())
				{
					return false;
				}
				
				int value = 0;
				try
				{
					value = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					showMenuHtml(activeChar);
					BuilderUtil.sendSysMessage(activeChar, "Invalid Value!");
					return false;
				}
				
				switch (action)
				{
					case "set":
					{
						target.setPrimePoints(value);
						target.sendMessage("Admin set your Prime Point(s) to " + value + "!");
						BuilderUtil.sendSysMessage(activeChar, "You set " + value + " Prime Point(s) to player " + target.getName());
						break;
					}
					case "increase":
					{
						if (target.getPrimePoints() == Integer.MAX_VALUE)
						{
							showMenuHtml(activeChar);
							activeChar.sendMessage(target.getName() + " already have max count of Prime Points!");
							return false;
						}
						
						int primeCount = Math.min((target.getPrimePoints() + value), Integer.MAX_VALUE);
						if (primeCount < 0)
						{
							primeCount = Integer.MAX_VALUE;
						}
						target.setPrimePoints(primeCount);
						target.sendMessage("Admin increase your Prime Point(s) by " + value + "!");
						BuilderUtil.sendSysMessage(activeChar, "You increased Prime Point(s) of " + target.getName() + " by " + value);
						break;
					}
					case "decrease":
					{
						if (target.getPrimePoints() == 0)
						{
							showMenuHtml(activeChar);
							activeChar.sendMessage(target.getName() + " already have min count of Prime Points!");
							return false;
						}
						
						final int primeCount = Math.max(target.getPrimePoints() - value, 0);
						target.setPrimePoints(primeCount);
						target.sendMessage("Admin decreased your Prime Point(s) by " + value + "!");
						BuilderUtil.sendSysMessage(activeChar, "You decreased Prime Point(s) of " + target.getName() + " by " + value);
						break;
					}
					case "rewardOnline":
					{
						int range = 0;
						try
						{
							range = Integer.parseInt(st.nextToken());
						}
						catch (Exception e)
						{
						}
						
						if (range <= 0)
						{
							final int count = increaseForAll(World.getInstance().getPlayers(), value);
							BuilderUtil.sendSysMessage(activeChar, "You increased Prime Point(s) of all online players (" + count + ") by " + value + ".");
						}
						else if (range > 0)
						{
							final int count = increaseForAll(World.getInstance().getVisibleObjectsInRange(activeChar, PlayerInstance.class, range), value);
							BuilderUtil.sendSysMessage(activeChar, "You increased Prime Point(s) of all players (" + count + ") in range " + range + " by " + value + ".");
						}
						break;
					}
				}
			}
			showMenuHtml(activeChar);
		}
		return true;
	}
	
	private int increaseForAll(Collection<PlayerInstance> playerList, int value)
	{
		int counter = 0;
		for (PlayerInstance temp : playerList)
		{
			if ((temp != null) && (temp.isOnlineInt() == 1))
			{
				if (temp.getPrimePoints() == Integer.MAX_VALUE)
				{
					continue;
				}
				
				int primeCount = Math.min((temp.getPrimePoints() + value), Integer.MAX_VALUE);
				if (primeCount < 0)
				{
					primeCount = Integer.MAX_VALUE;
				}
				temp.setPrimePoints(primeCount);
				temp.sendMessage("Admin increase your Prime Point(s) by " + value + "!");
				counter++;
			}
		}
		return counter;
	}
	
	private PlayerInstance getTarget(PlayerInstance activeChar)
	{
		return ((activeChar.getTarget() != null) && (activeChar.getTarget().getActingPlayer() != null)) ? activeChar.getTarget().getActingPlayer() : activeChar;
	}
	
	private void showMenuHtml(PlayerInstance activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		final PlayerInstance target = getTarget(activeChar);
		final int points = target.getPrimePoints();
		html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/primepoints.htm"));
		html.replace("%points%", Util.formatAdena(points));
		html.replace("%targetName%", target.getName());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}