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
package quests.Q10305_UnstoppableFutileEfforts;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10302_UnsettlingShadowAndRumors.Q10302_UnsettlingShadowAndRumors;

/**
 * Unstoppable Futile Efforts (10305)
 * @author Gladicek
 */
public class Q10305_UnstoppableFutileEfforts extends Quest
{
	// NPCs
	private static final int NOETI_MIMILEAD = 32895;
	private static final int LARGE_COCOON = 32920;
	private static final int COCOON = 32919;
	// Misc
	private static final int MIN_LEVEL = 88;
	
	public Q10305_UnstoppableFutileEfforts()
	{
		super(10305);
		addStartNpc(NOETI_MIMILEAD);
		addTalkId(NOETI_MIMILEAD);
		
		addCondMinLevel(MIN_LEVEL, "32895-06.htm");
		addCondCompletedQuest(Q10302_UnsettlingShadowAndRumors.class.getSimpleName(), "32895-06.htm");
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
			case "32895-02.htm":
			case "32895-03.htm":
			case "32895-04.htm":
			{
				htmltext = event;
				break;
			}
			case "32895-05.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "NOTIFY_Q10305":
			{
				int killedCocoon = qs.getMemoStateEx(LARGE_COCOON);
				
				killedCocoon++;
				if (killedCocoon < 5)
				{
					qs.setMemoStateEx(LARGE_COCOON, killedCocoon);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					qs.setCond(2, true);
				}
				break;
			}
			default:
			{
				if (event.startsWith("giveReward_") && qs.isCond(2) && (player.getLevel() >= MIN_LEVEL))
				{
					final int itemId = Integer.parseInt(event.replace("giveReward_", ""));
					giveAdena(player, 1_007_735, false);
					giveItems(player, itemId, 15);
					addExpAndSp(player, 34_971_975, 8_393);
					qs.exitQuest(false, true);
					htmltext = "32895-09.html";
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
				}
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
				htmltext = "32895-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "32895-07.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "32895-08.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "32895-10.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			npcLogList.add(new NpcLogListHolder(COCOON, false, qs.getMemoStateEx(LARGE_COCOON)));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}