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
package quests.Q00180_InfernalFlamesBurningInCrystalPrison;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.util.Util;

/**
 * @author hlwrave
 * @URL https://l2wiki.com/Infernal_Flames_Burning_in_Crystal_Prison
 */
public class Q00180_InfernalFlamesBurningInCrystalPrison extends Quest
{
	// NPC
	private static final int FIOREN = 33044;
	// Monster
	private static final int BAYLOR = 29213;
	// Misc
	private static final int MIN_LEVEL = 97;
	// Quest Item
	private static final int BELETH_MARK = 17591;
	// Item
	private static final int ENCHANT_SROLL_R = 22428;
	
	public Q00180_InfernalFlamesBurningInCrystalPrison()
	{
		super(180);
		addStartNpc(FIOREN);
		addTalkId(FIOREN);
		registerQuestItems(BELETH_MARK);
		addKillId(BAYLOR);
		addCondMinLevel(MIN_LEVEL, "33044-02.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		if ("33044-06.html".equals(event))
		{
			qs.startQuest();
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
			htmltext = "33044-01.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				htmltext = "33044-07.html";
			}
			else if (qs.isCond(2))
			{
				takeItems(player, BELETH_MARK, -1);
				giveItems(player, ENCHANT_SROLL_R, 1);
				addExpAndSp(player, 14000000, 6400000);
				qs.exitQuest(QuestType.ONE_TIME, true);
				htmltext = "33044-08.html";
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = "33044-03.html";
		}
		
		return htmltext;
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false))
		{
			giveItems(player, BELETH_MARK, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			qs.setCond(2, true);
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
}
