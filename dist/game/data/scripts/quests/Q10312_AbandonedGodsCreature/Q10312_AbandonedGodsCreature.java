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
package quests.Q10312_AbandonedGodsCreature;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;

import quests.Q10310_TwistedCreationTree.Q10310_TwistedCreationTree;

/**
 * Abandoned God's Creature (10312)
 * @URL https://l2wiki.com/Abandoned_God%27s_Creature
 * @author Gigi
 */
public class Q10312_AbandonedGodsCreature extends Quest
{
	// Npc
	private static final int HORPINA = 33031;
	// Boss
	private static final int APHERUS = 25775;
	// Items
	private static final int WARSMITH_HOLDER = 19305; // Corroded Giant's Warsmith' Holder
	private static final int REORINS_MOLD = 19306; // Corroded Giant's Reorin's Mold
	private static final int ARCSMITH_ANVIL = 19307; // Corroded Giant's Arcsmith' Anvil
	private static final int WARSMITH_MOLD = 19308; // Corroded Giant's Warsmith' Mold
	private static final int EAR = 17527; // Scroll: Enchant Armor (R-grade)
	private static final int POUCH = 34861; // Ingredient and Hardener Pouch (R-grade)
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q10312_AbandonedGodsCreature()
	{
		super(10312);
		addStartNpc(HORPINA);
		addTalkId(HORPINA);
		addKillId(APHERUS);
		addCondMinLevel(MIN_LEVEL, "33031-00.htm");
		addCondCompletedQuest(Q10310_TwistedCreationTree.class.getSimpleName(), "33031-00.htm");
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
			case "33031-02.htm":
			case "33031-03.htm":
			case "33031-06.html":
			{
				htmltext = event;
				break;
			}
			case "33031-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "gift1":
			{
				giveItems(player, WARSMITH_HOLDER, 1);
				giveItems(player, REORINS_MOLD, 1);
				giveItems(player, ARCSMITH_ANVIL, 1);
				giveItems(player, WARSMITH_MOLD, 1);
				addExpAndSp(player, 46847289, 11243);
				qs.exitQuest(false, true);
				htmltext = "33031-08.html";
				break;
			}
			case "gift2":
			{
				giveItems(player, EAR, 2);
				addExpAndSp(player, 46847289, 11243);
				qs.exitQuest(false, true);
				htmltext = "33031-08.html";
				break;
			}
			case "gift3":
			{
				giveItems(player, POUCH, 2);
				addExpAndSp(player, 46847289, 11243);
				qs.exitQuest(false, true);
				htmltext = "33031-08.html";
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
			htmltext = "33031-01.htm";
		}
		else if (qs.isCond(1))
		{
			htmltext = "33031-05.html";
		}
		else if (qs.isCond(2))
		{
			htmltext = "33031-07.html";
		}
		else if (qs.isCompleted())
		{
			htmltext = "Complete.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			qs.setCond(2, true);
		}
	}
}