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
package quests.Q10335_RequestToFindSakum;

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
 * Request To Find Sakum (10335)
 * @author St3eT
 */
public class Q10335_RequestToFindSakum extends Quest
{
	// NPCs
	private static final int BATHIS = 30332;
	private static final int KALLESIN = 33177;
	private static final int ZENATH = 33509;
	private static final int SKELETON_TRACKER = 20035;
	private static final int SKELETON_BOWMAN = 20051;
	private static final int RUIN_SPARTOI = 20054;
	private static final int RUIN_ZOMBIE = 20026;
	private static final int RUIN_ZOMBIE_LEADER = 20029;
	// Misc
	private static final int MIN_LEVEL = 23;
	private static final int MAX_LEVEL = 40;
	
	public Q10335_RequestToFindSakum()
	{
		super(10335);
		addStartNpc(BATHIS);
		addTalkId(BATHIS, KALLESIN, ZENATH);
		addKillId(SKELETON_TRACKER, SKELETON_BOWMAN, RUIN_SPARTOI, RUIN_ZOMBIE, RUIN_ZOMBIE_LEADER);
		addCondNotRace(Race.ERTHEIA, "30332-08.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "30332-07.html");
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
			case "30332-02.htm":
			case "33509-03.html":
			{
				htmltext = event;
				break;
			}
			case "30332-03.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33177-02.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "33509-04.html":
			{
				if (qs.isCond(3))
				{
					addExpAndSp(player, 350000, 84);
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
				if (npc.getId() == BATHIS)
				{
					htmltext = "30332-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case BATHIS:
					{
						htmltext = qs.isCond(1) ? "30332-04.html" : "30332-05.html";
						break;
					}
					case KALLESIN:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "33177-01.html";
								break;
							}
							case 2:
							{
								htmltext = "33177-03.html";
								break;
							}
							case 3:
							{
								htmltext = "33177-04.html";
								break;
							}
						}
						break;
					}
					case ZENATH:
					{
						switch (qs.getCond())
						{
							case 1:
							case 2:
							{
								htmltext = "33509-01.html";
								break;
							}
							case 3:
							{
								htmltext = "33509-02.html";
								break;
							}
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				switch (npc.getId())
				{
					case BATHIS:
					{
						htmltext = "30332-06.html";
						break;
					}
					case KALLESIN:
					{
						htmltext = "33177-05.html";
						break;
					}
					case ZENATH:
					{
						htmltext = "33509-05.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isStarted() && qs.isCond(2))
		{
			int killedTracker = qs.getInt("killed_" + SKELETON_TRACKER);
			int killedBowman = qs.getInt("killed_" + SKELETON_BOWMAN);
			int killedRuinSpartois = qs.getInt("killed_" + RUIN_SPARTOI);
			int killedZombie = qs.getInt("killed_" + RUIN_ZOMBIE);
			
			switch (npc.getId())
			{
				case SKELETON_TRACKER:
				{
					if (killedTracker < 10)
					{
						killedTracker++;
						qs.set("killed_" + SKELETON_TRACKER, killedTracker);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SKELETON_BOWMAN:
				{
					if (killedBowman < 10)
					{
						killedBowman++;
						qs.set("killed_" + SKELETON_BOWMAN, killedBowman);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case RUIN_SPARTOI:
				{
					if (killedRuinSpartois < 15)
					{
						killedRuinSpartois++;
						qs.set("killed_" + RUIN_SPARTOI, killedRuinSpartois);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case RUIN_ZOMBIE:
				case RUIN_ZOMBIE_LEADER:
				{
					if (killedZombie < 15)
					{
						killedZombie++;
						qs.set("killed_" + RUIN_ZOMBIE, killedZombie);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((killedTracker == 10) && (killedBowman == 10) && (killedRuinSpartois == 15) && (killedZombie == 15))
			{
				qs.setCond(3, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isStarted() && qs.isCond(2))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(4);
			npcLogList.add(new NpcLogListHolder(SKELETON_TRACKER, false, qs.getInt("killed_" + SKELETON_TRACKER)));
			npcLogList.add(new NpcLogListHolder(SKELETON_BOWMAN, false, qs.getInt("killed_" + SKELETON_BOWMAN)));
			npcLogList.add(new NpcLogListHolder(RUIN_SPARTOI, false, qs.getInt("killed_" + RUIN_SPARTOI)));
			npcLogList.add(new NpcLogListHolder(RUIN_ZOMBIE, false, qs.getInt("killed_" + RUIN_ZOMBIE)));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}