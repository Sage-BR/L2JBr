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
package handlers.itemhandlers;

import org.l2jbr.gameserver.data.xml.impl.AppearanceItemData;
import org.l2jbr.gameserver.handler.IItemHandler;
import org.l2jbr.gameserver.model.actor.Playable;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.request.ShapeShiftingItemRequest;
import org.l2jbr.gameserver.model.items.appearance.AppearanceStone;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.appearance.ExChooseShapeShiftingItem;

/**
 * @author UnAfraid
 */
public class Appearance implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final PlayerInstance player = playable.getActingPlayer();
		if (player.hasRequest(ShapeShiftingItemRequest.class))
		{
			player.sendPacket(SystemMessageId.APPEARANCE_MODIFICATION_OR_RESTORATION_IN_PROGRESS_PLEASE_TRY_AGAIN_AFTER_COMPLETING_THIS_TASK);
			return false;
		}
		
		final AppearanceStone stone = AppearanceItemData.getInstance().getStone(item.getId());
		if (stone == null)
		{
			player.sendMessage("This item is either not an appearance stone or is currently not handled!");
			return false;
		}
		
		player.addRequest(new ShapeShiftingItemRequest(player, item));
		player.sendPacket(new ExChooseShapeShiftingItem(stone));
		return true;
	}
}
