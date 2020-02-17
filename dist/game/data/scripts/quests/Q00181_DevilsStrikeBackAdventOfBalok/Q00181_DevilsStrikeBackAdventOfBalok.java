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
package quests.Q00181_DevilsStrikeBackAdventOfBalok;

import org.l2jbr.Config;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.util.Util;

/**
 * @author hlwrave
 * @URL https://l2wiki.com/Devils_Strike_Back,_Advent_of_Balok
 */
public class Q00181_DevilsStrikeBackAdventOfBalok extends Quest
{
	// NPC
	private static final int FIOREN = 33044;
	// Monster
	private static final int BALOK = 29218;
	// Items
	private static final int CONTRACT = 17592;
	private static final int EAR = 17527;
	private static final int EWR = 17526;
	private static final int POUCH = 34861;
	// Misc
	private static final int MIN_LEVEL = 97;
	
	public Q00181_DevilsStrikeBackAdventOfBalok()
	{
		super(181);
		addStartNpc(FIOREN);
		addTalkId(FIOREN);
		addKillId(BALOK);
		registerQuestItems(CONTRACT);
		addCondMinLevel(MIN_LEVEL, "33044-02.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "33044-06.html":
			{
				qs.startQuest();
				break;
			}
			case "reward":
			{
				addExpAndSp(player, 886750000, 414855000);
				giveAdena(player, 37128000, true);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				qs.exitQuest(QuestType.ONE_TIME, true);
				switch (Rnd.get(3))
				{
					case 0:
					{
						giveItems(player, EWR, 2);
						return "33044-09.html";
					}
					case 1:
					{
						giveItems(player, EAR, 2);
						return "33044-10.html";
					}
					case 2:
					{
						giveItems(player, POUCH, 2);
						return "33044-11.html";
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
		
		switch (npc.getId())
		{
			case FIOREN:
			{
				if (qs.isCreated())
				{
					htmltext = "33044-01.htm";
				}
				else if (qs.isStarted())
				{
					if (qs.isCond(1))
					{
						htmltext = "33044-07.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33044-08.html";
					}
				}
				else if (qs.isCompleted())
				{
					htmltext = "33044-03.html";
				}
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
			giveItems(player, CONTRACT, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			qs.setCond(2, true);
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
}
