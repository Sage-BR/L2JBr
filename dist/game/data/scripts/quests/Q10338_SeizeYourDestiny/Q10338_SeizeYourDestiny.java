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
package quests.Q10338_SeizeYourDestiny;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Seize Your Destiny (10338)
 * @author Sdw, Mobius
 */
public class Q10338_SeizeYourDestiny extends Quest
{
	// NPCs
	private static final int CELLPHINE = 33477;
	private static final int HADEL = 33344;
	private static final int HERMUNCUS = 33340;
	// Monsters
	private static final int HARNAKS_WRAITH = 27445;
	// Items
	private static final ItemHolder SCROLL_OF_AFTERLIFE = new ItemHolder(17600, 1);
	private static final ItemHolder STEEL_DOOR_GUILD_COIN = new ItemHolder(37045, 400);
	// Locations
	private static final Location RELIQUARY_OF_THE_GIANT = new Location(-114962, 226564, -2864);
	// Misc
	private static final String STARTED_CLASS_VAR = "STARTED_CLASS";
	private static final int MIN_LV = 85;
	
	public Q10338_SeizeYourDestiny()
	{
		super(10338);
		addStartNpc(CELLPHINE);
		addTalkId(CELLPHINE, HADEL, HERMUNCUS);
		addKillId(HARNAKS_WRAITH);
		addCondNotRace(Race.ERTHEIA, "33477-08.html");
		addCondNotClassId(ClassId.JUDICATOR, "");
		addCondIsNotSubClassActive("");
		addCondMinLevel(MIN_LV, "33477-07.html");
		addCondInCategory(CategoryType.FOURTH_CLASS_GROUP, "33477-07.html");
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
			case "TELEPORT":
			{
				if (player.isSubClassActive() && !player.isDualClassActive())
				{
					htmltext = "";
					break;
				}
				player.teleToLocation(RELIQUARY_OF_THE_GIANT, null);
				playMovie(player, Movie.SC_AWAKENING_VIEW);
				break;
			}
			case "33477-03.html":
			{
				if (!player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					qs.setSimulated(false);
					qs.setState(State.CREATED);
					qs.startQuest();
					qs.set(STARTED_CLASS_VAR, player.getActiveClass());
					htmltext = event;
				}
				break;
			}
			case "33344-05.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "33340-02.html":
			{
				if (qs.isCond(3))
				{
					showOnScreenMsg(player, NpcStringId.YOU_MAY_USE_SCROLL_OF_AFTERLIFE_FROM_HERMUNCUS_TO_AWAKEN, ExShowScreenMessage.TOP_CENTER, 10000);
					giveItems(player, SCROLL_OF_AFTERLIFE);
					rewardItems(player, STEEL_DOOR_GUILD_COIN);
					qs.exitQuest(true, true);
					htmltext = event;
				}
				break;
			}
			case "33344-02.html":
			case "33344-03.html":
			case "33344-04.html":
			case "33477-02.htm":
			{
				htmltext = event;
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case CELLPHINE:
			{
				if (qs.isStarted())
				{
					htmltext = "33477-06.html";
				}
				else if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) || hasQuestItems(player, SCROLL_OF_AFTERLIFE.getId()))
				{
					htmltext = "33477-05.html";
				}
				else if (player.getLevel() > 84)
				{
					// htmltext = "33477-01.htm";
					player.sendPacket(new NpcHtmlMessage(npc.getObjectId(), getHtm(player, "33477-01.htm")));
					htmltext = null;
				}
				else
				{
					htmltext = "33477-07.html";
				}
				break;
			}
			case HADEL:
			{
				if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) || hasQuestItems(player, SCROLL_OF_AFTERLIFE.getId()))
				{
					htmltext = "33344-07.html";
				}
				else if (player.getLevel() < 85)
				{
					htmltext = "33344-06.html";
				}
				else if ((qs.getInt(STARTED_CLASS_VAR) != player.getActiveClass()) || (player.isSubClassActive() && !player.isDualClassActive()))
				{
					htmltext = "33344-09.html";
				}
				else
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "33344-01.html";
							break;
						}
						case 2:
						{
							htmltext = "33344-08.html";
							break;
						}
						case 3:
						{
							htmltext = "33344-07.html";
							break;
						}
					}
				}
				break;
			}
			case HERMUNCUS:
			{
				if ((qs.getInt(STARTED_CLASS_VAR) != player.getActiveClass()) && !hasQuestItems(player, SCROLL_OF_AFTERLIFE.getId()))
				{
					htmltext = "33340-04.html";
				}
				else if (qs.isCond(3))
				{
					htmltext = "33340-01.html";
				}
				else if (hasQuestItems(player, SCROLL_OF_AFTERLIFE.getId()))
				{
					htmltext = "33340-03.html";
				}
				else
				{
					htmltext = "33340-02.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(2) && (qs.getInt(STARTED_CLASS_VAR) == player.getActiveClass()))
		{
			qs.setCond(3, true);
		}
		return super.onKill(npc, player, isSummon);
	}
}