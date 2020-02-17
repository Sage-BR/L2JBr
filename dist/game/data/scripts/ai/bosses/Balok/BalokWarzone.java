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
package ai.bosses.Balok;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.BuffInfo;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

import instances.AbstractInstance;

/**
 * Balok Warzone instance zone.
 * @author LasTravel, Gigi
 * @Video https://www.youtube.com/watch?v=w_-SNNPoulo&t=204s
 */
public class BalokWarzone extends AbstractInstance
{
	// NPCs
	private static final int BALOK = 29218;
	private static final int MINION = 23123;
	private static final int HELL_DISCIPLE = 29219;
	private static final int ENTRANCE_PORTAL = 33523;
	private static final int INVISIBLE_NPC_1 = 29106;
	private static final int HELLS_GATE = 19040;
	// Item
	private static final int PRISON_KEY = 10015;
	// Skills
	private static final SkillHolder DARKNESS_DRAIN = new SkillHolder(14367, 1);
	private static final SkillHolder REAR_DESTROY = new SkillHolder(14576, 1);
	private static final SkillHolder EARTH_DEMOLITION = new SkillHolder(14246, 1);
	private static final SkillHolder IMPRISION = new SkillHolder(5226, 1);
	private static final SkillHolder INVINCIBILITY_ACTIVATION = new SkillHolder(14190, 1);
	// Misc
	private static final int TEMPLATE_ID = 167;
	//@formatter:off
	private static final int[][] MINION_SPAWN = 
	{
		{154592, 141488, -12738, 26941},
        {154759, 142073, -12738, 32333},
        {154158, 143112, -12738, 43737},
        {152963, 143102, -12738, 53988},
        {152360, 142067, -12740, 0},
        {152530, 141457, -12740, 7246},
        {153571, 140878, -12738, 16756},
        {154174, 141057, -12738, 22165}
	};
	private static final int[][] PRISONS_SPAWN = 
	{
		{154428, 140551, -12712},
        {155061, 141204, -12704},
        {155268, 142097, -12712},
        {154438, 143581, -12712},
        {152695, 143560, -12704},
        {151819, 142063, -12712},
        {152055, 141231, -12712},
        {153608, 140371, -12712}
	};
	//@formatter:on	
	private final List<Npc> minionList = new ArrayList<>();
	private Npc currentMinion;
	private Npc balok;
	
	public BalokWarzone()
	{
		super(TEMPLATE_ID);
		addStartNpc(ENTRANCE_PORTAL);
		addTalkId(ENTRANCE_PORTAL);
		addInstanceCreatedId(TEMPLATE_ID);
		addAttackId(BALOK);
		addSkillSeeId(BALOK);
		addKillId(BALOK, MINION);
		addSpellFinishedId(BALOK);
		addSpawnId(INVISIBLE_NPC_1);
		setCreatureSeeId(this::onCreatureSee, INVISIBLE_NPC_1);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterInstance"))
		{
			enterInstance(player, npc, TEMPLATE_ID);
			if (hasQuestItems(player, PRISON_KEY))
			{
				takeItems(player, PRISON_KEY, -1);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (event)
			{
				case "stage_1_start":
				{
					playMovie(world.getPlayers(), Movie.SI_BARLOG_STORY);
					getTimers().addTimer("stage_1_balok_intro", 72500, npc, null);
					break;
				}
				case "stage_1_balok_intro":
				{
					playMovie(world.getPlayers(), Movie.SI_BARLOG_OPENING);
					getTimers().addTimer("stage_1_spawn_balok", 21300, npc, null);
					break;
				}
				case "stage_1_spawn_balok":
				{
					balok = addSpawn(BALOK, 153573, 142071, -12738, 16565, false, 0, false, world.getId());
					world.setStatus(1);
					break;
				}
				case "stage_last_send_minions":
				{
					
					Npc minion = minionList.get(Rnd.get(minionList.size()));
					if (minion != null)
					{
						minion.setRunning();
						((Attackable) minion).setCanReturnToSpawnPoint(false);
						currentMinion = minion;
						getTimers().addTimer("stage_last_minion_walk", 2000, minion, player);
					}
					
					break;
				}
				case "stage_last_minion_walk":
				{
					if (npc.getId() == MINION)
					{
						if (npc.calculateDistanceSq2D(balok) > 113125)
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(balok.getX() + 100, balok.getY() + 50, balok.getZ(), balok.getHeading()));
							getTimers().addTimer("stage_last_minion_walk", 2000, npc, player);
						}
						else
						{
							npc.stopSkillEffects(INVINCIBILITY_ACTIVATION.getSkill());
							balok.setTarget(npc);
							balok.doCast(DARKNESS_DRAIN.getSkill());
						}
					}
					break;
				}
				case "stage_spawn_apostols":
				{
					for (int i = 0; i < 4; i++)
					{
						Npc disciple = addSpawn(HELL_DISCIPLE, npc.getX(), npc.getY(), npc.getZ(), 0, true, 600000, false, world.getId());
						addAttackPlayerDesire(disciple, player);
					}
					break;
				}
				case "imprission_minions":
				{
					int[] randomJail = PRISONS_SPAWN[Rnd.get(PRISONS_SPAWN.length)]; // Random jail
					player.teleToLocation(randomJail[0], randomJail[1], randomJail[2]);
					world.broadcastPacket(new ExShowScreenMessage("$s1, locked away in the prison.".replace("$s1", player.getName()), 5000));
					break;
				}
			}
		}
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (npc.getId() == BALOK)
			{
				if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.85)) && (world.getStatus() == 1))
				{
					for (int[] a : MINION_SPAWN)
					{
						Npc minion = addSpawn(MINION, a[0], a[1], a[2], a[3], false, 0, false, world.getId());
						minionList.add(minion);
						INVINCIBILITY_ACTIVATION.getSkill().applyEffects(minion, minion);
						world.setStatus(2);
					}
				}
				if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.50)) && (world.getStatus() == 2))
				{
					if (npc.isScriptValue(0))
					{
						INVINCIBILITY_ACTIVATION.getSkill().applyEffects(npc, npc);
						npc.setScriptValue(1);
					}
					World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 300, instPlayer ->
					{
						if ((instPlayer == null) || (Rnd.get(100) > 2))
						{
							return;
						}
						npc.setTarget(instPlayer);
						npc.doCast(IMPRISION.getSkill());
						getTimers().addTimer("imprission_minions", 4000, npc, instPlayer);
					});
					getTimers().addTimer("stage_last_send_minions", 2000, npc, null);
				}
				if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.25)) && npc.isScriptValue(1))
				{
					npc.setScriptValue(2);
					npc.doCast(EARTH_DEMOLITION.getSkill());
					addSpawn(HELLS_GATE, npc.getX() + 100, npc.getY() + 50, npc.getZ(), npc.getHeading(), false, 0, false, world.getId());
					getTimers().addTimer("stage_spawn_apostols", 2000, npc, attacker);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (world != null)
			{
				if (skill.getId() == DARKNESS_DRAIN.getSkillId())
				{
					if (!currentMinion.isDead())
					{
						balok.setCurrentHp(balok.getCurrentHp() + currentMinion.getMaxHp());
					}
				}
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isPet)
	{
		final Instance world = npc.getInstanceWorld();
		
		if (npc == balok)
		{
			if (world.getAliveNpcs(BALOK).isEmpty())
			{
				world.getAliveNpcs(MINION, HELL_DISCIPLE, HELLS_GATE).forEach(guard -> guard.doDie(null));
				world.removeNpcs();
				world.finishInstance();
				world.broadcastPacket(new SystemMessage(SystemMessageId.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTE_S_YOU_WILL_BE_FORCED_OUT_OF_THE_DUNGEON_WHEN_THE_TIME_EXPIRES).addInt((int) 5.0D));
			}
			else
			{
				world.setReenterTime();
			}
		}
		else if (npc == currentMinion)
		{
			synchronized (minionList)
			{
				if (minionList.contains(npc))
				{
					minionList.remove(npc);
					
					if (minionList.size() > 0)
					{
						startQuestTimer("stage_last_send_minions", 2000, npc, null);
					}
					else
					{
						balok.stopSkillEffects(INVINCIBILITY_ACTIVATION.getSkill());
					}
				}
			}
		}
		return super.onKill(npc, player, isPet);
	}
	
	public void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world) && creature.isPlayer() && npc.isScriptValue(0))
		{
			npc.setScriptValue(1);
			getTimers().addTimer("stage_1_start", 60000, npc, null);
		}
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance caster, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		if (!npc.isDead() && caster.isBehind(npc))
		{
			final BuffInfo info = npc.getEffectList().getBuffInfoBySkillId(INVINCIBILITY_ACTIVATION.getSkillId());
			if ((info != null) && (getRandom(100) < 40))
			{
				npc.stopSkillEffects(INVINCIBILITY_ACTIVATION.getSkill());
			}
			npc.setTarget(caster);
			npc.doCast(REAR_DESTROY.getSkill());
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (npc.getId() == INVISIBLE_NPC_1)
		{
			npc.initSeenCreatures();
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new BalokWarzone();
	}
}