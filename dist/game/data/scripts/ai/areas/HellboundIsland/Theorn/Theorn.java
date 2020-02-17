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
package ai.areas.HellboundIsland.Theorn;

import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.skills.AbnormalType;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.AbstractNpcAI;

/**
 * Theorn AI
 * @author Gigi, Mobius
 * @date 2017-10-26 - [22:17:03]
 */
public class Theorn extends AbstractNpcAI
{
	// NPC
	private static final int THERON = 33897;
	// Skills
	private static final int REWARD_BUFF_X2 = 16136;
	private static final int REWARD_BUFF_X4 = 16137;
	private static final int REWARD_BUFF_X8 = 16138;
	private static final int REWARD_BUFF_X16 = 16139;
	private static final int REWARD_BUFF_X32 = 16140;
	private static final Skill RESEARCH_SUCCESS_1 = SkillData.getInstance().getSkill(16141, 1);
	private static final Skill RESEARCH_SUCCESS_2 = SkillData.getInstance().getSkill(16142, 1);
	private static final Skill RESEARCH_SUCCESS_3 = SkillData.getInstance().getSkill(16143, 1);
	private static final Skill RESEARCH_SUCCESS_4 = SkillData.getInstance().getSkill(16144, 1);
	private static final Skill RESEARCH_SUCCESS_5 = SkillData.getInstance().getSkill(16145, 1);
	private static final Skill RESEARCH_FAIL = SkillData.getInstance().getSkill(16146, 1);
	
	private Theorn()
	{
		addStartNpc(THERON);
		addTalkId(THERON);
		addFirstTalkId(THERON);
		addSpawnId(THERON);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "BROADCAST_TEXT":
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_WILL_LEAVE_THIS_PLACE_ONCE_DAY_BREAKS_IF_YOU_WANT_A_REWARD_HURRY_UP_AND_GET_IT_BE_CAREFUL_NOT_TO_LOSE_THE_RESEARCH_REWARDS_IN_THE_VOID);
				startQuestTimer("BROADCAST_TEXT", (60 + getRandom(10)) * 3000, npc, null);
				break;
			}
			case "light_1":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_1, true);
				break;
			}
			case "darkness_1":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_1, false);
				break;
			}
			case "light_2":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_2, true);
				break;
			}
			case "darkness_2":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_2, false);
				break;
			}
			case "light_3":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_3, true);
				break;
			}
			case "darkness_3":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_3, false);
				break;
			}
			case "light_4":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_4, true);
				break;
			}
			case "darkness_4":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_4, false);
				break;
			}
			case "light_5":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_5, true);
				break;
			}
			case "darkness_5":
			{
				htmltext = tryLuck(npc, player, RESEARCH_SUCCESS_5, false);
				break;
			}
			case "stop_1":
			{
				if ((player != null) && !player.isDead() && (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X2) != null))
				{
					addExpAndSp(player, 0, 1000000);
					showOnScreenMsg(player, NpcStringId.YOU_HAVE_ACQUIRED_SP_X_2, ExShowScreenMessage.TOP_CENTER, 5000);
					player.getEffectList().stopEffects(AbnormalType.STAR_AGATHION_EXP_SP_BUFF1);
				}
				break;
			}
			case "stop_2":
			{
				if ((player != null) && !player.isDead() && (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X4) != null))
				{
					addExpAndSp(player, 0, 2000000);
					showOnScreenMsg(player, NpcStringId.YOU_HAVE_ACQUIRED_SP_X_4, ExShowScreenMessage.TOP_CENTER, 5000);
					player.getEffectList().stopEffects(AbnormalType.STAR_AGATHION_EXP_SP_BUFF1);
				}
				break;
			}
			case "stop_3":
			{
				if ((player != null) && !player.isDead() && (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X8) != null))
				{
					addExpAndSp(player, 0, 4000000);
					showOnScreenMsg(player, NpcStringId.YOU_HAVE_ACQUIRED_SP_X_8, ExShowScreenMessage.TOP_CENTER, 5000);
					player.getEffectList().stopEffects(AbnormalType.STAR_AGATHION_EXP_SP_BUFF1);
				}
				break;
			}
			case "stop_4":
			{
				if ((player != null) && !player.isDead() && (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X16) != null))
				{
					addExpAndSp(player, 0, 8000000);
					showOnScreenMsg(player, NpcStringId.YOU_HAVE_ACQUIRED_SP_X_16, ExShowScreenMessage.TOP_CENTER, 5000);
					player.getEffectList().stopEffects(AbnormalType.STAR_AGATHION_EXP_SP_BUFF1);
				}
				break;
			}
			case "stop_5":
			{
				if ((player != null) && !player.isDead() && (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X32) != null))
				{
					addExpAndSp(player, 0, 16000000);
					showOnScreenMsg(player, NpcStringId.S1_ACQUIRED_32_TIMES_THE_SKILL_POINTS_AS_A_REWARD, ExShowScreenMessage.TOP_CENTER, 5000, player.getName());
					player.getEffectList().stopEffects(AbnormalType.STAR_AGATHION_EXP_SP_BUFF1);
				}
				break;
			}
			case "REMOVE_BUFF":
			{
				player.getEffectList().stopEffects(AbnormalType.STAR_AGATHION_EXP_SP_BUFF1);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		if (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X2) != null)
		{
			return "33897_x2.html";
		}
		else if (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X4) != null)
		{
			return "33897_x4.html";
		}
		else if (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X8) != null)
		{
			return "33897_x8.html";
		}
		else if (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X16) != null)
		{
			return "33897_x16.html";
		}
		else if (player.getEffectList().getBuffInfoBySkillId(REWARD_BUFF_X32) != null)
		{
			return "33897_x32.html";
		}
		return "33897.html";
	}
	
	private String tryLuck(Npc npc, PlayerInstance player, Skill skill, boolean isLight)
	{
		if ((player.getSp() < 500000) || (player.getAdena() < 100000))
		{
			return "nosp.html";
		}
		takeItems(player, Inventory.ADENA_ID, 100000);
		player.setSp(player.getSp() - 500000);
		if (isLight ? getRandom(10) > 4 : getRandom(10) < 5)
		{
			SkillCaster.triggerCast(npc, player, skill);
			return null;
		}
		SkillCaster.triggerCast(npc, player, RESEARCH_FAIL);
		startQuestTimer("REMOVE_BUFF", 3000, npc, player);
		return null;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		startQuestTimer("BROADCAST_TEXT", 3000, npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Theorn();
	}
}