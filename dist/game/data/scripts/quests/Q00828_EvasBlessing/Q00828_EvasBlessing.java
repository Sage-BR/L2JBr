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
package quests.Q00828_EvasBlessing;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExQuestNpcLogList;

/**
 * Eva's Blessing (828)
 * @URL https://l2wiki.com/Eva%27s_Blessing
 * @author Liamxroy
 */
public class Q00828_EvasBlessing extends Quest
{
	// NPC
	private static final int ADONIUS = 34097;
	private static final int ADONIUS_FINISH = 34152;
	private static final int[] CAPTIVES =
	{
		34104,
		34105,
		34106,
		34107,
		34108,
		34109,
		34110,
		34111,
		34112,
		34113,
		34114,
		34115,
		34116,
		34117,
		34118,
		34119,
		34120,
		34121,
		34122,
		34123,
		34124,
		34125,
		34126,
		34127,
		34128,
		34129,
		34130,
		34131,
		34132,
		34133,
		34134,
		34135,
	};
	// Items
	private static final int GLUDIN_HERO_REWARD = 46375;
	// Misc
	private static final NpcStringId[] CAPTIVES_TEXT =
	{
		NpcStringId.WHAT_WHO_ARE_YOU,
		NpcStringId.WE_MUST_ALERT_THE_COMMANDER_ABOUT_THESE_INTRUDERS,
		NpcStringId.ALERT_EVERYONE,
	};
	private static final int MIN_LEVEL = 100;
	
	public Q00828_EvasBlessing()
	{
		super(828);
		addStartNpc(ADONIUS);
		addFirstTalkId(CAPTIVES);
		addTalkId(ADONIUS, ADONIUS_FINISH);
		addCondMinLevel(MIN_LEVEL, "34097-00.htm");
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
			case "34097-02.htm":
			case "34097-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34097-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34152-02.html":
			{
				if (qs.isCond(2))
				{
					rewardItems(player, GLUDIN_HERO_REWARD, 1);
					addExpAndSp(player, 2422697985L, 5814450);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
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
				if (npc.getId() == ADONIUS)
				{
					htmltext = "34097-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == ADONIUS)
				{
					htmltext = "34097-05.html";
				}
				else
				{
					htmltext = "34152-01.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable() && (npc.getId() == ADONIUS))
				{
					qs.setState(State.CREATED);
					htmltext = "34097-01.htm";
				}
				else
				{
					htmltext = "34097-06.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && (qs.isCond(1)))
		{
			List<PlayerInstance> members = new ArrayList<>();
			if (player.getParty() != null)
			{
				members = player.getParty().getMembers();
			}
			else
			{
				members.add(player);
			}
			for (PlayerInstance member : members)
			{
				final QuestState ms = getQuestState(member, false);
				if ((ms != null) && ms.isCond(1))
				{
					int count = ms.getMemoState();
					count++;
					if (count < 20)
					{
						ms.setMemoState(count);
						final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
						log.addNpcString(NpcStringId.RESCUING_CAPTIVES, count);
						member.sendPacket(log);
					}
					if (count >= 20)
					{
						ms.setCond(2, true);
					}
				}
			}
			npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(CAPTIVES_TEXT));
			npc.deleteMe();
			return "captive-0" + getRandom(1, 3) + ".html";
		}
		return null;
	}
}
