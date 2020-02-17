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
package ai.others.CastleWarehouse;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Castle Warehouse Keeper AI.
 * @author malyelfik
 */
public class CastleWarehouse extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		35099, // Warehouse Keeper (Gludio)
		35141, // Warehouse Keeper (Dion)
		35183, // Warehouse Keeper (Giran)
		35225, // Warehouse Keeper (Oren)
		35273, // Warehouse Keeper (Aden)
		35315, // Warehouse Keeper (Inadril)
		35362, // Warehouse Keeper (Goddard)
		35508, // Warehouse Keeper (Rune)
		35554, // Warehouse Keeper (Schuttgart)
	};
	// Items
	private static final int BLOOD_OATH = 9910;
	private static final int BLOOD_ALLIANCE = 9911;
	
	private CastleWarehouse()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
		addFirstTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final boolean isMyLord = player.isClanLeader() ? (player.getClan().getCastleId() == (npc.getCastle() != null ? npc.getCastle().getResidenceId() : -1)) : false;
		switch (event)
		{
			case "warehouse-01.html":
			case "warehouse-02.html":
			case "warehouse-03.html":
			{
				break;
			}
			case "warehouse-04.html":
			{
				htmltext = (!isMyLord) ? "warehouse-no.html" : getHtm(player, "warehouse-04.html").replace("%blood%", Integer.toString(player.getClan().getBloodAllianceCount()));
				break;
			}
			case "Receive":
			{
				if (!isMyLord)
				{
					htmltext = "warehouse-no.html";
				}
				else if (player.getClan().getBloodAllianceCount() == 0)
				{
					htmltext = "warehouse-05.html";
				}
				else
				{
					giveItems(player, BLOOD_ALLIANCE, player.getClan().getBloodAllianceCount());
					player.getClan().resetBloodAllianceCount();
					htmltext = "warehouse-06.html";
				}
				break;
			}
			case "Exchange":
			{
				if (!isMyLord)
				{
					htmltext = "warehouse-no.html";
				}
				else if (!hasQuestItems(player, BLOOD_ALLIANCE))
				{
					htmltext = "warehouse-08.html";
				}
				else
				{
					takeItems(player, BLOOD_ALLIANCE, 1);
					giveItems(player, BLOOD_OATH, 30);
					htmltext = "warehouse-07.html";
				}
				break;
			}
			default:
			{
				htmltext = null;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "warehouse-01.html";
	}
	
	public static void main(String[] args)
	{
		new CastleWarehouse();
	}
}