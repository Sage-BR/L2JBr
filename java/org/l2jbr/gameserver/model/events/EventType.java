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
package org.l2jbr.gameserver.model.events;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.model.events.impl.IBaseEvent;
import org.l2jbr.gameserver.model.events.impl.OnDayNightChange;
import org.l2jbr.gameserver.model.events.impl.ceremonyofchaos.OnCeremonyOfChaosMatchResult;
import org.l2jbr.gameserver.model.events.impl.clan.OnClanWarFinish;
import org.l2jbr.gameserver.model.events.impl.clan.OnClanWarStart;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureAttack;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureAttackAvoid;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureAttacked;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureDamageDealt;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureDamageReceived;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureDeath;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureHpChange;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureKilled;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSee;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSkillFinishCast;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSkillUse;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureTeleport;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureTeleported;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureZoneEnter;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureZoneExit;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnAttackableAggroRangeEnter;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnAttackableAttack;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnAttackableFactionCall;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnAttackableHate;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnAttackableKill;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcCanBeSeen;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcCreatureSee;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcDespawn;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcEventReceived;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcFirstTalk;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcManorBypass;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcMenuSelect;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcMoveFinished;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcMoveNodeArrived;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcMoveRouteFinished;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcSkillFinished;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcSkillSee;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcSpawn;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcTeleport;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcTeleportRequest;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayableExpChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerAbilityPointsChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerAugment;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerBypass;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerCallToChangeClass;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerChangeToAwakenedClass;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerChat;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanCreate;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanDestroy;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanJoin;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanLeaderChange;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanLeft;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanLvlUp;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanWHItemAdd;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanWHItemDestroy;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerClanWHItemTransfer;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerCreate;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerDelete;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerDlgAnswer;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerEquipItem;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerFameChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerFishing;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerHennaAdd;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerHennaRemove;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerItemAdd;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerItemDestroy;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerItemDrop;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerItemPickup;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerItemTransfer;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLevelChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMenteeAdd;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMenteeLeft;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMenteeRemove;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMenteeStatus;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMentorStatus;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMoveRequest;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPKChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPressTutorialMark;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerProfessionCancel;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerProfessionChange;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPvPChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPvPKill;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerQuestAbort;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerQuestComplete;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerReputationChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerRestore;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSelect;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSkillLearn;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSocialAction;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSubChange;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSummonAgathion;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSummonSpawn;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSummonTalk;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerTransform;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerUnsummonAgathion;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnTrapAction;
import org.l2jbr.gameserver.model.events.impl.instance.OnInstanceCreated;
import org.l2jbr.gameserver.model.events.impl.instance.OnInstanceDestroy;
import org.l2jbr.gameserver.model.events.impl.instance.OnInstanceEnter;
import org.l2jbr.gameserver.model.events.impl.instance.OnInstanceLeave;
import org.l2jbr.gameserver.model.events.impl.instance.OnInstanceStatusChange;
import org.l2jbr.gameserver.model.events.impl.item.OnItemAttributeAdd;
import org.l2jbr.gameserver.model.events.impl.item.OnItemBypassEvent;
import org.l2jbr.gameserver.model.events.impl.item.OnItemCreate;
import org.l2jbr.gameserver.model.events.impl.item.OnItemEnchantAdd;
import org.l2jbr.gameserver.model.events.impl.item.OnItemSoulCrystalAdd;
import org.l2jbr.gameserver.model.events.impl.item.OnItemTalk;
import org.l2jbr.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2jbr.gameserver.model.events.impl.server.OnPacketReceived;
import org.l2jbr.gameserver.model.events.impl.server.OnPacketSent;
import org.l2jbr.gameserver.model.events.impl.sieges.OnCastleSiegeFinish;
import org.l2jbr.gameserver.model.events.impl.sieges.OnCastleSiegeOwnerChange;
import org.l2jbr.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
import org.l2jbr.gameserver.model.events.impl.sieges.OnFortSiegeFinish;
import org.l2jbr.gameserver.model.events.impl.sieges.OnFortSiegeStart;
import org.l2jbr.gameserver.model.events.returns.ChatFilterReturn;
import org.l2jbr.gameserver.model.events.returns.DamageReturn;
import org.l2jbr.gameserver.model.events.returns.LocationReturn;
import org.l2jbr.gameserver.model.events.returns.TerminateReturn;

/**
 * @author UnAfraid
 */
public enum EventType
{
	// Attackable events
	ON_ATTACKABLE_AGGRO_RANGE_ENTER(OnAttackableAggroRangeEnter.class, void.class),
	ON_ATTACKABLE_ATTACK(OnAttackableAttack.class, void.class),
	ON_ATTACKABLE_FACTION_CALL(OnAttackableFactionCall.class, void.class),
	ON_ATTACKABLE_KILL(OnAttackableKill.class, void.class),
	
	// Castle events
	ON_CASTLE_SIEGE_FINISH(OnCastleSiegeFinish.class, void.class),
	ON_CASTLE_SIEGE_OWNER_CHANGE(OnCastleSiegeOwnerChange.class, void.class),
	ON_CASTLE_SIEGE_START(OnCastleSiegeStart.class, void.class),
	
	// Clan events
	ON_CLAN_WAR_FINISH(OnClanWarFinish.class, void.class),
	ON_CLAN_WAR_START(OnClanWarStart.class, void.class),
	
	// Creature events
	ON_CREATURE_ATTACK(OnCreatureAttack.class, void.class, TerminateReturn.class),
	ON_CREATURE_ATTACK_AVOID(OnCreatureAttackAvoid.class, void.class, void.class),
	ON_CREATURE_ATTACKED(OnCreatureAttacked.class, void.class, TerminateReturn.class),
	ON_CREATURE_DAMAGE_RECEIVED(OnCreatureDamageReceived.class, void.class, DamageReturn.class),
	ON_CREATURE_DAMAGE_DEALT(OnCreatureDamageDealt.class, void.class),
	ON_CREATURE_HP_CHANGE(OnCreatureHpChange.class, void.class),
	ON_CREATURE_DEATH(OnCreatureDeath.class, void.class),
	ON_CREATURE_KILLED(OnCreatureKilled.class, void.class, TerminateReturn.class),
	ON_CREATURE_SEE(OnCreatureSee.class, void.class),
	ON_CREATURE_SKILL_USE(OnCreatureSkillUse.class, void.class, TerminateReturn.class),
	ON_CREATURE_SKILL_FINISH_CAST(OnCreatureSkillFinishCast.class, void.class),
	ON_CREATURE_TELEPORT(OnCreatureTeleport.class, void.class, LocationReturn.class),
	ON_CREATURE_TELEPORTED(OnCreatureTeleported.class, void.class),
	ON_CREATURE_ZONE_ENTER(OnCreatureZoneEnter.class, void.class),
	ON_CREATURE_ZONE_EXIT(OnCreatureZoneExit.class, void.class),
	
	// Fortress events
	ON_FORT_SIEGE_FINISH(OnFortSiegeFinish.class, void.class),
	ON_FORT_SIEGE_START(OnFortSiegeStart.class, void.class),
	
	// Item events
	ON_ITEM_BYPASS_EVENT(OnItemBypassEvent.class, void.class),
	ON_ITEM_CREATE(OnItemCreate.class, void.class),
	ON_ITEM_TALK(OnItemTalk.class, void.class),
	ON_ITEM_ATTRIBUTE_ADD(OnItemAttributeAdd.class, void.class),
	ON_ITEM_SOUL_CRYSTAL_ADD(OnItemSoulCrystalAdd.class, void.class),
	ON_ITEM_ENCHANT_ADD(OnItemEnchantAdd.class, void.class),
	
	// NPC events
	ON_NPC_CAN_BE_SEEN(OnNpcCanBeSeen.class, void.class, TerminateReturn.class),
	ON_NPC_CREATURE_SEE(OnNpcCreatureSee.class, void.class),
	ON_NPC_EVENT_RECEIVED(OnNpcEventReceived.class, void.class),
	ON_NPC_FIRST_TALK(OnNpcFirstTalk.class, void.class),
	ON_NPC_HATE(OnAttackableHate.class, void.class, TerminateReturn.class),
	ON_NPC_MOVE_FINISHED(OnNpcMoveFinished.class, void.class),
	ON_NPC_MOVE_NODE_ARRIVED(OnNpcMoveNodeArrived.class, void.class),
	ON_NPC_MOVE_ROUTE_FINISHED(OnNpcMoveRouteFinished.class, void.class),
	ON_NPC_QUEST_START(null, void.class),
	ON_NPC_SKILL_FINISHED(OnNpcSkillFinished.class, void.class),
	ON_NPC_SKILL_SEE(OnNpcSkillSee.class, void.class),
	ON_NPC_SPAWN(OnNpcSpawn.class, void.class),
	ON_NPC_TALK(null, void.class),
	ON_NPC_TELEPORT(OnNpcTeleport.class, void.class),
	ON_NPC_MANOR_BYPASS(OnNpcManorBypass.class, void.class),
	ON_NPC_MENU_SELECT(OnNpcMenuSelect.class, void.class),
	ON_NPC_DESPAWN(OnNpcDespawn.class, void.class),
	ON_NPC_TELEPORT_REQUEST(OnNpcTeleportRequest.class, void.class, TerminateReturn.class),
	
	// Olympiad events
	ON_OLYMPIAD_MATCH_RESULT(OnOlympiadMatchResult.class, void.class),
	
	// Ceremony of Chaos events
	ON_CEREMONY_OF_CHAOS_MATCH_RESULT(OnCeremonyOfChaosMatchResult.class, void.class),
	
	// Playable events
	ON_PLAYABLE_EXP_CHANGED(OnPlayableExpChanged.class, void.class, TerminateReturn.class),
	
	// Player events
	ON_PLAYER_AUGMENT(OnPlayerAugment.class, void.class),
	ON_PLAYER_BYPASS(OnPlayerBypass.class, void.class, TerminateReturn.class),
	ON_PLAYER_CALL_TO_CHANGE_CLASS(OnPlayerCallToChangeClass.class, void.class),
	ON_PLAYER_CHAT(OnPlayerChat.class, void.class, ChatFilterReturn.class),
	ON_PLAYER_ABILITY_POINTS_CHANGED(OnPlayerAbilityPointsChanged.class, void.class),
	// Clan events
	ON_PLAYER_CLAN_CREATE(OnPlayerClanCreate.class, void.class),
	ON_PLAYER_CLAN_DESTROY(OnPlayerClanDestroy.class, void.class),
	ON_PLAYER_CLAN_JOIN(OnPlayerClanJoin.class, void.class),
	ON_PLAYER_CLAN_LEADER_CHANGE(OnPlayerClanLeaderChange.class, void.class),
	ON_PLAYER_CLAN_LEFT(OnPlayerClanLeft.class, void.class),
	ON_PLAYER_CLAN_LVLUP(OnPlayerClanLvlUp.class, void.class),
	// Clan warehouse events
	ON_PLAYER_CLAN_WH_ITEM_ADD(OnPlayerClanWHItemAdd.class, void.class),
	ON_PLAYER_CLAN_WH_ITEM_DESTROY(OnPlayerClanWHItemDestroy.class, void.class),
	ON_PLAYER_CLAN_WH_ITEM_TRANSFER(OnPlayerClanWHItemTransfer.class, void.class),
	ON_PLAYER_CREATE(OnPlayerCreate.class, void.class),
	ON_PLAYER_DELETE(OnPlayerDelete.class, void.class),
	ON_PLAYER_DLG_ANSWER(OnPlayerDlgAnswer.class, void.class, TerminateReturn.class),
	ON_PLAYER_EQUIP_ITEM(OnPlayerEquipItem.class, void.class),
	ON_PLAYER_FAME_CHANGED(OnPlayerFameChanged.class, void.class),
	ON_PLAYER_FISHING(OnPlayerFishing.class, void.class),
	// Henna events
	ON_PLAYER_HENNA_ADD(OnPlayerHennaAdd.class, void.class),
	ON_PLAYER_HENNA_REMOVE(OnPlayerHennaRemove.class, void.class),
	// Inventory events
	ON_PLAYER_ITEM_ADD(OnPlayerItemAdd.class, void.class),
	ON_PLAYER_ITEM_DESTROY(OnPlayerItemDestroy.class, void.class),
	ON_PLAYER_ITEM_DROP(OnPlayerItemDrop.class, void.class),
	ON_PLAYER_ITEM_PICKUP(OnPlayerItemPickup.class, void.class),
	ON_PLAYER_ITEM_TRANSFER(OnPlayerItemTransfer.class, void.class),
	// Mentoring events
	ON_PLAYER_MENTEE_ADD(OnPlayerMenteeAdd.class, void.class),
	ON_PLAYER_MENTEE_LEFT(OnPlayerMenteeLeft.class, void.class),
	ON_PLAYER_MENTEE_REMOVE(OnPlayerMenteeRemove.class, void.class),
	ON_PLAYER_MENTEE_STATUS(OnPlayerMenteeStatus.class, void.class),
	ON_PLAYER_MENTOR_STATUS(OnPlayerMentorStatus.class, void.class),
	// Other player events
	ON_PLAYER_REPUTATION_CHANGED(OnPlayerReputationChanged.class, void.class),
	ON_PLAYER_LEVEL_CHANGED(OnPlayerLevelChanged.class, void.class),
	ON_PLAYER_LOGIN(OnPlayerLogin.class, void.class),
	ON_PLAYER_LOGOUT(OnPlayerLogout.class, void.class),
	ON_PLAYER_PK_CHANGED(OnPlayerPKChanged.class, void.class),
	ON_PLAYER_PRESS_TUTORIAL_MARK(OnPlayerPressTutorialMark.class, void.class),
	ON_PLAYER_MOVE_REQUEST(OnPlayerMoveRequest.class, void.class, TerminateReturn.class),
	ON_PLAYER_PROFESSION_CHANGE(OnPlayerProfessionChange.class, void.class),
	ON_PLAYER_PROFESSION_CANCEL(OnPlayerProfessionCancel.class, void.class),
	ON_PLAYER_CHANGE_TO_AWAKENED_CLASS(OnPlayerChangeToAwakenedClass.class, void.class),
	ON_PLAYER_PVP_CHANGED(OnPlayerPvPChanged.class, void.class),
	ON_PLAYER_PVP_KILL(OnPlayerPvPKill.class, void.class),
	ON_PLAYER_RESTORE(OnPlayerRestore.class, void.class),
	ON_PLAYER_SELECT(OnPlayerSelect.class, void.class, TerminateReturn.class),
	ON_PLAYER_SOCIAL_ACTION(OnPlayerSocialAction.class, void.class),
	ON_PLAYER_SKILL_LEARN(OnPlayerSkillLearn.class, void.class),
	ON_PLAYER_SUMMON_SPAWN(OnPlayerSummonSpawn.class, void.class),
	ON_PLAYER_SUMMON_TALK(OnPlayerSummonTalk.class, void.class),
	ON_PLAYER_TRANSFORM(OnPlayerTransform.class, void.class),
	ON_PLAYER_SUB_CHANGE(OnPlayerSubChange.class, void.class),
	ON_PLAYER_QUEST_ABORT(OnPlayerQuestAbort.class, void.class),
	ON_PLAYER_QUEST_COMPLETE(OnPlayerQuestComplete.class, void.class),
	ON_PLAYER_SUMMON_AGATHION(OnPlayerSummonAgathion.class, void.class),
	ON_PLAYER_UNSUMMON_AGATHION(OnPlayerUnsummonAgathion.class, void.class),
	
	// Trap events
	ON_TRAP_ACTION(OnTrapAction.class, void.class),
	
	ON_DAY_NIGHT_CHANGE(OnDayNightChange.class, void.class),
	
	ON_PACKET_RECEIVED(OnPacketReceived.class, void.class),
	ON_PACKET_SENT(OnPacketSent.class, void.class),
	
	// Instance events
	ON_INSTANCE_CREATED(OnInstanceCreated.class, void.class),
	ON_INSTANCE_DESTROY(OnInstanceDestroy.class, void.class),
	ON_INSTANCE_ENTER(OnInstanceEnter.class, void.class),
	ON_INSTANCE_LEAVE(OnInstanceLeave.class, void.class),
	ON_INSTANCE_STATUS_CHANGE(OnInstanceStatusChange.class, void.class);
	
	private final Class<? extends IBaseEvent> _eventClass;
	private final Class<?>[] _returnClass;
	
	EventType(Class<? extends IBaseEvent> eventClass, Class<?>... returnClasss)
	{
		_eventClass = eventClass;
		_returnClass = returnClasss;
	}
	
	public Class<? extends IBaseEvent> getEventClass()
	{
		return _eventClass;
	}
	
	public Class<?>[] getReturnClasses()
	{
		return _returnClass;
	}
	
	public boolean isEventClass(Class<?> clazz)
	{
		return _eventClass == clazz;
	}
	
	public boolean isReturnClass(Class<?> clazz)
	{
		return CommonUtil.contains(_returnClass, clazz);
	}
}
