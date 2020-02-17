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
package handlers.targethandlers.affectscope;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.l2jbr.gameserver.handler.AffectObjectHandler;
import org.l2jbr.gameserver.handler.IAffectObjectHandler;
import org.l2jbr.gameserver.handler.IAffectScopeHandler;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.targets.AffectScope;

/**
 * Range sorted by lowest to highest hp percent affect scope implementation.
 * @author Nik
 */
public class RangeSortByHp implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature creature, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final int affectRange = skill.getAffectRange();
		final int affectLimit = skill.getAffectLimit();
		
		// Target checks.
		final AtomicInteger affected = new AtomicInteger(0);
		final Predicate<Creature> filter = c ->
		{
			if ((affectLimit > 0) && (affected.get() >= affectLimit))
			{
				return false;
			}
			
			if (c.isDead())
			{
				return false;
			}
			
			// Range skills appear to not affect you unless you are the main target.
			if ((c == creature) && (target != creature))
			{
				return false;
			}
			
			if ((affectObject != null) && !affectObject.checkAffectedObject(creature, c))
			{
				return false;
			}
			
			affected.incrementAndGet();
			return true;
		};
		
		final List<Creature> result = World.getInstance().getVisibleObjectsInRange(target, Creature.class, affectRange, filter);
		
		// Add object of origin since its skipped in the getVisibleObjects method.
		if (target.isCreature() && filter.test((Creature) target))
		{
			result.add((Creature) target);
		}
		
		// Sort from lowest hp to highest hp.
		//@formatter:off
		result.stream()
		.sorted(Comparator.comparingInt(Creature::getCurrentHpPercent))
		.limit(affectLimit > 0 ? affectLimit : Long.MAX_VALUE)
		.forEach(action);
		//@formatter:on
	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.RANGE_SORT_BY_HP;
	}
}
