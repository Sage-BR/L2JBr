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
package events.HungryHorse;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.quest.LongTimeEvent;
import org.l2jbr.gameserver.model.skills.SkillCaster;

/**
 * Hungry Horse
 * @URL http://www.lineage2.com/en/news/events/hungry-horse-event-09192017.php
 * @author Mobius
 */
public class HungryHorse extends LongTimeEvent
{
	// NPC
	private static final int GALUP = 34010;
	// Items
	private static final int CARROT = 40363;
	private static final int POUCH = 40365;
	// Skills
	private static final SkillHolder[] GROUP_BUFFS =
	{
		new SkillHolder(15642, 1), // Horn Melody
		new SkillHolder(15643, 1), // Drum Melody
		new SkillHolder(15644, 1), // Pipe Organ Melody
		new SkillHolder(15645, 1), // Guitar Melody
		new SkillHolder(15651, 1), // Prevailing Sonata
		new SkillHolder(15652, 1), // Daring Sonata
		new SkillHolder(15653, 1), // Refreshing Sonata
	};
	private static final SkillHolder KNIGHT = new SkillHolder(15648, 1); // Knight's Harmony
	private static final SkillHolder WARRIOR = new SkillHolder(15649, 1); // Warrior's Harmony
	private static final SkillHolder WIZARD = new SkillHolder(15650, 1); // Wizard's Harmony
	private static final SkillHolder XP_BUFF = new SkillHolder(19036, 1); // Blessing of Light
	
	private HungryHorse()
	{
		addStartNpc(GALUP);
		addFirstTalkId(GALUP);
		addTalkId(GALUP);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34010-01.htm":
			case "34010-02.htm":
			{
				htmltext = event;
				break;
			}
			case "knight":
			{
				if (getQuestItemsCount(player, CARROT) >= 7)
				{
					takeItems(player, CARROT, 7);
					for (SkillHolder holder : GROUP_BUFFS)
					{
						SkillCaster.triggerCast(npc, player, holder.getSkill());
					}
					SkillCaster.triggerCast(npc, player, KNIGHT.getSkill());
					SkillCaster.triggerCast(npc, player, XP_BUFF.getSkill());
				}
				else
				{
					htmltext = "34010-03.htm";
				}
				break;
			}
			case "warrior":
			{
				if (getQuestItemsCount(player, CARROT) >= 7)
				{
					takeItems(player, CARROT, 7);
					for (SkillHolder holder : GROUP_BUFFS)
					{
						SkillCaster.triggerCast(npc, player, holder.getSkill());
					}
					SkillCaster.triggerCast(npc, player, WARRIOR.getSkill());
					SkillCaster.triggerCast(npc, player, XP_BUFF.getSkill());
				}
				else
				{
					htmltext = "34010-03.htm";
				}
				break;
			}
			case "wizard":
			{
				if (getQuestItemsCount(player, CARROT) >= 7)
				{
					takeItems(player, CARROT, 7);
					for (SkillHolder holder : GROUP_BUFFS)
					{
						SkillCaster.triggerCast(npc, player, holder.getSkill());
					}
					SkillCaster.triggerCast(npc, player, WIZARD.getSkill());
					SkillCaster.triggerCast(npc, player, XP_BUFF.getSkill());
				}
				else
				{
					htmltext = "34010-03.htm";
				}
				break;
			}
			case "giveCarrots40":
			{
				if (getQuestItemsCount(player, CARROT) >= 40)
				{
					takeItems(player, CARROT, 40);
					giveItems(player, POUCH, 1);
					htmltext = "34010-04.htm";
				}
				else
				{
					htmltext = "34010-03.htm";
				}
				break;
			}
			case "giveCarrots4000":
			{
				if (getQuestItemsCount(player, CARROT) >= 4000)
				{
					takeItems(player, CARROT, 4000);
					giveItems(player, POUCH, 100);
					htmltext = "34010-04.htm";
				}
				else
				{
					htmltext = "34010-03.htm";
				}
				break;
			}
			case "giveCarrots40000":
			{
				if (getQuestItemsCount(player, CARROT) >= 40000)
				{
					takeItems(player, CARROT, 40000);
					giveItems(player, POUCH, 1000);
					htmltext = "34010-04.htm";
				}
				else
				{
					htmltext = "34010-03.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + "-01.htm";
	}
	
	public static void main(String[] args)
	{
		new HungryHorse();
	}
}
