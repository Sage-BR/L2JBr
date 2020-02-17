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
package quests.Q10705_BottleOfTautisSoul;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10384_AnAudienceWithTauti.Q10384_AnAudienceWithTauti;

/**
 * Bottle of Tauti's Soul (10705)
 * @URL http://l2on.net/en/?c=quests&id=10705&game=1
 * @author Gigi
 */
public class Q10705_BottleOfTautisSoul extends Quest
{
	// NPCs
	private static final int FERGASON = 33681;
	// Item
	private static final int BOTTLE_OF_TAUTIS_SOUL = 35295;
	// Misc
	private static final int MIN_LEVEL = 97;
	
	public Q10705_BottleOfTautisSoul()
	{
		super(10705);
		addStartNpc(FERGASON);
		addTalkId(FERGASON);
		addCondMinLevel(MIN_LEVEL, "33681-00.html");
		addCondCompletedQuest(Q10384_AnAudienceWithTauti.class.getSimpleName(), "33681-00.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		final QuestState qs1 = player.getQuestState(Q10384_AnAudienceWithTauti.class.getSimpleName());
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "33681-02.html":
			case "33681-03.html":
			case "33681-04.html":
			{
				htmltext = event;
				break;
			}
			case "33681-05.html":
			{
				qs.startQuest();
				break;
			}
			case "33681-06.html":
			{
				if (qs.isCond(1) && (getQuestItemsCount(player, BOTTLE_OF_TAUTIS_SOUL) >= 1))
				{
					takeItems(player, BOTTLE_OF_TAUTIS_SOUL, 1);
					qs1.setState(State.CREATED);
					qs1.setMemoState(1);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				else
				{
					htmltext = getNoQuestMsg(player);
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
				if (getQuestItemsCount(player, BOTTLE_OF_TAUTIS_SOUL) >= 1)
				{
					htmltext = "33681-01.html";
					break;
				}
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33681-05.html";
					break;
				}
			}
			case State.COMPLETED:
			{
				htmltext = getNoQuestMsg(player);
				break;
			}
		}
		return htmltext;
	}
}