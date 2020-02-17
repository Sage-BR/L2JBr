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
package events.ThePowerOfLove;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.quest.LongTimeEvent;

/**
 * The Power Of Love
 * @URL http://www.lineage2.com/en/news/events/02102016-the-power-of-love-part-iii.php
 * @author hlwrave
 */
public class ThePowerOfLove extends LongTimeEvent
{
	// NPC
	private static final int COCO = 33893;
	// Items
	private static final int CT = 37705;
	private static final int CT_TRANSORM = 37708;
	private static final int CT_SUMMON = 37711;
	private static final int CH = 37706;
	private static final int CH_TRANSORM = 37709;
	private static final int CH_SUMMON = 37712;
	private static final int CC = 37707;
	private static final int CC_TRANSORM = 37710;
	private static final int CC_SUMMON = 37713;
	// Skill
	private static final SkillHolder COCO_M = new SkillHolder(17155, 1); // Coco's Magic
	
	private ThePowerOfLove()
	{
		addStartNpc(COCO);
		addFirstTalkId(COCO);
		addTalkId(COCO);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33893-1.htm":
			case "33893-2.htm":
			case "33893-3.htm":
			{
				htmltext = event;
				break;
			}
			case "coco_buff":
			{
				npc.setTarget(player);
				npc.doCast(COCO_M.getSkill());
				htmltext = "33893-4.htm";
				break;
			}
			case "ct":
			{
				if (!hasQuestItems(player, CT) && !hasQuestItems(player, CH) && !hasQuestItems(player, CC))
				{
					giveItems(player, CT, 1);
					giveItems(player, CT_TRANSORM, 1);
					giveItems(player, CT_SUMMON, 1);
					htmltext = "33893-5.htm";
				}
				else
				{
					htmltext = "33893-9.htm";
				}
				break;
			}
			case "ch":
			{
				if (!hasQuestItems(player, CT) && !hasQuestItems(player, CH) && !hasQuestItems(player, CC))
				{
					giveItems(player, CH, 1);
					giveItems(player, CH_TRANSORM, 1);
					giveItems(player, CH_SUMMON, 1);
					htmltext = "33893-6.htm";
				}
				else
				{
					htmltext = "33893-9.htm";
				}
				break;
			}
			case "cc":
			{
				if (!hasQuestItems(player, CT) && !hasQuestItems(player, CH) && !hasQuestItems(player, CC))
				{
					giveItems(player, CC, 1);
					giveItems(player, CC_TRANSORM, 1);
					giveItems(player, CC_SUMMON, 1);
					htmltext = "33893-7.htm";
				}
				else
				{
					htmltext = "33893-9.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + "-1.htm";
	}
	
	public static void main(String[] args)
	{
		new ThePowerOfLove();
	}
}
