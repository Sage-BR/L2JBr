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
package instances.TaintedDimension;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.network.NpcStringId;

import instances.AbstractInstance;

/**
 * Tainted Dimension instance zone.
 * @author St3eT
 */
public class TaintedDimension extends AbstractInstance
{
	// NPCs
	private static final int MYSTERIOUS_PRIEST = 33361;
	private static final int WARD = 33137;
	private static final int HUMAN_1 = 33365;
	private static final int HUMAN_2 = 33366;
	private static final int HUMAN_3 = 33367;
	private static final int HUMAN_4 = 33369;
	private static final int MONSTER_1 = 19117;
	private static final int MONSTER_2 = 19118;
	private static final int[] VICTIMS =
	{
		33364,
		33365,
		33366,
		33367,
		33368,
		33369,
		33370,
		33371,
		33372,
	};
	private static final int[] CORPSES =
	{
		33373,
		33374,
		33375,
		33376,
		33377,
	};
	// Skills
	private static final SkillHolder REVIVE_SKILL = new SkillHolder(14265, 1);
	private static final SkillHolder LEADER_KILL_SKILL = new SkillHolder(14497, 1);
	private static final SkillHolder WARD_BOOM_SKILL = new SkillHolder(14496, 1);
	private static final SkillHolder WARD_BIG_BOOM_SKILL = new SkillHolder(14501, 1);
	// Locations
	private static final Location[] WARD_LOCATIONS =
	{
		new Location(183914, 85930, -7748),
		new Location(183848, 85976, -7759),
		new Location(183976, 85928, -7759),
		new Location(183848, 85896, -7759),
		new Location(183912, 85864, -7759),
		new Location(183928, 85992, -7759),
	};
	// Misc
	private static final NpcStringId[] MONSTER_SAY =
	{
		NpcStringId.UH_AH_AH_AH_AH,
		NpcStringId.AH_UH_AH_UH_AH,
		NpcStringId.KRRRR_2,
		NpcStringId.AH_AH_AH_UH_UH_3,
	};
	private static final int TEMPLATE_ID = 192;
	
	public TaintedDimension()
	{
		super(TEMPLATE_ID);
		addSpawnId(CORPSES);
		addSpawnId(MYSTERIOUS_PRIEST, HUMAN_1, HUMAN_2, HUMAN_3, HUMAN_4, MONSTER_1, MONSTER_2);
		addEventReceivedId(WARD, MYSTERIOUS_PRIEST);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (event)
			{
				case "LEADER_ACTION_1":
				{
					addSkillCastDesire(npc, npc, REVIVE_SKILL, 23);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.GREAT_GODDESS_OF_DESTRUCTION);
					getTimers().addTimer("LEADER_ACTION_2", 3000, npc, null);
					break;
				}
				case "LEADER_ACTION_2":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DRINK_THE_SACRIFICE_OF_BLOOD_THAT_WE_HAVE);
					getTimers().addTimer("LEADER_ACTION_3", 3000, npc, null);
					break;
				}
				case "LEADER_ACTION_3":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.AND_BRING_DOWN_THE_HAMMER_OF_JUSTICE);
					npc.broadcastEvent("SCE_ARKAN_RUMBLE", 3000, null);
					getTimers().addTimer("LEADER_ACTION_4", 4000, npc, null);
					break;
				}
				case "LEADER_ACTION_4":
				{
					npc.broadcastSocialAction(1);
					getTimers().addTimer("LEADER_ACTION_5", 6000, npc, null);
					break;
				}
				case "LEADER_ACTION_5":
				{
					addSkillCastDesire(npc, npc, LEADER_KILL_SKILL, 23);
					instance.getAliveNpcs(VICTIMS).forEach(victim -> victim.doDie(null));
					npc.broadcastEvent("SCE_COMPLETED", 3000, null);
					break;
				}
				case "WARD_SKILL_TIMER":
				{
					addSkillCastDesire(npc, npc, WARD_BOOM_SKILL, 23);
					break;
				}
				case "WARD_BOOM_TIMER":
				{
					addSkillCastDesire(npc, npc, WARD_BIG_BOOM_SKILL, 23);
					break;
				}
				case "WARD_MONSTER_TIMER":
				{
					for (int i = 0; i < 4; i++)
					{
						addSpawn(MONSTER_1, (npc.getX() + getRandom(-200, 200)), (npc.getY() + getRandom(-200, 200)), npc.getZ(), 0, false, 0, false, instance.getId());
					}
					
					for (int i = 0; i < 3; i++)
					{
						addSpawn(MONSTER_2, (npc.getX() + getRandom(-200, 200)), (npc.getY() + getRandom(-200, 200)), npc.getZ(), 0, false, 0, false, instance.getId());
					}
					getTimers().addTimer("INSTANCE_FINISH", 6000, npc, null);
					break;
				}
				case "MONSTER_SAY":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(MONSTER_SAY));
					break;
				}
				case "HUMAN_1_SAY":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.PLEASE_LET_US_GO);
					break;
				}
				case "HUMAN_2_SAY":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_DON_T_WANNA_DIE);
					break;
				}
				case "HUMAN_3_SAY":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.OH_EINHASAD);
					break;
				}
				case "HUMAN_4_SAY":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DIRTY_SHILEN_S_DOGS);
					break;
				}
				case "INSTANCE_FINISH":
				{
					instance.finishInstance(0);
					break;
				}
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterInstance"))
		{
			enterInstance(player, npc, TEMPLATE_ID);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case MYSTERIOUS_PRIEST:
				{
					if (npc.getParameters().getBoolean("isLeader", false))
					{
						getTimers().addTimer("LEADER_ACTION_1", 10000, npc, null);
						int actionId = 0;
						for (Location loc : WARD_LOCATIONS)
						{
							final Npc ward = addSpawn(WARD, loc, false, 0, false, instance.getId());
							ward.getVariables().set("ACTION_ID", actionId);
							actionId++;
						}
					}
					break;
				}
				case HUMAN_1:
				{
					getTimers().addTimer("HUMAN_1_SAY", getRandom(10, 14) * 1000, npc, null);
					break;
				}
				case HUMAN_2:
				{
					getTimers().addTimer("HUMAN_2_SAY", getRandom(6, 10) * 1000, npc, null);
					break;
				}
				case HUMAN_3:
				{
					getTimers().addTimer("HUMAN_3_SAY", getRandom(6, 11) * 1000, npc, null);
					break;
				}
				case HUMAN_4:
				{
					getTimers().addTimer("HUMAN_4_SAY", getRandom(5, 9) * 1000, npc, null);
					break;
				}
				case MONSTER_1:
				case MONSTER_2:
				{
					getTimers().addTimer("MONSTER_SAY", getRandom(15, 18) * 1000, npc, null);
					break;
				}
				default:
				{
					npc.setRandomAnimation(false);
					break;
				}
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onEventReceived(String event, Npc sender, Npc receiver, WorldObject reference)
	{
		final Instance instance = receiver.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (receiver.getId())
			{
				case WARD:
				{
					if (event.equals("SCE_ARKAN_RUMBLE"))
					{
						final int actionId = receiver.getVariables().getInt("ACTION_ID", -1);
						if (actionId >= 0)
						{
							if (actionId == 0)
							{
								getTimers().addTimer("WARD_BOOM_TIMER", 7000, receiver, null);
								getTimers().addTimer("WARD_MONSTER_TIMER", 20000, receiver, null);
							}
							else
							{
								getTimers().addTimer("WARD_SKILL_TIMER", actionId * 1000, receiver, null);
							}
						}
					}
					break;
				}
				case MYSTERIOUS_PRIEST:
				{
					if (event.equals("SCE_ARKAN_RUMBLE"))
					{
						receiver.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.FOR_THE_DESTRUCTION_AND_RESURRECTION);
					}
					else if (event.equals("SCE_COMPLETED"))
					{
						receiver.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DEAR_THE_GODDESS_OF_DESTRUCTION_THE_LIGHT_AND_THEIR_CREATURES_FEAR_YOU);
						receiver.broadcastSocialAction(1);
					}
					break;
				}
			}
		}
		return super.onEventReceived(event, sender, receiver, reference);
	}
	
	public static void main(String[] args)
	{
		new TaintedDimension();
	}
}