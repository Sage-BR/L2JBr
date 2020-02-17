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
package org.l2jbr.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.CeremonyOfChaosState;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2jbr.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.eventengine.AbstractEventManager;
import org.l2jbr.gameserver.model.eventengine.ScheduleTarget;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerBypass;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jbr.gameserver.model.events.returns.TerminateReturn;
import org.l2jbr.gameserver.model.olympiad.OlympiadManager;
import org.l2jbr.gameserver.model.punishment.PunishmentAffect;
import org.l2jbr.gameserver.model.punishment.PunishmentType;
import org.l2jbr.gameserver.model.variables.PlayerVariables;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.network.serverpackets.ceremonyofchaos.ExCuriousHouseState;

/**
 * @author Sdw
 */
public class CeremonyOfChaosManager extends AbstractEventManager<CeremonyOfChaosEvent>
{
	protected static final Logger LOGGER = Logger.getLogger(CeremonyOfChaosManager.class.getName());
	
	public static final String INITIAL_BUFF_KEY = "initial_buff";
	public static final String INITIAL_ITEMS_KEY = "initial_items";
	public static final String MAX_PLAYERS_KEY = "max_players";
	public static final String MAX_ARENAS_KEY = "max_arenas";
	public static final String INSTANCE_TEMPLATES_KEY = "instance_templates";
	public static final String END_BUFFS_KEYH = "end_buffs";
	
	protected CeremonyOfChaosManager()
	{
	}
	
	@Override
	public void onInitialized()
	{
		if (getState() == null)
		{
			setState(CeremonyOfChaosState.SCHEDULED);
		}
	}
	
	@ScheduleTarget
	private void onPeriodEnd(String text)
	{
		// Set monthly true hero.
		GlobalVariablesManager.getInstance().set(GlobalVariablesManager.COC_TRUE_HERO, GlobalVariablesManager.getInstance().getInt(GlobalVariablesManager.COC_TOP_MEMBER, 0));
		GlobalVariablesManager.getInstance().set(GlobalVariablesManager.COC_TRUE_HERO_REWARDED, false);
		// Reset monthly winner.
		GlobalVariablesManager.getInstance().set(GlobalVariablesManager.COC_TOP_MARKS, 0);
		GlobalVariablesManager.getInstance().set(GlobalVariablesManager.COC_TOP_MEMBER, 0);
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ?"))
		{
			ps.setString(1, PlayerVariables.CEREMONY_OF_CHAOS_MARKS);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.severe(getClass().getSimpleName() + ": Could not reset Ceremony Of Chaos victories: " + e);
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var = ?"))
		{
			ps.setString(1, PlayerVariables.CEREMONY_OF_CHAOS_PROHIBITED_PENALTIES);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.severe(getClass().getSimpleName() + ": Could not reset Ceremony Of Chaos penalties: " + e);
		}
		
		// Update data for online players.
		World.getInstance().getPlayers().stream().forEach(player ->
		{
			player.getVariables().remove(PlayerVariables.CEREMONY_OF_CHAOS_PROHIBITED_PENALTIES);
			player.getVariables().remove(PlayerVariables.CEREMONY_OF_CHAOS_MARKS);
			player.getVariables().storeMe();
		});
		
		LOGGER.info(getClass().getSimpleName() + ": Ceremony of Chaos variables have been reset.");
		LOGGER.info(getClass().getSimpleName() + ": Ceremony of Chaos period has ended!");
	}
	
	@ScheduleTarget
	private void onEventStart()
	{
		LOGGER.info(getClass().getSimpleName() + ": Ceremony of Chaos event has started!");
	}
	
	@ScheduleTarget
	private void onEventEnd()
	{
		PunishmentManager.getInstance().stopPunishment(PunishmentAffect.CHARACTER, PunishmentType.COC_BAN);
		LOGGER.info(getClass().getSimpleName() + ": Ceremony of Chaos event has ended!");
	}
	
	@ScheduleTarget
	private void onRegistrationStart()
	{
		if (getState() != CeremonyOfChaosState.SCHEDULED)
		{
			return;
		}
		
		setState(CeremonyOfChaosState.REGISTRATION);
		for (PlayerInstance player : World.getInstance().getPlayers())
		{
			if (player.isOnline())
			{
				player.sendPacket(SystemMessageId.REGISTRATION_FOR_THE_CEREMONY_OF_CHAOS_HAS_BEGUN);
				if (canRegister(player, false))
				{
					player.sendPacket(ExCuriousHouseState.REGISTRATION_PACKET);
				}
			}
		}
	}
	
	@ScheduleTarget
	private void onRegistrationEnd()
	{
		if (getState() != CeremonyOfChaosState.REGISTRATION)
		{
			return;
		}
		
		setState(CeremonyOfChaosState.PREPARING_FOR_TELEPORT);
		for (PlayerInstance player : World.getInstance().getPlayers())
		{
			if (player.isOnline())
			{
				player.sendPacket(SystemMessageId.REGISTRATION_FOR_THE_CEREMONY_OF_CHAOS_HAS_ENDED);
				if (!isRegistered(player))
				{
					player.sendPacket(ExCuriousHouseState.IDLE_PACKET);
				}
			}
		}
		
		getTimers().addTimer("count_down", StatsSet.valueOf("time", 60), 60 * 1000, null, null);
	}
	
	@ScheduleTarget
	private void onPrepareForFight()
	{
		if (getState() != CeremonyOfChaosState.PREPARING_FOR_TELEPORT)
		{
			return;
		}
		
		setState(CeremonyOfChaosState.PREPARING_FOR_FIGHT);
		int eventId = 0;
		int position = 1;
		CeremonyOfChaosEvent event = null;
		final List<PlayerInstance> players = getRegisteredPlayers().stream().sorted(Comparator.comparingInt(PlayerInstance::getLevel)).collect(Collectors.toList());
		final int maxPlayers = getMaxPlayersInArena();
		final List<Integer> templates = getVariables().getList(INSTANCE_TEMPLATES_KEY, Integer.class);
		
		for (PlayerInstance player : players)
		{
			if (player.isOnline() && canRegister(player, true))
			{
				if ((event == null) || (event.getMembers().size() >= maxPlayers))
				{
					final int template = templates.get(Rnd.get(templates.size()));
					event = new CeremonyOfChaosEvent(eventId++, InstanceManager.getInstance().getInstanceTemplate(template));
					position = 1;
					getEvents().add(event);
				}
				
				event.addMember(new CeremonyOfChaosMember(player, event, position++));
			}
			else
			{
				player.prohibiteCeremonyOfChaos();
				player.sendPacket(ExCuriousHouseState.IDLE_PACKET);
			}
		}
		
		// Clear previously registrated players
		getRegisteredPlayers().clear();
		
		// Prepare all event's players for start
		getEvents().forEach(CeremonyOfChaosEvent::preparePlayers);
	}
	
	@ScheduleTarget
	private void onStartFight()
	{
		if (getState() != CeremonyOfChaosState.PREPARING_FOR_FIGHT)
		{
			return;
		}
		
		setState(CeremonyOfChaosState.RUNNING);
		getEvents().forEach(CeremonyOfChaosEvent::startFight);
	}
	
	@ScheduleTarget
	private void onEndFight()
	{
		if (getState() != CeremonyOfChaosState.RUNNING)
		{
			return;
		}
		
		setState(CeremonyOfChaosState.SCHEDULED);
		getEvents().forEach(CeremonyOfChaosEvent::stopFight);
		getEvents().clear();
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "count_down":
			{
				final int time = params.getInt("time", 0);
				final SystemMessage countdown = new SystemMessage(SystemMessageId.YOU_WILL_BE_MOVED_TO_THE_ARENA_IN_S1_SECOND_S);
				countdown.addByte(time);
				broadcastPacket(countdown);
				
				// Reschedule
				if (time == 60)
				{
					getTimers().addTimer(event, params.set("time", 10), 50 * 1000, null, null);
				}
				else if (time == 10)
				{
					getTimers().addTimer(event, params.set("time", 5), 5 * 1000, null, null);
				}
				else if ((time > 1) && (time <= 5))
				{
					getTimers().addTimer(event, params.set("time", time - 1), 1000, null, null);
				}
				break;
			}
		}
	}
	
	public void broadcastPacket(IClientOutgoingPacket... packets)
	{
		getRegisteredPlayers().forEach(member -> member.sendPacket(packets));
	}
	
	@Override
	public boolean canRegister(PlayerInstance player, boolean sendMessage)
	{
		boolean canRegister = true;
		
		final Clan clan = player.getClan();
		
		SystemMessageId sm = null;
		
		if (player.getLevel() < 85)
		{
			sm = SystemMessageId.ONLY_CHARACTERS_LEVEL_85_OR_ABOVE_MAY_PARTICIPATE_IN_THE_TOURNAMENT;
			canRegister = false;
		}
		else if (player.isFlyingMounted())
		{
			sm = SystemMessageId.YOU_CANNOT_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_AS_A_FLYING_TRANSFORMED_OBJECT;
			canRegister = false;
		}
		else if (!player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
		{
			sm = SystemMessageId.ONLY_CHARACTERS_WHO_HAVE_COMPLETED_THE_3RD_CLASS_TRANSFER_MAY_PARTICIPATE;
			canRegister = false;
		}
		else if (!player.isInventoryUnder80(false) || (player.getWeightPenalty() != 0))
		{
			sm = SystemMessageId.NOT_ENOUGH_SPACE_IN_THE_INVENTORY_UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY;
			canRegister = false;
		}
		else if ((clan == null) || (clan.getLevel() < 6))
		{
			sm = SystemMessageId.ONLY_CHARACTERS_WHO_ARE_A_PART_OF_A_CLAN_OF_LEVEL_6_OR_ABOVE_MAY_PARTICIPATE;
			canRegister = false;
		}
		else if (getRegisteredPlayers().size() >= (getVariables().getInt(MAX_ARENAS_KEY, 5) * getMaxPlayersInArena()))
		{
			sm = SystemMessageId.THERE_ARE_TOO_MANY_CHALLENGERS_YOU_CANNOT_PARTICIPATE_NOW;
			canRegister = false;
		}
		else if (player.isCursedWeaponEquipped() || (player.getReputation() < 0))
		{
			sm = SystemMessageId.WAITING_LIST_REGISTRATION_IS_NOT_ALLOWED_WHILE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE;
			canRegister = false;
		}
		else if (player.isInDuel())
		{
			sm = SystemMessageId.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_DURING_A_DUEL;
			canRegister = false;
		}
		else if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			sm = SystemMessageId.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_OLYMPIAD;
			canRegister = false;
		}
		else if (player.isOnEvent(CeremonyOfChaosEvent.class) || (player.getBlockCheckerArena() > -1)) // TODO underground coliseum and kratei checks.
		{
			sm = SystemMessageId.YOU_CANNOT_REGISTER_FOR_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_THE_BLOCK_CHECKER_COLISEUM_OLYMPIAD_KRATEI_S_CUBE_CEREMONY_OF_CHAOS;
			canRegister = false;
		}
		else if (player.isInInstance())
		{
			sm = SystemMessageId.YOU_MAY_NOT_REGISTER_WHILE_USING_THE_INSTANT_ZONE;
			canRegister = false;
		}
		else if (player.isInSiege())
		{
			sm = SystemMessageId.YOU_CANNOT_REGISTER_FOR_THE_WAITING_LIST_ON_THE_BATTLEFIELD_CASTLE_SIEGE_FORTRESS_SIEGE;
			canRegister = false;
		}
		else if (player.isInsideZone(ZoneId.SIEGE))
		{
			sm = SystemMessageId.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_BEING_INSIDE_OF_A_BATTLEGROUND_CASTLE_SIEGE_FORTRESS_SIEGE;
			canRegister = false;
		}
		else if (player.isFlyingMounted())
		{
			sm = SystemMessageId.YOU_CANNOT_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_AS_A_FLYING_TRANSFORMED_OBJECT;
			canRegister = false;
		}
		else if (player.isFishing())
		{
			sm = SystemMessageId.YOU_CANNOT_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_WHILE_FISHING;
			canRegister = false;
		}
		else if (player.isCeremonyOfChaosProhibited())
		{
			canRegister = false;
		}
		
		// TODO : One player can take part in 16 matches per day.
		
		if ((sm != null) && sendMessage)
		{
			player.sendPacket(sm);
		}
		
		return canRegister;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public TerminateReturn OnPlayerBypass(OnPlayerBypass event)
	{
		final PlayerInstance player = event.getPlayer();
		if (player == null)
		{
			return null;
		}
		
		if (event.getCommand().equalsIgnoreCase("pledgegame?command=apply"))
		{
			if (registerPlayer(player))
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOW_ON_THE_WAITING_LIST_YOU_WILL_AUTOMATICALLY_BE_TELEPORTED_WHEN_THE_TOURNAMENT_STARTS_AND_WILL_BE_REMOVED_FROM_THE_WAITING_LIST_IF_YOU_LOG_OUT_IF_YOU_CANCEL_REGISTRATION_WITHIN_THE_LAST_MINUTE_OF_ENTERING_THE_ARENA_AFTER_SIGNING_UP_30_TIMES_OR_MORE_OR_FORFEIT_AFTER_ENTERING_THE_ARENA_30_TIMES_OR_MORE_DURING_A_CYCLE_YOU_BECOME_INELIGIBLE_FOR_PARTICIPATION_IN_THE_CEREMONY_OF_CHAOS_UNTIL_THE_NEXT_CYCLE_ALL_THE_BUFFS_EXCEPT_THE_VITALITY_BUFF_WILL_BE_REMOVED_ONCE_YOU_ENTER_THE_ARENAS);
				player.sendPacket(SystemMessageId.EXCEPT_THE_VITALITY_BUFF_ALL_BUFFS_INCLUDING_ART_OF_SEDUCTION_WILL_BE_DELETED);
				player.sendPacket(ExCuriousHouseState.PREPARE_PACKET);
			}
			return new TerminateReturn(true, false, false);
		}
		return null;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		if (getState() == CeremonyOfChaosState.REGISTRATION)
		{
			final PlayerInstance player = event.getPlayer();
			if (canRegister(player, false))
			{
				player.sendPacket(ExCuriousHouseState.REGISTRATION_PACKET);
			}
		}
	}
	
	@Override
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void OnPlayerLogout(OnPlayerLogout event)
	{
		if (getState() == CeremonyOfChaosState.REGISTRATION)
		{
			final PlayerInstance player = event.getPlayer();
			if (getRegisteredPlayers().contains(player))
			{
				getRegisteredPlayers().remove(player);
			}
		}
	}
	
	// player leave clan
	
	public int getMaxPlayersInArena()
	{
		return getVariables().getInt(MAX_PLAYERS_KEY, 18);
	}
	
	public static CeremonyOfChaosManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CeremonyOfChaosManager INSTANCE = new CeremonyOfChaosManager();
	}
}
