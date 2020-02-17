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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jbr.gameserver.model.Party.MessageType;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.interfaces.INamable;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.CommonSkill;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.Earthquake;
import org.l2jbr.gameserver.network.serverpackets.ExRedSky;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.SocialAction;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.network.serverpackets.UserInfo;
import org.l2jbr.gameserver.util.Broadcast;

public class CursedWeapon implements INamable
{
	private static final Logger LOGGER = Logger.getLogger(CursedWeapon.class.getName());
	
	// _name is the name of the cursed weapon associated with its ID.
	private final String _name;
	// _itemId is the Item ID of the cursed weapon.
	private final int _itemId;
	// _skillId is the skills ID.
	private final int _skillId;
	private final int _skillMaxLevel;
	private int _dropRate;
	private int _duration;
	private int _durationLost;
	private int _disapearChance;
	private int _stageKills;
	
	// this should be false unless if the cursed weapon is dropped, in that case it would be true.
	private boolean _isDropped = false;
	// this sets the cursed weapon status to true only if a player has the cursed weapon, otherwise this should be false.
	private boolean _isActivated = false;
	private ScheduledFuture<?> _removeTask;
	
	private int _nbKills = 0;
	long _endTime = 0;
	
	private int _playerId = 0;
	protected PlayerInstance _player = null;
	private ItemInstance _item = null;
	private int _playerReputation = 0;
	private int _playerPkKills = 0;
	protected int transformationId = 0;
	
	public CursedWeapon(int itemId, int skillId, String name)
	{
		_name = name;
		_itemId = itemId;
		_skillId = skillId;
		_skillMaxLevel = SkillData.getInstance().getMaxLevel(_skillId);
	}
	
	public void endOfLife()
	{
		if (_isActivated)
		{
			if ((_player != null) && _player.isOnline())
			{
				// Remove from player
				LOGGER.info(_name + " being removed online.");
				
				_player.abortAttack();
				
				_player.setReputation(_playerReputation);
				_player.setPkKills(_playerPkKills);
				_player.setCursedWeaponEquippedId(0);
				removeSkill();
				
				// Remove
				_player.getInventory().unEquipItemInBodySlot(Item.SLOT_LR_HAND);
				_player.storeMe();
				
				// Destroy
				final ItemInstance removedItem = _player.getInventory().destroyItemByItemId("", _itemId, 1, _player, null);
				if (!Config.FORCE_INVENTORY_UPDATE)
				{
					final InventoryUpdate iu = new InventoryUpdate();
					if (removedItem.getCount() == 0)
					{
						iu.addRemovedItem(removedItem);
					}
					else
					{
						iu.addModifiedItem(removedItem);
					}
					
					_player.sendInventoryUpdate(iu);
				}
				else
				{
					_player.sendItemList();
				}
				
				_player.broadcastUserInfo();
			}
			else
			{
				// Remove from Db
				LOGGER.info(_name + " being removed offline.");
				
				try (Connection con = DatabaseFactory.getConnection();
					PreparedStatement del = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?");
					PreparedStatement ps = con.prepareStatement("UPDATE characters SET reputation=?, pkkills=? WHERE charId=?"))
				{
					// Delete the item
					del.setInt(1, _playerId);
					del.setInt(2, _itemId);
					if (del.executeUpdate() != 1)
					{
						LOGGER.warning("Error while deleting itemId " + _itemId + " from userId " + _playerId);
					}
					
					// Restore the reputation
					ps.setInt(1, _playerReputation);
					ps.setInt(2, _playerPkKills);
					ps.setInt(3, _playerId);
					if (ps.executeUpdate() != 1)
					{
						LOGGER.warning("Error while updating karma & pkkills for userId " + _playerId);
					}
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "Could not delete : " + e.getMessage(), e);
				}
			}
		}
		else
		{
			// either this cursed weapon is in the inventory of someone who has another cursed weapon equipped,
			// OR this cursed weapon is on the ground.
			if ((_player != null) && (_player.getInventory().getItemByItemId(_itemId) != null))
			{
				// Destroy
				final ItemInstance removedItem = _player.getInventory().destroyItemByItemId("", _itemId, 1, _player, null);
				if (!Config.FORCE_INVENTORY_UPDATE)
				{
					final InventoryUpdate iu = new InventoryUpdate();
					if (removedItem.getCount() == 0)
					{
						iu.addRemovedItem(removedItem);
					}
					else
					{
						iu.addModifiedItem(removedItem);
					}
					
					_player.sendInventoryUpdate(iu);
				}
				else
				{
					_player.sendItemList();
				}
				
				_player.broadcastUserInfo();
			}
			// is dropped on the ground
			else if (_item != null)
			{
				_item.decayMe();
				LOGGER.info(_name + " item has been removed from World.");
			}
		}
		
		// Delete infos from table if any
		CursedWeaponsManager.removeFromDb(_itemId);
		
		final SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_DISAPPEARED);
		sm.addItemName(_itemId);
		CursedWeaponsManager.announce(sm);
		
		// Reset state
		cancelTask();
		_isActivated = false;
		_isDropped = false;
		_endTime = 0;
		_player = null;
		_playerId = 0;
		_playerReputation = 0;
		_playerPkKills = 0;
		_item = null;
		_nbKills = 0;
	}
	
	private void cancelTask()
	{
		if (_removeTask != null)
		{
			_removeTask.cancel(true);
			_removeTask = null;
		}
	}
	
	private class RemoveTask implements Runnable
	{
		protected RemoveTask()
		{
		}
		
		@Override
		public void run()
		{
			if (System.currentTimeMillis() >= _endTime)
			{
				endOfLife();
			}
		}
	}
	
	private void dropIt(Attackable attackable, PlayerInstance player)
	{
		dropIt(attackable, player, null, true);
	}
	
	private void dropIt(Attackable attackable, PlayerInstance player, Creature killer, boolean fromMonster)
	{
		_isActivated = false;
		
		if (fromMonster)
		{
			_item = attackable.dropItem(player, _itemId, 1);
			_item.setDropTime(0); // Prevent item from being removed by ItemsAutoDestroy
			
			// RedSky and Earthquake
			final ExRedSky packet = new ExRedSky(10);
			final Earthquake eq = new Earthquake(player.getX(), player.getY(), player.getZ(), 14, 3);
			Broadcast.toAllOnlinePlayers(packet);
			Broadcast.toAllOnlinePlayers(eq);
		}
		else
		{
			_item = _player.getInventory().getItemByItemId(_itemId);
			_player.dropItem("DieDrop", _item, killer, true);
			_player.setReputation(_playerReputation);
			_player.setPkKills(_playerPkKills);
			_player.setCursedWeaponEquippedId(0);
			removeSkill();
			_player.abortAttack();
			// ItemInstance item = _player.getInventory().getItemByItemId(_itemId);
			// _player.getInventory().dropItem("DieDrop", item, _player, null);
			// _player.getInventory().getItemByItemId(_itemId).dropMe(_player, _player.getX(), _player.getY(), _player.getZ());
		}
		_isDropped = true;
		final SystemMessage sm = new SystemMessage(SystemMessageId.S2_WAS_DROPPED_IN_THE_S1_REGION);
		if (player != null)
		{
			sm.addZoneName(player.getX(), player.getY(), player.getZ()); // Region Name
		}
		else if (_player != null)
		{
			sm.addZoneName(_player.getX(), _player.getY(), _player.getZ()); // Region Name
		}
		else
		{
			sm.addZoneName(killer.getX(), killer.getY(), killer.getZ()); // Region Name
		}
		sm.addItemName(_itemId);
		CursedWeaponsManager.announce(sm); // in the Hot Spring region
	}
	
	public void cursedOnLogin()
	{
		doTransform();
		giveSkill();
		
		final SystemMessage msg = new SystemMessage(SystemMessageId.S2_S_OWNER_HAS_LOGGED_INTO_THE_S1_REGION);
		msg.addZoneName(_player.getX(), _player.getY(), _player.getZ());
		msg.addItemName(_player.getCursedWeaponEquippedId());
		CursedWeaponsManager.announce(msg);
		
		final CursedWeapon cw = CursedWeaponsManager.getInstance().getCursedWeapon(_player.getCursedWeaponEquippedId());
		final SystemMessage msg2 = new SystemMessage(SystemMessageId.S1_HAS_S2_MINUTE_S_OF_USAGE_TIME_REMAINING);
		final int timeLeft = (int) (cw.getTimeLeft() / 60000);
		msg2.addItemName(_player.getCursedWeaponEquippedId());
		msg2.addInt(timeLeft);
		_player.sendPacket(msg2);
	}
	
	/**
	 * Yesod:<br>
	 * Rebind the passive skill belonging to the CursedWeapon. Invoke this method if the weapon owner switches to a subclass.
	 */
	public void giveSkill()
	{
		int level = 1 + (_nbKills / _stageKills);
		if (level > _skillMaxLevel)
		{
			level = _skillMaxLevel;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(_skillId, level);
		_player.addSkill(skill, false);
		
		// Void Burst, Void Flow
		_player.addTransformSkill(CommonSkill.VOID_BURST.getSkill());
		_player.addTransformSkill(CommonSkill.VOID_FLOW.getSkill());
		_player.sendSkillList();
	}
	
	public void doTransform()
	{
		if (_itemId == 8689)
		{
			transformationId = 302;
		}
		else if (_itemId == 8190)
		{
			transformationId = 301;
		}
		
		if (_player.isTransformed())
		{
			_player.stopTransformation(true);
			
			ThreadPool.schedule(() -> _player.transform(transformationId, true), 500);
		}
		else
		{
			_player.transform(transformationId, true);
		}
	}
	
	public void removeSkill()
	{
		_player.removeSkill(_skillId);
		_player.untransform();
		_player.sendSkillList();
	}
	
	public void reActivate()
	{
		_isActivated = true;
		if ((_endTime - System.currentTimeMillis()) <= 0)
		{
			endOfLife();
		}
		else
		{
			_removeTask = ThreadPool.scheduleAtFixedRate(new RemoveTask(), _durationLost * 12000, _durationLost * 12000);
		}
		
	}
	
	public boolean checkDrop(Attackable attackable, PlayerInstance player)
	{
		if (Rnd.get(100000) < _dropRate)
		{
			// Drop the item
			dropIt(attackable, player);
			
			// Start the Life Task
			_endTime = System.currentTimeMillis() + (_duration * 60000);
			_removeTask = ThreadPool.scheduleAtFixedRate(new RemoveTask(), _durationLost * 12000, _durationLost * 12000);
			
			return true;
		}
		
		return false;
	}
	
	public void activate(PlayerInstance player, ItemInstance item)
	{
		// If the player is mounted, attempt to unmount first.
		// Only allow picking up the cursed weapon if unmounting is successful.
		if (player.isMounted() && !player.dismount())
		{
			// TODO: Verify the following system message, may still be custom.
			player.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
			player.dropItem("InvDrop", item, null, true);
			return;
		}
		
		_isActivated = true;
		
		// Player holding it data
		_player = player;
		_playerId = _player.getObjectId();
		_playerReputation = _player.getReputation();
		_playerPkKills = _player.getPkKills();
		saveData();
		
		// Change player stats
		_player.setCursedWeaponEquippedId(_itemId);
		_player.setReputation(-9999999);
		_player.setPkKills(0);
		if (_player.isInParty())
		{
			_player.getParty().removePartyMember(_player, MessageType.EXPELLED);
		}
		
		// Disable All Skills
		// Do Transform
		doTransform();
		// Add skill
		giveSkill();
		
		// Equip with the weapon
		_item = item;
		// ItemInstance[] items =
		_player.getInventory().equipItem(_item);
		SystemMessage sm = new SystemMessage(SystemMessageId.EQUIPPED_S1);
		sm.addItemName(_item);
		_player.sendPacket(sm);
		
		// Fully heal player
		_player.setCurrentHpMp(_player.getMaxHp(), _player.getMaxMp());
		_player.setCurrentCp(_player.getMaxCp());
		
		// Refresh inventory
		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addItem(_item);
			_player.sendInventoryUpdate(iu);
		}
		else
		{
			_player.sendItemList();
		}
		
		// Refresh player stats
		_player.broadcastUserInfo();
		
		final SocialAction atk = new SocialAction(_player.getObjectId(), 17);
		
		_player.broadcastPacket(atk);
		
		sm = new SystemMessage(SystemMessageId.THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION);
		sm.addZoneName(_player.getX(), _player.getY(), _player.getZ()); // Region Name
		sm.addItemName(_item);
		CursedWeaponsManager.announce(sm);
	}
	
	public void saveData()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement del = con.prepareStatement("DELETE FROM cursed_weapons WHERE itemId = ?");
			PreparedStatement ps = con.prepareStatement("INSERT INTO cursed_weapons (itemId, charId, playerReputation, playerPkKills, nbKills, endTime) VALUES (?, ?, ?, ?, ?, ?)"))
		{
			// Delete previous datas
			del.setInt(1, _itemId);
			del.executeUpdate();
			
			if (_isActivated)
			{
				ps.setInt(1, _itemId);
				ps.setInt(2, _playerId);
				ps.setInt(3, _playerReputation);
				ps.setInt(4, _playerPkKills);
				ps.setInt(5, _nbKills);
				ps.setLong(6, _endTime);
				ps.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "CursedWeapon: Failed to save data.", e);
		}
	}
	
	public void dropIt(Creature killer)
	{
		if (Rnd.get(100) <= _disapearChance)
		{
			// Remove it
			endOfLife();
		}
		else
		{
			// Unequip & Drop
			dropIt(null, null, killer, false);
			// Reset player stats
			_player.setReputation(_playerReputation);
			_player.setPkKills(_playerPkKills);
			_player.setCursedWeaponEquippedId(0);
			removeSkill();
			
			_player.abortAttack();
			
			_player.broadcastUserInfo();
		}
	}
	
	public void increaseKills()
	{
		_nbKills++;
		
		if ((_player != null) && _player.isOnline())
		{
			_player.setPkKills(_nbKills);
			_player.sendPacket(new UserInfo(_player));
			
			if (((_nbKills % _stageKills) == 0) && (_nbKills <= (_stageKills * (_skillMaxLevel - 1))))
			{
				giveSkill();
			}
		}
		// Reduce time-to-live
		_endTime -= _durationLost * 60000;
		saveData();
	}
	
	public void setDisapearChance(int disapearChance)
	{
		_disapearChance = disapearChance;
	}
	
	public void setDropRate(int dropRate)
	{
		_dropRate = dropRate;
	}
	
	public void setDuration(int duration)
	{
		_duration = duration;
	}
	
	public void setDurationLost(int durationLost)
	{
		_durationLost = durationLost;
	}
	
	public void setStageKills(int stageKills)
	{
		_stageKills = stageKills;
	}
	
	public void setNbKills(int nbKills)
	{
		_nbKills = nbKills;
	}
	
	public void setPlayerId(int playerId)
	{
		_playerId = playerId;
	}
	
	public void setPlayerReputation(int playerReputation)
	{
		_playerReputation = playerReputation;
	}
	
	public void setPlayerPkKills(int playerPkKills)
	{
		_playerPkKills = playerPkKills;
	}
	
	public void setActivated(boolean isActivated)
	{
		_isActivated = isActivated;
	}
	
	public void setDropped(boolean isDropped)
	{
		_isDropped = isDropped;
	}
	
	public void setEndTime(long endTime)
	{
		_endTime = endTime;
	}
	
	public void setPlayer(PlayerInstance player)
	{
		_player = player;
	}
	
	public void setItem(ItemInstance item)
	{
		_item = item;
	}
	
	public boolean isActivated()
	{
		return _isActivated;
	}
	
	public boolean isDropped()
	{
		return _isDropped;
	}
	
	public long getEndTime()
	{
		return _endTime;
	}
	
	@Override
	public String getName()
	{
		return _name;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public int getPlayerId()
	{
		return _playerId;
	}
	
	public PlayerInstance getPlayer()
	{
		return _player;
	}
	
	public int getPlayerReputation()
	{
		return _playerReputation;
	}
	
	public int getPlayerPkKills()
	{
		return _playerPkKills;
	}
	
	public int getNbKills()
	{
		return _nbKills;
	}
	
	public int getStageKills()
	{
		return _stageKills;
	}
	
	public boolean isActive()
	{
		return _isActivated || _isDropped;
	}
	
	public int getLevel()
	{
		if (_nbKills > (_stageKills * _skillMaxLevel))
		{
			return _skillMaxLevel;
		}
		return (_nbKills / _stageKills);
	}
	
	public long getTimeLeft()
	{
		return _endTime - System.currentTimeMillis();
	}
	
	public void goTo(PlayerInstance player)
	{
		if (player == null)
		{
			return;
		}
		
		if (_isActivated && (_player != null))
		{
			// Go to player holding the weapon
			player.teleToLocation(_player.getLocation(), true);
		}
		else if (_isDropped && (_item != null))
		{
			// Go to item on the ground
			player.teleToLocation(_item.getLocation(), true);
		}
		else
		{
			player.sendMessage(_name + " isn't in the World.");
		}
	}
	
	public Location getWorldPosition()
	{
		if (_isActivated && (_player != null))
		{
			return _player.getLocation();
		}
		
		if (_isDropped && (_item != null))
		{
			return _item.getLocation();
		}
		
		return null;
	}
	
	public long getDuration()
	{
		return _duration;
	}
}
