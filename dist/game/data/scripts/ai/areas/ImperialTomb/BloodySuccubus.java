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
package ai.areas.ImperialTomb;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Simple AI for Bloody Succubus.
 * @author Gigi
 */
public class BloodySuccubus extends AbstractNpcAI
{
	// NPCs
	private static final int BLOODY_SUCCUBUS = 23185;
	private static final int SUCCUBUS_OF_DEATH = 23191;
	private static final int SUCCUBUS_OF_DARKNESS = 23192;
	private static final int SUCCUBUS_OF_LUNACY = 23197;
	private static final int SUCCUBUS_OF_SILENCE = 23198;
	// Attack messages
	private static final NpcStringId[] ON_ATTACK_MSG_BLOODY_SUCCUBUS =
	{
		NpcStringId.HEY_THAT_HURT_YOU_JUST_WAIT_HERE_AND_I_LL_BE_BACK_AS_A_STRONGER_DRAGON,
		NpcStringId.HAHAHA_YOU_DARE_TO_DISRUPT_ME_I_WILL_BE_YOUR_NIGHTMARE_FROM_WHICH_YOU_CAN_NEVER_AWAKEN,
		NpcStringId.I_CANNOT_LET_YOU_STOP_THE_WRAITH_OF_SHILEN,
		NpcStringId.YOU_DARE_ATTACK_ME_I_WILL_FILL_YOUR_NIGHTMARES_WITH_BLOOD,
		NpcStringId.HALT_YOUR_NIGHTMARES_WILL_FILL_YOU_WITH_DREAD
	};
	private static final NpcStringId[] ON_ATTACK_MSG_SUCCUBUS_OF_DEATH =
	{
		NpcStringId.HOW_FOOLISH_THE_PRICE_OF_ATTACKING_ME_IS_DEATH,
		NpcStringId.PREPARE_I_SHALL_GRANT_YOU_DEATH,
		NpcStringId.MY_SWORD_WILL_TAKE_YOUR_LIFE,
		NpcStringId.OH_SHILEN_GIVE_ME_STRENGTH,
		NpcStringId.YAAAH
	};
	private static final NpcStringId[] ON_ATTACK_MSG_SUCCUBUS_OF_DARKNESS =
	{
		NpcStringId.HYAAAAAAH,
		NpcStringId.ARE_YOU_THE_ONE_TO_SHATTER_THE_PEACE,
		NpcStringId.OUR_MISSION_IS_TO_RESURRECT_THE_GODDESS_DO_NOT_INTERFERE,
		NpcStringId.FEEL_THE_TRUE_TERROR_OF_DARKNESS
	};
	private static final NpcStringId[] ON_ATTACK_MSG_SUCCUBUS_OF_LUNACY =
	{
		NpcStringId.HEHEHE_I_M_GLAD_YOU_CAME_I_WAS_HUNGRY,
		NpcStringId.SMALL_FRY_I_WILL_SHOW_YOU_TRUE_MADNESS_HAHAHA,
		NpcStringId.HEHEHE_PREPARE_MY_MADNESS_WILL_SWALLOW_YOU_UP,
		NpcStringId.HEHEHE_SHALL_WE_PLAY,
		NpcStringId.HEHEHE_PREPARE_MY_MADNESS_WILL_SWALLOW_YOU_UP
	};
	private static final NpcStringId[] ON_ATTACK_MSG_SUCCUBUS_OF_SILENCE =
	{
		NpcStringId.FOR_THE_GODDESS,
		NpcStringId.YOU_WILL_DIE,
		NpcStringId.DIE_2,
		NpcStringId.YOU_WILL_BE_DESTROYED,
		NpcStringId.OOOOH,
		NpcStringId.DO_NOT_INTERFERE
	};
	// Death messages
	private static final NpcStringId[] ON_FAILED_MSG_BLOODY_SUCCUBUS =
	{
		NpcStringId.AH,
		NpcStringId.SHILEN_I_HAVE_FAILED,
		NpcStringId.HOW_ALL_THAT_POWER_REMOVED,
		NpcStringId.TO_THINK_THAT_I_COULD_FAIL_IMPOSSIBLE
	};
	private static final NpcStringId[] ON_FAILED_MSG_SUCCUBUS_OF_DEATH =
	{
		NpcStringId.NO_I_LOST_ALL_THE_GATHERED_POWER_OF_LIGHT_TO_THIS_THIS,
		NpcStringId.I_WOULD_DEFEATED,
		NpcStringId.DON_T_THINK_THIS_IS_THE_END
	};
	private static final NpcStringId[] ON_FAILED_MSG_SUCCUBUS_OF_DARKNESS =
	{
		NpcStringId.I_WILL_ALWAYS_WATCH_YOU_FROM_THE_DARKNESS,
		NpcStringId.NO_N_NO_NO,
		NpcStringId.I_MUSTN_T_LOSE_THE_STRENGTH,
		NpcStringId.OH_CREATURES_OF_THE_GODDESS_LEND_ME_YOUR_STRENGTH
	};
	private static final NpcStringId[] ON_FAILED_MSG_SUCCUBUS_OF_LUNACY =
	{
		NpcStringId.ACK_NO_MY_BODY_IT_S_DISAPPEARING,
		NpcStringId.KYAAAH,
		NpcStringId.HUHUHU_HUHUHU_HUHAHAHA,
		NpcStringId.HUH_WHAT_HAPPENED_I_I_LOST
	};
	private static final NpcStringId[] ON_FAILED_MSG_SUCCUBUS_OF_SILENCE =
	{
		NpcStringId.IS_THIS_THE_END_2,
		NpcStringId.OH_GODDESS,
		NpcStringId.NO_I_DIDN_T_STAY_SILENT_ALL_THIS_TIME_JUST_TO_DISAPPEAR_NOW_LIKE_THIS
	};
	
	private BloodySuccubus()
	{
		addAttackId(BLOODY_SUCCUBUS, SUCCUBUS_OF_DEATH, SUCCUBUS_OF_DARKNESS, SUCCUBUS_OF_LUNACY, SUCCUBUS_OF_SILENCE);
		addKillId(BLOODY_SUCCUBUS, SUCCUBUS_OF_DEATH, SUCCUBUS_OF_DARKNESS, SUCCUBUS_OF_LUNACY, SUCCUBUS_OF_SILENCE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "say":
			{
				if (npc.isDead() || !npc.isScriptValue(1))
				{
					cancelQuestTimer("say", npc, null);
					return null;
				}
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_ATTACK_MSG_BLOODY_SUCCUBUS));
				break;
			}
			case "say1":
			{
				if (npc.isDead() || !npc.isScriptValue(1))
				{
					cancelQuestTimer("say1", npc, null);
					return null;
				}
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_ATTACK_MSG_SUCCUBUS_OF_DEATH));
				break;
			}
			case "say2":
			{
				if (npc.isDead() || !npc.isScriptValue(1))
				{
					cancelQuestTimer("say2", npc, null);
					return null;
				}
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_ATTACK_MSG_SUCCUBUS_OF_DARKNESS));
				break;
			}
			case "say3":
			{
				if (npc.isDead() || !npc.isScriptValue(1))
				{
					cancelQuestTimer("say3", npc, null);
					return null;
				}
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_ATTACK_MSG_SUCCUBUS_OF_LUNACY));
				break;
			}
			case "say4":
			{
				if (npc.isDead() || !npc.isScriptValue(1))
				{
					cancelQuestTimer("say4", npc, null);
					return null;
				}
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_ATTACK_MSG_SUCCUBUS_OF_SILENCE));
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		switch (npc.getId())
		{
			case BLOODY_SUCCUBUS:
			{
				if (npc.isScriptValue(0))
				{
					npc.setScriptValue(1);
					startQuestTimer("say", (getRandom(5) + 3) * 1000, npc, null, true);
				}
				break;
			}
			case SUCCUBUS_OF_DEATH:
			{
				if (npc.isScriptValue(0))
				{
					npc.setScriptValue(1);
					startQuestTimer("say1", (getRandom(5) + 3) * 1000, npc, null, true);
				}
				break;
			}
			case SUCCUBUS_OF_DARKNESS:
			{
				if (npc.isScriptValue(0))
				{
					npc.setScriptValue(1);
					startQuestTimer("say2", (getRandom(5) + 3) * 1000, npc, null, true);
				}
				break;
			}
			case SUCCUBUS_OF_LUNACY:
			{
				if (npc.isScriptValue(0))
				{
					npc.setScriptValue(1);
					startQuestTimer("say3", (getRandom(5) + 3) * 1000, npc, null, true);
				}
				break;
			}
			case SUCCUBUS_OF_SILENCE:
			{
				if (npc.isScriptValue(0))
				{
					npc.setScriptValue(1);
					startQuestTimer("say4", (getRandom(5) + 3) * 1000, npc, null, true);
				}
				break;
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		switch (npc.getId())
		{
			case BLOODY_SUCCUBUS:
			{
				cancelQuestTimer("say", npc, player);
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_FAILED_MSG_BLOODY_SUCCUBUS));
				break;
			}
			case SUCCUBUS_OF_DEATH:
			{
				cancelQuestTimer("say1", npc, player);
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_FAILED_MSG_SUCCUBUS_OF_DEATH));
				break;
			}
			case SUCCUBUS_OF_DARKNESS:
			{
				cancelQuestTimer("say2", npc, player);
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_FAILED_MSG_SUCCUBUS_OF_DARKNESS));
				break;
			}
			case SUCCUBUS_OF_LUNACY:
			{
				cancelQuestTimer("say3", npc, player);
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_FAILED_MSG_SUCCUBUS_OF_LUNACY));
				break;
			}
			case SUCCUBUS_OF_SILENCE:
			{
				cancelQuestTimer("say4", npc, player);
				npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(ON_FAILED_MSG_SUCCUBUS_OF_SILENCE));
				break;
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	public static void main(String[] args)
	{
		new BloodySuccubus();
	}
}