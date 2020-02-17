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
package quests.Q10390_KekropusLetter;

import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;
import org.l2jbr.gameserver.network.serverpackets.TutorialShowHtml;

import quests.Q10360_CertificationOfFate.Q10360_CertificationOfFate;

/**
 * Kekropus' Letter (10390)
 * @author St3eT
 */
public class Q10390_KekropusLetter extends Quest
{
	// NPCs
	private static final int RAYMOND = 30289;
	private static final int RAINS = 30288;
	private static final int TOBIAS = 30297;
	private static final int DRIKUS = 30505;
	private static final int MENDIO = 30504;
	private static final int GERSHWIN = 32196;
	private static final int ESRANDELL = 30158;
	private static final int ELLENIA = 30155;
	private static final int BATHIS = 30332;
	private static final int GOSTA = 30916;
	private static final int ELI = 33858;
	private static final int INVISIBLE_NPC = 19543;
	// Items
	private static final int KEKROPUS_LETTER = 36706;
	private static final int HAINE_SOE = 37112; // Scroll of Escape: Heine
	private static final int ALLIGATOR_ISLAND_SOE = 37025; // Scroll of Escape: Alligator Island
	// Misc
	private static final int MIN_LEVEL = 40;
	private static final int MAX_LEVEL = 45;
	
	public Q10390_KekropusLetter()
	{
		super(10390);
		addStartNpc(RAYMOND, RAINS, TOBIAS, DRIKUS, MENDIO, GERSHWIN, ESRANDELL, ELLENIA);
		addTalkId(RAYMOND, RAINS, TOBIAS, DRIKUS, MENDIO, GERSHWIN, ESRANDELL, ELLENIA, BATHIS, GOSTA, ELI);
		addSeeCreatureId(INVISIBLE_NPC);
		registerQuestItems(KEKROPUS_LETTER, HAINE_SOE, ALLIGATOR_ISLAND_SOE);
		addCondCompletedQuest(Q10360_CertificationOfFate.class.getSimpleName(), "");
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
			case "30289-03.htm":
			case "30288-03.htm":
			case "30297-03.htm":
			case "30505-03.htm":
			case "30504-03.htm":
			case "32196-03.htm":
			case "30158-03.htm":
			case "30155-03.htm":
			case "30916-02.html":
			{
				htmltext = event;
				break;
			}
			case "30289-04.htm":
			case "30288-04.htm":
			case "30297-04.htm":
			case "30505-04.htm":
			case "30504-04.htm":
			case "32196-04.htm":
			case "30158-04.htm":
			case "30155-04.htm":
			{
				qs.startQuest();
				player.sendPacket(new PlaySound(3, "Npcdialog1.kekrops_quest_1", 0, 0, 0, 0, 0));
				htmltext = event;
				break;
			}
			case "30332-09.html":
			{
				if (qs.isCond(1))
				{
					giveItems(player, KEKROPUS_LETTER, 1);
					htmltext = event;
				}
				break;
			}
			case "popup-letter.html":
			{
				if (qs.isCond(1))
				{
					player.sendPacket(new TutorialShowHtml(getHtm(player, event)));
					qs.setCond(2, true);
				}
				break;
			}
			case "30332-11.html":
			{
				if (qs.isCond(2))
				{
					takeItems(player, KEKROPUS_LETTER, -1);
					giveItems(player, HAINE_SOE, 1);
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30916-03.html":
			{
				if (qs.isCond(3))
				{
					giveItems(player, ALLIGATOR_ISLAND_SOE, 1);
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "33858-02.html":
			{
				if (qs.isCond(4))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 370440, 88);
					qs.exitQuest(false, true);
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_46, ExShowScreenMessage.TOP_CENTER, 6000);
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
				switch (npc.getId())
				{
					case RAYMOND:
					case ESRANDELL:
					case RAINS:
					case ELLENIA:
					case TOBIAS:
					case DRIKUS:
					case MENDIO:
					case GERSHWIN:
					{
						if ((player.getLevel() < MIN_LEVEL) || (player.getLevel() > MAX_LEVEL))
						{
							htmltext = npc.getId() + "-06.html";
						}
						else
						{
							htmltext = isRightMaster(npc, player) ? npc.getId() + "-02.htm" : npc.getId() + "-01.htm";
						}
						break;
					}
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case RAYMOND:
					case ESRANDELL:
					case RAINS:
					case ELLENIA:
					case TOBIAS:
					case DRIKUS:
					case MENDIO:
					case GERSHWIN:
					{
						if (qs.isCond(1))
						{
							htmltext = npc.getId() + "-05.html";
						}
						break;
					}
					case BATHIS:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								switch (player.getRace())
								{
									case HUMAN:
									{
										htmltext = player.isMageClass() ? "30332-01.html" : "30332-02.html";
										break;
									}
									case DARK_ELF:
									{
										htmltext = "30332-03.html";
										break;
									}
									case ORC:
									{
										htmltext = "30332-04.html";
										break;
									}
									case DWARF:
									{
										htmltext = "30332-05.html";
										break;
									}
									case KAMAEL:
									{
										htmltext = "30332-06.html";
										break;
									}
									case ELF:
									{
										htmltext = player.isMageClass() ? "30332-08.html" : "30332-07.html";
										break;
									}
								}
								break;
							}
							case 2:
							{
								htmltext = "30332-10.html";
								break;
							}
							case 3:
							{
								htmltext = "30332-11.html";
								break;
							}
						}
						break;
					}
					case GOSTA:
					{
						if (qs.isCond(3))
						{
							htmltext = "30916-01.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "30916-04.html";
						}
						break;
					}
					case ELI:
					{
						if (qs.isCond(4))
						{
							htmltext = "33858-01.html";
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
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			final QuestState qs = getQuestState(player, false);
			
			if ((qs != null) && qs.isCond(4))
			{
				showOnScreenMsg(player, NpcStringId.ALLIGATOR_ISLAND_IS_A_GOOD_HUNTING_ZONE_FOR_LV_40_OR_ABOVE, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	private boolean isRightMaster(Npc npc, PlayerInstance player)
	{
		switch (npc.getId())
		{
			case RAYMOND:
			case ESRANDELL:
			{
				if ((npc.getRace() == player.getRace()) && player.isMageClass())
				{
					return true;
				}
				break;
			}
			case RAINS:
			case ELLENIA:
			{
				if ((npc.getRace() == player.getRace()) && !player.isMageClass())
				{
					return true;
				}
				break;
			}
			case TOBIAS:
			case DRIKUS:
			case MENDIO:
			case GERSHWIN:
			{
				if (npc.getRace() == player.getRace())
				{
					return true;
				}
				break;
			}
		}
		return false;
	}
}