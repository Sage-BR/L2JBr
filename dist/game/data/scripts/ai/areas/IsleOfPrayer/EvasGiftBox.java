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
package ai.areas.IsleOfPrayer;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * Eva's Gift Box AI.
 * @author St3eT
 */
public class EvasGiftBox extends AbstractNpcAI
{
	// NPC
	private static final int BOX = 32342; // Eva's Gift Box
	// Skill
	private static final SkillHolder KISS_OF_EVA = new SkillHolder(1073, 1); // Kiss of Eva
	// Items
	private static final int CORAL = 9692; // Red Coral
	private static final int CRYSTAL = 9693; // Crystal Fragment
	
	private EvasGiftBox()
	{
		addKillId(BOX);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		if (killer.hasAbnormalType(KISS_OF_EVA.getSkill().getAbnormalType())) // It was checking if abnormal level is > 0. All cases when has this abnormal type, level is > 0.
		{
			if (getRandomBoolean())
			{
				npc.dropItem(killer, CRYSTAL, 1);
			}
			
			if (getRandom(100) < 33)
			{
				npc.dropItem(killer, CORAL, 1);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new EvasGiftBox();
	}
}