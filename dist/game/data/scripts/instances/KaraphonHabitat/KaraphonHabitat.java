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
package instances.KaraphonHabitat;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;

import instances.AbstractInstance;
import quests.Q10745_TheSecretIngredients.Q10745_TheSecretIngredients;

/**
 * Karaphon Habitat instance.
 * @author Sdw
 */
public class KaraphonHabitat extends AbstractInstance
{
	// NPCs
	private static final int DOLKIN = 33954;
	private static final int DOLKIN_INSTANCE = 34002;
	// Monsters
	private static final int KARAPHON = 23459;
	// Instance
	private static final int TEMPLATE_ID = 253;
	
	public KaraphonHabitat()
	{
		super(TEMPLATE_ID);
		addStartNpc(DOLKIN);
		addFirstTalkId(DOLKIN_INSTANCE);
		addTalkId(DOLKIN);
		addKillId(KARAPHON);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = player.getQuestState(Q10745_TheSecretIngredients.class.getSimpleName());
		if (qs != null)
		{
			switch (event)
			{
				case "enter_instance":
				{
					enterInstance(player, npc, TEMPLATE_ID);
					break;
				}
				case "exit_instance":
				{
					finishInstance(player, 0);
					break;
				}
			}
		}
		return null;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance world = killer.getInstanceWorld();
		if (world != null)
		{
			world.setReenterTime();
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new KaraphonHabitat();
	}
}