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
package ai.others.AdventureGuildsman;

import java.util.HashMap;
import java.util.Map;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.data.xml.impl.MultisellData;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.network.serverpackets.ExShowQuestInfo;

import ai.AbstractNpcAI;

/**
 * Adventure Guildsman AI.
 * @author ChaosPaladin
 */
public class AdventureGuildsman extends AbstractNpcAI
{
	// NPC
	private static final int ADVENTURE_GUILDSMAN = 33946;
	// Items
	private static final int PCCAFE_LOTTERY_TICKET_30DAYS = 15358;
	private static final int PCCAFE_1ST_LOTTERY_TICKET_30DAYS = 15359;
	private static final int PCCAFE_2ND_LOTTERY_TICKET_30DAYS = 15360;
	private static final int PCCAFE_3RD_LOTTERY_TICKET_30DAYS = 15361;
	private static final int PCCAFE_4TH_LOTTERY_TICKET_30DAYS = 15362;
	private static final int PCCAFE_5TH_LOTTERY_TICKET_30DAYS = 15363;
	private static final int VOUCHER_LEV_85 = 17739;
	private static final int VOUCHER_LEV_90 = 17740;
	private static final int VOUCHER_LEV_95 = 17741;
	private static final int VOUCHER_LEV_97 = 17742;
	private static final int SEAL_LEV_85 = 17743;
	private static final int SEAL_LEV_90 = 17744;
	private static final int SEAL_LEV_95 = 17745;
	private static final int SEAL_LEV_97 = 17746;
	// Skills
	private static final SkillHolder KNIGHT = new SkillHolder(17294, 1); // Player Commendation - Knight's Harmony
	private static final SkillHolder WARRIOR = new SkillHolder(17295, 1); // Player Commendation - Warrior's Harmony
	private static final SkillHolder WIZARD = new SkillHolder(17296, 1); // Player Commendation - Wizard's Harmony
	private static final SkillHolder[] GROUP_MELODY =
	{
		new SkillHolder(9273, 1), // Player Commendation - Horn Melody
		new SkillHolder(9274, 1), // Player Commendation - Drum Melody
		new SkillHolder(9276, 1), // Player Commendation - Pipe Organ Melody
		new SkillHolder(9277, 1), // Player Commendation - Guitar Melody
	};
	private static final SkillHolder[] GROUP_SONATA =
	{
		new SkillHolder(17291, 1), // Player Commendation - Prevailing Sonata
		new SkillHolder(17292, 1), // Player Commendation - Daring Sonata
		new SkillHolder(17293, 1), // Player Commendation - Refreshing Sonata
	};
	// Misc
	//@formatter:off
	private static final Map<CategoryType, Integer> R_CLASS_TALISMAN = new HashMap<>();
	static
	{
		R_CLASS_TALISMAN.put(CategoryType.SIXTH_SIGEL_GROUP, 735);
		R_CLASS_TALISMAN.put(CategoryType.SIXTH_TIR_GROUP, 736);
		R_CLASS_TALISMAN.put(CategoryType.SIXTH_OTHEL_GROUP, 737);
		R_CLASS_TALISMAN.put(CategoryType.SIXTH_YR_GROUP, 738);
		R_CLASS_TALISMAN.put(CategoryType.SIXTH_WYNN_GROUP, 739);
		R_CLASS_TALISMAN.put(CategoryType.SIXTH_IS_GROUP, 740);
		R_CLASS_TALISMAN.put(CategoryType.SIXTH_FEOH_GROUP, 741);
		R_CLASS_TALISMAN.put(CategoryType.SIXTH_EOLH_GROUP, 742);
		R_CLASS_TALISMAN.put(CategoryType.ERTHEIA_FIGHTER_GROUP, 736);
		R_CLASS_TALISMAN.put(CategoryType.ERTHEIA_WIZARD_GROUP, 741);
	}
	private static final Map<CategoryType, Integer> R90_CLASS_TALISMAN = new HashMap<>();
	static
	{
		R90_CLASS_TALISMAN.put(CategoryType.SIXTH_SIGEL_GROUP, 743);
		R90_CLASS_TALISMAN.put(CategoryType.SIXTH_TIR_GROUP, 744);
		R90_CLASS_TALISMAN.put(CategoryType.SIXTH_OTHEL_GROUP, 745);
		R90_CLASS_TALISMAN.put(CategoryType.SIXTH_YR_GROUP, 746);
		R90_CLASS_TALISMAN.put(CategoryType.SIXTH_WYNN_GROUP, 747);
		R90_CLASS_TALISMAN.put(CategoryType.SIXTH_IS_GROUP, 748);
		R90_CLASS_TALISMAN.put(CategoryType.SIXTH_FEOH_GROUP, 749);
		R90_CLASS_TALISMAN.put(CategoryType.SIXTH_EOLH_GROUP, 750);
		R90_CLASS_TALISMAN.put(CategoryType.ERTHEIA_FIGHTER_GROUP, 744);
		R90_CLASS_TALISMAN.put(CategoryType.ERTHEIA_WIZARD_GROUP, 749);
	}
	private static final Map<CategoryType, Integer> R95_CLASS_TALISMAN = new HashMap<>();
	static
	{
		R95_CLASS_TALISMAN.put(CategoryType.SIXTH_SIGEL_GROUP, 751);
		R95_CLASS_TALISMAN.put(CategoryType.SIXTH_TIR_GROUP, 752);
		R95_CLASS_TALISMAN.put(CategoryType.SIXTH_OTHEL_GROUP, 753);
		R95_CLASS_TALISMAN.put(CategoryType.SIXTH_YR_GROUP, 754);
		R95_CLASS_TALISMAN.put(CategoryType.SIXTH_WYNN_GROUP, 755);
		R95_CLASS_TALISMAN.put(CategoryType.SIXTH_IS_GROUP, 756);
		R95_CLASS_TALISMAN.put(CategoryType.SIXTH_FEOH_GROUP, 757);
		R95_CLASS_TALISMAN.put(CategoryType.SIXTH_EOLH_GROUP, 758);
		R95_CLASS_TALISMAN.put(CategoryType.ERTHEIA_FIGHTER_GROUP, 752);
		R95_CLASS_TALISMAN.put(CategoryType.ERTHEIA_WIZARD_GROUP, 757);
	}
	private static final Map<CategoryType, Integer> R99_CLASS_TALISMAN = new HashMap<>();
	static
	{
		R99_CLASS_TALISMAN.put(CategoryType.SIXTH_SIGEL_GROUP, 759);
		R99_CLASS_TALISMAN.put(CategoryType.SIXTH_TIR_GROUP, 760);
		R99_CLASS_TALISMAN.put(CategoryType.SIXTH_OTHEL_GROUP, 761);
		R99_CLASS_TALISMAN.put(CategoryType.SIXTH_YR_GROUP, 762);
		R99_CLASS_TALISMAN.put(CategoryType.SIXTH_WYNN_GROUP, 763);
		R99_CLASS_TALISMAN.put(CategoryType.SIXTH_IS_GROUP, 764);
		R99_CLASS_TALISMAN.put(CategoryType.SIXTH_FEOH_GROUP, 765);
		R99_CLASS_TALISMAN.put(CategoryType.SIXTH_EOLH_GROUP, 766);
		R99_CLASS_TALISMAN.put(CategoryType.ERTHEIA_FIGHTER_GROUP, 760);
		R99_CLASS_TALISMAN.put(CategoryType.ERTHEIA_WIZARD_GROUP, 765);
	}
	//@formatter:on
	private static final String USED_PC_LOTTERY_TICKET = "USED_PC_LOTTERY_TICKET";
	
	private AdventureGuildsman()
	{
		addStartNpc(ADVENTURE_GUILDSMAN);
		addTalkId(ADVENTURE_GUILDSMAN);
		addFirstTalkId(ADVENTURE_GUILDSMAN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "pccafe_list":
			{
				htmltext = "pccafe_list001.htm";
				break;
			}
			case "quest_list":
			{
				player.sendPacket(ExShowQuestInfo.STATIC_PACKET);
				break;
			}
			case "buff_list":
			{
				htmltext = "pccafe_buff_1001.htm";
				break;
			}
			case "item_list":
			{
				htmltext = "pccafe_item001.htm";
				break;
			}
			case "pccafe_help_inzone001.htm":
			case "pccafe_help_lottery001.htm":
			case "pccafe_help_lottery002.htm":
			case "adventurer_agent_town_voucher_change.htm":
			case "life_crystal_merge001.htm":
			case "life_crystal_merge002.htm":
			case "voucher_trader1001.htm":
			case "voucher_trader2001.htm":
			case "voucher_trader3001.htm":
			case "voucher_trader4001.htm":
			case "voucher_trader1004.htm":
			case "voucher_trader2004.htm":
			case "voucher_trader3004.htm":
			case "voucher_trader4004.htm":
			case "voucher_trader1005.htm":
			case "voucher_trader2005.htm":
			case "voucher_trader3005.htm":
			case "voucher_trader4005.htm":
			case "voucher_trader1006.htm":
			case "voucher_trader2006.htm":
			case "voucher_trader3006.htm":
			case "voucher_trader4006.htm":
			{
				htmltext = event;
				break;
			}
			case "index":
			{
				htmltext = player.getLevel() < 40 ? "adventurer_agent_town_77001.htm" : "adventurer_agent_town_77001e.htm";
				break;
			}
			case "buff_setlist":
			{
				htmltext = "pccafe_newbuff_001.htm";
				break;
			}
			case "buff_group":
			{
				htmltext = player.getPcCafePoints() >= 120 ? applyBuffsGroup(npc, player, GROUP_MELODY.length) : "pccafe_buff_1001.htm";
				break;
			}
			case "knight":
			{
				htmltext = player.getPcCafePoints() >= 200 ? applyBuffs(npc, player, KNIGHT.getSkill()) : "pccafe_buff_1001.htm";
				break;
			}
			case "warrior":
			{
				htmltext = player.getPcCafePoints() >= 200 ? applyBuffs(npc, player, WARRIOR.getSkill()) : "pccafe_buff_1001.htm";
				break;
			}
			case "wizard":
			{
				htmltext = player.getPcCafePoints() >= 200 ? applyBuffs(npc, player, WIZARD.getSkill()) : "pccafe_buff_1001.htm";
				break;
			}
			case "give_lottery_ticket":
			{
				if (!player.getVariables().getBoolean(USED_PC_LOTTERY_TICKET, false))
				{
					player.getVariables().set(USED_PC_LOTTERY_TICKET, true);
					giveItems(player, PCCAFE_LOTTERY_TICKET_30DAYS, 1);
				}
				else
				{
					htmltext = "pccafe_help_lottery_notoneday.htm";
				}
				break;
			}
			case "trade_10":
			{
				htmltext = tradeItem(player, PCCAFE_5TH_LOTTERY_TICKET_30DAYS, 10);
				break;
			}
			case "trade_100":
			{
				htmltext = tradeItem(player, PCCAFE_4TH_LOTTERY_TICKET_30DAYS, 100);
				break;
			}
			case "trade_200":
			{
				htmltext = tradeItem(player, PCCAFE_3RD_LOTTERY_TICKET_30DAYS, 200);
				break;
			}
			case "trade_1000":
			{
				htmltext = tradeItem(player, PCCAFE_2ND_LOTTERY_TICKET_30DAYS, 1000);
				break;
			}
			case "trade_10000":
			{
				htmltext = tradeItem(player, PCCAFE_1ST_LOTTERY_TICKET_30DAYS, 10000);
				break;
			}
			case "trade_seal85":
			{
				if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					if (hasQuestItems(player, VOUCHER_LEV_85))
					{
						takeItems(player, VOUCHER_LEV_85, 1);
						giveItems(player, SEAL_LEV_85, 20);
						addExpAndSp(player, 60000000, 0);
					}
					else
					{
						htmltext = "voucher_trader1003b.htm";
					}
				}
				else
				{
					htmltext = "voucher_trader1007.htm";
				}
				break;
			}
			case "trade_seal90":
			{
				if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					if (hasQuestItems(player, VOUCHER_LEV_90))
					{
						takeItems(player, VOUCHER_LEV_90, 1);
						giveItems(player, SEAL_LEV_90, 20);
						addExpAndSp(player, 66000000, 0);
					}
					else
					{
						htmltext = "voucher_trader2003b.htm";
					}
				}
				else
				{
					htmltext = "voucher_trader1007.htm";
				}
				break;
			}
			case "trade_seal95":
			{
				if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					if (hasQuestItems(player, VOUCHER_LEV_95))
					{
						takeItems(player, VOUCHER_LEV_95, 1);
						giveItems(player, SEAL_LEV_95, 20);
						addExpAndSp(player, 68000000, 0);
					}
					else
					{
						htmltext = "voucher_trader3003b.htm";
					}
				}
				else
				{
					htmltext = "voucher_trader1007.htm";
				}
				break;
			}
			case "trade_seal97":
			{
				if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					if (hasQuestItems(player, VOUCHER_LEV_97))
					{
						takeItems(player, VOUCHER_LEV_97, 1);
						giveItems(player, SEAL_LEV_97, 20);
						addExpAndSp(player, 76000000, 0);
					}
					else
					{
						htmltext = "voucher_trader3003b.htm";
					}
				}
				else
				{
					htmltext = "voucher_trader1007.htm";
				}
				break;
			}
			case "give_talismanR_by_class":
			{
				int multisellId = -1;
				
				for (CategoryType type : R_CLASS_TALISMAN.keySet())
				{
					if (player.isInCategory(type))
					{
						multisellId = R_CLASS_TALISMAN.get(type);
						break;
					}
				}
				
				if (multisellId > 0)
				{
					MultisellData.getInstance().separateAndSend(multisellId, player, npc, false);
				}
				break;
			}
			case "give_talismanR90_by_class":
			{
				int multisellId = -1;
				
				for (CategoryType type : R90_CLASS_TALISMAN.keySet())
				{
					if (player.isInCategory(type))
					{
						multisellId = R90_CLASS_TALISMAN.get(type);
						break;
					}
				}
				
				if (multisellId > 0)
				{
					MultisellData.getInstance().separateAndSend(multisellId, player, npc, false);
				}
				break;
			}
			case "give_talismanR95_by_class":
			{
				int multisellId = -1;
				for (CategoryType type : R95_CLASS_TALISMAN.keySet())
				{
					if (player.isInCategory(type))
					{
						multisellId = R95_CLASS_TALISMAN.get(type);
						break;
					}
				}
				
				if (multisellId > 0)
				{
					MultisellData.getInstance().separateAndSend(multisellId, player, npc, false);
				}
				break;
			}
			
			case "give_talismanR99_by_class":
			{
				int multisellId = -1;
				
				for (CategoryType type : R99_CLASS_TALISMAN.keySet())
				{
					if (player.isInCategory(type))
					{
						multisellId = R99_CLASS_TALISMAN.get(type);
						break;
					}
				}
				
				if (multisellId > 0)
				{
					MultisellData.getInstance().separateAndSend(multisellId, player, npc, false);
				}
				break;
			}
		}
		if (event.startsWith("melody"))
		{
			final int buffOffset = CommonUtil.constrain(Integer.parseInt(event.substring(event.indexOf(" ") + 1)), 0, GROUP_MELODY.length);
			if (player.getPcCafePoints() >= 20)
			{
				npc.setTarget(player);
				npc.doCast(GROUP_MELODY[buffOffset].getSkill());
				player.setPcCafePoints(player.getPcCafePoints() - 20);
				htmltext = "pccafe_buff_1001.htm";
			}
			else
			{
				htmltext = "pccafe_notpoint001.htm";
			}
			
		}
		return htmltext;
	}
	
	private String applyBuffs(Npc npc, PlayerInstance player, Skill skill)
	{
		for (SkillHolder holder : GROUP_MELODY)
		{
			SkillCaster.triggerCast(npc, player, holder.getSkill());
		}
		for (SkillHolder holder : GROUP_SONATA)
		{
			SkillCaster.triggerCast(npc, player, holder.getSkill());
		}
		SkillCaster.triggerCast(npc, player, skill);
		player.setPcCafePoints(player.getPcCafePoints() - 200);
		return null;
	}
	
	private String applyBuffsGroup(Npc npc, PlayerInstance player, int length)
	{
		for (SkillHolder holder : GROUP_MELODY)
		{
			SkillCaster.triggerCast(npc, player, holder.getSkill());
		}
		player.setPcCafePoints(player.getPcCafePoints() - 120);
		return null;
	}
	
	private String tradeItem(PlayerInstance player, int itemId, int points)
	{
		if (player.getPcCafePoints() >= 200000)
		{
			return "pccafe_help_lottery_fail2.htm";
		}
		
		if (takeItems(player, itemId, 1))
		{
			player.setPcCafePoints(player.getPcCafePoints() + points);
			return "pccafe_help_lottery003.htm";
		}
		return "pccafe_help_lottery_fail.htm";
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return player.getLevel() < 40 ? "adventurer_agent_town_77001.htm" : "adventurer_agent_town_77001e.htm";
	}
	
	public static void main(String[] args)
	{
		new AdventureGuildsman();
	}
}
