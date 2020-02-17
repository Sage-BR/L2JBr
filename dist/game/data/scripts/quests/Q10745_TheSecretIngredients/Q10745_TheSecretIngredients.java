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
package quests.Q10745_TheSecretIngredients;

import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * The Secret Ingredients (10745)
 * @author Sdw
 */
public class Q10745_TheSecretIngredients extends Quest
{
	// NPCs
	private static final int DOLKIN = 33954;
	private static final int KARLA = 33933;
	private static final int DOLKIN_INSTANCE = 34002;
	// Mobs
	private static final int KARAPHON = 23459;
	private static final int KEEN_HONEYBEE = 23460;
	private static final int KEEN_GROWLER = 23461;
	// Items
	private static final ItemHolder DOLKIN_REPORT = new ItemHolder(39534, 1);
	private static final ItemHolder SECRET_INGREDIENTS = new ItemHolder(39533, 1);
	private static final ItemHolder FAERON_SUPPORT_BOX_MAGE = new ItemHolder(40263, 1);
	private static final ItemHolder FAERON_SUPPORT_BOX = new ItemHolder(40262, 1);
	// Misc
	private static final int MIN_LEVEL = 17;
	private static final int MAX_LEVEL = 25;
	
	public Q10745_TheSecretIngredients()
	{
		super(10745);
		addStartNpc(DOLKIN);
		addTalkId(DOLKIN, KARLA);
		addKillId(KARAPHON, KEEN_HONEYBEE, KEEN_GROWLER);
		
		addCondRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33954-00.htm");
		registerQuestItems(SECRET_INGREDIENTS.getId(), DOLKIN_REPORT.getId());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33954-02.htm":
			{
				qs.startQuest();
				break;
			}
			case "33954-05.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
					takeItem(player, SECRET_INGREDIENTS);
					giveItems(player, DOLKIN_REPORT);
				}
				break;
			}
			case "33933-02.html":
			{
				if (qs.isCond(3))
				{
					addExpAndSp(player, 241076, 5);
					if (player.isMageClass())
					{
						giveItems(player, FAERON_SUPPORT_BOX_MAGE);
					}
					else
					{
						giveItems(player, FAERON_SUPPORT_BOX);
					}
					showOnScreenMsg(player, NpcStringId.CHECK_YOUR_EQUIPMENT_IN_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 10000);
					qs.exitQuest(false, true);
				}
				break;
			}
			case "SPAWN_DOLKIN":
			{
				showOnScreenMsg(player, NpcStringId.TALK_TO_DOLKIN_AND_LEAVE_THE_KARAPHON_HABITAT, ExShowScreenMessage.TOP_CENTER, 5000);
				addSpawn(DOLKIN_INSTANCE, npc.getLocation(), false, 0, false, player.getInstanceId());
				htmltext = null;
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = qs.isCompleted() ? getAlreadyCompletedMsg(player) : getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case DOLKIN:
			{
				if (qs.isCreated())
				{
					htmltext = "33954-01.htm";
				}
				else if (qs.isStarted())
				{
					if (qs.isCond(1))
					{
						htmltext = "33954-03.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33954-04.html";
					}
					else if (qs.isCond(3))
					{
						htmltext = "33954-06.html";
					}
				}
				break;
			}
			case KARLA:
			{
				if (qs.isCond(3))
				{
					htmltext = "33933-01.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null)
		{
			int killedMobs = qs.getMemoStateEx(0);
			if (npc.getId() == KARAPHON)
			{
				giveItems(killer, SECRET_INGREDIENTS);
				qs.setCond(2, true);
			}
			if ((++killedMobs) == 3)
			{
				startQuestTimer("SPAWN_DOLKIN", 5000, npc, killer);
			}
			else
			{
				qs.setMemoStateEx(0, killedMobs);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}