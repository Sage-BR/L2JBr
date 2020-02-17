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
package instances.SSQDisciplesNecropolisPast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.skills.SkillCaster;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;
import org.l2jbr.gameserver.util.Util;

import instances.AbstractInstance;
import quests.Q00196_SevenSignsSealOfTheEmperor.Q00196_SevenSignsSealOfTheEmperor;

/**
 * Disciple's Necropolis Past instance zone.
 * @author Adry_85
 */
public class SSQDisciplesNecropolisPast extends AbstractInstance
{
	// NPCs
	private static final int SEAL_DEVICE = 27384;
	private static final int PROMISE_OF_MAMMON = 32585;
	private static final int SHUNAIMAN = 32586;
	private static final int LEON = 32587;
	private static final int DISCIPLES_GATEKEEPER = 32657;
	// Monsters
	private static final int LILITH = 32715;
	private static final int ANAKIM = 32718;
	private static final int LILIM_BUTCHER = 27371;
	private static final int LILIM_MAGUS = 27372;
	private static final int LILIM_KNIGHT_ERRANT = 27373;
	private static final int SHILENS_EVIL_THOUGHTS1 = 27374;
	private static final int SHILENS_EVIL_THOUGHTS2 = 27375;
	private static final int LILIM_KNIGHT = 27376;
	private static final int LILIM_SLAYER = 27377;
	private static final int LILIM_GREAT_MAGUS = 27378;
	private static final int LILIM_GUARD_KNIGHT = 27379;
	private static final int[] ANAKIM_GROUP =
	{
		ANAKIM,
		32719,
		32720,
		32721
	};
	private static final int[] LILITH_GROUP =
	{
		LILITH,
		32716,
		32717
	};
	// Items
	private static final int SACRED_SWORD_OF_EINHASAD = 15310;
	private static final int SEAL_OF_BINDING = 13846;
	// Skills
	private static final SkillHolder SEAL_ISOLATION = new SkillHolder(5980, 3);
	private static final Map<Integer, SkillHolder> SKILLS = new HashMap<>();
	static
	{
		SKILLS.put(32715, new SkillHolder(6187, 1)); // Presentation - Lilith Battle
		SKILLS.put(32716, new SkillHolder(6188, 1)); // Presentation - Lilith's Steward Battle1
		SKILLS.put(32717, new SkillHolder(6190, 1)); // Presentation - Lilith's Bodyguards Battle1
		SKILLS.put(32718, new SkillHolder(6191, 1)); // Presentation - Anakim Battle
		SKILLS.put(32719, new SkillHolder(6192, 1)); // Presentation - Anakim's Guardian Battle1
		SKILLS.put(32720, new SkillHolder(6194, 1)); // Presentation - Anakim's Guard Battle
		SKILLS.put(32721, new SkillHolder(6195, 1)); // Presentation - Anakim's Executor Battle
	}
	// Locations
	private static final Location ENTER = new Location(-89554, 216078, -7488);
	// NpcStringId
	private static final NpcStringId[] LILITH_SHOUT =
	{
		NpcStringId.HOW_DARE_YOU_TRY_TO_CONTEND_AGAINST_ME_IN_STRENGTH_RIDICULOUS,
		NpcStringId.ANAKIM_IN_THE_NAME_OF_GREAT_SHILEN_I_WILL_CUT_YOUR_THROAT,
		NpcStringId.YOU_CANNOT_BE_THE_MATCH_OF_LILITH_I_LL_TEACH_YOU_A_LESSON
	};
	// Misc
	private static final int TEMPLATE_ID = 112;
	private static final int DOOR_1 = 17240102;
	private static final int DOOR_2 = 17240104;
	private static final int DOOR_3 = 17240106;
	private static final int DOOR_4 = 17240108;
	private static final int DOOR_5 = 17240110;
	private static final int DISCIPLES_NECROPOLIS_DOOR = 17240111;
	
	public SSQDisciplesNecropolisPast()
	{
		super(TEMPLATE_ID);
		addAttackId(SEAL_DEVICE);
		addFirstTalkId(SHUNAIMAN, LEON, DISCIPLES_GATEKEEPER);
		addKillId(LILIM_BUTCHER, LILIM_MAGUS, LILIM_KNIGHT_ERRANT, LILIM_KNIGHT, SHILENS_EVIL_THOUGHTS1, SHILENS_EVIL_THOUGHTS2, LILIM_SLAYER, LILIM_GREAT_MAGUS, LILIM_GUARD_KNIGHT);
		addAggroRangeEnterId(LILIM_BUTCHER, LILIM_MAGUS, LILIM_KNIGHT_ERRANT, LILIM_KNIGHT, SHILENS_EVIL_THOUGHTS1, SHILENS_EVIL_THOUGHTS2, LILIM_SLAYER, LILIM_GREAT_MAGUS, LILIM_GUARD_KNIGHT);
		addSpawnId(SEAL_DEVICE);
		addStartNpc(PROMISE_OF_MAMMON);
		addTalkId(PROMISE_OF_MAMMON, SHUNAIMAN, LEON, DISCIPLES_GATEKEEPER);
	}
	
	private void checkDoors(Npc npc, Instance world, int count)
	{
		switch (count)
		{
			case 4:
			{
				world.openCloseDoor(DOOR_1, true);
				break;
			}
			case 10:
			{
				world.openCloseDoor(DOOR_2, true);
				break;
			}
			case 18:
			{
				world.openCloseDoor(DOOR_3, true);
				break;
			}
			case 28:
			{
				world.openCloseDoor(DOOR_4, true);
				break;
			}
			case 40:
			{
				world.openCloseDoor(DOOR_5, true);
				break;
			}
		}
	}
	
	private void makeCast(Npc npc, List<Npc> targets)
	{
		npc.setTarget(targets.get(getRandom(targets.size())));
		if (SKILLS.containsKey(npc.getId()))
		{
			npc.doCast(SKILLS.get(npc.getId()).getSkill());
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			switch (event)
			{
				case "FINISH":
				{
					if (getQuestItemsCount(player, SEAL_OF_BINDING) >= 4)
					{
						playMovie(player, Movie.SSQ_SEALING_EMPEROR_2ND);
						startQuestTimer("TELEPORT", 27000, null, player);
					}
					break;
				}
				case "TELEPORT":
				{
					player.teleToLocation(ENTER);
					break;
				}
				case "FIGHT":
				{
					final List<Npc> anakimGroup = world.getNpcs(ANAKIM_GROUP);
					final List<Npc> lilithGroup = world.getNpcs(LILITH_GROUP);
					
					for (Npc caster : anakimGroup)
					{
						if ((caster != null) && !caster.isCastingNow(SkillCaster::isAnyNormalType))
						{
							makeCast(caster, lilithGroup);
						}
						if ((caster != null) && (caster.getId() == ANAKIM))
						{
							if (caster.isScriptValue(0))
							{
								caster.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.YOU_SUCH_A_FOOL_THE_VICTORY_OVER_THIS_WAR_BELONGS_TO_SHILEN);
								caster.setScriptValue(1);
							}
							else if (getRandom(100) < 10)
							{
								caster.broadcastSay(ChatType.NPC_SHOUT, LILITH_SHOUT[getRandom(3)]);
							}
						}
					}
					for (Npc caster : lilithGroup)
					{
						if ((caster != null) && !caster.isCastingNow(SkillCaster::isAnyNormalType))
						{
							makeCast(caster, anakimGroup);
						}
						if ((caster != null) && (caster.getId() == LILITH))
						{
							if (caster.isScriptValue(0))
							{
								caster.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.FOR_THE_ETERNITY_OF_EINHASAD);
								if (Util.checkIfInRange(2000, caster, player, true))
								{
									player.sendPacket(new NpcSay(caster, ChatType.NPC_WHISPER, NpcStringId.MY_POWER_S_WEAKENING_HURRY_AND_TURN_ON_THE_SEALING_DEVICE));
								}
								caster.setScriptValue(1);
							}
							else if (getRandom(100) < 10)
							{
								switch (getRandom(3))
								{
									case 0:
									{
										caster.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.DEAR_SHILLIEN_S_OFFSPRINGS_YOU_ARE_NOT_CAPABLE_OF_CONFRONTING_US);
										if (Util.checkIfInRange(2000, caster, player, true))
										{
											player.sendPacket(new NpcSay(caster, ChatType.NPC_WHISPER, NpcStringId.ALL_4_SEALING_DEVICES_MUST_BE_TURNED_ON));
										}
										break;
									}
									case 1:
									{
										caster.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.I_LL_SHOW_YOU_THE_REAL_POWER_OF_EINHASAD);
										if (Util.checkIfInRange(2000, caster, player, true))
										{
											player.sendPacket(new NpcSay(caster, ChatType.NPC_WHISPER, NpcStringId.LILITH_ATTACK_IS_GETTING_STRONGER_GO_AHEAD_AND_TURN_IT_ON));
										}
										break;
									}
									case 2:
									{
										caster.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.DEAR_MILITARY_FORCE_OF_LIGHT_GO_DESTROY_THE_OFFSPRINGS_OF_SHILLIEN);
										if (Util.checkIfInRange(2000, caster, player, true))
										{
											player.sendPacket(new NpcSay(caster, ChatType.NPC_WHISPER, NpcStringId.DEAR_S1_GIVE_ME_MORE_STRENGTH).addStringParameter(player.getName()));
										}
										break;
									}
								}
							}
						}
						startQuestTimer("FIGHT", 1000, null, player);
					}
					break;
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(Npc npc, PlayerInstance player, boolean isSummon)
	{
		switch (npc.getId())
		{
			case LILIM_BUTCHER:
			case LILIM_GUARD_KNIGHT:
			{
				if (npc.isScriptValue(0))
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THIS_PLACE_ONCE_BELONGED_TO_LORD_SHILEN);
					npc.setScriptValue(1);
				}
				break;
			}
			case LILIM_MAGUS:
			case LILIM_GREAT_MAGUS:
			{
				if (npc.isScriptValue(0))
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHO_DARES_ENTER_THIS_PLACE);
					npc.setScriptValue(1);
				}
				break;
			}
			case LILIM_KNIGHT_ERRANT:
			case LILIM_KNIGHT:
			{
				if (npc.isScriptValue(0))
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THOSE_WHO_ARE_AFRAID_SHOULD_GET_AWAY_AND_THOSE_WHO_ARE_BRAVE_SHOULD_FIGHT);
					npc.setScriptValue(1);
				}
				break;
			}
			case LILIM_SLAYER:
			{
				if (npc.isScriptValue(0))
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.LEAVE_NOW);
					npc.setScriptValue(1);
				}
				break;
			}
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance player, int damage, boolean isSummon)
	{
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			if (npc.isScriptValue(0))
			{
				if (npc.getCurrentHp() < (npc.getMaxHp() * 0.1))
				{
					giveItems(player, SEAL_OF_BINDING, 1);
					player.sendPacket(SystemMessageId.THE_SEALING_DEVICE_GLITTERS_AND_MOVES_ACTIVATION_COMPLETE_NORMALLY);
					npc.setScriptValue(1);
					startQuestTimer("FINISH", 1000, npc, player);
					cancelQuestTimer("FIGHT", npc, player);
				}
			}
			if (getRandom(100) < 50)
			{
				npc.doCast(SEAL_ISOLATION.getSkill());
			}
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + ".htm";
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			synchronized (world)
			{
				final int count = world.getParameters().getInt("countKill", 0) + 1;
				world.setParameter("countKill", count);
				checkDoors(npc, world, count);
			}
		}
		
		switch (npc.getId())
		{
			case LILIM_MAGUS:
			case LILIM_GREAT_MAGUS:
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.LORD_SHILEN_SOME_DAY_YOU_WILL_ACCOMPLISH_THIS_MISSION);
				break;
			}
			case LILIM_KNIGHT_ERRANT:
			case LILIM_KNIGHT:
			case LILIM_GUARD_KNIGHT:
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHY_ARE_YOU_GETTING_IN_OUR_WAY);
				break;
			}
			case LILIM_SLAYER:
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.FOR_SHILEN);
				break;
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setUndying(true);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance talker)
	{
		final QuestState qs = talker.getQuestState(Q00196_SevenSignsSealOfTheEmperor.class.getSimpleName());
		String htmltext = getNoQuestMsg(talker);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (npc.getId())
		{
			case PROMISE_OF_MAMMON:
			{
				if (qs.isCond(3) || qs.isCond(4))
				{
					enterInstance(talker, npc, TEMPLATE_ID);
					return "";
				}
				break;
			}
			case LEON:
			{
				if (qs.getCond() >= 3)
				{
					takeItems(talker, SACRED_SWORD_OF_EINHASAD, -1);
					finishInstance(talker, 0);
					htmltext = "32587-01.html";
				}
				break;
			}
			case DISCIPLES_GATEKEEPER:
			{
				if (qs.getCond() >= 3)
				{
					final Instance world = npc.getInstanceWorld();
					if (world != null)
					{
						world.openCloseDoor(DISCIPLES_NECROPOLIS_DOOR, true);
						playMovie(talker, Movie.SSQ_SEALING_EMPEROR_1ST);
						startQuestTimer("FIGHT", 1000, null, talker);
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new SSQDisciplesNecropolisPast();
	}
}