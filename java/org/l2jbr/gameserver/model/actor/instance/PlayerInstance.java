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

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.l2jbr.Config;
import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.GameTimeController;
import org.l2jbr.gameserver.ItemsAutoDestroy;
import org.l2jbr.gameserver.LoginServerThread;
import org.l2jbr.gameserver.ai.CreatureAI;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.ai.PlayerAI;
import org.l2jbr.gameserver.ai.SummonAI;
import org.l2jbr.gameserver.cache.WarehouseCacheManager;
import org.l2jbr.gameserver.communitybbs.BB.Forum;
import org.l2jbr.gameserver.communitybbs.Manager.ForumsBBSManager;
import org.l2jbr.gameserver.data.sql.impl.CharNameTable;
import org.l2jbr.gameserver.data.sql.impl.CharSummonTable;
import org.l2jbr.gameserver.data.sql.impl.ClanTable;
import org.l2jbr.gameserver.data.xml.impl.AdminData;
import org.l2jbr.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2jbr.gameserver.data.xml.impl.CategoryData;
import org.l2jbr.gameserver.data.xml.impl.ClassListData;
import org.l2jbr.gameserver.data.xml.impl.ExperienceData;
import org.l2jbr.gameserver.data.xml.impl.HennaData;
import org.l2jbr.gameserver.data.xml.impl.MonsterBookData;
import org.l2jbr.gameserver.data.xml.impl.NpcData;
import org.l2jbr.gameserver.data.xml.impl.NpcNameLocalisationData;
import org.l2jbr.gameserver.data.xml.impl.PetDataTable;
import org.l2jbr.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2jbr.gameserver.data.xml.impl.PlayerXpPercentLostData;
import org.l2jbr.gameserver.data.xml.impl.RecipeData;
import org.l2jbr.gameserver.data.xml.impl.SendMessageLocalisationData;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.data.xml.impl.SkillTreesData;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.enums.AdminTeleportType;
import org.l2jbr.gameserver.enums.BroochJewel;
import org.l2jbr.gameserver.enums.CastleSide;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.ClanWarState;
import org.l2jbr.gameserver.enums.Faction;
import org.l2jbr.gameserver.enums.GroupType;
import org.l2jbr.gameserver.enums.HtmlActionScope;
import org.l2jbr.gameserver.enums.IllegalActionPunishmentType;
import org.l2jbr.gameserver.enums.InstanceType;
import org.l2jbr.gameserver.enums.ItemGrade;
import org.l2jbr.gameserver.enums.MountType;
import org.l2jbr.gameserver.enums.NextActionType;
import org.l2jbr.gameserver.enums.PartyDistributionType;
import org.l2jbr.gameserver.enums.PartySmallWindowUpdateType;
import org.l2jbr.gameserver.enums.PlayerAction;
import org.l2jbr.gameserver.enums.PrivateStoreType;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.enums.Sex;
import org.l2jbr.gameserver.enums.ShortcutType;
import org.l2jbr.gameserver.enums.StatusUpdateType;
import org.l2jbr.gameserver.enums.SubclassInfoType;
import org.l2jbr.gameserver.enums.Team;
import org.l2jbr.gameserver.enums.UserInfoType;
import org.l2jbr.gameserver.geoengine.GeoEngine;
import org.l2jbr.gameserver.handler.IItemHandler;
import org.l2jbr.gameserver.handler.ItemHandler;
import org.l2jbr.gameserver.idfactory.IdFactory;
import org.l2jbr.gameserver.instancemanager.AntiFeedManager;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jbr.gameserver.instancemanager.DuelManager;
import org.l2jbr.gameserver.instancemanager.FortManager;
import org.l2jbr.gameserver.instancemanager.FortSiegeManager;
import org.l2jbr.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jbr.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2jbr.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2jbr.gameserver.instancemanager.MatchingRoomManager;
import org.l2jbr.gameserver.instancemanager.MentorManager;
import org.l2jbr.gameserver.instancemanager.PunishmentManager;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.instancemanager.SellBuffsManager;
import org.l2jbr.gameserver.instancemanager.SiegeManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.AccessLevel;
import org.l2jbr.gameserver.model.ArenaParticipantsHolder;
import org.l2jbr.gameserver.model.BlockList;
import org.l2jbr.gameserver.model.CommandChannel;
import org.l2jbr.gameserver.model.ContactList;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Macro;
import org.l2jbr.gameserver.model.MacroList;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.Party.MessageType;
import org.l2jbr.gameserver.model.PetData;
import org.l2jbr.gameserver.model.PetLevelData;
import org.l2jbr.gameserver.model.PlayerCondOverride;
import org.l2jbr.gameserver.model.PremiumItem;
import org.l2jbr.gameserver.model.Radar;
import org.l2jbr.gameserver.model.Request;
import org.l2jbr.gameserver.model.ShortCuts;
import org.l2jbr.gameserver.model.Shortcut;
import org.l2jbr.gameserver.model.SkillLearn;
import org.l2jbr.gameserver.model.TeleportBookmark;
import org.l2jbr.gameserver.model.TeleportWhereType;
import org.l2jbr.gameserver.model.TimeStamp;
import org.l2jbr.gameserver.model.TradeList;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.Playable;
import org.l2jbr.gameserver.model.actor.Summon;
import org.l2jbr.gameserver.model.actor.Vehicle;
import org.l2jbr.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2jbr.gameserver.model.actor.request.AbstractRequest;
import org.l2jbr.gameserver.model.actor.request.SayuneRequest;
import org.l2jbr.gameserver.model.actor.stat.PlayerStat;
import org.l2jbr.gameserver.model.actor.status.PlayerStatus;
import org.l2jbr.gameserver.model.actor.tasks.player.DismountTask;
import org.l2jbr.gameserver.model.actor.tasks.player.FameTask;
import org.l2jbr.gameserver.model.actor.tasks.player.HennaDurationTask;
import org.l2jbr.gameserver.model.actor.tasks.player.InventoryEnableTask;
import org.l2jbr.gameserver.model.actor.tasks.player.PetFeedTask;
import org.l2jbr.gameserver.model.actor.tasks.player.PvPFlagTask;
import org.l2jbr.gameserver.model.actor.tasks.player.RecoGiveTask;
import org.l2jbr.gameserver.model.actor.tasks.player.RentPetTask;
import org.l2jbr.gameserver.model.actor.tasks.player.ResetChargesTask;
import org.l2jbr.gameserver.model.actor.tasks.player.ResetSoulsTask;
import org.l2jbr.gameserver.model.actor.tasks.player.SitDownTask;
import org.l2jbr.gameserver.model.actor.tasks.player.StandUpTask;
import org.l2jbr.gameserver.model.actor.tasks.player.TeleportWatchdogTask;
import org.l2jbr.gameserver.model.actor.tasks.player.WarnUserTakeBreakTask;
import org.l2jbr.gameserver.model.actor.tasks.player.WaterTask;
import org.l2jbr.gameserver.model.actor.templates.PlayerTemplate;
import org.l2jbr.gameserver.model.actor.transform.Transform;
import org.l2jbr.gameserver.model.base.ClassId;
import org.l2jbr.gameserver.model.base.SubClass;
import org.l2jbr.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.clan.ClanMember;
import org.l2jbr.gameserver.model.clan.ClanPrivilege;
import org.l2jbr.gameserver.model.clan.ClanWar;
import org.l2jbr.gameserver.model.cubic.CubicInstance;
import org.l2jbr.gameserver.model.effects.EffectFlag;
import org.l2jbr.gameserver.model.effects.EffectType;
import org.l2jbr.gameserver.model.entity.Castle;
import org.l2jbr.gameserver.model.entity.Duel;
import org.l2jbr.gameserver.model.entity.Fort;
import org.l2jbr.gameserver.model.entity.GameEvent;
import org.l2jbr.gameserver.model.entity.Hero;
import org.l2jbr.gameserver.model.entity.Siege;
import org.l2jbr.gameserver.model.eventengine.AbstractEvent;
import org.l2jbr.gameserver.model.events.EventDispatcher;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerAbilityPointsChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerEquipItem;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerFameChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerHennaAdd;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerHennaRemove;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMenteeStatus;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMentorStatus;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPKChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerProfessionCancel;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerProfessionChange;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPvPChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerPvPKill;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerReputationChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerSubChange;
import org.l2jbr.gameserver.model.fishing.Fishing;
import org.l2jbr.gameserver.model.holders.AttendanceInfoHolder;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.holders.MonsterBookCardHolder;
import org.l2jbr.gameserver.model.holders.MonsterBookRewardHolder;
import org.l2jbr.gameserver.model.holders.MovieHolder;
import org.l2jbr.gameserver.model.holders.PlayerEventHolder;
import org.l2jbr.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2jbr.gameserver.model.holders.RecipeHolder;
import org.l2jbr.gameserver.model.holders.SellBuffHolder;
import org.l2jbr.gameserver.model.holders.SkillUseHolder;
import org.l2jbr.gameserver.model.holders.TrainingHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.interfaces.ILocational;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.itemcontainer.ItemContainer;
import org.l2jbr.gameserver.model.itemcontainer.PlayerFreight;
import org.l2jbr.gameserver.model.itemcontainer.PlayerInventory;
import org.l2jbr.gameserver.model.itemcontainer.PlayerRefund;
import org.l2jbr.gameserver.model.itemcontainer.PlayerWarehouse;
import org.l2jbr.gameserver.model.items.Armor;
import org.l2jbr.gameserver.model.items.EtcItem;
import org.l2jbr.gameserver.model.items.Henna;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.Weapon;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.items.type.ActionType;
import org.l2jbr.gameserver.model.items.type.ArmorType;
import org.l2jbr.gameserver.model.items.type.CrystalType;
import org.l2jbr.gameserver.model.items.type.EtcItemType;
import org.l2jbr.gameserver.model.items.type.WeaponType;
import org.l2jbr.gameserver.model.matching.MatchingRoom;
import org.l2jbr.gameserver.model.olympiad.OlympiadGameManager;
import org.l2jbr.gameserver.model.olympiad.OlympiadGameTask;
import org.l2jbr.gameserver.model.olympiad.OlympiadManager;
import org.l2jbr.gameserver.model.punishment.PunishmentAffect;
import org.l2jbr.gameserver.model.punishment.PunishmentTask;
import org.l2jbr.gameserver.model.punishment.PunishmentType;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.skills.AbnormalType;
import org.l2jbr.gameserver.model.skills.BuffInfo;
import org.l2jbr.gameserver.model.skills.CommonSkill;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.model.skills.SkillCastingType;
import org.l2jbr.gameserver.model.skills.targets.TargetType;
import org.l2jbr.gameserver.model.stats.BaseStats;
import org.l2jbr.gameserver.model.stats.Formulas;
import org.l2jbr.gameserver.model.stats.MoveType;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.model.variables.AccountVariables;
import org.l2jbr.gameserver.model.variables.PlayerVariables;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.WaterZone;
import org.l2jbr.gameserver.network.Disconnection;
import org.l2jbr.gameserver.network.GameClient;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.AbstractHtmlPacket;
import org.l2jbr.gameserver.network.serverpackets.AcquireSkillList;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;
import org.l2jbr.gameserver.network.serverpackets.ChangeWaitType;
import org.l2jbr.gameserver.network.serverpackets.CharInfo;
import org.l2jbr.gameserver.network.serverpackets.ConfirmDlg;
import org.l2jbr.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2jbr.gameserver.network.serverpackets.ExAbnormalStatusUpdateFromTarget;
import org.l2jbr.gameserver.network.serverpackets.ExAdenaInvenCount;
import org.l2jbr.gameserver.network.serverpackets.ExAlterSkillRequest;
import org.l2jbr.gameserver.network.serverpackets.ExAutoSoulShot;
import org.l2jbr.gameserver.network.serverpackets.ExBrPremiumState;
import org.l2jbr.gameserver.network.serverpackets.ExDuelUpdateUserInfo;
import org.l2jbr.gameserver.network.serverpackets.ExGetBookMarkInfoPacket;
import org.l2jbr.gameserver.network.serverpackets.ExGetOnAirShip;
import org.l2jbr.gameserver.network.serverpackets.ExMagicAttackInfo;
import org.l2jbr.gameserver.network.serverpackets.ExOlympiadMode;
import org.l2jbr.gameserver.network.serverpackets.ExPledgeCount;
import org.l2jbr.gameserver.network.serverpackets.ExPrivateStoreSetWholeMsg;
import org.l2jbr.gameserver.network.serverpackets.ExQuestItemList;
import org.l2jbr.gameserver.network.serverpackets.ExSetCompassZoneCode;
import org.l2jbr.gameserver.network.serverpackets.ExStartScenePlayer;
import org.l2jbr.gameserver.network.serverpackets.ExStopScenePlayer;
import org.l2jbr.gameserver.network.serverpackets.ExStorageMaxCount;
import org.l2jbr.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2jbr.gameserver.network.serverpackets.ExUseSharedGroupItem;
import org.l2jbr.gameserver.network.serverpackets.ExUserInfoAbnormalVisualEffect;
import org.l2jbr.gameserver.network.serverpackets.ExUserInfoCubic;
import org.l2jbr.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2jbr.gameserver.network.serverpackets.GetOnVehicle;
import org.l2jbr.gameserver.network.serverpackets.HennaInfo;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2jbr.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jbr.gameserver.network.serverpackets.ItemList;
import org.l2jbr.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jbr.gameserver.network.serverpackets.MyTargetSelected;
import org.l2jbr.gameserver.network.serverpackets.NicknameChanged;
import org.l2jbr.gameserver.network.serverpackets.ObservationMode;
import org.l2jbr.gameserver.network.serverpackets.ObservationReturn;
import org.l2jbr.gameserver.network.serverpackets.PartySmallWindowUpdate;
import org.l2jbr.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import org.l2jbr.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2jbr.gameserver.network.serverpackets.PrivateStoreListBuy;
import org.l2jbr.gameserver.network.serverpackets.PrivateStoreListSell;
import org.l2jbr.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import org.l2jbr.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import org.l2jbr.gameserver.network.serverpackets.PrivateStoreMsgSell;
import org.l2jbr.gameserver.network.serverpackets.RecipeShopMsg;
import org.l2jbr.gameserver.network.serverpackets.RecipeShopSellList;
import org.l2jbr.gameserver.network.serverpackets.RelationChanged;
import org.l2jbr.gameserver.network.serverpackets.Ride;
import org.l2jbr.gameserver.network.serverpackets.SetupGauge;
import org.l2jbr.gameserver.network.serverpackets.ShortCutInit;
import org.l2jbr.gameserver.network.serverpackets.SkillCoolTime;
import org.l2jbr.gameserver.network.serverpackets.SkillList;
import org.l2jbr.gameserver.network.serverpackets.Snoop;
import org.l2jbr.gameserver.network.serverpackets.SocialAction;
import org.l2jbr.gameserver.network.serverpackets.StatusUpdate;
import org.l2jbr.gameserver.network.serverpackets.StopMove;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.network.serverpackets.TargetSelected;
import org.l2jbr.gameserver.network.serverpackets.TargetUnselected;
import org.l2jbr.gameserver.network.serverpackets.TradeDone;
import org.l2jbr.gameserver.network.serverpackets.TradeOtherDone;
import org.l2jbr.gameserver.network.serverpackets.TradeStart;
import org.l2jbr.gameserver.network.serverpackets.UserInfo;
import org.l2jbr.gameserver.network.serverpackets.ValidateLocation;
import org.l2jbr.gameserver.network.serverpackets.commission.ExResponseCommissionInfo;
import org.l2jbr.gameserver.network.serverpackets.friend.FriendStatus;
import org.l2jbr.gameserver.network.serverpackets.monsterbook.ExMonsterBook;
import org.l2jbr.gameserver.network.serverpackets.monsterbook.ExMonsterBookCloseForce;
import org.l2jbr.gameserver.network.serverpackets.monsterbook.ExMonsterBookRewardIcon;
import org.l2jbr.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2jbr.gameserver.util.Broadcast;
import org.l2jbr.gameserver.util.EnumIntBitmask;
import org.l2jbr.gameserver.util.FloodProtectors;
import org.l2jbr.gameserver.util.Util;

/**
 * This class represents all player characters in the world.<br>
 * There is always a client-thread connected to this (except if a player-store is activated upon logout).
 */
public class PlayerInstance extends Playable
{
	// Character Skill SQL String Definitions:
	private static final String RESTORE_SKILLS_FOR_CHAR = "SELECT skill_id,skill_level,skill_sub_level FROM character_skills WHERE charId=? AND class_index=?";
	private static final String UPDATE_CHARACTER_SKILL_LEVEL = "UPDATE character_skills SET skill_level=?, skill_sub_level=?  WHERE skill_id=? AND charId=? AND class_index=?";
	private static final String ADD_NEW_SKILLS = "REPLACE INTO character_skills (charId,skill_id,skill_level,skill_sub_level,class_index) VALUES (?,?,?,?,?)";
	private static final String DELETE_SKILL_FROM_CHAR = "DELETE FROM character_skills WHERE skill_id=? AND charId=? AND class_index=?";
	private static final String DELETE_CHAR_SKILLS = "DELETE FROM character_skills WHERE charId=? AND class_index=?";
	
	// Character Skill Save SQL String Definitions:
	private static final String ADD_SKILL_SAVE = "INSERT INTO character_skills_save (charId,skill_id,skill_level,skill_sub_level,remaining_time,reuse_delay,systime,restore_type,class_index,buff_index) VALUES (?,?,?,?,?,?,?,?,?,?)";
	private static final String RESTORE_SKILL_SAVE = "SELECT skill_id,skill_level,skill_sub_level,remaining_time, reuse_delay, systime, restore_type FROM character_skills_save WHERE charId=? AND class_index=? ORDER BY buff_index ASC";
	private static final String DELETE_SKILL_SAVE = "DELETE FROM character_skills_save WHERE charId=? AND class_index=?";
	
	// Character Item Reuse Time String Definition:
	private static final String ADD_ITEM_REUSE_SAVE = "INSERT INTO character_item_reuse_save (charId,itemId,itemObjId,reuseDelay,systime) VALUES (?,?,?,?,?)";
	private static final String RESTORE_ITEM_REUSE_SAVE = "SELECT charId,itemId,itemObjId,reuseDelay,systime FROM character_item_reuse_save WHERE charId=?";
	private static final String DELETE_ITEM_REUSE_SAVE = "DELETE FROM character_item_reuse_save WHERE charId=?";
	
	// Character Character SQL String Definitions:
	private static final String INSERT_CHARACTER = "INSERT INTO characters (account_name,charId,char_name,level,maxHp,curHp,maxCp,curCp,maxMp,curMp,face,hairStyle,hairColor,sex,exp,sp,reputation,fame,raidbossPoints,pvpkills,pkkills,clanid,race,classid,deletetime,cancraft,title,title_color,online,clan_privs,wantspeace,base_class,nobless,power_grade,vitality_points,createDate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE_CHARACTER = "UPDATE characters SET level=?,maxHp=?,curHp=?,maxCp=?,curCp=?,maxMp=?,curMp=?,face=?,hairStyle=?,hairColor=?,sex=?,heading=?,x=?,y=?,z=?,exp=?,expBeforeDeath=?,sp=?,reputation=?,fame=?,raidbossPoints=?,pvpkills=?,pkkills=?,clanid=?,race=?,classid=?,deletetime=?,title=?,title_color=?,online=?,clan_privs=?,wantspeace=?,base_class=?,onlinetime=?,nobless=?,power_grade=?,subpledge=?,lvl_joined_academy=?,apprentice=?,sponsor=?,clan_join_expiry_time=?,clan_create_expiry_time=?,char_name=?,bookmarkslot=?,vitality_points=?,language=?,faction=?,pccafe_points=? WHERE charId=?";
	private static final String UPDATE_CHARACTER_ACCESS = "UPDATE characters SET accesslevel = ? WHERE charId = ?";
	private static final String RESTORE_CHARACTER = "SELECT * FROM characters WHERE charId=?";
	
	// Character Teleport Bookmark:
	private static final String INSERT_TP_BOOKMARK = "INSERT INTO character_tpbookmark (charId,Id,x,y,z,icon,tag,name) values (?,?,?,?,?,?,?,?)";
	private static final String UPDATE_TP_BOOKMARK = "UPDATE character_tpbookmark SET icon=?,tag=?,name=? where charId=? AND Id=?";
	private static final String RESTORE_TP_BOOKMARK = "SELECT Id,x,y,z,icon,tag,name FROM character_tpbookmark WHERE charId=?";
	private static final String DELETE_TP_BOOKMARK = "DELETE FROM character_tpbookmark WHERE charId=? AND Id=?";
	
	// Character Subclass SQL String Definitions:
	private static final String RESTORE_CHAR_SUBCLASSES = "SELECT class_id,exp,sp,level,vitality_points,class_index,dual_class FROM character_subclasses WHERE charId=? ORDER BY class_index ASC";
	private static final String ADD_CHAR_SUBCLASS = "INSERT INTO character_subclasses (charId,class_id,exp,sp,level,vitality_points,class_index,dual_class) VALUES (?,?,?,?,?,?,?,?)";
	private static final String UPDATE_CHAR_SUBCLASS = "UPDATE character_subclasses SET exp=?,sp=?,level=?,vitality_points=?,class_id=?,dual_class=? WHERE charId=? AND class_index =?";
	private static final String DELETE_CHAR_SUBCLASS = "DELETE FROM character_subclasses WHERE charId=? AND class_index=?";
	
	// Character Henna SQL String Definitions:
	private static final String RESTORE_CHAR_HENNAS = "SELECT slot,symbol_id FROM character_hennas WHERE charId=? AND class_index=?";
	private static final String ADD_CHAR_HENNA = "REPLACE INTO character_hennas (charId,symbol_id,slot,class_index) VALUES (?,?,?,?)";
	private static final String DELETE_CHAR_HENNA = "DELETE FROM character_hennas WHERE charId=? AND slot=? AND class_index=?";
	private static final String DELETE_CHAR_HENNAS = "DELETE FROM character_hennas WHERE charId=? AND class_index=?";
	
	// Character Shortcut SQL String Definitions:
	private static final String DELETE_CHAR_SHORTCUTS = "DELETE FROM character_shortcuts WHERE charId=? AND class_index=?";
	
	// Character Recipe List Save
	private static final String DELETE_CHAR_RECIPE_SHOP = "DELETE FROM character_recipeshoplist WHERE charId=?";
	private static final String INSERT_CHAR_RECIPE_SHOP = "REPLACE INTO character_recipeshoplist (`charId`, `recipeId`, `price`, `index`) VALUES (?, ?, ?, ?)";
	private static final String RESTORE_CHAR_RECIPE_SHOP = "SELECT * FROM character_recipeshoplist WHERE charId=? ORDER BY `index`";
	
	private static final String COND_OVERRIDE_KEY = "cond_override";
	
	public static final String NEWBIE_KEY = "NEWBIE";
	
	public static final int ID_NONE = -1;
	
	public static final int REQUEST_TIMEOUT = 15;
	
	private int _pcCafePoints = 0;
	
	private GameClient _client;
	private String _ip = "N/A";
	
	private final String _accountName;
	private long _deleteTimer;
	private Calendar _createDate = Calendar.getInstance();
	
	private String _lang = null;
	private String _htmlPrefix = "";
	
	private volatile boolean _isOnline = false;
	private long _onlineTime;
	private long _onlineBeginTime;
	private long _lastAccess;
	private long _uptime;
	
	private final ReentrantLock _subclassLock = new ReentrantLock();
	protected int _baseClass;
	protected int _activeClass;
	protected int _classIndex = 0;
	
	/** data for mounted pets */
	private int _controlItemId;
	private PetData _data;
	private PetLevelData _leveldata;
	private int _curFeed;
	protected Future<?> _mountFeedTask;
	private ScheduledFuture<?> _dismountTask;
	private boolean _petItems = false;
	
	/** The list of sub-classes this character has. */
	private final Map<Integer, SubClass> _subClasses = new ConcurrentHashMap<>();
	
	private static final String ORIGINAL_CLASS_VAR = "OriginalClass";
	
	private final PlayerAppearance _appearance;
	
	/** The Experience of the PlayerInstance before the last Death Penalty */
	private long _expBeforeDeath;
	
	/** The number of player killed during a PvP (the player killed was PvP Flagged) */
	private int _pvpKills;
	
	/** The PK counter of the PlayerInstance (= Number of non PvP Flagged player killed) */
	private int _pkKills;
	
	/** The PvP Flag state of the PlayerInstance (0=White, 1=Purple) */
	private byte _pvpFlag;
	
	/** The Fame of this PlayerInstance */
	private int _fame;
	private ScheduledFuture<?> _fameTask;
	
	/** The Raidboss points of this PlayerInstance */
	private int _raidbossPoints;
	
	private volatile ScheduledFuture<?> _teleportWatchdog;
	
	/** The Siege state of the PlayerInstance */
	private byte _siegeState = 0;
	
	/** The id of castle/fort which the PlayerInstance is registered for siege */
	private int _siegeSide = 0;
	
	private int _curWeightPenalty = 0;
	
	private int _lastCompassZone; // the last compass zone update send to the client
	
	private final ContactList _contactList = new ContactList(this);
	
	private int _bookmarkslot = 0; // The Teleport Bookmark Slot
	
	private final Map<Integer, TeleportBookmark> _tpbookmarks = new ConcurrentSkipListMap<>();
	
	private boolean _canFeed;
	private boolean _isInSiege;
	private boolean _isInHideoutSiege = false;
	
	/** Olympiad */
	private boolean _inOlympiadMode = false;
	private boolean _OlympiadStart = false;
	private int _olympiadGameId = -1;
	private int _olympiadSide = -1;
	
	/** Duel */
	private boolean _isInDuel = false;
	private boolean _startingDuel = false;
	private int _duelState = Duel.DUELSTATE_NODUEL;
	private int _duelId = 0;
	private SystemMessageId _noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
	
	/** Boat and AirShip */
	private Vehicle _vehicle = null;
	private Location _inVehiclePosition;
	
	private MountType _mountType = MountType.NONE;
	private int _mountNpcId;
	private int _mountLevel;
	/** Store object used to summon the strider you are mounting **/
	private int _mountObjectID = 0;
	
	private AdminTeleportType _teleportType = AdminTeleportType.NORMAL;
	
	private boolean _inCrystallize;
	private volatile boolean _isCrafting;
	
	private long _offlineShopStart = 0;
	
	/** The table containing all RecipeList of the PlayerInstance */
	private final Map<Integer, RecipeHolder> _dwarvenRecipeBook = new ConcurrentSkipListMap<>();
	private final Map<Integer, RecipeHolder> _commonRecipeBook = new ConcurrentSkipListMap<>();
	
	/** Premium Items */
	private final Map<Integer, PremiumItem> _premiumItems = new ConcurrentSkipListMap<>();
	
	/** True if the PlayerInstance is sitting */
	private boolean _waitTypeSitting = false;
	
	/** Location before entering Observer Mode */
	private Location _lastLoc;
	private boolean _observerMode = false;
	
	/** Stored from last ValidatePosition **/
	private final Location _lastServerPosition = new Location(0, 0, 0);
	
	/** The number of recommendation obtained by the PlayerInstance */
	private int _recomHave; // how much I was recommended by others
	/** The number of recommendation that the PlayerInstance can give */
	private int _recomLeft; // how many recommendations I can give to others
	/** Recommendation task **/
	private ScheduledFuture<?> _recoGiveTask;
	/** Recommendation Two Hours bonus **/
	protected boolean _recoTwoHoursGiven = false;
	
	private final PlayerInventory _inventory = new PlayerInventory(this);
	private final PlayerFreight _freight = new PlayerFreight(this);
	private PlayerWarehouse _warehouse;
	private PlayerRefund _refund;
	
	private PrivateStoreType _privateStoreType = PrivateStoreType.NONE;
	
	private TradeList _activeTradeList;
	private ItemContainer _activeWarehouse;
	private volatile Map<Integer, Long> _manufactureItems;
	private String _storeName = "";
	private TradeList _sellList;
	private TradeList _buyList;
	
	// Multisell
	private PreparedMultisellListHolder _currentMultiSell = null;
	
	private int _nobleLevel = 0;
	private boolean _hero = false;
	private boolean _trueHero = false;
	
	/** Premium System */
	private boolean _premiumStatus = false;
	
	/** Faction System */
	private boolean _isGood = false;
	private boolean _isEvil = false;
	
	/** The FolkInstance corresponding to the last Folk which one the player talked. */
	private Npc _lastFolkNpc = null;
	
	/** Last NPC Id talked on a quest */
	private int _questNpcObject = 0;
	
	/** Used for simulating Quest onTalk */
	private boolean _simulatedTalking = false;
	
	/** The table containing all Quests began by the PlayerInstance */
	private final Map<String, QuestState> _quests = new ConcurrentHashMap<>();
	
	/** The list containing all shortCuts of this player. */
	private final ShortCuts _shortCuts = new ShortCuts(this);
	
	/** The list containing all macros of this player. */
	private final MacroList _macros = new MacroList(this);
	
	private final Set<PlayerInstance> _snoopListener = ConcurrentHashMap.newKeySet();
	private final Set<PlayerInstance> _snoopedPlayer = ConcurrentHashMap.newKeySet();
	
	/** Hennas */
	private final Henna[] _henna = new Henna[4];
	private final Map<BaseStats, Integer> _hennaBaseStats = new ConcurrentHashMap<>();
	private final Map<Integer, ScheduledFuture<?>> _hennaRemoveSchedules = new ConcurrentHashMap<>(4);
	
	/** The Pet of the PlayerInstance */
	private PetInstance _pet = null;
	/** Servitors of the PlayerInstance */
	private final Map<Integer, Summon> _servitors = new ConcurrentHashMap<>(1);
	/** The Agathion of the PlayerInstance */
	private int _agathionId = 0;
	// apparently, a PlayerInstance CAN have both a summon AND a tamed beast at the same time!!
	// after Freya players can control more than one tamed beast
	private final Set<TamedBeastInstance> _tamedBeast = ConcurrentHashMap.newKeySet();
	
	// client radar
	// TODO: This needs to be better integrated and saved/loaded
	private final Radar _radar;
	
	private MatchingRoom _matchingRoom;
	
	// Clan related attributes
	/** The Clan Identifier of the PlayerInstance */
	private int _clanId;
	
	/** The Clan object of the PlayerInstance */
	private Clan _clan;
	
	/** Apprentice and Sponsor IDs */
	private int _apprentice = 0;
	private int _sponsor = 0;
	
	private long _clanJoinExpiryTime;
	private long _clanCreateExpiryTime;
	
	private int _powerGrade = 0;
	private volatile EnumIntBitmask<ClanPrivilege> _clanPrivileges = new EnumIntBitmask<>(ClanPrivilege.class, false);
	
	/** PlayerInstance's pledge class (knight, Baron, etc.) */
	private int _pledgeClass = 0;
	private int _pledgeType = 0;
	
	/** Level at which the player joined the clan as an academy member */
	private int _lvlJoinedAcademy = 0;
	
	private int _wantsPeace = 0;
	
	// charges
	private final AtomicInteger _charges = new AtomicInteger();
	private ScheduledFuture<?> _chargeTask = null;
	
	// Absorbed Souls
	private int _souls = 0;
	private ScheduledFuture<?> _soulTask = null;
	
	// WorldPosition used by TARGET_SIGNET_GROUND
	private Location _currentSkillWorldPosition;
	
	private AccessLevel _accessLevel;
	
	private boolean _messageRefusal = false; // message refusal mode
	
	private boolean _silenceMode = false; // silence mode
	private List<Integer> _silenceModeExcluded; // silence mode
	private boolean _dietMode = false; // ignore weight penalty
	private boolean _tradeRefusal = false; // Trade refusal
	private boolean _exchangeRefusal = false; // Exchange refusal
	
	private Party _party;
	
	// this is needed to find the inviting player for Party response
	// there can only be one active party request at once
	private PlayerInstance _activeRequester;
	private long _requestExpireTime = 0;
	private final Request _request = new Request(this);
	
	// Used for protection after teleport
	private long _spawnProtectEndTime = 0;
	private long _teleportProtectEndTime = 0;
	
	private final Map<Integer, ExResponseCommissionInfo> _lastCommissionInfos = new ConcurrentHashMap<>();
	
	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends AbstractEvent>, AbstractEvent<?>> _events = new ConcurrentHashMap<>();
	private boolean _isOnCustomEvent = false;
	
	// protects a char from aggro mobs when getting up from fake death
	private long _recentFakeDeathEndTime = 0;
	
	/** The fists Weapon of the PlayerInstance (used when no weapon is equipped) */
	private Weapon _fistsWeaponItem;
	
	private final Map<Integer, String> _chars = new ConcurrentSkipListMap<>();
	
	// private byte _updateKnownCounter = 0;
	
	private int _createItemLevel;
	private int _createCommonItemLevel;
	private ItemGrade _crystallizeGrade = ItemGrade.NONE;
	private CrystalType _expertiseLevel = CrystalType.NONE;
	private int _expertiseArmorPenalty = 0;
	private int _expertiseWeaponPenalty = 0;
	private int _expertisePenaltyBonus = 0;
	
	private final Map<Class<? extends AbstractRequest>, AbstractRequest> _requests = new ConcurrentHashMap<>();
	
	protected boolean _inventoryDisable = false;
	/** Player's cubics. */
	private final Map<Integer, CubicInstance> _cubics = new ConcurrentSkipListMap<>();
	/** Active shots. */
	protected Set<Integer> _activeSoulShots = ConcurrentHashMap.newKeySet();
	/** Active Brooch Jewels **/
	private BroochJewel _activeRubyJewel = null;
	private BroochJewel _activeShappireJewel = null;
	
	public ReentrantLock soulShotLock = new ReentrantLock();
	
	/** Event parameters */
	private PlayerEventHolder eventStatus = null;
	
	private byte _handysBlockCheckerEventArena = -1;
	
	/** new race ticket **/
	private final int _race[] = new int[2];
	
	private final BlockList _blockList = new BlockList(this);
	
	private final Map<Integer, Skill> _transformSkills = new ConcurrentHashMap<>();
	private ScheduledFuture<?> _taskRentPet;
	private ScheduledFuture<?> _taskWater;
	
	/** Last Html Npcs, 0 = last html was not bound to an npc */
	private final int[] _htmlActionOriginObjectIds = new int[HtmlActionScope.values().length];
	/**
	 * Origin of the last incoming html action request.<br>
	 * This can be used for htmls continuing the conversation with an npc.
	 */
	private int _lastHtmlActionOriginObjId;
	
	/** Bypass validations */
	@SuppressWarnings("unchecked")
	private final LinkedList<String>[] _htmlActionCaches = new LinkedList[HtmlActionScope.values().length];
	
	private Forum _forumMail;
	private Forum _forumMemo;
	
	/** Skills queued because a skill is already in progress */
	private SkillUseHolder _queuedSkill;
	private boolean _alterSkillActive = false;
	
	private int _cursedWeaponEquippedId = 0;
	private boolean _combatFlagEquippedId = false;
	
	private boolean _canRevive = true;
	private int _reviveRequested = 0;
	private double _revivePower = 0;
	private boolean _revivePet = false;
	
	private double _cpUpdateIncCheck = .0;
	private double _cpUpdateDecCheck = .0;
	private double _cpUpdateInterval = .0;
	private double _mpUpdateIncCheck = .0;
	private double _mpUpdateDecCheck = .0;
	private double _mpUpdateInterval = .0;
	
	private double _originalCp = .0;
	private double _originalHp = .0;
	private double _originalMp = .0;
	
	/** Char Coords from Client */
	private int _clientX;
	private int _clientY;
	private int _clientZ;
	private int _clientHeading;
	
	// during fall validations will be disabled for 1000 ms.
	private static final int FALLING_VALIDATION_DELAY = 1000;
	private volatile long _fallingTimestamp = 0;
	private volatile int _fallingDamage = 0;
	private Future<?> _fallingDamageTask = null;
	
	private int _multiSocialTarget = 0;
	private int _multiSociaAction = 0;
	
	private MovieHolder _movieHolder = null;
	
	private String _adminConfirmCmd = null;
	
	private volatile long _lastItemAuctionInfoRequest = 0;
	
	private Future<?> _PvPRegTask;
	
	private long _pvpFlagLasts;
	
	private long _notMoveUntil = 0;
	
	/** Map containing all custom skills of this player. */
	private Map<Integer, Skill> _customSkills = null;
	
	private volatile int _actionMask;
	
	private int _questZoneId = -1;
	
	private final Fishing _fishing = new Fishing(this);
	
	private Future<?> _autoSaveTask = null;
	
	public void setPvpFlagLasts(long time)
	{
		_pvpFlagLasts = time;
	}
	
	public long getPvpFlagLasts()
	{
		return _pvpFlagLasts;
	}
	
	public void startPvPFlag()
	{
		updatePvPFlag(1);
		
		if (_PvPRegTask == null)
		{
			_PvPRegTask = ThreadPool.scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
		}
	}
	
	public void stopPvpRegTask()
	{
		if (_PvPRegTask != null)
		{
			_PvPRegTask.cancel(true);
			_PvPRegTask = null;
		}
	}
	
	public void stopPvPFlag()
	{
		stopPvpRegTask();
		
		updatePvPFlag(0);
		
		_PvPRegTask = null;
	}
	
	// Monster Book variables
	private static final String MONSTER_BOOK_KILLS_VAR = "MONSTER_BOOK_KILLS_";
	private static final String MONSTER_BOOK_LEVEL_VAR = "MONSTER_BOOK_LEVEL_";
	
	// Training Camp
	private static final String TRAINING_CAMP_VAR = "TRAINING_CAMP";
	private static final String TRAINING_CAMP_DURATION = "TRAINING_CAMP_DURATION";
	
	// Attendance Reward system
	private static final String ATTENDANCE_DATE_VAR = "ATTENDANCE_DATE";
	private static final String ATTENDANCE_INDEX_VAR = "ATTENDANCE_INDEX";
	
	// Save responder name for log it
	private String _lastPetitionGmName = null;
	
	private boolean _hasCharmOfCourage = false;
	
	private final Set<Integer> _whisperers = ConcurrentHashMap.newKeySet();
	
	// Selling buffs system
	private boolean _isSellingBuffs = false;
	private List<SellBuffHolder> _sellingBuffs = null;
	
	public boolean isSellingBuffs()
	{
		return _isSellingBuffs;
	}
	
	public void setIsSellingBuffs(boolean val)
	{
		_isSellingBuffs = val;
	}
	
	public List<SellBuffHolder> getSellingBuffs()
	{
		if (_sellingBuffs == null)
		{
			_sellingBuffs = new ArrayList<>();
		}
		return _sellingBuffs;
	}
	
	/**
	 * Create a new PlayerInstance and add it in the characters table of the database.<br>
	 * <B><U> Actions</U> :</B>
	 * <ul>
	 * <li>Create a new PlayerInstance with an account name</li>
	 * <li>Set the name, the Hair Style, the Hair Color and the Face type of the PlayerInstance</li>
	 * <li>Add the player in the characters table of the database</li>
	 * </ul>
	 * @param template The PlayerTemplate to apply to the PlayerInstance
	 * @param accountName The name of the PlayerInstance
	 * @param name The name of the PlayerInstance
	 * @param app the player's appearance
	 * @return The PlayerInstance added to the database or null
	 */
	public static PlayerInstance create(PlayerTemplate template, String accountName, String name, PlayerAppearance app)
	{
		// Create a new PlayerInstance with an account name
		final PlayerInstance player = new PlayerInstance(template, accountName, app);
		// Set the name of the PlayerInstance
		player.setName(name);
		// Set access level
		player.setAccessLevel(0, false, false);
		// Set Character's create time
		player.setCreateDate(Calendar.getInstance());
		// Set the base class ID to that of the actual class ID.
		player.setBaseClass(player.getClassId());
		// Give 20 recommendations
		player.setRecomLeft(20);
		// Add the player in the characters table of the database
		if (player.createDb())
		{
			if (Config.CACHE_CHAR_NAMES)
			{
				CharNameTable.getInstance().addName(player);
			}
			return player;
		}
		return null;
	}
	
	public String getAccountName()
	{
		return _client == null ? _accountName : _client.getAccountName();
	}
	
	public String getAccountNamePlayer()
	{
		return _accountName;
	}
	
	public Map<Integer, String> getAccountChars()
	{
		return _chars;
	}
	
	public int getRelation(PlayerInstance target)
	{
		final Clan clan = getClan();
		final Party party = getParty();
		final Clan targetClan = target.getClan();
		
		int result = 0;
		
		if (clan != null)
		{
			result |= RelationChanged.RELATION_CLAN_MEMBER;
			if (clan == target.getClan())
			{
				result |= RelationChanged.RELATION_CLAN_MATE;
			}
			if (getAllyId() != 0)
			{
				result |= RelationChanged.RELATION_ALLY_MEMBER;
			}
		}
		if (isClanLeader())
		{
			result |= RelationChanged.RELATION_LEADER;
		}
		if ((party != null) && (party == target.getParty()))
		{
			result |= RelationChanged.RELATION_HAS_PARTY;
			for (int i = 0; i < party.getMembers().size(); i++)
			{
				if (party.getMembers().get(i) != this)
				{
					continue;
				}
				switch (i)
				{
					case 0:
					{
						result |= RelationChanged.RELATION_PARTYLEADER; // 0x10
						break;
					}
					case 1:
					{
						result |= RelationChanged.RELATION_PARTY4; // 0x8
						break;
					}
					case 2:
					{
						result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x7
						break;
					}
					case 3:
					{
						result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2; // 0x6
						break;
					}
					case 4:
					{
						result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY1; // 0x5
						break;
					}
					case 5:
					{
						result |= RelationChanged.RELATION_PARTY3; // 0x4
						break;
					}
					case 6:
					{
						result |= RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x3
						break;
					}
					case 7:
					{
						result |= RelationChanged.RELATION_PARTY2; // 0x2
						break;
					}
					case 8:
					{
						result |= RelationChanged.RELATION_PARTY1; // 0x1
						break;
					}
				}
			}
		}
		if (_siegeState != 0)
		{
			result |= RelationChanged.RELATION_INSIEGE;
			if (getSiegeState() != target.getSiegeState())
			{
				result |= RelationChanged.RELATION_ENEMY;
			}
			else
			{
				result |= RelationChanged.RELATION_ALLY;
			}
			if (_siegeState == 1)
			{
				result |= RelationChanged.RELATION_ATTACKER;
			}
		}
		if ((clan != null) && (targetClan != null))
		{
			ClanWar war = clan.getWarWith(target.getClan().getId());
			if (war != null)
			{
				switch (war.getState())
				{
					case DECLARATION:
					case BLOOD_DECLARATION:
					{
						result |= RelationChanged.RELATION_DECLARED_WAR;
						break;
					}
					case MUTUAL:
					{
						result |= RelationChanged.RELATION_DECLARED_WAR;
						result |= RelationChanged.RELATION_MUTUAL_WAR;
						break;
					}
				}
			}
		}
		if (_handysBlockCheckerEventArena != -1)
		{
			result |= RelationChanged.RELATION_INSIEGE;
			final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(getBlockCheckerArena());
			if (holder.getPlayerTeam(this) == 0)
			{
				result |= RelationChanged.RELATION_ENEMY;
			}
			else
			{
				result |= RelationChanged.RELATION_ALLY;
			}
			result |= RelationChanged.RELATION_ATTACKER;
		}
		return result;
	}
	
	/**
	 * Retrieve a PlayerInstance from the characters table of the database and add it in _allObjects of the L2world (call restore method).<br>
	 * <B><U> Actions</U> :</B>
	 * <ul>
	 * <li>Retrieve the PlayerInstance from the characters table of the database</li>
	 * <li>Add the PlayerInstance object in _allObjects</li>
	 * <li>Set the x,y,z position of the PlayerInstance and make it invisible</li>
	 * <li>Update the overloaded status of the PlayerInstance</li>
	 * </ul>
	 * @param objectId Identifier of the object to initialized
	 * @return The PlayerInstance loaded from the database
	 */
	public static PlayerInstance load(int objectId)
	{
		return restore(objectId);
	}
	
	private void initPcStatusUpdateValues()
	{
		_cpUpdateInterval = getMaxCp() / 352.0;
		_cpUpdateIncCheck = getMaxCp();
		_cpUpdateDecCheck = getMaxCp() - _cpUpdateInterval;
		_mpUpdateInterval = getMaxMp() / 352.0;
		_mpUpdateIncCheck = getMaxMp();
		_mpUpdateDecCheck = getMaxMp() - _mpUpdateInterval;
	}
	
	/**
	 * Constructor of PlayerInstance (use Creature constructor).<br>
	 * <B><U> Actions</U> :</B>
	 * <ul>
	 * <li>Call the Creature constructor to create an empty _skills slot and copy basic Calculator set to this PlayerInstance</li>
	 * <li>Set the name of the PlayerInstance</li>
	 * </ul>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method SET the level of the PlayerInstance to 1</B></FONT>
	 * @param objectId Identifier of the object to initialized
	 * @param template The PlayerTemplate to apply to the PlayerInstance
	 * @param accountName The name of the account including this PlayerInstance
	 * @param app
	 */
	private PlayerInstance(int objectId, PlayerTemplate template, String accountName, PlayerAppearance app)
	{
		super(objectId, template);
		setInstanceType(InstanceType.PlayerInstance);
		super.initCharStatusUpdateValues();
		initPcStatusUpdateValues();
		
		for (int i = 0; i < _htmlActionCaches.length; ++i)
		{
			_htmlActionCaches[i] = new LinkedList<>();
		}
		
		_accountName = accountName;
		app.setOwner(this);
		_appearance = app;
		
		// Create an AI
		getAI();
		
		// Create a Radar object
		_radar = new Radar(this);
	}
	
	/**
	 * Creates a player.
	 * @param template the player template
	 * @param accountName the account name
	 * @param app the player appearance
	 */
	private PlayerInstance(PlayerTemplate template, String accountName, PlayerAppearance app)
	{
		this(IdFactory.getInstance().getNextId(), template, accountName, app);
	}
	
	@Override
	public PlayerStat getStat()
	{
		return (PlayerStat) super.getStat();
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new PlayerStat(this));
	}
	
	@Override
	public PlayerStatus getStatus()
	{
		return (PlayerStatus) super.getStatus();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new PlayerStatus(this));
	}
	
	public PlayerAppearance getAppearance()
	{
		return _appearance;
	}
	
	public boolean isHairAccessoryEnabled()
	{
		return getVariables().getBoolean(PlayerVariables.HAIR_ACCESSORY_VARIABLE_NAME, true);
	}
	
	public void setHairAccessoryEnabled(boolean enabled)
	{
		getVariables().set(PlayerVariables.HAIR_ACCESSORY_VARIABLE_NAME, enabled);
	}
	
	/**
	 * @return the base PlayerTemplate link to the PlayerInstance.
	 */
	public PlayerTemplate getBaseTemplate()
	{
		final ClassId originalClass = getOriginalClass();
		if (originalClass != null)
		{
			return PlayerTemplateData.getInstance().getTemplate(originalClass.getId());
		}
		return PlayerTemplateData.getInstance().getTemplate(_baseClass);
	}
	
	public ClassId getOriginalClass()
	{
		return getVariables().getEnum(ORIGINAL_CLASS_VAR, ClassId.class, null);
	}
	
	public void setOriginalClass(ClassId newClass)
	{
		getVariables().set(ORIGINAL_CLASS_VAR, newClass);
	}
	
	public void resetOriginalClass()
	{
		getVariables().remove(ORIGINAL_CLASS_VAR);
	}
	
	/**
	 * @return the PlayerTemplate link to the PlayerInstance.
	 */
	@Override
	public PlayerTemplate getTemplate()
	{
		return (PlayerTemplate) super.getTemplate();
	}
	
	/**
	 * @param newclass
	 */
	public void setTemplate(ClassId newclass)
	{
		super.setTemplate(PlayerTemplateData.getInstance().getTemplate(newclass));
	}
	
	@Override
	protected CreatureAI initAI()
	{
		return new PlayerAI(this);
	}
	
	/** Return the Level of the PlayerInstance. */
	@Override
	public int getLevel()
	{
		return getStat().getLevel();
	}
	
	public void setBaseClass(int baseClass)
	{
		_baseClass = baseClass;
	}
	
	public void setBaseClass(ClassId classId)
	{
		_baseClass = classId.getId();
	}
	
	public boolean isInStoreMode()
	{
		return _privateStoreType != PrivateStoreType.NONE;
	}
	
	public boolean isCrafting()
	{
		return _isCrafting;
	}
	
	public void setIsCrafting(boolean isCrafting)
	{
		_isCrafting = isCrafting;
	}
	
	/**
	 * @return a table containing all Common RecipeList of the PlayerInstance.
	 */
	public Collection<RecipeHolder> getCommonRecipeBook()
	{
		return _commonRecipeBook.values();
	}
	
	/**
	 * @return a table containing all Dwarf RecipeList of the PlayerInstance.
	 */
	public Collection<RecipeHolder> getDwarvenRecipeBook()
	{
		return _dwarvenRecipeBook.values();
	}
	
	/**
	 * Add a new RecipList to the table _commonrecipebook containing all RecipeList of the PlayerInstance
	 * @param recipe The RecipeList to add to the _recipebook
	 * @param saveToDb
	 */
	public void registerCommonRecipeList(RecipeHolder recipe, boolean saveToDb)
	{
		_commonRecipeBook.put(recipe.getId(), recipe);
		
		if (saveToDb)
		{
			insertNewRecipeData(recipe.getId(), false);
		}
	}
	
	/**
	 * Add a new RecipList to the table _recipebook containing all RecipeList of the PlayerInstance
	 * @param recipe The RecipeList to add to the _recipebook
	 * @param saveToDb
	 */
	public void registerDwarvenRecipeList(RecipeHolder recipe, boolean saveToDb)
	{
		_dwarvenRecipeBook.put(recipe.getId(), recipe);
		
		if (saveToDb)
		{
			insertNewRecipeData(recipe.getId(), true);
		}
	}
	
	/**
	 * @param recipeId The Identifier of the RecipeList to check in the player's recipe books
	 * @return {@code true}if player has the recipe on Common or Dwarven Recipe book else returns {@code false}
	 */
	public boolean hasRecipeList(int recipeId)
	{
		return _dwarvenRecipeBook.containsKey(recipeId) || _commonRecipeBook.containsKey(recipeId);
	}
	
	/**
	 * Tries to remove a RecipList from the table _DwarvenRecipeBook or from table _CommonRecipeBook, those table contain all RecipeList of the PlayerInstance
	 * @param recipeId The Identifier of the RecipeList to remove from the _recipebook
	 */
	public void unregisterRecipeList(int recipeId)
	{
		if (_dwarvenRecipeBook.remove(recipeId) != null)
		{
			deleteRecipeData(recipeId, true);
		}
		else if (_commonRecipeBook.remove(recipeId) != null)
		{
			deleteRecipeData(recipeId, false);
		}
		else
		{
			LOGGER.warning("Attempted to remove unknown RecipeList: " + recipeId);
		}
		
		for (Shortcut sc : _shortCuts.getAllShortCuts())
		{
			if ((sc != null) && (sc.getId() == recipeId) && (sc.getType() == ShortcutType.RECIPE))
			{
				deleteShortCut(sc.getSlot(), sc.getPage());
			}
		}
	}
	
	private void insertNewRecipeData(int recipeId, boolean isDwarf)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO character_recipebook (charId, id, classIndex, type) values(?,?,?,?)"))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, recipeId);
			statement.setInt(3, isDwarf ? _classIndex : 0);
			statement.setInt(4, isDwarf ? 1 : 0);
			statement.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "SQL exception while inserting recipe: " + recipeId + " from character " + getObjectId(), e);
		}
	}
	
	private void deleteRecipeData(int recipeId, boolean isDwarf)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_recipebook WHERE charId=? AND id=? AND classIndex=?"))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, recipeId);
			statement.setInt(3, isDwarf ? _classIndex : 0);
			statement.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "SQL exception while deleting recipe: " + recipeId + " from character " + getObjectId(), e);
		}
	}
	
	/**
	 * @return the Id for the last talked quest NPC.
	 */
	public int getLastQuestNpcObject()
	{
		return _questNpcObject;
	}
	
	public void setLastQuestNpcObject(int npcId)
	{
		_questNpcObject = npcId;
	}
	
	public boolean isSimulatingTalking()
	{
		return _simulatedTalking;
	}
	
	public void setSimulatedTalking(boolean value)
	{
		_simulatedTalking = value;
	}
	
	/**
	 * @param quest The name of the quest
	 * @return the QuestState object corresponding to the quest name.
	 */
	public QuestState getQuestState(String quest)
	{
		return _quests.get(quest);
	}
	
	/**
	 * Add a QuestState to the table _quest containing all quests began by the PlayerInstance.
	 * @param qs The QuestState to add to _quest
	 */
	public void setQuestState(QuestState qs)
	{
		_quests.put(qs.getQuestName(), qs);
	}
	
	/**
	 * Verify if the player has the quest state.
	 * @param quest the quest state to check
	 * @return {@code true} if the player has the quest state, {@code false} otherwise
	 */
	public boolean hasQuestState(String quest)
	{
		return _quests.containsKey(quest);
	}
	
	/**
	 * Remove a QuestState from the table _quest containing all quests began by the PlayerInstance.
	 * @param quest The name of the quest
	 */
	public void delQuestState(String quest)
	{
		_quests.remove(quest);
	}
	
	/**
	 * @return List of {@link QuestState}s of the current player.
	 */
	public List<QuestState> getAllQuestStates()
	{
		return new ArrayList<>(_quests.values());
	}
	
	/**
	 * @return a table containing all Quest in progress from the table _quests.
	 */
	public List<Quest> getAllActiveQuests()
	{
		//@formatter:off
		return _quests.values().stream()
			.filter(QuestState::isStarted)
			.map(QuestState::getQuest)
			.filter(Objects::nonNull)
			.filter(q -> q.getId() > 1)
			.collect(Collectors.toList());
		//@formatter:on
	}
	
	public void processQuestEvent(String questName, String event)
	{
		final Quest quest = QuestManager.getInstance().getQuest(questName);
		if ((quest == null) || (event == null) || event.isEmpty())
		{
			return;
		}
		
		final Npc target = _lastFolkNpc;
		
		if ((target != null) && isInsideRadius2D(target, Npc.INTERACTION_DISTANCE))
		{
			quest.notifyEvent(event, target, this);
		}
		else if (_questNpcObject > 0)
		{
			final WorldObject object = World.getInstance().findObject(getLastQuestNpcObject());
			
			if (object.isNpc() && isInsideRadius2D(object, Npc.INTERACTION_DISTANCE))
			{
				final Npc npc = (Npc) object;
				quest.notifyEvent(event, npc, this);
			}
		}
	}
	
	/** List of all QuestState instance that needs to be notified of this PlayerInstance's or its pet's death */
	private volatile Set<QuestState> _notifyQuestOfDeathList;
	
	/**
	 * Add QuestState instance that is to be notified of PlayerInstance's death.
	 * @param qs The QuestState that subscribe to this event
	 */
	public void addNotifyQuestOfDeath(QuestState qs)
	{
		if (qs == null)
		{
			return;
		}
		
		if (!getNotifyQuestOfDeath().contains(qs))
		{
			getNotifyQuestOfDeath().add(qs);
		}
	}
	
	/**
	 * Remove QuestState instance that is to be notified of PlayerInstance's death.
	 * @param qs The QuestState that subscribe to this event
	 */
	public void removeNotifyQuestOfDeath(QuestState qs)
	{
		if ((qs == null) || (_notifyQuestOfDeathList == null))
		{
			return;
		}
		
		_notifyQuestOfDeathList.remove(qs);
	}
	
	/**
	 * @return a list of QuestStates which registered for notify of death of this PlayerInstance.
	 */
	public Set<QuestState> getNotifyQuestOfDeath()
	{
		if (_notifyQuestOfDeathList == null)
		{
			synchronized (this)
			{
				if (_notifyQuestOfDeathList == null)
				{
					_notifyQuestOfDeathList = ConcurrentHashMap.newKeySet();
				}
			}
		}
		
		return _notifyQuestOfDeathList;
	}
	
	public boolean isNotifyQuestOfDeathEmpty()
	{
		return (_notifyQuestOfDeathList == null) || _notifyQuestOfDeathList.isEmpty();
	}
	
	/**
	 * @return a table containing all ShortCut of the PlayerInstance.
	 */
	public Shortcut[] getAllShortCuts()
	{
		return _shortCuts.getAllShortCuts();
	}
	
	/**
	 * @param slot The slot in which the shortCuts is equipped
	 * @param page The page of shortCuts containing the slot
	 * @return the ShortCut of the PlayerInstance corresponding to the position (page-slot).
	 */
	public Shortcut getShortCut(int slot, int page)
	{
		return _shortCuts.getShortCut(slot, page);
	}
	
	/**
	 * Add a L2shortCut to the PlayerInstance _shortCuts
	 * @param shortcut
	 */
	public void registerShortCut(Shortcut shortcut)
	{
		_shortCuts.registerShortCut(shortcut);
	}
	
	/**
	 * Updates the shortcut bars with the new skill.
	 * @param skillId the skill Id to search and update.
	 * @param skillLevel the skill level to update.
	 * @param skillSubLevel the skill sub level to update.
	 */
	public void updateShortCuts(int skillId, int skillLevel, int skillSubLevel)
	{
		_shortCuts.updateShortCuts(skillId, skillLevel, skillSubLevel);
	}
	
	/**
	 * Delete the ShortCut corresponding to the position (page-slot) from the PlayerInstance _shortCuts.
	 * @param slot
	 * @param page
	 */
	public void deleteShortCut(int slot, int page)
	{
		_shortCuts.deleteShortCut(slot, page);
	}
	
	/**
	 * @param macro the macro to add to this PlayerInstance.
	 */
	public void registerMacro(Macro macro)
	{
		_macros.registerMacro(macro);
	}
	
	/**
	 * @param id the macro Id to delete.
	 */
	public void deleteMacro(int id)
	{
		_macros.deleteMacro(id);
	}
	
	/**
	 * @return all Macro of the PlayerInstance.
	 */
	public MacroList getMacros()
	{
		return _macros;
	}
	
	/**
	 * Set the siege state of the PlayerInstance.
	 * @param siegeState 1 = attacker, 2 = defender, 0 = not involved
	 */
	public void setSiegeState(byte siegeState)
	{
		_siegeState = siegeState;
	}
	
	/**
	 * Get the siege state of the PlayerInstance.
	 * @return 1 = attacker, 2 = defender, 0 = not involved
	 */
	public byte getSiegeState()
	{
		return _siegeState;
	}
	
	/**
	 * Set the siege Side of the PlayerInstance.
	 * @param val
	 */
	public void setSiegeSide(int val)
	{
		_siegeSide = val;
	}
	
	public boolean isRegisteredOnThisSiegeField(int val)
	{
		if ((_siegeSide != val) && ((_siegeSide < 81) || (_siegeSide > 89)))
		{
			return false;
		}
		return true;
	}
	
	public int getSiegeSide()
	{
		return _siegeSide;
	}
	
	public boolean isSiegeFriend(WorldObject target)
	{
		// If i'm natural or not in siege zone, not friends.
		if ((_siegeState == 0) || !isInsideZone(ZoneId.SIEGE))
		{
			return false;
		}
		
		// If target isn't a player, is self, isn't on same siege or not on same state, not friends.
		final PlayerInstance targetPlayer = target.getActingPlayer();
		if ((targetPlayer == null) || (targetPlayer == this) || (targetPlayer.getSiegeSide() != _siegeSide) || (_siegeState != targetPlayer.getSiegeState()))
		{
			return false;
		}
		
		// Attackers are considered friends only if castle has no owner.
		if (_siegeState == 1)
		{
			final Castle castle = CastleManager.getInstance().getCastleById(_siegeSide);
			if (castle == null)
			{
				return false;
			}
			if (castle.getOwner() == null)
			{
				return true;
			}
			
			return false;
		}
		
		// Both are defenders, friends.
		return true;
	}
	
	/**
	 * Set the PvP Flag of the PlayerInstance.
	 * @param pvpFlag
	 */
	public void setPvpFlag(int pvpFlag)
	{
		_pvpFlag = (byte) pvpFlag;
	}
	
	@Override
	public byte getPvpFlag()
	{
		return _pvpFlag;
	}
	
	@Override
	public void updatePvPFlag(int value)
	{
		if (_pvpFlag == value)
		{
			return;
		}
		setPvpFlag(value);
		
		final StatusUpdate su = new StatusUpdate(this);
		computeStatusUpdate(su, StatusUpdateType.PVP_FLAG);
		if (su.hasUpdates())
		{
			broadcastPacket(su);
			sendPacket(su);
		}
		
		// If this player has a pet update the pets pvp flag as well
		if (hasSummon())
		{
			final RelationChanged rc = new RelationChanged();
			final Summon pet = _pet;
			if (pet != null)
			{
				rc.addRelation(pet, getRelation(this), false);
			}
			if (hasServitors())
			{
				getServitors().values().forEach(s -> rc.addRelation(s, getRelation(this), false));
			}
			sendPacket(rc);
		}
		
		World.getInstance().forEachVisibleObject(this, PlayerInstance.class, player ->
		{
			if (!isVisibleFor(player))
			{
				return;
			}
			
			final int relation = getRelation(player);
			final Integer oldrelation = getKnownRelations().get(player.getObjectId());
			if ((oldrelation == null) || (oldrelation != relation))
			{
				final RelationChanged rc = new RelationChanged();
				rc.addRelation(this, relation, isAutoAttackable(player));
				if (hasSummon())
				{
					final Summon pet = _pet;
					if (pet != null)
					{
						rc.addRelation(pet, relation, isAutoAttackable(player));
					}
					if (hasServitors())
					{
						getServitors().values().forEach(s -> rc.addRelation(s, relation, isAutoAttackable(player)));
					}
				}
				player.sendPacket(rc);
				getKnownRelations().put(player.getObjectId(), relation);
			}
		});
	}
	
	@Override
	public void revalidateZone(boolean force)
	{
		// Cannot validate if not in a world region (happens during teleport)
		if (getWorldRegion() == null)
		{
			return;
		}
		
		// This function is called too often from movement code
		if (force)
		{
			_zoneValidateCounter = 4;
		}
		else
		{
			_zoneValidateCounter--;
			if (_zoneValidateCounter < 0)
			{
				_zoneValidateCounter = 4;
			}
			else
			{
				return;
			}
		}
		
		ZoneManager.getInstance().getRegion(this).revalidateZones(this);
		
		if (Config.ALLOW_WATER)
		{
			checkWaterState();
		}
		
		if (isInsideZone(ZoneId.ALTERED))
		{
			if (_lastCompassZone == ExSetCompassZoneCode.ALTEREDZONE)
			{
				return;
			}
			_lastCompassZone = ExSetCompassZoneCode.ALTEREDZONE;
			final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.ALTEREDZONE);
			sendPacket(cz);
		}
		else if (isInsideZone(ZoneId.SIEGE))
		{
			if (_lastCompassZone == ExSetCompassZoneCode.SIEGEWARZONE2)
			{
				return;
			}
			_lastCompassZone = ExSetCompassZoneCode.SIEGEWARZONE2;
			final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.SIEGEWARZONE2);
			sendPacket(cz);
		}
		else if (isInsideZone(ZoneId.PVP))
		{
			if (_lastCompassZone == ExSetCompassZoneCode.PVPZONE)
			{
				return;
			}
			_lastCompassZone = ExSetCompassZoneCode.PVPZONE;
			final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.PVPZONE);
			sendPacket(cz);
		}
		else if (isInsideZone(ZoneId.PEACE))
		{
			if (_lastCompassZone == ExSetCompassZoneCode.PEACEZONE)
			{
				return;
			}
			_lastCompassZone = ExSetCompassZoneCode.PEACEZONE;
			final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.PEACEZONE);
			sendPacket(cz);
		}
		else
		{
			if (_lastCompassZone == ExSetCompassZoneCode.GENERALZONE)
			{
				return;
			}
			if (_lastCompassZone == ExSetCompassZoneCode.SIEGEWARZONE2)
			{
				updatePvPStatus();
			}
			_lastCompassZone = ExSetCompassZoneCode.GENERALZONE;
			final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(ExSetCompassZoneCode.GENERALZONE);
			sendPacket(cz);
		}
	}
	
	/**
	 * @return the maximum dwarven recipe level this character can craft.
	 */
	public int getCreateItemLevel()
	{
		return _createItemLevel;
	}
	
	public void setCreateItemLevel(int createItemLevel)
	{
		_createItemLevel = createItemLevel;
	}
	
	/**
	 * @return the maximum common recipe level this character can craft.
	 */
	public int getCreateCommonItemLevel()
	{
		return _createCommonItemLevel;
	}
	
	public void setCreateCommonItemLevel(int createCommonItemLevel)
	{
		_createCommonItemLevel = createCommonItemLevel;
	}
	
	public ItemGrade getCrystallizeGrade()
	{
		return _crystallizeGrade;
	}
	
	public void setCrystallizeGrade(ItemGrade crystallizeGrade)
	{
		_crystallizeGrade = crystallizeGrade != null ? crystallizeGrade : ItemGrade.NONE;
	}
	
	/**
	 * @return the PK counter of the PlayerInstance.
	 */
	public int getPkKills()
	{
		return _pkKills;
	}
	
	/**
	 * Set the PK counter of the PlayerInstance.
	 * @param pkKills
	 */
	public void setPkKills(int pkKills)
	{
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPKChanged(this, _pkKills, pkKills), this);
		_pkKills = pkKills;
	}
	
	/**
	 * @return the _deleteTimer of the PlayerInstance.
	 */
	public long getDeleteTimer()
	{
		return _deleteTimer;
	}
	
	/**
	 * Set the _deleteTimer of the PlayerInstance.
	 * @param deleteTimer
	 */
	public void setDeleteTimer(long deleteTimer)
	{
		_deleteTimer = deleteTimer;
	}
	
	/**
	 * @return the number of recommendation obtained by the PlayerInstance.
	 */
	public int getRecomHave()
	{
		return _recomHave;
	}
	
	/**
	 * Increment the number of recommendation obtained by the PlayerInstance (Max : 255).
	 */
	protected void incRecomHave()
	{
		if (_recomHave < 255)
		{
			_recomHave++;
		}
	}
	
	/**
	 * Set the number of recommendation obtained by the PlayerInstance (Max : 255).
	 * @param value
	 */
	public void setRecomHave(int value)
	{
		_recomHave = Math.min(Math.max(value, 0), 255);
	}
	
	/**
	 * Set the number of recommendation obtained by the PlayerInstance (Max : 255).
	 * @param value
	 */
	public void setRecomLeft(int value)
	{
		_recomLeft = Math.min(Math.max(value, 0), 255);
	}
	
	/**
	 * @return the number of recommendation that the PlayerInstance can give.
	 */
	public int getRecomLeft()
	{
		return _recomLeft;
	}
	
	/**
	 * Increment the number of recommendation that the PlayerInstance can give.
	 */
	protected void decRecomLeft()
	{
		if (_recomLeft > 0)
		{
			_recomLeft--;
		}
	}
	
	public void giveRecom(PlayerInstance target)
	{
		target.incRecomHave();
		decRecomLeft();
	}
	
	/**
	 * Set the exp of the PlayerInstance before a death
	 * @param exp
	 */
	public void setExpBeforeDeath(long exp)
	{
		_expBeforeDeath = exp;
	}
	
	public long getExpBeforeDeath()
	{
		return _expBeforeDeath;
	}
	
	public void setInitialReputation(int reputation)
	{
		super.setReputation(reputation);
	}
	
	/**
	 * Set the reputation of the PlayerInstance and send a Server->Client packet StatusUpdate (broadcast).
	 * @param reputation
	 */
	@Override
	public void setReputation(int reputation)
	{
		// Notify to scripts.
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerReputationChanged(this, getReputation(), reputation), this);
		
		if (reputation > Config.MAX_REPUTATION) // Max count of positive reputation
		{
			reputation = Config.MAX_REPUTATION;
		}
		
		if (getReputation() == reputation)
		{
			return;
		}
		
		if ((getReputation() >= 0) && (reputation < 0))
		{
			World.getInstance().forEachVisibleObject(this, GuardInstance.class, object ->
			{
				if (object.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
				{
					object.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				}
			});
		}
		
		super.setReputation(reputation);
		
		sendPacket(new SystemMessage(SystemMessageId.YOUR_REPUTATION_HAS_BEEN_CHANGED_TO_S1).addInt(getReputation()));
		broadcastReputation();
	}
	
	public int getExpertiseArmorPenalty()
	{
		return _expertiseArmorPenalty;
	}
	
	public int getExpertiseWeaponPenalty()
	{
		return _expertiseWeaponPenalty;
	}
	
	public int getExpertisePenaltyBonus()
	{
		return _expertisePenaltyBonus;
	}
	
	public void setExpertisePenaltyBonus(int bonus)
	{
		_expertisePenaltyBonus = bonus;
	}
	
	public int getWeightPenalty()
	{
		return _dietMode ? 0 : _curWeightPenalty;
	}
	
	/**
	 * Update the overloaded status of the PlayerInstance.
	 * @param broadcast TODO
	 */
	public void refreshOverloaded(boolean broadcast)
	{
		final int maxLoad = getMaxLoad();
		if (maxLoad > 0)
		{
			final long weightproc = (((getCurrentLoad() - getBonusWeightPenalty()) * 1000) / getMaxLoad());
			int newWeightPenalty;
			if ((weightproc < 500) || _dietMode)
			{
				newWeightPenalty = 0;
			}
			else if (weightproc < 666)
			{
				newWeightPenalty = 1;
			}
			else if (weightproc < 800)
			{
				newWeightPenalty = 2;
			}
			else if (weightproc < 1000)
			{
				newWeightPenalty = 3;
			}
			else
			{
				newWeightPenalty = 4;
			}
			
			if (_curWeightPenalty != newWeightPenalty)
			{
				_curWeightPenalty = newWeightPenalty;
				if ((newWeightPenalty > 0) && !_dietMode)
				{
					addSkill(SkillData.getInstance().getSkill(CommonSkill.WEIGHT_PENALTY.getId(), newWeightPenalty));
					setIsOverloaded(getCurrentLoad() > maxLoad);
				}
				else
				{
					removeSkill(getKnownSkill(4270), false, true);
					setIsOverloaded(false);
				}
				if (broadcast)
				{
					sendPacket(new EtcStatusUpdate(this));
					broadcastUserInfo();
				}
			}
		}
	}
	
	public void refreshExpertisePenalty()
	{
		if (!Config.EXPERTISE_PENALTY)
		{
			return;
		}
		
		final CrystalType expertiseLevel = _expertiseLevel.plusLevel(_expertisePenaltyBonus);
		
		int armorPenalty = 0;
		int weaponPenalty = 0;
		
		for (ItemInstance item : _inventory.getPaperdollItems(item -> (item != null) && ((item.getItemType() != EtcItemType.ARROW) && (item.getItemType() != EtcItemType.BOLT)) && item.getItem().getCrystalType().isGreater(expertiseLevel)))
		{
			if (item.isArmor())
			{
				// Armor penalty level increases depending on amount of penalty armors equipped, not grade level difference.
				armorPenalty = CommonUtil.constrain(armorPenalty + 1, 0, 4);
			}
			else
			{
				// Weapon penalty level increases based on grade difference.
				weaponPenalty = CommonUtil.constrain(item.getItem().getCrystalType().getLevel() - expertiseLevel.getLevel(), 0, 4);
			}
		}
		
		boolean changed = false;
		
		if ((_expertiseWeaponPenalty != weaponPenalty) || (getSkillLevel(CommonSkill.WEAPON_GRADE_PENALTY.getId()) != weaponPenalty))
		{
			_expertiseWeaponPenalty = weaponPenalty;
			if (_expertiseWeaponPenalty > 0)
			{
				addSkill(SkillData.getInstance().getSkill(CommonSkill.WEAPON_GRADE_PENALTY.getId(), _expertiseWeaponPenalty));
			}
			else
			{
				removeSkill(CommonSkill.WEAPON_GRADE_PENALTY.getId(), true);
			}
			changed = true;
		}
		
		if ((_expertiseArmorPenalty != armorPenalty) || (getSkillLevel(CommonSkill.ARMOR_GRADE_PENALTY.getId()) != armorPenalty))
		{
			_expertiseArmorPenalty = armorPenalty;
			if (_expertiseArmorPenalty > 0)
			{
				addSkill(SkillData.getInstance().getSkill(CommonSkill.ARMOR_GRADE_PENALTY.getId(), _expertiseArmorPenalty));
			}
			else
			{
				removeSkill(CommonSkill.ARMOR_GRADE_PENALTY.getId(), true);
			}
			changed = true;
		}
		
		if (changed)
		{
			sendSkillList(); // Update expertise penalty icon in skill list.
			sendPacket(new EtcStatusUpdate(this));
		}
	}
	
	public void useEquippableItem(ItemInstance item, boolean abortAttack)
	{
		// Equip or unEquip
		ItemInstance[] items = null;
		final boolean isEquiped = item.isEquipped();
		final int oldInvLimit = getInventoryLimit();
		SystemMessage sm = null;
		
		if (isEquiped)
		{
			if (item.getEnchantLevel() > 0)
			{
				sm = new SystemMessage(SystemMessageId.S1_S2_HAS_BEEN_UNEQUIPPED);
				sm.addInt(item.getEnchantLevel());
				sm.addItemName(item);
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(item);
			}
			sendPacket(sm);
			
			final long slot = _inventory.getSlotFromItem(item);
			// we can't unequip talisman by body slot
			if ((slot == Item.SLOT_DECO) || (slot == Item.SLOT_BROOCH_JEWEL) || (slot == Item.SLOT_AGATHION) || (slot == Item.SLOT_ARTIFACT))
			{
				items = _inventory.unEquipItemInSlotAndRecord(item.getLocationSlot());
			}
			else
			{
				items = _inventory.unEquipItemInBodySlotAndRecord(slot);
			}
		}
		else
		{
			items = _inventory.equipItemAndRecord(item);
			
			if (item.isEquipped())
			{
				if (item.getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.EQUIPPED_S1_S2);
					sm.addInt(item.getEnchantLevel());
					sm.addItemName(item);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.EQUIPPED_S1);
					sm.addItemName(item);
				}
				sendPacket(sm);
				
				// Consume mana - will start a task if required; returns if item is not a shadow item
				item.decreaseMana(false);
				
				if ((item.getItem().getBodyPart() & Item.SLOT_MULTI_ALLWEAPON) != 0)
				{
					rechargeShots(true, true, false);
				}
			}
			else
			{
				sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
			}
		}
		
		refreshExpertisePenalty();
		
		broadcastUserInfo();
		
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addItems(Arrays.asList(items));
		sendInventoryUpdate(iu);
		
		if (abortAttack)
		{
			abortAttack();
		}
		
		if (getInventoryLimit() != oldInvLimit)
		{
			sendPacket(new ExStorageMaxCount(this));
		}
		
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerEquipItem(this, item), this);
	}
	
	/**
	 * @return the the PvP Kills of the PlayerInstance (Number of player killed during a PvP).
	 */
	public int getPvpKills()
	{
		return _pvpKills;
	}
	
	/**
	 * Set the the PvP Kills of the PlayerInstance (Number of player killed during a PvP).
	 * @param pvpKills
	 */
	public void setPvpKills(int pvpKills)
	{
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPvPChanged(this, _pvpKills, pvpKills), this);
		_pvpKills = pvpKills;
	}
	
	/**
	 * @return the Fame of this PlayerInstance
	 */
	public int getFame()
	{
		return _fame;
	}
	
	/**
	 * Set the Fame of this NcInstane
	 * @param fame
	 */
	public void setFame(int fame)
	{
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFameChanged(this, _fame, fame), this);
		_fame = (fame > Config.MAX_PERSONAL_FAME_POINTS) ? Config.MAX_PERSONAL_FAME_POINTS : fame;
	}
	
	/**
	 * @return the Raidboss points of this PlayerInstance
	 */
	public int getRaidbossPoints()
	{
		return _raidbossPoints;
	}
	
	/**
	 * Set the Raidboss points of this PlayerInstance
	 * @param points
	 */
	public void setRaidbossPoints(int points)
	{
		_raidbossPoints = points;
	}
	
	/**
	 * Increase the Raidboss points of this PlayerInstance
	 * @param increasePoints
	 */
	public void increaseRaidbossPoints(int increasePoints)
	{
		setRaidbossPoints(_raidbossPoints + increasePoints);
	}
	
	/**
	 * @return the ClassId object of the PlayerInstance contained in PlayerTemplate.
	 */
	public ClassId getClassId()
	{
		return getTemplate().getClassId();
	}
	
	/**
	 * Set the template of the PlayerInstance.
	 * @param Id The Identifier of the PlayerTemplate to set to the PlayerInstance
	 */
	public void setClassId(int Id)
	{
		if (!_subclassLock.tryLock())
		{
			return;
		}
		
		try
		{
			if ((getLvlJoinedAcademy() != 0) && (_clan != null) && CategoryData.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, Id))
			{
				if (_lvlJoinedAcademy <= 16)
				{
					_clan.addReputationScore(Config.JOIN_ACADEMY_MAX_REP_SCORE, true);
				}
				else if (_lvlJoinedAcademy >= 39)
				{
					_clan.addReputationScore(Config.JOIN_ACADEMY_MIN_REP_SCORE, true);
				}
				else
				{
					_clan.addReputationScore((Config.JOIN_ACADEMY_MAX_REP_SCORE - ((_lvlJoinedAcademy - 16) * 20)), true);
				}
				setLvlJoinedAcademy(0);
				// oust pledge member from the academy, cuz he has finished his 2nd class transfer
				final SystemMessage msg = new SystemMessage(SystemMessageId.S1_HAS_BEEN_DISMISSED);
				msg.addPcName(this);
				_clan.broadcastToOnlineMembers(msg);
				_clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(getName()));
				_clan.removeClanMember(getObjectId(), 0);
				sendPacket(SystemMessageId.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_YOU_CAN_NOW_JOIN_A_CLAN_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES);
				
				// receive graduation gift
				_inventory.addItem("Gift", 8181, 1, this, null); // give academy circlet
			}
			if (isSubClassActive())
			{
				getSubClasses().get(_classIndex).setClassId(Id);
			}
			setTarget(this);
			broadcastPacket(new MagicSkillUse(this, 5103, 1, 1000, 0));
			setClassTemplate(Id);
			if (isInCategory(CategoryType.FOURTH_CLASS_GROUP))
			{
				sendPacket(SystemMessageId.CONGRATULATIONS_YOU_VE_COMPLETED_YOUR_THIRD_CLASS_TRANSFER_QUEST);
			}
			else
			{
				sendPacket(SystemMessageId.CONGRATULATIONS_YOU_VE_COMPLETED_A_CLASS_TRANSFER);
			}
			
			// Remove class permitted hennas.
			for (int slot = 1; slot < 5; slot++)
			{
				final Henna henna = getHenna(slot);
				if ((henna != null) && !henna.isAllowedClass(getClassId()))
				{
					removeHenna(slot);
				}
			}
			
			// Update class icon in party and clan
			if (isInParty())
			{
				_party.broadcastPacket(new PartySmallWindowUpdate(this, true));
			}
			
			if (_clan != null)
			{
				_clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
			}
			
			sendPacket(new ExSubjobInfo(this, SubclassInfoType.CLASS_CHANGED));
			
			// Add AutoGet skills and normal skills and/or learnByFS depending on configurations.
			rewardSkills();
			
			if (!canOverrideCond(PlayerCondOverride.SKILL_CONDITIONS) && Config.DECREASE_SKILL_LEVEL)
			{
				checkPlayerSkills();
			}
			
			notifyFriends(FriendStatus.MODE_CLASS);
		}
		finally
		{
			_subclassLock.unlock();
		}
	}
	
	/**
	 * @return the Experience of the PlayerInstance.
	 */
	public long getExp()
	{
		return getStat().getExp();
	}
	
	/**
	 * Set the fists weapon of the PlayerInstance (used when no weapon is equiped).
	 * @param weaponItem The fists Weapon to set to the PlayerInstance
	 */
	public void setFistsWeaponItem(Weapon weaponItem)
	{
		_fistsWeaponItem = weaponItem;
	}
	
	/**
	 * @return the fists weapon of the PlayerInstance (used when no weapon is equipped).
	 */
	public Weapon getFistsWeaponItem()
	{
		return _fistsWeaponItem;
	}
	
	/**
	 * @param classId
	 * @return the fists weapon of the PlayerInstance Class (used when no weapon is equipped).
	 */
	public Weapon findFistsWeaponItem(int classId)
	{
		Weapon weaponItem = null;
		if ((classId >= 0x00) && (classId <= 0x09))
		{
			// human fighter fists
			final Item temp = ItemTable.getInstance().getTemplate(246);
			weaponItem = (Weapon) temp;
		}
		else if ((classId >= 0x0a) && (classId <= 0x11))
		{
			// human mage fists
			final Item temp = ItemTable.getInstance().getTemplate(251);
			weaponItem = (Weapon) temp;
		}
		else if ((classId >= 0x12) && (classId <= 0x18))
		{
			// elven fighter fists
			final Item temp = ItemTable.getInstance().getTemplate(244);
			weaponItem = (Weapon) temp;
		}
		else if ((classId >= 0x19) && (classId <= 0x1e))
		{
			// elven mage fists
			final Item temp = ItemTable.getInstance().getTemplate(249);
			weaponItem = (Weapon) temp;
		}
		else if ((classId >= 0x1f) && (classId <= 0x25))
		{
			// dark elven fighter fists
			final Item temp = ItemTable.getInstance().getTemplate(245);
			weaponItem = (Weapon) temp;
		}
		else if ((classId >= 0x26) && (classId <= 0x2b))
		{
			// dark elven mage fists
			final Item temp = ItemTable.getInstance().getTemplate(250);
			weaponItem = (Weapon) temp;
		}
		else if ((classId >= 0x2c) && (classId <= 0x30))
		{
			// orc fighter fists
			final Item temp = ItemTable.getInstance().getTemplate(248);
			weaponItem = (Weapon) temp;
		}
		else if ((classId >= 0x31) && (classId <= 0x34))
		{
			// orc mage fists
			final Item temp = ItemTable.getInstance().getTemplate(252);
			weaponItem = (Weapon) temp;
		}
		else if ((classId >= 0x35) && (classId <= 0x39))
		{
			// dwarven fists
			final Item temp = ItemTable.getInstance().getTemplate(247);
			weaponItem = (Weapon) temp;
		}
		
		return weaponItem;
	}
	
	/**
	 * This method reward all AutoGet skills and Normal skills if Auto-Learn configuration is true.
	 */
	public void rewardSkills()
	{
		// Give all normal skills if activated Auto-Learn is activated, included AutoGet skills.
		if (Config.AUTO_LEARN_SKILLS)
		{
			giveAvailableSkills(Config.AUTO_LEARN_FS_SKILLS, Config.AUTO_LEARN_FP_SKILLS, true);
		}
		else
		{
			giveAvailableAutoGetSkills();
		}
		
		if (Config.DECREASE_SKILL_LEVEL && !canOverrideCond(PlayerCondOverride.SKILL_CONDITIONS))
		{
			checkPlayerSkills();
		}
		
		checkItemRestriction();
		sendSkillList();
	}
	
	/**
	 * Re-give all skills which aren't saved to database, like Noble, Hero, Clan Skills.<br>
	 */
	public void regiveTemporarySkills()
	{
		// Do not call this on enterworld or char load
		
		// Add noble skills if noble
		if (_nobleLevel > 0)
		{
			setNobleLevel(_nobleLevel);
		}
		
		// Add Hero skills if hero
		if (_hero)
		{
			setHero(true);
		}
		
		// Add clan skills
		if (_clan != null)
		{
			_clan.addSkillEffects(this);
			
			if ((_clan.getLevel() >= SiegeManager.getInstance().getSiegeClanMinLevel()) && isClanLeader())
			{
				SiegeManager.getInstance().addSiegeSkills(this);
			}
			if (_clan.getCastleId() > 0)
			{
				CastleManager.getInstance().getCastleByOwner(getClan()).giveResidentialSkills(this);
			}
			if (_clan.getFortId() > 0)
			{
				FortManager.getInstance().getFortByOwner(getClan()).giveResidentialSkills(this);
			}
		}
		
		// Reload passive skills from armors / jewels / weapons
		_inventory.reloadEquippedItems();
	}
	
	/**
	 * Give all available skills to the player.
	 * @param includedByFs if {@code true} forgotten scroll skills present in the skill tree will be added
	 * @param includedByFp if {@code true} forgotten power skills present in the skill tree will be added
	 * @param includeAutoGet if {@code true} auto-get skills present in the skill tree will be added
	 * @return the amount of new skills earned
	 */
	public int giveAvailableSkills(boolean includedByFs, boolean includedByFp, boolean includeAutoGet)
	{
		int skillCounter = 0;
		// Get available skills
		final Collection<Skill> skills = SkillTreesData.getInstance().getAllAvailableSkills(this, getTemplate().getClassId(), includedByFs, includedByFp, includeAutoGet);
		final List<Skill> skillsForStore = new ArrayList<>();
		
		for (Skill skill : skills)
		{
			final Skill oldSkill = getKnownSkill(skill.getId());
			if (oldSkill == skill)
			{
				continue;
			}
			
			if (getSkillLevel(skill.getId()) == 0)
			{
				skillCounter++;
			}
			
			// fix when learning toggle skills
			if (skill.isToggle() && !skill.isNecessaryToggle() && isAffectedBySkill(skill.getId()))
			{
				stopSkillEffects(true, skill.getId());
			}
			
			// Mobius: Keep sublevel on skill level increase.
			if ((oldSkill != null) && (oldSkill.getSubLevel() > 0) && (skill.getSubLevel() == 0) && (oldSkill.getLevel() < skill.getLevel()))
			{
				skill = SkillData.getInstance().getSkill(skill.getId(), skill.getLevel(), oldSkill.getSubLevel());
			}
			
			addSkill(skill, false);
			skillsForStore.add(skill);
		}
		storeSkills(skillsForStore, -1);
		if (Config.AUTO_LEARN_SKILLS && (skillCounter > 0))
		{
			sendMessage("You have learned " + skillCounter + " new skills.");
		}
		return skillCounter;
	}
	
	/**
	 * Give all available auto-get skills to the player.
	 */
	public void giveAvailableAutoGetSkills()
	{
		// Get available skills
		final List<SkillLearn> autoGetSkills = SkillTreesData.getInstance().getAvailableAutoGetSkills(this);
		final SkillData st = SkillData.getInstance();
		Skill skill;
		for (SkillLearn s : autoGetSkills)
		{
			skill = st.getSkill(s.getSkillId(), s.getSkillLevel());
			if (skill != null)
			{
				addSkill(skill, true);
			}
			else
			{
				LOGGER.warning("Skipping null auto-get skill for player: " + toString());
			}
		}
	}
	
	/**
	 * Set the Experience value of the PlayerInstance.
	 * @param exp
	 */
	public void setExp(long exp)
	{
		if (exp < 0)
		{
			exp = 0;
		}
		
		getStat().setExp(exp);
	}
	
	/**
	 * @return the Race object of the PlayerInstance.
	 */
	@Override
	public Race getRace()
	{
		final ClassId originalClass = getOriginalClass();
		if (originalClass != null)
		{
			return originalClass.getRace();
		}
		
		if (!isSubClassActive())
		{
			return getTemplate().getRace();
		}
		return PlayerTemplateData.getInstance().getTemplate(_baseClass).getRace();
	}
	
	public Radar getRadar()
	{
		return _radar;
	}
	
	/**
	 * @return the SP amount of the PlayerInstance.
	 */
	public long getSp()
	{
		return getStat().getSp();
	}
	
	/**
	 * Set the SP amount of the PlayerInstance.
	 * @param sp
	 */
	public void setSp(long sp)
	{
		if (sp < 0)
		{
			sp = 0;
		}
		
		super.getStat().setSp(sp);
	}
	
	/**
	 * @param castleId
	 * @return true if this PlayerInstance is a clan leader in ownership of the passed castle
	 */
	public boolean isCastleLord(int castleId)
	{
		
		// player has clan and is the clan leader, check the castle info
		if ((_clan != null) && (_clan.getLeader().getPlayerInstance() == this))
		{
			// if the clan has a castle and it is actually the queried castle, return true
			final Castle castle = CastleManager.getInstance().getCastleByOwner(_clan);
			if ((castle != null) && (castle == CastleManager.getInstance().getCastleById(castleId)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the Clan Identifier of the PlayerInstance.
	 */
	@Override
	public int getClanId()
	{
		return _clanId;
	}
	
	/**
	 * @return the Clan Crest Identifier of the PlayerInstance or 0.
	 */
	public int getClanCrestId()
	{
		if (_clan != null)
		{
			return _clan.getCrestId();
		}
		
		return 0;
	}
	
	/**
	 * @return The Clan CrestLarge Identifier or 0
	 */
	public int getClanCrestLargeId()
	{
		if ((_clan != null) && ((_clan.getCastleId() != 0) || (_clan.getHideoutId() != 0)))
		{
			return _clan.getCrestLargeId();
		}
		return 0;
	}
	
	public long getClanJoinExpiryTime()
	{
		return _clanJoinExpiryTime;
	}
	
	public void setClanJoinExpiryTime(long time)
	{
		_clanJoinExpiryTime = time;
	}
	
	public long getClanCreateExpiryTime()
	{
		return _clanCreateExpiryTime;
	}
	
	public void setClanCreateExpiryTime(long time)
	{
		_clanCreateExpiryTime = time;
	}
	
	public void setOnlineTime(long time)
	{
		_onlineTime = time;
		_onlineBeginTime = System.currentTimeMillis();
	}
	
	/**
	 * Return the PcInventory Inventory of the PlayerInstance contained in _inventory.
	 */
	@Override
	public PlayerInventory getInventory()
	{
		return _inventory;
	}
	
	/**
	 * Delete a ShortCut of the PlayerInstance _shortCuts.
	 * @param objectId
	 */
	public void removeItemFromShortCut(int objectId)
	{
		_shortCuts.deleteShortCutByObjectId(objectId);
	}
	
	/**
	 * @return True if the PlayerInstance is sitting.
	 */
	public boolean isSitting()
	{
		return _waitTypeSitting;
	}
	
	/**
	 * Set _waitTypeSitting to given value
	 * @param state
	 */
	public void setIsSitting(boolean state)
	{
		_waitTypeSitting = state;
	}
	
	/**
	 * Sit down the PlayerInstance, set the AI Intention to AI_INTENTION_REST and send a Server->Client ChangeWaitType packet (broadcast)
	 */
	public void sitDown()
	{
		sitDown(true);
	}
	
	public void sitDown(boolean checkCast)
	{
		if (checkCast && isCastingNow())
		{
			sendMessage("Cannot sit while casting.");
			return;
		}
		
		if (!_waitTypeSitting && !isAttackingDisabled() && !isControlBlocked() && !isImmobilized() && !isFishing())
		{
			breakAttack();
			setIsSitting(true);
			getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
			broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_SITTING));
			// Schedule a sit down task to wait for the animation to finish
			ThreadPool.schedule(new SitDownTask(this), 2500);
			setBlockActions(true);
		}
	}
	
	/**
	 * Stand up the PlayerInstance, set the AI Intention to AI_INTENTION_IDLE and send a Server->Client ChangeWaitType packet (broadcast)
	 */
	public void standUp()
	{
		if (GameEvent.isParticipant(this) && eventStatus.isSitForced())
		{
			sendMessage("A dark force beyond your mortal understanding makes your knees to shake when you try to stand up...");
		}
		else if (_waitTypeSitting && !isInStoreMode() && !isAlikeDead())
		{
			if (getEffectList().isAffected(EffectFlag.RELAXING))
			{
				stopEffects(EffectFlag.RELAXING);
			}
			
			broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_STANDING));
			// Schedule a stand up task to wait for the animation to finish
			ThreadPool.schedule(new StandUpTask(this), 2500);
		}
	}
	
	/**
	 * @return the PcWarehouse object of the PlayerInstance.
	 */
	public PlayerWarehouse getWarehouse()
	{
		if (_warehouse == null)
		{
			_warehouse = new PlayerWarehouse(this);
			_warehouse.restore();
		}
		if (Config.WAREHOUSE_CACHE)
		{
			WarehouseCacheManager.getInstance().addCacheTask(this);
		}
		return _warehouse;
	}
	
	/**
	 * Free memory used by Warehouse
	 */
	public void clearWarehouse()
	{
		if (_warehouse != null)
		{
			_warehouse.deleteMe();
		}
		_warehouse = null;
	}
	
	/**
	 * @return the PcFreight object of the PlayerInstance.
	 */
	public PlayerFreight getFreight()
	{
		return _freight;
	}
	
	/**
	 * @return true if refund list is not empty
	 */
	public boolean hasRefund()
	{
		return (_refund != null) && (_refund.getSize() > 0) && Config.ALLOW_REFUND;
	}
	
	/**
	 * @return refund object or create new if not exist
	 */
	public PlayerRefund getRefund()
	{
		if (_refund == null)
		{
			_refund = new PlayerRefund(this);
		}
		return _refund;
	}
	
	/**
	 * Clear refund
	 */
	public void clearRefund()
	{
		if (_refund != null)
		{
			_refund.deleteMe();
		}
		_refund = null;
	}
	
	/**
	 * @return the Adena amount of the PlayerInstance.
	 */
	public long getAdena()
	{
		return _inventory.getAdena();
	}
	
	/**
	 * @return the Ancient Adena amount of the PlayerInstance.
	 */
	public long getAncientAdena()
	{
		return _inventory.getAncientAdena();
	}
	
	/**
	 * @return the Beauty Tickets of the PlayerInstance.
	 */
	public long getBeautyTickets()
	{
		return _inventory.getBeautyTickets();
	}
	
	/**
	 * Add adena to Inventory of the PlayerInstance and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param count : int Quantity of adena to be added
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 */
	public void addAdena(String process, long count, WorldObject reference, boolean sendMessage)
	{
		if (sendMessage)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_ADENA);
			sm.addLong(count);
			sendPacket(sm);
		}
		
		if (count > 0)
		{
			_inventory.addAdena(process, count, this, reference);
			
			// Send update packet
			if (!Config.FORCE_INVENTORY_UPDATE)
			{
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addItem(_inventory.getAdenaInstance());
				sendInventoryUpdate(iu);
			}
			else
			{
				sendItemList();
			}
		}
	}
	
	/**
	 * Reduce adena in Inventory of the PlayerInstance and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param count : long Quantity of adena to be reduced
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successful
	 */
	public boolean reduceAdena(String process, long count, WorldObject reference, boolean sendMessage)
	{
		if (count > _inventory.getAdena())
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			return false;
		}
		
		if (count > 0)
		{
			final ItemInstance adenaItem = _inventory.getAdenaInstance();
			if (!_inventory.reduceAdena(process, count, this, reference))
			{
				return false;
			}
			
			// Send update packet
			if (!Config.FORCE_INVENTORY_UPDATE)
			{
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addItem(adenaItem);
				sendInventoryUpdate(iu);
			}
			else
			{
				sendItemList();
			}
			
			if (sendMessage)
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_ADENA_DISAPPEARED);
				sm.addLong(count);
				sendPacket(sm);
			}
		}
		
		return true;
	}
	
	/**
	 * Reduce Beauty Tickets in Inventory of the PlayerInstance and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param count : long Quantity of Beauty Tickets to be reduced
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successful
	 */
	public boolean reduceBeautyTickets(String process, long count, WorldObject reference, boolean sendMessage)
	{
		if (count > _inventory.getBeautyTickets())
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			}
			return false;
		}
		
		if (count > 0)
		{
			final ItemInstance beautyTickets = _inventory.getBeautyTicketsInstance();
			if (!_inventory.reduceBeautyTickets(process, count, this, reference))
			{
				return false;
			}
			
			// Send update packet
			if (!Config.FORCE_INVENTORY_UPDATE)
			{
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addItem(beautyTickets);
				sendInventoryUpdate(iu);
			}
			else
			{
				sendItemList();
			}
			
			if (sendMessage)
			{
				if (count > 1)
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
					sm.addItemName(Inventory.BEAUTY_TICKET_ID);
					sm.addLong(count);
					sendPacket(sm);
				}
				else
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
					sm.addItemName(Inventory.BEAUTY_TICKET_ID);
					sendPacket(sm);
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Add ancient adena to Inventory of the PlayerInstance and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param count : int Quantity of ancient adena to be added
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 */
	public void addAncientAdena(String process, long count, WorldObject reference, boolean sendMessage)
	{
		if (sendMessage)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
			sm.addItemName(Inventory.ANCIENT_ADENA_ID);
			sm.addLong(count);
			sendPacket(sm);
		}
		
		if (count > 0)
		{
			_inventory.addAncientAdena(process, count, this, reference);
			
			if (!Config.FORCE_INVENTORY_UPDATE)
			{
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addItem(_inventory.getAncientAdenaInstance());
				sendInventoryUpdate(iu);
			}
			else
			{
				sendItemList();
			}
		}
	}
	
	/**
	 * Reduce ancient adena in Inventory of the PlayerInstance and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param count : long Quantity of ancient adena to be reduced
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successful
	 */
	public boolean reduceAncientAdena(String process, long count, WorldObject reference, boolean sendMessage)
	{
		if (count > _inventory.getAncientAdena())
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			
			return false;
		}
		
		if (count > 0)
		{
			final ItemInstance ancientAdenaItem = _inventory.getAncientAdenaInstance();
			if (!_inventory.reduceAncientAdena(process, count, this, reference))
			{
				return false;
			}
			
			if (!Config.FORCE_INVENTORY_UPDATE)
			{
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addItem(ancientAdenaItem);
				sendInventoryUpdate(iu);
			}
			else
			{
				sendItemList();
			}
			
			if (sendMessage)
			{
				if (count > 1)
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
					sm.addItemName(Inventory.ANCIENT_ADENA_ID);
					sm.addLong(count);
					sendPacket(sm);
				}
				else
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
					sm.addItemName(Inventory.ANCIENT_ADENA_ID);
					sendPacket(sm);
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Adds item to inventory and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param item : ItemInstance to be added
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 */
	public void addItem(String process, ItemInstance item, WorldObject reference, boolean sendMessage)
	{
		if (item.getCount() > 0)
		{
			// Sends message to client if requested
			if (sendMessage)
			{
				if (item.getCount() > 1)
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S2_S1);
					sm.addItemName(item);
					sm.addLong(item.getCount());
					sendPacket(sm);
				}
				else if (item.getEnchantLevel() > 0)
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_A_S1_S2);
					sm.addInt(item.getEnchantLevel());
					sm.addItemName(item);
					sendPacket(sm);
				}
				else
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1);
					sm.addItemName(item);
					sendPacket(sm);
				}
			}
			
			// Add the item to inventory
			final ItemInstance newitem = _inventory.addItem(process, item, this, reference);
			
			// If over capacity, drop the item
			if (!canOverrideCond(PlayerCondOverride.ITEM_CONDITIONS) && !_inventory.validateCapacity(0, item.isQuestItem()) && newitem.isDropable() && (!newitem.isStackable() || (newitem.getLastChange() != ItemInstance.MODIFIED)))
			{
				dropItem("InvDrop", newitem, null, true, true);
			}
			else if (CursedWeaponsManager.getInstance().isCursed(newitem.getId()))
			{
				CursedWeaponsManager.getInstance().activate(this, newitem);
			}
			
			// Combat Flag
			else if (FortSiegeManager.getInstance().isCombat(item.getId()))
			{
				if (FortSiegeManager.getInstance().activateCombatFlag(this, item))
				{
					final Fort fort = FortManager.getInstance().getFort(this);
					fort.getSiege().announceToPlayer(new SystemMessage(SystemMessageId.C1_HAS_ACQUIRED_THE_FLAG), getName());
				}
			}
		}
	}
	
	/**
	 * Adds item to Inventory and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item Identifier of the item to be added
	 * @param count : long Quantity of items to be added
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return
	 */
	public ItemInstance addItem(String process, int itemId, long count, WorldObject reference, boolean sendMessage)
	{
		if (count > 0)
		{
			final Item item = ItemTable.getInstance().getTemplate(itemId);
			if (item == null)
			{
				LOGGER.severe("Item doesn't exist so cannot be added. Item ID: " + itemId);
				return null;
			}
			// Sends message to client if requested
			if (sendMessage && ((!isCastingNow() && item.hasExImmediateEffect()) || !item.hasExImmediateEffect()))
			{
				if (count > 1)
				{
					if (process.equalsIgnoreCase("Sweeper") || process.equalsIgnoreCase("Quest"))
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
						sm.addItemName(itemId);
						sm.addLong(count);
						sendPacket(sm);
					}
					else
					{
						final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S2_S1);
						sm.addItemName(itemId);
						sm.addLong(count);
						sendPacket(sm);
					}
				}
				else if (process.equalsIgnoreCase("Sweeper") || process.equalsIgnoreCase("Quest"))
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
					sm.addItemName(itemId);
					sendPacket(sm);
				}
				else
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1);
					sm.addItemName(itemId);
					sendPacket(sm);
				}
			}
			
			// Auto-use herbs.
			if (item.hasExImmediateEffect())
			{
				final IItemHandler handler = ItemHandler.getInstance().getHandler(item instanceof EtcItem ? (EtcItem) item : null);
				if (handler == null)
				{
					LOGGER.warning("No item handler registered for Herb ID " + item.getId() + "!");
				}
				else
				{
					handler.useItem(this, new ItemInstance(itemId), false);
				}
			}
			else
			{
				// Add the item to inventory
				final ItemInstance createdItem = _inventory.addItem(process, itemId, count, this, reference);
				
				// If over capacity, drop the item
				if (!canOverrideCond(PlayerCondOverride.ITEM_CONDITIONS) && !_inventory.validateCapacity(0, item.isQuestItem()) && createdItem.isDropable() && (!createdItem.isStackable() || (createdItem.getLastChange() != ItemInstance.MODIFIED)))
				{
					dropItem("InvDrop", createdItem, null, true);
				}
				else if (CursedWeaponsManager.getInstance().isCursed(createdItem.getId()))
				{
					CursedWeaponsManager.getInstance().activate(this, createdItem);
				}
				return createdItem;
			}
		}
		return null;
	}
	
	/**
	 * @param process the process name
	 * @param item the item holder
	 * @param reference the reference object
	 * @param sendMessage if {@code true} a system message will be sent
	 */
	public void addItem(String process, ItemHolder item, WorldObject reference, boolean sendMessage)
	{
		addItem(process, item.getId(), item.getCount(), reference, sendMessage);
	}
	
	/**
	 * Destroy item from inventory and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param item : ItemInstance to be destroyed
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successful
	 */
	public boolean destroyItem(String process, ItemInstance item, WorldObject reference, boolean sendMessage)
	{
		return destroyItem(process, item, item.getCount(), reference, sendMessage);
	}
	
	/**
	 * Destroy item from inventory and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param item : ItemInstance to be destroyed
	 * @param count
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successful
	 */
	public boolean destroyItem(String process, ItemInstance item, long count, WorldObject reference, boolean sendMessage)
	{
		item = _inventory.destroyItem(process, item, count, this, reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			}
			return false;
		}
		
		// Send inventory update packet
		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			final InventoryUpdate playerIU = new InventoryUpdate();
			playerIU.addItem(item);
			sendInventoryUpdate(playerIU);
		}
		else
		{
			sendItemList();
		}
		
		// Sends message to client if requested
		if (sendMessage)
		{
			if (count > 1)
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
				sm.addItemName(item);
				sm.addLong(count);
				sendPacket(sm);
			}
			else
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
				sm.addItemName(item);
				sendPacket(sm);
			}
		}
		
		return true;
	}
	
	/**
	 * Destroys item from inventory and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Instance identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successful
	 */
	@Override
	public boolean destroyItem(String process, int objectId, long count, WorldObject reference, boolean sendMessage)
	{
		final ItemInstance item = _inventory.getItemByObjectId(objectId);
		
		if (item == null)
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			}
			
			return false;
		}
		return destroyItem(process, item, count, reference, sendMessage);
	}
	
	/**
	 * Destroys shots from inventory without logging and only occasional saving to database. Sends a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Instance identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successful
	 */
	public boolean destroyItemWithoutTrace(String process, int objectId, long count, WorldObject reference, boolean sendMessage)
	{
		final ItemInstance item = _inventory.getItemByObjectId(objectId);
		
		if ((item == null) || (item.getCount() < count))
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			}
			
			return false;
		}
		
		return destroyItem(null, item, count, reference, sendMessage);
	}
	
	/**
	 * Destroy item from inventory by using its <B>itemId</B> and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param itemId : int Item identifier of the item to be destroyed
	 * @param count : int Quantity of items to be destroyed
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successful
	 */
	@Override
	public boolean destroyItemByItemId(String process, int itemId, long count, WorldObject reference, boolean sendMessage)
	{
		if (itemId == Inventory.ADENA_ID)
		{
			return reduceAdena(process, count, reference, sendMessage);
		}
		
		final ItemInstance item = _inventory.getItemByItemId(itemId);
		
		if ((item == null) || (item.getCount() < count) || (_inventory.destroyItemByItemId(process, itemId, count, this, reference) == null))
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			}
			
			return false;
		}
		
		// Send inventory update packet
		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			final InventoryUpdate playerIU = new InventoryUpdate();
			playerIU.addItem(item);
			sendInventoryUpdate(playerIU);
		}
		else
		{
			sendItemList();
		}
		
		// Sends message to client if requested
		if (sendMessage)
		{
			if (count > 1)
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
				sm.addItemName(itemId);
				sm.addLong(count);
				sendPacket(sm);
			}
			else
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
				sm.addItemName(itemId);
				sendPacket(sm);
			}
		}
		
		return true;
	}
	
	/**
	 * Transfers item to another ItemContainer and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Identifier of the item to be transfered
	 * @param count : long Quantity of items to be transfered
	 * @param target
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @return ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance transferItem(String process, int objectId, long count, Inventory target, WorldObject reference)
	{
		final ItemInstance oldItem = checkItemManipulation(objectId, count, "transfer");
		if (oldItem == null)
		{
			return null;
		}
		final ItemInstance newItem = _inventory.transferItem(process, objectId, count, target, this, reference);
		if (newItem == null)
		{
			return null;
		}
		
		// Send inventory update packet
		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			final InventoryUpdate playerIU = new InventoryUpdate();
			
			if ((oldItem.getCount() > 0) && (oldItem != newItem))
			{
				playerIU.addModifiedItem(oldItem);
			}
			else
			{
				playerIU.addRemovedItem(oldItem);
			}
			
			sendInventoryUpdate(playerIU);
		}
		else
		{
			sendItemList();
		}
		
		// Send target update packet
		if (target instanceof PlayerInventory)
		{
			final PlayerInstance targetPlayer = ((PlayerInventory) target).getOwner();
			
			if (!Config.FORCE_INVENTORY_UPDATE)
			{
				final InventoryUpdate playerIU = new InventoryUpdate();
				
				if (newItem.getCount() > count)
				{
					playerIU.addModifiedItem(newItem);
				}
				else
				{
					playerIU.addNewItem(newItem);
				}
				
				targetPlayer.sendPacket(playerIU);
			}
			else
			{
				targetPlayer.sendItemList();
			}
		}
		return newItem;
	}
	
	/**
	 * Use instead of calling {@link #addItem(String, ItemInstance, WorldObject, boolean)} and {@link #destroyItemByItemId(String, int, long, WorldObject, boolean)}<br>
	 * This method validates slots and weight limit, for stackable and non-stackable items.
	 * @param process a generic string representing the process that is exchanging this items
	 * @param reference the (probably NPC) reference, could be null
	 * @param coinId the item Id of the item given on the exchange
	 * @param cost the amount of items given on the exchange
	 * @param rewardId the item received on the exchange
	 * @param count the amount of items received on the exchange
	 * @param sendMessage if {@code true} it will send messages to the acting player
	 * @return {@code true} if the player successfully exchanged the items, {@code false} otherwise
	 */
	public boolean exchangeItemsById(String process, WorldObject reference, int coinId, long cost, int rewardId, long count, boolean sendMessage)
	{
		final PlayerInventory inv = _inventory;
		if (!inv.validateCapacityByItemId(rewardId, count))
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
			}
			return false;
		}
		
		if (!inv.validateWeightByItemId(rewardId, count))
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			}
			return false;
		}
		
		if (destroyItemByItemId(process, coinId, cost, reference, sendMessage))
		{
			addItem(process, rewardId, count, reference, sendMessage);
			return true;
		}
		return false;
	}
	
	/**
	 * Drop item from inventory and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process String Identifier of process triggering this action
	 * @param item ItemInstance to be dropped
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @param protectItem whether or not dropped item must be protected temporary against other players
	 * @return boolean informing if the action was successful
	 */
	public boolean dropItem(String process, ItemInstance item, WorldObject reference, boolean sendMessage, boolean protectItem)
	{
		item = _inventory.dropItem(process, item, this, reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			}
			
			return false;
		}
		
		item.dropMe(this, (getX() + Rnd.get(50)) - 25, (getY() + Rnd.get(50)) - 25, getZ() + 20);
		
		if ((Config.AUTODESTROY_ITEM_AFTER > 0) && Config.DESTROY_DROPPED_PLAYER_ITEM && !Config.LIST_PROTECTED_ITEMS.contains(item.getId()))
		{
			if ((item.isEquipable() && Config.DESTROY_EQUIPABLE_PLAYER_ITEM) || !item.isEquipable())
			{
				ItemsAutoDestroy.getInstance().addItem(item);
			}
		}
		
		// protection against auto destroy dropped item
		if (Config.DESTROY_DROPPED_PLAYER_ITEM)
		{
			if (!item.isEquipable() || (item.isEquipable() && Config.DESTROY_EQUIPABLE_PLAYER_ITEM))
			{
				item.setProtected(false);
			}
			else
			{
				item.setProtected(true);
			}
		}
		else
		{
			item.setProtected(true);
		}
		
		// retail drop protection
		if (protectItem)
		{
			item.getDropProtection().protect(this);
		}
		
		// Send inventory update packet
		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			final InventoryUpdate playerIU = new InventoryUpdate();
			playerIU.addItem(item);
			sendInventoryUpdate(playerIU);
		}
		else
		{
			sendItemList();
		}
		
		// Sends message to client if requested
		if (sendMessage)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_DROPPED_S1);
			sm.addItemName(item);
			sendPacket(sm);
		}
		
		return true;
	}
	
	public boolean dropItem(String process, ItemInstance item, WorldObject reference, boolean sendMessage)
	{
		return dropItem(process, item, reference, sendMessage, false);
	}
	
	/**
	 * Drop item from inventory by using its <B>objectID</B> and send a Server->Client InventoryUpdate packet to the PlayerInstance.
	 * @param process : String Identifier of process triggering this action
	 * @param objectId : int Item Instance identifier of the item to be dropped
	 * @param count : long Quantity of items to be dropped
	 * @param x : int coordinate for drop X
	 * @param y : int coordinate for drop Y
	 * @param z : int coordinate for drop Z
	 * @param reference : WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage : boolean Specifies whether to send message to Client about this action
	 * @param protectItem
	 * @return ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance dropItem(String process, int objectId, long count, int x, int y, int z, WorldObject reference, boolean sendMessage, boolean protectItem)
	{
		final ItemInstance invitem = _inventory.getItemByObjectId(objectId);
		final ItemInstance item = _inventory.dropItem(process, objectId, count, this, reference);
		
		if (item == null)
		{
			if (sendMessage)
			{
				sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
			}
			
			return null;
		}
		
		item.dropMe(this, x, y, z);
		
		if ((Config.AUTODESTROY_ITEM_AFTER > 0) && Config.DESTROY_DROPPED_PLAYER_ITEM && !Config.LIST_PROTECTED_ITEMS.contains(item.getId()))
		{
			if ((item.isEquipable() && Config.DESTROY_EQUIPABLE_PLAYER_ITEM) || !item.isEquipable())
			{
				ItemsAutoDestroy.getInstance().addItem(item);
			}
		}
		if (Config.DESTROY_DROPPED_PLAYER_ITEM)
		{
			if (!item.isEquipable() || (item.isEquipable() && Config.DESTROY_EQUIPABLE_PLAYER_ITEM))
			{
				item.setProtected(false);
			}
			else
			{
				item.setProtected(true);
			}
		}
		else
		{
			item.setProtected(true);
		}
		
		// retail drop protection
		if (protectItem)
		{
			item.getDropProtection().protect(this);
		}
		
		// Send inventory update packet
		if (!Config.FORCE_INVENTORY_UPDATE)
		{
			final InventoryUpdate playerIU = new InventoryUpdate();
			playerIU.addItem(invitem);
			sendInventoryUpdate(playerIU);
		}
		else
		{
			sendItemList();
		}
		
		// Sends message to client if requested
		if (sendMessage)
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_DROPPED_S1);
			sm.addItemName(item);
			sendPacket(sm);
		}
		
		return item;
	}
	
	public ItemInstance checkItemManipulation(int objectId, long count, String action)
	{
		// TODO: if we remove objects that are not visible from the World, we'll have to remove this check
		if (World.getInstance().findObject(objectId) == null)
		{
			LOGGER.finest(getObjectId() + ": player tried to " + action + " item not available in World");
			return null;
		}
		
		final ItemInstance item = _inventory.getItemByObjectId(objectId);
		
		if ((item == null) || (item.getOwnerId() != getObjectId()))
		{
			LOGGER.finest(getObjectId() + ": player tried to " + action + " item he is not owner of");
			return null;
		}
		
		if ((count < 0) || ((count > 1) && !item.isStackable()))
		{
			LOGGER.finest(getObjectId() + ": player tried to " + action + " item with invalid count: " + count);
			return null;
		}
		
		if (count > item.getCount())
		{
			LOGGER.finest(getObjectId() + ": player tried to " + action + " more items than he owns");
			return null;
		}
		
		// Pet is summoned and not the item that summoned the pet AND not the buggle from strider you're mounting
		if (((_pet != null) && (_pet.getControlObjectId() == objectId)) || (_mountObjectID == objectId))
		{
			return null;
		}
		
		if (isProcessingItem(objectId))
		{
			return null;
		}
		
		// We cannot put a Weapon with Augmentation in WH while casting (Possible Exploit)
		if (item.isAugmented() && isCastingNow())
		{
			return null;
		}
		
		return item;
	}
	
	public boolean isSpawnProtected()
	{
		return (_spawnProtectEndTime != 0) && (_spawnProtectEndTime > System.currentTimeMillis());
	}
	
	public boolean isTeleportProtected()
	{
		return (_teleportProtectEndTime != 0) && (_teleportProtectEndTime > System.currentTimeMillis());
	}
	
	public void setSpawnProtection(boolean protect)
	{
		_spawnProtectEndTime = protect ? System.currentTimeMillis() + (Config.PLAYER_SPAWN_PROTECTION * 1000) : 0;
	}
	
	public void setTeleportProtection(boolean protect)
	{
		_teleportProtectEndTime = protect ? System.currentTimeMillis() + (Config.PLAYER_TELEPORT_PROTECTION * 1000) : 0;
	}
	
	/**
	 * Set protection from aggro mobs when getting up from fake death, according settings.
	 * @param protect
	 */
	public void setRecentFakeDeath(boolean protect)
	{
		_recentFakeDeathEndTime = protect ? GameTimeController.getInstance().getGameTicks() + (Config.PLAYER_FAKEDEATH_UP_PROTECTION * GameTimeController.TICKS_PER_SECOND) : 0;
	}
	
	public boolean isRecentFakeDeath()
	{
		return _recentFakeDeathEndTime > GameTimeController.getInstance().getGameTicks();
	}
	
	public boolean isFakeDeath()
	{
		return isAffected(EffectFlag.FAKE_DEATH);
	}
	
	@Override
	public boolean isAlikeDead()
	{
		return super.isAlikeDead() || isFakeDeath();
	}
	
	/**
	 * @return the client owner of this char.
	 */
	public GameClient getClient()
	{
		return _client;
	}
	
	public void setClient(GameClient client)
	{
		_client = client;
		if ((_client != null) && (_client.getConnectionAddress() != null))
		{
			_ip = _client.getConnectionAddress().getHostAddress();
		}
	}
	
	public String getIPAddress()
	{
		return _ip;
	}
	
	public Location getCurrentSkillWorldPosition()
	{
		return _currentSkillWorldPosition;
	}
	
	public void setCurrentSkillWorldPosition(Location worldPosition)
	{
		_currentSkillWorldPosition = worldPosition;
	}
	
	@Override
	public void enableSkill(Skill skill)
	{
		super.enableSkill(skill);
		removeTimeStamp(skill);
	}
	
	/**
	 * Returns true if cp update should be done, false if not
	 * @return boolean
	 */
	private boolean needCpUpdate()
	{
		final double currentCp = getCurrentCp();
		
		if ((currentCp <= 1.0) || (getMaxCp() < MAX_HP_BAR_PX))
		{
			return true;
		}
		
		if ((currentCp <= _cpUpdateDecCheck) || (currentCp >= _cpUpdateIncCheck))
		{
			if (currentCp == getMaxCp())
			{
				_cpUpdateIncCheck = currentCp + 1;
				_cpUpdateDecCheck = currentCp - _cpUpdateInterval;
			}
			else
			{
				final double doubleMulti = currentCp / _cpUpdateInterval;
				int intMulti = (int) doubleMulti;
				
				_cpUpdateDecCheck = _cpUpdateInterval * (doubleMulti < intMulti ? intMulti-- : intMulti);
				_cpUpdateIncCheck = _cpUpdateDecCheck + _cpUpdateInterval;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if mp update should be done, false if not
	 * @return boolean
	 */
	private boolean needMpUpdate()
	{
		final double currentMp = getCurrentMp();
		
		if ((currentMp <= 1.0) || (getMaxMp() < MAX_HP_BAR_PX))
		{
			return true;
		}
		
		if ((currentMp <= _mpUpdateDecCheck) || (currentMp >= _mpUpdateIncCheck))
		{
			if (currentMp == getMaxMp())
			{
				_mpUpdateIncCheck = currentMp + 1;
				_mpUpdateDecCheck = currentMp - _mpUpdateInterval;
			}
			else
			{
				final double doubleMulti = currentMp / _mpUpdateInterval;
				int intMulti = (int) doubleMulti;
				
				_mpUpdateDecCheck = _mpUpdateInterval * (doubleMulti < intMulti ? intMulti-- : intMulti);
				_mpUpdateIncCheck = _mpUpdateDecCheck + _mpUpdateInterval;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Send packet StatusUpdate with current HP,MP and CP to the PlayerInstance and only current HP, MP and Level to all other PlayerInstance of the Party. <B><U> Actions</U> :</B>
	 * <li>Send the Server->Client packet StatusUpdate with current HP, MP and CP to this PlayerInstance</li><BR>
	 * <li>Send the Server->Client packet PartySmallWindowUpdate with current HP, MP and Level to all other PlayerInstance of the Party</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND current HP and MP to all PlayerInstance of the _statusListener</B></FONT>
	 */
	@Override
	public void broadcastStatusUpdate(Creature caster)
	{
		final StatusUpdate su = new StatusUpdate(this);
		if (caster != null)
		{
			su.addCaster(caster);
		}
		
		computeStatusUpdate(su, StatusUpdateType.LEVEL);
		computeStatusUpdate(su, StatusUpdateType.MAX_HP);
		computeStatusUpdate(su, StatusUpdateType.CUR_HP);
		computeStatusUpdate(su, StatusUpdateType.MAX_MP);
		computeStatusUpdate(su, StatusUpdateType.CUR_MP);
		computeStatusUpdate(su, StatusUpdateType.MAX_CP);
		computeStatusUpdate(su, StatusUpdateType.CUR_CP);
		if (su.hasUpdates())
		{
			broadcastPacket(su);
		}
		
		final boolean needCpUpdate = needCpUpdate();
		final boolean needHpUpdate = needHpUpdate();
		final boolean needMpUpdate = needMpUpdate();
		final Party party = getParty();
		
		// Check if a party is in progress and party window update is usefull
		if ((_party != null) && (needCpUpdate || needHpUpdate || needMpUpdate))
		{
			final PartySmallWindowUpdate partyWindow = new PartySmallWindowUpdate(this, false);
			if (needCpUpdate)
			{
				partyWindow.addComponentType(PartySmallWindowUpdateType.CURRENT_CP);
				partyWindow.addComponentType(PartySmallWindowUpdateType.MAX_CP);
			}
			if (needHpUpdate)
			{
				partyWindow.addComponentType(PartySmallWindowUpdateType.CURRENT_HP);
				partyWindow.addComponentType(PartySmallWindowUpdateType.MAX_HP);
			}
			if (needMpUpdate)
			{
				partyWindow.addComponentType(PartySmallWindowUpdateType.CURRENT_MP);
				partyWindow.addComponentType(PartySmallWindowUpdateType.MAX_MP);
			}
			party.broadcastToPartyMembers(this, partyWindow);
		}
		
		if (_inOlympiadMode && _OlympiadStart && (needCpUpdate || needHpUpdate))
		{
			final OlympiadGameTask game = OlympiadGameManager.getInstance().getOlympiadTask(getOlympiadGameId());
			if ((game != null) && game.isBattleStarted())
			{
				game.getStadium().broadcastStatusUpdate(this);
			}
		}
		
		// In duel MP updated only with CP or HP
		if (_isInDuel && (needCpUpdate || needHpUpdate))
		{
			DuelManager.getInstance().broadcastToOppositTeam(this, new ExDuelUpdateUserInfo(this));
		}
	}
	
	/**
	 * Send a Server->Client packet UserInfo to this PlayerInstance and CharInfo to all PlayerInstance in its _KnownPlayers. <B><U> Concept</U> :</B> Others PlayerInstance in the detection area of the PlayerInstance are identified in <B>_knownPlayers</B>. In order to inform other players of this
	 * PlayerInstance state modifications, server just need to go through _knownPlayers to send Server->Client Packet <B><U> Actions</U> :</B>
	 * <li>Send a Server->Client packet UserInfo to this PlayerInstance (Public and Private Data)</li>
	 * <li>Send a Server->Client packet CharInfo to all PlayerInstance in _KnownPlayers of the PlayerInstance (Public data only)</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : DON'T SEND UserInfo packet to other players instead of CharInfo packet. Indeed, UserInfo packet contains PRIVATE DATA as
	 * MaxHP, STR, DEX...</B></FONT>
	 */
	public void broadcastUserInfo()
	{
		// Send user info to the current player
		sendPacket(new UserInfo(this));
		
		// Broadcast char info to known players
		broadcastCharInfo();
	}
	
	public void broadcastUserInfo(UserInfoType... types)
	{
		// Send user info to the current player
		final UserInfo ui = new UserInfo(this, false);
		ui.addComponentType(types);
		sendPacket(ui);
		
		// Broadcast char info to all known players
		broadcastCharInfo();
	}
	
	public void broadcastCharInfo()
	{
		final CharInfo charInfo = new CharInfo(this, false);
		World.getInstance().forEachVisibleObject(this, PlayerInstance.class, player ->
		{
			if (isVisibleFor(player))
			{
				if (isInvisible() && player.canOverrideCond(PlayerCondOverride.SEE_ALL_PLAYERS))
				{
					player.sendPacket(new CharInfo(this, true));
				}
				else
				{
					player.sendPacket(charInfo);
				}
				
				// Update relation.
				final int relation = getRelation(player);
				Integer oldrelation = getKnownRelations().get(player.getObjectId());
				if ((oldrelation == null) || (oldrelation != relation))
				{
					final RelationChanged rc = new RelationChanged();
					rc.addRelation(this, relation, !isInsideZone(ZoneId.PEACE));
					if (hasSummon())
					{
						final Summon pet = getPet();
						if (pet != null)
						{
							rc.addRelation(pet, relation, !isInsideZone(ZoneId.PEACE));
						}
						if (hasServitors())
						{
							getServitors().values().forEach(s -> rc.addRelation(s, relation, !isInsideZone(ZoneId.PEACE)));
						}
					}
					player.sendPacket(rc);
					getKnownRelations().put(player.getObjectId(), relation);
				}
			}
		});
	}
	
	public void broadcastTitleInfo()
	{
		// Send a Server->Client packet UserInfo to this PlayerInstance
		broadcastUserInfo(UserInfoType.CLAN);
		
		// Send a Server->Client packet TitleUpdate to all PlayerInstance in _KnownPlayers of the PlayerInstance
		broadcastPacket(new NicknameChanged(this));
	}
	
	@Override
	public void broadcastPacket(IClientOutgoingPacket mov)
	{
		if (mov instanceof CharInfo)
		{
			new IllegalArgumentException("CharInfo is being send via broadcastPacket. Do NOT do that! Use broadcastCharInfo() instead.");
		}
		
		sendPacket(mov);
		
		World.getInstance().forEachVisibleObject(this, PlayerInstance.class, player ->
		{
			if (!isVisibleFor(player))
			{
				return;
			}
			
			player.sendPacket(mov);
		});
	}
	
	@Override
	public void broadcastPacket(IClientOutgoingPacket mov, int radiusInKnownlist)
	{
		if (mov instanceof CharInfo)
		{
			new IllegalArgumentException("CharInfo is being send via broadcastPacket. Do NOT do that! Use broadcastCharInfo() instead.");
		}
		
		sendPacket(mov);
		
		World.getInstance().forEachVisibleObject(this, PlayerInstance.class, player ->
		{
			if (!isVisibleFor(player) || (calculateDistance3D(player) >= radiusInKnownlist))
			{
				return;
			}
			player.sendPacket(mov);
		});
	}
	
	/**
	 * @return the Alliance Identifier of the PlayerInstance.
	 */
	@Override
	public int getAllyId()
	{
		return _clan == null ? 0 : _clan.getAllyId();
	}
	
	public int getAllyCrestId()
	{
		return getAllyId() == 0 ? 0 : _clan.getAllyCrestId();
	}
	
	/**
	 * Send a Server->Client packet StatusUpdate to the PlayerInstance.
	 */
	@Override
	public void sendPacket(IClientOutgoingPacket... packets)
	{
		if (_client != null)
		{
			for (IClientOutgoingPacket packet : packets)
			{
				_client.sendPacket(packet);
			}
		}
	}
	
	/**
	 * Send SystemMessage packet.
	 * @param id SystemMessageId
	 */
	@Override
	public void sendPacket(SystemMessageId id)
	{
		sendPacket(new SystemMessage(id));
	}
	
	/**
	 * Manage Interact Task with another PlayerInstance. <B><U> Actions</U> :</B>
	 * <li>If the private store is a STORE_PRIVATE_SELL, send a Server->Client PrivateBuyListSell packet to the PlayerInstance</li>
	 * <li>If the private store is a STORE_PRIVATE_BUY, send a Server->Client PrivateBuyListBuy packet to the PlayerInstance</li>
	 * <li>If the private store is a STORE_PRIVATE_MANUFACTURE, send a Server->Client RecipeShopSellList packet to the PlayerInstance</li>
	 * @param target The Creature targeted
	 */
	public void doInteract(Creature target)
	{
		if (target == null)
		{
			return;
		}
		
		if (target.isPlayer())
		{
			final PlayerInstance targetPlayer = (PlayerInstance) target;
			sendPacket(ActionFailed.STATIC_PACKET);
			
			if ((targetPlayer.getPrivateStoreType() == PrivateStoreType.SELL) || (targetPlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL))
			{
				if (_isSellingBuffs)
				{
					SellBuffsManager.getInstance().sendBuffMenu(this, targetPlayer, 0);
				}
				else
				{
					sendPacket(new PrivateStoreListSell(this, targetPlayer));
				}
			}
			else if (targetPlayer.getPrivateStoreType() == PrivateStoreType.BUY)
			{
				sendPacket(new PrivateStoreListBuy(this, targetPlayer));
			}
			else if (targetPlayer.getPrivateStoreType() == PrivateStoreType.MANUFACTURE)
			{
				sendPacket(new RecipeShopSellList(this, targetPlayer));
			}
		}
		else // _interactTarget=null should never happen but one never knows ^^;
		{
			target.onAction(this);
		}
	}
	
	/**
	 * Manages AutoLoot Task.<br>
	 * <ul>
	 * <li>Send a system message to the player.</li>
	 * <li>Add the item to the player's inventory.</li>
	 * <li>Send a Server->Client packet InventoryUpdate to this player with NewItem (use a new slot) or ModifiedItem (increase amount).</li>
	 * <li>Send a Server->Client packet StatusUpdate to this player with current weight.</li>
	 * </ul>
	 * <font color=#FF0000><B><U>Caution</U>: If a party is in progress, distribute the items between the party members!</b></font>
	 * @param target the NPC dropping the item
	 * @param itemId the item ID
	 * @param itemCount the item count
	 */
	public void doAutoLoot(Attackable target, int itemId, long itemCount)
	{
		if (isInParty() && !ItemTable.getInstance().getTemplate(itemId).hasExImmediateEffect())
		{
			_party.distributeItem(this, itemId, itemCount, false, target);
		}
		else if (itemId == Inventory.ADENA_ID)
		{
			addAdena("Loot", itemCount, target, true);
		}
		else
		{
			addItem("Loot", itemId, itemCount, target, true);
		}
	}
	
	/**
	 * Method overload for {@link PlayerInstance#doAutoLoot(Attackable, int, long)}
	 * @param target the NPC dropping the item
	 * @param item the item holder
	 */
	public void doAutoLoot(Attackable target, ItemHolder item)
	{
		doAutoLoot(target, item.getId(), item.getCount());
	}
	
	/**
	 * Manage Pickup Task. <B><U> Actions</U> :</B>
	 * <li>Send a Server->Client packet StopMove to this PlayerInstance</li>
	 * <li>Remove the ItemInstance from the world and send server->client GetItem packets</li>
	 * <li>Send a System Message to the PlayerInstance : YOU_PICKED_UP_S1_ADENA or YOU_PICKED_UP_S1_S2</li>
	 * <li>Add the Item to the PlayerInstance inventory</li>
	 * <li>Send a Server->Client packet InventoryUpdate to this PlayerInstance with NewItem (use a new slot) or ModifiedItem (increase amount)</li>
	 * <li>Send a Server->Client packet StatusUpdate to this PlayerInstance with current weight</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : If a Party is in progress, distribute Items between party members</B></FONT>
	 * @param object The ItemInstance to pick up
	 */
	@Override
	public void doPickupItem(WorldObject object)
	{
		if (isAlikeDead() || isFakeDeath())
		{
			return;
		}
		
		// Set the AI Intention to AI_INTENTION_IDLE
		getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		
		// Check if the WorldObject to pick up is a ItemInstance
		if (!object.isItem())
		{
			// dont try to pickup anything that is not an item :)
			LOGGER.warning(this + " trying to pickup wrong target." + getTarget());
			return;
		}
		
		final ItemInstance target = (ItemInstance) object;
		
		// Send a Server->Client packet ActionFailed to this PlayerInstance
		sendPacket(ActionFailed.STATIC_PACKET);
		
		// Send a Server->Client packet StopMove to this PlayerInstance
		final StopMove sm = new StopMove(this);
		sendPacket(sm);
		
		SystemMessage smsg = null;
		synchronized (target)
		{
			// Check if the target to pick up is visible
			if (!target.isSpawned())
			{
				// Send a Server->Client packet ActionFailed to this PlayerInstance
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (!target.getDropProtection().tryPickUp(this))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				smsg = new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
				smsg.addItemName(target);
				sendPacket(smsg);
				return;
			}
			
			if (((isInParty() && (_party.getDistributionType() == PartyDistributionType.FINDERS_KEEPERS)) || !isInParty()) && !_inventory.validateCapacity(target))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
				return;
			}
			
			if (isInvul() && !canOverrideCond(PlayerCondOverride.ITEM_CONDITIONS))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				smsg = new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
				smsg.addItemName(target);
				sendPacket(smsg);
				return;
			}
			
			if ((target.getOwnerId() != 0) && (target.getOwnerId() != getObjectId()) && !isInLooterParty(target.getOwnerId()))
			{
				if (target.getId() == Inventory.ADENA_ID)
				{
					smsg = new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
					smsg.addLong(target.getCount());
				}
				else if (target.getCount() > 1)
				{
					smsg = new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S2_S1_S);
					smsg.addItemName(target);
					smsg.addLong(target.getCount());
				}
				else
				{
					smsg = new SystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
					smsg.addItemName(target);
				}
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(smsg);
				return;
			}
			
			// You can pickup only 1 combat flag
			if (FortSiegeManager.getInstance().isCombat(target.getId()))
			{
				if (!FortSiegeManager.getInstance().checkIfCanPickup(this))
				{
					return;
				}
			}
			
			if ((target.getItemLootShedule() != null) && ((target.getOwnerId() == getObjectId()) || isInLooterParty(target.getOwnerId())))
			{
				target.resetOwnerTimer();
			}
			
			// Remove the ItemInstance from the world and send server->client GetItem packets
			target.pickupMe(this);
			if (Config.SAVE_DROPPED_ITEM)
			{
				ItemsOnGroundManager.getInstance().removeObject(target);
			}
		}
		
		// Auto use herbs - pick up
		if (target.getItem().hasExImmediateEffect())
		{
			final IItemHandler handler = ItemHandler.getInstance().getHandler(target.getEtcItem());
			if (handler == null)
			{
				LOGGER.warning("No item handler registered for item ID: " + target.getId() + ".");
			}
			else
			{
				handler.useItem(this, target, false);
			}
			ItemTable.getInstance().destroyItem("Consume", target, this, null);
		}
		// Cursed Weapons are not distributed
		else if (CursedWeaponsManager.getInstance().isCursed(target.getId()))
		{
			addItem("Pickup", target, null, true);
		}
		else if (FortSiegeManager.getInstance().isCombat(target.getId()))
		{
			addItem("Pickup", target, null, true);
		}
		else
		{
			// if item is instance of ArmorType or WeaponType broadcast an "Attention" system message
			if ((target.getItemType() instanceof ArmorType) || (target.getItemType() instanceof WeaponType))
			{
				if (target.getEnchantLevel() > 0)
				{
					smsg = new SystemMessage(SystemMessageId.ATTENTION_C1_HAS_PICKED_UP_S2_S3);
					smsg.addPcName(this);
					smsg.addInt(target.getEnchantLevel());
					smsg.addItemName(target.getId());
					broadcastPacket(smsg, 1400);
				}
				else
				{
					smsg = new SystemMessage(SystemMessageId.ATTENTION_C1_HAS_PICKED_UP_S2);
					smsg.addPcName(this);
					smsg.addItemName(target.getId());
					broadcastPacket(smsg, 1400);
				}
			}
			
			// Check if a Party is in progress
			if (isInParty())
			{
				_party.distributeItem(this, target);
			}
			else if ((target.getId() == Inventory.ADENA_ID) && (_inventory.getAdenaInstance() != null))
			{
				addAdena("Pickup", target.getCount(), null, true);
				ItemTable.getInstance().destroyItem("Pickup", target, this, null);
			}
			else
			{
				addItem("Pickup", target, null, true);
				// Auto-Equip arrows/bolts if player has a bow/crossbow and player picks up arrows/bolts.
				final ItemInstance weapon = _inventory.getPaperdollItem(Inventory.PAPERDOLL_RHAND);
				if (weapon != null)
				{
					final EtcItem etcItem = target.getEtcItem();
					if (etcItem != null)
					{
						final EtcItemType itemType = etcItem.getItemType();
						if (((weapon.getItemType() == WeaponType.BOW) && (itemType == EtcItemType.ARROW)) || (((weapon.getItemType() == WeaponType.CROSSBOW) || (weapon.getItemType() == WeaponType.TWOHANDCROSSBOW)) && (itemType == EtcItemType.BOLT)))
						{
							checkAndEquipAmmunition(itemType);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void doAutoAttack(Creature target)
	{
		super.doAutoAttack(target);
		setRecentFakeDeath(false);
		if (target.isFakePlayer())
		{
			updatePvPStatus();
		}
	}
	
	@Override
	public void doCast(Skill skill)
	{
		super.doCast(skill);
		setRecentFakeDeath(false);
	}
	
	public boolean canOpenPrivateStore()
	{
		if ((Config.SHOP_MIN_RANGE_FROM_NPC > 0) || (Config.SHOP_MIN_RANGE_FROM_PLAYER > 0))
		{
			for (Creature creature : World.getInstance().getVisibleObjectsInRange(this, Creature.class, 1000))
			{
				if (Util.checkIfInRange(creature.getMinShopDistance(), this, creature, true))
				{
					sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE));
					return false;
				}
			}
		}
		
		return !_isSellingBuffs && !isAlikeDead() && !_inOlympiadMode && !isMounted() && !isInsideZone(ZoneId.NO_STORE) && !isCastingNow();
	}
	
	@Override
	public int getMinShopDistance()
	{
		return _waitTypeSitting ? Config.SHOP_MIN_RANGE_FROM_PLAYER : 0;
	}
	
	public void tryOpenPrivateBuyStore()
	{
		// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
		if (canOpenPrivateStore())
		{
			if ((_privateStoreType == PrivateStoreType.BUY) || (_privateStoreType == PrivateStoreType.BUY_MANAGE))
			{
				setPrivateStoreType(PrivateStoreType.NONE);
			}
			if (_privateStoreType == PrivateStoreType.NONE)
			{
				if (_waitTypeSitting)
				{
					standUp();
				}
				setPrivateStoreType(PrivateStoreType.BUY_MANAGE);
				sendPacket(new PrivateStoreManageListBuy(1, this));
				sendPacket(new PrivateStoreManageListBuy(2, this));
			}
		}
		else
		{
			if (isInsideZone(ZoneId.NO_STORE))
			{
				sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
			}
			sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
	
	public PreparedMultisellListHolder getMultiSell()
	{
		return _currentMultiSell;
	}
	
	public void setMultiSell(PreparedMultisellListHolder list)
	{
		_currentMultiSell = list;
	}
	
	/**
	 * Set a target. <B><U> Actions</U> :</B>
	 * <ul>
	 * <li>Remove the PlayerInstance from the _statusListener of the old target if it was a Creature</li>
	 * <li>Add the PlayerInstance to the _statusListener of the new target if it's a Creature</li>
	 * <li>Target the new WorldObject (add the target to the PlayerInstance _target, _knownObject and PlayerInstance to _KnownObject of the WorldObject)</li>
	 * </ul>
	 * @param newTarget The WorldObject to target
	 */
	@Override
	public void setTarget(WorldObject newTarget)
	{
		if (newTarget != null)
		{
			final boolean isInParty = (newTarget.isPlayer() && isInParty() && _party.containsPlayer(newTarget.getActingPlayer()));
			
			// Prevents /target exploiting
			if (!isInParty && (Math.abs(newTarget.getZ() - getZ()) > 1000))
			{
				newTarget = null;
			}
			
			// Check if the new target is visible
			if ((newTarget != null) && !isInParty && !newTarget.isSpawned())
			{
				newTarget = null;
			}
			
			// vehicles cant be targeted
			if (!isGM() && (newTarget instanceof Vehicle))
			{
				newTarget = null;
			}
		}
		
		// Get the current target
		final WorldObject oldTarget = getTarget();
		
		if (oldTarget != null)
		{
			if (oldTarget.equals(newTarget)) // no target change?
			{
				// Validate location of the target.
				if ((newTarget != null) && (newTarget.getObjectId() != getObjectId()))
				{
					sendPacket(new ValidateLocation(newTarget));
				}
				return;
			}
			
			// Remove the target from the status listener.
			oldTarget.removeStatusListener(this);
		}
		
		if ((newTarget != null) && newTarget.isCreature())
		{
			final Creature target = (Creature) newTarget;
			
			// Validate location of the new target.
			if (newTarget.getObjectId() != getObjectId())
			{
				sendPacket(new ValidateLocation(target));
			}
			
			// Show the client his new target.
			sendPacket(new MyTargetSelected(this, target));
			
			// Register target to listen for hp changes.
			target.addStatusListener(this);
			
			// Send max/current hp.
			final StatusUpdate su = new StatusUpdate(target);
			su.addUpdate(StatusUpdateType.MAX_HP, target.getMaxHp());
			su.addUpdate(StatusUpdateType.CUR_HP, (int) target.getCurrentHp());
			sendPacket(su);
			
			// To others the new target, and not yourself!
			Broadcast.toKnownPlayers(this, new TargetSelected(getObjectId(), newTarget.getObjectId(), getX(), getY(), getZ()));
			
			// Send buffs
			sendPacket(new ExAbnormalStatusUpdateFromTarget(target));
		}
		
		// Target was removed?
		if ((newTarget == null) && (getTarget() != null))
		{
			broadcastPacket(new TargetUnselected(this));
		}
		
		// Target the new WorldObject (add the target to the PlayerInstance _target, _knownObject and PlayerInstance to _KnownObject of the WorldObject)
		super.setTarget(newTarget);
	}
	
	/**
	 * Return the active weapon instance (always equiped in the right hand).
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return _inventory.getPaperdollItem(Inventory.PAPERDOLL_RHAND);
	}
	
	/**
	 * Return the active weapon item (always equiped in the right hand).
	 */
	@Override
	public Weapon getActiveWeaponItem()
	{
		final ItemInstance weapon = getActiveWeaponInstance();
		if (weapon == null)
		{
			return _fistsWeaponItem;
		}
		
		return (Weapon) weapon.getItem();
	}
	
	public ItemInstance getChestArmorInstance()
	{
		return _inventory.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
	}
	
	public ItemInstance getLegsArmorInstance()
	{
		return _inventory.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
	}
	
	public Armor getActiveChestArmorItem()
	{
		final ItemInstance armor = getChestArmorInstance();
		
		if (armor == null)
		{
			return null;
		}
		
		return (Armor) armor.getItem();
	}
	
	public Armor getActiveLegsArmorItem()
	{
		final ItemInstance legs = getLegsArmorInstance();
		
		if (legs == null)
		{
			return null;
		}
		
		return (Armor) legs.getItem();
	}
	
	public boolean isWearingHeavyArmor()
	{
		final ItemInstance legs = getLegsArmorInstance();
		final ItemInstance armor = getChestArmorInstance();
		
		if ((armor != null) && (legs != null))
		{
			if ((legs.getItemType() == ArmorType.HEAVY) && (armor.getItemType() == ArmorType.HEAVY))
			{
				return true;
			}
		}
		if (armor != null)
		{
			if (((_inventory.getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItem().getBodyPart() == Item.SLOT_FULL_ARMOR) && (armor.getItemType() == ArmorType.HEAVY)))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isWearingLightArmor()
	{
		final ItemInstance legs = getLegsArmorInstance();
		final ItemInstance armor = getChestArmorInstance();
		
		if ((armor != null) && (legs != null))
		{
			if ((legs.getItemType() == ArmorType.LIGHT) && (armor.getItemType() == ArmorType.LIGHT))
			{
				return true;
			}
		}
		if (armor != null)
		{
			if (((_inventory.getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItem().getBodyPart() == Item.SLOT_FULL_ARMOR) && (armor.getItemType() == ArmorType.LIGHT)))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isWearingMagicArmor()
	{
		final ItemInstance legs = getLegsArmorInstance();
		final ItemInstance armor = getChestArmorInstance();
		
		if ((armor != null) && (legs != null))
		{
			if ((legs.getItemType() == ArmorType.MAGIC) && (armor.getItemType() == ArmorType.MAGIC))
			{
				return true;
			}
		}
		if (armor != null)
		{
			if (((_inventory.getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItem().getBodyPart() == Item.SLOT_FULL_ARMOR) && (armor.getItemType() == ArmorType.MAGIC)))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return the secondary weapon instance (always equiped in the left hand).
	 */
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return _inventory.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
	}
	
	/**
	 * Return the secondary Item item (always equiped in the left hand).<BR>
	 * Arrows, Shield..<BR>
	 */
	@Override
	public Item getSecondaryWeaponItem()
	{
		final ItemInstance item = _inventory.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (item != null)
		{
			return item.getItem();
		}
		return null;
	}
	
	/**
	 * Kill the Creature, Apply Death Penalty, Manage gain/loss Karma and Item Drop. <B><U> Actions</U> :</B>
	 * <li>Reduce the Experience of the PlayerInstance in function of the calculated Death Penalty</li>
	 * <li>If necessary, unsummon the Pet of the killed PlayerInstance</li>
	 * <li>Manage Karma gain for attacker and Karam loss for the killed PlayerInstance</li>
	 * <li>If the killed PlayerInstance has Karma, manage Drop Item</li>
	 * <li>Kill the PlayerInstance</li>
	 * @param killer
	 */
	@Override
	public boolean doDie(Creature killer)
	{
		if (killer != null)
		{
			final PlayerInstance pk = killer.getActingPlayer();
			final boolean fpcKill = killer.isFakePlayer();
			if ((pk != null) || fpcKill)
			{
				if (pk != null)
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPvPKill(pk, this), this);
					
					if (GameEvent.isParticipant(pk))
					{
						pk.getEventStatus().addKill(this);
					}
					
					// pvp/pk item rewards
					if (!(Config.DISABLE_REWARDS_IN_INSTANCES && (getInstanceId() != 0)) && //
						!(Config.DISABLE_REWARDS_IN_PVP_ZONES && isInsideZone(ZoneId.PVP)))
					{
						// pvp
						if (Config.REWARD_PVP_ITEM && (_pvpFlag != 0))
						{
							pk.addItem("PvP Item Reward", Config.REWARD_PVP_ITEM_ID, Config.REWARD_PVP_ITEM_AMOUNT, this, Config.REWARD_PVP_ITEM_MESSAGE);
						}
						// pk
						if (Config.REWARD_PK_ITEM && (_pvpFlag == 0))
						{
							pk.addItem("PK Item Reward", Config.REWARD_PK_ITEM_ID, Config.REWARD_PK_ITEM_AMOUNT, this, Config.REWARD_PK_ITEM_MESSAGE);
						}
					}
				}
				
				// announce pvp/pk
				if (Config.ANNOUNCE_PK_PVP && (((pk != null) && !pk.isGM()) || fpcKill))
				{
					String msg = "";
					if (_pvpFlag == 0)
					{
						msg = Config.ANNOUNCE_PK_MSG.replace("$killer", killer.getName()).replace("$target", getName());
						if (Config.ANNOUNCE_PK_PVP_NORMAL_MESSAGE)
						{
							final SystemMessage sm = new SystemMessage(SystemMessageId.S1_3);
							sm.addString(msg);
							Broadcast.toAllOnlinePlayers(sm);
						}
						else
						{
							Broadcast.toAllOnlinePlayers(msg, false);
						}
					}
					else if (_pvpFlag != 0)
					{
						msg = Config.ANNOUNCE_PVP_MSG.replace("$killer", killer.getName()).replace("$target", getName());
						if (Config.ANNOUNCE_PK_PVP_NORMAL_MESSAGE)
						{
							final SystemMessage sm = new SystemMessage(SystemMessageId.S1_3);
							sm.addString(msg);
							Broadcast.toAllOnlinePlayers(sm);
						}
						else
						{
							Broadcast.toAllOnlinePlayers(msg, false);
						}
					}
				}
				
				if (fpcKill && Config.FAKE_PLAYER_KILL_KARMA && (_pvpFlag == 0) && (getReputation() >= 0))
				{
					killer.setReputation(killer.getReputation() - 150);
				}
			}
			
			broadcastStatusUpdate();
			// Clear resurrect xp calculation
			setExpBeforeDeath(0);
			
			// Calculate Shilen's Breath debuff level. It must happen right before death, because buffs aren't applied on dead characters.
			calculateShilensBreathDebuffLevel(killer);
			
			// Kill the PlayerInstance
			if (!super.doDie(killer))
			{
				return false;
			}
			
			// Issues drop of Cursed Weapon.
			if (isCursedWeaponEquipped())
			{
				CursedWeaponsManager.getInstance().drop(_cursedWeaponEquippedId, killer);
			}
			else if (_combatFlagEquippedId)
			{
				final Fort fort = FortManager.getInstance().getFort(this);
				if (fort != null)
				{
					FortSiegeManager.getInstance().dropCombatFlag(this, fort.getResidenceId());
				}
				else
				{
					final long slot = _inventory.getSlotFromItem(_inventory.getItemByItemId(9819));
					_inventory.unEquipItemInBodySlot(slot);
					destroyItem("CombatFlag", _inventory.getItemByItemId(9819), null, true);
				}
			}
			else
			{
				final boolean insidePvpZone = isInsideZone(ZoneId.PVP) || isInsideZone(ZoneId.SIEGE);
				if ((pk == null) || !pk.isCursedWeaponEquipped())
				{
					onDieDropItem(killer); // Check if any item should be dropped
					
					if (!insidePvpZone && (pk != null))
					{
						final Clan pkClan = pk.getClan();
						if ((pkClan != null) && (_clan != null) && !isAcademyMember() && !(pk.isAcademyMember()))
						{
							final ClanWar clanWar = _clan.getWarWith(pkClan.getId());
							if ((clanWar != null) && AntiFeedManager.getInstance().check(killer, this))
							{
								clanWar.onKill(pk, this);
							}
						}
					}
					// If player is Lucky shouldn't get penalized.
					if (!isLucky() && !insidePvpZone)
					{
						calculateDeathExpPenalty(killer);
					}
				}
			}
		}
		
		if (isMounted())
		{
			stopFeed();
		}
		// synchronized (this)
		// {
		if (isFakeDeath())
		{
			stopFakeDeath(true);
		}
		// }
		
		// Unsummon Cubics
		if (!_cubics.isEmpty())
		{
			_cubics.values().forEach(CubicInstance::deactivate);
			_cubics.clear();
		}
		
		if (isChannelized())
		{
			getSkillChannelized().abortChannelization();
		}
		
		if (_agathionId != 0)
		{
			setAgathionId(0);
		}
		
		stopRentPet();
		stopWaterTask();
		
		AntiFeedManager.getInstance().setLastDeathTime(getObjectId());
		
		// FIXME: Karma reduction tempfix.
		if (getReputation() < 0)
		{
			final int newRep = getReputation() - (getReputation() / 4);
			setReputation(newRep < -20 ? newRep : 0);
		}
		
		return true;
	}
	
	private void onDieDropItem(Creature killer)
	{
		if (GameEvent.isParticipant(this) || (killer == null))
		{
			return;
		}
		
		final PlayerInstance pk = killer.getActingPlayer();
		if ((getReputation() >= 0) && (pk != null) && (pk.getClan() != null) && (getClan() != null) && (pk.getClan().isAtWarWith(_clanId)
		// || _clan.isAtWarWith(((PlayerInstance)killer).getClanId())
		))
		{
			return;
		}
		
		if ((!isInsideZone(ZoneId.PVP) || (pk == null)) && (!isGM() || Config.KARMA_DROP_GM))
		{
			boolean isKarmaDrop = false;
			final int pkLimit = Config.KARMA_PK_LIMIT;
			
			int dropEquip = 0;
			int dropEquipWeapon = 0;
			int dropItem = 0;
			int dropLimit = 0;
			int dropPercent = 0;
			
			if ((getReputation() < 0) && (_pkKills >= pkLimit))
			{
				isKarmaDrop = true;
				dropPercent = Config.KARMA_RATE_DROP;
				dropEquip = Config.KARMA_RATE_DROP_EQUIP;
				dropEquipWeapon = Config.KARMA_RATE_DROP_EQUIP_WEAPON;
				dropItem = Config.KARMA_RATE_DROP_ITEM;
				dropLimit = Config.KARMA_DROP_LIMIT;
			}
			else if (killer.isNpc() && (getLevel() > 4))
			{
				dropPercent = Config.PLAYER_RATE_DROP;
				dropEquip = Config.PLAYER_RATE_DROP_EQUIP;
				dropEquipWeapon = Config.PLAYER_RATE_DROP_EQUIP_WEAPON;
				dropItem = Config.PLAYER_RATE_DROP_ITEM;
				dropLimit = Config.PLAYER_DROP_LIMIT;
			}
			
			if ((dropPercent > 0) && (Rnd.get(100) < dropPercent))
			{
				int dropCount = 0;
				int itemDropPercent = 0;
				
				for (ItemInstance itemDrop : _inventory.getItems())
				{
					// Don't drop
					if (itemDrop.isShadowItem() || // Dont drop Shadow Items
						itemDrop.isTimeLimitedItem() || // Dont drop Time Limited Items
						!itemDrop.isDropable() || (itemDrop.getId() == Inventory.ADENA_ID) || // Adena
						(itemDrop.getItem().getType2() == Item.TYPE2_QUEST) || // Quest Items
						((_pet != null) && (_pet.getControlObjectId() == itemDrop.getId())) || // Control Item of active pet
						(Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_ITEMS, itemDrop.getId()) >= 0) || // Item listed in the non droppable item list
						(Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_PET_ITEMS, itemDrop.getId()) >= 0 // Item listed in the non droppable pet item list
						))
					{
						continue;
					}
					
					if (itemDrop.isEquipped())
					{
						// Set proper chance according to Item type of equipped Item
						itemDropPercent = itemDrop.getItem().getType2() == Item.TYPE2_WEAPON ? dropEquipWeapon : dropEquip;
						_inventory.unEquipItemInSlot(itemDrop.getLocationSlot());
					}
					else
					{
						itemDropPercent = dropItem; // Item in inventory
					}
					
					// NOTE: Each time an item is dropped, the chance of another item being dropped gets lesser (dropCount * 2)
					if (Rnd.get(100) < itemDropPercent)
					{
						dropItem("DieDrop", itemDrop, killer, true);
						
						if (isKarmaDrop)
						{
							LOGGER.warning(getName() + " has karma and dropped id = " + itemDrop.getId() + ", count = " + itemDrop.getCount());
						}
						else
						{
							LOGGER.warning(getName() + " dropped id = " + itemDrop.getId() + ", count = " + itemDrop.getCount());
						}
						
						if (++dropCount >= dropLimit)
						{
							break;
						}
					}
				}
			}
		}
	}
	
	public void onPlayerKill(Playable killedPlayable)
	{
		final PlayerInstance killedPlayer = killedPlayable.getActingPlayer();
		
		// Avoid nulls && check if player != killedPlayer
		if ((killedPlayer == null) || (this == killedPlayer))
		{
			return;
		}
		
		// Cursed weapons progress
		if (isCursedWeaponEquipped() && killedPlayer.isPlayer())
		{
			CursedWeaponsManager.getInstance().increaseKills(getCursedWeaponEquippedId());
			return;
		}
		
		// Duel support
		if (isInDuel() && killedPlayer.isInDuel())
		{
			return;
		}
		
		// Do nothing if both players are in PVP zone
		if (isInsideZone(ZoneId.PVP) && killedPlayer.isInsideZone(ZoneId.PVP))
		{
			return;
		}
		
		// If both players are in SIEGE zone just increase siege kills/deaths
		if (isInsideZone(ZoneId.SIEGE) && killedPlayer.isInsideZone(ZoneId.SIEGE))
		{
			if (!isSiegeFriend(killedPlayer))
			{
				final Clan targetClan = killedPlayer.getClan();
				if ((_clan != null) && (targetClan != null))
				{
					_clan.addSiegeKill();
					targetClan.addSiegeDeath();
				}
			}
			return;
		}
		
		if (checkIfPvP(killedPlayer))
		{
			// Check if player should get + rep
			if (killedPlayer.getReputation() < 0)
			{
				final int levelDiff = killedPlayer.getLevel() - getLevel();
				if ((getReputation() >= 0) && (levelDiff < 11) && (levelDiff > -11)) // TODO: Time check, same player can't be killed again in 8 hours
				{
					setReputation(getReputation() + Config.REPUTATION_INCREASE);
				}
			}
			
			setPvpKills(_pvpKills + 1);
			
			updatePvpTitleAndColor(true);
		}
		else if ((getReputation() > 0) && (_pkKills == 0))
		{
			setReputation(0);
			setPkKills(getPkKills() + 1);
		}
		else // Calculate new karma and increase pk count
		{
			if (Config.FACTION_SYSTEM_ENABLED)
			{
				if ((_isGood && killedPlayer.isGood()) || (_isEvil && killedPlayer.isEvil()))
				{
					setReputation(getReputation() - Formulas.calculateKarmaGain(getPkKills(), killedPlayable.isSummon()));
					setPkKills(getPkKills() + 1);
				}
			}
			else
			{
				setReputation(getReputation() - Formulas.calculateKarmaGain(getPkKills(), killedPlayable.isSummon()));
				setPkKills(getPkKills() + 1);
			}
		}
		
		broadcastUserInfo(UserInfoType.SOCIAL);
		checkItemRestriction();
	}
	
	public void updatePvpTitleAndColor(boolean broadcastInfo)
	{
		if (Config.PVP_COLOR_SYSTEM_ENABLED && !Config.FACTION_SYSTEM_ENABLED) // Faction system uses title colors.
		{
			if ((_pvpKills >= (Config.PVP_AMOUNT1)) && (_pvpKills < (Config.PVP_AMOUNT2)))
			{
				setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT1 + " \u00AE");
				_appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT1);
			}
			else if ((_pvpKills >= (Config.PVP_AMOUNT2)) && (_pvpKills < (Config.PVP_AMOUNT3)))
			{
				setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT2 + " \u00AE");
				_appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT2);
			}
			else if ((_pvpKills >= (Config.PVP_AMOUNT3)) && (_pvpKills < (Config.PVP_AMOUNT4)))
			{
				setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT3 + " \u00AE");
				_appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT3);
			}
			else if ((_pvpKills >= (Config.PVP_AMOUNT4)) && (_pvpKills < (Config.PVP_AMOUNT5)))
			{
				setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT4 + " \u00AE");
				_appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT4);
			}
			else if (_pvpKills >= (Config.PVP_AMOUNT5))
			{
				setTitle("\u00AE " + Config.TITLE_FOR_PVP_AMOUNT5 + " \u00AE");
				_appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT5);
			}
			
			if (broadcastInfo)
			{
				broadcastTitleInfo();
			}
		}
	}
	
	public void updatePvPStatus()
	{
		if (isInsideZone(ZoneId.PVP))
		{
			return;
		}
		setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_NORMAL_TIME);
		
		if (_pvpFlag == 0)
		{
			startPvPFlag();
		}
	}
	
	public void updatePvPStatus(Creature target)
	{
		final PlayerInstance player_target = target.getActingPlayer();
		if (player_target == null)
		{
			return;
		}
		
		if (this == player_target)
		{
			return;
		}
		
		if (Config.FACTION_SYSTEM_ENABLED && target.isPlayer() && ((isGood() && player_target.isEvil()) || (isEvil() && player_target.isGood())))
		{
			return;
		}
		
		if (_isInDuel && (player_target.getDuelId() == getDuelId()))
		{
			return;
		}
		if ((!isInsideZone(ZoneId.PVP) || !player_target.isInsideZone(ZoneId.PVP)) && (player_target.getReputation() >= 0))
		{
			if (checkIfPvP(player_target))
			{
				setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_PVP_TIME);
			}
			else
			{
				setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_NORMAL_TIME);
			}
			if (_pvpFlag == 0)
			{
				startPvPFlag();
			}
		}
	}
	
	/**
	 * @return {@code true} if player has Lucky effect and is level 9 or less
	 */
	public boolean isLucky()
	{
		return (getLevel() <= 9) && isAffectedBySkill(CommonSkill.LUCKY.getId());
	}
	
	/**
	 * Restore the specified % of experience this PlayerInstance has lost and sends a Server->Client StatusUpdate packet.
	 * @param restorePercent
	 */
	public void restoreExp(double restorePercent)
	{
		if (_expBeforeDeath > 0)
		{
			// Restore the specified % of lost experience.
			getStat().addExp(Math.round(((_expBeforeDeath - getExp()) * restorePercent) / 100));
			setExpBeforeDeath(0);
		}
	}
	
	/**
	 * Reduce the Experience (and level if necessary) of the PlayerInstance in function of the calculated Death Penalty.<BR>
	 * <B><U> Actions</U> :</B>
	 * <li>Calculate the Experience loss</li>
	 * <li>Set the value of _expBeforeDeath</li>
	 * <li>Set the new Experience value of the PlayerInstance and Decrease its level if necessary</li>
	 * <li>Send a Server->Client StatusUpdate packet with its new Experience</li>
	 * @param killer
	 */
	public void calculateDeathExpPenalty(Creature killer)
	{
		final int lvl = getLevel();
		double percentLost = PlayerXpPercentLostData.getInstance().getXpPercent(getLevel());
		
		if (killer != null)
		{
			if (killer.isRaid())
			{
				percentLost *= getStat().getValue(Stats.REDUCE_EXP_LOST_BY_RAID, 1);
			}
			else if (killer.isMonster())
			{
				percentLost *= getStat().getValue(Stats.REDUCE_EXP_LOST_BY_MOB, 1);
			}
			else if (killer.isPlayable())
			{
				percentLost *= getStat().getValue(Stats.REDUCE_EXP_LOST_BY_PVP, 1);
			}
		}
		
		if (getReputation() < 0)
		{
			percentLost *= Config.RATE_KARMA_EXP_LOST;
		}
		
		// Calculate the Experience loss
		long lostExp = 0;
		if (!GameEvent.isParticipant(this))
		{
			if (lvl < ExperienceData.getInstance().getMaxLevel())
			{
				lostExp = Math.round(((getStat().getExpForLevel(lvl + 1) - getStat().getExpForLevel(lvl)) * percentLost) / 100);
			}
			else
			{
				lostExp = Math.round(((getStat().getExpForLevel(ExperienceData.getInstance().getMaxLevel()) - getStat().getExpForLevel(ExperienceData.getInstance().getMaxLevel() - 1)) * percentLost) / 100);
			}
		}
		
		if ((killer != null) && killer.isPlayable() && atWarWith(killer.getActingPlayer()))
		{
			lostExp /= 4.0;
		}
		
		setExpBeforeDeath(getExp());
		getStat().removeExp(lostExp);
	}
	
	/**
	 * Stop the HP/MP/CP Regeneration task. <B><U> Actions</U> :</B>
	 * <li>Set the RegenActive flag to False</li>
	 * <li>Stop the HP/MP/CP Regeneration task</li>
	 */
	public void stopAllTimers()
	{
		stopHpMpRegeneration();
		stopWarnUserTakeBreak();
		stopWaterTask();
		stopFeed();
		clearPetData();
		storePetFood(_mountNpcId);
		stopRentPet();
		stopPvpRegTask();
		stopSoulTask();
		stopChargeTask();
		stopFameTask();
		stopRecoGiveTask();
	}
	
	@Override
	public PetInstance getPet()
	{
		return _pet;
	}
	
	@Override
	public Map<Integer, Summon> getServitors()
	{
		return _servitors;
	}
	
	public Summon getAnyServitor()
	{
		return getServitors().values().stream().findAny().orElse(null);
	}
	
	public Summon getFirstServitor()
	{
		return getServitors().values().stream().findFirst().orElse(null);
	}
	
	@Override
	public Summon getServitor(int objectId)
	{
		return getServitors().get(objectId);
	}
	
	public List<Summon> getServitorsAndPets()
	{
		final List<Summon> summons = new ArrayList<>();
		summons.addAll(getServitors().values());
		
		if (_pet != null)
		{
			summons.add(_pet);
		}
		
		return summons;
	}
	
	/**
	 * @return any summoned trap by this player or null.
	 */
	public TrapInstance getTrap()
	{
		return getSummonedNpcs().stream().filter(Npc::isTrap).map(TrapInstance.class::cast).findAny().orElse(null);
	}
	
	/**
	 * Set the summoned Pet of the PlayerInstance.
	 * @param pet
	 */
	public void setPet(PetInstance pet)
	{
		_pet = pet;
	}
	
	public void addServitor(Summon servitor)
	{
		_servitors.put(servitor.getObjectId(), servitor);
	}
	
	/**
	 * @return the Summon of the PlayerInstance or null.
	 */
	public Set<TamedBeastInstance> getTrainedBeasts()
	{
		return _tamedBeast;
	}
	
	/**
	 * Set the Summon of the PlayerInstance.
	 * @param tamedBeast
	 */
	public void addTrainedBeast(TamedBeastInstance tamedBeast)
	{
		_tamedBeast.add(tamedBeast);
	}
	
	/**
	 * @return the PlayerInstance requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
	 */
	public Request getRequest()
	{
		return _request;
	}
	
	/**
	 * Set the PlayerInstance requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
	 * @param requester
	 */
	public void setActiveRequester(PlayerInstance requester)
	{
		_activeRequester = requester;
	}
	
	/**
	 * @return the PlayerInstance requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
	 */
	public PlayerInstance getActiveRequester()
	{
		final PlayerInstance requester = _activeRequester;
		if (requester != null)
		{
			if (requester.isRequestExpired() && (_activeTradeList == null))
			{
				_activeRequester = null;
			}
		}
		return _activeRequester;
	}
	
	/**
	 * @return True if a transaction is in progress.
	 */
	public boolean isProcessingRequest()
	{
		return (getActiveRequester() != null) || (_requestExpireTime > GameTimeController.getInstance().getGameTicks());
	}
	
	/**
	 * @return True if a transaction is in progress.
	 */
	public boolean isProcessingTransaction()
	{
		return (getActiveRequester() != null) || (_activeTradeList != null) || (_requestExpireTime > GameTimeController.getInstance().getGameTicks());
	}
	
	/**
	 * Used by fake players to emulate proper behavior.
	 */
	public void blockRequest()
	{
		_requestExpireTime = GameTimeController.getInstance().getGameTicks() + (REQUEST_TIMEOUT * GameTimeController.TICKS_PER_SECOND);
	}
	
	/**
	 * Select the Warehouse to be used in next activity.
	 * @param partner
	 */
	public void onTransactionRequest(PlayerInstance partner)
	{
		_requestExpireTime = GameTimeController.getInstance().getGameTicks() + (REQUEST_TIMEOUT * GameTimeController.TICKS_PER_SECOND);
		partner.setActiveRequester(this);
	}
	
	/**
	 * Return true if last request is expired.
	 * @return
	 */
	public boolean isRequestExpired()
	{
		return !(_requestExpireTime > GameTimeController.getInstance().getGameTicks());
	}
	
	/**
	 * Select the Warehouse to be used in next activity.
	 */
	public void onTransactionResponse()
	{
		_requestExpireTime = 0;
	}
	
	/**
	 * Select the Warehouse to be used in next activity.
	 * @param warehouse
	 */
	public void setActiveWarehouse(ItemContainer warehouse)
	{
		_activeWarehouse = warehouse;
	}
	
	/**
	 * @return active Warehouse.
	 */
	public ItemContainer getActiveWarehouse()
	{
		return _activeWarehouse;
	}
	
	/**
	 * Select the TradeList to be used in next activity.
	 * @param tradeList
	 */
	public void setActiveTradeList(TradeList tradeList)
	{
		_activeTradeList = tradeList;
	}
	
	/**
	 * @return active TradeList.
	 */
	public TradeList getActiveTradeList()
	{
		return _activeTradeList;
	}
	
	public void onTradeStart(PlayerInstance partner)
	{
		_activeTradeList = new TradeList(this);
		_activeTradeList.setPartner(partner);
		
		final SystemMessage msg = new SystemMessage(SystemMessageId.YOU_BEGIN_TRADING_WITH_C1);
		msg.addPcName(partner);
		sendPacket(msg);
		sendPacket(new TradeStart(1, this));
		sendPacket(new TradeStart(2, this));
	}
	
	public void onTradeConfirm(PlayerInstance partner)
	{
		final SystemMessage msg = new SystemMessage(SystemMessageId.C1_HAS_CONFIRMED_THE_TRADE);
		msg.addPcName(partner);
		sendPacket(msg);
		sendPacket(TradeOtherDone.STATIC_PACKET);
	}
	
	public void onTradeCancel(PlayerInstance partner)
	{
		if (_activeTradeList == null)
		{
			return;
		}
		
		_activeTradeList.lock();
		_activeTradeList = null;
		
		sendPacket(new TradeDone(0));
		final SystemMessage msg = new SystemMessage(SystemMessageId.C1_HAS_CANCELLED_THE_TRADE);
		msg.addPcName(partner);
		sendPacket(msg);
	}
	
	public void onTradeFinish(boolean successfull)
	{
		_activeTradeList = null;
		sendPacket(new TradeDone(1));
		if (successfull)
		{
			sendPacket(SystemMessageId.YOUR_TRADE_WAS_SUCCESSFUL);
		}
	}
	
	public void startTrade(PlayerInstance partner)
	{
		onTradeStart(partner);
		partner.onTradeStart(this);
	}
	
	public void cancelActiveTrade()
	{
		if (_activeTradeList == null)
		{
			return;
		}
		
		final PlayerInstance partner = _activeTradeList.getPartner();
		if (partner != null)
		{
			partner.onTradeCancel(this);
		}
		onTradeCancel(this);
	}
	
	public boolean hasManufactureShop()
	{
		return (_manufactureItems != null) && !_manufactureItems.isEmpty();
	}
	
	/**
	 * Get the manufacture items map of this player.
	 * @return the the manufacture items map
	 */
	public Map<Integer, Long> getManufactureItems()
	{
		if (_manufactureItems == null)
		{
			return Collections.emptyMap();
		}
		
		return _manufactureItems;
	}
	
	public void setManufactureItems(Map<Integer, Long> manufactureItems)
	{
		_manufactureItems = manufactureItems;
	}
	
	/**
	 * Get the store name, if any.
	 * @return the store name
	 */
	public String getStoreName()
	{
		return _storeName;
	}
	
	/**
	 * Set the store name.
	 * @param name the store name to set
	 */
	public void setStoreName(String name)
	{
		_storeName = name == null ? "" : name;
	}
	
	/**
	 * @return the _buyList object of the PlayerInstance.
	 */
	public TradeList getSellList()
	{
		if (_sellList == null)
		{
			_sellList = new TradeList(this);
		}
		return _sellList;
	}
	
	/**
	 * @return the _buyList object of the PlayerInstance.
	 */
	public TradeList getBuyList()
	{
		if (_buyList == null)
		{
			_buyList = new TradeList(this);
		}
		return _buyList;
	}
	
	/**
	 * Set the Private Store type of the PlayerInstance. <B><U> Values </U> :</B>
	 * <li>0 : STORE_PRIVATE_NONE</li>
	 * <li>1 : STORE_PRIVATE_SELL</li>
	 * <li>2 : sellmanage</li><BR>
	 * <li>3 : STORE_PRIVATE_BUY</li><BR>
	 * <li>4 : buymanage</li><BR>
	 * <li>5 : STORE_PRIVATE_MANUFACTURE</li><BR>
	 * @param privateStoreType
	 */
	public void setPrivateStoreType(PrivateStoreType privateStoreType)
	{
		_privateStoreType = privateStoreType;
		
		if (Config.OFFLINE_DISCONNECT_FINISHED && (privateStoreType == PrivateStoreType.NONE) && ((_client == null) || _client.isDetached()))
		{
			Disconnection.of(this).storeMe().deleteMe();
		}
	}
	
	/**
	 * <B><U> Values </U> :</B>
	 * <li>0 : STORE_PRIVATE_NONE</li>
	 * <li>1 : STORE_PRIVATE_SELL</li>
	 * <li>2 : sellmanage</li><BR>
	 * <li>3 : STORE_PRIVATE_BUY</li><BR>
	 * <li>4 : buymanage</li><BR>
	 * <li>5 : STORE_PRIVATE_MANUFACTURE</li><BR>
	 * @return the Private Store type of the PlayerInstance.
	 */
	public PrivateStoreType getPrivateStoreType()
	{
		return _privateStoreType;
	}
	
	/**
	 * Set the _clan object, _clanId, _clanLeader Flag and title of the PlayerInstance.
	 * @param clan
	 */
	public void setClan(Clan clan)
	{
		_clan = clan;
		
		if (clan == null)
		{
			setTitle("");
			_clanId = 0;
			_clanPrivileges = new EnumIntBitmask<>(ClanPrivilege.class, false);
			_pledgeType = 0;
			_powerGrade = 0;
			_lvlJoinedAcademy = 0;
			_apprentice = 0;
			_sponsor = 0;
			_activeWarehouse = null;
			return;
		}
		
		if (!clan.isMember(getObjectId()))
		{
			// char has been kicked from clan
			setClan(null);
			return;
		}
		
		_clanId = clan.getId();
	}
	
	/**
	 * @return the _clan object of the PlayerInstance.
	 */
	@Override
	public Clan getClan()
	{
		return _clan;
	}
	
	/**
	 * @return True if the PlayerInstance is the leader of its clan.
	 */
	public boolean isClanLeader()
	{
		if (_clan == null)
		{
			return false;
		}
		return getObjectId() == _clan.getLeaderId();
	}
	
	/**
	 * Equip arrows needed in left hand and send a Server->Client packet ItemList to the PlayerInstance then return True.
	 * @param type
	 */
	@Override
	protected boolean checkAndEquipAmmunition(EtcItemType type)
	{
		ItemInstance arrows = _inventory.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (arrows == null)
		{
			final Weapon weapon = getActiveWeaponItem();
			if (type == EtcItemType.ARROW)
			{
				arrows = _inventory.findArrowForBow(weapon);
			}
			else if (type == EtcItemType.BOLT)
			{
				arrows = _inventory.findBoltForCrossBow(weapon);
			}
			if (arrows != null)
			{
				// Equip arrows needed in left hand
				_inventory.setPaperdollItem(Inventory.PAPERDOLL_LHAND, arrows);
				sendItemList();
				return true;
			}
		}
		else
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Disarm the player's weapon.
	 * @return {@code true} if the player was disarmed or doesn't have a weapon to disarm, {@code false} otherwise.
	 */
	public boolean disarmWeapons()
	{
		// If there is no weapon to disarm then return true.
		final ItemInstance wpn = _inventory.getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		if (wpn == null)
		{
			return true;
		}
		
		// Don't allow disarming a cursed weapon
		if (isCursedWeaponEquipped())
		{
			return false;
		}
		
		// Don't allow disarming a Combat Flag or Territory Ward.
		if (_combatFlagEquippedId)
		{
			return false;
		}
		
		// Don't allow disarming if the weapon is force equip.
		if (wpn.getWeaponItem().isForceEquip())
		{
			return false;
		}
		
		final ItemInstance[] unequiped = _inventory.unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
		final InventoryUpdate iu = new InventoryUpdate();
		for (ItemInstance itm : unequiped)
		{
			iu.addModifiedItem(itm);
		}
		
		sendInventoryUpdate(iu);
		abortAttack();
		broadcastUserInfo();
		
		// This can be 0 if the user pressed the right mousebutton twice very fast.
		if (unequiped.length > 0)
		{
			final SystemMessage sm;
			if (unequiped[0].getEnchantLevel() > 0)
			{
				sm = new SystemMessage(SystemMessageId.S1_S2_HAS_BEEN_UNEQUIPPED);
				sm.addInt(unequiped[0].getEnchantLevel());
				sm.addItemName(unequiped[0]);
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(unequiped[0]);
			}
			sendPacket(sm);
		}
		return true;
	}
	
	/**
	 * Disarm the player's shield.
	 * @return {@code true}.
	 */
	public boolean disarmShield()
	{
		final ItemInstance sld = _inventory.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if (sld != null)
		{
			final ItemInstance[] unequiped = _inventory.unEquipItemInBodySlotAndRecord(sld.getItem().getBodyPart());
			final InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance itm : unequiped)
			{
				iu.addModifiedItem(itm);
			}
			sendInventoryUpdate(iu);
			
			abortAttack();
			broadcastUserInfo();
			
			// this can be 0 if the user pressed the right mousebutton twice very fast
			if (unequiped.length > 0)
			{
				SystemMessage sm = null;
				if (unequiped[0].getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.S1_S2_HAS_BEEN_UNEQUIPPED);
					sm.addInt(unequiped[0].getEnchantLevel());
					sm.addItemName(unequiped[0]);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
					sm.addItemName(unequiped[0]);
				}
				sendPacket(sm);
			}
		}
		return true;
	}
	
	public boolean mount(Summon pet)
	{
		if (!Config.ALLOW_MOUNTS_DURING_SIEGE && isInsideZone(ZoneId.SIEGE))
		{
			return false;
		}
		
		if (!disarmWeapons() || !disarmShield() || isTransformed())
		{
			return false;
		}
		
		getEffectList().stopAllToggles();
		setMount(pet.getId(), pet.getLevel());
		setMountObjectID(pet.getControlObjectId());
		clearPetData();
		startFeed(pet.getId());
		broadcastPacket(new Ride(this));
		
		// Notify self and others about speed change
		broadcastUserInfo();
		
		pet.unSummon(this);
		return true;
	}
	
	public boolean mount(int npcId, int controlItemObjId, boolean useFood)
	{
		if (!disarmWeapons() || !disarmShield() || isTransformed())
		{
			return false;
		}
		
		getEffectList().stopAllToggles();
		setMount(npcId, getLevel());
		clearPetData();
		setMountObjectID(controlItemObjId);
		broadcastPacket(new Ride(this));
		
		// Notify self and others about speed change
		broadcastUserInfo();
		if (useFood)
		{
			startFeed(npcId);
		}
		return true;
	}
	
	public boolean mountPlayer(Summon pet)
	{
		if ((pet != null) && pet.isMountable() && !isMounted() && !isBetrayed())
		{
			if (isDead())
			{
				// A strider cannot be ridden when dead
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_CANNOT_USE_A_MOUNT_WHILE_DEAD);
				return false;
			}
			else if (pet.isDead())
			{
				// A dead strider cannot be ridden.
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_CANNOT_USE_A_DEAD_MOUNT);
				return false;
			}
			else if (pet.isInCombat() || pet.isRooted())
			{
				// A strider in battle cannot be ridden
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_CANNOT_USE_A_MOUNT_THAT_IS_IN_BATTLE);
				return false;
				
			}
			else if (isInCombat())
			{
				// A strider cannot be ridden while in battle
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_CANNOT_USE_A_MOUNT_WHILE_IN_BATTLE);
				return false;
			}
			else if (_waitTypeSitting)
			{
				// A strider can be ridden only when standing
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_MUST_BE_STANDING_TO_USE_A_MOUNT);
				return false;
			}
			else if (isFishing())
			{
				// You can't mount, dismount, break and drop items while fishing
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
				return false;
			}
			else if (isTransformed() || isCursedWeaponEquipped())
			{
				// no message needed, player while transformed doesn't have mount action
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			else if (_inventory.getItemByItemId(9819) != null)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				// FIXME: Wrong Message
				sendMessage("You cannot mount a steed while holding a flag.");
				return false;
			}
			else if (pet.isHungry())
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_CAN_NEITHER_MOUNT_NOR_DISMOUNT_WHILE_HUNGRY);
				return false;
			}
			else if (!Util.checkIfInRange(200, this, pet, true))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_ARE_TOO_FAR_AWAY_FROM_YOUR_MOUNT_TO_RIDE);
				return false;
			}
			else if (!pet.isDead() && !isMounted())
			{
				mount(pet);
			}
		}
		else if (isRentedPet())
		{
			stopRentPet();
		}
		else if (isMounted())
		{
			if ((_mountType == MountType.WYVERN) && isInsideZone(ZoneId.NO_LANDING))
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION);
				return false;
			}
			else if (isHungry())
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				sendPacket(SystemMessageId.YOU_CAN_NEITHER_MOUNT_NOR_DISMOUNT_WHILE_HUNGRY);
				return false;
			}
			else
			{
				dismount();
			}
		}
		return true;
	}
	
	public boolean dismount()
	{
		WaterZone water = null;
		for (ZoneType zone : ZoneManager.getInstance().getZones(getX(), getY(), getZ() - 300))
		{
			if (zone instanceof WaterZone)
			{
				water = (WaterZone) zone;
			}
		}
		if (water == null)
		{
			if (!isInWater() && (getZ() > 10000))
			{
				sendPacket(SystemMessageId.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION);
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			if ((GeoEngine.getInstance().getHeight(getX(), getY(), getZ()) + 300) < getZ())
			{
				sendPacket(SystemMessageId.YOU_CANNOT_DISMOUNT_FROM_THIS_ELEVATION);
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
		}
		else
		{
			ThreadPool.schedule(() ->
			{
				if (isInWater())
				{
					broadcastUserInfo();
				}
			}, 1500);
		}
		
		final boolean wasFlying = isFlying();
		sendPacket(new SetupGauge(3, 0, 0));
		final int petId = _mountNpcId;
		setMount(0, 0);
		stopFeed();
		clearPetData();
		if (wasFlying)
		{
			removeSkill(CommonSkill.WYVERN_BREATH.getSkill());
		}
		broadcastPacket(new Ride(this));
		setMountObjectID(0);
		storePetFood(petId);
		// Notify self and others about speed change
		broadcastUserInfo();
		return true;
	}
	
	public void setUptime(long time)
	{
		_uptime = time;
	}
	
	public long getUptime()
	{
		return System.currentTimeMillis() - _uptime;
	}
	
	/**
	 * Return True if the PlayerInstance is invulnerable.
	 */
	@Override
	public boolean isInvul()
	{
		return super.isInvul() || isTeleportProtected();
	}
	
	/**
	 * Return True if the PlayerInstance has a Party in progress.
	 */
	@Override
	public boolean isInParty()
	{
		return _party != null;
	}
	
	/**
	 * Set the _party object of the PlayerInstance (without joining it).
	 * @param party
	 */
	public void setParty(Party party)
	{
		_party = party;
	}
	
	/**
	 * Set the _party object of the PlayerInstance AND join it.
	 * @param party
	 */
	public void joinParty(Party party)
	{
		if (party != null)
		{
			// First set the party otherwise this wouldn't be considered
			// as in a party into the Creature.updateEffectIcons() call.
			_party = party;
			party.addPartyMember(this);
		}
	}
	
	/**
	 * Manage the Leave Party task of the PlayerInstance.
	 */
	public void leaveParty()
	{
		if (isInParty())
		{
			_party.removePartyMember(this, MessageType.DISCONNECTED);
			_party = null;
		}
	}
	
	/**
	 * Return the _party object of the PlayerInstance.
	 */
	@Override
	public Party getParty()
	{
		return _party;
	}
	
	public boolean isInCommandChannel()
	{
		return isInParty() && _party.isInCommandChannel();
	}
	
	public CommandChannel getCommandChannel()
	{
		return (isInCommandChannel()) ? _party.getCommandChannel() : null;
	}
	
	/**
	 * Return True if the PlayerInstance is a GM.
	 */
	@Override
	public boolean isGM()
	{
		return _accessLevel.isGm();
	}
	
	/**
	 * Set the _accessLevel of the PlayerInstance.
	 * @param level
	 * @param broadcast
	 * @param updateInDb
	 */
	public void setAccessLevel(int level, boolean broadcast, boolean updateInDb)
	{
		AccessLevel accessLevel = AdminData.getInstance().getAccessLevel(level);
		if (accessLevel == null)
		{
			LOGGER.warning("Can't find access level " + level + " for character " + toString());
			accessLevel = AdminData.getInstance().getAccessLevel(0);
		}
		
		if ((accessLevel.getLevel() == 0) && (Config.DEFAULT_ACCESS_LEVEL > 0))
		{
			accessLevel = AdminData.getInstance().getAccessLevel(Config.DEFAULT_ACCESS_LEVEL);
			if (accessLevel == null)
			{
				LOGGER.warning("Config's default access level (" + Config.DEFAULT_ACCESS_LEVEL + ") is not defined, defaulting to 0!");
				accessLevel = AdminData.getInstance().getAccessLevel(0);
				Config.DEFAULT_ACCESS_LEVEL = 0;
			}
		}
		
		_accessLevel = accessLevel;
		
		_appearance.setNameColor(_accessLevel.getNameColor());
		_appearance.setTitleColor(_accessLevel.getTitleColor());
		if (broadcast)
		{
			broadcastUserInfo();
		}
		
		if (updateInDb)
		{
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_CHARACTER_ACCESS))
			{
				ps.setInt(1, accessLevel.getLevel());
				ps.setInt(2, getObjectId());
				ps.executeUpdate();
			}
			catch (SQLException e)
			{
				LOGGER.log(Level.WARNING, "Failed to update character's accesslevel in db: " + toString(), e);
			}
		}
		
		CharNameTable.getInstance().addName(this);
		
		if (accessLevel == null)
		{
			LOGGER.warning("Tryed to set unregistered access level " + level + " for " + toString() + ". Setting access level without privileges!");
		}
		else if (level > 0)
		{
			LOGGER.warning(_accessLevel.getName() + " access level set for character " + getName() + "! Just a warning to be careful ;)");
		}
	}
	
	public void setAccountAccesslevel(int level)
	{
		LoginServerThread.getInstance().sendAccessLevel(getAccountName(), level);
	}
	
	/**
	 * @return the _accessLevel of the PlayerInstance.
	 */
	@Override
	public AccessLevel getAccessLevel()
	{
		return _accessLevel;
	}
	
	/**
	 * Update Stats of the PlayerInstance client side by sending Server->Client packet UserInfo/StatusUpdate to this PlayerInstance and CharInfo/StatusUpdate to all PlayerInstance in its _KnownPlayers (broadcast).
	 * @param broadcastType
	 */
	public void updateAndBroadcastStatus(int broadcastType)
	{
		refreshOverloaded(true);
		refreshExpertisePenalty();
		// Send a Server->Client packet UserInfo to this PlayerInstance and CharInfo to all PlayerInstance in its _KnownPlayers (broadcast)
		if (broadcastType == 1)
		{
			sendPacket(new UserInfo(this));
		}
		if (broadcastType == 2)
		{
			broadcastUserInfo();
		}
	}
	
	/**
	 * Send a Server->Client StatusUpdate packet with Karma to the PlayerInstance and all PlayerInstance to inform (broadcast).
	 */
	public void broadcastReputation()
	{
		broadcastUserInfo(UserInfoType.SOCIAL);
		
		World.getInstance().forEachVisibleObject(this, PlayerInstance.class, player ->
		{
			if (!isVisibleFor(player))
			{
				return;
			}
			
			final int relation = getRelation(player);
			final Integer oldrelation = getKnownRelations().get(player.getObjectId());
			if ((oldrelation == null) || (oldrelation != relation))
			{
				final RelationChanged rc = new RelationChanged();
				rc.addRelation(this, relation, !isInsideZone(ZoneId.PEACE));
				if (hasSummon())
				{
					if (_pet != null)
					{
						rc.addRelation(_pet, relation, !isInsideZone(ZoneId.PEACE));
					}
					if (hasServitors())
					{
						getServitors().values().forEach(s -> rc.addRelation(s, relation, !isInsideZone(ZoneId.PEACE)));
					}
				}
				player.sendPacket(rc);
				getKnownRelations().put(player.getObjectId(), relation);
			}
		});
	}
	
	/**
	 * Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout).
	 * @param isOnline
	 * @param updateInDb
	 */
	public void setOnlineStatus(boolean isOnline, boolean updateInDb)
	{
		if (_isOnline != isOnline)
		{
			_isOnline = isOnline;
		}
		
		// Update the characters table of the database with online status and lastAccess (called when login and logout)
		if (updateInDb)
		{
			updateOnlineStatus();
		}
	}
	
	/**
	 * Update the characters table of the database with online status and lastAccess of this PlayerInstance (called when login and logout).
	 */
	public void updateOnlineStatus()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE charId=?"))
		{
			statement.setInt(1, isOnlineInt());
			statement.setLong(2, System.currentTimeMillis());
			statement.setInt(3, getObjectId());
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Failed updating character online status.", e);
		}
	}
	
	/**
	 * Create a new player in the characters table of the database.
	 * @return
	 */
	private boolean createDb()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_CHARACTER))
		{
			statement.setString(1, _accountName);
			statement.setInt(2, getObjectId());
			statement.setString(3, getName());
			statement.setInt(4, getLevel());
			statement.setInt(5, getMaxHp());
			statement.setDouble(6, getCurrentHp());
			statement.setInt(7, getMaxCp());
			statement.setDouble(8, getCurrentCp());
			statement.setInt(9, getMaxMp());
			statement.setDouble(10, getCurrentMp());
			statement.setInt(11, _appearance.getFace());
			statement.setInt(12, _appearance.getHairStyle());
			statement.setInt(13, _appearance.getHairColor());
			statement.setInt(14, _appearance.isFemale() ? 1 : 0);
			statement.setLong(15, getExp());
			statement.setLong(16, getSp());
			statement.setInt(17, getReputation());
			statement.setInt(18, _fame);
			statement.setInt(19, _raidbossPoints);
			statement.setInt(20, _pvpKills);
			statement.setInt(21, _pkKills);
			statement.setInt(22, _clanId);
			statement.setInt(23, getRace().ordinal());
			statement.setInt(24, getClassId().getId());
			statement.setLong(25, _deleteTimer);
			statement.setInt(26, _createItemLevel > 0 ? 1 : 0);
			statement.setString(27, getTitle());
			statement.setInt(28, _appearance.getTitleColor());
			statement.setInt(29, isOnlineInt());
			statement.setInt(30, _clanPrivileges.getBitmask());
			statement.setInt(31, _wantsPeace);
			statement.setInt(32, _baseClass);
			statement.setInt(33, _nobleLevel);
			statement.setLong(34, 0);
			statement.setInt(35, PlayerStat.MIN_VITALITY_POINTS);
			statement.setDate(36, new Date(_createDate.getTimeInMillis()));
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not insert char data: " + e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	/**
	 * Retrieve a PlayerInstance from the characters table of the database and add it in _allObjects of the L2world. <B><U> Actions</U> :</B>
	 * <li>Retrieve the PlayerInstance from the characters table of the database</li>
	 * <li>Add the PlayerInstance object in _allObjects</li>
	 * <li>Set the x,y,z position of the PlayerInstance and make it invisible</li>
	 * <li>Update the overloaded status of the PlayerInstance</li>
	 * @param objectId Identifier of the object to initialized
	 * @return The PlayerInstance loaded from the database
	 */
	private static PlayerInstance restore(int objectId)
	{
		PlayerInstance player = null;
		double currentCp = 0;
		double currentHp = 0;
		double currentMp = 0;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_CHARACTER))
		{
			// Retrieve the PlayerInstance from the characters table of the database
			statement.setInt(1, objectId);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					final int activeClassId = rset.getInt("classid");
					final boolean female = rset.getInt("sex") != Sex.MALE.ordinal();
					final PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(activeClassId);
					final PlayerAppearance app = new PlayerAppearance(rset.getByte("face"), rset.getByte("hairColor"), rset.getByte("hairStyle"), female);
					
					player = new PlayerInstance(objectId, template, rset.getString("account_name"), app);
					player.setName(rset.getString("char_name"));
					player.setLastAccess(rset.getLong("lastAccess"));
					
					player.getStat().setExp(rset.getLong("exp"));
					player.setExpBeforeDeath(rset.getLong("expBeforeDeath"));
					player.getStat().setLevel(rset.getByte("level"));
					player.getStat().setSp(rset.getLong("sp"));
					
					player.setWantsPeace(rset.getInt("wantspeace"));
					
					player.setHeading(rset.getInt("heading"));
					
					player.setInitialReputation(rset.getInt("reputation"));
					player.setFame(rset.getInt("fame"));
					player.setRaidbossPoints(rset.getInt("raidbossPoints"));
					player.setPvpKills(rset.getInt("pvpkills"));
					player.setPkKills(rset.getInt("pkkills"));
					player.setOnlineTime(rset.getLong("onlinetime"));
					final int nobleLevel = rset.getInt("nobless");
					player.setNobleLevel(nobleLevel);
					
					final int factionId = rset.getInt("faction");
					if (factionId == 1)
					{
						player.setGood();
					}
					if (factionId == 2)
					{
						player.setEvil();
					}
					
					player.setClanJoinExpiryTime(rset.getLong("clan_join_expiry_time"));
					if (player.getClanJoinExpiryTime() < System.currentTimeMillis())
					{
						player.setClanJoinExpiryTime(0);
					}
					player.setClanCreateExpiryTime(rset.getLong("clan_create_expiry_time"));
					if (player.getClanCreateExpiryTime() < System.currentTimeMillis())
					{
						player.setClanCreateExpiryTime(0);
					}
					
					player.setPcCafePoints(rset.getInt("pccafe_points"));
					
					final int clanId = rset.getInt("clanid");
					player.setPowerGrade(rset.getInt("power_grade"));
					player.getStat().setVitalityPoints(rset.getInt("vitality_points"));
					player.setPledgeType(rset.getInt("subpledge"));
					// player.setApprentice(rset.getInt("apprentice"));
					
					// Set Hero status if it applies.
					player.setHero(Hero.getInstance().isHero(objectId));
					
					if (clanId > 0)
					{
						player.setClan(ClanTable.getInstance().getClan(clanId));
					}
					
					if (player.getClan() != null)
					{
						if (player.getClan().getLeaderId() != player.getObjectId())
						{
							if (player.getPowerGrade() == 0)
							{
								player.setPowerGrade(5);
							}
							player.setClanPrivileges(player.getClan().getRankPrivs(player.getPowerGrade()));
						}
						else
						{
							player.getClanPrivileges().setAll();
							player.setPowerGrade(1);
						}
						player.setPledgeClass(ClanMember.calculatePledgeClass(player));
					}
					else
					{
						if (nobleLevel > 0)
						{
							player.setPledgeClass(5);
						}
						
						if (player.isHero())
						{
							player.setPledgeClass(8);
						}
						
						player.getClanPrivileges().clear();
					}
					
					player.setDeleteTimer(rset.getLong("deletetime"));
					player.setTitle(rset.getString("title"));
					player.setAccessLevel(rset.getInt("accesslevel"), false, false);
					final int titleColor = rset.getInt("title_color");
					if (titleColor != PlayerAppearance.DEFAULT_TITLE_COLOR)
					{
						player.getAppearance().setTitleColor(titleColor);
					}
					player.setFistsWeaponItem(player.findFistsWeaponItem(activeClassId));
					player.setUptime(System.currentTimeMillis());
					
					currentHp = rset.getDouble("curHp");
					currentCp = rset.getDouble("curCp");
					currentMp = rset.getDouble("curMp");
					
					player.setClassIndex(0);
					try
					{
						player.setBaseClass(rset.getInt("base_class"));
					}
					catch (Exception e)
					{
						player.setBaseClass(activeClassId);
						LOGGER.log(Level.WARNING, "Exception during player.setBaseClass for player: " + player + " base class: " + rset.getInt("base_class"), e);
					}
					
					// Restore Subclass Data (cannot be done earlier in function)
					if (restoreSubClassData(player))
					{
						if (activeClassId != player.getBaseClass())
						{
							for (SubClass subClass : player.getSubClasses().values())
							{
								if (subClass.getClassId() == activeClassId)
								{
									player.setClassIndex(subClass.getClassIndex());
								}
							}
						}
					}
					if ((player.getClassIndex() == 0) && (activeClassId != player.getBaseClass()))
					{
						// Subclass in use but doesn't exist in DB -
						// a possible restart-while-modifysubclass cheat has been attempted.
						// Switching to use base class
						player.setClassId(player.getBaseClass());
						LOGGER.warning("Player " + player.getName() + " reverted to base class. Possibly has tried a relogin exploit while subclassing.");
					}
					else
					{
						player._activeClass = activeClassId;
					}
					
					player.setApprentice(rset.getInt("apprentice"));
					player.setSponsor(rset.getInt("sponsor"));
					player.setLvlJoinedAcademy(rset.getInt("lvl_joined_academy"));
					
					CursedWeaponsManager.getInstance().checkPlayer(player);
					
					// Set the x,y,z position of the PlayerInstance and make it invisible
					final int x = rset.getInt("x");
					final int y = rset.getInt("y");
					final int z = rset.getInt("z");
					player.setXYZInvisible(x, y, z);
					player.setLastServerPosition(x, y, z);
					
					// Set Teleport Bookmark Slot
					player.setBookMarkSlot(rset.getInt("BookmarkSlot"));
					
					// character creation Time
					player.getCreateDate().setTime(rset.getDate("createDate"));
					
					// Language
					player.setLang(rset.getString("language"));
					
					// Retrieve the name and ID of the other characters assigned to this account.
					try (PreparedStatement stmt = con.prepareStatement("SELECT charId, char_name FROM characters WHERE account_name=? AND charId<>?"))
					{
						stmt.setString(1, player._accountName);
						stmt.setInt(2, objectId);
						try (ResultSet chars = stmt.executeQuery())
						{
							while (chars.next())
							{
								player._chars.put(chars.getInt("charId"), chars.getString("char_name"));
							}
						}
					}
				}
			}
			
			if (player == null)
			{
				return null;
			}
			
			if (player.isGM())
			{
				final long masks = player.getVariables().getLong(COND_OVERRIDE_KEY, PlayerCondOverride.getAllExceptionsMask());
				player.setOverrideCond(masks);
			}
			
			// Retrieve from the database all secondary data of this PlayerInstance
			// Note that Clan, Noblesse and Hero skills are given separately and not here.
			// Retrieve from the database all skills of this PlayerInstance and add them to _skills
			player.restoreCharData();
			
			// Reward auto-get skills and all available skills if auto-learn skills is true.
			player.rewardSkills();
			
			// Retrieve from the database all items of this PlayerInstance and add them to _inventory
			player.getInventory().restore();
			player.getFreight().restore();
			if (!Config.WAREHOUSE_CACHE)
			{
				player.getWarehouse();
			}
			
			player.restoreItemReuse();
			
			// Restore player shortcuts
			player.restoreShortCuts();
			
			// Initialize status update cache
			player.initStatusUpdateCache();
			
			// Restore current Cp, HP and MP values
			player.setCurrentCp(currentCp);
			player.setCurrentHp(currentHp);
			player.setCurrentMp(currentMp);
			
			player.setOriginalCpHpMp(currentCp, currentHp, currentMp);
			
			if (currentHp < 0.5)
			{
				player.setIsDead(true);
				player.stopHpMpRegeneration();
			}
			
			// Restore pet if exists in the world
			player.setPet(World.getInstance().getPet(player.getObjectId()));
			final Summon pet = player.getPet();
			if (pet != null)
			{
				pet.setOwner(player);
			}
			
			if (player.hasServitors())
			{
				for (Summon summon : player.getServitors().values())
				{
					summon.setOwner(player);
				}
			}
			
			// CoC Monthly winner. (True Hero)
			final int trueHeroId = GlobalVariablesManager.getInstance().getInt(GlobalVariablesManager.COC_TRUE_HERO, 0);
			if (trueHeroId == player.getObjectId())
			{
				if (!GlobalVariablesManager.getInstance().getBoolean(GlobalVariablesManager.COC_TRUE_HERO_REWARDED, true))
				{
					GlobalVariablesManager.getInstance().set(GlobalVariablesManager.COC_TRUE_HERO_REWARDED, true);
					player.addItem("CoC-Hero", 35565, 1, player, true); // Mysterious Belt
					player.addItem("CoC-Hero", 35564, 1, player, true); // Ruler's Authority
					player.setFame(player.getFame() + 5000);
					player.sendMessage("You have been rewarded with 5.000 fame points.");
				}
				player.setTrueHero(true);
			}
			
			// Recalculate all stats
			player.getStat().recalculateStats(false);
			
			// Update the overloaded status of the PlayerInstance
			player.refreshOverloaded(false);
			
			// Update the expertise status of the PlayerInstance
			player.refreshExpertisePenalty();
			
			player.restoreFriendList();
			
			player.loadRecommendations();
			player.startRecoGiveTask();
			
			player.setOnlineStatus(true, false);
			
			player.startAutoSaveTask();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Failed loading character.", e);
		}
		return player;
	}
	
	/**
	 * @return
	 */
	public Forum getMail()
	{
		if (_forumMail == null)
		{
			setMail(ForumsBBSManager.getInstance().getForumByName("MailRoot").getChildByName(getName()));
			
			if (_forumMail == null)
			{
				ForumsBBSManager.getInstance().createNewForum(getName(), ForumsBBSManager.getInstance().getForumByName("MailRoot"), Forum.MAIL, Forum.OWNERONLY, getObjectId());
				setMail(ForumsBBSManager.getInstance().getForumByName("MailRoot").getChildByName(getName()));
			}
		}
		
		return _forumMail;
	}
	
	/**
	 * @param forum
	 */
	public void setMail(Forum forum)
	{
		_forumMail = forum;
	}
	
	/**
	 * @return
	 */
	public Forum getMemo()
	{
		if (_forumMemo == null)
		{
			setMemo(ForumsBBSManager.getInstance().getForumByName("MemoRoot").getChildByName(_accountName));
			
			if (_forumMemo == null)
			{
				ForumsBBSManager.getInstance().createNewForum(_accountName, ForumsBBSManager.getInstance().getForumByName("MemoRoot"), Forum.MEMO, Forum.OWNERONLY, getObjectId());
				setMemo(ForumsBBSManager.getInstance().getForumByName("MemoRoot").getChildByName(_accountName));
			}
		}
		
		return _forumMemo;
	}
	
	/**
	 * @param forum
	 */
	public void setMemo(Forum forum)
	{
		_forumMemo = forum;
	}
	
	/**
	 * Restores sub-class data for the PlayerInstance, used to check the current class index for the character.
	 * @param player
	 * @return
	 */
	private static boolean restoreSubClassData(PlayerInstance player)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_SUBCLASSES))
		{
			statement.setInt(1, player.getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final SubClass subClass = new SubClass();
					subClass.setClassId(rset.getInt("class_id"));
					subClass.setIsDualClass(rset.getBoolean("dual_class"));
					subClass.setVitalityPoints(rset.getInt("vitality_points"));
					subClass.setLevel(rset.getByte("level"));
					subClass.setExp(rset.getLong("exp"));
					subClass.setSp(rset.getLong("sp"));
					subClass.setClassIndex(rset.getInt("class_index"));
					
					// Enforce the correct indexing of _subClasses against their class indexes.
					player.getSubClasses().put(subClass.getClassIndex(), subClass);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not restore classes for " + player.getName() + ": " + e.getMessage(), e);
		}
		return true;
	}
	
	/**
	 * Restores:
	 * <ul>
	 * <li>Skills</li>
	 * <li>Macros</li>
	 * <li>Henna</li>
	 * <li>Teleport Bookmark</li>
	 * <li>Recipe Book</li>
	 * <li>Recipe Shop List (If configuration enabled)</li>
	 * <li>Premium Item List</li>
	 * <li>Pet Inventory Items</li>
	 * </ul>
	 */
	private void restoreCharData()
	{
		// Retrieve from the database all skills of this PlayerInstance and add them to _skills.
		restoreSkills();
		
		// Retrieve from the database all macroses of this PlayerInstance and add them to _macros.
		_macros.restoreMe();
		
		// Retrieve from the database all henna of this PlayerInstance and add them to _henna.
		restoreHenna();
		
		// Retrieve from the database all teleport bookmark of this PlayerInstance and add them to _tpbookmark.
		restoreTeleportBookmark();
		
		// Retrieve from the database the recipe book of this PlayerInstance.
		restoreRecipeBook(true);
		
		// Restore Recipe Shop list.
		if (Config.STORE_RECIPE_SHOPLIST)
		{
			restoreRecipeShopList();
		}
		
		// Load Premium Item List.
		loadPremiumItemList();
		
		// Restore items in pet inventory.
		restorePetInventoryItems();
	}
	
	/**
	 * Restores:
	 * <ul>
	 * <li>Short-cuts</li>
	 * </ul>
	 */
	private void restoreShortCuts()
	{
		// Retrieve from the database all shortCuts of this PlayerInstance and add them to _shortCuts.
		_shortCuts.restoreMe();
	}
	
	/**
	 * Restore recipe book data for this PlayerInstance.
	 * @param loadCommon
	 */
	private void restoreRecipeBook(boolean loadCommon)
	{
		final String sql = loadCommon ? "SELECT id, type, classIndex FROM character_recipebook WHERE charId=?" : "SELECT id FROM character_recipebook WHERE charId=? AND classIndex=? AND type = 1";
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(sql))
		{
			statement.setInt(1, getObjectId());
			if (!loadCommon)
			{
				statement.setInt(2, _classIndex);
			}
			
			try (ResultSet rset = statement.executeQuery())
			{
				_dwarvenRecipeBook.clear();
				
				RecipeHolder recipe;
				RecipeData rd = RecipeData.getInstance();
				while (rset.next())
				{
					recipe = rd.getRecipe(rset.getInt("id"));
					if (loadCommon)
					{
						if (rset.getInt(2) == 1)
						{
							if (rset.getInt(3) == _classIndex)
							{
								registerDwarvenRecipeList(recipe, false);
							}
						}
						else
						{
							registerCommonRecipeList(recipe, false);
						}
					}
					else
					{
						registerDwarvenRecipeList(recipe, false);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not restore recipe book data:" + e.getMessage(), e);
		}
	}
	
	public Map<Integer, PremiumItem> getPremiumItemList()
	{
		return _premiumItems;
	}
	
	private void loadPremiumItemList()
	{
		final String sql = "SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?";
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(sql))
		{
			statement.setInt(1, getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final int itemNum = rset.getInt("itemNum");
					final int itemId = rset.getInt("itemId");
					final long itemCount = rset.getLong("itemCount");
					final String itemSender = rset.getString("itemSender");
					_premiumItems.put(itemNum, new PremiumItem(itemId, itemCount, itemSender));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not restore premium items: " + e.getMessage(), e);
		}
	}
	
	public void updatePremiumItem(int itemNum, long newcount)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE character_premium_items SET itemCount=? WHERE charId=? AND itemNum=? "))
		{
			statement.setLong(1, newcount);
			statement.setInt(2, getObjectId());
			statement.setInt(3, itemNum);
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not update premium items: " + e.getMessage(), e);
		}
	}
	
	public void deletePremiumItem(int itemNum)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_premium_items WHERE charId=? AND itemNum=? "))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, itemNum);
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.severe("Could not delete premium item: " + e);
		}
	}
	
	/**
	 * Update PlayerInstance stats in the characters table of the database.
	 * @param storeActiveEffects
	 */
	public synchronized void store(boolean storeActiveEffects)
	{
		storeCharBase();
		storeCharSub();
		storeEffect(storeActiveEffects);
		storeItemReuseDelay();
		if (Config.STORE_RECIPE_SHOPLIST)
		{
			storeRecipeShopList();
		}
		
		final PlayerVariables vars = getScript(PlayerVariables.class);
		if (vars != null)
		{
			vars.storeMe();
		}
		
		final AccountVariables aVars = getScript(AccountVariables.class);
		if (aVars != null)
		{
			aVars.storeMe();
		}
	}
	
	@Override
	public void storeMe()
	{
		store(true);
	}
	
	private void storeCharBase()
	{
		// Get the exp, level, and sp of base class to store in base table
		final long exp = getStat().getBaseExp();
		final int level = getStat().getBaseLevel();
		final long sp = getStat().getBaseSp();
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_CHARACTER))
		{
			statement.setInt(1, level);
			statement.setInt(2, getMaxHp());
			statement.setDouble(3, getCurrentHp());
			statement.setInt(4, getMaxCp());
			statement.setDouble(5, getCurrentCp());
			statement.setInt(6, getMaxMp());
			statement.setDouble(7, getCurrentMp());
			statement.setInt(8, _appearance.getFace());
			statement.setInt(9, _appearance.getHairStyle());
			statement.setInt(10, _appearance.getHairColor());
			statement.setInt(11, _appearance.isFemale() ? 1 : 0);
			statement.setInt(12, getHeading());
			statement.setInt(13, _lastLoc != null ? _lastLoc.getX() : getX());
			statement.setInt(14, _lastLoc != null ? _lastLoc.getY() : getY());
			statement.setInt(15, _lastLoc != null ? _lastLoc.getZ() : getZ());
			statement.setLong(16, exp);
			statement.setLong(17, _expBeforeDeath);
			statement.setLong(18, sp);
			statement.setInt(19, getReputation());
			statement.setInt(20, _fame);
			statement.setInt(21, _raidbossPoints);
			statement.setInt(22, _pvpKills);
			statement.setInt(23, _pkKills);
			statement.setInt(24, _clanId);
			statement.setInt(25, getRace().ordinal());
			statement.setInt(26, getClassId().getId());
			statement.setLong(27, _deleteTimer);
			statement.setString(28, getTitle());
			statement.setInt(29, _appearance.getTitleColor());
			statement.setInt(30, isOnlineInt());
			statement.setInt(31, _clanPrivileges.getBitmask());
			statement.setInt(32, _wantsPeace);
			statement.setInt(33, _baseClass);
			
			long totalOnlineTime = _onlineTime;
			if (_onlineBeginTime > 0)
			{
				totalOnlineTime += (System.currentTimeMillis() - _onlineBeginTime) / 1000;
			}
			
			statement.setLong(34, totalOnlineTime);
			statement.setInt(35, _nobleLevel);
			statement.setInt(36, _powerGrade);
			statement.setInt(37, _pledgeType);
			statement.setInt(38, _lvlJoinedAcademy);
			statement.setLong(39, _apprentice);
			statement.setLong(40, _sponsor);
			statement.setLong(41, _clanJoinExpiryTime);
			statement.setLong(42, _clanCreateExpiryTime);
			statement.setString(43, getName());
			statement.setInt(44, _bookmarkslot);
			statement.setInt(45, getStat().getBaseVitalityPoints());
			statement.setString(46, _lang);
			
			int factionId = 0;
			if (_isGood)
			{
				factionId = 1;
			}
			if (_isEvil)
			{
				factionId = 2;
			}
			statement.setInt(47, factionId);
			statement.setInt(48, _pcCafePoints);
			statement.setInt(49, getObjectId());
			
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not store char base data: " + this + " - " + e.getMessage(), e);
		}
	}
	
	private void storeCharSub()
	{
		if (getTotalSubClasses() <= 0)
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_CHAR_SUBCLASS))
		{
			for (SubClass subClass : getSubClasses().values())
			{
				statement.setLong(1, subClass.getExp());
				statement.setLong(2, subClass.getSp());
				statement.setInt(3, subClass.getLevel());
				statement.setInt(4, subClass.getVitalityPoints());
				statement.setInt(5, subClass.getClassId());
				statement.setBoolean(6, subClass.isDualClass());
				statement.setInt(7, getObjectId());
				statement.setInt(8, subClass.getClassIndex());
				statement.addBatch();
			}
			statement.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not store sub class data for " + getName() + ": " + e.getMessage(), e);
		}
	}
	
	@Override
	public void storeEffect(boolean storeEffects)
	{
		if (!Config.STORE_SKILL_COOLTIME)
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			// Delete all current stored effects for char to avoid dupe
			try (PreparedStatement delete = con.prepareStatement(DELETE_SKILL_SAVE))
			{
				delete.setInt(1, getObjectId());
				delete.setInt(2, _classIndex);
				delete.execute();
			}
			
			int buff_index = 0;
			final List<Long> storedSkills = new ArrayList<>();
			final long currentTime = System.currentTimeMillis();
			
			// Store all effect data along with calulated remaining
			// reuse delays for matching skills. 'restore_type'= 0.
			try (PreparedStatement statement = con.prepareStatement(ADD_SKILL_SAVE))
			{
				if (storeEffects)
				{
					for (BuffInfo info : getEffectList().getEffects())
					{
						if (info == null)
						{
							continue;
						}
						
						final Skill skill = info.getSkill();
						
						// Do not store those effects.
						if (skill.isDeleteAbnormalOnLeave())
						{
							continue;
						}
						
						// Do not save heals.
						if (skill.getAbnormalType() == AbnormalType.LIFE_FORCE_OTHERS)
						{
							continue;
						}
						
						// Toggles are skipped, unless they are necessary to be always on.
						if ((skill.isToggle() && !skill.isNecessaryToggle()))
						{
							continue;
						}
						
						if (skill.isMentoring())
						{
							continue;
						}
						
						// Dances and songs are not kept in retail.
						if (skill.isDance() && !Config.ALT_STORE_DANCES)
						{
							continue;
						}
						
						if (storedSkills.contains(skill.getReuseHashCode()))
						{
							continue;
						}
						
						storedSkills.add(skill.getReuseHashCode());
						
						statement.setInt(1, getObjectId());
						statement.setInt(2, skill.getId());
						statement.setInt(3, skill.getLevel());
						statement.setInt(4, skill.getSubLevel());
						statement.setInt(5, info.getTime());
						
						final TimeStamp t = getSkillReuseTimeStamp(skill.getReuseHashCode());
						statement.setLong(6, (t != null) && (currentTime < t.getStamp()) ? t.getReuse() : 0);
						statement.setDouble(7, (t != null) && (currentTime < t.getStamp()) ? t.getStamp() : 0);
						
						statement.setInt(8, 0); // Store type 0, active buffs/debuffs.
						statement.setInt(9, _classIndex);
						statement.setInt(10, ++buff_index);
						statement.addBatch();
					}
				}
				
				// Skills under reuse.
				for (Entry<Long, TimeStamp> ts : getSkillReuseTimeStamps().entrySet())
				{
					final long hash = ts.getKey();
					if (storedSkills.contains(hash))
					{
						continue;
					}
					
					final TimeStamp t = ts.getValue();
					if ((t != null) && (currentTime < t.getStamp()))
					{
						storedSkills.add(hash);
						
						statement.setInt(1, getObjectId());
						statement.setInt(2, t.getSkillId());
						statement.setInt(3, t.getSkillLvl());
						statement.setInt(4, t.getSkillSubLvl());
						statement.setInt(5, -1);
						statement.setLong(6, t.getReuse());
						statement.setDouble(7, t.getStamp());
						statement.setInt(8, 1); // Restore type 1, skill reuse.
						statement.setInt(9, _classIndex);
						statement.setInt(10, ++buff_index);
						statement.addBatch();
					}
				}
				
				statement.executeBatch();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not store char effect data: ", e);
		}
	}
	
	private void storeItemReuseDelay()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps1 = con.prepareStatement(DELETE_ITEM_REUSE_SAVE);
			PreparedStatement ps2 = con.prepareStatement(ADD_ITEM_REUSE_SAVE))
		{
			ps1.setInt(1, getObjectId());
			ps1.execute();
			
			final long currentTime = System.currentTimeMillis();
			for (TimeStamp ts : getItemReuseTimeStamps().values())
			{
				if ((ts != null) && (currentTime < ts.getStamp()))
				{
					ps2.setInt(1, getObjectId());
					ps2.setInt(2, ts.getItemId());
					ps2.setInt(3, ts.getItemObjectId());
					ps2.setLong(4, ts.getReuse());
					ps2.setDouble(5, ts.getStamp());
					ps2.addBatch();
				}
			}
			ps2.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not store char item reuse data: ", e);
		}
	}
	
	/**
	 * @return True if the PlayerInstance is on line.
	 */
	public boolean isOnline()
	{
		return _isOnline;
	}
	
	public int isOnlineInt()
	{
		if (_isOnline && (_client != null))
		{
			return _client.isDetached() ? 2 : 1;
		}
		return 0;
	}
	
	/**
	 * Verifies if the player is in offline mode.<br>
	 * The offline mode may happen for different reasons:<br>
	 * Abnormally: Player gets abruptly disconnected from server.<br>
	 * Normally: The player gets into offline shop mode, only available by enabling the offline shop mod.
	 * @return {@code true} if the player is in offline mode, {@code false} otherwise
	 */
	public boolean isInOfflineMode()
	{
		return (_client == null) || _client.isDetached();
	}
	
	@Override
	public Skill addSkill(Skill newSkill)
	{
		addCustomSkill(newSkill);
		return super.addSkill(newSkill);
	}
	
	/**
	 * Add a skill to the PlayerInstance _skills and its Func objects to the calculator set of the PlayerInstance and save update in the character_skills table of the database. <B><U> Concept</U> :</B> All skills own by a PlayerInstance are identified in <B>_skills</B> <B><U> Actions</U> :</B>
	 * <li>Replace oldSkill by newSkill or Add the newSkill</li>
	 * <li>If an old skill has been replaced, remove all its Func objects of Creature calculator set</li>
	 * <li>Add Func objects of newSkill to the calculator set of the Creature</li>
	 * @param newSkill The Skill to add to the Creature
	 * @param store
	 * @return The Skill replaced or null if just added a new Skill
	 */
	public Skill addSkill(Skill newSkill, boolean store)
	{
		// Add a skill to the PlayerInstance _skills and its Func objects to the calculator set of the PlayerInstance
		final Skill oldSkill = addSkill(newSkill);
		// Add or update a PlayerInstance skill in the character_skills table of the database
		if (store)
		{
			storeSkill(newSkill, oldSkill, -1);
		}
		return oldSkill;
	}
	
	@Override
	public Skill removeSkill(Skill skill, boolean store)
	{
		removeCustomSkill(skill);
		return store ? removeSkill(skill) : super.removeSkill(skill, true);
	}
	
	public Skill removeSkill(Skill skill, boolean store, boolean cancelEffect)
	{
		removeCustomSkill(skill);
		return store ? removeSkill(skill) : super.removeSkill(skill, cancelEffect);
	}
	
	/**
	 * Remove a skill from the Creature and its Func objects from calculator set of the Creature and save update in the character_skills table of the database. <B><U> Concept</U> :</B> All skills own by a Creature are identified in <B>_skills</B> <B><U> Actions</U> :</B>
	 * <li>Remove the skill from the Creature _skills</li>
	 * <li>Remove all its Func objects from the Creature calculator set</li> <B><U> Overridden in </U> :</B>
	 * <li>PlayerInstance : Save update in the character_skills table of the database</li>
	 * @param skill The Skill to remove from the Creature
	 * @return The Skill removed
	 */
	public Skill removeSkill(Skill skill)
	{
		removeCustomSkill(skill);
		
		// Remove a skill from the Creature and its stats
		final Skill oldSkill = super.removeSkill(skill, true);
		if (oldSkill != null)
		{
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement(DELETE_SKILL_FROM_CHAR))
			{
				// Remove or update a PlayerInstance skill from the character_skills table of the database
				statement.setInt(1, oldSkill.getId());
				statement.setInt(2, getObjectId());
				statement.setInt(3, _classIndex);
				statement.execute();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "Error could not delete skill: " + e.getMessage(), e);
			}
		}
		
		if ((getTransformationId() > 0) || isCursedWeaponEquipped())
		{
			return oldSkill;
		}
		
		if (skill != null)
		{
			for (Shortcut sc : _shortCuts.getAllShortCuts())
			{
				if ((sc != null) && (sc.getId() == skill.getId()) && (sc.getType() == ShortcutType.SKILL) && !((skill.getId() >= 3080) && (skill.getId() <= 3259)))
				{
					deleteShortCut(sc.getSlot(), sc.getPage());
				}
			}
		}
		return oldSkill;
	}
	
	/**
	 * Add or update a PlayerInstance skill in the character_skills table of the database.<br>
	 * If newClassIndex > -1, the skill will be stored with that class index, not the current one.
	 * @param newSkill
	 * @param oldSkill
	 * @param newClassIndex
	 */
	private void storeSkill(Skill newSkill, Skill oldSkill, int newClassIndex)
	{
		final int classIndex = (newClassIndex > -1) ? newClassIndex : _classIndex;
		try (Connection con = DatabaseFactory.getConnection())
		{
			if ((oldSkill != null) && (newSkill != null))
			{
				try (PreparedStatement ps = con.prepareStatement(UPDATE_CHARACTER_SKILL_LEVEL))
				{
					ps.setInt(1, newSkill.getLevel());
					ps.setInt(2, newSkill.getSubLevel());
					ps.setInt(3, oldSkill.getId());
					ps.setInt(4, getObjectId());
					ps.setInt(5, classIndex);
					ps.execute();
				}
			}
			else if (newSkill != null)
			{
				try (PreparedStatement ps = con.prepareStatement(ADD_NEW_SKILLS))
				{
					ps.setInt(1, getObjectId());
					ps.setInt(2, newSkill.getId());
					ps.setInt(3, newSkill.getLevel());
					ps.setInt(4, newSkill.getSubLevel());
					ps.setInt(5, classIndex);
					ps.execute();
				}
			}
			// else
			// {
			// LOGGER.warning("Could not store new skill, it's null!");
			// }
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error could not store char skills: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Adds or updates player's skills in the database.
	 * @param newSkills the list of skills to store
	 * @param newClassIndex if newClassIndex > -1, the skills will be stored for that class index, not the current one
	 */
	private void storeSkills(List<Skill> newSkills, int newClassIndex)
	{
		if (newSkills.isEmpty())
		{
			return;
		}
		
		final int classIndex = (newClassIndex > -1) ? newClassIndex : _classIndex;
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(ADD_NEW_SKILLS))
		{
			for (Skill addSkill : newSkills)
			{
				ps.setInt(1, getObjectId());
				ps.setInt(2, addSkill.getId());
				ps.setInt(3, addSkill.getLevel());
				ps.setInt(4, addSkill.getSubLevel());
				ps.setInt(5, classIndex);
				ps.addBatch();
			}
			ps.executeBatch();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Error could not store char skills: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Retrieve from the database all skills of this PlayerInstance and add them to _skills.
	 */
	private void restoreSkills()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_SKILLS_FOR_CHAR))
		{
			// Retrieve all skills of this PlayerInstance from the database
			statement.setInt(1, getObjectId());
			statement.setInt(2, _classIndex);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final int id = rset.getInt("skill_id");
					final int level = rset.getInt("skill_level");
					final int subLevel = rset.getInt("skill_sub_level");
					
					// Create a Skill object for each record
					final Skill skill = SkillData.getInstance().getSkill(id, level, subLevel);
					
					if (skill == null)
					{
						LOGGER.warning("Skipped null skill Id: " + id + " Level: " + level + " while restoring player skills for playerObjId: " + getObjectId());
						continue;
					}
					
					// Add the Skill object to the Creature _skills and its Func objects to the calculator set of the Creature
					addSkill(skill);
					
					if (Config.SKILL_CHECK_ENABLE && (!canOverrideCond(PlayerCondOverride.SKILL_CONDITIONS) || Config.SKILL_CHECK_GM))
					{
						if (!SkillTreesData.getInstance().isSkillAllowed(this, skill))
						{
							Util.handleIllegalPlayerAction(this, "Player " + getName() + " has invalid skill " + skill.getName() + " (" + skill.getId() + "/" + skill.getLevel() + "), class:" + ClassListData.getInstance().getClass(getClassId()).getClassName(), IllegalActionPunishmentType.BROADCAST);
							if (Config.SKILL_CHECK_REMOVE)
							{
								removeSkill(skill);
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not restore character " + this + " skills: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Retrieve from the database all skill effects of this PlayerInstance and add them to the player.
	 */
	@Override
	public void restoreEffects()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_SKILL_SAVE))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, _classIndex);
			try (ResultSet rset = statement.executeQuery())
			{
				final long currentTime = System.currentTimeMillis();
				while (rset.next())
				{
					final int remainingTime = rset.getInt("remaining_time");
					final long reuseDelay = rset.getLong("reuse_delay");
					final long systime = rset.getLong("systime");
					final int restoreType = rset.getInt("restore_type");
					
					final Skill skill = SkillData.getInstance().getSkill(rset.getInt("skill_id"), rset.getInt("skill_level"), rset.getInt("skill_sub_level"));
					if (skill == null)
					{
						continue;
					}
					
					final long time = systime - currentTime;
					if (time > 10)
					{
						disableSkill(skill, time);
						addTimeStamp(skill, reuseDelay, systime);
					}
					
					// Restore Type 1 The remaning skills lost effect upon logout but were still under a high reuse delay.
					if (restoreType > 0)
					{
						continue;
					}
					
					// Restore Type 0 These skill were still in effect on the character upon logout.
					// Some of which were self casted and might still have had a long reuse delay which also is restored.
					skill.applyEffects(this, this, false, remainingTime);
				}
			}
			// Remove previously restored skills
			try (PreparedStatement delete = con.prepareStatement(DELETE_SKILL_SAVE))
			{
				delete.setInt(1, getObjectId());
				delete.setInt(2, _classIndex);
				delete.executeUpdate();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not restore " + this + " active effect data: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Retrieve from the database all Item Reuse Time of this PlayerInstance and add them to the player.
	 */
	private void restoreItemReuse()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_ITEM_REUSE_SAVE);
			PreparedStatement delete = con.prepareStatement(DELETE_ITEM_REUSE_SAVE))
		{
			statement.setInt(1, getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				int itemId;
				long reuseDelay;
				long systime;
				boolean isInInventory;
				long remainingTime;
				final long currentTime = System.currentTimeMillis();
				while (rset.next())
				{
					itemId = rset.getInt("itemId");
					reuseDelay = rset.getLong("reuseDelay");
					systime = rset.getLong("systime");
					isInInventory = true;
					
					// Using item Id
					ItemInstance item = _inventory.getItemByItemId(itemId);
					if (item == null)
					{
						item = getWarehouse().getItemByItemId(itemId);
						isInInventory = false;
					}
					
					if ((item != null) && (item.getId() == itemId) && (item.getReuseDelay() > 0))
					{
						remainingTime = systime - currentTime;
						if (remainingTime > 10)
						{
							addTimeStampItem(item, reuseDelay, systime);
							
							if (isInInventory && item.isEtcItem())
							{
								final int group = item.getSharedReuseGroup();
								if (group > 0)
								{
									sendPacket(new ExUseSharedGroupItem(itemId, group, (int) remainingTime, (int) reuseDelay));
								}
							}
						}
					}
				}
			}
			
			// Delete item reuse.
			delete.setInt(1, getObjectId());
			delete.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not restore " + this + " Item Reuse data: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Retrieve from the database all Henna of this PlayerInstance, add them to _henna and calculate stats of the PlayerInstance.
	 */
	private void restoreHenna()
	{
		for (int i = 1; i < 5; i++)
		{
			_henna[i - 1] = null;
		}
		
		// Cancel and remove existing running tasks.
		for (Entry<Integer, ScheduledFuture<?>> entry : _hennaRemoveSchedules.entrySet())
		{
			final ScheduledFuture<?> task = entry.getValue();
			if ((task != null) && !task.isCancelled() && !task.isDone())
			{
				task.cancel(true);
			}
			_hennaRemoveSchedules.remove(entry.getKey());
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_HENNAS))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, _classIndex);
			try (ResultSet rset = statement.executeQuery())
			{
				int slot;
				int symbolId;
				final long currentTime = System.currentTimeMillis();
				while (rset.next())
				{
					slot = rset.getInt("slot");
					if ((slot < 1) || (slot > 4))
					{
						continue;
					}
					
					symbolId = rset.getInt("symbol_id");
					if (symbolId == 0)
					{
						continue;
					}
					
					final Henna henna = HennaData.getInstance().getHenna(symbolId);
					
					// Task for henna duration
					if (henna.getDuration() > 0)
					{
						final long remainingTime = getVariables().getLong("HennaDuration" + slot, currentTime) - currentTime;
						if (remainingTime < 0)
						{
							removeHenna(slot);
							continue;
						}
						
						// Add the new task.
						_hennaRemoveSchedules.put(slot, ThreadPool.schedule(new HennaDurationTask(this, slot), currentTime + remainingTime));
					}
					
					_henna[slot - 1] = henna;
					
					// Reward henna skills
					for (Skill skill : henna.getSkills())
					{
						addSkill(skill, false);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Failed restoing character " + this + " hennas.", e);
		}
		
		// Calculate henna modifiers of this player.
		recalcHennaStats();
	}
	
	/**
	 * @return the number of Henna empty slot of the PlayerInstance.
	 */
	public int getHennaEmptySlots()
	{
		int totalSlots = 0;
		if (getClassId().level() == 1)
		{
			totalSlots = 2;
		}
		else if (getClassId().level() > 1)
		{
			totalSlots = 3;
		}
		
		for (int i = 0; i < 3; i++)
		{
			if (_henna[i] != null)
			{
				totalSlots--;
			}
		}
		
		if (totalSlots <= 0)
		{
			return 0;
		}
		
		return totalSlots;
	}
	
	/**
	 * Remove a Henna of the PlayerInstance, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this PlayerInstance.
	 * @param slot
	 * @return
	 */
	public boolean removeHenna(int slot)
	{
		if ((slot < 1) || (slot > 4))
		{
			return false;
		}
		
		final Henna henna = _henna[slot - 1];
		if (henna == null)
		{
			return false;
		}
		
		_henna[slot - 1] = null;
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_CHAR_HENNA))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, slot);
			statement.setInt(3, _classIndex);
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Failed removing character henna.", e);
		}
		
		// Calculate Henna modifiers of this PlayerInstance
		recalcHennaStats();
		
		// Send Server->Client HennaInfo packet to this PlayerInstance
		sendPacket(new HennaInfo(this));
		
		// Send Server->Client UserInfo packet to this PlayerInstance
		broadcastUserInfo(UserInfoType.BASE_STATS, UserInfoType.MAX_HPCPMP, UserInfoType.STATS, UserInfoType.SPEED);
		
		final long currentTime = System.currentTimeMillis();
		final long timeLeft = getVariables().getLong("HennaDuration" + slot, currentTime) - currentTime;
		if ((henna.getDuration() < 0) || (timeLeft > 0))
		{
			// Add the recovered dyes to the player's inventory and notify them.
			if ((henna.getCancelFee() > 0) && (hasPremiumStatus() || (slot != 4)))
			{
				reduceAdena("Henna", henna.getCancelFee(), this, false);
			}
			if (henna.getCancelCount() > 0)
			{
				_inventory.addItem("Henna", henna.getDyeItemId(), henna.getCancelCount(), this, null);
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
				sm.addItemName(henna.getDyeItemId());
				sm.addLong(henna.getCancelCount());
				sendPacket(sm);
			}
		}
		sendPacket(SystemMessageId.THE_SYMBOL_HAS_BEEN_DELETED);
		
		// Remove henna duration task
		if (henna.getDuration() > 0)
		{
			getVariables().remove("HennaDuration" + slot);
			if (_hennaRemoveSchedules.get(slot) != null)
			{
				_hennaRemoveSchedules.get(slot).cancel(false);
				_hennaRemoveSchedules.remove(slot);
			}
		}
		
		// Remove henna skills
		for (Skill skill : henna.getSkills())
		{
			removeSkill(skill, false);
		}
		
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerHennaRemove(this, henna), this);
		return true;
	}
	
	/**
	 * Add a Henna to the PlayerInstance, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this PlayerInstance.
	 * @param henna the henna to add to the player.
	 * @return {@code true} if the henna is added to the player, {@code false} otherwise.
	 */
	public boolean addHenna(Henna henna)
	{
		for (int i = 1; i < 5; i++)
		{
			// Check for retail premium dyes slot
			if (!Config.PREMIUM_HENNA_SLOT_ALL_DYES)
			{
				if (i == 4)
				{
					if ((_henna[3] != null) || !henna.isPremium())
					{
						return false;
					}
				}
				else if (henna.isPremium())
				{
					continue;
				}
			}
			
			if (_henna[i - 1] == null)
			{
				_henna[i - 1] = henna;
				
				// Calculate Henna modifiers of this PlayerInstance
				recalcHennaStats();
				
				try (Connection con = DatabaseFactory.getConnection();
					PreparedStatement statement = con.prepareStatement(ADD_CHAR_HENNA))
				{
					statement.setInt(1, getObjectId());
					statement.setInt(2, henna.getDyeId());
					statement.setInt(3, i);
					statement.setInt(4, _classIndex);
					statement.execute();
				}
				catch (Exception e)
				{
					LOGGER.log(Level.SEVERE, "Failed saving character henna.", e);
				}
				
				// Task for henna duration
				if (henna.getDuration() > 0)
				{
					final long currentTime = System.currentTimeMillis();
					final long durationInMillis = henna.getDuration() * 60000;
					getVariables().set("HennaDuration" + i, currentTime + durationInMillis);
					_hennaRemoveSchedules.put(i, ThreadPool.schedule(new HennaDurationTask(this, i), currentTime + durationInMillis));
				}
				
				// Reward henna skills
				for (Skill skill : henna.getSkills())
				{
					addSkill(skill, false);
				}
				
				// Send Server->Client HennaInfo packet to this PlayerInstance
				sendPacket(new HennaInfo(this));
				
				// Send Server->Client UserInfo packet to this PlayerInstance
				broadcastUserInfo(UserInfoType.BASE_STATS, UserInfoType.MAX_HPCPMP, UserInfoType.STATS, UserInfoType.SPEED);
				
				// Notify to scripts
				EventDispatcher.getInstance().notifyEventAsync(new OnPlayerHennaAdd(this, henna), this);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Calculate Henna modifiers of this PlayerInstance.
	 */
	private void recalcHennaStats()
	{
		_hennaBaseStats.clear();
		for (Henna henna : _henna)
		{
			if (henna == null)
			{
				continue;
			}
			
			for (Entry<BaseStats, Integer> entry : henna.getBaseStats().entrySet())
			{
				_hennaBaseStats.merge(entry.getKey(), entry.getValue(), Integer::sum);
			}
		}
	}
	
	/**
	 * @param slot the character inventory henna slot.
	 * @return the Henna of this PlayerInstance corresponding to the selected slot.
	 */
	public Henna getHenna(int slot)
	{
		if ((slot < 1) || (slot > 4))
		{
			return null;
		}
		return _henna[slot - 1];
	}
	
	/**
	 * @return {@code true} if player has at least 1 henna symbol, {@code false} otherwise.
	 */
	public boolean hasHennas()
	{
		for (Henna henna : _henna)
		{
			if (henna != null)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return the henna holder for this player.
	 */
	public Henna[] getHennaList()
	{
		return _henna;
	}
	
	/**
	 * @param stat
	 * @return the henna bonus of specified base stat
	 */
	public int getHennaValue(BaseStats stat)
	{
		return _hennaBaseStats.getOrDefault(stat, 0);
	}
	
	/**
	 * @return map of all henna base stats bonus
	 */
	public Map<BaseStats, Integer> getHennaBaseStats()
	{
		return _hennaBaseStats;
	}
	
	/**
	 * Checks if the player has basic property resist towards mesmerizing debuffs.
	 * @return {@code true} if the player has resist towards mesmerizing debuffs, {@code false} otherwise
	 */
	@Override
	public boolean hasBasicPropertyResist()
	{
		return isInCategory(CategoryType.SIXTH_CLASS_GROUP);
	}
	
	private void startAutoSaveTask()
	{
		if ((Config.CHAR_DATA_STORE_INTERVAL > 0) && (_autoSaveTask == null))
		{
			_autoSaveTask = ThreadPool.scheduleAtFixedRate(this::autoSave, Config.CHAR_DATA_STORE_INTERVAL, Config.CHAR_DATA_STORE_INTERVAL);
		}
	}
	
	private void stopAutoSaveTask()
	{
		if (_autoSaveTask != null)
		{
			_autoSaveTask.cancel(false);
			_autoSaveTask = null;
		}
	}
	
	protected void autoSave()
	{
		storeMe();
		storeRecommendations();
		
		if (Config.UPDATE_ITEMS_ON_CHAR_STORE)
		{
			_inventory.updateDatabase();
			getWarehouse().updateDatabase();
		}
	}
	
	public boolean canLogout()
	{
		if (hasItemRequest())
		{
			return false;
		}
		
		if (isSubclassLocked())
		{
			LOGGER.warning("Player " + getName() + " tried to restart/logout during class change.");
			return false;
		}
		
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(this) && !(isGM() && Config.GM_RESTART_FIGHTING))
		{
			return false;
		}
		
		if (isBlockedFromExit())
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Return True if the PlayerInstance is autoAttackable.<br>
	 * <B><U>Actions</U>:</B>
	 * <ul>
	 * <li>Check if the attacker isn't the PlayerInstance Pet</li>
	 * <li>Check if the attacker is MonsterInstance</li>
	 * <li>If the attacker is a PlayerInstance, check if it is not in the same party</li>
	 * <li>Check if the PlayerInstance has Karma</li>
	 * <li>If the attacker is a PlayerInstance, check if it is not in the same siege clan (Attacker, Defender)</li>
	 * </ul>
	 */
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		if (attacker == null)
		{
			return false;
		}
		
		// Check if the attacker isn't the PlayerInstance Pet
		if ((attacker == this) || (attacker == _pet) || attacker.hasServitor(attacker.getObjectId()))
		{
			return false;
		}
		
		// Friendly mobs doesnt attack players
		if (attacker instanceof FriendlyMobInstance)
		{
			return false;
		}
		
		// Check if the attacker is a MonsterInstance
		if (attacker.isMonster())
		{
			return true;
		}
		
		// is AutoAttackable if both players are in the same duel and the duel is still going on
		if (attacker.isPlayable() && (_duelState == Duel.DUELSTATE_DUELLING) && (getDuelId() == attacker.getActingPlayer().getDuelId()))
		{
			return true;
		}
		
		// Check if the attacker is not in the same party. NOTE: Party checks goes before oly checks in order to prevent patry member autoattack at oly.
		if (isInParty() && _party.getMembers().contains(attacker))
		{
			return false;
		}
		
		// Check if the attacker is in olympia and olympia start
		if (attacker.isPlayer() && attacker.getActingPlayer().isInOlympiadMode())
		{
			if (_inOlympiadMode && _OlympiadStart && (((PlayerInstance) attacker).getOlympiadGameId() == getOlympiadGameId()))
			{
				return true;
			}
			return false;
		}
		
		if (_isOnCustomEvent && (getTeam() == attacker.getTeam()))
		{
			return false;
		}
		
		// CoC needs this check?
		if (isOnEvent())
		{
			return true;
		}
		
		// Check if the attacker is a Playable
		if (attacker.isPlayable())
		{
			if (isInsideZone(ZoneId.PEACE))
			{
				return false;
			}
			
			// Get PlayerInstance
			final PlayerInstance attackerPlayer = attacker.getActingPlayer();
			final Clan clan = getClan();
			final Clan attackerClan = attackerPlayer.getClan();
			if (clan != null)
			{
				final Siege siege = SiegeManager.getInstance().getSiege(getX(), getY(), getZ());
				if (siege != null)
				{
					// Check if a siege is in progress and if attacker and the PlayerInstance aren't in the Defender clan
					if (siege.checkIsDefender(attackerClan) && siege.checkIsDefender(clan))
					{
						return false;
					}
					
					// Check if a siege is in progress and if attacker and the PlayerInstance aren't in the Attacker clan
					if (siege.checkIsAttacker(attackerClan) && siege.checkIsAttacker(clan))
					{
						return false;
					}
				}
				
				// Check if clan is at war
				if ((attackerClan != null) && (getWantsPeace() == 0) && (attackerPlayer.getWantsPeace() == 0) && !isAcademyMember())
				{
					final ClanWar war = attackerClan.getWarWith(getClanId());
					if ((war != null) && (war.getState() == ClanWarState.MUTUAL))
					{
						return true;
					}
				}
			}
			
			// Check if the PlayerInstance is in an arena, but NOT siege zone. NOTE: This check comes before clan/ally checks, but after party checks.
			// This is done because in arenas, clan/ally members can autoattack if they arent in party.
			if ((isInsideZone(ZoneId.PVP) && attackerPlayer.isInsideZone(ZoneId.PVP)) && !(isInsideZone(ZoneId.SIEGE) && attackerPlayer.isInsideZone(ZoneId.SIEGE)))
			{
				return true;
			}
			
			// Check if the attacker is not in the same clan
			if ((clan != null) && clan.isMember(attacker.getObjectId()))
			{
				return false;
			}
			
			// Check if the attacker is not in the same ally
			if (attacker.isPlayer() && (getAllyId() != 0) && (getAllyId() == attackerPlayer.getAllyId()))
			{
				return false;
			}
			
			// Now check again if the PlayerInstance is in pvp zone, but this time at siege PvP zone, applying clan/ally checks
			if (isInsideZone(ZoneId.PVP) && attackerPlayer.isInsideZone(ZoneId.PVP) && isInsideZone(ZoneId.SIEGE) && attackerPlayer.isInsideZone(ZoneId.SIEGE))
			{
				return true;
			}
			
			if (Config.FACTION_SYSTEM_ENABLED && ((isGood() && attackerPlayer.isEvil()) || (isEvil() && attackerPlayer.isGood())))
			{
				return true;
			}
		}
		
		if (attacker instanceof DefenderInstance)
		{
			if (_clan != null)
			{
				final Siege siege = SiegeManager.getInstance().getSiege(this);
				return ((siege != null) && siege.checkIsAttacker(_clan));
			}
		}
		
		if (attacker instanceof GuardInstance)
		{
			if (Config.FACTION_SYSTEM_ENABLED && Config.FACTION_GUARDS_ENABLED && ((_isGood && ((Npc) attacker).getTemplate().isClan(Config.FACTION_EVIL_TEAM_NAME)) || (_isEvil && ((Npc) attacker).getTemplate().isClan(Config.FACTION_GOOD_TEAM_NAME))))
			{
				return true;
			}
			return (getReputation() < 0); // Guards attack only PK players.
		}
		
		// Check if the PlayerInstance has Karma
		if ((getReputation() < 0) || (_pvpFlag > 0))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check if the active Skill can be casted.<br>
	 * <B><U>Actions</U>:</B>
	 * <ul>
	 * <li>Check if the skill isn't toggle and is offensive</li>
	 * <li>Check if the target is in the skill cast range</li>
	 * <li>Check if the skill is Spoil type and if the target isn't already spoiled</li>
	 * <li>Check if the caster owns enought consummed Item, enough HP and MP to cast the skill</li>
	 * <li>Check if the caster isn't sitting</li>
	 * <li>Check if all skills are enabled and this skill is enabled</li>
	 * <li>Check if the caster own the weapon needed</li>
	 * <li>Check if the skill is active</li>
	 * <li>Check if all casting conditions are completed</li>
	 * <li>Notify the AI with AI_INTENTION_CAST and target</li>
	 * </ul>
	 * @param skill The Skill to use
	 * @param forceUse used to force ATTACK on players
	 * @param dontMove used to prevent movement, if not in range
	 */
	@Override
	public boolean useMagic(Skill skill, ItemInstance item, boolean forceUse, boolean dontMove)
	{
		// Passive skills cannot be used.
		if (skill.isPassive())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// If Alternate rule Karma punishment is set to true, forbid skill Return to player with Karma
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (getReputation() < 0) && skill.hasEffectType(EffectType.TELEPORT))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// players mounted on pets cannot use any toggle skills
		if (skill.isToggle() && isMounted())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Support for wizard skills with stances (Fire, Water, Wind, Earth)
		final Skill attachedSkill = skill.getAttachedSkill(this);
		if (attachedSkill != null)
		{
			skill = attachedSkill;
		}
		
		// Alter skills
		if (_alterSkillActive)
		{
			sendPacket(new ExAlterSkillRequest(null, -1, -1, -1));
			_alterSkillActive = false;
		}
		
		// ************************************* Check Player State *******************************************
		
		// Abnormal effects(ex : Stun, Sleep...) are checked in Creature useMagic()
		if (!skill.canCastWhileDisabled() && (isControlBlocked() || hasBlockActions()))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Check if the player is dead
		if (isDead())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Check if fishing and trying to use non-fishing skills.
		if (isFishing() && !skill.hasEffectType(EffectType.FISHING, EffectType.FISHING_START))
		{
			sendPacket(SystemMessageId.ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME);
			return false;
		}
		
		if (_observerMode)
		{
			sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (isSkillDisabled(skill))
		{
			final SystemMessage sm;
			if (hasSkillReuse(skill.getReuseHashCode()))
			{
				final int remainingTime = (int) (getSkillRemainingReuseTime(skill.getReuseHashCode()) / 1000);
				final int hours = remainingTime / 3600;
				final int minutes = (remainingTime % 3600) / 60;
				final int seconds = (remainingTime % 60);
				if (hours > 0)
				{
					sm = new SystemMessage(SystemMessageId.THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
					sm.addSkillName(skill);
					sm.addInt(hours);
					sm.addInt(minutes);
				}
				else if (minutes > 0)
				{
					sm = new SystemMessage(SystemMessageId.THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
					sm.addSkillName(skill);
					sm.addInt(minutes);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
					sm.addSkillName(skill);
				}
				
				sm.addInt(seconds);
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE);
				sm.addSkillName(skill);
			}
			
			// Don't send packet for Raise/Focus Shield if Final Ultimate Defense is active.
			if (!(getEffectList().getBuffInfoBySkillId(10017) != null) && ((skill.getId() == 10020) || (skill.getId() == 10021)))
			{
				sendPacket(sm);
				return false;
			}
		}
		
		// Check if the caster is sitting
		if (_waitTypeSitting)
		{
			sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SITTING);
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Check if the skill type is toggle and disable it, unless the toggle is necessary to be on.
		if (skill.isToggle())
		{
			if (isAffectedBySkill(skill.getId()))
			{
				if (!skill.isNecessaryToggle())
				{
					stopSkillEffects(true, skill.getId());
				}
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}
			else if (skill.getToggleGroupId() > 0)
			{
				getEffectList().stopAllTogglesOfGroup(skill.getToggleGroupId());
			}
		}
		
		// Check if the player uses "Fake Death" skill
		// Note: do not check this before TOGGLE reset
		if (isFakeDeath())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// ************************************* Check Target *******************************************
		// Create and set a WorldObject containing the target of the skill
		final WorldObject target = skill.getTarget(this, forceUse, dontMove, true);
		final Location worldPosition = _currentSkillWorldPosition;
		
		if ((skill.getTargetType() == TargetType.GROUND) && (worldPosition == null))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Check the validity of the target
		if (target == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		// Check if all casting conditions are completed
		if (!skill.checkCondition(this, target))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			
			// Upon failed conditions, next action is called.
			if ((skill.getNextAction() != NextActionType.NONE) && (target != this) && target.isAutoAttackable(this))
			{
				if ((getAI().getNextIntention() == null) || (getAI().getNextIntention().getCtrlIntention() != CtrlIntention.AI_INTENTION_MOVE_TO))
				{
					if (skill.getNextAction() == NextActionType.ATTACK)
					{
						getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
					}
					else if (skill.getNextAction() == NextActionType.CAST)
					{
						getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target, item, false, false);
					}
				}
			}
			
			return false;
		}
		
		final boolean doubleCast = isAffected(EffectFlag.DOUBLE_CAST) && skill.canDoubleCast();
		
		// If a skill is currently being used, queue this one if this is not the same
		// In case of double casting, check if both slots are occupied, then queue skill.
		if ((!doubleCast && isCastingNow(SkillCaster::isAnyNormalType)) || (isCastingNow(s -> s.getCastingType() == SkillCastingType.NORMAL) && isCastingNow(s -> s.getCastingType() == SkillCastingType.NORMAL_SECOND)))
		{
			// Do not queue skill if called by an item.
			if (item == null)
			{
				// Create a new SkillDat object and queue it in the player _queuedSkill
				setQueuedSkill(skill, item, forceUse, dontMove);
			}
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (_queuedSkill != null)
		{
			setQueuedSkill(null, null, false, false);
		}
		
		// Notify the AI with AI_INTENTION_CAST and target
		getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target, item, forceUse, dontMove);
		return true;
	}
	
	public boolean isInLooterParty(int LooterId)
	{
		final PlayerInstance looter = World.getInstance().getPlayer(LooterId);
		
		// if PlayerInstance is in a CommandChannel
		if (isInParty() && _party.isInCommandChannel() && (looter != null))
		{
			return _party.getCommandChannel().getMembers().contains(looter);
		}
		
		if (isInParty() && (looter != null))
		{
			return _party.getMembers().contains(looter);
		}
		
		return false;
	}
	
	/**
	 * @return True if the PlayerInstance is a Mage.
	 */
	public boolean isMageClass()
	{
		return getClassId().isMage();
	}
	
	public boolean isMounted()
	{
		return _mountType != MountType.NONE;
	}
	
	public boolean checkLandingState()
	{
		// Check if char is in a no landing zone
		if (isInsideZone(ZoneId.NO_LANDING))
		{
			return true;
		}
		else
		// if this is a castle that is currently being sieged, and the rider is NOT a castle owner
		// he cannot land.
		// castle owner is the leader of the clan that owns the castle where the pc is
		if (isInsideZone(ZoneId.SIEGE) && !((getClan() != null) && (CastleManager.getInstance().getCastle(this) == CastleManager.getInstance().getCastleByOwner(getClan())) && (this == getClan().getLeader().getPlayerInstance())))
		{
			return true;
		}
		
		return false;
	}
	
	// returns false if the change of mount type fails.
	public void setMount(int npcId, int npcLevel)
	{
		final MountType type = MountType.findByNpcId(npcId);
		switch (type)
		{
			case NONE: // None
			{
				setIsFlying(false);
				break;
			}
			case STRIDER: // Strider
			{
				if (_nobleLevel > 0)
				{
					addSkill(CommonSkill.STRIDER_SIEGE_ASSAULT.getSkill(), false);
				}
				break;
			}
			case WYVERN: // Wyvern
			{
				setIsFlying(true);
				break;
			}
		}
		
		_mountType = type;
		_mountNpcId = npcId;
		_mountLevel = npcLevel;
	}
	
	/**
	 * @return the type of Pet mounted (0 : none, 1 : Strider, 2 : Wyvern, 3: Wolf).
	 */
	public MountType getMountType()
	{
		return _mountType;
	}
	
	@Override
	public void stopAllEffects()
	{
		super.stopAllEffects();
		updateAndBroadcastStatus(2);
	}
	
	@Override
	public void stopAllEffectsExceptThoseThatLastThroughDeath()
	{
		super.stopAllEffectsExceptThoseThatLastThroughDeath();
		updateAndBroadcastStatus(2);
	}
	
	public void stopCubics()
	{
		if (!_cubics.isEmpty())
		{
			_cubics.values().forEach(CubicInstance::deactivate);
			_cubics.clear();
		}
	}
	
	public void stopCubicsByOthers()
	{
		if (!_cubics.isEmpty())
		{
			boolean broadcast = false;
			for (CubicInstance cubic : _cubics.values())
			{
				if (cubic.isGivenByOther())
				{
					cubic.deactivate();
					_cubics.remove(cubic.getTemplate().getId());
					broadcast = true;
				}
			}
			if (broadcast)
			{
				sendPacket(new ExUserInfoCubic(this));
				broadcastUserInfo();
			}
		}
	}
	
	/**
	 * Send a Server->Client packet UserInfo to this PlayerInstance and CharInfo to all PlayerInstance in its _KnownPlayers.<br>
	 * <B><U>Concept</U>:</B><br>
	 * Others PlayerInstance in the detection area of the PlayerInstance are identified in <B>_knownPlayers</B>.<br>
	 * In order to inform other players of this PlayerInstance state modifications, server just need to go through _knownPlayers to send Server->Client Packet<br>
	 * <B><U>Actions</U>:</B>
	 * <ul>
	 * <li>Send a Server->Client packet UserInfo to this PlayerInstance (Public and Private Data)</li>
	 * <li>Send a Server->Client packet CharInfo to all PlayerInstance in _KnownPlayers of the PlayerInstance (Public data only)</li>
	 * </ul>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : DON'T SEND UserInfo packet to other players instead of CharInfo packet. Indeed, UserInfo packet contains PRIVATE DATA as MaxHP, STR, DEX...</B></FONT>
	 */
	@Override
	public void updateAbnormalVisualEffects()
	{
		sendPacket(new ExUserInfoAbnormalVisualEffect(this));
		broadcastCharInfo();
	}
	
	/**
	 * Disable the Inventory and create a new task to enable it after 1.5s.
	 * @param val
	 */
	public void setInventoryBlockingStatus(boolean val)
	{
		_inventoryDisable = val;
		if (val)
		{
			ThreadPool.schedule(new InventoryEnableTask(this), 1500);
		}
	}
	
	/**
	 * @return True if the Inventory is disabled.
	 */
	public boolean isInventoryDisabled()
	{
		return _inventoryDisable;
	}
	
	/**
	 * Add a cubic to this player.
	 * @param cubic
	 * @return the old cubic for this cubic ID if any, otherwise {@code null}
	 */
	public CubicInstance addCubic(CubicInstance cubic)
	{
		return _cubics.put(cubic.getTemplate().getId(), cubic);
	}
	
	/**
	 * Get the player's cubics.
	 * @return the cubics
	 */
	public Map<Integer, CubicInstance> getCubics()
	{
		return _cubics;
	}
	
	/**
	 * Get the player cubic by cubic ID, if any.
	 * @param cubicId the cubic ID
	 * @return the cubic with the given cubic ID, {@code null} otherwise
	 */
	public CubicInstance getCubicById(int cubicId)
	{
		return _cubics.get(cubicId);
	}
	
	/**
	 * @return the modifier corresponding to the Enchant Effect of the Active Weapon (Min : 127).
	 */
	public int getEnchantEffect()
	{
		final ItemInstance wpn = getActiveWeaponInstance();
		
		if (wpn == null)
		{
			return 0;
		}
		
		return Math.min(127, wpn.getEnchantLevel());
	}
	
	/**
	 * Set the _lastFolkNpc of the PlayerInstance corresponding to the last Folk wich one the player talked.
	 * @param folkNpc
	 */
	public void setLastFolkNPC(Npc folkNpc)
	{
		_lastFolkNpc = folkNpc;
	}
	
	/**
	 * @return the _lastFolkNpc of the PlayerInstance corresponding to the last Folk wich one the player talked.
	 */
	public Npc getLastFolkNPC()
	{
		return _lastFolkNpc;
	}
	
	public void addAutoSoulShot(int itemId)
	{
		_activeSoulShots.add(itemId);
	}
	
	public boolean removeAutoSoulShot(int itemId)
	{
		return _activeSoulShots.remove(itemId);
	}
	
	public Set<Integer> getAutoSoulShot()
	{
		return _activeSoulShots;
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic, boolean fish)
	{
		for (int itemId : _activeSoulShots)
		{
			final ItemInstance item = _inventory.getItemByItemId(itemId);
			if (item == null)
			{
				removeAutoSoulShot(itemId);
				continue;
			}
			
			final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
			if (handler == null)
			{
				continue;
			}
			
			final ActionType defaultAction = item.getItem().getDefaultAction();
			if ((magic && (defaultAction == ActionType.SPIRITSHOT)) || (physical && (defaultAction == ActionType.SOULSHOT)) || (fish && (defaultAction == ActionType.FISHINGSHOT)))
			{
				handler.useItem(this, item, false);
			}
		}
	}
	
	/**
	 * Cancel autoshot for all shots matching crystaltype {@link Item#getCrystalType()}.
	 * @param crystalType int type to disable
	 */
	public void disableAutoShotByCrystalType(int crystalType)
	{
		for (int itemId : _activeSoulShots)
		{
			if (ItemTable.getInstance().getTemplate(itemId).getCrystalType().getLevel() == crystalType)
			{
				disableAutoShot(itemId);
			}
		}
	}
	
	/**
	 * Cancel autoshot use for shot itemId
	 * @param itemId int id to disable
	 * @return true if canceled.
	 */
	public boolean disableAutoShot(int itemId)
	{
		if (_activeSoulShots.contains(itemId))
		{
			removeAutoSoulShot(itemId);
			sendPacket(new ExAutoSoulShot(itemId, false, 0));
			
			final SystemMessage sm = new SystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED);
			sm.addItemName(itemId);
			sendPacket(sm);
			return true;
		}
		return false;
	}
	
	/**
	 * Cancel all autoshots for player
	 */
	public void disableAutoShotsAll()
	{
		for (int itemId : _activeSoulShots)
		{
			sendPacket(new ExAutoSoulShot(itemId, false, 0));
			final SystemMessage sm = new SystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED);
			sm.addItemName(itemId);
			sendPacket(sm);
		}
		_activeSoulShots.clear();
	}
	
	public BroochJewel getActiveRubyJewel()
	{
		return _activeRubyJewel;
	}
	
	public void setActiveRubyJewel(BroochJewel jewel)
	{
		_activeRubyJewel = jewel;
	}
	
	public BroochJewel getActiveShappireJewel()
	{
		return _activeShappireJewel;
	}
	
	public void setActiveShappireJewel(BroochJewel jewel)
	{
		_activeShappireJewel = jewel;
	}
	
	public void updateActiveBroochJewel()
	{
		final BroochJewel[] broochJewels = BroochJewel.values();
		// Update active Ruby jewel.
		setActiveRubyJewel(null);
		for (int i = broochJewels.length - 1; i > 0; i--)
		{
			final BroochJewel jewel = broochJewels[i];
			if (jewel.isRuby() && _inventory.isItemEquipped(jewel.getItemId()))
			{
				setActiveRubyJewel(jewel);
				break;
			}
		}
		// Update active Sapphire jewel.
		setActiveShappireJewel(null);
		for (int i = broochJewels.length - 1; i > 0; i--)
		{
			final BroochJewel jewel = broochJewels[i];
			if (!jewel.isRuby() && _inventory.isItemEquipped(jewel.getItemId()))
			{
				setActiveShappireJewel(jewel);
				break;
			}
		}
	}
	
	private ScheduledFuture<?> _taskWarnUserTakeBreak;
	
	public EnumIntBitmask<ClanPrivilege> getClanPrivileges()
	{
		return _clanPrivileges;
	}
	
	public void setClanPrivileges(EnumIntBitmask<ClanPrivilege> clanPrivileges)
	{
		_clanPrivileges = clanPrivileges.clone();
	}
	
	public boolean hasClanPrivilege(ClanPrivilege privilege)
	{
		return _clanPrivileges.has(privilege);
	}
	
	// baron etc
	public void setPledgeClass(int classId)
	{
		_pledgeClass = classId;
		checkItemRestriction();
	}
	
	public int getPledgeClass()
	{
		return _pledgeClass;
	}
	
	public void setPledgeType(int typeId)
	{
		if (_clan != null)
		{
			_clan.createSubPledge(typeId);
		}
		_pledgeType = typeId;
	}
	
	@Override
	public int getPledgeType()
	{
		return _pledgeType;
	}
	
	public int getApprentice()
	{
		return _apprentice;
	}
	
	public void setApprentice(int apprentice_id)
	{
		_apprentice = apprentice_id;
	}
	
	public int getSponsor()
	{
		return _sponsor;
	}
	
	public void setSponsor(int sponsor_id)
	{
		_sponsor = sponsor_id;
	}
	
	public int getBookMarkSlot()
	{
		return _bookmarkslot;
	}
	
	public void setBookMarkSlot(int slot)
	{
		_bookmarkslot = slot;
		sendPacket(new ExGetBookMarkInfoPacket(this));
	}
	
	@Override
	public void sendMessage(String message)
	{
		if (Config.MULTILANG_ENABLE)
		{
			final String localisation = SendMessageLocalisationData.getInstance().getLocalisation(_lang, message);
			if (localisation != null)
			{
				sendPacket(new SystemMessage(localisation));
				return;
			}
		}
		sendPacket(new SystemMessage(message));
	}
	
	public void setObserving(boolean state)
	{
		_observerMode = state;
		setTarget(null);
		setBlockActions(state);
		setIsInvul(state);
		setInvisible(state);
		if (hasAI() && !state)
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		}
	}
	
	public void enterObserverMode(Location loc)
	{
		setLastLocation();
		
		// Remove Hide.
		getEffectList().stopEffects(AbnormalType.HIDE);
		
		setObserving(true);
		sendPacket(new ObservationMode(loc));
		
		teleToLocation(loc, false);
		
		broadcastUserInfo();
	}
	
	public void setLastLocation()
	{
		_lastLoc = new Location(getX(), getY(), getZ());
	}
	
	public void unsetLastLocation()
	{
		_lastLoc = null;
	}
	
	public void enterOlympiadObserverMode(Location loc, int id)
	{
		if (_pet != null)
		{
			_pet.unSummon(this);
		}
		
		if (hasServitors())
		{
			getServitors().values().forEach(s -> s.unSummon(this));
		}
		
		// Remove Hide.
		getEffectList().stopEffects(AbnormalType.HIDE);
		
		if (!_cubics.isEmpty())
		{
			_cubics.values().forEach(CubicInstance::deactivate);
			_cubics.clear();
			sendPacket(new ExUserInfoCubic(this));
		}
		
		if (_party != null)
		{
			_party.removePartyMember(this, MessageType.EXPELLED);
		}
		
		_olympiadGameId = id;
		if (_waitTypeSitting)
		{
			standUp();
		}
		if (!_observerMode)
		{
			setLastLocation();
		}
		
		_observerMode = true;
		setTarget(null);
		setIsInvul(true);
		setInvisible(true);
		setInstance(OlympiadGameManager.getInstance().getOlympiadTask(id).getStadium().getInstance());
		teleToLocation(loc, false);
		sendPacket(new ExOlympiadMode(3));
		
		broadcastUserInfo();
	}
	
	public void leaveObserverMode()
	{
		setTarget(null);
		setInstance(null);
		teleToLocation(_lastLoc, false);
		unsetLastLocation();
		sendPacket(new ObservationReturn(getLocation()));
		
		setBlockActions(false);
		if (!isGM())
		{
			setInvisible(false);
			setIsInvul(false);
		}
		if (hasAI())
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		}
		
		setFalling(); // prevent receive falling damage
		_observerMode = false;
		
		broadcastUserInfo();
	}
	
	public void leaveOlympiadObserverMode()
	{
		if (_olympiadGameId == -1)
		{
			return;
		}
		_olympiadGameId = -1;
		_observerMode = false;
		setTarget(null);
		sendPacket(new ExOlympiadMode(0));
		setInstance(null);
		teleToLocation(_lastLoc, true);
		if (!isGM())
		{
			setInvisible(false);
			setIsInvul(false);
		}
		if (hasAI())
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		}
		unsetLastLocation();
		broadcastUserInfo();
	}
	
	public void setOlympiadSide(int i)
	{
		_olympiadSide = i;
	}
	
	public int getOlympiadSide()
	{
		return _olympiadSide;
	}
	
	public void setOlympiadGameId(int id)
	{
		_olympiadGameId = id;
	}
	
	public int getOlympiadGameId()
	{
		return _olympiadGameId;
	}
	
	public Location getLastLocation()
	{
		return _lastLoc;
	}
	
	public boolean inObserverMode()
	{
		return _observerMode;
	}
	
	public AdminTeleportType getTeleMode()
	{
		return _teleportType;
	}
	
	public void setTeleMode(AdminTeleportType type)
	{
		_teleportType = type;
	}
	
	public void setRace(int i, int val)
	{
		_race[i] = val;
	}
	
	public int getRace(int i)
	{
		return _race[i];
	}
	
	public boolean getMessageRefusal()
	{
		return _messageRefusal;
	}
	
	public void setMessageRefusal(boolean mode)
	{
		_messageRefusal = mode;
		sendPacket(new EtcStatusUpdate(this));
	}
	
	public void setDietMode(boolean mode)
	{
		_dietMode = mode;
	}
	
	public boolean getDietMode()
	{
		return _dietMode;
	}
	
	public void setTradeRefusal(boolean mode)
	{
		_tradeRefusal = mode;
	}
	
	public boolean getTradeRefusal()
	{
		return _tradeRefusal;
	}
	
	public void setExchangeRefusal(boolean mode)
	{
		_exchangeRefusal = mode;
	}
	
	public boolean getExchangeRefusal()
	{
		return _exchangeRefusal;
	}
	
	public BlockList getBlockList()
	{
		return _blockList;
	}
	
	/**
	 * @param player
	 * @return returns {@code true} if player is current player cannot accepting messages from the target player, {@code false} otherwise
	 */
	public boolean isBlocking(PlayerInstance player)
	{
		return _blockList.isBlockAll() || _blockList.isInBlockList(player);
	}
	
	/**
	 * @param player
	 * @return returns {@code true} if player is current player can accepting messages from the target player, {@code false} otherwise
	 */
	public boolean isNotBlocking(PlayerInstance player)
	{
		return !_blockList.isBlockAll() && !_blockList.isInBlockList(player);
	}
	
	/**
	 * @param player
	 * @return returns {@code true} if player is target player cannot accepting messages from the current player, {@code false} otherwise
	 */
	public boolean isBlocked(PlayerInstance player)
	{
		return player.getBlockList().isBlockAll() || player.getBlockList().isInBlockList(this);
	}
	
	/**
	 * @param player
	 * @return returns {@code true} if player is target player can accepting messages from the current player, {@code false} otherwise
	 */
	public boolean isNotBlocked(PlayerInstance player)
	{
		return !player.getBlockList().isBlockAll() && !player.getBlockList().isInBlockList(this);
	}
	
	public void setHero(boolean hero)
	{
		if (hero && (_baseClass == _activeClass))
		{
			for (Skill skill : SkillTreesData.getInstance().getHeroSkillTree())
			{
				addSkill(skill, false); // Don't persist hero skills into database
			}
		}
		else
		{
			for (Skill skill : SkillTreesData.getInstance().getHeroSkillTree())
			{
				removeSkill(skill, false, true); // Just remove skills from non-hero players
			}
		}
		_hero = hero;
		
		sendSkillList();
	}
	
	public void setIsInOlympiadMode(boolean b)
	{
		_inOlympiadMode = b;
	}
	
	public void setIsOlympiadStart(boolean b)
	{
		_OlympiadStart = b;
	}
	
	public boolean isOlympiadStart()
	{
		return _OlympiadStart;
	}
	
	public boolean isHero()
	{
		return _hero;
	}
	
	public boolean isInOlympiadMode()
	{
		return _inOlympiadMode;
	}
	
	public boolean isInDuel()
	{
		return _isInDuel;
	}
	
	public void setStartingDuel()
	{
		_startingDuel = true;
	}
	
	public int getDuelId()
	{
		return _duelId;
	}
	
	public void setDuelState(int mode)
	{
		_duelState = mode;
	}
	
	public int getDuelState()
	{
		return _duelState;
	}
	
	/**
	 * Sets up the duel state using a non 0 duelId.
	 * @param duelId 0=not in a duel
	 */
	public void setIsInDuel(int duelId)
	{
		if (duelId > 0)
		{
			_isInDuel = true;
			_duelState = Duel.DUELSTATE_DUELLING;
			_duelId = duelId;
		}
		else
		{
			if (_duelState == Duel.DUELSTATE_DEAD)
			{
				enableAllSkills();
				getStatus().startHpMpRegeneration();
			}
			_isInDuel = false;
			_duelState = Duel.DUELSTATE_NODUEL;
			_duelId = 0;
		}
		_startingDuel = false;
	}
	
	/**
	 * This returns a SystemMessage stating why the player is not available for duelling.
	 * @return S1_CANNOT_DUEL... message
	 */
	public SystemMessage getNoDuelReason()
	{
		final SystemMessage sm = new SystemMessage(_noDuelReason);
		sm.addPcName(this);
		_noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
		return sm;
	}
	
	/**
	 * Checks if this player might join / start a duel.<br>
	 * To get the reason use getNoDuelReason() after calling this function.
	 * @return true if the player might join/start a duel.
	 */
	public boolean canDuel()
	{
		if (isInCombat() || isJailed())
		{
			_noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
			return false;
		}
		if (isDead() || isAlikeDead() || ((getCurrentHp() < (getMaxHp() / 2)) || (getCurrentMp() < (getMaxMp() / 2))))
		{
			_noDuelReason = SystemMessageId.CANNOT_DUEL_BECAUSE_C1_S_HP_OR_MP_IS_BELOW_50;
			return false;
		}
		if (_isInDuel || _startingDuel)
		{
			_noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL;
			return false;
		}
		if (_inOlympiadMode || isOnEvent(CeremonyOfChaosEvent.class))
		{
			_noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_OR_THE_CEREMONY_OF_CHAOS;
			return false;
		}
		if (isOnEvent()) // custom event message
		{
			_noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
			return false;
		}
		if (isCursedWeaponEquipped())
		{
			_noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_IN_A_CHAOTIC_OR_PURPLE_STATE;
			return false;
		}
		if (_privateStoreType != PrivateStoreType.NONE)
		{
			_noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE;
			return false;
		}
		if (isMounted() || isInBoat())
		{
			_noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_FENRIR_OR_STRIDER;
			return false;
		}
		if (isFishing())
		{
			_noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING;
			return false;
		}
		if (isInsideZone(ZoneId.PVP) || isInsideZone(ZoneId.PEACE) || isInsideZone(ZoneId.SIEGE))
		{
			_noDuelReason = SystemMessageId.C1_IS_IN_AN_AREA_WHERE_DUEL_IS_NOT_ALLOWED_AND_YOU_CANNOT_APPLY_FOR_A_DUEL;
			return false;
		}
		return true;
	}
	
	public int getNobleLevel()
	{
		return _nobleLevel;
	}
	
	public void setNobleLevel(int level)
	{
		if (level != 0)
		{
			SkillTreesData.getInstance().getNobleSkillAutoGetTree().forEach(skill -> addSkill(skill, false));
		}
		else
		{
			SkillTreesData.getInstance().getNobleSkillTree().forEach(skill -> removeSkill(skill, false, true));
		}
		_nobleLevel = level;
		sendSkillList();
	}
	
	public void setLvlJoinedAcademy(int lvl)
	{
		_lvlJoinedAcademy = lvl;
	}
	
	public int getLvlJoinedAcademy()
	{
		return _lvlJoinedAcademy;
	}
	
	@Override
	public boolean isAcademyMember()
	{
		return _lvlJoinedAcademy > 0;
	}
	
	@Override
	public void setTeam(Team team)
	{
		super.setTeam(team);
		broadcastUserInfo();
		if (_pet != null)
		{
			_pet.broadcastStatusUpdate();
		}
		if (hasServitors())
		{
			getServitors().values().forEach(Summon::broadcastStatusUpdate);
		}
	}
	
	public void setWantsPeace(int wantsPeace)
	{
		_wantsPeace = wantsPeace;
	}
	
	public int getWantsPeace()
	{
		return _wantsPeace;
	}
	
	public void sendSkillList()
	{
		sendSkillList(0);
	}
	
	public void sendSkillList(int lastLearnedSkillId)
	{
		boolean isDisabled = false;
		final SkillList sl = new SkillList();
		
		for (Skill s : getSkillList())
		{
			if (_clan != null)
			{
				isDisabled = s.isClanSkill() && (_clan.getReputationScore() < 0);
			}
			
			sl.addSkill(s.getDisplayId(), s.getReuseDelayGroup(), s.getDisplayLevel(), s.getSubLevel(), s.isPassive(), isDisabled, s.isEnchantable());
		}
		if (lastLearnedSkillId > 0)
		{
			sl.setLastLearnedSkillId(lastLearnedSkillId);
		}
		sendPacket(sl);
		
		sendPacket(new AcquireSkillList(this));
	}
	
	/**
	 * 1. Add the specified class ID as a subclass (up to the maximum number of <b>three</b>) for this character.<BR>
	 * 2. This method no longer changes the active _classIndex of the player. This is only done by the calling of setActiveClass() method as that should be the only way to do so.
	 * @param classId
	 * @param classIndex
	 * @param isDualClass
	 * @return boolean subclassAdded
	 */
	public boolean addSubClass(int classId, int classIndex, boolean isDualClass)
	{
		if (!_subclassLock.tryLock())
		{
			return false;
		}
		
		try
		{
			if ((getTotalSubClasses() == Config.MAX_SUBCLASS) || (classIndex == 0))
			{
				return false;
			}
			
			if (getSubClasses().containsKey(classIndex))
			{
				return false;
			}
			
			// Note: Never change _classIndex in any method other than setActiveClass().
			
			final SubClass newClass = new SubClass();
			newClass.setClassId(classId);
			newClass.setClassIndex(classIndex);
			newClass.setVitalityPoints(PlayerStat.MAX_VITALITY_POINTS);
			if (isDualClass)
			{
				newClass.setIsDualClass(true);
				newClass.setExp(ExperienceData.getInstance().getExpForLevel(Config.BASE_DUALCLASS_LEVEL));
				newClass.setLevel(Config.BASE_DUALCLASS_LEVEL);
			}
			
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement(ADD_CHAR_SUBCLASS))
			{
				// Store the basic info about this new sub-class.
				statement.setInt(1, getObjectId());
				statement.setInt(2, newClass.getClassId());
				statement.setLong(3, newClass.getExp());
				statement.setLong(4, newClass.getSp());
				statement.setInt(5, newClass.getLevel());
				statement.setInt(6, newClass.getVitalityPoints());
				statement.setInt(7, newClass.getClassIndex());
				statement.setBoolean(8, newClass.isDualClass());
				statement.execute();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "WARNING: Could not add character sub class for " + getName() + ": " + e.getMessage(), e);
				return false;
			}
			
			// Commit after database INSERT in case exception is thrown.
			getSubClasses().put(newClass.getClassIndex(), newClass);
			
			final ClassId subTemplate = ClassId.getClassId(classId);
			final Map<Long, SkillLearn> skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(subTemplate);
			final Map<Integer, Skill> prevSkillList = new HashMap<>();
			for (SkillLearn skillInfo : skillTree.values())
			{
				if (!Config.AUTO_LEARN_FP_SKILLS && (skillInfo.getSkillId() > 11399) && (skillInfo.getSkillId() < 11405))
				{
					continue;
				}
				
				if (skillInfo.getGetLevel() <= newClass.getLevel())
				{
					final Skill prevSkill = prevSkillList.get(skillInfo.getSkillId());
					final Skill newSkill = SkillData.getInstance().getSkill(skillInfo.getSkillId(), skillInfo.getSkillLevel());
					
					if (((prevSkill != null) && (prevSkill.getLevel() > newSkill.getLevel())) || SkillTreesData.getInstance().isRemoveSkill(subTemplate, skillInfo.getSkillId()))
					{
						continue;
					}
					
					prevSkillList.put(newSkill.getId(), newSkill);
					storeSkill(newSkill, prevSkill, classIndex);
				}
			}
			return true;
		}
		finally
		{
			_subclassLock.unlock();
		}
	}
	
	/**
	 * 1. Completely erase all existance of the subClass linked to the classIndex.<br>
	 * 2. Send over the newClassId to addSubClass() to create a new instance on this classIndex.<br>
	 * 3. Upon Exception, revert the player to their BaseClass to avoid further problems.
	 * @param classIndex the class index to delete
	 * @param newClassId the new class Id
	 * @param isDualClass is subclass dualclass
	 * @return {@code true} if the sub-class was modified, {@code false} otherwise
	 */
	public boolean modifySubClass(int classIndex, int newClassId, boolean isDualClass)
	{
		if (!_subclassLock.tryLock())
		{
			return false;
		}
		
		try
		{
			// Notify to scripts before class is removed.
			if (!getSubClasses().isEmpty()) // also null check
			{
				final int classId = getSubClasses().get(classIndex).getClassId();
				EventDispatcher.getInstance().notifyEventAsync(new OnPlayerProfessionCancel(this, classId), this);
			}
			
			final SubClass subClass = getSubClasses().get(classIndex);
			if (subClass == null)
			{
				return false;
			}
			
			if (subClass.isDualClass())
			{
				getVariables().remove(PlayerVariables.ABILITY_POINTS_DUAL_CLASS);
				getVariables().remove(PlayerVariables.ABILITY_POINTS_USED_DUAL_CLASS);
				int revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_1_DUAL_CLASS, 0);
				if (revelationSkill != 0)
				{
					removeSkill(revelationSkill);
				}
				revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_2_DUAL_CLASS, 0);
				if (revelationSkill != 0)
				{
					removeSkill(revelationSkill);
				}
			}
			
			// Remove after stats are recalculated.
			getSubClasses().remove(classIndex);
			
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement deleteHennas = con.prepareStatement(DELETE_CHAR_HENNAS);
				PreparedStatement deleteShortcuts = con.prepareStatement(DELETE_CHAR_SHORTCUTS);
				PreparedStatement deleteSkillReuse = con.prepareStatement(DELETE_SKILL_SAVE);
				PreparedStatement deleteSkills = con.prepareStatement(DELETE_CHAR_SKILLS);
				PreparedStatement deleteSubclass = con.prepareStatement(DELETE_CHAR_SUBCLASS))
			{
				// Remove all henna info stored for this sub-class.
				deleteHennas.setInt(1, getObjectId());
				deleteHennas.setInt(2, classIndex);
				deleteHennas.execute();
				
				// Remove all shortcuts info stored for this sub-class.
				deleteShortcuts.setInt(1, getObjectId());
				deleteShortcuts.setInt(2, classIndex);
				deleteShortcuts.execute();
				
				// Remove all effects info stored for this sub-class.
				deleteSkillReuse.setInt(1, getObjectId());
				deleteSkillReuse.setInt(2, classIndex);
				deleteSkillReuse.execute();
				
				// Remove all skill info stored for this sub-class.
				deleteSkills.setInt(1, getObjectId());
				deleteSkills.setInt(2, classIndex);
				deleteSkills.execute();
				
				// Remove all basic info stored about this sub-class.
				deleteSubclass.setInt(1, getObjectId());
				deleteSubclass.setInt(2, classIndex);
				deleteSubclass.execute();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "Could not modify sub class for " + getName() + " to class index " + classIndex + ": " + e.getMessage(), e);
				return false;
			}
		}
		finally
		{
			_subclassLock.unlock();
		}
		
		return addSubClass(newClassId, classIndex, isDualClass);
	}
	
	public boolean isSubClassActive()
	{
		return _classIndex > 0;
	}
	
	public boolean isAwakenedClass()
	{
		return isInCategory(CategoryType.SIXTH_CLASS_GROUP);
	}
	
	public void setDualClass(int classIndex)
	{
		if (isSubClassActive())
		{
			getSubClasses().get(_classIndex).setIsDualClass(true);
		}
	}
	
	public boolean isDualClassActive()
	{
		if (!isSubClassActive())
		{
			return false;
		}
		if (_subClasses.isEmpty())
		{
			return false;
		}
		final SubClass subClass = _subClasses.get(_classIndex);
		if (subClass == null)
		{
			return false;
		}
		return subClass.isDualClass();
	}
	
	public boolean hasDualClass()
	{
		return getSubClasses().values().stream().anyMatch(SubClass::isDualClass);
	}
	
	public SubClass getDualClass()
	{
		return getSubClasses().values().stream().filter(SubClass::isDualClass).findFirst().orElse(null);
	}
	
	public Map<Integer, SubClass> getSubClasses()
	{
		return _subClasses;
	}
	
	public int getTotalSubClasses()
	{
		return getSubClasses().size();
	}
	
	public int getBaseClass()
	{
		return _baseClass;
	}
	
	public int getActiveClass()
	{
		return _activeClass;
	}
	
	public int getClassIndex()
	{
		return _classIndex;
	}
	
	protected void setClassIndex(int classIndex)
	{
		_classIndex = classIndex;
	}
	
	private void setClassTemplate(int classId)
	{
		_activeClass = classId;
		
		final PlayerTemplate pcTemplate = PlayerTemplateData.getInstance().getTemplate(classId);
		if (pcTemplate == null)
		{
			LOGGER.severe("Missing template for classId: " + classId);
			throw new Error();
		}
		// Set the template of the PlayerInstance
		setTemplate(pcTemplate);
		
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerProfessionChange(this, pcTemplate, isSubClassActive()), this);
	}
	
	/**
	 * Changes the character's class based on the given class index.<br>
	 * An index of zero specifies the character's original (base) class, while indexes 1-3 specifies the character's sub-classes respectively.<br>
	 * <font color="00FF00"/>WARNING: Use only on subclase change</font>
	 * @param classIndex
	 */
	public void setActiveClass(int classIndex)
	{
		if (!_subclassLock.tryLock())
		{
			return;
		}
		
		try
		{
			// Cannot switch or change subclasses while transformed
			if (isTransformed())
			{
				return;
			}
			
			// Remove active item skills before saving char to database
			// because next time when choosing this class, weared items can
			// be different
			for (ItemInstance item : _inventory.getPaperdollItems(ItemInstance::isAugmented))
			{
				if ((item != null) && item.isEquipped())
				{
					item.getAugmentation().removeBonus(this);
				}
			}
			
			// abort any kind of cast.
			abortCast();
			
			if (isChannelized())
			{
				getSkillChannelized().abortChannelization();
			}
			
			// 1. Call store() before modifying _classIndex to avoid skill effects rollover.
			// 2. Register the correct _classId against applied 'classIndex'.
			store(Config.SUBCLASS_STORE_SKILL_COOLTIME);
			
			if (_sellingBuffs != null)
			{
				_sellingBuffs.clear();
			}
			
			resetTimeStamps();
			
			// clear charges
			_charges.set(0);
			stopChargeTask();
			
			if (hasServitors())
			{
				getServitors().values().forEach(s -> s.unSummon(this));
			}
			
			if (classIndex == 0)
			{
				setClassTemplate(_baseClass);
			}
			else
			{
				try
				{
					setClassTemplate(getSubClasses().get(classIndex).getClassId());
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "Could not switch " + getName() + "'s sub class to class index " + classIndex + ": " + e.getMessage(), e);
					return;
				}
			}
			_classIndex = classIndex;
			
			if (isInParty())
			{
				_party.recalculatePartyLevel();
			}
			
			// Update the character's change in class status.
			// 1. Remove any active cubics from the player.
			// 2. Renovate the characters table in the database with the new class info, storing also buff/effect data.
			// 3. Remove all existing skills.
			// 4. Restore all the learned skills for the current class from the database.
			// 5. Restore effect/buff data for the new class.
			// 6. Restore henna data for the class, applying the new stat modifiers while removing existing ones.
			// 7. Reset HP/MP/CP stats and send Server->Client character status packet to reflect changes.
			// 8. Restore shortcut data related to this class.
			// 9. Resend a class change animation effect to broadcast to all nearby players.
			for (Skill oldSkill : getAllSkills())
			{
				removeSkill(oldSkill, false, true);
			}
			
			// stopAllEffectsExceptThoseThatLastThroughDeath();
			getEffectList().stopEffects(info -> !info.getSkill().isStayAfterDeath(), true, false);
			
			// stopAllEffects();
			getEffectList().stopEffects(info -> !info.getSkill().isNecessaryToggle() && !info.getSkill().isIrreplacableBuff(), true, false);
			
			// Update abnormal visual effects.
			sendPacket(new ExUserInfoAbnormalVisualEffect(this));
			
			stopCubics();
			
			restoreRecipeBook(false);
			
			restoreSkills();
			rewardSkills();
			regiveTemporarySkills();
			
			// Prevents some issues when changing between subclases that shares skills
			resetDisabledSkills();
			
			restoreEffects();
			
			sendPacket(new EtcStatusUpdate(this));
			
			for (int i = 0; i < 4; i++)
			{
				_henna[i] = null;
			}
			
			restoreHenna();
			sendPacket(new HennaInfo(this));
			
			if (getCurrentHp() > getMaxHp())
			{
				setCurrentHp(getMaxHp());
			}
			if (getCurrentMp() > getMaxMp())
			{
				setCurrentMp(getMaxMp());
			}
			if (getCurrentCp() > getMaxCp())
			{
				setCurrentCp(getMaxCp());
			}
			
			refreshOverloaded(true);
			refreshExpertisePenalty();
			broadcastUserInfo();
			
			// Clear resurrect xp calculation
			setExpBeforeDeath(0);
			
			_shortCuts.restoreMe();
			sendPacket(new ShortCutInit(this));
			
			broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));
			sendPacket(new SkillCoolTime(this));
			sendPacket(new ExStorageMaxCount(this));
			
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSubChange(this), this);
		}
		finally
		{
			_subclassLock.unlock();
		}
	}
	
	public boolean isSubclassLocked()
	{
		return _subclassLock.isLocked();
	}
	
	public void stopWarnUserTakeBreak()
	{
		if (_taskWarnUserTakeBreak != null)
		{
			_taskWarnUserTakeBreak.cancel(true);
			_taskWarnUserTakeBreak = null;
		}
	}
	
	public void startWarnUserTakeBreak()
	{
		if (_taskWarnUserTakeBreak == null)
		{
			_taskWarnUserTakeBreak = ThreadPool.scheduleAtFixedRate(new WarnUserTakeBreakTask(this), 3600000, 3600000);
		}
	}
	
	public void stopRentPet()
	{
		if (_taskRentPet != null)
		{
			// if the rent of a wyvern expires while over a flying zone, tp to down before unmounting
			if (checkLandingState() && (_mountType == MountType.WYVERN))
			{
				teleToLocation(TeleportWhereType.TOWN);
			}
			
			if (dismount()) // this should always be true now, since we teleported already
			{
				_taskRentPet.cancel(true);
				_taskRentPet = null;
			}
		}
	}
	
	public void startRentPet(int seconds)
	{
		if (_taskRentPet == null)
		{
			_taskRentPet = ThreadPool.scheduleAtFixedRate(new RentPetTask(this), seconds * 1000, seconds * 1000);
		}
	}
	
	public boolean isRentedPet()
	{
		if (_taskRentPet != null)
		{
			return true;
		}
		
		return false;
	}
	
	public void stopWaterTask()
	{
		if (_taskWater != null)
		{
			_taskWater.cancel(false);
			_taskWater = null;
			sendPacket(new SetupGauge(getObjectId(), 2, 0));
		}
	}
	
	public void startWaterTask()
	{
		if (!isDead() && (_taskWater == null))
		{
			final int timeinwater = (int) getStat().getValue(Stats.BREATH, 60000);
			
			sendPacket(new SetupGauge(getObjectId(), 2, timeinwater));
			_taskWater = ThreadPool.scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000);
		}
	}
	
	public boolean isInWater()
	{
		if (_taskWater != null)
		{
			return true;
		}
		
		return false;
	}
	
	public void checkWaterState()
	{
		if (isInsideZone(ZoneId.WATER))
		{
			startWaterTask();
		}
		else
		{
			stopWaterTask();
		}
	}
	
	public void onPlayerEnter()
	{
		startWarnUserTakeBreak();
		
		if (isGM() && !Config.GM_STARTUP_BUILDER_HIDE)
		{
			// Bleah, see L2J custom below.
			if (isInvul())
			{
				sendMessage("Entering world in Invulnerable mode.");
			}
			if (isInvisible())
			{
				sendMessage("Entering world in Invisible mode.");
			}
			if (_silenceMode)
			{
				sendMessage("Entering world in Silence mode.");
			}
		}
		
		// Buff and status icons
		if (Config.STORE_SKILL_COOLTIME)
		{
			restoreEffects();
		}
		
		// TODO : Need to fix that hack!
		if (!isDead())
		{
			setCurrentCp(_originalCp);
			setCurrentHp(_originalHp);
			setCurrentMp(_originalMp);
		}
		
		revalidateZone(true);
		
		notifyFriends(FriendStatus.MODE_ONLINE);
		if (!canOverrideCond(PlayerCondOverride.SKILL_CONDITIONS) && Config.DECREASE_SKILL_LEVEL)
		{
			checkPlayerSkills();
		}
		
		try
		{
			final SayuneRequest sayune = getRequest(SayuneRequest.class);
			if (sayune != null)
			{
				sayune.onLogout();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		try
		{
			for (ZoneType zone : ZoneManager.getInstance().getZones(this))
			{
				zone.onPlayerLoginInside(this);
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "", e);
		}
		
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogin(this), this);
		
		if (isMentee())
		{
			// Notify to scripts
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeStatus(this, true), this);
		}
		else if (isMentor())
		{
			// Notify to scripts
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMentorStatus(this, true), this);
		}
	}
	
	public long getLastAccess()
	{
		return _lastAccess;
	}
	
	protected void setLastAccess(long lastAccess)
	{
		_lastAccess = lastAccess;
	}
	
	@Override
	public void doRevive()
	{
		super.doRevive();
		sendPacket(new EtcStatusUpdate(this));
		_revivePet = false;
		_reviveRequested = 0;
		_revivePower = 0;
		
		if (isMounted())
		{
			startFeed(_mountNpcId);
		}
		
		// Notify instance
		final Instance instance = getInstanceWorld();
		if (instance != null)
		{
			instance.doRevive(this);
		}
	}
	
	@Override
	public void doRevive(double revivePower)
	{
		doRevive();
		restoreExp(revivePower);
	}
	
	public void reviveRequest(PlayerInstance reviver, Skill skill, boolean isPet, int power)
	{
		if (isResurrectionBlocked())
		{
			return;
		}
		
		if (_reviveRequested == 1)
		{
			if (_revivePet == isPet)
			{
				reviver.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
			}
			else if (isPet)
			{
				reviver.sendPacket(SystemMessageId.A_PET_CANNOT_BE_RESURRECTED_WHILE_IT_S_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING); // A pet cannot be resurrected while it's owner is in the process of resurrecting.
			}
			else
			{
				reviver.sendPacket(SystemMessageId.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER); // While a pet is attempting to resurrect, it cannot help in resurrecting its master.
			}
			return;
		}
		if ((isPet && (_pet != null) && _pet.isDead()) || (!isPet && isDead()))
		{
			_reviveRequested = 1;
			_revivePower = Formulas.calculateSkillResurrectRestorePercent(power, reviver);
			_revivePet = isPet;
			
			if (hasCharmOfCourage())
			{
				final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW.getId());
				dlg.addTime(60000);
				sendPacket(dlg);
				return;
			}
			
			final long restoreExp = Math.round(((_expBeforeDeath - getExp()) * _revivePower) / 100);
			final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.C1_IS_ATTEMPTING_TO_RESURRECT_YOU_AND_RESTORE_XP_S2_S3_ACCEPT.getId());
			dlg.getSystemMessage().addPcName(reviver);
			dlg.getSystemMessage().addLong(restoreExp);
			dlg.getSystemMessage().addInt(power);
			sendPacket(dlg);
		}
	}
	
	public void reviveAnswer(int answer)
	{
		if ((_reviveRequested != 1) || (!isDead() && !_revivePet) || (_revivePet && (_pet != null) && !_pet.isDead()))
		{
			return;
		}
		
		if (answer == 1)
		{
			if (!_revivePet)
			{
				if (_revivePower != 0)
				{
					doRevive(_revivePower);
				}
				else
				{
					doRevive();
				}
			}
			else if (_pet != null)
			{
				if (_revivePower != 0)
				{
					_pet.doRevive(_revivePower);
				}
				else
				{
					_pet.doRevive();
				}
			}
		}
		_reviveRequested = 0;
		_revivePower = 0;
	}
	
	public boolean isReviveRequested()
	{
		return (_reviveRequested == 1);
	}
	
	public boolean isRevivingPet()
	{
		return _revivePet;
	}
	
	public void removeReviving()
	{
		_reviveRequested = 0;
		_revivePower = 0;
	}
	
	public void onActionRequest()
	{
		if (isSpawnProtected())
		{
			setSpawnProtection(false);
			if (!isInsideZone(ZoneId.PEACE))
			{
				sendPacket(SystemMessageId.YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS);
			}
			if (Config.RESTORE_SERVITOR_ON_RECONNECT && !hasSummon() && CharSummonTable.getInstance().getServitors().containsKey(getObjectId()))
			{
				CharSummonTable.getInstance().restoreServitor(this);
			}
			if (Config.RESTORE_PET_ON_RECONNECT && !hasSummon() && CharSummonTable.getInstance().getPets().containsKey(getObjectId()))
			{
				CharSummonTable.getInstance().restorePet(this);
			}
		}
		if (isTeleportProtected())
		{
			setTeleportProtection(false);
			if (!isInsideZone(ZoneId.PEACE))
			{
				sendMessage("Teleport spawn protection ended.");
			}
		}
	}
	
	/**
	 * Expertise of the PlayerInstance (None=0, D=1, C=2, B=3, A=4, S=5, S80=6, S84=7, R=8, R95=9, R99=10)
	 * @return CrystalTyperepresenting expertise level..
	 */
	public CrystalType getExpertiseLevel()
	{
		return _expertiseLevel;
	}
	
	public void setExpertiseLevel(CrystalType crystalType)
	{
		_expertiseLevel = crystalType != null ? crystalType : CrystalType.NONE;
	}
	
	@Override
	public void teleToLocation(ILocational loc, boolean allowRandomOffset)
	{
		if ((_vehicle != null) && !_vehicle.isTeleporting())
		{
			setVehicle(null);
		}
		
		if (isFlyingMounted() && (loc.getZ() < -1005))
		{
			super.teleToLocation(loc.getX(), loc.getY(), -1005, loc.getHeading());
		}
		super.teleToLocation(loc, allowRandomOffset);
	}
	
	@Override
	public void onTeleported()
	{
		super.onTeleported();
		
		if (isInAirShip())
		{
			getAirShip().sendInfo(this);
		}
		else // Update last player position upon teleport.
		{
			setLastServerPosition(getX(), getY(), getZ());
		}
		
		// Force a revalidation
		revalidateZone(true);
		
		checkItemRestriction();
		
		if ((Config.PLAYER_TELEPORT_PROTECTION > 0) && !_inOlympiadMode)
		{
			setTeleportProtection(true);
		}
		
		// Trained beast is lost after teleport
		for (TamedBeastInstance tamedBeast : _tamedBeast)
		{
			tamedBeast.deleteMe();
		}
		_tamedBeast.clear();
		
		// Modify the position of the pet if necessary
		if (_pet != null)
		{
			_pet.setFollowStatus(false);
			_pet.teleToLocation(getLocation(), false);
			((SummonAI) _pet.getAI()).setStartFollowController(true);
			_pet.setFollowStatus(true);
			_pet.setInstance(getInstanceWorld());
			_pet.updateAndBroadcastStatus(0);
		}
		
		getServitors().values().forEach(s ->
		{
			s.setFollowStatus(false);
			s.teleToLocation(getLocation(), false);
			((SummonAI) s.getAI()).setStartFollowController(true);
			s.setFollowStatus(true);
			s.setInstance(getInstanceWorld());
			s.updateAndBroadcastStatus(0);
		});
		
		// show movie if available
		if (_movieHolder != null)
		{
			sendPacket(new ExStartScenePlayer(_movieHolder.getMovie()));
		}
		
		// send info to nearby players
		broadcastInfo();
	}
	
	@Override
	public void setIsTeleporting(boolean teleport)
	{
		setIsTeleporting(teleport, true);
	}
	
	public void setIsTeleporting(boolean teleport, boolean useWatchDog)
	{
		super.setIsTeleporting(teleport);
		if (!useWatchDog)
		{
			return;
		}
		if (teleport)
		{
			if ((_teleportWatchdog == null) && (Config.TELEPORT_WATCHDOG_TIMEOUT > 0))
			{
				synchronized (this)
				{
					if (_teleportWatchdog == null)
					{
						_teleportWatchdog = ThreadPool.schedule(new TeleportWatchdogTask(this), Config.TELEPORT_WATCHDOG_TIMEOUT * 1000);
					}
				}
			}
		}
		else if (_teleportWatchdog != null)
		{
			_teleportWatchdog.cancel(false);
			_teleportWatchdog = null;
		}
	}
	
	public void setLastServerPosition(int x, int y, int z)
	{
		_lastServerPosition.setXYZ(x, y, z);
	}
	
	public Location getLastServerPosition()
	{
		return _lastServerPosition;
	}
	
	@Override
	public void addExpAndSp(double addToExp, double addToSp)
	{
		getStat().addExpAndSp(addToExp, addToSp, false);
	}
	
	public void addExpAndSp(double addToExp, double addToSp, boolean useVitality)
	{
		getStat().addExpAndSp(addToExp, addToSp, useVitality);
	}
	
	public void removeExpAndSp(long removeExp, long removeSp)
	{
		getStat().removeExpAndSp(removeExp, removeSp, true);
	}
	
	public void removeExpAndSp(long removeExp, long removeSp, boolean sendMessage)
	{
		getStat().removeExpAndSp(removeExp, removeSp, sendMessage);
	}
	
	@Override
	public void reduceCurrentHp(double value, Creature attacker, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect)
	{
		super.reduceCurrentHp(value, attacker, skill, isDOT, directlyToHp, critical, reflect);
		
		// notify the tamed beast of attacks
		for (TamedBeastInstance tamedBeast : _tamedBeast)
		{
			tamedBeast.onOwnerGotAttacked(attacker);
		}
	}
	
	public void broadcastSnoop(ChatType type, String name, String _text)
	{
		if (!_snoopListener.isEmpty())
		{
			final Snoop sn = new Snoop(getObjectId(), getName(), type, name, _text);
			
			for (PlayerInstance pci : _snoopListener)
			{
				if (pci != null)
				{
					pci.sendPacket(sn);
				}
			}
		}
	}
	
	public void addSnooper(PlayerInstance pci)
	{
		if (!_snoopListener.contains(pci))
		{
			_snoopListener.add(pci);
		}
	}
	
	public void removeSnooper(PlayerInstance pci)
	{
		_snoopListener.remove(pci);
	}
	
	public void addSnooped(PlayerInstance pci)
	{
		if (!_snoopedPlayer.contains(pci))
		{
			_snoopedPlayer.add(pci);
		}
	}
	
	public void removeSnooped(PlayerInstance pci)
	{
		_snoopedPlayer.remove(pci);
	}
	
	public void addHtmlAction(HtmlActionScope scope, String action)
	{
		_htmlActionCaches[scope.ordinal()].add(action);
	}
	
	public void clearHtmlActions(HtmlActionScope scope)
	{
		_htmlActionCaches[scope.ordinal()].clear();
	}
	
	public void setHtmlActionOriginObjectId(HtmlActionScope scope, int npcObjId)
	{
		if (npcObjId < 0)
		{
			throw new IllegalArgumentException();
		}
		
		_htmlActionOriginObjectIds[scope.ordinal()] = npcObjId;
	}
	
	public int getLastHtmlActionOriginId()
	{
		return _lastHtmlActionOriginObjId;
	}
	
	private boolean validateHtmlAction(Iterable<String> actionIter, String action)
	{
		for (String cachedAction : actionIter)
		{
			if (cachedAction.charAt(cachedAction.length() - 1) == AbstractHtmlPacket.VAR_PARAM_START_CHAR)
			{
				if (action.startsWith(cachedAction.substring(0, cachedAction.length() - 1).trim()))
				{
					return true;
				}
			}
			else if (cachedAction.equals(action))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check if the HTML action was sent in a HTML packet.<br>
	 * If the HTML action was not sent for whatever reason, -1 is returned.<br>
	 * Otherwise, the NPC object ID or 0 is returned.<br>
	 * 0 means the HTML action was not bound to an NPC<br>
	 * and no range checks need to be made.
	 * @param action the HTML action to check
	 * @return NPC object ID, 0 or -1
	 */
	public int validateHtmlAction(String action)
	{
		for (int i = 0; i < _htmlActionCaches.length; ++i)
		{
			if (validateHtmlAction(_htmlActionCaches[i], action))
			{
				_lastHtmlActionOriginObjId = _htmlActionOriginObjectIds[i];
				return _lastHtmlActionOriginObjId;
			}
		}
		
		return -1;
	}
	
	/**
	 * Performs following tests:
	 * <ul>
	 * <li>Inventory contains item</li>
	 * <li>Item owner id == owner id</li>
	 * <li>It isnt pet control item while mounting pet or pet summoned</li>
	 * <li>It isnt active enchant item</li>
	 * <li>It isnt cursed weapon/item</li>
	 * <li>It isnt wear item</li>
	 * </ul>
	 * @param objectId item object id
	 * @param action just for login porpouse
	 * @return
	 */
	public boolean validateItemManipulation(int objectId, String action)
	{
		final ItemInstance item = _inventory.getItemByObjectId(objectId);
		
		if ((item == null) || (item.getOwnerId() != getObjectId()))
		{
			LOGGER.finest(getObjectId() + ": player tried to " + action + " item he is not owner of");
			return false;
		}
		
		// Pet is summoned and not the item that summoned the pet AND not the buggle from strider you're mounting
		if (((_pet != null) && (_pet.getControlObjectId() == objectId)) || (_mountObjectID == objectId))
		{
			return false;
		}
		
		if (isProcessingItem(objectId))
		{
			return false;
		}
		
		if (CursedWeaponsManager.getInstance().isCursed(item.getId()))
		{
			// can not trade a cursed weapon
			return false;
		}
		
		return true;
	}
	
	/**
	 * @return Returns the inBoat.
	 */
	public boolean isInBoat()
	{
		return (_vehicle != null) && _vehicle.isBoat();
	}
	
	/**
	 * @return
	 */
	public BoatInstance getBoat()
	{
		return (BoatInstance) _vehicle;
	}
	
	/**
	 * @return Returns the inAirShip.
	 */
	public boolean isInAirShip()
	{
		return (_vehicle != null) && _vehicle.isAirShip();
	}
	
	/**
	 * @return
	 */
	public AirShipInstance getAirShip()
	{
		return (AirShipInstance) _vehicle;
	}
	
	public boolean isInShuttle()
	{
		return _vehicle instanceof ShuttleInstance;
	}
	
	public ShuttleInstance getShuttle()
	{
		return (ShuttleInstance) _vehicle;
	}
	
	public Vehicle getVehicle()
	{
		return _vehicle;
	}
	
	public void setVehicle(Vehicle v)
	{
		if ((v == null) && (_vehicle != null))
		{
			_vehicle.removePassenger(this);
		}
		
		_vehicle = v;
	}
	
	public boolean isInVehicle()
	{
		return _vehicle != null;
	}
	
	public void setInCrystallize(boolean inCrystallize)
	{
		_inCrystallize = inCrystallize;
	}
	
	public boolean isInCrystallize()
	{
		return _inCrystallize;
	}
	
	/**
	 * @return
	 */
	public Location getInVehiclePosition()
	{
		return _inVehiclePosition;
	}
	
	public void setInVehiclePosition(Location pt)
	{
		_inVehiclePosition = pt;
	}
	
	/**
	 * Manage the delete task of a PlayerInstance (Leave Party, Unsummon pet, Save its inventory in the database, Remove it from the world...).<br>
	 * <B><U>Actions</U>:</B>
	 * <ul>
	 * <li>If the PlayerInstance is in observer mode, set its position to its position before entering in observer mode</li>
	 * <li>Set the online Flag to True or False and update the characters table of the database with online status and lastAccess</li>
	 * <li>Stop the HP/MP/CP Regeneration task</li>
	 * <li>Cancel Crafting, Attack or Cast</li>
	 * <li>Remove the PlayerInstance from the world</li>
	 * <li>Stop Party and Unsummon Pet</li>
	 * <li>Update database with items in its inventory and remove them from the world</li>
	 * <li>Remove all WorldObject from _knownObjects and _knownPlayer of the Creature then cancel Attak or Cast and notify AI</li>
	 * <li>Close the connection with the client</li>
	 * </ul>
	 * <br>
	 * Remember this method is not to be used to half-ass disconnect players! This method is dedicated only to erase the player from the world.<br>
	 * If you intend to disconnect a player please use {@link Disconnection}
	 */
	@Override
	public boolean deleteMe()
	{
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogout(this), this);
		
		try
		{
			for (ZoneType zone : ZoneManager.getInstance().getZones(this))
			{
				zone.onPlayerLogoutInside(this);
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		// Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout)
		try
		{
			if (!_isOnline)
			{
				LOGGER.log(Level.SEVERE, "deleteMe() called on offline character " + this, new RuntimeException());
			}
			setOnlineStatus(false, true);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		try
		{
			if (Config.ENABLE_BLOCK_CHECKER_EVENT && (_handysBlockCheckerEventArena != -1))
			{
				HandysBlockCheckerManager.getInstance().onDisconnect(this);
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		try
		{
			_isOnline = false;
			abortAttack();
			abortCast();
			stopMove(null);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		// remove combat flag
		try
		{
			if (_inventory.getItemByItemId(9819) != null)
			{
				final Fort fort = FortManager.getInstance().getFort(this);
				if (fort != null)
				{
					FortSiegeManager.getInstance().dropCombatFlag(this, fort.getResidenceId());
				}
				else
				{
					final long slot = _inventory.getSlotFromItem(_inventory.getItemByItemId(9819));
					_inventory.unEquipItemInBodySlot(slot);
					destroyItem("CombatFlag", _inventory.getItemByItemId(9819), null, true);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		try
		{
			if (_matchingRoom != null)
			{
				_matchingRoom.deleteMember(this, false);
			}
			MatchingRoomManager.getInstance().removeFromWaitingList(this);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		try
		{
			if (isFlying())
			{
				removeSkill(SkillData.getInstance().getSkill(CommonSkill.WYVERN_BREATH.getId(), 1));
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		// Recommendations must be saved before task (timer) is canceled
		try
		{
			storeRecommendations();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		// Stop the HP/MP/CP Regeneration task (scheduled tasks)
		try
		{
			stopAllTimers();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		try
		{
			setIsTeleporting(false);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		// Cancel Attak or Cast
		try
		{
			setTarget(null);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		if (isChannelized())
		{
			getSkillChannelized().abortChannelization();
		}
		
		// Stop all toggles.
		getEffectList().stopAllToggles();
		
		// Remove from world regions zones
		ZoneManager.getInstance().getRegion(this).removeFromZones(this);
		
		// Remove the PlayerInstance from the world
		try
		{
			decayMe();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		// If a Party is in progress, leave it (and festival party)
		if (isInParty())
		{
			try
			{
				leaveParty();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "deleteMe()", e);
			}
		}
		
		if (OlympiadManager.getInstance().isRegistered(this) || (getOlympiadGameId() != -1))
		{
			OlympiadManager.getInstance().removeDisconnectedCompetitor(this);
		}
		
		// If the PlayerInstance has Pet, unsummon it
		if (hasSummon())
		{
			try
			{
				Summon pet = _pet;
				if (pet != null)
				{
					pet.setRestoreSummon(true);
					pet.unSummon(this);
					// Dead pet wasn't unsummoned, broadcast npcinfo changes (pet will be without owner name - means owner offline)
					pet = _pet;
					if (pet != null)
					{
						pet.broadcastNpcInfo(0);
					}
				}
				
				getServitors().values().forEach(s ->
				{
					s.setRestoreSummon(true);
					s.unSummon(this);
				});
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "deleteMe()", e);
			} // returns pet to control item
		}
		
		if (_clan != null)
		{
			// set the status for pledge member list to OFFLINE
			try
			{
				final ClanMember clanMember = _clan.getClanMember(getObjectId());
				if (clanMember != null)
				{
					clanMember.setPlayerInstance(null);
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "deleteMe()", e);
			}
		}
		
		if (getActiveRequester() != null)
		{
			// deals with sudden exit in the middle of transaction
			setActiveRequester(null);
			cancelActiveTrade();
		}
		
		// If the PlayerInstance is a GM, remove it from the GM List
		if (isGM())
		{
			try
			{
				AdminData.getInstance().deleteGm(this);
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "deleteMe()", e);
			}
		}
		
		try
		{
			// Check if the PlayerInstance is in observer mode to set its position to its position
			// before entering in observer mode
			if (_observerMode)
			{
				setLocationInvisible(_lastLoc);
			}
			
			if (_vehicle != null)
			{
				_vehicle.oustPlayer(this);
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		// remove player from instance
		final Instance inst = getInstanceWorld();
		if (inst != null)
		{
			try
			{
				inst.onPlayerLogout(this);
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "deleteMe()", e);
			}
		}
		
		try
		{
			stopCubics();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		// Update database with items in its inventory and remove them from the world
		try
		{
			_inventory.deleteMe();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		// Update database with items in its warehouse and remove them from the world
		try
		{
			clearWarehouse();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		if (Config.WAREHOUSE_CACHE)
		{
			WarehouseCacheManager.getInstance().remCacheTask(this);
		}
		
		try
		{
			_freight.deleteMe();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		try
		{
			clearRefund();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "deleteMe()", e);
		}
		
		if (isCursedWeaponEquipped())
		{
			try
			{
				CursedWeaponsManager.getInstance().getCursedWeapon(_cursedWeaponEquippedId).setPlayer(null);
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "deleteMe()", e);
			}
		}
		
		if (_clanId > 0)
		{
			_clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(this), this);
			_clan.broadcastToOnlineMembers(new ExPledgeCount(_clan));
			// ClanTable.getInstance().getClan(getClanId()).broadcastToOnlineMembers(new PledgeShowMemberListAdd(this));
		}
		
		for (PlayerInstance player : _snoopedPlayer)
		{
			player.removeSnooper(this);
		}
		
		for (PlayerInstance player : _snoopListener)
		{
			player.removeSnooped(this);
		}
		
		if (isMentee())
		{
			// Notify to scripts
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeStatus(this, false), this);
		}
		else if (isMentor())
		{
			// Notify to scripts
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMentorStatus(this, false), this);
		}
		
		// we store all data from players who are disconnected while in an event in order to restore it in the next login
		if (GameEvent.isParticipant(this))
		{
			GameEvent.savePlayerEventStatus(this);
		}
		
		try
		{
			notifyFriends(FriendStatus.MODE_OFFLINE);
			_blockList.playerLogout();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception on deleteMe() notifyFriends: " + e.getMessage(), e);
		}
		
		// Stop all passives and augment options
		getEffectList().stopAllPassives(false, false);
		getEffectList().stopAllOptions(false, false);
		
		stopAutoSaveTask();
		
		return super.deleteMe();
	}
	
	public int getInventoryLimit()
	{
		int ivlim;
		if (isGM())
		{
			ivlim = Config.INVENTORY_MAXIMUM_GM;
		}
		else if (getRace() == Race.DWARF)
		{
			ivlim = Config.INVENTORY_MAXIMUM_DWARF;
		}
		else
		{
			ivlim = Config.INVENTORY_MAXIMUM_NO_DWARF;
		}
		ivlim += (int) getStat().getValue(Stats.INVENTORY_NORMAL, 0);
		
		return ivlim;
	}
	
	public int getWareHouseLimit()
	{
		int whlim;
		if (getRace() == Race.DWARF)
		{
			whlim = Config.WAREHOUSE_SLOTS_DWARF;
		}
		else
		{
			whlim = Config.WAREHOUSE_SLOTS_NO_DWARF;
		}
		
		whlim += (int) getStat().getValue(Stats.STORAGE_PRIVATE, 0);
		
		return whlim;
	}
	
	public int getPrivateSellStoreLimit()
	{
		int pslim;
		
		if (getRace() == Race.DWARF)
		{
			pslim = Config.MAX_PVTSTORESELL_SLOTS_DWARF;
		}
		else
		{
			pslim = Config.MAX_PVTSTORESELL_SLOTS_OTHER;
		}
		
		pslim += (int) getStat().getValue(Stats.TRADE_SELL, 0);
		
		return pslim;
	}
	
	public int getPrivateBuyStoreLimit()
	{
		int pblim;
		
		if (getRace() == Race.DWARF)
		{
			pblim = Config.MAX_PVTSTOREBUY_SLOTS_DWARF;
		}
		else
		{
			pblim = Config.MAX_PVTSTOREBUY_SLOTS_OTHER;
		}
		pblim += (int) getStat().getValue(Stats.TRADE_BUY, 0);
		
		return pblim;
	}
	
	public int getDwarfRecipeLimit()
	{
		int recdlim = Config.DWARF_RECIPE_LIMIT;
		recdlim += (int) getStat().getValue(Stats.RECIPE_DWARVEN, 0);
		return recdlim;
	}
	
	public int getCommonRecipeLimit()
	{
		int recclim = Config.COMMON_RECIPE_LIMIT;
		recclim += (int) getStat().getValue(Stats.RECIPE_COMMON, 0);
		return recclim;
	}
	
	/**
	 * @return Returns the mountNpcId.
	 */
	public int getMountNpcId()
	{
		return _mountNpcId;
	}
	
	/**
	 * @return Returns the mountLevel.
	 */
	public int getMountLevel()
	{
		return _mountLevel;
	}
	
	public void setMountObjectID(int newID)
	{
		_mountObjectID = newID;
	}
	
	public int getMountObjectID()
	{
		return _mountObjectID;
	}
	
	public SkillUseHolder getQueuedSkill()
	{
		return _queuedSkill;
	}
	
	/**
	 * Create a new SkillDat object and queue it in the player _queuedSkill.
	 * @param queuedSkill
	 * @param item
	 * @param ctrlPressed
	 * @param shiftPressed
	 */
	public void setQueuedSkill(Skill queuedSkill, ItemInstance item, boolean ctrlPressed, boolean shiftPressed)
	{
		if (queuedSkill == null)
		{
			_queuedSkill = null;
			return;
		}
		_queuedSkill = new SkillUseHolder(queuedSkill, item, ctrlPressed, shiftPressed);
	}
	
	public boolean isAlterSkillActive()
	{
		return _alterSkillActive;
	}
	
	public void setAlterSkillActive(boolean alterSkillActive)
	{
		_alterSkillActive = alterSkillActive;
	}
	
	/**
	 * @return {@code true} if player is jailed, {@code false} otherwise.
	 */
	public boolean isJailed()
	{
		return PunishmentManager.getInstance().hasPunishment(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL) || PunishmentManager.getInstance().hasPunishment(getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.JAIL) || PunishmentManager.getInstance().hasPunishment(getIPAddress(), PunishmentAffect.IP, PunishmentType.JAIL);
	}
	
	/**
	 * @return {@code true} if player is chat banned, {@code false} otherwise.
	 */
	public boolean isChatBanned()
	{
		return PunishmentManager.getInstance().hasPunishment(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN) || PunishmentManager.getInstance().hasPunishment(getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.CHAT_BAN) || PunishmentManager.getInstance().hasPunishment(getIPAddress(), PunishmentAffect.IP, PunishmentType.CHAT_BAN);
	}
	
	public void startFameTask(long delay, int fameFixRate)
	{
		if ((getLevel() < 40) || (getClassId().level() < 2))
		{
			return;
		}
		if (_fameTask == null)
		{
			_fameTask = ThreadPool.scheduleAtFixedRate(new FameTask(this, fameFixRate), delay, delay);
		}
	}
	
	public void stopFameTask()
	{
		if (_fameTask != null)
		{
			_fameTask.cancel(false);
			_fameTask = null;
		}
	}
	
	public int getPowerGrade()
	{
		return _powerGrade;
	}
	
	public void setPowerGrade(int power)
	{
		_powerGrade = power;
	}
	
	public boolean isCursedWeaponEquipped()
	{
		return _cursedWeaponEquippedId != 0;
	}
	
	public void setCursedWeaponEquippedId(int value)
	{
		_cursedWeaponEquippedId = value;
	}
	
	public int getCursedWeaponEquippedId()
	{
		return _cursedWeaponEquippedId;
	}
	
	public boolean isCombatFlagEquipped()
	{
		return _combatFlagEquippedId;
	}
	
	public void setCombatFlagEquipped(boolean value)
	{
		_combatFlagEquippedId = value;
	}
	
	/**
	 * Returns the Number of Souls this PlayerInstance got.
	 * @return
	 */
	public int getChargedSouls()
	{
		return _souls;
	}
	
	/**
	 * Increase Souls
	 * @param count
	 */
	public void increaseSouls(int count)
	{
		_souls += count;
		final SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_SOUL_COUNT_HAS_INCREASED_BY_S1_IT_IS_NOW_AT_S2);
		sm.addInt(count);
		sm.addInt(_souls);
		sendPacket(sm);
		restartSoulTask();
		sendPacket(new EtcStatusUpdate(this));
	}
	
	/**
	 * Decreases existing Souls.
	 * @param count
	 * @param skill
	 * @return
	 */
	public boolean decreaseSouls(int count, Skill skill)
	{
		_souls -= count;
		
		if (_souls < 0)
		{
			_souls = 0;
		}
		
		if (_souls == 0)
		{
			stopSoulTask();
		}
		else
		{
			restartSoulTask();
		}
		
		sendPacket(new EtcStatusUpdate(this));
		return true;
	}
	
	/**
	 * Clear out all Souls from this PlayerInstance
	 */
	public void clearSouls()
	{
		_souls = 0;
		stopSoulTask();
		sendPacket(new EtcStatusUpdate(this));
	}
	
	/**
	 * Starts/Restarts the SoulTask to Clear Souls after 10 Mins.
	 */
	private void restartSoulTask()
	{
		if (_soulTask != null)
		{
			_soulTask.cancel(false);
			_soulTask = null;
		}
		_soulTask = ThreadPool.schedule(new ResetSoulsTask(this), 600000);
		
	}
	
	/**
	 * Stops the Clearing Task.
	 */
	public void stopSoulTask()
	{
		if (_soulTask != null)
		{
			_soulTask.cancel(false);
			_soulTask = null;
		}
	}
	
	public int getShilensBreathDebuffLevel()
	{
		final BuffInfo buff = getEffectList().getBuffInfoBySkillId(CommonSkill.SHILENS_BREATH.getId());
		return buff == null ? 0 : buff.getSkill().getLevel();
	}
	
	public void calculateShilensBreathDebuffLevel(Creature killer)
	{
		if (killer == null)
		{
			LOGGER.warning(this + " called calculateShilensBreathDebuffLevel with killer null!");
			return;
		}
		
		if (isResurrectSpecialAffected() || isLucky() || isBlockedFromDeathPenalty() || isInsideZone(ZoneId.PVP) || isInsideZone(ZoneId.SIEGE) || canOverrideCond(PlayerCondOverride.DEATH_PENALTY))
		{
			return;
		}
		double percent = 1.0;
		
		if (killer.isRaid())
		{
			percent *= getStat().getValue(Stats.REDUCE_DEATH_PENALTY_BY_RAID, 1);
		}
		else if (killer.isMonster())
		{
			percent *= getStat().getValue(Stats.REDUCE_DEATH_PENALTY_BY_MOB, 1);
		}
		else if (killer.isPlayable())
		{
			percent *= getStat().getValue(Stats.REDUCE_DEATH_PENALTY_BY_PVP, 1);
		}
		
		if ((killer.isNpc() && ((Npc) killer).getTemplate().isDeathPenalty()) || (Rnd.get(1, 100) <= ((Config.DEATH_PENALTY_CHANCE) * percent)))
		{
			if (!killer.isPlayable() || (getReputation() < 0))
			{
				increaseShilensBreathDebuff();
			}
		}
	}
	
	public void increaseShilensBreathDebuff()
	{
		int nextLv = getShilensBreathDebuffLevel() + 1;
		if (nextLv > 5)
		{
			nextLv = 5;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(CommonSkill.SHILENS_BREATH.getId(), nextLv);
		if (skill != null)
		{
			skill.applyEffects(this, this);
			sendPacket(new SystemMessage(SystemMessageId.YOU_VE_BEEN_AFFLICTED_BY_SHILEN_S_BREATH_LEVEL_S1).addInt(nextLv));
		}
	}
	
	public void decreaseShilensBreathDebuff()
	{
		final int nextLv = getShilensBreathDebuffLevel() - 1;
		if (nextLv > 0)
		{
			final Skill skill = SkillData.getInstance().getSkill(CommonSkill.SHILENS_BREATH.getId(), nextLv);
			skill.applyEffects(this, this);
			sendPacket(new SystemMessage(SystemMessageId.YOU_VE_BEEN_AFFLICTED_BY_SHILEN_S_BREATH_LEVEL_S1).addInt(nextLv));
		}
		else
		{
			sendPacket(SystemMessageId.SHILEN_S_BREATH_HAS_BEEN_PURIFIED);
		}
	}
	
	public void setShilensBreathDebuffLevel(int level)
	{
		if (level > 0)
		{
			final Skill skill = SkillData.getInstance().getSkill(CommonSkill.SHILENS_BREATH.getId(), level);
			skill.applyEffects(this, this);
			sendPacket(new SystemMessage(SystemMessageId.YOU_VE_BEEN_AFFLICTED_BY_SHILEN_S_BREATH_LEVEL_S1).addInt(level));
		}
	}
	
	@Override
	public PlayerInstance getActingPlayer()
	{
		return this;
	}
	
	@Override
	public void sendDamageMessage(Creature target, Skill skill, int damage, boolean crit, boolean miss)
	{
		// Check if hit is missed
		if (miss)
		{
			if (skill == null)
			{
				if (target.isPlayer())
				{
					final SystemMessage sm = new SystemMessage(SystemMessageId.C1_HAS_EVADED_C2_S_ATTACK);
					sm.addPcName(target.getActingPlayer());
					sm.addString(getName());
					target.sendPacket(sm);
				}
				final SystemMessage sm = new SystemMessage(SystemMessageId.C1_S_ATTACK_WENT_ASTRAY);
				sm.addPcName(this);
				sendPacket(sm);
			}
			else
			{
				sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.EVADED));
			}
			return;
		}
		
		// Check if hit is critical
		if (crit)
		{
			if ((skill == null) || !skill.isMagic())
			{
				final SystemMessage sm = new SystemMessage(SystemMessageId.C1_LANDED_A_CRITICAL_HIT);
				sm.addPcName(this);
				sendPacket(sm);
			}
			else
			{
				sendPacket(SystemMessageId.M_CRITICAL);
			}
			
			if (skill != null)
			{
				sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.CRITICAL));
			}
		}
		
		if (isInOlympiadMode() && target.isPlayer() && target.getActingPlayer().isInOlympiadMode() && (target.getActingPlayer().getOlympiadGameId() == getOlympiadGameId()))
		{
			OlympiadGameManager.getInstance().notifyCompetitorDamage(this, damage);
		}
		
		SystemMessage sm = null;
		
		if ((target.isHpBlocked() && !target.isNpc()) || (target.isPlayer() && target.isAffected(EffectFlag.DUELIST_FURY) && !isAffected(EffectFlag.FACEOFF)))
		{
			sm = new SystemMessage(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
		}
		else if (target.isDoor() || (target instanceof ControlTowerInstance))
		{
			sm = new SystemMessage(SystemMessageId.YOU_HIT_FOR_S1_DAMAGE);
			sm.addInt(damage);
		}
		else if (this != target)
		{
			sm = new SystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
			sm.addPcName(this);
			
			// Localisation related.
			String targetName = target.getName();
			if (Config.MULTILANG_ENABLE && target.isNpc())
			{
				final String[] localisation = NpcNameLocalisationData.getInstance().getLocalisation(_lang, target.getId());
				if (localisation != null)
				{
					targetName = localisation[0];
				}
			}
			
			sm.addString(targetName);
			sm.addInt(damage);
			sm.addPopup(target.getObjectId(), getObjectId(), -damage);
		}
		
		if (sm != null)
		{
			sendPacket(sm);
		}
	}
	
	/**
	 * @param npcId
	 */
	public void setAgathionId(int npcId)
	{
		_agathionId = npcId;
	}
	
	/**
	 * @return
	 */
	public int getAgathionId()
	{
		return _agathionId;
	}
	
	public int getVitalityPoints()
	{
		return getStat().getVitalityPoints();
	}
	
	public void setVitalityPoints(int points, boolean quiet)
	{
		getStat().setVitalityPoints(points, quiet);
	}
	
	public void updateVitalityPoints(int points, boolean useRates, boolean quiet)
	{
		getStat().updateVitalityPoints(points, useRates, quiet);
	}
	
	public void checkItemRestriction()
	{
		for (int i = 0; i < Inventory.PAPERDOLL_TOTALSLOTS; i++)
		{
			final ItemInstance equippedItem = _inventory.getPaperdollItem(i);
			if ((equippedItem != null) && !equippedItem.getItem().checkCondition(this, this, false))
			{
				_inventory.unEquipItemInSlot(i);
				
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(equippedItem);
				sendInventoryUpdate(iu);
				
				SystemMessage sm = null;
				if (equippedItem.getItem().getBodyPart() == Item.SLOT_BACK)
				{
					sendPacket(SystemMessageId.YOUR_CLOAK_HAS_BEEN_UNEQUIPPED_BECAUSE_YOUR_ARMOR_SET_IS_NO_LONGER_COMPLETE);
					return;
				}
				
				if (equippedItem.getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.S1_S2_HAS_BEEN_UNEQUIPPED);
					sm.addInt(equippedItem.getEnchantLevel());
					sm.addItemName(equippedItem);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
					sm.addItemName(equippedItem);
				}
				sendPacket(sm);
			}
		}
	}
	
	public void addTransformSkill(Skill skill)
	{
		_transformSkills.put(skill.getId(), skill);
	}
	
	public boolean hasTransformSkill(Skill skill)
	{
		return _transformSkills.get(skill.getId()) == skill;
	}
	
	public boolean hasTransformSkills()
	{
		return !_transformSkills.isEmpty();
	}
	
	public Collection<Skill> getAllTransformSkills()
	{
		return _transformSkills.values();
	}
	
	public void removeAllTransformSkills()
	{
		_transformSkills.clear();
	}
	
	/**
	 * @param skillId the id of the skill that this player might have.
	 * @return {@code skill} object refered to this skill id that this player has, {@code null} otherwise.
	 */
	@Override
	public Skill getKnownSkill(int skillId)
	{
		return !_transformSkills.isEmpty() ? _transformSkills.getOrDefault(skillId, super.getKnownSkill(skillId)) : super.getKnownSkill(skillId);
	}
	
	/**
	 * @return all visible skills that appear on Alt+K for this player.
	 */
	public Collection<Skill> getSkillList()
	{
		Collection<Skill> currentSkills = getAllSkills();
		
		if (isTransformed())
		{
			if (!_transformSkills.isEmpty())
			{
				// Include transformation skills and those skills that are allowed during transformation.
				currentSkills = currentSkills.stream().filter(Skill::allowOnTransform).collect(Collectors.toList());
				
				// Revelation skills.
				if (isDualClassActive())
				{
					int revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_1_DUAL_CLASS, 0);
					if (revelationSkill != 0)
					{
						addSkill(SkillData.getInstance().getSkill(revelationSkill, 1), false);
					}
					revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_2_DUAL_CLASS, 0);
					if (revelationSkill != 0)
					{
						addSkill(SkillData.getInstance().getSkill(revelationSkill, 1), false);
					}
				}
				else if (!isSubClassActive())
				{
					int revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_1_MAIN_CLASS, 0);
					if (revelationSkill != 0)
					{
						addSkill(SkillData.getInstance().getSkill(revelationSkill, 1), false);
					}
					revelationSkill = getVariables().getInt(PlayerVariables.REVELATION_SKILL_2_MAIN_CLASS, 0);
					if (revelationSkill != 0)
					{
						addSkill(SkillData.getInstance().getSkill(revelationSkill, 1), false);
					}
				}
				// Include transformation skills.
				currentSkills.addAll(_transformSkills.values());
			}
		}
		
		//@formatter:off
		return currentSkills.stream()
							.filter(Objects::nonNull)
							.filter(s -> !s.isBlockActionUseSkill()) // Skills that are blocked from player use are not shown in skill list.
							.filter(s -> !SkillTreesData.getInstance().isAlchemySkill(s.getId(), s.getLevel()))
							.filter(s -> s.isDisplayInList())
							.collect(Collectors.toList());
		//@formatter:on
	}
	
	protected void startFeed(int npcId)
	{
		_canFeed = npcId > 0;
		if (!isMounted())
		{
			return;
		}
		if (hasPet())
		{
			setCurrentFeed(_pet.getCurrentFed());
			_controlItemId = _pet.getControlObjectId();
			sendPacket(new SetupGauge(3, (_curFeed * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume()));
			if (!isDead())
			{
				_mountFeedTask = ThreadPool.scheduleAtFixedRate(new PetFeedTask(this), 10000, 10000);
			}
		}
		else if (_canFeed)
		{
			setCurrentFeed(getMaxFeed());
			final SetupGauge sg = new SetupGauge(3, (_curFeed * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume());
			sendPacket(sg);
			if (!isDead())
			{
				_mountFeedTask = ThreadPool.scheduleAtFixedRate(new PetFeedTask(this), 10000, 10000);
			}
		}
	}
	
	public void stopFeed()
	{
		if (_mountFeedTask != null)
		{
			_mountFeedTask.cancel(false);
			_mountFeedTask = null;
		}
	}
	
	private void clearPetData()
	{
		_data = null;
	}
	
	public PetData getPetData(int npcId)
	{
		if (_data == null)
		{
			_data = PetDataTable.getInstance().getPetData(npcId);
		}
		return _data;
	}
	
	private PetLevelData getPetLevelData(int npcId)
	{
		if (_leveldata == null)
		{
			_leveldata = PetDataTable.getInstance().getPetData(npcId).getPetLevelData(getMountLevel());
		}
		return _leveldata;
	}
	
	public int getCurrentFeed()
	{
		return _curFeed;
	}
	
	public int getFeedConsume()
	{
		// if pet is attacking
		if (isAttackingNow())
		{
			return getPetLevelData(_mountNpcId).getPetFeedBattle();
		}
		return getPetLevelData(_mountNpcId).getPetFeedNormal();
	}
	
	public void setCurrentFeed(int num)
	{
		final boolean lastHungryState = isHungry();
		_curFeed = num > getMaxFeed() ? getMaxFeed() : num;
		final SetupGauge sg = new SetupGauge(3, (_curFeed * 10000) / getFeedConsume(), (getMaxFeed() * 10000) / getFeedConsume());
		sendPacket(sg);
		// broadcast move speed change when strider becomes hungry / full
		if (lastHungryState != isHungry())
		{
			broadcastUserInfo();
		}
	}
	
	private int getMaxFeed()
	{
		return getPetLevelData(_mountNpcId).getPetMaxFeed();
	}
	
	public boolean isHungry()
	{
		return hasPet() && _canFeed && (_curFeed < ((getPetData(_mountNpcId).getHungryLimit() / 100f) * getPetLevelData(_mountNpcId).getPetMaxFeed()));
	}
	
	public void enteredNoLanding(int delay)
	{
		_dismountTask = ThreadPool.schedule(new DismountTask(this), delay * 1000);
	}
	
	public void exitedNoLanding()
	{
		if (_dismountTask != null)
		{
			_dismountTask.cancel(true);
			_dismountTask = null;
		}
	}
	
	public void storePetFood(int petId)
	{
		if ((_controlItemId != 0) && (petId != 0))
		{
			String req;
			req = "UPDATE pets SET fed=? WHERE item_obj_id = ?";
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement(req))
			{
				statement.setInt(1, _curFeed);
				statement.setInt(2, _controlItemId);
				statement.executeUpdate();
				_controlItemId = 0;
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Failed to store Pet [NpcId: " + petId + "] data", e);
			}
		}
	}
	
	public void setIsInSiege(boolean b)
	{
		_isInSiege = b;
	}
	
	public boolean isInSiege()
	{
		return _isInSiege;
	}
	
	/**
	 * @param isInHideoutSiege sets the value of {@link #_isInHideoutSiege}.
	 */
	public void setIsInHideoutSiege(boolean isInHideoutSiege)
	{
		_isInHideoutSiege = isInHideoutSiege;
	}
	
	/**
	 * @return the value of {@link #_isInHideoutSiege}, {@code true} if the player is participing on a Hideout Siege, otherwise {@code false}.
	 */
	public boolean isInHideoutSiege()
	{
		return _isInHideoutSiege;
	}
	
	public FloodProtectors getFloodProtectors()
	{
		return _client.getFloodProtectors();
	}
	
	public boolean isFlyingMounted()
	{
		return checkTransformed(Transform::isFlying);
	}
	
	/**
	 * Returns the Number of Charges this PlayerInstance got.
	 * @return
	 */
	public int getCharges()
	{
		return _charges.get();
	}
	
	public void setCharges(int count)
	{
		restartChargeTask();
		_charges.set(count);
	}
	
	public boolean decreaseCharges(int count)
	{
		if (_charges.get() < count)
		{
			return false;
		}
		
		// Charge clear task should be reset every time a charge is decreased and stopped when charges become 0.
		if (_charges.addAndGet(-count) == 0)
		{
			stopChargeTask();
		}
		else
		{
			restartChargeTask();
		}
		
		sendPacket(new EtcStatusUpdate(this));
		return true;
	}
	
	public void clearCharges()
	{
		_charges.set(0);
		sendPacket(new EtcStatusUpdate(this));
	}
	
	/**
	 * Starts/Restarts the ChargeTask to Clear Charges after 10 Mins.
	 */
	private void restartChargeTask()
	{
		if (_chargeTask != null)
		{
			_chargeTask.cancel(false);
			_chargeTask = null;
		}
		_chargeTask = ThreadPool.schedule(new ResetChargesTask(this), 600000);
	}
	
	/**
	 * Stops the Charges Clearing Task.
	 */
	public void stopChargeTask()
	{
		if (_chargeTask != null)
		{
			_chargeTask.cancel(false);
			_chargeTask = null;
		}
	}
	
	public void teleportBookmarkModify(int id, int icon, String tag, String name)
	{
		final TeleportBookmark bookmark = _tpbookmarks.get(id);
		if (bookmark != null)
		{
			bookmark.setIcon(icon);
			bookmark.setTag(tag);
			bookmark.setName(name);
			
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement(UPDATE_TP_BOOKMARK))
			{
				statement.setInt(1, icon);
				statement.setString(2, tag);
				statement.setString(3, name);
				statement.setInt(4, getObjectId());
				statement.setInt(5, id);
				statement.execute();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "Could not update character teleport bookmark data: " + e.getMessage(), e);
			}
		}
		
		sendPacket(new ExGetBookMarkInfoPacket(this));
	}
	
	public void teleportBookmarkDelete(int id)
	{
		if (_tpbookmarks.remove(id) != null)
		{
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement(DELETE_TP_BOOKMARK))
			{
				statement.setInt(1, getObjectId());
				statement.setInt(2, id);
				statement.execute();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "Could not delete character teleport bookmark data: " + e.getMessage(), e);
			}
			
			sendPacket(new ExGetBookMarkInfoPacket(this));
		}
	}
	
	public void teleportBookmarkGo(int id)
	{
		if (!teleportBookmarkCondition(0))
		{
			return;
		}
		if (_inventory.getInventoryItemCount(13016, 0) == 0)
		{
			sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM);
			return;
		}
		final SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
		sm.addItemName(13016);
		sendPacket(sm);
		
		final TeleportBookmark bookmark = _tpbookmarks.get(id);
		if (bookmark != null)
		{
			destroyItem("Consume", _inventory.getItemByItemId(13016).getObjectId(), 1, null, false);
			teleToLocation(bookmark, false);
		}
		sendPacket(new ExGetBookMarkInfoPacket(this));
	}
	
	public boolean teleportBookmarkCondition(int type)
	{
		if (isInCombat())
		{
			sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
			return false;
		}
		else if (_isInSiege || (_siegeState != 0))
		{
			sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGE_SCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_CLAN_HALL_SIEGE);
			return false;
		}
		else if (_isInDuel || _startingDuel)
		{
			sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
			return false;
		}
		else if (isFlying())
		{
			sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
			return false;
		}
		else if (_inOlympiadMode)
		{
			sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
			return false;
		}
		else if (hasBlockActions() && hasAbnormalType(AbnormalType.PARALYZE))
		{
			sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_PETRIFIED_OR_PARALYZED_STATE);
			return false;
		}
		else if (isDead())
		{
			sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD);
			return false;
		}
		else if (isInWater())
		{
			sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
			return false;
		}
		else if ((type == 1) && (isInsideZone(ZoneId.SIEGE) || isInsideZone(ZoneId.CLAN_HALL) || isInsideZone(ZoneId.JAIL) || isInsideZone(ZoneId.CASTLE) || isInsideZone(ZoneId.NO_SUMMON_FRIEND) || isInsideZone(ZoneId.FORT)))
		{
			sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
			return false;
		}
		else if (isInsideZone(ZoneId.NO_BOOKMARK) || isInBoat() || isInAirShip())
		{
			if (type == 0)
			{
				sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA);
			}
			else if (type == 1)
			{
				sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
			}
			return false;
		}
		/*
		 * TODO: Instant Zone still not implemented else if (isInsideZone(ZoneId.INSTANT)) { sendPacket(new SystemMessage(2357)); return; }
		 */
		else
		{
			return true;
		}
	}
	
	public void teleportBookmarkAdd(int x, int y, int z, int icon, String tag, String name)
	{
		if (!teleportBookmarkCondition(1))
		{
			return;
		}
		
		if (_tpbookmarks.size() >= _bookmarkslot)
		{
			sendPacket(SystemMessageId.YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION);
			return;
		}
		
		if (_inventory.getInventoryItemCount(20033, 0) == 0)
		{
			sendPacket(SystemMessageId.YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG);
			return;
		}
		
		int id;
		for (id = 1; id <= _bookmarkslot; ++id)
		{
			if (!_tpbookmarks.containsKey(id))
			{
				break;
			}
		}
		_tpbookmarks.put(id, new TeleportBookmark(id, x, y, z, icon, tag, name));
		
		destroyItem("Consume", _inventory.getItemByItemId(20033).getObjectId(), 1, null, false);
		
		final SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
		sm.addItemName(20033);
		sendPacket(sm);
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_TP_BOOKMARK))
		{
			statement.setInt(1, getObjectId());
			statement.setInt(2, id);
			statement.setInt(3, x);
			statement.setInt(4, y);
			statement.setInt(5, z);
			statement.setInt(6, icon);
			statement.setString(7, tag);
			statement.setString(8, name);
			statement.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Could not insert character teleport bookmark data: " + e.getMessage(), e);
		}
		sendPacket(new ExGetBookMarkInfoPacket(this));
	}
	
	public void restoreTeleportBookmark()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_TP_BOOKMARK))
		{
			statement.setInt(1, getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					_tpbookmarks.put(rset.getInt("Id"), new TeleportBookmark(rset.getInt("Id"), rset.getInt("x"), rset.getInt("y"), rset.getInt("z"), rset.getInt("icon"), rset.getString("tag"), rset.getString("name")));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Failed restoing character teleport bookmark.", e);
		}
	}
	
	@Override
	public void sendInfo(PlayerInstance player)
	{
		if (isInBoat())
		{
			setXYZ(getBoat().getLocation());
			
			player.sendPacket(new CharInfo(this, isInvisible() && player.canOverrideCond(PlayerCondOverride.SEE_ALL_PLAYERS)));
			player.sendPacket(new GetOnVehicle(getObjectId(), getBoat().getObjectId(), _inVehiclePosition));
		}
		else if (isInAirShip())
		{
			setXYZ(getAirShip().getLocation());
			player.sendPacket(new CharInfo(this, isInvisible() && player.canOverrideCond(PlayerCondOverride.SEE_ALL_PLAYERS)));
			player.sendPacket(new ExGetOnAirShip(this, getAirShip()));
		}
		else
		{
			player.sendPacket(new CharInfo(this, isInvisible() && player.canOverrideCond(PlayerCondOverride.SEE_ALL_PLAYERS)));
		}
		
		final int relation1 = getRelation(player);
		final RelationChanged rc1 = new RelationChanged();
		rc1.addRelation(this, relation1, !isInsideZone(ZoneId.PEACE));
		if (hasSummon())
		{
			if (_pet != null)
			{
				rc1.addRelation(_pet, relation1, !isInsideZone(ZoneId.PEACE));
			}
			if (hasServitors())
			{
				getServitors().values().forEach(s -> rc1.addRelation(s, relation1, !isInsideZone(ZoneId.PEACE)));
			}
		}
		player.sendPacket(rc1);
		
		final int relation2 = player.getRelation(this);
		final RelationChanged rc2 = new RelationChanged();
		rc2.addRelation(player, relation2, !player.isInsideZone(ZoneId.PEACE));
		if (player.hasSummon())
		{
			if (_pet != null)
			{
				rc2.addRelation(_pet, relation2, !player.isInsideZone(ZoneId.PEACE));
			}
			if (hasServitors())
			{
				getServitors().values().forEach(s -> rc2.addRelation(s, relation2, !player.isInsideZone(ZoneId.PEACE)));
			}
		}
		sendPacket(rc2);
		
		switch (_privateStoreType)
		{
			case SELL:
			{
				player.sendPacket(new PrivateStoreMsgSell(this));
				break;
			}
			case PACKAGE_SELL:
			{
				player.sendPacket(new ExPrivateStoreSetWholeMsg(this));
				break;
			}
			case BUY:
			{
				player.sendPacket(new PrivateStoreMsgBuy(this));
				break;
			}
			case MANUFACTURE:
			{
				player.sendPacket(new RecipeShopMsg(this));
				break;
			}
		}
	}
	
	public void playMovie(MovieHolder holder)
	{
		if (_movieHolder != null)
		{
			return;
		}
		abortAttack();
		// abortCast(); Confirmed in retail, playing a movie does not abort cast.
		stopMove(null);
		setMovieHolder(holder);
		if (!isTeleporting())
		{
			sendPacket(new ExStartScenePlayer(holder.getMovie()));
		}
	}
	
	public void stopMovie()
	{
		sendPacket(new ExStopScenePlayer(_movieHolder.getMovie()));
		setMovieHolder(null);
	}
	
	public boolean isAllowedToEnchantSkills()
	{
		if (isSubclassLocked())
		{
			return false;
		}
		if (isTransformed())
		{
			return false;
		}
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(this))
		{
			return false;
		}
		if (isCastingNow())
		{
			return false;
		}
		if (isInBoat() || isInAirShip())
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Set the _createDate of the PlayerInstance.
	 * @param createDate
	 */
	public void setCreateDate(Calendar createDate)
	{
		_createDate = createDate;
	}
	
	/**
	 * @return the _createDate of the PlayerInstance.
	 */
	public Calendar getCreateDate()
	{
		return _createDate;
	}
	
	/**
	 * @return number of days to char birthday.
	 */
	public int checkBirthDay()
	{
		final Calendar now = Calendar.getInstance();
		
		// "Characters with a February 29 creation date will receive a gift on February 28."
		if ((_createDate.get(Calendar.DAY_OF_MONTH) == 29) && (_createDate.get(Calendar.MONTH) == 1))
		{
			_createDate.add(Calendar.HOUR_OF_DAY, -24);
		}
		
		if ((now.get(Calendar.MONTH) == _createDate.get(Calendar.MONTH)) && (now.get(Calendar.DAY_OF_MONTH) == _createDate.get(Calendar.DAY_OF_MONTH)) && (now.get(Calendar.YEAR) != _createDate.get(Calendar.YEAR)))
		{
			return 0;
		}
		
		int i;
		for (i = 1; i < 6; i++)
		{
			now.add(Calendar.HOUR_OF_DAY, 24);
			if ((now.get(Calendar.MONTH) == _createDate.get(Calendar.MONTH)) && (now.get(Calendar.DAY_OF_MONTH) == _createDate.get(Calendar.DAY_OF_MONTH)) && (now.get(Calendar.YEAR) != _createDate.get(Calendar.YEAR)))
			{
				return i;
			}
		}
		return -1;
	}
	
	public int getBirthdays()
	{
		long time = (System.currentTimeMillis() - _createDate.getTimeInMillis()) / 1000;
		time /= TimeUnit.DAYS.toMillis(365);
		return (int) time;
	}
	
	/**
	 * list of character friends
	 */
	private final Set<Integer> _friendList = ConcurrentHashMap.newKeySet();
	
	public Set<Integer> getFriendList()
	{
		return _friendList;
	}
	
	public void restoreFriendList()
	{
		_friendList.clear();
		
		final String sqlQuery = "SELECT friendId FROM character_friends WHERE charId=? AND relation=0";
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(sqlQuery))
		{
			statement.setInt(1, getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					final int friendId = rset.getInt("friendId");
					if (friendId == getObjectId())
					{
						continue;
					}
					_friendList.add(friendId);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Error found in " + getName() + "'s FriendList: " + e.getMessage(), e);
		}
	}
	
	public void notifyFriends(int type)
	{
		final FriendStatus pkt = new FriendStatus(this, type);
		for (int id : _friendList)
		{
			final PlayerInstance friend = World.getInstance().getPlayer(id);
			if (friend != null)
			{
				friend.sendPacket(pkt);
			}
		}
	}
	
	/**
	 * Verify if this player is in silence mode.
	 * @return the {@code true} if this player is in silence mode, {@code false} otherwise
	 */
	public boolean isSilenceMode()
	{
		return _silenceMode;
	}
	
	/**
	 * While at silenceMode, checks if this player blocks PMs for this user
	 * @param playerObjId the player object Id
	 * @return {@code true} if the given Id is not excluded and this player is in silence mode, {@code false} otherwise
	 */
	public boolean isSilenceMode(int playerObjId)
	{
		if (Config.SILENCE_MODE_EXCLUDE && _silenceMode && (_silenceModeExcluded != null))
		{
			return !_silenceModeExcluded.contains(playerObjId);
		}
		return _silenceMode;
	}
	
	/**
	 * Set the silence mode.
	 * @param mode the value
	 */
	public void setSilenceMode(boolean mode)
	{
		_silenceMode = mode;
		if (_silenceModeExcluded != null)
		{
			_silenceModeExcluded.clear(); // Clear the excluded list on each setSilenceMode
		}
		sendPacket(new EtcStatusUpdate(this));
	}
	
	/**
	 * Add a player to the "excluded silence mode" list.
	 * @param playerObjId the player's object Id
	 */
	public void addSilenceModeExcluded(int playerObjId)
	{
		if (_silenceModeExcluded == null)
		{
			_silenceModeExcluded = new ArrayList<>(1);
		}
		_silenceModeExcluded.add(playerObjId);
	}
	
	private void storeRecipeShopList()
	{
		if (hasManufactureShop())
		{
			try (Connection con = DatabaseFactory.getConnection())
			{
				try (PreparedStatement st = con.prepareStatement(DELETE_CHAR_RECIPE_SHOP))
				{
					st.setInt(1, getObjectId());
					st.execute();
				}
				
				try (PreparedStatement st = con.prepareStatement(INSERT_CHAR_RECIPE_SHOP))
				{
					final AtomicInteger slot = new AtomicInteger(1);
					con.setAutoCommit(false);
					for (Entry<Integer, Long> entry : _manufactureItems.entrySet())
					{
						st.setInt(1, getObjectId());
						st.setInt(2, entry.getKey());
						st.setLong(3, entry.getValue());
						st.setInt(4, slot.getAndIncrement());
						st.addBatch();
					}
					st.executeBatch();
					con.commit();
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Could not store recipe shop for playerId " + getObjectId() + ": ", e);
			}
		}
	}
	
	private void restoreRecipeShopList()
	{
		final Map<Integer, Long> manufactureItems = new HashMap<>();
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_CHAR_RECIPE_SHOP))
		{
			statement.setInt(1, getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					manufactureItems.put(rset.getInt("recipeId"), rset.getLong("price"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not restore recipe shop list data for playerId: " + getObjectId(), e);
		}
		
		_manufactureItems = manufactureItems;
	}
	
	@Override
	public double getCollisionRadius()
	{
		if (isMounted() && (_mountNpcId > 0))
		{
			return NpcData.getInstance().getTemplate(getMountNpcId()).getfCollisionRadius();
		}
		
		final double defaultCollisionRadius = _appearance.isFemale() ? getBaseTemplate().getFCollisionRadiusFemale() : getBaseTemplate().getfCollisionRadius();
		return getTransformation().map(transform -> transform.getCollisionRadius(this, defaultCollisionRadius)).orElse(defaultCollisionRadius);
	}
	
	@Override
	public double getCollisionHeight()
	{
		if (isMounted() && (_mountNpcId > 0))
		{
			return NpcData.getInstance().getTemplate(getMountNpcId()).getfCollisionHeight();
		}
		
		final double defaultCollisionHeight = _appearance.isFemale() ? getBaseTemplate().getFCollisionHeightFemale() : getBaseTemplate().getfCollisionHeight();
		return getTransformation().map(transform -> transform.getCollisionHeight(this, defaultCollisionHeight)).orElse(defaultCollisionHeight);
	}
	
	public int getClientX()
	{
		return _clientX;
	}
	
	public int getClientY()
	{
		return _clientY;
	}
	
	public int getClientZ()
	{
		return _clientZ;
	}
	
	public int getClientHeading()
	{
		return _clientHeading;
	}
	
	public void setClientX(int val)
	{
		_clientX = val;
	}
	
	public void setClientY(int val)
	{
		_clientY = val;
	}
	
	public void setClientZ(int val)
	{
		_clientZ = val;
	}
	
	public void setClientHeading(int val)
	{
		_clientHeading = val;
	}
	
	/**
	 * @param z
	 * @return true if character falling now on the start of fall return false for correct coord sync!
	 */
	public boolean isFalling(int z)
	{
		if (isDead() || isFlying() || isFlyingMounted() || isInsideZone(ZoneId.WATER))
		{
			return false;
		}
		
		if ((_fallingTimestamp != 0) && (System.currentTimeMillis() < _fallingTimestamp))
		{
			return true;
		}
		
		final int deltaZ = getZ() - z;
		if (deltaZ <= getBaseTemplate().getSafeFallHeight())
		{
			_fallingTimestamp = 0;
			return false;
		}
		
		// If there is no geodata loaded for the place we are, client Z correction might cause falling damage.
		if (!GeoEngine.getInstance().hasGeo(getX(), getY()))
		{
			_fallingTimestamp = 0;
			return false;
		}
		
		if (_fallingDamage == 0)
		{
			_fallingDamage = (int) Formulas.calcFallDam(this, deltaZ);
		}
		if (_fallingDamageTask != null)
		{
			_fallingDamageTask.cancel(true);
		}
		_fallingDamageTask = ThreadPool.schedule(() ->
		{
			if ((_fallingDamage > 0) && !isInvul())
			{
				reduceCurrentHp(Math.min(_fallingDamage, getCurrentHp() - 1), this, null, false, true, false, false);
				final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_RECEIVED_S1_FALLING_DAMAGE);
				sm.addInt(_fallingDamage);
				sendPacket(sm);
			}
			_fallingDamage = 0;
			_fallingDamageTask = null;
		}, 1500);
		
		// Prevent falling under ground.
		sendPacket(new ValidateLocation(this));
		
		setFalling();
		
		return false;
	}
	
	/**
	 * Set falling timestamp
	 */
	public void setFalling()
	{
		_fallingTimestamp = System.currentTimeMillis() + FALLING_VALIDATION_DELAY;
	}
	
	/**
	 * @return the _movie
	 */
	public MovieHolder getMovieHolder()
	{
		return _movieHolder;
	}
	
	public void setMovieHolder(MovieHolder movie)
	{
		_movieHolder = movie;
	}
	
	/**
	 * Update last item auction request timestamp to current
	 */
	public void updateLastItemAuctionRequest()
	{
		_lastItemAuctionInfoRequest = System.currentTimeMillis();
	}
	
	/**
	 * @return true if receiving item auction requests<br>
	 *         (last request was in 2 seconds before)
	 */
	public boolean isItemAuctionPolling()
	{
		return (System.currentTimeMillis() - _lastItemAuctionInfoRequest) < 2000;
	}
	
	@Override
	public boolean isMovementDisabled()
	{
		return super.isMovementDisabled() || (_movieHolder != null) || _fishing.isFishing();
	}
	
	public String getHtmlPrefix()
	{
		if (!Config.MULTILANG_ENABLE)
		{
			return "";
		}
		
		return _htmlPrefix;
	}
	
	public String getLang()
	{
		return _lang;
	}
	
	public boolean setLang(String lang)
	{
		boolean result = false;
		if (Config.MULTILANG_ENABLE)
		{
			if (Config.MULTILANG_ALLOWED.contains(lang))
			{
				_lang = lang;
				result = true;
			}
			else
			{
				_lang = Config.MULTILANG_DEFAULT;
			}
			
			_htmlPrefix = _lang.equals("en") ? "" : "data/lang/" + _lang + "/";
		}
		else
		{
			_lang = null;
			_htmlPrefix = "";
		}
		
		return result;
	}
	
	public long getOfflineStartTime()
	{
		return _offlineShopStart;
	}
	
	public void setOfflineStartTime(long time)
	{
		_offlineShopStart = time;
	}
	
	public int getPcCafePoints()
	{
		return _pcCafePoints;
	}
	
	public void setPcCafePoints(int count)
	{
		_pcCafePoints = count < Config.PC_CAFE_MAX_POINTS ? count : Config.PC_CAFE_MAX_POINTS;
	}
	
	/**
	 * Check all player skills for skill level. If player level is lower than skill learn level - 9, skill level is decreased to next possible level.
	 */
	public void checkPlayerSkills()
	{
		SkillLearn learn;
		for (Entry<Integer, Skill> e : getSkills().entrySet())
		{
			learn = SkillTreesData.getInstance().getClassSkill(e.getKey(), e.getValue().getLevel() % 100, getClassId());
			if (learn != null)
			{
				final int lvlDiff = e.getKey() == CommonSkill.EXPERTISE.getId() ? 0 : 9;
				if (getLevel() < (learn.getGetLevel() - lvlDiff))
				{
					deacreaseSkillLevel(e.getValue(), lvlDiff);
				}
			}
		}
	}
	
	private void deacreaseSkillLevel(Skill skill, int lvlDiff)
	{
		int nextLevel = -1;
		final Map<Long, SkillLearn> skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(getClassId());
		for (SkillLearn sl : skillTree.values())
		{
			if ((sl.getSkillId() == skill.getId()) && (nextLevel < sl.getSkillLevel()) && (getLevel() >= (sl.getGetLevel() - lvlDiff)))
			{
				nextLevel = sl.getSkillLevel(); // next possible skill level
			}
		}
		
		if (nextLevel == -1)
		{
			LOGGER.info("Removing skill " + skill + " from player " + toString());
			removeSkill(skill, true); // there is no lower skill
		}
		else
		{
			LOGGER.info("Decreasing skill " + skill + " to " + nextLevel + " for player " + toString());
			addSkill(SkillData.getInstance().getSkill(skill.getId(), nextLevel), true); // replace with lower one
		}
	}
	
	public boolean canMakeSocialAction()
	{
		return ((_privateStoreType == PrivateStoreType.NONE) && (getActiveRequester() == null) && !isAlikeDead() && !isAllSkillsDisabled() && !isCastingNow() && (getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE));
	}
	
	public void setMultiSocialAction(int id, int targetId)
	{
		_multiSociaAction = id;
		_multiSocialTarget = targetId;
	}
	
	public int getMultiSociaAction()
	{
		return _multiSociaAction;
	}
	
	public int getMultiSocialTarget()
	{
		return _multiSocialTarget;
	}
	
	public Collection<TeleportBookmark> getTeleportBookmarks()
	{
		return _tpbookmarks.values();
	}
	
	public int getBookmarkslot()
	{
		return _bookmarkslot;
	}
	
	/**
	 * @return
	 */
	public int getQuestInventoryLimit()
	{
		return Config.INVENTORY_MAXIMUM_QUEST_ITEMS;
	}
	
	public boolean canAttackCreature(Creature creature)
	{
		if (creature.isAttackable())
		{
			return true;
		}
		else if (creature.isPlayable())
		{
			if (creature.isInsideZone(ZoneId.PVP) && !creature.isInsideZone(ZoneId.SIEGE))
			{
				return true;
			}
			
			final PlayerInstance target = creature.isSummon() ? ((Summon) creature).getOwner() : (PlayerInstance) creature;
			
			if (isInDuel() && target.isInDuel() && (target.getDuelId() == getDuelId()))
			{
				return true;
			}
			else if (isInParty() && target.isInParty())
			{
				if (getParty() == target.getParty())
				{
					return false;
				}
				if (((getParty().getCommandChannel() != null) || (target.getParty().getCommandChannel() != null)) && (getParty().getCommandChannel() == target.getParty().getCommandChannel()))
				{
					return false;
				}
			}
			else if ((getClan() != null) && (target.getClan() != null))
			{
				if (getClanId() == target.getClanId())
				{
					return false;
				}
				if (((getAllyId() > 0) || (target.getAllyId() > 0)) && (getAllyId() == target.getAllyId()))
				{
					return false;
				}
				if (getClan().isAtWarWith(target.getClan().getId()) && target.getClan().isAtWarWith(getClan().getId()))
				{
					return true;
				}
			}
			else if ((getClan() == null) || (target.getClan() == null))
			{
				if ((target.getPvpFlag() == 0) && (target.getReputation() >= 0))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Test if player inventory is under 90% capacity
	 * @param includeQuestInv check also quest inventory
	 * @return
	 */
	public boolean isInventoryUnder90(boolean includeQuestInv)
	{
		return (_inventory.getSize(item -> !item.isQuestItem() || includeQuestInv) <= (getInventoryLimit() * 0.9));
	}
	
	/**
	 * Test if player inventory is under 80% capacity
	 * @param includeQuestInv check also quest inventory
	 * @return
	 */
	public boolean isInventoryUnder80(boolean includeQuestInv)
	{
		return (_inventory.getSize(item -> !item.isQuestItem() || includeQuestInv) <= (getInventoryLimit() * 0.8));
	}
	
	public boolean havePetInvItems()
	{
		return _petItems;
	}
	
	public void setPetInvItems(boolean haveit)
	{
		_petItems = haveit;
	}
	
	/**
	 * Restore Pet's inventory items from database.
	 */
	private void restorePetInventoryItems()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT object_id FROM `items` WHERE `owner_id`=? AND (`loc`='PET' OR `loc`='PET_EQUIP') LIMIT 1;"))
		{
			statement.setInt(1, getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				setPetInvItems(rset.next() && (rset.getInt("object_id") > 0));
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not check Items in Pet Inventory for playerId: " + getObjectId(), e);
		}
	}
	
	public String getAdminConfirmCmd()
	{
		return _adminConfirmCmd;
	}
	
	public void setAdminConfirmCmd(String adminConfirmCmd)
	{
		_adminConfirmCmd = adminConfirmCmd;
	}
	
	public void setBlockCheckerArena(byte arena)
	{
		_handysBlockCheckerEventArena = arena;
	}
	
	public int getBlockCheckerArena()
	{
		return _handysBlockCheckerEventArena;
	}
	
	/**
	 * Load PlayerInstance Recommendations data.
	 */
	private void loadRecommendations()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT rec_have, rec_left FROM character_reco_bonus WHERE charId = ?"))
		{
			statement.setInt(1, getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					setRecomHave(rset.getInt("rec_have"));
					setRecomLeft(rset.getInt("rec_left"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not restore Recommendations for player: " + getObjectId(), e);
		}
	}
	
	/**
	 * Update PlayerInstance Recommendations data.
	 */
	public void storeRecommendations()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("REPLACE INTO character_reco_bonus (charId,rec_have,rec_left,time_left) VALUES (?,?,?,?)"))
		{
			ps.setInt(1, getObjectId());
			ps.setInt(2, _recomHave);
			ps.setInt(3, _recomLeft);
			ps.setLong(4, 0);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Could not update Recommendations for player: " + getObjectId(), e);
		}
	}
	
	public void startRecoGiveTask()
	{
		// Create task to give new recommendations
		_recoGiveTask = ThreadPool.scheduleAtFixedRate(new RecoGiveTask(this), 7200000, 3600000);
		
		// Store new data
		storeRecommendations();
	}
	
	public void stopRecoGiveTask()
	{
		if (_recoGiveTask != null)
		{
			_recoGiveTask.cancel(false);
			_recoGiveTask = null;
		}
	}
	
	public boolean isRecoTwoHoursGiven()
	{
		return _recoTwoHoursGiven;
	}
	
	public void setRecoTwoHoursGiven(boolean val)
	{
		_recoTwoHoursGiven = val;
	}
	
	public void setPremiumStatus(boolean premiumStatus)
	{
		_premiumStatus = premiumStatus;
		sendPacket(new ExBrPremiumState(this));
	}
	
	public boolean hasPremiumStatus()
	{
		return Config.PREMIUM_SYSTEM_ENABLED && _premiumStatus;
	}
	
	public void setLastPetitionGmName(String gmName)
	{
		_lastPetitionGmName = gmName;
	}
	
	public String getLastPetitionGmName()
	{
		return _lastPetitionGmName;
	}
	
	public ContactList getContactList()
	{
		return _contactList;
	}
	
	public void setEventStatus()
	{
		eventStatus = new PlayerEventHolder(this);
	}
	
	public void setEventStatus(PlayerEventHolder pes)
	{
		eventStatus = pes;
	}
	
	public PlayerEventHolder getEventStatus()
	{
		return eventStatus;
	}
	
	public long getNotMoveUntil()
	{
		return _notMoveUntil;
	}
	
	public void updateNotMoveUntil()
	{
		_notMoveUntil = System.currentTimeMillis() + Config.PLAYER_MOVEMENT_BLOCK_TIME;
	}
	
	@Override
	public boolean isPlayer()
	{
		return true;
	}
	
	/**
	 * @param skillId the display skill Id
	 * @return the custom skill
	 */
	public Skill getCustomSkill(int skillId)
	{
		return (_customSkills != null) ? _customSkills.get(skillId) : null;
	}
	
	/**
	 * Add a skill level to the custom skills map.
	 * @param skill the skill to add
	 */
	private void addCustomSkill(Skill skill)
	{
		if ((skill != null) && (skill.getDisplayId() != skill.getId()))
		{
			if (_customSkills == null)
			{
				_customSkills = new ConcurrentSkipListMap<>();
			}
			_customSkills.put(skill.getDisplayId(), skill);
		}
	}
	
	/**
	 * Remove a skill level from the custom skill map.
	 * @param skill the skill to remove
	 */
	private void removeCustomSkill(Skill skill)
	{
		if ((skill != null) && (_customSkills != null) && (skill.getDisplayId() != skill.getId()))
		{
			_customSkills.remove(skill.getDisplayId());
		}
	}
	
	/**
	 * @return {@code true} if current player can revive and shows 'To Village' button upon death, {@code false} otherwise.
	 */
	@Override
	public boolean canRevive()
	{
		for (AbstractEvent<?> listener : _events.values())
		{
			if (listener.isOnEvent(this) && !listener.canRevive(this))
			{
				return false;
			}
		}
		return _canRevive;
	}
	
	/**
	 * This method can prevent from displaying 'To Village' button upon death.
	 * @param val
	 */
	@Override
	public void setCanRevive(boolean val)
	{
		_canRevive = val;
	}
	
	public boolean isOnCustomEvent()
	{
		return _isOnCustomEvent;
	}
	
	public void setOnCustomEvent(boolean value)
	{
		_isOnCustomEvent = value;
	}
	
	/**
	 * @return {@code true} if player is on event, {@code false} otherwise.
	 */
	@Override
	public boolean isOnEvent()
	{
		if (_isOnCustomEvent)
		{
			return true;
		}
		for (AbstractEvent<?> listener : _events.values())
		{
			if (listener.isOnEvent(this))
			{
				return true;
			}
		}
		return super.isOnEvent();
	}
	
	public boolean isBlockedFromExit()
	{
		if (_isOnCustomEvent)
		{
			return true;
		}
		for (AbstractEvent<?> listener : _events.values())
		{
			if (listener.isOnEvent(this) && listener.isBlockingExit(this))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isBlockedFromDeathPenalty()
	{
		if (_isOnCustomEvent)
		{
			return true;
		}
		for (AbstractEvent<?> listener : _events.values())
		{
			if (listener.isOnEvent(this) && listener.isBlockingDeathPenalty(this))
			{
				return true;
			}
		}
		return isAffected(EffectFlag.PROTECT_DEATH_PENALTY);
	}
	
	public void setOriginalCpHpMp(double cp, double hp, double mp)
	{
		_originalCp = cp;
		_originalHp = hp;
		_originalMp = mp;
	}
	
	@Override
	public void addOverrideCond(PlayerCondOverride... excs)
	{
		super.addOverrideCond(excs);
		getVariables().set(COND_OVERRIDE_KEY, Long.toString(_exceptions));
	}
	
	@Override
	public void removeOverridedCond(PlayerCondOverride... excs)
	{
		super.removeOverridedCond(excs);
		getVariables().set(COND_OVERRIDE_KEY, Long.toString(_exceptions));
	}
	
	/**
	 * @return {@code true} if {@link PlayerVariables} instance is attached to current player's scripts, {@code false} otherwise.
	 */
	public boolean hasVariables()
	{
		return getScript(PlayerVariables.class) != null;
	}
	
	/**
	 * @return {@link PlayerVariables} instance containing parameters regarding player.
	 */
	public PlayerVariables getVariables()
	{
		final PlayerVariables vars = getScript(PlayerVariables.class);
		return vars != null ? vars : addScript(new PlayerVariables(getObjectId()));
	}
	
	/**
	 * @return {@code true} if {@link AccountVariables} instance is attached to current player's scripts, {@code false} otherwise.
	 */
	public boolean hasAccountVariables()
	{
		return getScript(AccountVariables.class) != null;
	}
	
	/**
	 * @return {@link AccountVariables} instance containing parameters regarding player.
	 */
	public AccountVariables getAccountVariables()
	{
		final AccountVariables vars = getScript(AccountVariables.class);
		return vars != null ? vars : addScript(new AccountVariables(getAccountName()));
	}
	
	@Override
	public int getId()
	{
		return getClassId().getId();
	}
	
	public boolean isPartyBanned()
	{
		return PunishmentManager.getInstance().hasPunishment(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN);
	}
	
	/**
	 * @param act
	 * @return {@code true} if action was added successfully, {@code false} otherwise.
	 */
	public boolean addAction(PlayerAction act)
	{
		if (!hasAction(act))
		{
			_actionMask |= act.getMask();
			return true;
		}
		return false;
	}
	
	/**
	 * @param act
	 * @return {@code true} if action was removed successfully, {@code false} otherwise.
	 */
	public boolean removeAction(PlayerAction act)
	{
		if (hasAction(act))
		{
			_actionMask &= ~act.getMask();
			return true;
		}
		return false;
	}
	
	/**
	 * @param act
	 * @return {@code true} if action is present, {@code false} otherwise.
	 */
	public boolean hasAction(PlayerAction act)
	{
		return (_actionMask & act.getMask()) == act.getMask();
	}
	
	/**
	 * Set true/false if character got Charm of Courage
	 * @param val true/false
	 */
	public void setCharmOfCourage(boolean val)
	{
		_hasCharmOfCourage = val;
	}
	
	/**
	 * @return {@code true} if effect is present, {@code false} otherwise.
	 */
	public boolean hasCharmOfCourage()
	{
		return _hasCharmOfCourage;
		
	}
	
	public boolean isGood()
	{
		return _isGood;
	}
	
	public boolean isEvil()
	{
		return _isEvil;
	}
	
	public void setGood()
	{
		_isGood = true;
		_isEvil = false;
	}
	
	public void setEvil()
	{
		_isGood = false;
		_isEvil = true;
	}
	
	/**
	 * @param target the target
	 * @return {@code true} if this player got war with the target, {@code false} otherwise.
	 */
	public boolean atWarWith(Playable target)
	{
		if (target == null)
		{
			return false;
		}
		if ((_clan != null) && !isAcademyMember()) // Current player
		{
			if ((target.getClan() != null) && !target.isAcademyMember()) // Target player
			{
				return _clan.isAtWarWith(target.getClan());
			}
		}
		return false;
	}
	
	/**
	 * Sets the beauty shop hair
	 * @param hairId
	 */
	public void setVisualHair(int hairId)
	{
		getVariables().set("visualHairId", hairId);
	}
	
	/**
	 * Sets the beauty shop hair color
	 * @param colorId
	 */
	public void setVisualHairColor(int colorId)
	{
		getVariables().set("visualHairColorId", colorId);
	}
	
	/**
	 * Sets the beauty shop modified face
	 * @param faceId
	 */
	public void setVisualFace(int faceId)
	{
		getVariables().set("visualFaceId", faceId);
	}
	
	/**
	 * @return the beauty shop hair, or his normal if not changed.
	 */
	public int getVisualHair()
	{
		return getVariables().getInt("visualHairId", _appearance.getHairStyle());
	}
	
	/**
	 * @return the beauty shop hair color, or his normal if not changed.
	 */
	public int getVisualHairColor()
	{
		return getVariables().getInt("visualHairColorId", _appearance.getHairColor());
	}
	
	/**
	 * @return the beauty shop modified face, or his normal if not changed.
	 */
	public int getVisualFace()
	{
		return getVariables().getInt("visualFaceId", _appearance.getFace());
	}
	
	/**
	 * @return {@code true} if player has mentees, {@code false} otherwise
	 */
	public boolean isMentor()
	{
		return MentorManager.getInstance().isMentor(getObjectId());
	}
	
	/**
	 * @return {@code true} if player has mentor, {@code false} otherwise
	 */
	public boolean isMentee()
	{
		return MentorManager.getInstance().isMentee(getObjectId());
	}
	
	/**
	 * @return the amount of ability points player can spend on learning skills.
	 */
	public int getAbilityPoints()
	{
		// Grand Crusade: 1 point per level after 84
		return Math.max(0, getLevel() - 84);
	}
	
	/**
	 * @return how much ability points player has spend on learning skills.
	 */
	public int getAbilityPointsUsed()
	{
		return getVariables().getInt(isDualClassActive() ? PlayerVariables.ABILITY_POINTS_USED_DUAL_CLASS : PlayerVariables.ABILITY_POINTS_USED_MAIN_CLASS, 0);
	}
	
	/**
	 * Sets how much ability points player has spend on learning skills.
	 * @param points
	 */
	public void setAbilityPointsUsed(int points)
	{
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerAbilityPointsChanged(this, getAbilityPointsUsed(), points), this);
		getVariables().set(isDualClassActive() ? PlayerVariables.ABILITY_POINTS_USED_DUAL_CLASS : PlayerVariables.ABILITY_POINTS_USED_MAIN_CLASS, points);
	}
	
	/**
	 * @return The amount of times player can use world chat
	 */
	public int getWorldChatPoints()
	{
		return (int) getStat().getValue(Stats.WORLD_CHAT_POINTS, Config.WORLD_CHAT_POINTS_PER_DAY);
	}
	
	/**
	 * @return The amount of times player has used world chat
	 */
	public int getWorldChatUsed()
	{
		return getVariables().getInt(PlayerVariables.WORLD_CHAT_VARIABLE_NAME, 0);
	}
	
	/**
	 * Sets the amount of times player can use world chat
	 * @param timesUsed how many times world chat has been used up until now.
	 */
	public void setWorldChatUsed(int timesUsed)
	{
		getVariables().set(PlayerVariables.WORLD_CHAT_VARIABLE_NAME, timesUsed);
	}
	
	public void prohibiteCeremonyOfChaos()
	{
		if (!PunishmentManager.getInstance().hasPunishment(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.COC_BAN))
		{
			PunishmentManager.getInstance().startPunishment(new PunishmentTask(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.COC_BAN, 0, "", getClass().getSimpleName()));
			final int penalties = getVariables().getInt(PlayerVariables.CEREMONY_OF_CHAOS_PROHIBITED_PENALTIES, 0);
			getVariables().set(PlayerVariables.CEREMONY_OF_CHAOS_PROHIBITED_PENALTIES, penalties + 1);
		}
	}
	
	public boolean isCeremonyOfChaosProhibited()
	{
		return PunishmentManager.getInstance().hasPunishment(getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.COC_BAN) || (getVariables().getInt(PlayerVariables.CEREMONY_OF_CHAOS_PROHIBITED_PENALTIES, 0) >= 30);
	}
	
	/**
	 * @return Side of the player.
	 */
	public CastleSide getPlayerSide()
	{
		if ((_clan == null) || (_clan.getCastleId() == 0))
		{
			return CastleSide.NEUTRAL;
		}
		return CastleManager.getInstance().getCastleById(getClan().getCastleId()).getSide();
	}
	
	/**
	 * @return {@code true} if player is on Dark side, {@code false} otherwise.
	 */
	public boolean isOnDarkSide()
	{
		return getPlayerSide() == CastleSide.DARK;
	}
	
	/**
	 * @return {@code true} if player is on Light side, {@code false} otherwise.
	 */
	public boolean isOnLightSide()
	{
		return getPlayerSide() == CastleSide.LIGHT;
	}
	
	/**
	 * @return the maximum amount of points that player can use
	 */
	public int getMaxSummonPoints()
	{
		return (int) getStat().getValue(Stats.MAX_SUMMON_POINTS, 0);
	}
	
	/**
	 * @return the amount of points that player used
	 */
	public int getSummonPoints()
	{
		return getServitors().values().stream().mapToInt(Summon::getSummonPoints).sum();
	}
	
	/**
	 * @param request
	 * @return {@code true} if the request was registered successfully, {@code false} otherwise.
	 */
	public boolean addRequest(AbstractRequest request)
	{
		return canRequest(request) && (_requests.putIfAbsent(request.getClass(), request) == null);
	}
	
	public boolean canRequest(AbstractRequest request)
	{
		return _requests.values().stream().allMatch(request::canWorkWith);
	}
	
	/**
	 * @param clazz
	 * @return {@code true} if request was successfully removed, {@code false} in case processing set is not created or not containing the request.
	 */
	public boolean removeRequest(Class<? extends AbstractRequest> clazz)
	{
		return _requests.remove(clazz) != null;
	}
	
	/**
	 * @param <T>
	 * @param requestClass
	 * @return object that is instance of {@code requestClass} param, {@code null} if not instance or not set.
	 */
	public <T extends AbstractRequest> T getRequest(Class<T> requestClass)
	{
		return requestClass.cast(_requests.get(requestClass));
	}
	
	/**
	 * @return {@code true} if player has any processing request set, {@code false} otherwise.
	 */
	public boolean hasRequests()
	{
		return !_requests.isEmpty();
	}
	
	public boolean hasItemRequest()
	{
		return _requests.values().stream().anyMatch(AbstractRequest::isItemRequest);
	}
	
	/**
	 * @param requestClass
	 * @param classes
	 * @return {@code true} if player has the provided request and processing it, {@code false} otherwise.
	 */
	@SafeVarargs
	public final boolean hasRequest(Class<? extends AbstractRequest> requestClass, Class<? extends AbstractRequest>... classes)
	{
		for (Class<? extends AbstractRequest> clazz : classes)
		{
			if (_requests.containsKey(clazz))
			{
				return true;
			}
		}
		return _requests.containsKey(requestClass);
	}
	
	/**
	 * @param objectId
	 * @return {@code true} if item object id is currently in use by some request, {@code false} otherwise.
	 */
	public boolean isProcessingItem(int objectId)
	{
		return _requests.values().stream().anyMatch(req -> req.isUsing(objectId));
	}
	
	/**
	 * Removing all requests associated with the item object id provided.
	 * @param objectId
	 */
	public void removeRequestsThatProcessesItem(int objectId)
	{
		_requests.values().removeIf(req -> req.isUsing(objectId));
	}
	
	/**
	 * @return the prime shop points of the player.
	 */
	public int getPrimePoints()
	{
		return getAccountVariables().getInt("PRIME_POINTS", 0);
	}
	
	/**
	 * Sets prime shop for current player.
	 * @param points
	 */
	public void setPrimePoints(int points)
	{
		// Immediate store upon change
		final AccountVariables vars = getAccountVariables();
		vars.set("PRIME_POINTS", Math.max(points, 0));
		vars.storeMe();
	}
	
	/**
	 * Gets the last commission infos.
	 * @return the last commission infos
	 */
	public Map<Integer, ExResponseCommissionInfo> getLastCommissionInfos()
	{
		return _lastCommissionInfos;
	}
	
	/**
	 * Gets the whisperers.
	 * @return the whisperers
	 */
	public Set<Integer> getWhisperers()
	{
		return _whisperers;
	}
	
	public MatchingRoom getMatchingRoom()
	{
		return _matchingRoom;
	}
	
	public void setMatchingRoom(MatchingRoom matchingRoom)
	{
		_matchingRoom = matchingRoom;
	}
	
	public boolean isInMatchingRoom()
	{
		return _matchingRoom != null;
	}
	
	public int getVitalityItemsUsed()
	{
		return getVariables().getInt(PlayerVariables.VITALITY_ITEMS_USED_VARIABLE_NAME, 0);
	}
	
	public void setVitalityItemsUsed(int used)
	{
		final PlayerVariables vars = getVariables();
		vars.set(PlayerVariables.VITALITY_ITEMS_USED_VARIABLE_NAME, used);
		vars.storeMe();
	}
	
	@Override
	public boolean isVisibleFor(PlayerInstance player)
	{
		return (super.isVisibleFor(player) || ((player.getParty() != null) && (player.getParty() == getParty())));
	}
	
	/**
	 * Set the Quest zone ID.
	 * @param id the quest zone ID
	 */
	public void setQuestZoneId(int id)
	{
		_questZoneId = id;
	}
	
	/**
	 * Gets the Quest zone ID.
	 * @return int the quest zone ID
	 */
	public int getQuestZoneId()
	{
		return _questZoneId;
	}
	
	/**
	 * @param iu
	 */
	public void sendInventoryUpdate(InventoryUpdate iu)
	{
		sendPacket(iu);
		sendPacket(new ExAdenaInvenCount(this));
		sendPacket(new ExUserInfoInvenWeight(this));
	}
	
	public void sendItemList()
	{
		sendPacket(new ItemList(1, this));
		sendPacket(new ItemList(2, this));
		sendPacket(new ExQuestItemList(1, this));
		sendPacket(new ExQuestItemList(2, this));
		sendPacket(new ExAdenaInvenCount(this));
		sendPacket(new ExUserInfoInvenWeight(this));
	}
	
	/**
	 * @param event
	 * @return {@code true} if event is successfuly registered, {@code false} in case events map is not initialized yet or event is not registered
	 */
	public boolean registerOnEvent(AbstractEvent<?> event)
	{
		return _events.putIfAbsent(event.getClass(), event) == null;
	}
	
	/**
	 * @param event
	 * @return {@code true} if event is successfuly removed, {@code false} in case events map is not initialized yet or event is not registered
	 */
	public boolean removeFromEvent(AbstractEvent<?> event)
	{
		return _events.remove(event.getClass()) != null;
	}
	
	/**
	 * @param <T>
	 * @param clazz
	 * @return the event instance or null in case events map is not initialized yet or event is not registered
	 */
	public <T extends AbstractEvent<?>> T getEvent(Class<T> clazz)
	{
		return _events.values().stream().filter(event -> clazz.isAssignableFrom(event.getClass())).map(clazz::cast).findFirst().orElse(null);
	}
	
	/**
	 * @return the first event that player participates on or null if he doesn't
	 */
	public AbstractEvent<?> getEvent()
	{
		return _events.values().stream().findFirst().orElse(null);
	}
	
	/**
	 * @param clazz
	 * @return {@code true} if player is registered on specified event, {@code false} in case events map is not initialized yet or event is not registered
	 */
	public boolean isOnEvent(Class<? extends AbstractEvent<?>> clazz)
	{
		return _events.containsKey(clazz);
	}
	
	public Fishing getFishing()
	{
		return _fishing;
	}
	
	public boolean isFishing()
	{
		return _fishing.isFishing();
	}
	
	@Override
	public MoveType getMoveType()
	{
		if (_waitTypeSitting)
		{
			return MoveType.SITTING;
		}
		return super.getMoveType();
	}
	
	public GroupType getGroupType()
	{
		return isInParty() ? (_party.isInCommandChannel() ? GroupType.COMMAND_CHANNEL : GroupType.PARTY) : GroupType.NONE;
	}
	
	public boolean isTrueHero()
	{
		return _trueHero;
	}
	
	public void setTrueHero(boolean val)
	{
		_trueHero = val;
	}
	
	public int getFactionPoints(Faction faction)
	{
		return getVariables().getInt(faction.toString(), 0);
	}
	
	public int getFactionLevel(Faction faction)
	{
		final int currentPoints = getFactionPoints(faction);
		for (int i = 0; i < faction.getLevelCount(); i++)
		{
			if (currentPoints <= faction.getPointsOfLevel(i))
			{
				return i;
			}
		}
		return 0;
	}
	
	public float getFactionProgress(Faction faction)
	{
		final int currentLevel = getFactionLevel(faction);
		final int currentLevelPoints = getFactionPoints(faction);
		final int previousLevelPoints = faction.getPointsOfLevel(currentLevel - 1);
		final int nextLevelPoints = faction.getPointsOfLevel(currentLevel + 1);
		return (float) (currentLevelPoints - previousLevelPoints) / (nextLevelPoints - previousLevelPoints);
	}
	
	public void addFactionPoints(Faction faction, int count)
	{
		final int currentPoints = getFactionPoints(faction);
		final int oldLevel = getFactionLevel(faction);
		if ((currentPoints + count) < faction.getPointsOfLevel(faction.getLevelCount() - 1))
		{
			getVariables().set(faction.toString(), currentPoints + count);
			sendPacket(new SystemMessage(SystemMessageId.YOU_OBTAINED_S1_FACTION_POINTS_FOR_S2).addInt(count).addFactionName(faction.getId()));
		}
		else
		{
			getVariables().set(faction.toString(), faction.getPointsOfLevel(faction.getLevelCount() - 1));
			
		}
		if (oldLevel < getFactionLevel(faction))
		{
			sendPacket(new SystemMessage(SystemMessageId.THE_AMITY_LEVEL_OF_S1_HAS_INCREASED_OPEN_THE_FACTIONS_WINDOW_TO_CHECK).addFactionName(faction.getId()));
		}
	}
	
	public int getMonsterBookKillCount(int cardId)
	{
		return getVariables().getInt(MONSTER_BOOK_KILLS_VAR + cardId, 0);
	}
	
	public int getMonsterBookRewardLevel(int cardId)
	{
		return getVariables().getInt(MONSTER_BOOK_LEVEL_VAR + cardId, 0);
	}
	
	public void updateMonsterBook(MonsterBookCardHolder card)
	{
		final int killCount = getMonsterBookKillCount(card.getId());
		if (killCount < card.getReward(3).getKills()) // no point adding kills when player has reached max
		{
			getVariables().set(MONSTER_BOOK_KILLS_VAR + card.getId(), killCount + 1);
			sendPacket(new ExMonsterBookCloseForce()); // in case it is open
			final int rewardLevel = getMonsterBookRewardLevel(card.getId());
			if ((getMonsterBookKillCount(card.getId()) >= card.getReward(rewardLevel).getKills()) && (rewardLevel < 4)) // make sure player can be rewarded
			{
				sendPacket(new ExMonsterBookRewardIcon());
			}
		}
	}
	
	public void rewardMonsterBook(int cardId)
	{
		final int rewardLevel = getMonsterBookRewardLevel(cardId);
		final MonsterBookCardHolder card = MonsterBookData.getInstance().getMonsterBookCardById(cardId);
		final MonsterBookRewardHolder reward = card.getReward(rewardLevel);
		if ((getMonsterBookKillCount(cardId) >= reward.getKills()) && (rewardLevel < 4)) // make sure player can be rewarded
		{
			getVariables().set(MONSTER_BOOK_LEVEL_VAR + cardId, rewardLevel + 1);
			addExpAndSp(reward.getExp(), reward.getSp());
			addFactionPoints(card.getFaction(), reward.getPoints());
			sendPacket(new ExMonsterBook(this));
		}
	}
	
	@Override
	protected void initStatusUpdateCache()
	{
		super.initStatusUpdateCache();
		addStatusUpdateValue(StatusUpdateType.LEVEL);
		addStatusUpdateValue(StatusUpdateType.MAX_CP);
		addStatusUpdateValue(StatusUpdateType.CUR_CP);
	}
	
	public boolean tryLuck()
	{
		if (((Rnd.nextDouble() * 100) < (BaseStats.LUC.getValue(getLUC()) * Config.LUCKY_CHANCE_MULTIPLIER)) && !hasSkillReuse(CommonSkill.LUCKY_CLOVER.getSkill().getReuseHashCode()))
		{
			SkillCaster.triggerCast(this, this, CommonSkill.LUCKY_CLOVER.getSkill());
			sendPacket(SystemMessageId.LADY_LUCK_SMILES_UPON_YOU);
			return true;
		}
		return false;
	}
	
	public TrainingHolder getTraingCampInfo()
	{
		final String info = getAccountVariables().getString(TRAINING_CAMP_VAR, null);
		if (info == null)
		{
			return null;
		}
		return new TrainingHolder(Integer.parseInt(info.split(";")[0]), Integer.parseInt(info.split(";")[1]), Integer.parseInt(info.split(";")[2]), Long.parseLong(info.split(";")[3]), Long.parseLong(info.split(";")[4]));
	}
	
	public void setTraingCampInfo(TrainingHolder holder)
	{
		getAccountVariables().set(TRAINING_CAMP_VAR, holder.getObjectId() + ";" + holder.getClassIndex() + ";" + holder.getLevel() + ";" + holder.getStartTime() + ";" + holder.getEndTime());
	}
	
	public void removeTraingCampInfo()
	{
		getAccountVariables().remove(TRAINING_CAMP_VAR);
	}
	
	public long getTraingCampDuration()
	{
		return getAccountVariables().getLong(TRAINING_CAMP_DURATION, 0);
	}
	
	public void setTraingCampDuration(long duration)
	{
		getAccountVariables().set(TRAINING_CAMP_DURATION, duration);
	}
	
	public void resetTraingCampDuration()
	{
		getAccountVariables().remove(TRAINING_CAMP_DURATION);
	}
	
	public boolean isInTraingCamp()
	{
		final TrainingHolder trainingHolder = getTraingCampInfo();
		return (trainingHolder != null) && (trainingHolder.getEndTime() > 0);
	}
	
	public AttendanceInfoHolder getAttendanceInfo()
	{
		// Get reset time.
		final Calendar calendar = Calendar.getInstance();
		if ((calendar.get(Calendar.HOUR_OF_DAY) < 6) && (calendar.get(Calendar.MINUTE) < 30))
		{
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 6);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		// Get last player reward time.
		final long receiveDate;
		int rewardIndex;
		if (Config.ATTENDANCE_REWARDS_SHARE_ACCOUNT)
		{
			receiveDate = getAccountVariables().getLong(ATTENDANCE_DATE_VAR, 0);
			rewardIndex = getAccountVariables().getInt(ATTENDANCE_INDEX_VAR, 0);
		}
		else
		{
			receiveDate = getVariables().getLong(ATTENDANCE_DATE_VAR, 0);
			rewardIndex = getVariables().getInt(ATTENDANCE_INDEX_VAR, 0);
		}
		
		// Check if player can receive reward today.
		boolean canBeRewarded = false;
		if (calendar.getTimeInMillis() > receiveDate)
		{
			canBeRewarded = true;
			// Reset index if max is reached.
			if (rewardIndex >= AttendanceRewardData.getInstance().getRewardsCount())
			{
				rewardIndex = 0;
			}
		}
		
		return new AttendanceInfoHolder(rewardIndex, canBeRewarded);
	}
	
	public void setAttendanceInfo(int rewardIndex)
	{
		// At 6:30 next day, another reward may be taken.
		final Calendar nextReward = Calendar.getInstance();
		nextReward.set(Calendar.MINUTE, 30);
		if (nextReward.get(Calendar.HOUR_OF_DAY) >= 6)
		{
			nextReward.add(Calendar.DATE, 1);
		}
		nextReward.set(Calendar.HOUR_OF_DAY, 6);
		
		if (Config.ATTENDANCE_REWARDS_SHARE_ACCOUNT)
		{
			getAccountVariables().set(ATTENDANCE_DATE_VAR, nextReward.getTimeInMillis());
			getAccountVariables().set(ATTENDANCE_INDEX_VAR, rewardIndex);
		}
		else
		{
			getVariables().set(ATTENDANCE_DATE_VAR, nextReward.getTimeInMillis());
			getVariables().set(ATTENDANCE_INDEX_VAR, rewardIndex);
		}
	}
}
