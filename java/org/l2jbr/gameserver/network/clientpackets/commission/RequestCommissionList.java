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
package org.l2jbr.gameserver.network.clientpackets.commission;

import java.util.function.Predicate;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.instancemanager.CommissionManager;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.commission.CommissionItemType;
import org.l2jbr.gameserver.model.commission.CommissionTreeType;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.type.CrystalType;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.commission.ExCloseCommission;

/**
 * @author NosBit
 */
public class RequestCommissionList implements IClientIncomingPacket
{
	private int _treeViewDepth;
	private int _itemType;
	private int _type;
	private int _grade;
	private String _query;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_treeViewDepth = packet.readD();
		_itemType = packet.readD();
		_type = packet.readD();
		_grade = packet.readD();
		_query = packet.readS();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!CommissionManager.isPlayerAllowedToInteract(player))
		{
			client.sendPacket(ExCloseCommission.STATIC_PACKET);
			return;
		}
		
		Predicate<Item> filter = i -> true;
		switch (_treeViewDepth)
		{
			case 1:
			{
				final CommissionTreeType commissionTreeType = CommissionTreeType.findByClientId(_itemType);
				if (commissionTreeType != null)
				{
					filter = filter.and(i -> commissionTreeType.getCommissionItemTypes().contains(i.getCommissionItemType()));
				}
				break;
			}
			case 2:
			{
				final CommissionItemType commissionItemType = CommissionItemType.findByClientId(_itemType);
				if (commissionItemType != null)
				{
					filter = filter.and(i -> i.getCommissionItemType() == commissionItemType);
				}
				break;
			}
		}
		
		switch (_type)
		{
			case 0: // General
			{
				filter = filter.and(i -> true); // TODO: condition
				break;
			}
			case 1: // Rare
			{
				filter = filter.and(i -> true); // TODO: condition
				break;
			}
		}
		
		switch (_grade)
		{
			case 0:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.NONE);
				break;
			}
			case 1:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.D);
				break;
			}
			case 2:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.C);
				break;
			}
			case 3:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.B);
				break;
			}
			case 4:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.A);
				break;
			}
			case 5:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.S);
				break;
			}
			case 6:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.S80);
				break;
			}
			case 7:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.R);
				break;
			}
			case 8:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.R95);
				break;
			}
			case 9:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.R99);
				break;
			}
			case 10:
			{
				filter = filter.and(i -> i.getCrystalType() == CrystalType.R110);
				break;
			}
		}
		
		filter = filter.and(i -> _query.isEmpty() || i.getName().toLowerCase().contains(_query.toLowerCase()));
		
		CommissionManager.getInstance().showAuctions(player, filter);
	}
}
