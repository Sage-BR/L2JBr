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
package ai.others.DimensionalMerchant;

import java.util.HashMap;

import org.l2jbr.gameserver.handler.IItemHandler;
import org.l2jbr.gameserver.handler.ItemHandler;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExGetPremiumItemList;

import ai.AbstractNpcAI;

/**
 * Dimensional Merchant AI.
 * @author St3eT
 */
public class DimensionalMerchant extends AbstractNpcAI
{
	// NPC
	private static final int MERCHANT = 32478; // Dimensional Merchant
	// Items
	private static final int MINION_COUPON = 13273; // Minion Coupon (5-hour)
	private static final int MINION_COUPON_EV = 13383; // Minion Coupon (5-hour) (Event)
	private static final int SUP_MINION_COUPON = 14065; // Superior Minion Coupon - 5-hour
	private static final int SUP_MINION_COUPON_EV = 14074; // Superior Minion Coupon (Event) - 5-hour
	private static final int ENH_MINION_COUPON = 20914; // Enhanced Rose Spirit Coupon (5-hour)
	private static final int ENH_MINION_COUPON_EV = 22240; // Enhanced Rose Spirit Coupon (5-hour) - Event
	// Misc
	private static final HashMap<String, Integer> MINION_EXCHANGE = new HashMap<>();
	static
	{
		// Normal
		MINION_EXCHANGE.put("whiteWeasel", 13017); // White Weasel Minion Necklace
		MINION_EXCHANGE.put("fairyPrincess", 13018); // Fairy Princess Minion Necklace
		MINION_EXCHANGE.put("wildBeast", 13019); // Wild Beast Fighter Minion Necklace
		MINION_EXCHANGE.put("foxShaman", 13020); // Fox Shaman Minion Necklace
		// Superior
		MINION_EXCHANGE.put("toyKnight", 14061); // Toy Knight Summon Whistle
		MINION_EXCHANGE.put("spiritShaman", 14062); // Spirit Shaman Summon Whistle
		MINION_EXCHANGE.put("turtleAscetic", 14064); // Turtle Ascetic Summon Necklace
		// Enhanced
		MINION_EXCHANGE.put("desheloph", 20915); // Enhanced Rose Necklace: Desheloph
		MINION_EXCHANGE.put("hyum", 20916); // Enhanced Rose Necklace: Hyum
		MINION_EXCHANGE.put("lekang", 20917); // Enhanced Rose Necklace: Lekang
		MINION_EXCHANGE.put("lilias", 20918); // Enhanced Rose Necklace: Lilias
		MINION_EXCHANGE.put("lapham", 20919); // Enhanced Rose Necklace: Lapham
		MINION_EXCHANGE.put("mafum", 20920); // Enhanced Rose Necklace: Mafum
	}
	
	private DimensionalMerchant()
	{
		addStartNpc(MERCHANT);
		addFirstTalkId(MERCHANT);
		addTalkId(MERCHANT);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "32478.html":
			case "32478-01.html":
			case "32478-02.html":
			case "32478-03.html":
			case "32478-04.html":
			case "32478-05.html":
			case "32478-06.html":
			case "32478-07.html":
			case "32478-08.html":
			case "32478-09.html":
			case "32478-10.html":
			case "32478-11.html":
			case "32478-12.html":
			case "32478-13.html":
			case "32478-14.html":
			case "32478-15.html":
			case "32478-16.html":
			case "32478-17.html":
			case "32478-18.html":
			case "32478-19.html":
			case "32478-20.html":
			case "32478-21.html":
			case "32478-22.html":
			case "32478-23.html":
			{
				htmltext = event;
				break;
			}
			case "getDimensonalItem":
			{
				if (player.getPremiumItemList().isEmpty())
				{
					player.sendPacket(SystemMessageId.THERE_ARE_NO_MORE_DIMENSIONAL_ITEMS_TO_BE_FOUND);
				}
				else
				{
					player.sendPacket(new ExGetPremiumItemList(player));
				}
				break;
			}
			case "whiteWeasel":
			case "fairyPrincess":
			case "wildBeast":
			case "foxShaman":
			{
				htmltext = giveMinion(player, event, MINION_COUPON, MINION_COUPON_EV);
				break;
			}
			case "toyKnight":
			case "spiritShaman":
			case "turtleAscetic":
			{
				htmltext = giveMinion(player, event, SUP_MINION_COUPON, SUP_MINION_COUPON_EV);
				break;
			}
			case "desheloph":
			case "hyum":
			case "lekang":
			case "lilias":
			case "lapham":
			case "mafum":
			{
				htmltext = giveMinion(player, event, ENH_MINION_COUPON, ENH_MINION_COUPON_EV);
				break;
			}
		}
		return htmltext;
	}
	
	private String giveMinion(PlayerInstance player, String event, int couponId, int eventCouponId)
	{
		if (hasAtLeastOneQuestItem(player, couponId, eventCouponId))
		{
			takeItems(player, (hasQuestItems(player, eventCouponId) ? eventCouponId : couponId), 1);
			final int minionId = MINION_EXCHANGE.get(event);
			giveItems(player, minionId, 1);
			final ItemInstance summonItem = player.getInventory().getItemByItemId(minionId);
			final IItemHandler handler = ItemHandler.getInstance().getHandler(summonItem.getEtcItem());
			if ((handler != null) && !player.hasPet())
			{
				handler.useItem(player, summonItem, true);
			}
			return "32478-08.html";
		}
		return "32478-07.html";
	}
	
	public static void main(String[] args)
	{
		new DimensionalMerchant();
	}
}