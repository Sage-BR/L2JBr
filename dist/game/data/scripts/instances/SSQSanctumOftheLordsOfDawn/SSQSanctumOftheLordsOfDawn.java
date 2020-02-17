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
package instances.SSQSanctumOftheLordsOfDawn;

import java.util.List;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.MagicSkillUse;

import instances.AbstractInstance;
import quests.Q00195_SevenSignsSecretRitualOfThePriests.Q00195_SevenSignsSecretRitualOfThePriests;

/**
 * Sanctum of the Lords of Dawn instance zone.
 * @author Adry_85
 */
public class SSQSanctumOftheLordsOfDawn extends AbstractInstance
{
	// NPCs
	private static final int GUARDS_OF_THE_DAWN = 18834;
	private static final int GUARDS_OF_THE_DAWN_2 = 18835;
	private static final int GUARDS_OF_THE_DAWN_3 = 27351;
	private static final int LIGHT_OF_DAWN = 32575;
	private static final int PASSWORD_ENTRY_DEVICE = 32577;
	private static final int IDENTITY_CONFIRM_DEVICE = 32578;
	private static final int DARKNESS_OF_DAWN = 32579;
	private static final int SHELF = 32580;
	// Item
	private static final int IDENTITY_CARD = 13822;
	// Skill
	private static final SkillHolder GUARD_SKILL = new SkillHolder(5978, 1);
	// Misc
	private static final int TEMPLATE_ID = 111;
	private static final int DOOR_ONE = 17240001;
	private static final int DOOR_TWO = 17240003;
	private static final int DOOR_THREE = 17240005;
	private static final Location[] SAVE_POINT = new Location[]
	{
		new Location(-75775, 213415, -7120),
		new Location(-74959, 209240, -7472),
		new Location(-77699, 208905, -7640),
		new Location(-79939, 205857, -7888),
	};
	
	public SSQSanctumOftheLordsOfDawn()
	{
		super(TEMPLATE_ID);
		addStartNpc(LIGHT_OF_DAWN);
		addTalkId(LIGHT_OF_DAWN, IDENTITY_CONFIRM_DEVICE, PASSWORD_ENTRY_DEVICE, DARKNESS_OF_DAWN, SHELF);
		addAggroRangeEnterId(GUARDS_OF_THE_DAWN, GUARDS_OF_THE_DAWN_2, GUARDS_OF_THE_DAWN_3);
		addInstanceCreatedId(TEMPLATE_ID);
	}
	
	@Override
	public void onInstanceCreated(Instance instance, PlayerInstance player)
	{
		// Spawn guards and set save points
		for (int i = 1; i <= 4; i++)
		{
			final List<Npc> npcs = instance.spawnGroup("save_point" + i);
			for (Npc npc : npcs)
			{
				npc.setScriptValue(i - 1);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "spawn":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					world.spawnGroup("high_priest_of_dawn");
					player.sendPacket(SystemMessageId.BY_USING_THE_INVISIBLE_SKILL_SNEAK_INTO_THE_DAWN_S_DOCUMENT_STORAGE);
				}
				break;
			}
			case "teleportPlayer":
			{
				switch (npc.getId())
				{
					case GUARDS_OF_THE_DAWN:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.INTRUDER_PROTECT_THE_PRIESTS_OF_DAWN);
						break;
					}
					case GUARDS_OF_THE_DAWN_2:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HOW_DARE_YOU_INTRUDE_WITH_THAT_TRANSFORMATION_GET_LOST);
						break;
					}
					case GUARDS_OF_THE_DAWN_3:
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHO_ARE_YOU_A_NEW_FACE_LIKE_YOU_CAN_T_APPROACH_THIS_PLACE);
						break;
					}
				}
				player.teleToLocation(SAVE_POINT[npc.getScriptValue()]);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance talker)
	{
		switch (npc.getId())
		{
			case LIGHT_OF_DAWN:
			{
				final QuestState qs = talker.getQuestState(Q00195_SevenSignsSecretRitualOfThePriests.class.getSimpleName());
				if ((qs != null) && qs.isCond(3) && hasQuestItems(talker, IDENTITY_CARD) && (talker.getTransformationId() == 113))
				{
					enterInstance(talker, npc, TEMPLATE_ID);
					return "32575-01.html";
				}
				return "32575-02.html";
			}
			case IDENTITY_CONFIRM_DEVICE:
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					if (hasQuestItems(talker, IDENTITY_CARD) && (talker.getTransformationId() == 113))
					{
						if (world.isStatus(0))
						{
							talker.sendPacket(SystemMessageId.BY_USING_THE_INVISIBLE_SKILL_SNEAK_INTO_THE_DAWN_S_DOCUMENT_STORAGE);
							talker.sendPacket(SystemMessageId.MALE_GUARDS_CAN_DETECT_THE_CONCEALMENT_BUT_THE_FEMALE_GUARDS_CANNOT);
							talker.sendPacket(SystemMessageId.FEMALE_GUARDS_NOTICE_THE_DISGUISES_FROM_FAR_AWAY_BETTER_THAN_THE_MALE_GUARDS_DO_SO_BEWARE);
							world.openCloseDoor(DOOR_ONE, true);
							world.setStatus(1);
							npc.decayMe();
						}
						else if (world.isStatus(1))
						{
							world.openCloseDoor(DOOR_TWO, true);
							world.setStatus(2);
							npc.decayMe();
							playMovie(talker, Movie.SSQ_RITUAL_OF_PRIEST);
						}
						return "32578-01.html";
					}
					return "32578-02.html";
				}
				break;
			}
			case PASSWORD_ENTRY_DEVICE:
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					world.openCloseDoor(DOOR_THREE, true);
					return "32577-01.html";
				}
				break;
			}
			case DARKNESS_OF_DAWN:
			{
				finishInstance(talker, 0);
				return "32579-01.html";
			}
			case SHELF:
			{
				finishInstance(talker);
				talker.teleToLocation(-75925, 213399, -7128);
				return "32580-01.html";
			}
		}
		return "";
	}
	
	@Override
	public String onAggroRangeEnter(Npc npc, PlayerInstance player, boolean isSummon)
	{
		npc.broadcastPacket(new MagicSkillUse(npc, player, GUARD_SKILL.getSkillId(), 1, 2000, 1));
		startQuestTimer("teleportPlayer", 2000, npc, player);
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	public static void main(String[] args)
	{
		new SSQSanctumOftheLordsOfDawn();
	}
}