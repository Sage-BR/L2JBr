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

import java.util.logging.Level;

import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.handler.ITargetTypeHandler;
import org.l2jbr.gameserver.handler.TargetHandler;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureAttackAvoid;
import org.l2jbr.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.model.skills.targets.TargetType;

/**
 * Trigger Skill By Avoid effect implementation.
 * @author Zealar
 */
public class TriggerSkillByAvoid extends AbstractEffect
{
	private final int _chance;
	private final SkillHolder _skill;
	private final TargetType _targetType;
	
	/**
	 * @param params
	 */
	public TriggerSkillByAvoid(StatsSet params)
	{
		_chance = params.getInt("chance", 100);
		_skill = new SkillHolder(params.getInt("skillId", 0), params.getInt("skillLevel", 0));
		_targetType = params.getEnum("targetType", TargetType.class, TargetType.TARGET);
	}
	
	private void onAvoidEvent(OnCreatureAttackAvoid event)
	{
		if (event.isDamageOverTime() || (_chance == 0) || ((_skill.getSkillId() == 0) || (_skill.getSkillLevel() == 0)))
		{
			return;
		}
		
		final ITargetTypeHandler targetHandler = TargetHandler.getInstance().getHandler(_targetType);
		if (targetHandler == null)
		{
			LOGGER.warning("Handler for target type: " + _targetType + " does not exist.");
			return;
		}
		
		if ((_chance < 100) && (Rnd.get(100) > _chance))
		{
			return;
		}
		
		final Skill triggerSkill = _skill.getSkill();
		WorldObject target = null;
		try
		{
			target = TargetHandler.getInstance().getHandler(_targetType).getTarget(event.getTarget(), event.getAttacker(), triggerSkill, false, false, false);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
		}
		
		if ((target != null) && target.isCreature())
		{
			SkillCaster.triggerCast(event.getAttacker(), (Creature) target, triggerSkill);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_ATTACK_AVOID, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_ATTACK_AVOID, (OnCreatureAttackAvoid event) -> onAvoidEvent(event), this));
	}
}
