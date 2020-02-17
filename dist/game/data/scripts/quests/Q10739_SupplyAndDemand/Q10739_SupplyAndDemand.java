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
package quests.Q10739_SupplyAndDemand;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10738_AnInnerBeauty.Q10738_AnInnerBeauty;

/**
 * Supply And Demand (10739)
 * @author Sdw
 */
public class Q10739_SupplyAndDemand extends Quest
{
	// NPCs
	private static final int EVNA = 33935;
	private static final int DENYA = 33934;
	private static final int PELU = 33936;
	private static final int CERI = 33937;
	private static final int SIVANTHE = 33951;
	// Items
	private static final ItemHolder WEAPON_SUPPLY_BOX = new ItemHolder(39522, 1);
	private static final ItemHolder ARMOR_SUPPLY_BOX = new ItemHolder(39523, 1);
	private static final ItemHolder GROCERY_SUPPLY_BOX = new ItemHolder(39524, 1);
	private static final ItemHolder ACCESSORY_SUPPLY_BOX = new ItemHolder(39525, 1);
	private static final ItemHolder LEATHER_SHIRT = new ItemHolder(22, 1);
	private static final ItemHolder LEATHER_PANTS = new ItemHolder(29, 1);
	private static final ItemHolder APPRENTICE_EARRING = new ItemHolder(112, 2);
	private static final ItemHolder NECKLACE_OF_KNOWNLEDGE = new ItemHolder(906, 1);
	// Misc
	private static final int MIN_LEVEL = 6;
	private static final int MAX_LEVEL = 20;
	
	public Q10739_SupplyAndDemand()
	{
		super(10739);
		addStartNpc(EVNA);
		addTalkId(EVNA, DENYA, PELU, CERI, SIVANTHE);
		
		addCondRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33935-05.htm");
		addCondCompletedQuest(Q10738_AnInnerBeauty.class.getSimpleName(), "33935-05.htm");
		registerQuestItems(WEAPON_SUPPLY_BOX.getId(), ARMOR_SUPPLY_BOX.getId(), GROCERY_SUPPLY_BOX.getId(), ACCESSORY_SUPPLY_BOX.getId());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33935-02.htm":
			{
				break;
			}
			case "33935-03.htm":
			{
				qs.startQuest();
				giveItems(player, WEAPON_SUPPLY_BOX);
				break;
			}
			case "33934-02.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					giveItems(player, ARMOR_SUPPLY_BOX);
				}
				break;
			}
			case "33936-02.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true); // proper?
					giveItems(player, GROCERY_SUPPLY_BOX);
				}
				break;
			}
			case "33937-02.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4, true);
					giveItems(player, ACCESSORY_SUPPLY_BOX);
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case EVNA:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = "33935-01.htm";
						break;
					}
					case State.STARTED:
					{
						htmltext = "33935-04.html";
						break;
					}
					case State.COMPLETED:
					{
						htmltext = getAlreadyCompletedMsg(player);
						break;
					}
				}
				break;
			}
			case DENYA:
			{
				if (qs.isStarted())
				{
					if (qs.isCond(1))
					{
						htmltext = "33934-01.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33934-03.html";
					}
				}
				break;
			}
			case PELU:
			{
				if (qs.isStarted())
				{
					if (qs.isCond(2))
					{
						htmltext = "33936-01.html";
					}
					else if (qs.isCond(3))
					{
						htmltext = "33936-03.html";
					}
				}
				break;
			}
			case CERI:
			{
				if (qs.isStarted())
				{
					if (qs.isCond(3))
					{
						htmltext = "33937-01.html";
					}
					else if (qs.isCond(4))
					{
						htmltext = "33937-03.html";
					}
				}
				break;
			}
			case SIVANTHE:
			{
				if (qs.isStarted() && qs.isCond(4))
				{
					giveItems(player, LEATHER_SHIRT);
					giveItems(player, LEATHER_PANTS);
					giveItems(player, APPRENTICE_EARRING);
					giveItems(player, NECKLACE_OF_KNOWNLEDGE);
					addExpAndSp(player, 8136, 0);
					showOnScreenMsg(player, NpcStringId.CHECK_YOUR_EQUIPMENT_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 10000);
					qs.exitQuest(false, true);
					htmltext = "33951-01.html";
				}
				else if (qs.isCompleted())
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
}
