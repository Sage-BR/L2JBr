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
package org.l2jbr.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.l2jbr.gameserver.data.xml.impl.TeleportersData;
import org.l2jbr.gameserver.enums.InstanceType;
import org.l2jbr.gameserver.enums.TeleportType;
import org.l2jbr.gameserver.instancemanager.CastleManager;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.templates.NpcTemplate;
import org.l2jbr.gameserver.model.holders.TeleporterQuestRecommendationHolder;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.skills.CommonSkill;
import org.l2jbr.gameserver.model.teleporter.TeleportHolder;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jbr.gameserver.util.Util;

/**
 * @author NightMarez
 */
public class TeleporterInstance extends Npc
{
	private static final Logger LOGGER = Logger.getLogger(TeleporterInstance.class.getName());
	
	private static final CommonSkill[] FORBIDDEN_TRANSFORM =
	{
		CommonSkill.FROG_TRANSFORM,
		CommonSkill.CHILD_TRANSFORM,
		CommonSkill.NATIVE_TRANSFORM
	};
	
	private static final Map<Integer, List<TeleporterQuestRecommendationHolder>> QUEST_RECOMENDATIONS = new HashMap<>();
	static
	{
		// @formatter:off
		QUEST_RECOMENDATIONS.put(30006, new ArrayList<>()); // Gatekeeper Milia
		QUEST_RECOMENDATIONS.get(30006).add(new TeleporterQuestRecommendationHolder(30006, "Q00561_BasicMissionHarnakUndergroundRuins", new int[]{3}, "30006-Q561"));
		QUEST_RECOMENDATIONS.put(30134, new ArrayList<>()); // Dark Elf Village Teleport Device
		QUEST_RECOMENDATIONS.get(30134).add(new TeleporterQuestRecommendationHolder(30134, "Q00562_BasicMissionAltarOfEvil", new int[]{4}, "30134-Q562"));
		QUEST_RECOMENDATIONS.put(30256, new ArrayList<>()); // Gatekeeper Bella
		QUEST_RECOMENDATIONS.get(30256).add(new TeleporterQuestRecommendationHolder(30256, "Q00562_BasicMissionAltarOfEvil", new int[]{3}, "30256-Q562"));
		QUEST_RECOMENDATIONS.put(30848, new ArrayList<>()); // Gatekeeper Elisa
		QUEST_RECOMENDATIONS.get(30848).add(new TeleporterQuestRecommendationHolder(30848, "Q00561_BasicMissionHarnakUndergroundRuins", new int[]{2,4}, "30848-Q561-Q562"));
		QUEST_RECOMENDATIONS.get(30848).add(new TeleporterQuestRecommendationHolder(30848, "Q00562_BasicMissionAltarOfEvil", new int[]{2,4}, "30848-Q561-Q562"));
		// @formatter:on
	}
	
	public TeleporterInstance(NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.TeleporterInstance);
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.isMonster() || super.isAutoAttackable(attacker);
	}
	
	@Override
	public void onBypassFeedback(PlayerInstance player, String command)
	{
		// Check if transformed
		for (CommonSkill skill : FORBIDDEN_TRANSFORM)
		{
			if (player.isAffectedBySkill(skill.getId()))
			{
				sendHtmlMessage(player, "data/html/teleporter/epictransformed.htm");
				return;
			}
		}
		
		// Process bypass
		final StringTokenizer st = new StringTokenizer(command, " ");
		switch (st.nextToken())
		{
			case "showNoblesSelect":
			{
				sendHtmlMessage(player, "data/html/teleporter/" + (player.getNobleLevel() > 0 ? "nobles_select" : "not_nobles") + ".htm");
				break;
			}
			case "showTeleports":
			{
				final String listName = (st.hasMoreTokens()) ? st.nextToken() : TeleportType.NORMAL.name();
				final TeleportHolder holder = TeleportersData.getInstance().getHolder(getId(), listName);
				if (holder == null)
				{
					LOGGER.warning("Player " + player.getObjectId() + " requested show teleports for list with name " + listName + " at NPC " + getId() + "!");
					return;
				}
				holder.showTeleportList(player, this);
				break;
			}
			case "showTeleportsHunting":
			{
				final String listName = (st.hasMoreTokens()) ? st.nextToken() : TeleportType.HUNTING.name();
				final TeleportHolder holder = TeleportersData.getInstance().getHolder(getId(), listName);
				if (holder == null)
				{
					LOGGER.warning("Player " + player.getObjectId() + " requested show teleports for hunting list with name " + listName + " at NPC " + getId() + "!");
					return;
				}
				holder.showTeleportList(player, this);
				break;
			}
			case "teleport":
			{
				// Check for required count of params.
				if (st.countTokens() != 2)
				{
					LOGGER.warning("Player " + player.getObjectId() + " send unhandled teleport command: " + command);
					return;
				}
				
				final String listName = st.nextToken();
				final TeleportHolder holder = TeleportersData.getInstance().getHolder(getId(), listName);
				if (holder == null)
				{
					LOGGER.warning("Player " + player.getObjectId() + " requested unknown teleport list: " + listName + " for npc: " + getId() + "!");
					return;
				}
				holder.doTeleport(player, this, parseNextInt(st, -1));
				break;
			}
			case "chat":
			{
				int val = 0;
				try
				{
					val = Integer.parseInt(command.substring(5));
				}
				catch (IndexOutOfBoundsException | NumberFormatException ignored)
				{
				}
				showChatWindow(player, val);
				break;
			}
			default:
			{
				super.onBypassFeedback(player, command);
			}
		}
	}
	
	private int parseNextInt(StringTokenizer st, int defaultVal)
	{
		if (st.hasMoreTokens())
		{
			final String token = st.nextToken();
			if (Util.isDigit(token))
			{
				return Integer.valueOf(token);
			}
		}
		return defaultVal;
	}
	
	@Override
	public String getHtmlPath(int npcId, int val, PlayerInstance player)
	{
		String pom;
		if (val == 0)
		{
			pom = String.valueOf(npcId);
			if ((player != null) && QUEST_RECOMENDATIONS.containsKey(npcId))
			{
				CHECK: for (TeleporterQuestRecommendationHolder rec : QUEST_RECOMENDATIONS.get(npcId))
				{
					final QuestState qs = player.getQuestState(rec.getQuestName());
					if ((qs != null) && qs.isStarted())
					{
						for (int cond : rec.getConditions())
						{
							if ((cond == -1) || qs.isCond(cond))
							{
								pom = rec.getHtml();
								break CHECK;
							}
						}
					}
				}
			}
		}
		else
		{
			pom = (npcId + "-" + val);
		}
		return "data/html/teleporter/" + pom + ".htm";
	}
	
	@Override
	public void showChatWindow(PlayerInstance player)
	{
		// Teleporter isn't on castle ground
		if (CastleManager.getInstance().getCastle(this) == null)
		{
			super.showChatWindow(player);
			return;
		}
		
		// Teleporter is on castle ground
		String filename = "data/html/teleporter/castleteleporter-no.htm";
		if ((player.getClan() != null) && (getCastle().getOwnerId() == player.getClanId())) // Clan owns castle
		{
			filename = getHtmlPath(getId(), 0, player); // Owner message window
		}
		else if (getCastle().getSiege().isInProgress()) // Teleporter is busy due siege
		{
			filename = "data/html/teleporter/castleteleporter-busy.htm"; // Busy because of siege
		}
		sendHtmlMessage(player, filename);
	}
	
	private void sendHtmlMessage(PlayerInstance player, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player, filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
}
