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
package instances.DimensionalWrap;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * Dimensional Archon AI
 * @author Gigi
 * @date 2018-09-08 - [18:09:02]
 */
public class DimensionalArchon extends AbstractNpcAI
{
	// NPCs
	private static final int DEMINSIONAL_ARCHON = 23469;
	private static final int UNWORDLY_ARCHON = 23475;
	private static final int ABYSSAL_ARCHON = 23482;
	
	private static final NpcStringId[] ARCHON_MSG =
	{
		NpcStringId.DO_YOU_KNOW_WHO_IT_IS_THAT_YOU_FACE_IT_IS_BEST_THAT_YOU_RUN_NOW,
		NpcStringId.LOOK_INTO_MY_EYES_AND_SEE_WHAT_COUNTLESS_OTHERS_HAVE_SEEN_BEFORE_THEIR_DEATH,
		NpcStringId.I_COMMEND_YOUR_TENACITY_IN_COMING_THIS_FAR_BUT_NOW_IT_ENDS,
		NpcStringId.DO_YOU_SEE_THIS_SWORD_THE_LIGHT_THAT_SCREAMS_WITH_THE_LIFE_OF_THOSE_IT_HAS_KILLED,
		NpcStringId.I_WILL_SHOW_YOU_WHAT_TRUE_POWER_IS,
		NpcStringId.YOU_WILL_NEED_TO_SURPASS_ME_EVENTUALLY_BUT_DON_T_FORGET_KNOWING_HOW_TO_FLEE_IS_AN_IMPORTANT_PART_OF_BATTLE
	};
	
	public DimensionalArchon()
	{
		super();
		addSpawnId(DEMINSIONAL_ARCHON, UNWORDLY_ARCHON, ABYSSAL_ARCHON);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("NPC_SHOUT"))
		{
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), getRandomEntry(ARCHON_MSG)));
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setShowSummonAnimation(true);
		startQuestTimer("NPC_SHOUT", 2000, npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new DimensionalArchon();
	}
}
