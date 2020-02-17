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
package village_master.ClanMaster;

import java.util.HashMap;
import java.util.Map;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.ClanMember;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanJoin;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanLeft;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerProfessionChange;
import org.l2jbr.gameserver.model.skills.CommonSkill;

import ai.AbstractNpcAI;

/**
 * @author UnAfraid
 */
public class ClanMaster extends AbstractNpcAI
{
	// @formatter:off
	private static final int[] NPCS =
	{
		30026,30031,30037,30066,30070,30109,30115,30120,30154,30174,
		30175,30176,30187,30191,30195,30288,30289,30290,30297,30358,
		30373,30462,30474,30498,30499,30500,30503,30504,30505,30508,
		30511,30512,30513,30520,30525,30565,30594,30595,30676,30677,
		30681,30685,30687,30689,30694,30699,30704,30845,30847,30849,
		30854,30857,30862,30865,30894,30897,30900,30905,30910,30913,
		31269,31272,31276,31279,31285,31288,31314,31317,31321,31324,
		31326,31328,31331,31334,31336,31755,31958,31961,31965,31968,
		31974,31977,31996,32092,32093,32094,32095,32096,32097,32098,
		32145,32146,32147,32150,32153,32154,32157,32158,32160,32171,
		32193,32196,32199,32202,32205,32206,32209,32210,32213,32214,
		32217,32218,32221,32222,32225,32226,32229,32230,32233,32234
	};
	// @formatter:on
	private static final Map<String, String> LEADER_REQUIRED = new HashMap<>();
	static
	{
		LEADER_REQUIRED.put("9000-03.htm", "9000-03-no.htm");
		LEADER_REQUIRED.put("9000-04.htm", "9000-04-no.htm");
		LEADER_REQUIRED.put("9000-05.htm", "9000-05-no.htm");
		LEADER_REQUIRED.put("9000-07.htm", "9000-07-no.htm");
	}
	
	private ClanMaster()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (LEADER_REQUIRED.containsKey(event))
		{
			if (!player.isClanLeader())
			{
				return LEADER_REQUIRED.get(event);
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance talker)
	{
		return "9000-01.htm";
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getPlayer();
		if (player.isClanLeader() || (player.getPledgeType() == 100))
		{
			player.getClan().getMembers().forEach(member ->
			{
				if (member.isOnline())
				{
					CommonSkill.CLAN_ADVENT.getSkill().applyEffects(member.getPlayerInstance(), member.getPlayerInstance());
				}
			});
		}
		else if (player.getClan() != null)
		{
			if (player.getClan().getLeader().isOnline())
			{
				CommonSkill.CLAN_ADVENT.getSkill().applyEffects(player, player);
			}
			else
			{
				for (ClanMember member : player.getClan().getMembers())
				{
					if (member.getPledgeType() == 100)
					{
						CommonSkill.CLAN_ADVENT.getSkill().applyEffects(player, player);
						break;
					}
				}
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogout(OnPlayerLogout event)
	{
		final PlayerInstance player = event.getPlayer();
		if (player.isClanLeader())
		{
			boolean removing = true;
			for (ClanMember member : player.getClan().getMembers())
			{
				if (member.getPledgeType() == 100)
				{
					removing = false;
					break;
				}
			}
			if (removing)
			{
				player.getClan().getMembers().forEach(member ->
				{
					if (member.isOnline())
					{
						member.getPlayerInstance().getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getSkill());
					}
				});
				if (player.getClan() != null)
				{
					player.getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getSkill());
				}
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onProfessionChange(OnPlayerProfessionChange event)
	{
		final PlayerInstance player = event.getPlayer();
		if (player.isClanLeader() || (player.getClan() != null))
		{
			if (player.getClan().getLeader().isOnline())
			{
				CommonSkill.CLAN_ADVENT.getSkill().applyEffects(player, player);
			}
			else
			{
				for (ClanMember member : player.getClan().getMembers())
				{
					if (member.getPledgeType() == 100)
					{
						CommonSkill.CLAN_ADVENT.getSkill().applyEffects(player, player);
						break;
					}
				}
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_CLAN_JOIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerClanJoin(OnPlayerClanJoin event)
	{
		final PlayerInstance player = event.getClanMember().getPlayerInstance();
		if (player.getClan().getLeader().isOnline())
		{
			CommonSkill.CLAN_ADVENT.getSkill().applyEffects(player, player);
		}
		else
		{
			for (ClanMember member : player.getClan().getMembers())
			{
				if (member.getPledgeType() == 100)
				{
					CommonSkill.CLAN_ADVENT.getSkill().applyEffects(player, player);
					break;
				}
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_CLAN_LEFT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerClanLeft(OnPlayerClanLeft event)
	{
		event.getClanMember().getPlayerInstance().getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());
	}
	
	public static void main(String[] args)
	{
		new ClanMaster();
	}
}
