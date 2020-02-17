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
package ai.areas.Parnassus.EntrancePortalToCrystalCaverns;

import java.util.Calendar;

import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

import ai.AbstractNpcAI;
import instances.CrystalCaverns.CrystalCavernsCoralGarden;
import instances.CrystalCaverns.CrystalCavernsEmeraldSquare;
import instances.CrystalCaverns.CrystalCavernsSteamCorridor;

/**
 * Entrance Portal to Crystal Caverns AI.
 * @author St3eT
 */
public class EntrancePortalToCrystalCaverns extends AbstractNpcAI
{
	// NPCs
	private static final int CAVERNS_ENTRACE = 33522;
	// Misc
	private static final int EMERALD_SQUARE_TEMPLATE_ID = 163;
	private static final int STEAM_CORRIDOR_TEMPLATE_ID = 164;
	private static final int CORAL_GARDEN_TEMPLATE_ID = 165;
	private static final int PRISON_ENTRACE_TRIGGER_1 = 24230010;
	private static final int PRISON_ENTRACE_TRIGGER_2 = 24230012;
	private static final int CAVERNS_ENTRACE_TRIGGER_1 = 24230014;
	private static final int CAVERNS_ENTRACE_TRIGGER_2 = 24230016;
	private static final int CAVERNS_ENTRACE_TRIGGER_3 = 24230018;
	
	private EntrancePortalToCrystalCaverns()
	{
		addStartNpc(CAVERNS_ENTRACE);
		addTalkId(CAVERNS_ENTRACE);
		addFirstTalkId(CAVERNS_ENTRACE);
		addSpawnId(CAVERNS_ENTRACE);
		addSeeCreatureId(CAVERNS_ENTRACE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enterInstance"))
		{
			Quest instanceScript = null;
			
			switch (getCurrentInstanceTemplateId())
			{
				case EMERALD_SQUARE_TEMPLATE_ID:
				{
					instanceScript = QuestManager.getInstance().getQuest(CrystalCavernsEmeraldSquare.class.getSimpleName());
					break;
				}
				case STEAM_CORRIDOR_TEMPLATE_ID:
				{
					instanceScript = QuestManager.getInstance().getQuest(CrystalCavernsSteamCorridor.class.getSimpleName());
					break;
				}
				case CORAL_GARDEN_TEMPLATE_ID:
				{
					instanceScript = QuestManager.getInstance().getQuest(CrystalCavernsCoralGarden.class.getSimpleName());
					break;
				}
			}
			
			if (instanceScript != null)
			{
				instanceScript.notifyEvent(event, npc, player);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "EntrancePortal_" + getCurrentInstanceTemplateId() + ".html";
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		getTimers().addRepeatingTimer("LOOP_TIMER", 10000, npc, null);
		return super.onSpawn(npc);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		if (event.equals("LOOP_TIMER"))
		{
			final int currentTemplateId = getCurrentInstanceTemplateId();
			
			World.getInstance().forEachVisibleObjectInRange(npc, PlayerInstance.class, 500, p ->
			{
				updateTriggersForPlayer(player, currentTemplateId);
			});
		}
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			creature.getActingPlayer().sendPacket(new OnEventTrigger(PRISON_ENTRACE_TRIGGER_1, true));
			creature.getActingPlayer().sendPacket(new OnEventTrigger(PRISON_ENTRACE_TRIGGER_2, true));
			updateTriggersForPlayer(creature.getActingPlayer(), getCurrentInstanceTemplateId());
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	public void updateTriggersForPlayer(PlayerInstance player, int currentTemplateId)
	{
		if (player != null)
		{
			player.sendPacket(new OnEventTrigger(CAVERNS_ENTRACE_TRIGGER_1, false));
			player.sendPacket(new OnEventTrigger(CAVERNS_ENTRACE_TRIGGER_2, false));
			player.sendPacket(new OnEventTrigger(CAVERNS_ENTRACE_TRIGGER_3, false));
			
			switch (currentTemplateId)
			{
				case EMERALD_SQUARE_TEMPLATE_ID:
				{
					player.sendPacket(new OnEventTrigger(CAVERNS_ENTRACE_TRIGGER_1, true));
					break;
				}
				case STEAM_CORRIDOR_TEMPLATE_ID:
				{
					player.sendPacket(new OnEventTrigger(CAVERNS_ENTRACE_TRIGGER_2, true));
					break;
				}
				case CORAL_GARDEN_TEMPLATE_ID:
				{
					player.sendPacket(new OnEventTrigger(CAVERNS_ENTRACE_TRIGGER_3, true));
					break;
				}
			}
		}
	}
	
	public int getCurrentInstanceTemplateId()
	{
		final int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int templateId = -1;
		
		switch (day)
		{
			case Calendar.MONDAY:
			{
				templateId = (hour < 18) ? EMERALD_SQUARE_TEMPLATE_ID : STEAM_CORRIDOR_TEMPLATE_ID;
				break;
			}
			case Calendar.TUESDAY:
			{
				templateId = (hour < 18) ? CORAL_GARDEN_TEMPLATE_ID : EMERALD_SQUARE_TEMPLATE_ID;
				break;
			}
			case Calendar.WEDNESDAY:
			{
				templateId = (hour < 18) ? STEAM_CORRIDOR_TEMPLATE_ID : CORAL_GARDEN_TEMPLATE_ID;
				break;
			}
			case Calendar.THURSDAY:
			{
				templateId = (hour < 18) ? EMERALD_SQUARE_TEMPLATE_ID : STEAM_CORRIDOR_TEMPLATE_ID;
				break;
			}
			case Calendar.FRIDAY:
			{
				templateId = (hour < 18) ? CORAL_GARDEN_TEMPLATE_ID : EMERALD_SQUARE_TEMPLATE_ID;
				break;
			}
			case Calendar.SATURDAY:
			{
				templateId = (hour < 18) ? STEAM_CORRIDOR_TEMPLATE_ID : CORAL_GARDEN_TEMPLATE_ID;
				break;
			}
			case Calendar.SUNDAY:
			{
				templateId = (hour < 18) ? EMERALD_SQUARE_TEMPLATE_ID : STEAM_CORRIDOR_TEMPLATE_ID;
				break;
			}
		}
		return templateId;
	}
	
	public static void main(String[] args)
	{
		new EntrancePortalToCrystalCaverns();
	}
}