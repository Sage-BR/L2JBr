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
package quests.Q10447_TimingIsEverything;

import org.l2jbr.Config;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.util.Util;

import quests.Q10445_AnImpendingThreat.Q10445_AnImpendingThreat;

/**
 * Timing is Everything (10447)
 * @URL https://l2wiki.com/Timing_is_Everything
 * @author Gigi
 */
public class Q10447_TimingIsEverything extends Quest
{
	// Npc
	private static final int BRUENER = 33840;
	// Mobs
	private static final int[] MOBS =
	{
		23314, // Nerva Orc Raider
		23315, // Nerva Orc Archer
		23316, // Nerva Orc Priest
		23317, // Nerva Orc Wizard
		23318, // Nerva Orc Assassin
		23319, // Nerva Orc Ambusher
		23320, // Nerva Orc Merchant
		23321, // Nerva Orc Warrior
		23322, // Nerva Orc Prefect
		23323, // Nerva Orc Elite
		23324, // Nerva Bloodlust
		23325, // Nerva Bloodlust
		23326, // Nerva Bloodlust
		23327, // Nerva Bloodlust
		23328, // Nerva Bloodlust
		23329 // Nerva Kaiser
	};
	// Item
	private static final int NARVAS_PRISON_KEY = 36665;
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10447_TimingIsEverything()
	{
		super(10447);
		addStartNpc(BRUENER);
		addTalkId(BRUENER);
		addKillId(MOBS);
		registerQuestItems(NARVAS_PRISON_KEY);
		addCondMinLevel(MIN_LEVEL, "33840-00.htm");
		addCondCompletedQuest(Q10445_AnImpendingThreat.class.getSimpleName(), "33840-00.htm");
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
			case "33840-02.htm":
			{
				htmltext = event;
				break;
			}
			case "33840-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "33840-06.html":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 2_147_483_647L, 515396);
					qs.exitQuest(false, true);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33840-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33840-04.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33840-05.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getNoQuestMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		
		if (!Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			return null;
		}
		
		if ((qs != null) && qs.isCond(1) && (giveItemRandomly(qs.getPlayer(), npc, NARVAS_PRISON_KEY, 1, 1, 0.1, false)))
		{
			showOnScreenMsg(qs.getPlayer(), NpcStringId.YOU_TOOK_DOWN_THE_NERVA_ORCS_AND_GOT_THEIR_TEMPORARY_PRISON_KEY, ExShowScreenMessage.BOTTOM_RIGHT, 5000);
			qs.setCond(2, true);
		}
		else if (getRandom(100) < 0.03)
		{
			showOnScreenMsg(killer, NpcStringId.YOU_HAVE_OBTAINED_NERVA_S_TEMPORARY_PRISON_KEY, ExShowScreenMessage.BOTTOM_RIGHT, 5000);
			giveItems(killer, NARVAS_PRISON_KEY, 1);
		}
		return super.onKill(npc, killer, isSummon);
	}
}