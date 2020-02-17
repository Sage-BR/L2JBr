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
package ai.areas.TalkingIsland.GiantSoldiers;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Giant's Soldiers AI.
 * @author Gladicek
 */
public class GiantSoldiers extends AbstractNpcAI
{
	// NPCs
	private static final int LAKSIAN = 33486;
	private static final int BELOA = 33487;
	private static final int SKIA = 33484;
	private static final int RANLOF = 33483;
	private static final int ROVIEL = 33485;
	private static final int ASIN = 33482;
	private static final int SEKNUS = 33480;
	private static final int DRELL = 33481;
	private static final int CELLPHINE = 33477;
	// Location
	private static final Location NEAR_PANTHEON = new Location(-114371, 260183, -1192);
	
	public GiantSoldiers()
	{
		addStartNpc(LAKSIAN, BELOA, SKIA, RANLOF, ROVIEL, ASIN, SEKNUS, DRELL, CELLPHINE);
		addFirstTalkId(LAKSIAN, BELOA, SKIA, RANLOF, ROVIEL, ASIN, SEKNUS, DRELL);
		addSpawnId(LAKSIAN);
		addSpawnId(BELOA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (npc == null)
		{
			return null;
		}
		
		String htmltext = null;
		
		switch (event)
		{
			case "CHAT_MESSAGE_1A":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WE_SAW_PEOPLE_USE_SAYUNE_OTHER_THAN_THE_GIANT_S_MINIONS, 1000);
				startQuestTimer("CHAT_MESSAGE_2A", 2000, npc, null);
				break;
			}
			case "CHAT_MESSAGE_1B":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.IT_S_BECAUSE_THEY_HAVE_BECOME_AWAKEN_THE_POWER_OF_THE_GIANT_S, 1000);
				startQuestTimer("CHAT_MESSAGE_2B", 1500, npc, null);
				break;
			}
			case "CHAT_MESSAGE_2A":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DUH_YOU_CAN_T_USE_SAYUNE_WITHOUT_POWERS_OF_GIANTS_IN_THE_FIRST_PLACE, 1000);
				startQuestTimer("CHAT_MESSAGE_3A", 15000, npc, null);
				break;
			}
			case "CHAT_MESSAGE_2B":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_THINK_THAT_S_WHY_MANY_PEOPLE_COME_TO_VISIT_CELPHINE, 1000);
				startQuestTimer("CHAT_MESSAGE_3B", 15000, npc, null);
				break;
			}
			case "CHAT_MESSAGE_3A":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.AFTER_YE_SAGIRA_FELL_THERE_WERE_MANY_SACRIFICES_MADE, 1000);
				startQuestTimer("CHAT_MESSAGE_4A", 1500, npc, null);
				break;
			}
			case "CHAT_MESSAGE_3B":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DIDN_T_THAT_RELEASE_THE_SEAL_ON_HERMUNCUS, 1000);
				startQuestTimer("CHAT_MESSAGE_4B", 2000, npc, null);
				break;
			}
			case "CHAT_MESSAGE_4A":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THAT_S_THE_RUMOR_BUT_NOTHING_TO_CONFIRM_IT, 1000);
				startQuestTimer("CHAT_MESSAGE_1A", 15000, npc, null);
				break;
			}
			case "CHAT_MESSAGE_4B":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.IT_S_GOT_TO_BE_TRUE_WE_NEED_TO_FIND_IT, 1000);
				startQuestTimer("CHAT_MESSAGE_1B", 15000, npc, null);
				break;
			}
			case "33485-1.html":
			{
				htmltext = event;
				break;
			}
			case "TELEPORT_OUT":
			{
				player.teleToLocation(NEAR_PANTHEON);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case LAKSIAN:
			{
				startQuestTimer("CHAT_MESSAGE_1A", 1500, npc, null);
				break;
			}
			case BELOA:
			{
				startQuestTimer("CHAT_MESSAGE_1B", 2500, npc, null);
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new GiantSoldiers();
	}
}
