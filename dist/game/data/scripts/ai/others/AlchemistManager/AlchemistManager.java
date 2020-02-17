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
package ai.others.AlchemistManager;

import java.util.List;

import org.l2jbr.gameserver.data.xml.impl.SkillTreesData;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.SkillLearn;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.base.AcquireSkillType;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ExAcquirableSkillListByClass;
import org.l2jbr.gameserver.network.serverpackets.ExTutorialShowId;

import ai.AbstractNpcAI;

/**
 * Alchemist Manager AI.
 * @author Sdw
 */
public class AlchemistManager extends AbstractNpcAI
{
	// NPCs
	private static final int ALCHEMISTS[] =
	{
		33978, // Zephyra
		33977, // Veruti
	};
	
	private AlchemistManager()
	{
		addStartNpc(ALCHEMISTS);
		addTalkId(ALCHEMISTS);
		addFirstTalkId(ALCHEMISTS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33978.html":
			case "33977.html":
			{
				htmltext = event;
				break;
			}
			case "open_tutorial":
			{
				player.sendPacket(new ExTutorialShowId(26));
				htmltext = npc.getId() + "-1.html";
				break;
			}
			case "learn_skill":
			{
				if (player.getRace() == Race.ERTHEIA)
				{
					final List<SkillLearn> alchemySkills = SkillTreesData.getInstance().getAvailableAlchemySkills(player);
					
					if (alchemySkills.isEmpty())
					{
						player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
					}
					else
					{
						player.sendPacket(new ExAcquirableSkillListByClass(alchemySkills, AcquireSkillType.ALCHEMY));
					}
				}
				else
				{
					htmltext = npc.getId() + "-2.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new AlchemistManager();
	}
}