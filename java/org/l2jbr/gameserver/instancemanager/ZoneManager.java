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

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.interfaces.ILocational;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.zone.AbstractZoneSettings;
import org.l2jbr.gameserver.model.zone.ZoneForm;
import org.l2jbr.gameserver.model.zone.ZoneRegion;
import org.l2jbr.gameserver.model.zone.ZoneRespawn;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.form.ZoneCuboid;
import org.l2jbr.gameserver.model.zone.form.ZoneCylinder;
import org.l2jbr.gameserver.model.zone.form.ZoneNPoly;
import org.l2jbr.gameserver.model.zone.type.ArenaZone;
import org.l2jbr.gameserver.model.zone.type.CastleZone;
import org.l2jbr.gameserver.model.zone.type.ClanHallZone;
import org.l2jbr.gameserver.model.zone.type.ConditionZone;
import org.l2jbr.gameserver.model.zone.type.DamageZone;
import org.l2jbr.gameserver.model.zone.type.DerbyTrackZone;
import org.l2jbr.gameserver.model.zone.type.EffectZone;
import org.l2jbr.gameserver.model.zone.type.FishingZone;
import org.l2jbr.gameserver.model.zone.type.FortZone;
import org.l2jbr.gameserver.model.zone.type.HqZone;
import org.l2jbr.gameserver.model.zone.type.JailZone;
import org.l2jbr.gameserver.model.zone.type.LandingZone;
import org.l2jbr.gameserver.model.zone.type.MotherTreeZone;
import org.l2jbr.gameserver.model.zone.type.NoLandingZone;
import org.l2jbr.gameserver.model.zone.type.NoRestartZone;
import org.l2jbr.gameserver.model.zone.type.NoStoreZone;
import org.l2jbr.gameserver.model.zone.type.NoSummonFriendZone;
import org.l2jbr.gameserver.model.zone.type.OlympiadStadiumZone;
import org.l2jbr.gameserver.model.zone.type.PeaceZone;
import org.l2jbr.gameserver.model.zone.type.ResidenceHallTeleportZone;
import org.l2jbr.gameserver.model.zone.type.ResidenceTeleportZone;
import org.l2jbr.gameserver.model.zone.type.ResidenceZone;
import org.l2jbr.gameserver.model.zone.type.RespawnZone;
import org.l2jbr.gameserver.model.zone.type.SayuneZone;
import org.l2jbr.gameserver.model.zone.type.ScriptZone;
import org.l2jbr.gameserver.model.zone.type.SiegableHallZone;
import org.l2jbr.gameserver.model.zone.type.SiegeZone;
import org.l2jbr.gameserver.model.zone.type.SpawnTerritory;
import org.l2jbr.gameserver.model.zone.type.SwampZone;
import org.l2jbr.gameserver.model.zone.type.TaxZone;
import org.l2jbr.gameserver.model.zone.type.TeleportZone;
import org.l2jbr.gameserver.model.zone.type.UndyingZone;
import org.l2jbr.gameserver.model.zone.type.WaterZone;

/**
 * This class manages the zones
 * @author durgus
 */
public class ZoneManager implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(ZoneManager.class.getName());
	
	private static final Map<String, AbstractZoneSettings> SETTINGS = new HashMap<>();
	
	private static final int SHIFT_BY = 15;
	private static final int OFFSET_X = Math.abs(World.MAP_MIN_X >> SHIFT_BY);
	private static final int OFFSET_Y = Math.abs(World.MAP_MIN_Y >> SHIFT_BY);
	
	private final Map<Class<? extends ZoneType>, ConcurrentHashMap<Integer, ? extends ZoneType>> _classZones = new ConcurrentHashMap<>();
	private final Map<String, SpawnTerritory> _spawnTerritories = new ConcurrentHashMap<>();
	private final AtomicInteger _lastDynamicId = new AtomicInteger(300000);
	private List<ItemInstance> _debugItems;
	
	private final ZoneRegion[][] _zoneRegions = new ZoneRegion[(World.MAP_MAX_X >> SHIFT_BY) + OFFSET_X + 1][(World.MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y + 1];
	
	/**
	 * Instantiates a new zone manager.
	 */
	protected ZoneManager()
	{
		for (int x = 0; x < _zoneRegions.length; x++)
		{
			for (int y = 0; y < _zoneRegions[x].length; y++)
			{
				_zoneRegions[x][y] = new ZoneRegion(x, y);
			}
		}
		LOGGER.info(getClass().getSimpleName() + " " + _zoneRegions.length + " by " + _zoneRegions[0].length + " Zone Region Grid set up.");
		
		load();
	}
	
	/**
	 * Reload.
	 */
	public void reload()
	{
		// Unload zones.
		unload();
		
		// Load the zones.
		load();
		
		// Re-validate all characters in zones.
		for (WorldObject obj : World.getInstance().getVisibleObjects())
		{
			if (obj.isCreature())
			{
				((Creature) obj).revalidateZone(true);
			}
		}
		
		SETTINGS.clear();
	}
	
	public void unload()
	{
		// Get the world regions
		int count = 0;
		
		// Backup old zone settings
		for (Map<Integer, ? extends ZoneType> map : _classZones.values())
		{
			for (ZoneType zone : map.values())
			{
				if (zone.getSettings() != null)
				{
					SETTINGS.put(zone.getName(), zone.getSettings());
				}
			}
		}
		
		// Clear zones
		for (ZoneRegion[] zoneRegions : _zoneRegions)
		{
			for (ZoneRegion zoneRegion : zoneRegions)
			{
				zoneRegion.getZones().clear();
				count++;
			}
		}
		LOGGER.info(getClass().getSimpleName() + ": Removed zones in " + count + " regions.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		NamedNodeMap attrs;
		Node attribute;
		String zoneName;
		int[][] coords;
		int zoneId;
		int minZ;
		int maxZ;
		String zoneType;
		String zoneShape;
		final List<int[]> rs = new ArrayList<>();
		
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				attrs = n.getAttributes();
				attribute = attrs.getNamedItem("enabled");
				if ((attribute != null) && !Boolean.parseBoolean(attribute.getNodeValue()))
				{
					continue;
				}
				
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("zone".equalsIgnoreCase(d.getNodeName()))
					{
						attrs = d.getAttributes();
						
						attribute = attrs.getNamedItem("type");
						if (attribute != null)
						{
							zoneType = attribute.getNodeValue();
						}
						else
						{
							LOGGER.warning("ZoneData: Missing type for zone in file: " + f.getName());
							continue;
						}
						
						attribute = attrs.getNamedItem("id");
						if (attribute != null)
						{
							zoneId = Integer.parseInt(attribute.getNodeValue());
						}
						else
						{
							zoneId = zoneType.equalsIgnoreCase("NpcSpawnTerritory") ? 0 : _lastDynamicId.incrementAndGet();
						}
						
						attribute = attrs.getNamedItem("name");
						if (attribute != null)
						{
							zoneName = attribute.getNodeValue();
						}
						else
						{
							zoneName = null;
						}
						
						// Check zone name for NpcSpawnTerritory. Must exist and to be unique
						if (zoneType.equalsIgnoreCase("NpcSpawnTerritory"))
						{
							if (zoneName == null)
							{
								LOGGER.warning("ZoneData: Missing name for NpcSpawnTerritory in file: " + f.getName() + ", skipping zone");
								continue;
							}
							else if (_spawnTerritories.containsKey(zoneName))
							{
								LOGGER.warning("ZoneData: Name " + zoneName + " already used for another zone, check file: " + f.getName() + ". Skipping zone");
								continue;
							}
						}
						
						minZ = parseInteger(attrs, "minZ");
						maxZ = parseInteger(attrs, "maxZ");
						
						zoneType = parseString(attrs, "type");
						zoneShape = parseString(attrs, "shape");
						
						// Get the zone shape from xml
						ZoneForm zoneForm = null;
						try
						{
							for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
							{
								if ("node".equalsIgnoreCase(cd.getNodeName()))
								{
									attrs = cd.getAttributes();
									final int[] point = new int[2];
									point[0] = parseInteger(attrs, "X");
									point[1] = parseInteger(attrs, "Y");
									rs.add(point);
								}
							}
							
							coords = rs.toArray(new int[rs.size()][2]);
							rs.clear();
							
							if ((coords == null) || (coords.length == 0))
							{
								LOGGER.warning(getClass().getSimpleName() + ": ZoneData: missing data for zone: " + zoneId + " XML file: " + f.getName());
								continue;
							}
							
							// Create this zone. Parsing for cuboids is a bit different than for other polygons cuboids need exactly 2 points to be defined.
							// Other polygons need at least 3 (one per vertex)
							if (zoneShape.equalsIgnoreCase("Cuboid"))
							{
								if (coords.length == 2)
								{
									zoneForm = new ZoneCuboid(coords[0][0], coords[1][0], coords[0][1], coords[1][1], minZ, maxZ);
								}
								else
								{
									LOGGER.warning(getClass().getSimpleName() + ": ZoneData: Missing cuboid vertex data for zone: " + zoneId + " in file: " + f.getName());
									continue;
								}
							}
							else if (zoneShape.equalsIgnoreCase("NPoly"))
							{
								// nPoly needs to have at least 3 vertices
								if (coords.length > 2)
								{
									final int[] aX = new int[coords.length];
									final int[] aY = new int[coords.length];
									for (int i = 0; i < coords.length; i++)
									{
										aX[i] = coords[i][0];
										aY[i] = coords[i][1];
									}
									zoneForm = new ZoneNPoly(aX, aY, minZ, maxZ);
								}
								else
								{
									LOGGER.warning(getClass().getSimpleName() + ": ZoneData: Bad data for zone: " + zoneId + " in file: " + f.getName());
									continue;
								}
							}
							else if (zoneShape.equalsIgnoreCase("Cylinder"))
							{
								// A Cylinder zone requires a center point
								// at x,y and a radius
								attrs = d.getAttributes();
								final int zoneRad = Integer.parseInt(attrs.getNamedItem("rad").getNodeValue());
								if ((coords.length == 1) && (zoneRad > 0))
								{
									zoneForm = new ZoneCylinder(coords[0][0], coords[0][1], minZ, maxZ, zoneRad);
								}
								else
								{
									LOGGER.warning(getClass().getSimpleName() + ": ZoneData: Bad data for zone: " + zoneId + " in file: " + f.getName());
									continue;
								}
							}
							else
							{
								LOGGER.warning(getClass().getSimpleName() + ": ZoneData: Unknown shape: \"" + zoneShape + "\"  for zone: " + zoneId + " in file: " + f.getName());
								continue;
							}
						}
						catch (Exception e)
						{
							LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": ZoneData: Failed to load zone " + zoneId + " coordinates: " + e.getMessage(), e);
						}
						
						// No further parameters needed, if NpcSpawnTerritory is loading
						if (zoneType.equalsIgnoreCase("NpcSpawnTerritory"))
						{
							_spawnTerritories.put(zoneName, new SpawnTerritory(zoneName, zoneForm));
							continue;
						}
						
						// Create the zone
						Class<?> newZone = null;
						Constructor<?> zoneConstructor = null;
						ZoneType temp;
						try
						{
							newZone = Class.forName("org.l2jbr.gameserver.model.zone.type." + zoneType);
							zoneConstructor = newZone.getConstructor(int.class);
							temp = (ZoneType) zoneConstructor.newInstance(zoneId);
							temp.setZone(zoneForm);
						}
						catch (Exception e)
						{
							LOGGER.warning(getClass().getSimpleName() + ": ZoneData: No such zone type: " + zoneType + " in file: " + f.getName());
							continue;
						}
						
						// Check for additional parameters
						for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
						{
							if ("stat".equalsIgnoreCase(cd.getNodeName()))
							{
								attrs = cd.getAttributes();
								final String name = attrs.getNamedItem("name").getNodeValue();
								final String val = attrs.getNamedItem("val").getNodeValue();
								
								temp.setParameter(name, val);
							}
							else if ("spawn".equalsIgnoreCase(cd.getNodeName()) && (temp instanceof ZoneRespawn))
							{
								attrs = cd.getAttributes();
								final int spawnX = Integer.parseInt(attrs.getNamedItem("X").getNodeValue());
								final int spawnY = Integer.parseInt(attrs.getNamedItem("Y").getNodeValue());
								final int spawnZ = Integer.parseInt(attrs.getNamedItem("Z").getNodeValue());
								final Node val = attrs.getNamedItem("type");
								((ZoneRespawn) temp).parseLoc(spawnX, spawnY, spawnZ, val == null ? null : val.getNodeValue());
							}
							else if ("race".equalsIgnoreCase(cd.getNodeName()) && (temp instanceof RespawnZone))
							{
								attrs = cd.getAttributes();
								final String race = attrs.getNamedItem("name").getNodeValue();
								final String point = attrs.getNamedItem("point").getNodeValue();
								
								((RespawnZone) temp).addRaceRespawnPoint(race, point);
							}
						}
						if (checkId(zoneId))
						{
							LOGGER.config(getClass().getSimpleName() + ": Caution: Zone (" + zoneId + ") from file: " + f.getName() + " overrides previous definition.");
						}
						
						if ((zoneName != null) && !zoneName.isEmpty())
						{
							temp.setName(zoneName);
						}
						
						addZone(zoneId, temp);
						
						// Register the zone into any world region it
						// intersects with...
						// currently 11136 test for each zone :>
						for (int x = 0; x < _zoneRegions.length; x++)
						{
							for (int y = 0; y < _zoneRegions[x].length; y++)
							{
								
								final int ax = (x - OFFSET_X) << SHIFT_BY;
								final int bx = ((x + 1) - OFFSET_X) << SHIFT_BY;
								final int ay = (y - OFFSET_Y) << SHIFT_BY;
								final int by = ((y + 1) - OFFSET_Y) << SHIFT_BY;
								
								if (temp.getZone().intersectsRectangle(ax, bx, ay, by))
								{
									_zoneRegions[x][y].getZones().put(temp.getId(), temp);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void load()
	{
		_classZones.clear();
		_classZones.put(ArenaZone.class, new ConcurrentHashMap<>());
		_classZones.put(CastleZone.class, new ConcurrentHashMap<>());
		_classZones.put(ClanHallZone.class, new ConcurrentHashMap<>());
		_classZones.put(ConditionZone.class, new ConcurrentHashMap<>());
		_classZones.put(DamageZone.class, new ConcurrentHashMap<>());
		_classZones.put(DerbyTrackZone.class, new ConcurrentHashMap<>());
		_classZones.put(EffectZone.class, new ConcurrentHashMap<>());
		_classZones.put(FishingZone.class, new ConcurrentHashMap<>());
		_classZones.put(FortZone.class, new ConcurrentHashMap<>());
		_classZones.put(HqZone.class, new ConcurrentHashMap<>());
		_classZones.put(JailZone.class, new ConcurrentHashMap<>());
		_classZones.put(LandingZone.class, new ConcurrentHashMap<>());
		_classZones.put(MotherTreeZone.class, new ConcurrentHashMap<>());
		_classZones.put(NoLandingZone.class, new ConcurrentHashMap<>());
		_classZones.put(NoRestartZone.class, new ConcurrentHashMap<>());
		_classZones.put(NoStoreZone.class, new ConcurrentHashMap<>());
		_classZones.put(NoSummonFriendZone.class, new ConcurrentHashMap<>());
		_classZones.put(OlympiadStadiumZone.class, new ConcurrentHashMap<>());
		_classZones.put(PeaceZone.class, new ConcurrentHashMap<>());
		_classZones.put(ResidenceHallTeleportZone.class, new ConcurrentHashMap<>());
		_classZones.put(ResidenceTeleportZone.class, new ConcurrentHashMap<>());
		_classZones.put(ResidenceZone.class, new ConcurrentHashMap<>());
		_classZones.put(RespawnZone.class, new ConcurrentHashMap<>());
		_classZones.put(SayuneZone.class, new ConcurrentHashMap<>());
		_classZones.put(ScriptZone.class, new ConcurrentHashMap<>());
		_classZones.put(SiegableHallZone.class, new ConcurrentHashMap<>());
		_classZones.put(SiegeZone.class, new ConcurrentHashMap<>());
		_classZones.put(SwampZone.class, new ConcurrentHashMap<>());
		_classZones.put(TaxZone.class, new ConcurrentHashMap<>());
		_classZones.put(TeleportZone.class, new ConcurrentHashMap<>());
		_classZones.put(UndyingZone.class, new ConcurrentHashMap<>());
		_classZones.put(WaterZone.class, new ConcurrentHashMap<>());
		_spawnTerritories.clear();
		parseDatapackDirectory("data/zones", false);
		parseDatapackDirectory("data/zones/spawnZones", false);
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _classZones.size() + " zone classes and " + getSize() + " zones.");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _spawnTerritories.size() + " NPC spawn territoriers.");
		final OptionalInt maxId = _classZones.values().stream().flatMap(map -> map.keySet().stream()).mapToInt(Integer.class::cast).filter(value -> value < 300000).max();
		LOGGER.info(getClass().getSimpleName() + ": Last static id " + maxId.getAsInt() + ".");
	}
	
	/**
	 * Gets the size.
	 * @return the size
	 */
	public int getSize()
	{
		int i = 0;
		for (Map<Integer, ? extends ZoneType> map : _classZones.values())
		{
			i += map.size();
		}
		return i;
	}
	
	/**
	 * Check id.
	 * @param id the id
	 * @return true, if successful
	 */
	private boolean checkId(int id)
	{
		for (Map<Integer, ? extends ZoneType> map : _classZones.values())
		{
			if (map.containsKey(id))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add new zone.
	 * @param <T> the generic type
	 * @param id the id
	 * @param zone the zone
	 */
	@SuppressWarnings("unchecked")
	private <T extends ZoneType> void addZone(Integer id, T zone)
	{
		ConcurrentHashMap<Integer, T> map = (ConcurrentHashMap<Integer, T>) _classZones.get(zone.getClass());
		if (map == null)
		{
			_classZones.put(zone.getClass(), new ConcurrentHashMap<>());
			map = (ConcurrentHashMap<Integer, T>) _classZones.get(zone.getClass());
		}
		map.put(id, zone);
	}
	
	/**
	 * Return all zones by class type.
	 * @param <T> the generic type
	 * @param zoneType Zone class
	 * @return Collection of zones
	 */
	@SuppressWarnings("unchecked")
	public <T extends ZoneType> Collection<T> getAllZones(Class<T> zoneType)
	{
		return (Collection<T>) _classZones.get(zoneType).values();
	}
	
	/**
	 * Get zone by ID.
	 * @param id the id
	 * @return the zone by id
	 * @see #getZoneById(int, Class)
	 */
	public ZoneType getZoneById(int id)
	{
		for (Map<Integer, ? extends ZoneType> map : _classZones.values())
		{
			if (map.containsKey(id))
			{
				return map.get(id);
			}
		}
		return null;
	}
	
	/**
	 * Get zone by name.
	 * @param name the zone name
	 * @return the zone by name
	 */
	public ZoneType getZoneByName(String name)
	{
		for (Map<Integer, ? extends ZoneType> map : _classZones.values())
		{
			final Optional<? extends ZoneType> zoneType = map.values().stream().filter(z -> (z.getName() != null) && z.getName().equals(name)).findAny();
			if (zoneType.isPresent())
			{
				return zoneType.get();
			}
		}
		return null;
	}
	
	/**
	 * Get zone by ID and zone class.
	 * @param <T> the generic type
	 * @param id the id
	 * @param zoneType the zone type
	 * @return zone
	 */
	@SuppressWarnings("unchecked")
	public <T extends ZoneType> T getZoneById(int id, Class<T> zoneType)
	{
		return (T) _classZones.get(zoneType).get(id);
	}
	
	/**
	 * Get zone by name.
	 * @param <T> the generic type
	 * @param name the zone name
	 * @param zoneType the zone type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends ZoneType> T getZoneByName(String name, Class<T> zoneType)
	{
		final Optional<? extends ZoneType> zone = _classZones.get(zoneType).values().stream().filter(z -> (z.getName() != null) && z.getName().equals(name)).findAny();
		if (zone.isPresent())
		{
			return (T) zone.get();
		}
		return null;
	}
	
	/**
	 * Returns all zones from where the object is located.
	 * @param locational the locational
	 * @return zones
	 */
	public List<ZoneType> getZones(ILocational locational)
	{
		return getZones(locational.getX(), locational.getY(), locational.getZ());
	}
	
	/**
	 * Gets the zone.
	 * @param <T> the generic type
	 * @param locational the locational
	 * @param type the type
	 * @return zone from where the object is located by type
	 */
	public <T extends ZoneType> T getZone(ILocational locational, Class<T> type)
	{
		if (locational == null)
		{
			return null;
		}
		return getZone(locational.getX(), locational.getY(), locational.getZ(), type);
	}
	
	/**
	 * Returns all zones from given coordinates (plane).
	 * @param x the x
	 * @param y the y
	 * @return zones
	 */
	public List<ZoneType> getZones(int x, int y)
	{
		final List<ZoneType> temp = new ArrayList<>();
		for (ZoneType zone : getRegion(x, y).getZones().values())
		{
			if (zone.isInsideZone(x, y))
			{
				temp.add(zone);
			}
		}
		return temp;
	}
	
	/**
	 * Returns all zones from given coordinates.
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return zones
	 */
	public List<ZoneType> getZones(int x, int y, int z)
	{
		final List<ZoneType> temp = new ArrayList<>();
		for (ZoneType zone : getRegion(x, y).getZones().values())
		{
			if (zone.isInsideZone(x, y, z))
			{
				temp.add(zone);
			}
		}
		return temp;
	}
	
	/**
	 * Gets the zone.
	 * @param <T> the generic type
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @param type the type
	 * @return zone from given coordinates
	 */
	@SuppressWarnings("unchecked")
	private <T extends ZoneType> T getZone(int x, int y, int z, Class<T> type)
	{
		for (ZoneType zone : getRegion(x, y).getZones().values())
		{
			if (zone.isInsideZone(x, y, z) && type.isInstance(zone))
			{
				return (T) zone;
			}
		}
		return null;
	}
	
	/**
	 * Get spawm territory by name
	 * @param name name of territory to search
	 * @return link to zone form
	 */
	public SpawnTerritory getSpawnTerritory(String name)
	{
		return _spawnTerritories.containsKey(name) ? _spawnTerritories.get(name) : null;
	}
	
	/**
	 * Returns all spawm territories from where the object is located
	 * @param object
	 * @return zones
	 */
	public List<SpawnTerritory> getSpawnTerritories(WorldObject object)
	{
		final List<SpawnTerritory> temp = new ArrayList<>();
		for (SpawnTerritory territory : _spawnTerritories.values())
		{
			if (territory.isInsideZone(object.getX(), object.getY(), object.getZ()))
			{
				temp.add(territory);
			}
		}
		
		return temp;
	}
	
	/**
	 * Gets the olympiad stadium.
	 * @param creature the creature
	 * @return the olympiad stadium
	 */
	public OlympiadStadiumZone getOlympiadStadium(Creature creature)
	{
		if (creature == null)
		{
			return null;
		}
		
		for (ZoneType temp : getInstance().getZones(creature.getX(), creature.getY(), creature.getZ()))
		{
			if ((temp instanceof OlympiadStadiumZone) && temp.isCharacterInZone(creature))
			{
				return (OlympiadStadiumZone) temp;
			}
		}
		return null;
	}
	
	/**
	 * General storage for debug items used for visualizing zones.
	 * @return list of items
	 */
	public List<ItemInstance> getDebugItems()
	{
		if (_debugItems == null)
		{
			_debugItems = new ArrayList<>();
		}
		return _debugItems;
	}
	
	/**
	 * Remove all debug items from l2world.
	 */
	public void clearDebugItems()
	{
		if (_debugItems != null)
		{
			final Iterator<ItemInstance> it = _debugItems.iterator();
			while (it.hasNext())
			{
				final ItemInstance item = it.next();
				if (item != null)
				{
					item.decayMe();
				}
				it.remove();
			}
		}
	}
	
	public ZoneRegion getRegion(int x, int y)
	{
		try
		{
			return _zoneRegions[(x >> SHIFT_BY) + OFFSET_X][(y >> SHIFT_BY) + OFFSET_Y];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// LOGGER.warning(getClass().getSimpleName() + ": Incorrect zone region X: " + ((x >> SHIFT_BY) + OFFSET_X) + " Y: " + ((y >> SHIFT_BY) + OFFSET_Y) + " for coordinates x: " + x + " y: " + y);
			return null;
		}
	}
	
	public ZoneRegion getRegion(ILocational point)
	{
		return getRegion(point.getX(), point.getY());
	}
	
	/**
	 * Gets the settings.
	 * @param name the name
	 * @return the settings
	 */
	public static AbstractZoneSettings getSettings(String name)
	{
		return SETTINGS.get(name);
	}
	
	/**
	 * Gets the single instance of ZoneManager.
	 * @return single instance of ZoneManager
	 */
	public static ZoneManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneManager INSTANCE = new ZoneManager();
	}
}
