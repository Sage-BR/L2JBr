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
package org.l2jbr.gameserver.model.entity;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.DuelResult;
import org.l2jbr.gameserver.enums.Team;
import org.l2jbr.gameserver.instancemanager.DuelManager;
import org.l2jbr.gameserver.instancemanager.InstanceManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.instance.DoorInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.model.zone.type.OlympiadStadiumZone;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;
import org.l2jbr.gameserver.network.serverpackets.ExDuelEnd;
import org.l2jbr.gameserver.network.serverpackets.ExDuelReady;
import org.l2jbr.gameserver.network.serverpackets.ExDuelStart;
import org.l2jbr.gameserver.network.serverpackets.ExDuelUpdateUserInfo;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;
import org.l2jbr.gameserver.network.serverpackets.SocialAction;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

public class Duel
{
	protected static final Logger LOGGER = Logger.getLogger(Duel.class.getName());
	
	public static final int DUELSTATE_NODUEL = 0;
	public static final int DUELSTATE_DUELLING = 1;
	public static final int DUELSTATE_DEAD = 2;
	public static final int DUELSTATE_WINNER = 3;
	public static final int DUELSTATE_INTERRUPTED = 4;
	
	private static final PlaySound B04_S01 = new PlaySound(1, "B04_S01", 0, 0, 0, 0, 0);
	
	private static final int PARTY_DUEL_DURATION = 300;
	private static final int PLAYER_DUEL_DURATION = 120;
	
	private final int _duelId;
	private PlayerInstance _playerA;
	private PlayerInstance _playerB;
	private final boolean _partyDuel;
	private final Calendar _duelEndTime;
	private int _surrenderRequest = 0;
	private int _countdown = 5;
	private boolean _finished = false;
	
	private final Map<Integer, PlayerCondition> _playerConditions = new ConcurrentHashMap<>();
	Instance _duelInstance;
	
	public Duel(PlayerInstance playerA, PlayerInstance playerB, int partyDuel, int duelId)
	{
		_duelId = duelId;
		_playerA = playerA;
		_playerB = playerB;
		_partyDuel = partyDuel == 1;
		
		if (_partyDuel)
		{
			for (PlayerInstance member : _playerA.getParty().getMembers())
			{
				member.setStartingDuel();
			}
			for (PlayerInstance member : _playerB.getParty().getMembers())
			{
				member.setStartingDuel();
			}
		}
		else
		{
			_playerA.setStartingDuel();
			_playerB.setStartingDuel();
		}
		
		_duelEndTime = Calendar.getInstance();
		_duelEndTime.add(Calendar.SECOND, _partyDuel ? PARTY_DUEL_DURATION : PLAYER_DUEL_DURATION);
		
		setFinished(false);
		
		if (_partyDuel)
		{
			// inform players that they will be ported shortly
			final SystemMessage sm = new SystemMessage(SystemMessageId.IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE);
			broadcastToTeam1(sm);
			broadcastToTeam2(sm);
		}
		// Schedule duel start
		ThreadPool.schedule(new ScheduleStartDuelTask(this), 3000);
	}
	
	public static class PlayerCondition
	{
		private PlayerInstance _player;
		private double _hp;
		private double _mp;
		private double _cp;
		private boolean _paDuel;
		private int _x;
		private int _y;
		private int _z;
		private Set<Skill> _debuffs;
		
		public PlayerCondition(PlayerInstance player, boolean partyDuel)
		{
			if (player == null)
			{
				return;
			}
			_player = player;
			_hp = _player.getCurrentHp();
			_mp = _player.getCurrentMp();
			_cp = _player.getCurrentCp();
			_paDuel = partyDuel;
			
			if (_paDuel)
			{
				_x = _player.getX();
				_y = _player.getY();
				_z = _player.getZ();
			}
		}
		
		public void restoreCondition()
		{
			if (_player == null)
			{
				return;
			}
			_player.setCurrentHp(_hp);
			_player.setCurrentMp(_mp);
			_player.setCurrentCp(_cp);
			
			if (_paDuel)
			{
				teleportBack();
			}
			if (_debuffs != null) // Debuff removal
			{
				for (Skill skill : _debuffs)
				{
					if (skill != null)
					{
						_player.stopSkillEffects(true, skill.getId());
					}
				}
			}
		}
		
		public void registerDebuff(Skill debuff)
		{
			if (_debuffs == null)
			{
				_debuffs = ConcurrentHashMap.newKeySet();
			}
			
			_debuffs.add(debuff);
		}
		
		public void teleportBack()
		{
			if (_paDuel)
			{
				_player.teleToLocation(_x, _y, _z);
			}
		}
		
		public PlayerInstance getPlayer()
		{
			return _player;
		}
	}
	
	public class ScheduleDuelTask implements Runnable
	{
		private final Duel _duel;
		
		public ScheduleDuelTask(Duel duel)
		{
			_duel = duel;
		}
		
		@Override
		public void run()
		{
			try
			{
				switch (_duel.checkEndDuelCondition())
				{
					case CANCELED:
					{
						// do not schedule duel end if it was interrupted
						setFinished(true);
						_duel.endDuel(DuelResult.CANCELED);
						break;
					}
					case CONTINUE:
					{
						ThreadPool.schedule(this, 1000);
						break;
					}
					default:
					{
						setFinished(true);
						playKneelAnimation();
						ThreadPool.schedule(new ScheduleEndDuelTask(_duel, _duel.checkEndDuelCondition()), 5000);
						if (_duelInstance != null)
						{
							_duelInstance.destroy();
						}
						break;
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "There has been a problem while runing a duel task!", e);
			}
		}
	}
	
	public static class ScheduleStartDuelTask implements Runnable
	{
		private final Duel _duel;
		
		public ScheduleStartDuelTask(Duel duel)
		{
			_duel = duel;
		}
		
		@Override
		public void run()
		{
			try
			{
				// start/continue countdown
				final int count = _duel.countdown();
				
				if (count == 4)
				{
					// Save player conditions before teleporting players
					_duel.savePlayerConditions();
					
					_duel.teleportPlayers();
					
					// give players 20 seconds to complete teleport and get ready (its ought to be 30 on offical..)
					ThreadPool.schedule(this, 20000);
				}
				else if (count > 0) // duel not started yet - continue countdown
				{
					ThreadPool.schedule(this, 1000);
				}
				else
				{
					_duel.startDuel();
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "There has been a problem while runing a duel start task!", e);
			}
		}
	}
	
	public static class ScheduleEndDuelTask implements Runnable
	{
		private final Duel _duel;
		private final DuelResult _result;
		
		public ScheduleEndDuelTask(Duel duel, DuelResult result)
		{
			_duel = duel;
			_result = result;
		}
		
		@Override
		public void run()
		{
			try
			{
				_duel.endDuel(_result);
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "There has been a problem while runing a duel end task!", e);
			}
		}
	}
	
	public Instance getDueldInstance()
	{
		return _duelInstance;
	}
	
	/**
	 * Stops all players from attacking. Used for duel timeout / interrupt.
	 */
	private void stopFighting()
	{
		final ActionFailed af = ActionFailed.STATIC_PACKET;
		if (_partyDuel)
		{
			for (PlayerInstance temp : _playerA.getParty().getMembers())
			{
				temp.abortCast();
				temp.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				temp.setTarget(null);
				temp.sendPacket(af);
				temp.getServitorsAndPets().forEach(s ->
				{
					s.abortCast();
					s.abortAttack();
					s.setTarget(null);
					s.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				});
			}
			for (PlayerInstance temp : _playerB.getParty().getMembers())
			{
				temp.abortCast();
				temp.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				temp.setTarget(null);
				temp.sendPacket(af);
				temp.getServitorsAndPets().forEach(s ->
				{
					s.abortCast();
					s.abortAttack();
					s.setTarget(null);
					s.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				});
			}
		}
		else
		{
			_playerA.abortCast();
			_playerB.abortCast();
			_playerA.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			_playerA.setTarget(null);
			_playerB.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			_playerB.setTarget(null);
			_playerA.sendPacket(af);
			_playerB.sendPacket(af);
			_playerA.getServitorsAndPets().forEach(s ->
			{
				s.abortCast();
				s.abortAttack();
				s.setTarget(null);
				s.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			});
			_playerB.getServitorsAndPets().forEach(s ->
			{
				s.abortCast();
				s.abortAttack();
				s.setTarget(null);
				s.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			});
		}
	}
	
	/**
	 * Check if a player engaged in pvp combat (only for 1on1 duels)
	 * @param sendMessage
	 * @return returns true if a duelist is engaged in Pvp combat
	 */
	public boolean isDuelistInPvp(boolean sendMessage)
	{
		if (_partyDuel)
		{
			// Party duels take place in arenas - should be no other players there
			return false;
		}
		else if ((_playerA.getPvpFlag() != 0) || (_playerB.getPvpFlag() != 0))
		{
			if (sendMessage)
			{
				final String engagedInPvP = "The duel was canceled because a duelist engaged in PvP combat.";
				_playerA.sendMessage(engagedInPvP);
				_playerB.sendMessage(engagedInPvP);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Starts the duel
	 */
	public void startDuel()
	{
		if ((_playerA == null) || (_playerB == null) || _playerA.isInDuel() || _playerB.isInDuel())
		{
			_playerConditions.clear();
			DuelManager.getInstance().removeDuel(this);
			return;
		}
		
		if (_partyDuel)
		{
			// Set duel state and team
			for (PlayerInstance temp : _playerA.getParty().getMembers())
			{
				temp.cancelActiveTrade();
				temp.setIsInDuel(_duelId);
				temp.setTeam(Team.BLUE);
				temp.broadcastUserInfo();
				broadcastToTeam2(new ExDuelUpdateUserInfo(temp));
			}
			for (PlayerInstance temp : _playerB.getParty().getMembers())
			{
				temp.cancelActiveTrade();
				temp.setIsInDuel(_duelId);
				temp.setTeam(Team.RED);
				temp.broadcastUserInfo();
				broadcastToTeam1(new ExDuelUpdateUserInfo(temp));
			}
			
			// Send duel packets
			broadcastToTeam1(ExDuelReady.PARTY_DUEL);
			broadcastToTeam2(ExDuelReady.PARTY_DUEL);
			broadcastToTeam1(ExDuelStart.PARTY_DUEL);
			broadcastToTeam2(ExDuelStart.PARTY_DUEL);
			
			for (DoorInstance door : _duelInstance.getDoors())
			{
				if ((door != null) && !door.isOpen())
				{
					door.openMe();
				}
			}
		}
		else
		{
			// set isInDuel() state
			_playerA.setIsInDuel(_duelId);
			_playerA.setTeam(Team.BLUE);
			_playerB.setIsInDuel(_duelId);
			_playerB.setTeam(Team.RED);
			
			// Send duel packets
			broadcastToTeam1(ExDuelReady.PLAYER_DUEL);
			broadcastToTeam2(ExDuelReady.PLAYER_DUEL);
			broadcastToTeam1(ExDuelStart.PLAYER_DUEL);
			broadcastToTeam2(ExDuelStart.PLAYER_DUEL);
			
			broadcastToTeam1(new ExDuelUpdateUserInfo(_playerB));
			broadcastToTeam2(new ExDuelUpdateUserInfo(_playerA));
			
			_playerA.broadcastUserInfo();
			_playerB.broadcastUserInfo();
		}
		
		// play sound
		broadcastToTeam1(B04_S01);
		broadcastToTeam2(B04_S01);
		
		// start duelling task
		ThreadPool.schedule(new ScheduleDuelTask(this), 1000);
	}
	
	/**
	 * Save the current player condition: hp, mp, cp, location
	 */
	public void savePlayerConditions()
	{
		if (_partyDuel)
		{
			for (PlayerInstance player : _playerA.getParty().getMembers())
			{
				_playerConditions.put(player.getObjectId(), new PlayerCondition(player, _partyDuel));
			}
			for (PlayerInstance player : _playerB.getParty().getMembers())
			{
				_playerConditions.put(player.getObjectId(), new PlayerCondition(player, _partyDuel));
			}
		}
		else
		{
			_playerConditions.put(_playerA.getObjectId(), new PlayerCondition(_playerA, _partyDuel));
			_playerConditions.put(_playerB.getObjectId(), new PlayerCondition(_playerB, _partyDuel));
		}
	}
	
	/**
	 * Restore player conditions
	 * @param abnormalDuelEnd true if the duel was the duel canceled
	 */
	public void restorePlayerConditions(boolean abnormalDuelEnd)
	{
		// update isInDuel() state for all players
		if (_partyDuel)
		{
			for (PlayerInstance temp : _playerA.getParty().getMembers())
			{
				temp.setIsInDuel(0);
				temp.setTeam(Team.NONE);
				temp.broadcastUserInfo();
			}
			for (PlayerInstance temp : _playerB.getParty().getMembers())
			{
				temp.setIsInDuel(0);
				temp.setTeam(Team.NONE);
				temp.broadcastUserInfo();
			}
		}
		else
		{
			_playerA.setIsInDuel(0);
			_playerA.setTeam(Team.NONE);
			_playerA.broadcastUserInfo();
			_playerB.setIsInDuel(0);
			_playerB.setTeam(Team.NONE);
			_playerB.broadcastUserInfo();
		}
		
		// if it is an abnormal DuelEnd do not restore hp, mp, cp
		if (abnormalDuelEnd)
		{
			return;
		}
		
		// restore player conditions
		_playerConditions.values().forEach(c -> c.restoreCondition());
	}
	
	/**
	 * Get the duel id
	 * @return id
	 */
	public int getId()
	{
		return _duelId;
	}
	
	/**
	 * Returns the remaining time
	 * @return remaining time
	 */
	public int getRemainingTime()
	{
		return (int) (_duelEndTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
	}
	
	/**
	 * Get the player that requested the duel
	 * @return duel requester
	 */
	public PlayerInstance getPlayerA()
	{
		return _playerA;
	}
	
	/**
	 * Get the player that was challenged
	 * @return challenged player
	 */
	public PlayerInstance getPlayerB()
	{
		return _playerB;
	}
	
	/**
	 * Returns whether this is a party duel or not
	 * @return is party duel
	 */
	public boolean isPartyDuel()
	{
		return _partyDuel;
	}
	
	public void setFinished(boolean mode)
	{
		_finished = mode;
	}
	
	public boolean getFinished()
	{
		return _finished;
	}
	
	/**
	 * Teleports all players to a free arena.
	 */
	public void teleportPlayers()
	{
		if (!_partyDuel)
		{
			return;
		}
		
		final int instanceId = DuelManager.getInstance().getDuelArena();
		final OlympiadStadiumZone zone = ZoneManager.getInstance().getAllZones(OlympiadStadiumZone.class) //
			.stream().filter(z -> z.getInstanceTemplateId() == instanceId).findFirst().orElse(null);
		
		if (zone == null)
		{
			throw new RuntimeException("Unable to find a party duel arena!");
		}
		
		final List<Location> spawns = zone.getSpawns();
		_duelInstance = InstanceManager.getInstance().createInstance(InstanceManager.getInstance().getInstanceTemplate(instanceId), null);
		
		final Location spawn1 = spawns.get(Rnd.get(spawns.size() / 2));
		for (PlayerInstance temp : _playerA.getParty().getMembers())
		{
			temp.teleToLocation(spawn1.getX(), spawn1.getY(), spawn1.getZ(), 0, 0, _duelInstance);
		}
		
		final Location spawn2 = spawns.get(Rnd.get(spawns.size() / 2, spawns.size()));
		for (PlayerInstance temp : _playerB.getParty().getMembers())
		{
			temp.teleToLocation(spawn2.getX(), spawn2.getY(), spawn2.getZ(), 0, 0, _duelInstance);
		}
	}
	
	/**
	 * Broadcast a packet to the challenger team
	 * @param packet
	 */
	public void broadcastToTeam1(IClientOutgoingPacket packet)
	{
		if (_playerA == null)
		{
			return;
		}
		
		if (_partyDuel && (_playerA.getParty() != null))
		{
			for (PlayerInstance temp : _playerA.getParty().getMembers())
			{
				temp.sendPacket(packet);
			}
		}
		else
		{
			_playerA.sendPacket(packet);
		}
	}
	
	/**
	 * Broadcast a packet to the challenged team
	 * @param packet
	 */
	public void broadcastToTeam2(IClientOutgoingPacket packet)
	{
		if (_playerB == null)
		{
			return;
		}
		
		if (_partyDuel && (_playerB.getParty() != null))
		{
			for (PlayerInstance temp : _playerB.getParty().getMembers())
			{
				temp.sendPacket(packet);
			}
		}
		else
		{
			_playerB.sendPacket(packet);
		}
	}
	
	/**
	 * Get the duel winner
	 * @return winner
	 */
	public PlayerInstance getWinner()
	{
		if (!_finished || (_playerA == null) || (_playerB == null))
		{
			return null;
		}
		if (_playerA.getDuelState() == DUELSTATE_WINNER)
		{
			return _playerA;
		}
		if (_playerB.getDuelState() == DUELSTATE_WINNER)
		{
			return _playerB;
		}
		return null;
	}
	
	/**
	 * Get the duel looser
	 * @return looser
	 */
	public PlayerInstance getLooser()
	{
		if (!_finished || (_playerA == null) || (_playerB == null))
		{
			return null;
		}
		if (_playerA.getDuelState() == DUELSTATE_WINNER)
		{
			return _playerB;
		}
		else if (_playerB.getDuelState() == DUELSTATE_WINNER)
		{
			return _playerA;
		}
		return null;
	}
	
	/**
	 * Playback the bow animation for all loosers
	 */
	public void playKneelAnimation()
	{
		final PlayerInstance looser = getLooser();
		
		if (looser == null)
		{
			return;
		}
		
		if (_partyDuel && (looser.getParty() != null))
		{
			for (PlayerInstance temp : looser.getParty().getMembers())
			{
				temp.broadcastPacket(new SocialAction(temp.getObjectId(), 7));
			}
		}
		else
		{
			looser.broadcastPacket(new SocialAction(looser.getObjectId(), 7));
		}
	}
	
	/**
	 * Do the countdown and send message to players if necessary
	 * @return current count
	 */
	public int countdown()
	{
		_countdown--;
		
		if (_countdown > 3)
		{
			return _countdown;
		}
		
		// Broadcast countdown to duelists
		SystemMessage sm = null;
		if (_countdown > 0)
		{
			sm = new SystemMessage(SystemMessageId.THE_DUEL_WILL_BEGIN_IN_S1_SECOND_S);
			sm.addInt(_countdown);
		}
		else
		{
			sm = new SystemMessage(SystemMessageId.LET_THE_DUEL_BEGIN);
		}
		
		broadcastToTeam1(sm);
		broadcastToTeam2(sm);
		
		return _countdown;
	}
	
	/**
	 * The duel has reached a state in which it can no longer continue
	 * @param result the duel result.
	 */
	public void endDuel(DuelResult result)
	{
		if ((_playerA == null) || (_playerB == null))
		{
			// clean up
			_playerConditions.clear();
			DuelManager.getInstance().removeDuel(this);
			return;
		}
		
		// inform players of the result
		SystemMessage sm = null;
		switch (result)
		{
			case TEAM_1_WIN:
			case TEAM_2_SURRENDER:
			{
				restorePlayerConditions(false);
				sm = _partyDuel ? new SystemMessage(SystemMessageId.C1_S_PARTY_HAS_WON_THE_DUEL) : new SystemMessage(SystemMessageId.C1_HAS_WON_THE_DUEL);
				sm.addString(_playerA.getName());
				
				broadcastToTeam1(sm);
				broadcastToTeam2(sm);
				break;
			}
			case TEAM_1_SURRENDER:
			case TEAM_2_WIN:
			{
				restorePlayerConditions(false);
				sm = _partyDuel ? new SystemMessage(SystemMessageId.C1_S_PARTY_HAS_WON_THE_DUEL) : new SystemMessage(SystemMessageId.C1_HAS_WON_THE_DUEL);
				sm.addString(_playerB.getName());
				
				broadcastToTeam1(sm);
				broadcastToTeam2(sm);
				break;
			}
			case CANCELED:
			{
				stopFighting();
				// Don't restore hp, mp, cp
				restorePlayerConditions(true);
				// TODO: is there no other message for a canceled duel?
				// send SystemMessage
				sm = new SystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE);
				
				broadcastToTeam1(sm);
				broadcastToTeam2(sm);
				break;
			}
			case TIMEOUT:
			{
				stopFighting();
				// hp,mp,cp seem to be restored in a timeout too...
				restorePlayerConditions(false);
				// send SystemMessage
				sm = new SystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE);
				
				broadcastToTeam1(sm);
				broadcastToTeam2(sm);
				break;
			}
		}
		
		// Send end duel packet
		final ExDuelEnd duelEnd = _partyDuel ? ExDuelEnd.PARTY_DUEL : ExDuelEnd.PLAYER_DUEL;
		broadcastToTeam1(duelEnd);
		broadcastToTeam2(duelEnd);
		
		// clean up
		_playerConditions.clear();
		DuelManager.getInstance().removeDuel(this);
	}
	
	/**
	 * Did a situation occur in which the duel has to be ended?
	 * @return DuelResult duel status
	 */
	public DuelResult checkEndDuelCondition()
	{
		// one of the players might leave during duel
		if ((_playerA == null) || (_playerB == null))
		{
			return DuelResult.CANCELED;
		}
		
		// got a duel surrender request?
		if (_surrenderRequest != 0)
		{
			return _surrenderRequest == 1 ? DuelResult.TEAM_1_SURRENDER : DuelResult.TEAM_2_SURRENDER;
		}
		// duel timed out
		else if (getRemainingTime() <= 0)
		{
			return DuelResult.TIMEOUT;
		}
		// Has a player been declared winner yet?
		else if (_playerA.getDuelState() == DUELSTATE_WINNER)
		{
			// If there is a Winner already there should be no more fighting going on
			stopFighting();
			return DuelResult.TEAM_1_WIN;
		}
		else if (_playerB.getDuelState() == DUELSTATE_WINNER)
		{
			// If there is a Winner already there should be no more fighting going on
			stopFighting();
			return DuelResult.TEAM_2_WIN;
		}
		
		// More end duel conditions for 1on1 duels
		else if (!_partyDuel)
		{
			// Duel was interrupted e.g.: player was attacked by mobs / other players
			if ((_playerA.getDuelState() == DUELSTATE_INTERRUPTED) || (_playerB.getDuelState() == DUELSTATE_INTERRUPTED))
			{
				return DuelResult.CANCELED;
			}
			
			// Are the players too far apart?
			if (!_playerA.isInsideRadius2D(_playerB, 1600))
			{
				return DuelResult.CANCELED;
			}
			
			// Did one of the players engage in PvP combat?
			if (isDuelistInPvp(true))
			{
				return DuelResult.CANCELED;
			}
			
			// is one of the players in a Siege, Peace or PvP zone?
			if (_playerA.isInsideZone(ZoneId.PEACE) || _playerB.isInsideZone(ZoneId.PEACE) || _playerA.isInsideZone(ZoneId.SIEGE) || _playerB.isInsideZone(ZoneId.SIEGE) || _playerA.isInsideZone(ZoneId.PVP) || _playerB.isInsideZone(ZoneId.PVP))
			{
				return DuelResult.CANCELED;
			}
		}
		
		return DuelResult.CONTINUE;
	}
	
	/**
	 * Register a surrender request
	 * @param player the player that surrenders.
	 */
	public void doSurrender(PlayerInstance player)
	{
		// already received a surrender request
		if (_surrenderRequest != 0)
		{
			return;
		}
		
		// stop the fight
		stopFighting();
		
		// TODO: Can every party member cancel a party duel? or only the party leaders?
		if (_partyDuel)
		{
			if (_playerA.getParty().getMembers().contains(player))
			{
				_surrenderRequest = 1;
				for (PlayerInstance temp : _playerA.getParty().getMembers())
				{
					temp.setDuelState(DUELSTATE_DEAD);
				}
				for (PlayerInstance temp : _playerB.getParty().getMembers())
				{
					temp.setDuelState(DUELSTATE_WINNER);
				}
			}
			else if (_playerB.getParty().getMembers().contains(player))
			{
				_surrenderRequest = 2;
				for (PlayerInstance temp : _playerB.getParty().getMembers())
				{
					temp.setDuelState(DUELSTATE_DEAD);
				}
				for (PlayerInstance temp : _playerA.getParty().getMembers())
				{
					temp.setDuelState(DUELSTATE_WINNER);
				}
				
			}
		}
		else if (player == _playerA)
		{
			_surrenderRequest = 1;
			_playerA.setDuelState(DUELSTATE_DEAD);
			_playerB.setDuelState(DUELSTATE_WINNER);
		}
		else if (player == _playerB)
		{
			_surrenderRequest = 2;
			_playerB.setDuelState(DUELSTATE_DEAD);
			_playerA.setDuelState(DUELSTATE_WINNER);
		}
	}
	
	/**
	 * This function is called whenever a player was defeated in a duel
	 * @param player the player defeated.
	 */
	public void onPlayerDefeat(PlayerInstance player)
	{
		// Set player as defeated
		player.setDuelState(DUELSTATE_DEAD);
		
		if (_partyDuel)
		{
			boolean teamdefeated = player.getParty().getMembers().stream().anyMatch(member -> member.getDuelState() == DUELSTATE_DUELLING);
			
			if (teamdefeated)
			{
				final PlayerInstance winner = _playerA.getParty().getMembers().contains(player) ? _playerB : _playerA;
				for (PlayerInstance temp : winner.getParty().getMembers())
				{
					temp.setDuelState(DUELSTATE_WINNER);
				}
			}
		}
		else
		{
			if ((player != _playerA) && (player != _playerB))
			{
				LOGGER.warning("Error in onPlayerDefeat(): player is not part of this 1vs1 duel");
			}
			
			if (_playerA == player)
			{
				_playerB.setDuelState(DUELSTATE_WINNER);
			}
			else
			{
				_playerA.setDuelState(DUELSTATE_WINNER);
			}
		}
	}
	
	/**
	 * This function is called whenever a player leaves a party
	 * @param player the player quitting.
	 */
	public void onRemoveFromParty(PlayerInstance player)
	{
		// if it isn't a party duel ignore this
		if (!_partyDuel)
		{
			return;
		}
		
		// this player is leaving his party during party duel
		// if he's either playerA or playerB cancel the duel and port the players back
		if ((player == _playerA) || (player == _playerB))
		{
			for (PlayerCondition cond : _playerConditions.values())
			{
				cond.teleportBack();
				cond.getPlayer().setIsInDuel(0);
			}
			
			_playerA = null;
			_playerB = null;
		}
		else
		// teleport the player back & delete his PlayerCondition record
		{
			final PlayerCondition cond = _playerConditions.remove(player.getObjectId());
			if (cond != null)
			{
				cond.teleportBack();
			}
			player.setIsInDuel(0);
		}
	}
	
	public void onBuff(PlayerInstance player, Skill debuff)
	{
		final PlayerCondition cond = _playerConditions.get(player.getObjectId());
		if (cond != null)
		{
			cond.registerDebuff(debuff);
		}
	}
}