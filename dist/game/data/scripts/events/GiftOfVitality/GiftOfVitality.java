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
package events.GiftOfVitality;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.quest.LongTimeEvent;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * Gift of Vitality event AI.
 * @author Gnacik, Adry_85
 */
public class GiftOfVitality extends LongTimeEvent
{
	// NPC
	private static final int STEVE_SHYAGEL = 4306;
	// Skills
	private static final SkillHolder GIFT_OF_VITALITY = new SkillHolder(23179, 1);
	private static final SkillHolder JOY_OF_VITALITY = new SkillHolder(23180, 1);
	
	private static SkillHolder[] FIGHTER_SKILLS =
	{
		new SkillHolder(5627, 1), // Wind Walk
		new SkillHolder(5628, 1), // Shield
		new SkillHolder(5637, 1), // Magic Barrier
		new SkillHolder(5629, 1), // Bless the Body
		new SkillHolder(5630, 1), // Vampiric Rage
		new SkillHolder(5631, 1), // Regeneration
		new SkillHolder(5632, 1), // Haste
	};
	
	private static SkillHolder[] MAGE_SKILLS =
	{
		new SkillHolder(5627, 1), // Wind Walk
		new SkillHolder(5628, 1), // Shield
		new SkillHolder(5637, 1), // Magic Barrier
		new SkillHolder(5633, 1), // Bless the Soul
		new SkillHolder(5634, 1), // Acumen
		new SkillHolder(5635, 1), // Concentration
		new SkillHolder(5636, 1), // Empower
	};
	
	private static SkillHolder[] SERVITOR_SKILLS =
	{
		new SkillHolder(5627, 1), // Wind Walk
		new SkillHolder(5628, 1), // Shield
		new SkillHolder(5637, 1), // Magic Barrier
		new SkillHolder(5629, 1), // Bless the Body
		new SkillHolder(5633, 1), // Bless the Soul
		new SkillHolder(5630, 1), // Vampiric Rage
		new SkillHolder(5634, 1), // Acumen
		new SkillHolder(5631, 1), // Regeneration
		new SkillHolder(5635, 1), // Concentration
		new SkillHolder(5632, 1), // Haste
		new SkillHolder(5636, 1), // Empower
	};
	
	// Misc
	private static final int HOURS = 5; // Reuse between buffs
	private static final int MIN_LEVEL = 75;
	private static final String REUSE = GiftOfVitality.class.getSimpleName() + "_reuse";
	
	private GiftOfVitality()
	{
		addStartNpc(STEVE_SHYAGEL);
		addFirstTalkId(STEVE_SHYAGEL);
		addTalkId(STEVE_SHYAGEL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		switch (event)
		{
			case "vitality":
			{
				final long reuse = player.getVariables().getLong(REUSE, 0);
				if (reuse > System.currentTimeMillis())
				{
					final long remainingTime = (reuse - System.currentTimeMillis()) / 1000;
					final int hours = (int) (remainingTime / 3600);
					final int minutes = (int) ((remainingTime % 3600) / 60);
					final SystemMessage sm = new SystemMessage(SystemMessageId.S1_WILL_BE_AVAILABLE_FOR_RE_USE_AFTER_S2_HOUR_S_S3_MINUTE_S);
					sm.addSkillName(23179);
					sm.addInt(hours);
					sm.addInt(minutes);
					player.sendPacket(sm);
					htmltext = "4306-notime.htm";
				}
				else
				{
					SkillCaster.triggerCast(player, player, GIFT_OF_VITALITY.getSkill());
					SkillCaster.triggerCast(player, player, JOY_OF_VITALITY.getSkill());
					player.getVariables().set(REUSE, System.currentTimeMillis() + (HOURS * 3600000));
					htmltext = "4306-okvitality.htm";
				}
				break;
			}
			case "memories_player":
			{
				if (player.getLevel() <= MIN_LEVEL)
				{
					htmltext = "4306-nolevel.htm";
				}
				else
				{
					final SkillHolder[] skills = (player.isMageClass()) ? MAGE_SKILLS : FIGHTER_SKILLS;
					npc.setTarget(player);
					for (SkillHolder sk : skills)
					{
						npc.doCast(sk.getSkill());
					}
					htmltext = "4306-okbuff.htm";
				}
				break;
			}
			case "memories_summon":
			{
				if (player.getLevel() <= MIN_LEVEL)
				{
					htmltext = "4306-nolevel.htm";
				}
				else if (!player.hasServitors())
				{
					htmltext = "4306-nosummon.htm";
				}
				else
				{
					player.getServitors().values().forEach(s ->
					{
						npc.setTarget(s);
						for (SkillHolder sk : SERVITOR_SKILLS)
						{
							npc.doCast(sk.getSkill());
						}
					});
					htmltext = "4306-okbuff.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "4306.htm";
	}
	
	public static void main(String[] args)
	{
		new GiftOfVitality();
	}
}
