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
package quests.Q00468_BeLostInTheMysteriousScent;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.serverpackets.ExQuestNpcLogList;

/**
 * Be Lost in the Mysterious Scent (468)
 * @URL https://l2wiki.com/Be_Lost_in_the_Mysterious_Scent
 * @author Gigi
 */
public class Q00468_BeLostInTheMysteriousScent extends Quest
{
	// NPCs
	private static final int SELINA = 33032;
	private static final int MOON_GARDEN_MANAGER = 22958;
	private static final int GARDEN_PROTECTOR = 22959;
	private static final int GARDEN_COMMANDER = 22962;
	private static final int MOON_GARDENER = 22960;
	// Item
	private static final int CERTIFICATE_OF_LIFE = 30385;
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q00468_BeLostInTheMysteriousScent()
	{
		super(468);
		addStartNpc(SELINA);
		addTalkId(SELINA);
		addKillId(MOON_GARDEN_MANAGER, GARDEN_PROTECTOR, GARDEN_COMMANDER, MOON_GARDENER);
		addCondMinLevel(MIN_LEVEL, "32892-00a.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		switch (event)
		{
			case "33032-02.htm":
			{
				htmltext = event;
				break;
			}
			case "33032-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33032-06.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, CERTIFICATE_OF_LIFE, 2);
					qs.exitQuest(QuestType.DAILY, true);
				}
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
		if (npc.getId() == SELINA)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "32892-00.html";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "33032-01.htm";
					qs.isStarted();
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33032-04.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33032-05.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case MOON_GARDEN_MANAGER:
				{
					int kills = qs.getInt(Integer.toString(MOON_GARDEN_MANAGER));
					if (kills < 10)
					{
						kills++;
						qs.set(Integer.toString(MOON_GARDEN_MANAGER), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case GARDEN_PROTECTOR:
				{
					int kills = qs.getInt(Integer.toString(GARDEN_PROTECTOR));
					if (kills < 10)
					{
						kills++;
						qs.set(Integer.toString(GARDEN_PROTECTOR), kills);
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
				case MOON_GARDENER:
				{
					int kills = qs.getInt(Integer.toString(MOON_GARDENER));
					if (kills < 10)
					{
						kills++;
						qs.set(Integer.toString(MOON_GARDENER), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
			log.addNpc(MOON_GARDEN_MANAGER, qs.getInt(Integer.toString(MOON_GARDEN_MANAGER)));
			log.addNpc(GARDEN_PROTECTOR, qs.getInt(Integer.toString(GARDEN_PROTECTOR)));
			log.addNpc(GARDEN_COMMANDER, qs.getInt(Integer.toString(GARDEN_COMMANDER)));
			log.addNpc(MOON_GARDENER, qs.getInt(Integer.toString(MOON_GARDENER)));
			qs.getPlayer().sendPacket(log);
			
			if ((qs.getInt(Integer.toString(MOON_GARDEN_MANAGER)) >= 10) && (qs.getInt(Integer.toString(GARDEN_PROTECTOR)) >= 10) && (qs.getInt(Integer.toString(GARDEN_COMMANDER)) >= 10) && (qs.getInt(Integer.toString(MOON_GARDENER)) >= 10))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}