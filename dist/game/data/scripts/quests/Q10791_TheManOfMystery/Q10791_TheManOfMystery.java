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
package quests.Q10791_TheManOfMystery;

import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.FriendlyNpcInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.serverpackets.ExQuestNpcLogList;

import quests.Q10790_AMercenaryHelper.Q10790_AMercenaryHelper;

/**
 * The Man Of Mystery (10791)
 * @URL https://l2wiki.com/The_Man_of_Mystery
 * @author Gigi
 */
public class Q10791_TheManOfMystery extends Quest
{
	// NPCs
	private static final int DOKARA = 33847;
	private static final int KAIN_VAN_HALTER = 33993;
	// Monsters
	private static final int SUSPICIOUS_COCOON = 27536;
	private static final int SUSPICIOUS_COCOON1 = 27537;
	private static final int SUSPICIOUS_COCOON2 = 27538;
	private static final int NEEDLE_STAKATO_CAPTAIN = 27542;
	private static final int NEEDLE_STAKATO = 27543;
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 70;
	
	public Q10791_TheManOfMystery()
	{
		super(10791);
		addStartNpc(DOKARA);
		addTalkId(DOKARA, KAIN_VAN_HALTER);
		addFirstTalkId(KAIN_VAN_HALTER);
		addKillId(SUSPICIOUS_COCOON, SUSPICIOUS_COCOON1, SUSPICIOUS_COCOON2, NEEDLE_STAKATO_CAPTAIN);
		addAttackId(NEEDLE_STAKATO_CAPTAIN);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_level.html");
		addCondRace(Race.ERTHEIA, "noErtheia.html");
		addCondClassId(ClassId.MARAUDER, "no_quest.html");
		addCondCompletedQuest(Q10790_AMercenaryHelper.class.getSimpleName(), "restriction.html");
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
			case "33847-02.htm":
			case "33847-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33847-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "thank":
			{
				npc.deleteMe();
				htmltext = "33993-01.html";
				break;
			}
			default:
			{
				if (qs.isCond(3))
				{
					addExpAndSp(player, 46334481, 4072);
					giveStoryQuestReward(npc, player);
					qs.exitQuest(false, true);
					htmltext = "33847-07.html";
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
				htmltext = "33847-01.htm";
				break;
			}
			case State.STARTED:
			{
				if ((qs.getCond() > 0) && (qs.getCond() < 3))
				{
					htmltext = "33847-05.html";
				}
				else if (qs.isCond(3))
				{
					htmltext = "33847-06.html";
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
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "33993.html";
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		if (npc.isScriptValue(0))
		{
			for (int i = 0; i < 5; i++)
			{
				final Npc creature = addSpawn(NEEDLE_STAKATO, npc.getX() + getRandom(-20, 20), npc.getY() + getRandom(-20, 20), npc.getZ(), npc.getHeading(), true, 120000, false);
				addAttackPlayerDesire(creature, attacker);
				npc.setScriptValue(1);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, true);
		if ((qs != null) && (qs.getCond() > 0))
		{
			switch (npc.getId())
			{
				case SUSPICIOUS_COCOON:
				case SUSPICIOUS_COCOON1:
				case SUSPICIOUS_COCOON2:
				{
					int kills = qs.getInt(Integer.toString(SUSPICIOUS_COCOON));
					if (kills < 5)
					{
						kills++;
						qs.set(Integer.toString(SUSPICIOUS_COCOON), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					if (kills >= 5)
					{
						final Npc monster = addSpawn(NEEDLE_STAKATO_CAPTAIN, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 600000, false);
						final FriendlyNpcInstance kain = (FriendlyNpcInstance) addSpawn(KAIN_VAN_HALTER, killer.getX() + getRandom(-100, 100), killer.getY() + getRandom(-100, 100), killer.getZ(), 0, true, 300000, false);
						kain.setRunning();
						kain.setIsInvul(true);
						kain.reduceCurrentHp(1, monster, null); // TODO: Find better way for attack
						addAttackPlayerDesire(monster, killer);
						qs.setCond(2);
					}
					break;
				}
				case NEEDLE_STAKATO_CAPTAIN:
				{
					int kills = qs.getInt(Integer.toString(NEEDLE_STAKATO_CAPTAIN));
					if ((kills < 1) && qs.isCond(2))
					{
						kills++;
						qs.set(Integer.toString(NEEDLE_STAKATO_CAPTAIN), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					if (qs.getInt(Integer.toString(NEEDLE_STAKATO_CAPTAIN)) >= 1)
					{
						qs.setCond(1);
						qs.setCond(3, true);
					}
					break;
				}
			}
			final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
			log.addNpc(SUSPICIOUS_COCOON, qs.getInt(Integer.toString(SUSPICIOUS_COCOON)));
			log.addNpc(NEEDLE_STAKATO_CAPTAIN, qs.getInt(Integer.toString(NEEDLE_STAKATO_CAPTAIN)));
			qs.getPlayer().sendPacket(log);
		}
		return super.onKill(npc, killer, isSummon);
	}
}