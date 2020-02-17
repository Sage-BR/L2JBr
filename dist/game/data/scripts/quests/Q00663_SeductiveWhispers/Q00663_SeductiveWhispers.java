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
package quests.Q00663_SeductiveWhispers;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * @author Mathael
 */
public class Q00663_SeductiveWhispers extends Quest
{
	// NPC
	private static final int WILBERT = 30846;
	// Item
	private static final int SPIRIT_BEAD = 8766;
	// Monsters
	private static final int[] MOBS =
	{
		20956,
		20955,
		20954,
		20959,
		20958,
		20957,
		20963,
		20962,
		20961,
		20960,
		20976,
		20974,
		20975,
		21006,
		20674,
		21010,
		21009,
		21001,
		21002
	};
	// Rewards
	private static final int[] RECIPES_B_GRADE =
	{
		5000,
		4966,
		4967,
		4963,
		4968,
		5005,
		4969,
		5007,
		4971,
		5008,
		4970,
		5006,
		5001,
		4972,
		4964,
		5002,
		5003,
		4965,
		5004
	};
	private static final int[] PART_B_GRADE =
	{
		4114,
		4107,
		4108,
		1104,
		4109,
		4119,
		4110,
		4121,
		4112,
		2120,
		4111,
		4120,
		4115,
		4113,
		4105,
		4116,
		4117,
		4106,
		4118
	};
	// Misc
	private static final Card[] cards = new Card[10];
	static
	{
		cards[0] = new Card(Side.MOON, 1);
		cards[1] = new Card(Side.SUN, 1);
		cards[2] = new Card(Side.MOON, 2);
		cards[3] = new Card(Side.SUN, 2);
		cards[4] = new Card(Side.MOON, 3);
		cards[5] = new Card(Side.SUN, 3);
		cards[6] = new Card(Side.MOON, 4);
		cards[7] = new Card(Side.SUN, 4);
		cards[8] = new Card(Side.MOON, 5);
		cards[9] = new Card(Side.SUN, 5);
	}
	private int winCount = 0;
	private Card playerCard = null;
	private Card npcCard = null;
	
	public Q00663_SeductiveWhispers()
	{
		super(663);
		addStartNpc(WILBERT);
		addTalkId(WILBERT);
		addKillId(MOBS);
		addCondMinLevel(50, "30846-lvl.html");
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "30846-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = "30846-09.html";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "30846-02.htm":
			case "30846-04.htm":
			case "30846-05.html":
			case "30846-11.html":
			case "30846-12.html":
			case "30846-14.html":
			case "30846-19.html":
			{
				break;
			}
			case "30846-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "30846-10.html":
			{
				qs.exitQuest(true, true);
				break;
			}
			case "30846-06.html":
			{
				// practice
				if (getQuestItemsCount(player, SPIRIT_BEAD) < 1)
				{
					event = "30846-13.html";
				}
				else
				{
					takeItems(player, SPIRIT_BEAD, 1);
				}
				break;
			}
			case "30846-22.html":
			{
				if (playerCard == null)
				{
					playerCard = cards[Rnd.get(0, 9)];
				}
				else
				{
					playerCard = getRandomCard();
				}
				event = play(player, true, true);
				break;
			}
			case "30846-23.html":
			{
				if (npcCard == null)
				{
					npcCard = playerCard; // to avoid same card when getRandomCard()
					npcCard = getRandomCard();
				}
				else
				{
					npcCard = getRandomCard();
				}
				event = play(player, false, true);
				break;
			}
			case "30846-15.html":
			{
				if (getQuestItemsCount(player, SPIRIT_BEAD) < 50)
				{
					event = "30846-08.html";
				}
				else
				{
					takeItems(player, SPIRIT_BEAD, 50);
				}
				break;
			}
			case "30846-16.html":
			{
				// Player draw a card (Always player start first !!)
				if (playerCard == null)
				{
					playerCard = cards[Rnd.get(0, 9)];
				}
				else
				{
					playerCard = getRandomCard();
				}
				event = play(player, true, false);
				break;
			}
			case "30846-17.html":
			{
				// npc draw card
				if (npcCard == null)
				{
					npcCard = playerCard; // to avoid same card when getRandomCard()
					npcCard = getRandomCard();
				}
				else
				{
					npcCard = getRandomCard();
				}
				
				event = play(player, false, false);
				break;
			}
			case "30846-20.html":
			{
				if (winCount > 0)
				{
					giveRewards(player, winCount);
					winCount = 0;
				}
				reset(false);
				break;
			}
			case "30846-21.html":
			{
				reset(true);
				break;
			}
		}
		
		return event;
	}
	
	private String play(PlayerInstance player, boolean isPlayerTurn, boolean isPractice)
	{
		boolean isPlayerWin = false;
		boolean isNpcWin = false;
		String htmltext;
		final String table = getHtm(player, "table.html");
		
		if (isPlayerTurn)
		{
			if ((playerCard != null) && isWinner(playerCard, npcCard, true))
			{
				if (isPractice)
				{
					rewardItems(player, 57, 800);
				}
				else
				{
					winCount++;
				}
				
				if (winCount == 8)
				{
					// Cant win more than 8 consecutive game (I don't know html after 8 wins (too hard) -> give reward and show main page)
					giveRewards(player, winCount);
					reset(true);
					return "30846-05.html";
				}
				
				isPlayerWin = true;
				htmltext = getHtm(player, isPractice ? "30846-07.html" : "30846-18.html");
				htmltext = htmltext.replace("%table%", table);
				htmltext = htmltext.replaceAll("%win_count%", Integer.toString(winCount));
			}
			else
			{
				htmltext = getHtm(player, isPractice ? "30846-22.html" : "30846-16.html");
				htmltext = htmltext.replace("%table%", table);
			}
		}
		else if ((playerCard != null) && (npcCard != null) && isWinner(playerCard, npcCard, false))
		{
			htmltext = getHtm(player, isPractice ? "30846-24.html" : "30846-21.html");
			htmltext = htmltext.replace("%table%", table);
			isNpcWin = true;
		}
		else
		{
			htmltext = getHtm(player, isPractice ? "30846-23.html" : "30846-17.html");
			htmltext = htmltext.replace("%table%", table);
		}
		
		htmltext = htmltext.replaceAll("%player_name%", player.getName());
		htmltext = htmltext.replaceAll("%player_card%", playerCard.toString());
		htmltext = npcCard == null ? htmltext.replaceAll("%npc_card%", "No such card") : htmltext.replaceAll("%npc_card%", npcCard.toString());
		
		if (isPlayerWin)
		{
			reset(false);
		}
		if (isNpcWin)
		{
			reset(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			switch (Rnd.get(0, 5))
			{
				case 0:
				case 1:
				{
					break;
				}
				case 5:
				{
					giveItems(killer, SPIRIT_BEAD, 2);
					break;
				}
				default:
				{
					giveItems(killer, SPIRIT_BEAD, 1);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void reset(boolean resetCounter)
	{
		playerCard = null;
		npcCard = null;
		if (resetCounter)
		{
			winCount = 0;
		}
	}
	
	private Card getRandomCard()
	{
		final int index = Rnd.get(0, 7);
		return Arrays.stream(cards).filter(card -> (!card.equals(playerCard)) && (!card.equals(npcCard))).collect(Collectors.toList()).get(index);
	}
	
	private boolean isWinner(Card playerCard, Card npcCard, boolean playerIsLastDraw)
	{
		if ((npcCard == null) && playerIsLastDraw && (playerCard.getNumber() == 5))
		{
			return true;
		}
		if ((npcCard != null) && (playerCard.getSide().ordinal() == npcCard.getSide().ordinal()))
		{
			return (playerCard.getNumber() + npcCard.getNumber()) == 5;
		}
		else if (playerIsLastDraw)
		{
			return playerCard.getNumber() == 5;
		}
		else if (npcCard != null)
		{
			return npcCard.getNumber() == 5;
		}
		return false;
	}
	
	private void giveRewards(PlayerInstance player, int consecutiveWin)
	{
		switch (consecutiveWin)
		{
			case 1:
			{
				rewardItems(player, 57, 40000);
				break;
			}
			case 2:
			{
				rewardItems(player, 57, 80000);
				break;
			}
			case 3:
			{
				rewardItems(player, 57, 110000);
				rewardItems(player, 955, 1); // Enchant Weapon D
				break;
			}
			case 4:
			{
				rewardItems(player, 57, 199000);
				rewardItems(player, 951, 1); // Enchant Weapon C
				break;
			}
			case 5:
			{
				rewardItems(player, 57, 388000);
				rewardItems(player, getRandomRecipeBGrade(), 1); // Recipe Weapon B grade 60%
				break;
			}
			case 6:
			{
				rewardItems(player, 57, 675000);
				rewardItems(player, getRandomPartBGrade(), 1); // one part for making weapon b grade
				break;
			}
			case 7:
			{
				rewardItems(player, 57, 1284000);
				rewardItems(player, 947, 2); // Enchant Weapon B
				rewardItems(player, 948, 2); // Enchant Armor B
				break;
			}
			case 8:
			{
				rewardItems(player, 57, 2384000);
				rewardItems(player, 729, 1); // Enchant Weapon A
				rewardItems(player, 730, 2); // Enchant Armor A
				break;
			}
		}
	}
	
	private int getRandomRecipeBGrade()
	{
		return RECIPES_B_GRADE[Rnd.get(0, RECIPES_B_GRADE.length - 1)];
	}
	
	private int getRandomPartBGrade()
	{
		return PART_B_GRADE[Rnd.get(0, PART_B_GRADE.length - 1)];
	}
	
	private enum Side
	{
		SUN("Sun card"),
		MOON("Moon card");
		private String _name;
		
		Side(String name)
		{
			_name = name;
		}
		
		public String getName()
		{
			return _name;
		}
	}
	
	private static final class Card
	{
		private final int _number;
		private final Side _side;
		
		public Card(Side side, int number)
		{
			_side = side;
			_number = number;
		}
		
		public Side getSide()
		{
			return _side;
		}
		
		public int getNumber()
		{
			return _number;
		}
		
		@Override
		public boolean equals(Object o)
		{
			return (o instanceof Card) && (((Card) o).getNumber() == _number) && (((Card) o).getSide().ordinal() == _side.ordinal());
		}
		
		@Override
		public String toString()
		{
			return _side == Side.MOON ? "<font color=\"LEVEL\">" + _side.getName() + ": " + _number + "</font>" : "<font color=\"FF0000\">" + _side.getName() + ": " + _number + "</font>";
		}
	}
}
