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
package quests.Q10827_StepUpToLead;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnAttackableKill;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10823_ExaltedOneWhoShattersTheLimit.Q10823_ExaltedOneWhoShattersTheLimit;

/**
 * Step Up to Lead (10827)
 * @URL https://l2wiki.com/Step_Up_to_Lead
 * @author Mobius
 */
public class Q10827_StepUpToLead extends Quest
{
	// NPC
	private static final int GUSTAV = 30760;
	// Items
	private static final int MERLOT_SERTIFICATE = 46056;
	private static final int KURTIZ_CERTIFICATE = 46057;
	private static final int MAMMON_CERTIFICATE = 45635;
	// Rewards
	private static final int GUSTAV_CERTIFICATE = 45636;
	private static final int SPELLBOOK_FAVOR_OF_THE_EXALTED = 45870;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q10827_StepUpToLead()
	{
		super(10827);
		addStartNpc(GUSTAV);
		addTalkId(GUSTAV);
		addCondMinLevel(MIN_LEVEL, "30760-02.html");
		addCondStartedQuest(Q10823_ExaltedOneWhoShattersTheLimit.class.getSimpleName(), "30760-03.html");
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
			case "30760-04.htm":
			case "30760-05.htm":
			{
				htmltext = event;
				break;
			}
			case "30760-06.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30760-09.html":
			{
				if (qs.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						if (hasQuestItems(player, KURTIZ_CERTIFICATE, MERLOT_SERTIFICATE, MAMMON_CERTIFICATE))
						{
							htmltext = "30760-10.html";
						}
						else
						{
							htmltext = event;
						}
						giveItems(player, GUSTAV_CERTIFICATE, 1);
						giveItems(player, SPELLBOOK_FAVOR_OF_THE_EXALTED, 1);
						
						// Give Exalted status here?
						// https://l2wiki.com/Noblesse
						player.setNobleLevel(2);
						player.broadcastUserInfo();
						
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
				htmltext = "30760-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "30760-07.html";
				}
				else
				{
					htmltext = "30760-08.html";
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
	
	@RegisterEvent(EventType.ON_ATTACKABLE_KILL)
	@RegisterType(ListenerRegisterType.GLOBAL_MONSTERS)
	public void onAttackableKill(OnAttackableKill event)
	{
		final PlayerInstance player = event.getAttacker();
		if (player == null)
		{
			return;
		}
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return;
		}
		if (player.getParty() == null)
		{
			return;
		}
		if (player.getParty().getLeader() != player)
		{
			return;
		}
		if (!event.getTarget().isRaid())
		{
			return;
		}
		if (event.getTarget().isRaidMinion())
		{
			return;
		}
		
		if (qs.isCond(1))
		{
			final int memo = qs.getMemoState() + 1;
			qs.setMemoState(memo);
			
			if (memo >= 30)
			{
				qs.setCond(2, true);
			}
		}
	}
}