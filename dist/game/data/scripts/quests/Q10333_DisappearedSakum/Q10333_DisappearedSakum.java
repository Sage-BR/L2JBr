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
package quests.Q10333_DisappearedSakum;

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
 * Disappeared Sakum (10333)
 * @author St3eT
 */
public class Q10333_DisappearedSakum extends Quest
{
	// NPCs
	private static final int BATHIS = 30332;
	private static final int VENT = 33176;
	private static final int SCHUNAIN = 33508;
	private static final int LIZARDMEN = 20030;
	private static final int VAKU_ORC = 20017;
	private static final int[] SPIDERS =
	{
		23094, // Poisonous Spider
		23021, // Giant Venomous Spider
		23095, // Archnid Predator
	};
	// Items
	private static final int BADGE = 17583;
	// Misc
	private static final int MIN_LEVEL = 18;
	private static final int MAX_LEVEL = 40;
	
	public Q10333_DisappearedSakum()
	{
		super(10333);
		addStartNpc(BATHIS);
		addTalkId(BATHIS, VENT, SCHUNAIN);
		addKillId(LIZARDMEN, VAKU_ORC);
		addKillId(SPIDERS);
		registerQuestItems(BADGE);
		addCondNotRace(Race.ERTHEIA, "30332-09.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "30332-10.html");
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
			case "30332-03.htm":
			case "30332-04.htm":
			case "33176-02.html":
			case "33508-02.html":
			{
				htmltext = event;
				break;
			}
			case "30332-05.html":
			{
				qs.startQuest();
				qs.setCond(2); // arrow hack
				qs.setCond(1);
				htmltext = event;
				break;
			}
			case "33176-03.html":
			{
				if (qs.isCond(1))
				{
					htmltext = event;
					qs.setCond(2, true);
				}
				break;
			}
			case "33508-03.html":
			{
				if (qs.isCond(3))
				{
					addExpAndSp(player, 180000, 43);
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
						htmltext = qs.isCond(0) ? "30332-06.html" : "30332-07.html";
						break;
					}
					case VENT:
					{
						if (qs.isCond(1))
						{
							htmltext = "33176-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "33176-04.html";
						}
						else if (qs.isCond(3))
						{
							htmltext = "33176-05.html";
						}
						break;
					}
					case SCHUNAIN:
					{
						if (qs.isCond(3))
						{
							htmltext = "33508-01.html";
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
						htmltext = "30332-08.html";
						break;
					}
					case VENT:
					{
						htmltext = "33176-06.html";
						break;
					}
					case SCHUNAIN:
					{
						htmltext = "33508-04.html";
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
			int killedLizardmen = qs.getInt("killed_" + LIZARDMEN);
			int killedVakuOrc = qs.getInt("killed_" + VAKU_ORC);
			
			switch (npc.getId())
			{
				case LIZARDMEN:
				{
					if (killedLizardmen < 7)
					{
						killedLizardmen++;
						qs.set("killed_" + LIZARDMEN, killedLizardmen);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case VAKU_ORC:
				{
					if (killedVakuOrc < 5)
					{
						killedVakuOrc++;
						qs.set("killed_" + VAKU_ORC, killedVakuOrc);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				default:
				{
					if ((getQuestItemsCount(killer, BADGE) < 5) && getRandomBoolean())
					{
						giveItems(killer, BADGE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((getQuestItemsCount(killer, BADGE) == 5) && (killedLizardmen == 7) && (killedVakuOrc == 5))
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
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(2);
			npcLogList.add(new NpcLogListHolder(LIZARDMEN, false, qs.getInt("killed_" + LIZARDMEN)));
			npcLogList.add(new NpcLogListHolder(VAKU_ORC, false, qs.getInt("killed_" + VAKU_ORC)));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}