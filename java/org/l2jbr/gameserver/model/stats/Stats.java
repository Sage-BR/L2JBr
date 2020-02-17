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
package org.l2jbr.gameserver.model.stats;

import java.util.NoSuchElementException;
import java.util.OptionalDouble;
import java.util.function.DoubleBinaryOperator;
import java.util.logging.Logger;

import org.l2jbr.gameserver.enums.AttributeType;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.stats.finalizers.AttributeFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.BaseStatsFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.MAccuracyFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.MAttackFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.MAttackSpeedFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.MCritRateFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.MDefenseFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.MEvasionRateFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.MaxCpFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.MaxHpFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.MaxMpFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.PAccuracyFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.PAttackFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.PAttackSpeedFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.PCriticalRateFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.PDefenseFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.PEvasionRateFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.PRangeFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.RandomDamageFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.RegenCPFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.RegenHPFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.RegenMPFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.ShieldDefenceFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.ShieldDefenceRateFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.ShotsBonusFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.SpeedFinalizer;
import org.l2jbr.gameserver.model.stats.finalizers.VampiricChanceFinalizer;
import org.l2jbr.gameserver.util.MathUtil;

/**
 * Enum of basic stats.
 * @author mkizub, UnAfraid, NosBit, Sdw
 */
public enum Stats
{
	// HP, MP & CP
	MAX_HP("maxHp", new MaxHpFinalizer()),
	MAX_MP("maxMp", new MaxMpFinalizer()),
	MAX_CP("maxCp", new MaxCpFinalizer()),
	MAX_RECOVERABLE_HP("maxRecoverableHp"), // The maximum HP that is able to be recovered trough heals
	MAX_RECOVERABLE_MP("maxRecoverableMp"),
	MAX_RECOVERABLE_CP("maxRecoverableCp"),
	REGENERATE_HP_RATE("regHp", new RegenHPFinalizer()),
	REGENERATE_CP_RATE("regCp", new RegenCPFinalizer()),
	REGENERATE_MP_RATE("regMp", new RegenMPFinalizer()),
	ADDITIONAL_POTION_HP("addPotionHp"),
	ADDITIONAL_POTION_MP("addPotionMp"),
	ADDITIONAL_POTION_CP("addPotionCp"),
	MANA_CHARGE("manaCharge"),
	HEAL_EFFECT("healEffect"),
	HEAL_EFFECT_ADD("healEffectAdd"),
	
	// ATTACK & DEFENCE
	PHYSICAL_DEFENCE("pDef", new PDefenseFinalizer()),
	MAGICAL_DEFENCE("mDef", new MDefenseFinalizer()),
	PHYSICAL_ATTACK("pAtk", new PAttackFinalizer()),
	MAGIC_ATTACK("mAtk", new MAttackFinalizer()),
	PHYSICAL_ATTACK_SPEED("pAtkSpd", new PAttackSpeedFinalizer()),
	MAGIC_ATTACK_SPEED("mAtkSpd", new MAttackSpeedFinalizer()), // Magic Skill Casting Time Rate
	ATK_REUSE("atkReuse"), // Bows Hits Reuse Rate
	SHIELD_DEFENCE("sDef", new ShieldDefenceFinalizer()),
	CRITICAL_DAMAGE("cAtk"),
	CRITICAL_DAMAGE_ADD("cAtkAdd"), // this is another type for special critical damage mods - vicious stance, critical power and critical damage SA
	HATE_ATTACK("attackHate"),
	REAR_DAMAGE_RATE("rearDamage"),
	
	// PVP BONUS
	PVP_PHYSICAL_ATTACK_DAMAGE("pvpPhysDmg"),
	PVP_MAGICAL_SKILL_DAMAGE("pvpMagicalDmg"),
	PVP_PHYSICAL_SKILL_DAMAGE("pvpPhysSkillsDmg"),
	PVP_PHYSICAL_ATTACK_DEFENCE("pvpPhysDef"),
	PVP_MAGICAL_SKILL_DEFENCE("pvpMagicalDef"),
	PVP_PHYSICAL_SKILL_DEFENCE("pvpPhysSkillsDef"),
	
	// PVE BONUS
	PVE_PHYSICAL_ATTACK_DAMAGE("pvePhysDmg"),
	PVE_PHYSICAL_SKILL_DAMAGE("pvePhysSkillsDmg"),
	PVE_MAGICAL_SKILL_DAMAGE("pveMagicalDmg"),
	PVE_PHYSICAL_ATTACK_DEFENCE("pvePhysDef"),
	PVE_PHYSICAL_SKILL_DEFENCE("pvePhysSkillsDef"),
	PVE_MAGICAL_SKILL_DEFENCE("pveMagicalDef"),
	PVE_RAID_PHYSICAL_ATTACK_DEFENCE("pveRaidPhysDef"),
	PVE_RAID_PHYSICAL_SKILL_DEFENCE("pveRaidPhysSkillsDef"),
	PVE_RAID_MAGICAL_SKILL_DEFENCE("pveRaidMagicalDef"),
	
	// FIXED BONUS
	PVP_DAMAGE_TAKEN("pvpDamageTaken"),
	PVE_DAMAGE_TAKEN("pveDamageTaken"),
	
	// ATTACK & DEFENCE RATES
	MAGIC_CRITICAL_DAMAGE("mCritPower"),
	SKILL_POWER_ADD("skillPowerAdd"),
	PHYSICAL_SKILL_POWER("physicalSkillPower"),
	MAGICAL_SKILL_POWER("magicalSkillPower"),
	CRITICAL_DAMAGE_SKILL("cAtkSkill"),
	CRITICAL_DAMAGE_SKILL_ADD("cAtkSkillAdd"),
	MAGIC_CRITICAL_DAMAGE_ADD("mCritPowerAdd"),
	SHIELD_DEFENCE_RATE("rShld", new ShieldDefenceRateFinalizer()),
	CRITICAL_RATE("rCrit", new PCriticalRateFinalizer(), MathUtil::add, MathUtil::add, 0, 1),
	CRITICAL_RATE_SKILL("rCritSkill", Stats::defaultValue, MathUtil::add, MathUtil::add, 0, 1),
	MAGIC_CRITICAL_RATE("mCritRate", new MCritRateFinalizer()),
	BLOW_RATE("blowRate"),
	DEFENCE_CRITICAL_RATE("defCritRate"),
	DEFENCE_CRITICAL_RATE_ADD("defCritRateAdd"),
	DEFENCE_MAGIC_CRITICAL_RATE("defMCritRate"),
	DEFENCE_MAGIC_CRITICAL_RATE_ADD("defMCritRateAdd"),
	DEFENCE_CRITICAL_DAMAGE("defCritDamage"),
	DEFENCE_MAGIC_CRITICAL_DAMAGE("defMCritDamage"),
	DEFENCE_MAGIC_CRITICAL_DAMAGE_ADD("defMCritDamageAdd"),
	DEFENCE_CRITICAL_DAMAGE_ADD("defCritDamageAdd"), // Resistance to critical damage in value (Example: +100 will be 100 more critical damage, NOT 100% more).
	DEFENCE_CRITICAL_DAMAGE_SKILL("defCAtkSkill"),
	DEFENCE_CRITICAL_DAMAGE_SKILL_ADD("defCAtkSkillAdd"),
	INSTANT_KILL_RESIST("instantKillResist"),
	EXPSP_RATE("rExp"),
	BONUS_EXP("bonusExp"),
	BONUS_SP("bonusSp"),
	BONUS_DROP_AMOUNT("bonusDropAmount"),
	BONUS_DROP_RATE("bonusDropRate"),
	BONUS_SPOIL_RATE("bonusSpoilRate"),
	ATTACK_CANCEL("cancel"),
	
	// ACCURACY & RANGE
	ACCURACY_COMBAT("accCombat", new PAccuracyFinalizer()),
	ACCURACY_MAGIC("accMagic", new MAccuracyFinalizer()),
	EVASION_RATE("rEvas", new PEvasionRateFinalizer()),
	MAGIC_EVASION_RATE("mEvas", new MEvasionRateFinalizer()),
	PHYSICAL_ATTACK_RANGE("pAtkRange", new PRangeFinalizer()),
	MAGIC_ATTACK_RANGE("mAtkRange"),
	ATTACK_COUNT_MAX("atkCountMax"),
	PHYSICAL_POLEARM_TARGET_SINGLE("polearmSingleTarget"),
	HIT_AT_NIGHT("hitAtNight"),
	
	// Run speed, walk & escape speed are calculated proportionally, magic speed is a buff
	MOVE_SPEED("moveSpeed"),
	RUN_SPEED("runSpd", new SpeedFinalizer()),
	WALK_SPEED("walkSpd", new SpeedFinalizer()),
	SWIM_RUN_SPEED("fastSwimSpd", new SpeedFinalizer()),
	SWIM_WALK_SPEED("slowSimSpd", new SpeedFinalizer()),
	FLY_RUN_SPEED("fastFlySpd", new SpeedFinalizer()),
	FLY_WALK_SPEED("slowFlySpd", new SpeedFinalizer()),
	
	// BASIC STATS
	STAT_STR("STR", new BaseStatsFinalizer()),
	STAT_CON("CON", new BaseStatsFinalizer()),
	STAT_DEX("DEX", new BaseStatsFinalizer()),
	STAT_INT("INT", new BaseStatsFinalizer()),
	STAT_WIT("WIT", new BaseStatsFinalizer()),
	STAT_MEN("MEN", new BaseStatsFinalizer()),
	STAT_LUC("LUC", new BaseStatsFinalizer()),
	STAT_CHA("CHA", new BaseStatsFinalizer()),
	
	// Special stats, share one slot in Calculator
	
	// VARIOUS
	BREATH("breath"),
	FALL("fall"),
	FISHING_EXP_SP_BONUS("fishingExpSpBonus"),
	
	// VULNERABILITIES
	DAMAGE_ZONE_VULN("damageZoneVuln"),
	RESIST_DISPEL_BUFF("cancelVuln"), // Resistance for cancel type skills
	RESIST_ABNORMAL_DEBUFF("debuffVuln"),
	
	// RESISTANCES
	FIRE_RES("fireRes", new AttributeFinalizer(AttributeType.FIRE, false)),
	WIND_RES("windRes", new AttributeFinalizer(AttributeType.WIND, false)),
	WATER_RES("waterRes", new AttributeFinalizer(AttributeType.WATER, false)),
	EARTH_RES("earthRes", new AttributeFinalizer(AttributeType.EARTH, false)),
	HOLY_RES("holyRes", new AttributeFinalizer(AttributeType.HOLY, false)),
	DARK_RES("darkRes", new AttributeFinalizer(AttributeType.DARK, false)),
	BASE_ATTRIBUTE_RES("baseAttrRes"),
	MAGIC_SUCCESS_RES("magicSuccRes"),
	// BUFF_IMMUNITY("buffImmunity"), //TODO: Implement me
	ABNORMAL_RESIST_PHYSICAL("abnormalResPhysical"),
	ABNORMAL_RESIST_MAGICAL("abnormalResMagical"),
	
	// ELEMENT POWER
	FIRE_POWER("firePower", new AttributeFinalizer(AttributeType.FIRE, true)),
	WATER_POWER("waterPower", new AttributeFinalizer(AttributeType.WATER, true)),
	WIND_POWER("windPower", new AttributeFinalizer(AttributeType.WIND, true)),
	EARTH_POWER("earthPower", new AttributeFinalizer(AttributeType.EARTH, true)),
	HOLY_POWER("holyPower", new AttributeFinalizer(AttributeType.HOLY, true)),
	DARK_POWER("darkPower", new AttributeFinalizer(AttributeType.DARK, true)),
	
	// PROFICIENCY
	REFLECT_DAMAGE_PERCENT("reflectDam"),
	REFLECT_DAMAGE_PERCENT_DEFENSE("reflectDamDef"),
	REFLECT_SKILL_MAGIC("reflectSkillMagic"), // Need rework
	REFLECT_SKILL_PHYSIC("reflectSkillPhysic"), // Need rework
	VENGEANCE_SKILL_MAGIC_DAMAGE("vengeanceMdam"),
	VENGEANCE_SKILL_PHYSICAL_DAMAGE("vengeancePdam"),
	ABSORB_DAMAGE_PERCENT("absorbDam"),
	ABSORB_DAMAGE_CHANCE("absorbDamChance", new VampiricChanceFinalizer()),
	ABSORB_DAMAGE_DEFENCE("absorbDamDefence"),
	TRANSFER_DAMAGE_SUMMON_PERCENT("transDam"),
	MANA_SHIELD_PERCENT("manaShield"),
	TRANSFER_DAMAGE_TO_PLAYER("transDamToPlayer"),
	ABSORB_MANA_DAMAGE_PERCENT("absorbDamMana"),
	
	WEIGHT_LIMIT("weightLimit"),
	WEIGHT_PENALTY("weightPenalty"),
	
	// ExSkill
	INVENTORY_NORMAL("inventoryLimit"),
	STORAGE_PRIVATE("whLimit"),
	TRADE_SELL("PrivateSellLimit"),
	TRADE_BUY("PrivateBuyLimit"),
	RECIPE_DWARVEN("DwarfRecipeLimit"),
	RECIPE_COMMON("CommonRecipeLimit"),
	
	// Skill mastery
	SKILL_CRITICAL("skillCritical"),
	SKILL_CRITICAL_PROBABILITY("skillCriticalProbability"),
	
	// Vitality
	VITALITY_CONSUME_RATE("vitalityConsumeRate"),
	VITALITY_EXP_RATE("vitalityExpRate"),
	
	// Souls
	MAX_SOULS("maxSouls"),
	
	REDUCE_EXP_LOST_BY_PVP("reduceExpLostByPvp"),
	REDUCE_EXP_LOST_BY_MOB("reduceExpLostByMob"),
	REDUCE_EXP_LOST_BY_RAID("reduceExpLostByRaid"),
	
	REDUCE_DEATH_PENALTY_BY_PVP("reduceDeathPenaltyByPvp"),
	REDUCE_DEATH_PENALTY_BY_MOB("reduceDeathPenaltyByMob"),
	REDUCE_DEATH_PENALTY_BY_RAID("reduceDeathPenaltyByRaid"),
	
	// Brooches
	BROOCH_JEWELS("broochJewels"),
	
	// Agathions
	AGATHION_SLOTS("agathionSlots"),
	
	// Artifacts
	ARTIFACT_SLOTS("artifactSlots"),
	
	// Summon Points
	MAX_SUMMON_POINTS("summonPoints"),
	
	// Cubic Count
	MAX_CUBIC("cubicCount"),
	
	// The maximum allowed range to be damaged/debuffed from.
	SPHERIC_BARRIER_RANGE("sphericBarrier"),
	
	// Blocks given amount of debuffs.
	DEBUFF_BLOCK("debuffBlock"),
	
	// Affects the random weapon damage.
	RANDOM_DAMAGE("randomDamage", new RandomDamageFinalizer()),
	
	// Affects the random weapon damage.
	DAMAGE_LIMIT("damageCap"),
	
	// Maximun momentum one can charge
	MAX_MOMENTUM("maxMomentum"),
	
	// Which base stat ordinal should alter skill critical formula.
	STAT_BONUS_SKILL_CRITICAL("statSkillCritical"),
	STAT_BONUS_SPEED("statSpeed"),
	CRAFTING_CRITICAL("craftingCritical"),
	SHOTS_BONUS("shotBonus", new ShotsBonusFinalizer()),
	WORLD_CHAT_POINTS("worldChatPoints"),
	ATTACK_DAMAGE("attackDamage");
	
	static final Logger LOGGER = Logger.getLogger(Stats.class.getName());
	public static final int NUM_STATS = values().length;
	
	private final String _value;
	private final IStatsFunction _valueFinalizer;
	private final DoubleBinaryOperator _addFunction;
	private final DoubleBinaryOperator _mulFunction;
	private final double _resetAddValue;
	private final double _resetMulValue;
	
	public String getValue()
	{
		return _value;
	}
	
	Stats(String xmlString)
	{
		this(xmlString, Stats::defaultValue, MathUtil::add, MathUtil::mul, 0, 1);
	}
	
	Stats(String xmlString, IStatsFunction valueFinalizer)
	{
		this(xmlString, valueFinalizer, MathUtil::add, MathUtil::mul, 0, 1);
		
	}
	
	Stats(String xmlString, IStatsFunction valueFinalizer, DoubleBinaryOperator addFunction, DoubleBinaryOperator mulFunction, double resetAddValue, double resetMulValue)
	{
		_value = xmlString;
		_valueFinalizer = valueFinalizer;
		_addFunction = addFunction;
		_mulFunction = mulFunction;
		_resetAddValue = resetAddValue;
		_resetMulValue = resetMulValue;
	}
	
	public static Stats valueOfXml(String name)
	{
		name = name.intern();
		for (Stats s : values())
		{
			if (s.getValue().equals(name))
			{
				return s;
			}
		}
		
		throw new NoSuchElementException("Unknown name '" + name + "' for enum " + Stats.class.getSimpleName());
	}
	
	/**
	 * @param creature
	 * @param baseValue
	 * @return the final value
	 */
	public double finalize(Creature creature, OptionalDouble baseValue)
	{
		try
		{
			return _valueFinalizer.calc(creature, baseValue, this);
		}
		catch (Exception e)
		{
			// LOGGER.log(Level.WARNING, "Exception during finalization for : " + creature + " stat: " + toString() + " : ", e);
			return defaultValue(creature, baseValue, this);
		}
	}
	
	public double functionAdd(double oldValue, double value)
	{
		return _addFunction.applyAsDouble(oldValue, value);
	}
	
	public double functionMul(double oldValue, double value)
	{
		return _mulFunction.applyAsDouble(oldValue, value);
	}
	
	public double getResetAddValue()
	{
		return _resetAddValue;
	}
	
	public double getResetMulValue()
	{
		return _resetMulValue;
	}
	
	public static double weaponBaseValue(Creature creature, Stats stat)
	{
		return stat._valueFinalizer.calcWeaponBaseValue(creature, stat);
	}
	
	public static double defaultValue(Creature creature, OptionalDouble base, Stats stat)
	{
		final double mul = creature.getStat().getMul(stat);
		final double add = creature.getStat().getAdd(stat);
		return base.isPresent() ? defaultValue(creature, stat, base.getAsDouble()) : mul * (add + creature.getStat().getMoveTypeValue(stat, creature.getMoveType()));
	}
	
	public static double defaultValue(Creature creature, Stats stat, double baseValue)
	{
		final double mul = creature.getStat().getMul(stat);
		final double add = creature.getStat().getAdd(stat);
		return (mul * baseValue) + add + creature.getStat().getMoveTypeValue(stat, creature.getMoveType());
	}
}
