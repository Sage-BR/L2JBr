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
package quests.Q10526_TheDarkSecretOfTheKetraOrcs;

import java.util.HashSet;
import java.util.Set;

import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.NpcLogListHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;

/**
 * The Dark Secret of the Ketra Orcs (10526)
 * @URL https://l2wiki.com/The_Dark_Secret_of_the_Ketra_Orcs
 * @author Gigi
 * @date 2017-11-20 - [20:03:04]
 */
public class Q10526_TheDarkSecretOfTheKetraOrcs extends Quest
{
	// NPCs
	private static final int LUGONNES = 33852;
	// Monsters
	private static final int KETRA_ORC_ELITE_SOLDIER = 21335;
	private static final int KETRA_ORC_CENTURION = 21336;
	private static final int KETRA_ORC_LIEUTENANT = 21332;
	private static final int KETRA_ORC_RAIDER = 21327;
	private static final int KETRA_ORC_WARRIOR = 21331;
	private static final int KETRA_ORC_SCOUT = 21328;
	private static final int KETRA_ORC_OFFICER = 21339;
	private static final int KETRA_ORC_BATTALION_COMMANDER = 21340;
	private static final int KETRA_ORC_HEAD_ROYAL_GUARD = 21346;
	
	private static final int KETRAS_PROPHET = 21347;
	private static final int KETRA_ORC_GRAND_PRIEST = 21342;
	private static final int KETRAS_HEAD_SHAMAN = 21345;
	private static final int KETRA_ORC_SHAMAN = 21329;
	private static final int KETRA_ORC_MEDIUM = 21334;
	
	private static final int KETRA_BACKUP_SHOOTER = 27511;
	private static final int KETRA_BACKUP_WIZARD = 27512;
	
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 80;
	
	public Q10526_TheDarkSecretOfTheKetraOrcs()
	{
		super(10526);
		addStartNpc(LUGONNES);
		addTalkId(LUGONNES);
		addKillId(KETRA_ORC_HEAD_ROYAL_GUARD, KETRA_ORC_WARRIOR, KETRA_ORC_MEDIUM, KETRA_BACKUP_SHOOTER, KETRA_ORC_SHAMAN, KETRAS_HEAD_SHAMAN, KETRA_BACKUP_WIZARD, KETRA_ORC_ELITE_SOLDIER, KETRA_ORC_CENTURION, KETRA_ORC_LIEUTENANT, KETRA_ORC_RAIDER, KETRAS_PROPHET, KETRA_ORC_SCOUT, KETRA_ORC_OFFICER, KETRA_ORC_BATTALION_COMMANDER, KETRA_ORC_GRAND_PRIEST);
		addCondRace(Race.ERTHEIA, "33852-00a.html");
		addCondStart(p -> p.isInCategory(CategoryType.MAGE_GROUP), "33852-00.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33852-00.htm");
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
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 492760460, 5519);
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
				htmltext = "33852-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33852-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33852-06.html";
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
			int killedShooter = qs.getInt("killed_" + KETRA_BACKUP_SHOOTER);
			int killedWizard = qs.getInt("killed_" + KETRA_BACKUP_WIZARD);
			
			switch (npc.getId())
			{
				case KETRA_ORC_ELITE_SOLDIER:
				case KETRA_ORC_CENTURION:
				case KETRA_ORC_LIEUTENANT:
				case KETRA_ORC_RAIDER:
				case KETRA_ORC_SCOUT:
				case KETRA_ORC_OFFICER:
				case KETRA_ORC_BATTALION_COMMANDER:
				case KETRA_ORC_HEAD_ROYAL_GUARD:
				case KETRA_ORC_WARRIOR:
				{
					final Npc mob = addSpawn(KETRA_BACKUP_SHOOTER, npc, false, 60000);
					mob.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_DARE_INTERFERE_WITH_EMBRYO_SURELY_YOU_WISH_FOR_DEATH);
					addAttackPlayerDesire(mob, killer);
					break;
				}
				
				case KETRAS_PROPHET:
				case KETRA_ORC_GRAND_PRIEST:
				case KETRA_ORC_SHAMAN:
				case KETRAS_HEAD_SHAMAN:
				case KETRA_ORC_MEDIUM:
				{
					final Npc mob = addSpawn(KETRA_BACKUP_WIZARD, npc, false, 60000);
					mob.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_DARE_INTERFERE_WITH_EMBRYO_SURELY_YOU_WISH_FOR_DEATH);
					addAttackPlayerDesire(mob, killer);
					break;
				}
				
				case KETRA_BACKUP_SHOOTER:
				{
					if (killedShooter < 100)
					{
						qs.set("killed_" + KETRA_BACKUP_SHOOTER, ++killedShooter);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case KETRA_BACKUP_WIZARD:
				{
					if (killedWizard < 100)
					{
						qs.set("killed_" + KETRA_BACKUP_WIZARD, ++killedWizard);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((killedShooter >= 100) && (killedWizard >= 100))
			{
				qs.setCond(2, true);
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
			final Set<NpcLogListHolder> holder = new HashSet<>(2);
			holder.add(new NpcLogListHolder(KETRA_BACKUP_SHOOTER, false, qs.getInt("killed_" + KETRA_BACKUP_SHOOTER)));
			holder.add(new NpcLogListHolder(KETRA_BACKUP_WIZARD, false, qs.getInt("killed_" + KETRA_BACKUP_WIZARD)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
