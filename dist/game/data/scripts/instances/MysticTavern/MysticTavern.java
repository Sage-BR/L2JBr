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
package instances.MysticTavern;

import java.util.ArrayList;
import java.util.List;

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.instancemanager.InstanceManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.zone.type.ScriptZone;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.NpcSay;
import org.l2jbr.gameserver.network.serverpackets.OnEventTrigger;
import org.l2jbr.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;
import instances.MysticTavern.StoryOfFreya.StoryOfFreya;
import instances.MysticTavern.StoryOfTauti.StoryOfTauti;
import quests.Q10297_GrandOpeningComeToOurPub.Q10297_GrandOpeningComeToOurPub;

/**
 * This AI manages the entry to the Mystic Tavern instances.
 * @URL https://l2wiki.com/Mystic_Tavern
 * @VIDEO FREYA: https://www.youtube.com/watch?v=-pUB6ghrsLI
 * @VIDEO TAUTI: https://www.youtube.com/watch?v=_Wz-kxXzJK4
 * @VIDEO KELBIM: https://www.youtube.com/watch?v=wL1D49u6vxE
 * @author Mobius, Gigi
 */
public class MysticTavern extends AbstractNpcAI
{
	// NPC
	private static final int GLOBE = 34200;
	// Employee
	private static final int LOLLIA = 34182;
	private static final int HANNA = 34183;
	private static final int BRODIEN = 34184;
	private static final int LUPIA = 34185;
	private static final int MEY = 34186;
	// Instances
	private static final int INSTANCE_TAUTI = 261;
	// private static final int INSTANCE_KELBIM = 262;
	private static final int INSTANCE_FREYA = 263;
	// Zones
	private static final ScriptZone GLOBE_1_ZONE = ZoneManager.getInstance().getZoneById(80019, ScriptZone.class);
	private static final ScriptZone GLOBE_2_ZONE = ZoneManager.getInstance().getZoneById(80020, ScriptZone.class);
	private static final ScriptZone GLOBE_3_ZONE = ZoneManager.getInstance().getZoneById(80021, ScriptZone.class);
	private static final ScriptZone GLOBE_4_ZONE = ZoneManager.getInstance().getZoneById(80022, ScriptZone.class);
	private static final ScriptZone GLOBE_5_ZONE = ZoneManager.getInstance().getZoneById(80023, ScriptZone.class);
	// Misc
	private static final int MINIMUM_PLAYER_LEVEL = 99;
	private static final int MINIMUM_PARTY_MEMBERS = 5;
	private static Npc _lollia;
	private static Npc _hanna;
	private static Npc _brodien;
	private static Npc _lupia;
	private static Npc _mey;
	
	public MysticTavern()
	{
		addFirstTalkId(GLOBE);
		addSpawnId(LOLLIA, HANNA, BRODIEN, LUPIA, MEY);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "tellStory":
			{
				if (!player.isGM())
				{
					final Party party = player.getParty();
					if (party == null)
					{
						return "34200-no-party.html";
					}
					if (party.getLeader() != player)
					{
						return "34200-no-leader.html";
					}
					if (party.getMemberCount() < MINIMUM_PARTY_MEMBERS)
					{
						return "34200-not-enough-members.html";
					}
					final List<Integer> availableInstances = new ArrayList<>();
					availableInstances.add(INSTANCE_FREYA);
					availableInstances.add(INSTANCE_TAUTI);
					// availableInstances.add(INSTANCE_KELBIM);
					for (PlayerInstance member : party.getMembers())
					{
						if ((member == null) || !member.isSitting() || (member.calculateDistance3D(player) > 500))
						{
							return "34200-not-sitting.html";
						}
						if (member.getLevel() < MINIMUM_PLAYER_LEVEL)
						{
							return "34200-no-level.html";
						}
						final QuestState qs = member.getQuestState(Q10297_GrandOpeningComeToOurPub.class.getSimpleName());
						if ((qs == null) || !qs.isCompleted())
						{
							return "34200-no-quest.html";
						}
						if (InstanceManager.getInstance().getInstanceTime(member, INSTANCE_FREYA) > 0)
						{
							for (int i = 0; i < availableInstances.size(); i++)
							{
								if (availableInstances.get(i) == INSTANCE_FREYA)
								{
									availableInstances.remove(i);
								}
							}
						}
						if (InstanceManager.getInstance().getInstanceTime(member, INSTANCE_TAUTI) > 0)
						{
							for (int i = 0; i < availableInstances.size(); i++)
							{
								if (availableInstances.get(i) == INSTANCE_TAUTI)
								{
									availableInstances.remove(i);
								}
							}
						}
						// if (InstanceManager.getInstance().getInstanceTime(member, INSTANCE_KELBIM) > 0)
						// {
						// for (int i = 0; i < availableInstances.size(); i++)
						// {
						// if (availableInstances.get(i) == INSTANCE_KELBIM)
						// {
						// availableInstances.remove(i);
						// }
						// }
						// }
					}
					if (availableInstances.isEmpty())
					{
						return "34200-not-available.html";
					}
					npc.setScriptValue(getRandom(availableInstances.size()));
					startQuestTimer("npcRoute", 3000, npc, player);
				}
				break;
			}
			case "npcRoute":
			{
				if (GLOBE_1_ZONE.isInsideZone(npc))
				{
					npc.broadcastPacket(new OnEventTrigger(18133000, true));
					_brodien.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(-50000, -148560, -14152));
					_brodien.setHeading(48440);
					startQuestTimer("msg_text_brodien_1", 16000, _brodien, player);
					startQuestTimer("msg_text_brodien_2", 23000, _brodien, player);
					startQuestTimer("msg_text_brodien_3", 31000, _brodien, player);
				}
				else if (GLOBE_2_ZONE.isInsideZone(npc))
				{
					npc.broadcastPacket(new OnEventTrigger(18132000, true));
					_lupia.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(-50161, -148356, -14152));
					_lupia.setHeading(45808);
					startQuestTimer("msg_text_lupia_1", 12000, _lupia, player);
					startQuestTimer("msg_text_lupia_2", 19000, _lupia, player);
					startQuestTimer("msg_text_lupia_3", 27000, _lupia, player);
				}
				else if (GLOBE_3_ZONE.isInsideZone(npc))
				{
					npc.broadcastPacket(new OnEventTrigger(18131000, true));
					_mey.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(-50092, -148096, -14152));
					_mey.setHeading(34669);
					startQuestTimer("msg_text_mey_1", 8000, _mey, player);
					startQuestTimer("msg_text_mey_2", 15000, _mey, player);
					startQuestTimer("msg_text_mey_3", 23000, _mey, player);
				}
				else if (GLOBE_4_ZONE.isInsideZone(npc))
				{
					npc.broadcastPacket(new OnEventTrigger(18135000, true));
					_lollia.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(-49480, -148074, -14152));
					_lollia.setHeading(13255);
					startQuestTimer("msg_text_lollia_1", 10000, _lollia, player);
					startQuestTimer("msg_text_lollia_2", 17000, _lollia, player);
					startQuestTimer("msg_text_lollia_3", 25000, _lollia, player);
				}
				else if (GLOBE_5_ZONE.isInsideZone(npc))
				{
					npc.broadcastPacket(new OnEventTrigger(18134000, true));
					_hanna.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(-49283, -148179, -14152));
					_hanna.setHeading(26747);
					startQuestTimer("msg_text_hanna_1", 12000, _hanna, player);
					startQuestTimer("msg_text_hanna_2", 19000, _hanna, player);
					startQuestTimer("msg_text_hanna_3", 27000, _hanna, player);
				}
				break;
			}
			case "msg_text_brodien_1":
			{
				_brodien.broadcastPacket(new NpcSay(_brodien.getObjectId(), ChatType.NPC_GENERAL, _brodien.getId(), NpcStringId.I_HAVE_MANY_STORIES_TO_TELL));
				GLOBE_1_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.brodien_inzone_1", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_brodien_2":
			{
				_brodien.broadcastPacket(new NpcSay(_brodien.getObjectId(), ChatType.NPC_GENERAL, _brodien.getId(), NpcStringId.PLEASE_SIT_DOWN_SO_THAT_I_CAN_START));
				GLOBE_1_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.brodien_inzone_2", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_brodien_3":
			{
				_brodien.broadcastPacket(new NpcSay(_brodien.getObjectId(), ChatType.NPC_GENERAL, _brodien.getId(), NpcStringId.WELL_WHOSE_STORY_SHOULD_I_TELL_YOU_TODAY));
				GLOBE_1_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.brodien_inzone_3", 0, 0, 0, 0, 0));
				startQuestTimer("enter_instance", 3000, _brodien, player);
				startQuestTimer("return", 6000, _brodien, null);
				npc.broadcastPacket(new OnEventTrigger(18133000, false));
				break;
			}
			case "msg_text_lupia_1":
			{
				_lupia.broadcastPacket(new NpcSay(_lupia.getObjectId(), ChatType.NPC_GENERAL, _lupia.getId(), NpcStringId.HURRY_SIT_DOWN));
				GLOBE_2_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.rupia_inzone_1", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_lupia_2":
			{
				_lupia.broadcastPacket(new NpcSay(_lupia.getObjectId(), ChatType.NPC_GENERAL, _lupia.getId(), NpcStringId.WHOSE_STORY_DO_YOU_WANT_TO_HEAR));
				GLOBE_2_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.rupia_inzone_2", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_lupia_3":
			{
				_lupia.broadcastPacket(new NpcSay(_lupia.getObjectId(), ChatType.NPC_GENERAL, _lupia.getId(), NpcStringId.YOU_HAVE_TO_BE_READY));
				GLOBE_2_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.rupia_inzone_3", 0, 0, 0, 0, 0));
				startQuestTimer("enter_instance", 3000, _lupia, player);
				startQuestTimer("return", 6000, _lupia, null);
				npc.broadcastPacket(new OnEventTrigger(18132000, false));
				break;
			}
			case "msg_text_mey_1":
			{
				_mey.broadcastPacket(new NpcSay(_mey.getObjectId(), ChatType.NPC_GENERAL, _mey.getId(), NpcStringId.SHOULD_I_START_LET_S_SEE_IF_WE_RE_READY));
				GLOBE_3_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.mae_inzone_1", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_mey_2":
			{
				_mey.broadcastPacket(new NpcSay(_mey.getObjectId(), ChatType.NPC_GENERAL, _mey.getId(), NpcStringId.I_LL_BE_STARTING_NOW_SO_TAKE_A_SEAT));
				GLOBE_3_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.mae_inzone_2", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_mey_3":
			{
				_mey.broadcastPacket(new NpcSay(_mey.getObjectId(), ChatType.NPC_GENERAL, _mey.getId(), NpcStringId.WHICH_STORY_DO_YOU_WANT_TO_HEAR));
				GLOBE_3_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.mae_inzone_3", 0, 0, 0, 0, 0));
				startQuestTimer("enter_instance", 3000, _mey, player);
				startQuestTimer("return", 6000, _mey, null);
				npc.broadcastPacket(new OnEventTrigger(18131000, false));
				break;
			}
			case "msg_text_lollia_1":
			{
				_lollia.broadcastPacket(new NpcSay(_lollia.getObjectId(), ChatType.NPC_GENERAL, _lollia.getId(), NpcStringId.ARE_YOU_READY_TO_HEAR_THE_STORY));
				GLOBE_4_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.lollia_inzone_1", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_lollia_2":
			{
				_lollia.broadcastPacket(new NpcSay(_lollia.getObjectId(), ChatType.NPC_GENERAL, _lollia.getId(), NpcStringId.I_LL_START_ONCE_EVERYONE_IS_SEATED));
				GLOBE_4_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.lollia_inzone_2", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_lollia_3":
			{
				_lollia.broadcastPacket(new NpcSay(_lollia.getObjectId(), ChatType.NPC_GENERAL, _lollia.getId(), NpcStringId.HEH_WHAT_SHOULD_I_TALK_ABOUT_NEXT_HMM));
				GLOBE_4_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.lollia_inzone_3", 0, 0, 0, 0, 0));
				startQuestTimer("enter_instance", 3000, _lollia, player);
				startQuestTimer("return", 6000, _lollia, null);
				npc.broadcastPacket(new OnEventTrigger(18135000, false));
				break;
			}
			case "msg_text_hanna_1":
			{
				_hanna.setHeading(26747);
				_hanna.broadcastPacket(new NpcSay(_hanna.getObjectId(), ChatType.NPC_GENERAL, _hanna.getId(), NpcStringId.WELL_WHICH_STORY_DO_YOU_WANT_TO_HEAR));
				GLOBE_5_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.hanna_inzone_1", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_hanna_2":
			{
				_hanna.broadcastPacket(new NpcSay(_hanna.getObjectId(), ChatType.NPC_GENERAL, _hanna.getId(), NpcStringId.I_WONDER_WHAT_KIND_OF_STORIES_ARE_POPULAR_WITH_THE_CUSTOMERS));
				GLOBE_5_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.hanna_inzone_2", 0, 0, 0, 0, 0));
				break;
			}
			case "msg_text_hanna_3":
			{
				_hanna.broadcastPacket(new NpcSay(_hanna.getObjectId(), ChatType.NPC_GENERAL, _hanna.getId(), NpcStringId.SIT_DOWN_FIRST_I_CAN_T_START_OTHERWISE));
				GLOBE_5_ZONE.broadcastPacket(new PlaySound(3, "Npcdialog1.hanna_inzone_3", 0, 0, 0, 0, 0));
				startQuestTimer("enter_instance", 3000, _hanna, player);
				startQuestTimer("return", 6000, _hanna, null);
				npc.broadcastPacket(new OnEventTrigger(18134000, false));
				break;
			}
			case "return":
			{
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, npc.getSpawn());
				npc.setHeading(npc.getSpawn().getHeading());
				break;
			}
			case "enter_instance":
			{
				switch (npc.getScriptValue())
				{
					case INSTANCE_FREYA:
					{
						player.processQuestEvent(StoryOfFreya.class.getSimpleName(), "start_story");
						break;
					}
					case INSTANCE_TAUTI:
					{
						player.processQuestEvent(StoryOfTauti.class.getSimpleName(), "start_story");
						break;
					}
					// case INSTANCE_KELBIM:
					// {
					// player.processQuestEvent(StoryOfKelbim.class.getSimpleName(), "start_story");
					// break;
					// }
				}
				npc.setScriptValue(0);
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		switch (npc.getId())
		{
			case LOLLIA:
			{
				_lollia = npc;
				break;
			}
			case HANNA:
			{
				_hanna = npc;
				break;
			}
			case BRODIEN:
			{
				_brodien = npc;
				break;
			}
			case LUPIA:
			{
				_lupia = npc;
				break;
			}
			case MEY:
			{
				_mey = npc;
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "34200.html";
	}
	
	public static void main(String[] args)
	{
		new MysticTavern();
	}
}
