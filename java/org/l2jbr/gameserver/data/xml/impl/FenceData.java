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
package org.l2jbr.gameserver.data.xml.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.enums.FenceState;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldRegion;
import org.l2jbr.gameserver.model.actor.instance.FenceInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;

/**
 * @author HoridoJoho / FBIagent
 */
public class FenceData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FenceData.class.getSimpleName());
	
	private static final int MAX_Z_DIFF = 100;
	
	private final Map<WorldRegion, List<FenceInstance>> _regions = new ConcurrentHashMap<>();
	private final Map<Integer, FenceInstance> _fences = new ConcurrentHashMap<>();
	
	protected FenceData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		if (!_fences.isEmpty())
		{
			// Remove old fences when reloading
			_fences.values().forEach(this::removeFence);
		}
		
		parseDatapackFile("data/FenceData.xml");
		LOGGER.info("Loaded " + _fences.size() + " fences.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "fence", this::spawnFence));
	}
	
	public int getLoadedElementsCount()
	{
		return _fences.size();
	}
	
	private void spawnFence(Node fenceNode)
	{
		final StatsSet set = new StatsSet(parseAttributes(fenceNode));
		spawnFence(set.getInt("x"), set.getInt("y"), set.getInt("z"), set.getString("name"), set.getInt("width"), set.getInt("length"), set.getInt("height"), 0, set.getEnum("state", FenceState.class, FenceState.CLOSED));
	}
	
	public FenceInstance spawnFence(int x, int y, int z, int width, int length, int height, int instanceId, FenceState state)
	{
		return spawnFence(x, y, z, null, width, length, height, instanceId, state);
	}
	
	public FenceInstance spawnFence(int x, int y, int z, String name, int width, int length, int height, int instanceId, FenceState state)
	{
		final FenceInstance fence = new FenceInstance(x, y, name, width, length, height, state);
		if (instanceId > 0)
		{
			fence.setInstanceById(instanceId);
		}
		fence.spawnMe(x, y, z);
		addFence(fence);
		
		return fence;
	}
	
	private void addFence(FenceInstance fence)
	{
		_fences.put(fence.getObjectId(), fence);
		_regions.computeIfAbsent(World.getInstance().getRegion(fence), key -> new ArrayList<>()).add(fence);
	}
	
	public void removeFence(FenceInstance fence)
	{
		_fences.remove(fence.getObjectId());
		
		final List<FenceInstance> fencesInRegion = _regions.get(World.getInstance().getRegion(fence));
		if (fencesInRegion != null)
		{
			fencesInRegion.remove(fence);
		}
	}
	
	public Map<Integer, FenceInstance> getFences()
	{
		return _fences;
	}
	
	public FenceInstance getFence(int objectId)
	{
		return _fences.get(objectId);
	}
	
	public boolean checkIfFenceBetween(int x, int y, int z, int tx, int ty, int tz, Instance instance)
	{
		final Predicate<FenceInstance> filter = fence ->
		{
			// Check if fence is geodata enabled.
			if (!fence.getState().isGeodataEnabled())
			{
				return false;
			}
			
			// Check if fence is within the instance we search for.
			final int instanceId = (instance == null) ? 0 : instance.getId();
			if (fence.getInstanceId() != instanceId)
			{
				return false;
			}
			
			final int xMin = fence.getXMin();
			final int xMax = fence.getXMax();
			final int yMin = fence.getYMin();
			final int yMax = fence.getYMax();
			if ((x < xMin) && (tx < xMin))
			{
				return false;
			}
			if ((x > xMax) && (tx > xMax))
			{
				return false;
			}
			if ((y < yMin) && (ty < yMin))
			{
				return false;
			}
			if ((y > yMax) && (ty > yMax))
			{
				return false;
			}
			if ((x > xMin) && (tx > xMin) && (x < xMax) && (tx < xMax))
			{
				if ((y > yMin) && (ty > yMin) && (y < yMax) && (ty < yMax))
				{
					return false;
				}
			}
			
			if (crossLinePart(xMin, yMin, xMax, yMin, x, y, tx, ty, xMin, yMin, xMax, yMax) || crossLinePart(xMax, yMin, xMax, yMax, x, y, tx, ty, xMin, yMin, xMax, yMax) || crossLinePart(xMax, yMax, xMin, yMax, x, y, tx, ty, xMin, yMin, xMax, yMax) || crossLinePart(xMin, yMax, xMin, yMin, x, y, tx, ty, xMin, yMin, xMax, yMax))
			{
				if ((z > (fence.getZ() - MAX_Z_DIFF)) && (z < (fence.getZ() + MAX_Z_DIFF)))
				{
					return true;
				}
			}
			
			return false;
		};
		
		final WorldRegion region = World.getInstance().getRegion(x, y); // Should never be null.
		return region == null ? false : _regions.getOrDefault(region, Collections.emptyList()).stream().anyMatch(filter);
	}
	
	private boolean crossLinePart(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double xMin, double yMin, double xMax, double yMax)
	{
		final double[] result = intersection(x1, y1, x2, y2, x3, y3, x4, y4);
		if (result == null)
		{
			return false;
		}
		
		final double xCross = result[0];
		final double yCross = result[1];
		if ((xCross <= xMax) && (xCross >= xMin))
		{
			return true;
		}
		if ((yCross <= yMax) && (yCross >= yMin))
		{
			return true;
		}
		
		return false;
	}
	
	private double[] intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
	{
		final double d = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
		if (d == 0)
		{
			return null;
		}
		
		final double xi = (((x3 - x4) * ((x1 * y2) - (y1 * x2))) - ((x1 - x2) * ((x3 * y4) - (y3 * x4)))) / d;
		final double yi = (((y3 - y4) * ((x1 * y2) - (y1 * x2))) - ((y1 - y2) * ((x3 * y4) - (y3 * x4)))) / d;
		
		return new double[]
		{
			xi,
			yi
		};
	}
	
	public static FenceData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FenceData INSTANCE = new FenceData();
	}
}