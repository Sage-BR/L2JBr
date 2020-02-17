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
package org.l2jbr.gameserver.network.clientpackets;

import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketReader;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.data.xml.impl.SkillTreesData;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.IllegalActionPunishmentType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.enums.SubclassType;
import org.l2jbr.gameserver.enums.UserInfoType;
import org.l2jbr.gameserver.model.SkillLearn;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.FishermanInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.AcquireSkillType;
import org.l2jbr.gameserver.model.base.SubClass;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.clan.ClanPrivilege;
import org.l2jbr.gameserver.model.events.EventDispatcher;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSkillLearn;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.skills.CommonSkill;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.variables.PlayerVariables;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.AcquireSkillDone;
import org.l2jbr.gameserver.network.serverpackets.ExAcquirableSkillListByClass;
import org.l2jbr.gameserver.network.serverpackets.ExAlchemySkillList;
import org.l2jbr.gameserver.network.serverpackets.ExBasicActionList;
import org.l2jbr.gameserver.network.serverpackets.ExStorageMaxCount;
import org.l2jbr.gameserver.network.serverpackets.PledgeSkillList;
import org.l2jbr.gameserver.network.serverpackets.ShortCutInit;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.network.serverpackets.UserInfo;
import org.l2jbr.gameserver.util.Util;

/**
 * Request Acquire Skill client packet implementation.
 * @author Zoey76
 */
public class RequestAcquireSkill implements IClientIncomingPacket
{
	private static final String[] REVELATION_VAR_NAMES =
	{
		PlayerVariables.REVELATION_SKILL_1_MAIN_CLASS,
		PlayerVariables.REVELATION_SKILL_2_MAIN_CLASS
	};
	
	private static final String[] DUALCLASS_REVELATION_VAR_NAMES =
	{
		PlayerVariables.REVELATION_SKILL_1_DUAL_CLASS,
		PlayerVariables.REVELATION_SKILL_2_DUAL_CLASS
	};
	
	private int _id;
	private int _level;
	private AcquireSkillType _skillType;
	private int _subType;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_id = packet.readD();
		_level = packet.readD();
		_skillType = AcquireSkillType.getAcquireSkillType(packet.readD());
		if (_skillType == AcquireSkillType.SUBPLEDGE)
		{
			_subType = packet.readD();
		}
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final PlayerInstance player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if ((_level < 1) || (_level > 1000) || (_id < 1) || (_id > 64000))
		{
			Util.handleIllegalPlayerAction(player, "Wrong Packet Data in Aquired Skill", Config.DEFAULT_PUNISH);
			LOGGER.warning("Recived Wrong Packet Data in Aquired Skill - id: " + _id + " level: " + _level + " for " + player);
			return;
		}
		
		final Npc trainer = player.getLastFolkNPC();
		if ((_skillType != AcquireSkillType.CLASS) && ((trainer == null) || !trainer.isNpc() || (!trainer.canInteract(player) && !player.isGM())))
		{
			return;
		}
		
		final Skill existingSkill = player.getKnownSkill(_id); // Mobius: Keep existing sublevel.
		final Skill skill = SkillData.getInstance().getSkill(_id, _level, existingSkill == null ? 0 : existingSkill.getSubLevel());
		if (skill == null)
		{
			LOGGER.warning(RequestAcquireSkill.class.getSimpleName() + ": Player " + player.getName() + " is trying to learn a null skill Id: " + _id + " level: " + _level + "!");
			return;
		}
		
		// Hack check. Doesn't apply to all Skill Types
		final int prevSkillLevel = player.getSkillLevel(_id);
		if ((_skillType != AcquireSkillType.TRANSFER) && (_skillType != AcquireSkillType.SUBPLEDGE))
		{
			if (prevSkillLevel == _level)
			{
				return;
			}
			
			if (prevSkillLevel != (_level - 1))
			{
				// The previous level skill has not been learned.
				player.sendPacket(SystemMessageId.THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED);
				Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " without knowing it's previous level!", IllegalActionPunishmentType.NONE);
				return;
			}
		}
		
		final SkillLearn s = SkillTreesData.getInstance().getSkillLearn(_skillType, _id, _level, player);
		if (s == null)
		{
			return;
		}
		
		switch (_skillType)
		{
			case CLASS:
			{
				if (checkPlayerSkill(player, trainer, s))
				{
					giveSkill(player, trainer, skill);
				}
				break;
			}
			case TRANSFORM:
			{
				// Hack check.
				if (!canTransform(player))
				{
					player.sendPacket(SystemMessageId.YOU_HAVE_NOT_COMPLETED_THE_NECESSARY_QUEST_FOR_SKILL_ACQUISITION);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " without required quests!", IllegalActionPunishmentType.NONE);
					return;
				}
				
				if (checkPlayerSkill(player, trainer, s))
				{
					giveSkill(player, trainer, skill);
				}
				break;
			}
			case FISHING:
			{
				if (checkPlayerSkill(player, trainer, s))
				{
					giveSkill(player, trainer, skill);
				}
				break;
			}
			case SUBPLEDGE:
			{
				if (!player.isClanLeader() || !player.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME))
				{
					return;
				}
				
				final Clan clan = player.getClan();
				if ((clan.getFortId() == 0) && (clan.getCastleId() == 0))
				{
					return;
				}
				
				// Hack check. Check if SubPledge can accept the new skill:
				if (!clan.isLearnableSubPledgeSkill(skill, _subType))
				{
					player.sendPacket(SystemMessageId.THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_LEARNED);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " without knowing it's previous level!", IllegalActionPunishmentType.NONE);
					return;
				}
				
				final int repCost = (int) s.getLevelUpSp(); // Hopefully not greater that max int.
				if (clan.getReputationScore() < repCost)
				{
					player.sendPacket(SystemMessageId.THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION);
					return;
				}
				
				for (ItemHolder item : s.getRequiredItems())
				{
					if (!player.destroyItemByItemId("SubSkills", item.getId(), item.getCount(), trainer, false))
					{
						player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
						return;
					}
					
					final SystemMessage sm = new SystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
					sm.addItemName(item.getId());
					sm.addLong(item.getCount());
					player.sendPacket(sm);
				}
				
				if (repCost > 0)
				{
					clan.takeReputationScore(repCost, true);
					final SystemMessage cr = new SystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION);
					cr.addInt(repCost);
					player.sendPacket(cr);
				}
				
				clan.addNewSkill(skill, _subType);
				clan.broadcastToOnlineMembers(new PledgeSkillList(clan));
				player.sendPacket(new AcquireSkillDone());
				
				showSubUnitSkillList(player);
				break;
			}
			case TRANSFER:
			{
				if (checkPlayerSkill(player, trainer, s))
				{
					giveSkill(player, trainer, skill);
				}
				
				final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableTransferSkills(player);
				if (skills.isEmpty())
				{
					player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
				}
				else
				{
					player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.TRANSFER));
				}
				break;
			}
			case SUBCLASS:
			{
				if (player.isSubClassActive())
				{
					player.sendPacket(SystemMessageId.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE_MAIN_CLASS);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " while Sub-Class is active!", IllegalActionPunishmentType.NONE);
					return;
				}
				
				if (checkPlayerSkill(player, trainer, s))
				{
					final PlayerVariables vars = player.getVariables();
					String list = vars.getString("SubSkillList", "");
					if ((prevSkillLevel > 0) && list.contains(_id + "-" + prevSkillLevel))
					{
						list = list.replace(_id + "-" + prevSkillLevel, _id + "-" + _level);
					}
					else
					{
						if (!list.isEmpty())
						{
							list += ";";
						}
						list += _id + "-" + _level;
					}
					vars.set("SubSkillList", list);
					giveSkill(player, trainer, skill, false);
				}
				break;
			}
			case DUALCLASS:
			{
				if (player.isSubClassActive())
				{
					player.sendPacket(SystemMessageId.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE_MAIN_CLASS);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " while Sub-Class is active!", IllegalActionPunishmentType.NONE);
					return;
				}
				
				if (checkPlayerSkill(player, trainer, s))
				{
					final PlayerVariables vars = player.getVariables();
					String list = vars.getString("DualSkillList", "");
					if ((prevSkillLevel > 0) && list.contains(_id + "-" + prevSkillLevel))
					{
						list = list.replace(_id + "-" + prevSkillLevel, _id + "-" + _level);
					}
					else
					{
						if (!list.isEmpty())
						{
							list += ";";
						}
						list += _id + "-" + _level;
					}
					vars.set("DualSkillList", list);
					giveSkill(player, trainer, skill, false);
				}
				break;
			}
			case COLLECT:
			{
				if (checkPlayerSkill(player, trainer, s))
				{
					giveSkill(player, trainer, skill);
				}
				break;
			}
			case ALCHEMY:
			{
				if (player.getRace() != Race.ERTHEIA)
				{
					return;
				}
				
				if (checkPlayerSkill(player, trainer, s))
				{
					giveSkill(player, trainer, skill);
					
					player.sendPacket(new AcquireSkillDone());
					player.sendPacket(new ExAlchemySkillList(player));
					
					final List<SkillLearn> alchemySkills = SkillTreesData.getInstance().getAvailableAlchemySkills(player);
					
					if (alchemySkills.isEmpty())
					{
						player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
					}
					else
					{
						player.sendPacket(new ExAcquirableSkillListByClass(alchemySkills, AcquireSkillType.ALCHEMY));
					}
				}
				
				break;
			}
			case REVELATION:
			{
				if (player.isSubClassActive())
				{
					player.sendPacket(SystemMessageId.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE_MAIN_CLASS);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " while Sub-Class is active!", IllegalActionPunishmentType.NONE);
					return;
				}
				if ((player.getLevel() < 85) || !player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " while not being level 85 or awaken!", IllegalActionPunishmentType.NONE);
					return;
				}
				
				int count = 0;
				
				for (String varName : REVELATION_VAR_NAMES)
				{
					if (player.getVariables().getInt(varName, 0) > 0)
					{
						count++;
					}
				}
				
				if (count >= 2)
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " while having already learned 2 skills!", IllegalActionPunishmentType.NONE);
					return;
				}
				
				if (checkPlayerSkill(player, trainer, s))
				{
					final String varName = count == 0 ? REVELATION_VAR_NAMES[0] : REVELATION_VAR_NAMES[1];
					
					player.getVariables().set(varName, skill.getId());
					
					giveSkill(player, trainer, skill);
				}
				
				final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableRevelationSkills(player, SubclassType.BASECLASS);
				if (skills.size() > 0)
				{
					player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.REVELATION));
				}
				else
				{
					player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
				}
				break;
			}
			case REVELATION_DUALCLASS:
			{
				if (player.isSubClassActive() && !player.isDualClassActive())
				{
					player.sendPacket(SystemMessageId.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE_MAIN_CLASS);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " while Sub-Class is active!", IllegalActionPunishmentType.NONE);
					return;
				}
				
				if ((player.getLevel() < 85) || !player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " while not being level 85 or awaken!", IllegalActionPunishmentType.NONE);
					return;
				}
				
				int count = 0;
				
				for (String varName : DUALCLASS_REVELATION_VAR_NAMES)
				{
					if (player.getVariables().getInt(varName, 0) > 0)
					{
						count++;
					}
				}
				
				if (count >= 2)
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + _id + " level " + _level + " while having already learned 2 skills!", IllegalActionPunishmentType.NONE);
					return;
				}
				
				if (checkPlayerSkill(player, trainer, s))
				{
					final String varName = count == 0 ? DUALCLASS_REVELATION_VAR_NAMES[0] : DUALCLASS_REVELATION_VAR_NAMES[1];
					
					player.getVariables().set(varName, skill.getId());
					
					giveSkill(player, trainer, skill);
				}
				
				final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableRevelationSkills(player, SubclassType.DUALCLASS);
				if (skills.size() > 0)
				{
					player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.REVELATION_DUALCLASS));
				}
				else
				{
					player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
				}
				break;
			}
			default:
			{
				LOGGER.warning("Recived Wrong Packet Data in Aquired Skill, unknown skill type:" + _skillType);
				break;
			}
		}
	}
	
	public static void showSubUnitSkillList(PlayerInstance player)
	{
		final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableSubPledgeSkills(player.getClan());
		
		if (skills.isEmpty())
		{
			player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
		}
		else
		{
			player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.SUBPLEDGE));
		}
	}
	
	public static void showSubSkillList(PlayerInstance player)
	{
		final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableSubClassSkills(player);
		if (!skills.isEmpty())
		{
			player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.SUBCLASS));
		}
		else
		{
			player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
		}
	}
	
	public static void showDualSkillList(PlayerInstance player)
	{
		final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableDualClassSkills(player);
		if (!skills.isEmpty())
		{
			player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.DUALCLASS));
		}
		else
		{
			player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
		}
	}
	
	/**
	 * Perform a simple check for current player and skill.<br>
	 * Takes the needed SP if the skill require it and all requirements are meet.<br>
	 * Consume required items if the skill require it and all requirements are meet.<br>
	 * @param player the skill learning player.
	 * @param trainer the skills teaching Npc.
	 * @param skillLearn the skill to be learn.
	 * @return {@code true} if all requirements are meet, {@code false} otherwise.
	 */
	private boolean checkPlayerSkill(PlayerInstance player, Npc trainer, SkillLearn skillLearn)
	{
		if (skillLearn != null)
		{
			if ((skillLearn.getSkillId() == _id) && (skillLearn.getSkillLevel() == _level))
			{
				// Hack check.
				if (skillLearn.getGetLevel() > player.getLevel())
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_SKILL_LEVEL_REQUIREMENTS);
					Util.handleIllegalPlayerAction(player, "Player " + player.getName() + ", level " + player.getLevel() + " is requesting skill Id: " + _id + " level " + _level + " without having minimum required level, " + skillLearn.getGetLevel() + "!", IllegalActionPunishmentType.NONE);
					return false;
				}
				
				if (skillLearn.getDualClassLevel() > 0)
				{
					final SubClass playerDualClass = player.getDualClass();
					if ((playerDualClass == null) || (playerDualClass.getLevel() < skillLearn.getDualClassLevel()))
					{
						return false;
					}
				}
				
				// First it checks that the skill require SP and the player has enough SP to learn it.
				final long levelUpSp = skillLearn.getLevelUpSp();
				if ((levelUpSp > 0) && (levelUpSp > player.getSp()))
				{
					player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
					showSkillList(trainer, player);
					return false;
				}
				
				if (!Config.DIVINE_SP_BOOK_NEEDED && (_id == CommonSkill.DIVINE_INSPIRATION.getId()))
				{
					return true;
				}
				
				// Check for required skills.
				if (!skillLearn.getPreReqSkills().isEmpty())
				{
					for (SkillHolder skill : skillLearn.getPreReqSkills())
					{
						if (player.getSkillLevel(skill.getSkillId()) < skill.getSkillLevel())
						{
							if (skill.getSkillId() == CommonSkill.ONYX_BEAST_TRANSFORMATION.getId())
							{
								player.sendPacket(SystemMessageId.YOU_MUST_LEARN_THE_ONYX_BEAST_SKILL_BEFORE_YOU_CAN_LEARN_FURTHER_SKILLS);
							}
							else
							{
								player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
							}
							return false;
						}
					}
				}
				
				// Check for required items.
				if (!skillLearn.getRequiredItems().isEmpty())
				{
					// Then checks that the player has all the items
					long reqItemCount = 0;
					for (ItemHolder item : skillLearn.getRequiredItems())
					{
						reqItemCount = player.getInventory().getInventoryItemCount(item.getId(), -1);
						if (reqItemCount < item.getCount())
						{
							// Player doesn't have required item.
							player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
							showSkillList(trainer, player);
							return false;
						}
					}
					
					// If the player has all required items, they are consumed.
					for (ItemHolder itemIdCount : skillLearn.getRequiredItems())
					{
						if (!player.destroyItemByItemId("SkillLearn", itemIdCount.getId(), itemIdCount.getCount(), trainer, true))
						{
							Util.handleIllegalPlayerAction(player, "Somehow player " + player.getName() + ", level " + player.getLevel() + " lose required item Id: " + itemIdCount.getId() + " to learn skill while learning skill Id: " + _id + " level " + _level + "!", IllegalActionPunishmentType.NONE);
						}
					}
				}
				
				if (!skillLearn.getRemoveSkills().isEmpty())
				{
					skillLearn.getRemoveSkills().forEach(skillId ->
					{
						if (player.getSkillLevel(skillId) > 0)
						{
							final Skill skillToRemove = player.getKnownSkill(skillId);
							if (skillToRemove != null)
							{
								player.removeSkill(skillToRemove, true);
							}
						}
					});
				}
				
				// If the player has SP and all required items then consume SP.
				if (levelUpSp > 0)
				{
					player.setSp(player.getSp() - levelUpSp);
					final UserInfo ui = new UserInfo(player);
					ui.addComponentType(UserInfoType.CURRENT_HPMPCP_EXP_SP);
					player.sendPacket(ui);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add the skill to the player and makes proper updates.
	 * @param player the player acquiring a skill.
	 * @param trainer the Npc teaching a skill.
	 * @param skill the skill to be learn.
	 */
	private void giveSkill(PlayerInstance player, Npc trainer, Skill skill)
	{
		giveSkill(player, trainer, skill, true);
	}
	
	/**
	 * Add the skill to the player and makes proper updates.
	 * @param player the player acquiring a skill.
	 * @param trainer the Npc teaching a skill.
	 * @param skill the skill to be learn.
	 * @param store
	 */
	private void giveSkill(PlayerInstance player, Npc trainer, Skill skill, boolean store)
	{
		// Send message.
		final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_LEARNED_S1);
		sm.addSkillName(skill);
		player.sendPacket(sm);
		
		player.addSkill(skill, store);
		
		player.sendItemList();
		player.sendPacket(new ShortCutInit(player));
		player.sendPacket(new ExBasicActionList(ExBasicActionList.DEFAULT_ACTION_LIST));
		player.sendSkillList(skill.getId());
		
		player.updateShortCuts(_id, _level, 0);
		showSkillList(trainer, player);
		
		// If skill is expand type then sends packet:
		if ((_id >= 1368) && (_id <= 1372))
		{
			player.sendPacket(new ExStorageMaxCount(player));
		}
		
		// Notify scripts of the skill learn.
		if (trainer != null)
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, _skillType), trainer);
		}
		else
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, _skillType), player);
		}
	}
	
	/**
	 * Wrapper for returning the skill list to the player after it's done with current skill.
	 * @param trainer the Npc which the {@code player} is interacting
	 * @param player the active character
	 */
	private void showSkillList(Npc trainer, PlayerInstance player)
	{
		if (_skillType == AcquireSkillType.SUBCLASS)
		{
			showSubSkillList(player);
		}
		else if (_skillType == AcquireSkillType.DUALCLASS)
		{
			showDualSkillList(player);
		}
		else if (trainer instanceof FishermanInstance)
		{
			FishermanInstance.showFishSkillList(player);
		}
	}
	
	/**
	 * Verify if the player can transform.
	 * @param player the player to verify
	 * @return {@code true} if the player meets the required conditions to learn a transformation, {@code false} otherwise
	 */
	public static boolean canTransform(PlayerInstance player)
	{
		if (Config.ALLOW_TRANSFORM_WITHOUT_QUEST)
		{
			return true;
		}
		final QuestState qs = player.getQuestState("Q00136_MoreThanMeetsTheEye");
		return (qs != null) && qs.isCompleted();
	}
}
