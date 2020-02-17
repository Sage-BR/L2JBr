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
package handlers.bypasshandlers;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.cache.HtmCache;
import org.l2jbr.gameserver.handler.IBypassHandler;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.instance.TeleporterInstance;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;

public class Link implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"Link"
	};
	
	private static final String[] VALID_LINKS =
	{
		"common/attribute_info.htm",
		"common/augmentation_01.htm",
		"common/augmentation_02.htm",
		"common/augmentation_exchange.htm",
		"common/crafting_01.htm",
		"common/crafting_02.htm",
		"common/crafting_03.htm",
		"common/cursed_to_unidentified.htm",
		"common/duals_01.htm",
		"common/duals_02.htm",
		"common/duals_03.htm",
		"common/g_cube_warehouse001.htm",
		"common/skill_enchant_help.htm",
		"common/skill_enchant_help_01.htm",
		"common/skill_enchant_help_02.htm",
		"common/skill_enchant_help_03.htm",
		"common/smelting_trade001.htm",
		"common/weapon_sa_01.htm",
		"common/welcomeback002.htm",
		"common/welcomeback003.htm",
		"default/BlessingOfProtection.htm",
		"default/SupportMagic.htm",
		"default/SupportMagicServitor.htm",
		"fisherman/exchange_old_items.htm",
		"fisherman/fish_appearance_exchange.htm",
		"fisherman/fishing_manual001.htm",
		"fisherman/fishing_manual002.htm",
		"fisherman/fishing_manual003.htm",
		"fisherman/fishing_manual004.htm",
		"fisherman/fishing_manual008.htm",
		"fisherman/fishing_manual009.htm",
		"fisherman/fishing_manual010.htm",
		"fortress/foreman.htm",
		"guard/kamaloka_help.htm",
		"guard/kamaloka_level.htm",
		"petmanager/evolve.htm",
		"petmanager/exchange.htm",
		"petmanager/instructions.htm",
		"teleporter/separatedsoul.htm",
		"warehouse/clanwh.htm",
		"warehouse/privatewh.htm",
		// Quests
		"teleporter/30006.htm",
		"teleporter/30006-Q561.htm",
		"teleporter/30006-Q561-1.htm",
		"teleporter/30006-Q561-2.htm",
		"teleporter/30134.htm",
		"teleporter/30134-Q562.htm",
		"teleporter/30134-Q562-1.htm",
		"teleporter/30134-Q562-2.htm",
		"teleporter/30256.htm",
		"teleporter/30256-Q562.htm",
		"teleporter/30256-Q562-1.htm",
		"teleporter/30256-Q562-2.htm",
		"teleporter/30848.htm",
		"teleporter/30848-Q561-Q562.htm",
		"teleporter/30848-Q561-Q562-1.htm",
		"teleporter/30848-Q561-Q562-2.htm",
	};
	
	@Override
	public boolean useBypass(String command, PlayerInstance player, Creature target)
	{
		final String htmlPath = command.substring(4).trim();
		if (htmlPath.isEmpty())
		{
			LOGGER.warning("Player " + player.getName() + " sent empty link html!");
			return false;
		}
		
		if (htmlPath.contains(".."))
		{
			LOGGER.warning("Player " + player.getName() + " sent invalid link html: " + htmlPath);
			return false;
		}
		
		String content = CommonUtil.contains(VALID_LINKS, htmlPath) ? HtmCache.getInstance().getHtm(player, "data/html/" + htmlPath) : null;
		// Precaution.
		if (htmlPath.startsWith("teleporter/") && !(player.getTarget() instanceof TeleporterInstance))
		{
			content = null;
		}
		final NpcHtmlMessage html = new NpcHtmlMessage(target != null ? target.getObjectId() : 0);
		if (content != null)
		{
			html.setHtml(content.replace("%objectId%", String.valueOf(target != null ? target.getObjectId() : 0)));
		}
		player.sendPacket(html);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
