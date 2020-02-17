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
package quests.Q10379_AnUninvitedGuest;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExQuestNpcLogList;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;
import org.l2jbr.gameserver.util.Util;

import quests.Q10377_TheInvadedExecutionGrounds.Q10377_TheInvadedExecutionGrounds;

/**
 * An Uninvited Guest (10379)
 * @URL https://l2wiki.com/An_Uninvited_Guest
 * @author Gigi
 */
public class Q10379_AnUninvitedGuest extends Quest
{
	// NPCs
	private static final int ENDRIGO = 30632;
	// Monsters
	private static final int SCALDISECT_THE_FURIOUS = 23212;
	private static final int NAGDU_THE_DEFORMED = 23201;
	private static final int SADIAC_THE_KILLER = 23199;
	private static final int ROSENIAS_DIVINE_SPIRIT = 23208;
	private static final int HASKAL_GHOST = 23205;
	private static final int CANTA_STANDING_BEAST = 23203;
	private static final int GAZAM = 23207;
	private static final int TURAN_GHOST = 23200;
	private static final int KILLER_FRANGS = 23204;
	private static final int KALLBERA = 23209;
	private static final int HAKAL_THE_BUTTCHERED = 23202;
	private static final int SAMMITA = 23206;
	// Items
	private static final int SOE_GUILLOTINE_FORTRESS = 35292;
	private static final int ADENA = 57;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10379_AnUninvitedGuest()
	{
		super(10379);
		addStartNpc(ENDRIGO);
		addTalkId(ENDRIGO);
		addKillId(SCALDISECT_THE_FURIOUS, NAGDU_THE_DEFORMED, SADIAC_THE_KILLER, ROSENIAS_DIVINE_SPIRIT, HASKAL_GHOST, CANTA_STANDING_BEAST, GAZAM, TURAN_GHOST, KILLER_FRANGS, KALLBERA, HAKAL_THE_BUTTCHERED, SAMMITA);
		addCondMinLevel(MIN_LEVEL);
		addCondCompletedQuest(Q10377_TheInvadedExecutionGrounds.class.getSimpleName(), "warden_endrigo_q10379_02.html");
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
			case "warden_endrigo_q10379_04.htm":
			case "warden_endrigo_q10379_05.htm":
			{
				htmltext = event;
				break;
			}
			case "warden_endrigo_q10379_06.htm":
			{
				qs.startQuest();
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, ENDRIGO, NpcStringId.SCALDISECT_OF_HELLFIRE_BE_PREPARED_FOR_ITS_ATTACK));
				htmltext = event;
				break;
			}
			case "warden_endrigo_q10379_09.html":
			{
				if (qs.isCond(3))
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, ENDRIGO, NpcStringId.YOU_OBTAINED_A_REALLY_IMPORTANT_RESULT_I_WILL_DEFINITELY_TELL_THE_RULERS_OF_DION));
					giveItems(player, ADENA, 3441680);
					giveItems(player, SOE_GUILLOTINE_FORTRESS, 2);
					addExpAndSp(player, 934013430, 224163);
					qs.exitQuest(false, true);
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
		
		if (qs.isCreated())
		{
			htmltext = "warden_endrigo_q10379_01.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1) || qs.isCond(2))
			{
				htmltext = "warden_endrigo_q10379_07.html";
			}
			else if (qs.isCond(3))
			{
				htmltext = "warden_endrigo_q10379_08.html";
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = "warden_endrigo_q10379_03.html";
		}
		
		return htmltext;
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false))
		{
			switch (npc.getId())
			{
				case NAGDU_THE_DEFORMED:
				case SADIAC_THE_KILLER:
				case ROSENIAS_DIVINE_SPIRIT:
				case HASKAL_GHOST:
				case CANTA_STANDING_BEAST:
				case GAZAM:
				case TURAN_GHOST:
				case KILLER_FRANGS:
				case KALLBERA:
				case HAKAL_THE_BUTTCHERED:
				case SAMMITA:
				{
					if ((getRandom(100) < 5) && qs.isCond(1))
					{
						showOnScreenMsg(player, NpcStringId.S1_S_PROOF_OF_SURVIVAL_DISAPPEARS_AND_SCALDISECT_THE_FURIOUS_APPEARS, ExShowScreenMessage.TOP_CENTER, 8000);
						addSpawn(SCALDISECT_THE_FURIOUS, npc.getX() + 500, npc.getY() + 500, npc.getZ(), 0, false, 150000);
						qs.setCond(2);
					}
					break;
				}
				case SCALDISECT_THE_FURIOUS:
				{
					if (qs.isCond(2))
					{
						int kills = qs.getInt(Integer.toString(SCALDISECT_THE_FURIOUS));
						if (kills < 1)
						{
							kills++;
							qs.set(Integer.toString(SCALDISECT_THE_FURIOUS), kills);
						}
						final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
						log.addNpc(SCALDISECT_THE_FURIOUS, qs.getInt("SCALDISECT_THE_FURIOUS"));
						player.sendPacket(log);
					}
					if (qs.getInt(Integer.toString(SCALDISECT_THE_FURIOUS)) == 1)
					{
						qs.setCond(0);
						qs.setCond(3, true);
					}
					break;
				}
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
}