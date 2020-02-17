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

import org.l2jbr.gameserver.enums.ShotType;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.effects.EffectType;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Formulas;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * Energy Attack effect implementation.
 * @author NosBit
 */
public class EnergyAttack extends AbstractEffect
{
	private final double _power;
	private final int _chargeConsume;
	private final int _criticalChance;
	private final boolean _ignoreShieldDefence;
	private final boolean _overHit;
	private final double _pDefMod;
	
	public EnergyAttack(StatsSet params)
	{
		_power = params.getDouble("power", 0);
		_criticalChance = params.getInt("criticalChance", 0);
		_ignoreShieldDefence = params.getBoolean("ignoreShieldDefence", false);
		_overHit = params.getBoolean("overHit", false);
		_chargeConsume = params.getInt("chargeConsume", 0);
		_pDefMod = params.getDouble("pDefMod", 1.0);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		// TODO: Verify this on retail
		return !Formulas.calcPhysicalSkillEvasion(effector, effected, skill);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.PHYSICAL_ATTACK;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (!effector.isPlayer())
		{
			return;
		}
		
		final PlayerInstance attacker = effector.getActingPlayer();
		
		final int charge = Math.min(_chargeConsume, attacker.getCharges());
		
		if (!attacker.decreaseCharges(charge))
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
			sm.addSkillName(skill);
			attacker.sendPacket(sm);
			return;
		}
		
		if (_overHit && effected.isAttackable())
		{
			((Attackable) effected).overhitEnabled(true);
		}
		
		double defence = effected.getPDef() * _pDefMod;
		
		if (!_ignoreShieldDefence)
		{
			final byte shield = Formulas.calcShldUse(attacker, effected);
			switch (shield)
			{
				case Formulas.SHIELD_DEFENSE_SUCCEED:
				{
					defence += effected.getShldDef();
					break;
				}
				case Formulas.SHIELD_DEFENSE_PERFECT_BLOCK:
				{
					defence = -1;
					break;
				}
			}
		}
		
		double damage = 1;
		final boolean critical = Formulas.calcCrit(_criticalChance, attacker, effected, skill);
		
		if (defence != -1)
		{
			// Trait, elements
			final double weaponTraitMod = Formulas.calcWeaponTraitBonus(attacker, effected);
			final double generalTraitMod = Formulas.calcGeneralTraitBonus(attacker, effected, skill.getTraitType(), true);
			final double weaknessMod = Formulas.calcWeaknessBonus(attacker, effected, skill.getTraitType());
			final double attributeMod = Formulas.calcAttributeBonus(attacker, effected, skill);
			final double pvpPveMod = Formulas.calculatePvpPveBonus(attacker, effected, skill, true);
			
			// Skill specific mods.
			final double energyChargesBoost = 1 + (charge * 0.1); // 10% bonus damage for each charge used.
			final double critMod = critical ? Formulas.calcCritDamage(attacker, effected, skill) : 1;
			double ssmod = 1;
			if (skill.useSoulShot())
			{
				if (attacker.isChargedShot(ShotType.SOULSHOTS))
				{
					ssmod = 2 * attacker.getStat().getValue(Stats.SHOTS_BONUS); // 2.04 for dual weapon?
				}
				else if (attacker.isChargedShot(ShotType.BLESSED_SOULSHOTS))
				{
					ssmod = 4 * attacker.getStat().getValue(Stats.SHOTS_BONUS);
				}
			}
			
			// ...................________Initial Damage_________...__Charges Additional Damage__...____________________________________
			// ATTACK CALCULATION ((77 * ((pAtk * lvlMod) + power) * (1 + (0.1 * chargesConsumed)) / pdef) * skillPower) + skillPowerAdd
			// ```````````````````^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^^^^^^^^^^^^^^^^^^```^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			final double baseMod = (77 * ((attacker.getPAtk() * attacker.getLevelMod()) + _power + effector.getStat().getValue(Stats.SKILL_POWER_ADD, 0))) / defence;
			damage = baseMod * ssmod * critMod * weaponTraitMod * generalTraitMod * weaknessMod * attributeMod * energyChargesBoost * pvpPveMod;
		}
		
		damage = Math.max(0, damage * effector.getStat().getValue(Stats.PHYSICAL_SKILL_POWER, 1));
		
		effector.doAttack(damage, effected, skill, false, false, critical, false);
	}
}