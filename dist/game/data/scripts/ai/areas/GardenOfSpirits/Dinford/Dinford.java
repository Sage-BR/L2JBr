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
package ai.areas.GardenOfSpirits.Dinford;

import org.l2jbr.gameserver.data.xml.impl.MultisellData;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.Id;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcMenuSelect;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Dinford in Blackbird Campsite
 * @author Gigi
 * @date 2018-04-07 - [12:07:12]
 */
public class Dinford extends AbstractNpcAI
{
	// NPC
	private static final int DINFORD = 34236;
	
	private Dinford()
	{
		addFirstTalkId(DINFORD);
		addTalkId(DINFORD);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34236.html":
			case "34236-01.html":
			case "34236-02.html":
			case "34236-03.html":
			case "34236-04.html":
			case "34236-05.html":
			{
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(DINFORD)
	public void OnNpcMenuSelect(OnNpcMenuSelect event)
	{
		final PlayerInstance player = event.getTalker();
		final Npc npc = event.getNpc();
		final int ask = event.getAsk();
		final int reply = event.getReply();
		
		if (ask == -303)
		{
			if (reply == 2178)
			{
				MultisellData.getInstance().separateAndSend(2178, player, npc, false);
			}
			else if (reply == 2179)
			{
				MultisellData.getInstance().separateAndSend(2179, player, npc, false);
			}
		}
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		if (getRandom(10) < 5)
		{
			player.sendPacket(new PlaySound(3, "Npcdialog1.dinfod_faction_1", 0, 0, 0, 0, 0));
		}
		else
		{
			player.sendPacket(new PlaySound(3, "Npcdialog1.dinfod_faction_2", 0, 0, 0, 0, 0));
		}
		return "34236.html";
	}
	
	public static void main(String[] args)
	{
		new Dinford();
	}
}