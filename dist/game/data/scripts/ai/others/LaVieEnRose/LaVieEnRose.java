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
package ai.others.LaVieEnRose;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2jbr.gameserver.model.olympiad.OlympiadManager;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExResponseBeautyList;
import org.l2jbr.gameserver.network.serverpackets.ExResponseResetList;
import org.l2jbr.gameserver.network.serverpackets.ExShowBeautyMenu;

import ai.AbstractNpcAI;

/**
 * La Vie En Rose AI.
 * @author Sdw
 */
public class LaVieEnRose extends AbstractNpcAI
{
	// NPCs
	private static final int LA_VIE_EN_ROSE = 33825;
	private static final int BEAUTY_SHOP_HELPER = 33854;
	
	private LaVieEnRose()
	{
		addStartNpc(LA_VIE_EN_ROSE);
		addTalkId(LA_VIE_EN_ROSE);
		addFirstTalkId(LA_VIE_EN_ROSE);
		addSpawnId(BEAUTY_SHOP_HELPER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33825.html":
			case "33825-1.html":
			case "33825-2.html":
			case "33825-help.html":
			{
				htmltext = event;
				break;
			}
			case "restore_appearance":
			{
				if (canUseBeautyShop(player))
				{
					if (player.getVariables().hasVariable("visualHairId") || player.getVariables().hasVariable("visualFaceId") || player.getVariables().hasVariable("visualHairColorId"))
					{
						htmltext = "33825-2.html";
					}
					else
					{
						htmltext = "33825-norestore.html";
					}
				}
				break;
			}
			case "beauty-change":
			{
				if (canUseBeautyShop(player))
				{
					player.sendPacket(new ExShowBeautyMenu(player, ExShowBeautyMenu.MODIFY_APPEARANCE));
					player.sendPacket(new ExResponseBeautyList(player, ExResponseBeautyList.SHOW_FACESHAPE));
				}
				break;
			}
			case "beauty-restore":
			{
				if (canUseBeautyShop(player))
				{
					player.sendPacket(new ExShowBeautyMenu(player, ExShowBeautyMenu.RESTORE_APPEARANCE));
					player.sendPacket(new ExResponseResetList(player));
				}
				break;
			}
			case "SPAM_TEXT":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_BEAUTY_SHOP_IS_OPEN_COME_ON_IN);
				startQuestTimer("SPAM_TEXT2", 2500, npc, null);
				break;
			}
			case "SPAM_TEXT2":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_CAN_LOOK_GOOD_TOO_BUDDY_COME_ON_COME_ON);
				startQuestTimer("SPAM_TEXT3", 2500, npc, null);
				break;
			}
			case "SPAM_TEXT3":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.EVERYONE_COME_ON_LET_S_GO_GANGNAM_STYLE);
				break;
			}
			case "cancel":
			default:
			{
				break;
			}
		}
		return htmltext;
	}
	
	private boolean canUseBeautyShop(PlayerInstance player)
	{
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THE_BEAUTY_SHOP_WHILE_REGISTERED_IN_THE_OLYMPIAD);
			return false;
		}
		
		if (player.isOnEvent(CeremonyOfChaosEvent.class))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THE_BEAUTY_SHOP_WHILE_REGISTERED_IN_THE_CEREMONY_OF_CHAOS);
			return false;
		}
		
		if (player.isOnEvent()) // custom event message
		{
			player.sendMessage("You cannot use the Beauty Shop while registered in an event.");
			return false;
		}
		
		// player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THE_BEAUTY_SHOP_AS_THE_NPC_SERVER_IS_CURRENTLY_NOT_IN_FUNCTION);
		// player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THE_BEAUTY_SHOP_WHILE_USING_THE_AUTOMATIC_REPLACEMENT);
		
		return true;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		startQuestTimer("SPAM_TEXT", (5 * 60 * 1000), npc, null, true);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new LaVieEnRose();
	}
}