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
package quests.Q00831_SayhasScheme;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Sayha's Scheme (831)
 * @URL https://l2wiki.com/Sayha%27s_Scheme
 * @author Liamxroy
 */
public class Q00831_SayhasScheme extends Quest
{
	// NPC
	private static final int YUYURIA = 34100;
	private static final int YUYURIA_FINISH = 34155;
	private static final int ALTAR = 34103;
	// Items
	private static final int DESTROYED_MARK_FRAGMENT = 46374;
	private static final int GLUDIN_HERO_REWARD = 46375;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q00831_SayhasScheme()
	{
		super(831);
		addStartNpc(YUYURIA);
		addTalkId(YUYURIA, YUYURIA_FINISH);
		addKillId(ALTAR);
		addCondMinLevel(MIN_LEVEL, "34100-00.htm");
		registerQuestItems(DESTROYED_MARK_FRAGMENT);
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
			case "34100-02.htm":
			case "34100-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34100-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34155-02.html":
			{
				if (qs.isCond(2))
				{
					takeItems(player, -1, DESTROYED_MARK_FRAGMENT);
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
				if (npc.getId() == YUYURIA)
				{
					htmltext = "34100-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == YUYURIA)
				{
					htmltext = "34100-05.html";
				}
				else
				{
					htmltext = "34155-01.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable() && (npc.getId() == YUYURIA))
				{
					qs.setState(State.CREATED);
					htmltext = "34100-01.htm";
				}
				else
				{
					htmltext = "34100-06.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
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
			final QuestState qs = getQuestState(member, false);
			if ((qs != null) && qs.isCond(1) && member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
			{
				if (giveItemRandomly(member, npc, DESTROYED_MARK_FRAGMENT, 1, 10, 1.0, true))
				{
					qs.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
