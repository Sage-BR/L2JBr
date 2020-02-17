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
package org.l2jbr.gameserver.model.actor.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.data.xml.impl.NpcData;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.enums.AISkillScope;
import org.l2jbr.gameserver.enums.AIType;
import org.l2jbr.gameserver.enums.DropType;
import org.l2jbr.gameserver.enums.MpRewardAffectType;
import org.l2jbr.gameserver.enums.MpRewardType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.enums.Sex;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.holders.DropHolder;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.interfaces.IIdentifiable;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.util.Util;

/**
 * NPC template.
 * @author NosBit
 */
public class NpcTemplate extends CreatureTemplate implements IIdentifiable
{
	private static final Logger LOGGER = Logger.getLogger(NpcTemplate.class.getName());
	
	private int _id;
	private int _displayId;
	private byte _level;
	private String _type;
	private String _name;
	private boolean _usingServerSideName;
	private String _title;
	private boolean _usingServerSideTitle;
	private StatsSet _parameters;
	private Sex _sex;
	private int _chestId;
	private int _rhandId;
	private int _lhandId;
	private int _weaponEnchant;
	private double _exp;
	private double _sp;
	private double _raidPoints;
	private boolean _unique;
	private boolean _attackable;
	private boolean _targetable;
	private boolean _talkable;
	private boolean _isQuestMonster;
	private boolean _undying;
	private boolean _showName;
	private boolean _randomWalk;
	private boolean _randomAnimation;
	private boolean _flying;
	private boolean _fakePlayer;
	private boolean _fakePlayerTalkable;
	private boolean _canMove;
	private boolean _noSleepMode;
	private boolean _passableDoor;
	private boolean _hasSummoner;
	private boolean _canBeSown;
	private boolean _canBeCrt;
	private boolean _isDeathPenalty;
	private int _corpseTime;
	private AIType _aiType;
	private int _aggroRange;
	private int _clanHelpRange;
	private int _dodge;
	private boolean _isChaos;
	private boolean _isAggressive;
	private int _soulShot;
	private int _spiritShot;
	private int _soulShotChance;
	private int _spiritShotChance;
	private int _minSkillChance;
	private int _maxSkillChance;
	private double _hitTimeFactor;
	private double _hitTimeFactorSkill;
	private Map<Integer, Skill> _skills;
	private Map<AISkillScope, List<Skill>> _aiSkillLists;
	private Set<Integer> _clans;
	private Set<Integer> _ignoreClanNpcIds;
	private CopyOnWriteArrayList<DropHolder> _dropListDeath;
	private CopyOnWriteArrayList<DropHolder> _dropListSpoil;
	private double _collisionRadiusGrown;
	private double _collisionHeightGrown;
	private int _mpRewardValue;
	private MpRewardType _mpRewardType;
	private int _mpRewardTicks;
	private MpRewardAffectType _mpRewardAffectType;
	
	private List<Integer> _extendDrop;
	
	/**
	 * Constructor of Creature.
	 * @param set The StatsSet object to transfer data to the method
	 */
	public NpcTemplate(StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void set(StatsSet set)
	{
		super.set(set);
		_id = set.getInt("id");
		_displayId = set.getInt("displayId", _id);
		_level = set.getByte("level", (byte) 70);
		_type = set.getString("type", "Npc");
		_name = set.getString("name", "");
		_usingServerSideName = set.getBoolean("usingServerSideName", false);
		_title = set.getString("title", "");
		_usingServerSideTitle = set.getBoolean("usingServerSideTitle", false);
		setRace(set.getEnum("race", Race.class, Race.NONE));
		_sex = set.getEnum("sex", Sex.class, Sex.ETC);
		
		_chestId = set.getInt("chestId", 0);
		if ((_chestId > 0) && (ItemTable.getInstance().getTemplate(_chestId) == null))
		{
			LOGGER.warning("NpcTemplate " + _id + ": Could not find item for chestId with id " + _chestId + ".");
		}
		_rhandId = set.getInt("rhandId", 0);
		if ((_rhandId > 0) && (ItemTable.getInstance().getTemplate(_rhandId) == null))
		{
			LOGGER.warning("NpcTemplate " + _id + ": Could not find item for rhandId with id " + _rhandId + ".");
		}
		_lhandId = set.getInt("lhandId", 0);
		if ((_lhandId > 0) && (ItemTable.getInstance().getTemplate(_lhandId) == null))
		{
			LOGGER.warning("NpcTemplate " + _id + ": Could not find item for lhandId with id " + _lhandId + ".");
		}
		
		_weaponEnchant = set.getInt("weaponEnchant", 0);
		
		_exp = set.getDouble("exp", 0);
		_sp = set.getDouble("sp", 0);
		_raidPoints = set.getDouble("raidPoints", 0);
		
		_unique = set.getBoolean("unique", false);
		_attackable = set.getBoolean("attackable", true);
		_targetable = set.getBoolean("targetable", true);
		_talkable = set.getBoolean("talkable", true);
		_isQuestMonster = _title.contains("Quest");
		_undying = set.getBoolean("undying", true);
		_showName = set.getBoolean("showName", true);
		_randomWalk = set.getBoolean("randomWalk", !_type.equals("Guard"));
		_randomAnimation = set.getBoolean("randomAnimation", true);
		_flying = set.getBoolean("flying", false);
		_fakePlayer = set.getBoolean("fakePlayer", false);
		_fakePlayerTalkable = set.getBoolean("fakePlayerTalkable", true);
		_canMove = set.getBoolean("canMove", true);
		_noSleepMode = set.getBoolean("noSleepMode", false);
		_passableDoor = set.getBoolean("passableDoor", false);
		_hasSummoner = set.getBoolean("hasSummoner", false);
		_canBeSown = set.getBoolean("canBeSown", false);
		_canBeCrt = set.getBoolean("exCrtEffect", true);
		_isDeathPenalty = set.getBoolean("isDeathPenalty", false);
		
		_corpseTime = set.getInt("corpseTime", Config.DEFAULT_CORPSE_TIME);
		
		_aiType = set.getEnum("aiType", AIType.class, AIType.FIGHTER);
		_aggroRange = set.getInt("aggroRange", 0);
		_clanHelpRange = set.getInt("clanHelpRange", 0);
		_dodge = set.getInt("dodge", 0);
		_isChaos = set.getBoolean("isChaos", false);
		_isAggressive = set.getBoolean("isAggressive", false);
		
		_soulShot = set.getInt("soulShot", 0);
		_spiritShot = set.getInt("spiritShot", 0);
		_soulShotChance = set.getInt("shotShotChance", 0);
		_spiritShotChance = set.getInt("spiritShotChance", 0);
		
		_minSkillChance = set.getInt("minSkillChance", 7);
		_maxSkillChance = set.getInt("maxSkillChance", 15);
		
		_hitTimeFactor = set.getInt("hitTime", 100) / 100d;
		_hitTimeFactorSkill = set.getInt("hitTimeSkill", 100) / 100d;
		
		_collisionRadiusGrown = set.getDouble("collisionRadiusGrown", 0);
		_collisionHeightGrown = set.getDouble("collisionHeightGrown", 0);
		
		_mpRewardValue = set.getInt("mpRewardValue", 0);
		_mpRewardType = set.getEnum("mpRewardType", MpRewardType.class, MpRewardType.DIFF);
		_mpRewardTicks = set.getInt("mpRewardTicks", 0);
		_mpRewardAffectType = set.getEnum("mpRewardAffectType", MpRewardAffectType.class, MpRewardAffectType.SOLO);
		
		_extendDrop = set.getList("extendDrop", Integer.class);
		
		if (Config.ENABLE_NPC_STAT_MULTIPLIERS) // Custom NPC Stat Multipliers
		{
			switch (_type)
			{
				case "Monster":
				{
					_baseValues.put(Stats.MAX_HP, getBaseHpMax() * Config.MONSTER_HP_MULTIPLIER);
					_baseValues.put(Stats.MAX_MP, getBaseMpMax() * Config.MONSTER_MP_MULTIPLIER);
					_baseValues.put(Stats.PHYSICAL_ATTACK, getBasePAtk() * Config.MONSTER_PATK_MULTIPLIER);
					_baseValues.put(Stats.MAGIC_ATTACK, getBaseMAtk() * Config.MONSTER_MATK_MULTIPLIER);
					_baseValues.put(Stats.PHYSICAL_DEFENCE, getBasePDef() * Config.MONSTER_PDEF_MULTIPLIER);
					_baseValues.put(Stats.MAGICAL_DEFENCE, getBaseMDef() * Config.MONSTER_MDEF_MULTIPLIER);
					_aggroRange *= Config.MONSTER_AGRRO_RANGE_MULTIPLIER;
					_clanHelpRange *= Config.MONSTER_CLAN_HELP_RANGE_MULTIPLIER;
					break;
				}
				case "RaidBoss":
				case "GrandBoss":
				{
					_baseValues.put(Stats.MAX_HP, getBaseHpMax() * Config.RAIDBOSS_HP_MULTIPLIER);
					_baseValues.put(Stats.MAX_MP, getBaseMpMax() * Config.RAIDBOSS_MP_MULTIPLIER);
					_baseValues.put(Stats.PHYSICAL_ATTACK, getBasePAtk() * Config.RAIDBOSS_PATK_MULTIPLIER);
					_baseValues.put(Stats.MAGIC_ATTACK, getBaseMAtk() * Config.RAIDBOSS_MATK_MULTIPLIER);
					_baseValues.put(Stats.PHYSICAL_DEFENCE, getBasePDef() * Config.RAIDBOSS_PDEF_MULTIPLIER);
					_baseValues.put(Stats.MAGICAL_DEFENCE, getBaseMDef() * Config.RAIDBOSS_MDEF_MULTIPLIER);
					_aggroRange *= Config.RAIDBOSS_AGRRO_RANGE_MULTIPLIER;
					_clanHelpRange *= Config.RAIDBOSS_CLAN_HELP_RANGE_MULTIPLIER;
					break;
				}
				case "Guard":
				{
					_baseValues.put(Stats.MAX_HP, getBaseHpMax() * Config.GUARD_HP_MULTIPLIER);
					_baseValues.put(Stats.MAX_MP, getBaseMpMax() * Config.GUARD_MP_MULTIPLIER);
					_baseValues.put(Stats.PHYSICAL_ATTACK, getBasePAtk() * Config.GUARD_PATK_MULTIPLIER);
					_baseValues.put(Stats.MAGIC_ATTACK, getBaseMAtk() * Config.GUARD_MATK_MULTIPLIER);
					_baseValues.put(Stats.PHYSICAL_DEFENCE, getBasePDef() * Config.GUARD_PDEF_MULTIPLIER);
					_baseValues.put(Stats.MAGICAL_DEFENCE, getBaseMDef() * Config.GUARD_MDEF_MULTIPLIER);
					_aggroRange *= Config.GUARD_AGRRO_RANGE_MULTIPLIER;
					_clanHelpRange *= Config.GUARD_CLAN_HELP_RANGE_MULTIPLIER;
					break;
				}
				case "Defender":
				{
					_baseValues.put(Stats.MAX_HP, getBaseHpMax() * Config.DEFENDER_HP_MULTIPLIER);
					_baseValues.put(Stats.MAX_MP, getBaseMpMax() * Config.DEFENDER_MP_MULTIPLIER);
					_baseValues.put(Stats.PHYSICAL_ATTACK, getBasePAtk() * Config.DEFENDER_PATK_MULTIPLIER);
					_baseValues.put(Stats.MAGIC_ATTACK, getBaseMAtk() * Config.DEFENDER_MATK_MULTIPLIER);
					_baseValues.put(Stats.PHYSICAL_DEFENCE, getBasePDef() * Config.DEFENDER_PDEF_MULTIPLIER);
					_baseValues.put(Stats.MAGICAL_DEFENCE, getBaseMDef() * Config.DEFENDER_MDEF_MULTIPLIER);
					_aggroRange *= Config.DEFENDER_AGRRO_RANGE_MULTIPLIER;
					_clanHelpRange *= Config.DEFENDER_CLAN_HELP_RANGE_MULTIPLIER;
					break;
				}
			}
		}
	}
	
	@Override
	public int getId()
	{
		return _id;
	}
	
	public int getDisplayId()
	{
		return _displayId;
	}
	
	public byte getLevel()
	{
		return _level;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public boolean isType(String type)
	{
		return _type.equalsIgnoreCase(type);
	}
	
	public String getName()
	{
		return _name;
	}
	
	public boolean isUsingServerSideName()
	{
		return _usingServerSideName;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public boolean isUsingServerSideTitle()
	{
		return _usingServerSideTitle;
	}
	
	public StatsSet getParameters()
	{
		return _parameters;
	}
	
	public void setParameters(StatsSet set)
	{
		_parameters = set;
	}
	
	public Sex getSex()
	{
		return _sex;
	}
	
	public int getChestId()
	{
		return _chestId;
	}
	
	public int getRHandId()
	{
		return _rhandId;
	}
	
	public int getLHandId()
	{
		return _lhandId;
	}
	
	public int getWeaponEnchant()
	{
		return _weaponEnchant;
	}
	
	public double getExp()
	{
		return _exp;
	}
	
	public double getSP()
	{
		return _sp;
	}
	
	public double getRaidPoints()
	{
		return _raidPoints;
	}
	
	public boolean isUnique()
	{
		return _unique;
	}
	
	public boolean isAttackable()
	{
		return _attackable;
	}
	
	public boolean isTargetable()
	{
		return _targetable;
	}
	
	public boolean isTalkable()
	{
		return _talkable;
	}
	
	public boolean isQuestMonster()
	{
		return _isQuestMonster;
	}
	
	public boolean isUndying()
	{
		return _undying;
	}
	
	public boolean isShowName()
	{
		return _showName;
	}
	
	public boolean isRandomWalkEnabled()
	{
		return _randomWalk;
	}
	
	public boolean isRandomAnimationEnabled()
	{
		return _randomAnimation;
	}
	
	public boolean isFlying()
	{
		return _flying;
	}
	
	public boolean isFakePlayer()
	{
		return _fakePlayer;
	}
	
	public boolean isFakePlayerTalkable()
	{
		return _fakePlayerTalkable;
	}
	
	public boolean canMove()
	{
		return _canMove;
	}
	
	public boolean isNoSleepMode()
	{
		return _noSleepMode;
	}
	
	public boolean isPassableDoor()
	{
		return _passableDoor;
	}
	
	public boolean hasSummoner()
	{
		return _hasSummoner;
	}
	
	public boolean canBeSown()
	{
		return _canBeSown;
	}
	
	public boolean canBeCrt()
	{
		return _canBeCrt;
	}
	
	public boolean isDeathPenalty()
	{
		return _isDeathPenalty;
	}
	
	public int getCorpseTime()
	{
		return _corpseTime;
	}
	
	public AIType getAIType()
	{
		return _aiType;
	}
	
	public int getAggroRange()
	{
		return _aggroRange;
	}
	
	public int getClanHelpRange()
	{
		return _clanHelpRange;
	}
	
	public int getDodge()
	{
		return _dodge;
	}
	
	public boolean isChaos()
	{
		return _isChaos;
	}
	
	public boolean isAggressive()
	{
		return _isAggressive;
	}
	
	public int getSoulShot()
	{
		return _soulShot;
	}
	
	public int getSpiritShot()
	{
		return _spiritShot;
	}
	
	public int getSoulShotChance()
	{
		return _soulShotChance;
	}
	
	public int getSpiritShotChance()
	{
		return _spiritShotChance;
	}
	
	public int getMinSkillChance()
	{
		return _minSkillChance;
	}
	
	public int getMaxSkillChance()
	{
		return _maxSkillChance;
	}
	
	public double getHitTimeFactor()
	{
		return _hitTimeFactor;
	}
	
	public double getHitTimeFactorSkill()
	{
		return _hitTimeFactorSkill;
	}
	
	@Override
	public Map<Integer, Skill> getSkills()
	{
		return _skills;
	}
	
	public void setSkills(Map<Integer, Skill> skills)
	{
		_skills = skills != null ? Collections.unmodifiableMap(skills) : Collections.emptyMap();
	}
	
	public List<Skill> getAISkills(AISkillScope aiSkillScope)
	{
		return _aiSkillLists.getOrDefault(aiSkillScope, Collections.emptyList());
	}
	
	public void setAISkillLists(Map<AISkillScope, List<Skill>> aiSkillLists)
	{
		_aiSkillLists = aiSkillLists != null ? Collections.unmodifiableMap(aiSkillLists) : Collections.emptyMap();
	}
	
	public Set<Integer> getClans()
	{
		return _clans;
	}
	
	public int getMpRewardValue()
	{
		return _mpRewardValue;
	}
	
	public MpRewardType getMpRewardType()
	{
		return _mpRewardType;
	}
	
	public int getMpRewardTicks()
	{
		return _mpRewardTicks;
	}
	
	public MpRewardAffectType getMpRewardAffectType()
	{
		return _mpRewardAffectType;
	}
	
	/**
	 * @param clans A sorted array of clan ids
	 */
	public void setClans(Set<Integer> clans)
	{
		_clans = clans != null ? Collections.unmodifiableSet(clans) : null;
	}
	
	/**
	 * @param clanName clan name to check if it belongs to this NPC template clans.
	 * @param clanNames clan names to check if they belong to this NPC template clans.
	 * @return {@code true} if at least one of the clan names belong to this NPC template clans, {@code false} otherwise.
	 */
	public boolean isClan(String clanName, String... clanNames)
	{
		// Using local variable for the sake of reloading since it can be turned to null.
		final Set<Integer> clans = _clans;
		
		if (clans == null)
		{
			return false;
		}
		
		int clanId = NpcData.getInstance().getClanId("ALL");
		if (clans.contains(clanId))
		{
			return true;
		}
		
		clanId = NpcData.getInstance().getClanId(clanName);
		if (clans.contains(clanId))
		{
			return true;
		}
		
		for (String name : clanNames)
		{
			clanId = NpcData.getInstance().getClanId(name);
			if (clans.contains(clanId))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param clans A set of clan names to check if they belong to this NPC template clans.
	 * @return {@code true} if at least one of the clan names belong to this NPC template clans, {@code false} otherwise.
	 */
	public boolean isClan(Set<Integer> clans)
	{
		// Using local variable for the sake of reloading since it can be turned to null.
		final Set<Integer> clanSet = _clans;
		
		if ((clanSet == null) || (clans == null))
		{
			return false;
		}
		
		final int clanId = NpcData.getInstance().getClanId("ALL");
		if (clanSet.contains(clanId))
		{
			return true;
		}
		
		for (Integer id : clans)
		{
			if (clanSet.contains(id))
			{
				return true;
			}
		}
		return false;
	}
	
	public Set<Integer> getIgnoreClanNpcIds()
	{
		return _ignoreClanNpcIds;
	}
	
	/**
	 * @param ignoreClanNpcIds the ignore clan npc ids
	 */
	public void setIgnoreClanNpcIds(Set<Integer> ignoreClanNpcIds)
	{
		_ignoreClanNpcIds = ignoreClanNpcIds != null ? Collections.unmodifiableSet(ignoreClanNpcIds) : null;
	}
	
	public void addDrop(DropHolder dropHolder)
	{
		if (_dropListDeath == null)
		{
			_dropListDeath = new CopyOnWriteArrayList<>();
		}
		_dropListDeath.add(dropHolder);
	}
	
	public void addSpoil(DropHolder dropHolder)
	{
		if (_dropListSpoil == null)
		{
			_dropListSpoil = new CopyOnWriteArrayList<>();
		}
		_dropListSpoil.add(dropHolder);
	}
	
	public List<DropHolder> getDropList(DropType dropType)
	{
		switch (dropType)
		{
			case DROP:
			case LUCKY: // never happens
			{
				return _dropListDeath;
			}
			case SPOIL:
			{
				return _dropListSpoil;
			}
		}
		return null;
	}
	
	public Collection<ItemHolder> calculateDrops(DropType dropType, Creature victim, Creature killer)
	{
		if (getDropList(dropType) == null)
		{
			return null;
		}
		
		final List<DropHolder> dropList = new ArrayList<>(getDropList(dropType));
		
		// randomize drop order
		Collections.shuffle(dropList);
		
		final int levelDifference = victim.getLevel() - killer.getLevel();
		int dropOccurrenceCounter = victim.isRaid() ? Config.DROP_MAX_OCCURRENCES_RAIDBOSS : Config.DROP_MAX_OCCURRENCES_NORMAL;
		Collection<ItemHolder> calculatedDrops = null;
		for (DropHolder dropItem : dropList)
		{
			// check if maximum drop occurrences have been reached
			// items that have 100% drop chance without server rate multipliers drop normally
			if ((dropOccurrenceCounter == 0) && (dropItem.getChance() < 100))
			{
				continue;
			}
			
			// check level gap that may prevent drop this item
			final double levelGapChanceToDrop;
			if (dropItem.getItemId() == Inventory.ADENA_ID)
			{
				levelGapChanceToDrop = Util.map(levelDifference, -Config.DROP_ADENA_MAX_LEVEL_DIFFERENCE, -Config.DROP_ADENA_MIN_LEVEL_DIFFERENCE, Config.DROP_ADENA_MIN_LEVEL_GAP_CHANCE, 100.0);
			}
			else
			{
				levelGapChanceToDrop = Util.map(levelDifference, -Config.DROP_ITEM_MAX_LEVEL_DIFFERENCE, -Config.DROP_ITEM_MIN_LEVEL_DIFFERENCE, Config.DROP_ITEM_MIN_LEVEL_GAP_CHANCE, 100.0);
			}
			if ((Rnd.nextDouble() * 100) > levelGapChanceToDrop)
			{
				continue;
			}
			
			// calculate chances
			final ItemHolder drop = calculateDrop(dropItem, victim, killer);
			if (drop == null)
			{
				continue;
			}
			
			// create list
			if (calculatedDrops == null)
			{
				calculatedDrops = new ArrayList<>();
			}
			
			// finally
			if (dropItem.getChance() < 100)
			{
				dropOccurrenceCounter--;
			}
			calculatedDrops.add(drop);
		}
		
		// champion extra drop
		if (victim.isChampion())
		{
			if ((victim.getLevel() < killer.getLevel()) && (Rnd.get(100) < Config.CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE))
			{
				return calculatedDrops;
			}
			if ((victim.getLevel() > killer.getLevel()) && (Rnd.get(100) < Config.CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE))
			{
				return calculatedDrops;
			}
			
			// create list
			if (calculatedDrops == null)
			{
				calculatedDrops = new ArrayList<>();
			}
			
			calculatedDrops.add(new ItemHolder(Config.CHAMPION_REWARD_ID, Config.CHAMPION_REWARD_QTY));
		}
		
		return calculatedDrops;
	}
	
	/**
	 * All item drop chance calculations are done by this method.
	 * @param dropItem
	 * @param victim
	 * @param killer
	 * @return ItemHolder
	 */
	private ItemHolder calculateDrop(DropHolder dropItem, Creature victim, Creature killer)
	{
		switch (dropItem.getDropType())
		{
			case DROP:
			{
				final int itemId = dropItem.getItemId();
				final Item item = ItemTable.getInstance().getTemplate(itemId);
				final boolean champion = victim.isChampion();
				
				// chance
				double rateChance = 1;
				if (Config.RATE_DROP_CHANCE_BY_ID.get(itemId) != null)
				{
					rateChance *= Config.RATE_DROP_CHANCE_BY_ID.get(itemId);
					if (champion && (itemId == Inventory.ADENA_ID))
					{
						rateChance *= Config.CHAMPION_ADENAS_REWARDS_CHANCE;
					}
				}
				else if (item.hasExImmediateEffect())
				{
					rateChance *= Config.RATE_HERB_DROP_CHANCE_MULTIPLIER;
				}
				else if (victim.isRaid())
				{
					rateChance *= Config.RATE_RAID_DROP_CHANCE_MULTIPLIER;
				}
				else
				{
					rateChance *= Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER * (champion ? Config.CHAMPION_REWARDS_CHANCE : 1);
				}
				
				// premium chance
				if (Config.PREMIUM_SYSTEM_ENABLED && (killer.getActingPlayer() != null) && killer.getActingPlayer().hasPremiumStatus())
				{
					if (Config.PREMIUM_RATE_DROP_CHANCE_BY_ID.get(itemId) != null)
					{
						rateChance *= Config.PREMIUM_RATE_DROP_CHANCE_BY_ID.get(itemId);
					}
					else if (item.hasExImmediateEffect())
					{
						// TODO: Premium herb chance? :)
					}
					else if (victim.isRaid())
					{
						// TODO: Premium raid chance? :)
					}
					else
					{
						rateChance *= Config.PREMIUM_RATE_DROP_CHANCE;
					}
				}
				
				// bonus drop rate effect
				rateChance *= killer.getStat().getValue(Stats.BONUS_DROP_RATE, 1);
				
				// calculate if item will drop
				if ((Rnd.nextDouble() * 100) < (dropItem.getChance() * rateChance))
				{
					// amount is calculated after chance returned success
					double rateAmount = 1;
					if (Config.RATE_DROP_AMOUNT_BY_ID.get(itemId) != null)
					{
						rateAmount *= Config.RATE_DROP_AMOUNT_BY_ID.get(itemId);
						if (champion && (itemId == Inventory.ADENA_ID))
						{
							rateAmount *= Config.CHAMPION_ADENAS_REWARDS_AMOUNT;
						}
					}
					else if (item.hasExImmediateEffect())
					{
						rateAmount *= Config.RATE_HERB_DROP_AMOUNT_MULTIPLIER;
					}
					else if (victim.isRaid())
					{
						rateAmount *= Config.RATE_RAID_DROP_AMOUNT_MULTIPLIER;
					}
					else
					{
						rateAmount *= Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER * (champion ? Config.CHAMPION_REWARDS_AMOUNT : 1);
					}
					
					// premium chance
					if (Config.PREMIUM_SYSTEM_ENABLED && (killer.getActingPlayer() != null) && killer.getActingPlayer().hasPremiumStatus())
					{
						if (Config.PREMIUM_RATE_DROP_AMOUNT_BY_ID.get(itemId) != null)
						{
							rateAmount *= Config.PREMIUM_RATE_DROP_AMOUNT_BY_ID.get(itemId);
						}
						else if (item.hasExImmediateEffect())
						{
							// TODO: Premium herb amount? :)
						}
						else if (victim.isRaid())
						{
							// TODO: Premium raid amount? :)
						}
						else
						{
							rateAmount *= Config.PREMIUM_RATE_DROP_AMOUNT;
						}
					}
					
					// bonus drop amount effect
					rateAmount *= killer.getStat().getValue(Stats.BONUS_DROP_AMOUNT, 1);
					
					// finally
					return new ItemHolder(itemId, (long) (Rnd.get(dropItem.getMin(), dropItem.getMax()) * rateAmount));
				}
				break;
			}
			case LUCKY:
			{
				// try chance before luck
				if (((Rnd.nextDouble() * 100) < dropItem.getChance()) && (killer.getActingPlayer() != null) && killer.getActingPlayer().tryLuck())
				{
					return new ItemHolder(dropItem.getItemId(), Rnd.get(dropItem.getMin(), dropItem.getMax()));
				}
				break;
			}
			case SPOIL:
			{
				// chance
				double rateChance = Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER;
				// premium chance
				if (Config.PREMIUM_SYSTEM_ENABLED && (killer.getActingPlayer() != null) && killer.getActingPlayer().hasPremiumStatus())
				{
					rateChance *= Config.PREMIUM_RATE_SPOIL_CHANCE;
				}
				// bonus drop rate effect
				rateChance *= killer.getStat().getValue(Stats.BONUS_SPOIL_RATE, 1);
				
				// calculate if item will be rewarded
				if ((Rnd.nextDouble() * 100) < (dropItem.getChance() * rateChance))
				{
					// amount is calculated after chance returned success
					double rateAmount = Config.RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;
					// premium amount
					if (Config.PREMIUM_SYSTEM_ENABLED && (killer.getActingPlayer() != null) && killer.getActingPlayer().hasPremiumStatus())
					{
						rateAmount *= Config.PREMIUM_RATE_SPOIL_AMOUNT;
					}
					
					// finally
					return new ItemHolder(dropItem.getItemId(), (long) (Rnd.get(dropItem.getMin(), dropItem.getMax()) * rateAmount));
				}
				break;
			}
		}
		return null;
	}
	
	public double getCollisionRadiusGrown()
	{
		return _collisionRadiusGrown;
	}
	
	public double getCollisionHeightGrown()
	{
		return _collisionHeightGrown;
	}
	
	public static boolean isAssignableTo(Class<?> sub, Class<?> clazz)
	{
		// If clazz represents an interface
		if (clazz.isInterface())
		{
			// check if obj implements the clazz interface
			for (Class<?> interface1 : sub.getInterfaces())
			{
				if (clazz.getName().equals(interface1.getName()))
				{
					return true;
				}
			}
		}
		else
		{
			do
			{
				if (sub.getName().equals(clazz.getName()))
				{
					return true;
				}
				
				sub = sub.getSuperclass();
			}
			while (sub != null);
		}
		return false;
	}
	
	/**
	 * Checks if obj can be assigned to the Class represented by clazz.<br>
	 * This is true if, and only if, obj is the same class represented by clazz, or a subclass of it or obj implements the interface represented by clazz.
	 * @param obj
	 * @param clazz
	 * @return {@code true} if the object can be assigned to the class, {@code false} otherwise
	 */
	public static boolean isAssignableTo(Object obj, Class<?> clazz)
	{
		return isAssignableTo(obj.getClass(), clazz);
	}
	
	public List<Integer> getExtendDrop()
	{
		return _extendDrop == null ? Collections.emptyList() : _extendDrop;
	}
}
