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
package quests.Q10824_ConfrontingTheGreatestDanger;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10823_ExaltedOneWhoShattersTheLimit.Q10823_ExaltedOneWhoShattersTheLimit;

/**
 * Confronting the Greatest Danger (10824)
 * @URL https://l2wiki.com/Confronting_the_Greatest_Danger
 * @author Mobius
 */
public class Q10824_ConfrontingTheGreatestDanger extends Quest
{
	// NPC
	private static final int MERLOT = 34019;
	// Items
	private static final int MARK_OF_ADVANCE = 46058;
	private static final int KURTIZ_CERTIFICATE = 46057;
	private static final int MAMMON_CERTIFICATE = 45635;
	private static final int GUSTAV_CERTIFICATE = 45636;
	// Rewards
	private static final int MERLOT_SERTIFICATE = 46056;
	private static final int SPELLBOOK_BLESSING_OF_THE_EXALTED = 45926;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q10824_ConfrontingTheGreatestDanger()
	{
		super(10824);
		addStartNpc(MERLOT);
		addTalkId(MERLOT);
		addCondMinLevel(MIN_LEVEL, "34019-02.html");
		addCondStartedQuest(Q10823_ExaltedOneWhoShattersTheLimit.class.getSimpleName(), "34019-03.html");
		registerQuestItems(MARK_OF_ADVANCE);
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
			case "34019-04.htm":
			case "34019-05.htm":
			{
				htmltext = event;
				break;
			}
			case "34019-06.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34019-09.html":
			{
				if (qs.isCond(1) && (getQuestItemsCount(player, MARK_OF_ADVANCE) >= 3))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						if (hasQuestItems(player, KURTIZ_CERTIFICATE, MAMMON_CERTIFICATE, GUSTAV_CERTIFICATE))
						{
							htmltext = "34019-10.html";
						}
						else
						{
							htmltext = event;
						}
						takeItems(player, MARK_OF_ADVANCE, 3);
						giveItems(player, MERLOT_SERTIFICATE, 1);
						giveItems(player, SPELLBOOK_BLESSING_OF_THE_EXALTED, 1);
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
				htmltext = "34019-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (getQuestItemsCount(player, MARK_OF_ADVANCE) >= 3)
				{
					htmltext = "34019-08.html";
				}
				else
				{
					htmltext = "34019-07.html";
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
	
	// TODO: Dimensional Raid - https://l2wiki.com/Dimensional_Raid
}