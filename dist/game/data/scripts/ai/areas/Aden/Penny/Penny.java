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
package ai.areas.Aden.Penny;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Aden Faction Npc AI
 * @author NightBR
 * @date 2019-03-27
 */
public class Penny extends AbstractNpcAI
{
	// NPC
	private static final int PENNY = 34413;
	// Misc
	private static final String[] RANDOM_VOICE =
	{
		"Npcdialog1.peny_ep50_greeting_7",
		"Npcdialog1.peny_ep50_greeting_8",
		"Npcdialog1.peny_ep50_greeting_9"
	};
	
	private Penny()
	{
		addStartNpc(PENNY);
		addTalkId(PENNY);
		addFirstTalkId(PENNY);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "medal":
			{
				// Take medal / Give rep?
				return null;
			}
			case "grand_medal":
			{
				// Take medal / Give rep?
				return null;
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		player.sendPacket(new PlaySound(3, RANDOM_VOICE[getRandom(3)], 0, 0, 0, 0, 0));
		return "34413.html";
	}
	
	public static void main(String[] args)
	{
		new Penny();
	}
}
