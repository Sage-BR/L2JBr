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
package handlers.playeractions;

import org.l2jbr.gameserver.handler.IPlayerActionHandler;
import org.l2jbr.gameserver.model.ActionDataHolder;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;

/**
 * Tactical Signs targeting player action handler.
 * @author Nik
 */
public class TacticalSignTarget implements IPlayerActionHandler
{
	@Override
	public void useAction(PlayerInstance player, ActionDataHolder data, boolean ctrlPressed, boolean shiftPressed)
	{
		if (!player.isInParty())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.getParty().setTargetBasedOnTacticalSignId(player, data.getOptionId());
	}
}
