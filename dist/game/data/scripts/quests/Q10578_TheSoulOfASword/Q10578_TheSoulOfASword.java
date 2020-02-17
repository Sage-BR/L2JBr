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
package quests.Q10578_TheSoulOfASword;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.Containers;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.impl.item.OnItemSoulCrystalAdd;
import org.l2jbr.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.serverpackets.ExTutorialShowId;

import quests.Q10566_BestChoice.Q10566_BestChoice;

/**
 * The Soul of a Sword (10578)
 * @URL https://l2wiki.com/The_Soul_of_a_Sword
 * @author NightBR
 * @html by Werum
 */
public class Q10578_TheSoulOfASword extends Quest
{
	// NPCs
	private static final int VINCENZ = 31316;
	// Items
	private static final int PRACTICE_STORMBRINGER = 46629;
	private static final int PRACTICE_SOUL_CRYSTAL_STAGE1 = 46526;
	private static final int SOUL_CRYSTAL_PRACTICE_GEMSTONE = 36722;
	// Rewards
	private static final long XP = 597699960;
	private static final int SP = 597690;
	private static final int CERTIFICATE_FROM_VINCENZ = 48176;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10578_TheSoulOfASword()
	{
		super(10578);
		addStartNpc(VINCENZ);
		addTalkId(VINCENZ);
		addCondMinLevel(MIN_LEVEL, "noLevel.html");
		registerQuestItems(PRACTICE_STORMBRINGER, PRACTICE_SOUL_CRYSTAL_STAGE1, SOUL_CRYSTAL_PRACTICE_GEMSTONE);
		addCondStartedQuest(Q10566_BestChoice.class.getSimpleName(), "31316-99.html");
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_ITEM_SOUL_CRYSTAL_ADD, (OnItemSoulCrystalAdd event) -> OnItemSoulCrystalAdd(event), this));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		String htmltext = null;
		switch (event)
		{
			case "31316-02.htm":
			case "31316-04.html":
			case "31316-07.html":
			case "31316-11.html":
			{
				htmltext = event;
				break;
			}
			case "31316-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31316-05.html":
			{
				// show Service/Help/Soul Crystal Enhancement page
				player.sendPacket(new ExTutorialShowId(40));
				htmltext = event;
				break;
			}
			case "31316-06.html":
			{
				// show Service/Help/Buy Item page
				player.sendPacket(new ExTutorialShowId(36));
				// TODO: check if player already have quest items
				if (hasQuestItems(player, PRACTICE_STORMBRINGER, PRACTICE_SOUL_CRYSTAL_STAGE1, SOUL_CRYSTAL_PRACTICE_GEMSTONE))
				{
					htmltext = "31316-12.html";
				}
				else
				{
					// To make sure player does not have them already
					removeRegisteredQuestItems(player);
					giveItems(player, PRACTICE_STORMBRINGER, 1);
					giveItems(player, PRACTICE_SOUL_CRYSTAL_STAGE1, 1);
					giveItems(player, SOUL_CRYSTAL_PRACTICE_GEMSTONE, 10);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "31316-08.html":
			{
				addExpAndSp(player, XP, SP);
				giveItems(player, CERTIFICATE_FROM_VINCENZ, 1);
				qs.exitQuest(QuestType.ONE_TIME, true);
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
				htmltext = "31316-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "31316-09.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "31316-10.html";
				}
				else
				{
					htmltext = "31316-07.html";
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
	
	public void OnItemSoulCrystalAdd(OnItemSoulCrystalAdd event)
	{
		final PlayerInstance player = event.getPlayer();
		if ((player == null) || (event.getItem().getId() != PRACTICE_STORMBRINGER))
		{
			return;
		}
		
		final QuestState qs = getQuestState(player, false);
		// Check if weapon has been augmented to complete the quest
		if ((qs != null) && qs.isCond(2) && (!player.getInventory().getItemByItemId(PRACTICE_STORMBRINGER).getSpecialAbilities().isEmpty()))
		{
			qs.setCond(3, true);
		}
	}
}
