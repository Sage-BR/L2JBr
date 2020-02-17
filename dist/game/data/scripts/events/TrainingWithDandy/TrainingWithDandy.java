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
package events.TrainingWithDandy;

import java.util.Calendar;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.quest.LongTimeEvent;

/**
 * Training with Dandy
 * @author Gigi
 * @date 2019-08-27 - [17:41:16]
 */
public class TrainingWithDandy extends LongTimeEvent
{
	// NPC
	private static final int DANDY = 33894;
	// Skill
	private static final SkillHolder DANDY_CH = new SkillHolder(17186, 1); // Dandy's Cheers
	// Misc
	private static final String GIVE_DANDI_BUFF_VAR = "GIVE_DANDI_BUFF";
	
	private TrainingWithDandy()
	{
		addStartNpc(DANDY);
		addFirstTalkId(DANDY);
		addTalkId(DANDY);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33894-1.htm":
			{
				htmltext = event;
				break;
			}
			case "dandy_buff":
			{
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MINUTE, 30);
				calendar.set(Calendar.HOUR_OF_DAY, 6);
				final long resetTime = calendar.getTimeInMillis();
				final long previousDate = player.getVariables().getLong(GIVE_DANDI_BUFF_VAR, 0);
				if (previousDate < resetTime)
				{
					npc.setTarget(player);
					npc.doCast(DANDY_CH.getSkill());
					player.getVariables().set(GIVE_DANDI_BUFF_VAR, System.currentTimeMillis());
					player.broadcastStatusUpdate();
					htmltext = "33894-2.htm";
					break;
				}
				htmltext = "33894-3.htm";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + "-1.htm";
	}
	
	public static void main(String[] args)
	{
		new TrainingWithDandy();
	}
}