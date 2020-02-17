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
package custom.FakePlayers;

import org.l2jbr.Config;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * TODO: Move it to Creature.
 * @author Mobius
 */
public class PvpFlaggingStopTask extends AbstractNpcAI
{
	private PvpFlaggingStopTask()
	{
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if ((npc == null) || npc.isDead())
		{
			return null;
		}
		
		if (event.startsWith("FLAG_CHECK"))
		{
			final WorldObject target = npc.getTarget();
			if ((target != null) && (target.isPlayable() || target.isFakePlayer()))
			{
				npc.setScriptValue(1); // in combat
				cancelQuestTimer("FINISH_FLAG" + npc.getObjectId(), npc, null);
				cancelQuestTimer("REMOVE_FLAG" + npc.getObjectId(), npc, null);
				startQuestTimer("FINISH_FLAG" + npc.getObjectId(), Config.PVP_NORMAL_TIME - 20000, npc, null);
				startQuestTimer("FLAG_CHECK" + npc.getObjectId(), 5000, npc, null);
			}
		}
		else if (event.startsWith("FINISH_FLAG"))
		{
			if (npc.isScriptValue(1))
			{
				npc.setScriptValue(2); // blink status
				npc.broadcastInfo(); // update flag status
				startQuestTimer("REMOVE_FLAG" + npc.getObjectId(), 20000, npc, null);
			}
		}
		else if (event.startsWith("REMOVE_FLAG"))
		{
			if (npc.isScriptValue(2))
			{
				npc.setScriptValue(0); // not in combat
				npc.broadcastInfo(); // update flag status
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new PvpFlaggingStopTask();
	}
}
