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
package quests.Q10743_StrangeFungus;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Strange Fungus (10743)
 * @author Sdw
 */
public class Q10743_StrangeFungus extends Quest
{
	// NPCs
	private static final int LEIRA = 33952;
	private static final int MILONE = 33953;
	// Monsters
	private static final int GROWLER = 23455;
	private static final int ROBUST_GROWLER = 23486;
	private static final int EVOLVED_GROWLER = 23456;
	// Items
	private static final int PECULIAR_MUSHROOM_SPORE = 39530;
	private static final ItemHolder LEATHER_SHOES = new ItemHolder(37, 1);
	// Misc
	private static final int MIN_LEVEL = 13;
	private static final int MAX_LEVEL = 20;
	private static final String EVOLVED_SPAWN_VAR = "EvolvedSpawn";
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10743_StrangeFungus()
	{
		super(10743);
		addStartNpc(LEIRA);
		addTalkId(LEIRA, MILONE);
		addKillId(GROWLER, ROBUST_GROWLER, EVOLVED_GROWLER);
		
		addCondRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33952-00.htm");
		registerQuestItems(PECULIAR_MUSHROOM_SPORE);
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
			case "33952-02.htm":
			case "33953-02.html":
			{
				break;
			}
			case "33952-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "33953-03.html":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 93982, 0);
					giveItems(player, LEATHER_SHOES);
					showOnScreenMsg(player, NpcStringId.CHECK_YOUR_EQUIPMENT_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 10000);
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
		
		switch (npc.getId())
		{
			case LEIRA:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						htmltext = "33952-01.htm";
						break;
					}
					case State.STARTED:
					{
						htmltext = (qs.isCond(1)) ? "33952-04.html" : "33952-05.html";
						break;
					}
					case State.COMPLETED:
					{
						htmltext = getAlreadyCompletedMsg(player);
						break;
					}
				}
				break;
			}
			case MILONE:
			{
				if (qs.isStarted() && qs.isCond(2))
				{
					htmltext = "33953-01.html";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && (getQuestItemsCount(killer, PECULIAR_MUSHROOM_SPORE) < 10))
		{
			switch (npc.getId())
			{
				case GROWLER:
				case ROBUST_GROWLER:
				{
					final int killCount = qs.getInt(EVOLVED_SPAWN_VAR) + 1;
					if (killCount >= 3)
					{
						addAttackPlayerDesire(addSpawn(EVOLVED_GROWLER, npc.getLocation()), killer);
						qs.set(EVOLVED_SPAWN_VAR, 0);
					}
					else
					{
						qs.set(EVOLVED_SPAWN_VAR, killCount);
					}
					qs.set(KILL_COUNT_VAR, qs.getInt(KILL_COUNT_VAR) + 1);
					break;
				}
				case EVOLVED_GROWLER:
				{
					if (giveItemRandomly(killer, npc, PECULIAR_MUSHROOM_SPORE, 1, 10, 1.0, true))
					{
						qs.setCond(2);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>();
				holder.add(new NpcLogListHolder(GROWLER, false, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}
