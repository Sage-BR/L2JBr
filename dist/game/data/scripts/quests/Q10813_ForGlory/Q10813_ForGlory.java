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
package quests.Q10813_ForGlory;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.ceremonyofchaos.OnCeremonyOfChaosMatchResult;
import org.l2jbr.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10811_ExaltedOneWhoFacesTheLimit.Q10811_ExaltedOneWhoFacesTheLimit;

/**
 * For Glory (10813)
 * @author Gladicek
 */
public class Q10813_ForGlory extends Quest
{
	// Npc
	private static final int MYSTERIOUS_BUTLER = 33685;
	// Items
	private static final int PROOF_OF_BATTLE = 45872;
	private static final int MYSTERIOUS_BUTLER_CERTIFICATE = 45624;
	private static final int BATTLE_QUIKCK_HEALING_POTION = 45945;
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10813_ForGlory()
	{
		super(10813);
		addStartNpc(MYSTERIOUS_BUTLER);
		addTalkId(MYSTERIOUS_BUTLER);
		addCondMinLevel(MIN_LEVEL, "33685-09.htm");
		addCondStartedQuest(Q10811_ExaltedOneWhoFacesTheLimit.class.getSimpleName(), "33685-07.htm");
		registerQuestItems(PROOF_OF_BATTLE);
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
			case "33685-02.htm":
			case "33685-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33685-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33685-06.html":
			{
				if (qs.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						takeItems(player, PROOF_OF_BATTLE, -1);
						giveItems(player, BATTLE_QUIKCK_HEALING_POTION, 120);
						giveItems(player, MYSTERIOUS_BUTLER_CERTIFICATE, 1);
						qs.exitQuest(false, true);
						
						final Quest mainQ = QuestManager.getInstance().getQuest(Q10811_ExaltedOneWhoFacesTheLimit.class.getSimpleName());
						if (mainQ != null)
						{
							mainQ.notifyEvent("SUBQUEST_FINISHED_NOTIFY", npc, player);
						}
						htmltext = event;
						break;
					}
					htmltext = getNoQuestLevelRewardMsg(player);
					break;
				}
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
				htmltext = "33685-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33685-08.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33685-05.html";
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
				giveItems(player, PROOF_OF_BATTLE, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				
				if (getQuestItemsCount(player, PROOF_OF_BATTLE) >= 80)
				{
					qs.setCond(2, true);
				}
			}
		}
	}
	
	@RegisterEvent(EventType.ON_CEREMONY_OF_CHAOS_MATCH_RESULT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private void onCeremonyOfChaosMatchResult(OnCeremonyOfChaosMatchResult event)
	{
		event.getMembers().forEach(player -> manageQuestProgress(player.getPlayer()));
	}
	
	@RegisterEvent(EventType.ON_OLYMPIAD_MATCH_RESULT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	private void onOlympiadMatchResult(OnOlympiadMatchResult event)
	{
		manageQuestProgress(event.getWinner().getPlayer());
		manageQuestProgress(event.getLoser().getPlayer());
	}
}