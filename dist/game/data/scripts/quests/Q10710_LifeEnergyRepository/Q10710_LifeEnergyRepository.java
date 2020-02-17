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
package quests.Q10710_LifeEnergyRepository;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10406_BeforeDarknessBearsFruit.Q10406_BeforeDarknessBearsFruit;

/**
 * Life Energy Repository (10710)
 * @author St3eT
 */
public class Q10710_LifeEnergyRepository extends Quest
{
	// NPCs
	private static final int SHUVANN = 33867;
	private static final int LIFE_ENERGY = 33961; // Life Energy Repository
	private static final int EMBRYO = 27521;
	// Items
	private static final int FRAGMENT = 39512; // Shine Stone Fragment
	// Locations
	private static final Location[] EMBRYO_LOC =
	{
		new Location(177832, -14365, -2464),
		new Location(177531, -14191, -2464),
		new Location(177746, -14364, -2464),
		new Location(177658, -14223, -2464),
		new Location(177555, -14281, -2464),
	};
	// Misc
	private static final int MIN_LEVEL = 61;
	private static final int MAX_LEVEL = 65;
	
	public Q10710_LifeEnergyRepository()
	{
		super(10710);
		addStartNpc(SHUVANN);
		addTalkId(SHUVANN, LIFE_ENERGY);
		registerQuestItems(FRAGMENT);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33867-08.htm");
		addCondCompletedQuest(Q10406_BeforeDarknessBearsFruit.class.getSimpleName(), "33867-08.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		
		if (event.equals("action"))
		{
			if ((qs != null) && qs.isCond(1))
			{
				qs.setCond(2, true);
				giveItems(player, FRAGMENT, 1);
				
				for (Location loc : EMBRYO_LOC)
				{
					final Npc embryo = addSpawn(EMBRYO, loc, false, 120000);
					embryo.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_REPOSITORY_IS_ATTACKED_FIGHT_FIGHT);
					addAttackPlayerDesire(embryo, player);
				}
			}
			else
			{
				return "33962.html";
			}
		}
		
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33867-02.htm":
			case "33867-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33867-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33867-07.html":
			{
				if (qs.isCond(2))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(npc, player);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 15207327, 750);
					}
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
				if (npc.getId() == SHUVANN)
				{
					htmltext = "33867-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == SHUVANN)
				{
					htmltext = qs.isCond(1) ? "33867-05.html" : "33867-06.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == SHUVANN)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
}