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
package custom.SellBuff;

import java.util.StringTokenizer;

import org.l2jbr.Config;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.handler.BypassHandler;
import org.l2jbr.gameserver.handler.IBypassHandler;
import org.l2jbr.gameserver.handler.IVoicedCommandHandler;
import org.l2jbr.gameserver.handler.VoicedCommandHandler;
import org.l2jbr.gameserver.instancemanager.SellBuffsManager;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.AbstractScript;
import org.l2jbr.gameserver.model.holders.SellBuffHolder;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.util.Util;

/**
 * Sell Buffs voice command
 * @author St3eT
 */
public class SellBuff implements IVoicedCommandHandler, IBypassHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"sellbuff",
		"sellbuffs",
	};
	
	private static final String[] BYPASS_COMMANDS =
	{
		"sellbuffadd",
		"sellbuffaddskill",
		"sellbuffedit",
		"sellbuffchangeprice",
		"sellbuffremove",
		"sellbuffbuymenu",
		"sellbuffbuyskill",
		"sellbuffstart",
		"sellbuffstop",
	};
	
	private SellBuff()
	{
		if (Config.SELLBUFF_ENABLED)
		{
			BypassHandler.getInstance().registerHandler(this);
			VoicedCommandHandler.getInstance().registerHandler(this);
		}
	}
	
	@Override
	public boolean useBypass(String command, PlayerInstance player, Creature target)
	{
		String cmd = "";
		String params = "";
		final StringTokenizer st = new StringTokenizer(command, " ");
		
		if (st.hasMoreTokens())
		{
			cmd = st.nextToken();
		}
		
		while (st.hasMoreTokens())
		{
			params += st.nextToken() + (st.hasMoreTokens() ? " " : "");
		}
		
		if (cmd.isEmpty())
		{
			return false;
		}
		return useBypass(cmd, player, params);
	}
	
	@Override
	public boolean useVoicedCommand(String command, PlayerInstance player, String params)
	{
		switch (command)
		{
			case "sellbuff":
			case "sellbuffs":
			{
				SellBuffsManager.getInstance().sendSellMenu(player);
				break;
			}
		}
		return true;
	}
	
	public boolean useBypass(String command, PlayerInstance player, String params)
	{
		if (!Config.SELLBUFF_ENABLED)
		{
			return false;
		}
		
		switch (command)
		{
			case "sellbuffstart":
			{
				if (player.isSellingBuffs() || (params == null) || params.isEmpty())
				{
					return false;
				}
				else if (player.getSellingBuffs().isEmpty())
				{
					player.sendMessage("Your list of buffs is empty, please add some buffs first!");
					return false;
				}
				else
				{
					String title = "BUFF SELL: ";
					final StringTokenizer st = new StringTokenizer(params, " ");
					while (st.hasMoreTokens())
					{
						title += st.nextToken() + " ";
					}
					
					if (title.length() > 40)
					{
						player.sendMessage("Your title cannot exceed 29 characters in length. Please try again.");
						return false;
					}
					
					SellBuffsManager.getInstance().startSellBuffs(player, title);
				}
				break;
			}
			case "sellbuffstop":
			{
				if (player.isSellingBuffs())
				{
					SellBuffsManager.getInstance().stopSellBuffs(player);
				}
				break;
			}
			case "sellbuffadd":
			{
				if (!player.isSellingBuffs())
				{
					int index = 0;
					if ((params != null) && !params.isEmpty() && Util.isDigit(params))
					{
						index = Integer.parseInt(params);
					}
					
					SellBuffsManager.getInstance().sendBuffChoiceMenu(player, index);
				}
				break;
			}
			case "sellbuffedit":
			{
				if (!player.isSellingBuffs())
				{
					SellBuffsManager.getInstance().sendBuffEditMenu(player);
				}
				break;
			}
			case "sellbuffchangeprice":
			{
				if (!player.isSellingBuffs() && (params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int skillId = -1;
					int price = -1;
					
					if (st.hasMoreTokens())
					{
						skillId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						try
						{
							price = Integer.parseInt(st.nextToken());
						}
						catch (NumberFormatException e)
						{
							player.sendMessage("Too big price! Maximal price is " + Config.SELLBUFF_MAX_PRICE);
							SellBuffsManager.getInstance().sendBuffEditMenu(player);
						}
					}
					
					if ((skillId == -1) || (price == -1))
					{
						return false;
					}
					
					final Skill skillToChange = player.getKnownSkill(skillId);
					if (skillToChange == null)
					{
						return false;
					}
					
					final SellBuffHolder holder = player.getSellingBuffs().stream().filter(h -> (h.getSkillId() == skillToChange.getId())).findFirst().orElse(null);
					if ((holder != null))
					{
						player.sendMessage("Price of " + player.getKnownSkill(holder.getSkillId()).getName() + " has been changed to " + price + "!");
						holder.setPrice(price);
						SellBuffsManager.getInstance().sendBuffEditMenu(player);
					}
				}
				break;
			}
			case "sellbuffremove":
			{
				if (!player.isSellingBuffs() && (params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int skillId = -1;
					
					if (st.hasMoreTokens())
					{
						skillId = Integer.parseInt(st.nextToken());
					}
					
					if ((skillId == -1))
					{
						return false;
					}
					
					final Skill skillToRemove = player.getKnownSkill(skillId);
					if (skillToRemove == null)
					{
						return false;
					}
					
					final SellBuffHolder holder = player.getSellingBuffs().stream().filter(h -> (h.getSkillId() == skillToRemove.getId())).findFirst().orElse(null);
					if ((holder != null) && player.getSellingBuffs().remove(holder))
					{
						player.sendMessage("Skill " + player.getKnownSkill(holder.getSkillId()).getName() + " has been removed!");
						SellBuffsManager.getInstance().sendBuffEditMenu(player);
					}
				}
				break;
			}
			case "sellbuffaddskill":
			{
				if (!player.isSellingBuffs() && (params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int skillId = -1;
					long price = -1;
					
					if (st.hasMoreTokens())
					{
						skillId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						try
						{
							price = Integer.parseInt(st.nextToken());
						}
						catch (NumberFormatException e)
						{
							player.sendMessage("Too big price! Maximal price is " + Config.SELLBUFF_MIN_PRICE);
							SellBuffsManager.getInstance().sendBuffEditMenu(player);
						}
					}
					
					if ((skillId == -1) || (price == -1))
					{
						return false;
					}
					
					final Skill skillToAdd = player.getKnownSkill(skillId);
					if (skillToAdd == null)
					{
						return false;
					}
					else if (price < Config.SELLBUFF_MIN_PRICE)
					{
						player.sendMessage("Too small price! Minimal price is " + Config.SELLBUFF_MIN_PRICE);
						return false;
					}
					else if (price > Config.SELLBUFF_MAX_PRICE)
					{
						player.sendMessage("Too big price! Maximal price is " + Config.SELLBUFF_MAX_PRICE);
						return false;
					}
					else if (player.getSellingBuffs().size() >= Config.SELLBUFF_MAX_BUFFS)
					{
						player.sendMessage("You already reached max count of buffs! Max buffs is: " + Config.SELLBUFF_MAX_BUFFS);
						return false;
					}
					else if (!SellBuffsManager.getInstance().isInSellList(player, skillToAdd))
					{
						player.getSellingBuffs().add(new SellBuffHolder(skillToAdd.getId(), price));
						player.sendMessage(skillToAdd.getName() + " has been added!");
						SellBuffsManager.getInstance().sendBuffChoiceMenu(player, 0);
					}
				}
				break;
			}
			case "sellbuffbuymenu":
			{
				if ((params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					
					int objId = -1;
					int index = 0;
					if (st.hasMoreTokens())
					{
						objId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						index = Integer.parseInt(st.nextToken());
					}
					
					final PlayerInstance seller = World.getInstance().getPlayer(objId);
					if (seller != null)
					{
						if (!seller.isSellingBuffs() || !player.isInsideRadius3D(seller, Npc.INTERACTION_DISTANCE))
						{
							return false;
						}
						
						SellBuffsManager.getInstance().sendBuffMenu(player, seller, index);
					}
				}
				break;
			}
			case "sellbuffbuyskill":
			{
				if ((params != null) && !params.isEmpty())
				{
					final StringTokenizer st = new StringTokenizer(params, " ");
					int objId = -1;
					int skillId = -1;
					int index = 0;
					
					if (st.hasMoreTokens())
					{
						objId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						skillId = Integer.parseInt(st.nextToken());
					}
					
					if (st.hasMoreTokens())
					{
						index = Integer.parseInt(st.nextToken());
					}
					
					if ((skillId == -1) || (objId == -1))
					{
						return false;
					}
					
					final PlayerInstance seller = World.getInstance().getPlayer(objId);
					if (seller == null)
					{
						return false;
					}
					
					final Skill skillToBuy = seller.getKnownSkill(skillId);
					if (!seller.isSellingBuffs() || !Util.checkIfInRange(Npc.INTERACTION_DISTANCE, player, seller, true) || (skillToBuy == null))
					{
						return false;
					}
					
					if (seller.getCurrentMp() < (skillToBuy.getMpConsume() * Config.SELLBUFF_MP_MULTIPLER))
					{
						player.sendMessage(seller.getName() + " has no enough mana for " + skillToBuy.getName() + "!");
						SellBuffsManager.getInstance().sendBuffMenu(player, seller, index);
						return false;
					}
					
					final SellBuffHolder holder = seller.getSellingBuffs().stream().filter(h -> (h.getSkillId() == skillToBuy.getId())).findFirst().orElse(null);
					if (holder != null)
					{
						if (AbstractScript.getQuestItemsCount(player, Config.SELLBUFF_PAYMENT_ID) >= holder.getPrice())
						{
							AbstractScript.takeItems(player, Config.SELLBUFF_PAYMENT_ID, holder.getPrice());
							AbstractScript.giveItems(seller, Config.SELLBUFF_PAYMENT_ID, holder.getPrice());
							seller.reduceCurrentMp(skillToBuy.getMpConsume() * Config.SELLBUFF_MP_MULTIPLER);
							skillToBuy.activateSkill(seller, player);
						}
						else
						{
							final Item item = ItemTable.getInstance().getTemplate(Config.SELLBUFF_PAYMENT_ID);
							if (item != null)
							{
								player.sendMessage("Not enough " + item.getName() + "!");
							}
							else
							{
								player.sendMessage("Not enough items!");
							}
						}
					}
					SellBuffsManager.getInstance().sendBuffMenu(player, seller, index);
				}
				break;
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
	@Override
	public String[] getBypassList()
	{
		return BYPASS_COMMANDS;
	}
	
	public static void main(String[] args)
	{
		new SellBuff();
	}
}