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
package instances.HarnakUndergroundRuins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.variables.NpcVariables;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;
import quests.Q10338_SeizeYourDestiny.Q10338_SeizeYourDestiny;

/**
 * Harnak Underground Ruins Instance Zone.
 * @author Sdw
 */
public class HarnakUndergroundRuins extends AbstractInstance
{
	// NPCs
	private static final int HADEL = 33344;
	private static final int KRAKIA_BATHUS = 27437;
	private static final int KRAKIA_CARCASS = 27438;
	private static final int KRAKIA_LOTUS = 27439;
	private static final int RAKZAN = 27440;
	private static final int WEISS_KHAN = 27441;
	private static final int BAMONTI = 27442;
	private static final int SEKNUS = 27443;
	private static final int WEISS_ELE = 27454;
	private static final int HARNAKS_WRAITH = 27445;
	private static final int SEAL_CONTROL_DEVICE = 33548;
	private static final int POWER_SOURCE = 33501;
	private static final int[] POWER_SOURCES =
	{
		33501,
		33556,
		33557
	};
	// Locations
	private static final Location NPC_ROOM1_LOC = new Location(-107930, 206328, -10872);
	// Skills
	private static final SkillHolder RELEASE_OF_POWER = new SkillHolder(14625, 1);
	private static final SkillHolder MAXIMUM_DEFENSE = new SkillHolder(14700, 1);
	private static final SkillHolder LIGHT_HEAL = new SkillHolder(14736, 1);
	private static final SkillHolder ULTIMATE_BUFF = new SkillHolder(4318, 1);
	// Misc
	private static final int TEMPLATE_ID = 195;
	private static final int ZONE_ROOM_2 = 200032;
	private static final int ZONE_ROOM_3 = 200033;
	private static final int DOOR_ONE = 16240100;
	private static final int DOOR_TWO = 16240102;
	private static final Map<CategoryType, Integer> MOB_CATEGORY = new HashMap<>();
	static
	{
		MOB_CATEGORY.put(CategoryType.TANKER_CATEGORY, RAKZAN);
		MOB_CATEGORY.put(CategoryType.WARRIOR_CATEGORY, KRAKIA_BATHUS);
		MOB_CATEGORY.put(CategoryType.ROGUE_CATEGORY, BAMONTI);
		MOB_CATEGORY.put(CategoryType.ARCHER_CATEGORY, KRAKIA_CARCASS);
		MOB_CATEGORY.put(CategoryType.WIZARD_CATEGORY, WEISS_KHAN);
		MOB_CATEGORY.put(CategoryType.ENCHANTER_CATEGORY, SEKNUS);
		MOB_CATEGORY.put(CategoryType.SUMMONER_CATEGORY, KRAKIA_LOTUS);
		MOB_CATEGORY.put(CategoryType.HEALER_CATEGORY, WEISS_ELE);
	}
	
	public HarnakUndergroundRuins()
	{
		super(TEMPLATE_ID);
		registerMobs(KRAKIA_BATHUS, KRAKIA_CARCASS, KRAKIA_LOTUS, RAKZAN, WEISS_KHAN, BAMONTI, SEKNUS, WEISS_ELE, HARNAKS_WRAITH);
		addSeeCreatureId(POWER_SOURCES);
		addEnterZoneId(ZONE_ROOM_2, ZONE_ROOM_3);
		addFirstTalkId(SEAL_CONTROL_DEVICE);
		addTalkId(HADEL);
		addStartNpc(HADEL);
	}
	
	@Override
	protected void onEnter(PlayerInstance player, Instance instance, boolean firstEnter)
	{
		super.onEnter(player, instance, firstEnter);
		if (firstEnter)
		{
			startQuestTimer("fail_instance", 1260000, null, player);
			startQuestTimer("message1", 2500, null, player);
			startQuestTimer("message2", 5000, null, player);
			startQuestTimer("message3", 8500, null, player);
			startQuestTimer("spawn_npc1", 10000, null, player);
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "enter_instance":
			{
				enterInstance(player, npc, TEMPLATE_ID);
				break;
			}
			case "message1":
			{
				showOnScreenMsg(player, NpcStringId.AN_INTRUDER_INTERESTING, ExShowScreenMessage.TOP_CENTER, 5000);
				break;
			}
			case "message2":
			{
				showOnScreenMsg(player, NpcStringId.PROVE_YOUR_WORTH, ExShowScreenMessage.TOP_CENTER, 5000);
				break;
			}
			case "message3":
			{
				showOnScreenMsg(player, NpcStringId.ONLY_THOSE_STRONG_ENOUGH_SHALL_PROCEED, ExShowScreenMessage.TOP_CENTER, 5000);
				break;
			}
			case "message4":
			{
				showOnScreenMsg(player, NpcStringId.THOUGH_SMALL_THIS_POWER_WILL_HELP_YOU_GREATLY, ExShowScreenMessage.TOP_CENTER, 5000);
				break;
			}
			case "message5":
			{
				showOnScreenMsg(player, NpcStringId.ARE_YOU_STRONG_OR_WEAK_OF_THE_LIGHT_OR_DARKNESS, ExShowScreenMessage.TOP_CENTER, 5000);
				break;
			}
			case "message6":
			{
				showOnScreenMsg(player, NpcStringId.ONLY_THOSE_OF_LIGHT_MAY_PASS_OTHERS_MUST_PROVE_THEIR_STRENGTH, ExShowScreenMessage.TOP_CENTER, 5000);
				break;
			}
			case "razkan_say":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.COME_ATTACK_ME_IF_YOU_DARE);
				break;
			}
			case "bathus_say":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.IT_S_THE_END_FOR_YOU_TRAITOR);
				break;
			}
			case "bamonti_say":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_WANT_TO_HEAR_YOU_CRY);
				break;
			}
			case "carcass_say":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_WANT_TO_HEAR_YOU_CRY);
				break;
			}
			case "khan_say":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_LL_HAVE_TO_KILL_US_FIRST);
				break;
			}
			case "seknus_say":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.LETS_SEE_WHAT_YOU_ARE_MADE_OF);
				break;
			}
			case "lotus_say":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.REPENT_AND_YOUR_DEATH_WILL_BE_QUICK);
				break;
			}
			case "ele_say":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DIE_TRAITOR);
				break;
			}
			case "spawn_npc1":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					world.setStatus(1);
					world.spawnGroup("first_room");
					moveNpcRoom1(RAKZAN, NpcStringId.ARE_YOU_AGAINST_THE_WILL_OF_LIGHT, "razkan_say", world);
				}
				break;
			}
			case "spawn_npc2":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					world.openCloseDoor(DOOR_ONE, true);
					world.spawnGroup("power_sources");
				}
				break;
			}
			case "spawn_npc3":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					world.incStatus();
					final List<Npc> npcs = world.spawnGroup("third_room");
					final Npc powerSource = npcs.stream().filter(n -> n.getId() == POWER_SOURCE).findFirst().orElse(null);
					if (powerSource != null)
					{
						powerSource.setTarget(player);
						startQuestTimer("cast_light_heal", 3000, powerSource, player);
					}
				}
				break;
			}
			case "spawn_wave1":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					for (Entry<CategoryType, Integer> entry : MOB_CATEGORY.entrySet())
					{
						if (player.isInCategory(entry.getKey()))
						{
							world.setStatus(2);
							world.setParameter("wave", 1);
							world.setParameter("waveNpcId", entry.getValue());
							final List<Npc> npcs = world.spawnGroup("second_room_wave_1_" + entry.getValue());
							for (Npc n : npcs)
							{
								addAttackPlayerDesire(n, player);
							}
							break;
						}
					}
				}
				break;
			}
			case "spawn_wave2":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					final int waveNpcId = world.getParameters().getInt("waveNpcId");
					final List<Npc> npcs = world.spawnGroup("second_room_wave_2_" + waveNpcId);
					for (Npc n : npcs)
					{
						addAttackPlayerDesire(n, player);
					}
					world.setParameter("wave", 2);
				}
				break;
			}
			case "spawn_wave3":
			{
				showOnScreenMsg(player, NpcStringId.I_MUST_GO_HELP_SOME_MORE, ExShowScreenMessage.TOP_CENTER, 5000);
				
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					final int waveNpcId = world.getParameters().getInt("waveNpcId");
					List<Npc> npcs = world.spawnGroup("second_room_wave_3_" + waveNpcId);
					for (Npc n : npcs)
					{
						addAttackPlayerDesire(n, player);
					}
					
					npcs = world.spawnGroup("power_source");
					for (Npc n : npcs)
					{
						n.setTarget(player);
						startQuestTimer("cast_defense_maximum", 1, n, player);
					}
					world.setParameter("wave", 3);
				}
				break;
			}
			case "cast_defense_maximum":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					if (npc.calculateDistance3D(player) < MAXIMUM_DEFENSE.getSkill().getCastRange())
					{
						npc.doCast(MAXIMUM_DEFENSE.getSkill());
						
						final int defenseCounter = world.getParameters().getInt("defenseCounter", 0) + 1;
						world.setParameter("defenseCounter", defenseCounter);
						if (defenseCounter < 3)
						{
							startQuestTimer("cast_defense_maximum", 60000, npc, player);
						}
						else
						{
							npc.deleteMe();
						}
					}
					else
					{
						startQuestTimer("cast_defense_maximum", 1, npc, player);
					}
				}
				break;
			}
			case "cast_light_heal":
			{
				if (npc != null)
				{
					final Instance world = player.getInstanceWorld();
					if ((world != null) && (world.isStatus(3) || world.isStatus(4)))
					{
						if (npc.calculateDistance3D(player) < LIGHT_HEAL.getSkill().getCastRange())
						{
							npc.doCast(LIGHT_HEAL.getSkill());
						}
						startQuestTimer("cast_light_heal", 3000, npc, player);
					}
				}
				break;
			}
			case "fail_instance":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					world.removeNpcs();
					playMovie(player, Movie.SC_AWAKENING_BOSS_ENDING_B);
					startQuestTimer("exit", 13500, npc, player);
				}
				break;
			}
			case "exit":
			{
				finishInstance(player, 0);
				break;
			}
			case "spawn_npc4":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					final int waveNpcId = world.getParameters().getInt("waveNpcId");
					List<Npc> npcs = world.spawnGroup("third_room_" + waveNpcId);
					for (Npc n : npcs)
					{
						addAttackPlayerDesire(n, player);
					}
					
					npcs = world.spawnGroup("seal");
					for (Npc n : npcs)
					{
						n.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DISABLE_DEVICE_WILL_GO_OUT_OF_CONTROL_IN_1_MINUTE);
						startQuestTimer("seal_say", 10000, n, player);
					}
				}
				break;
			}
			case "activate_seal":
			{
				final Instance world = player.getInstanceWorld();
				if ((world != null) && npc.isScriptValue(0))
				{
					npc.setScriptValue(1);
					
					final int enabledSeal = world.getParameters().getInt("enabledSeal", 0) + 1;
					world.setParameter("enabledSeal", enabledSeal);
					if (enabledSeal == 2)
					{
						final QuestState qs = player.getQuestState(Q10338_SeizeYourDestiny.class.getSimpleName());
						if ((qs != null) && qs.isCond(2))
						{
							qs.setCond(3, true);
						}
						cancelQuestTimer("fail_instance", null, player);
						world.removeNpcs();
						playMovie(player, Movie.SC_AWAKENING_BOSS_ENDING_A);
						startQuestTimer("spawn_hermuncus", 25050, npc, player);
					}
				}
				break;
			}
			case "seal_say":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					final int timerCount = world.getParameters().getInt("timerCount", 0);
					switch (timerCount)
					{
						case 0:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.FIFTY_SECONDS_ARE_REMAINING);
							break;
						}
						case 1:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.FOURTY_SECONDS_ARE_REMAINING_2);
							break;
						}
						case 2:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.THIRTY_SECONDS_ARE_REMAINING_2);
							break;
						}
						case 3:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.TWENTY_SECONDS_ARE_REMAINING_2);
							break;
						}
						case 4:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.TEN_SECONDS_ARE_REMAINING_2);
							break;
						}
						case 5:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.FIVE_SECONDS);
							break;
						}
						case 6:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.FOUR_SECONDS);
							break;
						}
						case 7:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.THREE_SECONDS);
							break;
						}
						case 8:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.TWO_SECONDS);
							break;
						}
						case 9:
						{
							npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.ONE_SECOND);
							break;
						}
					}
					if (timerCount <= 4)
					{
						startQuestTimer("seal_say", 10000, npc, player);
					}
					else if ((timerCount > 4) && (timerCount <= 9))
					{
						startQuestTimer("seal_say", 1000, npc, player);
					}
					world.setParameter("timerCount", timerCount);
				}
				break;
			}
			case "spawn_hermuncus":
			{
				final Instance inst = player.getInstanceWorld();
				if (inst != null)
				{
					inst.spawnGroup("hermuncus");
				}
				break;
			}
			case "cast_release_power":
			{
				npc.setTarget(player);
				npc.doCast(RELEASE_OF_POWER.getSkill());
				break;
			}
			case "whisper_to_player":
			{
				showOnScreenMsg(player, NpcStringId.I_HERMUNCUS_GIVE_MY_POWER_TO_THOSE_WHO_FIGHT_FOR_ME, ExShowScreenMessage.TOP_CENTER, 5000);
				
				npc.broadcastSay(ChatType.WHISPER, NpcStringId.RECEIVE_THIS_POWER_FORM_THE_ANCIENT_GIANT);
				npc.broadcastSay(ChatType.WHISPER, NpcStringId.USE_THIS_NEW_POWER_WHEN_THE_TIME_IS_RIGHT);
				
				startQuestTimer("message4", 3000, npc, player);
			}
		}
		return null;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance world = killer.getInstanceWorld();
		if (world != null)
		{
			switch (world.getStatus())
			{
				case 0:
				{
					if (world.getAliveNpcs().isEmpty())
					{
						startQuestTimer("spawn_npc2", 100, npc, killer);
						world.setStatus(1);
					}
					break;
				}
				case 1:
				{
					switch (npc.getId())
					{
						case RAKZAN:
						{
							moveNpcRoom1(KRAKIA_BATHUS, NpcStringId.ARE_YOU_PLANNING_TO_BETRAY_THE_GODS_AND_FOLLOW_A_GIANT, "bathus_say", world);
							break;
						}
						case KRAKIA_BATHUS:
						{
							moveNpcRoom1(BAMONTI, NpcStringId.HAHA, "bamonti_say", world);
							break;
						}
						case BAMONTI:
						{
							moveNpcRoom1(KRAKIA_CARCASS, NpcStringId.HAHA, "carcass_say", world);
							break;
						}
						case KRAKIA_CARCASS:
						{
							moveNpcRoom1(WEISS_KHAN, NpcStringId.YOU_WILL_NOT_FREE_HERMUNCUS, "khan_say", world);
							break;
						}
						case WEISS_KHAN:
						{
							moveNpcRoom1(SEKNUS, NpcStringId.MORTAL, "seknus_say", world);
							break;
						}
						case SEKNUS:
						{
							moveNpcRoom1(KRAKIA_LOTUS, NpcStringId.TRYING_TO_FREE_HERMUNCUS, "lotus_say", world);
							break;
						}
						case KRAKIA_LOTUS:
						{
							moveNpcRoom1(WEISS_ELE, NpcStringId.YOU_WILL_NEVER_BREAK_THE_SEAL, "ele_say", world);
							break;
						}
						case WEISS_ELE:
						{
							startQuestTimer("spawn_npc2", 100, npc, killer);
							break;
						}
					}
					break;
				}
				case 2:
				{
					final StatsSet params = world.getParameters();
					final int waveNpc = params.getInt("waveNpcId");
					if (world.getAliveNpcs(waveNpc).isEmpty())
					{
						switch (params.getInt("wave"))
						{
							case 1:
							{
								startQuestTimer("spawn_wave2", 100, npc, killer);
								break;
							}
							case 2:
							{
								startQuestTimer("spawn_wave3", 100, npc, killer);
								break;
							}
							case 3:
							{
								world.openCloseDoor(DOOR_TWO, true);
								break;
							}
						}
					}
					break;
				}
				default:
				{
					if (npc.getId() == HARNAKS_WRAITH)
					{
						cancelQuestTimer("fail_instance", null, killer);
						world.removeNpcs();
						playMovie(killer, Movie.SC_AWAKENING_BOSS_ENDING_A);
						startQuestTimer("spawn_hermuncus", 25050, npc, killer);
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void moveNpcRoom1(int npcId, NpcStringId message, String timer, Instance world)
	{
		final Npc npc = world.getNpc(npcId);
		if (npc != null)
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, NPC_ROOM1_LOC);
			npc.broadcastSay(ChatType.NPC_GENERAL, message);
			startQuestTimer("timer", 2600, npc, null);
			world.setParameter("currentNpc", npcId);
		}
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance player, int damage, boolean isSummon)
	{
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			if (world.isStatus(1))
			{
				final int currentNpc = world.getParameters().getInt("currentNpc");
				if (npc.getId() != currentNpc)
				{
					world.setStatus(0);
					for (Npc n : world.getAliveNpcs())
					{
						addAttackPlayerDesire(n, player);
					}
				}
			}
			else if (world.isStatus(2))
			{
				if (((npc.getCurrentHp() / npc.getMaxHp()) * 100) < 80)
				{
					npc.doCast(ULTIMATE_BUFF.getSkill());
				}
			}
			else if (world.isStatus(3) && (npc.getId() == HARNAKS_WRAITH))
			{
				final NpcVariables vars = npc.getVariables();
				if (!vars.getBoolean("message1", false) && (((npc.getCurrentHp() / npc.getMaxHp()) * 100) > 80))
				{
					showOnScreenMsg(player, NpcStringId.FREE_ME_FROM_THIS_BINDING_OF_LIGHT, ExShowScreenMessage.TOP_CENTER, 5000);
					vars.set("message1", true);
				}
				else if (!vars.getBoolean("message2", false) && (((npc.getCurrentHp() / npc.getMaxHp()) * 100) <= 80))
				{
					showOnScreenMsg(player, NpcStringId.DESTROY_THE_GHOST_OF_HARNAK_THIS_CORRUPTED_CREATURE, ExShowScreenMessage.TOP_CENTER, 5000);
					vars.set("message2", true);
				}
				else if (!vars.getBoolean("message3", false) && (((npc.getCurrentHp() / npc.getMaxHp()) * 100) <= 60))
				{
					showOnScreenMsg(player, NpcStringId.FREE_ME_AND_I_PROMISE_YOU_THE_POWER_OF_GIANTS, ExShowScreenMessage.TOP_CENTER, 5000);
					vars.set("message3", true);
				}
				else if (((npc.getCurrentHp() / npc.getMaxHp()) * 100) <= 50)
				{
					world.incStatus();
					player.sendPacket(new ExSendUIEvent(player, false, false, 60, 0, NpcStringId.REMAINING_TIME));
					showOnScreenMsg(player, NpcStringId.NO_THE_SEAL_CONTROLS_HAVE_BEEN_EXPOSED_GUARDS_PROTECT_THE_SEAL_CONTROLS, ExShowScreenMessage.TOP_CENTER, 10000);
					startQuestTimer("spawn_npc4", 1, npc, player);
					cancelQuestTimer("fail_instance", null, player);
					startQuestTimer("fail_instance", 60000, null, player);
				}
			}
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			startQuestTimer("cast_release_power", 2000, npc, creature.getActingPlayer());
			if (npc.getId() == POWER_SOURCE)
			{
				startQuestTimer("whisper_to_player", 2000, npc, creature.getActingPlayer());
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		if (!creature.isPlayer())
		{
			return null;
		}
		
		final PlayerInstance player = creature.getActingPlayer();
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			switch (zone.getId())
			{
				case ZONE_ROOM_2:
				{
					if (world.isStatus(1))
					{
						world.incStatus();
						
						startQuestTimer("message2", 100, null, player);
						startQuestTimer("message5", 2600, null, player);
						startQuestTimer("message6", 5100, null, player);
						startQuestTimer("spawn_wave1", 5100, null, player);
					}
					break;
				}
				case ZONE_ROOM_3:
				{
					if (!world.getParameters().getBoolean("openingPlayed", false))
					{
						world.setParameter("openingPlayed", true);
						startQuestTimer("spawn_npc3", 29950, null, player);
						playMovie(player, Movie.SC_AWAKENING_BOSS_OPENING);
					}
					break;
				}
			}
		}
		return super.onEnterZone(creature, zone);
	}
	
	public static void main(String[] args)
	{
		new HarnakUndergroundRuins();
	}
}