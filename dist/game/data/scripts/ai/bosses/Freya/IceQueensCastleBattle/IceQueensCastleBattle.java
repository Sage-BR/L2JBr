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
package ai.bosses.Freya.IceQueensCastleBattle;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.MountType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.FriendlyNpcInstance;
import org.l2jbr.gameserver.model.actor.instance.GrandBossInstance;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.instance.RaidBossInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.model.variables.NpcVariables;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;
import org.l2jbr.gameserver.network.serverpackets.ExChangeClientEffectInfo;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;
import org.l2jbr.gameserver.taskmanager.DecayTaskManager;

import instances.AbstractInstance;
import quests.Q10286_ReunionWithSirra.Q10286_ReunionWithSirra;

/**
 * Ice Queen's Castle (Normal Battle) instance zone.
 * @author St3eT
 */
public class IceQueensCastleBattle extends AbstractInstance
{
	// Npcs
	private static final int FREYA_THRONE = 29177; // First freya
	private static final int FREYA_SPELLING = 29178; // Second freya
	private static final int FREYA_STAND_EASY = 29179; // Last freya - Easy mode
	private static final int FREYA_STAND_HARD = 29180; // Last freya - Hardcore mode
	private static final int INVISIBLE_NPC = 18919;
	private static final int KNIGHT_EASY = 18855; // Archery Knight - Easy mode
	private static final int KNIGHT_HARD = 18856; // Archery Knight - Hardcore mode
	private static final int GLACIER = 18853; // Glacier
	private static final int BREATH = 18854; // Archer's Breath
	private static final int GLAKIAS_EASY = 25699; // Glakias (Archery Knight Captain) - Easy mode
	private static final int GLAKIAS_HARD = 25700; // Glakias (Archery Knight Captain) - Hardcore mode
	private static final int SIRRA = 32762; // Sirra
	private static final int JINIA = 32781; // Jinia
	private static final int SUPP_JINIA = 18850; // Jinia
	private static final int SUPP_KEGOR = 18851; // Kegor
	// Skills
	private static final SkillHolder BLIZZARD_EASY = new SkillHolder(6274, 1); // Eternal Blizzard
	private static final SkillHolder BLIZZARD_HARD = new SkillHolder(6275, 1); // Eternal Blizzard
	private static final SkillHolder BLIZZARD_FORCE = new SkillHolder(6697, 1); // Eternal Blizzard
	private static final SkillHolder BLIZZARD_BREATH = new SkillHolder(6299, 1); // Breath of Ice Palace - Ice Storm
	private static final SkillHolder SUICIDE_BREATH = new SkillHolder(6300, 1); // Self-Destruction
	private static final SkillHolder JINIA_SUPPORT = new SkillHolder(6288, 1); // Jinia's Prayer
	private static final SkillHolder KEGOR_SUPPORT = new SkillHolder(6289, 1); // Kegor's Courage
	private static final SkillHolder ICE_STONE = new SkillHolder(6301, 1); // Cold Mana's Fragment
	private static final SkillHolder CANCEL = new SkillHolder(4618, 1); // NPC Cancel PC Target
	private static final SkillHolder POWER_STRIKE = new SkillHolder(6293, 1); // Power Strike
	private static final SkillHolder POINT_TARGET = new SkillHolder(6295, 1); // Point Target
	private static final SkillHolder CYLINDER_THROW = new SkillHolder(6297, 1); // Cylinder Throw
	private static final SkillHolder SELF_RANGE_BUFF = new SkillHolder(6294, 1); // Leader's Roar
	private static final SkillHolder LEADER_RUSH = new SkillHolder(6296, 1); // Rush
	private static final SkillHolder ANTI_STRIDER = new SkillHolder(4258, 1); // Hinder Strider
	private static final SkillHolder ICE_BALL = new SkillHolder(6278, 1); // Ice Ball
	private static final SkillHolder SUMMON_ELEMENTAL = new SkillHolder(6277, 1); // Summon Spirits
	private static final SkillHolder SELF_NOVA = new SkillHolder(6279, 1); // Attack Nearby Range
	private static final SkillHolder REFLECT_MAGIC = new SkillHolder(6282, 1); // Reflect Magic
	private static final SkillHolder FREYA_ANGER = new SkillHolder(6285, 1); // Rage of Ice
	private static final SkillHolder FREYA_BUFF = new SkillHolder(6284, 1); // Freya's Bless
	// Locations
	private static final Location FREYA_SPAWN = new Location(114720, -117085, -11088, 15956);
	private static final Location FREYA_SPELLING_SPAWN = new Location(114723, -117502, -10672, 15956);
	private static final Location FREYA_CORPSE = new Location(114767, -114795, -11200, 0);
	private static final Location MIDDLE_POINT = new Location(114730, -114805, -11200);
	private static final Location KEGOR_FINISH = new Location(114659, -114796, -11205);
	private static final Location GLAKIAS_SPAWN = new Location(114707, -114799, -11199, 15956);
	private static final Location SUPP_JINIA_SPAWN = new Location(114751, -114781, -11205);
	private static final Location SUPP_KEGOR_SPAWN = new Location(114659, -114796, -11205);
	private static final Location BATTLE_PORT = new Location(114694, -113700, -11200);
	private static final Location CONTROLLER_LOC = new Location(114394, -112383, -11200);
	private static final Location[] STATUES_LOC =
	{
		new Location(113845, -116091, -11168, 8264),
		new Location(113381, -115622, -11168, 8264),
		new Location(113380, -113978, -11168, -8224),
		new Location(113845, -113518, -11168, -8224),
		new Location(115591, -113516, -11168, -24504),
		new Location(116053, -113981, -11168, -24504),
		new Location(116061, -115611, -11168, 24804),
		new Location(115597, -116080, -11168, 24804),
		new Location(112942, -115480, -10960, 52),
		new Location(112940, -115146, -10960, 52),
		new Location(112945, -114453, -10960, 52),
		new Location(112945, -114123, -10960, 52),
		new Location(116497, -114117, -10960, 32724),
		new Location(116499, -114454, -10960, 32724),
		new Location(116501, -115145, -10960, 32724),
		new Location(116502, -115473, -10960, 32724),
	};
	private static final Location[] KNIGHTS_LOC =
	{
		new Location(114502, -115315, -11205, 15451),
		new Location(114937, -115323, -11205, 18106),
		new Location(114722, -115185, -11205, 16437),
	};
	// Misc
	private static final int TEMPLATE_ID_EASY = 139; // Ice Queen's Castle
	private static final int TEMPLATE_ID_HARD = 144; // Ice Queen's Castle (Epic)
	private static final int DOOR_ID = 23140101;
	private static final int[] EMMITERS =
	{
		23140202,
		23140204,
		23140206,
		23140208,
		23140212,
		23140214,
		23140216,
	};
	
	public IceQueensCastleBattle()
	{
		super(TEMPLATE_ID_EASY, TEMPLATE_ID_HARD);
		addStartNpc(SIRRA, SUPP_KEGOR, SUPP_JINIA);
		addFirstTalkId(SUPP_KEGOR, SUPP_JINIA);
		addTalkId(SIRRA, JINIA, SUPP_KEGOR);
		addAttackId(FREYA_THRONE, FREYA_STAND_EASY, FREYA_STAND_HARD, GLAKIAS_EASY, GLAKIAS_HARD, GLACIER, BREATH, KNIGHT_EASY, KNIGHT_HARD);
		addKillId(GLAKIAS_EASY, GLAKIAS_HARD, FREYA_STAND_EASY, FREYA_STAND_HARD, KNIGHT_EASY, KNIGHT_HARD, GLACIER, BREATH);
		addSpellFinishedId(GLACIER, BREATH);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterEasy"))
		{
			enterInstance(player, npc, TEMPLATE_ID_EASY);
		}
		else if (event.equals("enterHardcore"))
		{
			enterInstance(player, npc, TEMPLATE_ID_HARD);
		}
		else
		{
			final Instance world = npc.getInstanceWorld();
			if (world != null)
			{
				final StatsSet params = world.getParameters();
				final Npc controller = params.getObject("controller", Npc.class);
				final Npc freya = params.getObject("freya", Npc.class);
				switch (event)
				{
					case "openDoor":
					{
						if (npc.isScriptValue(0))
						{
							npc.setScriptValue(1);
							world.openCloseDoor(DOOR_ID, true);
							final Npc control = addSpawn(INVISIBLE_NPC, CONTROLLER_LOC, false, 0, true, world.getId());
							for (Location loc : STATUES_LOC)
							{
								if (loc.getZ() == -11168)
								{
									addSpawn(INVISIBLE_NPC, loc, false, 0, false, world.getId());
								}
							}
							
							if (!isHard(world))
							{
								for (PlayerInstance players : world.getPlayers())
								{
									if (!players.isDead())
									{
										final QuestState qs = player.getQuestState(Q10286_ReunionWithSirra.class.getSimpleName());
										if ((qs != null) && (qs.getState() == State.STARTED) && qs.isCond(5))
										{
											qs.setCond(6, true);
										}
									}
								}
							}
							startQuestTimer("STAGE_1_MOVIE", 60000, control, null);
							world.setParameter("controller", control);
						}
						break;
					}
					case "portInside":
					{
						player.teleToLocation(BATTLE_PORT);
						break;
					}
					case "killFreya":
					{
						final QuestState qs = player.getQuestState(Q10286_ReunionWithSirra.class.getSimpleName());
						if ((qs != null) && (qs.getState() == State.STARTED) && qs.isCond(6))
						{
							qs.setMemoState(10);
							qs.setCond(7, true);
						}
						world.getNpc(SUPP_KEGOR).deleteMe();
						freya.decayMe();
						manageMovie(world, Movie.SC_BOSS_FREYA_ENDING_B);
						cancelQuestTimer("FINISH_WORLD", controller, null);
						startQuestTimer("FINISH_WORLD", 58500, controller, null);
						break;
					}
					case "18851-01.html":
					{
						return event;
					}
					case "STAGE_1_MOVIE":
					{
						world.setStatus(1);
						world.openCloseDoor(DOOR_ID, false);
						manageMovie(world, Movie.SC_BOSS_FREYA_OPENING);
						startQuestTimer("STAGE_1_START", 53500, controller, null);
						break;
					}
					case "STAGE_1_START":
					{
						final GrandBossInstance frey = (GrandBossInstance) addSpawn(FREYA_THRONE, FREYA_SPAWN, false, 0, true, world.getId());
						frey.setUndying(true);
						manageScreenMsg(world, NpcStringId.BEGIN_STAGE_1);
						startQuestTimer("CAST_BLIZZARD", 50000, controller, null);
						startQuestTimer("STAGE_1_SPAWN", 2000, frey, null);
						world.setParameter("freya", frey);
						break;
					}
					case "STAGE_1_SPAWN":
					{
						notifyEvent("START_SPAWN", controller, null);
						break;
					}
					case "STAGE_1_FINISH":
					{
						if (freya != null)
						{
							world.setParameter("freya", null);
							freya.deleteMe();
							manageDespawnMinions(world);
							manageMovie(world, Movie.SC_BOSS_FREYA_PHASECH_A);
							startQuestTimer("STAGE_1_PAUSE", 24100 - 1000, controller, null);
						}
						break;
					}
					case "STAGE_1_PAUSE":
					{
						final GrandBossInstance frey = (GrandBossInstance) addSpawn(FREYA_SPELLING, FREYA_SPELLING_SPAWN, false, 0, true, world.getId());
						frey.setIsInvul(true);
						frey.setRandomWalking(false);
						frey.disableCoreAI(true);
						manageTimer(world, 60, NpcStringId.TIME_REMAINING_UNTIL_NEXT_BATTLE);
						world.setStatus(2);
						world.setParameter("freya", frey);
						startQuestTimer("STAGE_2_START", 60000, controller, null);
						break;
					}
					case "STAGE_2_START":
					{
						world.setParameter("canSpawnMobs", true);
						notifyEvent("START_SPAWN", controller, null);
						manageScreenMsg(world, NpcStringId.BEGIN_STAGE_2);
						
						if (isHard(world))
						{
							startQuestTimer("STAGE_2_FAILED", 360000, controller, null);
							manageTimer(world, 360, NpcStringId.BATTLE_END_LIMIT_TIME);
							controller.getVariables().set("TIMER_END", System.currentTimeMillis() + 360000);
						}
						break;
					}
					case "STAGE_2_MOVIE":
					{
						manageMovie(world, Movie.SC_ICE_HEAVYKNIGHT_SPAWN);
						startQuestTimer("STAGE_2_GLAKIAS", 7000, controller, null);
						break;
					}
					case "STAGE_2_GLAKIAS":
					{
						final boolean isHardMode = isHard(world);
						for (Location loc : STATUES_LOC)
						{
							if (loc.getZ() == -10960)
							{
								final Npc statue = addSpawn(INVISIBLE_NPC, loc, false, 0, false, world.getId());
								startQuestTimer("SPAWN_KNIGHT", 5000, statue, null);
							}
						}
						
						final RaidBossInstance glakias = (RaidBossInstance) addSpawn((isHardMode ? GLAKIAS_HARD : GLAKIAS_EASY), GLAKIAS_SPAWN, false, 0, true, world.getId());
						startQuestTimer("LEADER_DELAY", 5000, glakias, null);
						
						if (isHardMode)
						{
							startQuestTimer("SHOW_GLAKIAS_TIMER", 3000, controller, null);
						}
						break;
					}
					case "STAGE_2_FAILED":
					{
						manageMovie(world, Movie.SC_BOSS_FREYA_DEFEAT);
						startQuestTimer("STAGE_2_FAILED2", 22000, npc, null);
						break;
					}
					case "STAGE_2_FAILED2":
					{
						world.destroy();
						break;
					}
					case "STAGE_3_MOVIE":
					{
						freya.deleteMe();
						manageMovie(world, Movie.SC_BOSS_FREYA_PHASECH_B);
						startQuestTimer("STAGE_3_START", 21500, controller, null);
						break;
					}
					case "STAGE_3_START":
					{
						final boolean isHardMode = isHard(world);
						for (PlayerInstance players : world.getPlayers())
						{
							players.broadcastPacket(ExChangeClientEffectInfo.STATIC_FREYA_DESTROYED);
							for (int emmiterId : EMMITERS)
							{
								players.sendPacket(new OnEventTrigger(emmiterId, true));
							}
						}
						final GrandBossInstance frey = (GrandBossInstance) addSpawn((isHardMode ? FREYA_STAND_HARD : FREYA_STAND_EASY), FREYA_SPAWN, false, 0, true, world.getId());
						world.setStatus(4);
						world.setParameter("canSpawnMobs", true);
						world.setParameter("freya", frey);
						controller.getVariables().set("FREYA_MOVE", 0);
						notifyEvent("START_SPAWN", controller, null);
						startQuestTimer("START_MOVE", 10000, controller, null);
						startQuestTimer("CAST_BLIZZARD", 50000, controller, null);
						manageScreenMsg(world, NpcStringId.BEGIN_STAGE_3);
						
						if (isHardMode)
						{
							frey.doCast(FREYA_ANGER.getSkill());
							startQuestTimer("FREYA_BUFF", 15000, controller, null);
						}
						break;
					}
					case "FREYA_BUFF":
					{
						freya.doCast(FREYA_BUFF.getSkill());
						startQuestTimer("FREYA_BUFF", 15000, controller, null);
						break;
					}
					case "START_MOVE":
					{
						if (npc.getVariables().getInt("FREYA_MOVE") == 0)
						{
							controller.getVariables().set("FREYA_MOVE", 1);
							if (!freya.isInCombat())
							{
								freya.setRunning();
								freya.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, MIDDLE_POINT);
							}
						}
						break;
					}
					case "CAST_BLIZZARD":
					{
						final boolean isHardMode = isHard(world);
						if (!freya.isInvul())
						{
							final int manaBurnUse = controller.getVariables().getInt("MANA_BURN_USE", 0);
							if (isHardMode && (manaBurnUse < 4) && (freya.getCurrentHp() < (freya.getMaxHp() * (0.8 - (0.2 * manaBurnUse)))))
							{
								controller.getVariables().set("MANA_BURN_USE", manaBurnUse + 1);
								freya.doCast(BLIZZARD_FORCE.getSkill());
								startQuestTimer("MANA_BURN", 7000, controller, null);
								manageScreenMsg(world, NpcStringId.MAGIC_POWER_SO_STRONG_THAT_IT_COULD_MAKE_YOU_LOSE_YOUR_MIND_CAN_BE_FELT_FROM_SOMEWHERE);
							}
							else
							{
								final Skill skill = (isHardMode ? BLIZZARD_HARD.getSkill() : BLIZZARD_EASY.getSkill());
								freya.doCast(skill);
								manageScreenMsg(world, NpcStringId.STRONG_MAGIC_POWER_CAN_BE_FELT_FROM_SOMEWHERE);
							}
						}
						
						final int time = (isHardMode ? getRandom(35, 40) : getRandom(55, 60)) * 1000;
						startQuestTimer("CAST_BLIZZARD", time, controller, null);
						
						for (Npc minion : world.getNpcs(BREATH, GLACIER, KNIGHT_EASY, KNIGHT_HARD))
						{
							if ((minion != null) && !minion.isDead() && !minion.isInCombat())
							{
								manageRandomAttack(world, minion);
							}
						}
						break;
					}
					case "SPAWN_SUPPORT":
					{
						for (PlayerInstance players : world.getPlayers())
						{
							players.setIsInvul(false);
						}
						freya.setIsInvul(false);
						freya.disableCoreAI(false);
						manageScreenMsg(world, NpcStringId.BEGIN_STAGE_4);
						
						final FriendlyNpcInstance jinia = (FriendlyNpcInstance) addSpawn(SUPP_JINIA, SUPP_JINIA_SPAWN, false, 0, true, world.getId());
						jinia.setRunning();
						jinia.setIsInvul(true);
						jinia.setCanReturnToSpawnPoint(false);
						jinia.reduceCurrentHp(1, freya, null); // TODO: Find better way for attack
						freya.reduceCurrentHp(1, jinia, null);
						
						final FriendlyNpcInstance kegor = (FriendlyNpcInstance) addSpawn(SUPP_KEGOR, SUPP_KEGOR_SPAWN, false, 0, true, world.getId());
						kegor.setRunning();
						kegor.setIsInvul(true);
						kegor.setCanReturnToSpawnPoint(false);
						kegor.reduceCurrentHp(1, freya, null); // TODO: Find better way for attack
						freya.reduceCurrentHp(1, kegor, null);
						
						startQuestTimer("GIVE_SUPPORT", 1000, controller, null);
						break;
					}
					case "GIVE_SUPPORT":
					{
						if (params.getBoolean("isSupportActive", false))
						{
							world.getNpc(SUPP_JINIA).doCast(JINIA_SUPPORT.getSkill());
							world.getNpc(SUPP_KEGOR).doCast(KEGOR_SUPPORT.getSkill());
							startQuestTimer("GIVE_SUPPORT", 25000, controller, null);
						}
						break;
					}
					case "START_SPAWN":
					{
						for (Npc statues : getKnightStatues(world))
						{
							notifyEvent("SPAWN_KNIGHT", statues, null);
						}
						
						for (Location loc : KNIGHTS_LOC)
						{
							final Attackable knight = (Attackable) addSpawn((isHard(world) ? KNIGHT_HARD : KNIGHT_EASY), loc, false, 0, false, world.getId());
							knight.disableCoreAI(true);
							knight.setDisplayEffect(1);
							knight.getSpawn().setLocation(loc);
							startQuestTimer("ICE_RUPTURE", getRandom(2, 5) * 1000, knight, null);
						}
						
						for (int i = 0; i < world.getStatus(); i++)
						{
							notifyEvent("SPAWN_GLACIER", controller, null);
						}
						break;
					}
					case "SPAWN_KNIGHT":
					{
						if (params.getBoolean("canSpawnMobs", true))
						{
							final boolean isHardMode = isHard(world);
							final Location loc = new Location(MIDDLE_POINT.getX() + getRandom(-1000, 1000), MIDDLE_POINT.getY() + getRandom(-1000, 1000), MIDDLE_POINT.getZ());
							final Attackable knight = (Attackable) addSpawn(isHardMode ? KNIGHT_HARD : KNIGHT_EASY, npc.getLocation(), false, 0, false, world.getId());
							knight.getVariables().set("SPAWNED_NPC", npc);
							knight.disableCoreAI(true);
							knight.setIsImmobilized(true);
							knight.setDisplayEffect(1);
							knight.getSpawn().setLocation(loc);
							
							final int time = (isHardMode ? getRandom(5, 10) : getRandom(15, 20)) * 1000;
							startQuestTimer("ICE_RUPTURE", time, knight, null);
						}
						break;
					}
					case "SPAWN_GLACIER":
					{
						if (params.getBoolean("canSpawnMobs", true))
						{
							final Location loc = new Location(MIDDLE_POINT.getX() + getRandom(-1000, 1000), MIDDLE_POINT.getY() + getRandom(-1000, 1000), MIDDLE_POINT.getZ());
							final Attackable glacier = (Attackable) addSpawn(GLACIER, loc, false, 0, false, world.getId());
							glacier.setDisplayEffect(1);
							glacier.disableCoreAI(true);
							glacier.setIsImmobilized(true);
							startQuestTimer("CHANGE_STATE", 1400, glacier, null);
						}
						break;
					}
					case "ICE_RUPTURE":
					{
						if (npc.isCoreAIDisabled())
						{
							npc.disableCoreAI(false);
							npc.setIsImmobilized(false);
							npc.setDisplayEffect(2);
							manageRandomAttack(world, npc);
						}
						break;
					}
					case "FIND_TARGET":
					{
						manageRandomAttack(world, npc);
						break;
					}
					case "CHANGE_STATE":
					{
						npc.setDisplayEffect(2);
						startQuestTimer("CAST_SKILL", 20000, npc, null);
						break;
					}
					case "CAST_SKILL":
					{
						if (npc.isScriptValue(0) && !npc.isDead())
						{
							npc.setTarget(npc);
							npc.doCast(ICE_STONE.getSkill());
							npc.setScriptValue(1);
						}
						break;
					}
					case "SUICIDE":
					{
						npc.setDisplayEffect(3);
						npc.setUndying(false);
						npc.doDie(null);
						break;
					}
					case "BLIZZARD":
					{
						npc.getVariables().set("SUICIDE_COUNT", npc.getVariables().getInt("SUICIDE_COUNT") + 1);
						
						if (npc.getVariables().getInt("SUICIDE_ON") == 0)
						{
							if (npc.getVariables().getInt("SUICIDE_COUNT") == 2)
							{
								startQuestTimer("ELEMENTAL_SUICIDE", 20000, npc, null);
							}
							else
							{
								if (SkillCaster.checkUseConditions(npc, BLIZZARD_BREATH.getSkill()))
								{
									npc.setTarget(npc);
									npc.doCast(BLIZZARD_BREATH.getSkill());
								}
								startQuestTimer("BLIZZARD", 20000, npc, null);
							}
						}
						break;
					}
					case "ELEMENTAL_SUICIDE":
					{
						npc.setTarget(npc);
						npc.doCast(SUICIDE_BREATH.getSkill());
						break;
					}
					case "ELEMENTAL_KILLED":
					{
						if (npc.getVariables().getInt("SUICIDE_ON") == 1)
						{
							npc.setTarget(npc);
							npc.doCast(SUICIDE_BREATH.getSkill());
						}
						break;
					}
					case "FINISH_WORLD":
					{
						if (freya != null)
						{
							freya.decayMe();
						}
						
						for (PlayerInstance players : world.getPlayers())
						{
							players.broadcastPacket(ExChangeClientEffectInfo.STATIC_FREYA_DEFAULT);
						}
						world.destroy();
						break;
					}
					case "LEADER_RANGEBUFF":
					{
						if (SkillCaster.checkUseConditions(npc, SELF_RANGE_BUFF.getSkill()))
						{
							npc.setTarget(npc);
							npc.doCast(SELF_RANGE_BUFF.getSkill());
						}
						else
						{
							startQuestTimer("LEADER_RANGEBUFF", 30000, npc, null);
						}
						break;
					}
					case "LEADER_RANDOMIZE":
					{
						final Attackable mob = (Attackable) npc;
						mob.clearAggroList();
						
						World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 1000, characters ->
						{
							mob.addDamageHate(characters, 0, getRandom(10000, 20000));
						});
						startQuestTimer("LEADER_RANDOMIZE", 25000, npc, null);
						break;
					}
					case "LEADER_DASH":
					{
						final Creature mostHated = ((Attackable) npc).getMostHated();
						if (getRandomBoolean() && !npc.isCastingNow(SkillCaster::isAnyNormalType) && (mostHated != null) && !mostHated.isDead() && (npc.calculateDistance3D(mostHated) < 1000))
						{
							npc.setTarget(mostHated);
							npc.doCast(LEADER_RUSH.getSkill());
						}
						startQuestTimer("LEADER_DASH", 10000, npc, null);
						break;
					}
					case "LEADER_DESTROY":
					{
						final Attackable mob = (Attackable) npc;
						if (npc.getVariables().getInt("OFF_SHOUT") == 0)
						{
							manageScreenMsg(world, NpcStringId.THE_SPACE_FEELS_LIKE_ITS_GRADUALLY_STARTING_TO_SHAKE);
							
							switch (getRandom(4))
							{
								case 0:
								{
									npc.broadcastSay(ChatType.SHOUT, NpcStringId.ARCHER_GIVE_YOUR_BREATH_FOR_THE_INTRUDER);
									break;
								}
								case 1:
								{
									npc.broadcastSay(ChatType.SHOUT, NpcStringId.MY_KNIGHTS_SHOW_YOUR_LOYALTY);
									break;
								}
								case 2:
								{
									npc.broadcastSay(ChatType.SHOUT, NpcStringId.I_CAN_TAKE_IT_NO_LONGER);
									break;
								}
								case 3:
								{
									npc.broadcastSay(ChatType.SHOUT, NpcStringId.ARCHER_HEED_MY_CALL);
									for (int i = 0; i < 3; i++)
									{
										final Attackable breath = (Attackable) addSpawn(BREATH, npc.getLocation(), true, 0, false, world.getId());
										breath.setRunning();
										breath.addDamageHate(mob.getMostHated(), 0, 999);
										breath.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, mob.getMostHated());
										startQuestTimer("BLIZZARD", 20000, breath, null);
									}
									break;
								}
							}
						}
						break;
					}
					case "LEADER_DELAY":
					{
						if (npc.getVariables().getInt("DELAY_VAL") == 0)
						{
							npc.getVariables().set("DELAY_VAL", 1);
						}
						break;
					}
					case "SHOW_GLAKIAS_TIMER":
					{
						final int time = (int) ((controller.getVariables().getLong("TIMER_END", 0) - System.currentTimeMillis()) / 1000);
						manageTimer(world, time, NpcStringId.BATTLE_END_LIMIT_TIME);
						break;
					}
					case "MANA_BURN":
					{
						world.getPlayers().forEach(temp -> temp.setCurrentMp(0, true));
						break;
					}
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			if (npc.getId() == SUPP_JINIA)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return null;
			}
			else if (npc.getId() == SUPP_KEGOR)
			{
				if (world.getParameters().getBoolean("isSupportActive", false))
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return null;
				}
				return "18851.html";
			}
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return null;
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			final StatsSet params = world.getParameters();
			switch (npc.getId())
			{
				case FREYA_THRONE:
				{
					final Npc controller = params.getObject("controller", Npc.class);
					final Npc freya = params.getObject("freya", Npc.class);
					if ((controller.getVariables().getInt("FREYA_MOVE") == 0) && world.isStatus(1))
					{
						controller.getVariables().set("FREYA_MOVE", 1);
						if (!freya.isInCombat())
						{
							manageScreenMsg(world, NpcStringId.FREYA_HAS_STARTED_TO_MOVE);
							freya.setRunning();
							freya.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, MIDDLE_POINT);
						}
					}
					
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.02))
					{
						notifyEvent("STAGE_1_FINISH", controller, null);
						cancelQuestTimer("CAST_BLIZZARD", controller, null);
					}
					else
					{
						if ((attacker.getMountType() == MountType.STRIDER) && !attacker.isAffectedBySkill(ANTI_STRIDER.getSkillId()) && !npc.isCastingNow(SkillCaster::isAnyNormalType))
						{
							if (!npc.isSkillDisabled(ANTI_STRIDER.getSkill()))
							{
								npc.setTarget(attacker);
								npc.doCast(ANTI_STRIDER.getSkill());
							}
						}
						
						final Creature mostHated = ((Attackable) npc).getMostHated();
						final boolean canReachMostHated = (mostHated != null) && !mostHated.isDead() && (npc.calculateDistance3D(mostHated) <= 800);
						
						if (getRandom(10000) < 3333)
						{
							if (getRandomBoolean())
							{
								if ((npc.calculateDistance3D(attacker) <= 800) && SkillCaster.checkUseConditions(npc, ICE_BALL.getSkill()))
								{
									npc.setTarget(attacker);
									npc.doCast(ICE_BALL.getSkill());
								}
							}
							else if (canReachMostHated && SkillCaster.checkUseConditions(npc, ICE_BALL.getSkill()))
							{
								npc.setTarget(mostHated);
								npc.doCast(ICE_BALL.getSkill());
							}
						}
						else if (getRandom(10000) < 800)
						{
							if (getRandomBoolean())
							{
								if ((npc.calculateDistance3D(attacker) <= 800) && SkillCaster.checkUseConditions(npc, SUMMON_ELEMENTAL.getSkill()))
								{
									npc.setTarget(attacker);
									npc.doCast(SUMMON_ELEMENTAL.getSkill());
								}
							}
							else if (canReachMostHated && SkillCaster.checkUseConditions(npc, SUMMON_ELEMENTAL.getSkill()))
							{
								npc.setTarget(mostHated);
								npc.doCast(SUMMON_ELEMENTAL.getSkill());
							}
						}
						else if (getRandom(10000) < 1500)
						{
							if (!npc.isAffectedBySkill(SELF_NOVA.getSkillId()) && SkillCaster.checkUseConditions(npc, SELF_NOVA.getSkill()))
							{
								npc.setTarget(npc);
								npc.doCast(SELF_NOVA.getSkill());
							}
						}
					}
					break;
				}
				case FREYA_STAND_EASY:
				case FREYA_STAND_HARD:
				{
					final Npc controller = params.getObject("controller", Npc.class);
					final Npc freya = params.getObject("freya", Npc.class);
					if (controller.getVariables().getInt("FREYA_MOVE") == 0)
					{
						controller.getVariables().set("FREYA_MOVE", 1);
						if (!freya.isInCombat())
						{
							freya.setRunning();
							freya.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, MIDDLE_POINT);
						}
					}
					
					if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.2)) && !params.getBoolean("isSupportActive", false))
					{
						world.setParameter("isSupportActive", true);
						freya.setIsInvul(true);
						freya.disableCoreAI(true);
						for (PlayerInstance players : world.getPlayers())
						{
							players.setIsInvul(true);
							players.abortAttack();
						}
						manageMovie(world, Movie.SC_BOSS_KEGOR_INTRUSION);
						startQuestTimer("SPAWN_SUPPORT", 27000, controller, null);
					}
					
					if ((attacker.getMountType() == MountType.STRIDER) && !attacker.isAffectedBySkill(ANTI_STRIDER.getSkillId()) && !npc.isCastingNow(SkillCaster::isAnyNormalType))
					{
						if (!npc.isSkillDisabled(ANTI_STRIDER.getSkill()))
						{
							npc.setTarget(attacker);
							npc.doCast(ANTI_STRIDER.getSkill());
						}
					}
					
					final Creature mostHated = ((Attackable) npc).getMostHated();
					final boolean canReachMostHated = (mostHated != null) && !mostHated.isDead() && (npc.calculateDistance3D(mostHated) <= 800);
					
					if (getRandom(10000) < 3333)
					{
						if (getRandomBoolean())
						{
							if ((npc.calculateDistance3D(attacker) <= 800) && SkillCaster.checkUseConditions(npc, ICE_BALL.getSkill()))
							{
								npc.setTarget(attacker);
								npc.doCast(ICE_BALL.getSkill());
							}
						}
						else if (canReachMostHated && SkillCaster.checkUseConditions(npc, ICE_BALL.getSkill()))
						{
							npc.setTarget(mostHated);
							npc.doCast(ICE_BALL.getSkill());
						}
					}
					else if (getRandom(10000) < 1333)
					{
						if (getRandomBoolean())
						{
							if ((npc.calculateDistance3D(attacker) <= 800) && SkillCaster.checkUseConditions(npc, SUMMON_ELEMENTAL.getSkill()))
							{
								npc.setTarget(attacker);
								npc.doCast(SUMMON_ELEMENTAL.getSkill());
							}
						}
						else if (canReachMostHated && SkillCaster.checkUseConditions(npc, SUMMON_ELEMENTAL.getSkill()))
						{
							npc.setTarget(mostHated);
							npc.doCast(SUMMON_ELEMENTAL.getSkill());
						}
					}
					else if (getRandom(10000) < 1500)
					{
						if (!npc.isAffectedBySkill(SELF_NOVA.getSkillId()) && SkillCaster.checkUseConditions(npc, SELF_NOVA.getSkill()))
						{
							npc.setTarget(npc);
							npc.doCast(SELF_NOVA.getSkill());
						}
					}
					else if (getRandom(10000) < 1333)
					{
						if (!npc.isAffectedBySkill(REFLECT_MAGIC.getSkillId()) && SkillCaster.checkUseConditions(npc, REFLECT_MAGIC.getSkill()))
						{
							npc.setTarget(npc);
							npc.doCast(REFLECT_MAGIC.getSkill());
						}
					}
					break;
				}
				case GLACIER:
				{
					if (npc.isScriptValue(0) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.5)))
					{
						npc.setTarget(attacker);
						npc.doCast(ICE_STONE.getSkill());
						npc.setScriptValue(1);
					}
					break;
				}
				case BREATH:
				{
					if ((npc.getCurrentHp() < (npc.getMaxHp() / 20)) && (npc.getVariables().getInt("SUICIDE_ON", 0) == 0))
					{
						npc.getVariables().set("SUICIDE_ON", 1);
						startQuestTimer("ELEMENTAL_KILLED", 1000, npc, null);
					}
					break;
				}
				case KNIGHT_EASY:
				case KNIGHT_HARD:
				{
					if (npc.isCoreAIDisabled())
					{
						manageRandomAttack(world, npc);
						npc.disableCoreAI(false);
						npc.setIsImmobilized(false);
						npc.setDisplayEffect(2);
						cancelQuestTimer("ICE_RUPTURE", npc, null);
					}
					break;
				}
				case GLAKIAS_EASY:
				case GLAKIAS_HARD:
				{
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.02))
					{
						if (npc.getVariables().getInt("OFF_SHOUT") == 0)
						{
							npc.getVariables().set("OFF_SHOUT", 1);
							npc.getVariables().set("DELAY_VAL", 2);
							npc.setTarget(attacker);
							npc.doCast(CANCEL.getSkill());
						}
						else if (npc.getVariables().getInt("OFF_SHOUT") == 1)
						{
							npc.setTarget(attacker);
							npc.doCast(CANCEL.getSkill());
						}
					}
					else if ((npc.getVariables().getInt("OFF_SHOUT") == 0) && (npc.getVariables().getInt("DELAY_VAL") == 1))
					{
						final Creature mostHated = ((Attackable) npc).getMostHated();
						final boolean canReachMostHated = (mostHated != null) && !mostHated.isDead() && (npc.calculateDistance3D(mostHated) < 1000);
						
						if (npc.getVariables().getInt("TIMER_ON") == 0)
						{
							npc.getVariables().set("TIMER_ON", 1);
							startQuestTimer("LEADER_RANGEBUFF", getRandom(5, 30) * 1000, npc, null);
							startQuestTimer("LEADER_RANDOMIZE", 25000, npc, null);
							startQuestTimer("LEADER_DASH", 5000, npc, null);
							startQuestTimer("LEADER_DESTROY", 60000, npc, null);
						}
						
						if (getRandom(10000) < 2500)
						{
							if (getRandom(10000) < 2500)
							{
								if (SkillCaster.checkUseConditions(npc, POWER_STRIKE.getSkill()))
								{
									npc.setTarget(attacker);
									npc.doCast(POWER_STRIKE.getSkill());
								}
							}
							else if (SkillCaster.checkUseConditions(npc, POWER_STRIKE.getSkill()) && canReachMostHated)
							{
								npc.setTarget(((Attackable) npc).getMostHated());
								npc.doCast(POWER_STRIKE.getSkill());
							}
						}
						else if (getRandom(10000) < 1500)
						{
							if (getRandomBoolean())
							{
								if (SkillCaster.checkUseConditions(npc, POINT_TARGET.getSkill()))
								{
									npc.setTarget(attacker);
									npc.doCast(POINT_TARGET.getSkill());
								}
							}
							else if (SkillCaster.checkUseConditions(npc, POINT_TARGET.getSkill()) && canReachMostHated)
							{
								npc.setTarget(((Attackable) npc).getMostHated());
								npc.doCast(POINT_TARGET.getSkill());
							}
						}
						else if (getRandom(10000) < 1500)
						{
							if (getRandomBoolean())
							{
								if (SkillCaster.checkUseConditions(npc, CYLINDER_THROW.getSkill()))
								{
									npc.setTarget(attacker);
									npc.doCast(CYLINDER_THROW.getSkill());
								}
							}
							else if (SkillCaster.checkUseConditions(npc, CYLINDER_THROW.getSkill()) && canReachMostHated)
							{
								npc.setTarget(((Attackable) npc).getMostHated());
								npc.doCast(CYLINDER_THROW.getSkill());
							}
						}
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			switch (npc.getId())
			{
				case GLACIER:
				{
					if (skill == ICE_STONE.getSkill())
					{
						if (getRandom(100) < 75)
						{
							final Attackable breath = (Attackable) addSpawn(BREATH, npc.getLocation(), false, 0, false, world.getId());
							if (player != null)
							{
								breath.setRunning();
								breath.addDamageHate(player, 0, 999);
								breath.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
							}
							else
							{
								manageRandomAttack(world, breath);
							}
							startQuestTimer("BLIZZARD", 20000, breath, null);
						}
						notifyEvent("SUICIDE", npc, null);
					}
					break;
				}
				case BREATH:
				{
					if (skill == SUICIDE_BREATH.getSkill())
					{
						npc.doDie(null);
					}
					break;
				}
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			final StatsSet params = world.getParameters();
			final Npc controller = params.getObject("controller", Npc.class);
			switch (npc.getId())
			{
				case GLAKIAS_EASY:
				case GLAKIAS_HARD:
				{
					manageDespawnMinions(world);
					manageTimer(world, 60, NpcStringId.TIME_REMAINING_UNTIL_NEXT_BATTLE);
					cancelQuestTimer("STAGE_2_FAILED", controller, null);
					startQuestTimer("STAGE_3_MOVIE", 60000, controller, null);
					break;
				}
				case FREYA_STAND_EASY:
				case FREYA_STAND_HARD:
				{
					world.setParameter("isSupportActive", false);
					npc.teleToLocation(FREYA_CORPSE);
					final Npc jinia = world.getNpc(SUPP_JINIA);
					final Npc kegor = world.getNpc(SUPP_KEGOR);
					if (jinia != null)
					{
						world.getNpc(SUPP_JINIA).deleteMe();
					}
					if (kegor != null)
					{
						world.getNpc(SUPP_KEGOR).teleToLocation(KEGOR_FINISH);
					}
					manageMovie(world, Movie.SC_BOSS_FREYA_ENDING_A);
					manageDespawnMinions(world);
					world.finishInstance();
					DecayTaskManager.getInstance().cancel(npc);
					cancelQuestTimer("GIVE_SUPPORT", controller, null);
					cancelQuestTimer("CAST_BLIZZARD", controller, null);
					cancelQuestTimer("FREYA_BUFF", controller, null);
					startQuestTimer("FINISH_WORLD", 300000, controller, null);
					break;
				}
				case KNIGHT_EASY:
				case KNIGHT_HARD:
				{
					final Npc spawnedBy = npc.getVariables().getObject("SPAWNED_NPC", Npc.class);
					final NpcVariables var = controller.getVariables();
					int knightCount = var.getInt("KNIGHT_COUNT");
					
					if ((var.getInt("FREYA_MOVE") == 0) && world.isStatus(1))
					{
						var.set("FREYA_MOVE", 1);
						final Npc freya = params.getObject("freya", Npc.class);
						if (!freya.isInCombat())
						{
							manageScreenMsg(world, NpcStringId.FREYA_HAS_STARTED_TO_MOVE);
							freya.setRunning();
							freya.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, MIDDLE_POINT);
						}
					}
					
					if ((knightCount < 10) && (world.isStatus(2)))
					{
						knightCount++;
						var.set("KNIGHT_COUNT", knightCount);
						
						if (knightCount == 10)
						{
							notifyEvent("STAGE_2_MOVIE", controller, null);
							world.setStatus(3);
						}
					}
					
					if (spawnedBy != null)
					{
						final int time = (isHard(world) ? getRandom(30, 60) : getRandom(50, 60)) * 1000;
						startQuestTimer("SPAWN_KNIGHT", time, spawnedBy, null);
					}
					break;
				}
				case GLACIER:
				{
					startQuestTimer("SPAWN_GLACIER", getRandom(30, 60) * 1000, controller, null);
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	protected void onEnter(PlayerInstance player, Instance instance, boolean firstEnter)
	{
		super.onEnter(player, instance, firstEnter);
		
		if (firstEnter)
		{
			player.broadcastPacket(ExChangeClientEffectInfo.STATIC_FREYA_DEFAULT);
			for (int emmiterId : EMMITERS)
			{
				player.sendPacket(new OnEventTrigger(emmiterId, false));
			}
		}
	}
	
	private void manageRandomAttack(Instance world, Npc mob)
	{
		final List<PlayerInstance> players = world.getPlayers().stream().filter(p -> !p.isDead() && !p.isInvisible()).collect(Collectors.toList());
		Collections.shuffle(players);
		
		final PlayerInstance target = (!players.isEmpty()) ? players.get(0) : null;
		if (target != null)
		{
			((Attackable) mob).addDamageHate(target, 0, 999);
			mob.setRunning();
			mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
		else
		{
			startQuestTimer("FIND_TARGET", 10000, mob, null);
		}
	}
	
	private void manageDespawnMinions(Instance world)
	{
		world.setParameter("canSpawnMobs", false);
		for (MonsterInstance mobs : world.getAliveNpcs(MonsterInstance.class, BREATH, GLACIER, KNIGHT_EASY, KNIGHT_HARD))
		{
			mobs.doDie(null);
		}
	}
	
	private boolean isHard(Instance world)
	{
		return world.getTemplateId() == TEMPLATE_ID_HARD;
	}
	
	private void manageTimer(Instance world, int time, NpcStringId npcStringId)
	{
		for (PlayerInstance p : world.getPlayers())
		{
			p.sendPacket(new ExSendUIEvent(p, false, false, time, 0, npcStringId));
		}
	}
	
	private void manageScreenMsg(Instance world, NpcStringId stringId)
	{
		showOnScreenMsg(world, stringId, ExShowScreenMessage.TOP_CENTER, 6000);
	}
	
	private void manageMovie(Instance world, Movie movie)
	{
		final Npc controller = world.getParameters().getObject("controller", Npc.class);
		playMovie(World.getInstance().getVisibleObjectsInRange(controller, PlayerInstance.class, 8000), movie);
	}
	
	private List<Npc> getKnightStatues(Instance world)
	{
		final Npc controller = world.getParameters().getObject("controller", Npc.class);
		final List<Npc> invis = world.getNpcs(INVISIBLE_NPC);
		invis.remove(controller);
		return invis;
	}
	
	public static void main(String[] args)
	{
		new IceQueensCastleBattle();
	}
}