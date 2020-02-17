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
package quests.Q10735_ASpecialPower;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10734_DoOrDie.Q10734_DoOrDie;

/**
 * A Special Power (10735)<br>
 * Instance part is implemented in <b>FaeronTrainingGrounds1</b> script.
 * @author Sdw
 */
public class Q10735_ASpecialPower extends Quest
{
	// NPC
	private static final int AYANTHE = 33942;
	// Monsters
	private static final int FLOATO = 27526;
	private static final int FLOATO2 = 27531;
	private static final int RATEL = 27527;
	// Reward
	private static final ItemHolder SPIRITSHOTS_REWARD = new ItemHolder(2509, 500);
	// Misc
	private static final int MIN_LEVEL = 4;
	private static final int MAX_LEVEL = 20;
	public static final int KILL_COUNT_VAR = 0;
	
	public Q10735_ASpecialPower()
	{
		super(10735);
		addStartNpc(AYANTHE);
		addTalkId(AYANTHE);
		
		addCondRace(Race.ERTHEIA, "");
		addCondClassId(ClassId.ERTHEIA_WIZARD, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33942-00.htm");
		addCondCompletedQuest(Q10734_DoOrDie.class.getSimpleName(), "33942-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && event.equals("33942-02.htm"))
		{
			qs.startQuest();
			return event;
		}
		return null;
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
				htmltext = "33942-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "33942-03.html";
						break;
					}
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					{
						htmltext = "33942-04.html";
						break;
					}
					case 7:
					{
						giveAdena(player, 900, true);
						rewardItems(player, SPIRITSHOTS_REWARD);
						addExpAndSp(player, 3154, 0);
						qs.exitQuest(false, true);
						htmltext = "33942-05.html";
						break;
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
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final Set<NpcLogListHolder> holder = new HashSet<>();
		final QuestState qs = getQuestState(player, false);
		if (qs != null)
		{
			int npcId = -1;
			switch (qs.getCond())
			{
				case 2:
				{
					npcId = FLOATO;
					break;
				}
				case 4:
				{
					npcId = FLOATO2;
					break;
				}
				case 6:
				{
					npcId = RATEL;
					break;
				}
			}
			if (npcId != -1)
			{
				holder.add(new NpcLogListHolder(npcId, false, qs.getMemoStateEx(KILL_COUNT_VAR)));
			}
		}
		return holder;
	}
}