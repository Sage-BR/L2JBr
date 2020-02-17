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

import java.util.List;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.model.olympiad.OlympiadInfo;
import org.l2jbr.gameserver.network.OutgoingPackets;

/**
 * @author JIV
 */
public class ExOlympiadMatchResult implements IClientOutgoingPacket
{
	private final boolean _tie;
	private int _winTeam; // 1,2
	private int _loseTeam = 2;
	private final List<OlympiadInfo> _winnerList;
	private final List<OlympiadInfo> _loserList;
	
	public ExOlympiadMatchResult(boolean tie, int winTeam, List<OlympiadInfo> winnerList, List<OlympiadInfo> loserList)
	{
		_tie = tie;
		_winTeam = winTeam;
		_winnerList = winnerList;
		_loserList = loserList;
		
		if (_winTeam == 2)
		{
			_loseTeam = 1;
		}
		else if (_winTeam == 0)
		{
			_winTeam = 1;
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_RECEIVE_OLYMPIAD.writeId(packet);
		
		packet.writeD(0x01); // Type 0 = Match List, 1 = Match Result
		
		packet.writeD(_tie ? 1 : 0); // 0 - win, 1 - tie
		packet.writeS(_winnerList.get(0).getName());
		packet.writeD(_winTeam);
		packet.writeD(_winnerList.size());
		for (OlympiadInfo info : _winnerList)
		{
			packet.writeS(info.getName());
			packet.writeS(info.getClanName());
			packet.writeD(info.getClanId());
			packet.writeD(info.getClassId());
			packet.writeD(info.getDamage());
			packet.writeD(info.getCurrentPoints());
			packet.writeD(info.getDiffPoints());
			packet.writeD(0x00); // Helios
		}
		
		packet.writeD(_loseTeam);
		packet.writeD(_loserList.size());
		for (OlympiadInfo info : _loserList)
		{
			packet.writeS(info.getName());
			packet.writeS(info.getClanName());
			packet.writeD(info.getClanId());
			packet.writeD(info.getClassId());
			packet.writeD(info.getDamage());
			packet.writeD(info.getCurrentPoints());
			packet.writeD(info.getDiffPoints());
			packet.writeD(0x00); // Helios
		}
		return true;
	}
}
