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
package quests.Q00386_StolenDignity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.serverpackets.TutorialShowQuestionMark;
import org.l2jbr.gameserver.util.Util;

/**
 * Stolen Dignity (386)
 * @author Zealar / Stayway (Rework Helios)
 */
public class Q00386_StolenDignity extends Quest
{
	// NPCs
	private static final int WAREHOUSE_KEEPER_ROMP = 30843;
	// Monsters
	private static final int CRIMSON_DRAKE = 20670;
	private static final int KADIOS = 20671;
	private static final int SPITEFUL_SOUL_LEADER = 20974;
	private static final int SPITEFUL_SOUL_WIZARD = 20975;
	private static final int ARCHER_OF_DESTRUCTIONS = 21001;
	private static final int GRAVEYARD_LICH = 21003;
	private static final int GRAVEYARD_PREDATOR = 21005;
	private static final int FALLEN_ORC_SHAMAN = 21020;
	private static final int SHARP_TALON_TIGER = 21021;
	private static final int HAMES_ORC_SNIPER = 21113;
	private static final int HAMES_ORC_PREFECT = 21116;
	// Items
	private static final int Q_STOLEN_INF_ORE = 6363;
	// Reward
	private static final int DRAGON_SLAYER_EDGE = 5529;
	private static final int METEOR_SHOWER_HEAD = 5532;
	private static final int ELYSIAN_HEAD = 5533;
	private static final int SOUL_BOW_SHAFT = 5534;
	private static final int CARNIUM_BOW_SHAFT = 5535;
	private static final int BLOODY_ORCHID_HEAD = 5536;
	private static final int SOUL_SEPARATOR_HEAD = 5537;
	private static final int DRAGON_GRINDER_EDGE = 5538;
	private static final int BLOOD_TORNADO_EDGE = 5539;
	private static final int TALLUM_GLAIVE_EDGE = 5541;
	private static final int HALBARD_EDGE = 5542;
	private static final int DASPARIONS_STAFF_HEAD = 5543;
	private static final int WORLDTREES_BRANCH_HEAD = 5544;
	private static final int DARK_LEGIONS_EDGE_EDGE = 5545;
	private static final int SWORD_OF_MIRACLE_EDGE = 5546;
	private static final int ELEMENTAL_SWORD_EDGE = 5547;
	private static final int TALLUM_BLADE_EDGE = 5548;
	private static final int INFERNO_MASTER_BLADE = 8331;
	private static final int EYE_OF_SOUL_PIECE = 8341;
	private static final int DRAGON_FLAME_HEAD_PIECE = 8342;
	private static final int DOOM_CRUSHER_HEAD = 8349;
	private static final int HAMMER_OF_DESTROYER_PIECE = 8346;
	private static final int SIRR_BLADE_BLADE = 8712;
	private static final int SWORD_OF_IPOS_BLADE = 8713;
	private static final int BARAKIEL_AXE_PIECE = 8714;
	private static final int TUNING_FORK_OF_BEHEMOTH_PIECE = 8715;
	private static final int NAGA_STORM_PIECE = 8716;
	private static final int TIPHON_SPEAR_EDGE = 8717;
	private static final int SHYID_BOW_SHAFT = 8718;
	private static final int SOBEKK_HURRICANE_EDGE = 8719;
	private static final int TONGUE_OF_THEMIS_PIECE = 8720;
	private static final int HAND_OF_CABRIO_HEAD = 8721;
	private static final int CRYSTAL_OF_DEAMON_PIECE = 8722;
	
	public Q00386_StolenDignity()
	{
		super(386);
		addStartNpc(WAREHOUSE_KEEPER_ROMP);
		addTalkId(WAREHOUSE_KEEPER_ROMP);
		addKillId(CRIMSON_DRAKE, KADIOS, SPITEFUL_SOUL_LEADER, SPITEFUL_SOUL_WIZARD, ARCHER_OF_DESTRUCTIONS, GRAVEYARD_LICH, GRAVEYARD_PREDATOR, FALLEN_ORC_SHAMAN, SHARP_TALON_TIGER, HAMES_ORC_SNIPER, HAMES_ORC_PREFECT);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		final String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == WAREHOUSE_KEEPER_ROMP)
		{
			if (qs.isCreated())
			{
				if (player.getLevel() >= 58)
				{
					return "30843-01.htm";
				}
				return "30843-04.html";
			}
			if (getQuestItemsCount(player, Q_STOLEN_INF_ORE) < 100)
			{
				return "30843-06.html";
			}
			return "30843-07.html";
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && (npc.getId() == WAREHOUSE_KEEPER_ROMP))
		{
			if (event.equals("QUEST_ACCEPTED"))
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ACCEPT);
				qs.setMemoState(336);
				qs.startQuest();
				player.sendPacket(new TutorialShowQuestionMark(336, 1)); // why not getId() ?
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				return "30843-05.htm";
			}
			if (event.contains(".html"))
			{
				return event;
			}
			final int ask = Integer.parseInt(event);
			switch (ask)
			{
				case 3:
				{
					return "30843-09a.html";
				}
				case 5:
				{
					return "30843-03.html";
				}
				case 6:
				{
					qs.exitQuest(true, true);
					return "30843-08.html";
				}
				case 9:
				{
					return "30843-09.htm";
				}
				case 8:
				{
					if (getQuestItemsCount(player, Q_STOLEN_INF_ORE) >= 100)
					{
						takeItems(player, Q_STOLEN_INF_ORE, 100);
						createBingoBoard(qs);
						return "30843-12.html";
					}
					return "30843-11.html";
				}
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
				{
					selectBingoNumber(qs, (ask - 10) + 1);
					return fillBoard(player, qs, getHtm(player, "30843-13.html"));
				}
				case 19:
				{
					return takeHtml(player, qs, 1);
				}
				case 20:
				{
					return takeHtml(player, qs, 2);
				}
				case 21:
				{
					return takeHtml(player, qs, 3);
				}
				case 22:
				{
					return takeHtml(player, qs, 4);
				}
				case 23:
				{
					return takeHtml(player, qs, 5);
				}
				case 24:
				{
					return takeHtml(player, qs, 6);
				}
				case 25:
				{
					return takeHtml(player, qs, 7);
				}
				case 26:
				{
					return takeHtml(player, qs, 8);
				}
				case 27:
				{
					return takeHtml(player, qs, 9);
				}
				case 55:
				{
					return beforeReward(player, qs, 1);
				}
				case 56:
				{
					return beforeReward(player, qs, 2);
				}
				case 57:
				{
					return beforeReward(player, qs, 3);
				}
				case 58:
				{
					return beforeReward(player, qs, 4);
				}
				case 59:
				{
					return beforeReward(player, qs, 5);
				}
				case 60:
				{
					return beforeReward(player, qs, 6);
				}
				case 61:
				{
					return beforeReward(player, qs, 7);
				}
				case 62:
				{
					return beforeReward(player, qs, 8);
				}
				case 63:
				{
					return beforeReward(player, qs, 9);
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	private String takeHtml(PlayerInstance player, QuestState qs, int num)
	{
		String html = null;
		int i3;
		if (!isSelectedBingoNumber(qs, num))
		{
			selectBingoNumber(qs, num);
			i3 = getBingoSelectCount(qs);
			
			if (i3 == 2)
			{
				html = getHtm(player, "30843-14.html");
			}
			else if (i3 == 3)
			{
				html = getHtm(player, "30843-16.html");
			}
			else if (i3 == 4)
			{
				html = getHtm(player, "30843-18.html");
			}
			else if (i3 == 5)
			{
				html = getHtm(player, "30843-20.html");
			}
			return fillBoard(player, qs, html);
		}
		i3 = getBingoSelectCount(qs);
		if (i3 == 1)
		{
			html = getHtm(player, "30843-15.html");
		}
		else if (i3 == 2)
		{
			html = getHtm(player, "30843-17.html");
		}
		else if (i3 == 3)
		{
			html = getHtm(player, "30843-19.html");
		}
		else if (i3 == 4)
		{
			html = getHtm(player, "30843-21.html");
		}
		return fillBoard(player, qs, html);
	}
	
	private String fillBoard(PlayerInstance player, QuestState qs, String html)
	{
		for (int i0 = 0; i0 < 9; i0 += 1)
		{
			final int i1 = getNumberFromBingoBoard(qs, i0);
			if (isSelectedBingoNumber(qs, i1))
			{
				html = html.replace("<?Cell" + (i0 + 1) + "?>", Integer.toString(i1));
			}
			else
			{
				html = html.replace("<?Cell" + (i0 + 1) + "?>", "?");
			}
		}
		return html;
	}
	
	private String colorBoard(PlayerInstance player, QuestState qs, String html)
	{
		for (int i0 = 0; i0 < 9; i0 += 1)
		{
			final int i1 = getNumberFromBingoBoard(qs, i0);
			html = html.replace("<?FontColor" + (i0 + 1) + "?>", isSelectedBingoNumber(qs, i1) ? "ff0000" : "ffffff");
			html = html.replace("<?Cell" + (i0 + 1) + "?>", Integer.toString(i1));
		}
		return html;
	}
	
	private String beforeReward(PlayerInstance player, QuestState qs, int num)
	{
		if (!isSelectedBingoNumber(qs, num))
		{
			selectBingoNumber(qs, num);
			final int i3 = getMatchedBingoLineCount(qs);
			String html;
			if ((i3 == 3) && (getBingoSelectCount(qs) == 6))
			{
				reward(player, qs, 4);
				html = getHtm(player, "30843-22.html");
			}
			else if ((i3 == 0) && (getBingoSelectCount(qs) == 6))
			{
				reward(player, qs, 10);
				html = getHtm(player, "30843-24.html");
			}
			else
			{
				html = getHtm(player, "30843-23.html");
			}
			return colorBoard(player, qs, html);
		}
		return fillBoard(player, qs, getHtm(player, "30843-25.html"));
	}
	
	private void reward(PlayerInstance player, QuestState qs, int count)
	{
		switch (getRandom(33))
		{
			case 0:
			{
				giveItems(player, DRAGON_SLAYER_EDGE, count);
				break;
			}
			case 1:
			{
				giveItems(player, METEOR_SHOWER_HEAD, count);
				break;
			}
			case 2:
			{
				giveItems(player, ELYSIAN_HEAD, count);
				break;
			}
			case 3:
			{
				giveItems(player, SOUL_BOW_SHAFT, count);
				break;
			}
			case 4:
			{
				giveItems(player, CARNIUM_BOW_SHAFT, count);
				break;
			}
			case 5:
			{
				giveItems(player, BLOODY_ORCHID_HEAD, count);
				break;
			}
			case 6:
			{
				giveItems(player, SOUL_SEPARATOR_HEAD, count);
				break;
			}
			case 7:
			{
				giveItems(player, DRAGON_GRINDER_EDGE, count);
				break;
			}
			case 8:
			{
				giveItems(player, BLOOD_TORNADO_EDGE, count);
				break;
			}
			case 9:
			{
				giveItems(player, TALLUM_GLAIVE_EDGE, count);
				break;
			}
			case 10:
			{
				giveItems(player, HALBARD_EDGE, count);
				break;
			}
			case 11:
			{
				giveItems(player, DASPARIONS_STAFF_HEAD, count);
				break;
			}
			case 12:
			{
				giveItems(player, WORLDTREES_BRANCH_HEAD, count);
				break;
			}
			case 13:
			{
				giveItems(player, DARK_LEGIONS_EDGE_EDGE, count);
				break;
			}
			case 14:
			{
				giveItems(player, SWORD_OF_MIRACLE_EDGE, count);
				break;
			}
			case 15:
			{
				giveItems(player, ELEMENTAL_SWORD_EDGE, count);
				break;
			}
			case 16:
			{
				giveItems(player, TALLUM_BLADE_EDGE, count);
				break;
			}
			case 17:
			{
				giveItems(player, INFERNO_MASTER_BLADE, count);
				break;
			}
			case 18:
			{
				giveItems(player, EYE_OF_SOUL_PIECE, count);
				break;
			}
			case 19:
			{
				giveItems(player, DRAGON_FLAME_HEAD_PIECE, count);
				break;
			}
			case 20:
			{
				giveItems(player, DOOM_CRUSHER_HEAD, count);
				break;
			}
			case 21:
			{
				giveItems(player, HAMMER_OF_DESTROYER_PIECE, count);
				break;
			}
			case 22:
			{
				giveItems(player, SIRR_BLADE_BLADE, count);
				break;
			}
			case 23:
			{
				giveItems(player, SWORD_OF_IPOS_BLADE, count);
				break;
			}
			case 24:
			{
				giveItems(player, BARAKIEL_AXE_PIECE, count);
				break;
			}
			case 25:
			{
				giveItems(player, TUNING_FORK_OF_BEHEMOTH_PIECE, count);
				break;
			}
			case 26:
			{
				giveItems(player, NAGA_STORM_PIECE, count);
				break;
			}
			case 27:
			{
				giveItems(player, TIPHON_SPEAR_EDGE, count);
				break;
			}
			case 28:
			{
				giveItems(player, SHYID_BOW_SHAFT, count);
				break;
			}
			case 29:
			{
				giveItems(player, SOBEKK_HURRICANE_EDGE, count);
				break;
			}
			case 30:
			{
				giveItems(player, TONGUE_OF_THEMIS_PIECE, count);
				break;
			}
			case 31:
			{
				giveItems(player, HAND_OF_CABRIO_HEAD, count);
				break;
			}
			case 32:
			{
				giveItems(player, CRYSTAL_OF_DEAMON_PIECE, count);
				break;
			}
		}
	}
	
	/**
	 * @param qs
	 */
	private void createBingoBoard(QuestState qs)
	{
		//@formatter:off
		final Integer[] arr = {1,2,3,4,5,6,7,8,9};
		//@formatter:on
		Collections.shuffle(Arrays.asList(arr));
		qs.set("numbers", Arrays.asList(arr).toString().replaceAll("[^\\d ]", ""));
		qs.set("selected", "? ? ? ? ? ? ? ? ?");
	}
	
	/**
	 * @param qs
	 * @return
	 */
	private int getMatchedBingoLineCount(QuestState qs)
	{
		final String[] q = qs.get("selected").split(" ");
		int found = 0;
		// Horizontal
		if ((q[0] + q[1] + q[2]).matches("\\d+"))
		{
			found++;
		}
		if ((q[3] + q[4] + q[5]).matches("\\d+"))
		{
			found++;
		}
		if ((q[6] + q[7] + q[8]).matches("\\d+"))
		{
			found++;
		}
		// Vertical
		if ((q[0] + q[3] + q[6]).matches("\\d+"))
		{
			found++;
		}
		if ((q[1] + q[4] + q[7]).matches("\\d+"))
		{
			found++;
		}
		if ((q[2] + q[5] + q[8]).matches("\\d+"))
		{
			found++;
		}
		// Diagonal
		if ((q[0] + q[4] + q[8]).matches("\\d+"))
		{
			found++;
		}
		if ((q[2] + q[4] + q[6]).matches("\\d+"))
		{
			found++;
		}
		return found;
	}
	
	/**
	 * @param qs
	 * @param num
	 */
	private void selectBingoNumber(QuestState qs, int num)
	{
		final String[] numbers = qs.get("numbers").split(" ");
		int pos = 0;
		for (int i = 0; i < numbers.length; i++)
		{
			if (Integer.parseInt(numbers[i]) == num)
			{
				pos = i;
				break;
			}
		}
		final String[] selected = qs.get("selected").split(" ");
		for (int i = 0; i < selected.length; i++)
		{
			if (i == pos)
			{
				selected[i] = Integer.toString(num);
				continue;
			}
		}
		String result = selected[0];
		for (int i = 1; i < selected.length; i++)
		{
			result += " " + selected[i];
		}
		qs.set("selected", result);
	}
	
	/**
	 * @param qs
	 * @param num
	 * @return
	 */
	private boolean isSelectedBingoNumber(QuestState qs, int num)
	{
		return qs.get("selected").contains(Integer.toString(num));
	}
	
	/**
	 * @param qs
	 * @param num
	 * @return
	 */
	private int getNumberFromBingoBoard(QuestState qs, int num)
	{
		return Integer.parseInt(qs.get("numbers").split(" ")[num]);
	}
	
	/**
	 * @param qs
	 * @return
	 */
	private int getBingoSelectCount(QuestState qs)
	{
		return qs.get("selected").replaceAll("\\D", "").length();
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPlayerFromParty(killer, npc);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case CRIMSON_DRAKE:
				{
					if (getRandom(1000) < 20.200001)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case KADIOS:
				{
					if (getRandom(1000) < 211)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case SPITEFUL_SOUL_LEADER:
				{
					if (getRandom(100) < 44)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case SPITEFUL_SOUL_WIZARD:
				{
					if (getRandom(100) < 39)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case ARCHER_OF_DESTRUCTIONS:
				{
					if (getRandom(1000) < 214)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case GRAVEYARD_LICH:
				{
					if (getRandom(1000) < 173)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case GRAVEYARD_PREDATOR:
				{
					if (getRandom(1000) < 211)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case FALLEN_ORC_SHAMAN:
				{
					if (getRandom(1000) < 478)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case SHARP_TALON_TIGER:
				{
					if (getRandom(1000) < 234)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case HAMES_ORC_SNIPER:
				{
					if (getRandom(100) < 37)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case HAMES_ORC_PREFECT:
				{
					if (getRandom(1000) < 487)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private QuestState getRandomPlayerFromParty(PlayerInstance player, Npc npc)
	{
		final QuestState qs = getQuestState(player, false);
		final List<QuestState> candidates = new ArrayList<>();
		
		if ((qs != null) && qs.isStarted())
		{
			candidates.add(qs);
			candidates.add(qs);
		}
		
		if (player.isInParty())
		{
			player.getParty().getMembers().stream().forEach(pm ->
			{
				final QuestState qss = getQuestState(pm, false);
				if ((qss != null) && qss.isStarted() && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, pm, true))
				{
					candidates.add(qss);
				}
			});
		}
		return candidates.isEmpty() ? null : candidates.get(getRandom(candidates.size()));
	}
}
