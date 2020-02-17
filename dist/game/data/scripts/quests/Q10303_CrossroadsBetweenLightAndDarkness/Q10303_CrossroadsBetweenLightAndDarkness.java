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
package quests.Q10303_CrossroadsBetweenLightAndDarkness;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Crossroads between Light and Darkness (10303)
 * @URL https://l2wiki.com/Crossroads_between_Light_and_Darkness
 * @author Gigi
 */
public class Q10303_CrossroadsBetweenLightAndDarkness extends Quest
{
	// Npc
	private static final int JONAH = 32909;
	private static final int MYSTERIUS_PRIEST = 33361;
	// Mobs
	private static final int[] MOBS =
	{
		22863, // Fairy Warrior
		22871, // Fairy Rogue
		22879, // Fairy Knight
		22887, // Satyr Wizard
		22895 // Satyr Summoner
	};
	// Item
	private static final int TRACE_OF_DECAYNG_DARKNESS = 17747;
	private static final int TRACE_OF_DECAYNG_DARKNESS_Q = 17820;
	// Misc
	private static final int MIN_LEVEL = 90;
	// Reward
	private static final int[] JOHAN_REWARD =
	{
		13505, // Clownfish Hat
		16108, // Refined Turtle Hat
		16102, // Refined Shark Hat
		16105 // Refined Penguin Hat
	};
	private static final int[] PRIEST_REWARD =
	{
		16101, // Refined Brown Skeleton Circlet
		16100, // Refined Green Skeleton Circlet
		16099, // Refined Orange Skeleton Circlet
		16098 // Refined Black Skeleton Circlet
	};
	
	public Q10303_CrossroadsBetweenLightAndDarkness()
	{
		super(10303);
		addItemTalkId(TRACE_OF_DECAYNG_DARKNESS);
		addKillId(MOBS);
		addTalkId(JONAH, MYSTERIUS_PRIEST);
		registerQuestItems(TRACE_OF_DECAYNG_DARKNESS_Q);
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
			case "32909-02.htm":
			case "33361-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32909-04.html":
			{
				showOnScreenMsg(player, NpcStringId.S1_YOU_WILL_REGRET_NOT_GIVING_ME_THE_TRACE_OF_DECAYING_DARKNESS, ExShowScreenMessage.TOP_CENTER, 6000, false, player.getName());
				npc.setScriptValue(1);
				startQuestTimer("JONAH", 60000, npc, player, false);
				htmltext = event;
				break;
			}
			case "33361-04.html":
			{
				showOnScreenMsg(player, NpcStringId.S1_YOU_FOOL_YOU_WILL_REGRET_NOT_GIVING_ME_THE_TRACE_OF_DECAYING_DARKNESS, ExShowScreenMessage.TOP_CENTER, 6000, false, player.getName());
				npc.setScriptValue(1);
				startQuestTimer("PRIEST", 60000, npc, player, false);
				htmltext = event;
				break;
			}
			case "JONAH":
			{
				if (npc.getId() == JONAH)
				{
					npc.setScriptValue(0);
					break;
				}
			}
			case "PRIEST":
			{
				if (npc.getId() == MYSTERIUS_PRIEST)
				{
					npc.setScriptValue(0);
					break;
				}
			}
			case "32909-03.html":
			{
				if (qs.isCond(1))
				{
					giveAdena(player, 465855, true);
					giveItems(player, getRandomEntry(JOHAN_REWARD), 1);
					addExpAndSp(player, 6730155, 2847330);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
			case "33361-03.html":
			{
				if (qs.isCond(1))
				{
					giveAdena(player, 465855, true);
					giveItems(player, getRandomEntry(PRIEST_REWARD), 1);
					addExpAndSp(player, 6730155, 2847330);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onItemTalk(ItemInstance item, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCompleted())
		{
			htmltext = getNoQuestMsg(player);
		}
		boolean startQuest = false;
		switch (qs.getState())
		{
			case State.CREATED:
			{
				startQuest = true;
				break;
			}
		}
		
		if (startQuest)
		{
			if (player.getLevel() >= MIN_LEVEL)
			{
				qs.startQuest();
				takeItems(player, TRACE_OF_DECAYNG_DARKNESS, 1);
				giveItems(player, TRACE_OF_DECAYNG_DARKNESS_Q, 1);
				// htmltext = "start.html";
				htmltext = "";
			}
			else
			{
				htmltext = "noLevel.htm";
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
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case JONAH:
					{
						if ((player.getLevel() < MIN_LEVEL) || (getQuestItemsCount(player, TRACE_OF_DECAYNG_DARKNESS_Q) < 1))
						{
							htmltext = "32909-00a.htm";
							break;
						}
						else if (qs.isCond(1) && (npc.getScriptValue() == 0))
						{
							htmltext = "32909-01.htm";
						}
						break;
					}
					case MYSTERIUS_PRIEST:
					{
						if ((player.getLevel() < MIN_LEVEL) || (getQuestItemsCount(player, TRACE_OF_DECAYNG_DARKNESS_Q) < 1))
						{
							htmltext = "33361-00a.htm";
							break;
						}
						else if (qs.isCond(1) && (npc.getScriptValue() == 0))
						{
							htmltext = "33361-01.htm";
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
					case JONAH:
					{
						htmltext = "32909-00.html";
						break;
					}
					case MYSTERIUS_PRIEST:
					{
						htmltext = "33361-00.html";
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
		if (getRandom(100) < 0.03)
		{
			giveItems(killer, TRACE_OF_DECAYNG_DARKNESS, 1);
			showOnScreenMsg(killer, NpcStringId.S1_CANNOT_GIVE_THEM_DECAYING_DARKNESS, ExShowScreenMessage.BOTTOM_RIGHT, 6000, false, killer.getName());
		}
		return super.onKill(npc, killer, isSummon);
	}
}