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
package quests.Q10337_SakumsImpact;

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
 * Sakum's Impact (10337)
 * @author St3eT
 */
public class Q10337_SakumsImpact extends Quest
{
	// NPCs
	private static final int ADVENTURE_GUILDSMAN = 31795;
	private static final int SILVAN = 33178;
	private static final int LEF = 33510;
	private static final int SKELETON_WARRIOR = 23022;
	private static final int RUIN_IMP = 20506;
	private static final int RUIN_IMP_ELDER = 20507;
	private static final int RUIN_BAT = 23023;
	private static final int SCAVENGER_BAT = 20411;
	// Misc
	private static final int MIN_LEVEL = 28;
	private static final int MAX_LEVEL = 40;
	
	public Q10337_SakumsImpact()
	{
		super(10337);
		addStartNpc(ADVENTURE_GUILDSMAN);
		addTalkId(ADVENTURE_GUILDSMAN, SILVAN, LEF);
		addKillId(SKELETON_WARRIOR, RUIN_IMP, RUIN_IMP_ELDER, SCAVENGER_BAT, RUIN_BAT);
		addCondNotRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "");
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
			case "31795-02.htm":
			case "33178-02.html":
			{
				htmltext = event;
				break;
			}
			case "31795-03.html":
			{
				qs.startQuest();
				qs.setCond(2); // arrow hack
				qs.setCond(1);
				htmltext = event;
				break;
			}
			case "33178-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "33510-04.html":
			{
				if (qs.isCond(3))
				{
					addExpAndSp(player, 1000000, 156);
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
				if (npc.getId() == ADVENTURE_GUILDSMAN)
				{
					htmltext = "31795-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						switch (npc.getId())
						{
							case ADVENTURE_GUILDSMAN:
							{
								htmltext = "31795-04.html";
								break;
							}
							case SILVAN:
							{
								htmltext = "33178-01.html";
								break;
							}
							case LEF:
							{
								htmltext = "33510-01.html";
								break;
							}
						}
						break;
					}
					case 2:
					{
						switch (npc.getId())
						{
							case ADVENTURE_GUILDSMAN:
							{
								htmltext = "31795-04.html";
								break;
							}
							case SILVAN:
							{
								htmltext = "33178-04.html";
								break;
							}
							case LEF:
							{
								htmltext = "33510-02.html";
								break;
							}
						}
						break;
					}
					case 3:
					{
						switch (npc.getId())
						{
							case ADVENTURE_GUILDSMAN:
							{
								htmltext = "31795-04.html";
								break;
							}
							case SILVAN:
							{
								htmltext = "33178-05.html";
								break;
							}
							case LEF:
							{
								htmltext = "33510-03.html";
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
					case ADVENTURE_GUILDSMAN:
					{
						htmltext = "31795-05.html";
						break;
					}
					case SILVAN:
					{
						htmltext = "33178-06.html";
						break;
					}
					case LEF:
					{
						htmltext = "33510-05.html";
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
			int killedWarriors = qs.getInt("killed_" + SKELETON_WARRIOR);
			int killedImps = qs.getInt("killed_" + RUIN_IMP);
			int killedBats = qs.getInt("killed_" + SCAVENGER_BAT);
			
			switch (npc.getId())
			{
				case SKELETON_WARRIOR:
				{
					if (killedWarriors < 15)
					{
						killedWarriors++;
						qs.set("killed_" + SKELETON_WARRIOR, killedWarriors);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case RUIN_IMP:
				case RUIN_IMP_ELDER:
				{
					if (killedImps < 20)
					{
						killedImps++;
						qs.set("killed_" + RUIN_IMP, killedImps);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case RUIN_BAT:
				case SCAVENGER_BAT:
				{
					if (killedBats < 25)
					{
						killedBats++;
						qs.set("killed_" + SCAVENGER_BAT, killedBats);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((killedWarriors == 15) && (killedImps == 20) && (killedBats == 25))
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
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(3);
			npcLogList.add(new NpcLogListHolder(SKELETON_WARRIOR, false, qs.getInt("killed_" + SKELETON_WARRIOR)));
			npcLogList.add(new NpcLogListHolder(RUIN_IMP, false, qs.getInt("killed_" + RUIN_IMP)));
			npcLogList.add(new NpcLogListHolder(27458, false, qs.getInt("killed_" + SCAVENGER_BAT))); // NOTE: Somehow quest log react on bad ID, maybe client bug
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}