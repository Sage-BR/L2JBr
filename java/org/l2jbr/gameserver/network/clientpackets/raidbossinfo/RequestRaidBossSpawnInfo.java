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
package org.l2jbr.gameserver.network.clientpackets.raidbossinfo;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.enums.RaidBossStatus;
import org.l2jbr.gameserver.instancemanager.DBSpawnManager;
import org.l2jbr.gameserver.instancemanager.GrandBossManager;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jbr.gameserver.network.serverpackets.raidbossinfo.ExRaidBossSpawnInfo;

/**
 * @author Mobius
 */
public class RequestRaidBossSpawnInfo implements IClientIncomingPacket
{
	private final List<Integer> _bossIds = new ArrayList<>();
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		final int count = packet.readD();
		for (int i = 0; i < count; i++)
		{
			final int bossId = packet.readD();
			if (DBSpawnManager.getInstance().getNpcStatusId(bossId) == RaidBossStatus.ALIVE)
			{
				_bossIds.add(bossId);
			}
			else if (GrandBossManager.getInstance().getBossStatus(bossId) == 0)
			{
				_bossIds.add(bossId);
			}
			/*
			 * else { String message = "Could not find spawn info for boss " + bossId; final NpcTemplate template = NpcData.getInstance().getTemplate(bossId); if (template != null) { message += " - " + template.getName() + "."; } else { message += " - NPC template not found."; }
			 * System.out.println(message); }
			 */
		}
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		client.sendPacket(new ExRaidBossSpawnInfo(_bossIds));
	}
}
