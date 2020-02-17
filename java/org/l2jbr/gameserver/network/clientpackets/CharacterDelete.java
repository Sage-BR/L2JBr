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
package org.l2jbr.gameserver.network.clientpackets;

import java.util.logging.Level;

import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.enums.CharacterDeleteFailType;
import org.l2jbr.gameserver.model.CharSelectInfoPackage;
import org.l2jbr.gameserver.model.events.Containers;
import org.l2jbr.gameserver.model.events.EventDispatcher;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerDelete;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.serverpackets.CharDeleteFail;
import org.l2jbr.gameserver.network.serverpackets.CharDeleteSuccess;
import org.l2jbr.gameserver.network.serverpackets.CharSelectionInfo;

/**
 * @version $Revision: 1.8.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class CharacterDelete implements IClientIncomingPacket
{
	// cd
	private int _charSlot;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_charSlot = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		// if (!client.getFloodProtectors().getCharacterSelect().tryPerformAction("CharacterDelete"))
		// {
		// client.sendPacket(new CharDeleteFail(CharacterDeleteFailType.UNKNOWN));
		// return;
		// }
		
		try
		{
			final CharacterDeleteFailType failType = client.markToDeleteChar(_charSlot);
			switch (failType)
			{
				case NONE:// Success!
				{
					client.sendPacket(new CharDeleteSuccess());
					final CharSelectInfoPackage charInfo = client.getCharSelection(_charSlot);
					EventDispatcher.getInstance().notifyEvent(new OnPlayerDelete(charInfo.getObjectId(), charInfo.getName(), client), Containers.Players());
					break;
				}
				default:
				{
					client.sendPacket(new CharDeleteFail(failType));
					break;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Error:", e);
		}
		
		final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1, 0);
		client.sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
	}
}
