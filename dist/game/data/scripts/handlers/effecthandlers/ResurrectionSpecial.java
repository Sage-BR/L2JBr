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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PetInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.effects.EffectFlag;
import org.l2jbr.gameserver.model.effects.EffectType;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * Resurrection Special effect implementation.
 * @author Zealar
 */
public class ResurrectionSpecial extends AbstractEffect
{
	private final int _power;
	private final Set<Integer> _instanceId;
	
	public ResurrectionSpecial(StatsSet params)
	{
		_power = params.getInt("power", 0);
		
		final String instanceIds = params.getString("instanceId", null);
		if ((instanceIds != null) && !instanceIds.isEmpty())
		{
			_instanceId = new HashSet<>();
			for (String id : instanceIds.split(";"))
			{
				_instanceId.add(Integer.parseInt(id));
			}
		}
		else
		{
			_instanceId = Collections.<Integer> emptySet();
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RESURRECTION_SPECIAL;
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.RESURRECTION_SPECIAL.getMask();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (!effected.isPlayer() && !effected.isPet())
		{
			return;
		}
		
		final PlayerInstance caster = effector.getActingPlayer();
		final Instance instance = caster.getInstanceWorld();
		if (!_instanceId.isEmpty() && ((instance == null) || !_instanceId.contains(instance.getTemplateId())))
		{
			return;
		}
		
		if (effected.isPlayer())
		{
			effected.getActingPlayer().reviveRequest(caster, skill, false, _power);
		}
		else if (effected.isPet())
		{
			final PetInstance pet = (PetInstance) effected;
			effected.getActingPlayer().reviveRequest(pet.getActingPlayer(), skill, true, _power);
		}
	}
}