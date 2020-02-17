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
package org.l2jbr.gameserver.handler;

import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.skills.targets.AffectObject;

/**
 * @author Nik
 */
public interface IAffectObjectHandler
{
	/**
	 * Checks if the rules for the given affect object type are accepted or not.
	 * @param creature
	 * @param target
	 * @return {@code true} if target should be accepted, {@code false} otherwise
	 **/
	boolean checkAffectedObject(Creature creature, Creature target);
	
	Enum<AffectObject> getAffectObjectType();
}
