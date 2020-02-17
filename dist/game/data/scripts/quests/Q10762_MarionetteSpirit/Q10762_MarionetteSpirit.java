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
package quests.Q10762_MarionetteSpirit;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10761_AnOrcInLove.Q10761_AnOrcInLove;

/**
 * Marionette Spirit (10762)
 * @author malyelfik
 */
public class Q10762_MarionetteSpirit extends Quest
{
	// NPC
	private static final int VORBOS = 33966;
	// Monster
	private static final int MARIONETTE_SPIRIT = 23418;
	// Items
	private static final int MAGIC_CHAIN_KEY = 39488;
	// Misc
	private static final int MIN_LEVEL = 34;
	private static final int DROP_RATE = 70;
	
	public Q10762_MarionetteSpirit()
	{
		super(10762);
		addStartNpc(VORBOS);
		addTalkId(VORBOS);
		addKillId(MARIONETTE_SPIRIT);
		
		addCondRace(Race.ERTHEIA, "33966-00.htm");
		addCondMinLevel(MIN_LEVEL, "33966-00.htm");
		addCondCompletedQuest(Q10761_AnOrcInLove.class.getSimpleName(), "33966-00.htm");
		registerQuestItems(MAGIC_CHAIN_KEY);
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
			case "33966-02.htm":
			{
				break;
			}
			case "33966-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "33966-06.html":
			{
				if (qs.isCond(2))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 896996, 215);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33966-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33966-04.html" : "33966-05.html";
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
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && (DROP_RATE > getRandom(100)))
		{
			giveItems(killer, MAGIC_CHAIN_KEY, 1);
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}