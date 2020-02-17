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
package ai.areas.Aden.Ruine;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Ruine AI
 * @author Gigi
 * @date 2017-02-18 - [20:14:22]
 */
public class Ruine extends AbstractNpcAI
{
	// NPC
	private static final int COD_ADEN_OFFICER = 34229;
	// Level checks
	private static final int MIN_LEVEL_CRACK = 95;
	private static final int MIN_LEVEL_RIFT = 100;
	// Teleports
	private static final Location DIMENSIONAL_CRACK = new Location(-119304, -182456, -6752);
	private static final Location DIMENSIONAL_RIFT = new Location(140629, 79672, -5424);
	
	private Ruine()
	{
		addStartNpc(COD_ADEN_OFFICER);
		addFirstTalkId(COD_ADEN_OFFICER);
		addTalkId(COD_ADEN_OFFICER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "cod_aden_officer001.htm":
			case "cod_aden_officer004.htm":
			case "cod_aden_officer005.htm":
			{
				htmltext = event;
				break;
			}
			case "crack_teleport":
			{
				if (player.getLevel() >= MIN_LEVEL_CRACK)
				{
					player.teleToLocation(DIMENSIONAL_CRACK);
					break;
				}
				htmltext = "cod_aden_officer003.htm";
				break;
			}
			case "rift_teleport":
			{
				if (player.getLevel() >= MIN_LEVEL_RIFT)
				{
					player.teleToLocation(DIMENSIONAL_RIFT);
					break;
				}
				htmltext = "cod_aden_officer003.htm";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "cod_aden_officer001.htm";
	}
	
	public static void main(String[] args)
	{
		new Ruine();
	}
}
