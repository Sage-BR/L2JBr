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
package quests.Q10456_OperationRescue;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10455_ElikiasLetter.Q10455_ElikiasLetter;

/**
 * Operation: Rescue (10456)
 * @URL https://l2wiki.com/Operation:_Rescue
 * @author Gigi
 */
public class Q10456_OperationRescue extends Quest
{
	// NPCs
	private static final int DEVIANNE = 31590;
	private static final int[] MONSTERS =
	{
		23354, // Decay Hannibal
		23355, // Armor Beast
		23356, // Klein Soldier
		23357, // Disorder Warrior
		23358, // Blow Archer
		23360, // Bizuard
		23361, // Mutated Fly
		23362, // Amos Soldier
		23363, // Amos Officer
		23364, // Amos Master
		23365 // Ailith Hunter
	};
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10456_OperationRescue()
	{
		super(10456);
		addStartNpc(DEVIANNE);
		addTalkId(DEVIANNE);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "31590-00.htm");
		addFactionLevel(Faction.BLACKBIRD_CLAN, 2, "31590-05.html");
		addCondCompletedQuest(Q10455_ElikiasLetter.class.getSimpleName(), "31590-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		switch (event)
		{
			case "31590-02.htm":
			case "31590-03.htm":
			{
				htmltext = event;
				break;
			}
			case "31590-04.htm":
			{
				qs.startQuest();
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_HOPE_THEY_ARE_SAFE);
				htmltext = event;
				break;
			}
			case "31590-08.html":
			{
				if (qs.isCond(2) && (player.getLevel() >= MIN_LEVEL))
				{
					addExpAndSp(player, 1_507_456_500, 3_617_880);
					giveAdena(player, 659_250, false);
					qs.exitQuest(QuestType.ONE_TIME, true);
					htmltext = event;
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
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
				htmltext = "31590-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "31590-06.html" : "31590-07.html";
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
		if ((qs != null) && qs.isCond(1) && (npc.getTitleString() == NpcStringId.ABNORMAL_MAGIC_CIRCLE))
		{
			if (getRandom(100) < 5)
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
