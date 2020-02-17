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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.l2jbr.gameserver.handler.AffectObjectHandler;
import org.l2jbr.gameserver.handler.IAffectObjectHandler;
import org.l2jbr.gameserver.handler.IAffectScopeHandler;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Playable;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.targets.AffectScope;

/**
 * @author Nik
 */
public class Pledge implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature creature, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final int affectRange = skill.getAffectRange();
		final int affectLimit = skill.getAffectLimit();
		
		if (target.isPlayable())
		{
			final Playable playable = (Playable) target;
			final PlayerInstance player = playable.getActingPlayer();
			final int clanId = player.getClanId();
			
			// Create the target filter.
			final AtomicInteger affected = new AtomicInteger(0);
			final Predicate<Playable> filter = c ->
			{
				if ((affectLimit > 0) && (affected.get() >= affectLimit))
				{
					return false;
				}
				
				final PlayerInstance p = c.getActingPlayer();
				if ((p == null) || p.isDead())
				{
					return false;
				}
				if (p != player)
				{
					final int pClanId = p.getClanId();
					if ((pClanId == 0) || (pClanId != clanId))
					{
						return false;
					}
				}
				if ((affectObject != null) && !affectObject.checkAffectedObject(creature, p))
				{
					return false;
				}
				
				affected.incrementAndGet();
				return true;
			};
			
			// Add object of origin since its skipped in the forEachVisibleObjectInRange method.
			if (filter.test(playable))
			{
				action.accept(playable);
			}
			
			// Check and add targets.
			World.getInstance().forEachVisibleObjectInRange(playable, Playable.class, affectRange, c ->
			{
				if (filter.test(c))
				{
					action.accept(c);
				}
			});
		}
	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.PLEDGE;
	}
}
