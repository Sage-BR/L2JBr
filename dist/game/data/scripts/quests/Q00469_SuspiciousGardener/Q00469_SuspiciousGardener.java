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
package quests.Q00469_SuspiciousGardener;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.serverpackets.ExQuestNpcLogList;

/**
 * Suspicious Gardener (469)
 * @URL https://l2wiki.com/Suspicious_Gardener
 * @author Gigi
 */
public class Q00469_SuspiciousGardener extends Quest
{
	// NPC
	private static final int GOFINA = 33031;
	// Monsters
	private static final int APHERIUS_LOOKOUT_BEWILDERED = 22964;
	// Items
	private static final ItemHolder CERTIFICATE_OF_LIFE = new ItemHolder(30385, 2); // Certificate of Life
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q00469_SuspiciousGardener()
	{
		super(469);
		addStartNpc(GOFINA);
		addTalkId(GOFINA);
		addKillId(APHERIUS_LOOKOUT_BEWILDERED);
		addCondMinLevel(MIN_LEVEL, "no_level.html");
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
			case "33031-02.htm":
			{
				htmltext = event;
				break;
			}
			case "33031-03.htm":
			{
				qs.startQuest();
				qs.set(Integer.toString(APHERIUS_LOOKOUT_BEWILDERED), 0);
				htmltext = event;
				break;
			}
			case "33031-06.html":
			{
				giveItems(player, CERTIFICATE_OF_LIFE);
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
		if (npc.getId() == GOFINA)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "33031-04.html";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "33031-01.htm";
				}
					break;
				case State.STARTED:
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "33031-07.html";
							break;
						}
						case 2:
						{
							htmltext = "33031-05.html";
							break;
						}
					}
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
			int kills = qs.getInt(Integer.toString(APHERIUS_LOOKOUT_BEWILDERED));
			if (kills < 30)
			{
				kills++;
				qs.set(Integer.toString(APHERIUS_LOOKOUT_BEWILDERED), kills);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			
			final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
			log.addNpc(APHERIUS_LOOKOUT_BEWILDERED, qs.getInt(Integer.toString(APHERIUS_LOOKOUT_BEWILDERED)));
			qs.getPlayer().sendPacket(log);
			
			if ((qs.getInt(Integer.toString(APHERIUS_LOOKOUT_BEWILDERED)) >= 30))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}