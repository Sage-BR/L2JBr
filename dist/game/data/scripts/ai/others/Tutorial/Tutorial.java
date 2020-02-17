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
package ai.others.Tutorial;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerBypass;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerCreate;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jbr.gameserver.network.serverpackets.ExTutorialShowId;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;
import org.l2jbr.gameserver.network.serverpackets.TutorialCloseHtml;
import org.l2jbr.gameserver.network.serverpackets.TutorialShowHtml;

import ai.AbstractNpcAI;

/**
 * Tutorial
 * @author Gigi
 * @date 2019-08-21 - [21:06:44]
 */
public class Tutorial extends AbstractNpcAI
{
	// Misc
	private static final String TUTORIAL_VAR = "TUTORIAL";
	
	private Tutorial()
	{
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final PlayerInstance player = event.getPlayer();
		if (player.getVariables().getInt(TUTORIAL_VAR, 0) == 0)
		{
			if (player.getRace() == Race.ERTHEIA)
			{
				ThreadPool.schedule(() ->
				{
					final String html = getHtm(player, "tutorial_01_ertheia.html");
					player.sendPacket(new TutorialShowHtml(html));
					player.sendPacket(new ExTutorialShowId(5));
				}, 26000);
			}
			else
			{
				ThreadPool.schedule(() ->
				{
					final String html = getHtm(player, "tutorial_01.html");
					player.sendPacket(new TutorialShowHtml(html));
					player.sendPacket(new ExTutorialShowId(5));
				}, 26000);
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerBypass(OnPlayerBypass event)
	{
		final PlayerInstance player = event.getPlayer();
		if (event.getCommand().equals("chat_window"))
		{
			ThreadPool.schedule(() ->
			{
				player.sendPacket(new ExTutorialShowId(1));
				player.sendPacket(new PlaySound(2, "tutorial_voice_006", 0, 0, 0, 0, 0));
			}, 500);
			player.getVariables().set(TUTORIAL_VAR, 1);
			player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_CREATE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerCreate(OnPlayerCreate event)
	{
		final PlayerInstance player = event.getPlayer();
		if (Config.DISABLE_TUTORIAL)
		{
			return;
		}
		player.getVariables().set(TUTORIAL_VAR, 0);
	}
	
	public static void main(String[] args)
	{
		new Tutorial();
	}
}