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
package quests.Q10302_UnsettlingShadowAndRumors;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10301_ShadowOfTerrorBlackishRedFog.Q10301_ShadowOfTerrorBlackishRedFog;

/**
 * Unsettling Shadow and Rumors (10302)
 * @author Gladicek
 */
public class Q10302_UnsettlingShadowAndRumors extends Quest
{
	// NPCs
	private static final int KANTARUBIS = 32898;
	private static final int IZAEL = 32894;
	private static final int CAS = 32901;
	private static final int MR_KAY = 32903;
	private static final int KITT = 32902;
	// Items
	private static final int OLD_ROLL_OF_PAPER = 34033;
	// Misc
	private static final int MIN_LEVEL = 88;
	
	public Q10302_UnsettlingShadowAndRumors()
	{
		super(10302);
		addStartNpc(KANTARUBIS);
		addTalkId(KANTARUBIS, IZAEL, CAS, MR_KAY, KITT);
		
		addCondMinLevel(MIN_LEVEL, "32898-10.html");
		addCondCompletedQuest(Q10301_ShadowOfTerrorBlackishRedFog.class.getSimpleName(), "32898-10.html");
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
			case "32898-02.htm":
			case "32898-06.html":
			case "32898-07.html":
			case "32894-05.html":
			{
				htmltext = event;
				break;
			}
			case "32898-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32894-02.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "32901-02.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "32903-02.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "32902-02.html":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "32894-06.html":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			default:
			{
				if (event.startsWith("giveReward_") && qs.isCond(6) && (player.getLevel() >= MIN_LEVEL))
				{
					final int itemId = Integer.parseInt(event.replace("giveReward_", ""));
					giveAdena(player, 2_177_190, false);
					giveItems(player, itemId, 15);
					giveItems(player, OLD_ROLL_OF_PAPER, 1);
					addExpAndSp(player, 6_728_850, 1_614);
					qs.exitQuest(false, true);
					htmltext = "32898-08.html";
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
				}
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
				if (npc.getId() == KANTARUBIS)
				{
					htmltext = "32898-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case KANTARUBIS:
					{
						if (qs.isCond(1))
						{
							htmltext = "32898-04.html";
						}
						else if (qs.isCond(6))
						{
							htmltext = "32898-05.html";
						}
						break;
					}
					case IZAEL:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "32894-01.html";
								break;
							}
							case 2:
							{
								htmltext = "32894-03.html";
								break;
							}
							case 5:
							{
								htmltext = "32894-04.html";
								break;
							}
							case 6:
							{
								htmltext = "32894-07.html";
								break;
							}
						}
						break;
					}
					case CAS:
					{
						if (qs.isCond(2))
						{
							htmltext = "32901-01.html";
						}
						else if (qs.isCond(3))
						{
							htmltext = "32901-03.html";
						}
						break;
					}
					case MR_KAY:
					{
						if (qs.isCond(3))
						{
							htmltext = "32903-01.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "32903-03.html";
						}
						break;
					}
					case KITT:
					{
						if (qs.isCond(4))
						{
							htmltext = "32902-01.html";
						}
						else if (qs.isCond(5))
						{
							htmltext = "32902-03.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == KANTARUBIS)
				{
					htmltext = "32898-09.html";
				}
				break;
			}
		}
		return htmltext;
	}
}