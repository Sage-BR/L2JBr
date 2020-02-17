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
package quests.Q00778_OperationRoaringFlame;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.util.Util;

import quests.Q10445_AnImpendingThreat.Q10445_AnImpendingThreat;

/**
 * Operation Roaring Flame (778)
 * @URL https://l2wiki.com/Operation_Roaring_Flame
 * @author Gigi
 */
public class Q00778_OperationRoaringFlame extends Quest
{
	// NPCs
	private static final int BRUENER = 33840;
	// Mob
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
		23324 // Captain (Nerva Bloodlust)
	};
	// Items'
	private static final int TURAKANS_SECRET_LETTER = 36682;
	private static final int BROKEN_WEAPON_FRAGMENT = 36683;
	// rewards
	private static final int SCROLL_OF_ESCAPE_RAIDERS_CROSSROAD = 37017;
	private static final int ELIXIR_OF_BLESSING = 32316;
	private static final int ELIXIR_OF_MIND = 30358;
	private static final int ELIXIR_OF_LIFE = 30357;
	private static final int ELMORE_NOBLE_BOX = 37022;
	private static final int ENERGY_OF_DESTRUCTION = 35562;
	// Misc
	private static final int MIN_LEVEL = 97;
	
	public Q00778_OperationRoaringFlame()
	{
		super(778);
		addStartNpc(BRUENER);
		addTalkId(BRUENER);
		addKillId(MOBS);
		registerQuestItems(TURAKANS_SECRET_LETTER, BROKEN_WEAPON_FRAGMENT);
		addCondMinLevel(MIN_LEVEL, "33840-00.htm");
		addCondCompletedQuest(Q10445_AnImpendingThreat.class.getSimpleName(), "33840-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null)
		{
			return null;
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
				htmltext = event;
				break;
			}
			case "33840-06.html":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 3470807368L, 28945440);
					giveItems(player, SCROLL_OF_ESCAPE_RAIDERS_CROSSROAD, 1);
					giveItems(player, ELIXIR_OF_BLESSING, 5);
					giveItems(player, ELIXIR_OF_MIND, 5);
					giveItems(player, ELIXIR_OF_LIFE, 5);
					giveItems(player, ELMORE_NOBLE_BOX, 1);
					giveItems(player, ENERGY_OF_DESTRUCTION, 1);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == BRUENER)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "Complete.html";
						break;
					}
					qs.setState(State.CREATED);
				}
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
			}
		}
		return htmltext;
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false))
		{
			if ((getQuestItemsCount(player, TURAKANS_SECRET_LETTER) < 500) && (getRandom(100) < 70))
			{
				giveItems(player, TURAKANS_SECRET_LETTER, getRandom(1, 2));
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			if (getQuestItemsCount(player, BROKEN_WEAPON_FRAGMENT) < 500)
			{
				giveItems(player, BROKEN_WEAPON_FRAGMENT, getRandom(1, 2));
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			if ((getQuestItemsCount(player, TURAKANS_SECRET_LETTER) >= 500) && (getQuestItemsCount(player, BROKEN_WEAPON_FRAGMENT) >= 500))
			{
				qs.setCond(2, true);
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
}