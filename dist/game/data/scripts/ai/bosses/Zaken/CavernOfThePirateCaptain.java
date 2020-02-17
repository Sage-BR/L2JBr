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
package ai.bosses.Zaken;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;

/**
 * Cavern Of The Pirate Captain (Day Dream) instance Zone.
 * @author St3eT
 */
public class CavernOfThePirateCaptain extends AbstractInstance
{
	// NPCs
	private static final int PATHFINDER = 32713; // Pathfinder Worker
	private static final int ZAKEN_60 = 29176; // Zaken
	private static final int ZAKEN_83 = 29181; // Zaken
	private static final int CANDLE = 32705; // Zaken's Candle
	// Items
	private static final int VORPAL_RING = 15763; // Sealed Vorpal Ring
	private static final int VORPAL_EARRING = 15764; // Sealed Vorpal Earring
	// Reward - Zaken lvl 83 @formatter:off
	private static final int[][] VORPAL_JEWELS = 
	{
		// Time, jewel id, chance
		{300000, VORPAL_RING, 50}, // 5 minutes
		{600000, VORPAL_EARRING, 30}, // 10 minutes
		{900000, VORPAL_RING, 25}, // 15 minutes
	};
	// Locations @formatter:on
	private static final Location[] CANDLE_LOC =
	{
		// Floor 1
		new Location(53313, 220133, -3498),
		new Location(53313, 218079, -3498),
		new Location(54240, 221045, -3498),
		new Location(54325, 219095, -3498),
		new Location(54240, 217155, -3498),
		new Location(55257, 220028, -3498),
		new Location(55257, 218172, -3498),
		new Location(56280, 221045, -3498),
		new Location(56195, 219095, -3498),
		new Location(56280, 217155, -3498),
		new Location(57215, 220133, -3498),
		new Location(57215, 218079, -3498),
		// Floor 2
		new Location(53313, 220133, -3226),
		new Location(53313, 218079, -3226),
		new Location(54240, 221045, -3226),
		new Location(54325, 219095, -3226),
		new Location(54240, 217155, -3226),
		new Location(55257, 220028, -3226),
		new Location(55257, 218172, -3226),
		new Location(56280, 221045, -3226),
		new Location(56195, 219095, -3226),
		new Location(56280, 217155, -3226),
		new Location(57215, 220133, -3226),
		new Location(57215, 218079, -3226),
		// Floor 3
		new Location(53313, 220133, -2954),
		new Location(53313, 218079, -2954),
		new Location(54240, 221045, -2954),
		new Location(54325, 219095, -2954),
		new Location(54240, 217155, -2954),
		new Location(55257, 220028, -2954),
		new Location(55257, 218172, -2954),
		new Location(56280, 221045, -2954),
		new Location(56195, 219095, -2954),
		new Location(56280, 217155, -2954),
		new Location(57215, 220133, -2954),
		new Location(57215, 218079, -2954),
	};
	// Misc
	private static final int TEMPLATE_ID_60 = 133;
	private static final int TEMPLATE_ID_83 = 135;
	//@formatter:off
	private static final int[][] ROOM_DATA =
	{
		// Floor 1
		{54240, 220133, -3498, 1, 3, 4, 6},
		{54240, 218073, -3498, 2, 5, 4, 7},
		{55265, 219095, -3498, 4, 9, 6, 7},
		{56289, 220133, -3498, 8, 11, 6, 9},
		{56289, 218073, -3498, 10, 12, 7, 9},
		// Floor 2
		{54240, 220133, -3226, 13, 15, 16, 18},
		{54240, 218073, -3226, 14, 17, 16, 19},
		{55265, 219095, -3226, 21, 16, 19, 18},
		{56289, 220133, -3226, 20, 23, 21, 18},
		{56289, 218073, -3226, 22, 24, 19, 21},
		// Floor 3
		{54240, 220133, -2954, 25, 27, 28, 30},
		{54240, 218073, -2954, 26, 29, 28, 31},
		{55265, 219095, -2954, 33, 28, 31, 30},
		{56289, 220133, -2954, 32, 35, 30, 33},
		{56289, 218073, -2954, 34, 36, 31, 33}
	};
	//@formatter:on
	
	public CavernOfThePirateCaptain()
	{
		super(TEMPLATE_ID_60, TEMPLATE_ID_83);
		addStartNpc(PATHFINDER);
		addTalkId(PATHFINDER);
		addKillId(ZAKEN_60, ZAKEN_83);
		addFirstTalkId(CANDLE);
		addInstanceCreatedId(TEMPLATE_ID_60, TEMPLATE_ID_83);
		addInstanceEnterId(TEMPLATE_ID_60, TEMPLATE_ID_83);
		addInstanceLeaveId(TEMPLATE_ID_60, TEMPLATE_ID_83);
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		final List<Npc> candles = new ArrayList<>();
		final int zakenRoom = getRandom(1, 15);
		
		for (int i = 0; i < 36; i++)
		{
			final Npc candle = addSpawn(CANDLE, CANDLE_LOC[i], false, 0, false, instance.getId());
			candle.getVariables().set("candleId", i + 1);
			candles.add(candle);
		}
		
		for (int i = 3; i < 7; i++)
		{
			candles.get(ROOM_DATA[zakenRoom - 1][i] - 1).getVariables().set("isBlue", 1);
		}
		final Npc zaken = spawnNpc(instance.getTemplateParameters().getInt("Zaken"), zakenRoom, null, instance);
		zaken.setInvisible(true);
		zaken.setBlockActions(true);
		instance.setParameter("zakenRoom", zakenRoom);
		instance.setParameter("zaken", zaken);
	}
	
	@Override
	public void onInstanceEnter(PlayerInstance player, Instance instance)
	{
		final int startTime = (int) (instance.getElapsedTime() / 1000);
		final int endTime = (int) (instance.getRemainingTime() / 1000);
		player.sendPacket(new ExSendUIEvent(player, false, true, startTime, endTime, NpcStringId.ELAPSED_TIME));
	}
	
	@Override
	public void onInstanceLeave(PlayerInstance player, Instance instance)
	{
		player.sendPacket(new ExSendUIEvent(player, true, true, 0, 0, NpcStringId.ELAPSED_TIME));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equals("enter60"))
		{
			enterInstance(player, npc, TEMPLATE_ID_60);
		}
		else if (event.equals("enter83"))
		{
			enterInstance(player, npc, TEMPLATE_ID_83);
		}
		else
		{
			final Instance world = npc.getInstanceWorld();
			if (world != null)
			{
				final StatsSet templParams = world.getTemplateParameters();
				final StatsSet params = world.getParameters();
				switch (event)
				{
					case "BURN_BLUE":
					{
						if (npc.hasDisplayEffect(0))
						{
							npc.setDisplayEffect(1); // Burning
							startQuestTimer("BURN_BLUE2", 3000, npc, player);
							if (params.getInt("blueFounded") == 4)
							{
								startQuestTimer("SHOW_ZAKEN", 5000, npc, player);
							}
						}
						break;
					}
					case "BURN_BLUE2":
					{
						if (npc.hasDisplayEffect(1)) // Burning
						{
							npc.setDisplayEffect(3); // Blue glow
						}
						break;
					}
					case "BURN_RED":
					{
						if (npc.hasDisplayEffect(0))
						{
							npc.setDisplayEffect(1); // Burning
							startQuestTimer("BURN_RED2", 3000, npc, player);
						}
						break;
					}
					case "BURN_RED2":
					{
						if (npc.hasDisplayEffect(1)) // Burning
						{
							final int room = getRoomByCandle(npc);
							npc.setDisplayEffect(2); // Red glow
							showOnScreenMsg(world, NpcStringId.THE_CANDLES_CAN_LEAD_YOU_TO_ZAKEN_DESTROY_HIM, ExShowScreenMessage.MIDDLE_CENTER, 6000);
							spawnNpc(templParams.getInt("DollBlader"), room, player, world);
							spawnNpc(templParams.getInt("ValeMaster"), room, player, world);
							spawnNpc(templParams.getInt("PiratesZombie"), room, player, world);
							spawnNpc(templParams.getInt("PiratesZombieCaptain"), room, player, world);
						}
						break;
					}
					case "SHOW_ZAKEN":
					{
						if (world.getTemplateId() == ZAKEN_83)
						{
							showOnScreenMsg(world, NpcStringId.WHO_DARES_AWAKEN_THE_MIGHTY_ZAKEN, ExShowScreenMessage.MIDDLE_CENTER, 6000);
						}
						
						final int zakenRoom = params.getInt("zakenRoom");
						final Npc zaken = params.getObject("zaken", Npc.class);
						zaken.setInvisible(false);
						zaken.setBlockActions(false);
						spawnNpc(templParams.getInt("DollBlader"), zakenRoom, player, world);
						spawnNpc(templParams.getInt("PiratesZombie"), zakenRoom, player, world);
						spawnNpc(templParams.getInt("PiratesZombieCaptain"), zakenRoom, player, world);
						break;
					}
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			if (npc.getId() == ZAKEN_83)
			{
				final long time = world.getElapsedTime();
				for (PlayerInstance playersInside : world.getPlayersInsideRadius(npc, Config.ALT_PARTY_RANGE))
				{
					for (int[] reward : VORPAL_JEWELS)
					{
						if (time <= reward[0])
						{
							if (getRandom(100) < reward[2])
							{
								giveItems(playersInside, reward[1], 1);
							}
							break;
						}
					}
				}
			}
			world.finishInstance();
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final Instance world = npc.getInstanceWorld();
		if ((world != null) && npc.isScriptValue(0))
		{
			npc.setScriptValue(1);
			if (npc.getVariables().getInt("isBlue", 0) == 1)
			{
				final int blueCandles = world.getParameters().getInt("blueFounded", 0);
				world.setParameter("blueFounded", blueCandles + 1);
				startQuestTimer("BURN_BLUE", 500, npc, player);
			}
			else
			{
				startQuestTimer("BURN_RED", 500, npc, player);
			}
		}
		return null;
	}
	
	private int getRoomByCandle(Npc npc)
	{
		final int candleId = npc.getVariables().getInt("candleId", 0);
		for (int i = 0; i < 15; i++)
		{
			if ((ROOM_DATA[i][3] == candleId) || (ROOM_DATA[i][4] == candleId))
			{
				return i + 1;
			}
		}
		
		if ((candleId == 6) || (candleId == 7))
		{
			return 3;
		}
		else if ((candleId == 18) || (candleId == 19))
		{
			return 8;
		}
		else if ((candleId == 30) || (candleId == 31))
		{
			return 13;
		}
		return 0;
	}
	
	private Npc spawnNpc(int npcId, int roomId, PlayerInstance player, Instance world)
	{
		if ((player != null) && (npcId != ZAKEN_60) && (npcId != ZAKEN_83))
		{
			final Npc mob = addSpawn(npcId, ROOM_DATA[roomId - 1][0] + getRandom(350), ROOM_DATA[roomId - 1][1] + getRandom(350), ROOM_DATA[roomId - 1][2], 0, false, 0, false, world.getId());
			addAttackPlayerDesire(mob, player);
			return mob;
		}
		return addSpawn(npcId, ROOM_DATA[roomId - 1][0], ROOM_DATA[roomId - 1][1], ROOM_DATA[roomId - 1][2], 0, false, 0, false, world.getId());
	}
	
	public static void main(String[] args)
	{
		new CavernOfThePirateCaptain();
	}
}