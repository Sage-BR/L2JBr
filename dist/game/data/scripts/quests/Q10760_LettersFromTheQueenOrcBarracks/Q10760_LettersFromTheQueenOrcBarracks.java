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
package quests.Q10760_LettersFromTheQueenOrcBarracks;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Letters from the Queen: Orc Barracks (10760)
 * @author malyelfik
 */
public class Q10760_LettersFromTheQueenOrcBarracks extends LetterQuest
{
	// NPC
	private static final int LEVIAN = 30037;
	private static final int PIOTUR = 30597;
	// Items
	private static final int SOE_GLUDIN_VILLAGE = 39486;
	private static final int SOE_ORC_BARRACKS = 39487;
	// Location
	private static final Location TELEPORT_LOC = new Location(-79816, 150828, -3040);
	// Misc
	private static final int MIN_LEVEL = 30;
	private static final int MAX_LEVEL = 39;
	
	public Q10760_LettersFromTheQueenOrcBarracks()
	{
		super(10760);
		addTalkId(LEVIAN, PIOTUR);
		
		setIsErtheiaQuest(true);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartLocation(SOE_GLUDIN_VILLAGE, TELEPORT_LOC);
		setStartQuestSound("Npcdialog1.serenia_quest_2");
		registerQuestItems(SOE_GLUDIN_VILLAGE, SOE_ORC_BARRACKS);
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
			case "30597-02.html":
			{
				break;
			}
			case "30037-03.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					giveItems(player, SOE_ORC_BARRACKS, 1);
					showOnScreenMsg(player, NpcStringId.TRY_USING_THE_TELEPORT_SCROLL_LEVIAN_GAVE_YOU_TO_GO_TO_ORC_BARRACKS, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				break;
			}
			case "30597-03.html":
			{
				if (qs.isCond(3))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 242760, 58);
					showOnScreenMsg(player, NpcStringId.TRY_TALKING_TO_VORBOS_BY_THE_WELL_NYOU_CAN_RECEIVE_QUEEN_NAVARI_S_NEXT_LETTER_AT_LV_40, ExShowScreenMessage.TOP_CENTER, 8000);
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
				htmltext = "30597-01.html";
			}
		}
		return htmltext;
	}
}
