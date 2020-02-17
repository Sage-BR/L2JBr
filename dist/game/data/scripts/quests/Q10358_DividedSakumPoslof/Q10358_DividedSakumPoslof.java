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
package quests.Q10358_DividedSakumPoslof;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10337_SakumsImpact.Q10337_SakumsImpact;

/**
 * Divided Sakum, Poslof (10358)
 * @author St3eT
 */
public class Q10358_DividedSakumPoslof extends Quest
{
	// NPCs
	private static final int LEF = 33510;
	private static final int ADVENTURER_GUIDE = 31795;
	private static final int ZOMBIE_WARRIOR = 20458;
	private static final int VEELEAN = 20402; // Veelan Bugbear Warrior
	private static final int POSLOF = 27452;
	// Items
	private static final int SAKUM_SKETCH = 17585;
	// Misc
	private static final int MIN_LEVEL = 33;
	private static final int MAX_LEVEL = 40;
	
	public Q10358_DividedSakumPoslof()
	{
		super(10358);
		addStartNpc(LEF);
		addTalkId(LEF, ADVENTURER_GUIDE);
		addKillId(ZOMBIE_WARRIOR, VEELEAN, POSLOF);
		registerQuestItems(SAKUM_SKETCH);
		addCondCompletedQuest(Q10337_SakumsImpact.class.getSimpleName(), "33510-09.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33510-09.html");
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
			case "33510-02.htm":
			case "31795-04.html":
			{
				htmltext = event;
				break;
			}
			case "33510-03.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31795-05.html":
			{
				if (qs.isCond(4))
				{
					addExpAndSp(player, 750000, 180);
					qs.exitQuest(false, true);
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
				htmltext = npc.getId() == LEF ? "33510-01.htm" : "31795-02.html";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = npc.getId() == LEF ? "33510-04.html" : "31795-01.html";
						break;
					}
					case 2:
					{
						if (npc.getId() == LEF)
						{
							qs.setCond(3);
							giveItems(player, SAKUM_SKETCH, 1);
							htmltext = "33510-05.html";
						}
						else if (npc.getId() == ADVENTURER_GUIDE)
						{
							htmltext = "31795-01.html";
						}
						break;
					}
					case 3:
					{
						htmltext = npc.getId() == LEF ? "33510-06.html" : "31795-01.html";
						break;
					}
					case 4:
					{
						htmltext = npc.getId() == LEF ? "33510-07.html" : "31795-03.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = npc.getId() == LEF ? "33510-08.html" : "31795-06.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isStarted())
		{
			if (qs.isCond(1))
			{
				int killedZombies = qs.getInt("killed_" + ZOMBIE_WARRIOR);
				int killedVeelans = qs.getInt("killed_" + VEELEAN);
				
				if (npc.getId() == ZOMBIE_WARRIOR)
				{
					if (killedZombies < 20)
					{
						killedZombies++;
						qs.set("killed_" + ZOMBIE_WARRIOR, killedZombies);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				else if (killedVeelans < 23)
				{
					killedVeelans++;
					qs.set("killed_" + VEELEAN, killedVeelans);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				
				if ((killedZombies == 20) && (killedVeelans == 23))
				{
					qs.setCond(2, true);
				}
			}
			else if (qs.isCond(3))
			{
				qs.set("killed_" + POSLOF, 1);
				qs.setCond(4);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isStarted())
		{
			if (qs.isCond(1))
			{
				final Set<NpcLogListHolder> npcLogList = new HashSet<>(2);
				npcLogList.add(new NpcLogListHolder(ZOMBIE_WARRIOR, false, qs.getInt("killed_" + ZOMBIE_WARRIOR)));
				npcLogList.add(new NpcLogListHolder(VEELEAN, false, qs.getInt("killed_" + VEELEAN)));
				return npcLogList;
			}
			else if (qs.isCond(3))
			{
				final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
				npcLogList.add(new NpcLogListHolder(POSLOF, false, qs.getInt("killed_" + POSLOF)));
				return npcLogList;
			}
		}
		return super.getNpcLogList(player);
	}
}