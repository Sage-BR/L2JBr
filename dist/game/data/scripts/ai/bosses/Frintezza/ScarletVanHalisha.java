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
package ai.bosses.Frintezza;

import static org.l2jbr.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static org.l2jbr.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;
import static org.l2jbr.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;

import java.util.ArrayList;

import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.geoengine.GeoEngine;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.DecoyInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.util.Util;

import ai.AbstractNpcAI;

/**
 * @author Micr0, Zerox, Mobius
 */
public class ScarletVanHalisha extends AbstractNpcAI
{
	// NPCs
	private static final int HALISHA2 = 29046;
	private static final int HALISHA3 = 29047;
	// Skills
	private static final int FRINTEZZA_DAEMON_ATTACK = 5014;
	private static final int FRINTEZZA_DAEMON_CHARGE = 5015;
	private static final int YOKE_OF_SCARLET = 5016;
	private static final int FRINTEZZA_DAEMON_MORPH = 5018;
	private static final int FRINTEZZA_DAEMON_FIELD = 5019;
	// Misc
	private Creature _target;
	private Skill _skill;
	private long _lastRangedSkillTime;
	private final int _rangedSkillMinCoolTime = 60000; // 1 minute
	
	public ScarletVanHalisha()
	{
		addAttackId(HALISHA2, HALISHA3);
		addKillId(HALISHA2, HALISHA3);
		addSpellFinishedId(HALISHA2, HALISHA3);
		registerMobs(HALISHA2, HALISHA3);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "attack":
			{
				if (npc != null)
				{
					getSkillAI(npc);
				}
				break;
			}
			case "random_target":
			{
				_target = getRandomTarget(npc, null);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		getSkillAI(npc);
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		startQuestTimer("random_Target", 5000, npc, null, true);
		startQuestTimer("attack", 500, npc, null, true);
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		cancelQuestTimers("attack");
		cancelQuestTimers("random_Target");
		return super.onKill(npc, killer, isSummon);
	}
	
	private Skill getRndSkills(Npc npc)
	{
		switch (npc.getId())
		{
			case HALISHA2:
			{
				if (getRandom(100) < 10)
				{
					return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_CHARGE, 2);
				}
				else if (getRandom(100) < 10)
				{
					return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_CHARGE, 5);
				}
				else if (getRandom(100) < 2)
				{
					return SkillData.getInstance().getSkill(YOKE_OF_SCARLET, 1);
				}
				else
				{
					return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_ATTACK, 2);
				}
			}
			case HALISHA3:
			{
				if (getRandom(100) < 10)
				{
					return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_CHARGE, 3);
				}
				else if (getRandom(100) < 10)
				{
					return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_CHARGE, 6);
				}
				else if (getRandom(100) < 10)
				{
					return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_CHARGE, 2);
				}
				else if (((_lastRangedSkillTime + _rangedSkillMinCoolTime) < System.currentTimeMillis()) && (getRandom(100) < 10))
				{
					return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_FIELD, 1);
				}
				else if (((_lastRangedSkillTime + _rangedSkillMinCoolTime) < System.currentTimeMillis()) && (getRandom(100) < 10))
				{
					return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_MORPH, 1);
				}
				else if (getRandom(100) < 2)
				{
					return SkillData.getInstance().getSkill(YOKE_OF_SCARLET, 1);
				}
				else
				{
					return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_ATTACK, 3);
				}
			}
		}
		
		return SkillData.getInstance().getSkill(FRINTEZZA_DAEMON_ATTACK, 1);
	}
	
	private synchronized void getSkillAI(Npc npc)
	{
		if (npc.isInvul() || npc.isCastingNow())
		{
			return;
		}
		if ((getRandom(100) < 30) || (_target == null) || _target.isDead())
		{
			_skill = getRndSkills(npc);
			_target = getRandomTarget(npc, _skill);
		}
		final Creature target = _target;
		Skill skill = _skill;
		if (skill == null)
		{
			skill = getRndSkills(npc);
		}
		
		if (npc.isPhysicalMuted())
		{
			return;
		}
		
		if ((target == null) || target.isDead())
		{
			// npc.setIsCastingNow(false);
			return;
		}
		
		if (Util.checkIfInRange(skill.getCastRange(), npc, target, true))
		{
			npc.getAI().setIntention(AI_INTENTION_IDLE);
			npc.setTarget(target);
			// npc.setIsCastingNow(true);
			_target = null;
			npc.doCast(skill);
		}
		else
		{
			npc.getAI().setIntention(AI_INTENTION_FOLLOW, target, null);
			npc.getAI().setIntention(AI_INTENTION_ATTACK, target, null);
			// npc.setIsCastingNow(false);
		}
	}
	
	private Creature getRandomTarget(Npc npc, Skill skill)
	{
		final ArrayList<Creature> result = new ArrayList<>();
		{
			for (WorldObject obj : npc.getInstanceWorld().getPlayers())
			{
				if (obj.isPlayable() || (obj instanceof DecoyInstance))
				{
					if (obj.isPlayer() && obj.getActingPlayer().isInvisible())
					{
						continue;
					}
					
					if (((((Creature) obj).getZ() < (npc.getZ() - 100)) && (((Creature) obj).getZ() > (npc.getZ() + 100))) || !GeoEngine.getInstance().canSeeTarget(obj, npc))
					{
						continue;
					}
				}
				if (obj.isPlayable() || (obj instanceof DecoyInstance))
				{
					int skillRange = 150;
					if (skill != null)
					{
						switch (skill.getId())
						{
							case FRINTEZZA_DAEMON_ATTACK:
							{
								skillRange = 150;
								break;
							}
							case FRINTEZZA_DAEMON_CHARGE:
							{
								skillRange = 400;
								break;
							}
							case YOKE_OF_SCARLET:
							{
								skillRange = 200;
								break;
							}
							case FRINTEZZA_DAEMON_MORPH:
							case FRINTEZZA_DAEMON_FIELD:
							{
								_lastRangedSkillTime = System.currentTimeMillis();
								skillRange = 550;
								break;
							}
						}
					}
					if (Util.checkIfInRange(skillRange, npc, obj, true) && !((Creature) obj).isDead())
					{
						result.add((Creature) obj);
					}
				}
			}
		}
		return getRandomEntry(result);
	}
	
	public static void main(String[] args)
	{
		new ScarletVanHalisha();
	}
}