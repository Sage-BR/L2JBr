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
package quests.Q10820_RelationshipsBefittingOfTheStatus;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10817_ExaltedOneWhoOvercomesTheLimit.Q10817_ExaltedOneWhoOvercomesTheLimit;

/**
 * Relationships Befitting of the Status (10820)
 * @URL https://l2wiki.com/Relationships_Befitting_of_the_Status
 * @author Mobius
 */
public class Q10820_RelationshipsBefittingOfTheStatus extends Quest
{
	// NPC
	private static final int ISHUMA = 32615;
	// Items
	private static final int CITRINE_PENDANT = 45640;
	private static final int CITRINE_PENDANT_FRAGMENT = 45639;
	private static final int CITRINE_PENDANT_RECIPE = 45643;
	private static final int DAICHIR_SERTIFICATE = 45628;
	private static final int OLYMPIAD_MANAGER_CERTIFICATE = 45629;
	private static final int SIR_KRISTOF_RODEMAI_CERTIFICATE = 45631;
	// Rewards
	private static final int ISHUMA_CERTIFICATE = 45630;
	private static final int ETERNAL_ARMOR_CRAFTING_PACK = 39324;
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10820_RelationshipsBefittingOfTheStatus()
	{
		super(10820);
		addStartNpc(ISHUMA);
		addTalkId(ISHUMA);
		addCondMinLevel(MIN_LEVEL, "32615-02.html");
		addCondStartedQuest(Q10817_ExaltedOneWhoOvercomesTheLimit.class.getSimpleName(), "32615-03.html");
		registerQuestItems(CITRINE_PENDANT, CITRINE_PENDANT_FRAGMENT, CITRINE_PENDANT_RECIPE);
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
			case "32615-04.htm":
			{
				htmltext = event;
				break;
			}
			case "32615-05.htm":
			{
				if (!player.isInCategory(CategoryType.WARSMITH_GROUP))
				{
					htmltext = "32615-06.htm";
				}
				else
				{
					htmltext = event;
				}
				break;
			}
			case "32615-07.html":
			{
				if (qs.isCreated())
				{
					giveItems(player, CITRINE_PENDANT_RECIPE, 1);
					giveItems(player, CITRINE_PENDANT_FRAGMENT, 10);
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "32615-09.html":
			{
				if (qs.isCond(1))
				{
					giveItems(player, CITRINE_PENDANT_RECIPE, 1);
					htmltext = event;
				}
				break;
			}
			case "32615-10.html":
			{
				if (qs.isCond(1))
				{
					giveItems(player, CITRINE_PENDANT_FRAGMENT, 10);
					htmltext = event;
				}
				break;
			}
			case "32615-12.html":
			{
				if (qs.isCond(1) && hasQuestItems(player, CITRINE_PENDANT))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						if (hasQuestItems(player, DAICHIR_SERTIFICATE, OLYMPIAD_MANAGER_CERTIFICATE, SIR_KRISTOF_RODEMAI_CERTIFICATE))
						{
							htmltext = "32615-13.html";
						}
						else
						{
							htmltext = event;
						}
						takeItems(player, CITRINE_PENDANT, -1);
						giveItems(player, ETERNAL_ARMOR_CRAFTING_PACK, 1);
						giveItems(player, ISHUMA_CERTIFICATE, 1);
						qs.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
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
				htmltext = "32615-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (hasQuestItems(player, CITRINE_PENDANT))
				{
					htmltext = "32615-11.html";
				}
				else
				{
					htmltext = "32615-08.html";
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