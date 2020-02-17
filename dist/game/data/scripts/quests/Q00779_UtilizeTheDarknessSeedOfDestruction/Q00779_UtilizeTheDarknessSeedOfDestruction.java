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
package quests.Q00779_UtilizeTheDarknessSeedOfDestruction;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.util.Util;

/**
 * Utilize the Darkness - Seed of Destruction (779)
 * @URL https://l2wiki.com/Utilize_the_Darkness_-_Seed_of_Destruction
 * @author Gigi
 * @date 2018-06-17 - [12:11:48]
 */
public class Q00779_UtilizeTheDarknessSeedOfDestruction extends Quest
{
	// NPCs
	private static final int ALLENOS = 32526;
	private static final int[] MOBS =
	{
		22536, // Royal Guard Captain
		22537, // Dragontroop Spellshifter
		22538, // Dragontroop Commander
		22539, // Dragontroop Commando
		22540, // Dragontroop Centurion
		22541, // Dragontroop Infantry
		22542, // Dragontroop Archmage
		22543, // Dragontroop Wizard
		22544, // Dragontroop Magic Blader
		22546, // Berserker
		22547, // Dragontroop Healer
		22548, // Dragontroop Lancer
		22550, // Savage Warrior
		22551, // Priest of Darkness
		22552 // Mutated Drake
	};
	// Misc
	private static final int MIN_LEVEL = 93;
	private static final int MAX_LEVEL = 97;
	// Item
	private static final int TIATS_TOTEM = 38579;
	private static final int TIATS_CHARM = 38575;
	
	public Q00779_UtilizeTheDarknessSeedOfDestruction()
	{
		super(779);
		addStartNpc(ALLENOS);
		addTalkId(ALLENOS);
		addKillId(MOBS);
		registerQuestItems(TIATS_TOTEM);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "32526-00.htm");
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
			case "32526-02.htm":
			case "32526-08.html":
			{
				htmltext = event;
				break;
			}
			case "32526-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32526-06.html":
			{
				if (qs.isCond(2) && (player.getLevel() >= MIN_LEVEL))
				{
					final long itemCount = getQuestItemsCount(player, TIATS_TOTEM);
					giveItems(player, TIATS_CHARM, (int) (itemCount / 5));
				}
				qs.exitQuest(QuestType.DAILY, true);
				htmltext = event;
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
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = getAlreadyCompletedMsg(player, QuestType.DAILY);
					break;
				}
				qs.setState(State.CREATED);
			}
			case State.CREATED:
			{
				htmltext = "32526-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "32526-04.html";
						break;
					}
					case 2:
					{
						if (getQuestItemsCount(player, TIATS_TOTEM) >= 500)
						{
							htmltext = "32526-07.html";
							break;
						}
						htmltext = "32526-05.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false))
		{
			if ((getQuestItemsCount(player, TIATS_TOTEM) < 500) && (getRandom(100) < 20))
			{
				giveItems(player, TIATS_TOTEM, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			if ((getQuestItemsCount(player, TIATS_TOTEM) == 50))
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
