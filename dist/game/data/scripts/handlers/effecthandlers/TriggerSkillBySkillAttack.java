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
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.enums.InstanceType;
import org.l2jbr.gameserver.handler.ITargetTypeHandler;
import org.l2jbr.gameserver.handler.TargetHandler;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureDamageDealt;
import org.l2jbr.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.BuffInfo;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.model.skills.targets.TargetType;

/**
 * Trigger Skill By Skill Attack effect implementation.
 * @author Nik
 */
public class TriggerSkillBySkillAttack extends AbstractEffect
{
	private final int _minAttackerLevel;
	private final int _maxAttackerLevel;
	private final int _minDamage;
	private final int _chance;
	private final SkillHolder _attackSkill;
	private final SkillHolder _skill;
	private final int _skillLevelScaleTo;
	private final TargetType _targetType;
	private final InstanceType _attackerType;
	
	public TriggerSkillBySkillAttack(StatsSet params)
	{
		_minAttackerLevel = params.getInt("minAttackerLevel", 1);
		_maxAttackerLevel = params.getInt("maxAttackerLevel", 127);
		_minDamage = params.getInt("minDamage", 1);
		_chance = params.getInt("chance", 100);
		_skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1));
		_attackSkill = new SkillHolder(params.getInt("attackSkillId"), params.getInt("attackSkillLevel", 1));
		_skillLevelScaleTo = params.getInt("skillLevelScaleTo", 0);
		_targetType = params.getEnum("targetType", TargetType.class, TargetType.TARGET);
		_attackerType = params.getEnum("attackerType", InstanceType.class, InstanceType.Creature);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_DAMAGE_DEALT, (OnCreatureDamageDealt event) -> onAttackEvent(event), this));
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_DAMAGE_DEALT, listener -> listener.getOwner() == this);
	}
	
	private void onAttackEvent(OnCreatureDamageDealt event)
	{
		if (event.isDamageOverTime() || (_chance == 0) || ((_skill.getSkillId() == 0) || (_skill.getSkillLevel() == 0)) || (_attackSkill.getSkillId() == 0))
		{
			return;
		}
		
		if (event.getSkill() == null)
		{
			return;
		}
		
		if (event.getSkill().getId() != _attackSkill.getSkillId())
		{
			return;
		}
		
		final ITargetTypeHandler targetHandler = TargetHandler.getInstance().getHandler(_targetType);
		if (targetHandler == null)
		{
			LOGGER.warning("Handler for target type: " + _targetType + " does not exist.");
			return;
		}
		
		if (event.getAttacker() == event.getTarget())
		{
			return;
		}
		
		if ((event.getAttacker().getLevel() < _minAttackerLevel) || (event.getAttacker().getLevel() > _maxAttackerLevel))
		{
			return;
		}
		
		if ((event.getDamage() < _minDamage) || ((_chance < 100) && (Rnd.get(100) > _chance)) || !event.getAttacker().getInstanceType().isType(_attackerType))
		{
			return;
		}
		
		Skill triggerSkill = _skill.getSkill();
		WorldObject target = null;
		try
		{
			target = TargetHandler.getInstance().getHandler(_targetType).getTarget(event.getAttacker(), event.getTarget(), triggerSkill, false, false, false);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
		}
		
		if ((target != null) && target.isCreature())
		{
			if (_skillLevelScaleTo > 0)
			{
				final BuffInfo buffInfo = ((Creature) target).getEffectList().getBuffInfoBySkillId(_skill.getSkillId());
				if (buffInfo != null)
				{
					triggerSkill = SkillData.getInstance().getSkill(_skill.getSkillId(), Math.min(_skillLevelScaleTo, buffInfo.getSkill().getLevel() + 1));
				}
			}
			
			SkillCaster.triggerCast(event.getAttacker(), (Creature) target, triggerSkill);
		}
	}
}
