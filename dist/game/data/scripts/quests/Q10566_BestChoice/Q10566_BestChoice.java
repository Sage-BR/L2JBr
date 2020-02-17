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
package quests.Q10566_BestChoice;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Best Choice (10566)
 * @URL https://l2wiki.com/Best_Choice
 * @author Werum / NightBR
 */
public class Q10566_BestChoice extends Quest
{
	// NPC
	private static final int HERPHAH = 34362;
	// Misc
	private static final int MIN_LEVEL = 95;
	// Items
	private static final int CERTIFICATE_SANTIAGO = 48173;
	private static final int CERTIFICATE_RUPIO = 48174;
	private static final int CERTIFICATE_FLUTTER = 48175;
	private static final int CERTIFICATE_VINCENZ = 48176;
	private static final int CERTIFICATE_FERRIS = 48177;
	private static final int HERPHAHS_MISSION_LIST = 48172;
	// Rewards
	private static final int HERPHAHS_SUPPORT_BOX = 48250;
	
	public Q10566_BestChoice()
	{
		super(10566);
		addStartNpc(HERPHAH);
		addTalkId(HERPHAH);
		addCondMinLevel(MIN_LEVEL, "noLevel.html");
		registerQuestItems(CERTIFICATE_SANTIAGO, CERTIFICATE_RUPIO, CERTIFICATE_FLUTTER, CERTIFICATE_VINCENZ, CERTIFICATE_FERRIS, HERPHAHS_MISSION_LIST);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		String htmltext = null;
		switch (event)
		{
			case "34362-02.htm":
			case "34362-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34362-04.htm":
			{
				qs.startQuest();
				giveItems(player, HERPHAHS_MISSION_LIST, 1);
				break;
			}
			case "34362-07.html":
			{
				giveItems(player, HERPHAHS_SUPPORT_BOX, 1);
				qs.exitQuest(QuestType.ONE_TIME, true);
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
				htmltext = (player.hasPremiumStatus()) ? "34362-01.htm" : "34362-99.html";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					// Check if player has the necessary quest items to complete the quest
					if (hasQuestItems(player, CERTIFICATE_SANTIAGO, CERTIFICATE_RUPIO, CERTIFICATE_FLUTTER, CERTIFICATE_VINCENZ, CERTIFICATE_FERRIS))
					{
						qs.setCond(2, true);
						htmltext = "34362-06.html";
					}
					else
					{
						htmltext = "34362-05.html";
					}
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
