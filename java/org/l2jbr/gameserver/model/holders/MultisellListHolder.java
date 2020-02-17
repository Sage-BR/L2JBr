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
import java.util.Set;

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.interfaces.IIdentifiable;

/**
 * A static list container of all multisell entries of a given list.
 * @author Nik
 */
public class MultisellListHolder implements IIdentifiable
{
	private final int _listId;
	private final boolean _isChanceMultisell;
	private final boolean _applyTaxes;
	private final boolean _maintainEnchantment;
	private final double _ingredientMultiplier;
	private final double _productMultiplier;
	
	protected List<MultisellEntryHolder> _entries;
	protected final Set<Integer> _npcsAllowed;
	
	public MultisellListHolder(int listId, boolean isChanceMultisell, boolean applyTaxes, boolean maintainEnchantment, double ingredientMultiplier, double productMultiplier, List<MultisellEntryHolder> entries, Set<Integer> npcsAllowed)
	{
		_listId = listId;
		_isChanceMultisell = isChanceMultisell;
		_applyTaxes = applyTaxes;
		_maintainEnchantment = maintainEnchantment;
		_ingredientMultiplier = ingredientMultiplier;
		_productMultiplier = productMultiplier;
		_entries = entries;
		_npcsAllowed = npcsAllowed;
	}
	
	@SuppressWarnings("unchecked")
	public MultisellListHolder(StatsSet set)
	{
		_listId = set.getInt("listId");
		_isChanceMultisell = set.getBoolean("isChanceMultisell", false);
		_applyTaxes = set.getBoolean("applyTaxes", false);
		_maintainEnchantment = set.getBoolean("maintainEnchantment", false);
		_ingredientMultiplier = set.getDouble("ingredientMultiplier", 1.0);
		_productMultiplier = set.getDouble("productMultiplier", 1.0);
		_entries = Collections.unmodifiableList(set.getList("entries", MultisellEntryHolder.class, Collections.emptyList()));
		_npcsAllowed = set.getObject("allowNpc", Set.class);
	}
	
	public List<MultisellEntryHolder> getEntries()
	{
		return _entries;
	}
	
	@Override
	public int getId()
	{
		return _listId;
	}
	
	public boolean isChanceMultisell()
	{
		return _isChanceMultisell;
	}
	
	public boolean isApplyTaxes()
	{
		return _applyTaxes;
	}
	
	public boolean isMaintainEnchantment()
	{
		return _maintainEnchantment;
	}
	
	public double getIngredientMultiplier()
	{
		return _ingredientMultiplier;
	}
	
	public double getProductMultiplier()
	{
		return _productMultiplier;
	}
	
	public boolean isNpcAllowed(int npcId)
	{
		return (_npcsAllowed != null) && _npcsAllowed.contains(npcId);
	}
}