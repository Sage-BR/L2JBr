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
import java.util.Objects;

import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.model.items.Item;

/**
 * @author Nik
 */
public class MultisellEntryHolder
{
	private final boolean _stackable;
	private final List<ItemChanceHolder> _ingredients;
	private final List<ItemChanceHolder> _products;
	
	public MultisellEntryHolder(List<ItemChanceHolder> ingredients, List<ItemChanceHolder> products)
	{
		_ingredients = Collections.unmodifiableList(ingredients);
		_products = Collections.unmodifiableList(products);
		_stackable = products.stream().map(i -> ItemTable.getInstance().getTemplate(i.getId())).filter(Objects::nonNull).allMatch(Item::isStackable);
	}
	
	public List<ItemChanceHolder> getIngredients()
	{
		return _ingredients;
	}
	
	public List<ItemChanceHolder> getProducts()
	{
		return _products;
	}
	
	public boolean isStackable()
	{
		return _stackable;
	}
}