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
package quests.Q00036_MakeASewingKit;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Make a Sewing Kit (36)
 * @author malyelfik
 */
public class Q00036_MakeASewingKit extends Quest
{
	// NPC
	private static final int FERRIS = 30847;
	// Monster
	private static final int REINFORCED_IRON_GOLEM = 20566;
	// Items
	private static final int IRON_ORE = 36521;
	private static final int COKES = 36561;
	private static final int SEWING_KIT = 7078;
	private static final int REINFORCED_IRON = 7163;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int IRON_COUNT = 5;
	private static final int COUNT = 10;
	
	public Q00036_MakeASewingKit()
	{
		super(36);
		addStartNpc(FERRIS);
		addTalkId(FERRIS);
		addKillId(REINFORCED_IRON_GOLEM);
		registerQuestItems(REINFORCED_IRON);
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
			case "30847-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "30847-06.html":
			{
				if (getQuestItemsCount(player, REINFORCED_IRON) < IRON_COUNT)
				{
					return getNoQuestMsg(player);
				}
				takeItems(player, REINFORCED_IRON, -1);
				qs.setCond(3, true);
				break;
			}
			case "30847-09.html":
			{
				if ((getQuestItemsCount(player, IRON_ORE) >= COUNT) && (getQuestItemsCount(player, COKES) >= COUNT))
				{
					takeItems(player, IRON_ORE, 180);
					takeItems(player, COKES, 360);
					giveItems(player, SEWING_KIT, 1);
					qs.exitQuest(false, true);
				}
				else
				{
					htmltext = "30847-10.html";
				}
				break;
			}
			default:
			{
				htmltext = null;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final PlayerInstance member = getRandomPartyMember(player, 1);
		if ((member != null) && getRandomBoolean())
		{
			giveItems(player, REINFORCED_IRON, 1);
			if (getQuestItemsCount(player, REINFORCED_IRON) >= IRON_COUNT)
			{
				getQuestState(member, false).setCond(2, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
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
				htmltext = (player.getLevel() >= MIN_LEVEL) ? "30847-01.htm" : "30847-02.html";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "30847-04.html";
						break;
					}
					case 2:
					{
						htmltext = "30847-05.html";
						break;
					}
					case 3:
					{
						htmltext = ((getQuestItemsCount(player, IRON_ORE) >= COUNT) && (getQuestItemsCount(player, COKES) >= COUNT)) ? "30847-07.html" : "30847-08.html";
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
}