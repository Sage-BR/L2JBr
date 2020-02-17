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
package ai.areas.FrozenLabyrinth;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.skills.Skill;

import ai.AbstractNpcAI;

/**
 * Frozen Labyrinth AI.
 * @author malyelfik
 */
public class FrozenLabyrinth extends AbstractNpcAI
{
	// Monsters
	private static final int PRONGHORN_SPIRIT = 22087;
	private static final int PRONGHORN = 22088;
	private static final int LOST_BUFFALO = 22093;
	private static final int FROST_BUFFALO = 22094;
	
	private FrozenLabyrinth()
	{
		addAttackId(PRONGHORN, FROST_BUFFALO);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		if (npc.isScriptValue(0) && (skill != null) && !skill.isMagic())
		{
			final int spawnId = (npc.getId() == PRONGHORN) ? PRONGHORN_SPIRIT : LOST_BUFFALO;
			int diff = 0;
			for (int i = 0; i < 6; i++)
			{
				final int x = diff < 60 ? npc.getX() + diff : npc.getX();
				final int y = diff >= 60 ? npc.getY() + (diff - 40) : npc.getY();
				
				final Npc monster = addSpawn(spawnId, x, y, npc.getZ(), npc.getHeading(), false, 0);
				addAttackPlayerDesire(monster, attacker);
				diff += 20;
			}
			npc.setScriptValue(1);
			npc.deleteMe();
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	public static void main(String[] args)
	{
		new FrozenLabyrinth();
	}
}