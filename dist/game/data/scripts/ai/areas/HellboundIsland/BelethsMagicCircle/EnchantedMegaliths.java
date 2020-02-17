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
package ai.areas.HellboundIsland.BelethsMagicCircle;

import org.l2jbr.gameserver.datatables.SpawnTable;
import org.l2jbr.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.spawns.SpawnTemplate;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.model.zone.type.ScriptZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.Earthquake;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;

import ai.AbstractNpcAI;

/**
 * Beleths Magic Circle
 * @URL https://l2wiki.com/Beleths_Magic_Circle
 * @author Screw, Gigi, Mobius
 * @date 2017-10-26 - [16:06:10]
 */
public class EnchantedMegaliths extends AbstractNpcAI
{
	// NPCs
	private static final int[] MONSTERS =
	{
		19574,
		23354,
		23355,
		23356,
		23357,
		23358,
		23360,
		23361,
		23362,
		23363,
		23364,
		23365,
		23367,
		23368,
		23370,
		23372,
		23373
	};
	// Others
	private static final ScriptZone HELLBOUND_ZONE = ZoneManager.getInstance().getZoneById(40101, ScriptZone.class);
	private static final String HB_MEGALITH_STAGE = "HB_MegalithStage";
	private static final int KILL_COUNT = 300;
	private static SpawnTemplate spawnTemplateNormal;
	private static SpawnTemplate spawnTemplateFinal;
	private static int stage = 1;
	private static int kills = 0;
	
	private EnchantedMegaliths()
	{
		addKillId(MONSTERS);
		addSpawnId(MONSTERS);
		addEnterZoneId(HELLBOUND_ZONE.getId());
		startQuestTimer("CHECK_STATUS", 60000, null, null, true);
		stage = GlobalVariablesManager.getInstance().getInt(HB_MEGALITH_STAGE, 1);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "CHECK_STATUS":
			{
				if (kills > KILL_COUNT)
				{
					if (stage < 3)
					{
						stage++;
					}
					else
					{
						stage = 1;
					}
					updateMegalithStage();
					kills = 0;
				}
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		if (creature.isPlayer())
		{
			final PlayerInstance player = creature.getActingPlayer();
			switch (stage)
			{
				case 1:
				{
					// Activate green glow
					player.sendPacket(new OnEventTrigger(19250032, true));
					player.sendPacket(new OnEventTrigger(19250014, true));
					player.sendPacket(new OnEventTrigger(19250020, true));
					player.sendPacket(new OnEventTrigger(19250026, true));
					player.sendPacket(new OnEventTrigger(19250002, true));
					player.sendPacket(new OnEventTrigger(19250008, true));
					// Deactivate red or yellow glow
					player.sendPacket(new OnEventTrigger(19250034, false));
					player.sendPacket(new OnEventTrigger(19250016, false));
					player.sendPacket(new OnEventTrigger(19250022, false));
					player.sendPacket(new OnEventTrigger(19250028, false));
					player.sendPacket(new OnEventTrigger(19250004, false));
					player.sendPacket(new OnEventTrigger(19250010, false));
					player.sendPacket(new OnEventTrigger(19250036, false));
					player.sendPacket(new OnEventTrigger(19250018, false));
					player.sendPacket(new OnEventTrigger(19250024, false));
					player.sendPacket(new OnEventTrigger(19250030, false));
					player.sendPacket(new OnEventTrigger(19250006, false));
					player.sendPacket(new OnEventTrigger(19250012, false));
					break;
				}
				case 2:
				{
					// Activate yellow glow
					player.sendPacket(new OnEventTrigger(19250034, true));
					player.sendPacket(new OnEventTrigger(19250016, true));
					player.sendPacket(new OnEventTrigger(19250022, true));
					player.sendPacket(new OnEventTrigger(19250028, true));
					player.sendPacket(new OnEventTrigger(19250004, true));
					player.sendPacket(new OnEventTrigger(19250010, true));
					// Deactivate red or green glow
					player.sendPacket(new OnEventTrigger(19250032, false));
					player.sendPacket(new OnEventTrigger(19250014, false));
					player.sendPacket(new OnEventTrigger(19250020, false));
					player.sendPacket(new OnEventTrigger(19250026, false));
					player.sendPacket(new OnEventTrigger(19250002, false));
					player.sendPacket(new OnEventTrigger(19250008, false));
					player.sendPacket(new OnEventTrigger(19250036, false));
					player.sendPacket(new OnEventTrigger(19250018, false));
					player.sendPacket(new OnEventTrigger(19250024, false));
					player.sendPacket(new OnEventTrigger(19250030, false));
					player.sendPacket(new OnEventTrigger(19250006, false));
					player.sendPacket(new OnEventTrigger(19250012, false));
					break;
				}
				case 3:
				{
					// Activate red glow
					player.sendPacket(new OnEventTrigger(19250036, true));
					player.sendPacket(new OnEventTrigger(19250018, true));
					player.sendPacket(new OnEventTrigger(19250024, true));
					player.sendPacket(new OnEventTrigger(19250030, true));
					player.sendPacket(new OnEventTrigger(19250006, true));
					player.sendPacket(new OnEventTrigger(19250012, true));
					// Deactivate green or yellow glow
					player.sendPacket(new OnEventTrigger(19250034, false));
					player.sendPacket(new OnEventTrigger(19250016, false));
					player.sendPacket(new OnEventTrigger(19250022, false));
					player.sendPacket(new OnEventTrigger(19250028, false));
					player.sendPacket(new OnEventTrigger(19250004, false));
					player.sendPacket(new OnEventTrigger(19250010, false));
					player.sendPacket(new OnEventTrigger(19250032, false));
					player.sendPacket(new OnEventTrigger(19250014, false));
					player.sendPacket(new OnEventTrigger(19250020, false));
					player.sendPacket(new OnEventTrigger(19250026, false));
					player.sendPacket(new OnEventTrigger(19250002, false));
					player.sendPacket(new OnEventTrigger(19250008, false));
					break;
				}
			}
			if (spawnTemplateNormal == null)
			{
				updateMegalithStage();
			}
		}
		return super.onEnterZone(creature, zone);
	}
	
	private void updateMegalithStage()
	{
		GlobalVariablesManager.getInstance().set(HB_MEGALITH_STAGE, stage);
		LOGGER.info("[Hellbound] - Enchanted Megaliths stage: " + stage);
		
		switch (stage)
		{
			case 1:
			{
				for (PlayerInstance pl : HELLBOUND_ZONE.getPlayersInside())
				{
					pl.sendPacket(new OnEventTrigger(19250032, true));
					pl.sendPacket(new OnEventTrigger(19250014, true));
					pl.sendPacket(new OnEventTrigger(19250020, true));
					pl.sendPacket(new OnEventTrigger(19250026, true));
					pl.sendPacket(new OnEventTrigger(19250002, true));
					pl.sendPacket(new OnEventTrigger(19250008, true));
					pl.sendPacket(new OnEventTrigger(19250036, false));
					pl.sendPacket(new OnEventTrigger(19250018, false));
					pl.sendPacket(new OnEventTrigger(19250024, false));
					pl.sendPacket(new OnEventTrigger(19250030, false));
					pl.sendPacket(new OnEventTrigger(19250006, false));
					pl.sendPacket(new OnEventTrigger(19250012, false));
					if (spawnTemplateNormal != null)
					{
						pl.sendPacket(new Earthquake(pl.getLocation(), 40, 7));
					}
				}
				
				if (spawnTemplateNormal == null)
				{
					spawnTemplateNormal = SpawnTable.getInstance().getAnySpawn(MONSTERS[9]).getNpcSpawnTemplate().getSpawnTemplate();
					spawnTemplateFinal = SpawnTable.getInstance().getAnySpawn(MONSTERS[12]).getNpcSpawnTemplate().getSpawnTemplate();
					spawnTemplateFinal.despawnAll();
				}
				else
				{
					spawnTemplateNormal.spawnAll();
					spawnTemplateFinal.despawnAll();
				}
				break;
			}
			case 2:
			{
				for (PlayerInstance pl : HELLBOUND_ZONE.getPlayersInside())
				{
					pl.sendPacket(new OnEventTrigger(19250034, true));
					pl.sendPacket(new OnEventTrigger(19250016, true));
					pl.sendPacket(new OnEventTrigger(19250022, true));
					pl.sendPacket(new OnEventTrigger(19250028, true));
					pl.sendPacket(new OnEventTrigger(19250004, true));
					pl.sendPacket(new OnEventTrigger(19250010, true));
					pl.sendPacket(new OnEventTrigger(19250032, false));
					pl.sendPacket(new OnEventTrigger(19250014, false));
					pl.sendPacket(new OnEventTrigger(19250020, false));
					pl.sendPacket(new OnEventTrigger(19250026, false));
					pl.sendPacket(new OnEventTrigger(19250002, false));
					pl.sendPacket(new OnEventTrigger(19250008, false));
					if (spawnTemplateNormal != null)
					{
						pl.sendPacket(new Earthquake(pl.getLocation(), 40, 7));
					}
				}
				
				if (spawnTemplateNormal == null)
				{
					spawnTemplateNormal = SpawnTable.getInstance().getAnySpawn(MONSTERS[9]).getNpcSpawnTemplate().getSpawnTemplate();
					spawnTemplateFinal = SpawnTable.getInstance().getAnySpawn(MONSTERS[12]).getNpcSpawnTemplate().getSpawnTemplate();
					spawnTemplateNormal.despawnAll();
					spawnTemplateNormal.spawnAll();
					spawnTemplateFinal.despawnAll();
				}
				else
				{
					spawnTemplateNormal.despawnAll();
					spawnTemplateNormal.spawnAll();
				}
				break;
			}
			case 3:
			{
				for (PlayerInstance pl : HELLBOUND_ZONE.getPlayersInside())
				{
					pl.sendPacket(new OnEventTrigger(19250036, true));
					pl.sendPacket(new OnEventTrigger(19250018, true));
					pl.sendPacket(new OnEventTrigger(19250024, true));
					pl.sendPacket(new OnEventTrigger(19250030, true));
					pl.sendPacket(new OnEventTrigger(19250006, true));
					pl.sendPacket(new OnEventTrigger(19250012, true));
					pl.sendPacket(new OnEventTrigger(19250034, false));
					pl.sendPacket(new OnEventTrigger(19250016, false));
					pl.sendPacket(new OnEventTrigger(19250022, false));
					pl.sendPacket(new OnEventTrigger(19250028, false));
					pl.sendPacket(new OnEventTrigger(19250004, false));
					pl.sendPacket(new OnEventTrigger(19250010, false));
					if (spawnTemplateNormal != null)
					{
						pl.sendPacket(new Earthquake(pl.getLocation(), 40, 7));
					}
				}
				
				if (spawnTemplateNormal == null)
				{
					spawnTemplateNormal = SpawnTable.getInstance().getAnySpawn(MONSTERS[9]).getNpcSpawnTemplate().getSpawnTemplate();
					spawnTemplateFinal = SpawnTable.getInstance().getAnySpawn(MONSTERS[12]).getNpcSpawnTemplate().getSpawnTemplate();
					spawnTemplateNormal.despawnAll();
				}
				else
				{
					spawnTemplateNormal.despawnAll();
					spawnTemplateFinal.spawnAll();
				}
				break;
			}
		}
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		kills++;
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if ((stage == 2) && (npc.getSpawn().getNpcSpawnTemplate().getSpawnTemplate().getName().equals("enchanted_megaliths_stage_1")))
		{
			npc.setTitleString(NpcStringId.ABNORMAL_MAGIC_CIRCLE);
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new EnchantedMegaliths();
	}
}