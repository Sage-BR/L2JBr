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
package ai.areas.GainakUnderground.TavernEmployee;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * Tavern Employee AI.
 * @author Edoo
 */
public class TavernEmployee extends AbstractNpcAI
{
	// NPCs
	private static final int LOYEE1 = 34202;
	private static final int LOYEE2 = 34203;
	private static final int LOYEE3 = 34204;
	private static final int LOYEE4 = 34205;
	private static final int LOYEE5 = 34206;
	private static final int LOYEE6 = 34207;
	// Text
	private static final NpcStringId[] SPAM_TEXT1 =
	{
		NpcStringId.SIGH_BUSY_AS_ALWAYS,
		NpcStringId.HOW_LONG_UNTIL_WE_CAN_TELL_STORIES_TO_THE_CUSTOMERS_TOO,
		NpcStringId.I_WONDER_WHAT_HANNA_WILL_BE_MAKING_TODAY,
		NpcStringId.HEY_YOU_WE_DON_T_WANT_DRUNK_CUSTOMERS_HERE,
		NpcStringId.WHAT_DO_YOU_THINK_ABOUT_OUR_TAVERN_ISN_T_IT_GREAT,
		NpcStringId.YOU_CAN_PLACE_YOUR_ORDER_OVER_THERE,
		NpcStringId.JUST_RELAX_AND_HAVE_A_DRINK,
		NpcStringId.HANNA_S_COOKING_IS_THE_BEST,
		NpcStringId.YOU_ARE_A_REGULAR_RIGHT_THANKS_FOR_COMING_AGAIN,
		NpcStringId.HERE_YOU_CAN_FORGET_ABOUT_YOUR_RESPONSIBILITIES_FOR_A_WHILE,
		NpcStringId.LUPIA_INTRODUCED_ME_HERE_SO_THAT_S_HOW_I_STARTED_WORKING_HERE,
		NpcStringId.IS_THERE_ANYTHING_TO_CLEAN_UP,
		NpcStringId.I_WONDER_IF_THERE_S_ANYONE_COMING_FROM_THAT_SIDE,
		NpcStringId.I_THINK_WE_CAN_WAIT_FOR_SOME_MORE_CUSTOMERS,
	};
	private static final NpcStringId[] SPAM_TEXT2 =
	{
		NpcStringId.THE_MYSTIC_TAVERN_IS_OPEN_NOW
	};
	private static final NpcStringId[] SPAM_TEXT3 =
	{
		NpcStringId.SIGH_BUSY_AS_ALWAYS,
		NpcStringId.HOW_LONG_UNTIL_WE_CAN_TELL_STORIES_TO_THE_CUSTOMERS_TOO,
		NpcStringId.I_WONDER_WHAT_HANNA_WILL_BE_MAKING_TODAY,
		NpcStringId.HEY_YOU_WE_DON_T_WANT_DRUNK_CUSTOMERS_HERE,
		NpcStringId.WHAT_DO_YOU_THINK_ABOUT_OUR_TAVERN_ISN_T_IT_GREAT,
		NpcStringId.YOU_CAN_PLACE_YOUR_ORDER_OVER_THERE,
		NpcStringId.JUST_RELAX_AND_HAVE_A_DRINK,
		NpcStringId.HANNA_S_COOKING_IS_THE_BEST,
		NpcStringId.YOU_ARE_A_REGULAR_RIGHT_THANKS_FOR_COMING_AGAIN,
		NpcStringId.HERE_YOU_CAN_FORGET_ABOUT_YOUR_RESPONSIBILITIES_FOR_A_WHILE,
		NpcStringId.LUPIA_INTRODUCED_ME_HERE_SO_THAT_S_HOW_I_STARTED_WORKING_HERE,
		NpcStringId.IS_THERE_ANYTHING_TO_CLEAN_UP,
		NpcStringId.I_WONDER_IF_THERE_S_ANYONE_COMING_FROM_THAT_SIDE,
		NpcStringId.I_THINK_WE_CAN_WAIT_FOR_SOME_MORE_CUSTOMERS,
	};
	private static final NpcStringId[] SPAM_TEXT4 =
	{
		NpcStringId.ADVENTURER_THE_TAVERN_IS_THIS_WAY,
		NpcStringId.ARE_YOU_LOOKING_FOR_THE_TAVERN_IT_S_THIS_WAY,
		NpcStringId.COME_ON_CHANCES_LIKE_THESE_DON_T_COME_BY_OFTEN
	};
	private static final NpcStringId[] SPAM_TEXT5 =
	{
		NpcStringId.SIGH_BUSY_AS_ALWAYS,
		NpcStringId.HOW_LONG_UNTIL_WE_CAN_TELL_STORIES_TO_THE_CUSTOMERS_TOO,
		NpcStringId.I_WONDER_WHAT_HANNA_WILL_BE_MAKING_TODAY,
		NpcStringId.HEY_YOU_WE_DON_T_WANT_DRUNK_CUSTOMERS_HERE,
		NpcStringId.WHAT_DO_YOU_THINK_ABOUT_OUR_TAVERN_ISN_T_IT_GREAT,
		NpcStringId.YOU_CAN_PLACE_YOUR_ORDER_OVER_THERE,
		NpcStringId.JUST_RELAX_AND_HAVE_A_DRINK,
		NpcStringId.HANNA_S_COOKING_IS_THE_BEST,
		NpcStringId.YOU_ARE_A_REGULAR_RIGHT_THANKS_FOR_COMING_AGAIN,
		NpcStringId.HERE_YOU_CAN_FORGET_ABOUT_YOUR_RESPONSIBILITIES_FOR_A_WHILE,
		NpcStringId.LUPIA_INTRODUCED_ME_HERE_SO_THAT_S_HOW_I_STARTED_WORKING_HERE,
		NpcStringId.IS_THERE_ANYTHING_TO_CLEAN_UP,
		NpcStringId.I_WONDER_IF_THERE_S_ANYONE_COMING_FROM_THAT_SIDE,
		NpcStringId.I_THINK_WE_CAN_WAIT_FOR_SOME_MORE_CUSTOMERS,
	};
	private static final NpcStringId[] SPAM_TEXT6 =
	{
		NpcStringId.SIGH_BUSY_AS_ALWAYS,
		NpcStringId.HOW_LONG_UNTIL_WE_CAN_TELL_STORIES_TO_THE_CUSTOMERS_TOO,
		NpcStringId.I_WONDER_WHAT_HANNA_WILL_BE_MAKING_TODAY,
		NpcStringId.HEY_YOU_WE_DON_T_WANT_DRUNK_CUSTOMERS_HERE,
		NpcStringId.WHAT_DO_YOU_THINK_ABOUT_OUR_TAVERN_ISN_T_IT_GREAT,
		NpcStringId.YOU_CAN_PLACE_YOUR_ORDER_OVER_THERE,
		NpcStringId.JUST_RELAX_AND_HAVE_A_DRINK,
		NpcStringId.HANNA_S_COOKING_IS_THE_BEST,
		NpcStringId.YOU_ARE_A_REGULAR_RIGHT_THANKS_FOR_COMING_AGAIN,
		NpcStringId.HERE_YOU_CAN_FORGET_ABOUT_YOUR_RESPONSIBILITIES_FOR_A_WHILE,
		NpcStringId.LUPIA_INTRODUCED_ME_HERE_SO_THAT_S_HOW_I_STARTED_WORKING_HERE,
		NpcStringId.IS_THERE_ANYTHING_TO_CLEAN_UP,
		NpcStringId.I_WONDER_IF_THERE_S_ANYONE_COMING_FROM_THAT_SIDE,
		NpcStringId.I_THINK_WE_CAN_WAIT_FOR_SOME_MORE_CUSTOMERS,
	};
	
	private TavernEmployee()
	{
		addSpawnId(LOYEE1);
		addSpawnId(LOYEE2);
		addSpawnId(LOYEE3);
		addSpawnId(LOYEE4);
		addSpawnId(LOYEE5);
		addSpawnId(LOYEE6);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "spam_text1":
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), SPAM_TEXT1[getRandom(SPAM_TEXT1.length)]));
				break;
			}
			case "spam_text2":
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), SPAM_TEXT2[getRandom(SPAM_TEXT2.length)]));
				break;
			}
			case "spam_text3":
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), SPAM_TEXT3[getRandom(SPAM_TEXT3.length)]));
				break;
			}
			case "spam_text4":
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), SPAM_TEXT4[getRandom(SPAM_TEXT4.length)]));
				break;
			}
			case "spam_text5":
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), SPAM_TEXT5[getRandom(SPAM_TEXT5.length)]));
				break;
			}
			case "spam_text6":
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), SPAM_TEXT6[getRandom(SPAM_TEXT6.length)]));
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setIsTalkable(false);
		
		if (npc.getId() == LOYEE1)
		{
			startQuestTimer("spam_text1", 17000, npc, null, true);
		}
		if (npc.getId() == LOYEE2)
		{
			startQuestTimer("spam_text2", 180000, npc, null, true);
		}
		if (npc.getId() == LOYEE3)
		{
			startQuestTimer("spam_text3", 16000, npc, null, true);
		}
		if (npc.getId() == LOYEE4)
		{
			startQuestTimer("spam_text4", 180000, npc, null, true);
		}
		if (npc.getId() == LOYEE5)
		{
			startQuestTimer("spam_text5", 15000, npc, null, true);
		}
		if (npc.getId() == LOYEE6)
		{
			startQuestTimer("spam_text6", 18000, npc, null, true);
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new TavernEmployee();
	}
}