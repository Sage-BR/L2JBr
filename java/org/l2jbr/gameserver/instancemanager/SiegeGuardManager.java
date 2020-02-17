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
package org.l2jbr.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.gameserver.data.xml.impl.CastleData;
import org.l2jbr.gameserver.data.xml.impl.NpcData;
import org.l2jbr.gameserver.enums.ItemLocation;
import org.l2jbr.gameserver.model.Spawn;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.DefenderInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.templates.NpcTemplate;
import org.l2jbr.gameserver.model.entity.Castle;
import org.l2jbr.gameserver.model.holders.SiegeGuardHolder;
import org.l2jbr.gameserver.model.interfaces.IPositionable;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;

/**
 * Siege Guard Manager.
 * @author St3eT
 */
public class SiegeGuardManager
{
	private static final Logger LOGGER = Logger.getLogger(SiegeGuardManager.class.getName());
	private static final Set<ItemInstance> _droppedTickets = ConcurrentHashMap.newKeySet();
	private static final Map<Integer, Set<Spawn>> _siegeGuardSpawn = new ConcurrentHashMap<>();
	
	protected SiegeGuardManager()
	{
		_droppedTickets.clear();
		load();
	}
	
	private void load()
	{
		try (Connection con = DatabaseFactory.getConnection();
			ResultSet rs = con.createStatement().executeQuery("SELECT * FROM castle_siege_guards Where isHired = 1"))
		{
			while (rs.next())
			{
				final int npcId = rs.getInt("npcId");
				final int x = rs.getInt("x");
				final int y = rs.getInt("y");
				final int z = rs.getInt("z");
				
				final Castle castle = CastleManager.getInstance().getCastle(x, y, z);
				if (castle == null)
				{
					LOGGER.warning("Siege guard ticket cannot be placed! Castle is null at X: " + x + ", Y: " + y + ", Z: " + z);
					continue;
				}
				
				final SiegeGuardHolder holder = getSiegeGuardByNpc(castle.getResidenceId(), npcId);
				if ((holder != null) && !castle.getSiege().isInProgress())
				{
					final ItemInstance dropticket = new ItemInstance(holder.getItemId());
					dropticket.setItemLocation(ItemLocation.VOID);
					dropticket.dropMe(null, x, y, z);
					World.getInstance().addObject(dropticket);
					_droppedTickets.add(dropticket);
				}
			}
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + _droppedTickets.size() + " siege guards tickets.");
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	/**
	 * Finds {@code SiegeGuardHolder} equals to castle id and npc id.
	 * @param castleId the ID of the castle
	 * @param itemId the ID of the item
	 * @return the {@code SiegeGuardHolder} for this castle ID and item ID if any, otherwise {@code null}
	 */
	public SiegeGuardHolder getSiegeGuardByItem(int castleId, int itemId)
	{
		return CastleData.getInstance().getSiegeGuardsForCastle(castleId).stream().filter(g -> (g.getItemId() == itemId)).findFirst().orElse(null);
	}
	
	/**
	 * Finds {@code SiegeGuardHolder} equals to castle id and npc id.
	 * @param castleId the ID of the castle
	 * @param npcId the ID of the npc
	 * @return the {@code SiegeGuardHolder} for this castle ID and npc ID if any, otherwise {@code null}
	 */
	public SiegeGuardHolder getSiegeGuardByNpc(int castleId, int npcId)
	{
		return CastleData.getInstance().getSiegeGuardsForCastle(castleId).stream().filter(g -> (g.getNpcId() == npcId)).findFirst().orElse(null);
	}
	
	/**
	 * Checks if {@code PlayerInstance} is too much close to another ticket.
	 * @param player the PlayerInstance
	 * @return {@code true} if {@code PlayerInstance} is too much close to another ticket, {@code false} otherwise
	 */
	public boolean isTooCloseToAnotherTicket(PlayerInstance player)
	{
		return _droppedTickets.stream().filter(g -> g.calculateDistance3D(player) < 25).findFirst().orElse(null) != null;
	}
	
	/**
	 * Checks if castle is under npc limit.
	 * @param castleId the ID of the castle
	 * @param itemId the ID of the item
	 * @return {@code true} if castle is under npc limit, {@code false} otherwise
	 */
	public boolean isAtNpcLimit(int castleId, int itemId)
	{
		final long count = _droppedTickets.stream().filter(i -> i.getId() == itemId).count();
		final SiegeGuardHolder holder = getSiegeGuardByItem(castleId, itemId);
		return count >= holder.getMaxNpcAmout();
	}
	
	/**
	 * Adds ticket in current world.
	 * @param itemId the ID of the item
	 * @param player the PlayerInstance
	 */
	public void addTicket(int itemId, PlayerInstance player)
	{
		final Castle castle = CastleManager.getInstance().getCastle(player);
		if (castle == null)
		{
			return;
		}
		
		if (isAtNpcLimit(castle.getResidenceId(), itemId))
		{
			return;
		}
		
		final SiegeGuardHolder holder = getSiegeGuardByItem(castle.getResidenceId(), itemId);
		if (holder != null)
		{
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement("Insert Into castle_siege_guards (castleId, npcId, x, y, z, heading, respawnDelay, isHired) Values (?, ?, ?, ?, ?, ?, ?, ?)"))
			{
				statement.setInt(1, castle.getResidenceId());
				statement.setInt(2, holder.getNpcId());
				statement.setInt(3, player.getX());
				statement.setInt(4, player.getY());
				statement.setInt(5, player.getZ());
				statement.setInt(6, player.getHeading());
				statement.setInt(7, 0);
				statement.setInt(8, 1);
				statement.execute();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "Error adding siege guard for castle " + castle.getName() + ": " + e.getMessage(), e);
			}
			
			spawnMercenary(player, holder);
			final ItemInstance dropticket = new ItemInstance(itemId);
			dropticket.setItemLocation(ItemLocation.VOID);
			dropticket.dropMe(null, player.getX(), player.getY(), player.getZ());
			World.getInstance().addObject(dropticket);
			_droppedTickets.add(dropticket);
		}
	}
	
	/**
	 * Spawns Siege Guard in current world.
	 * @param pos the object containing the spawn location coordinates
	 * @param holder SiegeGuardHolder holder
	 */
	private void spawnMercenary(IPositionable pos, SiegeGuardHolder holder)
	{
		final NpcTemplate template = NpcData.getInstance().getTemplate(holder.getNpcId());
		if (template != null)
		{
			final DefenderInstance npc = new DefenderInstance(template);
			npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
			npc.setDecayed(false);
			npc.setHeading(pos.getHeading());
			npc.spawnMe(pos.getX(), pos.getY(), (pos.getZ() + 20));
			npc.scheduleDespawn(3000);
			npc.setIsImmobilized(holder.isStationary());
		}
	}
	
	/**
	 * Delete all tickets from a castle.
	 * @param castleId the ID of the castle
	 */
	public void deleteTickets(int castleId)
	{
		for (ItemInstance ticket : _droppedTickets)
		{
			if ((ticket != null) && (getSiegeGuardByItem(castleId, ticket.getId()) != null))
			{
				ticket.decayMe();
				_droppedTickets.remove(ticket);
			}
		}
	}
	
	/**
	 * remove a single ticket and its associated spawn from the world (used when the castle lord picks up a ticket, for example).
	 * @param item the item ID
	 */
	public void removeTicket(ItemInstance item)
	{
		final Castle castle = CastleManager.getInstance().getCastle(item);
		if (castle == null)
		{
			return;
		}
		
		final SiegeGuardHolder holder = getSiegeGuardByItem(castle.getResidenceId(), item.getId());
		if (holder == null)
		{
			return;
		}
		
		removeSiegeGuard(holder.getNpcId(), item);
		_droppedTickets.remove(item);
	}
	
	/**
	 * Loads all siege guards for castle.
	 * @param castle the castle instance
	 */
	private void loadSiegeGuard(Castle castle)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM castle_siege_guards Where castleId = ? And isHired = ?"))
		{
			ps.setInt(1, castle.getResidenceId());
			ps.setInt(2, castle.getOwnerId() > 0 ? 1 : 0);
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final Spawn spawn = new Spawn(rs.getInt("npcId"));
					spawn.setAmount(1);
					spawn.setXYZ(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
					spawn.setHeading(rs.getInt("heading"));
					spawn.setRespawnDelay(rs.getInt("respawnDelay"));
					spawn.setLocationId(0);
					
					getSpawnedGuards(castle.getResidenceId()).add(spawn);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error loading siege guard for castle " + castle.getName() + ": " + e.getMessage(), e);
		}
	}
	
	/**
	 * Remove single siege guard.
	 * @param npcId the ID of NPC
	 * @param pos
	 */
	public void removeSiegeGuard(int npcId, IPositionable pos)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("Delete From castle_siege_guards Where npcId = ? And x = ? AND y = ? AND z = ? AND isHired = 1"))
		{
			ps.setInt(1, npcId);
			ps.setInt(2, pos.getX());
			ps.setInt(3, pos.getY());
			ps.setInt(4, pos.getZ());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error deleting hired siege guard at " + pos + " : " + e.getMessage(), e);
		}
	}
	
	/**
	 * Remove all siege guards for castle.
	 * @param castle the castle instance
	 */
	public void removeSiegeGuards(Castle castle)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("Delete From castle_siege_guards Where castleId = ? And isHired = 1"))
		{
			ps.setInt(1, castle.getResidenceId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error deleting hired siege guard for castle " + castle.getName() + ": " + e.getMessage(), e);
		}
	}
	
	/**
	 * Spawn all siege guards for castle.
	 * @param castle the castle instance
	 */
	public void spawnSiegeGuard(Castle castle)
	{
		try
		{
			final boolean isHired = (castle.getOwnerId() > 0);
			loadSiegeGuard(castle);
			
			for (Spawn spawn : getSpawnedGuards(castle.getResidenceId()))
			{
				if (spawn != null)
				{
					spawn.init();
					if (isHired || (spawn.getRespawnDelay() == 0))
					{
						spawn.stopRespawn();
					}
					
					final SiegeGuardHolder holder = getSiegeGuardByNpc(castle.getResidenceId(), spawn.getLastSpawn().getId());
					if (holder == null)
					{
						continue;
					}
					
					spawn.getLastSpawn().setIsImmobilized(holder.isStationary());
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Error spawning siege guards for castle " + castle.getName(), e);
		}
	}
	
	/**
	 * Unspawn all siege guards for castle.
	 * @param castle the castle instance
	 */
	public void unspawnSiegeGuard(Castle castle)
	{
		for (Spawn spawn : getSpawnedGuards(castle.getResidenceId()))
		{
			if ((spawn != null) && (spawn.getLastSpawn() != null))
			{
				spawn.stopRespawn();
				spawn.getLastSpawn().doDie(spawn.getLastSpawn());
			}
		}
		getSpawnedGuards(castle.getResidenceId()).clear();
	}
	
	public Set<Spawn> getSpawnedGuards(int castleId)
	{
		return _siegeGuardSpawn.computeIfAbsent(castleId, key -> ConcurrentHashMap.newKeySet());
	}
	
	/**
	 * Gets the single instance of {@code MercTicketManager}.
	 * @return single instance of {@code MercTicketManager}
	 */
	public static SiegeGuardManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SiegeGuardManager INSTANCE = new SiegeGuardManager();
	}
}
