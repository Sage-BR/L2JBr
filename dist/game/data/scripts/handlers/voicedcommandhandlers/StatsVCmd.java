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
package handlers.voicedcommandhandlers;

import org.l2jbr.gameserver.handler.IVoicedCommandHandler;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.entity.GameEvent;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Zoey76.
 */
public class StatsVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"stats"
	};
	
	@Override
	public boolean useVoicedCommand(String command, PlayerInstance activeChar, String params)
	{
		if (!command.equals("stats") || (params == null) || params.isEmpty())
		{
			activeChar.sendMessage("Usage: .stats <player name>");
			return false;
		}
		
		final PlayerInstance pc = World.getInstance().getPlayer(params);
		if ((pc == null))
		{
			activeChar.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
			return false;
		}
		
		if (pc.getClient().isDetached())
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_CURRENTLY_OFFLINE);
			sm.addPcName(pc);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (!GameEvent.isParticipant(pc) || (pc.getEventStatus() == null))
		{
			activeChar.sendMessage("That player is not an event participant.");
			return false;
		}
		
		final StringBuilder replyMSG = new StringBuilder(300 + (pc.getEventStatus().getKills().size() * 50));
		replyMSG.append("<html><body><center><font color=\"LEVEL\">[ L2J EVENT ENGINE ]</font></center><br><br>Statistics for player <font color=\"LEVEL\">" + pc.getName() + "</font><br>Total kills <font color=\"FF0000\">" + pc.getEventStatus().getKills().size() + "</font><br><br>Detailed list: <br>");
		pc.getEventStatus().getKills().forEach((p, k) ->
		{
			replyMSG.append("<font color=\"FF0000\">" + p.getName() + "</font> killed " + k + " times.<br>");
		});
		replyMSG.append("</body></html>");
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
