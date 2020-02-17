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
package quests.Q00726_LightWithinTheDarkness;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.entity.Fort;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;

/**
 * Light within the Darkness (726)
 * @author Mobius
 */
public class Q00726_LightWithinTheDarkness extends Quest
{
	// NPCs
	private static final int[] NPCS =
	{
		35666, // Shanty
		35698, // Southern
		35735, // Hive
		35767, // Valley
		35804, // Ivory
		35835, // Narsell
		35867, // Bayou
		35904, // White Sands
		35936, // Borderland
		35974, // Swamp
		36011, // Archaic
		36043, // Floran
		36081, // Cloud Mountain
		36118, // Tanor
		36149, // Dragonspine
		36181, // Antharas
		36219, // Western
		36257, // Hunter
		36294, // Aaru
		36326, // Demon
		36364, // Monastic
	};
	// Items
	private static final int KNIGHT_EPALUETTE = 9912;
	// Misc
	private static final int MIN_LEVEL = 85;
	
	public Q00726_LightWithinTheDarkness()
	{
		super(726);
		addStartNpc(NPCS);
		addTalkId(NPCS);
		addCondMinLevel(MIN_LEVEL, "Warden-00a.htm");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "Warden-03.html":
			case "Warden-04.html":
			{
				break;
			}
			case "Warden-02.htm":
			{
				qs.startQuest();
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (qs.isCreated())
		{
			final Fort fort = npc.getFort();
			final Clan clan = player.getClan();
			htmltext = ((fort != null) && (clan != null) && (clan.getFortId() == fort.getResidenceId())) ? "Warden-01.htm" : "Warden-00b.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				htmltext = "Warden-03.html";
			}
			else
			{
				player.setPkKills(Math.max(0, player.getPkKills() - 1));
				giveItems(player, KNIGHT_EPALUETTE, 200);
				qs.exitQuest(QuestType.REPEATABLE);
				htmltext = "Warden-05.html";
			}
		}
		return htmltext;
	}
}