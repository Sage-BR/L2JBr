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
package instances.KartiasLabyrinth;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureAttacked;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureDeath;
import org.l2jbr.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Kartia Boss AI.
 * @author flanagak
 */
public class KartiaBoss extends AbstractNpcAI
{
	// NPCs
	private static final int[] BOSSES =
	{
		19253, // Zellaka (solo 85)
		25882, // Zellaka (group 85)
		19254, // Pelline (solo 90)
		25883, // Pelline (group 90)
		19255, // Kalios (solo 95)
		25884, // Kalios (group 95)
	};
	private static final int FIGHTER_SOLO_85 = 19220;
	private static final int MAGE_SOLO_85 = 19221;
	private static final int FIGHTER_GROUP_85 = 19229;
	private static final int MAGE_GROUP_85 = 19230;
	private static final int FIGHTER_SOLO_90 = 19223;
	private static final int MAGE_SOLO_90 = 19224;
	private static final int FIGHTER_GROUP_90 = 19232;
	private static final int MAGE_GROUP_90 = 19233;
	private static final int FIGHTER_SOLO_95 = 19226;
	private static final int MAGE_SOLO_95 = 19227;
	private static final int FIGHTER_GROUP_95 = 19235;
	private static final int MAGE_GROUP_95 = 19236;
	
	private KartiaBoss()
	{
		addSpawnId(BOSSES);
		setCreatureKillId(this::onCreatureKill, BOSSES);
		setCreatureAttackedId(this::onCreatureAttacked, BOSSES);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		getTimers().addRepeatingTimer("NPC_SAY", 20000, npc, null);
		return super.onSpawn(npc);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		if (event.equals("NPC_SAY") && npc.isTargetable())
		{
			npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.YOU_PUNY_INSECTS_DON_T_KNOW_YOUR_PLACE_YOU_CANNOT_STOP_ME);
		}
	}
	
	public void onCreatureKill(OnCreatureDeath event)
	{
		final Npc npc = (Npc) event.getTarget();
		npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.NO_HOW_COULD_THIS_BE_I_CAN_T_GO_BACK_TO_NIHIL_LIKE_THIS);
		getTimers().cancelTimersOf(npc);
	}
	
	public void onCreatureAttacked(OnCreatureAttacked event)
	{
		final Npc npc = (Npc) event.getTarget();
		if ((npc.getCurrentHpPercent() <= 75) && npc.isScriptValue(0))
		{
			spawnMinions(npc);
			npc.setScriptValue(1);
		}
		else if ((npc.getCurrentHpPercent() <= 50) && npc.isScriptValue(1))
		{
			spawnMinions(npc);
			npc.setScriptValue(2);
		}
		else if ((npc.getCurrentHpPercent() <= 25) && npc.isScriptValue(2))
		{
			spawnMinions(npc);
			npc.setScriptValue(3);
		}
	}
	
	public void spawnMinions(Npc npc)
	{
		final StatsSet param = npc.getParameters();
		final int kartiaLevel = param.getInt("cartia_level", 0);
		final boolean isParty = param.getInt("party_type", 0) == 1;
		
		int fighter = 0;
		int mage = 0;
		switch (kartiaLevel)
		{
			case 85:
			{
				fighter = isParty ? FIGHTER_GROUP_85 : FIGHTER_SOLO_85;
				mage = isParty ? MAGE_GROUP_85 : MAGE_SOLO_85;
				break;
			}
			case 90:
			{
				fighter = isParty ? FIGHTER_GROUP_90 : FIGHTER_SOLO_90;
				mage = isParty ? MAGE_GROUP_90 : MAGE_SOLO_90;
				break;
			}
			case 95:
			{
				fighter = isParty ? FIGHTER_GROUP_95 : FIGHTER_SOLO_95;
				mage = isParty ? MAGE_GROUP_95 : MAGE_SOLO_95;
				break;
			}
		}
		
		if ((fighter > 0) && (mage > 0))
		{
			for (int i = 0; i < 3; i++)
			{
				addSpawn(fighter, npc, true, 0, false, npc.getInstanceId());
				addSpawn(mage, npc, true, 0, false, npc.getInstanceId());
			}
		}
	}
	
	public static void main(String[] args)
	{
		new KartiaBoss();
	}
}