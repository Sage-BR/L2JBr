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
package quests.Q00144_PailakaInjuredDragon;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.instancemanager.InstanceManager;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * @author Sdw
 */
public class Q00144_PailakaInjuredDragon extends Quest
{
	// NPCs
	private static final int KETRA_ORC_SHAMAN = 32499;
	private static final int KETRA_ORC_SUPPORTER = 32502;
	private static final int KETRA_ORC_INTELLIGENCE_OFFICIER = 32509;
	private static final int KETRA_ORC_SUPPORTER_2 = 32512;
	private static final int LATANA = 18660;
	// Items
	private static final int SPEAR_OF_SILENOS = 13052;
	private static final int SPEAR_OF_SILENOS_REINFORCED = 13053;
	private static final int SPEAR_OF_SILENOS_COMPLETED = 13054;
	private static final int WEAPON_UPGRADE_STAGE_1 = 13056;
	private static final int WEAPON_UPGRADE_STAGE_2 = 13057;
	private static final int PAILAKA_INSTANT_SHIELD = 13032;
	private static final int QUICK_HEALING_POTION = 13033;
	private static final int SCROLL_OF_ESCAPE = 736;
	private static final ItemHolder PAILAKA_SHIRT = new ItemHolder(13296, 1);
	// Skills
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(1086, 2),
		new SkillHolder(1204, 2),
		new SkillHolder(1059, 3),
		new SkillHolder(1085, 3),
		new SkillHolder(1078, 6),
		new SkillHolder(1068, 3),
		new SkillHolder(1240, 3),
		new SkillHolder(1077, 3),
		new SkillHolder(1242, 3),
		new SkillHolder(1062, 2),
		new SkillHolder(1268, 4),
		new SkillHolder(1045, 6),
	};
	// Misc
	private static final int MIN_LEVEL = 73;
	private static final int MAX_LEVEL = 77;
	
	public Q00144_PailakaInjuredDragon()
	{
		super(144);
		addStartNpc(KETRA_ORC_SHAMAN);
		addFirstTalkId(KETRA_ORC_INTELLIGENCE_OFFICIER, KETRA_ORC_SUPPORTER_2);
		addTalkId(KETRA_ORC_SHAMAN, KETRA_ORC_SUPPORTER, KETRA_ORC_INTELLIGENCE_OFFICIER);
		addKillId(LATANA);
		addCondMinLevel(MIN_LEVEL, "32499-03.html");
		addCondMaxLevel(MAX_LEVEL, "32499-04.html");
		registerQuestItems(SPEAR_OF_SILENOS, SPEAR_OF_SILENOS_REINFORCED, SPEAR_OF_SILENOS_COMPLETED, WEAPON_UPGRADE_STAGE_1, WEAPON_UPGRADE_STAGE_2, PAILAKA_INSTANT_SHIELD, QUICK_HEALING_POTION);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		if (npc.getId() == KETRA_ORC_INTELLIGENCE_OFFICIER)
		{
			return "32509-01.html";
		}
		else if (npc.getId() == KETRA_ORC_SUPPORTER_2)
		{
			final QuestState qs = getQuestState(player, false);
			if (qs != null)
			{
				return qs.isCompleted() ? "32512-02.html" : "32512-01.html";
			}
		}
		return null;
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
			case "32499-05.html":
			case "32499-06.html":
			case "32499-07.html":
			case "32499-11.html":
			case "32502-02.html":
			case "32502-03.html":
			case "32502-04.html":
			case "32502-07.html":
			{
				htmltext = event;
				break;
			}
			case "32502-05.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					giveItems(player, SPEAR_OF_SILENOS, 1);
					htmltext = event;
				}
				break;
			}
			case "32499-08.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "upgrade_weapon":
			{
				if (hasQuestItems(player, SPEAR_OF_SILENOS_COMPLETED))
				{
					htmltext = "32509-06.html";
				}
				else if (hasQuestItems(player, SPEAR_OF_SILENOS))
				{
					if (hasQuestItems(player, WEAPON_UPGRADE_STAGE_1))
					{
						takeItems(player, SPEAR_OF_SILENOS, -1);
						takeItems(player, WEAPON_UPGRADE_STAGE_1, -1);
						giveItems(player, SPEAR_OF_SILENOS_REINFORCED, 1, true);
						htmltext = "32509-02.html";
					}
					else
					{
						htmltext = "32509-05.html";
					}
				}
				else if (hasQuestItems(player, SPEAR_OF_SILENOS_REINFORCED))
				{
					if (hasQuestItems(player, WEAPON_UPGRADE_STAGE_2))
					{
						takeItems(player, SPEAR_OF_SILENOS_REINFORCED, -1);
						takeItems(player, WEAPON_UPGRADE_STAGE_2, -1);
						giveItems(player, SPEAR_OF_SILENOS_COMPLETED, 1, true);
						htmltext = "32509-08.html";
					}
					else
					{
						htmltext = "32509-04.html";
					}
				}
				else
				{
					htmltext = "32509-01a.html";
				}
				break;
			}
			case "ask_buff":
			{
				htmltext = npc.isScriptValue(1) ? "32509-07.html" : "32509-08.html";
				break;
			}
			case "32512-03.html":
			{
				if (qs.isCond(4))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 24570000, 5896);
						giveAdena(player, 798840, true);
						giveItems(player, PAILAKA_SHIRT);
						giveItems(player, SCROLL_OF_ESCAPE, 1); // Not a reward.
						qs.exitQuest(false, true);
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
					final Instance inst = InstanceManager.getInstance().getPlayerInstance(player, true);
					if (inst != null)
					{
						inst.finishInstance();
					}
				}
				break;
			}
		}
		if (event.startsWith("buff"))
		{
			if (npc.isScriptValue(0))
			{
				final int currentBuffCount = npc.getVariables().getInt("buff_count");
				if (currentBuffCount < 5)
				{
					final int buffOffset = CommonUtil.constrain(Integer.parseInt(event.substring(event.indexOf(" ") + 1)), 0, BUFFS.length);
					npc.setTarget(player);
					npc.doCast(BUFFS[buffOffset].getSkill());
					npc.getVariables().set("buff_count", currentBuffCount + 1);
					htmltext = "32509-10.html";
					if ((currentBuffCount + 1) >= 5)
					{
						htmltext = "32509-09.html";
						npc.setScriptValue(1);
					}
				}
				else
				{
					htmltext = "32509-07.html";
					npc.setScriptValue(1);
				}
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
				if (npc.getId() == KETRA_ORC_SHAMAN)
				{
					htmltext = "32499-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == KETRA_ORC_SHAMAN)
				{
					if (qs.isCond(1))
					{
						htmltext = "32499-09.html";
					}
					else
					{
						htmltext = "32499-10.html";
					}
				}
				else if (npc.getId() == KETRA_ORC_SUPPORTER)
				{
					if (qs.isCond(2))
					{
						htmltext = "32502-01.html";
					}
					else
					{
						htmltext = "32502-06.html";
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "32499-02.html";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null && qs.isCond(3))
		{
			qs.setCond(4, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
