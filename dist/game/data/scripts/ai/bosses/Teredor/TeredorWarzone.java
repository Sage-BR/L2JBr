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
package ai.bosses.Teredor;

import java.util.HashMap;
import java.util.Map;

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.instancemanager.WalkingManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;

/**
 * Teredor Warzone instance zone.
 * @author St3eT
 */
public class TeredorWarzone extends AbstractInstance
{
	// NPCs
	private static final int FILAUR = 30535;
	private static final int TEREDOR = 25785;
	private static final int FAKE_TEREDOR = 25801;
	private static final int TEREDOR_POISON = 18998;
	private static final int BEETLE = 19024;
	private static final int POS_CHECKER = 18999;
	private static final int EGG_2 = 18997;
	private static final int ELITE_MILLIPADE = 19015;
	private static final int AWAKENED_MILLIPADE = 18995; // Awakened Millipede
	private static final int HATCHET_MILLIPADE = 18993; // Hatched Millipede
	private static final int HATCHET_UNDERBUG = 18994; // Hatched Underbug
	private static final int TEREDOR_LARVA = 19016; // Teredor's Larva
	private static final int MUTANTED_MILLIPADE = 19000; // Mutated Millipede
	private static final int[] MILLIPADES =
	{
		MUTANTED_MILLIPADE,
		HATCHET_UNDERBUG,
		HATCHET_MILLIPADE
	};
	// Items
	private static final int FAKE_TEREDOR_WEAPON = 15280;
	// Skill
	private static final SkillHolder FAKE_TEREDOR_JUMP_SKILL = new SkillHolder(6268, 1);
	private static final SkillHolder POISON_SKILL = new SkillHolder(14113, 1);
	private static final SkillHolder BEETLE_SKILL = new SkillHolder(14412, 1);
	private static final SkillHolder TEREDOR_POISON_SKILL = new SkillHolder(14112, 1);
	private static final SkillHolder TEREDOR_POISON_SKILL_2 = new SkillHolder(14112, 2);
	private static final SkillHolder TEREDOR_CANCEL = new SkillHolder(5902, 1);
	// Misc
	private static final int TEMPLATE_ID = 160;
	private static final int MIN_PLAYERS = 5;
	//@formatter:off
	private static final Map<Integer, String[]> WALKING_DATA = new HashMap<>();
	static
	{
		WALKING_DATA.put(1, new String[] {"trajan101", "trajan102", "trajan103", "trajan104", "trajan105"});
		WALKING_DATA.put(2, new String[] {"trajan106", "trajan107", "trajan108", "trajan109", "trajan110"});
		WALKING_DATA.put(3, new String[] {"trajan111", "trajan112", "trajan113"});
		WALKING_DATA.put(4, new String[] {"trajan114", "trajan115"});
		WALKING_DATA.put(5, new String[] {"trajan116", "trajan117"});
		WALKING_DATA.put(6, new String[] {"trajan118", "trajan119", "trajan120"});
		WALKING_DATA.put(7, new String[] {"trajan121", "trajan122"});
		WALKING_DATA.put(8, new String[] {"trajan14", "trajan15", "trajan16"});
	}
	//@formatter:on
	
	public TeredorWarzone()
	{
		super(TEMPLATE_ID);
		addStartNpc(FILAUR);
		addTalkId(FILAUR);
		addSpawnId(BEETLE, POS_CHECKER, EGG_2, FAKE_TEREDOR, TEREDOR, TEREDOR_POISON);
		addSpellFinishedId(BEETLE);
		addEventReceivedId(EGG_2);
		addAttackId(TEREDOR);
		addKillId(EGG_2, TEREDOR);
		setCreatureSeeId(this::onCreatureSee, BEETLE, POS_CHECKER, EGG_2, FAKE_TEREDOR);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcVars = npc.getVariables();
			final StatsSet npcParams = npc.getParameters();
			
			switch (event)
			{
				case "EGG_SPAWN_TIMER":
				{
					final Npc minion = addSpawn(AWAKENED_MILLIPADE, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
					WalkingManager.getInstance().startMoving(minion, getRandomEntry(WALKING_DATA.get(npcParams.getInt("Spot", 0))));
					npc.deleteMe();
					break;
				}
				case "FAKE_TEREDOR_POISON_TIMER":
				{
					addSpawn(TEREDOR_POISON, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
					break;
				}
				case "POISON_TIMER_1":
				case "POISON_TIMER_2":
				case "POISON_TIMER_3":
				case "POISON_TIMER_4":
				{
					addSkillCastDesire(npc, npc, POISON_SKILL, 23);
					break;
				}
				case "DELETE_ME":
				{
					npc.deleteMe();
					break;
				}
				case "TEREDOR_LAIR_CHECK":
				{
					final Location spawnLoc = npc.getSpawn();
					
					if (((spawnLoc.getX() - npc.getX()) > 1000) || ((spawnLoc.getX() - npc.getX()) < -2000))
					{
						showOnScreenMsg(instance, NpcStringId.TEREDOR_SUMMONS_SUBORDINATE_BECAUSE_YOU_MOVED_OUT_OF_TEREDOR_S_AREA, ExShowScreenMessage.TOP_CENTER, 4000);
						addSpawn(ELITE_MILLIPADE, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
					}
					break;
				}
				case "TEREDOR_BUHATIMER":
				{
					npcVars.increaseInt("i_ai3", 0, 1);
					break;
				}
				case "TEREDOR_POISON_TIMER":
				{
					final int i_ai4 = npcVars.increaseInt("i_ai4", 0, 1);
					if (i_ai4 == 7)
					{
						npcVars.increaseInt("i_ai4", 0, -7);
					}
					else
					{
						addSpawn(TEREDOR_POISON, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
						getTimers().addTimer("TEREDOR_POISON_TIMER", 2000, npc, null);
					}
					break;
				}
				case "TEREDOR_RUNTIMER_1":
				case "TEREDOR_RUNTIMER_2":
				case "TEREDOR_RUNTIMER_3":
				case "TEREDOR_RUNTIMER_4":
				{
					npc.disableCoreAI(false);
					npc.setTargetable(true);
					npc.broadcastSocialAction(3);
					break;
				}
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "enterInstance":
			{
				enterInstance(player, npc, TEMPLATE_ID);
				break;
			}
			case "checkConditions":
			{
				final Instance playerInstance = getPlayerInstance(player);
				final Party playerParty = player.getParty();
				
				if ((playerInstance != null) && (playerInstance.getTemplateId() == TEMPLATE_ID))
				{
					enterInstance(player, npc, TEMPLATE_ID);
				}
				else if (playerParty == null)
				{
					htmltext = "condNoParty.html";
				}
				else
				{
					if (playerParty.getLeader() == player)
					{
						htmltext = (playerParty.getMemberCount() >= MIN_PLAYERS ? "30535-01.html" : "condMinLimit.html");
					}
					else
					{
						htmltext = "condNoPartyLeader.html";
					}
				}
				break;
			}
			case "30535-02.html":
			{
				htmltext = event;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcParams = npc.getParameters();
			
			switch (npc.getId())
			{
				case TEREDOR:
				{
					WalkingManager.getInstance().startMoving(npc, "trajan5");
					getTimers().addRepeatingTimer("TEREDOR_LAIR_CHECK", 5000, npc, null);
					((Attackable) npc).setCanReturnToSpawnPoint(false);
					npc.initSeenCreatures();
					break;
				}
				case BEETLE:
				{
					if (npcParams.getInt("Sp", 0) == 1)
					{
						WalkingManager.getInstance().startMoving(npc, npcParams.getString("SuperPointName1", ""));
					}
					npc.initSeenCreatures();
					break;
				}
				case FAKE_TEREDOR:
				{
					WalkingManager.getInstance().startMoving(npc, npcParams.getString("SuperPointName", ""));
					npc.setRHandId(FAKE_TEREDOR_WEAPON);
					npc.initSeenCreatures();
					getTimers().addTimer("FAKE_TEREDOR_POISON_TIMER", 3000, npc, null);
					break;
				}
				case TEREDOR_POISON:
				{
					getTimers().addTimer("POISON_TIMER_1", 5000, npc, null);
					getTimers().addTimer("POISON_TIMER_2", 10000, npc, null);
					getTimers().addTimer("POISON_TIMER_3", 15000, npc, null);
					getTimers().addTimer("POISON_TIMER_4", 20000, npc, null);
					getTimers().addTimer("DELETE_ME", 22000, npc, null);
					break;
				}
				default:
				{
					npc.initSeenCreatures();
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	public void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance))
		{
			final StatsSet npcParams = npc.getParameters();
			
			switch (npc.getId())
			{
				case BEETLE:
				{
					if (creature.isPlayer() && npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						addSkillCastDesire(npc, npc, BEETLE_SKILL, 23);
					}
					break;
				}
				case FAKE_TEREDOR:
				{
					if (creature.isPlayer() && npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						addSkillCastDesire(npc, npc, FAKE_TEREDOR_JUMP_SKILL, 23);
						getTimers().addTimer("FAKE_TEREDOR_ELITE_REUSE", 30000, n -> npc.setScriptValue(0));
						final Npc minion = addSpawn(ELITE_MILLIPADE, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
						addAttackPlayerDesire(minion, creature.getActingPlayer());
					}
					break;
				}
				case POS_CHECKER:
				{
					if (creature.isPlayer() && npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						final int spot = npcParams.getInt("Spot", 0);
						
						switch (spot)
						{
							case 1:
							{
								instance.spawnGroup("schuttgart29_2512_sp1m1");
								npc.broadcastEvent("SCE_BREAK_AN_EGG1", 800, null);
								getTimers().addTimer("EGG_SPAWN_TIMER_2", 30000, n -> npc.broadcastEvent("SCE_BREAK_AN_EGG2", 800, creature));
								break;
							}
							case 3:
							{
								instance.spawnGroup("schuttgart29_2512_sp2m1");
								npc.broadcastEvent("SCE_BREAK_AN_EGG1", 800, null);
								getTimers().addTimer("EGG_SPAWN_TIMER_2", 30000, n -> npc.broadcastEvent("SCE_BREAK_AN_EGG2", 800, creature));
								break;
							}
							case 5:
							{
								instance.spawnGroup("schuttgart29_2512_sp4m1");
								break;
							}
							case 6:
							{
								instance.spawnGroup("schuttgart29_2512_306m1");
								npc.broadcastEvent("SCE_BREAK_AN_EGG1", 800, null);
								getTimers().addTimer("EGG_SPAWN_TIMER_2", 30000, n -> npc.broadcastEvent("SCE_BREAK_AN_EGG2", 800, creature));
								break;
							}
							case 7:
							{
								instance.spawnGroup("schuttgart29_2512_305m1");
								npc.broadcastEvent("SCE_BREAK_AN_EGG1", 800, null);
								getTimers().addTimer("EGG_SPAWN_TIMER_2", 30000, n -> npc.broadcastEvent("SCE_BREAK_AN_EGG2", 800, creature));
								break;
							}
						}
					}
					break;
				}
				case EGG_2:
				{
					if (creature.isPlayer() && npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						getTimers().addTimer("EGG_SPAWN_TIMER", (180 + getRandom(120)) * 1000, npc, null);
						npc.getVariables().set("SEE_CREATURE_ID", creature.getObjectId());
					}
					break;
				}
			}
		}
	}
	
	@Override
	public String onEventReceived(String eventName, Npc sender, Npc npc, WorldObject reference)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case EGG_2:
				{
					switch (eventName)
					{
						case "SCE_EGG_DIE":
						{
							npc.setDisplayEffect(2);
							final PlayerInstance player = instance.getPlayerById(npc.getVariables().getInt("SEE_CREATURE_ID", 0));
							if (player != null)
							{
								final Npc minion = addSpawn(getRandomBoolean() ? HATCHET_MILLIPADE : HATCHET_UNDERBUG, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
								addAttackPlayerDesire(minion, player, 23);
								npc.deleteMe();
							}
							break;
						}
						case "SCE_BREAK_AN_EGG1":
						{
							npc.setDisplayEffect(2);
							break;
						}
						case "SCE_BREAK_AN_EGG2":
						{
							final PlayerInstance player = reference == null ? instance.getPlayerById(npc.getVariables().getInt("SEE_CREATURE_ID", 0)) : reference.getActingPlayer();
							int npcId = 0;
							
							switch (npc.getParameters().getInt("Spot", 0))
							{
								case 1:
								{
									npcId = getRandomBoolean() ? TEREDOR_LARVA : MUTANTED_MILLIPADE;
									break;
								}
								case 2:
								case 3:
								{
									npcId = getRandomBoolean() ? MUTANTED_MILLIPADE : HATCHET_UNDERBUG;
									break;
								}
								case 4:
								case 5:
								case 6:
								{
									npcId = getRandomBoolean() ? MUTANTED_MILLIPADE : HATCHET_UNDERBUG;
									break;
								}
								case 7:
								{
									npcId = getRandomEntry(MILLIPADES);
									break;
								}
							}
							
							if (npcId > 0)
							{
								final Npc minion = addSpawn(npcId, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
								if ((player != null) && (minion.calculateDistance3D(player) < 2000))
								{
									addAttackPlayerDesire(minion, player, 23);
								}
								npc.deleteMe();
							}
							break;
						}
						case "SCE_BREAK_AN_EGG3":
						{
							npc.setDisplayEffect(4);
							break;
						}
					}
					break;
				}
			}
		}
		return super.onEventReceived(eventName, sender, npc, reference);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case TEREDOR:
				{
					final StatsSet npcVars = npc.getVariables();
					final int hpPer = npc.getCurrentHpPercent();
					int teredorStatus = npcVars.getInt("TEREDOR_STATUS", 1);
					
					if ((npc.calculateDistance3D(attacker) > 450) && (getRandom(100) < 5))
					{
						addSkillCastDesire(npc, attacker, TEREDOR_POISON_SKILL, 23);
						addSpawn(TEREDOR_POISON, attacker.getX(), attacker.getY(), attacker.getZ(), 0, false, 0, false, instance.getId());
					}
					
					if ((hpPer <= 80) && (hpPer >= 60))
					{
						if (teredorStatus == 1)
						{
							addSkillCastDesire(npc, npc, TEREDOR_CANCEL, 23);
							teredorStatus = npcVars.increaseInt("TEREDOR_STATUS", 1);
							npc.disableCoreAI(true);
							npc.setTargetable(false);
							npc.breakAttack();
							npc.breakCast();
							npc.setWalking();
							((Attackable) npc).clearAggroList();
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
							npc.broadcastSocialAction(4);
							WalkingManager.getInstance().resumeMoving(npc);
							getTimers().addTimer("TEREDOR_BUHATIMER", 10000, npc, null);
							getTimers().addTimer("TEREDOR_RUNTIMER_1", 30000, npc, null);
							getTimers().addTimer("TEREDOR_POISON_TIMER", 2000, npc, null);
						}
						else if (getRandom(100) < 1)
						{
							addSkillCastDesire(npc, attacker, TEREDOR_POISON_SKILL, 23);
							addSpawn(TEREDOR_POISON, attacker.getX(), attacker.getY(), attacker.getZ(), 0, false, 0, false, instance.getId());
						}
					}
					else if ((hpPer <= 60) && (hpPer >= 40))
					{
						if (teredorStatus == 2)
						{
							addSkillCastDesire(npc, npc, TEREDOR_CANCEL, 23);
							teredorStatus = npcVars.increaseInt("TEREDOR_STATUS", 1);
							npc.disableCoreAI(true);
							npc.setTargetable(false);
							npc.breakAttack();
							npc.breakCast();
							npc.setWalking();
							((Attackable) npc).clearAggroList();
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
							npc.broadcastSocialAction(4);
							WalkingManager.getInstance().resumeMoving(npc);
							// myself->BroadcastScriptEvent(@SCE_EGG_SPAWN5, 0, 3000);
							getTimers().addTimer("TEREDOR_BUHATIMER", 10000, npc, null);
							getTimers().addTimer("TEREDOR_RUNTIMER_2", 30000, npc, null);
							getTimers().addTimer("TEREDOR_POISON_TIMER", 2000, npc, null);
						}
						else if (getRandom(100) < 3)
						{
							addSkillCastDesire(npc, attacker, TEREDOR_POISON_SKILL, 23);
							addSpawn(TEREDOR_POISON, attacker.getX(), attacker.getY(), attacker.getZ(), 0, false, 0, false, instance.getId());
						}
					}
					else if ((hpPer <= 40) && (hpPer >= 20))
					{
						if (teredorStatus == 3)
						{
							addSkillCastDesire(npc, npc, TEREDOR_CANCEL, 23);
							// if (myself->i_ai2 == 1)
							// {
							// myself->AddUseSkillDesire(myself->sm, toggle_shield1, @HEAL, @STAND, 500000000);
							// myself->ChangeNPCState(myself->sm, 4);
							// myself->i_ai2 = myself->i_ai2 - 1;
							// }
							// if (myself->i_ai2 == 2)
							// {
							// myself->AddUseSkillDesire(myself->sm, toggle_shield2, @HEAL, @STAND, 500000000);
							npc.setDisplayEffect(4);
							// myself->i_ai2 = myself->i_ai2 - 2;
							// }
							teredorStatus = npcVars.increaseInt("TEREDOR_STATUS", 1);
							npc.disableCoreAI(true);
							npc.setTargetable(false);
							npc.breakAttack();
							npc.breakCast();
							npc.setWalking();
							((Attackable) npc).clearAggroList();
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
							npc.broadcastSocialAction(4);
							WalkingManager.getInstance().resumeMoving(npc);
							// myself->BroadcastScriptEvent(@SCE_TRAJAN_EGG_DIE, gg->GetIndexFromCreature(c0), 3000);
							// myself->BroadcastScriptEvent(@SCE_EGG_SPAWN6, 0, 3000);
							getTimers().addTimer("TEREDOR_BUHATIMER", 10000, npc, null);
							getTimers().addTimer("TEREDOR_RUNTIMER_3", 30000, npc, null);
							getTimers().addTimer("TEREDOR_POISON_TIMER", 2000, npc, null);
						}
						else if (getRandom(100) < 5)
						{
							addSkillCastDesire(npc, attacker, TEREDOR_POISON_SKILL, 23);
							addSpawn(TEREDOR_POISON, attacker.getX(), attacker.getY(), attacker.getZ(), 0, false, 0, false, instance.getId());
						}
					}
					else if (hpPer < 20)
					{
						if (teredorStatus == 4)
						{
							addSkillCastDesire(npc, npc, TEREDOR_CANCEL, 23);
							// if (myself->i_ai2 == 1)
							// {
							// myself->AddUseSkillDesire(myself->sm, toggle_shield1, @HEAL, @STAND, 500000000);
							// myself->ChangeNPCState(myself->sm, 4);
							// myself->i_ai2 = myself->i_ai2 - 1;
							// }
							// if (myself->i_ai2 == 2)
							// {
							// myself->AddUseSkillDesire(myself->sm, toggle_shield2, @HEAL, @STAND, 500000000);
							npc.setDisplayEffect(4);
							// myself->i_ai2 = myself->i_ai2 - 2;
							// }
							teredorStatus = npcVars.increaseInt("TEREDOR_STATUS", 1);
							npc.disableCoreAI(true);
							npc.setTargetable(false);
							npc.breakAttack();
							npc.breakCast();
							npc.setWalking();
							((Attackable) npc).clearAggroList();
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
							npc.broadcastSocialAction(4);
							WalkingManager.getInstance().resumeMoving(npc);
							// myself->BroadcastScriptEvent(@SCE_TRAJAN_EGG_DIE, gg->GetIndexFromCreature(c0), 3000);
							// myself->BroadcastScriptEvent(@SCE_EGG_SPAWN7, 0, 3000);
							// myself->AddUseSkillDesire(myself->sm, Lastskill, @HEAL, @STAND, 500000000);
							npc.setDisplayEffect(1);
							getTimers().addTimer("TEREDOR_BUHATIMER", 10000, npc, null);
							getTimers().addTimer("TEREDOR_RUNTIMER_4", 30000, npc, null);
							getTimers().addTimer("TEREDOR_POISON_TIMER", 2000, npc, null);
						}
						else if (getRandom(100) < 5)
						{
							addSkillCastDesire(npc, attacker, TEREDOR_POISON_SKILL_2, 23);
							addSpawn(TEREDOR_POISON, attacker.getX(), attacker.getY(), attacker.getZ(), 0, false, 0, false, instance.getId());
						}
						else if (getRandom(100) < 5)
						{
							// myself->AddUseSkillDesire(attacker, Specialskill1, @ATTACK, @MOVE_TO_TARGET, 1000000);
						}
					}
					else if ((teredorStatus == 5) && (hpPer < 7))
					{
						// myself->BroadcastScriptEvent(@SCE_ALL_BREAK_AN_EGG5, gg->GetIndexFromCreature(c0), 3000);
						teredorStatus = npcVars.increaseInt("TEREDOR_STATUS", 1);
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case TEREDOR:
				{
					instance.finishInstance();
					break;
				}
				case EGG_2:
				{
					if (getRandom(4) < 3)
					{
						final Npc minion = addSpawn(getRandomBoolean() ? MUTANTED_MILLIPADE : TEREDOR_LARVA, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false, instance.getId());
						addAttackPlayerDesire(minion, killer, 23);
						npc.deleteMe();
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case BEETLE:
				{
					npc.broadcastEvent("SCE_EGG_DIE", 500, null);
					break;
				}
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	public static void main(String[] args)
	{
		new TeredorWarzone();
	}
}