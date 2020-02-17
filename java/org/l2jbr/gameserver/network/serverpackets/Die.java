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
package org.l2jbr.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.instancemanager.FortManager;
import org.l2jbr.gameserver.model.SiegeClan;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.entity.Castle;
import org.l2jbr.gameserver.model.entity.Fort;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author UnAfraid, Nos
 */
public class Die implements IClientOutgoingPacket
{
	private final int _objectId;
	private boolean _toVillage;
	private boolean _toClanHall;
	private boolean _toCastle;
	private boolean _toOutpost;
	private final boolean _isSweepable;
	private boolean _useFeather;
	private boolean _toFortress;
	private boolean _hideAnimation;
	private List<Integer> _items = null;
	private boolean _itemsEnabled;
	
	public Die(Creature creature)
	{
		_objectId = creature.getObjectId();
		if (creature.isPlayer())
		{
			final Clan clan = creature.getActingPlayer().getClan();
			boolean isInCastleDefense = false;
			boolean isInFortDefense = false;
			
			SiegeClan siegeClan = null;
			final Castle castle = CastleManager.getInstance().getCastle(creature);
			final Fort fort = FortManager.getInstance().getFort(creature);
			if ((castle != null) && castle.getSiege().isInProgress())
			{
				siegeClan = castle.getSiege().getAttackerClan(clan);
				isInCastleDefense = (siegeClan == null) && castle.getSiege().checkIsDefender(clan);
			}
			else if ((fort != null) && fort.getSiege().isInProgress())
			{
				siegeClan = fort.getSiege().getAttackerClan(clan);
				isInFortDefense = (siegeClan == null) && fort.getSiege().checkIsDefender(clan);
			}
			
			_toVillage = creature.canRevive() && !creature.isPendingRevive();
			_toClanHall = (clan != null) && (clan.getHideoutId() > 0);
			_toCastle = ((clan != null) && (clan.getCastleId() > 0)) || isInCastleDefense;
			_toOutpost = ((siegeClan != null) && !isInCastleDefense && !isInFortDefense && !siegeClan.getFlag().isEmpty());
			_useFeather = creature.getAccessLevel().allowFixedRes() || creature.getInventory().haveItemForSelfResurrection();
			_toFortress = ((clan != null) && (clan.getFortId() > 0)) || isInFortDefense;
		}
		
		_isSweepable = creature.isAttackable() && creature.isSweepActive();
	}
	
	public void setHideAnimation(boolean val)
	{
		_hideAnimation = val;
	}
	
	public void addItem(int itemId)
	{
		if (_items == null)
		{
			_items = new ArrayList<>(8);
		}
		
		if (_items.size() < 8)
		{
			_items.add(itemId);
		}
		else
		{
			throw new IndexOutOfBoundsException("Die packet doesn't support more then 8 items!");
		}
	}
	
	public List<Integer> getItems()
	{
		return _items != null ? _items : Collections.emptyList();
	}
	
	public void setItemsEnabled(boolean val)
	{
		_itemsEnabled = val;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.DIE.writeId(packet);
		
		packet.writeD(_objectId);
		packet.writeD(_toVillage ? 0x01 : 0x00);
		packet.writeD(_toClanHall ? 0x01 : 0x00);
		packet.writeD(_toCastle ? 0x01 : 0x00);
		packet.writeD(_toOutpost ? 0x01 : 0x00);
		packet.writeD(_isSweepable ? 0x01 : 0x00);
		packet.writeD(_useFeather ? 0x01 : 0x00);
		packet.writeD(_toFortress ? 0x01 : 0x00);
		packet.writeD(0x00); // Disables use Feather button for X seconds
		packet.writeD(0x00); // Adventure's Song
		packet.writeC(_hideAnimation ? 0x01 : 0x00);
		
		packet.writeD(_itemsEnabled ? 0x01 : 0x00);
		packet.writeD(getItems().size());
		getItems().forEach(packet::writeD);
		return true;
	}
}
