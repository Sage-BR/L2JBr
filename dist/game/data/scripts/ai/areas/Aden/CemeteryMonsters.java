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
package ai.areas.Aden;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * Cemetery Monsters AI
 * @author Gigi
 * @date 2018-07-10 - [20:17:37]
 */
public class CemeteryMonsters extends AbstractNpcAI
{
	private static final int CHIEF_QUARTERMASTER = 23296;
	private static final int ROYAL_QUARTERMASTER = 23298;
	private static final int PERSONAL_MAGICIAN = 23291;
	private static final int ADEN_RAIDER = 19455;
	
	private static final int[] MONSTERS =
	{
		23290, // Royal Knight
		PERSONAL_MAGICIAN, // Personal Magician
		23292, // Royal Guard
		23293, // Royal Guard Captain
		23294, // Chief Magician
		23295, // Operations Manager
		CHIEF_QUARTERMASTER, // Chief Quartermaster
		23297, // Escort
		ROYAL_QUARTERMASTER, // Royal Quartermaster
		23299, // Operations Chief of the 7th Divisiony
		23300 // Commander of Operations
	};
	
	private static final NpcStringId[] SHOUT_MSG =
	{
		NpcStringId.WHAT_IS_THIS_SO_DISORIENTING,
		NpcStringId.WHAT_WOULD_YOU_KNOW_OF_LOYALTY,
		NpcStringId.I_CANNOT_I_WILL_NOT_BACK_DOWN,
		NpcStringId.MY_STRENGTH_HAS_NOT_YET_FULLY_RETURNED,
		NpcStringId.DO_NOT_DEFILE_THIS_PLACE_WITH_YOUR_PRESENCE,
		NpcStringId.HEH_INTERESTING_TRICK_YOU_USE_THERE,
		NpcStringId.I_WILL_REMEMBER_YOU,
		NpcStringId.I_WON_T_BEG_FOR_MY_LIFE_DO_WHAT_YOU_WILL,
		NpcStringId.BEHOLD_MY_POWER,
		NpcStringId.HIS_MAJESTY_TRAVIS_HAS_ORDERED_IMMEDIATE_EXECUTION,
		NpcStringId.ARE_YOU_TRYING_TO_MAKE_ME_DIE_A_SECOND_DEATH,
		NpcStringId.WHAT_FOOLISHNESS,
		NpcStringId.FOOL,
		NpcStringId.REMEMBER_OUR_HISTORY_OUR_GLORIOUS_HISTORY,
		NpcStringId.WHO_ARE_YOU_YOU_WEREN_T_HERE_BEFORE,
		NpcStringId.I_DON_T_HAVE_MUCH_TIME_LEFT,
		NpcStringId.I_M_TEN_STEPS_AHEAD_OF_YOU_DON_T_BOTHER_TRYING,
		NpcStringId.MAY_ADEN_LIVE_ON_FOREVER,
		NpcStringId.YOU_WON_T_LAY_A_FINGER_ON_HIS_MAJESTY_TRAVIS,
		NpcStringId.I_WILL_AVENGE_MY_FALLEN_COMRADES,
		NpcStringId.I_DON_T_HAVE_MUCH_TIME_LEFT,
		NpcStringId.A_MOUNTAIN_OF_CORPSES,
		NpcStringId.I_WILL_DESTROY_YOU_JUST_AS_I_HAVE_DESTROYED_THE_ENEMY_FORCES,
		NpcStringId.WELL_WHAT_DO_YOU_KNOW_YOU_RE_GUTSIER_THAN_YOU_LOOK,
		NpcStringId.WE_WERE_THE_ONES_WHO_CLEANSED_THIS_PLACE_OF_EVIL_PESTS,
		NpcStringId.TODAY_S_YOUR_JUDGMENT_DAY,
		NpcStringId.A_LONG_SLUMBER_HAS_COME_TO_AN_END,
		NpcStringId.LOOK_UPON_THIS_GREAT_GRAVE_AND_TELL_ME_YOU_SEE_NOTHING,
		NpcStringId.YOU,
		NpcStringId.AGH_MY_EYE_MY_EYE,
		NpcStringId.SO_THIS_IS_THE_END,
		NpcStringId.NO_USE_TRYING_YOUR_PETTY_TRICKS,
		NpcStringId.FEEL_FOR_YOURSELF_THE_STRENGTH_OF_MY_WILL,
		NpcStringId.I_HEAR_HIS_MAJESTY_S_VOICE_I_HEAR_IT_EVERY_WAKING_MOMENT,
	};
	
	private CemeteryMonsters()
	{
		addAttackId(MONSTERS);
		addKillId(CHIEF_QUARTERMASTER, ROYAL_QUARTERMASTER, PERSONAL_MAGICIAN);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		if (getRandom(25) < 5)
		{
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), getRandomEntry(SHOUT_MSG)));
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		if (getRandom(100) < 5)
		{
			switch (npc.getId())
			{
				case CHIEF_QUARTERMASTER:
				case ROYAL_QUARTERMASTER:
				{
					killer.addExpAndSp(getRandom(15_000_000, 20_000_00), 0);
					npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.S1_HAS_OBTAINED_BONUS_XP_FOR_DEFEATING_A_RARE_A_GRADE_SOLDIER, ExShowScreenMessage.BOTTOM_CENTER, 10000, false, killer.getName()));
					break;
				}
				case PERSONAL_MAGICIAN:
				{
					for (int i = 0; i < 6; i++)
					{
						Npc raider = addSpawn(ADEN_RAIDER, killer, true, 180000, false);
						addAttackPlayerDesire(raider, killer);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new CemeteryMonsters();
	}
}
