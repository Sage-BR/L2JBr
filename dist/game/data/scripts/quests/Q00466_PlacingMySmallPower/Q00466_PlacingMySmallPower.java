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
package quests.Q00466_PlacingMySmallPower;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Placing My Small Power (466)
 * @URL https://l2wiki.com/Placing_My_Small_Power
 * @VIDEO http://www.dailymotion.com/video/x24y1jl_quest-placing-my-small-power_videogames
 * @author Gigi
 */
public class Q00466_PlacingMySmallPower extends Quest
{
	// NPCs
	private static final int ASTERIOS = 30154;
	private static final int NOETI_MIMILEAD = 32895;
	private static final int[] COCON =
	{
		19394 // Large Cocoon
	};
	private static final int[] WING =
	{
		22863, // Fairy Warrior
		22864, // Fairy Warrior
		22907, // Satyr Witch
		22899, // Satyr Summoner
		22891, // Satyr Wizard
		22875, // Fairy Rogue
		22867, // Fairy Warrior
		22883 // Fairy Knight
	};
	private static final int[] BREATH =
	{
		22870, // Fairy Warrior
		22886, // Fairy Knight
		22902, // Satyr Summoner
		22894, // Satyr Wizard
		22878 // Fairy Rogue
	};
	// Items
	private static final int FAIRY_WING = 17597;
	private static final int COCOON_FRAGMENT = 17598;
	private static final int KIMERIANS_BREATH = 17599;
	private static final int TEMINIELS_TONIC = 17596;
	private static final int TONIC_RECIPE = 17603;
	// Reward
	private static final int CERTIFICATE_OF_PROMISE = 30384;
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q00466_PlacingMySmallPower()
	{
		super(466);
		addStartNpc(ASTERIOS);
		addTalkId(ASTERIOS, NOETI_MIMILEAD);
		addKillId(COCON);
		addKillId(WING);
		addKillId(BREATH);
		registerQuestItems(FAIRY_WING, COCOON_FRAGMENT, KIMERIANS_BREATH, TEMINIELS_TONIC, TONIC_RECIPE);
		addCondMinLevel(MIN_LEVEL, "30154-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		switch (event)
		{
			case "30154-02.htm":
			case "32895-02.html":
			{
				htmltext = event;
				break;
			}
			case "30154-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32895-03.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30154-05.html":
			{
				giveItems(player, CERTIFICATE_OF_PROMISE, 1);
				qs.exitQuest(QuestType.DAILY, true);
				htmltext = event;
				break;
			}
			case "32895-07.html":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5);
				}
				htmltext = event;
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
			case State.COMPLETED:
			{
				if ((npc.getId() == ASTERIOS) && !qs.isNowAvailable())
				{
					htmltext = "30154-00a.htm";
					break;
				}
				qs.setState(State.CREATED);
			}
			case State.CREATED:
			{
				htmltext = "30154-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ASTERIOS:
					{
						if (qs.isCond(1))
						{
							htmltext = "30154-04.html";
						}
						else if (qs.isCond(5))
						{
							htmltext = "30154-05a.html";
						}
						break;
					}
					case NOETI_MIMILEAD:
					{
						
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "32895-01.html";
								break;
							}
							case 2:
							{
								htmltext = "32895-04.html";
								break;
							}
							case 3:
							{
								giveItems(player, TONIC_RECIPE, 1);
								qs.setCond(4, true);
								htmltext = "32895-05.html";
								break;
							}
							case 4:
							{
								if (getQuestItemsCount(player, TEMINIELS_TONIC) < 5)
								{
									htmltext = "32895-06.html";
								}
								else
								{
									htmltext = "32895-07.html";
									// TODO: Remove timer.
									startQuestTimer("32895-07.html", 100, npc, player, false);
								}
								break;
							}
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		if (qs.isCond(2))
		{
			if (CommonUtil.contains(COCON, npc.getId()) && (getQuestItemsCount(player, COCOON_FRAGMENT) < 5) && (getRandom(100) < 7))
			{
				giveItems(player, COCOON_FRAGMENT, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			if (CommonUtil.contains(WING, npc.getId()) && (getQuestItemsCount(player, FAIRY_WING) < 5) && (getRandom(100) < 10))
			{
				giveItems(player, FAIRY_WING, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			if (CommonUtil.contains(BREATH, npc.getId()) && (getQuestItemsCount(player, KIMERIANS_BREATH) < 5) && (getRandom(100) < 12))
			{
				giveItems(player, KIMERIANS_BREATH, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		if ((getQuestItemsCount(player, COCOON_FRAGMENT) == 5) && (getQuestItemsCount(player, FAIRY_WING) == 5) && (getQuestItemsCount(player, KIMERIANS_BREATH) == 5))
		{
			qs.setCond(1);
			qs.setCond(3, true);
		}
		return super.onKill(npc, player, isSummon);
	}
}