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
package org.l2jbr.gameserver.model.stats.finalizers;

import java.util.OptionalDouble;

import org.l2jbr.Config;
import org.l2jbr.gameserver.data.xml.impl.ClanHallData;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.instancemanager.FortManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PetInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.entity.Castle;
import org.l2jbr.gameserver.model.entity.Castle.CastleFunction;
import org.l2jbr.gameserver.model.entity.Fort;
import org.l2jbr.gameserver.model.entity.Fort.FortFunction;
import org.l2jbr.gameserver.model.residences.AbstractResidence;
import org.l2jbr.gameserver.model.residences.ResidenceFunction;
import org.l2jbr.gameserver.model.residences.ResidenceFunctionType;
import org.l2jbr.gameserver.model.stats.BaseStats;
import org.l2jbr.gameserver.model.stats.IStatsFunction;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.model.zone.type.CastleZone;
import org.l2jbr.gameserver.model.zone.type.ClanHallZone;
import org.l2jbr.gameserver.model.zone.type.FortZone;
import org.l2jbr.gameserver.model.zone.type.MotherTreeZone;

/**
 * @author UnAfraid
 */
public class RegenMPFinalizer implements IStatsFunction
{
	@Override
	public double calc(Creature creature, OptionalDouble base, Stats stat)
	{
		throwIfPresent(base);
		
		double baseValue = creature.isPlayer() ? creature.getActingPlayer().getTemplate().getBaseMpRegen(creature.getLevel()) : creature.getTemplate().getBaseMpReg();
		baseValue *= creature.isRaid() ? Config.RAID_MP_REGEN_MULTIPLIER : Config.MP_REGEN_MULTIPLIER;
		
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			
			if (player.isInsideZone(ZoneId.CLAN_HALL) && (player.getClan() != null) && (player.getClan().getHideoutId() > 0))
			{
				final ClanHallZone zone = ZoneManager.getInstance().getZone(player, ClanHallZone.class);
				final int posChIndex = zone == null ? -1 : zone.getResidenceId();
				final int clanHallIndex = player.getClan().getHideoutId();
				if ((clanHallIndex > 0) && (clanHallIndex == posChIndex))
				{
					final AbstractResidence residense = ClanHallData.getInstance().getClanHallById(player.getClan().getHideoutId());
					if (residense != null)
					{
						final ResidenceFunction func = residense.getFunction(ResidenceFunctionType.MP_REGEN);
						if (func != null)
						{
							baseValue *= func.getValue();
						}
					}
				}
			}
			
			if (player.isInsideZone(ZoneId.CASTLE) && (player.getClan() != null) && (player.getClan().getCastleId() > 0))
			{
				final CastleZone zone = ZoneManager.getInstance().getZone(player, CastleZone.class);
				final int posCastleIndex = zone == null ? -1 : zone.getResidenceId();
				final int castleIndex = player.getClan().getCastleId();
				if ((castleIndex > 0) && (castleIndex == posCastleIndex))
				{
					final Castle castle = CastleManager.getInstance().getCastleById(player.getClan().getCastleId());
					if (castle != null)
					{
						final CastleFunction func = castle.getCastleFunction(Castle.FUNC_RESTORE_MP);
						if (func != null)
						{
							baseValue *= (func.getLvl() / 100);
						}
					}
				}
			}
			
			if (player.isInsideZone(ZoneId.FORT) && (player.getClan() != null) && (player.getClan().getFortId() > 0))
			{
				final FortZone zone = ZoneManager.getInstance().getZone(player, FortZone.class);
				final int posFortIndex = zone == null ? -1 : zone.getResidenceId();
				final int fortIndex = player.getClan().getFortId();
				if ((fortIndex > 0) && (fortIndex == posFortIndex))
				{
					final Fort fort = FortManager.getInstance().getFortById(player.getClan().getCastleId());
					if (fort != null)
					{
						final FortFunction func = fort.getFortFunction(Fort.FUNC_RESTORE_MP);
						if (func != null)
						{
							baseValue *= (func.getLvl() / 100);
						}
					}
				}
			}
			
			// Mother Tree effect is calculated at last'
			if (player.isInsideZone(ZoneId.MOTHER_TREE))
			{
				final MotherTreeZone zone = ZoneManager.getInstance().getZone(player, MotherTreeZone.class);
				final int mpBonus = zone == null ? 0 : zone.getMpRegenBonus();
				baseValue += mpBonus;
			}
			
			// Calculate Movement bonus
			if (player.isSitting())
			{
				baseValue *= 1.5; // Sitting
			}
			else if (!player.isMoving())
			{
				baseValue *= 1.1; // Staying
			}
			else if (player.isRunning())
			{
				baseValue *= 0.7; // Running
			}
			
			// Add MEN bonus
			baseValue *= creature.getLevelMod() * BaseStats.MEN.calcBonus(creature);
		}
		else if (creature.isPet())
		{
			baseValue = ((PetInstance) creature).getPetLevelData().getPetRegenMP() * Config.PET_MP_REGEN_MULTIPLIER;
		}
		
		return Stats.defaultValue(creature, stat, baseValue);
	}
}
