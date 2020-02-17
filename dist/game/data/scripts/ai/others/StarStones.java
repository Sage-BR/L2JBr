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
package ai.others;

import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.SystemMessageId;

import ai.AbstractNpcAI;

/**
 * Star Stones AI.
 * @author Gigiikun
 */
public class StarStones extends AbstractNpcAI
{
	// NPCs
	// @formatter:off
	private static final int[] MOBS =
	{
		18684, 18685, 18686, 18687, 18688, 18689, 18690, 18691, 18692
	};
	// @formatter:on
	// Misc
	private static final int COLLECTION_RATE = 1;
	
	public StarStones()
	{
		addSkillSeeId(MOBS);
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance caster, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		if (skill.getId() == 932)
		{
			int itemId = 0;
			
			switch (npc.getId())
			{
				case 18684:
				case 18685:
				case 18686:
				{
					// give Red item
					itemId = 14009;
					break;
				}
				case 18687:
				case 18688:
				case 18689:
				{
					// give Blue item
					itemId = 14010;
					break;
				}
				case 18690:
				case 18691:
				case 18692:
				{
					// give Green item
					itemId = 14011;
					break;
				}
				default:
				{
					// unknown npc!
					return super.onSkillSee(npc, caster, skill, targets, isSummon);
				}
			}
			if (getRandom(100) < 33)
			{
				caster.sendPacket(SystemMessageId.YOUR_COLLECTION_HAS_SUCCEEDED);
				caster.addItem("StarStone", itemId, getRandom(COLLECTION_RATE + 1, 2 * COLLECTION_RATE), null, true);
			}
			else if (((skill.getLevel() == 1) && (getRandom(100) < 15)) || ((skill.getLevel() == 2) && (getRandom(100) < 50)) || ((skill.getLevel() == 3) && (getRandom(100) < 75)))
			{
				caster.sendPacket(SystemMessageId.YOUR_COLLECTION_HAS_SUCCEEDED);
				caster.addItem("StarStone", itemId, getRandom(1, COLLECTION_RATE), null, true);
			}
			else
			{
				caster.sendPacket(SystemMessageId.THE_COLLECTION_HAS_FAILED);
			}
			npc.deleteMe();
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	public static void main(String[] args)
	{
		new StarStones();
	}
}
