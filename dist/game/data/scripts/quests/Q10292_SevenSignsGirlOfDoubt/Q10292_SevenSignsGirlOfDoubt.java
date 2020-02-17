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
package quests.Q10292_SevenSignsGirlOfDoubt;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;

import quests.Q00198_SevenSignsEmbryo.Q00198_SevenSignsEmbryo;

/**
 * Seven Signs, Girl of Doubt (10292)
 * @author Adry_85 / Stayway (Rework Helios)
 */
public class Q10292_SevenSignsGirlOfDoubt extends Quest
{
	// NPCs
	private static final int HARDIN = 30832;
	private static final int WOOD = 32593;
	private static final int FRANZ = 32597;
	private static final int JAINA = 32617;
	private static final int ELCADIA = 32784;
	// Item
	private static final ItemHolder ELCADIAS_MARK = new ItemHolder(17226, 10);
	// Misc
	private static final int MIN_LEVEL = 81;
	private boolean isBusy = false;
	// Monster
	private static final int CREATURE_OF_THE_DUSK1 = 27422;
	private static final int CREATURE_OF_THE_DUSK2 = 27424;
	private static final int[] MOBS =
	{
		18906, // Farm Bandit
		18907, // Beast Devourer
	};
	
	public Q10292_SevenSignsGirlOfDoubt()
	{
		super(10292);
		addStartNpc(WOOD);
		addTalkId(WOOD, FRANZ, JAINA, ELCADIA, HARDIN);
		addKillId(MOBS);
		addKillId(CREATURE_OF_THE_DUSK1, CREATURE_OF_THE_DUSK2);
		addCondMinLevel(MIN_LEVEL, "32593-03.htm");
		addCondCompletedQuest(Q00198_SevenSignsEmbryo.class.getSimpleName(), "32593-03.htm");
		registerQuestItems(ELCADIAS_MARK.getId());
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
			case "32593-04.htm":
			case "32593-06.htm":
			case "32597-02.html":
			case "32597-06.html":
			{
				htmltext = event;
				break;
			}
			case "32593-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32597-07.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "32784-02.html":
			{
				if (qs.isCond(2))
				{
					htmltext = event;
				}
				break;
			}
			case "32784-03.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "32784-06.html":
			{
				if (qs.isCond(4) && hasItem(player, ELCADIAS_MARK))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					htmltext = event;
				}
				break;
			}
			case "32784-08.html":
			{
				if (qs.isCond(4) && hasItem(player, ELCADIAS_MARK))
				{
					takeItem(player, ELCADIAS_MARK);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "32784-12.html":
			case "32784-13.html":
			{
				if (qs.isCond(6))
				{
					htmltext = event;
				}
				break;
			}
			case "32784-14.html":
			{
				if (qs.isCond(6))
				{
					qs.setCond(7, true);
					htmltext = event;
				}
				break;
			}
			case "SPAWN":
			{
				if (qs.isCond(5))
				{
					isBusy = true;
					final Npc creature1 = addSpawn(CREATURE_OF_THE_DUSK1, 89440, -238016, -9632, 335, false, 0, false, player.getInstanceId());
					creature1.setRandomWalking(true);
					final Npc creature2 = addSpawn(CREATURE_OF_THE_DUSK2, 89524, -238131, -9632, 56, false, 0, false, player.getInstanceId());
					creature2.setRandomWalking(true);
					ThreadPool.schedule(() ->
					{
						creature1.deleteMe();
						creature2.deleteMe();
						qs.unset("ex");
						isBusy = false;
					}, 60000);
				}
				break;
			}
			case "30832-02.html":
			{
				if (qs.isCond(7))
				{
					qs.setCond(8, true);
					htmltext = event;
				}
				break;
			}
			case "30832-03.html":
			{
				if (qs.isCond(8))
				{
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		if (CommonUtil.contains(MOBS, npc.getId()))
		{
			final QuestState qs = getRandomPartyMemberState(player, 3, 3, npc);
			if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, ELCADIAS_MARK.getId(), 1, ELCADIAS_MARK.getCount(), 1.0, true))
			{
				qs.setCond(4, true);
			}
		}
		else
		{
			final QuestState qs = getQuestState(player, false);
			if ((qs != null) && qs.isCond(5))
			{
				final int value = qs.getInt("ex") + 1;
				qs.set("ex", value);
				if (value == 2)
				{
					qs.setCond(6, true);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (npc.getId())
		{
			case WOOD:
			{
				if (qs.isCompleted())
				{
					htmltext = "32593-02.html";
				}
				else if (qs.isCreated())
				{
					htmltext = "32593-01.htm";
				}
				else if (qs.isStarted())
				{
					htmltext = "32593-07.html";
				}
				break;
			}
			case FRANZ:
			{
				if (qs.isCond(1))
				{
					htmltext = "32597-01.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "32597-03.html";
				}
				break;
			}
			case ELCADIA:
			{
				switch (qs.getCond())
				{
					case 2:
					{
						htmltext = "32784-01.html";
						break;
					}
					case 3:
					{
						htmltext = "32784-04.html";
						break;
					}
					case 4:
					{
						if (hasItem(player, ELCADIAS_MARK))
						{
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							htmltext = "32784-05.html";
						}
						break;
					}
					case 5:
					{
						if (isBusy)
						{
							htmltext = "32784-17.html";
						}
						else
						{
							htmltext = "32784-07.html";
						}
						break;
					}
					case 6:
					{
						htmltext = "32784-11.html";
						break;
					}
					case 7:
					{
						htmltext = "32784-15.html";
						break;
					}
					case 8:
					{
						if (player.isSubClassActive())
						{
							htmltext = "32784-18.html";
						}
						else
						{
							addExpAndSp(player, 10000000, 1000000);
							qs.exitQuest(false, true);
							htmltext = "32784-16.html";
						}
						break;
					}
				}
				break;
			}
			case HARDIN:
			{
				if (qs.isCond(7))
				{
					htmltext = "30832-01.html";
				}
				else if (qs.isCond(8))
				{
					htmltext = "30832-04.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
