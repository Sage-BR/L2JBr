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
package quests.Q00035_FindGlitteringJewelry;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Find Glittering Jewelry (35)
 * @author malyelfik
 */
public class Q00035_FindGlitteringJewelry extends Quest
{
	// NPCs
	private static final int ELLIE = 30091;
	private static final int FELTON = 30879;
	// Monster
	private static final int ALLIGATOR = 20135;
	// Items
	private static final int ACCESSORY_GEM = 36556;
	private static final int IRON_ORE = 36521;
	private static final int ARMOR_FRAGMENT = 36551;
	private static final int JEWEL_BOX = 7077;
	private static final int ROUGH_JEWEL = 7162;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int JEWEL_COUNT = 10;
	private static final int IRON_ORE_COUNT = 95;
	private static final int ACCESSORY_GEM_COUNT = 385;
	private static final int ARMOR_FRAGMENT_COUNT = 405;
	
	public Q00035_FindGlitteringJewelry()
	{
		super(35);
		addStartNpc(ELLIE);
		addTalkId(ELLIE, FELTON);
		addKillId(ALLIGATOR);
		registerQuestItems(ROUGH_JEWEL);
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
			case "30091-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "30879-02.html":
			{
				qs.setCond(2, true);
				break;
			}
			case "30091-07.html":
			{
				if (getQuestItemsCount(player, ROUGH_JEWEL) < JEWEL_COUNT)
				{
					return "30091-08.html";
				}
				takeItems(player, ROUGH_JEWEL, -1);
				qs.setCond(4, true);
				break;
			}
			case "30091-11.html":
			{
				if ((getQuestItemsCount(player, IRON_ORE) >= IRON_ORE_COUNT) && (getQuestItemsCount(player, ACCESSORY_GEM) >= ACCESSORY_GEM_COUNT) && (getQuestItemsCount(player, ARMOR_FRAGMENT) >= ARMOR_FRAGMENT_COUNT))
				{
					takeItems(player, IRON_ORE, IRON_ORE_COUNT);
					takeItems(player, ACCESSORY_GEM, ACCESSORY_GEM_COUNT);
					takeItems(player, ARMOR_FRAGMENT, ARMOR_FRAGMENT_COUNT);
					giveItems(player, JEWEL_BOX, 1);
					qs.exitQuest(false, true);
				}
				else
				{
					htmltext = "30091-12.html";
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
		final PlayerInstance member = getRandomPartyMember(player, 2);
		if ((member != null) && getRandomBoolean())
		{
			giveItems(player, ROUGH_JEWEL, 1);
			if (getQuestItemsCount(player, ROUGH_JEWEL) >= JEWEL_COUNT)
			{
				getQuestState(member, false).setCond(3, true);
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
		
		switch (npc.getId())
		{
			case ELLIE:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "30091-01.htm" : "30091-02.html";
						break;
					}
					case State.STARTED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "30091-04.html";
								break;
							}
							case 3:
							{
								htmltext = (getQuestItemsCount(player, ROUGH_JEWEL) >= JEWEL_COUNT) ? "30091-06.html" : "30091-05.html";
								break;
							}
							case 4:
							{
								htmltext = ((getQuestItemsCount(player, IRON_ORE) >= IRON_ORE_COUNT) && (getQuestItemsCount(player, ACCESSORY_GEM) >= ACCESSORY_GEM_COUNT) && (getQuestItemsCount(player, ARMOR_FRAGMENT) >= ARMOR_FRAGMENT_COUNT)) ? "30091-09.html" : "30091-10.html";
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
				break;
			}
			case FELTON:
			{
				if (qs.isStarted())
				{
					if (qs.isCond(1))
					{
						htmltext = "30879-01.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "30879-03.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
}