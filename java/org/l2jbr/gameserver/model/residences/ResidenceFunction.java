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
package org.l2jbr.gameserver.model.residences;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.data.sql.impl.ClanTable;
import org.l2jbr.gameserver.data.xml.impl.ResidenceFunctionsData;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.itemcontainer.ItemContainer;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.serverpackets.AgitDecoInfo;

/**
 * @author UnAfraid
 */
public class ResidenceFunction
{
	private final int _id;
	private final int _level;
	private long _expiration;
	private final AbstractResidence _residense;
	private ScheduledFuture<?> _task;
	
	public ResidenceFunction(int id, int level, long expiration, AbstractResidence residense)
	{
		_id = id;
		_level = level;
		_expiration = expiration;
		_residense = residense;
		init();
	}
	
	public ResidenceFunction(int id, int level, AbstractResidence residense)
	{
		_id = id;
		_level = level;
		final ResidenceFunctionTemplate template = getTemplate();
		_expiration = Instant.now().toEpochMilli() + template.getDuration().toMillis();
		_residense = residense;
		init();
	}
	
	/**
	 * Initializes the function task
	 */
	private void init()
	{
		final ResidenceFunctionTemplate template = getTemplate();
		if ((template != null) && (_expiration > System.currentTimeMillis()))
		{
			_task = ThreadPool.schedule(this::onFunctionExpiration, _expiration - System.currentTimeMillis());
		}
	}
	
	/**
	 * @return the function id
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return the function level
	 */
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * @return the expiration of this function instance
	 */
	public long getExpiration()
	{
		return _expiration;
	}
	
	/**
	 * @return the owner (clan) of this function instance
	 */
	public int getOwnerId()
	{
		return _residense.getOwnerId();
	}
	
	/**
	 * @return value of the function
	 */
	public double getValue()
	{
		final ResidenceFunctionTemplate template = getTemplate();
		return template == null ? 0 : template.getValue();
	}
	
	/**
	 * @return the type of this function instance
	 */
	public ResidenceFunctionType getType()
	{
		final ResidenceFunctionTemplate template = getTemplate();
		return template == null ? ResidenceFunctionType.NONE : template.getType();
	}
	
	/**
	 * @return the template of this function instance
	 */
	public ResidenceFunctionTemplate getTemplate()
	{
		return ResidenceFunctionsData.getInstance().getFunction(_id, _level);
	}
	
	/**
	 * The function invoked when task run, it either re-activate the function or removes it (In case clan doesn't cannot pay for it)
	 */
	private void onFunctionExpiration()
	{
		if (!reactivate())
		{
			_residense.removeFunction(this);
			
			final Clan clan = ClanTable.getInstance().getClan(_residense.getOwnerId());
			if (clan != null)
			{
				clan.broadcastToOnlineMembers(new AgitDecoInfo(_residense));
			}
		}
	}
	
	/**
	 * @return {@code true} if function instance is re-activated successfully, {@code false} otherwise
	 */
	public boolean reactivate()
	{
		final ResidenceFunctionTemplate template = getTemplate();
		if (template == null)
		{
			return false;
		}
		
		final Clan clan = ClanTable.getInstance().getClan(_residense.getOwnerId());
		if (clan == null)
		{
			return false;
		}
		
		final ItemContainer wh = clan.getWarehouse();
		final ItemInstance item = wh.getItemByItemId(template.getCost().getId());
		if ((item == null) || (item.getCount() < template.getCost().getCount()))
		{
			return false;
		}
		
		if (wh.destroyItem("FunctionFee", item, template.getCost().getCount(), null, this) != null)
		{
			_expiration = System.currentTimeMillis() + (template.getDuration().getSeconds() * 1000);
			init();
		}
		return true;
	}
	
	/**
	 * Cancels the task to {@link #onFunctionExpiration()}
	 */
	public void cancelExpiration()
	{
		if ((_task != null) && !_task.isDone())
		{
			_task.cancel(true);
		}
		_task = null;
	}
}
