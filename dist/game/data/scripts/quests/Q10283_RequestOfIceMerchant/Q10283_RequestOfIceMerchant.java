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
package quests.Q10283_RequestOfIceMerchant;

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q00115_TheOtherSideOfTruth.Q00115_TheOtherSideOfTruth;

/**
 * Request of Ice Merchant (10283)
 * @author Gnacik
 * @version 2013-02-07 Updated to High Five
 */
public class Q10283_RequestOfIceMerchant extends Quest
{
	// NPCs
	private static final int RAFFORTY = 32020;
	private static final int KIER = 32022;
	private static final int JINIA = 32760;
	// Location
	private static final Location MOVE_TO_END = new Location(104457, -107010, -3698, 0);
	// Misc
	private boolean _jiniaOnSpawn = false;
	
	public Q10283_RequestOfIceMerchant()
	{
		super(10283);
		addStartNpc(RAFFORTY);
		addTalkId(RAFFORTY, KIER, JINIA);
		addFirstTalkId(JINIA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		if (npc.getId() == RAFFORTY)
		{
			if (event.equalsIgnoreCase("32020-03.htm"))
			{
				qs.startQuest();
			}
			else if (event.equalsIgnoreCase("32020-07.htm"))
			{
				qs.setCond(2, true);
			}
		}
		else if ((npc.getId() == KIER) && event.equalsIgnoreCase("spawn"))
		{
			if (_jiniaOnSpawn)
			{
				htmltext = "32022-02.html";
			}
			else
			{
				addSpawn(JINIA, 104473, -107549, -3695, 44954, false, 180000);
				_jiniaOnSpawn = true;
				startQuestTimer("despawn", 180000, npc, player);
				return null;
			}
		}
		else if (event.equalsIgnoreCase("despawn"))
		{
			_jiniaOnSpawn = false;
			return null;
		}
		else if ((npc.getId() == JINIA) && event.equalsIgnoreCase("32760-04.html"))
		{
			giveAdena(player, 190000, true);
			addExpAndSp(player, 627000, 50300);
			qs.exitQuest(false, true);
			npc.setRunning();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, MOVE_TO_END);
			npc.decayMe();
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		if (npc.isInInstance())
		{
			return "32760-10.html";
		}
		
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(2))
		{
			return "32760-01.html";
		}
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (npc.getId())
		{
			case RAFFORTY:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						final QuestState _prev = player.getQuestState(Q00115_TheOtherSideOfTruth.class.getSimpleName());
						htmltext = ((_prev != null) && _prev.isCompleted() && (player.getLevel() >= 82)) ? "32020-01.htm" : "32020-00.htm";
						break;
					}
					case State.STARTED:
					{
						if (qs.isCond(1))
						{
							htmltext = "32020-04.htm";
						}
						else if (qs.isCond(2))
						{
							htmltext = "32020-08.htm";
						}
						break;
					}
					case State.COMPLETED:
					{
						htmltext = "32020-09.htm";
						break;
					}
				}
				break;
			}
			case KIER:
			{
				if (qs.isCond(2))
				{
					htmltext = "32022-01.html";
				}
				break;
			}
			case JINIA:
			{
				if (qs.isCond(2))
				{
					htmltext = "32760-02.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
