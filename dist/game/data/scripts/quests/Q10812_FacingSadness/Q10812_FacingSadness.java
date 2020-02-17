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
package quests.Q10812_FacingSadness;

import org.l2jbr.Config;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.ItemHolder;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

import quests.Q10811_ExaltedOneWhoFacesTheLimit.Q10811_ExaltedOneWhoFacesTheLimit;

/**
 * Facing Sadness (10812)
 * @author Stayway
 */
public class Q10812_FacingSadness extends Quest
{
	// Npc
	private static final int ELIKIA = 31620;
	// Items
	private static final int ELIKIA_CERTIFICATE = 45623;
	private static final int PROOF_OF_DISPOSAL = 45871;
	private static final ItemHolder LIONEL_HUNTERS_LIST_PART_1 = new ItemHolder(45627, 1);
	// Mobs
	private static final int[] MONSTERS =
	{
		// Hellbound Mobs
		23811, // Cantera Tanya
		23812, // Cantera Deathmoz
		23813, // Cantera Floxis
		23814, // Cantera Belika
		23815, // Cantera Bridget
		23354, // Decay Hannibal
		23355, // Armor Beast
		23356, // Klein Soldier
		23357, // Disorder Warrior
		23358, // Blow Archer
		23360, // Bizuard
		23361, // Mutated Fly
		23362, // Amos Soldier
		23363, // Amos Officer
		23364, // Amos Master
		23365, // Ailith Hunter
		23366, // Durable Charger
		23367, // Armor Beast
		23368, // Klein Soldier
		23369, // Disorder Warrior
		23370, // Blow Archer
		23372, // Bizuard
		23373, // Mutated Fly
		23384, // Smaug
		23385, // Lunatikan
		23386, // Jabberwok
		23387, // Kanzaroth
		23388, // Kandiloth
		23393, // Slaver
		23394, // Slaver
		23395, // Garion
		23396, // Garion Neti
		23397, // Desert Wendigo
		23398, // Koraza
		23399, // Bend Beetle
		19574, // Cowing
		// Raider's Crossroads Mobs
		23314, // Nerva Orc Raider
		23315, // Nerva Orc Archer
		23316, // Nerva Orc Priest
		23317, // Nerva Orc Wizard
		23318, // Nerva Orc Assassin
		23319, // Nerva Orc Ambusher
		23320, // Nerva Orc Merchant
		23321, // Nerva Orc Warrior
		23322, // Nerva Orc Prefect
		23323, // Nerva Orc Elite
		23324, // Nerva Kaiser
		29291, // Nerva Orc Raider
		29292, // Nerva Orc Elite
		29296, // Nerva Orc Assassin
		29297, // Nerva Orc Ambusher
	};
	
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10812_FacingSadness()
	{
		super(10812);
		addStartNpc(ELIKIA);
		addTalkId(ELIKIA);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LEVEL, "31620-09.htm");
		addCondStartedQuest(Q10811_ExaltedOneWhoFacesTheLimit.class.getSimpleName(), "31620-06.htm");
		registerQuestItems(PROOF_OF_DISPOSAL);
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
			case "31620-02.htm":
			case "31620-03.htm":
			{
				htmltext = event;
				break;
			}
			case "31620-04.html":
			{
				if (hasItem(player, LIONEL_HUNTERS_LIST_PART_1))
				{
					qs.startQuest();
					htmltext = event;
					break;
				}
			}
			case "31620-08.html":
			{
				if (qs.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						takeItems(player, PROOF_OF_DISPOSAL, -1);
						giveItems(player, ELIKIA_CERTIFICATE, 1);
						addExpAndSp(player, 0, 498204432);
						qs.exitQuest(false, true);
						
						final Quest mainQ = QuestManager.getInstance().getQuest(Q10811_ExaltedOneWhoFacesTheLimit.class.getSimpleName());
						if (mainQ != null)
						{
							mainQ.notifyEvent("SUBQUEST_FINISHED_NOTIFY", npc, player);
						}
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (hasItem(player, LIONEL_HUNTERS_LIST_PART_1))
				{
					htmltext = "31620-01.htm";
				}
				else
				{
					htmltext = "noItem.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "31620-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "31620-07.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(PlayerInstance player, Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE) && CommonUtil.contains(MONSTERS, npc.getId()))
		{
			giveItems(player, PROOF_OF_DISPOSAL, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			
			if (getQuestItemsCount(player, PROOF_OF_DISPOSAL) >= 8000)
			{
				qs.setCond(2, true);
			}
		}
	}
}