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
package quests.Q10792_LettersFromTheQueenForestOfTheDead;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Letters from the Queen: Forest of the Dead (10792)
 * @author malyelfik
 */
public class Q10792_LettersFromTheQueenForestOfTheDead extends LetterQuest
{
	// NPCs
	private static final int INNOCENTIN = 31328;
	private static final int HATUBA = 33849;
	// Items
	private static final int SOE_RUNE = 39582;
	private static final int SOE_FOREST_OF_DEAD = 39583;
	// Location
	private static final Location TELEPORT_LOC = new Location(36563, -49178, -1128);
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 69;
	
	public Q10792_LettersFromTheQueenForestOfTheDead()
	{
		super(10792);
		addTalkId(INNOCENTIN, HATUBA);
		
		setIsErtheiaQuest(true);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartLocation(SOE_RUNE, TELEPORT_LOC);
		setStartQuestSound("Npcdialog1.serenia_quest_8");
		registerQuestItems(SOE_RUNE, SOE_FOREST_OF_DEAD);
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
			case "31328-02.html":
			case "33849-02.html":
			{
				break;
			}
			case "31328-03.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					giveItems(player, SOE_FOREST_OF_DEAD, 1);
				}
				break;
			}
			case "33849-03.html":
			{
				if (qs.isCond(3))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 942690, 226);
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_70, ExShowScreenMessage.TOP_CENTER, 8000);
					qs.exitQuest(false, true);
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
		final QuestState qs = getQuestState(player, false);
		String htmltext = getNoQuestMsg(player);
		if (qs == null)
		{
			return htmltext;
		}
		
		if (qs.isStarted())
		{
			if (npc.getId() == INNOCENTIN)
			{
				htmltext = (qs.isCond(2)) ? "31328-01.html" : "31328-04.html";
			}
			else if (qs.isCond(3))
			{
				htmltext = "33849-01.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public boolean canShowTutorialMark(PlayerInstance player)
	{
		return player.isMageClass();
	}
}