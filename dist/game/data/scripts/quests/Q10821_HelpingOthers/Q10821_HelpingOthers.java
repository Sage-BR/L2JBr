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
package quests.Q10821_HelpingOthers;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10817_ExaltedOneWhoOvercomesTheLimit.Q10817_ExaltedOneWhoOvercomesTheLimit;

/**
 * Helping Others (10821)
 * @URL https://l2wiki.com/Helping_Others
 * @author Mobius
 */
public class Q10821_HelpingOthers extends Quest
{
	// NPC
	private static final int SIR_ERIC_RODEMAI = 30868;
	// Items
	private static final int MENTEE_MARK = 33804;
	private static final int DAICHIR_SERTIFICATE = 45628;
	private static final int OLYMPIAD_MANAGER_CERTIFICATE = 45629;
	private static final int ISHUMA_CERTIFICATE = 45630;
	// Rewards
	private static final int SIR_KRISTOF_RODEMAI_CERTIFICATE = 45631;
	private static final int SPELLBOOK_FAVOR_OF_THE_EXALTED = 45928;
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10821_HelpingOthers()
	{
		super(10821);
		addStartNpc(SIR_ERIC_RODEMAI);
		addTalkId(SIR_ERIC_RODEMAI);
		addCondMinLevel(MIN_LEVEL, "30868-02.html");
		addCondStartedQuest(Q10817_ExaltedOneWhoOvercomesTheLimit.class.getSimpleName(), "30868-03.html");
		// registerQuestItems(MENTEE_MARK); Should they be removed when abandoning quest?
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30868-04.htm":
			case "30868-05.htm":
			{
				htmltext = event;
				break;
			}
			case "30868-06.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30868-09.html":
			{
				if (qs.isCond(1) && (getQuestItemsCount(player, MENTEE_MARK) >= 45000))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						if (hasQuestItems(player, DAICHIR_SERTIFICATE, ISHUMA_CERTIFICATE, OLYMPIAD_MANAGER_CERTIFICATE))
						{
							htmltext = "30868-10.html";
						}
						else
						{
							htmltext = event;
						}
						takeItems(player, MENTEE_MARK, 45000);
						giveItems(player, SIR_KRISTOF_RODEMAI_CERTIFICATE, 1);
						giveItems(player, SPELLBOOK_FAVOR_OF_THE_EXALTED, 1);
						qs.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
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
				htmltext = "30868-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (getQuestItemsCount(player, MENTEE_MARK) >= 45000)
				{
					htmltext = "30868-08.html";
				}
				else
				{
					htmltext = "30868-07.html";
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
}