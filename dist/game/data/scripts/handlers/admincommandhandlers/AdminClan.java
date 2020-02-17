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

import org.l2jbr.gameserver.cache.HtmCache;
import org.l2jbr.gameserver.data.sql.impl.ClanTable;
import org.l2jbr.gameserver.data.xml.impl.ClanHallData;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.instancemanager.FortManager;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.clan.ClanMember;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jbr.gameserver.util.BuilderUtil;
import org.l2jbr.gameserver.util.Util;

/**
 * @author UnAfraid, Zoey76
 */
public class AdminClan implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_clan_info",
		"admin_clan_changeleader",
		"admin_clan_show_pending",
		"admin_clan_force_pending"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_clan_info":
			{
				final PlayerInstance player = getPlayer(activeChar, st);
				if (player == null)
				{
					break;
				}
				
				final Clan clan = player.getClan();
				if (clan == null)
				{
					activeChar.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
					return false;
				}
				
				final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
				html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/claninfo.htm"));
				html.replace("%clan_name%", clan.getName());
				html.replace("%clan_leader%", clan.getLeaderName());
				html.replace("%clan_level%", String.valueOf(clan.getLevel()));
				html.replace("%clan_has_castle%", clan.getCastleId() > 0 ? CastleManager.getInstance().getCastleById(clan.getCastleId()).getName() : "No");
				html.replace("%clan_has_clanhall%", clan.getHideoutId() > 0 ? ClanHallData.getInstance().getClanHallById(clan.getHideoutId()).getName() : "No");
				html.replace("%clan_has_fortress%", clan.getFortId() > 0 ? FortManager.getInstance().getFortById(clan.getFortId()).getName() : "No");
				html.replace("%clan_points%", String.valueOf(clan.getReputationScore()));
				html.replace("%clan_players_count%", String.valueOf(clan.getMembersCount()));
				html.replace("%clan_ally%", clan.getAllyId() > 0 ? clan.getAllyName() : "Not in ally");
				html.replace("%current_player_objectId%", String.valueOf(player.getObjectId()));
				html.replace("%current_player_name%", player.getName());
				activeChar.sendPacket(html);
				break;
			}
			case "admin_clan_changeleader":
			{
				final PlayerInstance player = getPlayer(activeChar, st);
				if (player == null)
				{
					break;
				}
				
				final Clan clan = player.getClan();
				if (clan == null)
				{
					activeChar.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
					return false;
				}
				
				final ClanMember member = clan.getClanMember(player.getObjectId());
				if (member != null)
				{
					if (player.isAcademyMember())
					{
						player.sendPacket(SystemMessageId.THAT_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY_MEMBER);
					}
					else
					{
						clan.setNewLeader(member);
					}
				}
				break;
			}
			case "admin_clan_show_pending":
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
				html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/clanchanges.htm"));
				final StringBuilder sb = new StringBuilder();
				for (Clan clan : ClanTable.getInstance().getClans())
				{
					if (clan.getNewLeaderId() != 0)
					{
						sb.append("<tr>");
						sb.append("<td>" + clan.getName() + "</td>");
						sb.append("<td>" + clan.getNewLeaderName() + "</td>");
						sb.append("<td><a action=\"bypass -h admin_clan_force_pending " + clan.getId() + "\">Force</a></td>");
						sb.append("</tr>");
					}
				}
				html.replace("%data%", sb.toString());
				activeChar.sendPacket(html);
				break;
			}
			case "admin_clan_force_pending":
			{
				if (st.hasMoreElements())
				{
					final String token = st.nextToken();
					if (!Util.isDigit(token))
					{
						break;
					}
					final int clanId = Integer.parseInt(token);
					
					final Clan clan = ClanTable.getInstance().getClan(clanId);
					if (clan == null)
					{
						break;
					}
					
					final ClanMember member = clan.getClanMember(clan.getNewLeaderId());
					if (member == null)
					{
						break;
					}
					
					clan.setNewLeader(member);
					BuilderUtil.sendSysMessage(activeChar, "Task have been forcely executed.");
					break;
				}
			}
		}
		return true;
	}
	
	/**
	 * @param activeChar
	 * @param st
	 * @return
	 */
	private PlayerInstance getPlayer(PlayerInstance activeChar, StringTokenizer st)
	{
		String val;
		PlayerInstance player = null;
		if (st.hasMoreTokens())
		{
			val = st.nextToken();
			// From the HTML we receive player's object Id.
			if (Util.isDigit(val))
			{
				player = World.getInstance().getPlayer(Integer.parseInt(val));
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
					return null;
				}
			}
			else
			{
				player = World.getInstance().getPlayer(val);
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_NAME_PLEASE_TRY_AGAIN);
					return null;
				}
			}
		}
		else
		{
			final WorldObject targetObj = activeChar.getTarget();
			if ((targetObj == null) || !targetObj.isPlayer())
			{
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				return null;
			}
			player = targetObj.getActingPlayer();
		}
		return player;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
