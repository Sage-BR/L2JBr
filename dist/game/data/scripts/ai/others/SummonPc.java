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
package ai.others;

import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.Skill;

import ai.AbstractNpcAI;

/**
 * Summon Pc AI.<br>
 * Summon the player to the NPC on attack.
 * @author Zoey76
 */
public class SummonPc extends AbstractNpcAI
{
	// NPCs
	private static final int PORTA = 20213;
	private static final int PERUM = 20221;
	// Skill
	private static final SkillHolder SUMMON_PC = new SkillHolder(4161, 1);
	
	private SummonPc()
	{
		addAttackId(PORTA, PERUM);
		addSpellFinishedId(PORTA, PERUM);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final int chance = getRandom(100);
		final boolean attacked = npc.getVariables().getBoolean("attacked", false);
		if ((npc.calculateDistance3D(attacker) > 300) && !attacked)
		{
			if (chance < 50)
			{
				if ((SUMMON_PC.getSkill().getMpConsume() < npc.getCurrentMp()) && (SUMMON_PC.getSkill().getHpConsume() < npc.getCurrentHp()) && !npc.isSkillDisabled(SUMMON_PC.getSkill()))
				{
					npc.setTarget(attacker);
					npc.doCast(SUMMON_PC.getSkill());
				}
				
				if ((SUMMON_PC.getSkill().getMpConsume() < npc.getCurrentMp()) && (SUMMON_PC.getSkill().getHpConsume() < npc.getCurrentHp()) && !npc.isSkillDisabled(SUMMON_PC.getSkill()))
				{
					npc.setTarget(attacker);
					npc.doCast(SUMMON_PC.getSkill());
					npc.getVariables().set("attacked", true);
				}
			}
		}
		else if ((npc.calculateDistance3D(attacker) > 100) && !attacked)
		{
			final Attackable monster = (Attackable) npc;
			if (monster.getMostHated() != null)
			{
				if (((monster.getMostHated() == attacker) && (chance < 50)) || (chance < 10))
				{
					if ((SUMMON_PC.getSkill().getMpConsume() < npc.getCurrentMp()) && (SUMMON_PC.getSkill().getHpConsume() < npc.getCurrentHp()) && !npc.isSkillDisabled(SUMMON_PC.getSkill()))
					{
						npc.setTarget(attacker);
						npc.doCast(SUMMON_PC.getSkill());
						npc.getVariables().set("attacked", true);
					}
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		if ((skill.getId() == SUMMON_PC.getSkillId()) && !npc.isDead() && npc.getVariables().getBoolean("attacked", false))
		{
			player.teleToLocation(npc);
			npc.getVariables().set("attacked", false);
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	public static void main(String[] args)
	{
		new SummonPc();
	}
}
