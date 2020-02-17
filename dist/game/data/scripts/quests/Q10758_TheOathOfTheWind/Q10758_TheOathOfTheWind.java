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
package quests.Q10758_TheOathOfTheWind;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10757_QuietingTheStorm.Q10757_QuietingTheStorm;

/**
 * The Oath of the Wind (10758)
 * @author malyelfik
 */
public class Q10758_TheOathOfTheWind extends Quest
{
	// NPC
	private static final int PIO = 33963;
	// Monster
	private static final int WINDIMA = 27522;
	// Misc
	private static final int MIN_LEVEL = 28;
	
	public Q10758_TheOathOfTheWind()
	{
		super(10758);
		addStartNpc(PIO);
		addTalkId(PIO);
		addSpawnId(WINDIMA);
		addKillId(WINDIMA);
		
		addCondRace(Race.ERTHEIA, "33963-00.htm");
		addCondMinLevel(MIN_LEVEL, "33963-00.htm");
		addCondCompletedQuest(Q10757_QuietingTheStorm.class.getSimpleName(), "33963-00.htm");
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
			case "33963-01.htm":
			case "33963-02.htm":
			{
				break;
			}
			case "33963-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "SPAWN":
			{
				if (qs.isCond(1))
				{
					final Npc mob = addSpawn(WINDIMA, -93427, 89595, -3216, 0, true, 180000);
					addAttackPlayerDesire(mob, player);
				}
				htmltext = null;
				break;
			}
			case "33963-06.html":
			{
				if (qs.isCond(2))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 561645, 134);
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
				htmltext = "33963-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33963-04.html" : "33963-05.html";
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
	public String onSpawn(Npc npc)
	{
		npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ARGHH);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			qs.setCond(2, true);
		}
		npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_AM_LOYAL_TO_YOU_MASTER_OF_THE_WINDS_AND_LOYAL_I_SHALL_REMAIN_IF_MY_VERY_SOUL_BETRAYS_ME);
		return super.onKill(npc, killer, isSummon);
	}
}
