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
package ai.areas.TalkingIsland.Hardin;

import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.gameserver.data.xml.impl.ClassListData;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.data.xml.impl.SkillTreesData;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.enums.SubclassInfoType;
import org.l2jbr.gameserver.model.SkillLearn;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.olympiad.Olympiad;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2jbr.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.AbstractNpcAI;
import quests.Q10472_WindsOfFateEncroachingShadows.Q10472_WindsOfFateEncroachingShadows;

/**
 * Hardin (Agent of Chaos) AI.
 * @author Mobius
 */
public class Hardin extends AbstractNpcAI
{
	// NPC
	private static final int HARDIN = 33870;
	// Items
	private static final int CHAOS_ESSENCE = 36949;
	private static final int CHAOS_ESSENCE_DUAL_CLASS = 37494;
	private static final int CHAOS_POMANDER = 37374;
	private static final int CHAOS_POMANDER_DUAL_CLASS = 37375;
	
	private Hardin()
	{
		addStartNpc(HARDIN);
		addFirstTalkId(HARDIN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final String htmltext = getHtmlMessage(player);
		if (htmltext != null)
		{
			return htmltext;
		}
		
		if (event.equals("list"))
		{
			StringBuilder classes = new StringBuilder();
			for (ClassId c : ClassId.values())
			{
				if ((((c.level() != 4) && (c.getRace() != Race.ERTHEIA)) //
					|| (Config.HARDIN_ENABLE_ERTHEIAS && (c.getRace() == Race.ERTHEIA) && (c.level() != 3))) //
					|| (!Config.HARDIN_ENABLE_ERTHEIAS && (c.getRace() == Race.ERTHEIA)) //
					|| (c == player.getClassId()))
				{
					continue;
				}
				if (!player.isDualClassActive() || (player.isDualClassActive() && Config.HARDIN_ENABLE_DUALCLASS_CHECKS))
				{
					if (!Config.HARDIN_ENABLE_ALL_RACES && (c.getRace() != player.getClassId().getRace()))
					{
						continue;
					}
					if (!Config.HARDIN_ENABLE_ALL_SPECS && (c.isMage() != player.isMageClass()))
					{
						continue;
					}
					if (Config.HARDIN_SAME_AWAKEN_GROUP)
					{
						final String original = c.toString().contains("_") ? c.toString().substring(0, c.toString().indexOf("_") - 1) : c.toString();
						final String search = player.getClassId().toString().contains("_") ? player.getClassId().toString().substring(0, player.getClassId().toString().indexOf("_") - 1) : player.getClassId().toString();
						if (!original.equals(search))
						{
							continue;
						}
					}
					if (Config.HARDIN_RETAIL_LIMITATIONS)
					{
						if ((c == ClassId.TYRR_MAESTRO) && (player.getRace() != Race.DWARF))
						{
							continue;
						}
						if ((c == ClassId.ISS_DOMINATOR) && (player.getRace() != Race.ORC))
						{
							continue;
						}
					}
				}
				classes.append("<button value=\"");
				classes.append(ClassListData.getInstance().getClass(c.getId()).getClassName());
				classes.append("\" action=\"bypass -h Quest Hardin try_");
				classes.append(String.valueOf(c.getId()));
				classes.append("\" width=\"200\" height=\"31\" back=\"L2UI_CT1.HtmlWnd_DF_Awake_Down\" fore=\"L2UI_CT1.HtmlWnd_DF_Awake\"><br1>");
			}
			classes.append("<br><br><br><br><br>"); // prettify
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setHtml(getHtm(player, "33870-reawake_list.html"));
			html.replace("%CLASS_LIST%", classes.toString());
			player.sendPacket(html);
		}
		else if (event.contains("try"))
		{
			// Take item
			takeItems(player, player.isDualClassActive() ? CHAOS_ESSENCE_DUAL_CLASS : CHAOS_ESSENCE, 1);
			// Give item
			giveItems(player, player.isDualClassActive() ? CHAOS_POMANDER_DUAL_CLASS : CHAOS_POMANDER, 1);
			// Save original ClassId
			if (!player.isDualClassActive() && (player.getOriginalClass() == null))
			{
				player.setOriginalClass(player.getClassId());
			}
			// Ertheias can only be female
			final ClassId newClass = ClassId.getClassId(Integer.parseInt(event.replace("try_", "")));
			if ((newClass.getRace() == Race.ERTHEIA) && (player.getClassId().getRace() != Race.ERTHEIA) && !player.getAppearance().isFemale())
			{
				player.getAppearance().setFemale();
			}
			// Change class
			player.setClassId(newClass.getId());
			if (player.isDualClassActive())
			{
				player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
			}
			else
			{
				player.setBaseClass(player.getActiveClass());
			}
			// Adjustments
			SkillTreesData.getInstance().cleanSkillUponAwakening(player);
			for (SkillLearn skill : SkillTreesData.getInstance().getRaceSkillTree(player.getRace()))
			{
				player.addSkill(SkillData.getInstance().getSkill(skill.getSkillId(), skill.getSkillLevel()), true);
			}
			final List<Integer> removedSkillIds = Config.HARDIN_REMOVED_SKILLS.get(newClass.getId());
			if (removedSkillIds != null)
			{
				for (int skillId : removedSkillIds)
				{
					final Skill skill = player.getKnownSkill(skillId);
					if (skill != null)
					{
						player.removeSkill(skill);
					}
				}
			}
			player.store(false);
			player.broadcastUserInfo();
			player.sendSkillList();
			player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
			player.sendPacket(new ExUserInfoInvenWeight(player));
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final String htmltext = getHtmlMessage(player);
		return htmltext == null ? "33870-01.html" : htmltext;
	}
	
	private String getHtmlMessage(PlayerInstance player)
	{
		if (player.getRace() == Race.ERTHEIA)
		{
			final QuestState qs = player.getQuestState(Q10472_WindsOfFateEncroachingShadows.class.getSimpleName());
			if ((qs != null) && (qs.getCond() >= 7) && (qs.getCond() <= 17))
			{
				return "33870-03.html";
			}
			if (!Config.HARDIN_ENABLE_ERTHEIAS)
			{
				return "33870-02.html";
			}
		}
		if (!player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
		{
			return "33870-no.html";
		}
		if (player.isDualClassActive()) // dual class
		{
			if (!hasQuestItems(player, CHAOS_ESSENCE_DUAL_CLASS))
			{
				return "33870-no_already_reawakened.html";
			}
		}
		else if (player.isSubClassActive()) // subclass
		{
			return "33870-no.html";
		}
		else if (!hasQuestItems(player, CHAOS_ESSENCE)) // main class
		{
			return "33870-no_already_reawakened.html";
		}
		if (player.hasServitors())
		{
			return "33870-no_summon.html";
		}
		if (player.isInOlympiadMode() || (Olympiad.getInstance().getCompetitionDone(player.getObjectId()) > 0))
		{
			return "33870-no_olympiad.html";
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new Hardin();
	}
}