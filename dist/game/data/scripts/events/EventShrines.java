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
package events;

import java.util.HashMap;
import java.util.Map;

import org.l2jbr.gameserver.instancemanager.EventShrineManager;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

/**
 * @author hlwrave, Mobius
 * @Add in event config.xml enableShrines="true" after event name to enable them.
 */
public class EventShrines extends Quest
{
	private static final Map<Integer, Integer> ZONE_TRIGGERS = new HashMap<>();
	static
	{
		ZONE_TRIGGERS.put(11030, 23206292); // Hunter
		ZONE_TRIGGERS.put(11031, 24186292); // Aden
		ZONE_TRIGGERS.put(11032, 24166292); // Goddard
		ZONE_TRIGGERS.put(11035, 22136292); // Shuttgard
		ZONE_TRIGGERS.put(11028, 20226292); // Dion
		ZONE_TRIGGERS.put(11029, 22196292); // Oren
		ZONE_TRIGGERS.put(11020, 22226292); // Giran
		ZONE_TRIGGERS.put(11027, 19216292); // Gludio
		ZONE_TRIGGERS.put(11034, 23246292); // Heine
		ZONE_TRIGGERS.put(11025, 17226292); // Gluddin
		ZONE_TRIGGERS.put(11033, 21166292); // Rune
		ZONE_TRIGGERS.put(11042, 17256292); // Faeron
		ZONE_TRIGGERS.put(11043, 26206292); // Arcan
		ZONE_TRIGGERS.put(11022, 16256292); // Talking Island
	}
	
	public EventShrines()
	{
		super(-1);
		addEnterZoneId(ZONE_TRIGGERS.keySet());
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		if (creature.isPlayer())
		{
			if (EventShrineManager.getInstance().areShrinesEnabled())
			{
				creature.sendPacket(new OnEventTrigger(ZONE_TRIGGERS.get(zone.getId()), true));
			}
			else // Deactivate shrine.
			{
				creature.sendPacket(new OnEventTrigger(ZONE_TRIGGERS.get(zone.getId()) + 2, true));
			}
		}
		return super.onEnterZone(creature, zone);
	}
	
	public static void main(String[] args)
	{
		new EventShrines();
	}
}