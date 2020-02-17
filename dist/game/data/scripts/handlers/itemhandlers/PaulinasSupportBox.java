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

/**
 * @author Mobius
 */
public class PaulinasSupportBox implements IItemHandler
{
	// Items
	private static final int BOX_D_GRADE = 46849;
	private static final int BOX_C_GRADE = 46850;
	private static final int BOX_A_GRADE = 46851;
	private static final int BOX_S_GRADE = 46852;
	private static final int BOX_R_GRADE = 46919;
	// Rewards
	private static final int BOX_D_HEAVY = 46837;
	private static final int BOX_D_LIGHT = 46838;
	private static final int BOX_D_ROBE = 46839;
	private static final int BOX_C_HEAVY = 46840;
	private static final int BOX_C_LIGHT = 46841;
	private static final int BOX_C_ROBE = 46842;
	private static final int BOX_A_HEAVY = 46843;
	private static final int BOX_A_LIGHT = 46844;
	private static final int BOX_A_ROBE = 46845;
	private static final int BOX_S_HEAVY = 46846;
	private static final int BOX_S_LIGHT = 46847;
	private static final int BOX_S_ROBE = 46848;
	private static final int BOX_R_HEAVY = 46924;
	private static final int BOX_R_LIGHT = 46925;
	private static final int BOX_R_ROBE = 46926;
	// D-Grade weapon rewards
	private static final int WEAPON_SWORD_D = 46791;
	private static final int WEAPON_GSWORD_D = 46792;
	private static final int WEAPON_BLUNT_D = 46793;
	private static final int WEAPON_FIST_D = 46794;
	// private static final int WEAPON_BOW_D = 46795;
	private static final int WEAPON_DAGGER_D = 46796;
	private static final int WEAPON_STAFF_D = 46797;
	// private static final int WEAPON_ANCIENT_D = 46798;
	// private static final int WEAPON_RAPIER_D = 46799;
	private static final int WEAPON_CROSSBOW_D = 46795;
	// C-Grade weapon rewards
	private static final int WEAPON_SWORD_C = 46801;
	private static final int WEAPON_GSWORD_C = 46802;
	private static final int WEAPON_BLUNT_C = 46803;
	private static final int WEAPON_FIST_C = 46804;
	private static final int WEAPON_SPEAR_C = 46805;
	private static final int WEAPON_BOW_C = 46806;
	private static final int WEAPON_DAGGER_C = 46807;
	private static final int WEAPON_STAFF_C = 46808;
	private static final int WEAPON_DUALSWORD_C = 46809;
	// private static final int WEAPON_ANCIENT_C = 46810;
	private static final int WEAPON_RAPIER_C = 46811;
	private static final int WEAPON_CROSSBOW_C = 46812;
	// A-Grade weapon rewards
	private static final int WEAPON_SWORD_A = 46813;
	private static final int WEAPON_GSWORD_A = 46814;
	private static final int WEAPON_BLUNT_A = 46815;
	private static final int WEAPON_FIST_A = 46816;
	private static final int WEAPON_SPEAR_A = 46817;
	private static final int WEAPON_BOW_A = 46818;
	private static final int WEAPON_DAGGER_A = 46819;
	private static final int WEAPON_STAFF_A = 46820;
	private static final int WEAPON_DUALSWORD_A = 46821;
	// private static final int WEAPON_ANCIENT_A = 46822;
	private static final int WEAPON_RAPIER_A = 46823;
	private static final int WEAPON_CROSSBOW_A = 46824;
	// S-Grade weapon rewards
	private static final int WEAPON_SWORD_S = 46825;
	private static final int WEAPON_GSWORD_S = 46826;
	private static final int WEAPON_BLUNT_S = 46827;
	private static final int WEAPON_FIST_S = 46828;
	private static final int WEAPON_SPEAR_S = 46829;
	private static final int WEAPON_BOW_S = 46830;
	private static final int ARROW_OF_LIGHT_S = 1345;
	private static final int WEAPON_DAGGER_S = 46831;
	private static final int WEAPON_STAFF_S = 46832;
	private static final int WEAPON_DUALSWORD_S = 46833;
	// private static final int WEAPON_ANCIENT_S = 46834;
	private static final int WEAPON_RAPIER_S = 46835;
	private static final int WEAPON_CROSSBOW_S = 46836;
	private static final int BOLT_OF_LIGHT_S = 9637;
	// R-Grade weapon rewards
	private static final int WEAPON_SWORD_R = 47008;
	private static final int WEAPON_SHIELD_R = 47026;
	private static final int WEAPON_GSWORD_R = 47009;
	private static final int WEAPON_BLUNT_R = 47010;
	private static final int WEAPON_FIST_R = 47011;
	private static final int WEAPON_SPEAR_R = 47012;
	private static final int WEAPON_BOW_R = 47013;
	private static final int ORICHALCUM_ARROW_R = 18550;
	private static final int WEAPON_DUALDAGGER_R = 47019;
	private static final int WEAPON_CASTER_R = 47016;
	private static final int WEAPON_SIGIL_R = 47037;
	private static final int WEAPON_STAFF_R = 47017;
	private static final int WEAPON_DUALSWORD_R = 47018;
	private static final int WEAPON_CROSSBOW_R = 47014;
	private static final int ORICHALCUM_BOLT_R = 19443;
	
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
		
		player.getInventory().destroyItem(getClass().getSimpleName(), item, 1, player, null);
		player.sendPacket(new InventoryUpdate(item));
		
		switch (item.getId())
		{
			case BOX_D_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_D_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_D, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_ROGUE_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_D_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DAGGER_D, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_WARRIOR_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_D_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SWORD_D, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_KNIGHT_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_D_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_D, 1, player, true);
						}
						break;
					}
					case DWARF:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.DWARF_BOUNTY_CLASS, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_D_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DAGGER_D, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_D_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_BLUNT_D, 1, player, true);
						}
					}
					case ORC:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_D_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_D, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.LIGHT_ARMOR_CLASS, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_D_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_D, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_D_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_D, 1, player, true);
						}
						break;
					}
					case KAMAEL:
					{
						player.addItem(getClass().getSimpleName(), BOX_D_LIGHT, 1, player, true);
						
						if (CategoryData.getInstance().isInCategory(CategoryType.KAMAEL_FEMALE_MAIN_OCCUPATION, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), WEAPON_CROSSBOW_D, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_D, 1, player, true);
						}
						break;
					}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_D_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_D, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_D_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_D, 1, player, true);
						}
						break;
					}
				}
				break;
			}
			case BOX_C_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.DIVISION_WIZARD, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_C_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_C, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SUBJOB_GROUP_BOW, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_BOW_C, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SUBJOB_GROUP_DAGGER, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DAGGER_C, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SUBJOB_GROUP_DANCE, classId.getId()) || (player.getClassId() == ClassId.GLADIATOR))
						{
							player.addItem(getClass().getSimpleName(), BOX_C_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DUALSWORD_C, 1, player, true);
						}
						else if (player.getClassId() == ClassId.WARLORD)
						{
							player.addItem(getClass().getSimpleName(), BOX_C_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SPEAR_C, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_WARRIOR_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_C_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SWORD_C, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_C_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_C, 1, player, true);
						}
						break;
					}
					case DWARF:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.DWARF_BOUNTY_CLASS, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DAGGER_C, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_C_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_BLUNT_C, 1, player, true);
						}
						break;
					}
					case ORC:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_C, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.LIGHT_ARMOR_CLASS, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_C, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_C_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_C, 1, player, true);
						}
						break;
					}
					case KAMAEL:
					{
						player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
						
						if (CategoryData.getInstance().isInCategory(CategoryType.DIVISION_WIZARD, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), WEAPON_RAPIER_C, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.DIVISION_ARCHER, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), WEAPON_CROSSBOW_C, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_C, 1, player, true);
						}
						break;
					}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_C_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_C, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_C, 1, player, true);
						}
						break;
					}
				}
				break;
			}
			case BOX_A_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.DIVISION_WIZARD, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_A_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_A, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SUBJOB_GROUP_BOW, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_BOW_A, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SUBJOB_GROUP_DAGGER, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DAGGER_A, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SUBJOB_GROUP_DANCE, classId.getId()) || (player.getClassId() == ClassId.GLADIATOR))
						{
							player.addItem(getClass().getSimpleName(), BOX_A_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DUALSWORD_A, 1, player, true);
						}
						else if (player.getClassId() == ClassId.WARLORD)
						{
							player.addItem(getClass().getSimpleName(), BOX_A_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SPEAR_A, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_WARRIOR_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_A_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SWORD_A, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_A_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_A, 1, player, true);
						}
						break;
					}
					case DWARF:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.DWARF_BOUNTY_CLASS, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DAGGER_A, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_A_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_BLUNT_A, 1, player, true);
						}
						break;
					}
					case ORC:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_A_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_A, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.LIGHT_ARMOR_CLASS, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_A, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_A_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SWORD_A, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_A, 1, player, true);
							
						}
						break;
					}
					case KAMAEL:
					{
						player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
						
						if (CategoryData.getInstance().isInCategory(CategoryType.DIVISION_WIZARD, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), WEAPON_RAPIER_A, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.DIVISION_ARCHER, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), WEAPON_CROSSBOW_A, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_A, 1, player, true);
						}
						break;
					}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_A_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_A, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_A, 1, player, true);
						}
						break;
					}
				}
				break;
			}
			case BOX_S_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.DIVISION_WIZARD, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_S_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_S, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SUBJOB_GROUP_BOW, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), ARROW_OF_LIGHT_S, 5000, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_BOW_S, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SUBJOB_GROUP_DAGGER, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DAGGER_S, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SUBJOB_GROUP_DANCE, classId.getId()) || (player.getClassId() == ClassId.DUELIST))
						{
							player.addItem(getClass().getSimpleName(), BOX_S_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DUALSWORD_S, 1, player, true);
						}
						else if (player.getClassId() == ClassId.DREADNOUGHT)
						{
							player.addItem(getClass().getSimpleName(), BOX_S_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SPEAR_S, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_WARRIOR_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_S_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SWORD_S, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_S_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_S, 1, player, true);
						}
						break;
					}
					case DWARF:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.DWARF_BOUNTY_CLASS, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DAGGER_S, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_S_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_BLUNT_S, 1, player, true);
						}
						break;
					}
					case ORC:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_S_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_S, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.LIGHT_ARMOR_CLASS, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_S, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_S_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_S, 1, player, true);
						}
						break;
					}
					case KAMAEL:
					{
						player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
						
						if (CategoryData.getInstance().isInCategory(CategoryType.DIVISION_WIZARD, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), WEAPON_RAPIER_S, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.DIVISION_ARCHER, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), WEAPON_CROSSBOW_S, 1, player, true);
							player.addItem(getClass().getSimpleName(), BOLT_OF_LIGHT_S, 5000, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_S, 1, player, true);
						}
						break;
					}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_S_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_S, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_S, 1, player, true);
						}
						break;
					}
				}
				break;
			}
			case BOX_R_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_FEOH_GROUP, classId.getId()) || (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_WYNN_GROUP, classId.getId())))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_R, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_EOLH_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_CASTER_R, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SIGIL_R, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_OTHEL_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DUALDAGGER_R, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_YR_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), ORICHALCUM_ARROW_R, 5000, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_BOW_R, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_IS_GROUP, classId.getId()) || (player.getClassId() == ClassId.TYRR_DUELIST))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DUALSWORD_R, 1, player, true);
						}
						else if (player.getClassId() == ClassId.TYRR_DREADNOUGHT)
						{
							player.addItem(getClass().getSimpleName(), BOX_R_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SPEAR_R, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_SIGEL_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SWORD_R, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SHIELD_R, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_R_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_SWORD_R, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_R, 1, player, true);
						}
						break;
					}
					case DWARF:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_OTHEL_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DUALDAGGER_R, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_R_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_BLUNT_R, 1, player, true);
						}
						break;
					}
					case ORC:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_IS_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_DUALSWORD_R, 1, player, true);
						}
						else if (player.getClassId() == ClassId.TYRR_GRAND_KHAVATARI)
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_R, 1, player, true);
						}
						else if (player.getClassId() == ClassId.TYRR_TITAN)
						{
							player.addItem(getClass().getSimpleName(), BOX_R_HEAVY, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_R, 1, player, true);
						}
						break;
					}
					case KAMAEL:
					{
						if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_FEOH_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_R, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_YR_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), ORICHALCUM_BOLT_R, 5000, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_CROSSBOW_R, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_GSWORD_R, 1, player, true);
							break;
						}
					}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_R_ROBE, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_STAFF_R, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
							player.addItem(getClass().getSimpleName(), WEAPON_FIST_R, 1, player, true);
						}
						break;
					}
				}
				break;
			}
		}
		return true;
	}
}
