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
package instances.PailakaInjuredDragon;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.Id;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnAttackableFactionCall;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.skills.AbnormalType;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.TeleportZone;
import org.l2jbr.gameserver.network.serverpackets.SpecialCamera;

import instances.AbstractInstance;
import quests.Q00144_PailakaInjuredDragon.Q00144_PailakaInjuredDragon;

/**
 * Pailaka (Varka Silenos Barracks) Instance zone.
 * @author Sdw
 */
public class PailakaInjuredDragon extends AbstractInstance
{
	// NPCs
	private static final int KETRA_ORC_SHAMAN = 32499;
	private static final int KETRA_ORC_INTELLIGENCE_OFFICIER = 32509;
	private static final int KETRA_ORC_SUPPORTER = 32512;
	private static final int LATANA = 18660;
	private static final int DRAGON_CAMERA_1 = 18603;
	private static final int DRAGON_CAMERA_2 = 18604;
	private static final int DRAGON_TARGET = 18605;
	private static final int LATANA_SKILL_USE = 18661;
	private static final int TELEPORT_TRAP = 18663;
	//@formatter:off
	private static final int[] WARRIORS =
	{
		18635, 18636, 18638,
		18642, 18646, 18649,
		18650, 18653, 18654,
		18655, 18657,
	};
	private static final int[] WIZARDS =
	{
		18639, 18640, 18641,
		18644, 18645, 18648,
		18652, 18656, 18658,
		18659,
	};
	private static final int[] ANIMALS =
	{
		18637, 18643,
		18647, 18651,
	};
	// Skills
	private static final SkillHolder ULTIMATE_DEFENCE = new SkillHolder(5044, 3);
	private static final SkillHolder HEAL = new SkillHolder(4065, 7);
	private static final SkillHolder ELECTRIC_FLAME = new SkillHolder(5715, 1);
	private static final SkillHolder STUN = new SkillHolder(5716, 1);
	private static final SkillHolder FIRE_BREATH = new SkillHolder(5717, 1);
	private static final SkillHolder ANGER = new SkillHolder(5719, 1);
	private static final SkillHolder RISE_OF_LATANA = new SkillHolder(5759, 1);
	private static final int[] REJECTED_SKILLS =
	{
		28, 680, 51,
		511, 15, 254,
		1069, 1097, 1042,
		1072, 1170, 352,
		358, 1394, 695,
		115, 1083, 1160,
		1164, 1201, 1206,
		1222, 1223, 1224,
		1092, 65, 106,
		122, 127, 1049,
		1064, 1071, 1074,
		1169, 1263, 1269,
		352, 353, 1336,
		1337, 1338, 1358,
		1359, 402, 403,
		412, 1386, 1394,
		1396, 485, 501,
		1445, 1446, 1447,
		522, 531, 1481,
		1482, 1483, 1484,
		1485, 1486, 695,
		696, 716, 775,
		1511, 792, 1524,
		1529,
	};
	//@formatter:on
	// Items
	private static final int PAILAKA_INSTANT_SHIELD = 13032;
	private static final int QUICK_HEALING_POTION = 13033;
	private static final int WEAPON_UPGRADE_STAGE_1 = 13056;
	private static final int WEAPON_UPGRADE_STAGE_2 = 13057;
	// Misc
	private static final int TEMPLATE_ID = 45;
	
	public PailakaInjuredDragon()
	{
		super(TEMPLATE_ID);
		addStartNpc(KETRA_ORC_SHAMAN);
		addTalkId(KETRA_ORC_SHAMAN, KETRA_ORC_INTELLIGENCE_OFFICIER);
		addAttackId(WARRIORS);
		addAttackId(WIZARDS);
		addAttackId(LATANA);
		addSpawnId(TELEPORT_TRAP, LATANA, DRAGON_TARGET, LATANA_SKILL_USE, DRAGON_CAMERA_2);
		addSpawnId(WIZARDS);
		setCreatureSeeId(this::onCreatureSee, WIZARDS);
		setCreatureSeeId(this::onCreatureSee, LATANA);
		addKillId(ANIMALS);
		addKillId(LATANA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = player.getQuestState(Q00144_PailakaInjuredDragon.class.getSimpleName());
		if (qs != null)
		{
			switch (event)
			{
				case "enterInstance":
				{
					enterInstance(player, npc, TEMPLATE_ID);
					qs.setCond(2, true);
					htmltext = "32499-01.html";
					break;
				}
				case "reEnterInstance":
				{
					enterInstance(player, npc, TEMPLATE_ID);
					htmltext = "32499-02.html";
					break;
				}
				case "exitInstance":
				{
					finishInstance(player, 0);
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (event)
			{
				case "SET_VISIBLE":
				{
					if (!npc.getVariables().getBoolean("visible", false))
					{
						npc.setInvisible(false);
						npc.getVariables().set("visible", true);
						getTimers().addTimer("CAST_SKILL", 5000, npc, player);
					}
					break;
				}
				case "CAST_SKILL":
				{
					final SkillHolder holder = npc.getParameters().getSkillHolder(getRandomBoolean() ? "Skill01_ID" : "Skill02_ID");
					if (holder != null)
					{
						addSkillCastDesire(npc, player, holder, 500000);
					}
					getTimers().addTimer("CAST_SKILL", 10000, npc, player);
					break;
				}
				case "LOOK_NEIGHBOR":
				{
					World.getInstance().forEachVisibleObjectInRange(npc, Npc.class, HEAL.getSkill().getCastRange(), npcs ->
					{
						if ((npcs.getCurrentHpPercent() < 70) && (getRandom(100) < 10))
						{
							addSkillCastDesire(npc, npcs, HEAL, 1000000);
						}
					});
					getTimers().addTimer("LOOK_NEIGHBOR", 5000, npc, null);
					break;
				}
				case "CHECK_FOR_DROP":
				{
					if (world.getNpcsOfGroup(npc.getParameters().getString("control_maker"), n -> !n.isDead()).isEmpty())
					{
						final ZoneType zone = ZoneManager.getInstance().getZoneByName(npc.getParameters().getString("AreaName"), TeleportZone.class);
						if (zone != null)
						{
							zone.setEnabled(false, world.getId());
						}
						npc.dropItem(world.getFirstPlayer(), PAILAKA_INSTANT_SHIELD, getRandom(10) + 1);
						npc.dropItem(world.getFirstPlayer(), QUICK_HEALING_POTION, getRandom(10) + 1);
						switch (npc.getParameters().getInt("GM_ID", 0))
						{
							case 2316001:
							case 2316002:
							case 2316003:
							{
								if ((world.getParameters().getInt("2316001", 0) == 1) && (world.getParameters().getInt("2316002", 0) == 1) && (world.getParameters().getInt("2316003", 0) == 1))
								{
									giveItems(world.getFirstPlayer(), WEAPON_UPGRADE_STAGE_1, 1, true);
									world.getParameters().set(npc.getParameters().getString("GM_ID"), 0);
								}
								break;
							}
							case 2316007:
							case 2316008:
							{
								if ((world.getParameters().getInt("2316007", 0) == 1) && (world.getParameters().getInt("2316008", 0) == 1))
								{
									giveItems(world.getFirstPlayer(), WEAPON_UPGRADE_STAGE_2, 1, true);
									world.getParameters().set(npc.getParameters().getString("GM_ID"), 0);
								}
								break;
							}
						}
						npc.deleteMe();
					}
					else
					{
						getTimers().addTimer("CHECK_FOR_DROP", 2000, npc, null);
					}
					break;
				}
				case "SCE_RATANA_CAMERA_START_1":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 600, 200, 5, 0, 15000, 10000, -10, 8, 1, 1, 1));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_1_1", 2000, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_1_1":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 400, 200, 5, 4000, 15000, 10000, -10, 8, 1, 1, 0));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_1_2", 4000, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_1_2":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 300, 195, 4, 1500, 15000, 10000, -5, 10, 1, 1, 0));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_1_3", 1700, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_1_3":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 130, 2, 5, 0, 15000, 10000, 0, 0, 1, 0, 1));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_1_4", 2000, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_1_4":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 220, 0, 4, 800, 15000, 10000, 5, 10, 1, 0, 0));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_1_5", 2000, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_1_5":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 250, 185, 5, 4000, 15000, 10000, -5, 10, 1, 1, 0));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_1_6", 4000, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_1_6":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 200, 0, 5, 2000, 15000, 10000, 0, 25, 1, 0, 0));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_1_7", 4530, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_1_7":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 300, -3, 5, 3500, 15000, 6000, 0, 6, 1, 0, 0));
					getTimers().addTimer("SUICIDE_DRAGON", 10000, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_2":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 250, 0, 6, 0, 15000, 10000, 2, 0, 1, 0, 1));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_2_1", 2000, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_2_1":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 230, 0, 5, 2000, 15000, 10000, 0, 0, 1, 0, 0));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_2_2", 2500, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_2_2":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 180, 175, 2, 1500, 15000, 10000, 0, 10, 1, 1, 0));
					getTimers().addTimer("SCE_RATANA_CAMERA_START_2_3", 1500, npc, player);
					break;
				}
				case "SCE_RATANA_CAMERA_START_2_3":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 300, 180, 5, 1500, 15000, 3000, 0, 6, 1, 1, 0));
					getTimers().addTimer("SUICIDE_DRAGON", 3000, npc, player);
					break;
				}
				case "SUICIDE_DRAGON":
				{
					npc.deleteMe();
					break;
				}
				case "LATANA_ANGER_RESET":
				{
					npc.setScriptValue(0);
					break;
				}
				case "SPAWN_DRAGON_TARGET":
				{
					addSpawn(npc, DRAGON_TARGET, 105465, -41817, -1768, 0, false, 0, false, world.getId());
					getTimers().addTimer("SOCIAL_ACTION", 3000, npc, player);
					break;
				}
				case "SOCIAL_ACTION":
				{
					npc.broadcastSocialAction(0);
					getTimers().addTimer("DISPLAY_SKILL", 3000, npc, player);
					break;
				}
				case "DISPLAY_SKILL":
				{
					final Npc target = world.getNpc(DRAGON_TARGET);
					if (target != null)
					{
						addSkillCastDesire(npc, target, RISE_OF_LATANA, 5000);
					}
					getTimers().addTimer("LATANA_CAST", 9700, npc, player);
					break;
				}
				case "LATANA_CAST":
				{
					final Npc target = world.getNpc(DRAGON_TARGET);
					if (target != null)
					{
						addSkillCastDesire(npc, target, STUN, 5000);
					}
					getTimers().addTimer("ATTACK", 11030, npc, player);
					break;
				}
				case "ATTACK":
				{
					if (npc.calculateDistance2D(player) < 100)
					{
						if (getRandom(100) < 30)
						{
							addSkillCastDesire(npc, player, ELECTRIC_FLAME, 500000);
						}
						else
						{
							addAttackDesire(npc, player);
						}
					}
					else if (getRandomBoolean())
					{
						addSpawn(npc, LATANA_SKILL_USE, player.getLocation(), false, world.getId());
					}
					else
					{
						addSkillCastDesire(npc, player, FIRE_BREATH, 500000);
					}
					getTimers().addTimer("ATTACK", 6000, npc, player);
					break;
				}
				case "SPAWN_DRAGON_TARGET2":
				{
					addSpawn(npc, DRAGON_TARGET, 105465, -41817, -1768, 0, false, 0, false, world.getId());
					getTimers().addTimer("SOCIAL_ACTION", 3000, npc, player);
					break;
				}
				case "LATANA_ATTACK":
				{
					addSkillCastDesire(npc, npc, ELECTRIC_FLAME, 50000);
					getTimers().addTimer("LATANA_CAST", 3000, npc, player);
					break;
				}
				case "SPAWN_REWARD_NPC":
				{
					addSpawn(npc, KETRA_ORC_SUPPORTER, npc.getLocation(), false, world.getId());
					npc.deleteMe();
					break;
				}
				case "DRAGON_CAMERA_2_1":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 350, 200, 5, 5600, 15000, 10000, -15, 10, 1, 1, 0));
					getTimers().addTimer("DRAGON_CAMERA_2_2", 5600, npc, null);
					break;
				}
				case "DRAGON_CAMERA_2_2":
				{
					npc.broadcastPacket(new SpecialCamera(npc, 360, 200, 5, 1000, 15000, 2000, -15, 10, 1, 1, 0));
					getTimers().addTimer("SUICIDE", 10000, npc, null);
					break;
				}
			}
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			switch (npc.getId())
			{
				case TELEPORT_TRAP:
				{
					world.getParameters().set(npc.getParameters().getString("GM_ID"), 1);
					getTimers().addTimer("CHECK_FOR_DROP", 2000, npc, null);
					break;
				}
				case DRAGON_TARGET:
				{
					getTimers().addTimer("SUICIDE", 60000, npc, null);
					break;
				}
				case LATANA_SKILL_USE:
				{
					final Npc latana = world.getNpc(LATANA);
					final PlayerInstance player = world.getFirstPlayer();
					if ((latana != null) && (player != null) && (latana.calculateDistance2D(player) <= 900))
					{
						addSkillCastDesire(npc, player, STUN, 500000);
					}
					getTimers().addTimer("SUICIDE", 5000, npc, null);
					break;
				}
				case DRAGON_CAMERA_2:
				{
					npc.broadcastPacket(new SpecialCamera(npc, 450, 200, 3, 0, 15000, 10000, -15, 20, 1, 1, 1));
					getTimers().addTimer("DRAGON_CAMERA_2_1", 100, npc, null);
					break;
				}
				default:
				{
					if (CommonUtil.contains(WIZARDS, npc.getId()))
					{
						npc.setInvisible(true);
						getTimers().addTimer("LOOK_NEIGHBOR", 1000, npc, null);
					}
					npc.initSeenCreatures();
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	private void onCreatureSee(OnCreatureSee event)
	{
		final Creature creature = event.getSeen();
		final Npc npc = (Npc) event.getSeer();
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (CommonUtil.contains(WIZARDS, npc.getId()))
			{
				if (creature.isPlayer() && npc.isScriptValue(0))
				{
					npc.setScriptValue(1);
					getTimers().addTimer("SET_VISIBLE", 1000, npc, creature.getActingPlayer());
				}
			}
			else if ((npc.getId() == LATANA) && !npc.getVariables().getBoolean("creatureSeen", false) && creature.isPlayer())
			{
				final QuestState qs = creature.getActingPlayer().getQuestState(Q00144_PailakaInjuredDragon.class.getSimpleName());
				if (qs != null)
				{
					if (qs.getCond() == 4)
					{
						getTimers().addTimer("SPAWN_REWARD_NPC", 1000, npc, creature.getActingPlayer());
						npc.setScriptValue(1);
					}
					else
					{
						playSound(creature.getActingPlayer(), QuestSound.BS08_A);
						getTimers().addTimer("SPAWN_DRAGON_TARGET", 1000, npc, creature.getActingPlayer());
						getTimers().addTimer("SCE_RATANA_CAMERA_START_1", 10, world.getNpc(DRAGON_CAMERA_1), creature.getActingPlayer());
						npc.getVariables().set("creatureSeen", true);
					}
				}
				
			}
		}
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (CommonUtil.contains(WARRIORS, npc.getId()) || CommonUtil.contains(WIZARDS, npc.getId()))
			{
				final int longRangeGuardRate = npc.getParameters().getInt("LongRangeGuardRate", -1);
				if ((longRangeGuardRate != -1) && ((skill != null) && !CommonUtil.contains(REJECTED_SKILLS, skill.getId())))
				{
					if (npc.calculateDistance2D(attacker) > 150)
					{
						if ((npc.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.PD_UP_SPECIAL) == null) && (getRandom(100) < longRangeGuardRate))
						{
							addSkillCastDesire(npc, npc, ULTIMATE_DEFENCE.getSkill(), 1000000000);
						}
					}
					else
					{
						npc.getEffectList().stopEffects(AbnormalType.PD_UP_SPECIAL);
					}
				}
				if (CommonUtil.contains(WARRIORS, npc.getId()))
				{
					if (npc.calculateDistance2D(attacker) < 40)
					{
						addAttackPlayerDesire(npc, attacker, 1000);
					}
					else if (getRandom(100) < 33)
					{
						final SkillHolder holder = npc.getParameters().getSkillHolder(getRandomBoolean() ? "Skill01_ID" : "Skill02_ID");
						if (holder != null)
						{
							addSkillCastDesire(npc, attacker, holder, 500000);
						}
					}
				}
			}
			else if ((npc.getId() == LATANA))
			{
				if (!npc.getVariables().getBoolean("creatureSeen", false))
				{
					final QuestState qs = attacker.getQuestState(Q00144_PailakaInjuredDragon.class.getSimpleName());
					if (qs != null)
					{
						if (qs.getCond() == 4)
						{
							getTimers().addTimer("SPAWN_REWARD_NPC", 1000, npc, attacker);
							npc.setScriptValue(1);
						}
						else
						{
							playSound(attacker, QuestSound.BS08_A);
							getTimers().addTimer("SPAWN_DRAGON_TARGET2", 1000, npc, attacker);
							getTimers().addTimer("SCE_RATANA_CAMERA_START_2", 10, world.getNpc(DRAGON_CAMERA_1), attacker);
							npc.getVariables().set("creatureSeen", true);
						}
					}
				}
				
				if ((npc.getCurrentHpPercent() < 30) && npc.isScriptValue(0))
				{
					addSkillCastDesire(npc, npc, ANGER, 50000000);
					npc.setScriptValue(1);
					getTimers().addTimer("LATANA_ANGER_RESET", 120000, npc, attacker);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	// @formatter:off
	@RegisterEvent(EventType.ON_ATTACKABLE_FACTION_CALL)
	@RegisterType(ListenerRegisterType.NPC)
	@Id({18635,18636,18638,18642,18646,18649,18650,18653,18654,18655,18657})
	// @formatter:on
	public void onAttackableFactionCall(OnAttackableFactionCall event)
	{
		final Npc npc = event.getNpc();
		final PlayerInstance attacker = event.getAttacker();
		if (npc.calculateDistance2D(attacker) < 40)
		{
			addAttackPlayerDesire(npc, attacker, 1000);
		}
		else if (getRandom(100) < 33)
		{
			final SkillHolder holder = npc.getParameters().getSkillHolder(getRandomBoolean() ? "Skill01_ID" : "Skill02_ID");
			if (holder != null)
			{
				addSkillCastDesire(npc, attacker, holder, 500000);
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			if (CommonUtil.contains(ANIMALS, npc.getId()))
			{
				npc.dropItem(killer, getRandomBoolean() ? PAILAKA_INSTANT_SHIELD : QUICK_HEALING_POTION, getRandom(10) + 1);
			}
			else if (npc.getId() == LATANA)
			{
				addSpawn(npc, KETRA_ORC_SUPPORTER, 105974, -41794, -1784, 32768, false, 0, false, world.getId());
				addSpawn(npc, DRAGON_CAMERA_2, npc.getLocation(), false, world.getId());
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new PailakaInjuredDragon();
	}
}
