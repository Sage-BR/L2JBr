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
package ai.areas.BloodySwampland;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * Bloody Swampland AI.
 * @author St3eT
 */
public class BloodySwampland extends AbstractNpcAI
{
	// NPCs
	private static final int COLLECTOR = 23171; // Corpse Collector
	
	public BloodySwampland()
	{
		addAttackId(COLLECTOR);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		if (npc.isScriptValue(0) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.3)))
		{
			addSkillCastDesire(npc, attacker, npc.getParameters().getSkillHolder("Skill01_ID"), 23);
			npc.setScriptValue(1);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	public static void main(String[] args)
	{
		new BloodySwampland();
	}
}