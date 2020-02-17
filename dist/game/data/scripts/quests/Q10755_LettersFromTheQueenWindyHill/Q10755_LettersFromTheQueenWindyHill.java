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
package quests.Q10755_LettersFromTheQueenWindyHill;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Letters from the Queen: Windy Hill (10755)
 * @author malyelfik
 */
public class Q10755_LettersFromTheQueenWindyHill extends LetterQuest
{
	// NPCs
	private static final int LEVIAN = 30037;
	private static final int PIO = 33963;
	// Location
	private static final Location TELEPORT_LOC = new Location(-79816, 150828, -3040);
	// Item
	private static final int SOE_GLUDIN_VILLAGE = 39491;
	private static final int SOE_WINDY_HILL = 39492;
	// Misc
	private static final int MIN_LEVEL = 20;
	private static final int MAX_LEVEL = 29;
	
	public Q10755_LettersFromTheQueenWindyHill()
	{
		super(10755);
		addTalkId(LEVIAN, PIO);
		
		setIsErtheiaQuest(true);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartLocation(SOE_GLUDIN_VILLAGE, TELEPORT_LOC);
		setStartQuestSound("Npcdialog1.serenia_quest_1");
		registerQuestItems(SOE_GLUDIN_VILLAGE, SOE_WINDY_HILL);
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
			case "30037-02.html":
			case "33963-02.html":
			{
				break;
			}
			case "30037-03.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					giveItems(player, SOE_WINDY_HILL, 1);
					showOnScreenMsg(player, NpcStringId.TRY_USING_THE_TELEPORT_SCROLL_LEVIAN_GAVE_YOU, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				break;
			}
			case "33963-03.html":
			{
				if (qs.isCond(3))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 120960, 29);
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_30, ExShowScreenMessage.TOP_CENTER, 8000);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = event;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = getNoQuestMsg(player);
		
		if (qs == null)
		{
			return htmltext;
		}
		
		if (qs.isStarted())
		{
			if ((npc.getId() == LEVIAN))
			{
				htmltext = (qs.isCond(2)) ? "30037-01.html" : "30037-04.html";
			}
			else if (qs.isCond(3))
			{
				htmltext = "33963-01.html";
			}
		}
		return htmltext;
	}
}
