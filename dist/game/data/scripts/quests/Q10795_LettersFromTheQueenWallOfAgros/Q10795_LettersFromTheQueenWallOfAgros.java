package quests.Q10795_LettersFromTheQueenWallOfAgros;

import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Letters from the Queen: Wall of Argos (10795)
 * @URL https://l2wiki.com/Letters_from_the_Queen:_Wall_of_Argos
 * @author Gigi
 */
public class Q10795_LettersFromTheQueenWallOfAgros extends LetterQuest
{
	// NPCs
	private static final int GREGORY = 31279;
	private static final int HERMIT = 31616;
	// Items
	private static final int SOE_WAAL_OF_ARGOS = 39585;
	private static final int SOE_GODDARD = 39584;
	// Misc
	private static final int MIN_LEVEL = 70;
	private static final int MAX_LEVEL = 75;
	// Teleport
	private static final Location TELEPORT_LOC = new Location(147711, -53956, -2728);
	
	public Q10795_LettersFromTheQueenWallOfAgros()
	{
		super(10795);
		addTalkId(GREGORY, HERMIT);
		
		setIsErtheiaQuest(true);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartLocation(SOE_GODDARD, TELEPORT_LOC);
		setStartQuestSound("Npcdialog1.serenia_quest_10");
		registerQuestItems(SOE_GODDARD, SOE_WAAL_OF_ARGOS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "31279-02.html":
			case "31616-02.html":
			{
				htmltext = event;
				break;
			}
			case "31279-03.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					giveItems(player, SOE_WAAL_OF_ARGOS, 1);
					htmltext = event;
				}
				break;
			}
			case "31616-03.html":
			{
				if (qs.isCond(3))
				{
					giveStoryQuestReward(npc, player);
					addExpAndSp(player, 1088640, 261);
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_76, ExShowScreenMessage.TOP_CENTER, 8000);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs == null)
		{
			return htmltext;
		}
		if (qs.isStarted())
		{
			if (npc.getId() == GREGORY)
			{
				htmltext = (qs.isCond(2)) ? "31279-01.html" : "31279-04.html";
			}
			else if (qs.isCond(3))
			{
				htmltext = "31616-01.html";
			}
		}
		return htmltext;
	}
}