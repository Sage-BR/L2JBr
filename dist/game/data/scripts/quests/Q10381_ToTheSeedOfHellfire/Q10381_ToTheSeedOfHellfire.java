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
package quests.Q10381_ToTheSeedOfHellfire;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

/**
 * To The Seed of Hellfire (10381)
 * @author Gladicek
 */
public class Q10381_ToTheSeedOfHellfire extends Quest
{
	// NPCs
	private static final int KEUCEREUS = 32548;
	private static final int KBALDIR = 32733;
	private static final int SIZRAK = 33669;
	// Item
	private static final int KBALDIR_LETTER = 34957;
	// Misc
	private static final int MIN_LEVEL = 97;
	
	public Q10381_ToTheSeedOfHellfire()
	{
		super(10381);
		addStartNpc(KEUCEREUS);
		addTalkId(KEUCEREUS, KBALDIR, SIZRAK);
		addCondMinLevel(MIN_LEVEL, "32548-06.htm");
		registerQuestItems(KBALDIR_LETTER);
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
			case "32548-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32548-03.html":
			{
				qs.startQuest();
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HURRY_AND_GO_FIND_COMMANDER_KBALDIR);
				break;
			}
			case "32733-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					giveItems(player, KBALDIR_LETTER, 1);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_CAN_REACH_THE_SEED_OF_HELLFIRE_THROUGH_THE_SEED_TELEPORT_DEVICE);
				}
				break;
			}
			case "33669-03.html":
			{
				if (qs.isCond(2))
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_WILL_LOOK_FORWARD_TO_YOUR_ACTIVITY);
					giveAdena(player, 3_256_740, true);
					addExpAndSp(player, 951_127_800, 228_270);
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
				if (npc.getId() == KEUCEREUS)
				{
					htmltext = "32548-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case KEUCEREUS:
					{
						if (qs.isCond(1))
						{
							htmltext = "32548-04.html";
						}
						break;
					}
					case KBALDIR:
					{
						if (qs.isCond(1))
						{
							htmltext = "32733-01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "32733-04.html";
						}
						break;
					}
					case SIZRAK:
					{
						if (qs.isCond(2))
						{
							htmltext = "33669-01.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == KEUCEREUS)
				{
					htmltext = "32548-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
}