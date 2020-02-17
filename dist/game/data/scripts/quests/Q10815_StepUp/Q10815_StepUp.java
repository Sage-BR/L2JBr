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
package quests.Q10815_StepUp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerChat;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10811_ExaltedOneWhoFacesTheLimit.Q10811_ExaltedOneWhoFacesTheLimit;

/**
 * Step Up (10815)
 * @author Gladicek
 */
public class Q10815_StepUp extends Quest
{
	// Npc
	private static final int SIR_ERIC_RODEMAI = 30868;
	// Items
	private static final int RODEMAI_RUNE = 45642;
	private static final int SIR_ERIC_RODEMAI_CERTIFICATE = 45626;
	// Misc
	private static final int MIN_LEVEL = 99;
	private static final int WORLD_CHAT_COUNT = 120;
	private static final Map<Integer, Instant> REUSE = new ConcurrentHashMap<>();
	
	public Q10815_StepUp()
	{
		super(10815);
		addStartNpc(SIR_ERIC_RODEMAI);
		addTalkId(SIR_ERIC_RODEMAI);
		addCondMinLevel(MIN_LEVEL, "30868-09.htm");
		addCondStartedQuest(Q10811_ExaltedOneWhoFacesTheLimit.class.getSimpleName(), "30868-05.htm");
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
			case "30868-02.htm":
			case "30868-03.htm":
			{
				htmltext = event;
				break;
			}
			case "30868-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32548-08.html":
			{
				if (qs.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						giveItems(player, RODEMAI_RUNE, 1);
						giveItems(player, SIR_ERIC_RODEMAI_CERTIFICATE, 1);
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
				htmltext = "30868-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "30868-06.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "30868-07.html";
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
	
	@RegisterEvent(EventType.ON_PLAYER_CHAT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerChat(OnPlayerChat event)
	{
		final PlayerInstance player = event.getPlayer();
		final QuestState qs = getQuestState(player, false);
		
		final Instant now = Instant.now();
		if (!REUSE.isEmpty())
		{
			REUSE.values().removeIf(now::isAfter);
		}
		
		if ((qs != null) && qs.isCond(1))
		{
			int chatCount = qs.getInt("chat");
			
			if (event.getChatType() == ChatType.WORLD)
			{
				if (Config.WORLD_CHAT_INTERVAL.getSeconds() > 0)
				{
					final Instant instant = REUSE.getOrDefault(player.getObjectId(), null);
					if ((instant != null) && instant.isAfter(now))
					{
						return;
					}
				}
				chatCount++;
				
				if (chatCount >= WORLD_CHAT_COUNT)
				{
					qs.setCond(2, true);
				}
				else
				{
					qs.set("chat", chatCount);
					if (Config.WORLD_CHAT_INTERVAL.getSeconds() > 0)
					{
						REUSE.put(player.getObjectId(), now.plus(Config.WORLD_CHAT_INTERVAL));
					}
				}
			}
		}
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			npcLogList.add(new NpcLogListHolder(NpcStringId.WORLD_CHAT, qs.getInt("chat")));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}