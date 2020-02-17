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
package quests.Q10836_DisappearedClanMember;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.Id;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerItemAdd;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Disappeared Clan Member (10836)
 * @URL https://l2wiki.com/Disappeared_Clan_Member
 * @author Gigi
 */
public class Q10836_DisappearedClanMember extends Quest
{
	// NPC
	private static final int ELIKIA = 34057;
	// Items
	private static final int BLACKBIRD_SEAL = 46132;
	private static final int BLACKBIRD_REPORT_GLENKINCHIE = 46134;
	private static final int BLACKBIRD_REPORT_HURAK = 46135;
	private static final int BLACKBIRD_REPORT_LAFFIAN = 46136;
	private static final int BLACKBIRD_REPORT_SHERRY = 46137;
	// Misc
	private static final int MIN_LEVEL = 101;
	private static final int EAR = 17527;
	private static final int ELEXIR_OF_LIFE_R = 37097;
	private static final int ELEXIR_OF_MIND_R = 37098;
	
	public Q10836_DisappearedClanMember()
	{
		super(10836);
		addStartNpc(ELIKIA);
		addTalkId(ELIKIA);
		registerQuestItems(BLACKBIRD_SEAL, BLACKBIRD_REPORT_GLENKINCHIE, BLACKBIRD_REPORT_HURAK, BLACKBIRD_REPORT_LAFFIAN, BLACKBIRD_REPORT_SHERRY);
		addCondMinLevel(MIN_LEVEL, "34057-00.htm");
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
			case "34057-02.htm":
			case "34057-03.htm":
			case "34057-07.html":
			{
				htmltext = event;
				break;
			}
			case "34057-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34057-08.html":
			{
				giveItems(player, EAR, 5);
				giveItems(player, ELEXIR_OF_LIFE_R, 10);
				giveItems(player, ELEXIR_OF_MIND_R, 10);
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
				htmltext = "34057-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "34057-05.html";
				}
				else
				{
					htmltext = "34057-06.html";
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
	
	@RegisterEvent(EventType.ON_PLAYER_ITEM_ADD)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(BLACKBIRD_REPORT_GLENKINCHIE)
	@Id(BLACKBIRD_REPORT_HURAK)
	@Id(BLACKBIRD_REPORT_LAFFIAN)
	@Id(BLACKBIRD_REPORT_SHERRY)
	public void onItemAdd(OnPlayerItemAdd event)
	{
		final PlayerInstance player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && (qs.isCond(1)) && (hasQuestItems(player, BLACKBIRD_REPORT_GLENKINCHIE)) && (hasQuestItems(player, BLACKBIRD_REPORT_HURAK)) && (hasQuestItems(player, BLACKBIRD_REPORT_LAFFIAN)) && (hasQuestItems(player, BLACKBIRD_REPORT_SHERRY)))
		{
			qs.setCond(2, true);
		}
	}
}