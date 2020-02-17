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

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.stat.PlayerStat;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.BuffInfo;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.zone.ZoneId;

/**
 * Recover Vitality in Peace Zone effect implementation.
 * @author Mobius
 */
public class RecoverVitalityInPeaceZone extends AbstractEffect
{
	private final double _amount;
	private final int _ticks;
	
	public RecoverVitalityInPeaceZone(StatsSet params)
	{
		_amount = params.getDouble("amount", 0);
		_ticks = params.getInt("ticks", 10);
	}
	
	@Override
	public int getTicks()
	{
		return _ticks;
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if ((effected == null) //
			|| effected.isDead() //
			|| !effected.isPlayer() //
			|| !effected.isInsideZone(ZoneId.PEACE))
		{
			return false;
		}
		
		long vitality = effected.getActingPlayer().getVitalityPoints();
		vitality += _amount;
		if (vitality >= PlayerStat.MAX_VITALITY_POINTS)
		{
			vitality = PlayerStat.MAX_VITALITY_POINTS;
		}
		effected.getActingPlayer().setVitalityPoints((int) vitality, true);
		
		return skill.isToggle();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if ((effected != null) //
			&& effected.isPlayer())
		{
			final BuffInfo info = effected.getEffectList().getBuffInfoBySkillId(skill.getId());
			if ((info != null) && !info.isRemoved())
			{
				long vitality = effected.getActingPlayer().getVitalityPoints();
				vitality += _amount * 100;
				if (vitality >= PlayerStat.MAX_VITALITY_POINTS)
				{
					vitality = PlayerStat.MAX_VITALITY_POINTS;
				}
				effected.getActingPlayer().setVitalityPoints((int) vitality, true);
			}
		}
	}
}
