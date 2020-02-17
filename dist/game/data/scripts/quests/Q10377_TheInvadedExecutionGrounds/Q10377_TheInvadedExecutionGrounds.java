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
package quests.Q10377_TheInvadedExecutionGrounds;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;

/**
 * The Invaded Execution Grounds (10377)
 * @URL https://l2wiki.com/The_Invaded_Execution_Grounds
 * @author Gigi
 */
public class Q10377_TheInvadedExecutionGrounds extends Quest
{
	// NPCs
	private static final int SYLVAIN = 30070;
	private static final int HARLAN = 30074;
	private static final int RODERIK = 30631;
	private static final int ENDRIGO = 30632;
	private static final int TOMBSTONE_OF_THE_GUILLOTINE_OF_DEATH = 33717;
	private static final int TOMBSTONE_OF_HOUPON_THE_WARDEN_OVERSEER = 33718;
	private static final int TOMBSTONE_OF_CROOK_THE_MAD = 33719;
	// Items
	private static final int SOE_GUILLOTINE_FORTRESS = 35292;
	private static final int ADENA = 57;
	private static final int HARLANS_ORDERS = 34972;
	private static final int ENDRIGOS_REPORT = 34973;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10377_TheInvadedExecutionGrounds()
	{
		super(10377);
		addStartNpc(SYLVAIN);
		addFirstTalkId(TOMBSTONE_OF_THE_GUILLOTINE_OF_DEATH, TOMBSTONE_OF_HOUPON_THE_WARDEN_OVERSEER, TOMBSTONE_OF_CROOK_THE_MAD);
		addTalkId(SYLVAIN, HARLAN, RODERIK, ENDRIGO);
		registerQuestItems(HARLANS_ORDERS, ENDRIGOS_REPORT);
		addCondMinLevel(MIN_LEVEL);
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
			case "sylvain_q10377_02a.html":
			case "sylvain_q10377_04.htm":
			case "sylvain_q10377_05.htm":
			case "hitsran_q10377_02.html":
			case "warden_roderik_q10377_02.html":
			{
				htmltext = event;
				break;
			}
			case "sylvain_q10377_06.htm":
			{
				qs.startQuest();
				htmltext = event;
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, SYLVAIN, NpcStringId.OH_GODS_THANK_YOU_FOR_SENDING_US_AN_ADVENTURER_LIKE_S1));
				break;
			}
			case "hitsran_q10377_03.html":
			{
				qs.setCond(2, true);
				giveItems(player, HARLANS_ORDERS, 1);
				htmltext = event;
				break;
			}
			case "warden_roderik_q10377_03.html":
			{
				qs.setCond(3, true);
				takeItems(player, HARLANS_ORDERS, -1);
				giveItems(player, ENDRIGOS_REPORT, 1);
				htmltext = event;
				break;
			}
			case "warden_endrigo_q10377_02.html":
			{
				if (qs.isCond(6))
				{
					giveItems(player, ADENA, 2970560);
					giveItems(player, SOE_GUILLOTINE_FORTRESS, 2);
					addExpAndSp(player, 756106110, 181465);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		QuestState qs = getQuestState(player, false);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case TOMBSTONE_OF_HOUPON_THE_WARDEN_OVERSEER:
				{
					if (qs.isCond(3))
					{
						qs.setCond(4, true);
					}
					break;
				}
				case TOMBSTONE_OF_CROOK_THE_MAD:
				{
					if (qs.isCond(4))
					{
						qs.setCond(5, true);
					}
					break;
				}
				case TOMBSTONE_OF_THE_GUILLOTINE_OF_DEATH:
				{
					if (qs.isCond(5))
					{
						qs.setCond(6, true);
					}
					break;
				}
			}
		}
		return null;
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
				if (npc.getId() == SYLVAIN)
				{
					htmltext = "sylvain_q10377_01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SYLVAIN:
					{
						if (qs.isCond(1))
						{
							htmltext = "sylvain_q10377_03.html";
						}
						break;
					}
					case HARLAN:
					{
						if (qs.isCond(1))
						{
							htmltext = "hitsran_q10377_01.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "hitsran_q10377_04.html";
						}
						break;
					}
					case RODERIK:
					{
						if (qs.isCond(2))
						{
							htmltext = "warden_roderik_q10377_01.html";
						}
						else if (qs.isCond(3))
						{
							htmltext = "warden_roderik_q10377_04.html";
						}
						break;
					}
					case ENDRIGO:
					{
						if ((qs.getCond() > 3) && (qs.getCond() < 6))
						{
							htmltext = "warden_endrigo_q10377_03.html";
						}
						else if (qs.isCond(6))
						{
							takeItems(player, ENDRIGOS_REPORT, -1);
							htmltext = "warden_endrigo_q10377_01.html";
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
}