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
package quests.Q10561_AcrossTheDeathLine;

import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.serverpackets.ExTutorialShowId;

import quests.Q10560_WayOfWanderingKnight.Q10560_WayOfWanderingKnight;

/**
 * Across The DeathLine (10561)
 * @URL https://l2wiki.com/Across_the_Death_Line
 * @author NightBR
 */
public class Q10561_AcrossTheDeathLine extends Quest
{
	// NPCs
	private static final int HERPHAH = 34362;
	private static final int PENNY = 34413;
	// Reward's
	private static final long EXP = 4409345453L;
	private static final int SP = 3968411;
	private static final int SOUL_SHOT_GRADE_R = 22433;
	private static final int BS_SHOT_GRADE_R = 22434;
	private static final int PA_ART_OF_SEDUCTION = 37928;
	private static final int TALISMAN_DESTRUCTION = 34985;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final int MAX_LEVEL = 99;
	
	public Q10561_AcrossTheDeathLine()
	{
		super(10561);
		addStartNpc(HERPHAH);
		addTalkId(HERPHAH, PENNY);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "nolevel.html");
		addCondCompletedQuest(Q10560_WayOfWanderingKnight.class.getSimpleName(), "34362-99.html");
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
			case "34362-02.htm":
			case "34362-03.htm":
			case "34362-05.html":
			case "34362-06.html":
			{
				htmltext = event;
				break;
			}
			case "34362-04.htm":
			{
				// show Service/Help/Death Penalty page
				player.sendPacket(new ExTutorialShowId(60));
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34362-07.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "34362-09.html":
			{
				// Rewards
				giveItems(player, SOUL_SHOT_GRADE_R, 2500);
				giveItems(player, BS_SHOT_GRADE_R, 2500);
				giveItems(player, PA_ART_OF_SEDUCTION, 5);
				giveItems(player, TALISMAN_DESTRUCTION, 1);
				addExpAndSp(player, EXP, SP);
				qs.exitQuest(QuestType.ONE_TIME, true);
				htmltext = event;
				break;
			}
			case "34413-02.html":
			{
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "34413-04.html":
			{
				qs.setCond(4, true);
				htmltext = event;
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
				if (npc.getId() == HERPHAH)
				{
					htmltext = "34362-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case HERPHAH:
					{
						if (qs.isCond(1))
						{
							htmltext = "34362-04.htm";
						}
						else if (qs.isCond(4))
						{
							htmltext = "34362-08.html";
						}
						else
						{
							htmltext = "34362-07.html";
						}
						break;
					}
					case PENNY:
					{
						if (qs.isCond(2))
						{
							htmltext = "34413-01.html";
						}
						else if (qs.isCond(3))
						{
							addFactionPoints(player, Faction.ADVENTURE_GUILD, 200);
							// TODO: we need to add reward % of amity points to factions in all faction quests
							// Checking if reached level 2 with Adventurer's Guild Faction
							if (player.getFactionLevel(Faction.ADVENTURE_GUILD) >= 2)
							{
								htmltext = "34413-03.html";
							}
							else
							{
								htmltext = "noAmity.html";
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