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
package ai.bosses.Kimerian;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureDeath;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.util.Util;

import instances.AbstractInstance;

/**
 * Kimerian Common instance zone.
 * @author St3eT, Gladicek
 */
public class KimerianCommon extends AbstractInstance
{
	// NPCs
	private static final int KIMERIAN = 25745;
	private static final int KIMERIAN_GHOST = 25746;
	private static final int KIMERIAN_DEAD = 25747;
	private static final int NOETI_KASHERON = 32896;
	private static final int NOETI_KASHERON_ENTRANCE = 33098;
	private static final int NOETI_KASHERON_LEAVE = 33131;
	private static final int NEOMI_KASHERON = 32914;
	private static final int FAIRY_REBEL = 32913;
	private static final int INVISIBLE_NPC = 33137;
	private static final int KIMERIAN_HOLLOW = 25745;
	private static final int KIMERIAN_HOLLOW_2 = 25746;
	private static final int FAIRY_WARRIOR = 25735;
	private static final int SATYR_WITCH = 25740;
	private static final int FAIRY_ROGUE = 25736;
	// Skills
	private static final SkillHolder INVUL_SKILL = new SkillHolder(14190, 1);
	// Items
	private static final int GLIMMER = 17374;
	private static final int FLUTE = 17378; // Fairy's Leaf Flute
	// Misc
	private static final NpcStringId[] KIMERIAN_MSG =
	{
		NpcStringId.THEY_ARE_ROOKIE_REBELLIONS,
		NpcStringId.RESISTANCE_UNDERLINGS,
		NpcStringId.TREASON_IS_PUNISHABLE_BY_DEATH,
		NpcStringId.WHO_DO_YOU_THINK_YOU_ARE_TO_TRY_MY_AUTHORITY,
	};
	private static final int TEMPLATE_ID = 161;
	
	public KimerianCommon()
	{
		super(TEMPLATE_ID);
		addStartNpc(NOETI_KASHERON);
		addTalkId(NOETI_KASHERON, NOETI_KASHERON_ENTRANCE);
		addFirstTalkId(NOETI_KASHERON_ENTRANCE, NOETI_KASHERON_LEAVE);
		addSpawnId(FAIRY_REBEL, NEOMI_KASHERON, INVISIBLE_NPC, KIMERIAN);
		addAttackId(KIMERIAN);
		addKillId(KIMERIAN_GHOST, KIMERIAN);
		setCreatureSeeId(this::onCreatureSee, FAIRY_REBEL, NEOMI_KASHERON, INVISIBLE_NPC, KIMERIAN);
		setCreatureKillId(this::onCreatureKill, FAIRY_REBEL, NEOMI_KASHERON);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (event)
			{
				case "HELPER_TIME_ACTION":
				{
					player = npc.getVariables().getObject("PC_INSTANCE", PlayerInstance.class);
					if (player != null)
					{
						final double distance = npc.calculateDistance2D(player);
						if (distance > 1000)
						{
							npc.teleToLocation(new Location(player.getX() + getRandom(-100, 100), player.getY() + getRandom(-100, 100), player.getZ() + 50));
						}
						else if (!npc.isAttackingNow() && (distance > 250))
						{
							npc.setRunning();
							addMoveToDesire(npc, new Location(player.getX() + getRandom(-100, 100), player.getY() + getRandom(-100, 100), player.getZ() + 50), 23);
						}
						else if (!npc.isInCombat() || !npc.isAttackingNow() || (npc.getTarget() == null))
						{
							final Creature monster = (Creature) player.getTarget();
							if ((monster != null) && monster.isMonster() && player.isInCombat())
							{
								addAttackDesire(npc, monster);
							}
						}
					}
					else
					{
						getTimers().cancelTimersOf(npc);
					}
					break;
				}
				case "KIMERIAN_INVUL_END":
				{
					if (npc.getVariables().getBoolean("INVUL_CAN_BE_CANCELLED", true))
					{
						npc.getVariables().set("INVUL_CAN_BE_CANCELLED", false);
						npc.getEffectList().stopSkillEffects(true, INVUL_SKILL.getSkillId());
						npc.disableCoreAI(false);
						npc.setTargetable(true);
						addAttackPlayerDesire(npc, player, 23);
					}
					break;
				}
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (event)
			{
				case "giveFlute":
				{
					if (npc.getVariables().getBoolean("CAN_SPAWN_SUPPORT", true))
					{
						if (hasQuestItems(player, FLUTE))
						{
							if (player.isInCategory(CategoryType.SIXTH_EOLH_GROUP))
							{
								addSpawn(FAIRY_REBEL, player.getX() + 60, player.getY(), player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX() - 60, player.getY(), player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX(), player.getY() + 60, player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX(), player.getY() - 60, player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX() + 120, player.getY(), player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX() - 120, player.getY(), player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX(), player.getY() + 120, player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX(), player.getY() - 120, player.getZ(), 0, false, 0, false, instance.getId());
								takeItems(player, FLUTE, 1);
								npc.getVariables().set("CAN_SPAWN_SUPPORT", false);
							}
							else
							{
								addSpawn(FAIRY_REBEL, player.getX() + 60, player.getY(), player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX() - 60, player.getY(), player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX(), player.getY() + 60, player.getZ(), 0, false, 0, false, instance.getId());
								addSpawn(FAIRY_REBEL, player.getX(), player.getY() - 60, player.getZ(), 0, false, 0, false, instance.getId());
								takeItems(player, FLUTE, 1);
								npc.getVariables().set("CAN_SPAWN_SUPPORT", false);
							}
						}
						else
						{
							htmltext = "33098-02.html";
						}
					}
					else
					{
						htmltext = "33098-03.html";
					}
					break;
				}
				case "zdrhamCus":
				{
					instance.destroy();
					break;
				}
			}
		}
		else if (instance == null)
		{
			if (event.equals("enterInstance"))
			{
				enterInstance(player, npc, TEMPLATE_ID);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance player, int damage, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			if (npc.getId() == KIMERIAN)
			{
				if ((npc.getCurrentHpPercent() <= 50) && npc.getVariables().getBoolean("CAN_ACTIVATE_INVUL", true))
				{
					npc.getVariables().set("CAN_ACTIVATE_INVUL", false);
					npc.setTargetable(false);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.PHANTOM_IMAGE);
					getTimers().addRepeatingTimer("KIMERIAN_INVUL_END", 60000, npc, player);
					
					for (int i = 0; i < 5; i++)
					{
						final Npc ghost = addSpawn(KIMERIAN_GHOST, npc.getX(), npc.getY(), npc.getZ(), Util.calculateHeadingFrom(npc, player), false, 0, false, instance.getId());
						addAttackPlayerDesire(ghost, player, 23);
					}
					
					npc.disableCoreAI(true);
					npc.breakAttack();
					npc.breakCast();
					((Attackable) npc).clearAggroList();
					
					getTimers().addTimer("KIMERIAN_INVUL_START", 6000, n ->
					{
						addSkillCastDesire(npc, npc, INVUL_SKILL, 23);
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.FOOLISH_INSIGNIFICANT_CREATURES_HOW_DARE_YOU_CHALLENGE_ME);
					});
				}
			}
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case KIMERIAN_GHOST:
				{
					final int killedCount = instance.getParameters().getInt("GHOST_KILLED_COUNT", 0) + 1;
					instance.getParameters().set("GHOST_KILLED_COUNT", killedCount);
					
					if (killedCount >= 5)
					{
						onTimerEvent("KIMERIAN_INVUL_END", null, npc, killer);
					}
					break;
				}
				case KIMERIAN:
				{
					instance.finishInstance(5);
					final Npc kimerian = addSpawn(KIMERIAN_DEAD, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
					final Location loc = Util.getRandomPosition(kimerian, 500, 500);
					kimerian.setInvisible(true);
					playSound(killer, "RM01_S");
					getTimers().addTimer("KIMERIAN_VISIBLE", 4000, t -> kimerian.setInvisible(false));
					getTimers().addTimer("KIMERIAN_CHAT_1", 5000, t -> kimerian.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_WILL_COME_BACK_ALIVE_WITH_ROTTING_AURA));
					getTimers().addTimer("KIMERIAN_RUN", 6000, t ->
					{
						kimerian.setRunning();
						addMoveToDesire(kimerian, loc, 23);
						kimerian.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HA_HA_HA_HA);
					});
					getTimers().addTimer("KIMERIAN_SPAWN_DEFKA", 7000, t ->
					{
						kimerian.deleteMe();
						final Npc noeti = addSpawn(NOETI_KASHERON_LEAVE, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
						getTimers().addTimer("NOETI_SAY2", 3000, n -> noeti.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.UNFORTUNATELY_THEY_RAN_AWAY));
					});
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public void onCreatureKill(OnCreatureDeath event)
	{
		final Npc npc = (Npc) event.getTarget();
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			getTimers().cancelTimersOf(npc);
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case FAIRY_REBEL:
				case NEOMI_KASHERON:
				{
					npc.initSeenCreatures();
					npc.setRunning();
					break;
				}
				case INVISIBLE_NPC:
				case KIMERIAN:
				{
					npc.initSeenCreatures();
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (npc.getId())
		{
			case NOETI_KASHERON_ENTRANCE:
			{
				if (npc.getVariables().getBoolean("CAN_GET_GLIMMER", true))
				{
					giveItems(player, GLIMMER, 10);
					npc.getVariables().set("CAN_GET_GLIMMER", false);
				}
				htmltext = "33098-01.html";
				break;
			}
			case NOETI_KASHERON_LEAVE:
			{
				htmltext = " 33131-01.html";
				break;
			}
		}
		return htmltext;
	}
	
	public void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final StatsSet npcParams = npc.getParameters();
		final StatsSet npcVars = npc.getVariables();
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case FAIRY_REBEL:
				case NEOMI_KASHERON:
				{
					if (creature.isPlayer() && (npcVars.getObject("PC_INSTANCE", PlayerInstance.class) == null))
					{
						npcVars.set("PC_INSTANCE", creature.getActingPlayer());
						getTimers().addRepeatingTimer("HELPER_TIME_ACTION", 2000, npc, null);
					}
					break;
				}
				case INVISIBLE_NPC:
				{
					final int hollow = npcParams.getInt("hollow", -1);
					final int trap = npcParams.getInt("trap", -1);
					
					if (creature.isPlayer() && npc.isScriptValue(0))
					{
						if (hollow == 1)
						{
							spawnHollow(npc, creature.getActingPlayer(), true);
						}
						else
						{
							switch (trap)
							{
								case 1:
								{
									spawnHollow(npc, creature.getActingPlayer(), false);
									addSpawn(FAIRY_WARRIOR, npc.getX() + 80, npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
									addSpawn(FAIRY_WARRIOR, npc.getX(), npc.getY() + 80, npc.getZ(), 0, false, 0, false, instance.getId());
									addSpawn(SATYR_WITCH, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, false, 0, false, instance.getId());
									break;
								}
								case 2:
								{
									spawnHollow(npc, creature.getActingPlayer(), false);
									addSpawn(SATYR_WITCH, npc.getX() + 80, npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
									addSpawn(SATYR_WITCH, npc.getX(), npc.getY() + 80, npc.getZ(), 0, false, 0, false, instance.getId());
									addSpawn(FAIRY_ROGUE, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, false, 0, false, instance.getId());
									break;
								}
								case 3:
								{
									spawnHollow(npc, creature.getActingPlayer(), false);
									addSpawn(FAIRY_WARRIOR, npc.getX() + 80, npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
									addSpawn(SATYR_WITCH, npc.getX(), npc.getY() + 80, npc.getZ(), 0, false, 0, false, instance.getId());
									addSpawn(FAIRY_ROGUE, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, false, 0, false, instance.getId());
									break;
								}
							}
						}
					}
					break;
				}
				case KIMERIAN:
				{
					if (creature.isPlayer() && npcVars.getBoolean("FIGHT_CAN_START", true))
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, KIMERIAN_MSG[getRandom(KIMERIAN_MSG.length)]);
						addAttackPlayerDesire(npc, creature.getActingPlayer(), 23);
						npcVars.set("FIGHT_CAN_START", false);
					}
					break;
				}
			}
		}
	}
	
	private void spawnHollow(Npc npc, PlayerInstance player, boolean isHollow)
	{
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance))
		{
			if (isHollow)
			{
				final Npc kimerian = addSpawn(KIMERIAN_HOLLOW, npc.getX(), npc.getY(), npc.getZ(), Util.calculateHeadingFrom(npc, player), false, 0, false, instance.getId());
				kimerian.setTargetable(false);
				kimerian.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HOW_RIDICULOUS_YOU_THINK_YOU_CAN_FIND_ME);
				getTimers().addTimer("KIMERIAN_HOLLOW_SAY_2", 3000, n -> kimerian.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THEN_TRY_HA_HA_HA));
				getTimers().addTimer("KIMERIAN_HOLLOW_DELETE", 6000, n -> kimerian.deleteMe());
				
			}
			else
			{
				final Npc kimerian = addSpawn(KIMERIAN_HOLLOW_2, npc.getX(), npc.getY(), npc.getZ(), Util.calculateHeadingFrom(npc, player), false, 0, false, instance.getId());
				kimerian.setTargetable(false);
				kimerian.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_RE_STILL_TRYING);
				getTimers().addTimer("KIMERIAN_HOLLOW_SAY_2", 3000, n -> kimerian.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HA_HA_HA_HA));
				getTimers().addTimer("KIMERIAN_HOLLOW_DELETE", 6000, n -> kimerian.deleteMe());
			}
			npc.setScriptValue(1);
		}
	}
	
	public static void main(String[] args)
	{
		new KimerianCommon();
	}
}