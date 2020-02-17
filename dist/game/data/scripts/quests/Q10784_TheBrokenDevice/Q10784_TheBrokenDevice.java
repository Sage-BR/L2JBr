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
package quests.Q10784_TheBrokenDevice;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10783_TracesOfAnAmbush.Q10783_TracesOfAnAmbush;

/**
 * The Broken Device (10784)
 * @author malyelfik
 */
public class Q10784_TheBrokenDevice extends Quest
{
	// NPC
	private static final int NOVAIN = 33866;
	// Monsters
	private static final int[] MONSTERS =
	{
		20647, // Yintzu
		20648, // Paliote
		20649, // Hamrut
		20650, // Kranrot
	};
	// Items
	private static final int BROKE_MAGIC_DEVICE_FRAGMENT = 39723;
	private static final int SOULSHOT = 1466;
	private static final int SPIRITSHOT = 3951;
	private static final int PAULINA_EQUIPMENT_SET = 46851;
	private static final int BLESSED_SCROLL_OF_ESCAPE = 33640;
	// Misc
	private static final int MIN_LEVEL = 56;
	private static final int MAX_LEVEL = 61;
	
	public Q10784_TheBrokenDevice()
	{
		super(10784);
		addStartNpc(NOVAIN);
		addTalkId(NOVAIN);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "33866-00.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33866-01.htm");
		addCondCompletedQuest(Q10783_TracesOfAnAmbush.class.getSimpleName(), "33866-01.htm");
		registerQuestItems(BROKE_MAGIC_DEVICE_FRAGMENT);
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
			case "33866-03.htm":
			case "33866-04.htm":
			{
				break;
			}
			case "33866-05.htm":
			{
				qs.startQuest();
				break;
			}
			case "33866-08.html":
			{
				if (qs.isCond(2))
				{
					giveStoryQuestReward(npc, player);
					giveAdena(player, 990000, true);
					giveItems(player, SOULSHOT, 6000);
					giveItems(player, SPIRITSHOT, 6000);
					giveItems(player, BLESSED_SCROLL_OF_ESCAPE, 3);
					giveItems(player, PAULINA_EQUIPMENT_SET, 1);
					addExpAndSp(player, 14369328, 1578);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
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
				htmltext = "33866-02.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33866-06.html" : "33866-07.html";
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
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			giveItems(killer, BROKE_MAGIC_DEVICE_FRAGMENT, 1);
			if (getQuestItemsCount(killer, BROKE_MAGIC_DEVICE_FRAGMENT) >= 200)
			{
				qs.setCond(2, true);
			}
			else
			{
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}