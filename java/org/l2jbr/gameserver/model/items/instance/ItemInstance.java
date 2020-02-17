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
package org.l2jbr.gameserver.model.items.instance;

import static org.l2jbr.gameserver.model.itemcontainer.Inventory.ADENA_ID;
import static org.l2jbr.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.data.xml.impl.AppearanceItemData;
import org.l2jbr.gameserver.data.xml.impl.EnchantItemOptionsData;
import org.l2jbr.gameserver.data.xml.impl.EnsoulData;
import org.l2jbr.gameserver.data.xml.impl.OptionData;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.enums.AttributeType;
import org.l2jbr.gameserver.enums.InstanceType;
import org.l2jbr.gameserver.enums.ItemLocation;
import org.l2jbr.gameserver.enums.ItemSkillType;
import org.l2jbr.gameserver.enums.UserInfoType;
import org.l2jbr.gameserver.geoengine.GeoEngine;
import org.l2jbr.gameserver.idfactory.IdFactory;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2jbr.gameserver.instancemanager.SiegeGuardManager;
import org.l2jbr.gameserver.model.DropProtection;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.VariationInstance;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.WorldRegion;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Summon;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.conditions.Condition;
import org.l2jbr.gameserver.model.ensoul.EnsoulOption;
import org.l2jbr.gameserver.model.entity.Castle;
import org.l2jbr.gameserver.model.events.EventDispatcher;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerAugment;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerItemDrop;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerItemPickup;
import org.l2jbr.gameserver.model.events.impl.item.OnItemAttributeAdd;
import org.l2jbr.gameserver.model.events.impl.item.OnItemBypassEvent;
import org.l2jbr.gameserver.model.events.impl.item.OnItemEnchantAdd;
import org.l2jbr.gameserver.model.events.impl.item.OnItemSoulCrystalAdd;
import org.l2jbr.gameserver.model.events.impl.item.OnItemTalk;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.items.Armor;
import org.l2jbr.gameserver.model.items.EtcItem;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.Weapon;
import org.l2jbr.gameserver.model.items.appearance.AppearanceStone;
import org.l2jbr.gameserver.model.items.enchant.attribute.AttributeHolder;
import org.l2jbr.gameserver.model.items.type.EtcItemType;
import org.l2jbr.gameserver.model.items.type.ItemType;
import org.l2jbr.gameserver.model.options.EnchantOptions;
import org.l2jbr.gameserver.model.options.Options;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.variables.ItemVariables;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.DropItem;
import org.l2jbr.gameserver.network.serverpackets.GetItem;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.SpawnItem;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.util.GMAudit;

/**
 * This class manages items.
 * @version $Revision: 1.4.2.1.2.11 $ $Date: 2005/03/31 16:07:50 $
 */
public class ItemInstance extends WorldObject
{
	private static final Logger LOGGER = Logger.getLogger(ItemInstance.class.getName());
	private static final Logger LOG_ITEMS = Logger.getLogger("item");
	
	/** ID of the owner */
	private int _ownerId;
	
	/** ID of who dropped the item last, used for knownlist */
	private int _dropperObjectId = 0;
	
	/** Quantity of the item */
	private long _count = 1;
	/** Initial Quantity of the item */
	private long _initCount;
	/** Remaining time (in miliseconds) */
	private long _time;
	/** Quantity of the item can decrease */
	private boolean _decrease = false;
	
	/** ID of the item */
	private final int _itemId;
	
	/** Object Item associated to the item */
	private final Item _item;
	
	/** Location of the item : Inventory, PaperDoll, WareHouse */
	private ItemLocation _loc;
	
	/** Slot where item is stored : Paperdoll slot, inventory order ... */
	private int _locData;
	
	/** Level of enchantment of the item */
	private int _enchantLevel;
	
	/** Wear Item */
	private boolean _wear;
	
	/** Augmented Item */
	private VariationInstance _augmentation = null;
	
	/** Shadow item */
	private int _mana = -1;
	private boolean _consumingMana = false;
	private static final int MANA_CONSUMPTION_RATE = 60000;
	
	/** Custom item types (used loto, race tickets) */
	private int _type1;
	private int _type2;
	
	private long _dropTime;
	
	private boolean _published = false;
	
	private boolean _protected;
	
	public static final int UNCHANGED = 0;
	public static final int ADDED = 1;
	public static final int REMOVED = 3;
	public static final int MODIFIED = 2;
	
	//@formatter:off
	public static final int[] DEFAULT_ENCHANT_OPTIONS = new int[] { 0, 0, 0 };
	//@formatter:on
	
	private int _lastChange = 2; // 1 ??, 2 modified, 3 removed
	private boolean _existsInDb; // if a record exists in DB.
	private boolean _storedInDb; // if DB data is up-to-date.
	
	private final ReentrantLock _dbLock = new ReentrantLock();
	
	private Map<AttributeType, AttributeHolder> _elementals = null;
	
	private ScheduledFuture<?> itemLootShedule = null;
	private ScheduledFuture<?> _lifeTimeTask;
	private ScheduledFuture<?> _appearanceLifeTimeTask;
	
	private final DropProtection _dropProtection = new DropProtection();
	
	private final List<Options> _enchantOptions = new ArrayList<>();
	private final EnsoulOption[] _ensoulOptions = new EnsoulOption[2];
	private final EnsoulOption[] _ensoulSpecialOptions = new EnsoulOption[1];
	
	/**
	 * Constructor of the ItemInstance from the objectId and the itemId.
	 * @param objectId : int designating the ID of the object in the world
	 * @param itemId : int designating the ID of the item
	 */
	public ItemInstance(int objectId, int itemId)
	{
		super(objectId);
		setInstanceType(InstanceType.ItemInstance);
		_itemId = itemId;
		_item = ItemTable.getInstance().getTemplate(itemId);
		if ((_itemId == 0) || (_item == null))
		{
			throw new IllegalArgumentException();
		}
		super.setName(_item.getName());
		_loc = ItemLocation.VOID;
		_type1 = 0;
		_type2 = 0;
		_dropTime = 0;
		_mana = _item.getDuration();
		_time = _item.getTime() == -1 ? -1 : System.currentTimeMillis() + (_item.getTime() * 60 * 1000);
		scheduleLifeTimeTask();
		scheduleVisualLifeTime();
	}
	
	/**
	 * Constructor of the ItemInstance from the objetId and the description of the item given by the Item.
	 * @param objectId : int designating the ID of the object in the world
	 * @param item : Item containing informations of the item
	 */
	public ItemInstance(int objectId, Item item)
	{
		super(objectId);
		setInstanceType(InstanceType.ItemInstance);
		_itemId = item.getId();
		_item = item;
		if (_itemId == 0)
		{
			throw new IllegalArgumentException();
		}
		super.setName(_item.getName());
		_loc = ItemLocation.VOID;
		_mana = _item.getDuration();
		_time = _item.getTime() == -1 ? -1 : System.currentTimeMillis() + (_item.getTime() * 60 * 1000);
		scheduleLifeTimeTask();
		scheduleVisualLifeTime();
	}
	
	/**
	 * @param rs
	 * @throws SQLException
	 */
	public ItemInstance(ResultSet rs) throws SQLException
	{
		this(rs.getInt("object_id"), ItemTable.getInstance().getTemplate(rs.getInt("item_id")));
		_count = rs.getLong("count");
		_ownerId = rs.getInt("owner_id");
		_loc = ItemLocation.valueOf(rs.getString("loc"));
		_locData = rs.getInt("loc_data");
		_enchantLevel = rs.getInt("enchant_level");
		_type1 = rs.getInt("custom_type1");
		_type2 = rs.getInt("custom_type2");
		_mana = rs.getInt("mana_left");
		_time = rs.getLong("time");
		_existsInDb = true;
		_storedInDb = true;
		
		if (isEquipable())
		{
			restoreAttributes();
			restoreSpecialAbilities();
		}
	}
	
	/**
	 * Constructor overload.<br>
	 * Sets the next free object ID in the ID factory.
	 * @param itemId the item template ID
	 */
	public ItemInstance(int itemId)
	{
		this(IdFactory.getInstance().getNextId(), itemId);
	}
	
	/**
	 * Remove a ItemInstance from the world and send server->client GetItem packets.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Send a Server->Client Packet GetItem to player that pick up and its _knowPlayers member</li>
	 * <li>Remove the WorldObject from the world</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of World </B></FONT><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Do Pickup Item : PCInstance and Pet</li><BR>
	 * <BR>
	 * @param creature Character that pick up the item
	 */
	public void pickupMe(Creature creature)
	{
		final WorldRegion oldregion = getWorldRegion();
		
		// Create a server->client GetItem packet to pick up the ItemInstance
		creature.broadcastPacket(new GetItem(this, creature.getObjectId()));
		
		synchronized (this)
		{
			setSpawned(false);
		}
		
		// if this item is a mercenary ticket, remove the spawns!
		final Castle castle = CastleManager.getInstance().getCastle(this);
		if ((castle != null) && (SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getResidenceId(), getId()) != null))
		{
			SiegeGuardManager.getInstance().removeTicket(this);
			ItemsOnGroundManager.getInstance().removeObject(this);
		}
		
		// outside of synchronized to avoid deadlocks
		// Remove the ItemInstance from the world
		World.getInstance().removeVisibleObject(this, oldregion);
		
		if (creature.isPlayer())
		{
			// Notify to scripts
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemPickup(creature.getActingPlayer(), this), getItem());
		}
	}
	
	/**
	 * Sets the ownerID of the item
	 * @param process : String Identifier of process triggering this action
	 * @param owner_id : int designating the ID of the owner
	 * @param creator : PlayerInstance Player requesting the item creation
	 * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void setOwnerId(String process, int owner_id, PlayerInstance creator, Object reference)
	{
		setOwnerId(owner_id);
		
		if (Config.LOG_ITEMS)
		{
			if (!Config.LOG_ITEMS_SMALL_LOG || (Config.LOG_ITEMS_SMALL_LOG && (_item.isEquipable() || (_item.getId() == ADENA_ID))))
			{
				if (_enchantLevel > 0)
				{
					LOG_ITEMS.info("SETOWNER:" + String.valueOf(process) // in case of null
						+ ", item " + getObjectId() //
						+ ":+" + _enchantLevel //
						+ " " + _item.getName() //
						+ "(" + _count + "), " //
						+ String.valueOf(creator) + ", " // in case of null
						+ String.valueOf(reference)); // in case of null
				}
				else
				{
					LOG_ITEMS.info("SETOWNER:" + String.valueOf(process) // in case of null
						+ ", item " + getObjectId() //
						+ ":" + _item.getName() //
						+ "(" + _count + "), " //
						+ String.valueOf(creator) + ", " // in case of null
						+ String.valueOf(reference)); // in case of null
				}
			}
		}
		
		if ((creator != null) && creator.isGM())
		{
			String referenceName = "no-reference";
			if (reference instanceof WorldObject)
			{
				referenceName = (((WorldObject) reference).getName() != null ? ((WorldObject) reference).getName() : "no-name");
			}
			else if (reference instanceof String)
			{
				referenceName = (String) reference;
			}
			final String targetName = (creator.getTarget() != null ? creator.getTarget().getName() : "no-target");
			if (Config.GMAUDIT)
			{
				GMAudit.auditGMAction(creator.getName() + " [" + creator.getObjectId() + "]", process + "(id: " + _itemId + " name: " + getName() + ")", targetName, "Object referencing this action is: " + referenceName);
			}
		}
	}
	
	/**
	 * Sets the ownerID of the item
	 * @param owner_id : int designating the ID of the owner
	 */
	public void setOwnerId(int owner_id)
	{
		if (owner_id == _ownerId)
		{
			return;
		}
		
		// Remove any inventory skills from the old owner.
		removeSkillsFromOwner();
		
		_ownerId = owner_id;
		_storedInDb = false;
		
		// Give any inventory skills to the new owner only if the item is in inventory
		// else the skills will be given when location is set to inventory.
		giveSkillsToOwner();
	}
	
	/**
	 * Returns the ownerID of the item
	 * @return int : ownerID of the item
	 */
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	/**
	 * Sets the location of the item
	 * @param loc : ItemLocation (enumeration)
	 */
	public void setItemLocation(ItemLocation loc)
	{
		setItemLocation(loc, 0);
	}
	
	/**
	 * Sets the location of the item.<BR>
	 * <BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param loc : ItemLocation (enumeration)
	 * @param loc_data : int designating the slot where the item is stored or the village for freights
	 */
	public void setItemLocation(ItemLocation loc, int loc_data)
	{
		if ((loc == _loc) && (loc_data == _locData))
		{
			return;
		}
		
		// Remove any inventory skills from the old owner.
		removeSkillsFromOwner();
		
		_loc = loc;
		_locData = loc_data;
		_storedInDb = false;
		
		// Give any inventory skills to the new owner only if the item is in inventory
		// else the skills will be given when location is set to inventory.
		giveSkillsToOwner();
	}
	
	public ItemLocation getItemLocation()
	{
		return _loc;
	}
	
	/**
	 * Sets the quantity of the item.<BR>
	 * <BR>
	 * @param count the new count to set
	 */
	public void setCount(long count)
	{
		if (_count == count)
		{
			return;
		}
		
		_count = count >= -1 ? count : 0;
		_storedInDb = false;
	}
	
	/**
	 * @return Returns the count.
	 */
	public long getCount()
	{
		return _count;
	}
	
	/**
	 * Sets the quantity of the item.<BR>
	 * <BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param process : String Identifier of process triggering this action
	 * @param count : int
	 * @param creator : PlayerInstance Player requesting the item creation
	 * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void changeCount(String process, long count, PlayerInstance creator, Object reference)
	{
		if (count == 0)
		{
			return;
		}
		final long old = _count;
		final long max = _itemId == ADENA_ID ? MAX_ADENA : Integer.MAX_VALUE;
		
		if ((count > 0) && (_count > (max - count)))
		{
			setCount(max);
		}
		else
		{
			setCount(_count + count);
		}
		
		if (_count < 0)
		{
			setCount(0);
		}
		
		_storedInDb = false;
		
		if (Config.LOG_ITEMS && (process != null))
		{
			if (!Config.LOG_ITEMS_SMALL_LOG || (Config.LOG_ITEMS_SMALL_LOG && (_item.isEquipable() || (_item.getId() == ADENA_ID))))
			{
				if (_enchantLevel > 0)
				{
					LOG_ITEMS.info("CHANGE:" + String.valueOf(process) // in case of null
						+ ", item " + getObjectId() //
						+ ":+" + _enchantLevel //
						+ " " + _item.getName() //
						+ "(" + _count + "), PrevCount(" //
						+ String.valueOf(old) + "), " // in case of null
						+ String.valueOf(creator) + ", " // in case of null
						+ String.valueOf(reference)); // in case of null
				}
				else
				{
					LOG_ITEMS.info("CHANGE:" + String.valueOf(process) // in case of null
						+ ", item " + getObjectId() //
						+ ":" + _item.getName() //
						+ "(" + _count + "), PrevCount(" //
						+ String.valueOf(old) + "), " // in case of null
						+ String.valueOf(creator) + ", " // in case of null
						+ String.valueOf(reference)); // in case of null
				}
			}
		}
		
		if ((creator != null) && creator.isGM())
		{
			String referenceName = "no-reference";
			if (reference instanceof WorldObject)
			{
				referenceName = (((WorldObject) reference).getName() != null ? ((WorldObject) reference).getName() : "no-name");
			}
			else if (reference instanceof String)
			{
				referenceName = (String) reference;
			}
			final String targetName = (creator.getTarget() != null ? creator.getTarget().getName() : "no-target");
			if (Config.GMAUDIT)
			{
				GMAudit.auditGMAction(creator.getName() + " [" + creator.getObjectId() + "]", process + "(id: " + _itemId + " objId: " + getObjectId() + " name: " + getName() + " count: " + count + ")", targetName, "Object referencing this action is: " + referenceName);
			}
		}
	}
	
	// No logging (function designed for shots only)
	public void changeCountWithoutTrace(int count, PlayerInstance creator, Object reference)
	{
		changeCount(null, count, creator, reference);
	}
	
	/**
	 * Return true if item can be enchanted
	 * @return boolean
	 */
	public int isEnchantable()
	{
		if ((_loc == ItemLocation.INVENTORY) || (_loc == ItemLocation.PAPERDOLL))
		{
			return _item.isEnchantable();
		}
		return 0;
	}
	
	/**
	 * Returns if item is equipable
	 * @return boolean
	 */
	public boolean isEquipable()
	{
		return _item.getBodyPart() != Item.SLOT_NONE;
	}
	
	/**
	 * Returns if item is equipped
	 * @return boolean
	 */
	public boolean isEquipped()
	{
		return (_loc == ItemLocation.PAPERDOLL) || (_loc == ItemLocation.PET_EQUIP);
	}
	
	/**
	 * Returns the slot where the item is stored
	 * @return int
	 */
	public int getLocationSlot()
	{
		return _locData;
	}
	
	/**
	 * Returns the characteristics of the item
	 * @return Item
	 */
	public Item getItem()
	{
		return _item;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public void setCustomType1(int newtype)
	{
		_type1 = newtype;
	}
	
	public void setCustomType2(int newtype)
	{
		_type2 = newtype;
	}
	
	public void setDropTime(long time)
	{
		_dropTime = time;
	}
	
	public long getDropTime()
	{
		return _dropTime;
	}
	
	/**
	 * @return the type of item.
	 */
	public ItemType getItemType()
	{
		return _item.getItemType();
	}
	
	/**
	 * Gets the item ID.
	 * @return the item ID
	 */
	@Override
	public int getId()
	{
		return _itemId;
	}
	
	/**
	 * @return the display Id of the item.
	 */
	public int getDisplayId()
	{
		return _item.getDisplayId();
	}
	
	/**
	 * @return {@code true} if item is an EtcItem, {@code false} otherwise.
	 */
	public boolean isEtcItem()
	{
		return (_item instanceof EtcItem);
	}
	
	/**
	 * @return {@code true} if item is a Weapon/Shield, {@code false} otherwise.
	 */
	public boolean isWeapon()
	{
		return (_item instanceof Weapon);
	}
	
	/**
	 * @return {@code true} if item is an Armor, {@code false} otherwise.
	 */
	public boolean isArmor()
	{
		return (_item instanceof Armor);
	}
	
	/**
	 * @return the characteristics of the EtcItem, {@code false} otherwise.
	 */
	public EtcItem getEtcItem()
	{
		if (_item instanceof EtcItem)
		{
			return (EtcItem) _item;
		}
		return null;
	}
	
	/**
	 * @return the characteristics of the Weapon.
	 */
	public Weapon getWeaponItem()
	{
		if (_item instanceof Weapon)
		{
			return (Weapon) _item;
		}
		return null;
	}
	
	/**
	 * @return the characteristics of the Armor.
	 */
	public Armor getArmorItem()
	{
		if (_item instanceof Armor)
		{
			return (Armor) _item;
		}
		return null;
	}
	
	/**
	 * @return the quantity of crystals for crystallization.
	 */
	public int getCrystalCount()
	{
		return _item.getCrystalCount(_enchantLevel);
	}
	
	/**
	 * @return the reference price of the item.
	 */
	public long getReferencePrice()
	{
		return _item.getReferencePrice();
	}
	
	/**
	 * @return the name of the item.
	 */
	public String getItemName()
	{
		return _item.getName();
	}
	
	/**
	 * @return the reuse delay of this item.
	 */
	public int getReuseDelay()
	{
		return _item.getReuseDelay();
	}
	
	/**
	 * @return the shared reuse item group.
	 */
	public int getSharedReuseGroup()
	{
		return _item.getSharedReuseGroup();
	}
	
	/**
	 * @return the last change of the item
	 */
	public int getLastChange()
	{
		return _lastChange;
	}
	
	/**
	 * Sets the last change of the item
	 * @param lastChange : int
	 */
	public void setLastChange(int lastChange)
	{
		_lastChange = lastChange;
	}
	
	/**
	 * Returns if item is stackable
	 * @return boolean
	 */
	public boolean isStackable()
	{
		return _item.isStackable();
	}
	
	/**
	 * Returns if item is dropable
	 * @return boolean
	 */
	public boolean isDropable()
	{
		if (Config.ALT_ALLOW_AUGMENT_TRADE && isAugmented())
		{
			return true;
		}
		return !isAugmented() && _item.isDropable();
	}
	
	/**
	 * Returns if item is destroyable
	 * @return boolean
	 */
	public boolean isDestroyable()
	{
		if (!Config.ALT_ALLOW_AUGMENT_DESTROY && isAugmented())
		{
			return false;
		}
		return _item.isDestroyable();
	}
	
	/**
	 * Returns if item is tradeable
	 * @return boolean
	 */
	public boolean isTradeable()
	{
		if (Config.ALT_ALLOW_AUGMENT_TRADE && isAugmented())
		{
			return true;
		}
		return !isAugmented() && _item.isTradeable();
	}
	
	/**
	 * Returns if item is sellable
	 * @return boolean
	 */
	public boolean isSellable()
	{
		if (Config.ALT_ALLOW_AUGMENT_TRADE && isAugmented())
		{
			return true;
		}
		return !isAugmented() && _item.isSellable();
	}
	
	/**
	 * @param isPrivateWareHouse
	 * @return if item can be deposited in warehouse or freight
	 */
	public boolean isDepositable(boolean isPrivateWareHouse)
	{
		// equipped, hero and quest items
		if (isEquipped() || !_item.isDepositable())
		{
			return false;
		}
		if (!isPrivateWareHouse)
		{
			// augmented not tradeable
			if (!isTradeable() || isShadowItem())
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isPotion()
	{
		return _item.isPotion();
	}
	
	public boolean isElixir()
	{
		return _item.isElixir();
	}
	
	public boolean isScroll()
	{
		return _item.isScroll();
	}
	
	public boolean isHeroItem()
	{
		return _item.isHeroItem();
	}
	
	public boolean isCommonItem()
	{
		return _item.isCommon();
	}
	
	/**
	 * Returns whether this item is pvp or not
	 * @return boolean
	 */
	public boolean isPvp()
	{
		return _item.isPvpItem();
	}
	
	public boolean isOlyRestrictedItem()
	{
		return _item.isOlyRestrictedItem();
	}
	
	/**
	 * @param player
	 * @param allowAdena
	 * @param allowNonTradeable
	 * @return if item is available for manipulation
	 */
	public boolean isAvailable(PlayerInstance player, boolean allowAdena, boolean allowNonTradeable)
	{
		final Summon pet = player.getPet();
		
		return ((!isEquipped()) // Not equipped
			&& (_item.getType2() != Item.TYPE2_QUEST) // Not Quest Item
			&& ((_item.getType2() != Item.TYPE2_MONEY) || (_item.getType1() != Item.TYPE1_SHIELD_ARMOR)) // not money, not shield
			&& ((pet == null) || (getObjectId() != pet.getControlObjectId())) // Not Control item of currently summoned pet
			&& !(player.isProcessingItem(getObjectId())) // Not momentarily used enchant scroll
			&& (allowAdena || (_itemId != ADENA_ID)) // Not Adena
			&& (!player.isCastingNow(s -> s.getSkill().getItemConsumeId() != _itemId)) && (allowNonTradeable || (isTradeable() && (!((_item.getItemType() == EtcItemType.PET_COLLAR) && player.havePetInvItems())))));
	}
	
	/**
	 * Returns the level of enchantment of the item
	 * @return int
	 */
	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
	
	/**
	 * @return {@code true} if item is enchanted, {@code false} otherwise
	 */
	public boolean isEnchanted()
	{
		return _enchantLevel > 0;
	}
	
	/**
	 * @param enchantLevel the enchant value to set
	 */
	public void setEnchantLevel(int enchantLevel)
	{
		if (_enchantLevel == enchantLevel)
		{
			return;
		}
		clearEnchantStats();
		_enchantLevel = enchantLevel;
		applyEnchantStats();
		_storedInDb = false;
		
		// Notify to Scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnItemEnchantAdd(getActingPlayer(), this));
	}
	
	/**
	 * Returns whether this item is augmented or not
	 * @return true if augmented
	 */
	public boolean isAugmented()
	{
		return _augmentation != null;
	}
	
	/**
	 * Returns the augmentation object for this item
	 * @return augmentation
	 */
	public VariationInstance getAugmentation()
	{
		return _augmentation;
	}
	
	/**
	 * Sets a new augmentation
	 * @param augmentation
	 * @param updateDatabase
	 * @return return true if successfully
	 */
	public boolean setAugmentation(VariationInstance augmentation, boolean updateDatabase)
	{
		// there shall be no previous augmentation..
		if (_augmentation != null)
		{
			LOGGER.info("Warning: Augment set for (" + getObjectId() + ") " + getName() + " owner: " + _ownerId);
			return false;
		}
		
		_augmentation = augmentation;
		if (updateDatabase)
		{
			updateItemOptions();
		}
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerAugment(getActingPlayer(), this, augmentation, true), getItem());
		return true;
	}
	
	/**
	 * Remove the augmentation
	 */
	public void removeAugmentation()
	{
		if (_augmentation == null)
		{
			return;
		}
		
		// Copy augmentation before removing it.
		final VariationInstance augment = _augmentation;
		_augmentation = null;
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM item_variations WHERE itemId = ?"))
		{
			ps.setInt(1, getObjectId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not remove augmentation for item: " + toString() + " from DB: ", e);
		}
		
		// Notify to scripts.
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerAugment(getActingPlayer(), this, augment, false), getItem());
	}
	
	public void restoreAttributes()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps1 = con.prepareStatement("SELECT mineralId,option1,option2 FROM item_variations WHERE itemId=?");
			PreparedStatement ps2 = con.prepareStatement("SELECT elemType,elemValue FROM item_elementals WHERE itemId=?"))
		{
			ps1.setInt(1, getObjectId());
			try (ResultSet rs = ps1.executeQuery())
			{
				if (rs.next())
				{
					int mineralId = rs.getInt("mineralId");
					int option1 = rs.getInt("option1");
					int option2 = rs.getInt("option2");
					if ((option1 != -1) && (option2 != -1))
					{
						_augmentation = new VariationInstance(mineralId, option1, option2);
					}
				}
			}
			
			ps2.setInt(1, getObjectId());
			try (ResultSet rs = ps2.executeQuery())
			{
				while (rs.next())
				{
					final byte attributeType = rs.getByte(1);
					final int attributeValue = rs.getInt(2);
					if ((attributeType != -1) && (attributeValue != -1))
					{
						applyAttribute(new AttributeHolder(AttributeType.findByClientId(attributeType), attributeValue));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not restore augmentation and elemental data for item " + toString() + " from DB: ", e);
		}
	}
	
	public void updateItemOptions()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			updateItemOptions(con);
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Could not update atributes for item: " + toString() + " from DB:", e);
		}
	}
	
	private void updateItemOptions(Connection con)
	{
		try (PreparedStatement ps = con.prepareStatement("REPLACE INTO item_variations VALUES(?,?,?,?)"))
		{
			ps.setInt(1, getObjectId());
			ps.setInt(2, _augmentation != null ? _augmentation.getMineralId() : 0);
			ps.setInt(3, _augmentation != null ? _augmentation.getOption1Id() : -1);
			ps.setInt(4, _augmentation != null ? _augmentation.getOption2Id() : -1);
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Could not update atributes for item: " + toString() + " from DB: ", e);
		}
	}
	
	public void updateItemElementals()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			updateItemElements(con);
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Could not update elementals for item: " + toString() + " from DB: ", e);
		}
	}
	
	private void updateItemElements(Connection con)
	{
		try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_elementals WHERE itemId = ?"))
		{
			ps.setInt(1, getObjectId());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Could not update elementals for item: " + toString() + " from DB: ", e);
		}
		
		if (_elementals == null)
		{
			return;
		}
		
		try (PreparedStatement ps = con.prepareStatement("INSERT INTO item_elementals VALUES(?,?,?)"))
		{
			for (AttributeHolder attribute : _elementals.values())
			{
				ps.setInt(1, getObjectId());
				ps.setByte(2, attribute.getType().getClientId());
				ps.setInt(3, attribute.getValue());
				ps.executeUpdate();
				ps.clearParameters();
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Could not update elementals for item: " + toString() + " from DB: ", e);
		}
	}
	
	public Collection<AttributeHolder> getAttributes()
	{
		return _elementals != null ? _elementals.values() : null;
	}
	
	public boolean hasAttributes()
	{
		return (_elementals != null) && !_elementals.isEmpty();
	}
	
	public AttributeHolder getAttribute(AttributeType type)
	{
		return _elementals != null ? _elementals.get(type) : null;
	}
	
	public AttributeHolder getAttackAttribute()
	{
		if (isWeapon())
		{
			if (_item.getAttributes() != null)
			{
				return _item.getAttributes().stream().findFirst().orElse(null);
			}
			else if (_elementals != null)
			{
				return _elementals.values().stream().findFirst().orElse(null);
			}
		}
		return null;
	}
	
	public AttributeType getAttackAttributeType()
	{
		final AttributeHolder holder = getAttackAttribute();
		return holder != null ? holder.getType() : AttributeType.NONE;
	}
	
	public int getAttackAttributePower()
	{
		final AttributeHolder holder = getAttackAttribute();
		return holder != null ? holder.getValue() : 0;
	}
	
	public int getDefenceAttribute(AttributeType element)
	{
		if (isArmor())
		{
			if (_item.getAttributes() != null)
			{
				final AttributeHolder attribute = _item.getAttribute(element);
				if (attribute != null)
				{
					return attribute.getValue();
				}
			}
			else if (_elementals != null)
			{
				final AttributeHolder attribute = getAttribute(element);
				if (attribute != null)
				{
					return attribute.getValue();
				}
			}
		}
		return 0;
	}
	
	private synchronized void applyAttribute(AttributeHolder holder)
	{
		if (_elementals == null)
		{
			_elementals = new LinkedHashMap<>(3);
			_elementals.put(holder.getType(), holder);
		}
		else
		{
			final AttributeHolder attribute = getAttribute(holder.getType());
			if (attribute != null)
			{
				attribute.setValue(holder.getValue());
			}
			else
			{
				_elementals.put(holder.getType(), holder);
			}
		}
		
		// Notify to Scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnItemAttributeAdd(getActingPlayer(), this));
	}
	
	/**
	 * Add elemental attribute to item and save to db
	 * @param holder
	 * @param updateDatabase
	 */
	public void setAttribute(AttributeHolder holder, boolean updateDatabase)
	{
		applyAttribute(holder);
		if (updateDatabase)
		{
			updateItemElementals();
		}
	}
	
	/**
	 * Remove elemental from item
	 * @param type byte element to remove
	 */
	public void clearAttribute(AttributeType type)
	{
		if ((_elementals == null) || (getAttribute(type) == null))
		{
			return;
		}
		
		synchronized (_elementals)
		{
			_elementals.remove(type);
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM item_elementals WHERE itemId = ? AND elemType = ?"))
		{
			ps.setInt(1, getObjectId());
			ps.setByte(2, type.getClientId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not remove elemental enchant for item: " + toString() + " from DB: ", e);
		}
	}
	
	public void clearAllAttributes()
	{
		if (_elementals == null)
		{
			return;
		}
		
		synchronized (_elementals)
		{
			_elementals.clear();
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM item_elementals WHERE itemId = ?"))
		{
			ps.setInt(1, getObjectId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not remove all elemental enchant for item: " + toString() + " from DB: ", e);
		}
	}
	
	/**
	 * Used to decrease mana (mana means life time for shadow items)
	 */
	public static class ScheduleConsumeManaTask implements Runnable
	{
		private static final Logger LOGGER = Logger.getLogger(ScheduleConsumeManaTask.class.getName());
		private final ItemInstance _shadowItem;
		
		public ScheduleConsumeManaTask(ItemInstance item)
		{
			_shadowItem = item;
		}
		
		@Override
		public void run()
		{
			try
			{
				// decrease mana
				if (_shadowItem != null)
				{
					_shadowItem.decreaseMana(true);
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "", e);
			}
		}
	}
	
	/**
	 * Returns true if this item is a shadow item Shadow items have a limited life-time
	 * @return
	 */
	public boolean isShadowItem()
	{
		return (_mana >= 0);
	}
	
	/**
	 * Returns the remaining mana of this shadow item
	 * @return lifeTime
	 */
	public int getMana()
	{
		return _mana;
	}
	
	/**
	 * Decreases the mana of this shadow item, sends a inventory update schedules a new consumption task if non is running optionally one could force a new task
	 * @param resetConsumingMana if true forces a new consumption task if item is equipped
	 */
	public void decreaseMana(boolean resetConsumingMana)
	{
		decreaseMana(resetConsumingMana, 1);
	}
	
	/**
	 * Decreases the mana of this shadow item, sends a inventory update schedules a new consumption task if non is running optionally one could force a new task
	 * @param resetConsumingMana if forces a new consumption task if item is equipped
	 * @param count how much mana decrease
	 */
	public void decreaseMana(boolean resetConsumingMana, int count)
	{
		if (!isShadowItem())
		{
			return;
		}
		
		if ((_mana - count) >= 0)
		{
			_mana -= count;
		}
		else
		{
			_mana = 0;
		}
		
		if (_storedInDb)
		{
			_storedInDb = false;
		}
		if (resetConsumingMana)
		{
			_consumingMana = false;
		}
		
		final PlayerInstance player = getActingPlayer();
		if (player != null)
		{
			SystemMessage sm;
			switch (_mana)
			{
				case 10:
				{
					sm = new SystemMessage(SystemMessageId.S1_S_REMAINING_MANA_IS_NOW_10);
					sm.addItemName(_item);
					player.sendPacket(sm);
					break;
				}
				case 5:
				{
					sm = new SystemMessage(SystemMessageId.S1_S_REMAINING_MANA_IS_NOW_5);
					sm.addItemName(_item);
					player.sendPacket(sm);
					break;
				}
				case 1:
				{
					sm = new SystemMessage(SystemMessageId.S1_S_REMAINING_MANA_IS_NOW_1_IT_WILL_DISAPPEAR_SOON);
					sm.addItemName(_item);
					player.sendPacket(sm);
					break;
				}
			}
			
			if (_mana == 0) // The life time has expired
			{
				sm = new SystemMessage(SystemMessageId.S1_S_REMAINING_MANA_IS_NOW_0_AND_THE_ITEM_HAS_DISAPPEARED);
				sm.addItemName(_item);
				player.sendPacket(sm);
				
				// unequip
				if (isEquipped())
				{
					final ItemInstance[] unequiped = player.getInventory().unEquipItemInSlotAndRecord(getLocationSlot());
					final InventoryUpdate iu = new InventoryUpdate();
					for (ItemInstance item : unequiped)
					{
						iu.addModifiedItem(item);
					}
					player.sendInventoryUpdate(iu);
					player.broadcastUserInfo();
				}
				
				if (_loc != ItemLocation.WAREHOUSE)
				{
					// destroy
					player.getInventory().destroyItem("ItemInstance", this, player, null);
					
					// send update
					final InventoryUpdate iu = new InventoryUpdate();
					iu.addRemovedItem(this);
					player.sendInventoryUpdate(iu);
				}
				else
				{
					player.getWarehouse().destroyItem("ItemInstance", this, player, null);
				}
			}
			else
			{
				// Reschedule if still equipped
				if (!_consumingMana && isEquipped())
				{
					scheduleConsumeManaTask();
				}
				if (_loc != ItemLocation.WAREHOUSE)
				{
					final InventoryUpdate iu = new InventoryUpdate();
					iu.addModifiedItem(this);
					player.sendInventoryUpdate(iu);
				}
			}
		}
	}
	
	public void scheduleConsumeManaTask()
	{
		if (_consumingMana)
		{
			return;
		}
		_consumingMana = true;
		ThreadPool.schedule(new ScheduleConsumeManaTask(this), MANA_CONSUMPTION_RATE);
	}
	
	/**
	 * Returns false cause item can't be attacked
	 * @return boolean false
	 */
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}
	
	/**
	 * Updates the database.<BR>
	 */
	public void updateDatabase()
	{
		updateDatabase(false);
	}
	
	/**
	 * Updates the database.<BR>
	 * @param force if the update should necessarilly be done.
	 */
	public void updateDatabase(boolean force)
	{
		_dbLock.lock();
		
		try
		{
			if (_existsInDb)
			{
				if ((_ownerId == 0) || (_loc == ItemLocation.VOID) || (_loc == ItemLocation.REFUND) || ((_count == 0) && (_loc != ItemLocation.LEASE)))
				{
					removeFromDb();
				}
				else if (!Config.LAZY_ITEMS_UPDATE || force)
				{
					updateInDb();
				}
			}
			else
			{
				if ((_ownerId == 0) || (_loc == ItemLocation.VOID) || (_loc == ItemLocation.REFUND) || ((_count == 0) && (_loc != ItemLocation.LEASE)))
				{
					return;
				}
				insertIntoDb();
			}
		}
		finally
		{
			_dbLock.unlock();
		}
	}
	
	/**
	 * Init a dropped ItemInstance and add it in the world as a visible object.<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Set the x,y,z position of the ItemInstance dropped and update its _worldregion</li>
	 * <li>Add the ItemInstance dropped to _visibleObjects of its WorldRegion</li>
	 * <li>Add the ItemInstance dropped in the world as a <B>visible</B> object</li><BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to _allObjects of World </B></FONT><BR>
	 * <BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <BR>
	 * <li>Drop item</li>
	 * <li>Call Pet</li><BR>
	 */
	public class ItemDropTask implements Runnable
	{
		private int _x;
		private int _y;
		private int _z;
		private final Creature _dropper;
		private final ItemInstance _itеm;
		
		public ItemDropTask(ItemInstance item, Creature dropper, int x, int y, int z)
		{
			_x = x;
			_y = y;
			_z = z;
			_dropper = dropper;
			_itеm = item;
		}
		
		@Override
		public void run()
		{
			if (_dropper != null)
			{
				final Instance instance = _dropper.getInstanceWorld();
				final Location dropDest = GeoEngine.getInstance().canMoveToTargetLoc(_dropper.getX(), _dropper.getY(), _dropper.getZ(), _x, _y, _z, instance);
				_x = dropDest.getX();
				_y = dropDest.getY();
				_z = dropDest.getZ();
				setInstance(instance); // Inherit instancezone when dropped in visible world
			}
			else
			{
				setInstance(null); // No dropper? Make it a global item...
			}
			
			synchronized (_itеm)
			{
				// Set the x,y,z position of the ItemInstance dropped and update its _worldregion
				_itеm.setSpawned(true);
				_itеm.setXYZ(_x, _y, _z);
			}
			
			_itеm.setDropTime(System.currentTimeMillis());
			_itеm.setDropperObjectId(_dropper != null ? _dropper.getObjectId() : 0); // Set the dropper Id for the knownlist packets in sendInfo
			
			// Add the ItemInstance dropped in the world as a visible object
			World.getInstance().addVisibleObject(_itеm, _itеm.getWorldRegion());
			if (Config.SAVE_DROPPED_ITEM)
			{
				ItemsOnGroundManager.getInstance().save(_itеm);
			}
			_itеm.setDropperObjectId(0); // Set the dropper Id back to 0 so it no longer shows the drop packet
		}
	}
	
	public void dropMe(Creature dropper, int x, int y, int z)
	{
		ThreadPool.execute(new ItemDropTask(this, dropper, x, y, z));
		if ((dropper != null) && dropper.isPlayer())
		{
			// Notify to scripts
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemDrop(dropper.getActingPlayer(), this, new Location(x, y, z)), getItem());
		}
	}
	
	/**
	 * Update the database with values of the item
	 */
	private void updateInDb()
	{
		if (!_existsInDb || _wear || _storedInDb)
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE items SET owner_id=?,count=?,loc=?,loc_data=?,enchant_level=?,custom_type1=?,custom_type2=?,mana_left=?,time=? WHERE object_id = ?"))
		{
			ps.setInt(1, _ownerId);
			ps.setLong(2, _count);
			ps.setString(3, _loc.name());
			ps.setInt(4, _locData);
			ps.setInt(5, _enchantLevel);
			ps.setInt(6, _type1);
			ps.setInt(7, _type2);
			ps.setInt(8, _mana);
			ps.setLong(9, _time);
			ps.setInt(10, getObjectId());
			ps.executeUpdate();
			_existsInDb = true;
			_storedInDb = true;
			
			if (_augmentation != null)
			{
				updateItemOptions(con);
			}
			
			if (_elementals != null)
			{
				updateItemElements(con);
			}
			
			updateSpecialAbilities(con);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not update item " + this + " in DB: Reason: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Insert the item in database
	 */
	private void insertIntoDb()
	{
		if (_existsInDb || (getObjectId() == 0) || _wear)
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO items (owner_id,item_id,count,loc,loc_data,enchant_level,object_id,custom_type1,custom_type2,mana_left,time) VALUES (?,?,?,?,?,?,?,?,?,?,?)"))
		{
			ps.setInt(1, _ownerId);
			ps.setInt(2, _itemId);
			ps.setLong(3, _count);
			ps.setString(4, _loc.name());
			ps.setInt(5, _locData);
			ps.setInt(6, _enchantLevel);
			ps.setInt(7, getObjectId());
			ps.setInt(8, _type1);
			ps.setInt(9, _type2);
			ps.setInt(10, _mana);
			ps.setLong(11, _time);
			
			ps.executeUpdate();
			_existsInDb = true;
			_storedInDb = true;
			
			if (_augmentation != null)
			{
				updateItemOptions(con);
			}
			
			if (_elementals != null)
			{
				updateItemElements(con);
			}
			
			updateSpecialAbilities(con);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not insert item " + this + " into DB: Reason: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Delete item from database
	 */
	private void removeFromDb()
	{
		if (!_existsInDb || _wear)
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM items WHERE object_id = ?"))
			{
				ps.setInt(1, getObjectId());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_variations WHERE itemId = ?"))
			{
				ps.setInt(1, getObjectId());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_elementals WHERE itemId = ?"))
			{
				ps.setInt(1, getObjectId());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_special_abilities WHERE objectId = ?"))
			{
				ps.setInt(1, getObjectId());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM item_variables WHERE id = ?"))
			{
				ps.setInt(1, getObjectId());
				ps.executeUpdate();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not delete item " + this + " in DB ", e);
		}
		finally
		{
			_existsInDb = false;
			_storedInDb = false;
		}
	}
	
	/**
	 * Returns the item in String format
	 * @return String
	 */
	@Override
	public String toString()
	{
		return _item + "[" + getObjectId() + "]";
	}
	
	public void resetOwnerTimer()
	{
		if (itemLootShedule != null)
		{
			itemLootShedule.cancel(true);
			itemLootShedule = null;
		}
	}
	
	public void setItemLootShedule(ScheduledFuture<?> sf)
	{
		itemLootShedule = sf;
	}
	
	public ScheduledFuture<?> getItemLootShedule()
	{
		return itemLootShedule;
	}
	
	public void setProtected(boolean isProtected)
	{
		_protected = isProtected;
	}
	
	public boolean isProtected()
	{
		return _protected;
	}
	
	public boolean isAvailable()
	{
		if (!_item.isConditionAttached())
		{
			return true;
		}
		if ((_loc == ItemLocation.PET) || (_loc == ItemLocation.PET_EQUIP))
		{
			return true;
		}
		Creature owner = getActingPlayer();
		if (owner != null)
		{
			for (Condition condition : _item.getConditions())
			{
				if (condition == null)
				{
					continue;
				}
				try
				{
					if (!condition.test(owner, owner, null, null))
					{
						return false;
					}
				}
				catch (Exception e)
				{
				}
			}
		}
		return true;
	}
	
	public void setCountDecrease(boolean decrease)
	{
		_decrease = decrease;
	}
	
	public boolean getCountDecrease()
	{
		return _decrease;
	}
	
	public void setInitCount(int InitCount)
	{
		_initCount = InitCount;
	}
	
	public long getInitCount()
	{
		return _initCount;
	}
	
	public void restoreInitCount()
	{
		if (_decrease)
		{
			setCount(_initCount);
		}
	}
	
	public boolean isTimeLimitedItem()
	{
		return _time > 0;
	}
	
	/**
	 * Returns (current system time + time) of this time limited item
	 * @return Time
	 */
	public long getTime()
	{
		return _time;
	}
	
	public long getRemainingTime()
	{
		return _time - System.currentTimeMillis();
	}
	
	public void endOfLife()
	{
		final PlayerInstance player = getActingPlayer();
		if (player != null)
		{
			if (isEquipped())
			{
				final ItemInstance[] unequiped = player.getInventory().unEquipItemInSlotAndRecord(getLocationSlot());
				final InventoryUpdate iu = new InventoryUpdate();
				for (ItemInstance item : unequiped)
				{
					iu.addModifiedItem(item);
				}
				player.sendInventoryUpdate(iu);
			}
			
			if (_loc != ItemLocation.WAREHOUSE)
			{
				// destroy
				player.getInventory().destroyItem("ItemInstance", this, player, null);
				
				// send update
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addRemovedItem(this);
				player.sendInventoryUpdate(iu);
			}
			else
			{
				player.getWarehouse().destroyItem("ItemInstance", this, player, null);
			}
			player.sendPacket(new SystemMessage(SystemMessageId.S1_HAS_EXPIRED).addItemName(_itemId));
		}
	}
	
	public void scheduleLifeTimeTask()
	{
		if (!isTimeLimitedItem())
		{
			return;
		}
		else if (getRemainingTime() <= 0)
		{
			endOfLife();
		}
		else
		{
			if (_lifeTimeTask != null)
			{
				_lifeTimeTask.cancel(true);
			}
			_lifeTimeTask = ThreadPool.schedule(new ScheduleLifeTimeTask(this), getRemainingTime());
		}
	}
	
	static class ScheduleLifeTimeTask implements Runnable
	{
		private static final Logger LOGGER = Logger.getLogger(ScheduleLifeTimeTask.class.getName());
		private final ItemInstance _limitedItem;
		
		ScheduleLifeTimeTask(ItemInstance item)
		{
			_limitedItem = item;
		}
		
		@Override
		public void run()
		{
			try
			{
				if (_limitedItem != null)
				{
					_limitedItem.endOfLife();
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "", e);
			}
		}
	}
	
	public void setDropperObjectId(int id)
	{
		_dropperObjectId = id;
	}
	
	@Override
	public void sendInfo(PlayerInstance player)
	{
		if (_dropperObjectId != 0)
		{
			player.sendPacket(new DropItem(this, _dropperObjectId));
		}
		else
		{
			player.sendPacket(new SpawnItem(this));
		}
	}
	
	public DropProtection getDropProtection()
	{
		return _dropProtection;
	}
	
	public boolean isPublished()
	{
		return _published;
	}
	
	public void publish()
	{
		_published = true;
	}
	
	@Override
	public boolean decayMe()
	{
		if (Config.SAVE_DROPPED_ITEM)
		{
			ItemsOnGroundManager.getInstance().removeObject(this);
		}
		
		return super.decayMe();
	}
	
	public boolean isQuestItem()
	{
		return _item.isQuestItem();
	}
	
	public boolean isElementable()
	{
		if ((_loc == ItemLocation.INVENTORY) || (_loc == ItemLocation.PAPERDOLL))
		{
			return _item.isElementable();
		}
		return false;
	}
	
	public boolean isFreightable()
	{
		return _item.isFreightable();
	}
	
	public int useSkillDisTime()
	{
		return _item.useSkillDisTime();
	}
	
	public int getOlyEnchantLevel()
	{
		final PlayerInstance player = getActingPlayer();
		int enchant = _enchantLevel;
		
		if (player == null)
		{
			return enchant;
		}
		
		if (player.isInOlympiadMode() && (Config.ALT_OLY_ENCHANT_LIMIT >= 0) && (enchant > Config.ALT_OLY_ENCHANT_LIMIT))
		{
			enchant = Config.ALT_OLY_ENCHANT_LIMIT;
		}
		
		return enchant;
	}
	
	public boolean hasPassiveSkills()
	{
		return (_item.getItemType() == EtcItemType.ENCHT_ATTR_RUNE) && (_loc == ItemLocation.INVENTORY) && (_ownerId > 0) && (_item.getSkills(ItemSkillType.NORMAL) != null);
	}
	
	public void giveSkillsToOwner()
	{
		if (!hasPassiveSkills())
		{
			return;
		}
		
		final PlayerInstance player = getActingPlayer();
		if (player != null)
		{
			_item.forEachSkill(ItemSkillType.NORMAL, holder ->
			{
				final Skill skill = holder.getSkill();
				if (skill.isPassive())
				{
					player.addSkill(skill, false);
				}
			});
		}
	}
	
	public void removeSkillsFromOwner()
	{
		if (!hasPassiveSkills())
		{
			return;
		}
		
		final PlayerInstance player = getActingPlayer();
		if (player != null)
		{
			_item.forEachSkill(ItemSkillType.NORMAL, holder ->
			{
				final Skill skill = holder.getSkill();
				if (skill.isPassive())
				{
					player.removeSkill(skill, false, skill.isPassive());
				}
			});
		}
	}
	
	@Override
	public boolean isItem()
	{
		return true;
	}
	
	@Override
	public PlayerInstance getActingPlayer()
	{
		return World.getInstance().getPlayer(getOwnerId());
	}
	
	public int getEquipReuseDelay()
	{
		return _item.getEquipReuseDelay();
	}
	
	/**
	 * @param player
	 * @param command
	 */
	public void onBypassFeedback(PlayerInstance player, String command)
	{
		if (command.startsWith("Quest"))
		{
			final String questName = command.substring(6);
			String event = null;
			final int idx = questName.indexOf(' ');
			if (idx > 0)
			{
				event = questName.substring(idx).trim();
			}
			
			if (event != null)
			{
				EventDispatcher.getInstance().notifyEventAsync(new OnItemBypassEvent(this, player, event), getItem());
			}
			else
			{
				EventDispatcher.getInstance().notifyEventAsync(new OnItemTalk(this, player), getItem());
			}
		}
	}
	
	/**
	 * Returns enchant effect object for this item
	 * @return enchanteffect
	 */
	public int[] getEnchantOptions()
	{
		final EnchantOptions op = EnchantItemOptionsData.getInstance().getOptions(this);
		if (op != null)
		{
			return op.getOptions();
		}
		return DEFAULT_ENCHANT_OPTIONS;
	}
	
	public Collection<EnsoulOption> getSpecialAbilities()
	{
		final List<EnsoulOption> result = new ArrayList<>();
		for (EnsoulOption ensoulOption : _ensoulOptions)
		{
			if (ensoulOption != null)
			{
				result.add(ensoulOption);
			}
		}
		return result;
	}
	
	public EnsoulOption getSpecialAbility(int index)
	{
		return _ensoulOptions[index];
	}
	
	public Collection<EnsoulOption> getAdditionalSpecialAbilities()
	{
		final List<EnsoulOption> result = new ArrayList<>();
		for (EnsoulOption ensoulSpecialOption : _ensoulSpecialOptions)
		{
			if (ensoulSpecialOption != null)
			{
				result.add(ensoulSpecialOption);
			}
		}
		return result;
	}
	
	public EnsoulOption getAdditionalSpecialAbility(int index)
	{
		return _ensoulSpecialOptions[index];
	}
	
	public void addSpecialAbility(EnsoulOption option, int position, int type, boolean updateInDB)
	{
		if ((type == 1) && ((position < 0) || (position > 1))) // two first slots
		{
			return;
		}
		if ((type == 2) && (position != 0)) // third slot
		{
			return;
		}
		
		if (type == 1) // Adding regular ability
		{
			final EnsoulOption oldOption = _ensoulOptions[position];
			if (oldOption != null)
			{
				removeSpecialAbility(oldOption);
			}
			_ensoulOptions[position] = option;
		}
		else if (type == 2) // Adding special ability
		{
			final EnsoulOption oldOption = _ensoulSpecialOptions[position];
			if (oldOption != null)
			{
				removeSpecialAbility(oldOption);
			}
			_ensoulSpecialOptions[position] = option;
		}
		
		if (updateInDB)
		{
			updateSpecialAbilities();
		}
		
		// Notify to Scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnItemSoulCrystalAdd(getActingPlayer(), this));
	}
	
	public void removeSpecialAbility(int position, int type)
	{
		if (type == 1)
		{
			final EnsoulOption option = _ensoulOptions[position];
			if (option != null)
			{
				removeSpecialAbility(option);
				_ensoulOptions[position] = null;
				
				// Rearrange.
				if (position == 0)
				{
					final EnsoulOption secondEnsoul = _ensoulOptions[1];
					if (secondEnsoul != null)
					{
						removeSpecialAbility(secondEnsoul);
						_ensoulOptions[1] = null;
						addSpecialAbility(secondEnsoul, 0, type, true);
					}
				}
			}
		}
		else if (type == 2)
		{
			final EnsoulOption option = _ensoulSpecialOptions[position];
			if (option != null)
			{
				removeSpecialAbility(option);
				_ensoulSpecialOptions[position] = null;
			}
		}
	}
	
	public void clearSpecialAbilities()
	{
		for (EnsoulOption ensoulOption : _ensoulOptions)
		{
			clearSpecialAbility(ensoulOption);
		}
		for (EnsoulOption ensoulSpecialOption : _ensoulSpecialOptions)
		{
			clearSpecialAbility(ensoulSpecialOption);
		}
	}
	
	public void applySpecialAbilities()
	{
		if (!isEquipped())
		{
			return;
		}
		
		for (EnsoulOption ensoulOption : _ensoulOptions)
		{
			applySpecialAbility(ensoulOption);
		}
		for (EnsoulOption ensoulSpecialOption : _ensoulSpecialOptions)
		{
			applySpecialAbility(ensoulSpecialOption);
		}
	}
	
	private void removeSpecialAbility(EnsoulOption option)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM item_special_abilities WHERE objectId = ? AND optionId = ?"))
		{
			ps.setInt(1, getObjectId());
			ps.setInt(2, option.getId());
			ps.execute();
			
			final Skill skill = option.getSkill();
			if (skill != null)
			{
				final PlayerInstance player = getActingPlayer();
				if (player != null)
				{
					player.removeSkill(skill.getId());
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Couldn't remove special ability for item: " + this, e);
		}
	}
	
	private void applySpecialAbility(EnsoulOption option)
	{
		if (option == null)
		{
			return;
		}
		
		final Skill skill = option.getSkill();
		if (skill != null)
		{
			final PlayerInstance player = getActingPlayer();
			if (player != null)
			{
				if (player.getSkillLevel(skill.getId()) != skill.getLevel())
				{
					player.addSkill(skill, false);
				}
			}
		}
	}
	
	private void clearSpecialAbility(EnsoulOption option)
	{
		if (option == null)
		{
			return;
		}
		
		final Skill skill = option.getSkill();
		if (skill != null)
		{
			final PlayerInstance player = getActingPlayer();
			if (player != null)
			{
				player.removeSkill(skill, false, true);
			}
		}
	}
	
	private void restoreSpecialAbilities()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM item_special_abilities WHERE objectId = ? ORDER BY position"))
		{
			ps.setInt(1, getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final int optionId = rs.getInt("optionId");
					final int type = rs.getInt("type");
					final int position = rs.getInt("position");
					final EnsoulOption option = EnsoulData.getInstance().getOption(optionId);
					if (option != null)
					{
						addSpecialAbility(option, position, type, false);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Couldn't restore special abilities for item: " + this, e);
		}
	}
	
	public void updateSpecialAbilities()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			updateSpecialAbilities(con);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Couldn't update item special abilities", e);
		}
	}
	
	private void updateSpecialAbilities(Connection con)
	{
		try (PreparedStatement ps = con.prepareStatement("INSERT INTO item_special_abilities (`objectId`, `type`, `optionId`, `position`) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE type = ?, optionId = ?, position = ?"))
		{
			ps.setInt(1, getObjectId());
			for (int i = 0; i < _ensoulOptions.length; i++)
			{
				if (_ensoulOptions[i] == null)
				{
					continue;
				}
				
				ps.setInt(2, 1); // regular options
				ps.setInt(3, _ensoulOptions[i].getId());
				ps.setInt(4, i);
				
				ps.setInt(5, 1); // regular options
				ps.setInt(6, _ensoulOptions[i].getId());
				ps.setInt(7, i);
				ps.execute();
			}
			
			for (int i = 0; i < _ensoulSpecialOptions.length; i++)
			{
				if (_ensoulSpecialOptions[i] == null)
				{
					continue;
				}
				
				ps.setInt(2, 2); // special options
				ps.setInt(3, _ensoulSpecialOptions[i].getId());
				ps.setInt(4, i);
				
				ps.setInt(5, 2); // special options
				ps.setInt(6, _ensoulSpecialOptions[i].getId());
				ps.setInt(7, i);
				ps.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Couldn't update item special abilities", e);
		}
	}
	
	/**
	 * Clears all the enchant bonuses if item is enchanted and containing bonuses for enchant value.
	 */
	public void clearEnchantStats()
	{
		final PlayerInstance player = getActingPlayer();
		if (player == null)
		{
			_enchantOptions.clear();
			return;
		}
		
		for (Options op : _enchantOptions)
		{
			op.remove(player);
		}
		_enchantOptions.clear();
	}
	
	/**
	 * Clears and applies all the enchant bonuses if item is enchanted and containing bonuses for enchant value.
	 */
	public void applyEnchantStats()
	{
		final PlayerInstance player = getActingPlayer();
		if (!isEquipped() || (player == null) || (getEnchantOptions() == DEFAULT_ENCHANT_OPTIONS))
		{
			return;
		}
		
		for (int id : getEnchantOptions())
		{
			final Options options = OptionData.getInstance().getOptions(id);
			if (options != null)
			{
				options.apply(player);
				_enchantOptions.add(options);
			}
			else if (id != 0)
			{
				LOGGER.info("applyEnchantStats: Couldn't find option: " + id);
			}
		}
	}
	
	@Override
	public void setHeading(int heading)
	{
	}
	
	public void deleteMe()
	{
		if ((_lifeTimeTask != null) && !_lifeTimeTask.isDone())
		{
			_lifeTimeTask.cancel(false);
			_lifeTimeTask = null;
		}
		
		if ((_appearanceLifeTimeTask != null) && !_appearanceLifeTimeTask.isDone())
		{
			_appearanceLifeTimeTask.cancel(false);
			_appearanceLifeTimeTask = null;
		}
	}
	
	public ItemVariables getVariables()
	{
		final ItemVariables vars = getScript(ItemVariables.class);
		return vars != null ? vars : addScript(new ItemVariables(getObjectId()));
	}
	
	public int getVisualId()
	{
		final int visualId = getVariables().getInt(ItemVariables.VISUAL_ID, 0);
		if (visualId > 0)
		{
			final int appearanceStoneId = getVariables().getInt(ItemVariables.VISUAL_APPEARANCE_STONE_ID, 0);
			if (appearanceStoneId > 0)
			{
				final AppearanceStone stone = AppearanceItemData.getInstance().getStone(appearanceStoneId);
				if (stone != null)
				{
					final PlayerInstance player = getActingPlayer();
					if (player != null)
					{
						if (!stone.getRaces().isEmpty() && !stone.getRaces().contains(player.getRace()))
						{
							return 0;
						}
						if (!stone.getRacesNot().isEmpty() && stone.getRacesNot().contains(player.getRace()))
						{
							return 0;
						}
					}
				}
			}
		}
		return visualId;
	}
	
	public void setVisualId(int visualId)
	{
		getVariables().set(ItemVariables.VISUAL_ID, visualId);
		
		// When removed, cancel existing lifetime task.
		if ((visualId == 0) && (_appearanceLifeTimeTask != null))
		{
			_appearanceLifeTimeTask.cancel(true);
			_appearanceLifeTimeTask = null;
			onVisualLifeTimeEnd();
		}
	}
	
	public long getVisualLifeTime()
	{
		return getVariables().getLong(ItemVariables.VISUAL_APPEARANCE_LIFE_TIME, 0);
	}
	
	public void scheduleVisualLifeTime()
	{
		if (_appearanceLifeTimeTask != null)
		{
			_appearanceLifeTimeTask.cancel(false);
		}
		if (getVisualLifeTime() > 0)
		{
			final long time = getVisualLifeTime() - System.currentTimeMillis();
			if (time > 0)
			{
				_appearanceLifeTimeTask = ThreadPool.schedule(this::onVisualLifeTimeEnd, time);
			}
			else
			{
				ThreadPool.execute(this::onVisualLifeTimeEnd);
			}
		}
	}
	
	private void onVisualLifeTimeEnd()
	{
		final ItemVariables vars = getVariables();
		vars.remove(ItemVariables.VISUAL_ID);
		vars.remove(ItemVariables.VISUAL_APPEARANCE_STONE_ID);
		vars.remove(ItemVariables.VISUAL_APPEARANCE_LIFE_TIME);
		vars.storeMe();
		
		final PlayerInstance player = getActingPlayer();
		if (player != null)
		{
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(this);
			player.broadcastUserInfo(UserInfoType.APPAREANCE);
			player.sendInventoryUpdate(iu);
			
			if (isEnchanted())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.S1_S2_HAS_BEEN_RESTORED_TO_ITS_PREVIOUS_APPEARANCE_AS_ITS_TEMPORARY_MODIFICATION_HAS_EXPIRED).addInt(_enchantLevel).addItemName(this));
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessageId.S1_HAS_BEEN_RESTORED_TO_ITS_PREVIOUS_APPEARANCE_AS_ITS_TEMPORARY_MODIFICATION_HAS_EXPIRED).addItemName(this));
			}
		}
	}
}
