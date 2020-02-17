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
package ai.areas.TalkingIsland.HarnakUndergroundRuinsZone;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.MonsterInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.spawns.SpawnTemplate;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.AbstractNpcAI;

/**
 * Harnak Underground Ruins
 * @video https://www.youtube.com/watch?v=0IdcAB3aYO0
 * @author LasTravel, Gigi
 * @date 2017-03-10 - [13:09:05]
 */
public class HarnakUndergroundRuinsZone extends AbstractNpcAI
{
	//@formatter:off
	private static final int[] NORMAL_MOBS = {22931, 22932, 22933, 22934, 22935, 22936, 22937, 22938, 23349};
	//@formatter:on
	static Map<ZoneType, zoneInfo> _roomInfo = new HashMap<>(24);
	static final Set<SpawnTemplate> _templates = ConcurrentHashMap.newKeySet();
	
	public HarnakUndergroundRuinsZone()
	{
		addKillId(NORMAL_MOBS);
		addSpawnId(NORMAL_MOBS);
		startQuestTimer("HARNAK_SPAWN", 15000, null, null, false);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("HARNAK_SPAWN"))
		{
			for (int zoneId = 60142; zoneId <= 60165; zoneId++)
			{
				ZoneType zone = ZoneManager.getInstance().getZoneById(zoneId);
				_roomInfo.put(zone, new zoneInfo());
				String zoneName = zone.getName().toLowerCase().replace(" ", "_");
				_templates.stream().forEach(t -> t.spawn(g -> String.valueOf(g.getName()).equalsIgnoreCase(zoneName), null));
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	private static final class zoneInfo
	{
		private int currentPoints = 0;
		private int currentMonitorizedDamage = 0;
		private int zoneStage = 0;
		
		public zoneInfo()
		{
		}
		
		void setZoneStage(int a)
		{
			zoneStage = a;
		}
		
		void setCurrentPoint(int a)
		{
			currentPoints = a;
		}
		
		void setMonitorizedDamage(int a)
		{
			currentMonitorizedDamage = a;
		}
		
		int getZoneStage()
		{
			return zoneStage;
		}
		
		int getCurrentPoints()
		{
			return currentPoints;
		}
		
		int getCurrentMonitorizedDamage()
		{
			return currentMonitorizedDamage;
		}
		
		void reset()
		{
			currentPoints = 0;
			currentMonitorizedDamage = 0;
			zoneStage = 0;
		}
	}
	
	private static final class changeZoneStage implements Runnable
	{
		private final ZoneType _zone;
		
		public changeZoneStage(ZoneType zone)
		{
			_zone = zone;
		}
		
		@Override
		public void run()
		{
			try
			{
				zoneInfo currentInfo = _roomInfo.get(_zone);
				switch (currentInfo.getZoneStage())
				{
					case 0:
					{
						_zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.MONITOR_THE_DAMAGE_FOR_30_SEC, ExShowScreenMessage.TOP_CENTER, 3000));
						break;
					}
					case 1:
					{
						_zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.TWENTY_FIVE_SECONDS_LEFT, ExShowScreenMessage.TOP_CENTER, 3000));
						break;
					}
					case 2:
					{
						_zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.TWENTY_SECONDS_LEFT, ExShowScreenMessage.TOP_CENTER, 3000));
						break;
					}
					case 3:
					{
						_zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.FIFTEEN_SECONDS_LEFT, ExShowScreenMessage.TOP_CENTER, 3000));
						break;
					}
					case 4:
					{
						_zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.TEN_SECONDS_LEFT, ExShowScreenMessage.TOP_CENTER, 3000));
						break;
					}
					case 5:
					{
						_zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.FIVE_SECONDS_LEFT, ExShowScreenMessage.TOP_CENTER, 3000));
						break;
					}
					case 6:
					{
						if (currentInfo.getCurrentMonitorizedDamage() >= 10)
						{
							_zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.DEMONIC_SYSTEM_WILL_ACTIVATE, ExShowScreenMessage.TOP_CENTER, 3000));
							String zoneName = _zone.getName().toLowerCase().replace(" ", "_");
							_templates.stream().forEach(t -> t.despawn(g -> String.valueOf(g.getName()).equalsIgnoreCase(zoneName)));
							_templates.stream().forEach(t -> t.spawn(g -> String.valueOf(g.getName()).equalsIgnoreCase(zoneName + "_demonic"), null));
							_zone.getPlayersInside().forEach(temp -> temp.sendPacket(new ExSendUIEvent(temp, false, false, 600, 0, NpcStringId.DEMONIC_SYSTEM_ACTIVATED)));
							currentInfo.setZoneStage(7);
							ThreadPool.schedule(new changeZoneStage(_zone), 600000); // 10min
						}
						else
						{
							currentInfo.reset();
							return;
						}
						break;
					}
					case 7:
					{
						currentInfo.reset();
						for (PlayerInstance player : _zone.getPlayersInside())
						{
							if (player != null)
							{
								player.sendPacket(new ExSendUIEvent(player));
							}
						}
						String zoneName = _zone.getName().toLowerCase().replace(" ", "_");
						_templates.stream().forEach(t -> t.despawn(g -> String.valueOf(g.getName()).equalsIgnoreCase(zoneName + "_demonic")));
						_templates.stream().forEach(t -> t.spawn(g -> String.valueOf(g.getName()).equalsIgnoreCase(zoneName), null));
						return;
					}
				}
				if (currentInfo.getZoneStage() < 6)
				{
					currentInfo.setZoneStage(currentInfo.getZoneStage() + 1);
					ThreadPool.schedule(new changeZoneStage(_zone), 5000);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		for (Entry<ZoneType, zoneInfo> currentZone : _roomInfo.entrySet())
		{
			if (currentZone.getKey().isInsideZone(npc))
			{
				final zoneInfo currentInfo = currentZone.getValue();
				int currentPoints = currentInfo.getCurrentPoints();
				if (currentPoints == 300)
				{
					if (currentInfo.getZoneStage() < 1)
					{
						return super.onKill(npc, killer, isSummon);
					}
					int currentDamage = currentInfo.getCurrentMonitorizedDamage();
					int calcDamage = currentDamage + 1;
					if (calcDamage >= 10)
					{
						calcDamage = 10;
					}
					currentInfo.setMonitorizedDamage(calcDamage);
					for (PlayerInstance player : currentZone.getKey().getPlayersInside())
					{
						if (player != null)
						{
							player.sendPacket(new ExSendUIEvent(player, ExSendUIEvent.TYPE_NORNIL, calcDamage, 10, NpcStringId.MONITOR_THE_DAMAGE));
						}
					}
					return super.onKill(npc, killer, isSummon);
				}
				
				int calcPoints = currentPoints + 1;
				if (calcPoints >= 300)
				{
					calcPoints = 300;
					ThreadPool.schedule(new changeZoneStage(currentZone.getKey()), 1000);
				}
				currentInfo.setCurrentPoint(calcPoints);
				for (PlayerInstance player : currentZone.getKey().getPlayersInside())
				{
					if (player != null)
					{
						player.sendPacket(new ExSendUIEvent(player, ExSendUIEvent.TYPE_NORNIL, calcPoints, 300, NpcStringId.DANGER_INCREASING_DANGER_INCREASING));
					}
				}
			}
		}
		
		if (npc.getDisplayEffect() > 0)
		{
			MonsterInstance copy = (MonsterInstance) addSpawn(npc.getId(), npc.getX(), npc.getY(), npc.getZ(), 0, true, 0, false);
			copy.setTarget(killer);
			copy.addDamageHate(killer, 500, 99999);
			copy.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, killer);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (getRandom(20) > 18)
		{
			npc.setDisplayEffect(1);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public void onSpawnActivate(SpawnTemplate template)
	{
		_templates.add(template);
	}
	
	public static void main(String[] args)
	{
		new HarnakUndergroundRuinsZone();
	}
}
