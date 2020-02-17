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
package quests.Q10544_SeekerSupplies;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10543_SheddingWeight.Q10543_SheddingWeight;

/**
 * Seeker Supplies (10544)
 * @URL https://l2wiki.com/Commando_Supplies
 * @author Gigi
 */
public class Q10544_SeekerSupplies extends Quest
{
	// NPCs
	private static final int WILFORD = 30005;
	private static final int KATERINA = 30004;
	private static final int LECTOR = 30001;
	private static final int JACKSON = 30002;
	private static final int TREVOR = 32166;
	private static final int FRANCO = 32153; // Human
	private static final int RIVIAN = 32147; // Elf
	private static final int TOOK = 32150; // Orc
	private static final int DEVON = 32160; // Dark Elf
	private static final int MOKA = 32157; // Dwarf
	private static final int VALFAR = 32146; // Kamael
	// Item's
	private static final int GROCERY_SUPLLY_BOX = 39524;
	private static final int WEAPON_SUPLLY_BOX = 39522;
	private static final int ARMOR_SUPLLY_BOX = 39523;
	private static final int ACCESSORY_SUPPLY_BOX = 39525;
	// Reward's
	private static final int LEATHER_SHIRT = 22;
	private static final int LEATHER_PANTS = 29;
	private static final int LEATHER_TUNIC = 429;
	private static final int LEATHER_STOCKINGS = 464;
	private static final int APPRENTICE_EARRING = 112;
	private static final int NECKLACE_OF_KNOWNLEDGE = 906;
	// Misc
	private static final int MAX_LEVEL = 20;
	
	public Q10544_SeekerSupplies()
	{
		super(10544);
		addStartNpc(WILFORD);
		addTalkId(WILFORD, KATERINA, LECTOR, JACKSON, TREVOR, FRANCO, RIVIAN, TOOK, DEVON, MOKA, VALFAR);
		registerQuestItems(GROCERY_SUPLLY_BOX, WEAPON_SUPLLY_BOX, ARMOR_SUPLLY_BOX, ACCESSORY_SUPPLY_BOX);
		addCondNotRace(Race.ERTHEIA, "noRace.html");
		addCondMaxLevel(MAX_LEVEL, "noLevel.html");
		addCondCompletedQuest(Q10543_SheddingWeight.class.getSimpleName(), "noLevel.html");
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
			case "30005-02.htm":
			case "30005-03.htm":
			{
				htmltext = event;
				break;
			}
			case "30005-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30004-02.html":
			{
				qs.setCond(2, true);
				giveItems(player, GROCERY_SUPLLY_BOX, 1);
				htmltext = event;
				break;
			}
			case "30001-02.html":
			{
				qs.setCond(0);
				qs.setCond(3, true);
				giveItems(player, WEAPON_SUPLLY_BOX, 1);
				htmltext = event;
				break;
			}
			case "30002-02.html":
			{
				qs.setCond(4, true);
				giveItems(player, ARMOR_SUPLLY_BOX, 1);
				htmltext = event;
				break;
			}
			case "32166-02.html":
			{
				switch (player.getRace())
				{
					case HUMAN:
					{
						qs.setCond(5, true);
						break;
					}
					case ELF:
					{
						qs.setCond(6, true);
						break;
					}
					case DARK_ELF:
					{
						qs.setCond(7, true);
						break;
					}
					case ORC:
					{
						qs.setCond(8, true);
						break;
					}
					case DWARF:
					{
						qs.setCond(9, true);
						break;
					}
					case KAMAEL:
					{
						qs.setCond(10, true);
						break;
					}
				}
				giveItems(player, ACCESSORY_SUPPLY_BOX, 1);
				htmltext = event;
				break;
			}
			case "32153-02.html":
			{
				htmltext = getHtm(player, "32153-02.html").replace("%name%", npc.getName());
				break;
			}
			case "32153-03.html":
			{
				if (player.isMageClass())
				{
					giveItems(player, LEATHER_TUNIC, 1);
					giveItems(player, LEATHER_STOCKINGS, 1);
				}
				else if (!player.isMageClass())
				{
					giveItems(player, LEATHER_SHIRT, 1);
					giveItems(player, LEATHER_PANTS, 1);
				}
				giveItems(player, APPRENTICE_EARRING, 2);
				giveItems(player, NECKLACE_OF_KNOWNLEDGE, 1);
				addExpAndSp(player, 2630, 10);
				qs.exitQuest(false, true);
				htmltext = getHtm(player, "32153-03.html").replace("%name%", npc.getName());
				break;
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
			case State.CREATED:
			{
				if (npc.getId() == WILFORD)
				{
					htmltext = "30005-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case WILFORD:
					{
						if (qs.isCond(1))
						{
							htmltext = "30005-05.html";
						}
						break;
					}
					case KATERINA:
					{
						if (qs.isCond(1))
						{
							htmltext = "30004-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "30004-03.html";
						}
						break;
					}
					case LECTOR:
					{
						if (qs.isCond(2))
						{
							htmltext = "30001-01.html";
						}
						else if (qs.isCond(3))
						{
							htmltext = "30001-03.html";
						}
						break;
					}
					case JACKSON:
					{
						if (qs.isCond(3))
						{
							htmltext = "30002-01.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "30002-03.html";
						}
						break;
					}
					case TREVOR:
					{
						if (qs.isCond(4))
						{
							htmltext = "32166-01.html";
						}
						else if (qs.isCond(5))
						{
							htmltext = "32166-03.html";
						}
						break;
					}
					case FRANCO:
					{
						if (qs.isCond(5))
						{
							htmltext = (player.getRace() == Race.HUMAN) ? getHtm(player, "32153-01.html").replace("%name%", npc.getName()) : "32153-00.html";
						}
						break;
					}
					case RIVIAN:
					{
						if (qs.isCond(6))
						{
							htmltext = (player.getRace() == Race.ELF) ? getHtm(player, "32153-01.html").replace("%name%", npc.getName()) : "32147-00.html";
						}
						break;
					}
					case TOOK:
					{
						if (qs.isCond(8))
						{
							htmltext = (player.getRace() == Race.ORC) ? getHtm(player, "32153-01.html").replace("%name%", npc.getName()) : "32150-00.html";
						}
						break;
					}
					case DEVON:
					{
						if (qs.isCond(7))
						{
							htmltext = (player.getRace() == Race.DARK_ELF) ? getHtm(player, "32153-01.html").replace("%name%", npc.getName()) : "32160-00.html";
						}
						break;
					}
					case MOKA:
					{
						if (qs.isCond(9))
						{
							htmltext = (player.getRace() == Race.DWARF) ? getHtm(player, "32153-01.html").replace("%name%", npc.getName()) : "32157-00.html";
						}
						break;
					}
					case VALFAR:
					{
						if (qs.isCond(10))
						{
							htmltext = (player.getRace() == Race.KAMAEL) ? getHtm(player, "32153-01.html").replace("%name%", npc.getName()) : "32146-00.html";
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