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
package ai.areas.Aden.Tarti;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Tarti AI
 * @author Gigi
 * @date 2019-08-17 - [22:44:02]
 */
public class Tarti extends AbstractNpcAI
{
	// NPC
	private static final int TARTI = 34360;
	// Misc
	private static final String[] TARTI_VOICE =
	{
		"Npcdialog1.tarti_ep50_greeting_8",
		"Npcdialog1.tarti_ep50_greeting_9",
		"Npcdialog1.tarti_ep50_greeting_10"
	};
	
	private Tarti()
	{
		addStartNpc(TARTI);
		addFirstTalkId(TARTI);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		player.sendPacket(new PlaySound(3, TARTI_VOICE[getRandom(3)], 0, 0, 0, 0, 0));
		return "34360.html";
	}
	
	public static void main(String[] args)
	{
		new Tarti();
	}
}
