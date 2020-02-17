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
package instances.EvasHiddenSpace;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;

import instances.AbstractInstance;
import quests.Q10369_NoblesseSoulTesting.Q10369_NoblesseSoulTesting;

/**
 * Eva's Hidden Space instance zone.
 * @author Gladicek, St3eT
 */
public class EvasHiddenSpace extends AbstractInstance
{
	// NPCs
	private static final int CERENAS = 31281;
	private static final int EVAS_AVATAR = 33686;
	// Misc
	private static final int TEMPLATE_ID = 217;
	
	public EvasHiddenSpace()
	{
		super(TEMPLATE_ID);
		addStartNpc(CERENAS);
		addTalkId(CERENAS, EVAS_AVATAR);
		addFirstTalkId(EVAS_AVATAR);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		final QuestState qs = player.getQuestState(Q10369_NoblesseSoulTesting.class.getSimpleName());
		
		if (event.equals("enterInstance"))
		{
			if ((qs != null) && qs.isStarted())
			{
				enterInstance(player, npc, TEMPLATE_ID);
				
				if (qs.isCond(14))
				{
					qs.setCond(15);
				}
				else if (qs.isCond(17))
				{
					qs.setCond(18);
				}
			}
			else if ((qs != null) && qs.isCompleted())
			{
				htmltext = "31281-01.html";
			}
			else
			{
				htmltext = "31281.html";
			}
		}
		else if (event.equals("exitInstance"))
		{
			final Instance world = getPlayerInstance(player);
			if (world != null)
			{
				teleportPlayerOut(player, world);
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new EvasHiddenSpace();
	}
}
