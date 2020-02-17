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
package quests.Q10851_ElvenBotany;

import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Elven Botany (10851)
 * @URL https://l2wiki.com/Elven_Botany
 * @author Gigi
 * @date 2019-06-11 - [21:21:45]
 */
public class Q10851_ElvenBotany extends Quest
{
	// NPCs
	private static final int CELESTIEL = 34234;
	private static final int IRENE = 34233;
	// Misc
	private static final int MIN_LEVEL = 102;
	// Monsters
	private static final int FLOWER_BUD = 19600;
	private static final int APHERUS = 23581;
	// Items
	private static final int APHERUS_SAMPLE = 47200;
	private static final int FLOWER_BUD_SAMPLE = 47201;
	private static final int BASIC_SUPPLY_BOX = 47178;
	
	public Q10851_ElvenBotany()
	{
		super(10851);
		addStartNpc(CELESTIEL);
		addTalkId(CELESTIEL, IRENE);
		addKillId(FLOWER_BUD, APHERUS);
		registerQuestItems(APHERUS_SAMPLE, FLOWER_BUD_SAMPLE);
		addCondMinLevel(MIN_LEVEL, "guardian_follower_q10851_02.htm");
		addFactionLevel(Faction.MOTHER_TREE_GUARDIANS, 2, "guardian_follower_q10851_03.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "guardian_follower_q10851_04.htm":
			case "guardian_follower_q10851_05.htm":
			{
				htmltext = event;
				break;
			}
			case "guardian_follower_q10851_06.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "guardian_leader_q10851_02.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "guardian_leader_q10851_05.html":
			{
				if (qs.isCond(3) && (player.getLevel() >= MIN_LEVEL))
				{
					giveItems(player, BASIC_SUPPLY_BOX, 1);
					addExpAndSp(player, 44_442_855_900L, 44_442_720);
					qs.exitQuest(QuestType.ONE_TIME, true);
					htmltext = event;
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
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
				htmltext = "guardian_follower_q10851_04.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case CELESTIEL:
					{
						if (qs.getCond() > 0)
						{
							htmltext = "guardian_follower_q10851_07.html";
						}
						break;
					}
					case IRENE:
					{
						if (qs.isCond(1))
						{
							htmltext = "guardian_leader_q10851_01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "guardian_leader_q10851_03.html";
						}
						else
						{
							htmltext = "guardian_leader_q10851_04.html";
						}
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
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(2))
		{
			switch (npc.getId())
			{
				case FLOWER_BUD:
				{
					if (getQuestItemsCount(killer, FLOWER_BUD_SAMPLE) < 50)
					{
						giveItems(killer, FLOWER_BUD_SAMPLE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case APHERUS:
				{
					if (getQuestItemsCount(killer, APHERUS_SAMPLE) < 150)
					{
						giveItems(killer, APHERUS_SAMPLE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			if ((getQuestItemsCount(killer, FLOWER_BUD_SAMPLE) >= 50) && (getQuestItemsCount(killer, APHERUS_SAMPLE) >= 150))
			{
				qs.setCond(3, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
