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
package quests.Q10764_FreeSpirit;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10763_TerrifyingChertuba.Q10763_TerrifyingChertuba;

/**
 * Free Spirit (10764)
 * @author malyelfik
 */
public class Q10764_FreeSpirit extends Quest
{
	// NPC
	private static final int VORBOS = 33966;
	private static final int TREE_SPIRIT = 33964;
	private static final int WIND_SPIRIT = 33965;
	private static final int SYLPH = 33967;
	private static final int LIBERATED_WIND_SPIRIT = 33968;
	private static final int LIBERATED_TREE_SPIRIT = 33969;
	// Items
	private static final int MAGIC_CHAIN_KEY_BUNDLE = 39490;
	private static final int LOOSENED_CHAIN = 39518;
	// Location
	private static final Location SYLPH_LOCATION = new Location(-85001, 106057, -3592);
	// Misc
	private static final int MIN_LEVEL = 38;
	
	public Q10764_FreeSpirit()
	{
		super(10764);
		addStartNpc(VORBOS);
		addTalkId(VORBOS, TREE_SPIRIT, WIND_SPIRIT);
		addSpawnId(LIBERATED_TREE_SPIRIT, LIBERATED_WIND_SPIRIT, SYLPH);
		
		addCondRace(Race.ERTHEIA, "33966-00.htm");
		addCondMinLevel(MIN_LEVEL, "33966-00.htm");
		addCondCompletedQuest(Q10763_TerrifyingChertuba.class.getSimpleName(), "33966-00.htm");
		registerQuestItems(MAGIC_CHAIN_KEY_BUNDLE, LOOSENED_CHAIN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33966-02.htm":
			{
				break;
			}
			case "33966-03.htm":
			{
				qs.startQuest();
				giveItems(player, MAGIC_CHAIN_KEY_BUNDLE, 10);
				break;
			}
			case "33966-06.html":
			{
				if (qs.isCond(2))
				{
					addSpawn(SYLPH, SYLPH_LOCATION, false, 4000);
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 1312934, 315);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == VORBOS)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "33966-01.htm";
					break;
				}
				case State.STARTED:
				{
					htmltext = (qs.isCond(1)) ? "33966-04.html" : "33966-05.html";
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if (qs.isStarted() && qs.isCond(1))
		{
			final int npcId = (npc.getId() == WIND_SPIRIT) ? LIBERATED_WIND_SPIRIT : LIBERATED_TREE_SPIRIT;
			
			giveItems(player, LOOSENED_CHAIN, 1);
			addSpawn(npcId, npc, false, 2500);
			npc.deleteMe();
			
			if (getQuestItemsCount(player, LOOSENED_CHAIN) >= 10)
			{
				qs.setCond(2, true);
			}
			htmltext = null;
		}
		else
		{
			htmltext = npc.getId() + "-01.html";
		}
		return htmltext;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (npc.getId() == SYLPH)
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THANK_YOU_YOU_ARE_KIND);
		}
		else
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THANK_YOU_THANK_YOU_FOR_HELPING);
		}
		return super.onSpawn(npc);
	}
}
