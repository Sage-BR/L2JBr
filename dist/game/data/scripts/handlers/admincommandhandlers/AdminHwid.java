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

import org.l2jbr.gameserver.cache.HtmCache;
import org.l2jbr.gameserver.handler.IAdminCommandHandler;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Mobius
 */
public class AdminHwid implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_hwid",
		"admin_hwinfo"
	};
	
	@Override
	public boolean useAdminCommand(String command, PlayerInstance activeChar)
	{
		if ((activeChar.getTarget() == null) || !activeChar.getTarget().isPlayer() || (activeChar.getTarget().getActingPlayer().getClient() == null) || (activeChar.getTarget().getActingPlayer().getClient().getHardwareInfo() == null))
		{
			return true;
		}
		final PlayerInstance target = activeChar.getTarget().getActingPlayer();
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/charhwinfo.htm"));
		html.replace("%name%", target.getName());
		html.replace("%macAddress%", target.getClient().getHardwareInfo().getMacAddress());
		html.replace("%windowsPlatformId%", target.getClient().getHardwareInfo().getWindowsPlatformId());
		html.replace("%windowsMajorVersion%", target.getClient().getHardwareInfo().getWindowsMajorVersion());
		html.replace("%windowsMinorVersion%", target.getClient().getHardwareInfo().getWindowsMinorVersion());
		html.replace("%windowsBuildNumber%", target.getClient().getHardwareInfo().getWindowsBuildNumber());
		html.replace("%cpuName%", target.getClient().getHardwareInfo().getCpuName());
		html.replace("%cpuSpeed%", target.getClient().getHardwareInfo().getCpuSpeed());
		html.replace("%cpuCoreCount%", target.getClient().getHardwareInfo().getCpuCoreCount());
		html.replace("%vgaName%", target.getClient().getHardwareInfo().getVgaName());
		html.replace("%vgaDriverVersion%", target.getClient().getHardwareInfo().getVgaDriverVersion());
		activeChar.sendPacket(html);
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}