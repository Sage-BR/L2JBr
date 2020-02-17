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
package quests.Q10709_TheStolenSeed;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10403_TheGuardianGiant.Q10403_TheGuardianGiant;

/**
 * The Stolen Seed (10709)
 * @author St3eT
 */
public class Q10709_TheStolenSeed extends Quest
{
	// NPCs
	private static final int NOVIAN = 33866;
	private static final int CONTROL_DEVICE = 33961; // Magic Circle Control Device
	private static final int REMEMBERED_AKUM = 27524; // Remembered Giant Akum
	private static final int REMEMBERED_EMBRYO = 27525; // Remembered Embryo
	private static final int CURSED_AKUM = 27520; // Cursed Giant Akum
	// Items
	private static final int FRAGMENT = 39511; // Normal Fragment
	private static final int MEMORY_FRAGMENT = 39510; // Akum's Memory Fragment
	private static final int SOULSHOT = 1466; // Soulshot (A-grade)
	private static final int SPIRITSHOT = 3951; // Blessed Spiritshot (A-grade)
	private static final int BLESSED_SCROLL_OF_ESCAPE = 33640; // Blessed Scroll of Escape
	private static final int PAULINA_EQUIPMENT_SET = 46851; // Paulina's Equipment Set (A-grade)
	// Misc
	private static final int MIN_LEVEL = 56;
	private static final int MAX_LEVEL = 61;
	
	public Q10709_TheStolenSeed()
	{
		super(10709);
		addStartNpc(NOVIAN);
		addTalkId(NOVIAN, CONTROL_DEVICE);
		addKillId(CURSED_AKUM);
		registerQuestItems(FRAGMENT, MEMORY_FRAGMENT);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33866-08.htm");
		addCondCompletedQuest(Q10403_TheGuardianGiant.class.getSimpleName(), "33866-08.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		
		if (event.equals("action"))
		{
			if ((qs != null) && (qs.isCond(1)) && (getQuestItemsCount(player, MEMORY_FRAGMENT) >= 1))
			{
				// Take items
				takeItems(player, MEMORY_FRAGMENT, -1);
				
				// Spawn + chat
				final Npc akum = addSpawn(REMEMBERED_AKUM, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, false, 0);
				akum.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ARGH_WHO_IS_HIDING_THERE);
				final Npc embryo = addSpawn(REMEMBERED_EMBRYO, akum.getX() + 100, akum.getY() + 100, akum.getZ(), 0, false, 0);
				embryo.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.A_SMART_GIANT_HUH_WELL_HAND_IT_OVER_THE_KARTIA_S_SEED_IS_OURS);
				
				// Attack + invul
				akum.reduceCurrentHp(1, embryo, null);
				embryo.reduceCurrentHp(1, akum, null); // TODO: Find better way for attack
				
				embryo.setIsInvul(true);
				akum.setIsInvul(true);
				
				startQuestTimer("EMBRYO_DELAY", 3000, embryo, player);
			}
			else
			{
				return "33961-01.html";
			}
		}
		
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33866-02.htm":
			case "33866-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33866-04.htm":
			{
				qs.startQuest();
				giveItems(player, MEMORY_FRAGMENT, 1);
				htmltext = event;
				break;
			}
			case "33866-07.html":
			{
				if (qs.isCond(3) && (getQuestItemsCount(player, FRAGMENT) >= 1))
				{
					qs.exitQuest(false, true);
					takeItems(player, FRAGMENT, -1);
					giveItems(player, SOULSHOT, 6000);
					giveItems(player, SPIRITSHOT, 6000);
					giveItems(player, BLESSED_SCROLL_OF_ESCAPE, 3);
					giveItems(player, PAULINA_EQUIPMENT_SET, 1);
					giveStoryQuestReward(npc, player);
					giveAdena(player, 990000, true);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 5598386, 175);
					}
					htmltext = event;
				}
				break;
			}
			case "EMBRYO_DELAY":
			{
				final Npc akum = (Npc) npc.getTarget();
				if (akum != null)
				{
					qs.setCond(2, true);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.KARTIA_S_SEED_GOT_IT);
					akum.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ARGHH);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_WORTHLESS_GIANT_CURSE_YOU_FOR_ETERNITY);
					addSpawn(CURSED_AKUM, akum);
					npc.deleteMe();
					akum.deleteMe();
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == NOVIAN)
				{
					htmltext = "33866-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == NOVIAN)
				{
					switch (qs.getCond())
					{
						case 1:
						case 2:
						{
							htmltext = "33866-05.html";
							break;
						}
						case 3:
						{
							htmltext = "33866-06.html";
							break;
						}
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == NOVIAN)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isStarted() && qs.isCond(2))
		{
			qs.setCond(0);
			qs.setCond(3, true);
			giveItems(killer, FRAGMENT, 1);
		}
		return super.onKill(npc, killer, isSummon);
	}
}