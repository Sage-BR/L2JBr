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
package quests.Q00493_KickingOutUnwelcomeGuests;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Kicking Out Unwelcome Guests (493)
 * @author St3eT
 */
public class Q00493_KickingOutUnwelcomeGuests extends Quest
{
	// NPCs
	private static final int GEORGIO = 33515;
	private static final int LUNATIC_CREATURE = 23148;
	private static final int RESURRECTED_CREATION = 23147;
	private static final int UNDEAD_CREATURE = 23149;
	private static final int SHILEN_MESSENGER = 23151;
	private static final int HELLISH_CREATURE = 23150;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q00493_KickingOutUnwelcomeGuests()
	{
		super(493);
		addStartNpc(GEORGIO);
		addTalkId(GEORGIO);
		addKillId(LUNATIC_CREATURE, RESURRECTED_CREATION, UNDEAD_CREATURE, SHILEN_MESSENGER, HELLISH_CREATURE);
		addCondMinLevel(MIN_LEVEL, "33515-10.htm");
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
			case "33515-02.htm":
			case "33515-03.htm":
			case "33515-04.htm":
			{
				htmltext = event;
				break;
			}
			case "33515-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33515-08.html":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 2926255980L, 2926200);
					giveAdena(player, 1187940, true);
					qs.exitQuest(QuestType.DAILY, true);
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
		
		if (npc.getId() == GEORGIO)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "33515-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33515-06.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33515-07.html";
					}
					break;
				}
				case State.COMPLETED:
				{
					if (qs.isNowAvailable())
					{
						qs.setState(State.CREATED);
						htmltext = "33515-01.htm";
					}
					else
					{
						htmltext = "33515-09.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int killedCount = qs.getInt(Integer.toString(npc.getId()));
			if (killedCount < 50)
			{
				qs.set(Integer.toString(npc.getId()), killedCount + 1);
			}
			
			final int killedLunatic = qs.getInt(Integer.toString(LUNATIC_CREATURE));
			final int killedRessurected = qs.getInt(Integer.toString(RESURRECTED_CREATION));
			final int killedUndead = qs.getInt(Integer.toString(UNDEAD_CREATURE));
			final int killedMessenger = qs.getInt(Integer.toString(SHILEN_MESSENGER));
			final int killedHellish = qs.getInt(Integer.toString(HELLISH_CREATURE));
			
			if ((killedLunatic == 50) && (killedRessurected == 50) && (killedUndead == 50) && (killedMessenger == 50) && (killedHellish == 50))
			{
				qs.setCond(2, true);
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(5);
			npcLogList.add(new NpcLogListHolder(LUNATIC_CREATURE, false, qs.getInt(Integer.toString(LUNATIC_CREATURE))));
			npcLogList.add(new NpcLogListHolder(RESURRECTED_CREATION, false, qs.getInt(Integer.toString(RESURRECTED_CREATION))));
			npcLogList.add(new NpcLogListHolder(UNDEAD_CREATURE, false, qs.getInt(Integer.toString(UNDEAD_CREATURE))));
			npcLogList.add(new NpcLogListHolder(SHILEN_MESSENGER, false, qs.getInt(Integer.toString(SHILEN_MESSENGER))));
			npcLogList.add(new NpcLogListHolder(HELLISH_CREATURE, false, qs.getInt(Integer.toString(HELLISH_CREATURE))));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}