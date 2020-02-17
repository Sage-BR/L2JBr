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
package ai.areas.MonasteryOfSilence;

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.effects.EffectType;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Monastery of Silence AI.
 * @author Kerberos, nonom
 */
public class MonasteryOfSilence extends AbstractNpcAI
{
	// NPCs
	private static final int CAPTAIN = 18910; // Solina Knight Captain
	private static final int KNIGHT = 18909; // Solina Knights
	private static final int SCARECROW = 18912; // Scarecrow
	private static final int GUIDE = 22789; // Guide Solina
	private static final int SEEKER = 22790; // Seeker Solina
	private static final int SAVIOR = 22791; // Savior Solina
	private static final int ASCETIC = 22793; // Ascetic Solina
	private static final int[] DIVINITY_CLAN =
	{
		22794, // Divinity Judge
		22795, // Divinity Manager
	};
	// Skills
	private static final SkillHolder ORDEAL_STRIKE = new SkillHolder(6303, 1); // Trial of the Coup
	private static final SkillHolder LEADER_STRIKE = new SkillHolder(6304, 1); // Shock
	private static final SkillHolder SAVER_STRIKE = new SkillHolder(6305, 1); // Sacred Gnosis
	private static final SkillHolder SAVER_BLEED = new SkillHolder(6306, 1); // Solina Strike
	private static final SkillHolder LEARNING_MAGIC = new SkillHolder(6308, 1); // Opus of the Wave
	private static final SkillHolder STUDENT_CANCEL = new SkillHolder(6310, 1); // Loss of Quest
	private static final SkillHolder WARRIOR_THRUSTING = new SkillHolder(6311, 1); // Solina Thrust
	private static final SkillHolder KNIGHT_BLESS = new SkillHolder(6313, 1); // Solina Bless
	// Misc
	private static final NpcStringId[] DIVINITY_MSG =
	{
		NpcStringId.S1_WHY_WOULD_YOU_CHOOSE_THE_PATH_OF_DARKNESS,
		NpcStringId.S1_HOW_DARE_YOU_DEFY_THE_WILL_OF_EINHASAD
	};
	
	// Removed with Etina's Fate.
	// private static final NpcStringId[] SOLINA_KNIGHTS_MSG =
	// {
	// NpcStringId.PUNISH_ALL_THOSE_WHO_TREAD_FOOTSTEPS_IN_THIS_PLACE,
	// NpcStringId.WE_ARE_THE_SWORD_OF_TRUTH_THE_SWORD_OF_SOLINA,
	// NpcStringId.WE_RAISE_OUR_BLADES_FOR_THE_GLORY_OF_SOLINA
	// };
	
	private MonasteryOfSilence()
	{
		addSkillSeeId(DIVINITY_CLAN);
		addAttackId(KNIGHT, CAPTAIN, GUIDE, SEEKER, ASCETIC);
		addNpcHateId(GUIDE, SEEKER, SAVIOR, ASCETIC);
		addAggroRangeEnterId(GUIDE, SEEKER, SAVIOR, ASCETIC);
		addSpawnId(SCARECROW);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "TRAINING":
			{
				World.getInstance().forEachVisibleObjectInRange(npc, Npc.class, 400, character ->
				{
					if ((getRandom(100) < 30) && !character.isDead() && !character.isInCombat())
					{
						if ((character.getId() == CAPTAIN) && (getRandom(100) < 10) && npc.isScriptValue(0))
						{
							// character.broadcastSay(ChatType.NPC_GENERAL, SOLINA_KNIGHTS_MSG[getRandom(SOLINA_KNIGHTS_MSG.length)]);
							character.setScriptValue(1);
							startQuestTimer("TIMER", 10000, character, null);
						}
						else if (character.getId() == KNIGHT)
						{
							character.setRunning();
							((Attackable) character).addDamageHate(npc, 0, 100);
							character.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc, null);
						}
					}
				});
				break;
			}
			case "DO_CAST":
			{
				if ((npc != null) && (player != null) && (getRandom(100) < 3))
				{
					if (SkillCaster.checkUseConditions(npc, STUDENT_CANCEL.getSkill()))
					{
						npc.setTarget(player);
						npc.doCast(STUDENT_CANCEL.getSkill());
					}
					npc.setScriptValue(0);
				}
				break;
			}
			case "TIMER":
			{
				if (npc != null)
				{
					npc.setScriptValue(0);
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance player, int damage, boolean isSummon)
	{
		final Attackable mob = (Attackable) npc;
		
		switch (npc.getId())
		{
			case KNIGHT:
			{
				if ((getRandom(100) < 10) && (mob.getMostHated() == player) && SkillCaster.checkUseConditions(mob, WARRIOR_THRUSTING.getSkill()))
				{
					npc.setTarget(player);
					npc.doCast(WARRIOR_THRUSTING.getSkill());
				}
				break;
			}
			case CAPTAIN:
			{
				if ((getRandom(100) < 20) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.5)) && npc.isScriptValue(0))
				{
					if (SkillCaster.checkUseConditions(npc, KNIGHT_BLESS.getSkill()))
					{
						npc.setTarget(npc);
						npc.doCast(KNIGHT_BLESS.getSkill());
					}
					npc.setScriptValue(1);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.FOR_THE_GLORY_OF_SOLINA);
					addAttackPlayerDesire(addSpawn(KNIGHT, npc), player);
				}
				break;
			}
			case GUIDE:
			{
				if ((getRandom(100) < 3) && (mob.getMostHated() == player) && SkillCaster.checkUseConditions(npc, ORDEAL_STRIKE.getSkill()))
				{
					npc.setTarget(player);
					npc.doCast(ORDEAL_STRIKE.getSkill());
				}
				break;
			}
			case SEEKER:
			{
				if ((getRandom(100) < 33) && (mob.getMostHated() == player) && SkillCaster.checkUseConditions(npc, SAVER_STRIKE.getSkill()))
				{
					npc.setTarget(npc);
					npc.doCast(SAVER_STRIKE.getSkill());
				}
				break;
			}
			case ASCETIC:
			{
				if ((mob.getMostHated() == player) && npc.isScriptValue(0))
				{
					npc.setScriptValue(1);
					startQuestTimer("DO_CAST", 20000, npc, player);
				}
				break;
			}
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
	
	@Override
	public boolean onNpcHate(Attackable mob, PlayerInstance player, boolean isSummon)
	{
		return player.getActiveWeaponInstance() != null;
	}
	
	@Override
	public String onAggroRangeEnter(Npc npc, PlayerInstance player, boolean isSummon)
	{
		if (player.getActiveWeaponInstance() != null)
		{
			SkillHolder skill = null;
			switch (npc.getId())
			{
				case GUIDE:
				{
					if (getRandom(100) < 3)
					{
						skill = LEADER_STRIKE;
					}
					break;
				}
				case SEEKER:
				{
					skill = SAVER_BLEED;
					break;
				}
				case SAVIOR:
				{
					skill = LEARNING_MAGIC;
					break;
				}
				case ASCETIC:
				{
					if (getRandom(100) < 3)
					{
						skill = STUDENT_CANCEL;
					}
					
					if (npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						startQuestTimer("DO_CAST", 20000, npc, player);
					}
					break;
				}
			}
			
			if ((skill != null) && SkillCaster.checkUseConditions(npc, skill.getSkill()))
			{
				npc.setTarget(player);
				npc.doCast(skill.getSkill());
			}
			
			if (!npc.isInCombat())
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_CANNOT_CARRY_A_WEAPON_WITHOUT_AUTHORIZATION);
			}
			
			addAttackPlayerDesire(npc, player);
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance caster, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		if (skill.hasEffectType(EffectType.AGGRESSION) && (targets.length != 0))
		{
			for (WorldObject obj : targets)
			{
				if (obj.equals(npc))
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, DIVINITY_MSG[getRandom(DIVINITY_MSG.length)], caster.getName());
					addAttackPlayerDesire(npc, caster);
					break;
				}
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setIsInvul(true);
		npc.disableCoreAI(true);
		cancelQuestTimer("TRAINING", npc, null);
		startQuestTimer("TRAINING", 30000, npc, null, true);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new MonasteryOfSilence();
	}
}
