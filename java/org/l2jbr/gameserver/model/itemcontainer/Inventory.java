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
package org.l2jbr.gameserver.model.itemcontainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.l2jbr.Config;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.data.xml.impl.AppearanceItemData;
import org.l2jbr.gameserver.data.xml.impl.ArmorSetsData;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.enums.ItemLocation;
import org.l2jbr.gameserver.enums.ItemSkillType;
import org.l2jbr.gameserver.enums.PrivateStoreType;
import org.l2jbr.gameserver.model.ArmorSet;
import org.l2jbr.gameserver.model.PlayerCondOverride;
import org.l2jbr.gameserver.model.VariationInstance;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2jbr.gameserver.model.items.EtcItem;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.appearance.AppearanceStone;
import org.l2jbr.gameserver.model.items.appearance.AppearanceType;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.items.type.EtcItemType;
import org.l2jbr.gameserver.model.items.type.ItemType;
import org.l2jbr.gameserver.model.items.type.WeaponType;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.SkillConditionScope;
import org.l2jbr.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2jbr.gameserver.network.serverpackets.SkillCoolTime;

/**
 * This class manages inventory
 * @version $Revision: 1.13.2.9.2.12 $ $Date: 2005/03/29 23:15:15 $ rewritten 23.2.2006 by Advi
 */
public abstract class Inventory extends ItemContainer
{
	protected static final Logger LOGGER = Logger.getLogger(Inventory.class.getName());
	
	public interface PaperdollListener
	{
		void notifyEquiped(int slot, ItemInstance inst, Inventory inventory);
		
		void notifyUnequiped(int slot, ItemInstance inst, Inventory inventory);
	}
	
	// Common Items
	public static final int ADENA_ID = 57;
	public static final int ANCIENT_ADENA_ID = 5575;
	public static final int BEAUTY_TICKET_ID = 36308;
	public static final int AIR_STONE_ID = 39461;
	public static final int TEMPEST_STONE_ID = 39592;
	public static final int ELCYUM_CRYSTAL_ID = 36514;
	
	public static final long MAX_ADENA = Config.MAX_ADENA;
	
	public static final int PAPERDOLL_UNDER = 0;
	public static final int PAPERDOLL_HEAD = 1;
	public static final int PAPERDOLL_HAIR = 2;
	public static final int PAPERDOLL_HAIR2 = 3;
	public static final int PAPERDOLL_NECK = 4;
	public static final int PAPERDOLL_RHAND = 5;
	public static final int PAPERDOLL_CHEST = 6;
	public static final int PAPERDOLL_LHAND = 7;
	public static final int PAPERDOLL_REAR = 8;
	public static final int PAPERDOLL_LEAR = 9;
	public static final int PAPERDOLL_GLOVES = 10;
	public static final int PAPERDOLL_LEGS = 11;
	public static final int PAPERDOLL_FEET = 12;
	public static final int PAPERDOLL_RFINGER = 13;
	public static final int PAPERDOLL_LFINGER = 14;
	public static final int PAPERDOLL_LBRACELET = 15;
	public static final int PAPERDOLL_RBRACELET = 16;
	public static final int PAPERDOLL_AGATHION1 = 17;
	public static final int PAPERDOLL_AGATHION2 = 18;
	public static final int PAPERDOLL_AGATHION3 = 19;
	public static final int PAPERDOLL_AGATHION4 = 20;
	public static final int PAPERDOLL_AGATHION5 = 21;
	public static final int PAPERDOLL_DECO1 = 22;
	public static final int PAPERDOLL_DECO2 = 23;
	public static final int PAPERDOLL_DECO3 = 24;
	public static final int PAPERDOLL_DECO4 = 25;
	public static final int PAPERDOLL_DECO5 = 26;
	public static final int PAPERDOLL_DECO6 = 27;
	public static final int PAPERDOLL_CLOAK = 28;
	public static final int PAPERDOLL_BELT = 29;
	public static final int PAPERDOLL_BROOCH = 30;
	public static final int PAPERDOLL_BROOCH_JEWEL1 = 31;
	public static final int PAPERDOLL_BROOCH_JEWEL2 = 32;
	public static final int PAPERDOLL_BROOCH_JEWEL3 = 33;
	public static final int PAPERDOLL_BROOCH_JEWEL4 = 34;
	public static final int PAPERDOLL_BROOCH_JEWEL5 = 35;
	public static final int PAPERDOLL_BROOCH_JEWEL6 = 36;
	public static final int PAPERDOLL_ARTIFACT_BOOK = 37;
	public static final int PAPERDOLL_ARTIFACT1 = 38; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT2 = 39; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT3 = 40; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT4 = 41; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT5 = 42; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT6 = 43; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT7 = 44; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT8 = 45; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT9 = 46; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT10 = 47; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT11 = 48; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT12 = 49; // Artifact Balance
	public static final int PAPERDOLL_ARTIFACT13 = 50; // Artifact Spirit
	public static final int PAPERDOLL_ARTIFACT14 = 51; // Artifact Spirit
	public static final int PAPERDOLL_ARTIFACT15 = 52; // Artifact Spirit
	public static final int PAPERDOLL_ARTIFACT16 = 53; // Artifact Protection
	public static final int PAPERDOLL_ARTIFACT17 = 54; // Artifact Protection
	public static final int PAPERDOLL_ARTIFACT18 = 55; // Artifact Protection
	public static final int PAPERDOLL_ARTIFACT19 = 56; // Artifact Support
	public static final int PAPERDOLL_ARTIFACT20 = 57; // Artifact Support
	public static final int PAPERDOLL_ARTIFACT21 = 58; // Artifact Support
	
	public static final int PAPERDOLL_TOTALSLOTS = 59;
	
	// Speed percentage mods
	public static final double MAX_ARMOR_WEIGHT = 12000;
	
	private final ItemInstance[] _paperdoll;
	private final List<PaperdollListener> _paperdollListeners;
	
	// protected to be accessed from child classes only
	protected int _totalWeight;
	
	// used to quickly check for using of items of special type
	private int _wearedMask;
	
	private int _blockedItemSlotsMask;
	
	// Recorder of alterations in inventory
	private static final class ChangeRecorder implements PaperdollListener
	{
		private final Inventory _inventory;
		private final Set<ItemInstance> _changed = ConcurrentHashMap.newKeySet();
		
		/**
		 * Constructor of the ChangeRecorder
		 * @param inventory
		 */
		ChangeRecorder(Inventory inventory)
		{
			_inventory = inventory;
			_inventory.addPaperdollListener(this);
		}
		
		/**
		 * Add alteration in inventory when item equipped
		 * @param slot
		 * @param item
		 * @param inventory
		 */
		@Override
		public void notifyEquiped(int slot, ItemInstance item, Inventory inventory)
		{
			_changed.add(item);
		}
		
		/**
		 * Add alteration in inventory when item unequipped
		 * @param slot
		 * @param item
		 * @param inventory
		 */
		@Override
		public void notifyUnequiped(int slot, ItemInstance item, Inventory inventory)
		{
			_changed.add(item);
		}
		
		/**
		 * Returns alterations in inventory
		 * @return ItemInstance[] : array of altered items
		 */
		public ItemInstance[] getChangedItems()
		{
			return _changed.toArray(new ItemInstance[_changed.size()]);
		}
	}
	
	private static final class BowCrossRodListener implements PaperdollListener
	{
		private static BowCrossRodListener instance = new BowCrossRodListener();
		
		public static BowCrossRodListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (slot != PAPERDOLL_RHAND)
			{
				return;
			}
			
			if (item.getItemType() == WeaponType.BOW)
			{
				final ItemInstance arrow = inventory.getPaperdollItem(PAPERDOLL_LHAND);
				if (arrow != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, null);
				}
			}
			else if ((item.getItemType() == WeaponType.CROSSBOW) || (item.getItemType() == WeaponType.TWOHANDCROSSBOW))
			{
				final ItemInstance bolts = inventory.getPaperdollItem(PAPERDOLL_LHAND);
				if (bolts != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, null);
				}
			}
			else if (item.getItemType() == WeaponType.FISHINGROD)
			{
				final ItemInstance lure = inventory.getPaperdollItem(PAPERDOLL_LHAND);
				if (lure != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, null);
				}
			}
		}
		
		@Override
		public void notifyEquiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (slot != PAPERDOLL_RHAND)
			{
				return;
			}
			
			if (item.getItemType() == WeaponType.BOW)
			{
				final ItemInstance arrow = inventory.findArrowForBow(item.getItem());
				if (arrow != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, arrow);
				}
			}
			else if ((item.getItemType() == WeaponType.CROSSBOW) || (item.getItemType() == WeaponType.TWOHANDCROSSBOW))
			{
				final ItemInstance bolts = inventory.findBoltForCrossBow(item.getItem());
				if (bolts != null)
				{
					inventory.setPaperdollItem(PAPERDOLL_LHAND, bolts);
				}
			}
		}
	}
	
	private static final class StatsListener implements PaperdollListener
	{
		private static StatsListener instance = new StatsListener();
		
		public static StatsListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, ItemInstance item, Inventory inventory)
		{
			inventory.getOwner().getStat().recalculateStats(true);
		}
		
		@Override
		public void notifyEquiped(int slot, ItemInstance item, Inventory inventory)
		{
			inventory.getOwner().getStat().recalculateStats(true);
		}
	}
	
	private static final class ItemSkillsListener implements PaperdollListener
	{
		private static ItemSkillsListener instance = new ItemSkillsListener();
		
		public static ItemSkillsListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (!inventory.getOwner().isPlayer())
			{
				return;
			}
			
			final PlayerInstance player = (PlayerInstance) inventory.getOwner();
			final Item it = item.getItem();
			final AtomicBoolean update = new AtomicBoolean();
			final AtomicBoolean updateTimestamp = new AtomicBoolean();
			
			// Remove augmentation bonuses on unequip
			if (item.isAugmented())
			{
				item.getAugmentation().removeBonus(player);
			}
			
			// Recalculate all stats
			player.getStat().recalculateStats(true);
			
			it.forEachSkill(ItemSkillType.ON_ENCHANT, holder ->
			{
				// Remove skills bestowed from +4 armor
				if (item.getEnchantLevel() >= holder.getValue())
				{
					player.removeSkill(holder.getSkill(), false, holder.getSkill().isPassive());
					update.compareAndSet(false, true);
				}
			});
			
			// Clear enchant bonus
			item.clearEnchantStats();
			
			// Clear SA Bonus
			item.clearSpecialAbilities();
			
			it.forEachSkill(ItemSkillType.NORMAL, holder ->
			{
				final Skill Skill = holder.getSkill();
				
				if (Skill != null)
				{
					player.removeSkill(Skill, false, Skill.isPassive());
					update.compareAndSet(false, true);
				}
				else
				{
					LOGGER.warning("Inventory.ItemSkillsListener.Weapon: Incorrect skill: " + holder);
				}
			});
			
			if (item.isArmor())
			{
				for (ItemInstance itm : inventory.getItems())
				{
					if (!itm.isEquipped() || (itm.getItem().getSkills(ItemSkillType.NORMAL) == null) || itm.equals(item))
					{
						continue;
					}
					
					itm.getItem().forEachSkill(ItemSkillType.NORMAL, holder ->
					{
						if (player.getSkillLevel(holder.getSkillId()) != 0)
						{
							return;
						}
						
						final Skill skill = holder.getSkill();
						if (skill != null)
						{
							player.addSkill(skill, false);
							
							if (skill.isActive())
							{
								if (!player.hasSkillReuse(skill.getReuseHashCode()))
								{
									final int equipDelay = item.getEquipReuseDelay();
									if (equipDelay > 0)
									{
										player.addTimeStamp(skill, equipDelay);
										player.disableSkill(skill, equipDelay);
									}
									updateTimestamp.compareAndSet(false, true);
								}
							}
							update.compareAndSet(false, true);
						}
					});
				}
			}
			
			// Must check all equipped items for enchant conditions.
			for (ItemInstance equipped : inventory.getPaperdollItems())
			{
				equipped.getItem().forEachSkill(ItemSkillType.ON_ENCHANT, holder ->
				{
					// Add skills bestowed from +4 armor
					if (equipped.getEnchantLevel() >= holder.getValue())
					{
						final Skill skill = holder.getSkill();
						// Check passive skill conditions.
						if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
						{
							player.removeSkill(holder.getSkill(), false, holder.getSkill().isPassive());
							update.compareAndSet(false, true);
						}
					}
				});
			}
			// Must check for toggle and isRemovedOnUnequipWeapon skill item conditions.
			for (Skill skill : player.getAllSkills())
			{
				if ((skill.isToggle() && player.isAffectedBySkill(skill.getId()) && !skill.checkConditions(SkillConditionScope.GENERAL, player, player)) //
					|| (it.isWeapon() && skill.isRemovedOnUnequipWeapon()))
				{
					player.stopSkillEffects(true, skill.getId());
					update.compareAndSet(false, true);
				}
			}
			
			// Apply skill, if weapon have "skills on unequip"
			it.forEachSkill(ItemSkillType.ON_UNEQUIP, holder -> holder.getSkill().activateSkill(player, player));
			
			if (update.get())
			{
				player.sendSkillList();
			}
			if (updateTimestamp.get())
			{
				player.sendPacket(new SkillCoolTime(player));
			}
			
			if (item.isWeapon())
			{
				player.unchargeAllShots();
			}
		}
		
		@Override
		public void notifyEquiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (!inventory.getOwner().isPlayer())
			{
				return;
			}
			
			final PlayerInstance player = (PlayerInstance) inventory.getOwner();
			
			// Any items equipped that result in expertise penalty do not give any skills at all.
			if (item.getItem().getCrystalType().isGreater(player.getExpertiseLevel()))
			{
				return;
			}
			
			final AtomicBoolean update = new AtomicBoolean();
			final AtomicBoolean updateTimestamp = new AtomicBoolean();
			
			// Apply augmentation bonuses on equip
			if (item.isAugmented())
			{
				item.getAugmentation().applyBonus(player);
			}
			
			// Recalculate all stats
			player.getStat().recalculateStats(true);
			
			item.getItem().forEachSkill(ItemSkillType.ON_ENCHANT, holder ->
			{
				// Add skills bestowed from +4 armor
				if (item.getEnchantLevel() >= holder.getValue())
				{
					final Skill skill = holder.getSkill();
					// Check passive skill conditions.
					if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
					{
						return;
					}
					player.addSkill(skill, false);
					update.compareAndSet(false, true);
				}
			});
			
			// Apply enchant stats
			item.applyEnchantStats();
			
			// Apply SA skill
			item.applySpecialAbilities();
			
			item.getItem().forEachSkill(ItemSkillType.NORMAL, holder ->
			{
				final Skill skill = holder.getSkill();
				if (skill != null)
				{
					if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
					{
						return;
					}
					
					player.addSkill(skill, false);
					
					if (skill.isActive())
					{
						if (!player.hasSkillReuse(skill.getReuseHashCode()))
						{
							final int equipDelay = item.getEquipReuseDelay();
							if (equipDelay > 0)
							{
								player.addTimeStamp(skill, equipDelay);
								player.disableSkill(skill, equipDelay);
							}
							updateTimestamp.compareAndSet(false, true);
						}
					}
					update.compareAndSet(false, true);
				}
				else
				{
					LOGGER.warning("Inventory.ItemSkillsListener.Weapon: Incorrect skill: " + holder);
				}
			});
			
			// Must check all equipped items for enchant conditions.
			for (ItemInstance equipped : inventory.getPaperdollItems())
			{
				equipped.getItem().forEachSkill(ItemSkillType.ON_ENCHANT, holder ->
				{
					// Add skills bestowed from +4 armor
					if (equipped.getEnchantLevel() >= holder.getValue())
					{
						final Skill skill = holder.getSkill();
						// Check passive skill conditions.
						if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
						{
							return;
						}
						player.addSkill(skill, false);
						update.compareAndSet(false, true);
					}
				});
			}
			
			// Apply skill, if weapon have "skills on equip"
			item.getItem().forEachSkill(ItemSkillType.ON_EQUIP, holder -> holder.getSkill().activateSkill(player, player));
			
			if (update.get())
			{
				player.sendSkillList();
			}
			if (updateTimestamp.get())
			{
				player.sendPacket(new SkillCoolTime(player));
			}
		}
	}
	
	private static final class ArmorSetListener implements PaperdollListener
	{
		private static ArmorSetListener instance = new ArmorSetListener();
		
		public static ArmorSetListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyEquiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (!inventory.getOwner().isPlayer())
			{
				return;
			}
			
			final PlayerInstance player = (PlayerInstance) inventory.getOwner();
			boolean update = false;
			
			// Verify and apply normal set
			if (verifyAndApply(player, item, ItemInstance::getId))
			{
				update = true;
			}
			
			// Verify and apply visual set
			final int itemVisualId = item.getVisualId();
			if (itemVisualId > 0)
			{
				final AppearanceStone stone = AppearanceItemData.getInstance().getStone(itemVisualId);
				if (stone != null)
				{
					if ((stone.getType() == AppearanceType.FIXED) && verifyAndApply(player, item, ItemInstance::getVisualId))
					{
						update = true;
					}
				}
			}
			
			if (update)
			{
				player.sendSkillList();
			}
			
			if ((item.getItem().getBodyPart() == Item.SLOT_BROOCH_JEWEL) || (item.getItem().getBodyPart() == Item.SLOT_BROOCH))
			{
				player.updateActiveBroochJewel();
			}
		}
		
		private static boolean applySkills(PlayerInstance player, ItemInstance item, ArmorSet armorSet, Function<ItemInstance, Integer> idProvider)
		{
			final long piecesCount = armorSet.getPiecesCount(player, idProvider);
			if (piecesCount >= armorSet.getMinimumPieces())
			{
				// Applying all skills that matching the conditions
				final AtomicBoolean updateTimeStamp = new AtomicBoolean();
				final AtomicBoolean update = new AtomicBoolean();
				for (ArmorsetSkillHolder holder : armorSet.getSkills())
				{
					if (holder.validateConditions(player, armorSet, idProvider))
					{
						final Skill itemSkill = holder.getSkill();
						if (itemSkill == null)
						{
							LOGGER.warning("Inventory.ArmorSetListener.addSkills: Incorrect skill: " + holder);
							continue;
						}
						
						if (itemSkill.isPassive() && !itemSkill.checkConditions(SkillConditionScope.PASSIVE, player, player))
						{
							continue;
						}
						
						player.addSkill(itemSkill, false);
						if (itemSkill.isActive() && (item != null))
						{
							if (!player.hasSkillReuse(itemSkill.getReuseHashCode()))
							{
								final int equipDelay = item.getEquipReuseDelay();
								if (equipDelay > 0)
								{
									player.addTimeStamp(itemSkill, equipDelay);
									player.disableSkill(itemSkill, equipDelay);
								}
							}
							updateTimeStamp.compareAndSet(false, true);
						}
						update.compareAndSet(false, true);
					}
				}
				if (updateTimeStamp.get())
				{
					player.sendPacket(new SkillCoolTime(player));
				}
				return update.get();
			}
			return false;
		}
		
		private static boolean verifyAndApply(PlayerInstance player, ItemInstance item, Function<ItemInstance, Integer> idProvider)
		{
			boolean update = false;
			final List<ArmorSet> armorSets = ArmorSetsData.getInstance().getSets(idProvider.apply(item));
			for (ArmorSet armorSet : armorSets)
			{
				if (applySkills(player, item, armorSet, idProvider))
				{
					update = true;
				}
			}
			return update;
		}
		
		private static boolean verifyAndRemove(PlayerInstance player, ItemInstance item, Function<ItemInstance, Integer> idProvider)
		{
			boolean update = false;
			final List<ArmorSet> armorSets = ArmorSetsData.getInstance().getSets(idProvider.apply(item));
			for (ArmorSet armorSet : armorSets)
			{
				// Remove all skills that doesn't matches the conditions
				for (ArmorsetSkillHolder holder : armorSet.getSkills())
				{
					if (!holder.validateConditions(player, armorSet, idProvider))
					{
						final Skill itemSkill = holder.getSkill();
						if (itemSkill == null)
						{
							LOGGER.warning("Inventory.ArmorSetListener.removeSkills: Incorrect skill: " + holder);
							continue;
						}
						
						// Update if a skill has been removed.
						if (player.removeSkill(itemSkill, false, itemSkill.isPassive()) != null)
						{
							update = true;
						}
					}
				}
				
				// Attempt to apply lower level skills if possible
				if (applySkills(player, item, armorSet, idProvider))
				{
					update = true;
				}
			}
			
			return update;
		}
		
		@Override
		public void notifyUnequiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (!inventory.getOwner().isPlayer())
			{
				return;
			}
			
			final PlayerInstance player = (PlayerInstance) inventory.getOwner();
			boolean remove = false;
			
			// verify and remove normal set bonus
			if (verifyAndRemove(player, item, ItemInstance::getId))
			{
				remove = true;
			}
			
			// verify and remove visual set bonus
			if (verifyAndRemove(player, item, ItemInstance::getVisualId))
			{
				remove = true;
			}
			
			if (remove)
			{
				player.checkItemRestriction();
				player.sendSkillList();
			}
			
			if ((item.getItem().getBodyPart() == Item.SLOT_BROOCH_JEWEL) || (item.getItem().getBodyPart() == Item.SLOT_BROOCH))
			{
				player.updateActiveBroochJewel();
			}
		}
	}
	
	private static final class BraceletListener implements PaperdollListener
	{
		private static BraceletListener instance = new BraceletListener();
		
		public static BraceletListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (item.getItem().getBodyPart() == Item.SLOT_R_BRACELET)
			{
				inventory.unEquipItemInSlot(PAPERDOLL_DECO1);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO2);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO3);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO4);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO5);
				inventory.unEquipItemInSlot(PAPERDOLL_DECO6);
			}
		}
		
		// Note (April 3, 2009): Currently on equip, talismans do not display properly, do we need checks here to fix this?
		@Override
		public void notifyEquiped(int slot, ItemInstance item, Inventory inventory)
		{
		}
	}
	
	private static final class BroochListener implements PaperdollListener
	{
		private static BroochListener instance = new BroochListener();
		
		public static BroochListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (item.getItem().getBodyPart() == Item.SLOT_BROOCH)
			{
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL1);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL2);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL3);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL4);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL5);
				inventory.unEquipItemInSlot(PAPERDOLL_BROOCH_JEWEL6);
			}
		}
		
		// Note (April 3, 2009): Currently on equip, talismans do not display properly, do we need checks here to fix this?
		@Override
		public void notifyEquiped(int slot, ItemInstance item, Inventory inventory)
		{
		}
	}
	
	private static final class AgathionBraceletListener implements PaperdollListener
	{
		private static AgathionBraceletListener instance = new AgathionBraceletListener();
		
		public static AgathionBraceletListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (item.getItem().getBodyPart() == Item.SLOT_L_BRACELET)
			{
				inventory.unEquipItemInSlot(PAPERDOLL_AGATHION1);
				inventory.unEquipItemInSlot(PAPERDOLL_AGATHION2);
				inventory.unEquipItemInSlot(PAPERDOLL_AGATHION3);
				inventory.unEquipItemInSlot(PAPERDOLL_AGATHION4);
				inventory.unEquipItemInSlot(PAPERDOLL_AGATHION5);
			}
		}
		
		@Override
		public void notifyEquiped(int slot, ItemInstance item, Inventory inventory)
		{
		}
	}
	
	private static final class ArtifactBookListener implements PaperdollListener
	{
		private static ArtifactBookListener instance = new ArtifactBookListener();
		
		public static ArtifactBookListener getInstance()
		{
			return instance;
		}
		
		@Override
		public void notifyUnequiped(int slot, ItemInstance item, Inventory inventory)
		{
			if (item.getItem().getBodyPart() == Item.SLOT_ARTIFACT_BOOK)
			{
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT1);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT2);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT3);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT4);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT5);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT6);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT7);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT8);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT9);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT10);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT11);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT12);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT13);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT14);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT15);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT16);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT17);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT18);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT19);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT20);
				inventory.unEquipItemInSlot(PAPERDOLL_ARTIFACT21);
				
			}
		}
		
		@Override
		public void notifyEquiped(int slot, ItemInstance item, Inventory inventory)
		{
		}
	}
	
	/**
	 * Constructor of the inventory
	 */
	protected Inventory()
	{
		_paperdoll = new ItemInstance[PAPERDOLL_TOTALSLOTS];
		_paperdollListeners = new ArrayList<>();
		
		if (this instanceof PlayerInventory)
		{
			addPaperdollListener(ArmorSetListener.getInstance());
			addPaperdollListener(BowCrossRodListener.getInstance());
			addPaperdollListener(ItemSkillsListener.getInstance());
			addPaperdollListener(BraceletListener.getInstance());
			addPaperdollListener(BroochListener.getInstance());
			addPaperdollListener(AgathionBraceletListener.getInstance());
			addPaperdollListener(ArtifactBookListener.getInstance());
		}
		
		// common
		addPaperdollListener(StatsListener.getInstance());
		
	}
	
	protected abstract ItemLocation getEquipLocation();
	
	/**
	 * Returns the instance of new ChangeRecorder
	 * @return ChangeRecorder
	 */
	private ChangeRecorder newRecorder()
	{
		return new ChangeRecorder(this);
	}
	
	/**
	 * Drop item from inventory and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param item : ItemInstance to be dropped
	 * @param actor : PlayerInstance Player requesting the item drop
	 * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance dropItem(String process, ItemInstance item, PlayerInstance actor, Object reference)
	{
		if (item == null)
		{
			return null;
		}
		
		synchronized (item)
		{
			if (!_items.containsKey(item.getObjectId()))
			{
				return null;
			}
			
			removeItem(item);
			item.setOwnerId(process, 0, actor, reference);
			item.setItemLocation(ItemLocation.VOID);
			item.setLastChange(ItemInstance.REMOVED);
			
			item.updateDatabase();
			refreshWeight();
		}
		return item;
	}
	
	/**
	 * Drop item from inventory by using its <B>objectID</B> and updates database
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Instance identifier of the item to be dropped
	 * @param count : int Quantity of items to be dropped
	 * @param actor : PlayerInstance Player requesting the item drop
	 * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return ItemInstance corresponding to the destroyed item or the updated item in inventory
	 */
	public ItemInstance dropItem(String process, int objectId, long count, PlayerInstance actor, Object reference)
	{
		ItemInstance item = getItemByObjectId(objectId);
		if (item == null)
		{
			return null;
		}
		
		synchronized (item)
		{
			if (!_items.containsKey(item.getObjectId()))
			{
				return null;
			}
			
			// Adjust item quantity and create new instance to drop
			// Directly drop entire item
			if (item.getCount() > count)
			{
				item.changeCount(process, -count, actor, reference);
				item.setLastChange(ItemInstance.MODIFIED);
				item.updateDatabase();
				
				item = ItemTable.getInstance().createItem(process, item.getId(), count, actor, reference);
				item.updateDatabase();
				refreshWeight();
				return item;
			}
		}
		return dropItem(process, item, actor, reference);
	}
	
	/**
	 * Adds item to inventory for further adjustments and Equip it if necessary (itemlocation defined)
	 * @param item : ItemInstance to be added from inventory
	 */
	@Override
	protected void addItem(ItemInstance item)
	{
		super.addItem(item);
		if (item.isEquipped())
		{
			equipItem(item);
		}
	}
	
	/**
	 * Removes item from inventory for further adjustments.
	 * @param item : ItemInstance to be removed from inventory
	 */
	@Override
	protected boolean removeItem(ItemInstance item)
	{
		// Unequip item if equiped
		for (int i = 0; i < _paperdoll.length; i++)
		{
			if (_paperdoll[i] == item)
			{
				unEquipItemInSlot(i);
			}
		}
		return super.removeItem(item);
	}
	
	/**
	 * @param slot the slot.
	 * @return the item in the paperdoll slot
	 */
	public ItemInstance getPaperdollItem(int slot)
	{
		return _paperdoll[slot];
	}
	
	/**
	 * @param slot the slot.
	 * @return {@code true} if specified paperdoll slot is empty, {@code false} otherwise
	 */
	public boolean isPaperdollSlotEmpty(int slot)
	{
		return _paperdoll[slot] == null;
	}
	
	public boolean isPaperdollSlotNotEmpty(int slot)
	{
		return _paperdoll[slot] != null;
	}
	
	public boolean isItemEquipped(int itemId)
	{
		for (ItemInstance item : getItemsByItemId(itemId))
		{
			if (item.isEquipped())
			{
				return true;
			}
		}
		return false;
	}
	
	public static int getPaperdollIndex(long slot)
	{
		if (slot == Item.SLOT_UNDERWEAR)
		{
			return PAPERDOLL_UNDER;
		}
		else if (slot == Item.SLOT_R_EAR)
		{
			return PAPERDOLL_REAR;
		}
		else if ((slot == Item.SLOT_LR_EAR) || (slot == Item.SLOT_L_EAR))
		{
			return PAPERDOLL_LEAR;
		}
		else if (slot == Item.SLOT_NECK)
		{
			return PAPERDOLL_NECK;
		}
		else if ((slot == Item.SLOT_R_FINGER) || (slot == Item.SLOT_LR_FINGER))
		{
			return PAPERDOLL_RFINGER;
		}
		else if (slot == Item.SLOT_L_FINGER)
		{
			return PAPERDOLL_LFINGER;
		}
		else if (slot == Item.SLOT_HEAD)
		{
			return PAPERDOLL_HEAD;
		}
		else if ((slot == Item.SLOT_R_HAND) || (slot == Item.SLOT_LR_HAND))
		{
			return PAPERDOLL_RHAND;
		}
		else if (slot == Item.SLOT_L_HAND)
		{
			return PAPERDOLL_LHAND;
		}
		else if (slot == Item.SLOT_GLOVES)
		{
			return PAPERDOLL_GLOVES;
		}
		else if ((slot == Item.SLOT_CHEST) || (slot == Item.SLOT_FULL_ARMOR) || (slot == Item.SLOT_ALLDRESS))
		{
			return PAPERDOLL_CHEST;
		}
		else if (slot == Item.SLOT_LEGS)
		{
			return PAPERDOLL_LEGS;
		}
		else if (slot == Item.SLOT_FEET)
		{
			return PAPERDOLL_FEET;
		}
		else if (slot == Item.SLOT_BACK)
		{
			return PAPERDOLL_CLOAK;
		}
		else if ((slot == Item.SLOT_HAIR) || (slot == Item.SLOT_HAIRALL))
		{
			return PAPERDOLL_HAIR;
		}
		else if (slot == Item.SLOT_HAIR2)
		{
			return PAPERDOLL_HAIR2;
		}
		else if (slot == Item.SLOT_R_BRACELET)
		{
			return PAPERDOLL_RBRACELET;
		}
		else if (slot == Item.SLOT_L_BRACELET)
		{
			return PAPERDOLL_LBRACELET;
		}
		else if (slot == Item.SLOT_DECO)
		{
			return PAPERDOLL_DECO1; // return first we deal with it later
		}
		else if (slot == Item.SLOT_BELT)
		{
			return PAPERDOLL_BELT;
		}
		else if (slot == Item.SLOT_BROOCH)
		{
			return PAPERDOLL_BROOCH;
		}
		else if (slot == Item.SLOT_BROOCH_JEWEL)
		{
			return PAPERDOLL_BROOCH_JEWEL1;
		}
		else if (slot == Item.SLOT_AGATHION)
		{
			return PAPERDOLL_AGATHION1;
		}
		else if (slot == Item.SLOT_ARTIFACT_BOOK)
		{
			return PAPERDOLL_ARTIFACT_BOOK;
		}
		else if (slot == Item.SLOT_ARTIFACT)
		{
			return PAPERDOLL_ARTIFACT1;
		}
		return -1;
	}
	
	/**
	 * Returns the item in the paperdoll Item slot
	 * @param slot identifier
	 * @return ItemInstance
	 */
	public ItemInstance getPaperdollItemByItemId(int slot)
	{
		final int index = getPaperdollIndex(slot);
		if (index == -1)
		{
			return null;
		}
		return _paperdoll[index];
	}
	
	/**
	 * Returns the ID of the item in the paperdoll slot
	 * @param slot : int designating the slot
	 * @return int designating the ID of the item
	 */
	public int getPaperdollItemId(int slot)
	{
		final ItemInstance item = _paperdoll[slot];
		if (item != null)
		{
			return item.getId();
		}
		return 0;
	}
	
	/**
	 * Returns the ID of the item in the paperdoll slot
	 * @param slot : int designating the slot
	 * @return int designating the ID of the item
	 */
	public int getPaperdollItemDisplayId(int slot)
	{
		final ItemInstance item = _paperdoll[slot];
		return (item != null) ? item.getDisplayId() : 0;
	}
	
	/**
	 * Returns the visual id of the item in the paperdoll slot
	 * @param slot : int designating the slot
	 * @return int designating the ID of the item
	 */
	public int getPaperdollItemVisualId(int slot)
	{
		final ItemInstance item = _paperdoll[slot];
		return (item != null) ? item.getVisualId() : 0;
	}
	
	public VariationInstance getPaperdollAugmentation(int slot)
	{
		final ItemInstance item = _paperdoll[slot];
		return (item != null) ? item.getAugmentation() : null;
	}
	
	/**
	 * Returns the objectID associated to the item in the paperdoll slot
	 * @param slot : int pointing out the slot
	 * @return int designating the objectID
	 */
	public int getPaperdollObjectId(int slot)
	{
		final ItemInstance item = _paperdoll[slot];
		return (item != null) ? item.getObjectId() : 0;
	}
	
	/**
	 * Adds new inventory's paperdoll listener.
	 * @param listener the new listener
	 */
	public synchronized void addPaperdollListener(PaperdollListener listener)
	{
		if (!_paperdollListeners.contains(listener))
		{
			_paperdollListeners.add(listener);
		}
	}
	
	/**
	 * Removes a paperdoll listener.
	 * @param listener the listener to be deleted
	 */
	public synchronized void removePaperdollListener(PaperdollListener listener)
	{
		_paperdollListeners.remove(listener);
	}
	
	/**
	 * Equips an item in the given slot of the paperdoll.<br>
	 * <U><I>Remark :</I></U> The item <B>must be</B> in the inventory already.
	 * @param slot : int pointing out the slot of the paperdoll
	 * @param item : ItemInstance pointing out the item to add in slot
	 * @return ItemInstance designating the item placed in the slot before
	 */
	public synchronized ItemInstance setPaperdollItem(int slot, ItemInstance item)
	{
		final ItemInstance old = _paperdoll[slot];
		if (old != item)
		{
			if (old != null)
			{
				_paperdoll[slot] = null;
				// Put old item from paperdoll slot to base location
				old.setItemLocation(getBaseLocation());
				old.setLastChange(ItemInstance.MODIFIED);
				// Get the mask for paperdoll
				int mask = 0;
				for (int i = 0; i < PAPERDOLL_TOTALSLOTS; i++)
				{
					final ItemInstance pi = _paperdoll[i];
					if (pi != null)
					{
						mask |= pi.getItem().getItemMask();
					}
				}
				_wearedMask = mask;
				// Notify all paperdoll listener in order to unequip old item in slot
				for (PaperdollListener listener : _paperdollListeners)
				{
					if (listener == null)
					{
						continue;
					}
					
					listener.notifyUnequiped(slot, old, this);
				}
				old.updateDatabase();
			}
			// Add new item in slot of paperdoll
			if (item != null)
			{
				_paperdoll[slot] = item;
				item.setItemLocation(getEquipLocation(), slot);
				item.setLastChange(ItemInstance.MODIFIED);
				_wearedMask |= item.getItem().getItemMask();
				for (PaperdollListener listener : _paperdollListeners)
				{
					if (listener == null)
					{
						continue;
					}
					
					listener.notifyEquiped(slot, item, this);
				}
				item.updateDatabase();
			}
			
			if (getOwner().isPlayer())
			{
				getOwner().sendPacket(new ExUserInfoEquipSlot(getOwner().getActingPlayer()));
			}
		}
		return old;
	}
	
	/**
	 * @return the mask of wore item
	 */
	public int getWearedMask()
	{
		return _wearedMask;
	}
	
	public long getSlotFromItem(ItemInstance item)
	{
		long slot = -1;
		final int location = item.getLocationSlot();
		switch (location)
		{
			case PAPERDOLL_UNDER:
			{
				slot = Item.SLOT_UNDERWEAR;
				break;
			}
			case PAPERDOLL_LEAR:
			{
				slot = Item.SLOT_L_EAR;
				break;
			}
			case PAPERDOLL_REAR:
			{
				slot = Item.SLOT_R_EAR;
				break;
			}
			case PAPERDOLL_NECK:
			{
				slot = Item.SLOT_NECK;
				break;
			}
			case PAPERDOLL_RFINGER:
			{
				slot = Item.SLOT_R_FINGER;
				break;
			}
			case PAPERDOLL_LFINGER:
			{
				slot = Item.SLOT_L_FINGER;
				break;
			}
			case PAPERDOLL_HAIR:
			{
				slot = Item.SLOT_HAIR;
				break;
			}
			case PAPERDOLL_HAIR2:
			{
				slot = Item.SLOT_HAIR2;
				break;
			}
			case PAPERDOLL_HEAD:
			{
				slot = Item.SLOT_HEAD;
				break;
			}
			case PAPERDOLL_RHAND:
			{
				slot = Item.SLOT_R_HAND;
				break;
			}
			case PAPERDOLL_LHAND:
			{
				slot = Item.SLOT_L_HAND;
				break;
			}
			case PAPERDOLL_GLOVES:
			{
				slot = Item.SLOT_GLOVES;
				break;
			}
			case PAPERDOLL_CHEST:
			{
				slot = item.getItem().getBodyPart();
				break;
			}
			case PAPERDOLL_LEGS:
			{
				slot = Item.SLOT_LEGS;
				break;
			}
			case PAPERDOLL_CLOAK:
			{
				slot = Item.SLOT_BACK;
				break;
			}
			case PAPERDOLL_FEET:
			{
				slot = Item.SLOT_FEET;
				break;
			}
			case PAPERDOLL_LBRACELET:
			{
				slot = Item.SLOT_L_BRACELET;
				break;
			}
			case PAPERDOLL_RBRACELET:
			{
				slot = Item.SLOT_R_BRACELET;
				break;
			}
			case PAPERDOLL_DECO1:
			case PAPERDOLL_DECO2:
			case PAPERDOLL_DECO3:
			case PAPERDOLL_DECO4:
			case PAPERDOLL_DECO5:
			case PAPERDOLL_DECO6:
			{
				slot = Item.SLOT_DECO;
				break;
			}
			case PAPERDOLL_BELT:
			{
				slot = Item.SLOT_BELT;
				break;
			}
			case PAPERDOLL_BROOCH:
			{
				slot = Item.SLOT_BROOCH;
				break;
			}
			case PAPERDOLL_BROOCH_JEWEL1:
			case PAPERDOLL_BROOCH_JEWEL2:
			case PAPERDOLL_BROOCH_JEWEL3:
			case PAPERDOLL_BROOCH_JEWEL4:
			case PAPERDOLL_BROOCH_JEWEL5:
			case PAPERDOLL_BROOCH_JEWEL6:
			{
				slot = Item.SLOT_BROOCH_JEWEL;
				break;
			}
			case PAPERDOLL_AGATHION1:
			case PAPERDOLL_AGATHION2:
			case PAPERDOLL_AGATHION3:
			case PAPERDOLL_AGATHION4:
			case PAPERDOLL_AGATHION5:
			{
				slot = Item.SLOT_AGATHION;
				break;
			}
			case PAPERDOLL_ARTIFACT_BOOK:
			{
				slot = Item.SLOT_ARTIFACT_BOOK;
				break;
			}
			case PAPERDOLL_ARTIFACT1:
			case PAPERDOLL_ARTIFACT2:
			case PAPERDOLL_ARTIFACT3:
			case PAPERDOLL_ARTIFACT4:
			case PAPERDOLL_ARTIFACT5:
			case PAPERDOLL_ARTIFACT6:
			case PAPERDOLL_ARTIFACT7:
			case PAPERDOLL_ARTIFACT8:
			case PAPERDOLL_ARTIFACT9:
			case PAPERDOLL_ARTIFACT10:
			case PAPERDOLL_ARTIFACT11:
			case PAPERDOLL_ARTIFACT12:
			case PAPERDOLL_ARTIFACT13:
			case PAPERDOLL_ARTIFACT14:
			case PAPERDOLL_ARTIFACT15:
			case PAPERDOLL_ARTIFACT16:
			case PAPERDOLL_ARTIFACT17:
			case PAPERDOLL_ARTIFACT18:
			case PAPERDOLL_ARTIFACT19:
			case PAPERDOLL_ARTIFACT20:
			case PAPERDOLL_ARTIFACT21:
			{
				slot = Item.SLOT_ARTIFACT;
			}
		}
		return slot;
	}
	
	/**
	 * Unequips item in body slot and returns alterations.<BR>
	 * <B>If you dont need return value use {@link Inventory#unEquipItemInBodySlot(long)} instead</B>
	 * @param slot : int designating the slot of the paperdoll
	 * @return ItemInstance[] : list of changes
	 */
	public ItemInstance[] unEquipItemInBodySlotAndRecord(long slot)
	{
		final ChangeRecorder recorder = newRecorder();
		
		try
		{
			unEquipItemInBodySlot(slot);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Sets item in slot of the paperdoll to null value
	 * @param pdollSlot : int designating the slot
	 * @return ItemInstance designating the item in slot before change
	 */
	public ItemInstance unEquipItemInSlot(int pdollSlot)
	{
		return setPaperdollItem(pdollSlot, null);
	}
	
	/**
	 * Unequips item in slot and returns alterations<BR>
	 * <B>If you dont need return value use {@link Inventory#unEquipItemInSlot(int)} instead</B>
	 * @param slot : int designating the slot
	 * @return ItemInstance[] : list of items altered
	 */
	public ItemInstance[] unEquipItemInSlotAndRecord(int slot)
	{
		final ChangeRecorder recorder = newRecorder();
		
		try
		{
			unEquipItemInSlot(slot);
			if (getOwner().isPlayer())
			{
				((PlayerInstance) getOwner()).refreshExpertisePenalty();
			}
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Unequips item in slot (i.e. equips with default value)
	 * @param slot : int designating the slot
	 * @return {@link ItemInstance} designating the item placed in the slot
	 */
	public ItemInstance unEquipItemInBodySlot(long slot)
	{
		int pdollSlot = -1;
		
		if (slot == Item.SLOT_L_EAR)
		{
			pdollSlot = PAPERDOLL_LEAR;
		}
		else if (slot == Item.SLOT_R_EAR)
		{
			pdollSlot = PAPERDOLL_REAR;
		}
		else if (slot == Item.SLOT_NECK)
		{
			pdollSlot = PAPERDOLL_NECK;
		}
		else if (slot == Item.SLOT_R_FINGER)
		{
			pdollSlot = PAPERDOLL_RFINGER;
		}
		else if (slot == Item.SLOT_L_FINGER)
		{
			pdollSlot = PAPERDOLL_LFINGER;
		}
		else if (slot == Item.SLOT_HAIR)
		{
			pdollSlot = PAPERDOLL_HAIR;
		}
		else if (slot == Item.SLOT_HAIR2)
		{
			pdollSlot = PAPERDOLL_HAIR2;
		}
		else if (slot == Item.SLOT_HAIRALL)
		{
			setPaperdollItem(PAPERDOLL_HAIR, null);
			pdollSlot = PAPERDOLL_HAIR;
		}
		else if (slot == Item.SLOT_HEAD)
		{
			pdollSlot = PAPERDOLL_HEAD;
		}
		else if ((slot == Item.SLOT_R_HAND) || (slot == Item.SLOT_LR_HAND))
		{
			pdollSlot = PAPERDOLL_RHAND;
		}
		else if (slot == Item.SLOT_L_HAND)
		{
			pdollSlot = PAPERDOLL_LHAND;
		}
		else if (slot == Item.SLOT_GLOVES)
		{
			pdollSlot = PAPERDOLL_GLOVES;
		}
		else if ((slot == Item.SLOT_CHEST) || (slot == Item.SLOT_ALLDRESS) || (slot == Item.SLOT_FULL_ARMOR))
		{
			pdollSlot = PAPERDOLL_CHEST;
		}
		else if (slot == Item.SLOT_LEGS)
		{
			pdollSlot = PAPERDOLL_LEGS;
		}
		else if (slot == Item.SLOT_BACK)
		{
			pdollSlot = PAPERDOLL_CLOAK;
		}
		else if (slot == Item.SLOT_FEET)
		{
			pdollSlot = PAPERDOLL_FEET;
		}
		else if (slot == Item.SLOT_UNDERWEAR)
		{
			pdollSlot = PAPERDOLL_UNDER;
		}
		else if (slot == Item.SLOT_L_BRACELET)
		{
			pdollSlot = PAPERDOLL_LBRACELET;
		}
		else if (slot == Item.SLOT_R_BRACELET)
		{
			pdollSlot = PAPERDOLL_RBRACELET;
		}
		else if (slot == Item.SLOT_DECO)
		{
			pdollSlot = PAPERDOLL_DECO1;
		}
		else if (slot == Item.SLOT_BELT)
		{
			pdollSlot = PAPERDOLL_BELT;
		}
		else if (slot == Item.SLOT_BROOCH)
		{
			pdollSlot = PAPERDOLL_BROOCH;
		}
		else if (slot == Item.SLOT_BROOCH_JEWEL)
		{
			pdollSlot = PAPERDOLL_BROOCH_JEWEL1;
		}
		else if (slot == Item.SLOT_AGATHION)
		{
			pdollSlot = PAPERDOLL_AGATHION1;
		}
		else if (slot == Item.SLOT_ARTIFACT_BOOK)
		{
			pdollSlot = PAPERDOLL_ARTIFACT_BOOK;
		}
		else if (slot == Item.SLOT_ARTIFACT)
		{
			pdollSlot = PAPERDOLL_ARTIFACT1;
		}
		else
		{
			LOGGER.info("Unhandled slot type: " + slot);
			LOGGER.info(CommonUtil.getTraceString(Thread.currentThread().getStackTrace()));
		}
		if (pdollSlot >= 0)
		{
			final ItemInstance old = setPaperdollItem(pdollSlot, null);
			if (old != null)
			{
				if (getOwner().isPlayer())
				{
					((PlayerInstance) getOwner()).refreshExpertisePenalty();
				}
			}
			return old;
		}
		return null;
	}
	
	/**
	 * Equips item and returns list of alterations<BR>
	 * <B>If you don't need return value use {@link Inventory#equipItem(ItemInstance)} instead</B>
	 * @param item : ItemInstance corresponding to the item
	 * @return ItemInstance[] : list of alterations
	 */
	public ItemInstance[] equipItemAndRecord(ItemInstance item)
	{
		final ChangeRecorder recorder = newRecorder();
		
		try
		{
			equipItem(item);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Equips item in slot of paperdoll.
	 * @param item : ItemInstance designating the item and slot used.
	 */
	public void equipItem(ItemInstance item)
	{
		if (getOwner().isPlayer())
		{
			if (((PlayerInstance) getOwner()).getPrivateStoreType() != PrivateStoreType.NONE)
			{
				return;
			}
			
			// Equip only identical grade arrows.
			final EtcItem etcItem = item.getEtcItem();
			if (etcItem != null)
			{
				final ItemInstance weapon = getPaperdollItem(Inventory.PAPERDOLL_RHAND);
				if (weapon != null)
				{
					final EtcItemType itemType = etcItem.getItemType();
					final ItemType weaponItemType = weapon.getItemType();
					if ((((weaponItemType == WeaponType.BOW) && (itemType == EtcItemType.ARROW)) //
						|| (((weaponItemType == WeaponType.CROSSBOW) || (weaponItemType == WeaponType.TWOHANDCROSSBOW)) && (itemType == EtcItemType.BOLT))) //
						&& (weapon.getItem().getCrystalTypePlus() != item.getItem().getCrystalTypePlus()))
					{
						return;
					}
				}
			}
			
			final PlayerInstance player = (PlayerInstance) getOwner();
			if (!player.canOverrideCond(PlayerCondOverride.ITEM_CONDITIONS) && !player.isHero() && item.isHeroItem())
			{
				return;
			}
		}
		
		final long targetSlot = item.getItem().getBodyPart();
		
		// Check if player is using Formal Wear and item isn't Wedding Bouquet.
		final ItemInstance formal = getPaperdollItem(PAPERDOLL_CHEST);
		if ((item.getId() != 21163) && (formal != null) && (formal.getItem().getBodyPart() == Item.SLOT_ALLDRESS))
		{
			// only chest target can pass this
			if ((targetSlot == Item.SLOT_LR_HAND) || (targetSlot == Item.SLOT_L_HAND) || (targetSlot == Item.SLOT_R_HAND) || (targetSlot == Item.SLOT_LEGS) || (targetSlot == Item.SLOT_FEET) || (targetSlot == Item.SLOT_GLOVES) || (targetSlot == Item.SLOT_HEAD))
			{
				return;
			}
		}
		
		// don't care about arrows, listener will unequip them (hopefully)
		// handle full armor
		// formal dress
		if (targetSlot == Item.SLOT_LR_HAND)
		{
			setPaperdollItem(PAPERDOLL_LHAND, null);
			setPaperdollItem(PAPERDOLL_RHAND, item);
		}
		else if (targetSlot == Item.SLOT_L_HAND)
		{
			final ItemInstance rh = getPaperdollItem(PAPERDOLL_RHAND);
			if ((rh != null) && (rh.getItem().getBodyPart() == Item.SLOT_LR_HAND) && !(((rh.getItemType() == WeaponType.BOW) && (item.getItemType() == EtcItemType.ARROW)) || (((rh.getItemType() == WeaponType.CROSSBOW) || (rh.getItemType() == WeaponType.TWOHANDCROSSBOW)) && (item.getItemType() == EtcItemType.BOLT)) || ((rh.getItemType() == WeaponType.FISHINGROD) && (item.getItemType() == EtcItemType.LURE))))
			{
				setPaperdollItem(PAPERDOLL_RHAND, null);
			}
			setPaperdollItem(PAPERDOLL_LHAND, item);
		}
		else if (targetSlot == Item.SLOT_R_HAND)
		{
			setPaperdollItem(PAPERDOLL_RHAND, item);
		}
		else if ((targetSlot == Item.SLOT_L_EAR) || (targetSlot == Item.SLOT_R_EAR) || (targetSlot == Item.SLOT_LR_EAR))
		{
			if (_paperdoll[PAPERDOLL_LEAR] == null)
			{
				setPaperdollItem(PAPERDOLL_LEAR, item);
			}
			else if (_paperdoll[PAPERDOLL_REAR] == null)
			{
				setPaperdollItem(PAPERDOLL_REAR, item);
			}
			else
			{
				setPaperdollItem(PAPERDOLL_LEAR, item);
			}
		}
		else if ((targetSlot == Item.SLOT_L_FINGER) || (targetSlot == Item.SLOT_R_FINGER) || (targetSlot == Item.SLOT_LR_FINGER))
		{
			if (_paperdoll[PAPERDOLL_LFINGER] == null)
			{
				setPaperdollItem(PAPERDOLL_LFINGER, item);
			}
			else if (_paperdoll[PAPERDOLL_RFINGER] == null)
			{
				setPaperdollItem(PAPERDOLL_RFINGER, item);
			}
			else
			{
				setPaperdollItem(PAPERDOLL_LFINGER, item);
			}
		}
		else if (targetSlot == Item.SLOT_NECK)
		{
			setPaperdollItem(PAPERDOLL_NECK, item);
		}
		else if (targetSlot == Item.SLOT_FULL_ARMOR)
		{
			setPaperdollItem(PAPERDOLL_LEGS, null);
			setPaperdollItem(PAPERDOLL_CHEST, item);
		}
		else if (targetSlot == Item.SLOT_CHEST)
		{
			setPaperdollItem(PAPERDOLL_CHEST, item);
		}
		else if (targetSlot == Item.SLOT_LEGS)
		{
			final ItemInstance chest = getPaperdollItem(PAPERDOLL_CHEST);
			if ((chest != null) && (chest.getItem().getBodyPart() == Item.SLOT_FULL_ARMOR))
			{
				setPaperdollItem(PAPERDOLL_CHEST, null);
			}
			setPaperdollItem(PAPERDOLL_LEGS, item);
		}
		else if (targetSlot == Item.SLOT_FEET)
		{
			setPaperdollItem(PAPERDOLL_FEET, item);
		}
		else if (targetSlot == Item.SLOT_GLOVES)
		{
			setPaperdollItem(PAPERDOLL_GLOVES, item);
		}
		else if (targetSlot == Item.SLOT_HEAD)
		{
			setPaperdollItem(PAPERDOLL_HEAD, item);
		}
		else if (targetSlot == Item.SLOT_HAIR)
		{
			final ItemInstance hair = getPaperdollItem(PAPERDOLL_HAIR);
			if ((hair != null) && (hair.getItem().getBodyPart() == Item.SLOT_HAIRALL))
			{
				setPaperdollItem(PAPERDOLL_HAIR2, null);
			}
			else
			{
				setPaperdollItem(PAPERDOLL_HAIR, null);
			}
			setPaperdollItem(PAPERDOLL_HAIR, item);
		}
		else if (targetSlot == Item.SLOT_HAIR2)
		{
			final ItemInstance hair2 = getPaperdollItem(PAPERDOLL_HAIR);
			if ((hair2 != null) && (hair2.getItem().getBodyPart() == Item.SLOT_HAIRALL))
			{
				setPaperdollItem(PAPERDOLL_HAIR, null);
			}
			else
			{
				setPaperdollItem(PAPERDOLL_HAIR2, null);
			}
			setPaperdollItem(PAPERDOLL_HAIR2, item);
		}
		else if (targetSlot == Item.SLOT_HAIRALL)
		{
			setPaperdollItem(PAPERDOLL_HAIR2, null);
			setPaperdollItem(PAPERDOLL_HAIR, item);
		}
		else if (targetSlot == Item.SLOT_UNDERWEAR)
		{
			setPaperdollItem(PAPERDOLL_UNDER, item);
		}
		else if (targetSlot == Item.SLOT_BACK)
		{
			setPaperdollItem(PAPERDOLL_CLOAK, item);
		}
		else if (targetSlot == Item.SLOT_L_BRACELET)
		{
			setPaperdollItem(PAPERDOLL_LBRACELET, item);
		}
		else if (targetSlot == Item.SLOT_R_BRACELET)
		{
			setPaperdollItem(PAPERDOLL_RBRACELET, item);
		}
		else if (targetSlot == Item.SLOT_DECO)
		{
			equipTalisman(item);
		}
		else if (targetSlot == Item.SLOT_BELT)
		{
			setPaperdollItem(PAPERDOLL_BELT, item);
		}
		else if (targetSlot == Item.SLOT_ALLDRESS)
		{
			setPaperdollItem(PAPERDOLL_LEGS, null);
			setPaperdollItem(PAPERDOLL_LHAND, null);
			setPaperdollItem(PAPERDOLL_RHAND, null);
			setPaperdollItem(PAPERDOLL_HEAD, null);
			setPaperdollItem(PAPERDOLL_FEET, null);
			setPaperdollItem(PAPERDOLL_GLOVES, null);
			setPaperdollItem(PAPERDOLL_CHEST, item);
		}
		else if (targetSlot == Item.SLOT_BROOCH)
		{
			setPaperdollItem(PAPERDOLL_BROOCH, item);
		}
		else if (targetSlot == Item.SLOT_BROOCH_JEWEL)
		{
			equipBroochJewel(item);
		}
		else if (targetSlot == Item.SLOT_AGATHION)
		{
			equipAgathion(item);
		}
		else if (targetSlot == Item.SLOT_ARTIFACT_BOOK)
		{
			setPaperdollItem(PAPERDOLL_ARTIFACT_BOOK, item);
		}
		else if (targetSlot == Item.SLOT_ARTIFACT)
		{
			equipArtifact(item);
		}
		else
		{
			LOGGER.warning("Unknown body slot " + targetSlot + " for Item ID: " + item.getId());
		}
	}
	
	/**
	 * Refresh the weight of equipment loaded
	 */
	@Override
	protected void refreshWeight()
	{
		long weight = 0;
		
		for (ItemInstance item : _items.values())
		{
			if ((item != null) && (item.getItem() != null))
			{
				weight += item.getItem().getWeight() * item.getCount();
			}
		}
		_totalWeight = (int) Math.min(weight, Integer.MAX_VALUE);
	}
	
	/**
	 * @return the totalWeight.
	 */
	public int getTotalWeight()
	{
		return _totalWeight;
	}
	
	/**
	 * Return the ItemInstance of the arrows needed for this bow.
	 * @param bow : Item designating the bow
	 * @return ItemInstance pointing out arrows for bow
	 */
	public ItemInstance findArrowForBow(Item bow)
	{
		if (bow == null)
		{
			return null;
		}
		
		ItemInstance arrow = null;
		
		for (ItemInstance item : getItems())
		{
			if (item.isEtcItem() && (item.getItem().getCrystalTypePlus() == bow.getCrystalTypePlus()) && (item.getEtcItem().getItemType() == EtcItemType.ARROW))
			{
				arrow = item;
				break;
			}
		}
		
		// Get the ItemInstance corresponding to the item identifier and return it
		return arrow;
	}
	
	/**
	 * Return the ItemInstance of the bolts needed for this crossbow.
	 * @param crossbow : Item designating the crossbow
	 * @return ItemInstance pointing out bolts for crossbow
	 */
	public ItemInstance findBoltForCrossBow(Item crossbow)
	{
		ItemInstance bolt = null;
		
		for (ItemInstance item : getItems())
		{
			if (item.isEtcItem() && (item.getItem().getCrystalTypePlus() == crossbow.getCrystalTypePlus()) && (item.getEtcItem().getItemType() == EtcItemType.BOLT))
			{
				bolt = item;
				break;
			}
		}
		
		// Get the ItemInstance corresponding to the item identifier and return it
		return bolt;
	}
	
	/**
	 * Get back items in inventory from database
	 */
	@Override
	public void restore()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM items WHERE owner_id=? AND (loc=? OR loc=?) ORDER BY loc_data"))
		{
			ps.setInt(1, getOwnerId());
			ps.setString(2, getBaseLocation().name());
			ps.setString(3, getEquipLocation().name());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					try
					{
						final ItemInstance item = new ItemInstance(rs);
						if (getOwner().isPlayer())
						{
							final PlayerInstance player = (PlayerInstance) getOwner();
							
							if (!player.canOverrideCond(PlayerCondOverride.ITEM_CONDITIONS) && !player.isHero() && item.isHeroItem())
							{
								item.setItemLocation(ItemLocation.INVENTORY);
							}
						}
						
						World.getInstance().addObject(item);
						
						// If stackable item is found in inventory just add to current quantity
						if (item.isStackable() && (getItemByItemId(item.getId()) != null))
						{
							addItem("Restore", item, getOwner().getActingPlayer(), null);
						}
						else
						{
							addItem(item);
						}
					}
					catch (Exception e)
					{
						LOGGER.warning("Could not restore item " + rs.getInt("item_id") + " for " + getOwner());
					}
				}
			}
			refreshWeight();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not restore inventory: " + e.getMessage(), e);
		}
	}
	
	public int getTalismanSlots()
	{
		return getOwner().getActingPlayer().getStat().getTalismanSlots();
	}
	
	private void equipTalisman(ItemInstance item)
	{
		if (getTalismanSlots() == 0)
		{
			return;
		}
		
		// find same (or incompatible) talisman type
		for (int i = PAPERDOLL_DECO1; i < (PAPERDOLL_DECO1 + getTalismanSlots()); i++)
		{
			if (_paperdoll[i] != null)
			{
				if (getPaperdollItemId(i) == item.getId())
				{
					// overwrite
					setPaperdollItem(i, item);
					return;
				}
			}
		}
		
		// no free slot found - put on first free
		for (int i = PAPERDOLL_DECO1; i < (PAPERDOLL_DECO1 + getTalismanSlots()); i++)
		{
			if (_paperdoll[i] == null)
			{
				setPaperdollItem(i, item);
				return;
			}
		}
		
		// no free slots - put on first
		setPaperdollItem(PAPERDOLL_DECO1, item);
	}
	
	public int getArtifactSlots()
	{
		return getOwner().getActingPlayer().getStat().getArtifactSlots();
	}
	
	private void equipArtifact(ItemInstance item)
	{
		if (getArtifactSlots() == 0)
		{
			return;
		}
		
		switch (getArtifactSlots())
		{
			case 1:
			{
				// 4 Balance Artifact Equip
				if ((item.getId() >= 48969) && (item.getId() <= 48985))
				{
					for (int i = PAPERDOLL_ARTIFACT1; i < (PAPERDOLL_ARTIFACT1 + 4); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
				}
				
				// 1 Spirit Artifact Equip
				if ((item.getId() >= 48957) && (item.getId() <= 48960))
				{
					for (int i = PAPERDOLL_ARTIFACT13; i < (PAPERDOLL_ARTIFACT13 + 1); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
					
				}
				// 1 Protection Artifact Equip
				if ((item.getId() >= 48961) && (item.getId() <= 48964))
				{
					for (int i = PAPERDOLL_ARTIFACT16; i < (PAPERDOLL_ARTIFACT16 + 1); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
					
				}
				// 1 Support Artifact Equip
				if ((item.getId() >= 48965) && (item.getId() <= 48968))
				{
					for (int i = PAPERDOLL_ARTIFACT19; i < (PAPERDOLL_ARTIFACT19 + 1); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
					
				}
				break;
			}
			case 2:
			{
				// 8 Balance Artifact Equip
				if ((item.getId() >= 48969) && (item.getId() <= 48985))
				{
					for (int i = PAPERDOLL_ARTIFACT1; i < (PAPERDOLL_ARTIFACT1 + 8); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
				}
				
				// 2 Spirit Artifact Equip
				if ((item.getId() >= 48957) && (item.getId() <= 48960))
				{
					for (int i = PAPERDOLL_ARTIFACT13; i < (PAPERDOLL_ARTIFACT13 + 2); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
					
				}
				// 2 Protection Artifact Equip
				if ((item.getId() >= 48961) && (item.getId() <= 48964))
				{
					for (int i = PAPERDOLL_ARTIFACT16; i < (PAPERDOLL_ARTIFACT16 + 2); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
					
				}
				// 2 Support Artifact Equip
				if ((item.getId() >= 48965) && (item.getId() <= 48968))
				{
					for (int i = PAPERDOLL_ARTIFACT19; i < (PAPERDOLL_ARTIFACT19 + 2); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
					
				}
				break;
			}
			case 3:
			{
				// 12 Balance Artifact Equip
				if ((item.getId() >= 48969) && (item.getId() <= 48985))
				{
					for (int i = PAPERDOLL_ARTIFACT1; i < (PAPERDOLL_ARTIFACT1 + 12); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
				}
				
				// 3 Spirit Artifact Equip
				if ((item.getId() >= 48957) && (item.getId() <= 48960))
				{
					for (int i = PAPERDOLL_ARTIFACT13; i < (PAPERDOLL_ARTIFACT13 + 3); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
					
				}
				// 3 Protection Artifact Equip
				if ((item.getId() >= 48961) && (item.getId() <= 48964))
				{
					for (int i = PAPERDOLL_ARTIFACT16; i < (PAPERDOLL_ARTIFACT16 + 3); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
					
				}
				// 3 Support Artifact Equip
				if ((item.getId() >= 48965) && (item.getId() <= 48968))
				{
					for (int i = PAPERDOLL_ARTIFACT19; i < (PAPERDOLL_ARTIFACT19 + 3); i++)
					{
						if (_paperdoll[i] == null)
						{
							setPaperdollItem(i, item);
							return;
						}
					}
					
				}
				break;
			}
		}
		
	}
	
	public int getBroochJewelSlots()
	{
		return getOwner().getActingPlayer().getStat().getBroochJewelSlots();
	}
	
	private void equipBroochJewel(ItemInstance item)
	{
		if (getBroochJewelSlots() == 0)
		{
			return;
		}
		
		// find same (or incompatible) brooch jewel type
		for (int i = PAPERDOLL_BROOCH_JEWEL1; i < (PAPERDOLL_BROOCH_JEWEL1 + getBroochJewelSlots()); i++)
		{
			if ((_paperdoll[i] != null) && (getPaperdollItemId(i) == item.getId()))
			{
				// overwrite
				setPaperdollItem(i, item);
				return;
			}
		}
		
		// no free slot found - put on first free
		for (int i = PAPERDOLL_BROOCH_JEWEL1; i < (PAPERDOLL_BROOCH_JEWEL1 + getBroochJewelSlots()); i++)
		{
			if (_paperdoll[i] == null)
			{
				setPaperdollItem(i, item);
				return;
			}
		}
		
		// no free slots - put on first
		setPaperdollItem(PAPERDOLL_BROOCH_JEWEL1, item);
	}
	
	public int getAgathionSlots()
	{
		return getOwner().getActingPlayer().getStat().getAgathionSlots();
	}
	
	private void equipAgathion(ItemInstance item)
	{
		if (getAgathionSlots() == 0)
		{
			return;
		}
		
		// find same (or incompatible) agathion type
		for (int i = PAPERDOLL_AGATHION1; i < (PAPERDOLL_AGATHION1 + getAgathionSlots()); i++)
		{
			if ((_paperdoll[i] != null) && (getPaperdollItemId(i) == item.getId()))
			{
				// overwrite
				setPaperdollItem(i, item);
				return;
			}
		}
		
		// no free slot found - put on first free
		for (int i = PAPERDOLL_AGATHION1; i < (PAPERDOLL_AGATHION1 + getAgathionSlots()); i++)
		{
			if (_paperdoll[i] == null)
			{
				setPaperdollItem(i, item);
				return;
			}
		}
		
		// no free slots - put on first
		setPaperdollItem(PAPERDOLL_AGATHION1, item);
	}
	
	public boolean canEquipCloak()
	{
		return getOwner().getActingPlayer().getStat().canEquipCloak();
	}
	
	/**
	 * Re-notify to paperdoll listeners every equipped item
	 */
	public void reloadEquippedItems()
	{
		int slot;
		
		for (ItemInstance item : _paperdoll)
		{
			if (item == null)
			{
				continue;
			}
			
			slot = item.getLocationSlot();
			
			for (PaperdollListener listener : _paperdollListeners)
			{
				if (listener == null)
				{
					continue;
				}
				
				listener.notifyUnequiped(slot, item, this);
				listener.notifyEquiped(slot, item, this);
			}
		}
		if (getOwner().isPlayer())
		{
			getOwner().sendPacket(new ExUserInfoEquipSlot(getOwner().getActingPlayer()));
		}
	}
	
	public int getArmorMinEnchant()
	{
		if ((getOwner() == null) || !getOwner().isPlayer())
		{
			return 0;
		}
		
		final PlayerInstance player = getOwner().getActingPlayer();
		int maxSetEnchant = 0;
		for (ItemInstance item : getPaperdollItems())
		{
			for (ArmorSet set : ArmorSetsData.getInstance().getSets(item.getId()))
			{
				final int enchantEffect = set.getLowestSetEnchant(player);
				if (enchantEffect > maxSetEnchant)
				{
					maxSetEnchant = enchantEffect;
				}
			}
		}
		return maxSetEnchant;
	}
	
	public int getWeaponEnchant()
	{
		final ItemInstance item = getPaperdollItem(PAPERDOLL_RHAND);
		return item != null ? item.getEnchantLevel() : 0;
	}
	
	/**
	 * Blocks the given item slot from being equipped.
	 * @param itemSlot mask from Item
	 */
	public void blockItemSlot(long itemSlot)
	{
		_blockedItemSlotsMask |= itemSlot;
	}
	
	/**
	 * Unblocks the given item slot so it can be equipped.
	 * @param itemSlot mask from Item
	 */
	public void unblockItemSlot(long itemSlot)
	{
		_blockedItemSlotsMask &= ~itemSlot;
	}
	
	/**
	 * @param itemSlot mask from Item
	 * @return if the given item slot is blocked or not.
	 */
	public boolean isItemSlotBlocked(long itemSlot)
	{
		return (_blockedItemSlotsMask & itemSlot) == itemSlot;
	}
	
	/**
	 * @param itemSlotsMask use 0 to unset all blocked item slots.
	 */
	public void setBlockedItemSlotsMask(int itemSlotsMask)
	{
		_blockedItemSlotsMask = itemSlotsMask;
	}
	
	/**
	 * Reduce the arrow number of the Creature.<br>
	 * <B><U> Overridden in </U> :</B>
	 * <li>PlayerInstance</li>
	 * @param type
	 */
	public void reduceArrowCount(EtcItemType type)
	{
		// default is to do nothing
	}
	
	/**
	 * Gets the items in paperdoll slots filtered by filter.
	 * @param filters multiple filters
	 * @return the filtered items in inventory
	 */
	@SafeVarargs
	public final Collection<ItemInstance> getPaperdollItems(Predicate<ItemInstance>... filters)
	{
		Predicate<ItemInstance> filter = Objects::nonNull;
		for (Predicate<ItemInstance> additionalFilter : filters)
		{
			filter = filter.and(additionalFilter);
		}
		return Arrays.stream(_paperdoll).filter(filter).collect(Collectors.toCollection(LinkedList::new));
	}
}
