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
package quests.Q00149_PrimalMotherIstina;

import org.l2jbr.Config;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Primal Mother, Istina (149)
 * @URL https://l2wiki.com/Primal_Mother,_Istina
 * @author Gigi
 */
public class Q00149_PrimalMotherIstina extends Quest
{
	// NPCs
	private static final int RUMIESE = 33293;
	private static final int ISTHINA_NORMAL = 29195;
	// Item
	private static final int SHILENS_MARK = 17589;
	private static final int ISTHINA_BRACELET = 19455;
	private static final int EAR = 17527;
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q00149_PrimalMotherIstina()
	{
		super(149);
		addStartNpc(RUMIESE);
		addTalkId(RUMIESE);
		addKillId(ISTHINA_NORMAL);
		addCondMinLevel(MIN_LEVEL, "33293-00.htm");
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
			case "33293-02.htm":
			case "33293-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33293-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward_9546":
			case "reward_9547":
			case "reward_9548":
			case "reward_9549":
			case "reward_9550":
			case "reward_9551":
			{
				if (qs.isCond(2) && (getQuestItemsCount(player, SHILENS_MARK) >= 1))
				{
					final int stoneId = Integer.parseInt(event.replaceAll("reward_", ""));
					takeItems(player, SHILENS_MARK, 1);
					addExpAndSp(player, 833065000, 199935);
					giveItems(player, ISTHINA_BRACELET, 1);
					giveItems(player, EAR, 10);
					giveItems(player, stoneId, 15);
					qs.exitQuest(false, true);
				}
				htmltext = "33293-07.html";
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
				if (npc.getId() == RUMIESE)
				{
					htmltext = "33293-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33293-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33293-06.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "Complete.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isStarted() && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
		{
			giveItems(player, SHILENS_MARK, 1);
			qs.setCond(2, true);
		}
	}
}