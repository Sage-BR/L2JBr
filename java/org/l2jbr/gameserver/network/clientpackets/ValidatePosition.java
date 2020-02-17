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

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.data.xml.impl.DoorData;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.serverpackets.GetOnVehicle;
import org.l2jbr.gameserver.network.serverpackets.ValidateLocation;

/**
 * @version $Revision: 1.13.4.7 $ $Date: 2005/03/27 15:29:30 $
 */
public class ValidatePosition implements IClientIncomingPacket
{
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	private int _data; // vehicle id
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_x = packet.readD();
		_y = packet.readD();
		_z = packet.readD();
		_heading = packet.readD();
		_data = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if ((player == null) || player.isTeleporting() || player.inObserverMode())
		{
			return;
		}
		
		final int realX = player.getX();
		final int realY = player.getY();
		int realZ = player.getZ();
		
		if ((_x == 0) && (_y == 0))
		{
			if (realX != 0)
			{
				return;
			}
		}
		
		int dx;
		int dy;
		int dz;
		double diffSq;
		
		if (player.isInBoat())
		{
			if (Config.COORD_SYNCHRONIZE == 2)
			{
				dx = _x - player.getInVehiclePosition().getX();
				dy = _y - player.getInVehiclePosition().getY();
				// dz = _z - player.getInVehiclePosition().getZ();
				diffSq = ((dx * dx) + (dy * dy));
				if (diffSq > 250000)
				{
					client.sendPacket(new GetOnVehicle(player.getObjectId(), _data, player.getInVehiclePosition()));
				}
			}
			return;
		}
		if (player.isInAirShip())
		{
			// Zoey76: TODO: Implement or cleanup.
			// if (Config.COORD_SYNCHRONIZE == 2)
			// {
			// dx = _x - player.getInVehiclePosition().getX();
			// dy = _y - player.getInVehiclePosition().getY();
			// dz = _z - player.getInVehiclePosition().getZ();
			// diffSq = ((dx * dx) + (dy * dy));
			// if (diffSq > 250000)
			// {
			// sendPacket(new GetOnVehicle(player.getObjectId(), _data, player.getInBoatPosition()));
			// }
			// }
			return;
		}
		
		if (player.isFalling(_z))
		{
			return; // disable validations during fall to avoid "jumping"
		}
		
		dx = _x - realX;
		dy = _y - realY;
		dz = _z - realZ;
		diffSq = ((dx * dx) + (dy * dy));
		
		// Zoey76: TODO: Implement or cleanup.
		// Party party = player.getParty();
		// if ((party != null) && (player.getLastPartyPositionDistance(_x, _y, _z) > 150))
		// {
		// player.setLastPartyPosition(_x, _y, _z);
		// party.broadcastToPartyMembers(player, new PartyMemberPosition(player));
		// }
		
		// Don't allow flying transformations outside gracia area!
		if (player.isFlyingMounted() && (_x > World.GRACIA_MAX_X))
		{
			player.untransform();
		}
		
		if (player.isFlying() || player.isInsideZone(ZoneId.WATER))
		{
			player.setXYZ(realX, realY, _z);
			if (diffSq > 90000)
			{
				player.sendPacket(new ValidateLocation(player));
			}
		}
		else if (diffSq < 360000) // if too large, messes observation
		{
			if (Config.COORD_SYNCHRONIZE == -1) // Only Z coordinate synched to server, mainly used when no geodata but can be used also with geodata
			{
				player.setXYZ(realX, realY, _z);
				return;
			}
			if (Config.COORD_SYNCHRONIZE == 1) // Trusting also client x,y coordinates (should not be used with geodata)
			{
				if (!player.isMoving() || !player.validateMovementHeading(_heading)) // Heading changed on client = possible obstacle
				{
					// character is not moving, take coordinates from client
					if (diffSq < 2500)
					{
						player.setXYZ(realX, realY, _z);
					}
					else
					{
						player.setXYZ(_x, _y, _z);
					}
				}
				else
				{
					player.setXYZ(realX, realY, _z);
				}
				
				player.setHeading(_heading);
				return;
			}
			// Sync 2 (or other),
			// intended for geodata. Sends a validation packet to client
			// when too far from server calculated true coordinate.
			// Due to geodata/zone errors, some Z axis checks are made. (maybe a temporary solution)
			// Important: this code part must work together with Creature.updatePosition
			if ((diffSq > 250000) || (Math.abs(dz) > 200))
			{
				if ((Math.abs(dz) > 200) && (Math.abs(dz) < 1500) && (Math.abs(_z - player.getClientZ()) < 800))
				{
					player.setXYZ(realX, realY, _z);
					realZ = _z;
				}
				else
				{
					if (player.isFalling(_z))
					{
						player.setXYZ(realX, realY, _z);
					}
					player.sendPacket(new ValidateLocation(player));
				}
			}
		}
		
		player.setClientX(_x);
		player.setClientY(_y);
		player.setClientZ(_z);
		player.setClientHeading(_heading); // No real need to validate heading.
		
		// Mobius: Check for possible door logout and move over exploit. Also checked at MoveBackwardToLocation.
		if (!DoorData.getInstance().checkIfDoorsBetween(realX, realY, realZ, _x, _y, _z, player.getInstanceWorld(), false))
		{
			player.setLastServerPosition(realX, realY, realZ);
		}
	}
}
