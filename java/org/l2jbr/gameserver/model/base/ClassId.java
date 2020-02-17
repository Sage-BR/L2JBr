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
package org.l2jbr.gameserver.model.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.interfaces.IIdentifiable;

/**
 * This class defines all classes (ex : human fighter, darkFighter...) that a player can chose.<br>
 * Data:
 * <ul>
 * <li>id : The Identifier of the class</li>
 * <li>isMage : True if the class is a mage class</li>
 * <li>race : The race of this class</li>
 * <li>parent : The parent ClassId or null if this class is the root</li>
 * </ul>
 * @version $Revision: 1.4.4.4 $ $Date: 2005/03/27 15:29:33 $
 */
public enum ClassId implements IIdentifiable
{
	FIGHTER(0, false, Race.HUMAN, null),
	
	WARRIOR(1, false, Race.HUMAN, FIGHTER),
	GLADIATOR(2, false, Race.HUMAN, WARRIOR),
	WARLORD(3, false, Race.HUMAN, WARRIOR),
	KNIGHT(4, false, Race.HUMAN, FIGHTER),
	PALADIN(5, false, Race.HUMAN, KNIGHT),
	DARK_AVENGER(6, false, Race.HUMAN, KNIGHT),
	ROGUE(7, false, Race.HUMAN, FIGHTER),
	TREASURE_HUNTER(8, false, Race.HUMAN, ROGUE),
	HAWKEYE(9, false, Race.HUMAN, ROGUE),
	
	MAGE(10, true, Race.HUMAN, null),
	WIZARD(11, true, Race.HUMAN, MAGE),
	SORCERER(12, true, Race.HUMAN, WIZARD),
	NECROMANCER(13, true, Race.HUMAN, WIZARD),
	WARLOCK(14, true, true, Race.HUMAN, WIZARD),
	CLERIC(15, true, Race.HUMAN, MAGE),
	BISHOP(16, true, Race.HUMAN, CLERIC),
	PROPHET(17, true, Race.HUMAN, CLERIC),
	
	ELVEN_FIGHTER(18, false, Race.ELF, null),
	ELVEN_KNIGHT(19, false, Race.ELF, ELVEN_FIGHTER),
	TEMPLE_KNIGHT(20, false, Race.ELF, ELVEN_KNIGHT),
	SWORDSINGER(21, false, Race.ELF, ELVEN_KNIGHT),
	ELVEN_SCOUT(22, false, Race.ELF, ELVEN_FIGHTER),
	PLAINS_WALKER(23, false, Race.ELF, ELVEN_SCOUT),
	SILVER_RANGER(24, false, Race.ELF, ELVEN_SCOUT),
	
	ELVEN_MAGE(25, true, Race.ELF, null),
	ELVEN_WIZARD(26, true, Race.ELF, ELVEN_MAGE),
	SPELLSINGER(27, true, Race.ELF, ELVEN_WIZARD),
	ELEMENTAL_SUMMONER(28, true, true, Race.ELF, ELVEN_WIZARD),
	ORACLE(29, true, Race.ELF, ELVEN_MAGE),
	ELDER(30, true, Race.ELF, ORACLE),
	
	DARK_FIGHTER(31, false, Race.DARK_ELF, null),
	PALUS_KNIGHT(32, false, Race.DARK_ELF, DARK_FIGHTER),
	SHILLIEN_KNIGHT(33, false, Race.DARK_ELF, PALUS_KNIGHT),
	BLADEDANCER(34, false, Race.DARK_ELF, PALUS_KNIGHT),
	ASSASSIN(35, false, Race.DARK_ELF, DARK_FIGHTER),
	ABYSS_WALKER(36, false, Race.DARK_ELF, ASSASSIN),
	PHANTOM_RANGER(37, false, Race.DARK_ELF, ASSASSIN),
	
	DARK_MAGE(38, true, Race.DARK_ELF, null),
	DARK_WIZARD(39, true, Race.DARK_ELF, DARK_MAGE),
	SPELLHOWLER(40, true, Race.DARK_ELF, DARK_WIZARD),
	PHANTOM_SUMMONER(41, true, true, Race.DARK_ELF, DARK_WIZARD),
	SHILLIEN_ORACLE(42, true, Race.DARK_ELF, DARK_MAGE),
	SHILLIEN_ELDER(43, true, Race.DARK_ELF, SHILLIEN_ORACLE),
	
	ORC_FIGHTER(44, false, Race.ORC, null),
	ORC_RAIDER(45, false, Race.ORC, ORC_FIGHTER),
	DESTROYER(46, false, Race.ORC, ORC_RAIDER),
	ORC_MONK(47, false, Race.ORC, ORC_FIGHTER),
	TYRANT(48, false, Race.ORC, ORC_MONK),
	
	ORC_MAGE(49, true, Race.ORC, null),
	ORC_SHAMAN(50, true, Race.ORC, ORC_MAGE),
	OVERLORD(51, true, Race.ORC, ORC_SHAMAN),
	WARCRYER(52, true, Race.ORC, ORC_SHAMAN),
	
	DWARVEN_FIGHTER(53, false, Race.DWARF, null),
	SCAVENGER(54, false, Race.DWARF, DWARVEN_FIGHTER),
	BOUNTY_HUNTER(55, false, Race.DWARF, SCAVENGER),
	ARTISAN(56, false, Race.DWARF, DWARVEN_FIGHTER),
	WARSMITH(57, false, Race.DWARF, ARTISAN),
	
	DUELIST(88, false, Race.HUMAN, GLADIATOR),
	DREADNOUGHT(89, false, Race.HUMAN, WARLORD),
	PHOENIX_KNIGHT(90, false, Race.HUMAN, PALADIN),
	HELL_KNIGHT(91, false, Race.HUMAN, DARK_AVENGER),
	SAGITTARIUS(92, false, Race.HUMAN, HAWKEYE),
	ADVENTURER(93, false, Race.HUMAN, TREASURE_HUNTER),
	ARCHMAGE(94, true, Race.HUMAN, SORCERER),
	SOULTAKER(95, true, Race.HUMAN, NECROMANCER),
	ARCANA_LORD(96, true, true, Race.HUMAN, WARLOCK),
	CARDINAL(97, true, Race.HUMAN, BISHOP),
	HIEROPHANT(98, true, Race.HUMAN, PROPHET),
	
	EVA_TEMPLAR(99, false, Race.ELF, TEMPLE_KNIGHT),
	SWORD_MUSE(100, false, Race.ELF, SWORDSINGER),
	WIND_RIDER(101, false, Race.ELF, PLAINS_WALKER),
	MOONLIGHT_SENTINEL(102, false, Race.ELF, SILVER_RANGER),
	MYSTIC_MUSE(103, true, Race.ELF, SPELLSINGER),
	ELEMENTAL_MASTER(104, true, true, Race.ELF, ELEMENTAL_SUMMONER),
	EVA_SAINT(105, true, Race.ELF, ELDER),
	
	SHILLIEN_TEMPLAR(106, false, Race.DARK_ELF, SHILLIEN_KNIGHT),
	SPECTRAL_DANCER(107, false, Race.DARK_ELF, BLADEDANCER),
	GHOST_HUNTER(108, false, Race.DARK_ELF, ABYSS_WALKER),
	GHOST_SENTINEL(109, false, Race.DARK_ELF, PHANTOM_RANGER),
	STORM_SCREAMER(110, true, Race.DARK_ELF, SPELLHOWLER),
	SPECTRAL_MASTER(111, true, true, Race.DARK_ELF, PHANTOM_SUMMONER),
	SHILLIEN_SAINT(112, true, Race.DARK_ELF, SHILLIEN_ELDER),
	
	TITAN(113, false, Race.ORC, DESTROYER),
	GRAND_KHAVATARI(114, false, Race.ORC, TYRANT),
	DOMINATOR(115, true, Race.ORC, OVERLORD),
	DOOMCRYER(116, true, Race.ORC, WARCRYER),
	
	FORTUNE_SEEKER(117, false, Race.DWARF, BOUNTY_HUNTER),
	MAESTRO(118, false, Race.DWARF, WARSMITH),

	MALE_SOLDIER(123, false, Race.KAMAEL, null),
	FEMALE_SOLDIER(124, false, Race.KAMAEL, null),
	TROOPER(125, false, Race.KAMAEL, MALE_SOLDIER),
	WARDER(126, false, Race.KAMAEL, FEMALE_SOLDIER),
	BERSERKER(127, false, Race.KAMAEL, TROOPER),
	MALE_SOULBREAKER(128, false, Race.KAMAEL, TROOPER),
	FEMALE_SOULBREAKER(129, false, Race.KAMAEL, WARDER),
	ARBALESTER(130, false, Race.KAMAEL, WARDER),
	DOOMBRINGER(131, false, Race.KAMAEL, BERSERKER),
	MALE_SOUL_HOUND(132, false, Race.KAMAEL, MALE_SOULBREAKER),
	FEMALE_SOUL_HOUND(133, false, Race.KAMAEL, FEMALE_SOULBREAKER),
	TRICKSTER(134, false, Race.KAMAEL, ARBALESTER),
	INSPECTOR(135, false, Race.KAMAEL, WARDER),
	JUDICATOR(136, false, Race.KAMAEL, INSPECTOR),

	SIGEL_KNIGHT(139, false, null, null),
	TYRR_WARRIOR(140, false, null, null),
	OTHELL_ROGUE(141, false, null, null),
	YUL_ARCHER(142, false, null, null),
	FEOH_WIZARD(143, false, null, null),
	ISS_ENCHANTER(144, false, null, null),
	WYNN_SUMMONER(145, false, null, null),
	AEORE_HEALER(146, false, null, null),

	SIGEL_PHOENIX_KNIGHT(148, false, Race.HUMAN, PHOENIX_KNIGHT),
	SIGEL_HELL_KNIGHT(149, false, Race.HUMAN, HELL_KNIGHT),
	SIGEL_EVA_TEMPLAR(150, false, Race.ELF, EVA_TEMPLAR),
	SIGEL_SHILLIEN_TEMPLAR(151, false, Race.DARK_ELF, SHILLIEN_TEMPLAR),
	TYRR_DUELIST(152, false, Race.HUMAN, DUELIST),
	TYRR_DREADNOUGHT(153, false, Race.HUMAN, DREADNOUGHT),
	TYRR_TITAN(154, false, Race.ORC, TITAN),
	TYRR_GRAND_KHAVATARI(155, false, Race.ORC, GRAND_KHAVATARI),
	TYRR_MAESTRO(156, false, Race.DWARF, MAESTRO),
	TYRR_DOOMBRINGER(157, false, Race.KAMAEL, DOOMBRINGER),
	OTHELL_ADVENTURER(158, false, Race.HUMAN, ADVENTURER),
	OTHELL_WIND_RIDER(159, false, Race.ELF, WIND_RIDER),
	OTHELL_GHOST_HUNTER(160, false, Race.DARK_ELF, GHOST_HUNTER),
	OTHELL_FORTUNE_SEEKER(161, false, Race.DWARF, FORTUNE_SEEKER),
	YUL_SAGITTARIUS(162, false, Race.HUMAN, SAGITTARIUS),
	YUL_MOONLIGHT_SENTINEL(163, false, Race.ELF, MOONLIGHT_SENTINEL),
	YUL_GHOST_SENTINEL(164, false, Race.DARK_ELF, GHOST_SENTINEL),
	YUL_TRICKSTER(165, false, Race.KAMAEL, TRICKSTER),
	FEOH_ARCHMAGE(166, true, Race.HUMAN, ARCHMAGE),
	FEOH_SOULTAKER(167, true, Race.HUMAN, SOULTAKER),
	FEOH_MYSTIC_MUSE(168, true, Race.ELF, MYSTIC_MUSE),
	FEOH_STORM_SCREAMER(169, true, Race.DARK_ELF, STORM_SCREAMER),
	FEOH_SOUL_HOUND(170, true, Race.KAMAEL, MALE_SOUL_HOUND), // fix me ?
	ISS_HIEROPHANT(171, true, Race.HUMAN, HIEROPHANT),
	ISS_SWORD_MUSE(172, false, Race.ELF, SWORD_MUSE),
	ISS_SPECTRAL_DANCER(173, false, Race.DARK_ELF, SPECTRAL_DANCER),
	ISS_DOMINATOR(174, true, Race.ORC, DOMINATOR),
	ISS_DOOMCRYER(175, true, Race.ORC, DOOMCRYER),
	WYNN_ARCANA_LORD(176, true, true, Race.HUMAN, ARCANA_LORD),
	WYNN_ELEMENTAL_MASTER(177, true, true, Race.ELF, ELEMENTAL_MASTER),
	WYNN_SPECTRAL_MASTER(178, true, true, Race.DARK_ELF, SPECTRAL_MASTER),
	AEORE_CARDINAL(179, true, Race.HUMAN, CARDINAL),
	AEORE_EVA_SAINT(180, true, Race.ELF, EVA_SAINT),
	AEORE_SHILLIEN_SAINT(181, true, Race.DARK_ELF, SHILLIEN_SAINT),
	
	ERTHEIA_FIGHTER(182, false, Race.ERTHEIA, null),
	ERTHEIA_WIZARD(183, true, Race.ERTHEIA, null),
	
	MARAUDER(184, false, Race.ERTHEIA, ERTHEIA_FIGHTER),
	CLOUD_BREAKER(185, true, Race.ERTHEIA, ERTHEIA_WIZARD),
	
	RIPPER(186, false, Race.ERTHEIA, MARAUDER),
	STRATOMANCER(187, true, Race.ERTHEIA, CLOUD_BREAKER),
	
	EVISCERATOR(188, false, Race.ERTHEIA, RIPPER),
	SAYHA_SEER(189, true, Race.ERTHEIA, STRATOMANCER);
	
	/** The Identifier of the Class */
	private final int _id;
	
	/** True if the class is a mage class */
	private final boolean _isMage;
	
	/** True if the class is a summoner class */
	private final boolean _isSummoner;
	
	/** The Race object of the class */
	private final Race _race;
	
	/** The parent ClassId or null if this class is a root */
	private final ClassId _parent;
	
	/** List of available Class for next transfer **/
	private final Set<ClassId> _nextClassIds = new HashSet<>(1);
	
	private static Map<Integer, ClassId> _classIdMap = new HashMap<>(ClassId.values().length);
	static
	{
		for (ClassId classId : ClassId.values())
		{
			_classIdMap.put(classId.getId(), classId);
		}
	}
	
	public static ClassId getClassId(int cId)
	{
		return _classIdMap.get(cId);
	}
	
	/**
	 * Class constructor.
	 * @param pId the class Id.
	 * @param pIsMage {code true} if the class is mage class.
	 * @param race the race related to the class.
	 * @param pParent the parent class Id.
	 */
	private ClassId(int pId, boolean pIsMage, Race race, ClassId pParent)
	{
		_id = pId;
		_isMage = pIsMage;
		_isSummoner = false;
		_race = race;
		_parent = pParent;
		
		if (_parent != null)
		{
			_parent.addNextClassId(this);
		}
	}
	
	/**
	 * Class constructor.
	 * @param pId the class Id.
	 * @param pIsMage {code true} if the class is mage class.
	 * @param pIsSummoner {code true} if the class is summoner class.
	 * @param race the race related to the class.
	 * @param pParent the parent class Id.
	 */
	private ClassId(int pId, boolean pIsMage, boolean pIsSummoner, Race race, ClassId pParent)
	{
		_id = pId;
		_isMage = pIsMage;
		_isSummoner = pIsSummoner;
		_race = race;
		_parent = pParent;
		
		if (_parent != null)
		{
			_parent.addNextClassId(this);
		}
	}
	
	/**
	 * Gets the ID of the class.
	 * @return the ID of the class
	 */
	@Override
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return {code true} if the class is a mage class.
	 */
	public boolean isMage()
	{
		return _isMage;
	}
	
	/**
	 * @return {code true} if the class is a summoner class.
	 */
	public boolean isSummoner()
	{
		return _isSummoner;
	}
	
	/**
	 * @return the Race object of the class.
	 */
	public Race getRace()
	{
		return _race;
	}
	
	/**
	 * @param cid the parent ClassId to check.
	 * @return {code true} if this Class is a child of the selected ClassId.
	 */
	public boolean childOf(ClassId cid)
	{
		if (_parent == null)
		{
			return false;
		}
		
		if (_parent == cid)
		{
			return true;
		}
		
		return _parent.childOf(cid);
	}
	
	/**
	 * @param cid the parent ClassId to check.
	 * @return {code true} if this Class is equal to the selected ClassId or a child of the selected ClassId.
	 */
	public boolean equalsOrChildOf(ClassId cid)
	{
		return (this == cid) || childOf(cid);
	}
	
	/**
	 * @return the child level of this Class (0=root, 1=child leve 1...)
	 */
	public int level()
	{
		if (_parent == null)
		{
			return 0;
		}
		
		return 1 + _parent.level();
	}
	
	/**
	 * @return its parent Class Id
	 */
	public ClassId getParent()
	{
		return _parent;
	}
	
	public ClassId getRootClassId()
	{
		if (_parent != null)
		{
			return _parent.getRootClassId();
		}
		return this;
	}
	
	/**
	 * @return list of possible class transfer for this class
	 */
	public Set<ClassId> getNextClassIds()
	{
		return _nextClassIds;
	}
	
	private final void addNextClassId(ClassId cId)
	{
		_nextClassIds.add(cId);
	}
}
