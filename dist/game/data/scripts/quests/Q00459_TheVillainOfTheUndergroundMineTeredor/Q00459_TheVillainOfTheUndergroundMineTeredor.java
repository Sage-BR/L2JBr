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
package quests.Q00459_TheVillainOfTheUndergroundMineTeredor;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.util.Util;

/**
 * The Villain of the Underground Mine, Teredor (459)
 * @URL https://l2wiki.com/The_Villain_of_the_Underground_Mine,_Teredor
 * @VIDEO http://www.dailymotion.com/video/x4hvrk2_quest-the-villain-of-the-underground-mine-teredor-infinity-odyssey_videogames
 * @author Gigi
 */
public class Q00459_TheVillainOfTheUndergroundMineTeredor extends Quest
{
	// NPCs
	private static final int FILAUR = 30535;
	// Monster
	private static final int TEREDOR = 25785;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int PROOF_OF_FIDELITY = 19450;
	
	public Q00459_TheVillainOfTheUndergroundMineTeredor()
	{
		super(459);
		addStartNpc(FILAUR);
		addTalkId(FILAUR);
		addKillId(TEREDOR);
		addCondMinLevel(MIN_LEVEL, "30535-00.html");
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
			case "30535-02.htm":
			case "30535-03.htm":
			case "30535-04.htm":
			{
				htmltext = event;
				break;
			}
			case "30535-05.htm":
			{
				qs.startQuest();
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
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "30535-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "30535-05.htm";
						break;
					}
					case 2:
					{
						giveItems(player, PROOF_OF_FIDELITY, 6);
						qs.exitQuest(QuestType.DAILY, true);
						htmltext = "30535-07.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = "Complete.html";
				}
				qs.setState(State.CREATED);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false))
		{
			qs.setCond(2, true);
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, true);
		return super.onKill(npc, killer, isSummon);
	}
}
