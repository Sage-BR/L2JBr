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
package ai.areas.FaeronVillage.RemembranceTower;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

import ai.AbstractNpcAI;

/**
 * Remembrance Tower AI.
 * @author St3eT
 */
public class RemembranceTower extends AbstractNpcAI
{
	// NPCs
	private static final int REMEMBRANCE_TOWER = 33989;
	// Misc
	private static final int EMMITER_ID = 17250700;
	
	private RemembranceTower()
	{
		addStartNpc(REMEMBRANCE_TOWER);
		addTalkId(REMEMBRANCE_TOWER);
		addFirstTalkId(REMEMBRANCE_TOWER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("action") && npc.isScriptValue(0))
		{
			npc.broadcastPacket(new OnEventTrigger(EMMITER_ID, true));
			npc.setScriptValue(1);
			startQuestTimer("TRIGGER", 3000, npc, null);
		}
		else if (event.equals("TRIGGER"))
		{
			npc.setScriptValue(0);
			npc.broadcastPacket(new OnEventTrigger(EMMITER_ID, false));
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new RemembranceTower();
	}
}