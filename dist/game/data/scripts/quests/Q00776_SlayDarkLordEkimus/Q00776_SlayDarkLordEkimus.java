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
package quests.Q00776_SlayDarkLordEkimus;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Slay Dark Lord Ekimus (776)
 * @URL https://l2wiki.com/Slay_Dark_Lord_Ekimus
 * @author Gigi
 */
public class Q00776_SlayDarkLordEkimus extends Quest
{
	// NPCs
	private static final int TEPIOS = 32603;
	// Boss
	private static final int EKIMUS = 29150;
	// Misc
	private static final int MIN_LEVEL = 95;
	private static final int FREED_SOUL_CRYSTAL = 38576;
	
	public Q00776_SlayDarkLordEkimus()
	{
		super(776);
		addStartNpc(TEPIOS);
		addTalkId(TEPIOS);
		addKillId(EKIMUS);
		addCondMinLevel(MIN_LEVEL, "32603-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null)
		{
			return null;
		}
		switch (event)
		{
			case "32603-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32603-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32603-06.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, FREED_SOUL_CRYSTAL, 100);
					qs.exitQuest(QuestType.DAILY, true);
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
		
		if (npc.getId() == TEPIOS)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "Complete.html";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "32603-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "32603-04.html";
					}
					else
					{
						htmltext = "32603-05.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1) && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
		{
			qs.setCond(2, true);
		}
	}
}