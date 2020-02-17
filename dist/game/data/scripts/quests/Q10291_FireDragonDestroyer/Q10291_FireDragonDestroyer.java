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
package quests.Q10291_FireDragonDestroyer;

import java.util.function.Function;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.util.Util;

/**
 * Fire Dragon Destroyer (10291)
 * @author malyelfik
 */
public class Q10291_FireDragonDestroyer extends Quest
{
	// NPC
	private static final int KLEIN = 31540;
	// Monster
	private static final int VALAKAS = 29028;
	// Items
	private static final int FLOATING_STONE = 7267;
	private static final int POOR_NECKLACE = 15524;
	private static final int VALOR_NECKLACE = 15525;
	
	private static final int VALAKAS_SLAYER_CIRCLET = 8567;
	
	public Q10291_FireDragonDestroyer()
	{
		super(10291);
		addStartNpc(KLEIN);
		addTalkId(KLEIN);
		addKillId(VALAKAS);
		registerQuestItems(POOR_NECKLACE, VALOR_NECKLACE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		if (event.equals("31540-05.htm"))
		{
			qs.startQuest();
			giveItems(player, POOR_NECKLACE, 1);
		}
		
		return event;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		if (!player.isInParty())
		{
			return super.onKill(npc, player, isSummon);
		}
		
		final Function<PlayerInstance, Boolean> rewardCheck = p ->
		{
			if (Util.checkIfInRange(8000, npc, p, false))
			{
				final QuestState qs = getQuestState(p, false);
				
				if ((qs != null) && qs.isCond(1) && hasQuestItems(p, POOR_NECKLACE))
				{
					takeItems(p, POOR_NECKLACE, -1);
					giveItems(p, VALOR_NECKLACE, 1);
					qs.setCond(2, true);
				}
			}
			return true;
		};
		
		// Rewards go only to command channel, not to a single party or player.
		if (player.getParty().isInCommandChannel())
		{
			player.getParty().getCommandChannel().forEachMember(rewardCheck);
		}
		else
		{
			player.getParty().forEachMember(rewardCheck);
		}
		return super.onKill(npc, player, isSummon);
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
				if (player.getLevel() < 85)
				{
					htmltext = "31540-00.htm";
				}
				else
				{
					htmltext = hasQuestItems(player, FLOATING_STONE) ? "31540-02.htm" : "31540-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					if (hasQuestItems(player, POOR_NECKLACE))
					{
						htmltext = "31540-06.html";
					}
					else
					{
						giveItems(player, POOR_NECKLACE, 1);
						htmltext = "31540-07.html";
					}
				}
				else if (qs.isCond(2) && hasQuestItems(player, VALOR_NECKLACE))
				{
					htmltext = "31540-08.html";
					giveAdena(player, 126549, true);
					addExpAndSp(player, 717291, 172);
					giveItems(player, VALAKAS_SLAYER_CIRCLET, 1);
					qs.exitQuest(false, true);
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "31540-09.html";
				break;
			}
		}
		
		return htmltext;
	}
}
