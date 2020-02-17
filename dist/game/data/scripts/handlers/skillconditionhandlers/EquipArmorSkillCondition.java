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
package handlers.skillconditionhandlers;

import java.util.List;

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.items.type.ArmorType;
import org.l2jbr.gameserver.model.skills.ISkillCondition;
import org.l2jbr.gameserver.model.skills.Skill;

/**
 * @author Sdw
 */
public class EquipArmorSkillCondition implements ISkillCondition
{
	private int _armorTypesMask = 0;
	
	public EquipArmorSkillCondition(StatsSet params)
	{
		final List<ArmorType> armorTypes = params.getEnumList("armorType", ArmorType.class);
		if (armorTypes != null)
		{
			for (ArmorType armorType : armorTypes)
			{
				_armorTypesMask |= armorType.mask();
			}
		}
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		if ((caster == null) || !caster.isPlayer())
		{
			return false;
		}
		
		final Inventory inv = caster.getInventory();
		
		// Get the itemMask of the weared chest (if exists)
		final ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if (chest == null)
		{
			return false;
		}
		final int chestMask = chest.getItem().getItemMask();
		
		// If chest armor is different from the condition one return false
		if ((_armorTypesMask & chestMask) == 0)
		{
			return false;
		}
		
		// So from here, chest armor matches conditions
		
		final long chestBodyPart = chest.getItem().getBodyPart();
		// return True if chest armor is a Full Armor
		if (chestBodyPart == Item.SLOT_FULL_ARMOR)
		{
			return true;
		}
		// check legs armor
		final ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		if (legs == null)
		{
			return false;
		}
		final int legMask = legs.getItem().getItemMask();
		// return true if legs armor matches too
		return (_armorTypesMask & legMask) != 0;
	}
}
