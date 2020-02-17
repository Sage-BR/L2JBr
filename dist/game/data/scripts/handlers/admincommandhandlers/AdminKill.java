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

import org.l2jbr.Config;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.ControllableMobInstance;
import org.l2jbr.gameserver.model.actor.instance.FriendlyNpcInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.util.BuilderUtil;

/**
 * This class handles following admin commands: - kill = kills target Creature - kill_monster = kills target non-player - kill <radius> = If radius is specified, then ALL players only in that radius will be killed. - kill_monster <radius> = If radius is specified, then ALL non-players only in
 * that radius will be killed.
 * @version $Revision: 1.2.4.5 $ $Date: 2007/07/31 10:06:06 $
 */
public class AdminKill implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_kill",
		"admin_kill_monster"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		if (command.startsWith("admin_kill"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (st.hasMoreTokens())
			{
				final String firstParam = st.nextToken();
				final PlayerInstance plyr = World.getInstance().getPlayer(firstParam);
				if (plyr != null)
				{
					if (st.hasMoreTokens())
					{
						try
						{
							final int radius = Integer.parseInt(st.nextToken());
							World.getInstance().forEachVisibleObjectInRange(plyr, Creature.class, radius, knownChar ->
							{
								if ((knownChar instanceof ControllableMobInstance) || (knownChar instanceof FriendlyNpcInstance) || (knownChar == activeChar))
								{
									return;
								}
								
								kill(activeChar, knownChar);
							});
							
							BuilderUtil.sendSysMessage(activeChar, "Killed all characters within a " + radius + " unit radius.");
							return true;
						}
						catch (NumberFormatException e)
						{
							BuilderUtil.sendSysMessage(activeChar, "Invalid radius.");
							return false;
						}
					}
					kill(activeChar, plyr);
				}
				else
				{
					try
					{
						final int radius = Integer.parseInt(firstParam);
						
						World.getInstance().forEachVisibleObjectInRange(activeChar, Creature.class, radius, wo ->
						{
							if ((wo instanceof ControllableMobInstance) || (wo instanceof FriendlyNpcInstance))
							{
								return;
							}
							kill(activeChar, wo);
						});
						
						BuilderUtil.sendSysMessage(activeChar, "Killed all characters within a " + radius + " unit radius.");
						return true;
					}
					catch (NumberFormatException e)
					{
						BuilderUtil.sendSysMessage(activeChar, "Usage: //kill <player_name | radius>");
						return false;
					}
				}
			}
			else
			{
				final WorldObject obj = activeChar.getTarget();
				if ((obj instanceof ControllableMobInstance) || !obj.isCreature())
				{
					activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				}
				else
				{
					kill(activeChar, (Creature) obj);
				}
			}
		}
		return true;
	}
	
	private void kill(PlayerInstance activeChar, Creature target)
	{
		if (target.isPlayer())
		{
			if (!target.isGM())
			{
				target.stopAllEffects(); // e.g. invincibility effect
			}
			target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar, null);
		}
		else if (Config.CHAMPION_ENABLE && target.isChampion())
		{
			target.reduceCurrentHp((target.getMaxHp() * Config.CHAMPION_HP) + 1, activeChar, null);
		}
		else
		{
			boolean targetIsInvul = false;
			if (target.isInvul())
			{
				targetIsInvul = true;
				target.setIsInvul(false);
			}
			
			target.reduceCurrentHp(target.getMaxHp() + 1, activeChar, null);
			
			if (targetIsInvul)
			{
				target.setIsInvul(true);
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
