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
package quests.Q10310_TwistedCreationTree;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.serverpackets.ExQuestNpcLogList;

import quests.Q10302_UnsettlingShadowAndRumors.Q10302_UnsettlingShadowAndRumors;

/**
 * Twisted Creation Tree (10310)
 * @URL https://l2wiki.com/Creation_of_Twisted_Spiral
 * @author Gigi
 */
public class Q10310_TwistedCreationTree extends Quest
{
	// NPCs
	private static final int SELINA = 33032;
	private static final int GORFINA = 33031;
	// Monsters
	private static final int GARDEN_SENTRY = 22947;
	private static final int GARDEN_SCOUT = 22948;
	private static final int GARDEN_COMMANDER = 22949;
	private static final int OUTDOOR_GARDENER = 22950;
	private static final int GARDEN_DESTROYER = 22951;
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q10310_TwistedCreationTree()
	{
		super(10310);
		addStartNpc(SELINA);
		addTalkId(SELINA, GORFINA);
		addKillId(GARDEN_SENTRY, GARDEN_SCOUT, GARDEN_COMMANDER, OUTDOOR_GARDENER, GARDEN_DESTROYER);
		addCondMinLevel(MIN_LEVEL, "33032-00.htm");
		addCondCompletedQuest(Q10302_UnsettlingShadowAndRumors.class.getSimpleName(), "33032-00.htm");
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
			case "33032-02.htm":
			case "33032-03.htm":
			case "33032-07.html":
			case "33031-02.html":
			case "33031-06.html":
			{
				htmltext = event;
				break;
			}
			case "33032-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33031-03.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			default:
			{
				if (qs.isCond(3) && event.startsWith("giveReward_"))
				{
					final int itemId = Integer.parseInt(event.replace("giveReward_", ""));
					qs.exitQuest(false, true);
					giveAdena(player, 3424540, false);
					giveItems(player, itemId, 11);
					addExpAndSp(player, 50178765, 12042);
					htmltext = "33031-07.html";
				}
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == SELINA)
				{
					htmltext = "33032-01.htm";
					break;
				}
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SELINA:
					{
						if (qs.isCond(1))
						{
							htmltext = "33032-05.html";
						}
						else if (qs.getCond() > 1)
						{
							htmltext = "33032-06.html";
						}
						break;
					}
					case GORFINA:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "33031-01.html";
								break;
							}
							case 2:
							{
								htmltext = "33031-04.html";
								break;
							}
							case 3:
							{
								htmltext = "33031-05.html";
								break;
							}
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "complete.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 2, 3, npc);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case GARDEN_SENTRY:
				{
					int kills = qs.getInt(Integer.toString(GARDEN_SENTRY));
					if (kills < 10)
					{
						kills++;
						qs.set(Integer.toString(GARDEN_SENTRY), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case GARDEN_SCOUT:
				{
					int kills = qs.getInt(Integer.toString(GARDEN_SCOUT));
					if (kills < 10)
					{
						kills++;
						qs.set(Integer.toString(GARDEN_SCOUT), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case GARDEN_COMMANDER:
				{
					int kills = qs.getInt(Integer.toString(GARDEN_COMMANDER));
					if (kills < 10)
					{
						kills++;
						qs.set(Integer.toString(GARDEN_COMMANDER), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case OUTDOOR_GARDENER:
				{
					int kills = qs.getInt(Integer.toString(OUTDOOR_GARDENER));
					if (kills < 10)
					{
						kills++;
						qs.set(Integer.toString(OUTDOOR_GARDENER), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case GARDEN_DESTROYER:
				{
					int kills = qs.getInt(Integer.toString(GARDEN_DESTROYER));
					if (kills < 10)
					{
						kills++;
						qs.set(Integer.toString(GARDEN_DESTROYER), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
			log.addNpc(GARDEN_SENTRY, qs.getInt(Integer.toString(GARDEN_SENTRY)));
			log.addNpc(GARDEN_SCOUT, qs.getInt(Integer.toString(GARDEN_SCOUT)));
			log.addNpc(GARDEN_COMMANDER, qs.getInt(Integer.toString(GARDEN_COMMANDER)));
			log.addNpc(OUTDOOR_GARDENER, qs.getInt(Integer.toString(OUTDOOR_GARDENER)));
			log.addNpc(GARDEN_DESTROYER, qs.getInt(Integer.toString(GARDEN_DESTROYER)));
			qs.getPlayer().sendPacket(log);
			if ((qs.getInt(Integer.toString(GARDEN_SENTRY)) >= 10) && (qs.getInt(Integer.toString(GARDEN_SCOUT)) >= 10) && (qs.getInt(Integer.toString(GARDEN_COMMANDER)) >= 10) && (qs.getInt(Integer.toString(GARDEN_DESTROYER)) >= 10) && (qs.getInt(Integer.toString(GARDEN_DESTROYER)) >= 10))
			{
				qs.setCond(3, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}