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
package quests.Q10394_MutualBenefit;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Mutual Benefit (10394)
 * @author St3eT
 */
public class Q10394_MutualBenefit extends Quest
{
	// NPCs
	private static final int KELIOS = 33862;
	private static final int GARGOYLE = 20241; // Hunter Gargoyle
	private static final int BASILISK = 20573; // Tarlk Basilisk
	private static final int ELDER_BASILISK = 20574; // Elder Tarlk Basilisk
	// Misc
	private static final int MIN_LEVEL = 46;
	private static final int MAX_LEVEL = 52;
	
	public Q10394_MutualBenefit()
	{
		super(10394);
		addStartNpc(KELIOS);
		addTalkId(KELIOS);
		addKillId(GARGOYLE, BASILISK, ELDER_BASILISK);
		addCondNotRace(Race.ERTHEIA, "33862-09.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33862-08.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33862-02.htm":
			case "33862-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33862-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33862-07.html":
			{
				if (qs.isCond(2))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(npc, player);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 6758129, 756);
					}
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
				htmltext = "33862-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33862-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33862-06.html";
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
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isStarted() && qs.isCond(1))
		{
			int killedGargoyle = qs.getInt("killed_" + GARGOYLE);
			int killedBasilisk = qs.getInt("killed_" + BASILISK);
			int killedElderBasilisk = qs.getInt("killed_" + ELDER_BASILISK);
			
			switch (npc.getId())
			{
				case GARGOYLE:
				{
					if (killedGargoyle < 30)
					{
						killedGargoyle++;
						qs.set("killed_" + GARGOYLE, killedGargoyle);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case BASILISK:
				{
					if (killedBasilisk < 40)
					{
						killedBasilisk++;
						qs.set("killed_" + BASILISK, killedBasilisk);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case ELDER_BASILISK:
				{
					if (killedElderBasilisk < 40)
					{
						killedElderBasilisk++;
						qs.set("killed_" + ELDER_BASILISK, killedElderBasilisk);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((killedGargoyle == 30) && (killedBasilisk == 40) && (killedElderBasilisk == 40))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isStarted() && qs.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(3);
			npcLogList.add(new NpcLogListHolder(GARGOYLE, false, qs.getInt("killed_" + GARGOYLE)));
			npcLogList.add(new NpcLogListHolder(BASILISK, false, qs.getInt("killed_" + BASILISK)));
			npcLogList.add(new NpcLogListHolder(ELDER_BASILISK, false, qs.getInt("killed_" + ELDER_BASILISK)));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}