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
package quests.Q00371_ShrieksOfGhosts;

import java.util.HashMap;
import java.util.Map;

import org.l2jbr.Config;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.util.Util;

/**
 * Shrieks of Ghosts (371)
 * @author Adry_85
 */
public class Q00371_ShrieksOfGhosts extends Quest
{
	private static final class DropInfo
	{
		public int _firstChance;
		public int _secondChance;
		
		public DropInfo(int firstChance, int secondChance)
		{
			_firstChance = firstChance;
			_secondChance = secondChance;
		}
		
		public int getFirstChance()
		{
			return _firstChance;
		}
		
		public int getSecondChance()
		{
			return _secondChance;
		}
	}
	
	// NPCs
	private static final int REVA = 30867;
	private static final int PATRIN = 30929;
	// Items
	private static final int ANCIENT_ASH_URN = 5903;
	private static final int ANCIENT_PORCELAIN = 6002;
	private static final int ANCIENT_PORCELAIN_EXCELLENT = 6003;
	private static final int ANCIENT_PORCELAIN_HIGH_QUALITY = 6004;
	private static final int ANCIENT_PORCELAIN_LOW_QUALITY = 6005;
	private static final int ANCIENT_PORCELAIN_LOWEST_QUALITY = 6006;
	// Misc
	private static final int MIN_LEVEL = 59;
	
	private static final Map<Integer, DropInfo> MOBS = new HashMap<>();
	static
	{
		MOBS.put(20977, new DropInfo(350, 400)); // Elmoreden's Lady
		MOBS.put(20978, new DropInfo(583, 673)); // Elmoreden's Archer
		MOBS.put(20979, new DropInfo(458, 538)); // Elmoreden's Maid
		MOBS.put(21073, new DropInfo(540, 620)); // Elmoreden's Warrior
	}
	
	public Q00371_ShrieksOfGhosts()
	{
		super(371);
		addStartNpc(REVA);
		addTalkId(REVA, PATRIN);
		addKillId(MOBS.keySet());
		registerQuestItems(ANCIENT_ASH_URN);
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
			case "30867-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30867-05.html":
			{
				final long ancientAshUrnCount = getQuestItemsCount(player, ANCIENT_ASH_URN);
				
				if (ancientAshUrnCount < 1)
				{
					htmltext = event;
				}
				else if (ancientAshUrnCount < 100)
				{
					giveAdena(player, (ancientAshUrnCount * 1000) + 15000, true);
					takeItems(player, ANCIENT_ASH_URN, -1);
					htmltext = "30867-06.html";
				}
				else
				{
					giveAdena(player, (ancientAshUrnCount * 1000) + 37700, true);
					takeItems(player, ANCIENT_ASH_URN, -1);
					htmltext = "30867-07.html";
				}
				break;
			}
			case "30867-08.html":
			case "30929-01.html":
			case "30929-02.html":
			{
				htmltext = event;
				break;
			}
			case "30867-09.html":
			{
				giveAdena(player, getQuestItemsCount(player, ANCIENT_ASH_URN) * 1000, true);
				qs.exitQuest(true, true);
				htmltext = "30867-09.html";
				break;
			}
			case "30929-03.html":
			{
				if (!hasQuestItems(player, ANCIENT_PORCELAIN))
				{
					htmltext = event;
				}
				else
				{
					final int random = getRandom(100);
					
					if (random < 2)
					{
						giveItems(player, ANCIENT_PORCELAIN_EXCELLENT, 1);
						htmltext = "30929-04.html";
					}
					else if (random < 32)
					{
						giveItems(player, ANCIENT_PORCELAIN_HIGH_QUALITY, 1);
						htmltext = "30929-05.html";
					}
					else if (random < 62)
					{
						giveItems(player, ANCIENT_PORCELAIN_LOW_QUALITY, 1);
						htmltext = "30929-06.html";
					}
					else if (random < 77)
					{
						giveItems(player, ANCIENT_PORCELAIN_LOWEST_QUALITY, 1);
						htmltext = "30929-07.html";
					}
					else
					{
						htmltext = "30929-08.html";
					}
					
					takeItems(player, ANCIENT_PORCELAIN, 1);
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
		
		if ((qs == null) || !Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			return null;
		}
		
		final DropInfo info = MOBS.get(npc.getId());
		final int random = getRandom(1000);
		
		if (random < info.getFirstChance())
		{
			giveItemRandomly(killer, npc, ANCIENT_ASH_URN, 1, 0, 1.0, true);
		}
		else if (random < info.getSecondChance())
		{
			giveItemRandomly(killer, npc, ANCIENT_PORCELAIN, 1, 0, 1.0, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			htmltext = (player.getLevel() >= MIN_LEVEL) ? "30867-01.htm" : "30867-03.htm";
		}
		else if (qs.isStarted())
		{
			if (npc.getId() == REVA)
			{
				htmltext = hasQuestItems(player, ANCIENT_PORCELAIN) ? "30867-04.html" : "30867-10.html";
			}
			else
			{
				htmltext = "30929-01.html";
			}
		}
		return htmltext;
	}
}
