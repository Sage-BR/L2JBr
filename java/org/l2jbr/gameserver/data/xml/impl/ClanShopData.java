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
package org.l2jbr.gameserver.data.xml.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.holders.ClanShopProductHolder;
import org.l2jbr.gameserver.model.items.Item;

/**
 * @author Mobius
 */
public class ClanShopData implements IXmlReader
{
	private static Logger LOGGER = Logger.getLogger(ClanShopData.class.getName());
	
	private final List<ClanShopProductHolder> _clanShopProducts = new ArrayList<>();
	
	protected ClanShopData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_clanShopProducts.clear();
		
		parseDatapackFile("config/ClanShop.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _clanShopProducts.size() + " clan shop products.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "clan", productNode ->
		{
			final StatsSet set = new StatsSet(parseAttributes(productNode));
			final int clanLevel = set.getInt("level");
			final int itemId = set.getInt("item");
			final int count = set.getInt("count");
			final long adena = set.getLong("adena");
			final int fame = set.getInt("fame");
			
			final Item item = ItemTable.getInstance().getTemplate(itemId);
			if (item == null)
			{
				LOGGER.info(getClass().getSimpleName() + ": Could not create clan shop item " + itemId + ", it does not exist.");
			}
			else
			{
				_clanShopProducts.add(new ClanShopProductHolder(clanLevel, item, count, adena, fame));
			}
		}));
	}
	
	public ClanShopProductHolder getProduct(int itemId)
	{
		for (ClanShopProductHolder product : _clanShopProducts)
		{
			if (product.getTradeItem().getItem().getId() == itemId)
			{
				return product;
			}
		}
		return null;
	}
	
	public List<ClanShopProductHolder> getProducts()
	{
		return _clanShopProducts;
	}
	
	public static ClanShopData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanShopData INSTANCE = new ClanShopData();
	}
}
