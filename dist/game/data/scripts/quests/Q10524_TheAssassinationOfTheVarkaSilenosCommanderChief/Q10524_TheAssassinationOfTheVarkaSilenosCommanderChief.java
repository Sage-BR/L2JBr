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
package quests.Q10524_TheAssassinationOfTheVarkaSilenosCommanderChief;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

import quests.Q10523_TheAssassinationOfTheVarkaSilenosCommander.Q10523_TheAssassinationOfTheVarkaSilenosCommander;

/**
 * The Assassination of the Varka Silenos Commander Chief (10524)
 * @author Gigi
 * @date 2017-11-18 - [14:37:41]
 */
public class Q10524_TheAssassinationOfTheVarkaSilenosCommanderChief extends Quest
{
	// NPCs
	private static final int HANSEN = 33853;
	// Monsters
	private static final int VARKAS_CHIEF_HORUS = 27503;
	private static final int KAMPF = 27516;
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 80;
	
	public Q10524_TheAssassinationOfTheVarkaSilenosCommanderChief()
	{
		super(10524);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addKillId(VARKAS_CHIEF_HORUS);
		addCondRace(Race.ERTHEIA, "33853-00.html");
		addCondStart(p -> p.isInCategory(CategoryType.FIGHTER_GROUP), "33853-00a.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33853-00.html");
		addCondCompletedQuest(Q10523_TheAssassinationOfTheVarkaSilenosCommander.class.getSimpleName(), "33853-00.html");
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
			case "33853-02.htm":
			case "33853-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33853-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33853-07.html":
			{
				if (qs.isCond(2))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 351479151, 1839);
						qs.exitQuest(QuestType.ONE_TIME, true);
						htmltext = event;
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
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33853-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33853-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33853-06.html";
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
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, true);
		if ((qs != null) && qs.isCond(1))
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.COME_KAMPF_PROTECT_ME);
			final Npc mob = addSpawn(KAMPF, npc, false, 120000);
			addAttackPlayerDesire(mob, killer);
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
