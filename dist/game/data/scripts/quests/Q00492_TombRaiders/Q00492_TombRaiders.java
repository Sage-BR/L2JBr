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
package quests.Q00492_TombRaiders;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;

/**
 * Tomb Raiders (492)
 * @URL https://l2wiki.com/Tomb_Raiders
 * @author Gigi
 */
public class Q00492_TombRaiders extends Quest
{
	// NPCs
	private static final int ZENYA = 32140;
	// Items
	private static final int RELICS_OF_THE_EMPIRE = 34769;
	// Reward
	private static final int EXP_REWARD = 300500;
	private static final int SP_REWARD = 75;
	// Misc
	private static final int MIN_LEVEL = 80;
	// Monsters
	private static final int[] MONSTERS =
	{
		23193, // Apparition Destroyer (83)
		23194, // Apparition Assassin (83)
		23195, // Apparition Sniper (83)
		23196 // Apparition Wizard (83)
	};
	
	public Q00492_TombRaiders()
	{
		super(492);
		addStartNpc(ZENYA);
		addTalkId(ZENYA);
		registerQuestItems(RELICS_OF_THE_EMPIRE);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "noLevel.html");
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
			case "32140-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32140-03.htm":
			{
				qs.startQuest();
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
		String htmltext = qs.isCompleted() ? getAlreadyCompletedMsg(player) : getNoQuestMsg(player);
		if ((npc.getId() == ZENYA) && !player.isSubClassActive() && !player.isDualClassActive() && (player.getClassId().level() == 4))
		{
			return "noClass.html";
		}
		
		switch (npc.getId())
		{
			case ZENYA:
			{
				if (qs.isCreated())
				{
					htmltext = "32140-01.htm";
				}
				else if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "32140-05.html";
							break;
						}
						case 2:
						{
							if (qs.isCond(2) && (getQuestItemsCount(player, RELICS_OF_THE_EMPIRE) >= 50))
							{
								takeItems(player, RELICS_OF_THE_EMPIRE, 50);
								addExpAndSp(player, EXP_REWARD * player.getLevel(), SP_REWARD * player.getLevel());
								playSound(player, QuestSound.ITEMSOUND_QUEST_FINISH);
								qs.exitQuest(QuestType.DAILY, true);
								htmltext = "32140-04.html";
							}
							break;
						}
					}
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
		if ((qs != null) && qs.isCond(1) && giveItemRandomly(killer, npc, RELICS_OF_THE_EMPIRE, 1, 50, 0.30, true))
		{
			qs.setCond(2);
		}
		return super.onKill(npc, killer, isSummon);
	}
}