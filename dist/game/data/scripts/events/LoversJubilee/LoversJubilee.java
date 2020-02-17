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
package events.LoversJubilee;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.quest.LongTimeEvent;
import org.l2jbr.gameserver.network.serverpackets.ExBrBroadcastEventState;

public class LoversJubilee extends LongTimeEvent
{
	// NPC
	private static final int ROSALIA = 4305;
	// Item
	private static final int ONE_RED_ROSE_BUD = 20905;
	private static final int ONE_BLUE_ROSE_BUD = 20906;
	private static final int ONE_WHILE_ROSE_BUD = 20907;
	private static final int DESELOPH_ROSE_NECKLACE = 20908;
	private static final int HYUM_ROSE_NECKLACE = 20909;
	private static final int REKANG_ROSE_NECKLACE = 20910;
	private static final int LILIAS_ROSE_NECKLACE = 20911;
	private static final int LAPHAM_ROSE_NECKLACE = 20912;
	private static final int MAFUM_ROSE_NECKLACE = 20913;
	private static final int IMPROVED_ROSE_SPIRIT_EXCHANGE_TICKET = 20914;
	private static final int IMPROVED_DESELOPH_ROSE_NECKLACE = 20915;
	private static final int IMPROVED_HYUM_ROSE_NECKLACE = 20916;
	private static final int IMPROVED_REKANG_ROSE_NECKLACE = 20917;
	private static final int IMPROVED_LILIAS_ROSE_NECKLACE = 20918;
	private static final int IMPROVED_LAPHAM_ROSE_NECKLACE = 20919;
	private static final int IMPROVED_MAFUM_ROSE_NECKLACE = 20920;
	private static final int SPIRIT_TEST_REPORT = 20921;
	// Misc
	private static final int ONE_ROSE_PRICE = 500;
	private static final int TEN_ROSES_PRICE = 5000;
	
	private LoversJubilee()
	{
		addStartNpc(ROSALIA);
		addFirstTalkId(ROSALIA);
		addTalkId(ROSALIA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmtext = event;
		switch (event)
		{
			case "50020_1":
			{
				htmtext = hasQuestItems(player, SPIRIT_TEST_REPORT) ? "4305-010.htm" : "4305-002.htm";
				break;
			}
			case "50020_2":
			case "50020_3":
			case "50020_4":
			{
				if (getQuestItemsCount(player, Inventory.ADENA_ID) < ONE_ROSE_PRICE)
				{
					htmtext = "4305-024.htm";
				}
				else
				{
					takeItems(player, Inventory.ADENA_ID, ONE_ROSE_PRICE);
					switch (event)
					{
						case "50020_2":
						{
							giveItems(player, ONE_RED_ROSE_BUD, 1);
							break;
						}
						case "50020_3":
						{
							giveItems(player, ONE_BLUE_ROSE_BUD, 1);
							break;
						}
						case "50020_4":
						{
							giveItems(player, ONE_WHILE_ROSE_BUD, 1);
							break;
						}
					}
					htmtext = "4305-023.htm";
				}
				break;
			}
			case "50020_5":
			case "50020_6":
			case "50020_7":
			{
				if (getQuestItemsCount(player, Inventory.ADENA_ID) < TEN_ROSES_PRICE)
				{
					htmtext = "4305-024.htm";
				}
				else
				{
					takeItems(player, Inventory.ADENA_ID, TEN_ROSES_PRICE);
					switch (event)
					{
						case "50020_5":
						{
							giveItems(player, ONE_RED_ROSE_BUD, 10);
							break;
						}
						case "50020_6":
						{
							giveItems(player, ONE_BLUE_ROSE_BUD, 10);
							break;
						}
						case "50020_7":
						{
							giveItems(player, ONE_WHILE_ROSE_BUD, 10);
							break;
						}
					}
					htmtext = "4305-023.htm";
				}
				break;
			}
			case "50020_8":
			{
				if (hasQuestItems(player, IMPROVED_ROSE_SPIRIT_EXCHANGE_TICKET))
				{
					htmtext = "4305-007.htm";
				}
				else
				{
					htmtext = "4305-008.htm";
				}
				break;
			}
			case "50020_9":
			case "50020_10":
			case "50020_11":
			case "50020_12":
			case "50020_13":
			case "50020_14":
			{
				giveItems(player, SPIRIT_TEST_REPORT, 1);
				switch (event)
				{
					case "50020_9":
					{
						giveItems(player, DESELOPH_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_10":
					{
						giveItems(player, HYUM_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_11":
					{
						giveItems(player, REKANG_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_12":
					{
						giveItems(player, LILIAS_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_13":
					{
						giveItems(player, LAPHAM_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_14":
					{
						giveItems(player, MAFUM_ROSE_NECKLACE, 1);
						break;
					}
				}
				htmtext = "4305-025.htm";
				break;
			}
			case "50020_15":
			case "50020_16":
			case "50020_17":
			case "50020_18":
			case "50020_19":
			case "50020_20":
			{
				takeItems(player, IMPROVED_ROSE_SPIRIT_EXCHANGE_TICKET, 1);
				switch (event)
				{
					case "50020_15":
					{
						giveItems(player, IMPROVED_DESELOPH_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_16":
					{
						giveItems(player, IMPROVED_HYUM_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_17":
					{
						giveItems(player, IMPROVED_REKANG_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_18":
					{
						giveItems(player, IMPROVED_LILIAS_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_19":
					{
						giveItems(player, IMPROVED_LAPHAM_ROSE_NECKLACE, 1);
						break;
					}
					case "50020_20":
					{
						giveItems(player, IMPROVED_MAFUM_ROSE_NECKLACE, 1);
						break;
					}
				}
				htmtext = "4305-026.htm";
				break;
			}
		}
		return htmtext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "4305-001.htm";
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		if (isEventPeriod())
		{
			event.getPlayer().sendPacket(new ExBrBroadcastEventState(ExBrBroadcastEventState.LOVERS_JUBILEE, 1));
		}
	}
	
	public static void main(String[] args)
	{
		new LoversJubilee();
	}
}