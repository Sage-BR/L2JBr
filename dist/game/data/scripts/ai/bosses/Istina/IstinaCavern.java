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
package ai.bosses.Istina;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.AggroInfo;
import org.l2jbr.gameserver.model.DamageDoneInfo;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.DoorInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;
import org.l2jbr.gameserver.util.Util;

import instances.AbstractInstance;

/**
 * Istina Cavern instance zone.
 * @author St3eT
 */
public class IstinaCavern extends AbstractInstance
{
	// NPCs
	private static final int RUMIESE = 33151;
	private static final int RUMIESE_INSTANCE = 33293;
	private static final int INVISIBLE_NPC = 18919;
	private static final int BALLISTA = 19021;
	private static final int EXTREME_MINION = 23125;
	private static final int[] ISTINA =
	{
		29195, // Istina (Common)
		29196, // Istina (Extreme)
	};
	// Skills
	private static final SkillHolder ERUPTION_1 = new SkillHolder(14222, 1);
	private static final SkillHolder ERUPTION_2 = new SkillHolder(14223, 1);
	private static final SkillHolder ISTINA_ERUPTION_SKILL = new SkillHolder(14221, 1);
	// Items
	private static final int CONTROL_DEVICE = 17608; // Energy Control Device
	private static final int REWARD_BOX_COMMON = 30371; // Box Containing Magic Power
	private static final int REWARD_BOX_EXTREME = 30374; // Magic Filled Box
	// Locations
	private static final Location DEFEAT_ISTINA_LOC = new Location(-177120, 148782, -11384, 49140);
	private static final Location RUMIESE_LOC = new Location(-177001, 147844, -11380, 47684);
	private static final Location BATTLE_LOC = new Location(-177111, 146802, -11384);
	// Misc
	private static final int PERFECT_SCORE_1 = 30000000;
	private static final int PERFECT_SCORE_2 = 300000000;
	private static final int TEMPLATE_ID_COMMON = 169;
	private static final int TEMPLATE_ID_EXTREME = 170;
	
	public IstinaCavern()
	{
		super(TEMPLATE_ID_COMMON, TEMPLATE_ID_EXTREME);
		addStartNpc(RUMIESE);
		addTalkId(RUMIESE, RUMIESE_INSTANCE);
		addFirstTalkId(RUMIESE_INSTANCE);
		addAttackId(ISTINA);
		addAttackId(BALLISTA);
		addSpellFinishedId(ISTINA);
		addSpellFinishedId(INVISIBLE_NPC);
		addSpawnId(INVISIBLE_NPC, BALLISTA);
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
				case "leaveInstance":
				{
					teleportPlayerOut(player, instance);
					break;
				}
				case "battleTeleport":
				{
					player.teleToLocation(BATTLE_LOC);
					break;
				}
				case "giveDevice":
				{
					if (!hasQuestItems(player, CONTROL_DEVICE))
					{
						giveItems(player, CONTROL_DEVICE, 1);
						htmltext = "33293-06.html";
					}
					else
					{
						htmltext = "33293-07.html";
					}
				}
				case "giveReward":
				{
					if (instance.isStatus(3))
					{
						if (npc.getVariables().getBoolean("PLAYER_" + player.getObjectId(), true))
						{
							giveItems(player, (isExtremeMode(instance) ? REWARD_BOX_EXTREME : REWARD_BOX_COMMON), 1);
							npc.getVariables().set("PLAYER_" + player.getObjectId(), false);
						}
						else
						{
							htmltext = "33293-08.html";
						}
					}
					break;
				}
			}
		}
		else if (event.equals("enterInstanceCommon"))
		{
			enterInstance(player, npc, TEMPLATE_ID_COMMON);
		}
		else if (event.equals("enterInstanceExtreme"))
		{
			enterInstance(player, npc, TEMPLATE_ID_EXTREME);
		}
		return htmltext;
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcParams = npc.getParameters();
			final StatsSet npcVars = npc.getVariables();
			switch (event)
			{
				case "DEATH_TIMER":
				{
					final Creature mostHated = ((Attackable) npc).getMostHated();
					if ((mostHated != null) && npc.isInsideRadius2D(mostHated, 15000))
					{
						final SkillHolder death1 = npcParams.getSkillHolder("Istina_Death_Skill01");
						final SkillHolder death2 = npcParams.getSkillHolder("Istina_Death_Skill02");
						
						if (mostHated.isInCategory(CategoryType.TANKER_GROUP))
						{
							addSkillCastDesire(npc, mostHated, (mostHated.isAffectedBySkill(death1) ? death2 : death1), 23);
						}
						else
						{
							addSkillCastDesire(npc, mostHated, death2, 23);
						}
					}
					getTimers().addTimer("DEATH_TIMER", 35000, npc, null);
					break;
				}
				case "DEATH_CHECK_TIMER":
				{
					final SkillHolder death1 = npcParams.getSkillHolder("Istina_Death_Skill01");
					final SkillHolder death2 = npcParams.getSkillHolder("Istina_Death_Skill02");
					final Creature mostHated = ((Attackable) npc).getMostHated();
					
					if ((mostHated != null) && npc.isInsideRadius2D(mostHated, 15000) && mostHated.isInCategory(CategoryType.TANKER_GROUP) && mostHated.isAffectedBySkill(death1))
					{
						addSkillCastDesire(npc, mostHated, death2, 23);
					}
					break;
				}
				case "REFLECT_TIMER":
				{
					if (!isExtremeMode(instance))
					{
						showOnScreenMsg(instance, NpcStringId.ISTINA_SPREADS_THE_REFLECTING_PROTECTIVE_SHEET, ExShowScreenMessage.TOP_CENTER, 4000);
					}
					final SkillHolder reflect = npcParams.getSkillHolder("Istina_Refraction_Skill");
					npc.setTarget(npc);
					npc.doCast(reflect.getSkill());
					instance.broadcastPacket(new PlaySound(1, "Npcdialog1.istina_voice_02", 0, 0, 0, 0, 0));
					getTimers().addTimer("REFLECT_TIMER", 90000 + getRandom(15000), npc, null);
					break;
				}
				case "REFLECT_CHECK_TIMER":
				{
					if (npc.isAffectedBySkill(npcParams.getSkillHolder("Istina_Refraction_Skill")))
					{
						getTimers().addTimer("REFLECT_CHECK_TIMER", 1000, npc, null);
					}
					else
					{
						npc.setDisplayEffect(2);
					}
					break;
				}
				case "LOW_ERUPTION_TIMER":
				{
					final Attackable istina = (Attackable) npc;
					if ((istina.getHateList() != null) && (istina.getHateList().size() > 0))
					{
						final Creature target = istina.getHateList().stream().sorted((o1, o2) -> (int) o1.calculateDistance3D(o2)).findFirst().orElse(null);
						if (target != null)
						{
							final Npc eruption = addSpawn(INVISIBLE_NPC, Util.getRandomPosition(target, 50, 50), false, 0, false, instance.getId());
							eruption.getVariables().set("ERUPTION_TARGET", target);
						}
					}
					getTimers().addTimer("LOW_ERUPTION_TIMER", 15000, npc, null);
					break;
				}
				case "ERUPTION_TIMER":
				{
					addSkillCastDesire(npc, npc, ISTINA_ERUPTION_SKILL, 23);
					showOnScreenMsg(instance, NpcStringId.POWERFUL_ACIDIC_ENERGY_IS_ERUPTING_FROM_ISTINA_S_BODY, ExShowScreenMessage.TOP_CENTER, 6000);
					getTimers().addTimer("ERUPTION_TIMER", 50000 + getRandom(15000), npc, null);
					break;
				}
				case "BALLISTA_START_TIMER":
				{
					final int helpCountDown = npcVars.getInt("HELP_COUNT_DOWN", 5);
					if (helpCountDown == 0)
					{
						getTimers().addTimer("BALLISTA_CHECK_TIMER", 1000, npc, null);
						npc.setTargetable(true);
						npc.setIsInvul(false);
						npcVars.set("COUNTING_ENABLED", true);
						showOnScreenMsg(instance, NpcStringId.START_CHARGING_MANA_BALLISTA, ExShowScreenMessage.MIDDLE_CENTER, 4000);
					}
					else
					{
						showOnScreenMsg(instance, NpcStringId.AFTER_S1_SECONDS_THE_CHARGING_MAGIC_BALLISTAS_STARTS, ExShowScreenMessage.MIDDLE_CENTER, 4000, Integer.toString(helpCountDown));
						npcVars.set("HELP_COUNT_DOWN", helpCountDown - 1);
						getTimers().addTimer("BALLISTA_START_TIMER", 1000, npc, null);
					}
					break;
				}
				case "BALLISTA_CHECK_TIMER":
				{
					if (npcVars.getBoolean("COUNTING_ENABLED", false))
					{
						final int countDown = npcVars.getInt("COUNT_DOWN", 30);
						final int charged = getChargedPercent(npcVars.getInt("SCORE_VAL", 0), isExtremeMode(instance));
						
						if (countDown == 0)
						{
							npcVars.set("COUNTING_ENABLED", false);
							npc.setTargetable(false);
							npc.setIsInvul(true);
							getTimers().addTimer("BALLISTA_END_TIMER", 1000, npc, null);
							instance.getPlayers().forEach(temp -> temp.sendPacket(new ExSendUIEvent(temp, 2, countDown, charged, 0, 2042, 0, NpcStringId.REPLENISH_BALLISTA_MAGIC_POWER.getId())));
						}
						
						npcVars.set("HELP_COUNT_DOWN", npcVars.getInt("HELP_COUNT_DOWN", 0) + 1);
						if (npcVars.getInt("HELP_COUNT_DOWN", 0) == 2)
						{
							npcVars.set("COUNT_DOWN", countDown - 1);
							npcVars.set("HELP_COUNT_DOWN", 0);
							instance.getPlayers().forEach(temp -> temp.sendPacket(new ExSendUIEvent(temp, 2, countDown, charged, 0, 2042, 0, NpcStringId.REPLENISH_BALLISTA_MAGIC_POWER.getId())));
						}
						getTimers().addTimer("BALLISTA_CHECK_TIMER", 500, npc, null);
					}
					
					break;
				}
				case "BALLISTA_END_TIMER":
				{
					if (getChargedPercent(npcVars.getInt("SCORE_VAL", 0), isExtremeMode(instance)) >= getRandom(100))
					{
						playMovie(instance, Movie.SC_ISTINA_ENDING_A);
						instance.setStatus(3);
						getTimers().addTimer("INSTANCE_FINISH_TIMER", 23000, npc, null);
					}
					else
					{
						playMovie(instance, Movie.SC_ISTINA_ENDING_B);
						instance.setStatus(4);
						getTimers().addTimer("INSTANCE_FINISH_TIMER", 22000, npc, null);
					}
					instance.getAliveNpcs(ISTINA).forEach(istina ->
					{
						istina.teleToLocation(instance.getEnterLocation());
						istina.setInvisible(true);
					});
					break;
				}
				case "INSTANCE_FINISH_TIMER":
				{
					getTimers().addTimer("NPC_DELETE", 3000, evnt -> npc.deleteMe());
					instance.finishInstance();
					instance.getAliveNpcs(Attackable.class, ISTINA).forEach(istina ->
					{
						istina.teleToLocation(DEFEAT_ISTINA_LOC);
						istina.setInvisible(false);
						istina.setUndying(false);
						istina.reduceCurrentHp(istina.getVariables().getInt("REWARD_DAMAGE", 1000000), istina.getVariables().getObject("REWARD_PLAYER", PlayerInstance.class), null);
					});
					break;
				}
				case "AUTHORITY_TIMER":
				{
					final int random = getRandom(1, 3);
					final SkillHolder authoritySkill = npcParams.getSkillHolder("Istina_Authority_Skill0" + random);
					if (authoritySkill != null)
					{
						addSkillCastDesire(npc, npc, authoritySkill, 23);
						instance.broadcastPacket(new PlaySound(1, "Npcdialog1.istina_voice_01", 0, 0, 0, 0, 0));
						
						switch (random)
						{
							case 1:
							{
								showOnScreenMsg(instance, NpcStringId.ISTINA_S_SOUL_STONE_STARTS_POWERFULLY_ILLUMINATING_IN_RED, ExShowScreenMessage.TOP_CENTER, 6000);
								break;
							}
							case 2:
							{
								showOnScreenMsg(instance, NpcStringId.ISTINA_S_SOUL_STONE_STARTS_POWERFULLY_ILLUMINATING_IN_BLUE, ExShowScreenMessage.TOP_CENTER, 6000);
								break;
							}
							case 3:
							{
								showOnScreenMsg(instance, NpcStringId.ISTINA_S_SOUL_STONE_STARTS_POWERFULLY_ILLUMINATING_IN_GREEN, ExShowScreenMessage.TOP_CENTER, 6000);
								break;
							}
						}
					}
					getTimers().addTimer("AUTHORITY_TIMER", 70000 + getRandom(25000), npc, null);
					break;
				}
				case "AUTHORITY_END_TIMER":
				{
					// gg->Area_SetOnOffEx(Istina_Authority_AreaData1, @OFF, myself->InstantZone_GetId());
					// gg->Area_SetOnOffEx(Istina_Authority_AreaData2, @OFF, myself->InstantZone_GetId());
					// gg->Area_SetOnOffEx(Istina_Authority_AreaData3, @OFF, myself->InstantZone_GetId());
					break;
				}
			}
		}
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		final Instance instance = npc.getInstanceWorld();
		if ((skill != null) && isInInstance(instance))
		{
			final int skillId = skill.getId();
			
			if (npc.getId() == INVISIBLE_NPC)
			{
				if (skillId == ERUPTION_1.getSkillId())
				{
					npc.setTarget(npc);
					npc.doCast(ERUPTION_2.getSkill());
				}
				else if (skillId == ERUPTION_2.getSkillId())
				{
					getTimers().addTimer("NPC_DELETE", 2000, event -> npc.deleteMe());
					
					if (isExtremeMode(instance) && (getRandom(100) < 30))
					{
						addAttackPlayerDesire(addSpawn(EXTREME_MINION, npc, false, 0, false, instance.getId()), npc.getVariables().getObject("ERUPTION_TARGET", PlayerInstance.class), 23);
					}
				}
			}
			else
			{
				final StatsSet npcParams = npc.getParameters();
				
				if (skillId == npcParams.getSkillHolder("Istina_Death_Skill01").getSkillId())
				{
					showOnScreenMsg(player, NpcStringId.ISTINA_S_MARK_SHINES_ABOVE_THE_HEAD, ExShowScreenMessage.TOP_CENTER, 4000);
					getTimers().addTimer("DEATH_CHECK_TIMER", 10000, npc, null);
				}
				else if (skillId == npcParams.getSkillHolder("Istina_Refraction_Skill").getSkillId())
				{
					npc.setDisplayEffect(1);
					getTimers().addTimer("REFLECT_CHECK_TIMER", 1000, npc, null);
				}
				else if (skillId == ISTINA_ERUPTION_SKILL.getSkillId())
				{
					((Attackable) npc).getAggroList().values().stream().sorted(Comparator.comparingInt(AggroInfo::getHate)).map(AggroInfo::getAttacker).limit(5).forEach(character ->
					{
						final Npc eruption = addSpawn(INVISIBLE_NPC, Util.getRandomPosition(character, 150, 150), false, 0, false, instance.getId());
						eruption.getVariables().set("ERUPTION_TARGET", character);
					});
				}
				else if (skillId == npcParams.getSkillHolder("Istina_Authority_Skill01").getSkillId())
				{
					// gg->Area_SetOnOffEx(Istina_Authority_AreaData2, @ON, myself->InstantZone_GetId()); // TODO: Need be finished after zone rework
					// gg->Area_SetOnOffEx(Istina_Authority_AreaData3, @ON, myself->InstantZone_GetId()); // TODO: Need be finished after zone rework
					getTimers().addTimer("AUTHORITY_END_TIMER", 15000, npc, null);
				}
				else if (skillId == npcParams.getSkillHolder("Istina_Authority_Skill02").getSkillId())
				{
					// gg->Area_SetOnOffEx(Istina_Authority_AreaData1, @ON, myself->InstantZone_GetId()); // TODO: Need be finished after zone rework
					// gg->Area_SetOnOffEx(Istina_Authority_AreaData3, @ON, myself->InstantZone_GetId()); // TODO: Need be finished after zone rework
					getTimers().addTimer("AUTHORITY_END_TIMER", 15000, npc, null);
				}
				else if (skillId == npcParams.getSkillHolder("Istina_Authority_Skill03").getSkillId())
				{
					// gg->Area_SetOnOffEx(Istina_Authority_AreaData1, @ON, myself->InstantZone_GetId()); // TODO: Need be finished after zone rework
					// gg->Area_SetOnOffEx(Istina_Authority_AreaData2, @ON, myself->InstantZone_GetId()); // TODO: Need be finished after zone rework
					getTimers().addTimer("AUTHORITY_END_TIMER", 15000, npc, null);
				}
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcVars = npc.getVariables();
			final int stage = npcVars.getInt("ISTINA_STAGE", -1);
			
			if (npc.getId() == BALLISTA)
			{
				if (npcVars.getBoolean("COUNTING_ENABLED", false))
				{
					final int score = npcVars.getInt("SCORE_VAL", 0);
					
					if (getChargedPercent(score, isExtremeMode(instance)) < 100)
					{
						int addScore = damage;
						
						if (skill != null)
						{
							if (skill.getEffectPoint() < 0)
							{
								addScore *= 1.5;
							}
							addScore *= 1.5;
						}
						
						if (attacker.isInCategory(CategoryType.FIGHTER_GROUP))
						{
							addScore *= 2;
						}
						npcVars.set("SCORE_VAL", score + addScore);
					}
				}
			}
			else
			{
				switch (stage)
				{
					case -1:
					{
						instance.setStatus(1);
						npcVars.set("ISTINA_STAGE", 1);
						instance.getDoors().forEach(DoorInstance::closeMe);
						npc.setUndying(true);
						((Attackable) npc).setCanReturnToSpawnPoint(false);
						getTimers().addTimer("DEATH_TIMER", 3500, npc, null);
						getTimers().addTimer("REFLECT_TIMER", 3500, npc, null);
						getTimers().addTimer("LOW_ERUPTION_TIMER", 15000, npc, null);
						break;
					}
					case 1:
					{
						if (npc.getCurrentHpPercent() < 85)
						{
							npcVars.set("ISTINA_STAGE", 2);
							getTimers().addTimer("AUTHORITY_TIMER", 3000, npc, null);
						}
						break;
					}
					case 2:
					{
						if (npc.getCurrentHpPercent() < 70)
						{
							npcVars.set("ISTINA_STAGE", 3);
							if (isExtremeMode(instance))
							{
								// myself->AddTimerEx(Seal_Timer, (3 * 1000));
							}
							// myself->AddTimerEx(Overcrowding_Timer, (10 * 1000));
						}
						break;
					}
					case 3:
					{
						if (npc.getCurrentHpPercent() < 55)
						{
							npcVars.set("ISTINA_STAGE", 4);
							getTimers().addTimer("ERUPTION_TIMER", 50000, npc, null);
							// myself->AddTimerEx(Creation_Timer, (Creation_Timer_Interval * 1000));
						}
						break;
					}
					case 4:
					{
						if (npc.getCurrentHpPercent() < 40)
						{
							npcVars.set("ISTINA_STAGE", 5);
							if (isExtremeMode(instance))
							{
								// myself->AddTimerEx(Order_Timer, (Order_Timer_Interval * 100));
							}
						}
						break;
					}
					case 5:
					{
						if (npc.getCurrentHpPercent() < 0.01)
						{
							setPlayerRewardInfo(npc);
							npcVars.set("ISTINA_STAGE", 6);
							getTimers().cancelTimer("DEATH_TIMER", npc, null);
							getTimers().cancelTimer("DEATH_CHECK_TIMER", npc, null);
							getTimers().cancelTimer("REFLECT_TIMER", npc, null);
							getTimers().cancelTimer("REFLECT_CHECK_TIMER", npc, null);
							getTimers().cancelTimer("AUTHORITY_TIMER", npc, null);
							getTimers().cancelTimer("ERUPTION_TIMER", npc, null);
							getTimers().cancelTimer("LOW_ERUPTION_TIMER", npc, null);
							// myself->BlockTimer(Overcrowding_Timer);
							if (isExtremeMode(instance))
							{
								// myself->BlockTimer(Seal_Timer);
								// myself->BlockTimer(Order_Timer);
							}
							instance.getAliveNpcs(EXTREME_MINION, INVISIBLE_NPC).forEach(Npc::deleteMe);
							instance.spawnGroup("BALLISTA");
							instance.setStatus(2);
							instance.getAliveNpcs(RUMIESE_INSTANCE).forEach(rumiese -> rumiese.teleToLocation(RUMIESE_LOC));
							instance.getDoors().forEach(DoorInstance::openMe);
							// myself->AddTimerEx(Broadcast_Timer, (2 * 1000));
							onTimerEvent("AUTHORITY_END_TIMER", null, npc, null);
							// myself->AddTimerEx(Location_Check_Timer, (4 * 1000));
							npc.disableCoreAI(true);
							npc.teleToLocation(DEFEAT_ISTINA_LOC);
							npc.setDisplayEffect(3);
							npc.setTargetable(false);
							playMovie(instance, Movie.SC_ISTINA_BRIDGE);
							instance.finishInstance(15);
						}
						break;
					}
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			if (npc.getId() == INVISIBLE_NPC)
			{
				npc.setTarget(npc);
				npc.doCast(ERUPTION_1.getSkill());
			}
			else if (npc.getId() == BALLISTA)
			{
				npc.disableCoreAI(true);
				npc.setUndying(true);
				npc.setIsInvul(true);
				npc.setTargetable(false);
				getTimers().cancelTimer("BALLISTA_START_TIMER", npc, null);
				getTimers().addTimer("BALLISTA_START_TIMER", 10000, npc, null);
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (instance.getStatus())
			{
				case 0:
				{
					htmltext = "33293-01.html";
					break;
				}
				case 1:
				{
					htmltext = "33293-02.html";
					break;
				}
				case 2:
				{
					htmltext = "33293-03.html";
					break;
				}
				case 3:
				{
					htmltext = "33293-04.html";
					break;
				}
				case 4:
				{
					htmltext = "33293-05.html";
					break;
				}
			}
		}
		return htmltext;
	}
	
	private boolean isExtremeMode(Instance instance)
	{
		return instance.getTemplateId() == TEMPLATE_ID_EXTREME;
	}
	
	private int getChargedPercent(int score, boolean isExtreme)
	{
		final int charged;
		if (score >= (isExtreme ? PERFECT_SCORE_2 : PERFECT_SCORE_1))
		{
			charged = 100;
		}
		else
		{
			charged = (score * 100) / (isExtreme ? PERFECT_SCORE_2 : PERFECT_SCORE_1);
		}
		return charged;
	}
	
	private PlayerInstance setPlayerRewardInfo(Npc npc)
	{
		final Map<PlayerInstance, DamageDoneInfo> rewards = new ConcurrentHashMap<>();
		final StatsSet npcVars = npc.getVariables();
		PlayerInstance maxDealer = null;
		long maxDamage = 0;
		int totalDamage = 0;
		
		for (AggroInfo info : ((Attackable) npc).getAggroList().values())
		{
			if (info == null)
			{
				continue;
			}
			
			final PlayerInstance attacker = info.getAttacker().getActingPlayer();
			if (attacker != null)
			{
				final long damage = info.getDamage();
				if (damage > 1)
				{
					if (!Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, attacker, true))
					{
						continue;
					}
					
					final DamageDoneInfo reward = rewards.computeIfAbsent(attacker, DamageDoneInfo::new);
					reward.addDamage(damage);
					
					if (reward.getDamage() > maxDamage)
					{
						maxDealer = attacker;
						maxDamage = reward.getDamage();
					}
					totalDamage += damage;
				}
			}
		}
		
		npcVars.set("REWARD_PLAYER", maxDealer);
		npcVars.set("REWARD_DAMAGE", totalDamage);
		return maxDealer;
	}
	
	public static void main(String[] args)
	{
		new IstinaCavern();
	}
}