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
package org.l2jbr.gameserver.model.holders;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.enums.StatusUpdateType;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.interfaces.IIdentifiable;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * A holder representing a craftable recipe based on the former RecipeList.<br>
 * It contains all the recipe data and methods required for crafting this recipe.
 * @author Nik
 */
public class RecipeHolder implements IIdentifiable
{
	/** List of materials required to craft this recipe. */
	private final List<ItemHolder> _materials;
	
	/** Group of products where a single product will randomly be selected upon crafting. */
	private final List<ItemChanceHolder> _productGroup;
	
	private final List<ItemHolder> _npcFee;
	
	/** Stats and amount required to perform the craft. */
	private final Map<StatusUpdateType, Double> _statUse;
	
	private final int _id;
	private final int _level;
	private final int _itemId;
	private final String _name;
	private final double _successRate;
	private final boolean _isCommonRecipe;
	private final double _maxOfferingBonus;
	private final long _maxOffering;
	
	public RecipeHolder(StatsSet set, List<ItemHolder> ingredients, List<ItemChanceHolder> productGroup, List<ItemHolder> npcFee, Map<StatusUpdateType, Double> statUse)
	{
		_id = set.getInt("id");
		_level = set.getInt("level");
		_itemId = set.getInt("itemId");
		_name = set.getString("name");
		_successRate = set.getDouble("successRate");
		_isCommonRecipe = set.getBoolean("isCommonRecipe");
		_maxOfferingBonus = set.getDouble("maxOfferingBonus", Math.max(0, 100 - _successRate));
		_maxOffering = set.getLong("maxOffering", 0);
		
		_materials = Collections.unmodifiableList(ingredients);
		_productGroup = Collections.unmodifiableList(productGroup);
		_npcFee = Collections.unmodifiableList(npcFee);
		_statUse = Collections.unmodifiableMap(statUse);
	}
	
	/**
	 * @return the recipe id, NOT the recipe's item id
	 */
	@Override
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return the crafting level needed to use this RecipeList.
	 */
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * @return the recipe's item id.
	 */
	public int getItemId()
	{
		return _itemId;
	}
	
	/**
	 * @return the name of the RecipeList.
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * @return the crafting success rate when using the RecipeList.
	 */
	public double getSuccessRate()
	{
		return _successRate;
	}
	
	/**
	 * @return {@code true} if this a Dwarven recipe or {@code false} if its a Common recipe
	 */
	public boolean isDwarvenRecipe()
	{
		return !_isCommonRecipe;
	}
	
	/**
	 * @return list of materials required to complete the recipe.
	 */
	public List<ItemHolder> getMaterials()
	{
		return _materials;
	}
	
	/**
	 * @return the whole group of products from which one random item will result in being crafted.
	 */
	public List<ItemChanceHolder> getProductGroup()
	{
		return _productGroup;
	}
	
	/**
	 * @return list of items that NPCs take for crafting this recipe.
	 */
	public List<ItemHolder> getNpcFee()
	{
		return _npcFee;
	}
	
	/**
	 * @return the table containing all RecipeStatInstance of the statUse parameter of the RecipeList.
	 */
	public Map<StatusUpdateType, Double> getStatUse()
	{
		return _statUse;
	}
	
	/**
	 * @return Maximum bonus success rate when maximum offering is reached. Default is the rate needed to reach 100% success rate.
	 */
	public double getMaxOfferingBonus()
	{
		return _maxOfferingBonus;
	}
	
	/**
	 * @return Maximum amount of items' adena worth offering. {@code 0} if this recipe does not allow offering.
	 */
	public long getMaxOffering()
	{
		return _maxOffering;
	}
	
	/**
	 * Picks a random number then attempts to get a product from the group based on it.
	 * @return {@code ItemChanceHolder} that is the randomly picked product from the group,<br>
	 *         or {@code null} if the whole chance sum of the products in the group didn't manage to outnumber the random.
	 */
	public ItemChanceHolder getRandomProduct()
	{
		double random = Rnd.get(100);
		for (ItemChanceHolder product : _productGroup)
		{
			if (product.getChance() > random)
			{
				return product;
			}
			
			random -= product.getChance();
		}
		
		return null;
	}
	
	public boolean checkNecessaryStats(PlayerInstance player, PlayerInstance manufacturer, boolean sendMessage)
	{
		for (Entry<StatusUpdateType, Double> entry : _statUse.entrySet())
		{
			final StatusUpdateType stat = entry.getKey();
			final double requiredAmount = entry.getValue();
			
			// Less than or equals to because some stats bad interraction - like HP could kill the player if its taken all.
			if (stat.getValue(manufacturer) <= requiredAmount)
			{
				if (sendMessage)
				{
					switch (stat)
					{
						case CUR_HP:
						{
							player.sendPacket(SystemMessageId.NOT_ENOUGH_HP);
							break;
						}
						case CUR_MP:
						{
							player.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
							break;
						}
						default:
						{
							player.sendMessage("You need " + requiredAmount + " " + stat.toString().toLowerCase() + " to perform this craft.");
							break;
						}
					}
				}
				
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param player the player's inventory to check.
	 * @param sendMessage send system messages for item requirements if there is missing ingredient.
	 * @return {@code true} if all necessary ingredients are met, {@code false} if there are missing ingredients.
	 */
	public boolean checkNecessaryIngredients(PlayerInstance player, boolean sendMessage)
	{
		for (ItemHolder ingredient : _materials)
		{
			final long count = player.getInventory().getInventoryItemCount(ingredient.getId(), -1);
			if (count < ingredient.getCount())
			{
				if (sendMessage)
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_NEED_S2_MORE_S1_S);
					sm.addItemName(ingredient.getId());
					sm.addLong(ingredient.getCount() - count);
					player.sendPacket(sm);
				}
				
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @param player the player requesting the craft.
	 * @param manufacturer the player doing the craft (either the same player or manufacture shop).
	 * @param success {@code true} to give the product item to the player, {@code false} otherwise.
	 * @param craftingCritical {@code true} to give double of the product (if success), {@code false} otherwise.
	 * @param sendMessage send system messages of the process.
	 * @return {@code ItemHolder} of the randomly created product (even if its failing craft), {@code null} if the item creation was not performed due to failed checks.
	 */
	public ItemHolder doCraft(PlayerInstance player, PlayerInstance manufacturer, boolean success, boolean craftingCritical, boolean sendMessage)
	{
		if (!checkNecessaryStats(player, manufacturer, sendMessage))
		{
			return null;
		}
		
		if (!checkNecessaryIngredients(player, sendMessage))
		{
			return null;
		}
		
		// Take necessary stats.
		for (Entry<StatusUpdateType, Double> entry : _statUse.entrySet())
		{
			final StatusUpdateType stat = entry.getKey();
			final double requiredAmount = entry.getValue();
			
			switch (stat)
			{
				case CUR_HP:
				{
					manufacturer.reduceCurrentHp(requiredAmount, manufacturer, null);
					break;
				}
				case CUR_MP:
				{
					manufacturer.reduceCurrentMp(requiredAmount);
					break;
				}
				case CUR_CP:
				{
					manufacturer.getStatus().reduceCp((int) requiredAmount);
					break;
				}
				case EXP:
				{
					manufacturer.getStat().removeExp((long) requiredAmount);
					break;
				}
				case REPUTATION:
				{
					manufacturer.setReputation((int) (manufacturer.getReputation() - requiredAmount));
					break;
				}
			}
		}
		
		// Take necessary ingredients. If there was problem destroying item, return null to insicate that process didn't go well.
		if (_materials.stream().anyMatch(i -> !player.destroyItemByItemId("Recipe " + getId(), i.getId(), i.getCount(), manufacturer, sendMessage)))
		{
			return null;
		}
		
		// Check if success. Luck triggers no matter the success rate - even with 100% craft. Luck chance is taken from your stat and not manufacturer's stat.
		final ItemHolder result = getRandomProduct();
		if (success)
		{
			player.addItem("Craft", result, manufacturer, true);
			
			// Award another item if its crafting critical. Double blessed items is very, very rare, but still possible.
			if (craftingCritical)
			{
				player.addItem("CraftCritical", result, manufacturer, true);
			}
		}
		
		return result;
	}
}
