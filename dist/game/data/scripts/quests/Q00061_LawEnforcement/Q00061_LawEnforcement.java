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
package quests.Q00061_LawEnforcement;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.util.Util;

/**
 * Law Enforcement (61)
 * @author Gladicek
 */
public class Q00061_LawEnforcement extends Quest
{
	// NPCs
	private static final int LIANE = 32222;
	private static final int PANTHEON = 32972;
	private static final int KEKROPUS = 32138;
	private static final int EINDBURGH = 32469;
	// Location
	private static final Location MUSEUM = new Location(-114711, 243911, -7968);
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int JUDICATOR = 136;
	
	public Q00061_LawEnforcement()
	{
		super(61);
		addStartNpc(LIANE);
		addTalkId(LIANE, PANTHEON, KEKROPUS, EINDBURGH);
		addCondMinLevel(MIN_LEVEL, "32222-03.htm");
		addCondRace(Race.KAMAEL, "32222-02.htm");
		addCondClassId(ClassId.INSPECTOR, "32222-03.htm");
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
			case "32222-04.htm":
			case "32138-02.html":
			case "32138-03.html":
			case "32138-04.html":
			case "32138-05.html":
			case "32138-06.html":
			case "32138-07.html":
			case "32138-08.html":
			case "32469-02.html":
			case "32469-03.html":
			case "32469-04.html":
			case "32469-05.html":
			case "32469-06.html":
			case "32469-07.html":
			{
				htmltext = event;
				break;
			}
			case "32222-05.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "teleport":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					player.teleToLocation(MUSEUM);
				}
				break;
			}
			case "32138-09.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
				}
				break;
			}
			case "32469-08.html":
			case "32469-09.html":
			{
				if (qs.isCond(3))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						final ClassId newClassId = player.getClassId().getNextClassIds().stream().findFirst().orElse(null);
						if (newClassId != null)
						{
							final ClassId currentClassId = player.getClassId();
							
							if (!newClassId.childOf(currentClassId))
							{
								Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to cheat class transfer for Judicator!", Config.DEFAULT_PUNISH);
							}
							player.setClassId(JUDICATOR);
							player.broadcastUserInfo();
							giveAdena(player, 26000, true);
							qs.exitQuest(false, true);
							htmltext = event;
						}
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == LIANE)
				{
					htmltext = "32222-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case LIANE:
					{
						if (qs.isCond(1))
						{
							htmltext = "32222-06.html";
						}
						break;
					}
					case PANTHEON:
					{
						if (qs.isCond(1))
						{
							htmltext = "32972-01.html";
						}
						break;
					}
					case KEKROPUS:
					{
						if (qs.isCond(2))
						{
							htmltext = "32138-01.html";
						}
						else if (qs.isCond(3))
						{
							htmltext = "32138-10.html";
						}
						break;
					}
					case EINDBURGH:
					{
						if (qs.isCond(3))
						{
							htmltext = "32469-01.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
}
