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
package quests.Q10378_WeedingWork;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;
import org.l2jbr.gameserver.util.Util;

/**
 * Weeding Work (10378)
 * @URL https://l2wiki.com/Weeding_Work
 * @author Gigi
 */
public class Q10378_WeedingWork extends Quest
{
	// NPCs
	private static final int DADFPHYNA = 33697;
	// Monster's
	private static final int MANDRAGORA_OF_JOY_AND_SORROW = 23210;
	private static final int MANDRAGORA_OF_PRAYER = 23211;
	// Items
	private static final int MANDRAGORA_ROOT = 34975;
	private static final int MANDRAGORA_STEM = 34974;
	private static final int SOE_GUILLOTINE_FORTRESS = 35292;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10378_WeedingWork()
	{
		super(10378);
		addStartNpc(DADFPHYNA);
		addTalkId(DADFPHYNA);
		addKillId(MANDRAGORA_OF_JOY_AND_SORROW, MANDRAGORA_OF_PRAYER);
		registerQuestItems(MANDRAGORA_ROOT, MANDRAGORA_STEM);
		addCondMinLevel(MIN_LEVEL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "33697-01a.htm":
			case "33697-01b.htm":
			{
				htmltext = event;
				break;
			}
			case "33697-02.htm":
			{
				qs.startQuest();
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, DADFPHYNA, NpcStringId.PLEASE_HELP_US_DISCOVER_THE_CAUSE_OF_THIS_CHAOS));
				htmltext = event;
				break;
			}
			case "33697-05.html":
			{
				giveAdena(player, 3000000, true);
				giveItems(player, SOE_GUILLOTINE_FORTRESS, 2);
				addExpAndSp(player, 845059770, 202814);
				qs.exitQuest(false, true);
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, DADFPHYNA, NpcStringId.THANK_YOU_IT_WILL_CERTAINLY_HELP_THE_RESEARCH));
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
		String htmltext = getNoQuestMsg(player);
		
		if (qs.isCreated())
		{
			htmltext = "33697-01.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				htmltext = "33697-03.html";
			}
			else if (qs.isCond(2))
			{
				htmltext = "33697-04.html";
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = getNoQuestMsg(player);
		}
		return htmltext;
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false))
		{
			switch (npc.getId())
			{
				case MANDRAGORA_OF_PRAYER:
				{
					if (getQuestItemsCount(player, MANDRAGORA_ROOT) < 5)
					{
						giveItems(player, MANDRAGORA_ROOT, 1);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case MANDRAGORA_OF_JOY_AND_SORROW:
				{
					if (getQuestItemsCount(player, MANDRAGORA_STEM) < 5)
					{
						giveItems(player, MANDRAGORA_STEM, 1);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((getQuestItemsCount(player, MANDRAGORA_ROOT) >= 5) && (getQuestItemsCount(player, MANDRAGORA_STEM) >= 5))
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