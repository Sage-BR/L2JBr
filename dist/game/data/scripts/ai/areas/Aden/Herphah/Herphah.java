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
package ai.areas.Aden.Herphah;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Aden Faction Npc AI
 * @author NightBR
 * @date 2019-03-27
 */
public class Herphah extends AbstractNpcAI
{
	// NPC
	private static final int HERPHAH = 34362;
	// Misc
	@SuppressWarnings("unused")
	private static final String[] RANDOM_VOICE =
	{
		"Npcdialog1.herphah_ep50_greeting_1",
		"Npcdialog1.herphah_ep50_greeting_2",
		"Npcdialog1.herphah_ep50_greeting_3"
	};
	
	private Herphah()
	{
		addStartNpc(HERPHAH);
		addTalkId(HERPHAH);
		addFirstTalkId(HERPHAH);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "34362-01.html":
			case "34362-02.html":
			case "34362-03.html":
			case "34362-04.html":
			case "34362-05.html":
			case "34362-06.html":
			case "34362-07.html":
			case "34362-08.html":
			case "34362-09.html":
			case "34362-10.html":
			{
				return event;
			}
			default:
			{
				return null;
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		// Chance to broadcast at nearby players?
		// player.sendPacket(new PlaySound(RANDOM_VOICE[getRandom(3)]));
		return "34362.html";
	}
	
	public static void main(String[] args)
	{
		new Herphah();
	}
}
