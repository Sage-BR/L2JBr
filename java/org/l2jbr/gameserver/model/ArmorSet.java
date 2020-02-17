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
package org.l2jbr.gameserver.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.stats.BaseStats;

/**
 * @author UnAfraid
 */
public class ArmorSet
{
	private final int _id;
	private final int _minimumPieces;
	private final boolean _isVisual;
	
	private final Set<Integer> _requiredItems = new LinkedHashSet<>();
	private final Set<Integer> _optionalItems = new LinkedHashSet<>();
	
	private final List<ArmorsetSkillHolder> _skills = new ArrayList<>();
	private final Map<BaseStats, Double> _stats = new LinkedHashMap<>();
	
	private static final int[] ARMORSET_SLOTS = new int[]
	{
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_FEET
	};
	private static final int[] ARTIFACT_1_SLOTS = new int[]
	{
		Inventory.PAPERDOLL_ARTIFACT1,
		Inventory.PAPERDOLL_ARTIFACT2,
		Inventory.PAPERDOLL_ARTIFACT3,
		Inventory.PAPERDOLL_ARTIFACT4,
		Inventory.PAPERDOLL_ARTIFACT13,
		Inventory.PAPERDOLL_ARTIFACT16,
		Inventory.PAPERDOLL_ARTIFACT19,
	
	};
	private static final int[] ARTIFACT_2_SLOTS = new int[]
	{
		Inventory.PAPERDOLL_ARTIFACT5,
		Inventory.PAPERDOLL_ARTIFACT6,
		Inventory.PAPERDOLL_ARTIFACT7,
		Inventory.PAPERDOLL_ARTIFACT8,
		Inventory.PAPERDOLL_ARTIFACT14,
		Inventory.PAPERDOLL_ARTIFACT17,
		Inventory.PAPERDOLL_ARTIFACT20,
	
	};
	private static final int[] ARTIFACT_3_SLOTS = new int[]
	{
		Inventory.PAPERDOLL_ARTIFACT9,
		Inventory.PAPERDOLL_ARTIFACT10,
		Inventory.PAPERDOLL_ARTIFACT11,
		Inventory.PAPERDOLL_ARTIFACT12,
		Inventory.PAPERDOLL_ARTIFACT15,
		Inventory.PAPERDOLL_ARTIFACT18,
		Inventory.PAPERDOLL_ARTIFACT21,
	
	};
	
	/**
	 * @param id
	 * @param minimumPieces
	 * @param isVisual
	 */
	public ArmorSet(int id, int minimumPieces, boolean isVisual)
	{
		_id = id;
		_minimumPieces = minimumPieces;
		_isVisual = isVisual;
	}
	
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return the minimum amount of pieces equipped to form a set
	 */
	public int getMinimumPieces()
	{
		return _minimumPieces;
	}
	
	/**
	 * @return {@code true} if the set is visual only, {@code} otherwise
	 */
	public boolean isVisual()
	{
		return _isVisual;
	}
	
	/**
	 * Adds an item to the set
	 * @param item
	 * @return {@code true} if item was successfully added, {@code false} in case it already exists
	 */
	public boolean addRequiredItem(Integer item)
	{
		return _requiredItems.add(item);
	}
	
	/**
	 * @return the set of items that can form a set
	 */
	public Set<Integer> getRequiredItems()
	{
		return _requiredItems;
	}
	
	/**
	 * Adds an shield to the set
	 * @param item
	 * @return {@code true} if shield was successfully added, {@code false} in case it already exists
	 */
	public boolean addOptionalItem(Integer item)
	{
		return _optionalItems.add(item);
	}
	
	/**
	 * @return the set of shields
	 */
	public Set<Integer> getOptionalItems()
	{
		return _optionalItems;
	}
	
	/**
	 * Adds an skill to the set
	 * @param holder
	 */
	public void addSkill(ArmorsetSkillHolder holder)
	{
		_skills.add(holder);
	}
	
	/**
	 * The list of skills that are activated when set reaches it's minimal equipped items condition
	 * @return
	 */
	public List<ArmorsetSkillHolder> getSkills()
	{
		return _skills;
	}
	
	/**
	 * Adds stats bonus to the set activated when set reaches it's minimal equipped items condition
	 * @param stat
	 * @param value
	 */
	public void addStatsBonus(BaseStats stat, double value)
	{
		_stats.putIfAbsent(stat, value);
	}
	
	/**
	 * @param stat
	 * @return the stats bonus value or 0 if doesn't exists
	 */
	public double getStatsBonus(BaseStats stat)
	{
		return _stats.getOrDefault(stat, 0d);
	}
	
	/**
	 * @param shield_id
	 * @return {@code true} if player has the shield of this set equipped, {@code false} in case set doesn't have a shield or player doesn't
	 */
	public boolean containOptionalItem(int shield_id)
	{
		return _optionalItems.contains(shield_id);
	}
	
	/**
	 * @param player
	 * @return true if all parts of set are enchanted to +6 or more
	 */
	public int getLowestSetEnchant(PlayerInstance player)
	{
		// Player don't have full set
		if (getPiecesCount(player, ItemInstance::getId) < _minimumPieces)
		{
			return 0;
		}
		
		final PlayerInventory inv = player.getInventory();
		int enchantLevel = Byte.MAX_VALUE;
		for (int armorSlot : ARMORSET_SLOTS)
		{
			final ItemInstance itemPart = inv.getPaperdollItem(armorSlot);
			if ((itemPart != null) && _requiredItems.contains(itemPart.getId()))
			{
				if (enchantLevel > itemPart.getEnchantLevel())
				{
					enchantLevel = itemPart.getEnchantLevel();
				}
			}
		}
		if (enchantLevel == Byte.MAX_VALUE)
		{
			enchantLevel = 0;
		}
		return enchantLevel;
	}
	
	/**
	 * Condition for 3 Lv. Set Effect Applied Skill
	 * @param player
	 * @param bookSlot
	 * @return total paperdoll(busy) count for 1 of 3 artifact book slots
	 */
	public int getArtifactSlotMask(PlayerInstance player, int bookSlot)
	{
		final PlayerInventory inv = player.getInventory();
		int slotMask = 0;
		switch (bookSlot)
		{
			case 1:
			{
				
				for (int artifactSlot : ARTIFACT_1_SLOTS)
				{
					final ItemInstance itemPart = inv.getPaperdollItem(artifactSlot);
					if ((itemPart != null) && _requiredItems.contains(itemPart.getId()))
					{
						slotMask += artifactSlot;
					}
				}
				break;
			}
			case 2:
			{
				for (int artifactSlot : ARTIFACT_2_SLOTS)
				{
					final ItemInstance itemPart = inv.getPaperdollItem(artifactSlot);
					if ((itemPart != null) && _requiredItems.contains(itemPart.getId()))
					{
						slotMask += artifactSlot;
					}
				}
				break;
			}
			case 3:
			{
				for (int artifactSlot : ARTIFACT_3_SLOTS)
				{
					final ItemInstance itemPart = inv.getPaperdollItem(artifactSlot);
					if ((itemPart != null) && _requiredItems.contains(itemPart.getId()))
					{
						slotMask += artifactSlot;
					}
				}
				break;
			}
		}
		return slotMask;
	}
	
	public boolean hasOptionalEquipped(PlayerInstance player, Function<ItemInstance, Integer> idProvider)
	{
		return player.getInventory().getPaperdollItems().stream().anyMatch(item -> _optionalItems.contains(idProvider.apply(item)));
	}
	
	/**
	 * @param player
	 * @param idProvider
	 * @return the amount of set visual items that player has equipped
	 */
	public long getPiecesCount(PlayerInstance player, Function<ItemInstance, Integer> idProvider)
	{
		return player.getInventory().getPaperdollItems(item -> _requiredItems.contains(idProvider.apply(item))).size();
	}
}
