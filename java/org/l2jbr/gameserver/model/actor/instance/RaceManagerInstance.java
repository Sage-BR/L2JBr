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
package org.l2jbr.gameserver.model.actor.instance;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.MonsterRace;
import org.l2jbr.gameserver.enums.InstanceType;
import org.l2jbr.gameserver.idfactory.IdFactory;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.templates.NpcTemplate;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;
import org.l2jbr.gameserver.network.serverpackets.DeleteObject;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.MonRaceInfo;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.util.Broadcast;

public class RaceManagerInstance extends Npc
{
	public static final int LANES = 8;
	public static final int WINDOW_START = 0;
	
	private static Collection<RaceManagerInstance> _managers;
	protected static int _raceNumber = 4;
	
	// Time Constants
	private static final long SECOND = 1000;
	private static final long MINUTE = 60 * SECOND;
	
	private static int _minutes = 5;
	
	// States
	private static final int ACCEPTING_BETS = 0;
	private static final int WAITING = 1;
	private static final int STARTING_RACE = 2;
	private static final int RACE_END = 3;
	private static int _state = RACE_END;
	
	protected static final int[][] _codes =
	{
		{
			-1,
			0
		},
		{
			0,
			15322
		},
		{
			13765,
			-1
		}
	};
	private static boolean _notInitialized = true;
	protected static MonRaceInfo _packet;
	protected static final int _cost[] =
	{
		100,
		500,
		1000,
		5000,
		10000,
		20000,
		50000,
		100000
	};
	
	public RaceManagerInstance(NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.RaceManagerInstance);
		
		if (!Config.ALLOW_RACE)
		{
			return;
		}
		
		if (_notInitialized)
		{
			_notInitialized = false;
			
			_managers = ConcurrentHashMap.newKeySet();
			
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1), 0, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1), 30 * SECOND, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1), MINUTE, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1), MINUTE + (30 * SECOND), 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTE_S), 2 * MINUTE, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTE_S), 3 * MINUTE, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTE_S), 4 * MINUTE, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTE_S), 5 * MINUTE, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTE_S), 6 * MINUTE, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.TICKETS_SALES_ARE_CLOSED_FOR_MONSTER_RACE_S1_ODDS_ARE_POSTED), 7 * MINUTE, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.MONSTER_RACE_S2_WILL_BEGIN_IN_S1_MINUTE_S), 7 * MINUTE, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.MONSTER_RACE_S2_WILL_BEGIN_IN_S1_MINUTE_S), 8 * MINUTE, 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.MONSTER_RACE_S1_WILL_BEGIN_IN_30_SECONDS), (8 * MINUTE) + (30 * SECOND), 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.MONSTER_RACE_S1_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS), (8 * MINUTE) + (50 * SECOND), 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.THE_RACE_WILL_BEGIN_IN_S1_SECOND_S), (8 * MINUTE) + (55 * SECOND), 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.THE_RACE_WILL_BEGIN_IN_S1_SECOND_S), (8 * MINUTE) + (56 * SECOND), 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.THE_RACE_WILL_BEGIN_IN_S1_SECOND_S), (8 * MINUTE) + (57 * SECOND), 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.THE_RACE_WILL_BEGIN_IN_S1_SECOND_S), (8 * MINUTE) + (58 * SECOND), 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.THE_RACE_WILL_BEGIN_IN_S1_SECOND_S), (8 * MINUTE) + (59 * SECOND), 10 * MINUTE);
			ThreadPool.scheduleAtFixedRate(new Announcement(SystemMessageId.THEY_RE_OFF), 9 * MINUTE, 10 * MINUTE);
		}
		_managers.add(this);
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		if (attacker.isMonster())
		{
			return true;
		}
		
		return super.isAutoAttackable(attacker);
	}
	
	class Announcement implements Runnable
	{
		private final SystemMessageId _type;
		
		public Announcement(SystemMessageId pType)
		{
			_type = pType;
		}
		
		@Override
		public void run()
		{
			makeAnnouncement(_type);
		}
	}
	
	public void makeAnnouncement(SystemMessageId type)
	{
		final SystemMessage sm = new SystemMessage(type);
		switch (type.getId())
		{
			case 816: // SystemMessageId.TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1
			case 817: // SystemMessageId.NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1
			{
				if (_state != ACCEPTING_BETS)
				{// LOGGER.info("Race Initializing");
					_state = ACCEPTING_BETS;
					startRace();
				} // else{LOGGER.info("Race open");}
				sm.addInt(_raceNumber);
				break;
			}
			case 818: // SystemMessageId.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTE_S
			case 820: // SystemMessageId.MONSTER_RACE_S2_WILL_BEGIN_IN_S1_MINUTE_S
			case 823: // SystemMessageId.THE_RACE_WILL_BEGIN_IN_S1_SECOND_S
			{
				sm.addInt(_minutes);
				if (type.getId() == 820)
				{
					sm.addInt(_raceNumber);
				}
				_minutes--;
				break;
			}
			case 819: // SystemMessageId.TICKETS_SALES_ARE_CLOSED_FOR_MONSTER_RACE_S1_ODDS_ARE_POSTED
			{
				// LOGGER.info("Sales closed");
				sm.addInt(_raceNumber);
				_state = WAITING;
				_minutes = 2;
				break;
			}
			case 821: // SystemMessageId.MONSTER_RACE_S1_WILL_BEGIN_IN_30_SECONDS
			case 822: // SystemMessageId.MONSTER_RACE_S1_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS
			case 825: // SystemMessageId.MONSTER_RACE_S1_IS_FINISHED
			{
				sm.addInt(_raceNumber);
				_minutes = 5;
				break;
			}
			case 826: // SystemMessageId.FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_SECOND_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S2
			{
				// LOGGER.info("Placing");
				_state = RACE_END;
				sm.addInt(MonsterRace.getInstance().getFirstPlace());
				sm.addInt(MonsterRace.getInstance().getSecondPlace());
				break;
			}
		}
		// _logn.info("Counter: "+minutes);
		// LOGGER.info("State: "+state);
		broadcast(sm);
		// LOGGER.info("Player's known: "+getKnownPlayers().size());
		
		if (type == SystemMessageId.THEY_RE_OFF)
		{
			// LOGGER.info("Starting race");
			_state = STARTING_RACE;
			startRace();
			_minutes = 5;
		}
	}
	
	protected void broadcast(IClientOutgoingPacket pkt)
	{
		for (RaceManagerInstance manager : _managers)
		{
			if (!manager.isDead())
			{
				Broadcast.toKnownPlayers(manager, pkt);
			}
		}
	}
	
	public void sendMonsterInfo()
	{
		broadcast(_packet);
	}
	
	private void startRace()
	{
		final MonsterRace race = MonsterRace.getInstance();
		if (_state == STARTING_RACE)
		{
			// state++;
			final PlaySound SRace = new PlaySound(1, "S_Race", 0, 0, 0, 0, 0);
			broadcast(SRace);
			final PlaySound SRace2 = new PlaySound(0, "ItemSound2.race_start", 1, 121209259, 12125, 182487, -3559);
			broadcast(SRace2);
			_packet = new MonRaceInfo(_codes[1][0], _codes[1][1], race.getMonsters(), race.getSpeeds());
			sendMonsterInfo();
			
			ThreadPool.schedule(new RunRace(), 5000);
		}
		else
		{
			// state++;
			race.newRace();
			race.newSpeeds();
			_packet = new MonRaceInfo(_codes[0][0], _codes[0][1], race.getMonsters(), race.getSpeeds());
			sendMonsterInfo();
		}
		
	}
	
	@Override
	public void onBypassFeedback(PlayerInstance player, String command)
	{
		if (command.startsWith("BuyTicket") && (_state != ACCEPTING_BETS))
		{
			if (!Config.ALLOW_RACE)
			{
				return;
			}
			player.sendPacket(SystemMessageId.MONSTER_RACE_TICKETS_ARE_NO_LONGER_AVAILABLE);
			command = "Chat 0";
		}
		if (command.startsWith("ShowOdds") && (_state == ACCEPTING_BETS))
		{
			if (!Config.ALLOW_RACE)
			{
				return;
			}
			player.sendPacket(SystemMessageId.MONSTER_RACE_PAYOUT_INFORMATION_IS_NOT_AVAILABLE_WHILE_TICKETS_ARE_BEING_SOLD);
			command = "Chat 0";
		}
		
		if (command.startsWith("BuyTicket"))
		{
			if (!Config.ALLOW_RACE)
			{
				return;
			}
			int val = Integer.parseInt(command.substring(10));
			if (val == 0)
			{
				player.setRace(0, 0);
				player.setRace(1, 0);
			}
			if (((val == 10) && (player.getRace(0) == 0)) || ((val == 20) && (player.getRace(0) == 0) && (player.getRace(1) == 0)))
			{
				val = 0;
			}
			showBuyTicket(player, val);
		}
		else if (command.equals("ShowOdds"))
		{
			if (!Config.ALLOW_RACE)
			{
				return;
			}
			showOdds(player);
		}
		else if (command.equals("ShowInfo"))
		{
			if (!Config.ALLOW_RACE)
			{
				return;
			}
			showMonsterInfo(player);
		}
		else if (command.equals("calculateWin"))
		{
			if (!Config.ALLOW_RACE)
			{
				return;
			}
			// displayCalculateWinnings(player);
		}
		else if (command.equals("viewHistory"))
		{
			if (!Config.ALLOW_RACE)
			{
				return;
			}
			// displayHistory(player);
		}
		else
		{
			// getKnownList().removeKnownObject(player);
			super.onBypassFeedback(player, command);
		}
	}
	
	public void showOdds(PlayerInstance player)
	{
		if (_state == ACCEPTING_BETS)
		{
			return;
		}
		final int npcId = getTemplate().getId();
		String filename;
		String search;
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		filename = getHtmlPath(npcId, 5, player);
		html.setFile(player, filename);
		for (int i = 0; i < 8; i++)
		{
			final int n = i + 1;
			search = "Mob" + n;
			html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().getName());
		}
		html.replace("1race", String.valueOf(_raceNumber));
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void showMonsterInfo(PlayerInstance player)
	{
		final int npcId = getTemplate().getId();
		String filename;
		String search;
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		filename = getHtmlPath(npcId, 6, player);
		html.setFile(player, filename);
		for (int i = 0; i < 8; i++)
		{
			final int n = i + 1;
			search = "Mob" + n;
			html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().getName());
		}
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void showBuyTicket(PlayerInstance player, int val)
	{
		if (_state != ACCEPTING_BETS)
		{
			return;
		}
		final int npcId = getTemplate().getId();
		SystemMessage sm;
		String filename;
		String search;
		String replace;
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		if (val < 10)
		{
			filename = getHtmlPath(npcId, 2, player);
			html.setFile(player, filename);
			for (int i = 0; i < 8; i++)
			{
				final int n = i + 1;
				search = "Mob" + n;
				html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().getName());
			}
			search = "No1";
			if (val == 0)
			{
				html.replace(search, "");
			}
			else
			{
				html.replace(search, Integer.toString(val));
				player.setRace(0, val);
			}
		}
		else if (val < 20)
		{
			if (player.getRace(0) == 0)
			{
				return;
			}
			filename = getHtmlPath(npcId, 3, player);
			html.setFile(player, filename);
			html.replace("0place", Integer.toString(player.getRace(0)));
			search = "Mob1";
			replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().getName();
			html.replace(search, replace);
			search = "0adena";
			if (val == 10)
			{
				html.replace(search, "");
			}
			else
			{
				html.replace(search, Integer.toString(_cost[val - 11]));
				player.setRace(1, val - 10);
			}
		}
		else if (val == 20)
		{
			if ((player.getRace(0) == 0) || (player.getRace(1) == 0))
			{
				return;
			}
			filename = getHtmlPath(npcId, 4, player);
			html.setFile(player, filename);
			html.replace("0place", Integer.toString(player.getRace(0)));
			search = "Mob1";
			replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().getName();
			html.replace(search, replace);
			search = "0adena";
			final int price = _cost[player.getRace(1) - 1];
			html.replace(search, Integer.toString(price));
			search = "0tax";
			final int tax = 0;
			html.replace(search, Integer.toString(tax));
			search = "0total";
			final int total = price + tax;
			html.replace(search, Integer.toString(total));
		}
		else
		{
			if ((player.getRace(0) == 0) || (player.getRace(1) == 0))
			{
				return;
			}
			final int ticket = player.getRace(0);
			final int priceId = player.getRace(1);
			if (!player.reduceAdena("Race", _cost[priceId - 1], this, true))
			{
				return;
			}
			player.setRace(0, 0);
			player.setRace(1, 0);
			sm = new SystemMessage(SystemMessageId.ACQUIRED_S1_S2);
			sm.addInt(_raceNumber);
			sm.addItemName(4443);
			player.sendPacket(sm);
			final ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), 4443);
			item.setCount(1);
			item.setEnchantLevel(_raceNumber);
			item.setCustomType1(ticket);
			item.setCustomType2(_cost[priceId - 1] / 100);
			player.getInventory().addItem("Race", item, player, this);
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addItem(item);
			final ItemInstance adenaupdate = player.getInventory().getItemByItemId(Inventory.ADENA_ID);
			iu.addModifiedItem(adenaupdate);
			player.sendInventoryUpdate(iu);
			return;
		}
		html.replace("1race", String.valueOf(_raceNumber));
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public static class Race
	{
		private final Info[] _info;
		
		public Race(Info[] pInfo)
		{
			_info = pInfo;
		}
		
		public Info getLaneInfo(int lane)
		{
			return _info[lane];
		}
		
		public class Info
		{
			private final int _id;
			private final int _place;
			private final int _odds;
			private final int _payout;
			
			public Info(int pId, int pPlace, int pOdds, int pPayout)
			{
				_id = pId;
				_place = pPlace;
				_odds = pOdds;
				_payout = pPayout;
			}
			
			public int getId()
			{
				return _id;
			}
			
			public int getOdds()
			{
				return _odds;
			}
			
			public int getPayout()
			{
				return _payout;
			}
			
			public int getPlace()
			{
				return _place;
			}
		}
		
	}
	
	class RunRace implements Runnable
	{
		@Override
		public void run()
		{
			_packet = new MonRaceInfo(_codes[2][0], _codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds());
			sendMonsterInfo();
			ThreadPool.schedule(new RunEnd(), 30000);
		}
	}
	
	class RunEnd implements Runnable
	{
		@Override
		public void run()
		{
			makeAnnouncement(SystemMessageId.FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_SECOND_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S2);
			makeAnnouncement(SystemMessageId.MONSTER_RACE_S1_IS_FINISHED);
			_raceNumber++;
			
			DeleteObject obj = null;
			for (int i = 0; i < 8; i++)
			{
				obj = new DeleteObject(MonsterRace.getInstance().getMonsters()[i]);
				broadcast(obj);
				MonsterRace.getInstance().getMonsters()[i].deleteMe();
			}
		}
	}
	
}
