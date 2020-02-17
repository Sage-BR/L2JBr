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
package quests.Q00034_InSearchOfCloth;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * In Search of Cloth (34)
 * @URL https://l2wiki.com/In_Search_of_Cloth
 * @author malyelfik
 */
public class Q00034_InSearchOfCloth extends Quest
{
	// NPCs
	private static final int RADIA = 30088;
	private static final int RALFORD = 30165;
	private static final int VARAN = 30294;
	// Monsters
	private static final int MARSH_SPIDER = 20233;
	// Items
	private static final int ARMOR_FRAGMENT_LOW_GRADE = 36551;
	private static final int ACCESSORY_GEM_LOW_GRADE = 36556;
	private static final int MYSTERIOUS_CLOTH = 7076;
	private static final int SKEIN_OF_YARN = 7161;
	private static final int SPINNERET = 7528;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int SPINNERET_COUNT = 10;
	private static final int ARMOR_FRAGMENT_COUNT = 420;
	private static final int ACCESSORY_GEM_COUNT = 750;
	
	public Q00034_InSearchOfCloth()
	{
		super(34);
		addStartNpc(RADIA);
		addTalkId(RADIA, RALFORD, VARAN);
		addKillId(MARSH_SPIDER);
		addCondMinLevel(MIN_LEVEL, "30088-02.html");
		registerQuestItems(SKEIN_OF_YARN, SPINNERET);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "30088-03.htm":
			{
				st.startQuest();
				break;
			}
			case "30294-02.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
				}
				break;
			}
			case "30088-06.html":
			{
				if (st.isCond(2))
				{
					st.setCond(3, true);
				}
				break;
			}
			case "30165-02.html":
			{
				if (st.isCond(3))
				{
					st.setCond(4, true);
				}
				break;
			}
			case "30165-05.html":
			{
				if (st.isCond(5))
				{
					if (getQuestItemsCount(player, SPINNERET) < SPINNERET_COUNT)
					{
						return getNoQuestMsg(player);
					}
					takeItems(player, SPINNERET, SPINNERET_COUNT);
					giveItems(player, SKEIN_OF_YARN, 1);
					st.setCond(6, true);
				}
				break;
			}
			case "30088-10.html":
			{
				if (st.isCond(6))
				{
					if ((getQuestItemsCount(player, ARMOR_FRAGMENT_LOW_GRADE) >= ARMOR_FRAGMENT_COUNT) && (getQuestItemsCount(player, ACCESSORY_GEM_LOW_GRADE) >= ACCESSORY_GEM_COUNT) && hasQuestItems(player, SKEIN_OF_YARN))
					{
						if ((player.getLevel() >= MIN_LEVEL))
						{
							takeItems(player, SKEIN_OF_YARN, 1);
							takeItems(player, ARMOR_FRAGMENT_LOW_GRADE, ARMOR_FRAGMENT_COUNT);
							takeItems(player, ACCESSORY_GEM_LOW_GRADE, ACCESSORY_GEM_COUNT);
							giveItems(player, MYSTERIOUS_CLOTH, 1);
							st.exitQuest(false, true);
						}
						else
						{
							htmltext = getNoQuestLevelRewardMsg(player);
						}
					}
					else
					{
						htmltext = "30088-11.html";
					}
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
		final QuestState st = getQuestState(player, false);
		final PlayerInstance member = getRandomPartyMember(player, 4);
		
		if ((st != null) && (st.isCond(4)) && (member != null) && getRandomBoolean())
		{
			giveItems(member, SPINNERET, 1);
			if (getQuestItemsCount(member, SPINNERET) >= SPINNERET_COUNT)
			{
				st.setCond(5, true);
			}
			else
			{
				playSound(member, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == RADIA)
				{
					htmltext = "30088-01.htm";
					break;
				}
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case RADIA:
					{
						switch (st.getCond())
						{
							case 1:
							{
								htmltext = "30088-04.html";
								break;
							}
							case 2:
							{
								htmltext = "30088-05.html";
								break;
							}
							case 3:
							{
								htmltext = "30088-07.html";
								break;
							}
							case 6:
							{
								htmltext = ((getQuestItemsCount(player, ARMOR_FRAGMENT_LOW_GRADE) >= ARMOR_FRAGMENT_COUNT) && (getQuestItemsCount(player, ACCESSORY_GEM_LOW_GRADE) >= ACCESSORY_GEM_COUNT)) ? "30088-08.html" : "30088-09.html";
								break;
							}
						}
						break;
					}
					case VARAN:
					{
						if (st.isCond(1))
						{
							htmltext = "30294-01.html";
						}
						else if (st.isCond(2))
						{
							htmltext = "30294-03.html";
						}
						break;
					}
					case RALFORD:
					{
						switch (st.getCond())
						{
							case 3:
							{
								htmltext = "30165-01.html";
								break;
							}
							case 4:
							{
								htmltext = "30165-03.html";
								break;
							}
							case 5:
							{
								htmltext = "30165-04.html";
								break;
							}
							case 6:
							{
								htmltext = "30165-06.html";
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
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
}