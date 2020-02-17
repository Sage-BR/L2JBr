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
package quests.Q10841_DeepInsideAteliaFortress;

import org.l2jbr.Config;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Deep Inside Atelia Fortress (10841)
 * @URL https://l2wiki.com/Deep_Inside_Atelia_Fortress
 * @author Gigi
 */
public class Q10841_DeepInsideAteliaFortress extends Quest
{
	// NPCs
	private static final int ELIKIA = 34057;
	private static final int KAYSIA = 34051;
	// Boss
	private static final int KELBIM = 26124;
	// Items
	private static final int KELBIM_ARMOR_PIECE = 46144;
	private static final int SPIRIT_STONE_HAIR_ACCESSORY = 45937;
	private static final int SUPERIOR_GIANTS_CODEX = 46151;
	// Misc
	private static final int MIN_LEVEL = 101;
	
	public Q10841_DeepInsideAteliaFortress()
	{
		super(10841);
		addStartNpc(ELIKIA);
		addTalkId(ELIKIA, KAYSIA);
		addKillId(KELBIM);
		registerQuestItems(KELBIM_ARMOR_PIECE);
		addCondMinLevel(MIN_LEVEL, "34057-00.htm");
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
			case "34057-02.htm":
			case "34057-03.htm":
			case "34057-04.htm":
			{
				htmltext = event;
				break;
			}
			case "34057-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34051-02.html":
			{
				giveItems(player, SPIRIT_STONE_HAIR_ACCESSORY, 1);
				giveItems(player, SUPERIOR_GIANTS_CODEX, 1);
				addExpAndSp(player, 7262301690L, 17429400);
				qs.exitQuest(false, true);
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "34057-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ELIKIA:
					{
						if (qs.isCond(1))
						{
							htmltext = "34057-06.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "34057-07.html";
						}
						break;
					}
					case KAYSIA:
					{
						if (qs.isCond(1))
						{
							htmltext = "34051-00.html";
						}
						else if (qs.isCond(2) && hasQuestItems(player, KELBIM_ARMOR_PIECE))
						{
							htmltext = "34051-01.html";
						}
						break;
					}
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
		if ((qs != null) && qs.isStarted() && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
		{
			giveItems(player, KELBIM_ARMOR_PIECE, 1);
			qs.setCond(2, true);
		}
	}
}