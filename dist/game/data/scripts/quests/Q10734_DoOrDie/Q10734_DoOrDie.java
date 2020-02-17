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
package quests.Q10734_DoOrDie;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.ExTutorialShowId;

import quests.Q10733_TheTestForSurvival.Q10733_TheTestForSurvival;

/**
 * Do Or Die (10734)
 * @author Sdw
 */
public class Q10734_DoOrDie extends Quest
{
	// NPCs
	private static final int KATALIN = 33943;
	private static final int AYANTHE = 33942;
	private static final int ADVENTURER_S_GUIDE_APPRENTICE = 33950;
	private static final int TRAINING_DUMMY = 19546;
	// Skills
	private static final SkillHolder[] COMMON_BUFFS =
	{
		new SkillHolder(15642, 1), // Horn Melody (Adventurer)
		new SkillHolder(15645, 1), // Guitar Melody (Adventurer)
		new SkillHolder(15643, 1), // Drum Melody (Adventurer)
		new SkillHolder(15644, 1), // Pipe Organ Melody (Adventurer)
		new SkillHolder(15651, 1), // Prevailing Sonata (Adventurer)
		new SkillHolder(15652, 1), // Daring Sonata (Adventurer)
		new SkillHolder(15653, 1), // Refreshing Sonata (Adventurer)
	};
	private static final SkillHolder WARRIOR_HARMONY = new SkillHolder(15649, 1);
	private static final SkillHolder WIZARD_HARMONY = new SkillHolder(15650, 1);
	// Misc
	private static final int MAX_LEVEL = 20;
	
	public Q10734_DoOrDie()
	{
		super(10734);
		addStartNpc(KATALIN, AYANTHE);
		addTalkId(KATALIN, AYANTHE, ADVENTURER_S_GUIDE_APPRENTICE);
		addKillId(TRAINING_DUMMY);
		addCondRace(Race.ERTHEIA, "");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33942-02.htm":
			case "33943-02.htm":
			case "33950-02.html":
			{
				break;
			}
			case "33942-03.htm":
			case "33943-03.htm":
			{
				qs.startQuest();
				showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 10000);
				break;
			}
			case "other_buffs":
			{
				castBuffs(npc, player, "33950-06.html", "33950-04.html");
				htmltext = (player.isMageClass()) ? "33950-03.html" : "33950-05.html";
				player.sendPacket(new ExTutorialShowId(25));
				break;
			}
			case "buffs":
			{
				if (qs.isCond(4) || qs.isCond(5))
				{
					qs.setCond(6, true);
					showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 10000);
					htmltext = castBuffs(npc, player, "33950-06.html", "33950-04.html");
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
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
			case KATALIN:
			{
				if (!player.isMageClass())
				{
					switch (qs.getState())
					{
						case State.CREATED:
						{
							htmltext = (meetStartRestrictions(player)) ? "33943-01.htm" : "33943-08.htm";
							break;
						}
						case State.STARTED:
						{
							switch (qs.getCond())
							{
								case 1:
								{
									showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 10000);
									htmltext = "33943-04.html";
									break;
								}
								case 3:
								{
									showOnScreenMsg(player, NpcStringId.TALK_TO_THE_APPRENTICE_ADVENTURERS_GUIDE, ExShowScreenMessage.TOP_CENTER, 10000);
									qs.setCond(5, true);
									htmltext = "33943-05.html";
									break;
								}
								case 5:
								{
									showOnScreenMsg(player, NpcStringId.TALK_TO_THE_APPRENTICE_ADVENTURERS_GUIDE, ExShowScreenMessage.TOP_CENTER, 10000);
									htmltext = "33943-06.html";
									break;
								}
								case 8:
								{
									addExpAndSp(player, 966, 2);
									qs.exitQuest(false, true);
									htmltext = "33943-07.html";
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
				}
				break;
			}
			case AYANTHE:
			{
				if (player.isMageClass())
				{
					switch (qs.getState())
					{
						case State.CREATED:
						{
							htmltext = (meetStartRestrictions(player)) ? "33942-01.htm" : "33942-08.htm";
							break;
						}
						case State.STARTED:
						{
							switch (qs.getCond())
							{
								case 1:
								{
									showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 10000);
									htmltext = "33942-04.html";
									break;
								}
								case 2:
								{
									showOnScreenMsg(player, NpcStringId.TALK_TO_THE_APPRENTICE_ADVENTURERS_GUIDE, ExShowScreenMessage.TOP_CENTER, 10000);
									qs.setCond(4, true);
									htmltext = "33942-05.html";
									break;
								}
								case 4:
								{
									showOnScreenMsg(player, NpcStringId.TALK_TO_THE_APPRENTICE_ADVENTURERS_GUIDE, ExShowScreenMessage.TOP_CENTER, 10000);
									htmltext = "33942-06.html";
									break;
								}
								case 7:
								{
									addExpAndSp(player, 966, 2);
									qs.exitQuest(false, true);
									htmltext = "33942-07.html";
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
				}
				break;
			}
			case ADVENTURER_S_GUIDE_APPRENTICE:
			{
				if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 4:
						case 5:
						{
							htmltext = "33950-01.html";
							break;
						}
						case 6:
						{
							showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 10000);
							htmltext = castBuffs(npc, player, "33950-07.html", "33950-08.html");
							break;
						}
					}
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && (qs.isCond(1) || qs.isCond(6)))
		{
			final int nextCond = (killer.isMageClass()) ? (qs.getCond() + 1) : (qs.getCond() + 2);
			qs.setCond(nextCond, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private boolean meetStartRestrictions(PlayerInstance player)
	{
		final QuestState qs = player.getQuestState(Q10733_TheTestForSurvival.class.getSimpleName());
		return ((player.getLevel() < MAX_LEVEL) && (qs != null) && qs.isCompleted());
	}
	
	private String castBuffs(Npc npc, PlayerInstance player, String mage, String fighter)
	{
		for (SkillHolder skillHolder : COMMON_BUFFS)
		{
			npc.setTarget(player);
			npc.doCast(skillHolder.getSkill());
		}
		
		npc.setTarget(player);
		if (player.isMageClass())
		{
			npc.doCast(WIZARD_HARMONY.getSkill());
			return mage;
		}
		npc.doCast(WARRIOR_HARMONY.getSkill());
		return fighter;
	}
}
