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
package ai.areas.GainakUnderground.Lailly;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.instancemanager.InstanceManager;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * Lailly AI.
 * @author Stayway
 */
public class Lailly extends AbstractNpcAI
{
	// NPCs
	private static final int LAILLY = 34181;
	// Instances
	private static final int INSTANCE_TAUTI = 261;
	private static final int INSTANCE_KELBIM = 262;
	private static final int INSTANCE_FREYA = 263;
	
	private Lailly()
	{
		addSpawnId(LAILLY);
		addFirstTalkId(LAILLY);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34181.html":
			{
				htmltext = event;
				break;
			}
			case "spam_text":
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.READY_TO_LISTEN_TO_A_STORY_COME_NOW));
				break;
			}
			case "okay":
			{
				final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
				if ((instance != null) && (instance.getEndTime() > System.currentTimeMillis()))
				{
					switch (instance.getTemplateId())
					{
						case INSTANCE_TAUTI:
						case INSTANCE_KELBIM:
						case INSTANCE_FREYA:
						{
							player.teleToLocation(instance.getEnterLocation(), instance);
							break;
						}
					}
					break;
				}
				htmltext = "34181-1.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		startQuestTimer("spam_text", 180000, npc, null, true);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "34181.html";
	}
	
	public static void main(String[] args)
	{
		new Lailly();
	}
}