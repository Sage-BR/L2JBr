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
package quests.Q00754_AssistingTheRebelForces;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.serverpackets.ExQuestNpcLogList;

/**
 * @author hlwrave
 */
public class Q00754_AssistingTheRebelForces extends Quest
{
	// Items
	private static final int REBEL_SUPPLY_BOX = 35549;
	private static final int MARK_OF_RESISTANCE = 34909;
	// Npcs
	private static final int SIZRAK = 33669;
	private static final int COMMUNICATION = 33676;
	// Monsters
	private static final int KUNDA_GUARDIAN = 23224;
	private static final int KUNDA_BERSERKER = 23225;
	private static final int KUNDA_EXECUTOR = 23226;
	// Misc
	private static final int MIN_LEVEL = 97;
	private static final int KUNDA_GUARDIAN_KILL = 5;
	private static final int KUNDA_BERSERKER_KILL = 5;
	private static final int KUNDA_EXECUTOR_KILL = 5;
	
	public Q00754_AssistingTheRebelForces()
	{
		super(754);
		addStartNpc(SIZRAK);
		addTalkId(SIZRAK, COMMUNICATION);
		addKillId(KUNDA_GUARDIAN, KUNDA_BERSERKER, KUNDA_EXECUTOR);
		addCondMinLevel(MIN_LEVEL, "sofa_sizraku_q0754_05.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		if (event.equals("quest_accpted.htm"))
		{
			qs.startQuest();
			qs.set(Integer.toString(KUNDA_GUARDIAN), 0);
			qs.set(Integer.toString(KUNDA_BERSERKER), 0);
			qs.set(Integer.toString(KUNDA_EXECUTOR), 0);
			htmltext = "sofa_sizraku_q0754_04.html";
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
			case SIZRAK:
			{
				if (qs.isCreated())
				{
					htmltext = "sofa_sizraku_q0754_01.htm";
				}
				else if (qs.isCond(0))
				{
					htmltext = "sofa_sizraku_q0754_03.html";
					
				}
				else if (qs.isCond(1))
				{
					htmltext = "sofa_sizraku_q0754_07.html";
				}
				else if (qs.isCond(2))
				{
					addExpAndSp(player, 570676680, 261024840);
					giveItems(player, REBEL_SUPPLY_BOX, 1);
					giveItems(player, MARK_OF_RESISTANCE, 1);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = "sofa_sizraku_q0754_08.html";
				}
				else if (qs.isCompleted())
				{
					htmltext = "sofa_sizraku_q0754_06.html";
				}
				break;
			}
			case COMMUNICATION:
			{
				if (qs.isCond(2))
				{
					qs.getPlayer().addExpAndSp(570676680, 261024840);
					giveItems(player, REBEL_SUPPLY_BOX, 1);
					giveItems(player, MARK_OF_RESISTANCE, 1);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = "sofa_sizraku_q0754_08.html";
				}
				break;
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
				case KUNDA_GUARDIAN:
				{
					int kills = qs.getInt(Integer.toString(KUNDA_GUARDIAN));
					if (kills < KUNDA_GUARDIAN_KILL)
					{
						kills++;
						qs.set(Integer.toString(KUNDA_GUARDIAN), kills);
					}
					break;
				}
				case KUNDA_BERSERKER:
				{
					int kills = qs.getInt(Integer.toString(KUNDA_BERSERKER));
					if (kills < KUNDA_BERSERKER_KILL)
					{
						kills++;
						qs.set(Integer.toString(KUNDA_BERSERKER), kills);
					}
					break;
				}
				case KUNDA_EXECUTOR:
				{
					int kills = qs.getInt(Integer.toString(KUNDA_EXECUTOR));
					if (kills < KUNDA_EXECUTOR_KILL)
					{
						kills++;
						qs.set(Integer.toString(KUNDA_EXECUTOR), kills);
					}
					break;
				}
			}
			
			final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
			log.addNpc(KUNDA_GUARDIAN, qs.getInt(Integer.toString(KUNDA_GUARDIAN)));
			log.addNpc(KUNDA_BERSERKER, qs.getInt(Integer.toString(KUNDA_BERSERKER)));
			log.addNpc(KUNDA_EXECUTOR, qs.getInt(Integer.toString(KUNDA_EXECUTOR)));
			qs.getPlayer().sendPacket(log);
			
			if ((qs.getInt(Integer.toString(KUNDA_GUARDIAN)) >= KUNDA_GUARDIAN_KILL) && (qs.getInt(Integer.toString(KUNDA_BERSERKER)) >= KUNDA_BERSERKER_KILL) && (qs.getInt(Integer.toString(KUNDA_EXECUTOR)) >= KUNDA_EXECUTOR_KILL))
			{
				qs.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}