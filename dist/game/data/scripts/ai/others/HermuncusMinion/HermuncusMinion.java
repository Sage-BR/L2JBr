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
package ai.others.HermuncusMinion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.network.SystemMessageId;

import ai.AbstractNpcAI;

/**
 * Hermuncus' Minion AI.
 * @author St3eT
 */
public class HermuncusMinion extends AbstractNpcAI
{
	// NPCs
	private static final Map<Integer, Integer> HERMUNCUS_MINIONS = new HashMap<>();
	
	static
	{
		HERMUNCUS_MINIONS.put(33560, 1010720); // Town of Schuttgart
		HERMUNCUS_MINIONS.put(33561, 1010721); // Seed of Annihilation
		HERMUNCUS_MINIONS.put(33562, 1010722); // Bloody Swampland
		HERMUNCUS_MINIONS.put(33563, 1010723); // Ruins of Ye Sagira
		HERMUNCUS_MINIONS.put(33564, 1010724); // Ancient City Arcan
		HERMUNCUS_MINIONS.put(33565, 1010725); // Garden of Genesis
		HERMUNCUS_MINIONS.put(33566, 1010726); // Fairy Settlement
		HERMUNCUS_MINIONS.put(33567, 1010727); // Seal of Shilen
		HERMUNCUS_MINIONS.put(33568, 1010728); // Orbis Temple
		HERMUNCUS_MINIONS.put(33569, 1010729); // Parnassus
		HERMUNCUS_MINIONS.put(33747, 1010114); // Guilloutine Fortress
		HERMUNCUS_MINIONS.put(33779, 0); // Nornil's Cave
	}
	
	// Locations
	private static final Map<Integer, Location> TELEPORTS_85 = new LinkedHashMap<>();
	
	static
	{
		TELEPORTS_85.put(1010720, new Location(86153, -143707, -1336)); // Town of Schuttgart
		TELEPORTS_85.put(1010721, new Location(-178445, 154072, 2568)); // Seed of Annihilation
		TELEPORTS_85.put(1010722, new Location(-15826, 30477, -3616)); // Bloody Swampland
		TELEPORTS_85.put(1010723, new Location(-116021, 236167, -3088)); // Ruins of Ye Sagira
	}
	
	private static final Map<Integer, Location> TELEPORTS_90 = new LinkedHashMap<>();
	
	static
	{
		TELEPORTS_90.put(1010724, new Location(207688, 84720, -1144)); // Ancient City Arcan
		TELEPORTS_90.put(1010725, new Location(207129, 111132, -2040)); // Garden of Genesis (Lv. 90)
		TELEPORTS_90.put(1010726, new Location(214432, 79587, 824)); // Fairy Settlement (Lv. 90)
		TELEPORTS_90.put(1010727, new Location(187383, 20498, -3584)); // Seal of Shilen (Lv. 95)
		TELEPORTS_90.put(1010728, new Location(198703, 86034, -192)); // Orbis Temple Entrance (Lv. 95)
		TELEPORTS_90.put(1010114, new Location(44725, 146026, -3512)); // Guilloutine Fortress
		TELEPORTS_90.put(1010732, new Location(-74377, 53701, -3672)); // Isle of Souls Harbot
		TELEPORTS_90.put(1010729, new Location(149358, 172479, -952)); // Parnassus (Lv. 97)
	}
	
	private HermuncusMinion()
	{
		addStartNpc(HERMUNCUS_MINIONS.keySet());
		addTalkId(HERMUNCUS_MINIONS.keySet());
		addFirstTalkId(HERMUNCUS_MINIONS.keySet());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final StringTokenizer st = new StringTokenizer(event, " ");
		event = st.nextToken();
		
		if (event.equals("teleportList"))
		{
			if (!player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
			{
				htmltext = "HermuncusMinion-no.html";
			}
			else
			{
				final ArrayList<Integer> teleportList = new ArrayList<>(TELEPORTS_85.keySet());
				if (player.getLevel() >= 90)
				{
					teleportList.addAll(TELEPORTS_90.keySet());
				}
				
				final Integer currentLoc = HERMUNCUS_MINIONS.get(npc.getId());
				if (teleportList.contains(currentLoc))
				{
					teleportList.remove(currentLoc);
				}
				
				final StringBuilder sb = new StringBuilder();
				for (Integer teleportLoc : teleportList)
				{
					sb.append(generateButton(teleportLoc));
				}
				htmltext = getHtm(player, "HermuncusMinion-01.html").replace("%locations%", sb.toString());
			}
		}
		else if (event.equals("teleport") && st.hasMoreTokens())
		{
			final int locId = Integer.parseInt(st.nextToken());
			
			if (player.getAdena() < 150000)
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return null;
			}
			
			Location loc = null;
			if (TELEPORTS_85.containsKey(locId))
			{
				loc = TELEPORTS_85.get(locId);
			}
			else if (TELEPORTS_90.containsKey(locId))
			{
				loc = TELEPORTS_90.get(locId);
			}
			
			if (loc != null)
			{
				takeItems(player, Inventory.ADENA_ID, 150000);
				player.teleToLocation(loc);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) ? "HermuncusMinion.html" : "HermuncusMinion-no.html";
	}
	
	private String generateButton(Integer locationId)
	{
		return "<Button align=LEFT icon=TELEPORT action=\"bypass -h Quest HermuncusMinion teleport " + locationId + "\" msg=\"811;F;" + locationId + "\"><fstring>" + locationId + "</fstring> - 150000 <fstring>1000308</fstring></Button>";
	}
	
	public static void main(String[] args)
	{
		new HermuncusMinion();
	}
}
