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
package quests.Q00752_UncoverTheSecret;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10386_MysteriousJourney.Q10386_MysteriousJourney;

/**
 * Uncover the Secret (752)
 * @URL https://l2wiki.com/Uncover_the_Secret
 * @author Gigi
 */
public class Q00752_UncoverTheSecret extends Quest
{
	// Npc
	private static final int HESET = 33780;
	// Monster's
	private static final int SPICULA1 = 23246;
	private static final int SPICULA2 = 23247;
	private static final int SPICULA3 = 23248;
	private static final int SPICULA4 = 23249;
	private static final int SPICULA5 = 23250;
	private static final int SPICULA6 = 23251;
	private static final int TWOSWORD_GOLEM = 23257;
	private static final int SCREW_GOLEM = 23267;
	private static final int LIGHT_GOLEM = 23255;
	private static final int BROKEN_BODIED_GOLEM = 23259;
	
	private static final int YIN_SPICULA = 23254;
	private static final int SPICULA_ELITE_GUARD = 23303;
	private static final int SPICULA_ELITE_SOLDIER = 23262;
	
	// Items
	private static final int TRACE = 36074;
	private static final int ENIGMATIC = 36075;
	private static final int SCROLL = 36082;
	// Misc
	private static final int MIN_LEVEL = 93;
	
	public Q00752_UncoverTheSecret()
	{
		super(752);
		addStartNpc(HESET);
		addTalkId(HESET);
		addKillId(SPICULA1, SPICULA2, SPICULA3, SPICULA4, SPICULA5, SPICULA6, TWOSWORD_GOLEM, SCREW_GOLEM, LIGHT_GOLEM, BROKEN_BODIED_GOLEM, YIN_SPICULA, SPICULA_ELITE_GUARD, SPICULA_ELITE_SOLDIER);
		registerQuestItems(TRACE, ENIGMATIC);
		addCondMinLevel(MIN_LEVEL, "lvl.htm");
		addCondCompletedQuest(Q10386_MysteriousJourney.class.getSimpleName(), "restriction.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "33780-02.htm":
			case "33780-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33780-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33780-05.html":
			{
				giveItems(player, SCROLL, 1);
				addExpAndSp(player, 408665250, 98079);
				qs.exitQuest(QuestType.DAILY, true);
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
		
		if (npc.getId() == HESET)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "33780-0.htm";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "33780-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33780-04a.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33780-05a.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			switch (npc.getId())
			{
				case SPICULA1:
				case SPICULA2:
				case SPICULA3:
				case SPICULA4:
				case SPICULA5:
				case SPICULA6:
				case TWOSWORD_GOLEM:
				case SCREW_GOLEM:
				case LIGHT_GOLEM:
				case BROKEN_BODIED_GOLEM:
				{
					if ((getQuestItemsCount(killer, ENIGMATIC) < 20) && (getRandom(100) < 10))
					{
						giveItems(killer, ENIGMATIC, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case YIN_SPICULA:
				case SPICULA_ELITE_GUARD:
				case SPICULA_ELITE_SOLDIER:
				{
					if ((getQuestItemsCount(killer, TRACE) < 10) && (getRandom(100) < 5))
					{
						giveItems(killer, TRACE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			if ((getQuestItemsCount(killer, TRACE) >= 10) && (getQuestItemsCount(killer, ENIGMATIC) >= 20))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}