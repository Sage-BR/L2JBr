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
package ai.areas.Rune.AltarOfSacrifice;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * Altar Of Sacrifice AI
 * @author Gigi
 */
public class AltarOfSacrifice extends AbstractNpcAI
{
	// NPCs
	private static final int IMMERIAL = 19478;
	private static final int JENNAS_GUARD = 33887;
	private static final int GIGGLE = 33812;
	
	private static Npc _immerial;
	private static Npc _jenas_guard;
	private static Npc _giggle;
	
	private AltarOfSacrifice()
	{
		addSeeCreatureId(JENNAS_GUARD);
		addSpawnId(IMMERIAL, JENNAS_GUARD, GIGGLE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equalsIgnoreCase("msg_text"))
		{
			sendMessage(_giggle, NpcStringId.IT_SURE_SEEMS_STURDY_BUT_WOULD_THIS_REALLY_BE_ABLE_TO_STOP_THE_SACRIFICES_HM, 10000); // It sure seems sturdy, but would this really be able to stop the sacrifices? Hm..
			sendMessage(_immerial, NpcStringId.WE_DID_MAKE_THIS_GENERATOR_AT_LADY_JENNA_S_SUGGESTION_BUT_I_M_STILL_NERVOUS, 25000); // We did make this Generator at Lady Jenna's suggestion, but...I'm still nervous.
			sendMessage(_jenas_guard, NpcStringId.YOU_NEED_TO_USE_A_SKILL_JUST_RIGHT_ON_THE_GENERATOR_TO_OBTAIN_A_SCALE_TALK_TO_JENNA_ABOUT_IT, 35000); // You need to use a skill just right on the Generator to obtain a scale. Talk to Jenna about it.
			sendMessage(_giggle, NpcStringId.RUMORS_HAVE_IT_THAT_LINDVIOR_IS_HEADED_THIS_WAY, 37000); // Rumors have it that Lindvior is headed this way.
			sendMessage(_giggle, NpcStringId.DO_YOU_THINK_HE_CAN_BE_STOPPED, 42000); // Do you think he can be stopped?
			sendMessage(_immerial, NpcStringId.FOR_NOW_WE_HAVE_NO_CHOICE_BUT_TO_RELY_ON_THESE_CANNONS_PLACED_AROUND_THE_GENERATORS, 52000); // For now, we have no choice but to rely on these cannons placed around the Generators.
			sendMessage(_immerial, NpcStringId.MAY_THE_GODS_WATCH_OVER_US, 57000); // May the gods watch over us
			sendMessage(_giggle, NpcStringId.I_VE_NEVER_SEEN_SO_MANY_SCHOLARS_AND_WIZARDS_IN_MY_LIFE, 87000); // I've never seen so many scholars and wizards in my life.
			sendMessage(_jenas_guard, NpcStringId.YOU_NEED_TO_USE_A_SKILL_JUST_RIGHT_ON_THE_GENERATOR_TO_OBTAIN_A_SCALE_TALK_TO_JENNA_ABOUT_IT, 97000); // You need to use a skill just right on the Generator to obtain a scale. Talk to Jenna about it.
			sendMessage(_immerial, NpcStringId.IT_S_NOT_EVERYDAY_YOU_GET_TO_SEE_SUCH_A_SIGHT_HUH, 99000); // It's not everyday you get to see such a sight, huh?
			sendMessage(_giggle, NpcStringId.IT_JUST_GOES_TO_SHOW_HOW_IMPORTANT_AND_DIFFICULT_IT_IS_TO_ACTIVATE_THE_SEAL_DEVICE, 109000); // It just goes to show how important and difficult it is to activate the Seal Device!
			sendMessage(_immerial, NpcStringId.THIS_HAS_BEEN_TOO_TAXING_ON_US_ALL, 119000); // This has been too taxing on us all.
			sendMessage(_immerial, NpcStringId.WE_NEED_A_NEW_SOUL_THAT_CAN_MAINTAIN_THE_SEAL, 126000); // We need a new soul that can maintain the seal.
			
			startQuestTimer("msg_text", 135000, npc, null);
			_jenas_guard.setScriptValue(0);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if ((creature != null) && creature.isPlayer() && _jenas_guard.isScriptValue(0))
		{
			startQuestTimer("msg_text", 3000, npc, null);
			_jenas_guard.setScriptValue(1);
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case IMMERIAL:
			{
				_immerial = npc;
				break;
			}
			case GIGGLE:
			{
				_giggle = npc;
				break;
			}
			case JENNAS_GUARD:
			{
				_jenas_guard = npc;
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	private void sendMessage(Npc npc, NpcStringId npcString, int delay)
	{
		ThreadPool.schedule(() ->
		{
			if (npc != null)
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), npcString));
			}
		}, delay);
	}
	
	public static void main(String[] args)
	{
		new AltarOfSacrifice();
	}
}
