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
package ai.areas.DenOfEvil;

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.zone.type.EffectZone;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.scripting.annotations.Disabled;

import ai.AbstractNpcAI;

/**
 * Dummy AI for spawns/respawns only for testing.
 * @author Gnacik
 */
@Disabled // Mobius: this needs to be rewritten.
public class DenOfEvil extends AbstractNpcAI
{
	// private static final int _buffer_id = 32656;
	protected static final int[] EYE_IDS =
	{
		18812,
		18813,
		18814
	};
	private static final int SKILL_ID = 6150; // others +2
	
	private static final Location[] EYE_SPAWNS =
	{
		new Location(71544, -129400, -3360, 16472),
		new Location(70954, -128854, -3360, 16),
		new Location(72145, -128847, -3368, 32832),
		new Location(76147, -128372, -3144, 16152),
		new Location(71573, -128309, -3360, 49152),
		new Location(75211, -127441, -3152, 0),
		new Location(77005, -127406, -3144, 32784),
		new Location(75965, -126486, -3144, 49120),
		new Location(70972, -126429, -3016, 19208),
		new Location(69916, -125838, -3024, 2840),
		new Location(71658, -125459, -3016, 35136),
		new Location(70605, -124646, -3040, 52104),
		new Location(67283, -123237, -2912, 12376),
		new Location(68383, -122754, -2912, 27904),
		new Location(74137, -122733, -3024, 13272),
		new Location(66736, -122007, -2896, 60576),
		new Location(73289, -121769, -3024, 1024),
		new Location(67894, -121491, -2912, 43872),
		new Location(75530, -121477, -3008, 34424),
		new Location(74117, -120459, -3024, 52344),
		new Location(69608, -119855, -2534, 17251),
		new Location(71014, -119027, -2520, 31904),
		new Location(68944, -118964, -2527, 59874),
		new Location(62261, -118263, -3072, 12888),
		new Location(70300, -117942, -2528, 46208),
		new Location(74312, -117583, -2272, 15280),
		new Location(63276, -117409, -3064, 24760),
		new Location(68104, -117192, -2168, 15888),
		new Location(73758, -116945, -2216, 0),
		new Location(74944, -116858, -2220, 30892),
		new Location(61715, -116623, -3064, 59888),
		new Location(69140, -116464, -2168, 28952),
		new Location(67311, -116374, -2152, 1280),
		new Location(62459, -116370, -3064, 48624),
		new Location(74475, -116260, -2216, 47456),
		new Location(68333, -115015, -2168, 45136),
		new Location(68280, -108129, -1160, 17992),
		new Location(62983, -107259, -2384, 12552),
		new Location(67062, -107125, -1144, 64008),
		new Location(68893, -106954, -1160, 36704),
		new Location(63848, -106771, -2384, 32784),
		new Location(62372, -106514, -2384, 0),
		new Location(67838, -106143, -1160, 51232),
		new Location(62905, -106109, -2384, 51288)
	};
	
	private DenOfEvil()
	{
		addKillId(EYE_IDS);
		addSpawnId(EYE_IDS);
		for (Location loc : EYE_SPAWNS)
		{
			addSpawn(getRandomEntry(EYE_IDS), loc, false, 0);
		}
	}
	
	private int getSkillIdByNpcId(int npcId)
	{
		int diff = npcId - EYE_IDS[0];
		diff *= 2;
		return SKILL_ID + diff;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.disableCoreAI(true);
		npc.setIsImmobilized(true);
		final EffectZone zone = ZoneManager.getInstance().getZone(npc, EffectZone.class);
		if (zone == null)
		{
			LOGGER.warning("NPC " + npc + " spawned outside of EffectZone, check your zone coords! X:" + npc.getX() + " Y:" + npc.getY() + " Z:" + npc.getZ());
			return null;
		}
		final int skillId = getSkillIdByNpcId(npc.getId());
		final int skillLevel = zone.getSkillLevel(skillId);
		zone.addSkill(skillId, skillLevel + 1);
		if (skillLevel == 3) // 3+1=4
		{
			ThreadPool.schedule(new KashaDestruction(zone), 2 * 60 * 1000);
			zone.broadcastPacket(new SystemMessage(SystemMessageId.DEFEAT_KASHA_S_EYES_TO_LIFT_THE_GREAT_CURSE));
		}
		else if (skillLevel == 2)
		{
			zone.broadcastPacket(new SystemMessage(SystemMessageId.A_GREAT_CURSE_CAN_BE_FELT_FROM_KASHA_S_EYES));
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		ThreadPool.schedule(new RespawnNewEye(npc.getLocation()), 15000);
		final EffectZone zone = ZoneManager.getInstance().getZone(npc, EffectZone.class);
		if (zone == null)
		{
			LOGGER.warning("NPC " + npc + " killed outside of EffectZone, check your zone coords! X:" + npc.getX() + " Y:" + npc.getY() + " Z:" + npc.getZ());
			return null;
		}
		final int skillId = getSkillIdByNpcId(npc.getId());
		final int skillLevel = zone.getSkillLevel(skillId);
		zone.addSkill(skillId, skillLevel - 1);
		return super.onKill(npc, killer, isSummon);
	}
	
	private class RespawnNewEye implements Runnable
	{
		private final Location _loc;
		
		public RespawnNewEye(Location loc)
		{
			_loc = loc;
		}
		
		@Override
		public void run()
		{
			addSpawn(getRandomEntry(EYE_IDS), _loc, false, 0);
		}
	}
	
	private class KashaDestruction implements Runnable
	{
		EffectZone _zone;
		
		public KashaDestruction(EffectZone zone)
		{
			_zone = zone;
		}
		
		@Override
		public void run()
		{
			for (int i = SKILL_ID; i <= (SKILL_ID + 4); i += 2)
			{
				// test 3 skills if some is lvl 4
				if (_zone.getSkillLevel(i) > 3)
				{
					destroyZone();
					break;
				}
			}
		}
		
		private void destroyZone()
		{
			for (Creature creature : _zone.getCharactersInside())
			{
				if (creature == null)
				{
					continue;
				}
				if (creature.isPlayable())
				{
					final Skill skill = SkillData.getInstance().getSkill(6149, 1);
					skill.applyEffects(creature, creature);
				}
				else if (creature.doDie(null)) // mobs die
				{
					if (creature.isNpc())
					{
						// respawn eye
						final Npc npc = (Npc) creature;
						if (CommonUtil.contains(EYE_IDS, npc.getId()))
						{
							ThreadPool.schedule(new RespawnNewEye(npc.getLocation()), 15000);
						}
					}
				}
			}
			for (int i = SKILL_ID; i <= (SKILL_ID + 4); i += 2)
			{
				_zone.removeSkill(i);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new DenOfEvil();
	}
}
