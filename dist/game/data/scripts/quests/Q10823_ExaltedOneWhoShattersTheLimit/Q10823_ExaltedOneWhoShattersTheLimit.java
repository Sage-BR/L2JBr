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
package quests.Q10823_ExaltedOneWhoShattersTheLimit;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10817_ExaltedOneWhoOvercomesTheLimit.Q10817_ExaltedOneWhoOvercomesTheLimit;

/**
 * Exalted, One Who Shatters the Limit (10823)
 * @URL https://l2wiki.com/Exalted,_One_Who_Shatters_the_Limit
 * @author Mobius
 */
public class Q10823_ExaltedOneWhoShattersTheLimit extends Quest
{
	// NPC
	private static final int LIONEL = 33907;
	// Items
	private static final int MERLOT_SERTIFICATE = 46056;
	private static final int KURTIZ_CERTIFICATE = 46057;
	private static final int MAMMON_CERTIFICATE = 45635;
	private static final int GUSTAV_CERTIFICATE = 45636;
	private static final int LIONEL_MISSION_LIST_3 = 45637;
	// Rewards
	private static final int EXALTED_CLOAK = 37763;
	private static final int OBTAIN_EXALTED_STATUS = 45638;
	private static final int EXALTED_TIARA = 45644;
	private static final int DIGNITY_OF_THE_EXALTED = 45924;
	// Misc
	private static final int MIN_LEVEL = 100;
	private static final int MIN_DUALCLASS_LEVEL = 100;
	
	public Q10823_ExaltedOneWhoShattersTheLimit()
	{
		super(10823);
		addStartNpc(LIONEL);
		addTalkId(LIONEL);
		addCondMinLevel(MIN_LEVEL, "");
		addCondCompletedQuest(Q10817_ExaltedOneWhoOvercomesTheLimit.class.getSimpleName(), "33907-02.html");
		registerQuestItems(LIONEL_MISSION_LIST_3, MERLOT_SERTIFICATE, KURTIZ_CERTIFICATE, MAMMON_CERTIFICATE, GUSTAV_CERTIFICATE);
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
			case "33907-03.htm":
			case "33907-04.htm":
			{
				htmltext = event;
				break;
			}
			case "33907-05.html":
			{
				if (qs.isCreated())
				{
					giveItems(player, LIONEL_MISSION_LIST_3, 1);
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "33907-08.html":
			{
				if (hasQuestItems(player, MERLOT_SERTIFICATE, KURTIZ_CERTIFICATE, MAMMON_CERTIFICATE, GUSTAV_CERTIFICATE) && ((player.getDualClass() != null) && (player.getDualClass().getLevel() >= MIN_DUALCLASS_LEVEL)))
				{
					giveItems(player, EXALTED_CLOAK, 1);
					giveItems(player, OBTAIN_EXALTED_STATUS, 1);
					giveItems(player, EXALTED_TIARA, 1);
					giveItems(player, DIGNITY_OF_THE_EXALTED, 1);
					qs.exitQuest(false, true);
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
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33907-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (hasQuestItems(player, MERLOT_SERTIFICATE, KURTIZ_CERTIFICATE, MAMMON_CERTIFICATE, GUSTAV_CERTIFICATE) && ((player.getDualClass() != null) && (player.getDualClass().getLevel() >= MIN_DUALCLASS_LEVEL)))
				{
					htmltext = "33907-07.html";
				}
				else
				{
					htmltext = "33907-06.html";
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
