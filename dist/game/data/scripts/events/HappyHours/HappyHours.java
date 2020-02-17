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
package events.HappyHours;

import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.LongTimeEvent;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Mobius
 */
public class HappyHours extends LongTimeEvent
{
	// NPC
	private static final int SIBI = 34262;
	// Items
	private static final int SUPPLY_BOX = 47399;
	private static final int SIBIS_COIN = 49783;
	// Skill
	private static final int TRANSFORMATION_SKILL = 39171;
	// Other
	private static final int MIN_LEVEL = 20;
	private static final int REWARD_INTERVAL = 60 * 60 * 1000; // 1 hour
	private static long _lastRewardTime = System.currentTimeMillis();
	
	private HappyHours()
	{
		addStartNpc(SIBI);
		addFirstTalkId(SIBI);
		addTalkId(SIBI);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34262-1.htm":
			{
				htmltext = event;
				break;
			}
			case "giveSupplyBox":
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					return "34262-2.htm";
				}
				if (hasQuestItems(player, SUPPLY_BOX))
				{
					return "34262-3.htm";
				}
				giveItems(player, SUPPLY_BOX, 1);
				break;
			}
			case "REWARD_SIBI_COINS":
			{
				if (isEventPeriod())
				{
					if ((System.currentTimeMillis() - (_lastRewardTime + REWARD_INTERVAL)) > 0) // Exploit check - Just in case.
					{
						_lastRewardTime = System.currentTimeMillis();
						final ExShowScreenMessage screenMsg = new ExShowScreenMessage("You obtained 20 Sibi's coins.", ExShowScreenMessage.TOP_CENTER, 7000, 0, true, true);
						final SystemMessage systemMsg = new SystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1_SIBI_S_COINS);
						systemMsg.addInt(20);
						for (PlayerInstance plr : World.getInstance().getPlayers())
						{
							if ((plr != null) && (plr.isOnlineInt() == 1) && plr.isAffectedBySkill(TRANSFORMATION_SKILL))
							{
								plr.addItem("HappyHours", SIBIS_COIN, 20, player, false);
								plr.sendPacket(screenMsg);
								plr.sendPacket(systemMsg);
								// TODO: Random reward.
							}
						}
					}
				}
				else
				{
					cancelQuestTimers("REWARD_SIBI_COINS");
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "34262.htm";
	}
	
	@Override
	protected void startEvent()
	{
		super.startEvent();
		cancelQuestTimers("REWARD_SIBI_COINS");
		startQuestTimer("REWARD_SIBI_COINS", REWARD_INTERVAL + 1000, null, null, true);
	}
	
	public static void main(String[] args)
	{
		new HappyHours();
	}
}
