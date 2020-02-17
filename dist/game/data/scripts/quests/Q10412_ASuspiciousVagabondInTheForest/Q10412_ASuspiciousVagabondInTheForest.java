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
package quests.Q10412_ASuspiciousVagabondInTheForest;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * A Suspicious Vagabond in the Forest (10412)
 * @author St3eT
 */
public class Q10412_ASuspiciousVagabondInTheForest extends Quest
{
	// NPCs
	private static final int HATUBA = 33849;
	private static final int VAGABOND = 33850; // Suspicious Vagabond Mortally Endangered
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 70;
	
	public Q10412_ASuspiciousVagabondInTheForest()
	{
		super(10412);
		addStartNpc(HATUBA);
		addTalkId(HATUBA, VAGABOND);
		addCondNotRace(Race.ERTHEIA, "33849-08.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33849-09.htm");
		addCondInCategory(CategoryType.MAGE_GROUP, "33849-09.htm");
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
			case "33849-02.htm":
			case "33849-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33849-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33849-07.html":
			{
				if (qs.isCond(2))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(npc, player);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 7541520, 226);
					}
					htmltext = event;
				}
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
				if (npc.getId() == HATUBA)
				{
					htmltext = "33849-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == HATUBA)
				{
					htmltext = qs.isCond(1) ? "33849-05.html" : "33849-06.html";
				}
				else if ((npc.getId() == VAGABOND) && qs.isCond(1))
				{
					qs.setCond(2, true);
					htmltext = "33850-01.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == HATUBA)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
}