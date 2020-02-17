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
package quests.Q10425_TheKetraOrcSupporters;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

/**
 * The Ketra Orc Supporters (10425)
 * @author Stayway
 */
public class Q10425_TheKetraOrcSupporters extends Quest
{
	// NPCs
	private static final int LUGONNES = 33852;
	private static final int EMBRYO_SHOOTER = 27511;
	private static final int EMBRYO_WIZARD = 27512;
	private static final int[] SHOOTER_MONSTERS =
	{
		21327, // Ketra Orc Raider
		21331, // Ketra Orc Warrior
		21332, // Ketra Orc Lieutenant
		21335, // Ketra Orc Elite Soldier
		21336, // Ketra Orc White Captain
		21339, // Ketra Orc Officer
		21340, // Ketra Orc Battalion Commander
		27511, // Ketra Backup Shooter
	};
	private static final int[] WIZARD_MONSTERS =
	{
		21334, // Ketra Orc Medium
		21338, // Ketra Orc Seer
		21342, // Ketra Orc Grand Priest
		27512, // Varka Backup Wizard
	};
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 80;
	
	public Q10425_TheKetraOrcSupporters()
	{
		super(10425);
		addStartNpc(LUGONNES);
		addTalkId(LUGONNES);
		addKillId(SHOOTER_MONSTERS);
		addKillId(WIZARD_MONSTERS);
		addCondNotRace(Race.ERTHEIA, "33852-09.html");
		addCondInCategory(CategoryType.WIZARD_GROUP, "33852-08.html");
		addCondMinLevel(MIN_LEVEL, "33852-08.html");
		addCondMaxLevel(MAX_LEVEL, "33852-08.html");
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
			case "33852-02.htm":
			case "33852-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33852-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33852-07.html":
			{
				if (qs.isCond(2))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(npc, player);
					if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL))
					{
						addExpAndSp(player, 492760460, 5519);
					}
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = null;
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33852-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = qs.isCond(1) ? "33852-05.html" : "33852-06.html";
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
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isCond(1))
		{
			if ((npc.getId() == EMBRYO_SHOOTER) || (npc.getId() == EMBRYO_WIZARD))
			{
				int shooterCount = qs.getInt("KillCount_" + EMBRYO_SHOOTER);
				int wizardCount = qs.getInt("KillCount_" + EMBRYO_WIZARD);
				
				if (npc.getId() == EMBRYO_SHOOTER)
				{
					if (shooterCount < 100)
					{
						qs.set("KillCount_" + EMBRYO_SHOOTER, ++shooterCount);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				else if (wizardCount < 100)
				{
					qs.set("KillCount_" + EMBRYO_WIZARD, ++wizardCount);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				
				if ((shooterCount >= 100) && (wizardCount >= 100))
				{
					qs.setCond(2, true);
				}
			}
			else if (CommonUtil.contains(WIZARD_MONSTERS, npc.getId()))
			{
				if (qs.getInt("KillCount_" + EMBRYO_WIZARD) < 100)
				{
					final Npc embryo = addSpawn(EMBRYO_WIZARD, npc, false, 60000);
					addAttackPlayerDesire(embryo, killer);
					embryo.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_DARE_INTERFERE_WITH_EMBRYO_SURELY_YOU_WISH_FOR_DEATH);
				}
			}
			else if (qs.getInt("KillCount_" + EMBRYO_SHOOTER) < 100)
			{
				final Npc embryo = addSpawn(EMBRYO_SHOOTER, npc, false, 60000);
				addAttackPlayerDesire(embryo, killer);
				embryo.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_DARE_INTERFERE_WITH_EMBRYO_SURELY_YOU_WISH_FOR_DEATH);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(2);
			npcLogList.add(new NpcLogListHolder(EMBRYO_SHOOTER, false, qs.getInt("KillCount_" + EMBRYO_SHOOTER)));
			npcLogList.add(new NpcLogListHolder(EMBRYO_WIZARD, false, qs.getInt("KillCount_" + EMBRYO_WIZARD)));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}