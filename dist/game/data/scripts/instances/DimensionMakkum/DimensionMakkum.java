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
package instances.DimensionMakkum;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;

import instances.AbstractInstance;
import quests.Q10658_MakkumInTheDimension.Q10658_MakkumInTheDimension;

/**
 * Dimension Makkum instance
 * @author Gigi
 */
public class DimensionMakkum extends AbstractInstance
{
	// NPCs
	private static final int PIORE = 34290;
	private static final int DIMENSIONAL_MAKKUM = 26195;
	// Misc
	private static final int TEMPLATE_ID = 10658;
	
	public DimensionMakkum()
	{
		super(TEMPLATE_ID);
		addFirstTalkId(PIORE);
		addKillId(DIMENSIONAL_MAKKUM);
		addInstanceCreatedId(TEMPLATE_ID);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final Instance world = getPlayerInstance(player);
		switch (event)
		{
			case "enter_instance":
			{
				enterInstance(player, npc, TEMPLATE_ID);
				break;
			}
			case "spawn_piore":
			{
				addSpawn(PIORE, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 300000, false, world.getId());
				break;
			}
			case "exitInstance":
			{
				if (world != null)
				{
					teleportPlayerOut(player, world);
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		addSpawn(DIMENSIONAL_MAKKUM, 185064, -9610, -5488, 19610, false, 430000, true, instance.getId());
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = killer.getQuestState(Q10658_MakkumInTheDimension.class.getSimpleName());
		final Instance world = npc.getInstanceWorld();
		if (world == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		startQuestTimer("spawn_piore", 4000, npc, killer);
		if ((qs != null) && qs.isCond(1))
		{
			qs.setCond(2, true);
		}
		world.finishInstance();
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "34290.html";
	}
	
	public static void main(String[] args)
	{
		new DimensionMakkum();
	}
}