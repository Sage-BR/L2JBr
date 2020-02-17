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
package ai.areas.AncientCityArcan;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.spawns.SpawnGroup;
import org.l2jbr.gameserver.model.spawns.SpawnTemplate;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.ScriptZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.Earthquake;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

import ai.AbstractNpcAI;
import instances.TaintedDimension.TaintedDimension;
import quests.Q10301_ShadowOfTerrorBlackishRedFog.Q10301_ShadowOfTerrorBlackishRedFog;

/**
 * Ancient Arcan City AI.
 * @author St3eT
 */
public class AncientArcanCity extends AbstractNpcAI
{
	// NPC
	private static final int CEREMONIAL_CAT = 33093;
	// Location
	private static final Location ANCIENT_ARCAN_CITY = new Location(207559, 86429, -1000);
	private static final Location EARTHQUAKE = new Location(207088, 88720, -1128);
	// Zones
	private static final ScriptZone BROADCAST_ZONE = ZoneManager.getInstance().getZoneById(23600, ScriptZone.class); // Ancient Arcan City zone
	private static final ScriptZone TELEPORT_ZONE = ZoneManager.getInstance().getZoneById(12015, ScriptZone.class); // Anghel Waterfall teleport zone
	// Misc
	private static final int CHANGE_STATE_TIME = 1800000; // 30min
	private boolean isCeremonyRunning = false;
	private final Set<SpawnTemplate> _templates = ConcurrentHashMap.newKeySet();
	
	private AncientArcanCity()
	{
		addEnterZoneId(BROADCAST_ZONE.getId(), TELEPORT_ZONE.getId());
		startQuestTimer("CHANGE_STATE", CHANGE_STATE_TIME, null, null, true);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("CHANGE_STATE"))
		{
			isCeremonyRunning = !isCeremonyRunning;
			
			for (PlayerInstance temp : BROADCAST_ZONE.getPlayersInside())
			{
				temp.sendPacket(new OnEventTrigger(262001, !isCeremonyRunning));
				temp.sendPacket(new OnEventTrigger(262003, isCeremonyRunning));
				
				if (isCeremonyRunning)
				{
					showOnScreenMsg(temp, NpcStringId.THE_INCREASED_GRASP_OF_DARK_ENERGY_CAUSES_THE_GROUND_TO_SHAKE, ExShowScreenMessage.TOP_CENTER, 5000, true);
					temp.sendPacket(new Earthquake(EARTHQUAKE, 10, 5));
				}
			}
			
			if (isCeremonyRunning)
			{
				_templates.stream().forEach(t -> t.spawn(g -> String.valueOf(g.getName()).equalsIgnoreCase("Ceremony"), null));
			}
			else
			{
				_templates.stream().forEach(t -> t.despawn(g -> String.valueOf(g.getName()).equalsIgnoreCase("Ceremony")));
				cancelQuestTimers("SOCIAL_ACTION");
			}
		}
		else if (event.contains("SOCIAL_ACTION") && (npc != null))
		{
			npc.broadcastSocialAction(2);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			
			if (zone.getId() == TELEPORT_ZONE.getId())
			{
				final QuestState qs = creature.getActingPlayer().getQuestState(Q10301_ShadowOfTerrorBlackishRedFog.class.getSimpleName());
				if ((qs != null) && qs.isCond(3))
				{
					final Quest instance = QuestManager.getInstance().getQuest(TaintedDimension.class.getSimpleName());
					if (instance != null)
					{
						instance.notifyEvent("enterInstance", null, player);
					}
				}
				else
				{
					player.teleToLocation(ANCIENT_ARCAN_CITY);
				}
			}
			else
			{
				player.sendPacket(new OnEventTrigger(262001, !isCeremonyRunning));
				player.sendPacket(new OnEventTrigger(262003, isCeremonyRunning));
				
				if (player.getVariables().getBoolean("ANCIENT_ARCAN_CITY_SCENE", true))
				{
					player.getVariables().set("ANCIENT_ARCAN_CITY_SCENE", false);
					playMovie(player, Movie.SI_ARKAN_ENTER);
				}
			}
		}
		return super.onEnterZone(creature, zone);
	}
	
	@Override
	public void onSpawnActivate(SpawnTemplate template)
	{
		_templates.add(template);
	}
	
	// @Override
	// public void onSpawnDeactivate(SpawnTemplate template)
	// {
	// _templates.remove(template);
	// }
	
	@Override
	public void onSpawnNpc(SpawnTemplate template, SpawnGroup group, Npc npc)
	{
		if (npc.getId() == CEREMONIAL_CAT)
		{
			npc.setRandomAnimation(npc.getParameters().getBoolean("disableRandomAnimation", false));
			startQuestTimer("SOCIAL_ACTION", 4500, npc, null, true);
		}
	}
	
	public static void main(String[] args)
	{
		new AncientArcanCity();
	}
}
