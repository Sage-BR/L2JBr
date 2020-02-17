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
package quests.Q00460_PreciousResearchMaterial;

import org.l2jbr.gameserver.enums.QuestType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Precious Research Material (460)
 * @URL https://l2wiki.com/Precious_Research_Material
 * @author Gigi
 */
public class Q00460_PreciousResearchMaterial extends Quest
{
	// NPCs
	private static final int AMER = 33092;
	private static final int FILAUR = 30535;
	// Monster
	private static final int EGG = 18997;
	// Item's
	private static final int PROOF_OF_FIDELITY = 19450; //
	private static final int TEREDOR_EGG_FRAGMENT = 17735;
	// Misc
	private static final int MIN_LEVEL = 85;
	
	public Q00460_PreciousResearchMaterial()
	{
		super(460);
		addStartNpc(AMER);
		addTalkId(AMER, FILAUR);
		addKillId(EGG);
		registerQuestItems(TEREDOR_EGG_FRAGMENT);
		addCondMinLevel(MIN_LEVEL, "30535-00.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		switch (event)
		{
			case "30535-02.html":
			{
				giveItems(player, PROOF_OF_FIDELITY, 3);
				qs.exitQuest(QuestType.DAILY, true);
				htmltext = event;
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
		switch (npc.getId())
		{
			case AMER:
			{
				switch (qs.getState())
				{
					case State.COMPLETED:
					{
						if (!qs.isNowAvailable())
						{
							htmltext = getNoQuestMsg(player);
							break;
						}
						qs.setState(State.CREATED);
					}
					case State.CREATED:
					{
						htmltext = "33092-02.html";
						qs.startQuest();
						break;
					}
					case State.STARTED:
					{
						htmltext = "33092-01.htm";
						break;
					}
				}
				break;
			}
			case FILAUR:
			{
				if (qs.isCond(2) && (getQuestItemsCount(player, TEREDOR_EGG_FRAGMENT) >= 20))
				{
					htmltext = "30535-01.html";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		if (qs != null)
		{
			if (giveItemRandomly(killer, TEREDOR_EGG_FRAGMENT, 1, 20, 0.7, true))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}