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
package quests.Q00753_ReactingToACrisis;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExQuestNpcLogList;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10386_MysteriousJourney.Q10386_MysteriousJourney;

/**
 * Uncover the Secret (753)
 * @URL https://l2wiki.com/Reacting_to_a_Crisis
 * @VIDEO http://www.dailymotion.com/video/x24y4lx_quest-reacting-to-a-crisis_videogames
 * @author Gigi
 */
public class Q00753_ReactingToACrisis extends Quest
{
	// Npc
	private static final int BERNA = 33796;
	// Monster's
	private static final int GOLEM_GENERATOR = 19296;
	private static final int BATTLE_GOLEM = 23269;
	private static final int[] MOBS =
	{
		23270, // Patrol Fighter
		23271, // Patrol Archer
		23272, // Spicula Fighter
		23273, // Spicula Archer
		23274, // Specula Elite Captain
		23275, // Spicula Captain
		23276 // Cheif Scout
	};
	// Items
	private static final int RED_GATE_KEY = 36054;
	private static final int VERNAS_VACCINE = 36065;
	private static final int SCROLL = 36082;
	// Skills;
	private static final int VACCINE = 9584;
	private static final double DAMAGE_BY_SKILL = 0.5d; // Percent
	// Misc
	private static final int MIN_LEVEL = 93;
	
	public Q00753_ReactingToACrisis()
	{
		super(753);
		addStartNpc(BERNA);
		addTalkId(BERNA);
		addKillId(GOLEM_GENERATOR);
		addKillId(MOBS);
		addSkillSeeId(GOLEM_GENERATOR);
		registerQuestItems(RED_GATE_KEY, VERNAS_VACCINE);
		addCondMinLevel(MIN_LEVEL, "lvl.htm");
		addCondCompletedQuest(Q10386_MysteriousJourney.class.getSimpleName(), "restriction.html");
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
			case "33796-02.htm":
			{
				qs.startQuest();
				giveItems(player, VERNAS_VACCINE, 1);
				htmltext = event;
				break;
			}
			case "33796-05.html":
			{
				giveItems(player, SCROLL, 1);
				addExpAndSp(player, 408665250, 98079);
				qs.exitQuest(QuestType.DAILY, true);
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
		
		if (npc.getId() == BERNA)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "33796-00.htm";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "33796-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33796-03.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33796-04.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance player, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		if (!npc.isDead() && (player.getTarget() == npc) && (skill.getId() == VACCINE))
		{
			final double dmg = npc.getMaxHp() * DAMAGE_BY_SKILL;
			npc.reduceCurrentHp(dmg, player, null);
		}
		return super.onSkillSee(npc, player, skill, targets, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (CommonUtil.contains(MOBS, npc.getId()))
		{
			if ((qs != null) && qs.isCond(1) && (giveItemRandomly(killer, RED_GATE_KEY, 1, 30, 0.2, true)))
			{
				qs.setMemoState(1);
			}
		}
		if ((npc.getId() == 23275) || (npc.getId() == 23276) || (npc.getId() == 23274))
		{
			if ((getRandom(100) < 10))
			{
				addSpawn(GOLEM_GENERATOR, npc.getX() + 30, npc.getY() + 30, npc.getZ(), 0, false, 60000);
				showOnScreenMsg(killer, NpcStringId.THE_GOLEM_GENERATOR_HAS_APPEARED, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		if ((qs != null) && qs.isCond(1) && (npc.getId() == GOLEM_GENERATOR))
		{
			int kills = qs.getInt(Integer.toString(GOLEM_GENERATOR));
			if (kills < 5)
			{
				kills++;
				qs.set(Integer.toString(GOLEM_GENERATOR), kills);
			}
			final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
			log.addNpcString(NpcStringId.USE_VACCINE_ON_GOLEM_GENERATOR, kills);
			killer.sendPacket(log);
			for (int i = 0; i < 4; i++)
			{
				final Npc mob = addSpawn(BATTLE_GOLEM, killer, true, 70000);
				addAttackPlayerDesire(mob, killer);
			}
		}
		if ((qs != null) && (qs.getInt(Integer.toString(GOLEM_GENERATOR)) >= 5) && (qs.isMemoState(1)))
		{
			takeItems(killer, VERNAS_VACCINE, -1);
			qs.setCond(2, true);
		}
		
		return super.onKill(npc, killer, isSummon);
	}
}