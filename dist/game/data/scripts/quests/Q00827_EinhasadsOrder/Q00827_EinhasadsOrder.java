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
package quests.Q00827_EinhasadsOrder;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Einhasad's Order (827)
 * @URL https://l2wiki.com/Einhasad%27s_Order
 * @author Liamxroy
 */
public class Q00827_EinhasadsOrder extends Quest
{
	// NPC
	private static final int SIR_KLAUS_VASPER = 34096;
	private static final int SIR_KLAUS_VASPER_FINISH = 34151;
	private static final int[] UNIT_ELITE_SOLDIER =
	{
		23616, // Unit 1 Elite Soldier
		23617, // Unit 2 Elite Soldier
		23618, // Unit 3 Elite Soldier
		23619, // Unit 4 Elite Soldier
		23620, // Unit 5 Elite Soldier
		23621, // Unit 6 Elite Soldier
		23622, // Unit 7 Elite Soldier
		23623, // Unit 8 Elite Soldier
		23624, // Unit 1 Elite Soldier
		23625, // Unit 2 Elite Soldier
		23626, // Unit 3 Elite Soldier
		23627, // Unit 4 Elite Soldier
		23628, // Unit 5 Elite Soldier
		23629, // Unit 6 Elite Soldier
		23630, // Unit 7 Elite Soldier
		23631, // Unit 8 Elite Soldier
		23632, // Unit 1 Elite Soldier
		23633, // Unit 2 Elite Soldier
		23634, // Unit 3 Elite Soldier
		23635, // Unit 4 Elite Soldier
		23636, // Unit 5 Elite Soldier
		23637, // Unit 6 Elite Soldier
		23638, // Unit 7 Elite Soldier
		23639, // Unit 8 Elite Soldier
		23640, // Unit 1 Elite Soldier
		23641, // Unit 2 Elite Soldier
		23642, // Unit 3 Elite Soldier
		23643, // Unit 4 Elite Soldier
		23644, // Unit 5 Elite Soldier
		23645, // Unit 6 Elite Soldier
		23646, // Unit 7 Elite Soldier
		23647, // Unit 8 Elite Soldier
	};
	// Items
	private static final int REVOLUTIONARIES_MARK_PIECE = 46372;
	private static final int GLUDIN_HERO_REWARD = 46375;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q00827_EinhasadsOrder()
	{
		super(827);
		addStartNpc(SIR_KLAUS_VASPER);
		addTalkId(SIR_KLAUS_VASPER, SIR_KLAUS_VASPER_FINISH);
		addKillId(UNIT_ELITE_SOLDIER);
		addCondMinLevel(MIN_LEVEL, "34096-00.htm");
		registerQuestItems(REVOLUTIONARIES_MARK_PIECE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "34096-02.htm":
			case "34096-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34096-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34151-02.html":
			{
				if (qs.isCond(2))
				{
					takeItems(player, -1, REVOLUTIONARIES_MARK_PIECE);
					rewardItems(player, GLUDIN_HERO_REWARD, 1);
					addExpAndSp(player, 2175228000L, 5220534);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
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
				if (npc.getId() == SIR_KLAUS_VASPER)
				{
					htmltext = "34096-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == SIR_KLAUS_VASPER)
				{
					htmltext = "34096-05.html";
				}
				else
				{
					htmltext = "34151-01.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable() && (npc.getId() == SIR_KLAUS_VASPER))
				{
					qs.setState(State.CREATED);
					htmltext = "34096-01.htm";
				}
				else
				{
					htmltext = "34096-06.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		List<PlayerInstance> members = new ArrayList<>();
		if (player.getParty() != null)
		{
			members = player.getParty().getMembers();
		}
		else
		{
			members.add(player);
		}
		for (PlayerInstance member : members)
		{
			final QuestState qs = getQuestState(member, false);
			if ((qs != null) && qs.isCond(1) && member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
			{
				if (giveItemRandomly(member, npc, REVOLUTIONARIES_MARK_PIECE, 1, 30, 1.0, true))
				{
					qs.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
