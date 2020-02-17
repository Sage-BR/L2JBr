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
package ai.areas.BeastFarm;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.Summon;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

import ai.AbstractNpcAI;

/**
 * Improved Baby Pets AI.
 * @author St3eT
 */
public class ImprovedBabyPets extends AbstractNpcAI
{
	// NPCs
	private static final int[] BABY_PETS =
	{
		16034, // Improved Baby Buffalo
		16035, // Improved Baby Kookaburra
		16036, // Improved Baby Cougar
	};
	// Skills
	private static final int PET_CONTROL = 5771;
	
	private ImprovedBabyPets()
	{
		addSummonSpawnId(BABY_PETS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (player != null)
		{
			final Summon summon = player.getPet();
			
			if (summon == null)
			{
				cancelQuestTimer("HEAL", null, player);
				cancelQuestTimer("BUFF", null, player);
			}
			else if (event.equals("HEAL") && player.isInCombat() && !summon.isHungry())
			{
				final double hpPer = (player.getCurrentHp() / player.getMaxHp()) * 100;
				final double mpPer = (player.getCurrentMp() / player.getMaxMp()) * 100;
				final int healType = summon.getTemplate().getParameters().getInt("heal_type", 0);
				final int skillLv = (int) Math.floor((summon.getLevel() / 5) - 11);
				
				if (healType == 1)
				{
					final int stepLv = CommonUtil.constrain(skillLv, 0, 3);
					
					if ((hpPer >= 30) && (hpPer < 70))
					{
						castHeal(summon, stepLv, 1);
					}
					else if (hpPer < 30)
					{
						castHeal(summon, stepLv, 2);
					}
				}
				else if (healType == 0)
				{
					if (hpPer < 30)
					{
						castHeal(summon, CommonUtil.constrain(skillLv, 0, 3), 2);
					}
					else if (mpPer < 60)
					{
						castHeal(summon, CommonUtil.constrain(skillLv, 0, 5), 1);
					}
				}
			}
			else if (event.equals("BUFF") && !summon.isAffectedBySkill(PET_CONTROL) && !summon.isHungry())
			{
				final int buffStep = (int) CommonUtil.constrain(Math.floor((summon.getLevel() / 5) - 11), 0, 3);
				
				for (int i = 1; i <= (2 * (1 + buffStep)); i++)
				{
					if (castBuff(summon, buffStep, i))
					{
						break;
					}
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public void onSummonSpawn(Summon summon)
	{
		startQuestTimer("HEAL", 5000, null, summon.getOwner(), true);
		startQuestTimer("BUFF", 10000, null, summon.getOwner(), true);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void OnPlayerLogout(OnPlayerLogout event)
	{
		cancelQuestTimer("HEAL", null, event.getPlayer());
		cancelQuestTimer("BUFF", null, event.getPlayer());
	}
	
	private boolean castBuff(Summon summon, int stepNumber, int buffNumber)
	{
		final PlayerInstance owner = summon.getOwner();
		final StatsSet parameters = summon.getTemplate().getParameters();
		final SkillHolder skill = parameters.getObject("step" + stepNumber + "_buff0" + buffNumber, SkillHolder.class);
		
		if ((skill != null) && (owner != null))
		{
			final boolean previousFollowStatus = summon.getFollowStatus();
			final SkillHolder mergedSkill = parameters.getObject("step" + stepNumber + "_merged_buff0" + buffNumber, SkillHolder.class);
			final int targetType = parameters.getInt("step" + stepNumber + "_buff_target0" + buffNumber, 0);
			
			if (!owner.hasAbnormalType(skill.getSkill().getAbnormalType()) && SkillCaster.checkUseConditions(summon, skill.getSkill()) && !owner.isDead())
			{
				if (mergedSkill != null)
				{
					if (owner.hasAbnormalType(mergedSkill.getSkill().getAbnormalType()))
					{
						return false;
					}
				}
				
				if (!previousFollowStatus && !summon.isInsideRadius3D(owner, skill.getSkill().getCastRange()))
				{
					return false;
				}
				
				if ((targetType >= 0) && (targetType <= 2))
				{
					summon.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill.getSkill(), (targetType == 1) ? summon : owner);
					summon.sendPacket(new SystemMessage(SystemMessageId.YOUR_PET_USES_S1).addSkillName(skill.getSkill()));
					
					if (previousFollowStatus != summon.getFollowStatus())
					{
						summon.setFollowStatus(previousFollowStatus);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	private void castHeal(Summon summon, int stepNumber, int healNumber)
	{
		final boolean previousFollowStatus = summon.getFollowStatus();
		final PlayerInstance owner = summon.getOwner();
		final StatsSet parameters = summon.getTemplate().getParameters();
		final SkillHolder skill = parameters.getObject("step" + stepNumber + "_heal0" + healNumber, SkillHolder.class);
		final int targetType = parameters.getInt("step" + stepNumber + "_heal_target0" + healNumber, 0);
		
		if ((skill != null) && (owner != null) && SkillCaster.checkUseConditions(summon, skill.getSkill()) && !owner.isDead())
		{
			if (!previousFollowStatus && !summon.isInsideRadius3D(owner, skill.getSkill().getCastRange()))
			{
				return;
			}
			
			if (!owner.hasAbnormalType(skill.getSkill().getAbnormalType()))
			{
				if ((targetType >= 0) && (targetType <= 2))
				{
					summon.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill.getSkill(), (targetType == 1) ? summon : owner);
					summon.sendPacket(new SystemMessage(SystemMessageId.YOUR_PET_USES_S1).addSkillName(skill.getSkill()));
					
					if (previousFollowStatus != summon.getFollowStatus())
					{
						summon.setFollowStatus(previousFollowStatus);
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new ImprovedBabyPets();
	}
}