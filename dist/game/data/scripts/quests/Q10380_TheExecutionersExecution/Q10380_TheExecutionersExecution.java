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
package quests.Q10380_TheExecutionersExecution;

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

import quests.Q10379_AnUninvitedGuest.Q10379_AnUninvitedGuest;

/**
 * The Executioner's Execution (10380)
 * @URL https://l2wiki.com/The_Executioner%27s_Execution
 * @author Gigi
 */
public class Q10380_TheExecutionersExecution extends Quest
{
	// NPCs
	private static final int ENDRIGO = 30632;
	private static final int GUILLOTINE_OF_DEATH = 25892;
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
	// Item's
	private static final int GLORIOUS_T_SHIRT = 35291;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10380_TheExecutionersExecution()
	{
		super(10380);
		addStartNpc(ENDRIGO);
		addTalkId(ENDRIGO);
		addKillId(GUILLOTINE_OF_DEATH, NAGDU_THE_DEFORMED, SADIAC_THE_KILLER, ROSENIAS_DIVINE_SPIRIT, HASKAL_GHOST, CANTA_STANDING_BEAST, GAZAM, TURAN_GHOST, KILLER_FRANGS, KALLBERA, HAKAL_THE_BUTTCHERED, SAMMITA);
		addCondMinLevel(MIN_LEVEL);
		addCondCompletedQuest(Q10379_AnUninvitedGuest.class.getSimpleName(), "warden_endrigo_q10380_02.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "warden_endrigo_q10380_04.htm":
			case "warden_endrigo_q10380_05.htm":
			case "warden_endrigo_q10380_09.html":
			{
				htmltext = event;
				break;
			}
			case "warden_endrigo_q10380_06.htm":
			{
				qs.startQuest();
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, ENDRIGO, NpcStringId.IT_IS_TIME_TO_PUT_THIS_TO_AN_END_ARE_YOU_READY));
				htmltext = event;
				break;
			}
			case "warden_endrigo_q10380_10.html":
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, ENDRIGO, NpcStringId.YOU_DEFEATED_THE_GUILLOTINE_OF_DEATH_I_THINK_THAT_WAS_TRULY_AMAZING));
				giveItems(player, GLORIOUS_T_SHIRT, 1);
				addExpAndSp(player, 1022967090, 245512);
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
		
		if (qs.isCreated())
		{
			htmltext = "warden_endrigo_q10380_01.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				htmltext = "warden_endrigo_q10380_07.html";
			}
			else if (qs.isCond(2))
			{
				htmltext = "warden_endrigo_q10380_08.html";
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = "warden_endrigo_q10380_03.html";
		}
		
		return htmltext;
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false) && qs.isCond(1))
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
					if (getRandom(100) < 5)
					{
						showOnScreenMsg(player, NpcStringId.TO_DEFEAT_THE_GUILLOTINE_OF_DEATH_HOW_AMAZING, ExShowScreenMessage.TOP_CENTER, 8000);
						addSpawn(GUILLOTINE_OF_DEATH, npc.getX() + 500, npc.getY() + 500, npc.getZ(), 0, false, 180000);
					}
					break;
				}
				case GUILLOTINE_OF_DEATH:
				{
					int kills = qs.getInt(Integer.toString(GUILLOTINE_OF_DEATH));
					if (kills < 1)
					{
						kills++;
						qs.set(Integer.toString(GUILLOTINE_OF_DEATH), kills);
					}
					final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
					log.addNpc(GUILLOTINE_OF_DEATH, qs.getInt("GUILLOTINE_OF_DEATH"));
					player.sendPacket(log);
					
					if (qs.getInt(Integer.toString(GUILLOTINE_OF_DEATH)) == 1)
					{
						qs.setCond(2, true);
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