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

import org.l2jbr.gameserver.data.xml.impl.CategoryData;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.handler.IItemHandler;
import org.l2jbr.gameserver.model.actor.Playable;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Mobius
 */
public class FatedSupportBox implements IItemHandler
{
	// Items
	private static final int FATED_BOX_ERTHEIA_WIZARD = 26229;
	private static final int FATED_BOX_ERTHEIA_FIGHTER = 26230;
	private static final int FATED_BOX_FIGHTER = 37315;
	private static final int FATED_BOX_WIZARD = 37316;
	private static final int FATED_BOX_WARRIOR = 37317;
	private static final int FATED_BOX_ROGUE = 37318;
	private static final int FATED_BOX_KAMAEL = 37319;
	private static final int FATED_BOX_ORC_FIGHTER = 37320;
	private static final int FATED_BOX_ORC_WIZARD = 37321;
	
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final PlayerInstance player = playable.getActingPlayer();
		final Race race = player.getRace();
		final ClassId classId = player.getClassId();
		
		if (!player.isInventoryUnder80(false))
		{
			player.sendPacket(SystemMessageId.YOU_VE_EXCEEDED_THE_LIMIT_AND_CANNOT_RETRIEVE_THE_ITEM_PLEASE_CHECK_YOUR_LIMIT_IN_THE_INVENTORY);
			return false;
		}
		
		// Characters that have gone through their 2nd class transfer/1st liberation will be able to open the Fated Support Box at level 40.
		if ((player.getLevel() < 40) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP) || ((race != Race.ERTHEIA) && player.isInCategory(CategoryType.SECOND_CLASS_GROUP)))
		{
			player.sendPacket(new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item));
			return false;
		}
		
		player.getInventory().destroyItem(getClass().getSimpleName(), item, 1, player, null);
		player.sendPacket(new InventoryUpdate(item));
		
		// It will stay in your inventory after use until you reach level 84.
		if (player.getLevel() > 84)
		{
			player.sendMessage("Fated Support Box was removed because your level has exceeded the maximum requirement."); // custom message
			return true;
		}
		
		switch (race)
		{
			case HUMAN:
			case ELF:
			case DARK_ELF:
			case DWARF:
			{
				if (player.isMageClass())
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_WIZARD, 1, player, true);
				}
				else if (CategoryData.getInstance().isInCategory(CategoryType.SUB_GROUP_ROGUE, classId.getId()))
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_ROGUE, 1, player, true);
				}
				else if (CategoryData.getInstance().isInCategory(CategoryType.SUB_GROUP_KNIGHT, classId.getId()))
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_FIGHTER, 1, player, true);
				}
				else
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_WARRIOR, 1, player, true);
				}
				break;
			}
			case ORC:
			{
				if (player.isMageClass())
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_ORC_WIZARD, 1, player, true);
				}
				else
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_ORC_FIGHTER, 1, player, true);
				}
				break;
			}
			case KAMAEL:
			{
				player.addItem(getClass().getSimpleName(), FATED_BOX_KAMAEL, 1, player, true);
				break;
			}
			case ERTHEIA:
			{
				if (player.isMageClass())
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_ERTHEIA_WIZARD, 1, player, true);
				}
				else
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_ERTHEIA_FIGHTER, 1, player, true);
				}
				break;
			}
		}
		return true;
	}
}
