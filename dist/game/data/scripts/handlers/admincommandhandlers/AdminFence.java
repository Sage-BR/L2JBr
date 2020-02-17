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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.l2jbr.gameserver.data.xml.impl.FenceData;
import org.l2jbr.gameserver.enums.FenceState;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.instance.FenceInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.html.PageBuilder;
import org.l2jbr.gameserver.model.html.PageResult;
import org.l2jbr.gameserver.model.html.styles.ButtonsStyle;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jbr.gameserver.util.BuilderUtil;

/**
 * @author Sahar, Nik64
 */
public class AdminFence implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_addfence",
		"admin_setfencestate",
		"admin_removefence",
		"admin_listfence",
		"admin_gofence"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_addfence":
			{
				try
				{
					final int width = Integer.parseInt(st.nextToken());
					final int length = Integer.parseInt(st.nextToken());
					final int height = Integer.parseInt(st.nextToken());
					if ((width < 1) || (length < 1))
					{
						BuilderUtil.sendSysMessage(activeChar, "Width and length values must be positive numbers.");
						return false;
					}
					if ((height < 1) || (height > 3))
					{
						BuilderUtil.sendSysMessage(activeChar, "The range for height can only be 1-3.");
						return false;
					}
					
					FenceData.getInstance().spawnFence(activeChar.getX(), activeChar.getY(), activeChar.getZ(), width, length, height, activeChar.getInstanceId(), FenceState.CLOSED);
					BuilderUtil.sendSysMessage(activeChar, "Fence added succesfully.");
				}
				catch (NoSuchElementException | NumberFormatException e)
				{
					BuilderUtil.sendSysMessage(activeChar, "Format must be: //addfence <width> <length> <height>");
				}
				break;
			}
			case "admin_setfencestate":
			{
				try
				{
					final int objId = Integer.parseInt(st.nextToken());
					final int fenceTypeOrdinal = Integer.parseInt(st.nextToken());
					
					if ((fenceTypeOrdinal < 0) || (fenceTypeOrdinal >= FenceState.values().length))
					{
						BuilderUtil.sendSysMessage(activeChar, "Specified FenceType is out of range. Only 0-" + (FenceState.values().length - 1) + " are permitted.");
					}
					else
					{
						final WorldObject obj = World.getInstance().findObject(objId);
						if (obj instanceof FenceInstance)
						{
							final FenceInstance fence = (FenceInstance) obj;
							final FenceState state = FenceState.values()[fenceTypeOrdinal];
							fence.setState(state);
							BuilderUtil.sendSysMessage(activeChar, "Fence " + fence.getName() + "[" + fence.getId() + "]'s state has been changed to " + state.toString());
						}
						else
						{
							BuilderUtil.sendSysMessage(activeChar, "Target is not a fence.");
						}
					}
				}
				catch (NoSuchElementException | NumberFormatException e)
				{
					BuilderUtil.sendSysMessage(activeChar, "Format mustr be: //setfencestate <fenceObjectId> <fenceState>");
				}
				break;
			}
			case "admin_removefence":
			{
				try
				{
					final int objId = Integer.parseInt(st.nextToken());
					final WorldObject obj = World.getInstance().findObject(objId);
					if (obj instanceof FenceInstance)
					{
						((FenceInstance) obj).deleteMe();
						BuilderUtil.sendSysMessage(activeChar, "Fence removed succesfully.");
					}
					else
					{
						BuilderUtil.sendSysMessage(activeChar, "Target is not a fence.");
					}
				}
				catch (Exception e)
				{
					BuilderUtil.sendSysMessage(activeChar, "Invalid object ID or target was not found.");
				}
				sendHtml(activeChar, 0);
				break;
			}
			case "admin_listfence":
			{
				int page = 0;
				if (st.hasMoreTokens())
				{
					page = Integer.parseInt(st.nextToken());
				}
				sendHtml(activeChar, page);
				break;
			}
			case "admin_gofence":
			{
				try
				{
					final int objId = Integer.parseInt(st.nextToken());
					final WorldObject obj = World.getInstance().findObject(objId);
					if (obj != null)
					{
						activeChar.teleToLocation(obj);
					}
				}
				catch (Exception e)
				{
					BuilderUtil.sendSysMessage(activeChar, "Invalid object ID or target was not found.");
				}
				break;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void sendHtml(PlayerInstance activeChar, int page)
	{
		final PageResult result = PageBuilder.newBuilder(FenceData.getInstance().getFences().values(), 10, "bypass -h admin_listfence").currentPage(page).style(ButtonsStyle.INSTANCE).bodyHandler((pages, fence, sb) ->
		{
			sb.append("<tr><td>");
			sb.append(fence.getName() == null ? fence.getId() : fence.getName());
			sb.append("</td><td>");
			sb.append("<button value=\"Go\" action=\"bypass -h admin_gofence ");
			sb.append(fence.getId());
			sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			sb.append("</td><td>");
			sb.append("<button value=\"Hide\" action=\"bypass -h admin_setfencestate ");
			sb.append(fence.getId());
			sb.append(" 0");
			sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			sb.append("</td><td>");
			sb.append("<button value=\"Off\" action=\"bypass -h admin_setfencestate ");
			sb.append(fence.getId());
			sb.append(" 1");
			sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			sb.append("</td><td>");
			sb.append("<button value=\"On\" action=\"bypass -h admin_setfencestate ");
			sb.append(fence.getId());
			sb.append(" 2");
			sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			sb.append("</td><td>");
			sb.append("<button value=\"X\" action=\"bypass -h admin_removefence ");
			sb.append(fence.getId());
			sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			sb.append("</td></tr>");
		}).build();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setFile(activeChar, "data/html/admin/fences.htm");
		
		if (result.getPages() > 0)
		{
			html.replace("%pages%", "<table width=280 cellspacing=0><tr>" + result.getPagerTemplate() + "</tr></table>");
		}
		else
		{
			html.replace("%pages%", "");
		}
		
		html.replace("%fences%", result.getBodyTemplate().toString());
		activeChar.sendPacket(html);
	}
}