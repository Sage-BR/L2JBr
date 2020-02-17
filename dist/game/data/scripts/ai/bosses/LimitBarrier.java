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
package ai.bosses;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.AbstractNpcAI;

/**
 * Limit Barrier AI
 * @author RobikBobik<br>
 *         OK - Raid Bosses lvl 100 and higher from now on use "Limit Barrier" skill when their HP reaches 90%, 60% and 30%.<br>
 *         OK - 600 hits in 15 seconds are required to destroy the barrier. Amount of damage does not matter.<br>
 *         OK - If barrier destruction is failed, Boss restores full HP.<br>
 *         OK - Epic Bosses Orfen, Queen Ant and Core also use Limit Barrier.<br>
 *         OK - Epic Bosses Antharas, Zaken and Baium and their analogues in instance zones do not use Limit Barrier.<br>
 *         OK - Raid Bosses in instances do not use Limit Barrier.<br>
 *         OK - All Raid Bosses who use Limit Barrier are listed below:<br>
 */
public final class LimitBarrier extends AbstractNpcAI
{
	// NPCs
	private static final int[] RAID_BOSSES =
	{
		29325, // Orfen
		29001, // Queen Ant
		29006, // Core
		26044, // Evil Magikus
		26045, // Kerfaus
		26046, // Milinaus
		26047, // Sarkaus
		26048, // Shimeus
		26049, // Evil Kinigos
		26131, // Isabella
		26137, // Mimir
		26162, // Demon Worshipper Dorgon
		26163, // Demon Worshipper Zarka
		26164, // Demon Worshipper Kultaan
		26165, // Demon Worshipper Nortan
		26170, // Valac
		26171, // Kurion
		26172, // Atluum
		26173, // Lebruum
		26178, // Berserker Hard
		26179, // Patrol Commander Pho
		26180, // Guardian Gastra
		26181, // Dark Wizard Ruby
		26186, // Wandering Croamis
		26187, // Wandering Harpe
		26188, // Wandering Barrena
		26189, // Wandering Purka
		26277, // Ashen Shadow Expeditionary Force Leaders Kantu
		26278, // Ashen Shadow Expeditionary Force Leaders Kai
		26279, // Ashen Shadow Expeditionary Force Leaders Heine
		26280, // Ashen Shadow Expeditionary Force Leaders Xenon
		26285, // Blood Devil Brutal
		26286, // Blood Devil Akun
		26287, // Blood Devil Epica
		26288, // Blood Devil Julia
		26293, // Valiant Edgar
		26294, // Vicious Blood
		26295, // Coquette Morrigan
		26296, // Cunning Croby
		26301, // Evil Spirit Knight Tretan
		26302, // Evil Spirit Knight Boros
		26303, // Evil Spirit Alusion
		26304, // Evil Spirits Puppet Graff
		26372, // Corrupted Atrofa
		26373, // Corrupted Arbor
		26374, // Corrupted Falena
		26375, // Corrupted Arimus
		26380, // Spirit of Time Arachine
		26381, // Spirit of Time Tisraki
		26382, // Spirit of Time Nympia
		26383, // Spirit of Time Lafros
		29374, // Cyrax
	};
	// Skill
	private static final SkillHolder LIMIT_BARRIER = new SkillHolder(32203, 1);
	// Misc
	private static final int HIT_COUNT = 600;
	private static final Map<Npc, Integer> RAIDBOSS_HITS = new ConcurrentHashMap<>();
	
	private LimitBarrier()
	{
		addAttackId(RAID_BOSSES);
		addKillId(RAID_BOSSES);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "RESTORE_FULL_HP":
			{
				final int hits = RAIDBOSS_HITS.getOrDefault(npc, 0);
				if (hits < HIT_COUNT)
				{
					if (player != null)
					{
						npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.YOU_HAVE_FAILED_TO_DESTROY_THE_LIMIT_BARRIER_NTHE_RAID_BOSS_FULLY_RECOVERS_ITS_CON, 2, 5000, true));
					}
					npc.setCurrentHp(npc.getStat().getMaxHp(), true);
					npc.stopSkillEffects(true, LIMIT_BARRIER.getSkillId());
					RAIDBOSS_HITS.put(npc, 0);
				}
				else if (hits > HIT_COUNT)
				{
					if (player != null)
					{
						npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.YOU_HAVE_DESTROYED_THE_LIMIT_BARRIER, 2, 5000, true));
					}
					npc.stopSkillEffects(true, LIMIT_BARRIER.getSkillId());
					RAIDBOSS_HITS.put(npc, 0);
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		if (npc.isAffectedBySkill(LIMIT_BARRIER.getSkillId()))
		{
			final int hits = RAIDBOSS_HITS.getOrDefault(npc, 0);
			RAIDBOSS_HITS.put(npc, hits + 1);
		}
		
		if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.9)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.87)))
		{
			if (!npc.isAffectedBySkill(LIMIT_BARRIER.getSkillId()))
			{
				npc.setTarget(npc);
				npc.abortAttack();
				npc.abortCast();
				npc.doCast(LIMIT_BARRIER.getSkill());
				npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_RAID_BOSS_USES_THE_LIMIT_BARRIER_NFOCUS_YOUR_ATTACKS_TO_DESTROY_THE_LIMIT_BARRIER_IN_15_SECONDS, 2, 5000, true));
				startQuestTimer("RESTORE_FULL_HP", 15000, npc, attacker);
			}
		}
		else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.6)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.58)))
		{
			if (!npc.isAffectedBySkill(LIMIT_BARRIER.getSkillId()))
			{
				npc.setTarget(npc);
				npc.abortAttack();
				npc.abortCast();
				npc.doCast(LIMIT_BARRIER.getSkill());
				npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_RAID_BOSS_USES_THE_LIMIT_BARRIER_NFOCUS_YOUR_ATTACKS_TO_DESTROY_THE_LIMIT_BARRIER_IN_15_SECONDS, 2, 5000, true));
				startQuestTimer("RESTORE_FULL_HP", 15000, npc, attacker);
			}
		}
		else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.3)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.28)))
		{
			if (!npc.isAffectedBySkill(LIMIT_BARRIER.getSkillId()))
			{
				npc.setTarget(npc);
				npc.abortAttack();
				npc.abortCast();
				npc.doCast(LIMIT_BARRIER.getSkill());
				npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.THE_RAID_BOSS_USES_THE_LIMIT_BARRIER_NFOCUS_YOUR_ATTACKS_TO_DESTROY_THE_LIMIT_BARRIER_IN_15_SECONDS, 2, 5000, true));
				startQuestTimer("RESTORE_FULL_HP", 15000, npc, attacker);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		RAIDBOSS_HITS.remove(npc);
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new LimitBarrier();
	}
}
