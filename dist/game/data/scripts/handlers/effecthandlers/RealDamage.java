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
package handlers.effecthandlers;

import org.l2jbr.Config;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * @author Sdw
 */
public class RealDamage extends AbstractEffect
{
	private final double _power;
	
	public RealDamage(StatsSet params)
	{
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		// Check if fake players should aggro each other.
		if (effector.isFakePlayer() && !Config.FAKE_PLAYER_AGGRO_FPC && effected.isFakePlayer())
		{
			return;
		}
		
		effected.reduceCurrentHp(_power, effector, skill, false, false, false, false);
		if (effector.isPlayer())
		{
			effector.sendDamageMessage(effected, skill, (int) _power, false, false);
		}
	}
}
