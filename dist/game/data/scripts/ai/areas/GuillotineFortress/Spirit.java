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
package ai.areas.GuillotineFortress;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Spirit AI.
 * @author Gladicek
 */
public class Spirit extends AbstractNpcAI
{
	// NPCs
	private static final int EXECUTED_MAIDEN_VENGEFUL_SPIRIT = 33682;
	private static final int SPIRIT_OF_THE_TORTURED_DWARF = 33683;
	private static final int SPIRIT_OF_ONE_BURNED_ALIVE = 33684;
	// Misc
	private static final NpcStringId[] EXECUTED_MAIDEN_VENGEFUL_SPIRIT_SHOUT =
	{
		NpcStringId.SADNESS_SURROUNDS_ME,
		NpcStringId.DO_YOU_KNOW_HOW_IT_FEELS_TO_DIE_ON_THOSE_YOU_LOVE,
		NpcStringId.I_WILL_BRING_PEACE_BACK_TO_THIS_PLACE,
		NpcStringId.THE_GUILLOTINE_OF_DEATH_IS_HIDING_YOUR_IDENTITY,
		NpcStringId.THE_GUILLOTINE_ALWAYS_MAKES_ME_COWER,
		NpcStringId.SOMETIMES_I_CANNOT_ACCEPT_MY_DEATH,
		NpcStringId.I_DIED_WITHOUT_MERCY_IN_THIS_PLACE,
		NpcStringId.MY_BRUTAL_CURSE_KEEPS_ME_TRAPPED_IN_THIS_PLACE,
		NpcStringId.I_CANNOT_RELY_ON_ANYONE_ANYMORE,
		NpcStringId.HURRY_AND_ESCAPE_FROM_THIS_PLACE,
		NpcStringId.I_LOVE_YOU_PLEASE_DON_T_FORGET_ME,
		NpcStringId.I_REALLY_DIDN_T_WANT_TO_DIE,
	};
	private static final NpcStringId[] SPIRIT_OF_THE_TORTURED_DWARF_SHOUT =
	{
		NpcStringId.YOU_CAN_YOU_SEE_ME,
		NpcStringId.THIS_PLACE_IS_ALL_FILLED_WITH_SUFFOCATING_DARKNESS,
		NpcStringId.MY_BODY_WAS_BEATEN_TO_A_PULP,
		NpcStringId.I_WILL_NEVER_FORGET_THE_FACE_OF_MY_TORTURER,
		NpcStringId.MY_ENTIRE_BODY_WAS_DESTROYED_BY_FEAR,
		NpcStringId.NO_GO_AWAY_DON_T_LET_ANYONE_COME_TOWARD_ME,
		NpcStringId.I_LEFT_MY_FAMILY_BEHIND_AND_WAS_DRAGGED_TO_THIS_PLACE,
		NpcStringId.IF_I_LET_GO_OF_MY_GRUDGE_WILL_I_BE_ABLE_TO_ESCAPE_FROM_THIS_PLACE,
		NpcStringId.I_WANT_TO_FEEL_THE_GROUND_UNDER_MY_FEET_BUT_I_NO_LONGER_HAVE_ANY_WEIGHT,
	};
	private static final NpcStringId[] SPIRIT_OF_ONE_BURNED_ALIVE_SHOUT =
	{
		NpcStringId.HOT_TOO_HOT,
		NpcStringId.THE_AIR_HERE_FEELS_LIKE_IT_S_BURNING,
		NpcStringId.GET_AWAY_GET_THAT_FIRE_AWAY_FROM_ME,
		NpcStringId.ACK_AAAHHHH,
		NpcStringId.I_M_ALWAYS_COVERED_IN_SWEAT_SO_PITIFUL,
		NpcStringId.I_DON_T_KNOW_HOW_MANY_TIMES_I_PLEADED_FOR_MY_LIFE,
		NpcStringId.PLEASE_DESTROY_THE_GUILLOTINE_THAT_IS_MY_WISH,
		NpcStringId.CRACKLE_CRACKLE_THAT_SOUND_DRIVES_ME_CRAZY,
		NpcStringId.I_REMEMBER_THE_EYES_OF_THE_MONSTERS_THAT_LOOKED_UPON_ME,
		NpcStringId.PLEASE_JUST_KILL_ME,
		NpcStringId.I_HATE_RED_THE_COLOR_OF_FIRE_AND_BLOOD,
	};
	
	private Spirit()
	{
		addSpawnId(EXECUTED_MAIDEN_VENGEFUL_SPIRIT, SPIRIT_OF_THE_TORTURED_DWARF, SPIRIT_OF_ONE_BURNED_ALIVE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "SPAM_TEXT1":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, EXECUTED_MAIDEN_VENGEFUL_SPIRIT_SHOUT[getRandom(12)], 1000);
				break;
			}
			case "SPAM_TEXT2":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, SPIRIT_OF_THE_TORTURED_DWARF_SHOUT[getRandom(9)], 1000);
				break;
			}
			case "SPAM_TEXT3":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, SPIRIT_OF_ONE_BURNED_ALIVE_SHOUT[getRandom(11)], 1000);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case EXECUTED_MAIDEN_VENGEFUL_SPIRIT:
			{
				cancelQuestTimer("SPAM_TEXT1", npc, null);
				startQuestTimer("SPAM_TEXT1", 50000, npc, null, true);
				break;
			}
			case SPIRIT_OF_THE_TORTURED_DWARF:
			{
				cancelQuestTimer("SPAM_TEXT2", npc, null);
				startQuestTimer("SPAM_TEXT2", 50000, npc, null, true);
				break;
			}
			case SPIRIT_OF_ONE_BURNED_ALIVE:
			{
				cancelQuestTimer("SPAM_TEXT3", npc, null);
				startQuestTimer("SPAM_TEXT3", 50000, npc, null, true);
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Spirit();
	}
}