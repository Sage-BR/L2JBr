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
package events.SavingSanta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.LongTimeEvent;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.ActionFailed;
import org.l2jbr.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;
import org.l2jbr.gameserver.network.serverpackets.SocialAction;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.util.Broadcast;
import org.l2jbr.gameserver.util.Util;

/**
 * Christmas Event: Saving Santa<br>
 * http://legacy.lineage2.com/archive/2008/12/saving_santa_ev.html<br>
 * TODO:<br>
 * 1) Heading for Santa's Helpers.<br>
 * 2) Unhardcode HTMLs.<br>
 * @author Zoey76, Mobius
 */
public class SavingSanta extends LongTimeEvent
{
	private boolean _isSantaFree = true;
	private boolean _isJackPot = false;
	private boolean _isWaitingForPlayerSkill = false;
	private static final List<Npc> _santaHelpers = new ArrayList<>();
	private static final List<Npc> _specialTrees = new ArrayList<>();
	private static final Map<String, Long> _rewardedPlayers = new ConcurrentHashMap<>();
	private static final Map<String, Long> _blessedPlayers = new ConcurrentHashMap<>();
	
	// Is Saving Santa event used?
	private static boolean SAVING_SANTA = true;
	// Use Santa's Helpers Auto Buff?
	private static boolean SANTAS_HELPER_AUTOBUFF = false;
	
	private static final ItemHolder[] TREE_REQUIRED_ITEMS =
	{
		new ItemHolder(5556, 4),
		new ItemHolder(5557, 4),
		new ItemHolder(5558, 10),
		new ItemHolder(5559, 1)
	};
	
	//@formatter:off
	private static final int SANTA_TRAINEE_ID = 31863;
	private static final int SPECIAL_CHRISTMAS_TREE_ID = 13007;
	private static final int HOLIDAY_SANTA_ID = 104;
	private static final int HOLIDAY_SLED_ID = 105;
	private static final int THOMAS_D_TURKEY_ID = 13183;
	private static final long MIN_TIME_BETWEEN_2_REWARDS = 43200000;
	private static final long MIN_TIME_BETWEEN_2_BLESSINGS = 14400000;
	private static final int BR_XMAS_PRESENT_NORMAL = 20101;
	private static final int BR_XMAS_PRESENT_JACKPOT = 20102;
	private static final int BR_XMAS_WPN_TICKET_NORMAL = 20107;
	private static final int BR_XMAS_WPN_TICKET_JACKPOT = 20108;
	private static final int BR_XMAS_REWARD_BUFF = 23017;
	private static final int BR_XMAS_GAWIBAWIBO_CAP = 20100;
	private static final int X_MAS_TREE1 = 5560;
	private static final int X_MAS_TREE2 = 5561;
	private static final int SANTAS_HAT_ID = 7836;
	private static final int[] WEAPON_REWARDS = {14621, 14622, 14625, 14623, 14624, 14630, 14631, 14628, 14627, 14626, 14674, 14632, 14634, 14633};
	private static final Location THOMAS_SPAWN = new Location(117935, -126003, -2585, 54625);
	private static final int[] SANTA_MAGE_BUFFS = {7055, 7054, 7051};
	private static final int[] SANTA_FIGHTER_BUFFS = {7043, 7057, 7051};
	//@formatter:on
	
	private static final NpcStringId[] NPC_STRINGS =
	{
		NpcStringId.IT_S_HURTING_I_M_IN_PAIN_WHAT_CAN_I_DO_FOR_THE_PAIN,
		NpcStringId.NO_WHEN_I_LOSE_THAT_ONE_I_LL_BE_IN_MORE_PAIN,
		NpcStringId.HAHAHAH_I_CAPTURED_SANTA_THERE_WILL_BE_NO_GIFTS_THIS_YEAR,
		NpcStringId.NOW_WHY_DON_T_YOU_TAKE_UP_THE_CHALLENGE,
		NpcStringId.COME_ON_I_LL_TAKE_ALL_OF_YOU_ON,
		NpcStringId.HOW_ABOUT_IT_I_THINK_I_WON,
		NpcStringId.NOW_THOSE_OF_YOU_WHO_LOST_GO_AWAY,
		NpcStringId.WHAT_A_BUNCH_OF_LOSERS,
		NpcStringId.I_GUESS_YOU_CAME_TO_RESCUE_SANTA_BUT_YOU_PICKED_THE_WRONG_PERSON,
		NpcStringId.AH_OKAY,
		NpcStringId.AGH_I_WASN_T_GOING_TO_DO_THAT,
		NpcStringId.YOU_RE_CURSED_OH_WHAT,
		NpcStringId.STOP_IT_NO_MORE_I_DID_IT_BECAUSE_I_WAS_TOO_LONELY,
		NpcStringId.I_HAVE_TO_RELEASE_SANTA_HOW_INFURIATING,
		NpcStringId.I_HATE_HAPPY_HAPPY_HOLIDAYS,
		NpcStringId.OH_I_M_BORED,
		NpcStringId.SHALL_I_GO_TO_TAKE_A_LOOK_IF_SANTA_IS_STILL_THERE_HEHE,
		NpcStringId.OH_HO_HO_HAPPY_HOLIDAYS,
		NpcStringId.SANTA_COULD_GIVE_NICE_PRESENTS_ONLY_IF_HE_S_RELEASED_FROM_THE_TURKEY,
		NpcStringId.OH_HO_HO_OH_HO_HO_THANK_YOU_LADIES_AND_GENTLEMEN_I_WILL_REPAY_YOU_FOR_SURE,
		NpcStringId.HAPPY_HOLIDAYS_YOU_RE_DOING_A_GOOD_JOB,
		NpcStringId.HAPPY_HOLIDAYS_THANK_YOU_FOR_RESCUING_ME_FROM_THAT_WRETCHED_TURKEY,
		NpcStringId.S1_I_HAVE_PREPARED_A_GIFT_FOR_YOU,
		NpcStringId.I_HAVE_A_GIFT_FOR_S1,
		NpcStringId.TAKE_A_LOOK_AT_THE_INVENTORY_I_HOPE_YOU_LIKE_THE_GIFT_I_GAVE_YOU,
		NpcStringId.TAKE_A_LOOK_AT_THE_INVENTORY_PERHAPS_THERE_MIGHT_BE_A_BIG_PRESENT,
		NpcStringId.I_M_TIRED_OF_DEALING_WITH_YOU_I_M_LEAVING,
		NpcStringId.WHEN_ARE_YOU_GOING_TO_STOP_I_SLOWLY_STARTED_TO_BE_TIRED_OF_IT,
		NpcStringId.MESSAGE_FROM_SANTA_MANY_BLESSINGS_TO_S1_WHO_SAVED_ME
	};
	
	public SavingSanta()
	{
		if (!isEventPeriod())
		{
			return;
		}
		
		addStartNpc(SANTA_TRAINEE_ID);
		addFirstTalkId(SANTA_TRAINEE_ID);
		addTalkId(SANTA_TRAINEE_ID);
		addFirstTalkId(THOMAS_D_TURKEY_ID);
		addFirstTalkId(HOLIDAY_SANTA_ID);
		addFirstTalkId(HOLIDAY_SLED_ID);
		addSkillSeeId(THOMAS_D_TURKEY_ID);
		addSpellFinishedId(THOMAS_D_TURKEY_ID);
		addSpawnId(SPECIAL_CHRISTMAS_TREE_ID);
		
		startQuestTimer("SpecialTreeHeal", 5000, null, null);
		if (SAVING_SANTA)
		{
			startQuestTimer("ThomasQuest", 1000, null, null);
		}
		if (SANTAS_HELPER_AUTOBUFF)
		{
			startQuestTimer("SantaBlessings", 5000, null, null);
		}
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		_specialTrees.add(npc);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance caster, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		if (_isWaitingForPlayerSkill && (skill.getId() > 21013) && (skill.getId() < 21017))
		{
			caster.broadcastPacket(new MagicSkillUse(caster, caster, 23019, skill.getId() - 21013, 3000, 1));
			SkillData.getInstance().getSkill(23019, skill.getId() - 21013).applyEffects(caster, caster);
		}
		return null;
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		// Turkey's Choice
		// Level 1: Scissors
		// Level 2: Rock
		// Level 3: Paper
		if (skill.getId() == 6100)
		{
			_isWaitingForPlayerSkill = false;
			for (PlayerInstance pl : World.getInstance().getVisibleObjectsInRange(npc, PlayerInstance.class, 600))
			{
				// Level 1: Scissors
				// Level 2: Rock
				// Level 3: Paper
				if (!pl.isAffectedBySkill(23019))
				{
					continue;
				}
				
				int result = pl.getEffectList().getBuffInfoBySkillId(23019).getSkill().getLevel() - skill.getLevel();
				
				if (result == 0)
				{
					// Oh. I'm bored.
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[15]));
				}
				else if ((result == -1) || (result == 2))
				{
					// Now!! Those of you who lost, go away!
					// What a bunch of losers.
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[6 + getRandom(2)]));
					pl.broadcastPacket(new MagicSkillUse(pl, pl, 23023, 1, 3000, 1));
					pl.getEffectList().stopSkillEffects(true, 23022);
				}
				else if ((result == 1) || (result == -2))
				{
					int level = (pl.isAffectedBySkill(23022) ? (pl.getEffectList().getBuffInfoBySkillId(23022).getSkill().getLevel() + 1) : 1);
					pl.broadcastPacket(new MagicSkillUse(pl, pl, 23022, level, 3000, 1));
					SkillData.getInstance().getSkill(23022, level).applyEffects(pl, pl);
					
					if ((level == 1) || (level == 2))
					{
						// Ah, okay...
						// Agh!! I wasn't going to do that!
						// You're cursed!! Oh.. What?
						// Have you done nothing but rock-paper-scissors??
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[10 + getRandom(4)]));
					}
					else if (level == 3)
					{
						SkillData.getInstance().getSkill(23018, 1).applyEffects(pl, pl);
						// Stop it, no more... I did it because I was too lonely...
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[13]));
					}
					else if (level == 4)
					{
						Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.YOU_HAVE_DEFEATED_THOMAS_D_TURKEY_AND_RESCUED_SANTA));
						// I have to release Santa... How infuriating!!!
						// I hate happy Merry Christmas!!!
						npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[14 + getRandom(2)]));
						
						startQuestTimer("SantaSpawn", 120000, null, null);
						final Npc holidaySled = addSpawn(HOLIDAY_SLED_ID, 117935, -126003, -2585, 54625, false, 12000);
						// Message from Santa Claus: Many blessings to $s1, who saved me~
						final NpcSay santaSaved = new NpcSay(holidaySled.getObjectId(), ChatType.NPC_SHOUT, holidaySled.getId(), NPC_STRINGS[28]);
						santaSaved.addStringParameter(pl.getName());
						holidaySled.broadcastPacket(santaSaved);
						// Oh ho ho.... Merry Christmas!!
						holidaySled.broadcastPacket(new NpcSay(holidaySled.getObjectId(), ChatType.NPC_GENERAL, holidaySled.getId(), NPC_STRINGS[17]));
						
						if (getRandom(100) > 90)
						{
							_isJackPot = true;
							pl.addItem("SavingSantaPresent", BR_XMAS_PRESENT_JACKPOT, 1, pl, true);
						}
						else
						{
							pl.addItem("SavingSantaPresent", BR_XMAS_PRESENT_NORMAL, 1, pl, true);
						}
						
						ThreadPool.schedule(new SledAnimation(holidaySled), 7000);
						npc.decayMe();
						_isSantaFree = true;
						break;
					}
				}
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	private static class SledAnimation implements Runnable
	{
		private final Npc _sled;
		
		public SledAnimation(Npc sled)
		{
			_sled = sled;
		}
		
		@Override
		public void run()
		{
			try
			{
				_sled.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				_sled.broadcastPacket(new SocialAction(_sled.getObjectId(), 1));
			}
			catch (Exception e)
			{
				// Ignore.
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (!isEventPeriod())
		{
			return null;
		}
		
		String htmltext = null;
		if (event.equalsIgnoreCase("ThomasQuest"))
		{
			startQuestTimer("ThomasQuest", 14400000, null, null);
			Npc ThomasDTurkey = addSpawn(THOMAS_D_TURKEY_ID, THOMAS_SPAWN.getX(), THOMAS_SPAWN.getY(), THOMAS_SPAWN.getZ(), THOMAS_SPAWN.getHeading(), false, 1800000);
			
			Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.THOMAS_D_TURKEY_HAS_APPEARED_PLEASE_SAVE_SANTA));
			
			startQuestTimer("ThomasCast1", 15000, ThomasDTurkey, null);
			
			_isSantaFree = false;
		}
		else if (event.equalsIgnoreCase("SantaSpawn"))
		{
			if (_isSantaFree)
			{
				startQuestTimer("SantaSpawn", 120000, null, null);
				// for (PlayerInstance pl : L2World.getInstance().getAllPlayers().values())
				for (PlayerInstance pl : World.getInstance().getPlayers())
				{
					if ((pl != null) && pl.isOnline() && (pl.getLevel() >= 20) && pl.isInCombat() && !pl.isInsideZone(ZoneId.PEACE) && !pl.isFlyingMounted())
					{
						if (_rewardedPlayers.containsKey(pl.getAccountName()))
						{
							long elapsedTimeSinceLastRewarded = System.currentTimeMillis() - _rewardedPlayers.get(pl.getAccountName());
							if (elapsedTimeSinceLastRewarded < MIN_TIME_BETWEEN_2_REWARDS)
							{
								continue;
							}
						}
						else
						{
							final long time = player.getVariables().getLong("LAST_SANTA_REWARD", 0);
							if ((System.currentTimeMillis() - time) < MIN_TIME_BETWEEN_2_REWARDS)
							{
								_rewardedPlayers.put(pl.getAccountName(), time);
								continue;
							}
						}
						int locx = (int) (pl.getX() + (Math.pow(-1, getRandom(1, 2)) * 50));
						int locy = (int) (pl.getY() + (Math.pow(-1, getRandom(1, 2)) * 50));
						int heading = Util.calculateHeadingFrom(locx, locy, pl.getX(), pl.getY());
						Npc santa = addSpawn(HOLIDAY_SANTA_ID, locx, locy, pl.getZ(), heading, false, 30000);
						_rewardedPlayers.put(pl.getAccountName(), System.currentTimeMillis());
						player.getVariables().set("LAST_SANTA_REWARD", System.currentTimeMillis());
						startQuestTimer("SantaRewarding0", 500, santa, pl);
					}
				}
			}
		}
		else if (event.equalsIgnoreCase("ThomasCast1"))
		{
			if (!npc.isDecayed())
			{
				_isWaitingForPlayerSkill = true;
				startQuestTimer("ThomasCast2", 4000, npc, null);
				npc.doCast(SkillData.getInstance().getSkill(6116, 1));
				// It's hurting... I'm in pain... What can I do for the pain...
				// No... When I lose that one... I'll be in more pain...
				// Hahahah!!! I captured Santa Claus!! There will be no gifts this year!!!
				// Now! Why don't you take up the challenge?
				// Come on, I'll take all of you on!
				// How about it? I think I won?
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[getRandom(6)]));
			}
			else
			{
				if (!_isSantaFree)
				{
					Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.YOU_DID_NOT_RESCUE_SANTA_AND_THOMAS_D_TURKEY_HAS_DISAPPEARED));
					_isWaitingForPlayerSkill = false;
				}
			}
		}
		else if (event.equalsIgnoreCase("ThomasCast2"))
		{
			if (!npc.isDecayed())
			{
				startQuestTimer("ThomasCast1", 13000, npc, null);
				npc.doCast(SkillData.getInstance().getSkill(6100, getRandom(1, 3)));
				// It's hurting... I'm in pain... What can I do for the pain...
				// No... When I lose that one... I'll be in more pain...
				// Hahahah!!! I captured Santa Claus!! There will be no gifts this year!!!
				// Now! Why don't you take up the challenge?
				// Come on, I'll take all of you on!
				// How about it? I think I won?
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[getRandom(6)]));
			}
			else
			{
				if (!_isSantaFree)
				{
					Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.YOU_DID_NOT_RESCUE_SANTA_AND_THOMAS_D_TURKEY_HAS_DISAPPEARED));
					_isWaitingForPlayerSkill = false;
				}
			}
		}
		else if (event.equalsIgnoreCase("SantaRewarding0"))
		{
			startQuestTimer("SantaRewarding1", 9500, npc, player);
			npc.broadcastPacket(new SocialAction(npc.getObjectId(), 3));
		}
		else if (event.equalsIgnoreCase("SantaRewarding1"))
		{
			startQuestTimer("SantaRewarding2", 5000, npc, player);
			npc.broadcastPacket(new SocialAction(npc.getObjectId(), 1));
			// Merry Christmas~ Thank you for rescuing me from that wretched Turkey.
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[21]));
		}
		else if (event.equalsIgnoreCase("SantaRewarding2"))
		{
			startQuestTimer("SantaRewarding3", 5000, npc, player);
			// I have a gift for $s1.
			final NpcSay iHaveAGift = new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[23]);
			iHaveAGift.addStringParameter(player.getName());
			npc.broadcastPacket(iHaveAGift);
		}
		else if (event.equalsIgnoreCase("SantaRewarding3"))
		{
			npc.broadcastPacket(new SocialAction(npc.getObjectId(), 2));
			if (_isJackPot)
			{
				// Take a look at the inventory. Perhaps there might be a big present~
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[25]));
				player.addItem("SavingSantaPresent", BR_XMAS_PRESENT_JACKPOT, 1, player, true);
				_isJackPot = false;
			}
			else
			{
				// Take a look at the inventory. I hope you like the gift I gave you.
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[24]));
				player.addItem("SavingSantaPresent", BR_XMAS_PRESENT_NORMAL, 1, player, true);
			}
		}
		else if (event.equalsIgnoreCase("SantaBlessings") && SANTAS_HELPER_AUTOBUFF)
		{
			startQuestTimer("SantaBlessings", 15000, null, null);
			final long currentTime = System.currentTimeMillis();
			for (Npc santaHelper1 : _santaHelpers)
			{
				for (PlayerInstance plb : World.getInstance().getVisibleObjects(santaHelper1, PlayerInstance.class))
				{
					if ((plb.getLevel() >= 20) && !plb.isFlyingMounted())
					{
						if (_blessedPlayers.containsKey(plb.getAccountName()))
						{
							long elapsedTimeSinceLastBlessed = currentTime - _blessedPlayers.get(plb.getAccountName());
							if (elapsedTimeSinceLastBlessed < MIN_TIME_BETWEEN_2_BLESSINGS)
							{
								continue;
							}
						}
						else
						{
							final long time = player.getVariables().getLong("LAST_SANTA_BLESSING", 0);
							if ((currentTime - time) < MIN_TIME_BETWEEN_2_BLESSINGS)
							{
								_blessedPlayers.put(plb.getAccountName(), time);
								continue;
							}
						}
						for (Npc santaHelper : _santaHelpers)
						{
							for (PlayerInstance playerx : World.getInstance().getVisibleObjects(santaHelper, PlayerInstance.class))
							{
								if (playerx.getClassId().isMage())
								{
									for (int buffId : SANTA_MAGE_BUFFS)
									{
										if (!playerx.getEffectList().isAffectedBySkill(buffId))
										{
											playerx.broadcastPacket(new MagicSkillUse(santaHelper, playerx, buffId, 1, 2000, 1));
											SkillData.getInstance().getSkill(buffId, 1).applyEffects(playerx, playerx);
											_blessedPlayers.put(playerx.getAccountName(), currentTime);
											playerx.getVariables().set("LAST_SANTA_BLESSING", currentTime);
										}
									}
								}
								else
								{
									for (int buffId : SANTA_FIGHTER_BUFFS)
									{
										if (!playerx.getEffectList().isAffectedBySkill(buffId))
										{
											playerx.broadcastPacket(new MagicSkillUse(santaHelper, playerx, buffId, 1, 2000, 1));
											SkillData.getInstance().getSkill(buffId, 1).applyEffects(playerx, playerx);
											_blessedPlayers.put(playerx.getAccountName(), currentTime);
											playerx.getVariables().set("LAST_SANTA_BLESSING", currentTime);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		else if (event.equalsIgnoreCase("SpecialTreeHeal"))
		{
			startQuestTimer("SpecialTreeHeal", 9000, null, null);
			for (Npc tree : _specialTrees)
			{
				for (PlayerInstance playerr : World.getInstance().getVisibleObjects(tree, PlayerInstance.class))
				{
					int xxMin = tree.getX() - 60;
					int yyMin = tree.getY() - 60;
					int xxMax = tree.getX() + 60;
					int yyMax = tree.getY() + 60;
					int playerX = playerr.getX();
					int playerY = playerr.getY();
					
					if ((playerX > xxMin) && (playerX < xxMax) && (playerY > yyMin) && (playerY < yyMax))
					{
						SkillData.getInstance().getSkill(2139, 1).applyEffects(tree, playerr);
					}
				}
			}
		}
		else if (player != null)
		{
			// FIXME: Unhardcore html!
			if (event.equalsIgnoreCase("Tree"))
			{
				int itemsOk = 0;
				htmltext = "<html><title>Christmas Event</title><body><br><br><table width=260><tr><td></td><td width=40></td><td width=40></td></tr><tr><td><font color=LEVEL>Christmas Tree</font></td><td width=40><img src=\"Icon.etc_x_mas_tree_i00\" width=32 height=32></td><td width=40></td></tr></table><br><br><table width=260>";
				
				for (ItemHolder item : TREE_REQUIRED_ITEMS)
				{
					long pieceCount = player.getInventory().getInventoryItemCount(item.getId(), -1);
					if (pieceCount >= item.getCount())
					{
						itemsOk = itemsOk + 1;
						htmltext = htmltext + "<tr><td>" + ItemTable.getInstance().getTemplate(item.getId()).getName() + "</td><td width=40>" + pieceCount + "</td><td width=40><font color=0FF000>OK</font></td></tr>";
					}
					
					else
					{
						htmltext = htmltext + "<tr><td>" + ItemTable.getInstance().getTemplate(item.getId()).getName() + "</td><td width=40>" + pieceCount + "</td><td width=40><font color=8ae2ffb>NO</font></td></tr>";
					}
				}
				
				if (itemsOk == 4)
				{
					htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
					htmltext = htmltext + "<tr><td><center><button value=\"Get the tree\" action=\"bypass -h Quest SavingSanta buyTree\" width=110 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td></tr></table></body></html>";
				}
				else if (itemsOk < 4)
				{
					htmltext = htmltext + "</table><br><br>You do not have enough items.</center></body></html>";
				}
				return htmltext;
			}
			else if (event.equalsIgnoreCase("buyTree"))
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				for (ItemHolder item : TREE_REQUIRED_ITEMS)
				{
					if (player.getInventory().getInventoryItemCount(item.getId(), -1) < item.getCount())
					{
						player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
						return null;
					}
				}
				
				for (ItemHolder item : TREE_REQUIRED_ITEMS)
				{
					player.destroyItemByItemId(event, item.getId(), item.getCount(), player, true);
				}
				player.addItem(event, X_MAS_TREE1, 1, player, true);
			}
			else if (event.equalsIgnoreCase("SpecialTree"))
			{
				htmltext = "<html><title>Christmas Event</title><body><br><br><table width=260><tr><td></td><td width=40></td><td width=40></td></tr><tr><td><font color=LEVEL>Special Christmas Tree</font></td><td width=40><img src=\"Icon.etc_x_mas_tree_i00\" width=32 height=32></td><td width=40></td></tr></table><br><br><table width=260>";
				long pieceCount = player.getInventory().getInventoryItemCount(X_MAS_TREE1, -1);
				int itemsOk = 0;
				
				if (pieceCount >= 10)
				{
					itemsOk = 1;
					htmltext = htmltext + "<tr><td>Christmas Tree</td><td width=40>" + pieceCount + "</td><td width=40><font color=0FF000>OK</font></td></tr>";
				}
				else
				{
					htmltext = htmltext + "<tr><td>Christmas Tree</td><td width=40>" + pieceCount + "</td><td width=40><font color=8ae2ffb>NO</font></td></tr>";
				}
				
				if (itemsOk == 1)
				{
					htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
					htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
					htmltext = htmltext + "<tr><td><center><button value=\"Get the tree\" action=\"bypass -h Quest SavingSanta buySpecialTree\" width=110 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td></tr></table></body></html>";
				}
				else if (itemsOk == 0)
				{
					htmltext = htmltext + "</table><br><br>You do not have enough items.</center></body></html>";
				}
				
				return htmltext;
			}
			else if (event.equalsIgnoreCase("buySpecialTree"))
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				if (player.getInventory().getInventoryItemCount(X_MAS_TREE1, -1) < 10)
				{
					player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
					return null;
				}
				player.destroyItemByItemId(event, X_MAS_TREE1, 10, player, true);
				player.addItem(event, X_MAS_TREE2, 1, player, true);
			}
			else if (event.equalsIgnoreCase("SantaHat"))
			{
				htmltext = "<html><title>Christmas Event</title><body><br><br><table width=260><tr><td></td><td width=40></td><td width=40></td></tr><tr><td><font color=LEVEL>Santa's Hat</font></td><td width=40><img src=\"Icon.Accessory_santas_cap_i00\" width=32 height=32></td><td width=40></td></tr></table><br><br><table width=260>";
				long pieceCount = player.getInventory().getInventoryItemCount(X_MAS_TREE1, -1);
				int itemsOk = 0;
				
				if (pieceCount >= 10)
				{
					itemsOk = 1;
					htmltext = htmltext + "<tr><td>Christmas Tree</td><td width=40>" + pieceCount + "</td><td width=40><font color=0FF000>OK</font></td></tr>";
				}
				else
				{
					htmltext = htmltext + "<tr><td>Christmas Tree</td><td width=40>" + pieceCount + "</td><td width=40><font color=8ae2ffb>NO</font></td></tr>";
				}
				
				if (itemsOk == 1)
				{
					htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
					htmltext = htmltext + "<tr><td><center><button value=\"Get the hat\" action=\"bypass -h Quest SavingSanta buyHat\" width=110 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td></tr></table></body></html>";
				}
				else if (itemsOk == 0)
				{
					htmltext = htmltext + "</table><br><br>You do not have enough items.</center></body></html>";
				}
				return htmltext;
			}
			else if (event.equalsIgnoreCase("buyHat"))
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				if (player.getInventory().getInventoryItemCount(X_MAS_TREE1, -1) < 10)
				{
					player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
					return null;
				}
				player.destroyItemByItemId(event, X_MAS_TREE1, 10, player, true);
				player.addItem(event, SANTAS_HAT_ID, 1, player, true);
			}
			else if (event.equalsIgnoreCase("SavingSantaHat"))
			{
				htmltext = "<html><title>Christmas Event</title><body><br><br><table width=260><tr><td></td><td width=40></td><td width=40></td></tr><tr><td><font color=LEVEL>Saving Santa's Hat</font></td><td width=40><img src=\"Icon.Accessory_santas_cap_i00\" width=32 height=32></td><td width=40></td></tr></table><br><br><table width=260>";
				long pieceCount = player.getInventory().getAdena();
				int itemsOk = 0;
				
				if (pieceCount >= 50000)
				{
					itemsOk = 1;
					htmltext = htmltext + "<tr><td>Adena</td><td width=40>" + pieceCount + "</td><td width=40><font color=0FF000>OK</font></td></tr>";
				}
				
				else
				{
					htmltext = htmltext + "<tr><td>Adena</td><td width=40>" + pieceCount + "</td><td width=40><font color=8ae2ffb>NO</font></td></tr>";
				}
				
				if (itemsOk == 1)
				{
					htmltext = htmltext + "<tr><td><br></td><td width=40></td><td width=40></td></tr></table><table width=260>";
					htmltext = htmltext + "<tr><td><center><button value=\"Get the hat\" action=\"bypass -h Quest SavingSanta buySavingHat\" width=110 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td></tr></table></body></html>";
				}
				
				else if (itemsOk == 0)
				{
					htmltext = htmltext + "</table><br><br>You do not have enough Adena.</center></body></html>";
				}
				
				return htmltext;
			}
			else if (event.equalsIgnoreCase("buySavingHat"))
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				if (player.getInventory().getAdena() < 50000)
				{
					return null;
				}
				player.reduceAdena(event, 50000, player, true);
				player.addItem(event, BR_XMAS_GAWIBAWIBO_CAP, 1, player, true);
			}
			else if (event.equalsIgnoreCase("HolidayFestival"))
			{
				if (_isSantaFree)
				{
					npc.broadcastPacket(new MagicSkillUse(npc, player, BR_XMAS_REWARD_BUFF, 1, 2000, 1));
					SkillData.getInstance().getSkill(BR_XMAS_REWARD_BUFF, 1).applyEffects(player, player);
				}
				else
				{
					return "savingsanta-nobuff.htm";
				}
			}
			else if (event.equalsIgnoreCase("getWeapon"))
			{
				if ((player.getInventory().getInventoryItemCount(BR_XMAS_WPN_TICKET_NORMAL, -1) > 0) && (player.getInventory().getInventoryItemCount(BR_XMAS_WPN_TICKET_JACKPOT, -1) > 0))
				{
					return "savingsanta-noweapon.htm";
				}
				return "savingsanta-weapon.htm";
			}
			else if (event.startsWith("weapon_"))
			{
				final int itemId = Integer.parseInt(event.split("weapon_")[1]) - 1;
				if (player.getInventory().getInventoryItemCount(BR_XMAS_WPN_TICKET_JACKPOT, -1) > 0)
				{
					player.destroyItemByItemId(event, BR_XMAS_WPN_TICKET_JACKPOT, 1, player, true);
					player.addItem(event, WEAPON_REWARDS[itemId], 1, player, true).setEnchantLevel(10);
					player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT_2);
				}
				else if ((player.getInventory().getInventoryItemCount(BR_XMAS_WPN_TICKET_NORMAL, -1) > 0) /* || (player.getLevel() < 20) */)
				{
					player.destroyItemByItemId(event, BR_XMAS_WPN_TICKET_NORMAL, 1, player, true);
					player.addItem(event, WEAPON_REWARDS[itemId], 1, player, true).setEnchantLevel(getRandom(4, 16));
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		switch (npc.getId())
		{
			case THOMAS_D_TURKEY_ID:
			case HOLIDAY_SANTA_ID:
			case HOLIDAY_SLED_ID:
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return null;
			}
			case SANTA_TRAINEE_ID:
			{
				return SAVING_SANTA ? "savingsanta.htm" : "santa.htm";
			}
			default:
			{
				return null;
			}
		}
	}
	
	@Override
	public boolean unload()
	{
		for (Npc santaHelper : _santaHelpers)
		{
			santaHelper.deleteMe();
		}
		for (Npc specialTree : _specialTrees)
		{
			specialTree.deleteMe();
		}
		return super.unload();
	}
	
	@Override
	public String onAggroRangeEnter(Npc npc, PlayerInstance player, boolean isSummon)
	{
		// FIXME: Increase Thomas D. Turkey aggro rage.
		if (npc.getId() == THOMAS_D_TURKEY_ID)
		{
			// I guess you came to rescue Santa. But you picked the wrong person.
			npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NPC_STRINGS[8]));
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new SavingSanta();
	}
}