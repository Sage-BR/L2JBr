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
package events.LetterCollector;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.LongTimeEvent;

/**
 * Event: Letter Collector
 * @URL http://www.lineage2.com/en/news/events/letter-collector-event-05112016.php
 * @author Mobius, Gigi
 */
public class LetterCollector extends LongTimeEvent
{
	// NPC
	private static final int ANGEL_CAT = 33873;
	// Items
	private static final int A = 3875;
	private static final int C = 3876;
	private static final int E = 3877;
	private static final int F = 3878;
	private static final int G = 3879;
	private static final int H = 3880;
	private static final int I = 3881;
	private static final int L = 3882;
	private static final int N = 3883;
	private static final int O = 3884;
	private static final int R = 3885;
	private static final int S = 3886;
	private static final int T = 3887;
	private static final int II = 3888;
	// Rewards
	private static final ItemHolder[] REWARDS_TAUTI =
	{
		new ItemHolder(34998, 1), // Tauti's One-handed Axe
		new ItemHolder(35001, 1), // Tauti's Dual Axe
	};
	private static final ItemHolder[] REWARDS_SPECTER =
	{
		new ItemHolder(18035, 1), // Blessed Specter Shaper
		new ItemHolder(18036, 1), // Blessed Specter Cutter
		new ItemHolder(18037, 1), // Blessed Specter Slasher
		new ItemHolder(18038, 1), // Blessed Specter Avenger
		new ItemHolder(18039, 1), // Blessed Specter Fighter
		new ItemHolder(18040, 1), // Blessed Specter Stormer
		new ItemHolder(18041, 1), // Blessed Specter Thrower
		new ItemHolder(18042, 1), // Blessed Specter Shooter
		new ItemHolder(18043, 1), // Blessed Specter Buster
		new ItemHolder(18044, 1), // Blessed Specter Caster
		new ItemHolder(18045, 1), // Blessed Specter Retributer
		new ItemHolder(18046, 1), // Blessed Specter Dualsword
		new ItemHolder(18047, 1), // Blessed Specter Dual Dagger
		new ItemHolder(18048, 1), // Blessed Specter Dual Blunt Weapon
	};
	private static final ItemHolder[] REWARDS_SERAPH =
	{
		new ItemHolder(18049, 1), // Blessed Seraph Helmet
		new ItemHolder(18050, 1), // Blessed Seraph Breastplate
		new ItemHolder(18051, 1), // Blessed Seraph Gaiters
		new ItemHolder(18052, 1), // Blessed Seraph Gauntlets
		new ItemHolder(18053, 1), // Blessed Seraph Boots
		new ItemHolder(18054, 1), // Blessed Seraph Shield
		new ItemHolder(18055, 1), // Blessed Seraph Leather Helmet
		new ItemHolder(18056, 1), // Blessed Seraph Leather Armor
		new ItemHolder(18057, 1), // Blessed Seraph Leather Leggings
		new ItemHolder(18058, 1), // Blessed Seraph Leather Gloves
		new ItemHolder(18059, 1), // Blessed Seraph Leather Boots
		new ItemHolder(18060, 1), // Blessed Seraph Circlet
		new ItemHolder(18061, 1), // Blessed Seraph Tunic
		new ItemHolder(18062, 1), // Blessed Seraph Stockings
		new ItemHolder(18063, 1), // Blessed Seraph Gloves
		new ItemHolder(18064, 1), // Blessed Seraph Shoes
		new ItemHolder(18065, 1), // Blessed Seraph Sigil
	};
	private static final ItemHolder[] REWARDS_OTHER =
	{
		new ItemHolder(17371, 20), // Crystal (R-grade)
		new ItemHolder(17754, 2000), // Soulshot (R-grade)
		new ItemHolder(19440, 1), // Gemstone (R-grade)
		new ItemHolder(19442, 500), // Blessed Spiritshot (R-grade)
		new ItemHolder(34945, 1), // Lv. 3 Giant Dye Pack
		new ItemHolder(34946, 1), // Lv. 3 Legendary Dye Pack
		new ItemHolder(34947, 1), // Lv. 3 Ancient Dye Pack
		new ItemHolder(34950, 1), // Lv. 4 Giant Dye Pack
		new ItemHolder(34951, 1), // Lv. 4 Legendary Dye Pack
		new ItemHolder(34952, 1), // Lv. 4 Ancient Dye Pack
		new ItemHolder(34953, 1), // Lv. 5 Giant Dye Pack
		new ItemHolder(34954, 1), // Lv. 5 Legendary Dye Pack
		new ItemHolder(34955, 1), // Lv. 5 Ancient Dye Pack
		new ItemHolder(35567, 1), // Dark Eternal Enhancement Stone
		new ItemHolder(37009, 3), // Blueberry Cake (MP)
		new ItemHolder(37802, 1), // Dark Eternal Enhancement Stone Fragment
		new ItemHolder(38101, 1), // Leona's Scroll: 1,000,000 SP
		new ItemHolder(38102, 1), // Leona's Scroll: 5,000,000 SP
		new ItemHolder(38103, 1), // Leona's Scroll: 10,000,000 SP
		new ItemHolder(39560, 1), // Low-grade Wind Dye Pack
		new ItemHolder(39561, 1), // Mid-grade Wind Dye Pack
		new ItemHolder(39562, 1), // High-grade Wind Dye Pack
		new ItemHolder(39633, 1), // Fortune Pocket - Stage 5
	};
	
	private LetterCollector()
	{
		addStartNpc(ANGEL_CAT);
		addFirstTalkId(ANGEL_CAT);
		addTalkId(ANGEL_CAT);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33873-1.htm":
			case "33873-2.htm":
			{
				htmltext = event;
				break;
			}
			case "lineage":
			{
				if ((getQuestItemsCount(player, L) >= 1) && //
					(getQuestItemsCount(player, I) >= 1) && //
					(getQuestItemsCount(player, N) >= 1) && //
					(getQuestItemsCount(player, E) >= 2) && //
					(getQuestItemsCount(player, A) >= 1) && //
					(getQuestItemsCount(player, G) >= 1) && //
					(getQuestItemsCount(player, II) >= 1))
				{
					takeItems(player, L, 1);
					takeItems(player, I, 1);
					takeItems(player, N, 1);
					takeItems(player, E, 2);
					takeItems(player, A, 1);
					takeItems(player, G, 1);
					takeItems(player, II, 1);
					giveItems(player, getReward());
					htmltext = "33873-1.htm";
				}
				else
				{
					htmltext = "noItem.htm";
				}
				break;
			}
			case "together":
			{
				if ((getQuestItemsCount(player, T) >= 2) && //
					(getQuestItemsCount(player, O) >= 1) && //
					(getQuestItemsCount(player, G) >= 1) && //
					(getQuestItemsCount(player, E) >= 2) && //
					(getQuestItemsCount(player, H) >= 1) && //
					(getQuestItemsCount(player, R) >= 1))
				{
					takeItems(player, T, 2);
					takeItems(player, O, 1);
					takeItems(player, G, 1);
					takeItems(player, E, 2);
					takeItems(player, H, 1);
					takeItems(player, R, 1);
					giveItems(player, getReward());
					htmltext = "33873-1.htm";
				}
				else
				{
					htmltext = "noItem.htm";
				}
				break;
			}
			case "ncsoft":
			{
				if ((getQuestItemsCount(player, N) >= 1) && //
					(getQuestItemsCount(player, C) >= 1) && //
					(getQuestItemsCount(player, S) >= 1) && //
					(getQuestItemsCount(player, O) >= 1) && //
					(getQuestItemsCount(player, F) >= 1) && //
					(getQuestItemsCount(player, T) >= 1))
				{
					takeItems(player, N, 1);
					takeItems(player, C, 1);
					takeItems(player, S, 1);
					takeItems(player, O, 1);
					takeItems(player, F, 1);
					takeItems(player, T, 1);
					giveItems(player, getReward());
					htmltext = "33873-1.htm";
				}
				else
				{
					htmltext = "noItem.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	private ItemHolder getReward()
	{
		if (getRandom(100) < 1)
		{
			return getRandomEntry(REWARDS_TAUTI);
		}
		else if (getRandom(100) < 3)
		{
			return getRandomEntry(REWARDS_SPECTER);
		}
		else if (getRandom(100) < 3)
		{
			return getRandomEntry(REWARDS_SERAPH);
		}
		else
		{
			return getRandomEntry(REWARDS_OTHER);
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + "-1.htm";
	}
	
	public static void main(String[] args)
	{
		new LetterCollector();
	}
}