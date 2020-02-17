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
package quests.Q10825_ForVictory;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.sieges.OnCastleSiegeFinish;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10823_ExaltedOneWhoShattersTheLimit.Q10823_ExaltedOneWhoShattersTheLimit;

/**
 * For Victory (10825)
 * @URL https://l2wiki.com/For_Victory
 * @author Mobius
 */
public class Q10825_ForVictory extends Quest
{
	// NPC
	private static final int KURTIZ = 34019;
	// Items
	private static final int MARK_OF_VALOR = 46059;
	private static final int MERLOT_SERTIFICATE = 46056;
	private static final int MAMMON_CERTIFICATE = 45635;
	private static final int GUSTAV_CERTIFICATE = 45636;
	// Rewards
	private static final int KURTIZ_CERTIFICATE = 46057;
	private static final int SPELLBOOK_SUMMON_BATTLE_POTION = 45927;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q10825_ForVictory()
	{
		super(10825);
		addStartNpc(KURTIZ);
		addTalkId(KURTIZ);
		addCondMinLevel(MIN_LEVEL, "30870-02.html");
		addCondStartedQuest(Q10823_ExaltedOneWhoShattersTheLimit.class.getSimpleName(), "30870-03.html");
		registerQuestItems(MARK_OF_VALOR);
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
			case "30870-04.htm":
			case "30870-05.htm":
			{
				htmltext = event;
				break;
			}
			case "30870-06.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30870-09.html":
			{
				if (qs.isCond(1) && (getQuestItemsCount(player, MARK_OF_VALOR) >= 10))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						if (hasQuestItems(player, MERLOT_SERTIFICATE, MAMMON_CERTIFICATE, GUSTAV_CERTIFICATE))
						{
							htmltext = "30870-10.html";
						}
						else
						{
							htmltext = event;
						}
						giveItems(player, KURTIZ_CERTIFICATE, 1);
						giveItems(player, SPELLBOOK_SUMMON_BATTLE_POTION, 1);
						qs.exitQuest(false, true);
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
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
				htmltext = "30870-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (getQuestItemsCount(player, MARK_OF_VALOR) >= 10)
				{
					htmltext = "30870-08.html";
				}
				else
				{
					htmltext = "30870-07.html";
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
	
	private void manageQuestProgress(PlayerInstance player)
	{
		if (player != null)
		{
			final QuestState qs = getQuestState(player, false);
			
			if ((qs != null) && qs.isCond(1))
			{
				giveItems(player, MARK_OF_VALOR, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
	}
	
	@RegisterEvent(EventType.ON_CASTLE_SIEGE_FINISH)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private void OnCastleSiegeFinish(OnCastleSiegeFinish event)
	{
		event.getSiege().getPlayersInZone().forEach(player -> manageQuestProgress(player));
	}
	
	// TODO: Dimensional Raid - https://l2wiki.com/Dimensional_Raid
}