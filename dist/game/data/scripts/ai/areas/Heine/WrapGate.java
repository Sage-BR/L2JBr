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
package ai.areas.Heine;

import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestState;

import ai.AbstractNpcAI;
import quests.Q10455_ElikiasLetter.Q10455_ElikiasLetter;

/**
 * Warp Gate AI.
 * @author Gigi
 */
public class WrapGate extends AbstractNpcAI
{
	// NPC
	private static final int WRAP_GATE = 33900;
	// Location
	private static final Location TELEPORT_LOC = new Location(-28575, 255984, -2195);
	
	private WrapGate()
	{
		addStartNpc(WRAP_GATE);
		addFirstTalkId(WRAP_GATE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if ("enter_hellbound".equals(event))
		{
			final QuestState qs = player.getQuestState(Q10455_ElikiasLetter.class.getSimpleName());
			if ((qs != null) && qs.isCond(1))
			{
				playMovie(player, Movie.SC_HELLBOUND);
			}
			player.teleToLocation(TELEPORT_LOC);
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "33900.html";
	}
	
	public static void main(String[] args)
	{
		new WrapGate();
	}
}