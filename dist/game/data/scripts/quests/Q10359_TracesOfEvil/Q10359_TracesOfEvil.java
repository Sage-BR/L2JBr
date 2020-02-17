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
package quests.Q10359_TracesOfEvil;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Traces of Evil (10359)
 * @author St3eT
 */
public class Q10359_TracesOfEvil extends Quest
{
	// NPCs
	private static final int ADVENTURER_GUIDE = 31795;
	private static final int FRED = 33179;
	private static final int RAYMOND = 30289;
	private static final int RAINS = 30288;
	private static final int TOBIAS = 30297;
	private static final int DRIKUS = 30505;
	private static final int MENDIO = 30504;
	private static final int GERSHWIN = 32196;
	private static final int ESRANDELL = 30158;
	private static final int ELLENIA = 30155;
	private static final int[] MONSTERS =
	{
		20067, // Monster Eye Watcher
		20070, // Lesser Basilisk
		20072, // Basilisk
		23097, // Skeleton Marauder
		23098, // Granite Golem
		23026, // Sahara
		20192, // Tyrant
	};
	// Items
	private static final int FRAGMENT = 17586; // Suspicious Fragment
	// Misc
	private static final int MIN_LEVEL = 34;
	private static final int MAX_LEVEL = 40;
	
	public Q10359_TracesOfEvil()
	{
		super(10359);
		addStartNpc(ADVENTURER_GUIDE);
		addTalkId(ADVENTURER_GUIDE, FRED, RAYMOND, RAINS, TOBIAS, DRIKUS, MENDIO, GERSHWIN, ESRANDELL, ELLENIA);
		addKillId(MONSTERS);
		registerQuestItems(FRAGMENT);
		addCondNotRace(Race.ERTHEIA, "31795-09.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "31795-08.htm");
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
			case "31795-02.htm":
			case "31795-03.htm":
			case "33179-02.htm":
			case "30297-09.html":
			case "30289-09.html":
			case "30288-09.html":
			case "30505-09.html":
			case "30504-09.html":
			case "30158-09.html":
			case "32196-09.html":
			case "30155-09.html":
			{
				htmltext = event;
				break;
			}
			case "31795-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33179-03.htm":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2);
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					htmltext = event;
				}
				break;
			}
			case "30297-10.html":
			case "30289-10.html":
			case "30288-10.html":
			case "30505-10.html":
			case "30504-10.html":
			case "30158-10.html":
			case "32196-10.html":
			case "30155-10.html":
			{
				if ((qs.getCond() >= 4) && (qs.getCond() <= 11))
				{
					addExpAndSp(player, 1800000, 216);
					qs.exitQuest(false, true);
					htmltext = event;
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
				if (npc.getId() == ADVENTURER_GUIDE)
				{
					htmltext = "31795-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ADVENTURER_GUIDE:
					{
						htmltext = "31795-06.htm";
						break;
					}
					case FRED:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "33179-01.htm";
								break;
							}
							case 2:
							{
								htmltext = "33179-04.htm";
								break;
							}
							case 3:
							{
								switch (player.getRace())
								{
									case HUMAN:
									{
										qs.setCond(player.isMageClass() ? 4 : 5);
										htmltext = player.isMageClass() ? "33179-06.htm" : "33179-11.htm";
										break;
									}
									case DARK_ELF:
									{
										qs.setCond(6);
										htmltext = "33179-05.htm";
										break;
									}
									case ORC:
									{
										qs.setCond(7);
										htmltext = "33179-07.htm";
										break;
									}
									case DWARF:
									{
										qs.setCond(8);
										htmltext = "33179-08.htm";
										break;
									}
									case KAMAEL:
									{
										qs.setCond(9);
										htmltext = "33179-09.htm";
										break;
									}
									case ELF:
									{
										qs.setCond(player.isMageClass() ? 11 : 10);
										htmltext = player.isMageClass() ? "33179-12.htm" : "33179-10.htm";
										break;
									}
								}
								takeItems(player, FRAGMENT, 20);
								playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
								break;
							}
						}
						break;
					}
					case RAYMOND:
					{
						if ((qs.getCond() >= 4) && (qs.getCond() <= 11))
						{
							switch (player.getRace())
							{
								case DARK_ELF:
								{
									htmltext = "30289-01.html";
									break;
								}
								case HUMAN:
								{
									htmltext = player.isMageClass() ? "30289-08.html" : "30289-02.html";
									break;
								}
								case ORC:
								{
									htmltext = "30289-03.html";
									break;
								}
								case DWARF:
								{
									htmltext = "30289-04.html";
									break;
								}
								case KAMAEL:
								{
									htmltext = "30289-05.html";
									break;
								}
								case ELF:
								{
									htmltext = player.isMageClass() ? "30289-06.html" : "30289-07.html";
									break;
								}
							}
						}
						break;
					}
					case RAINS:
					{
						if ((qs.getCond() >= 4) && (qs.getCond() <= 11))
						{
							switch (player.getRace())
							{
								case DARK_ELF:
								{
									htmltext = "30288-01.html";
									break;
								}
								case HUMAN:
								{
									htmltext = player.isMageClass() ? "30288-02.html" : "30288-08.html";
									break;
								}
								case ORC:
								{
									htmltext = "30288-03.html";
									break;
								}
								case DWARF:
								{
									htmltext = "30288-04.html";
									break;
								}
								case KAMAEL:
								{
									htmltext = "30288-05.html";
									break;
								}
								case ELF:
								{
									htmltext = player.isMageClass() ? "30288-06.html" : "30288-07.html";
									break;
								}
							}
						}
						break;
					}
					case TOBIAS:
					{
						if ((qs.getCond() >= 4) && (qs.getCond() <= 11))
						{
							switch (player.getRace())
							{
								case DARK_ELF:
								{
									htmltext = "30297-08.html";
									break;
								}
								case HUMAN:
								{
									htmltext = player.isMageClass() ? "30297-01.html" : "30297-02.html";
									break;
								}
								case ORC:
								{
									htmltext = "30297-03.html";
									break;
								}
								case DWARF:
								{
									htmltext = "30297-04.html";
									break;
								}
								case KAMAEL:
								{
									htmltext = "30297-05.html";
									break;
								}
								case ELF:
								{
									htmltext = player.isMageClass() ? "30297-06.html" : "30297-07.html";
									break;
								}
							}
						}
						break;
					}
					case DRIKUS:
					{
						if ((qs.getCond() >= 4) && (qs.getCond() <= 11))
						{
							switch (player.getRace())
							{
								case DARK_ELF:
								{
									htmltext = "30505-01.html";
									break;
								}
								case HUMAN:
								{
									htmltext = player.isMageClass() ? "30505-02.html" : "30505-03.html";
									break;
								}
								case ORC:
								{
									htmltext = "30505-08.html";
									break;
								}
								case DWARF:
								{
									htmltext = "30505-04.html";
									break;
								}
								case KAMAEL:
								{
									htmltext = "30505-05.html";
									break;
								}
								case ELF:
								{
									htmltext = player.isMageClass() ? "30505-06.html" : "30505-07.html";
									break;
								}
							}
						}
						break;
					}
					case MENDIO:
					{
						if ((qs.getCond() >= 4) && (qs.getCond() <= 11))
						{
							switch (player.getRace())
							{
								case DARK_ELF:
								{
									htmltext = "30504-01.html";
									break;
								}
								case HUMAN:
								{
									htmltext = player.isMageClass() ? "30504-02.html" : "30504-03.html";
									break;
								}
								case ORC:
								{
									htmltext = "30504-04.html";
									break;
								}
								case DWARF:
								{
									htmltext = "30504-08.html";
									break;
								}
								case KAMAEL:
								{
									htmltext = "30504-05.html";
									break;
								}
								case ELF:
								{
									htmltext = player.isMageClass() ? "30504-06.html" : "30504-07.html";
									break;
								}
							}
						}
						break;
					}
					case GERSHWIN:
					{
						if ((qs.getCond() >= 4) && (qs.getCond() <= 11))
						{
							switch (player.getRace())
							{
								case DARK_ELF:
								{
									htmltext = "32196-01.html";
									break;
								}
								case HUMAN:
								{
									htmltext = player.isMageClass() ? "32196-02.html" : "32196-03.html";
									break;
								}
								case ORC:
								{
									htmltext = "32196-04.html";
									break;
								}
								case DWARF:
								{
									htmltext = "32196-05.html";
									break;
								}
								case KAMAEL:
								{
									htmltext = "32196-08.html";
									break;
								}
								case ELF:
								{
									htmltext = player.isMageClass() ? "32196-06.html" : "32196-07.html";
									break;
								}
							}
						}
						break;
					}
					case ESRANDELL:
					{
						if ((qs.getCond() >= 4) && (qs.getCond() <= 11))
						{
							switch (player.getRace())
							{
								case DARK_ELF:
								{
									htmltext = "30158-01.html";
									break;
								}
								case HUMAN:
								{
									htmltext = player.isMageClass() ? "30158-02.html" : "30158-03.html";
									break;
								}
								case ORC:
								{
									htmltext = "30158-04.html";
									break;
								}
								case DWARF:
								{
									htmltext = "30158-05.html";
									break;
								}
								case KAMAEL:
								{
									htmltext = "30158-06.html";
									break;
								}
								case ELF:
								{
									htmltext = player.isMageClass() ? "30158-08.html" : "30158-07.html";
									break;
								}
							}
						}
						break;
					}
					case ELLENIA:
					{
						if ((qs.getCond() >= 4) && (qs.getCond() <= 11))
						{
							switch (player.getRace())
							{
								case DARK_ELF:
								{
									htmltext = "30155-01.html";
									break;
								}
								case HUMAN:
								{
									htmltext = player.isMageClass() ? "30155-02.html" : "30155-03.html";
									break;
								}
								case ORC:
								{
									htmltext = "30155-04.html";
									break;
								}
								case DWARF:
								{
									htmltext = "30155-05.html";
									break;
								}
								case KAMAEL:
								{
									htmltext = "30155-06.html";
									break;
								}
								case ELF:
								{
									htmltext = player.isMageClass() ? "30155-07.html" : "30155-08.html";
									break;
								}
							}
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
					case ADVENTURER_GUIDE:
					{
						htmltext = "31795-07.html";
						break;
					}
					case FRED:
					{
						htmltext = "33179-13.html";
						break;
					}
					case RAYMOND:
					case RAINS:
					case TOBIAS:
					case DRIKUS:
					case MENDIO:
					case GERSHWIN:
					case ESRANDELL:
					case ELLENIA:
					{
						htmltext = npc.getId() + "-11.html";
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
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isStarted() && qs.isCond(2))
		{
			if (getRandom(100) < 40)
			{
				giveItems(killer, FRAGMENT, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			
			if (getQuestItemsCount(killer, FRAGMENT) == 20)
			{
				playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				qs.setCond(3);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}