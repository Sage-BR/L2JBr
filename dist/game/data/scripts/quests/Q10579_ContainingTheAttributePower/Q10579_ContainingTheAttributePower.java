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
package quests.Q10579_ContainingTheAttributePower;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.Containers;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.impl.item.OnItemAttributeAdd;
import org.l2jbr.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.serverpackets.ExTutorialShowId;

import quests.Q10566_BestChoice.Q10566_BestChoice;

/**
 * Containing the Attribute Power (10579)
 * @URL https://l2wiki.com/Containing_the_Attribute_Power
 * @author Werum / NightBR
 */
public class Q10579_ContainingTheAttributePower extends Quest
{
	// NPC
	private static final int FERRIS = 30847;
	// Items
	// TODO: Need to add some of the Stones that are not present in the current client
	private static final int ATTRIBUTE_PRACTICE_LONG_SWORD = 48168;
	private static final int ATTRIBUTE_PRACTICE_FIRE_STONE = 48169;
	private static final int ATTRIBUTE_PRACTICE_WATER_STONE = 48169; // FIXME: Does not exist.
	private static final int ATTRIBUTE_PRACTICE_EARTH_STONE = 48169; // FIXME: Does not exist.
	private static final int ATTRIBUTE_PRACTICE_WIND_STONE = 48169; // FIXME: Does not exist.
	private static final int ATTRIBUTE_PRACTICE_HOLY_STONE = 48169; // FIXME: Does not exist.
	private static final int ATTRIBUTE_PRACTICE_DARK_STONE = 48169; // FIXME: Does not exist.
	// Rewards
	private static final int XP = 597699960;
	private static final int SP = 597690;
	private static final int CERTIFICATE_FROM_FERRIS = 48177;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10579_ContainingTheAttributePower()
	{
		super(10579);
		addStartNpc(FERRIS);
		addTalkId(FERRIS);
		addCondMinLevel(MIN_LEVEL, "noLevel.html");
		registerQuestItems(ATTRIBUTE_PRACTICE_LONG_SWORD, ATTRIBUTE_PRACTICE_FIRE_STONE, ATTRIBUTE_PRACTICE_WATER_STONE, ATTRIBUTE_PRACTICE_EARTH_STONE, ATTRIBUTE_PRACTICE_WIND_STONE, ATTRIBUTE_PRACTICE_HOLY_STONE, ATTRIBUTE_PRACTICE_DARK_STONE);
		addCondStartedQuest(Q10566_BestChoice.class.getSimpleName(), "34138-99.html");
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_ITEM_ATTRIBUTE_ADD, (OnItemAttributeAdd event) -> OnItemAttributeAdd(event), this));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		String htmltext = null;
		switch (event)
		{
			case "30847-02.htm":
			case "30847-05.html":
			case "30847-06.html":
			case "30847-07.html":
			case "30847-13.html":
			case "30847-15.html":
			{
				htmltext = event;
				break;
			}
			case "30847-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30847-04.html":
			{
				// show Service/Help/Auction House page
				player.sendPacket(new ExTutorialShowId(58));
				htmltext = event;
				break;
			}
			case "30847-08.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30847-12.html":
			{
				// show Service/Help/Auction House page
				player.sendPacket(new ExTutorialShowId(58));
				htmltext = event;
				break;
			}
			case "30847-16.html":
			{
				// show Service/Help/Applying Elemental Attribute page
				player.sendPacket(new ExTutorialShowId(41));
				htmltext = event;
				break;
			}
			case "30847-fire.html":
			{
				// show Service/Help/Applying Elemental Attribute page
				player.sendPacket(new ExTutorialShowId(41));
				giveItems(player, ATTRIBUTE_PRACTICE_LONG_SWORD, 1);
				giveItems(player, ATTRIBUTE_PRACTICE_FIRE_STONE, 3);
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30847-water.html":
			{
				// show Service/Help/Applying Elemental Attribute page
				player.sendPacket(new ExTutorialShowId(41));
				giveItems(player, ATTRIBUTE_PRACTICE_LONG_SWORD, 1);
				giveItems(player, ATTRIBUTE_PRACTICE_WATER_STONE, 3);
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30847-earth.html":
			{
				// show Service/Help/Applying Elemental Attribute page
				player.sendPacket(new ExTutorialShowId(41));
				giveItems(player, ATTRIBUTE_PRACTICE_LONG_SWORD, 1);
				giveItems(player, ATTRIBUTE_PRACTICE_EARTH_STONE, 3);
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30847-wind.html":
			{
				// show Service/Help/Applying Elemental Attribute page
				player.sendPacket(new ExTutorialShowId(41));
				giveItems(player, ATTRIBUTE_PRACTICE_LONG_SWORD, 1);
				giveItems(player, ATTRIBUTE_PRACTICE_WIND_STONE, 3);
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30847-holy.html":
			{
				// show Service/Help/Applying Elemental Attribute page
				player.sendPacket(new ExTutorialShowId(41));
				giveItems(player, ATTRIBUTE_PRACTICE_LONG_SWORD, 1);
				giveItems(player, ATTRIBUTE_PRACTICE_HOLY_STONE, 3);
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30847-dark.html":
			{
				// show Service/Help/Applying Elemental Attribute page
				player.sendPacket(new ExTutorialShowId(41));
				giveItems(player, ATTRIBUTE_PRACTICE_LONG_SWORD, 1);
				giveItems(player, ATTRIBUTE_PRACTICE_DARK_STONE, 3);
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30847-10.html":
			{
				addExpAndSp(player, XP, SP);
				giveItems(player, CERTIFICATE_FROM_FERRIS, 1);
				qs.exitQuest(QuestType.ONE_TIME, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "30847-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "30847-04.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "30847-08.html";
				}
				else
				{
					htmltext = (qs.isCond(4)) ? "30847-09.html" : "30847-11.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	public void OnItemAttributeAdd(OnItemAttributeAdd event)
	{
		final PlayerInstance player = event.getPlayer();
		if ((player == null) || (event.getItem().getId() != ATTRIBUTE_PRACTICE_LONG_SWORD))
		{
			return;
		}
		
		final QuestState qs = getQuestState(player, false);
		// Check weapon has elemental enchant to complete the quest
		if ((qs != null) && qs.isCond(3) && (player.getInventory().getItemByItemId(ATTRIBUTE_PRACTICE_LONG_SWORD).hasAttributes()))
		{
			qs.setCond(4, true);
		}
	}
}
