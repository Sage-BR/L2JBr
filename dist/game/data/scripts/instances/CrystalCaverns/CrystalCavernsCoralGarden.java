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
package instances.CrystalCaverns;

import org.l2jbr.gameserver.instancemanager.WalkingManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Spawn;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;

/**
 * Crystal Caverns - Coral Garden instance zone.
 * @author St3eT
 */
public class CrystalCavernsCoralGarden extends AbstractInstance
{
	// NPCs
	private static final int CAVERNS_ENTRACE = 33522;
	private static final int MICHAELA_NORMAL = 25799;
	private static final int MICHAELA_WISE = 26116;
	private static final int MICHAELA_WEALTHY = 26115;
	private static final int MICHAELA_ARMED = 26114;
	private static final int GOLEM_1 = 19013; // Crystalline Golem
	private static final int GOLEM_2 = 19014; // Crystalline Golem
	// Location
	private static final Location BOSS_LOC = new Location(144307, 220032, -11824);
	// Misc
	private static final int TEMPLATE_ID = 165;
	private static final int BOSS_DOOR_ID = 24240026;
	private static final int PLAYER_MAX_DISTANCE = 250;
	
	public CrystalCavernsCoralGarden()
	{
		super(TEMPLATE_ID);
		addStartNpc(CAVERNS_ENTRACE);
		addTalkId(CAVERNS_ENTRACE);
		addFirstTalkId(GOLEM_1, GOLEM_2);
		addKillId(MICHAELA_NORMAL, MICHAELA_WISE, MICHAELA_WEALTHY, MICHAELA_ARMED);
		addAttackId(MICHAELA_NORMAL, MICHAELA_WISE, MICHAELA_WEALTHY, MICHAELA_ARMED);
		addRouteFinishedId(GOLEM_1, GOLEM_2);
		addInstanceEnterId(TEMPLATE_ID);
		addInstanceLeaveId(TEMPLATE_ID);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final StatsSet npcVars = npc.getVariables();
			
			switch (event)
			{
				case "SUCCESS_TIMER":
				{
					showOnScreenMsg(instance, NpcStringId.GOLEM_LOCATION_SUCCESSFUL_ENTRY_ACCESSED, ExShowScreenMessage.MIDDLE_CENTER, 5000);
					break;
				}
				case "LOOP_TIMER":
				{
					player = npcVars.getObject("PLAYER_OBJECT", PlayerInstance.class);
					
					if ((player != null) && (npc.calculateDistance3D(player) > PLAYER_MAX_DISTANCE) && npcVars.getBoolean("NPC_FOLLOWING", true))
					{
						WalkingManager.getInstance().cancelMoving(npc);
						addMoveToDesire(npc, new Location(npc.getX() + getRandom(-100, 100), npc.getY() + getRandom(-150, 150), npc.getZ()), 23);
						npc.setRunning();
						npcVars.set("NPC_FOLLOWING", false);
						getTimers().cancelTimer("LOOP_TIMER", npc, null);
						getTimers().addTimer("FAIL_TIMER", 5000, npc, null);
					}
					break;
				}
				case "FAIL_TIMER":
				{
					final Spawn spawn = npc.getSpawn();
					
					if (!npcVars.getBoolean("NPC_FOLLOWING", true))
					{
						WalkingManager.getInstance().cancelMoving(npc);
						npc.setWalking();
						npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
						npc.setScriptValue(0);
						npc.setNameString(null);
						npc.setTitleString(null);
						npc.setTitle(null);
						npc.broadcastInfo();
					}
					npcVars.set("CAN_CALL_MONSTERS", ((spawn.getX() - ((npc.getX() * spawn.getX()) - npc.getX())) + (spawn.getY() - (npc.getY() * spawn.getY()) - npc.getY())) > (200 * 200));
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
	public void onInstanceEnter(PlayerInstance player, Instance instance)
	{
		final int startTime = (int) (instance.getElapsedTime() / 1000);
		final int endTime = (int) (instance.getRemainingTime() / 1000);
		player.sendPacket(new ExSendUIEvent(player, false, true, startTime, endTime, NpcStringId.ELAPSED_TIME));
	}
	
	@Override
	public void onInstanceLeave(PlayerInstance player, Instance instance)
	{
		player.sendPacket(new ExSendUIEvent(player, true, true, 0, 0, NpcStringId.ELAPSED_TIME));
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			if (npc.isScriptValue(0))
			{
				npc.setScriptValue(1);
				npc.getVariables().set("PLAYER_OBJECT", player);
				npc.setNameString(NpcStringId.TRAITOR_CRYSTALLINE_GOLEM);
				npc.setTitleString(NpcStringId.GIVEN_TO_S1);
				npc.setTitle(player.getName());
				npc.broadcastInfo();
				WalkingManager.getInstance().startMoving(npc, npc.getId() == GOLEM_1 ? "gd_golem_1" : "gd_golem_2");
				getTimers().addRepeatingTimer("LOOP_TIMER", 500, npc, null);
			}
		}
		return null;
	}
	
	@Override
	public void onRouteFinished(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (instance != null)
		{
			WalkingManager.getInstance().cancelMoving(npc);
			showOnScreenMsg(instance, NpcStringId.GOLEM_ENTERED_THE_REQUIRED_ZONE, ExShowScreenMessage.MIDDLE_CENTER, 5000);
			npc.deleteMe();
			
			if (instance.getAliveNpcs(GOLEM_1, GOLEM_2).isEmpty())
			{
				instance.openCloseDoor(BOSS_DOOR_ID, true);
				
				final int random = getRandom(100);
				int bossId = -1;
				
				if (random < 55)
				{
					bossId = MICHAELA_NORMAL;
				}
				else if (random < 80)
				{
					bossId = MICHAELA_WISE;
				}
				else if (random < 95)
				{
					bossId = MICHAELA_WEALTHY;
				}
				else
				{
					bossId = MICHAELA_ARMED;
				}
				
				final Npc boss = addSpawn(bossId, BOSS_LOC, false, 0, false, instance.getId());
				getTimers().addTimer("SUCCESS_TIMER", 5000, boss, null);
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case MICHAELA_NORMAL:
				case MICHAELA_WISE:
				case MICHAELA_WEALTHY:
				case MICHAELA_ARMED:
				{
					instance.finishInstance();
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			switch (npc.getId())
			{
				case MICHAELA_NORMAL:
				case MICHAELA_WISE:
				case MICHAELA_WEALTHY:
				case MICHAELA_ARMED:
				{
					if (npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						instance.openCloseDoor(BOSS_DOOR_ID, false);
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	public static void main(String[] args)
	{
		new CrystalCavernsCoralGarden();
	}
}