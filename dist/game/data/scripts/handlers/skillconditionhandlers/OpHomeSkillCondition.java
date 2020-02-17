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
package handlers.skillconditionhandlers;

import org.l2jbr.gameserver.data.xml.impl.ClanHallData;
import org.l2jbr.gameserver.enums.ResidenceType;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.instancemanager.FortManager;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.skills.ISkillCondition;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * @author Sdw
 */
public class OpHomeSkillCondition implements ISkillCondition
{
	private final ResidenceType _type;
	
	public OpHomeSkillCondition(StatsSet params)
	{
		_type = params.getEnum("type", ResidenceType.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		if (caster.isPlayer())
		{
			final Clan clan = caster.getActingPlayer().getClan();
			if (clan != null)
			{
				switch (_type)
				{
					case CASTLE:
					{
						return CastleManager.getInstance().getCastleByOwner(clan) != null;
					}
					case FORTRESS:
					{
						return FortManager.getInstance().getFortByOwner(clan) != null;
					}
					case CLANHALL:
					{
						return ClanHallData.getInstance().getClanHallByClan(clan) != null;
					}
				}
			}
		}
		
		return false;
	}
}
