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
package org.l2jbr.gameserver.network.serverpackets.monsterbook;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.data.xml.impl.MonsterBookData;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.MonsterBookCardHolder;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Mobius
 */
public class ExMonsterBook implements IClientOutgoingPacket
{
	final PlayerInstance _player;
	final List<Integer> _cardIds = new ArrayList<>();
	
	public ExMonsterBook(PlayerInstance player)
	{
		_player = player;
		
		for (MonsterBookCardHolder card : MonsterBookData.getInstance().getMonsterBookCards())
		{
			if (player.getMonsterBookKillCount(card.getId()) > 0)
			{
				_cardIds.add(card.getId());
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_MONSTER_BOOK.writeId(packet);
		packet.writeH(_cardIds.size()); // loop count
		for (int cardId : _cardIds)
		{
			packet.writeH(cardId); // card id
			packet.writeC(_player.getMonsterBookRewardLevel(cardId)); // player reward level
			packet.writeD(_player.getMonsterBookKillCount(cardId)); // player kills
		}
		return true;
	}
}
