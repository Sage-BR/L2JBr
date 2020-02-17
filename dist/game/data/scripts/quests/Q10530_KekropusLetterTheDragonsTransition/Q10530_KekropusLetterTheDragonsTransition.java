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
package quests.Q10530_KekropusLetterTheDragonsTransition;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Kekropus' Letter: Belos' Whereabouts (10424)
 * @author Stayawy
 */
public class Q10530_KekropusLetterTheDragonsTransition extends LetterQuest
{
	// NPCs
	private static final int JERONIN = 30121;
	private static final int NAMO = 33973;
	private static final int INVISIBLE_NPC = 19543;
	// Items
	private static final int SOE_TOWN_OF_GIRAN = 46733; // Scroll of Escape: Town of GIRAN
	private static final int SOE_DRAGON_VALLEY = 46734; // Scroll of Escape: Dragon Valley
	// Location
	private static final Location TELEPORT_LOC = new Location(84015, 147219, -3395);
	// Rewards
	private static final int XP = 1533168;
	private static final int SP = 306;
	// Misc
	private static final int MIN_LEVEL = 81;
	private static final int MAX_LEVEL = 84;
	
	public Q10530_KekropusLetterTheDragonsTransition()
	{
		super(10530);
		addTalkId(JERONIN, NAMO);
		addSeeCreatureId(INVISIBLE_NPC);
		setIsErtheiaQuest(false);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartQuestSound("Npcdialog1.kekrops_quest_15");
		setStartLocation(SOE_TOWN_OF_GIRAN, TELEPORT_LOC);
		registerQuestItems(SOE_TOWN_OF_GIRAN, SOE_DRAGON_VALLEY);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30121-02.htm":
			case "33973-02.html":
			{
				htmltext = event;
				break;
			}
			case "30121-03.htm":
			{
				if (qs.isCond(2))
				{
					takeItems(player, SOE_TOWN_OF_GIRAN, -1);
					giveItems(player, SOE_DRAGON_VALLEY, 1);
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "33973-03.html":
			{
				if (qs.isCond(3))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(npc, player);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, XP, SP);
					}
					showOnScreenMsg(player, NpcStringId.YOU_HAVE_COMPLETED_ALL_OF_KEKROPUS_LETTER, ExShowScreenMessage.TOP_CENTER, 6000);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, false);
		
		if (qs == null)
		{
			return htmltext;
		}
		
		if (qs.isStarted())
		{
			if ((npc.getId() == JERONIN) && qs.isCond(2))
			{
				htmltext = "30121-01.htm";
			}
			else if (qs.isCond(3))
			{
				htmltext = npc.getId() == JERONIN ? "30121-04.htm" : "33973-01.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSeeCreature(Npc npc, Creature creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			final QuestState qs = getQuestState(player, false);
			
			if ((qs != null) && qs.isCond(3))
			{
				showOnScreenMsg(player, NpcStringId.DEN_OF_EVIL_IS_A_GOOD_HUNTING_ZONE_FOR_LV_81_OR_ABOVE, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
}