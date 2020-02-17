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

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * Area Skill Npc AI.
 * @author St3eT
 */
public class AreaSkillNpc extends AbstractNpcAI
{
	// NPCs
	private static final int[] BASIC = // area_skill_npc
	{
		143, // Totem of Body
		144, // Totem of Spirit
		145, // Totem of Bravery
		146, // Totem of Fortitude
		13018, // Maximum Defense
		13019, // Anti-Music
		13020, // Maximum Resist Status
		13021, // Maximum Recovery
		13022, // Recover Force
		13023, // Maximize long-range weapon use
		13024, // Smokescreen
		13028, // Day of Doom
		13030, // Anti-Summoning Field
		13323, // Whisper of Fear
		13324, // Whisper of Fear
		13325, // Whisper of Fear
		13458, // Whisper of Fear
		13459, // Whisper of Fear
		13460, // Whisper of Fear
		13310, // Solo Dance
		13377, // Solo Dance
		13378, // Solo Dance
		13379, // Solo Dance
		13380, // Solo Dance
		13381, // Solo Dance
		13452, // Solo Dance
		13453, // Solo Dance
		13454, // Solo Dance
		13576, // Solo Dance
		13577, // Solo Dance
		13541, // Solo Dance
		13542, // Solo Dance
		13543, // Solo Dance
		13544, // Solo Dance
		13545, // Solo Dance
		13546, // Solo Dance
		13547, // Solo Dance
		13548, // Solo Dance
		13549, // Solo Dance
		13580, // Solo Dance
		13581, // Solo Dance
	};
	private static final int[] DECOY = // ai_decoy
	{
		13071, // Virtual Image
		13072, // Virtual Image
		13073, // Virtual Image
		13074, // Virtual Image
		13075, // Virtual Image
		13076, // Virtual Image
		13257, // Virtual Image
		13258, // Virtual Image
		13259, // Virtual Image
		13260, // Virtual Image
		13261, // Virtual Image
		13262, // Virtual Image
		13263, // Virtual Image
		13264, // Virtual Image
		13265, // Virtual Image
		13266, // Virtual Image
		13267, // Virtual Image
		13328, // Decoy
	};
	
	private AreaSkillNpc()
	{
		addSpawnId(BASIC);
		addSpawnId(DECOY);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (CommonUtil.contains(DECOY, npc.getId()))
		{
			final int castTime = npc.getTemplate().getParameters().getInt("i_use_term_time", 5000);
			final int despawnTime = npc.getTemplate().getParameters().getInt("i_despawn_time", 30000);
			onTimerEvent("SKILL_CAST_BASIC", null, npc, null); // Trigger cast instantly
			getTimers().cancelTimer("SKILL_CAST_TIMED", npc, null);
			getTimers().cancelTimer("DELETE_ME", npc, null);
			getTimers().addTimer("SKILL_CAST_TIMED", castTime, npc, null);
			getTimers().addTimer("DELETE_ME", despawnTime, npc, null);
		}
		else
		{
			final int despawnTime = npc.getTemplate().getParameters().getInt("despawn_time", 7);
			getTimers().cancelTimer("SKILL_CAST_TIMED", npc, null);
			getTimers().cancelTimer("DELETE_ME", npc, null);
			getTimers().addTimer("SKILL_CAST_TIMED", 100, npc, null);
			getTimers().addTimer("DELETE_ME", (despawnTime * 1000), npc, null);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "SKILL_CAST_BASIC":
			{
				final SkillHolder skill = npc.getParameters().getSkillHolder(CommonUtil.contains(DECOY, npc.getId()) ? "decoy_skill" : "union_skill");
				if (skill != null)
				{
					npc.doCast(skill.getSkill());
				}
				break;
			}
			case "SKILL_CAST_TIMED":
			{
				final SkillHolder skill = npc.getParameters().getSkillHolder(CommonUtil.contains(DECOY, npc.getId()) ? "decoy_skill" : "union_skill");
				if (skill != null)
				{
					npc.doCast(skill.getSkill());
					getTimers().addRepeatingTimer("SKILL_CAST_BASIC", npc.getParameters().getInt("skill_delay", 2) * 1000, npc, null);
				}
				break;
			}
			case "DELETE_ME":
			{
				getTimers().cancelTimer("SKILL_CAST_BASIC", npc, null);
				npc.deleteMe();
				break;
			}
		}
	}
	
	public static void main(String[] args)
	{
		new AreaSkillNpc();
	}
}