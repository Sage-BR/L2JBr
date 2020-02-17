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
package quests.Q10343_DayOfDestinyDarkElfsFate;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.ThirdClassTransferQuest;

/**
 * Day of Destiny: Dark Elf's Fate (10343)
 * @author St3eT
 */
public class Q10343_DayOfDestinyDarkElfsFate extends ThirdClassTransferQuest
{
	// NPC
	private static final int OLTRAN = 30862;
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final Race START_RACE = Race.DARK_ELF;
	
	public Q10343_DayOfDestinyDarkElfsFate()
	{
		super(10343, MIN_LEVEL, START_RACE);
		addStartNpc(OLTRAN);
		addTalkId(OLTRAN);
		addCondMinLevel(MIN_LEVEL, "30862-11.html");
		addCondRace(START_RACE, "30862-11.html");
		addCondInCategory(CategoryType.THIRD_CLASS_GROUP, "30862-12.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30862-02.htm":
			case "30862-03.htm":
			case "30862-04.htm":
			case "30862-08.html":
			{
				htmltext = event;
				break;
			}
			case "30862-05.htm":
			{
				qs.startQuest();
				qs.set("STARTED_CLASS", player.getClassId().getId());
				htmltext = event;
				break;
			}
			default:
			{
				htmltext = super.onAdvEvent(event, npc, player);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		if (npc.getId() == OLTRAN)
		{
			if (qs.getState() == State.CREATED)
			{
				htmltext = "30862-01.htm";
			}
			else if (qs.getState() == State.STARTED)
			{
				if (qs.isCond(1))
				{
					htmltext = "30862-06.html";
				}
				else if (qs.isCond(13))
				{
					htmltext = "30862-07.html";
				}
			}
		}
		return (!htmltext.equals(getNoQuestMsg(player)) ? htmltext : super.onTalk(npc, player));
	}
}