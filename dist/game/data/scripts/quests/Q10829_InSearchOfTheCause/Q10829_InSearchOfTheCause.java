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
package quests.Q10829_InSearchOfTheCause;

import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * In Search of the Cause (10829)
 * @URL https://l2wiki.com/In_Search_of_the_Cause
 * @author Gigi
 */
public class Q10829_InSearchOfTheCause extends Quest
{
	// NPC
	private static final int SERESIN = 30657;
	private static final int BELAS = 34056;
	private static final int FERIN = 34054;
	private static final int CYPHONIA = 34055;
	// Misc
	private static final int MIN_LEVEL = 100;
	private static final int SOE = 46158;
	
	public Q10829_InSearchOfTheCause()
	{
		super(10829);
		addStartNpc(SERESIN);
		addTalkId(SERESIN, BELAS, FERIN, CYPHONIA);
		addSeeCreatureId(BELAS);
		addCondMinLevel(MIN_LEVEL, "30657-00.htm");
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
			case "30657-02.htm":
			case "30657-03.htm":
			case "34054-02.html":
			{
				htmltext = event;
				break;
			}
			case "30657-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34054-03.html":
			{
				qs.setCond(2);
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "34055-03.html":
			{
				giveItems(player, SOE, 1);
				addExpAndSp(player, 55369440, 132885);
				qs.exitQuest(false, true);
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
				if (npc.getId() == SERESIN)
				{
					htmltext = "30657-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SERESIN:
					{
						if (qs.isCond(1))
						{
							htmltext = "30657-05.html";
							break;
						}
					}
					case BELAS:
					{
						if (qs.isCond(2))
						{
							htmltext = "34056-01.html";
						}
						else if (qs.getCond() > 2)
						{
							htmltext = "34056-02.html";
						}
						break;
					}
					case FERIN:
					{
						if (qs.isCond(2))
						{
							htmltext = "34054-01.html";
						}
						else if (qs.getCond() > 2)
						{
							htmltext = "34054-04.html";
						}
						break;
					}
					case CYPHONIA:
					{
						if ((qs.getCond() > 1) && (qs.getCond() < 4))
						{
							htmltext = "34055-01.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "34055-02.html";
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
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final QuestState qs = getQuestState((PlayerInstance) creature, true);
			if ((qs != null) && qs.isCond(1) && creature.isPlayer())
			{
				qs.setCond(2, true);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
}