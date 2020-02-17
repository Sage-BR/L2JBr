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
package instances.PailakaRuneCastle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.FriendlyNpcInstance;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.entity.Castle;
import org.l2jbr.gameserver.model.entity.Fort;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.instancezone.InstanceTemplate;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;
import quests.Q00726_LightWithinTheDarkness.Q00726_LightWithinTheDarkness;
import quests.Q00727_HopeWithinTheDarkness.Q00727_HopeWithinTheDarkness;

/**
 * Pailaka (Rune Castle)
 * @author Mobius
 */
public class PailakaRuneCastle extends AbstractInstance
{
	// NPCs
	private static final int[] VICTIMS =
	{
		36562,
		36563,
		36564,
		36565,
		36566,
		36567,
		36568,
		36569,
	};
	private static final Map<Integer, Integer> MANAGERS = new HashMap<>();
	static
	{
		MANAGERS.put(36403, 80); // Gludio
		MANAGERS.put(36404, 81); // Dion
		MANAGERS.put(36405, 82); // Giran
		MANAGERS.put(36406, 83); // Oren
		MANAGERS.put(36407, 84); // Aden
		MANAGERS.put(36408, 85); // Innadril
		MANAGERS.put(36409, 86); // Goddard
		MANAGERS.put(36410, 87); // Rune
		MANAGERS.put(36411, 88); // Schuttgart
		MANAGERS.put(35666, 89); // Shanty
		MANAGERS.put(35698, 90); // Southern
		MANAGERS.put(35735, 91); // Hive
		MANAGERS.put(35767, 92); // Valley
		MANAGERS.put(35804, 93); // Ivory
		MANAGERS.put(35835, 94); // Narsell
		MANAGERS.put(35867, 95); // Bayou
		MANAGERS.put(35904, 96); // White Sands
		MANAGERS.put(35936, 97); // Borderland
		MANAGERS.put(35974, 98); // Swamp
		MANAGERS.put(36011, 99); // Archaic
		MANAGERS.put(36043, 100); // Floran
		MANAGERS.put(36081, 101); // Cloud Mountain
		MANAGERS.put(36118, 102); // Tanor
		MANAGERS.put(36149, 103); // Dragonspine
		MANAGERS.put(36181, 104); // Antharas
		MANAGERS.put(36219, 105); // Western
		MANAGERS.put(36257, 106); // Hunter
		MANAGERS.put(36294, 107); // Aaru
		MANAGERS.put(36326, 108); // Demon
		MANAGERS.put(36364, 109); // Monastic
	}
	// Misc
	private static final long REENTER = 24 * 3600000; // 24 hours
	private static final Map<Integer, Long> REENETER_HOLDER = new ConcurrentHashMap<>();
	
	public PailakaRuneCastle()
	{
		super(MANAGERS.values().stream().mapToInt(Integer::valueOf).toArray());
		addFirstTalkId(VICTIMS);
		addTalkId(VICTIMS);
		addTalkId(MANAGERS.keySet());
		addStartNpc(MANAGERS.keySet());
		addInstanceCreatedId(MANAGERS.values());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "WAVE_DEFEATED_CHECK":
			{
				final Instance world = player.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				if (world.getAliveNpcs(MonsterInstance.class).isEmpty())
				{
					switch (world.getStatus())
					{
						case 0:
						case 1:
						case 2:
						{
							startQuestTimer("SPAWN_NEXT_WAVE", 20000, null, player, false);
							break;
						}
						case 3:
						{
							for (PlayerInstance member : world.getPlayers())
							{
								final QuestState qs = member.getQuestState(world.getTemplateId() < 89 ? Q00727_HopeWithinTheDarkness.class.getSimpleName() : Q00726_LightWithinTheDarkness.class.getSimpleName());
								if ((qs != null) && qs.isCond(1))
								{
									qs.setCond(2);
								}
							}
							world.finishInstance();
							break;
						}
					}
					world.incStatus();
					return null;
				}
				startQuestTimer("WAVE_DEFEATED_CHECK", 5000, null, player, false);
				break;
			}
			case "SPAWN_NEXT_WAVE":
			{
				final Instance world = player.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				List<Npc> monsters = new ArrayList<>();
				switch (world.getStatus())
				{
					case 0:
					{
						showOnScreenMsg(world, NpcStringId.STAGE_1, ExShowScreenMessage.TOP_CENTER, 5000, true);
						monsters = world.spawnGroup("monsters_first_wave");
						break;
					}
					case 1:
					{
						showOnScreenMsg(world, NpcStringId.STAGE_2, ExShowScreenMessage.TOP_CENTER, 5000, true);
						monsters = world.spawnGroup("monsters_second_wave");
						break;
					}
					case 2:
					{
						showOnScreenMsg(world, NpcStringId.STAGE_3, ExShowScreenMessage.TOP_CENTER, 5000, true);
						monsters = world.spawnGroup("monsters_third_wave");
						break;
					}
				}
				final List<FriendlyNpcInstance> helpers = world.getAliveNpcs(FriendlyNpcInstance.class);
				if (!helpers.isEmpty())
				{
					for (Npc monster : monsters)
					{
						final Npc helper = helpers.get(getRandom(helpers.size()));
						helper.reduceCurrentHp(1, monster, null); // TODO: Find better way for attack
						addAttackDesire(helper, monster);
						addAttackDesire(monster, helper);
						helper.setRunning();
						monster.reduceCurrentHp(1, helper, null); // TODO: Find better way for attack
						monster.setRandomWalking(false);
						monster.setRunning();
						addMoveToDesire(monster, helper.getLocation(), 10);
					}
				}
				cancelQuestTimer("FORCE_NEXT_WAVE", null, player);
				startQuestTimer("FORCE_NEXT_WAVE", 480000, null, player, false); // 8 minutes
				startQuestTimer("WAVE_DEFEATED_CHECK", 1000, null, player, false);
				break;
			}
			case "FORCE_NEXT_WAVE":
			{
				final Instance world = player.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				if (world.getStatus() < 3)
				{
					cancelQuestTimer("WAVE_DEFEATED_CHECK", null, player);
					world.incStatus();
					startQuestTimer("SPAWN_NEXT_WAVE", 1000, null, player, false);
				}
				break;
			}
			case "exit":
			{
				final Instance world = npc.getInstanceWorld();
				if (world == null)
				{
					return null;
				}
				world.ejectPlayer(player);
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		if (world == null)
		{
			return null;
		}
		if (npc.isDead())
		{
			return "victim-02.html";
		}
		if (world.getStatus() < 3)
		{
			return "victim-01.html";
		}
		return "victim-03.html";
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final int npcId = npc.getId();
		if (MANAGERS.containsKey(npcId))
		{
			enterInstance(player, npc, MANAGERS.get(npcId));
		}
		return null;
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		// Put re-enter for instance
		REENETER_HOLDER.put(instance.getTemplateId(), System.currentTimeMillis() + REENTER);
		// Schedule spawn of first wave
		startQuestTimer("SPAWN_NEXT_WAVE", 120000, null, player, false); // 2 minutes
	}
	
	@Override
	protected boolean validateConditions(List<PlayerInstance> group, Npc npc, InstanceTemplate template)
	{
		final PlayerInstance groupLeader = group.get(0);
		if (template.getId() < 89) // Castle
		{
			final Castle castle = npc.getCastle();
			if (castle == null)
			{
				showHtmlFile(groupLeader, "noProperPledge.html");
				return false;
			}
			else if (REENETER_HOLDER.containsKey(template.getId()))
			{
				final long time = REENETER_HOLDER.get(template.getId());
				if (time > System.currentTimeMillis())
				{
					showHtmlFile(groupLeader, "enterRestricted.html");
					return false;
				}
				REENETER_HOLDER.remove(template.getId());
			}
		}
		else // Fort
		{
			final Fort fort = npc.getFort();
			if (fort == null)
			{
				showHtmlFile(groupLeader, "noProperPledge.html");
				return false;
			}
			else if (fort.getFortState() == 0)
			{
				showHtmlFile(groupLeader, "noContractYet.html");
				return false;
			}
			else if (fort.getFortState() == 2)
			{
				showHtmlFile(groupLeader, "noCastleContract.html");
				return false;
			}
			else if (REENETER_HOLDER.containsKey(template.getId()))
			{
				final long time = REENETER_HOLDER.get(template.getId());
				if (time > System.currentTimeMillis())
				{
					showHtmlFile(groupLeader, "enterRestricted.html");
					return false;
				}
				REENETER_HOLDER.remove(template.getId());
			}
		}
		return true;
	}
	
	public static void main(String[] args)
	{
		new PailakaRuneCastle();
	}
}