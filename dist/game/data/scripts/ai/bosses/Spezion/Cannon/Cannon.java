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
package ai.bosses.Spezion.Cannon;

import java.util.HashMap;
import java.util.Map;

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.serverpackets.Earthquake;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

import ai.AbstractNpcAI;

/**
 * Cannon AI.
 * @author St3eT
 */
public class Cannon extends AbstractNpcAI
{
	// NPCs
	private static final int[] CANNONS =
	{
		32939,
		32940,
		32941,
		32942,
	};
	private static final int INVISIBLE_NPC = 32943;
	// Skills
	private static final SkillHolder PRESENT_SKILL = new SkillHolder(14175, 1); // Cannon Blast
	// Items
	private static final int CANNONBALL = 17611; // Giant Cannonbal
	private static final int MEMORY_FRAGMENT = 17612; // Memory Fragment
	private static final int F_MEMORY_FRAGMENT = 17613; // Frightening Memory Fragment
	// Misc
	private static final Map<Integer, Integer> TRANSFORM_DATA = new HashMap<>();
	static
	{
		TRANSFORM_DATA.put(22965, 22979); // Novice Phantom -> Novice Escort Swordsman
		TRANSFORM_DATA.put(22966, 22980); // Phantom Wizard -> Novice Escort Wizard
		TRANSFORM_DATA.put(22967, 22981); // Median Phantom -> Median Escort Swordsman
	}
	
	private Cannon()
	{
		addStartNpc(CANNONS);
		addTalkId(CANNONS);
		addFirstTalkId(CANNONS);
		addSpawnId(CANNONS);
		addSpellFinishedId(CANNONS);
		addKillId(TRANSFORM_DATA.keySet());
		addKillId(TRANSFORM_DATA.values());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "useCannonBall":
			{
				if (npc.isScriptValue(0))
				{
					htmltext = "cannon-recharge.html";
				}
				else if (getQuestItemsCount(player, CANNONBALL) == 0)
				{
					htmltext = "cannon-noItem.html";
				}
				else
				{
					takeItems(player, CANNONBALL, 1);
					npc.setScriptValue(0);
					npc.setTitle("Cannon is loading");
					npc.broadcastInfo();
					addSkillCastDesire(npc, npc, PRESENT_SKILL, 23);
					startQuestTimer("CANNON_RECHARGE", 300000, npc, null);
				}
				break;
			}
			case "CANNON_RECHARGE":
			{
				npc.setScriptValue(1);
				npc.setTitle("Empty Cannon");
				npc.broadcastInfo();
				break;
			}
			case "LIGHT_CHECK":
			{
				World.getInstance().forEachVisibleObjectInRange(npc, MonsterInstance.class, 1000, monster ->
				{
					final int monsterId = monster.getId();
					if (TRANSFORM_DATA.containsKey(monsterId))
					{
						final Npc transformed = addSpawn(TRANSFORM_DATA.get(monster.getId()), monster);
						transformed.getVariables().set("DROP_MEMORY_FRAGMENT", true);
						transformed.getVariables().set("COUNTDOWN_TIME", 21);
						startQuestTimer("COUTDOWN", 100, transformed, null);
						monster.deleteMe();
					}
				});
				break;
			}
			case "COUTDOWN":
			{
				final int time = npc.getVariables().getInt("COUNTDOWN_TIME", 0) - 1;
				npc.setTitle(npc.isDead() ? null : Integer.toString(time));
				npc.broadcastInfo();
				if (time == 0)
				{
					npc.deleteMe();
				}
				else if (!npc.isDead())
				{
					npc.getVariables().set("COUNTDOWN_TIME", time);
					startQuestTimer("COUTDOWN", 1000, npc, null);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		if (npc.getVariables().getBoolean("DROP_MEMORY_FRAGMENT", false))
		{
			npc.dropItem(killer, MEMORY_FRAGMENT, 1);
			npc.dropItem(killer, F_MEMORY_FRAGMENT, 1);
		}
		else if (getRandom(10) < 1)
		{
			npc.dropItem(killer, CANNONBALL, 1);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		startQuestTimer("CANNON_RECHARGE", 1000, npc, null);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		if (skill.getId() == PRESENT_SKILL.getSkillId())
		{
			final StatsSet npcParams = npc.getParameters();
			
			npc.broadcastPacket(new Earthquake(npc, 10, 5));
			npc.broadcastPacket(new OnEventTrigger(npcParams.getInt("TRIGGER_ID"), true));
			final Npc light = addSpawn(INVISIBLE_NPC, npcParams.getInt("LIGHT_ZONE_POS_X"), npcParams.getInt("LIGHT_ZONE_POS_Y"), npcParams.getInt("LIGHT_ZONE_POS_Z"), 0, false, 10000);
			startQuestTimer("LIGHT_CHECK", 500, light, null);
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "cannon.html";
	}
	
	public static void main(String[] args)
	{
		new Cannon();
	}
}