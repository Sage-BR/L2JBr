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
package quests.Q10814_BefittingOfTheStatus;

import java.util.Arrays;
import java.util.List;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.instancemanager.QuestManager;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;

import quests.Q10811_ExaltedOneWhoFacesTheLimit.Q10811_ExaltedOneWhoFacesTheLimit;

/**
 * Befitting of the Status (10814)
 * @author Gladicek, St3eT
 */
public class Q10814_BefittingOfTheStatus extends Quest
{
	// Npc
	private static final int GALLADUCCI = 30097;
	// Items
	private static final int REPLICA_TIARA = 37804;
	private static final int GALLADUCI_RODEMAI_CERTIFICATE = 45625;
	private static final int[] HATS =
	{
		6844, // Lady's Hairpin
		8184, // Party Hat
		7696, // Daisy Hairpin
		8185, // Chapeau
		6846, // Monocle
		7681, // Outlaw's Eyepatch
		7695, // Forget-me-not Hairpin
		7682, // Maiden's Hairpin
		8916, // Eyepatch
		8188, // Little Angel Wings
		8186, // Artisan's Goggles
		5808, // Party Mask
		8189, // Fairy Antennae
		21892, // Pirate King Hat
		6845, // Pirate's Eyepatch
		13490, // Arrow-pierced Apple
	};
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10814_BefittingOfTheStatus()
	{
		super(10814);
		addStartNpc(GALLADUCCI);
		addTalkId(GALLADUCCI);
		addCondMinLevel(MIN_LEVEL, "30097-09.htm");
		addCondStartedQuest(Q10811_ExaltedOneWhoFacesTheLimit.class.getSimpleName(), "30097-05.htm");
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
			case "30097-02.htm":
			case "30097-03.htm":
			case "30097-06.html":
			{
				htmltext = event;
				break;
			}
			case "30097-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30097-08.html":
			{
				if (qs.isCond(6))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						giveItems(player, REPLICA_TIARA, 1);
						giveItems(player, GALLADUCI_RODEMAI_CERTIFICATE, 1);
						qs.exitQuest(false, true);
						
						final Quest mainQ = QuestManager.getInstance().getQuest(Q10811_ExaltedOneWhoFacesTheLimit.class.getSimpleName());
						if (mainQ != null)
						{
							mainQ.notifyEvent("SUBQUEST_FINISHED_NOTIFY", npc, player);
						}
						htmltext = event;
						break;
					}
					htmltext = getNoQuestLevelRewardMsg(player);
					break;
				}
			}
			case "showItemList":
			{
				htmltext = generateItemListHtml(player, npc);
				break;
			}
			default:
			{
				if (event.startsWith("insertItem_"))
				{
					final int itemId = Integer.parseInt(event.replace("insertItem_", ""));
					if (CommonUtil.contains(HATS, itemId))
					{
						if (hasQuestItems(player, itemId))
						{
							for (int i = 1; i < 5; i++)
							{
								final int slotValue = qs.getMemoStateEx(i);
								
								if (slotValue != 0)
								{
									continue;
								}
								qs.setMemoStateEx(i, itemId);
								break;
							}
							takeItems(player, itemId, 1);
							
							if (qs.getCond() < 6)
							{
								qs.setCond(qs.getCond() + 1);
							}
							
							switch (qs.getCond())
							{
								case 2:
								{
									htmltext = "next-item-01.html";
									break;
								}
								case 3:
								{
									htmltext = "next-item-02.html";
									break;
								}
								case 4:
								{
									htmltext = "next-item-03.html";
									break;
								}
								case 5:
								{
									htmltext = "next-item-04.html";
									break;
								}
								case 6:
								{
									htmltext = "30097-07.html";
									break;
								}
							}
						}
						else
						{
							htmltext = "no-item.html";
						}
					}
				}
			}
		}
		return htmltext;
	}
	
	private String generateItemListHtml(PlayerInstance player, Npc npc)
	{
		String html = null;
		final QuestState qs = getQuestState(player, false);
		if (qs != null)
		{
			final String htmlfile;
			switch (qs.getCond())
			{
				case 1:
				{
					htmlfile = "list-01.html";
					break;
				}
				case 2:
				{
					htmlfile = "list-02.html";
					break;
				}
				case 3:
				{
					htmlfile = "list-03.html";
					break;
				}
				case 4:
				{
					htmlfile = "list-04.html";
					break;
				}
				case 5:
				{
					htmlfile = "list-05.html";
					break;
				}
				default:
				{
					htmlfile = "list-01.html";
				}
			}
			
			final NpcHtmlMessage htmlFile = getNpcHtmlMessage(player, npc, htmlfile);
			final StringBuilder sb = new StringBuilder();
			if (htmlFile != null)
			{
				for (int i = 1; i < 5; i++)
				{
					final int itemId = qs.getMemoStateEx(i);
					final Item item = ItemTable.getInstance().getTemplate(itemId);
					if (item != null)
					{
						htmlFile.replace("%slot" + i + "%", item.getName());
					}
				}
				
				final List<Integer> itemList = Arrays.asList(qs.getMemoStateEx(1), qs.getMemoStateEx(2), qs.getMemoStateEx(3), qs.getMemoStateEx(4), qs.getMemoStateEx(5));
				
				for (int itemId : HATS)
				{
					if (!itemList.contains(itemId))
					{
						final Item item = ItemTable.getInstance().getTemplate(itemId);
						if (item != null)
						{
							sb.append("<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h Quest Q10814_BefittingOfTheStatus insertItem_" + itemId + "\">" + item.getName() + "</Button>");
						}
					}
				}
				htmlFile.replace("%itemList%", sb.toString());
				html = htmlFile.getHtml();
			}
		}
		return html;
	}
	
	private NpcHtmlMessage getNpcHtmlMessage(PlayerInstance player, Npc npc, String fileName)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		final String text = getHtm(player, fileName);
		if (text == null)
		{
			LOGGER.info("Cannot find HTML file for " + Q10814_BefittingOfTheStatus.class.getSimpleName() + " Quest: " + fileName);
			return null;
		}
		html.setHtml(text);
		return html;
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
				htmltext = "30097-01.htm";
				break;
			}
			case State.STARTED:
			{
				if ((qs.getCond() >= 1) && (qs.getCond() < 6))
				{
					htmltext = generateItemListHtml(player, npc);
				}
				else if (qs.isCond(6))
				{
					htmltext = "30097-07.html";
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
}