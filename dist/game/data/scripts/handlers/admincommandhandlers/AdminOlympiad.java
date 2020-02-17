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

import java.util.StringTokenizer;

import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.olympiad.Olympiad;
import org.l2jbr.gameserver.model.olympiad.OlympiadGameManager;
import org.l2jbr.gameserver.model.olympiad.OlympiadGameNonClassed;
import org.l2jbr.gameserver.model.olympiad.OlympiadGameTask;
import org.l2jbr.gameserver.model.olympiad.OlympiadManager;
import org.l2jbr.gameserver.model.olympiad.Participant;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.util.BuilderUtil;
import org.l2jbr.gameserver.util.Util;

/**
 * @author UnAfraid
 */
public class AdminOlympiad implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_olympiad_game",
		"admin_addolypoints",
		"admin_removeolypoints",
		"admin_setolypoints",
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_olympiad_game":
			{
				if (!st.hasMoreTokens())
				{
					BuilderUtil.sendSysMessage(activeChar, "Syntax: //olympiad_game <player name>");
					return false;
				}
				
				final PlayerInstance player = World.getInstance().getPlayer(st.nextToken());
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
					return false;
				}
				
				if (player == activeChar)
				{
					activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
					return false;
				}
				
				if (!checkplayer(player, activeChar) || !checkplayer(activeChar, activeChar))
				{
					return false;
				}
				
				for (int i = 0; i < OlympiadGameManager.getInstance().getNumberOfStadiums(); i++)
				{
					final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(i);
					if (task != null)
					{
						synchronized (task)
						{
							if (!task.isRunning())
							{
								final Participant[] players = new Participant[2];
								players[0] = new Participant(activeChar, 1);
								players[1] = new Participant(player, 2);
								task.attachGame(new OlympiadGameNonClassed(i, players));
								return true;
							}
						}
					}
				}
				break;
			}
			case "admin_addolypoints":
			{
				final WorldObject target = activeChar.getTarget();
				final PlayerInstance player = target != null ? target.getActingPlayer() : null;
				if (player != null)
				{
					final int val = parseInt(st, Integer.MIN_VALUE);
					if (val == Integer.MIN_VALUE)
					{
						BuilderUtil.sendSysMessage(activeChar, "Syntax: //addolypoints <points>");
						return false;
					}
					
					if (player.getNobleLevel() > 0)
					{
						final StatsSet statDat = getPlayerSet(player);
						final int oldpoints = Olympiad.getInstance().getNoblePoints(player);
						final int points = Math.max(oldpoints + val, 0);
						if (points > 1000)
						{
							BuilderUtil.sendSysMessage(activeChar, "You can't set more than 1000 or less than 0 Olympiad points!");
							return false;
						}
						
						statDat.set(Olympiad.POINTS, points);
						BuilderUtil.sendSysMessage(activeChar, "Player " + player.getName() + " now has " + points + " Olympiad points.");
					}
					else
					{
						BuilderUtil.sendSysMessage(activeChar, "This player is not noblesse!");
						return false;
					}
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: target a player and write the amount of points you would like to add.");
					BuilderUtil.sendSysMessage(activeChar, "Example: //addolypoints 10");
					BuilderUtil.sendSysMessage(activeChar, "However, keep in mind that you can't have less than 0 or more than 1000 points.");
				}
				break;
			}
			case "admin_removeolypoints":
			{
				final WorldObject target = activeChar.getTarget();
				final PlayerInstance player = target != null ? target.getActingPlayer() : null;
				if (player != null)
				{
					final int val = parseInt(st, Integer.MIN_VALUE);
					if (val == Integer.MIN_VALUE)
					{
						BuilderUtil.sendSysMessage(activeChar, "Syntax: //removeolypoints <points>");
						return false;
					}
					
					if (player.getNobleLevel() > 0)
					{
						final StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
						if (playerStat == null)
						{
							BuilderUtil.sendSysMessage(activeChar, "This player hasn't played on Olympiad yet!");
							return false;
						}
						
						final int oldpoints = Olympiad.getInstance().getNoblePoints(player);
						final int points = Math.max(oldpoints - val, 0);
						playerStat.set(Olympiad.POINTS, points);
						
						BuilderUtil.sendSysMessage(activeChar, "Player " + player.getName() + " now has " + points + " Olympiad points.");
					}
					else
					{
						BuilderUtil.sendSysMessage(activeChar, "This player is not noblesse!");
						return false;
					}
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: target a player and write the amount of points you would like to remove.");
					BuilderUtil.sendSysMessage(activeChar, "Example: //removeolypoints 10");
					BuilderUtil.sendSysMessage(activeChar, "However, keep in mind that you can't have less than 0 or more than 1000 points.");
				}
				break;
			}
			case "admin_setolypoints":
			{
				final WorldObject target = activeChar.getTarget();
				final PlayerInstance player = target != null ? target.getActingPlayer() : null;
				if (player != null)
				{
					final int val = parseInt(st, Integer.MIN_VALUE);
					if (val == Integer.MIN_VALUE)
					{
						BuilderUtil.sendSysMessage(activeChar, "Syntax: //setolypoints <points>");
						return false;
					}
					
					if (player.getNobleLevel() > 0)
					{
						final StatsSet statDat = getPlayerSet(player);
						final int oldpoints = Olympiad.getInstance().getNoblePoints(player);
						final int points = oldpoints - val;
						if ((points < 1) && (points > 1000))
						{
							BuilderUtil.sendSysMessage(activeChar, "You can't set more than 1000 or less than 0 Olympiad points! or lower then 0");
							return false;
						}
						
						statDat.set(Olympiad.POINTS, points);
						BuilderUtil.sendSysMessage(activeChar, "Player " + player.getName() + " now has " + points + " Olympiad points.");
					}
					else
					{
						BuilderUtil.sendSysMessage(activeChar, "This player is not noblesse!");
						return false;
					}
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: target a player and write the amount of points you would like to set.");
					BuilderUtil.sendSysMessage(activeChar, "Example: //setolypoints 10");
					BuilderUtil.sendSysMessage(activeChar, "However, keep in mind that you can't have less than 0 or more than 1000 points.");
				}
				break;
			}
		}
		return false;
	}
	
	private int parseInt(StringTokenizer st, int defaultVal)
	{
		final String token = st.nextToken();
		if (!Util.isDigit(token))
		{
			return -1;
		}
		return Integer.decode(token);
	}
	
	private StatsSet getPlayerSet(PlayerInstance player)
	{
		StatsSet statDat = Olympiad.getNobleStats(player.getObjectId());
		if (statDat == null)
		{
			statDat = new StatsSet();
			statDat.set(Olympiad.CLASS_ID, player.getBaseClass());
			statDat.set(Olympiad.CHAR_NAME, player.getName());
			statDat.set(Olympiad.POINTS, Olympiad.DEFAULT_POINTS);
			statDat.set(Olympiad.COMP_DONE, 0);
			statDat.set(Olympiad.COMP_WON, 0);
			statDat.set(Olympiad.COMP_LOST, 0);
			statDat.set(Olympiad.COMP_DRAWN, 0);
			statDat.set(Olympiad.COMP_DONE_WEEK, 0);
			statDat.set("to_save", true);
			Olympiad.addNobleStats(player.getObjectId(), statDat);
		}
		return statDat;
	}
	
	private boolean checkplayer(PlayerInstance player, PlayerInstance activeChar)
	{
		if (player.isSubClassActive())
		{
			BuilderUtil.sendSysMessage(activeChar, "Player " + player + " subclass active.");
			return false;
		}
		else if (player.getClassId().level() < 3)
		{
			BuilderUtil.sendSysMessage(activeChar, "Player " + player + " has not 3rd class.");
			return false;
		}
		else if (Olympiad.getInstance().getNoblePoints(player) <= 0)
		{
			BuilderUtil.sendSysMessage(activeChar, "Player " + player + " has 0 oly points (add them with (//addolypoints).");
			return false;
		}
		else if (OlympiadManager.getInstance().isRegistered(player))
		{
			BuilderUtil.sendSysMessage(activeChar, "Player " + player + " registered to oly.");
			return false;
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
