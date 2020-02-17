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
package events.BirthOfDraco;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.LongTimeEvent;

/**
 * Birth of Draco
 * @URL http://www.lineage2.com/en/news/events/birth-of-draco.php
 * @author Mobius
 */
public class BirthOfDraco extends LongTimeEvent
{
	// NPCs
	private static final int ASLAN = 33687;
	private static final int LYN_DRACO = 33828;
	// Items
	private static final int SMALL_EGG = 34864;
	private static final int LARGE_EGG = 34865;
	private static final int SHATTERED_EGG = 34866;
	private static final int SHINY_GIFT = 34868;
	// private static final int AGATHION = 34869; // needs proper ExSendUIEvent
	
	private BirthOfDraco()
	{
		addStartNpc(ASLAN);
		addFirstTalkId(ASLAN);
		addTalkId(ASLAN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33687-01.htm":
			case "33687-04.htm":
			case "33687-07.htm":
			case "33687-08.htm":
			case "33687-09.htm":
			case "howtohatch.htm": // custom info
			case "lyndraco.htm": // custom info
			case "rewards.htm": // custom info
			{
				htmltext = event;
				break;
			}
			case "giveFiftySmallEggs":
			{
				if (getQuestItemsCount(player, SMALL_EGG) >= 50)
				{
					takeItems(player, SMALL_EGG, 50);
					addSpawn(LYN_DRACO, player.getX(), player.getY(), player.getZ(), player.getHeading() + 32500, true, 180000);
				}
				else
				{
					htmltext = "33687-11.htm";
				}
				break;
			}
			case "getRandomReward":
			{
				if (getQuestItemsCount(player, SHATTERED_EGG) >= 50)
				{
					takeItems(player, SHATTERED_EGG, 50);
					if (getRandom(100) < 30)
					{
						giveItems(player, SHINY_GIFT, 1);
					}
					else if (getRandom(100) < 30)
					{
						giveItems(player, LARGE_EGG, 1);
					}
					else if (getRandom(100) < 30)
					{
						giveItems(player, SMALL_EGG, 1);
					}
					else
					{
						htmltext = "33687-05.htm";
					}
				}
				else
				{
					htmltext = "33687-06.htm";
				}
				break;
			}
			/*
			 * case "giveAgathion": { if (hasQuestItems(player, AGATHION)) { htmltext = "33687-03.htm"; } else { giveItems(player, AGATHION, 1); htmltext = "33687-02.htm"; } break; }
			 */
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + "-01.htm";
	}
	
	public static void main(String[] args)
	{
		new BirthOfDraco();
	}
}
