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
package ai.others.ClanHallDoorManager;

import java.util.StringTokenizer;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.ClanPrivilege;
import org.l2jbr.gameserver.model.entity.ClanHall;

import ai.AbstractNpcAI;

/**
 * Clan Hall Door Manager AI.
 * @author St3eT
 */
public class ClanHallDoorManager extends AbstractNpcAI
{
	// NPCs
	// @formatter:off
	private static final int[] DOOR_MANAGERS =
	{
		35385, 35387, 35389, 35391, // Gludio
		35393, 35395, 35397, 35399, 35401, // Gludin
		35402, 35404, 35406, // Dion
		35440, 35442, 35444, 35446, 35448, 35450, // Aden
		35452, 35454, 35456, 35458, 35460, // Giran
		35462, 35464, 35466, 35468, // Goddard
		35567, 35569, 35571, 35573, 35575, 35577, 35579, // Rune
		35581, 35583, 35585, 35587, // Schuttgart
		36722, 36724, 36726, 36728, // Gludio Outskirts
		36730, 36732, 36734, 36736, // Dion Outskirts
		36738, 36740, // Floran Village		
	};
	// @formatter:on
	
	private ClanHallDoorManager()
	{
		addStartNpc(DOOR_MANAGERS);
		addTalkId(DOOR_MANAGERS);
		addFirstTalkId(DOOR_MANAGERS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final StringTokenizer st = new StringTokenizer(event, " ");
		final String action = st.nextToken();
		final ClanHall clanHall = npc.getClanHall();
		String htmltext = null;
		
		if (clanHall != null)
		{
			switch (action)
			{
				case "index":
				{
					htmltext = onFirstTalk(npc, player);
					break;
				}
				case "manageDoors":
				{
					if (isOwningClan(player, npc) && st.hasMoreTokens() && player.hasClanPrivilege(ClanPrivilege.CH_OPEN_DOOR))
					{
						final boolean open = st.nextToken().equals("1");
						clanHall.openCloseDoors(open);
						htmltext = "ClanHallDoorManager-0" + (open ? "5" : "6") + ".html";
					}
					else
					{
						htmltext = "ClanHallDoorManager-04.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final ClanHall clanHall = npc.getClanHall();
		if (isOwningClan(player, npc))
		{
			htmltext = getHtm(player, "ClanHallDoorManager-01.html");
			htmltext = htmltext.replace("%ownerClanName%", clanHall.getOwner().getName());
		}
		else if (clanHall.getOwnerId() <= 0)
		{
			htmltext = "ClanHallDoorManager-02.html";
		}
		else
		{
			htmltext = getHtm(player, "ClanHallDoorManager-03.html");
			htmltext = htmltext.replace("%ownerName%", clanHall.getOwner().getLeaderName());
			htmltext = htmltext.replace("%ownerClanName%", clanHall.getOwner().getName());
		}
		return htmltext;
	}
	
	private boolean isOwningClan(PlayerInstance player, Npc npc)
	{
		return ((npc.getClanHall().getOwnerId() == player.getClanId()) && (player.getClanId() != 0));
	}
	
	public static void main(String[] args)
	{
		new ClanHallDoorManager();
	}
}