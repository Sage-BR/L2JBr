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
package quests.Q00751_LiberatingTheSpirits;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExQuestNpcLogList;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Liberating the Spirits (00751)
 * @URL https://l2wiki.com/Liberating_the_Spirits
 * @author Gigi
 */
public class Q00751_LiberatingTheSpirits extends Quest
{
	// Npc
	private static final int RODERIK = 30631;
	// Monster's
	private static final int SCALDISECT = 23212;
	private static final int[] MOBS =
	{
		23199,
		23201,
		23202,
		23200,
		23203,
		23204,
		23205,
		23206,
		23207,
		23208,
		23209,
		23242,
		23243,
		23244,
		23245
	};
	// Item's
	private static final int DEADMANS_FLESH = 34971;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q00751_LiberatingTheSpirits()
	{
		super(751);
		addStartNpc(RODERIK);
		addTalkId(RODERIK);
		addKillId(SCALDISECT);
		addKillId(MOBS);
		registerQuestItems(DEADMANS_FLESH);
		addCondMinLevel(MIN_LEVEL, "lvl.htm");
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
			case "30631-1.htm":
			case "30631-2.htm":
			{
				htmltext = event;
				break;
			}
			case "30631-3.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30631-5.html":
			{
				takeItems(player, DEADMANS_FLESH, -1);
				addExpAndSp(player, 600000000, 144000);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == RODERIK)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "30631-0.htm";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "30631.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "30631-3a.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "30631-4.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		if (qs != null)
		{
			if ((giveItemRandomly(killer, DEADMANS_FLESH, 1, 40, 0.2, true)) && (qs.getMemoState() < 1))
			{
				qs.setMemoState(1);
				showOnScreenMsg(killer, NpcStringId.SUMMON_SCALDISECT_OF_HELLFIRE, ExShowScreenMessage.TOP_CENTER, 6000);
				addSpawn(SCALDISECT, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, false, 120000);
			}
			else if ((qs.isMemoState(1)) && (getQuestItemsCount(killer, DEADMANS_FLESH) >= 40) && (npc.getId() == SCALDISECT))
			{
				int kills = qs.getInt(Integer.toString(SCALDISECT));
				if (kills < 1)
				{
					kills++;
					qs.set(Integer.toString(SCALDISECT), kills);
				}
				
				final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
				log.addNpc(SCALDISECT, qs.getInt(Integer.toString(SCALDISECT)));
				qs.getPlayer().sendPacket(log);
				
				if ((qs.getInt(Integer.toString(SCALDISECT)) >= 1))
				{
					qs.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}